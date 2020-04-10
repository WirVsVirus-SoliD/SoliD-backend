package de.solid.backend.dao.repository;

import javax.enterprise.context.ApplicationScoped;
import de.solid.backend.dao.TicketEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<TicketEntity> {

  public TicketEntity findByUUID(String uuid) {
    return this.find("uuid", uuid).firstResult();
  }

}
