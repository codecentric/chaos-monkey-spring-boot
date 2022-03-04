/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codecentric.spring.boot.chaos.monkey.assaults;

import static org.junit.jupiter.api.Assertions.assertThrows;

import de.codecentric.spring.boot.chaos.monkey.component.MetricEventPublisher;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultException;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeySettings;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/** @author Thorsten Deelmann */
class ExceptionAssaultTest {

  @Mock MetricEventPublisher metricsMock;

  @Test
  void throwsRuntimeExceptionWithDefaultAssaultSettings() {
    ExceptionAssault exceptionAssault = new ExceptionAssault(getChaosMonkeySettings(), metricsMock);
    assertThrows(RuntimeException.class, exceptionAssault::attack);
  }

  @Test
  void throwsRuntimeExceptionWithNullTypeAndNullArgument() {
    ChaosMonkeySettings settings = getChaosMonkeySettings();
    settings.getAssaultProperties().setException(null);
    ExceptionAssault exceptionAssault = new ExceptionAssault(settings, metricsMock);
    assertThrows(RuntimeException.class, exceptionAssault::attack);
  }

  @Test
  void throwsDefaultRuntimeExceptionWithNullTypeAndNonNullArgument() {
    String exceptionArgumentClassName = "java.lang.String";
    String exceptionArgumentValue = "Chaos Monkey - RuntimeException";

    ChaosMonkeySettings settings = getChaosMonkeySettings();
    settings
        .getAssaultProperties()
        .setException(
            getAssaultException(null, exceptionArgumentClassName, exceptionArgumentValue));

    ExceptionAssault exceptionAssault = new ExceptionAssault(settings, metricsMock);
    assertThrows(RuntimeException.class, exceptionAssault::attack, exceptionArgumentValue);
  }

  @Test
  void throwsRuntimeExceptionWithNonNullTypeAndNullArgument() {
    ChaosMonkeySettings settings = getChaosMonkeySettings();
    settings
        .getAssaultProperties()
        .setException(getAssaultException("java.lang.ArithmeticException", null, null));

    ExceptionAssault exceptionAssault = new ExceptionAssault(settings, metricsMock);
    assertThrows(ArithmeticException.class, exceptionAssault::attack);
  }

  @Test
  void throwsRuntimeExceptionWithNonnullTypeAndNonNullArgument() {
    String exceptionArgumentClassName = "java.lang.String";
    String exceptionArgumentValue = "ArithmeticException Test";

    ChaosMonkeySettings settings = getChaosMonkeySettings();
    settings
        .getAssaultProperties()
        .setException(
            getAssaultException(
                "java.lang.ArithmeticException",
                exceptionArgumentClassName,
                exceptionArgumentValue));

    ExceptionAssault exceptionAssault = new ExceptionAssault(settings, metricsMock);
    assertThrows(ArithmeticException.class, exceptionAssault::attack, exceptionArgumentValue);
  }

  @Test
  void throwsGeneralException() {
    ChaosMonkeySettings settings = getChaosMonkeySettings();
    settings
        .getAssaultProperties()
        .setException(getAssaultException("java.io.IOException", null, null));

    ExceptionAssault exceptionAssault = new ExceptionAssault(settings, metricsMock);
    assertThrows(IOException.class, exceptionAssault::attack);
  }

  @Test
  void throwsError() {
    ChaosMonkeySettings settings = getChaosMonkeySettings();
    settings
        .getAssaultProperties()
        .setException(getAssaultException("java.lang.OutOfMemoryError", null, null));

    ExceptionAssault exceptionAssault = new ExceptionAssault(settings, metricsMock);
    assertThrows(OutOfMemoryError.class, exceptionAssault::attack);
  }

  private ChaosMonkeySettings getChaosMonkeySettings() {
    ChaosMonkeySettings settings = new ChaosMonkeySettings();
    settings.setAssaultProperties(getDefaultAssaultProperties());
    return settings;
  }

  private AssaultProperties getDefaultAssaultProperties() {
    AssaultProperties assaultProperties = new AssaultProperties();
    assaultProperties.setLevel(5);
    assaultProperties.setLatencyRangeStart(1000);
    assaultProperties.setLatencyRangeEnd(3000);
    assaultProperties.setLatencyActive(true);
    assaultProperties.setExceptionsActive(false);
    assaultProperties.setException(getAssaultException(null, null, null));
    assaultProperties.setKillApplicationActive(false);
    assaultProperties.setWatchedCustomServices(null);

    return assaultProperties;
  }

  private AssaultException getAssaultException(
      String exceptionClassName, String argumentClass, String argumentValue) {
    AssaultException assaultException = new AssaultException();

    if (exceptionClassName != null) {
      assaultException.setType(exceptionClassName);
    }

    if (argumentClass != null) {
      AssaultException.ExceptionArgument argument = new AssaultException.ExceptionArgument();
      argument.setType(argumentClass);
      argument.setValue(argumentValue);
      assaultException.setArguments(Collections.singletonList(argument));
    }

    return assaultException;
  }
}
