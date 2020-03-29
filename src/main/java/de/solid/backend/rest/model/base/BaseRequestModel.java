package de.solid.backend.rest.model.base;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import de.solid.backend.dao.AbstractEntity;

public abstract class BaseRequestModel<M, E extends AbstractEntity>
		extends
			BaseModel {
	/**
	 * map additional attributes manually
	 * 
	 * @param entity
	 * @param model
	 */
	protected abstract void mapAdditionalAttributes(E entity);

	/**
	 * convert given request model to entity class - mapping the attributes of
	 * the given request model to the entity
	 * 
	 * @param model
	 * @return the mapped entity
	 */
	public E toEntity(Optional<E> entityToUpdate) {
		ParameterizedType superClass = (ParameterizedType) getClass()
				.getGenericSuperclass();
		Class<M> modelType = (Class<M>) superClass.getActualTypeArguments()[0];
		Class<E> entityType = (Class<E>) superClass.getActualTypeArguments()[1];
		try {
			E entity = entityType.getDeclaredConstructor().newInstance();
			if (entityToUpdate.isPresent()) {
				entity = entityToUpdate.get();
			}
			copyProperties(entity, this);
			mapAdditionalAttributes(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException(String.format(
				"Cannot instantiate entity for model class %s with values %s",
				modelType, this));
	}
}
