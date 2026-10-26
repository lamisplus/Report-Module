package org.lamisplus.modules.report.domain.dto;

public interface AppPreviewProjection {

    String getDataElement();
    String getPeriod(); // e.g., "2025W19" (ISO week)
    String getOrgUnit();
    String getCategoryOptionCombo();
    Long getValue(); // kept as String because DHIS2 values may be numeric or text
    String getAttributeOptionCombo();
    String getFacilityName();
    String getFacilityLga();
    String getIpName();
    String dataElementName();

}
