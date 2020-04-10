package de.solid.backend.dao;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import de.solid.backend.common.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * entity for helper related data
 * 
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solid_helper")
public class HelperEntity extends AbstractEntity {

  private EmploymentStatus EmploymentStatus;

  private boolean fullTime;

  private int pickupRange;

  private boolean driverLicense;

  private boolean pickupRequired;

  @OneToMany(mappedBy = "helper")
  private List<InquiryEntity> inquiries;

  @OneToOne(cascade = CascadeType.ALL)
  private AccountEntity account;
}
