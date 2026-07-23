package org.lamisplus.modules.report.domain;

public interface HtsMsfProjection {

    String getSection();

    String getTestResult();

    String getRowLabel();

    String getColumnKey();

    Long getValue();
}