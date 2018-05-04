package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Benjamin Wilms
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey.assaults")
public class AssaultProperties {

    @Value("${level : 5}")
    @Min(value = 1)
    @Max(value = 10)
    private int level;

    @Value("${latencyRangeStart : 1000}")
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private int latencyRangeStart;

    @Value("${latencyRangeEnd : 3000}")
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private int latencyRangeEnd;

    @Value("${latencyActive : true}")
    private boolean latencyActive;

    @Value("${exceptionsActive : false}")
    private boolean exceptionsActive;

    @Value("${killApplicationActive : false}")
    private boolean killApplicationActive;

    @Value("${restartApplicationActive : false}")
    private boolean restartApplicationActive;

    @JsonIgnore
    public int getTroubleRandom() {
        return RandomUtils.nextInt(1, 11);
    }

    @JsonIgnore
    public int getExceptionRandom() {
        return RandomUtils.nextInt(0, 10);
    }


}
