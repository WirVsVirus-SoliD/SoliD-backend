package de.solid.backend.dao;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
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

  @Column(nullable = true)
  @Lob
  private byte[] picture;

  @Column(nullable = true)
  private String pictureName;

  private String crops;

  @Column(nullable = true)
  private String url;

  @Column(nullable = true)
  private String minWorkPeriod;

  @Column(nullable = true)
  private float hourlyRate;

  @Column(nullable = true)
  private boolean pickupPossible;

  @Column(nullable = true)
  private int pickupRange;

  @Column(nullable = true)
  private boolean overnightPossible;

  @Column(nullable = true)
  private double latitude;

  @Column(nullable = true)
  private double longitude;

  @Column(nullable = true)
  private String description;

  @Column(nullable = true)
  private float overnightPrice;

  @OneToOne(cascade = CascadeType.ALL)
  private AddressEntity address;

  @OneToMany(mappedBy = "provider")
  private List<InquiryEntity> inquiries;

  @OneToOne(cascade = CascadeType.ALL)
  private AccountEntity account;
}
