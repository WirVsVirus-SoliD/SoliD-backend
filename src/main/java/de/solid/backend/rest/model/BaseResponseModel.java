package de.solid.backend.rest.model;

import java.lang.reflect.ParameterizedType;
import de.solid.backend.dao.AbstractEntity;

/**
 * basic response model, provides fromEntity method
 *
 * @param <M> response model type
 * @param <E> entity type
 */
public abstract class BaseResponseModel<M, E extends AbstractEntity> extends BaseModel {

  /**
   * map additional attributes manually
   * 
   * @param model
   * @param entity
   */
  protected abstract void mapAdditionalAttributes(M model, E entity);

  /**
   * convert given entity to response model class - mapping the attributes of the given entity to
   * the response model
   * 
   * @param entity
   * @return the mapped model
   */
  public M fromEntity(E entity) {
    ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
    Class<M> modelType = (Class<M>) superClass.getActualTypeArguments()[0];
    Class<E> entityType = (Class<E>) superClass.getActualTypeArguments()[1];
    try {
      M model = modelType.getDeclaredConstructor().newInstance();
      copyProperties(entity, model);
      mapAdditionalAttributes(model, entity);
      return model;
    } catch (Exception e) {
      e.printStackTrace();
    }
    throw new RuntimeException(String.format(
        "Cannot instantiate model for entity class %s with id %s", entityType, entity.getT_id()));
  }

}
