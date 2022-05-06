/*
 * Copyright 2018-2022 the original author or authors.
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
package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.ClassUtils;

@Data
public class AssaultException {

    private static final Logger Logger = LoggerFactory.getLogger(AssaultException.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * special value used to represent constructors. "<init>" was chosen because it
     * is used as jvm internal name for constructors, which means no method could be
     * named like this.
     */
    private static final String CONSTRUCTOR = "<init>";

    @NotNull
    private String type = "java.lang.RuntimeException";

    @NotNull
    private String method = CONSTRUCTOR;

    @NotNull
    @NestedConfigurationProperty
    private List<ExceptionArgument> arguments = Collections
            .singletonList(new ExceptionArgument(String.class.getName(), "Chaos Monkey - RuntimeException"));

    @SneakyThrows
    @JsonIgnore
    public void throwExceptionInstance() {
        throw getThrowable();
    }

    @JsonIgnore
    private Throwable getThrowable() {
        Throwable instance;
        try {
            ThrowableCreator creator = getCreator();
            instance = creator.create(getExceptionArgumentValues());
        } catch (ReflectiveOperationException | ClassCastException | JsonProcessingException e) {
            Logger.warn("Failed to create custom exception. Fallback: Throw RuntimeException");
            instance = new RuntimeException("Chaos Monkey - RuntimeException (Fallback)", e);
        }
        return instance;
    }

    @JsonIgnore
    public ThrowableCreator getCreator() throws ReflectiveOperationException {
        Class<?> exceptionClass = getExceptionClass();
        Class<?>[] argumentTypes = getExceptionArgumentTypes().toArray(new Class[0]);
        if (CONSTRUCTOR.equals(method)) {
            return new ThrowableConstructor(exceptionClass.asSubclass(Throwable.class).getConstructor(argumentTypes));
        } else {
            return new ThrowableStaticInitializer(exceptionClass.getMethod(method, argumentTypes));
        }
    }

    @JsonIgnore
    public Class<?> getExceptionClass() throws ClassNotFoundException {
        return Class.forName(type);
    }

    private List<Class<?>> getExceptionArgumentTypes() throws ClassNotFoundException {
        List<Class<?>> exceptionArgumentTypes = new ArrayList<>();
        for (ExceptionArgument argument : arguments) {
            exceptionArgumentTypes.add(argument.getClassType());
        }
        return exceptionArgumentTypes;
    }

    private List<Object> getExceptionArgumentValues() throws ClassNotFoundException, JsonProcessingException {
        List<Object> exceptionArgumentValues = new ArrayList<>();
        for (ExceptionArgument argument : arguments) {
            Class<?> classType = argument.getClassType();
            String value = argument.getValue();
            try {
                // this mostly works for primitive values and strings
                exceptionArgumentValues.add(objectMapper.convertValue(value, classType));
            } catch (IllegalArgumentException e) {
                // treat value as json encoded otherwise
                exceptionArgumentValues.add(objectMapper.readValue(value, classType));
            }
        }
        return exceptionArgumentValues;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExceptionArgument {
        @NotNull
        private String type;

        @NotNull
        private String value;

        @JsonIgnore
        public Class<?> getClassType() throws ClassNotFoundException {
            return ClassUtils.forName(type, null);
        }
    }

    private interface ThrowableCreator {
        Throwable create(List<?> arguments) throws ReflectiveOperationException;
    }

    @RequiredArgsConstructor
    private static class ThrowableConstructor implements ThrowableCreator {
        private final Constructor<? extends Throwable> constructor;

        @Override
        public Throwable create(List<?> arguments) throws ReflectiveOperationException {
            return constructor.newInstance(arguments.toArray());
        }
    }

    @RequiredArgsConstructor
    private static class ThrowableStaticInitializer implements ThrowableCreator {
        private final Method initializer;

        @Override
        public Throwable create(List<?> arguments) throws ReflectiveOperationException {
            return (Throwable) initializer.invoke(null, arguments.toArray());
        }
    }
}
