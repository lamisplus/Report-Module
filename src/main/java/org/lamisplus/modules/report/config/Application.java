package org.lamisplus.modules.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "query", ignoreUnknownFields = false)
@Configuration
@Data
public class Application {
    private String indexQuery;
    public static String iq;

    @PostConstruct
    public void setQuery(){
        iq = getIndexQuery();
    }
}
