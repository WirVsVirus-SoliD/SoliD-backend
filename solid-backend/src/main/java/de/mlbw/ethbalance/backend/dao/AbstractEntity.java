package de.mlbw.ethbalance.backend.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.mlbw.ethbalance.backend.dao.repository.PrePersistListener;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(PrePersistListener.class)
public class AbstractEntity {
	public static final String SYSTEM_USER = "SYSTEM";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column()
	private int t_id;

	@Column()
	private String t_manadator;

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
