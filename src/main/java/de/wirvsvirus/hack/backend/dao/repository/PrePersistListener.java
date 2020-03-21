package de.wirvsvirus.hack.backend.dao.repository;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.util.StringUtils;

import de.wirvsvirus.hack.backend.dao.AbstractEntity;

public class PrePersistListener {
	@PrePersist
	public void setCreatedFields(AbstractEntity e) {
		e.setT_dateCreated(new Date());
		if (!StringUtils.hasText(e.getT_userCreated())) {
			e.setT_userCreated(AbstractEntity.SYSTEM_USER);
		}
		setUpdateFields(e);
		if (!StringUtils.hasText(e.getT_manadator())) {
			e.setT_manadator(AbstractEntity.SOLID_MANDATOR);
		}
	}

	@PreUpdate
	public void setUpdateFields(AbstractEntity e) {
		e.setT_dateChanged(new Date());
		if (!StringUtils.hasText(e.getT_userChanged())) {
			e.setT_userChanged(AbstractEntity.SYSTEM_USER);
		}
	}
}
