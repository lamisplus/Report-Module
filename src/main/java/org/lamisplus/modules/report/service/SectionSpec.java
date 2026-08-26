// SectionSpec.java — replaces the duplicated private inner class in both builders
package org.lamisplus.modules.report.service;

/**
 * One entry per section on a report form: display title, the layout it
 * renders with (matches a SectionLayoutRegistry key, or falls back to
 * auto-inferred columns if unregistered), which "section" value in the raw
 * projection data it pulls from, and an optional testResult filter.
 */
public final class SectionSpec {
    final String title;
    final String layoutType;
    final String dataSection;
    final String testResult; // nullable

    public SectionSpec(String title, String layoutType, String dataSection, String testResult) {
        this.title = title;
        this.layoutType = layoutType;
        this.dataSection = dataSection;
        this.testResult = testResult;
    }
}