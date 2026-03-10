package org.lamisplus.modules.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApprCollectionProjection {

    private List<ApprDataValue> dataValues;

}
