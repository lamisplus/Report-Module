package org.lamisplus.modules.report;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;

@AcrossApplication(
		modules = {
				
		}
)
public class ReportModule extends AcrossModule {
	public  static final String NAME = "ReportModule";

	public ReportModule() {
		super ();
		addApplicationContextConfigurer (new ComponentScanConfigurer (
				getClass ().getPackage ().getName () + ".repositories",
				getClass ().getPackage ().getName () + ".service",
				getClass ().getPackage ().getName () + ".controller"
		));
	}

	@Override
	public String getName() {
		return NAME;
	}
}

