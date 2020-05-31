package de.solid.backend.rest.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import de.solid.backend.dao.AccountEntity;
import de.solid.backend.dao.AddressEntity;
import de.solid.backend.dao.InquiryEntity;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.dao.repository.InquiriesRepository;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.model.AddressRequestModel;
import de.solid.backend.rest.model.provider.GeoJsonFeatureResponseModel;
import de.solid.backend.rest.model.provider.GeoJsonResponseModel;
import de.solid.backend.rest.model.provider.InquiryResponseModel;
import de.solid.backend.rest.model.provider.ProviderRequestModel;
import de.solid.backend.rest.model.provider.ProviderResponseModel;
import de.solid.backend.rest.model.provider.PublicProviderResponseModel;
import de.solid.backend.rest.service.exception.NoSuchEntityException;
import io.quarkus.mailer.MailTemplate;
import io.vertx.mutiny.core.eventbus.EventBus;

/*
 * provides provider related operations
 * 
 */
@ApplicationScoped
public class ProviderService {

  @Inject
  private ProvidersRepository providersRepository;

  @Inject
  private InquiriesRepository inquiriesRepository;

  @Inject
  private AccountService accountService;

  @Inject
  private TicketService ticketService;

  @Inject
  private MailTemplate helperActivationMail;

  @Inject
  private EventBus bus;

  @ConfigProperty(name = "ticket.activation.url")
  private String ticketActivationUrl;

  @Transactional
  public void insertProvider(ProviderRequestModel model) {
    AccountEntity account = this.accountService.createAccount(model.getAccount());
    ProviderEntity provider = model.toEntity(null);
    provider.setAccount(account);
    this.providersRepository.persist(provider);
    this.resolveLatLongAsync(provider.getT_id());
    String uuid = this.ticketService.createRegisterProviderTicket(account.getT_id());
    String tau = this.ticketActivationUrl.replace("{uuid}", uuid);
    this.helperActivationMail.to(model.getAccount().getEmail()).subject("Registrierung abschließen")
        .data("firstName", model.getAccount().getFirstName()).data("ticketActivationUrl", tau)
        .send();
  }

  @Transactional
  public ProviderResponseModel updateProvider(ProviderRequestModel model,
      String authenticatedUserEmail) {
    AccountEntity account =
        this.accountService.updateAccount(model.getAccount(), authenticatedUserEmail);
    ProviderEntity provider = this.getProviderByAccountId(account.getT_id());
    provider = model.toEntity(provider);
    if (!addressIsSame(model.getAddress(), provider.getAddress())) {
      this.resolveLatLongAsync(provider.getT_id());
    }
    this.providersRepository.persist(provider);
    return new ProviderResponseModel().fromEntity(provider);
  }

  @Transactional
  public void deleteProvider(String authenticatedUserEmail) {
    AccountEntity account = this.accountService.deleteAccount(authenticatedUserEmail);
    ProviderEntity provider = this.getProviderByAccountId(account.getT_id());
    List<InquiryEntity> inquires = this.inquiriesRepository.findByProviderId(provider.getT_id());
    for (InquiryEntity inquiryEntity : inquires) {
      this.removeFromInquiry(inquiryEntity.getT_id(), authenticatedUserEmail);
    }
    this.providersRepository.delete(provider);
  }

  @Transactional
  public void deleteProvider(long accountId) {
    this.accountService.deleteAccount(accountId);
    this.providersRepository.deleteByAccountId(accountId);
  }

  public List<InquiryResponseModel> getHelpersInquired(String authenticatedUserEmail) {
    long providerEntityId = getProviderIdByEmail(authenticatedUserEmail);
    List<InquiryEntity> entites = this.inquiriesRepository.findByProviderId(providerEntityId);
    return entites.stream().map(en -> new InquiryResponseModel().fromEntity(en))
        .collect(Collectors.toList());
  }

