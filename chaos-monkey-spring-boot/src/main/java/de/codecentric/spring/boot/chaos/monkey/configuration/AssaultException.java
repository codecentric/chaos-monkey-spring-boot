package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;

@Data
@ConfigurationProperties(prefix = "chaos.monkey.assaults.exception")
public class AssaultException {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssaultException.class);

    @Value("${type : java.lang.RuntimeException}")
    private String type;

    @Value("${arguments : #{null}}")
    private List<ExceptionArgument> arguments;

    public List<ExceptionArgument> getArguments() {
        return arguments;
    }

    public void setArguments(List<ExceptionArgument> arguments) {
        this.arguments = arguments;
    }

    @Data
    public static class ExceptionArgument {
        @NotNull
        private String className;

        @NotNull
        private String value;

        public Class<?> getClassType() {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found for class name: " + className);
            }
        }
    }

    @JsonIgnore
    public RuntimeException getExceptionInstance() {
        try {
            Class<? extends RuntimeException> exceptionClass = getExceptionClass();
            if (arguments == null) {
                Constructor<?> constructor = exceptionClass.getConstructor();
                return (RuntimeException) constructor.newInstance();
            } else {
                Constructor<?> constructor = exceptionClass.getConstructor(this.getExceptionArgumentTypes().toArray(new Class[0]));
                return (RuntimeException) constructor.newInstance(this.getExceptionArgumentValues().toArray(new Object[0]));
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Cannot instantiate the class for provided type: {}. Fallback: Throw RuntimeException", type);
            return new RuntimeException("Chaos Monkey - RuntimeException");
        }
    }

    @JsonIgnore
    public Class<? extends RuntimeException> getExceptionClass() throws ClassNotFoundException {
        if (type == null) {
            type = "java.lang.RuntimeException";
        }
        return (Class<? extends RuntimeException>) Class.forName(type);
    }

    @JsonIgnore
    public List<Class> getExceptionArgumentTypes() {
        return arguments.stream().map(ExceptionArgument::getClassType).collect(Collectors.toList());
    }

    @JsonIgnore
    private List<Object> getExceptionArgumentValues() {
        return arguments.stream().map(ExceptionArgument::getValue).collect(Collectors.toList());
    }
}

