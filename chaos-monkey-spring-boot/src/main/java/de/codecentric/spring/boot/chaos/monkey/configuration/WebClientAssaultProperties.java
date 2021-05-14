package de.codecentric.spring.boot.chaos.monkey.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "chaos.monkey.web.client")
public class WebClientAssaultProperties {

  private Boolean enabled = Boolean.FALSE;
  @NestedConfigurationProperty
  private RestTemplateProperties restTemplate = new RestTemplateProperties();

  @Data
  public static class RestTemplateProperties {

    private Boolean enabled = Boolean.FALSE;
    private Boolean errorResponse = Boolean.FALSE;
  }

}
