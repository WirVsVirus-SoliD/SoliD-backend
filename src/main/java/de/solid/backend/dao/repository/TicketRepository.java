package de.solid.backend.dao.repository;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import de.solid.backend.dao.TicketEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<TicketEntity> {

  public TicketEntity findByUUID(String uuid) {
    return this.find("uuid", uuid).firstResult();
  }

  public void deleteByActivated() {
    this.delete("ticketValidated", true);
  }

  public List<TicketEntity> findByNotActivated() {
    return this.find("ticketValidated", false).list();
  }
}
