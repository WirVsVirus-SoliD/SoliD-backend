package de.solid.backend.dao;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import de.solid.backend.common.AccountType;
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

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "expires_at")
  private Date expiresAt;

  @Column(name = "related_account")
  private Long relatedAccount;

  @Column(name = "related_account_type")
  private AccountType relatedAccountType;

  @Column(name = "ticket_validated")
  private boolean ticketValidated;

  @Column(name = "visited_provider")
  private Long visitedProvider;

}
