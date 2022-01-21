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

  @NotNull private String type = "java.lang.RuntimeException";

  @NotNull private String method = "<init>";

  @NotNull @NestedConfigurationProperty
  private List<ExceptionArgument> arguments =
      Collections.singletonList(
          new ExceptionArgument(String.class.getName(), "Chaos Monkey - RuntimeException"));

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
    if ("<init>".equals(method)) {
      return new ThrowableConstructor(
          exceptionClass.asSubclass(Throwable.class).getConstructor(argumentTypes));
    } else {
      return new ThrowableStaticInitializer(exceptionClass.getMethod(method, argumentTypes));
    }
  }

  @JsonIgnore
  public Class<?> getExceptionClass() throws ClassNotFoundException {
    return Class.forName(type);
  }

  @JsonIgnore
  public List<Class<?>> getExceptionArgumentTypes() throws ClassNotFoundException {
    List<Class<?>> list = new ArrayList<>();
    for (ExceptionArgument argument : arguments) {
      list.add(argument.getClassType());
    }
    return list;
  }

  @JsonIgnore
  private List<Object> getExceptionArgumentValues()
      throws ClassNotFoundException, JsonProcessingException {
    List<Object> list = new ArrayList<>();
    for (ExceptionArgument argument : arguments) {
      Class<?> classType = argument.getClassType();
      String value = argument.getValue();
      try {
        list.add(objectMapper.convertValue(value, classType));
      } catch (IllegalArgumentException e) {
        list.add(objectMapper.readValue(value, classType));
      }
    }
    return list;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExceptionArgument {
    @NotNull private String className;

    @NotNull private String value;

    @JsonIgnore
    public Class<?> getClassType() throws ClassNotFoundException {
      return ClassUtils.forName(className, null);
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
