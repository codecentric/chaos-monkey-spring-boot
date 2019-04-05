package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Constructor;
import java.util.Collections;
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

        @JsonIgnore
        public Class<?> getClassType() {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found for class name: " + className);
            }
        }
    }

    private static class TypeLoophole<E extends Throwable> {
        private final E payload;

        @SuppressWarnings({"rawtypes", "unchecked"})
        static void throwException(Throwable e) {
            TypeLoophole<RuntimeException> instance = new TypeLoophole(e);
            instance.throwIt();
        }

        TypeLoophole(E exception) {
            payload = exception;
        }

        void throwIt() throws E {
            throw payload;
        }
    }

    @JsonIgnore
    public void throwExceptionInstance() {
        Exception instance;
        try {
            Class<? extends Exception> exceptionClass = getExceptionClass();
            if (arguments == null) {
                Constructor<? extends Exception> constructor = exceptionClass.getConstructor();
                instance = constructor.newInstance();
            } else {
                Constructor<? extends Exception> constructor = exceptionClass.getConstructor(this.getExceptionArgumentTypes().toArray(new Class[0]));
                instance = constructor.newInstance(this.getExceptionArgumentValues().toArray(new Object[0]));
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Cannot instantiate the class for provided type: {}. Fallback: Throw RuntimeException", type);
            instance = new RuntimeException("Chaos Monkey - RuntimeException");
        }

        TypeLoophole.throwException(instance);
    }

    @JsonIgnore
    public Class<? extends Exception> getExceptionClass() throws ClassNotFoundException {
        if (type == null) {
            // use Chaos Monkey default Runtime Exception
            type = "java.lang.RuntimeException";
            ExceptionArgument exceptionArgument = new ExceptionArgument();
            exceptionArgument.setClassName("java.lang.String");
            exceptionArgument.setValue("Chaos Monkey - RuntimeException");
            arguments = Collections.singletonList(exceptionArgument);
        }
        return Class.forName(type).asSubclass(Exception.class);
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

