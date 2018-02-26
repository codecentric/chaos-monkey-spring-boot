package de.mrbwilms.spring.boot.chaos.monkey.configuration;

import de.mrbwilms.spring.boot.chaos.monkey.aop.SpringControllerAspect;
import de.mrbwilms.spring.boot.chaos.monkey.aop.SpringRestControllerAspect;
import de.mrbwilms.spring.boot.chaos.monkey.aop.SpringServiceAspect;
import de.mrbwilms.spring.boot.chaos.monkey.component.ChaosMonkey;
import de.mrbwilms.spring.boot.chaos.monkey.conditions.AttackControllerCondition;
import de.mrbwilms.spring.boot.chaos.monkey.conditions.AttackRestControllerCondition;
import de.mrbwilms.spring.boot.chaos.monkey.conditions.AttackServiceCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Benjamin Wilms
 */
@Configuration
@Profile("chaos-monkey")
public class ChaosMonkeyConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkey.class);

    public ChaosMonkeyConfiguration() {
        try {
            String chaosLogo = StreamUtils.copyToString(new ClassPathResource("chaos-logo.txt").getInputStream(), Charset.defaultCharset());
            LOGGER.info(chaosLogo);
        } catch (IOException e) {
            LOGGER.info("Chaos Monkey - ready to do evil");
        }


    }

    @Autowired
    private Environment env;

    @Bean
    public ChaosMonkey chaosMonkey() {
        return new ChaosMonkey(env);
    }

    @Bean
    @Conditional(AttackControllerCondition.class)
    public SpringControllerAspect controllerAspect() {
        return new SpringControllerAspect(chaosMonkey());
    }

    @Bean
    @Conditional(AttackRestControllerCondition.class)
    public SpringRestControllerAspect restControllerAspect() {
        return new SpringRestControllerAspect(chaosMonkey());
    }

    @Bean
    @Conditional(AttackServiceCondition.class)
    public SpringServiceAspect serviceAspect() {
        return new SpringServiceAspect(chaosMonkey());
    }
}
