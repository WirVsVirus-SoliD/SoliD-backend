package de.solid.backend.dao;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import de.solid.backend.common.PrePersistListener;
import lombok.Getter;
import lombok.Setter;

/**
 * base entity, provides default technical attributes
 *
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(PrePersistListener.class)
public class AbstractEntity {

  public static final String SYSTEM_USER = "SOLID_BACKEND";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private long t_id;

  @Column(name = "t_user_created")
  private String t_userCreated;

  @Column(name = "t_user_changed")
  private String t_userChanged;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "t_date_created")
  private Date t_dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "t_date_changed")
  private Date t_dateChanged;

}
