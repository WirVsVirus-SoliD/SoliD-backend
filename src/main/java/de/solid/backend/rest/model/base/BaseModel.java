package de.solid.backend.rest.model.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public abstract class BaseModel {

	private static final Logger _log = LoggerFactory.getLogger(BaseModel.class);

	public void copyProperties(Object dest, Object src) {
		try {
			BeanUtils.copyProperties(src, dest);
		} catch (Exception e) {
			_log.error(String.format(
					"Error on BeanUtils.copyProperties from %s to %s", src,
					dest), e);
		}
	}

}
