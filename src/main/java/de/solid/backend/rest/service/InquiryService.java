package de.solid.backend.rest.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import de.solid.backend.rest.service.exception.NoSuchEntityException;

@ApplicationScoped
public class InquiryService {

  @Inject
  private HelperService helperService;

  @Inject
  private ProviderService providerService;

  public Object getInquires(String authenticatedEmail) {
    if (this.helperService.helperExistsForEmail(authenticatedEmail)) {
      return this.helperService.getProvidersInquiredFor(authenticatedEmail);
    } else if (this.providerService.providerExistsForEmail(authenticatedEmail)) {
      return this.providerService.getHelpersInquired(authenticatedEmail);
    } else {
      throw new NoSuchEntityException(this.getClass(), "getInquiries",
          String.format("No account exists with email %s", authenticatedEmail));
    }
  }

  public void removeInquiry(long inquiryId, String authenticatedUserEmail) {
    if (this.helperService.helperExistsForEmail(authenticatedUserEmail)) {
      this.helperService.removeFromInquiry(inquiryId, authenticatedUserEmail);
    } else if (this.providerService.providerExistsForEmail(authenticatedUserEmail)) {
      this.providerService.removeFromInquiry(inquiryId, authenticatedUserEmail);
    } else {
      throw new NoSuchEntityException(this.getClass(), "removeInquiry",
          String.format("No account exists with email %s", authenticatedUserEmail));
    }
  }
}
