package de.solid.backend.dao;

import javax.persistence.Entity;
import javax.persistence.Lob;
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
@Table(name = "solid_account_media")
public class MediaEntity extends AbstractEntity {

  @Lob
  private byte[] media;

  private String mediaName;
}
