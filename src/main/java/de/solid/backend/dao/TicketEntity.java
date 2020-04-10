package de.solid.backend.dao;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * entity for ticket related data
 * 
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solid_ticket")
public class TicketEntity extends AbstractEntity {

  private String uuid;

  @Column(name = "expires_at")
  private Date expiresAt;

  @Column(name = "related_account")
  private long relatedAccount;

  @Column(name = "ticket_validated")
  private boolean ticketValidated;

}
