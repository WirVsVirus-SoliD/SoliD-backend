package de.solid.backend.rest.service.async;

import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.solid.backend.clients.GeocodeRestClient;
import de.solid.backend.clients.model.GeocodeResponse;
import de.solid.backend.dao.AddressEntity;
import de.solid.backend.dao.ProviderEntity;
import de.solid.backend.dao.repository.ProvidersRepository;
import de.solid.backend.rest.service.exception.RestClientException;
import de.solid.backend.rest.service.exception.SolidException;
import io.quarkus.vertx.ConsumeEvent;

@ApplicationScoped
public class GeocodeProcessor {

  private static final Logger _log = LoggerFactory.getLogger(GeocodeProcessor.class);

  @Inject
  @RestClient
  private GeocodeRestClient geocodeRestClient;

  @Inject
  private ProvidersRepository providersRepository;

  @ConfigProperty(name = "geocode.resolution.retry.interval.seconds")
  private int resolutionRetryInterval;

  @ConfigProperty(name = "geocode.resolution.retry.amount")
  private long resulutionRetries;

  /**
   * triggers fail safe resolution of lat/long via public geocode service
   * 
   * @param providerId the provider which ones address should be resolved
   * @throws InterruptedException
   */
  @ConsumeEvent(value = "geocode", blocking = true)
  @Transactional
  public void updateLatLong(Long providerId) throws InterruptedException {
    if (providerId != null) {
      ProviderEntity entity = this.providersRepository.findById(providerId);
      boolean resolved = false;
      int resolverCount = 0;
      if (entity != null) {
        while (resolverCount < resolutionRetryInterval && !resolved)
          resolved = this.retrievedLatLong(entity);
      }
      if (resolved && resolverCount < resolutionRetryInterval) {
        this.providersRepository.persist(entity);
      } else
        throw new SolidException(this.getClass(), "updateLatLong",
            String.format(
                "Cannot resolve address for provider with id %s due to retry interval exceeded",
                entity.getT_id()));
    }
  }

  private boolean retrievedLatLong(ProviderEntity entity) throws InterruptedException {
    try {
      GeocodeResponse geocodeCallResult =
          this.geocodeRestClient.getLatLong(this.getGeocodeRequestParam(entity.getAddress()));
      if (geocodeCallResult.getError() != null) {
        throw new RestClientException(this.getClass(), "retrieveLtLong",
            String.format("Cannot retrieve lat and long for address %s: %s", entity.getAddress(),
                geocodeCallResult));
      } else {
        entity.setLatitude(geocodeCallResult.getLatt());
        entity.setLongitude(geocodeCallResult.getLongt());
        _log.info("Successfully resolved address for provider with id {} from geocode",
            entity.getT_id());
        return true;
      }
    } catch (Exception e) {
      _log.info(
          "Got throttled calling geojson address resolution for provider with id {}, retrying in {} seconds",
          entity.getT_id(), resolutionRetryInterval);
      TimeUnit.SECONDS.sleep(resolutionRetryInterval);
    }
    return false;
  }

  private String getGeocodeRequestParam(AddressEntity address) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(address.getStreet());
    buffer.append("+");
    buffer.append(address.getHousenr());
    buffer.append("+");
    buffer.append(address.getZip());
    buffer.append("+");
    buffer.append(address.getCity());

    return buffer.toString();
  }
}