  @Transactional
  public void toggleHelperContacted(long inquiryId, String authenticatedUserEmail) {
    InquiryEntity entity = this.inquiriesRepository.findById(inquiryId);
    if (entity != null) {
      long providerEntityId = getProviderIdByEmail(authenticatedUserEmail);
      if (entity.getProvider().getT_id() == providerEntityId) {
        entity.setContacted(!entity.isContacted());
        this.inquiriesRepository.persist(entity);
      }
    } else {
      throw new NoSuchEntityException(this.getClass(), "toggleHelperContacted",
          String.format("no inquiry exists with id %s", inquiryId));
    }
  }

  @Transactional
  public void removeFromInquiry(long inquiryId, String authenticatedUserEmail) {
    InquiryEntity entity = this.inquiriesRepository.findById(inquiryId);
    if (entity != null) {
      long providerEntityId = getProviderIdByEmail(authenticatedUserEmail);
      if (entity.getProvider() != null) {
        if (entity.getProvider().getT_id() == providerEntityId) {
          entity.setProvider(null);
          this.inquiriesRepository.persist(entity);
        }
      } else {
        throw new NoSuchEntityException(this.getClass(), "removeFromInquiry",
            String.format("inquiry with id %s already removed for provider with email %s",
                inquiryId, authenticatedUserEmail));
      }
    } else {
      throw new NoSuchEntityException(this.getClass(), "removeFromInquiry",
          String.format("no inquiry exists with id %s for provider with email %s", inquiryId,
              authenticatedUserEmail));
    }
  }

  public GeoJsonResponseModel getProvidersInRange(float latitude, float longitude,
      Optional<Double> radius) {
    List<ProviderEntity> list = this.providersRepository.findAll().list();
    List<GeoJsonFeatureResponseModel> features = list.stream().filter(entity -> {
      if (radius.isPresent()) {
        return calculateDistance(entity, latitude, longitude) <= radius.get();
      } else
        return true;
    }).map(entity -> {
      GeoJsonFeatureResponseModel model = new GeoJsonFeatureResponseModel().fromEntity(entity);
      model.getProperties().setDistance(calculateDistance(entity, latitude, longitude));
      return model;
    }).collect(Collectors.toList());
    return new GeoJsonResponseModel(features);
  }

  public boolean providerExistsForEmail(String email) {
    return this.providersRepository.findByEmail(email) != null;
  }

  public PublicProviderResponseModel getProviderById(long providerId) {
    ProviderEntity provider = this.providersRepository.findById(providerId);
    if (provider != null) {
      return new PublicProviderResponseModel().fromEntity(provider);
    } else {
      throw new NoSuchEntityException(this.getClass(), "getProviderById",
          String.format("No provider exists with providerId %s", providerId));
    }
  }

  private long getProviderIdByEmail(String authenticatedUserEmail) {
    ProviderEntity provider = this.providersRepository.findByEmail(authenticatedUserEmail);
    if (provider != null) {
      return provider.getT_id();
    } else {
      throw new NoSuchEntityException(this.getClass(), "getProviderIdByEmail",
          String.format("No provider exists with email %s", authenticatedUserEmail));
    }
  }

  private ProviderEntity getProviderByAccountId(long accountId) {
    ProviderEntity provider = this.providersRepository.findByAccount(accountId);
    if (provider != null) {
      return provider;
    } else {
      throw new NoSuchEntityException(this.getClass(), "getProviderByAccount",
          String.format("No provider exists for accountId %s", accountId));
    }
  }

  private boolean addressIsSame(AddressRequestModel model, AddressEntity entity) {
    return model.getCity().equals(entity.getCity()) && model.getStreet().equals(entity.getStreet())
        && model.getHousenr().equals(entity.getHousenr()) && model.getZip().equals(entity.getZip());
  }

  private double calculateDistance(ProviderEntity entity, double latitude, double longitude) {
    return LatLngTool.distance(new LatLng(entity.getLatitude(), entity.getLongitude()),
        new LatLng(latitude, longitude), LengthUnit.KILOMETER);
  }

  private void resolveLatLongAsync(Long providerId) {
    bus.sendAndForget("geocode", providerId);
  }
}
