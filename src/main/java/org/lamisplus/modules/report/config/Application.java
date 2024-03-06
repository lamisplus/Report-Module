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
    public static String indexElicitation;
    public static String indexElicitationName;
    public static String biometric;
    public static String biometricName;

    @PostConstruct
    public void setParameters(){
        indexElicitation = getIndexQuery();
        indexElicitationName = getIndexQueryName();
        biometric = getBiometricQuery();
        biometricName = getBiometricQueryName();
    }
}
