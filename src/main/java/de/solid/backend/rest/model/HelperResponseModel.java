package de.solid.backend.rest.model;

import de.solid.backend.common.EmploymentStatus;
import de.solid.backend.dao.HelperEntity;
import de.solid.backend.rest.model.base.BaseResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelperResponseModel
		extends
			BaseResponseModel<HelperResponseModel, HelperEntity> {

	private long helperId;

	private String firstName;

	private String lastName;

	private String mobileNumber;

	private String email;

	private EmploymentStatus EmploymentStatus;

	private boolean fullTime;

	private int pickupRange;

	private boolean driverLicense;

	private boolean pickupRequired;

	@Override
	protected void mapAdditionalAttributes(HelperResponseModel model,
			HelperEntity entity) {
		model.setHelperId(entity.getT_id());
	}
}