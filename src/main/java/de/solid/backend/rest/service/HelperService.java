package de.solid.backend.rest.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import de.solid.backend.dao.AccountEntity;
import de.solid.backend.dao.HelperEntity;
import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.dao.repository.HelpersRepository;
import de.solid.backend.dao.repository.InquiriesRepository;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.model.helper.HelperRequestModel;
import de.solid.backend.rest.model.helper.InquiryRequestModel;
import de.solid.backend.rest.model.provider.ProviderResponseModel;
import de.solid.backend.rest.service.exception.DuplicateException;
import de.solid.backend.rest.service.exception.NoSuchEntityException;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

/*
 * provides helper related operations
 * 
 */
@ApplicationScoped
public class HelperService {

  @Inject
  private HelpersRepository helpersRepository;

  @Inject
  private InquiriesRepository inquiriesRepository;

  @Inject
  private AccountService accountService;

  @Inject
  private TicketService ticketService;

  @Inject
  private ProvidersRepository providerRepository;

  @Inject
  private Mailer mailer;

  @Transactional
  public void registerHelper(HelperRequestModel model) {
    AccountEntity account = this.accountService.createAccount(model.getAccount());
    HelperEntity helper = model.toEntity(null);
    helper.setAccount(account);
    this.helpersRepository.persist(helper);
    this.ticketService.createTicket(account.getT_id());
    this.mailer.send(Mail.withText(model.getAccount().getEmail(), "Registierung abschliessen",
        "follow the link"));
  }

  @Transactional
  public void updateHelper(HelperRequestModel model, String authenticatedUserEmail) {
    AccountEntity account =
        this.accountService.updateAccount(model.getAccount(), authenticatedUserEmail);
    HelperEntity helper = this.getHelperByAccountId(account.getT_id());
    helper = model.toEntity(helper);
    this.helpersRepository.persist(helper);
  }

  @Transactional
  public void deleteHelper(String authenticatedUserEmail) throws NoSuchEntityException {
    AccountEntity account = this.accountService.deleteAccount(authenticatedUserEmail);
    HelperEntity helper = this.getHelperByAccountId(account.getT_id());
    List<InquiryEntity> inquires = this.inquiriesRepository.findByHelperId(helper.getT_id());
    for (InquiryEntity inquiryEntity : inquires) {
      this.removeFromInquiry(inquiryEntity.getT_id(), authenticatedUserEmail);
    }
    this.helpersRepository.delete(helper);
  }

  @Transactional
  public void removeFromInquiry(long providerId, String authenticatedUserEmail)
      throws NoSuchEntityException {
    long helperEntityId = getHelperByEmail(authenticatedUserEmail).getT_id();
    InquiryEntity entity =
        this.inquiriesRepository.findByHelperAndProvider(helperEntityId, providerId);
    if (entity != null) {
      if (entity.getProvider().getT_id() == helperEntityId) {
        entity.setProvider(null);
        this.inquiriesRepository.persist(entity);
      }
    } else {
      throw new NoSuchEntityException(this.getClass(), "removeFromInquiry",
          String.format("no inquiry exists for helper with email %s and provider with id %s",
              authenticatedUserEmail, providerId));
    }
  }

  public List<ProviderResponseModel> getProvidersInquiredFor(String authenticatedUserEmail)
      throws NoSuchEntityException {
    long helperEntityId = getHelperByEmail(authenticatedUserEmail).getT_id();
    List<InquiryEntity> entites = this.inquiriesRepository.findByHelperId(helperEntityId);
    return entites.stream()
        .map(entity -> new ProviderResponseModel().fromEntity(entity.getProvider()))
        .collect(Collectors.toList());
  }

  @Transactional
  public void inquireForProvider(InquiryRequestModel model, String authenticatedUserEmail) {
    HelperEntity helperEntity = this.getHelperByEmail(authenticatedUserEmail);
    ProviderEntity providerEntity = this.providerRepository.findById(model.getProviderId());
    if (providerEntity != null) {
      InquiryEntity entity = this.inquiriesRepository
          .findByHelperAndProvider(helperEntity.getT_id(), model.getProviderId());
      if (entity == null) {
        entity = InquiryEntity.builder().build();
        entity.setHelper(helperEntity);
        entity.setProvider(providerEntity);
        this.inquiriesRepository.persist(entity);
      } else {
        throw new DuplicateException(this.getClass(), "inquireForProvider",
            String.format("helper with email %s already inquired for provider with id %s",
                authenticatedUserEmail, model.getProviderId()));
      }
    } else {
      throw new NoSuchEntityException(this.getClass(), "inquireForProvider",
          String.format("no provider exists with id %s to be inquired by helper with email %s",
              model.getProviderId(), authenticatedUserEmail));
    }
  }

  private HelperEntity getHelperByAccountId(long accountId) {
    HelperEntity helper = this.helpersRepository.findByAccount(accountId);
    if (helper != null) {
      return helper;
    } else {
      throw new NoSuchEntityException(this.getClass(), "getHelperByAccount",
          String.format("no helper exists for accountId %s", accountId));
    }
  }

  private HelperEntity getHelperByEmail(String authenticatedUserEmail) {
    HelperEntity helper = this.helpersRepository.findByEmail(authenticatedUserEmail);
    if (helper != null) {
      return helper;
    } else {
      throw new NoSuchEntityException(this.getClass(), "getHelperIdByEmail",
          String.format("no helper exists with email %s", authenticatedUserEmail));
    }
  }
}
