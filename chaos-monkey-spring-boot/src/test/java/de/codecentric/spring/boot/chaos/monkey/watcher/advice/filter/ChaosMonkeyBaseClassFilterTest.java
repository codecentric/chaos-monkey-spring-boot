/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.spring.boot.chaos.monkey.watcher.advice.filter;

import static org.assertj.core.api.Assertions.assertThat;

import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import de.codecentric.spring.boot.demo.chaos.monkey.component.DemoComponent;
import de.codecentric.spring.boot.demo.chaos.monkey.component.InheritedFinalMethodComponent;
import de.codecentric.spring.boot.demo.chaos.monkey.service.DemoService;
import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;

class ChaosMonkeyBaseClassFilterTest {
    private final ChaosMonkeyBaseClassFilter filter = new ChaosMonkeyBaseClassFilter(new WatcherProperties());

    @Test
    void excludesInheritedFinalMethodsThatCannotBeProxied() {
        assertThat(filter.matches(InheritedFinalMethodComponent.class)).isFalse();
        assertThat(filter.matches(DemoComponent.class)).isTrue();
    }

    @Test
    void allowsServicesWithoutFinalInstanceMethods() {
        assertThat(filter.matches(DemoService.class)).isTrue();
    }

    @Test
    void allowsJdkProxiesDespiteTheirFinalClassModifier() {
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Runnable.class}, (instance, method, arguments) -> null);
        assertThat(filter.matches(proxy.getClass())).isTrue();
    }

}
