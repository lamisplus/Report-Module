package org.lamisplus.modules.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "query", ignoreUnknownFields = true, ignoreInvalidFields = true)
@Configuration
@Data
public class Application {
    private String indexQuery;
    private String indexQueryName;
    private String biometricQuery;
    private String biometricQueryName;
    private String pmtctHtsQueryName;
    private String pmtctHtsQuery;
    private String pmtctMaternalCohortQueryName;
    private String pmtctMaternalCohortQuery;
    private String prepQueryName;
    private String prepQuery;
    public static String indexElicitation;
    public static String indexElicitationName;
    public static String biometric;
    public static String biometricName;

    public static String pmtctHtsName;
    public static String pmtctHts;

    public static String pmtctMaternalCohortName;
    public static String pmtctMaternalCohort;
    public static String prepName;
    public static String prep;

    @PostConstruct
    public void mapQueryYmlParameters(){
        indexElicitation = getIndexQuery();
        indexElicitationName = getIndexQueryName();
        biometric = getBiometricQuery();
        biometricName = getBiometricQueryName();
        pmtctHtsName = getPmtctHtsQueryName();
        pmtctHts = getPmtctHtsQuery();
        pmtctMaternalCohortName = getPmtctMaternalCohortQueryName();
        pmtctMaternalCohort = getPmtctMaternalCohortQuery();
        prep = getPrepQuery();
        prepName = getPrepQueryName();
    }
}
