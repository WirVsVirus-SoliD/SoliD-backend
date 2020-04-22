package de.solid.backend.rest.service;

import java.util.Date;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.solid.backend.common.AccountType;
import de.solid.backend.dao.TicketEntity;
import de.solid.backend.dao.repository.TicketRepository;
import de.solid.backend.rest.service.exception.DuplicateException;
import de.solid.backend.rest.service.exception.NoSuchEntityException;
import de.solid.backend.rest.service.exception.TimeoutException;

/*
 * provides ticket related operations
 * 
 */
@ApplicationScoped
public class TicketService {

  private static final Logger _log = LoggerFactory.getLogger(TicketService.class);

  @ConfigProperty(name = "ticket.activation.timeout.hours")
  private int activationTimeoutHours;

  @ConfigProperty(name = "ticket.password.reset.timeout.mins")
  private int passwordResetMins;

  @Inject
  private TicketRepository ticketRepository;

  @Inject
  private HelperService helperService;

  @Inject
  private ProviderService providerService;

  public String createAccountResetTicket(long relatedAccount) {
    String uuid = UUID.randomUUID().toString();
    this.ticketRepository.persist(
        TicketEntity.builder().relatedAccount(relatedAccount).uuid(uuid).ticketValidated(false)
            .expiresAt(DateUtils.addMinutes(new Date(), passwordResetMins)).build());
    return uuid;
  }

  @Transactional
  public String createRegisterProviderTicket(long relatedAccount) {
    String uuid = UUID.randomUUID().toString();
    this.ticketRepository.persist(TicketEntity.builder().relatedAccount(relatedAccount).uuid(uuid)
        .relatedAccountType(AccountType.Provider).ticketValidated(false)
        .expiresAt(DateUtils.addHours(new Date(), activationTimeoutHours)).build());
    return uuid;
  }

  @Transactional
  public String createHelperRegistrationTicket(long relatedAccount, Long visitedProvider) {
    String uuid = UUID.randomUUID().toString();
    this.ticketRepository.persist(TicketEntity.builder().relatedAccount(relatedAccount).uuid(uuid)
        .visitedProvider(visitedProvider).relatedAccountType(AccountType.Helper)
        .ticketValidated(false).expiresAt(DateUtils.addHours(new Date(), activationTimeoutHours))
        .build());
    return uuid;
  }

  /**
   * validate if ticket is valid and return related account
   * 
   * @param uuid
   * @return related accountId
   * @throws TimeoutException
   */
  @Transactional
  public long validateTicket(String uuid) throws TimeoutException {
    if (!StringUtils.isEmpty(uuid)) {
      TicketEntity entity = this.ticketRepository.findByUUID(uuid);
      if (entity != null) {
        if (!entity.isTicketValidated()) {
          if (entity.getExpiresAt().after(new Date())) {
            entity.setTicketValidated(true);
            this.ticketRepository.persist(entity);
            return entity.getRelatedAccount();
          } else {
            throw new TimeoutException(this.getClass(), "validateTicket",
                String.format("ticket with uuid %s is no longer valid", uuid));
          }
        } else {
          throw new DuplicateException(this.getClass(), "validateTicket",
              String.format("Ticket with uuid %s was already validated", uuid));
        }
      }
    }
    throw new NoSuchEntityException(this.getClass(), "validateTicket",
        String.format("Ticket with uuid %s could not be validated", uuid));
  }

  /**
   * switched of for now
   */
  // @Scheduled(every = "12h")
  @Transactional
  public void periodicCleanup() {
    _log.info("Period cleanup for expired and activated tickets started");
    this.ticketRepository.deleteByActivated();
    this.ticketRepository.findByNotActivated().forEach(ticket -> {
      if (ticket.getExpiresAt().before(new Date())) {
        _log.info("Ticket with id {} expired, removing corresponding {} entities", ticket.getUuid(),
            ticket.getRelatedAccountType().toString());
        this.ticketRepository.delete(ticket);
        switch (ticket.getRelatedAccountType()) {
          case Helper:
            helperService.deleteHelper(ticket.getRelatedAccount());
            break;
          case Provider:
            providerService.deleteProvider(ticket.getRelatedAccount());
            break;
          default:
            break;
        }
      }
    });
  }
}
