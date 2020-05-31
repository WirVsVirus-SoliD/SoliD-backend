package de.solid.backend.rest.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import de.solid.backend.dao.AccountEntity;
import de.solid.backend.dao.FavoriteEntity;
import de.solid.backend.dao.HelperEntity;
import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.dao.repository.FavoritesRepository;
import de.solid.backend.dao.repository.HelpersRepository;
import de.solid.backend.dao.repository.InquiriesRepository;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.model.FavoriteRequestModel;
import de.solid.backend.rest.model.FavoriteResponseModel;
import de.solid.backend.rest.model.helper.HelperRequestModel;
import de.solid.backend.rest.model.helper.HelperResponseModel;
import de.solid.backend.rest.model.helper.InquiryRequestModel;
import de.solid.backend.rest.model.helper.InquiryResponseModel;
import de.solid.backend.rest.service.exception.DuplicateException;
import de.solid.backend.rest.service.exception.NoSuchEntityException;
import de.solid.backend.rest.service.exception.UnauthorizedException;
import io.quarkus.mailer.MailTemplate;

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
  private FavoritesRepository favoritesRepository;

  @Inject
  private MailTemplate helperActivationMail;

  @Inject
  private MailTemplate helperInquiredMail;

  @ConfigProperty(name = "ticket.activation.url")
  private String ticketActivationUrl;

  @ConfigProperty(name = "provider.dashboard.url")
  private String providerDashboardUrl;

  @Transactional
  public void registerHelper(HelperRequestModel model) {
    AccountEntity account = this.accountService.createAccount(model.getAccount());
    HelperEntity helper = model.toEntity(null);
    helper.setAccount(account);
    this.helpersRepository.persist(helper);
    String uuid = this.ticketService.createHelperRegistrationTicket(account.getT_id(),
        model.getVisitedProvider());
    String tau = this.ticketActivationUrl.replace("{uuid}", uuid);
    if (model.getVisitedProvider() != null) {
      tau += "&provider=" + model.getVisitedProvider();
    }
    this.helperActivationMail.to(model.getAccount().getEmail())
        .subject("soliD - Registrierung abschließen")
        .data("firstName", model.getAccount().getFirstName()).data("ticketActivationUrl", tau)
        .send();
  }

  @Transactional
  public HelperResponseModel updateHelper(HelperRequestModel model, String authenticatedUserEmail) {
    AccountEntity account =
        this.accountService.updateAccount(model.getAccount(), authenticatedUserEmail);
    HelperEntity helper = this.getHelperByAccountId(account.getT_id());
    helper = model.toEntity(helper);
    this.helpersRepository.persist(helper);
    return new HelperResponseModel().fromEntity(helper);
  }

  @Transactional
  public void deleteHelper(String authenticatedUserEmail) {
    AccountEntity account = this.accountService.deleteAccount(authenticatedUserEmail);
    HelperEntity helper = this.getHelperByAccountId(account.getT_id());
    this.inquiriesRepository.findByHelperId(helper.getT_id())
        .forEach(inquiry -> this.removeFromInquiry(inquiry.getT_id(), authenticatedUserEmail));
    this.favoritesRepository.findByHelperId(helper.getT_id())
        .forEach(favorite -> this.favoritesRepository.delete(favorite));
    this.helpersRepository.delete(helper);
  }

  @Transactional
  public void deleteHelper(long accountId) {
    this.accountService.deleteAccount(accountId);
    this.helpersRepository.deleteByAccountId(accountId);
  }

  @Transactional
  public void removeFromInquiry(long inquiryId, String authenticatedUserEmail) {
    InquiryEntity entity = this.inquiriesRepository.findById(inquiryId);
    if (entity != null) {
      long helperEntityId = getHelperByEmail(authenticatedUserEmail).getT_id();
      if (entity.getHelper() != null) {
        if (entity.getHelper().getT_id() == helperEntityId) {
          entity.setHelper(null);
          this.inquiriesRepository.persist(entity);
        }
      } else {
        throw new NoSuchEntityException(this.getClass(), "removeFromInquiry",
            String.format("inquiry with id %s already removed for helper with email %s", inquiryId,
                authenticatedUserEmail));
      }
    } else {
      throw new NoSuchEntityException(this.getClass(), "removeFromInquiry",
          String.format("no inquiry exists with id %s for helper with email %s", inquiryId,
              authenticatedUserEmail));
    }
  }

  public List<InquiryResponseModel> getProvidersInquiredFor(String authenticatedUserEmail) {
    long helperEntityId = getHelperByEmail(authenticatedUserEmail).getT_id();
    List<InquiryEntity> entites = this.inquiriesRepository.findByHelperId(helperEntityId);
    return entites.stream().map(entity -> new InquiryResponseModel().fromEntity(entity))
        .collect(Collectors.toList());
  }

  @Transactional
  public InquiryResponseModel inquireForProvider(InquiryRequestModel model,
      String authenticatedUserEmail) {
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
        this.helperInquiredMail.to(providerEntity.getAccount().getEmail())
            .subject("soliD - neuer Helfer registriert")
            .data("firstName", providerEntity.getAccount().getFirstName())
            .data("dashboardUrl", providerDashboardUrl).send();
        return new InquiryResponseModel().fromEntity(entity);
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

  public List<FavoriteResponseModel> getFavorites(String authenticatedUserEmail) {
    HelperEntity helperEntity = this.getHelperByEmail(authenticatedUserEmail);
    return this.favoritesRepository.findByHelperId(helperEntity.getT_id()).stream()
        .map(favorite -> new FavoriteResponseModel().fromEntity(favorite))
        .collect(Collectors.toList());
  }

  @Transactional
  public FavoriteResponseModel markFavorite(FavoriteRequestModel model,
      String authenticatedUserEmail) {
    HelperEntity helperEntity = this.getHelperByEmail(authenticatedUserEmail);
    ProviderEntity providerEntity = this.providerRepository.findById(model.getProviderId());
    if (providerEntity != null) {
      FavoriteEntity fe = this.favoritesRepository.findByHelperAndProvider(helperEntity.getT_id(),
          model.getProviderId());
      if (fe == null) {
        fe = FavoriteEntity.builder().build();
        fe.setHelper(helperEntity);
        fe.setProvider(providerEntity);
        this.favoritesRepository.persist(fe);
        return new FavoriteResponseModel().fromEntity(fe);
      } else {
        throw new DuplicateException(this.getClass(), "markFavorite",
            String.format(
                "provider with id %s already marked as favorites for helper with email %s",
                model.getProviderId(), authenticatedUserEmail));
      }
    } else {
      throw new NoSuchEntityException(this.getClass(), "markFavorite",
          String.format(
              "no provider exists with id %s to be marked as favorite by helper with email %s",
              model.getProviderId(), authenticatedUserEmail));
    }
  }

  @Transactional
  public void deleteFavorite(long favoriteId, String authenticatedUserEmail) {
    HelperEntity helperEntity = this.getHelperByEmail(authenticatedUserEmail);
    FavoriteEntity entity = this.favoritesRepository.findById(favoriteId);
    if (entity != null) {
      if (entity.getHelper().getT_id() == helperEntity.getT_id()) {
        this.favoritesRepository.delete(entity);
      } else {
        throw new UnauthorizedException(this.getClass(), "deleteFavorite",
            String.format("unauthorized try to remove favorite with id %s for helper with email %s",
                favoriteId, helperEntity.getT_id()));
      }
    } else {
      throw new NoSuchEntityException(this.getClass(), "deleteFavorite",
          String.format(
              "no favorite entry exists id %s to be removed from favorite by helper with email %s",
              favoriteId, authenticatedUserEmail));
    }
  }

  public boolean helperExistsForEmail(String email) {
    return this.helpersRepository.findByEmail(email) != null;
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
