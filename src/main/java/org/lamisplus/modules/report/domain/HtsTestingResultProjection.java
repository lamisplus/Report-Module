package org.lamisplus.modules.report.domain;

public interface HtsTestingResultProjection {

    String getFinalHivTestResult();

    String getAgeGroup();

    Long getInpatientM();

    Long getInpatientF();

    Long getCtM();

    Long getCtF();

    Long getOutpatientM();

    Long getOutpatientF();

    Long getOthersM();

    Long getOthersF();

    Long getPregnantWomen();

    Long getCommunityM();

    Long getCommunityF();

    Long getTotalM();

    Long getTotalF();
}