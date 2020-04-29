package de.solid.backend.dao;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * entity for provider related data
 * 
 * Hint: primitives are always not null
 * 
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solid_provider")
public class ProviderEntity extends AbstractEntity {

  private String farmName;

  private String crops;

  private String url;

  private String minWorkPeriod;

  private Float hourlyRate;

  private Integer pickupRange;

  private Double latitude;

  private Double longitude;

  private String workingConditions;

  private String overnightInformation;

  private String providingInformation;

  private String languages;

  private String otherInformation;

  private Float overnightPrice;

  private String workActivities;

  private Boolean bio;

  @OneToOne(cascade = CascadeType.ALL)
  private AddressEntity address;

  @OneToMany(mappedBy = "provider")
  private List<InquiryEntity> inquiries;

  @OneToOne(cascade = CascadeType.ALL)
  private AccountEntity account;
}
