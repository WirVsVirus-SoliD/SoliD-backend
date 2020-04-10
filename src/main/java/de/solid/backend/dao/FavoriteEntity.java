package de.solid.backend.dao;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * entity for favorites - connects helper and provider oneToOne
 * 
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solid_favorites")
public class FavoriteEntity extends AbstractEntity {

  @ManyToOne
  private HelperEntity helper;

  @ManyToOne
  private ProviderEntity provider;
}
