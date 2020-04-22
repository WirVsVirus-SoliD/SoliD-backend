package de.solid.backend.dao;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * entity for basic account data
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solid_account")
public class AccountEntity extends AbstractEntity {

  @Column(nullable = true)
  private String firstName;

  @Column(nullable = true)
  private String lastName;

  private String email;

  @Column(nullable = true)
  private String phone;

  private String keycloakUserId;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private MediaEntity media;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastPasswordReset;
}
