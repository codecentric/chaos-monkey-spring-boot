package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.client.RestClient;

public class ChaosMonkeyRestClientPostProcessor implements BeanPostProcessor {

    private final ChaosMonkeyRestClientWatcher interceptor;

    public ChaosMonkeyRestClientPostProcessor(final ChaosMonkeyRestClientWatcher interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof RestClient client) {
            // a built RestClient is immutable, so create a copy with the interceptor added
            return client.mutate().requestInterceptors(interceptors -> {
                if (!interceptors.contains(interceptor)) {
                    interceptors.add(interceptor);
                }
            }).build();
        }
        return bean;
    }
}