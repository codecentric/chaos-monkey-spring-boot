package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Benjamin Wilms
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "chaos.monkey.assaults")
public class AssaultProperties {

    @Value("${level : 5}")
    private int level;

    @Value("${latencyRangeStart : 3000}")
    private int latencyRangeStart;

    @Value("${latencyRangeEnd : 15000}")
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
