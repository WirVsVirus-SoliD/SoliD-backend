package de.solid.backend.common;

import java.util.Date;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.apache.commons.lang3.StringUtils;
import de.solid.backend.dao.AbstractEntity;

/*
 * hooks in when inserting or updating entities and provides values for technical fields if they
 * aren't set
 * 
 */
public class PrePersistListener {
  @PrePersist
  public void setCreatedFields(AbstractEntity e) {
    e.setT_dateCreated(new Date());
    if (StringUtils.isEmpty(e.getT_userCreated())) {
      e.setT_userCreated(AbstractEntity.SYSTEM_USER);
    }
    setUpdateFields(e);
  }

  @PreUpdate
  public void setUpdateFields(AbstractEntity e) {
    e.setT_dateChanged(new Date());
    if (StringUtils.isEmpty(e.getT_userChanged())) {
      e.setT_userChanged(AbstractEntity.SYSTEM_USER);
    }
  }
}
