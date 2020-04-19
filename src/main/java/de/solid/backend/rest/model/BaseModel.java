package de.solid.backend.rest.model;

import org.springframework.beans.BeanUtils;
import de.solid.backend.rest.service.exception.SolidException;

/**
 * base rest model, provides safe copy properties
 *
 */
public abstract class BaseModel {

  public static final String RESPONSE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public void copyProperties(Object from, Object to) {
    try {
      BeanUtils.copyProperties(from, to);
    } catch (Exception e) {
      throw new SolidException(this.getClass(), "copyProperties",
          String.format("Error on BeanUtils.copyProperties from %s to %s", to, from), e);
    }
  }

}
