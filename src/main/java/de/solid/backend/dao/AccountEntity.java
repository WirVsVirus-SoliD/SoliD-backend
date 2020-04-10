package de.solid.backend.dao;

import javax.persistence.Entity;
import javax.persistence.Table;
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

  private String firstName;

  private String lastName;

  private String email;

  private String phone;

  private String keycloakUserId;
}
