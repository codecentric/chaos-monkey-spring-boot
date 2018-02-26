package de.mrbwilms.spring.boot.chaos.monkey.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Benjamin Wilms
 */
public class ApplicationContextProvider implements ApplicationContextAware {

    private ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        final ApplicationContextProvider cprovider = new ApplicationContextProvider();
        return cprovider.getContext();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    ApplicationContext getContext() {
        return context;
    }
}
