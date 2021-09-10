package de.codecentric.spring.boot.chaos.monkey.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "chaos.monkey.assaults.exception")
public class AssaultException {

  private static final Logger Logger = LoggerFactory.getLogger(AssaultException.class);

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

  @JsonIgnore
  @SneakyThrows
  public void throwExceptionInstance() {
    Throwable instance;
    try {
      Class<? extends Throwable> exceptionClass = getExceptionClass();
      if (arguments == null) {
        Constructor<? extends Throwable> constructor = exceptionClass.getConstructor();
        instance = constructor.newInstance();
      } else {
        Constructor<? extends Throwable> constructor =
            exceptionClass.getConstructor(this.getExceptionArgumentTypes().toArray(new Class[0]));
        instance =
            constructor.newInstance(this.getExceptionArgumentValues().toArray(new Object[0]));
      }
    } catch (ReflectiveOperationException e) {
      Logger.warn(
          "Cannot instantiate the class for provided type: {}. Fallback: Throw RuntimeException",
          type);
      instance = new RuntimeException("Chaos Monkey - RuntimeException");
    }

    throw instance;
  }

  @JsonIgnore
  public Class<? extends Throwable> getExceptionClass() throws ClassNotFoundException {
    if (type == null) {
      // use Chaos Monkey default Runtime Exception
      type = "java.lang.RuntimeException";
      ExceptionArgument exceptionArgument = new ExceptionArgument();
      exceptionArgument.setClassName("java.lang.String");
      exceptionArgument.setValue("Chaos Monkey - RuntimeException");
      arguments = Collections.singletonList(exceptionArgument);
    }
    return Class.forName(type).asSubclass(Throwable.class);
  }

  @JsonIgnore
  public List<Class> getExceptionArgumentTypes() {
    return arguments.stream().map(ExceptionArgument::getClassType).collect(Collectors.toList());
  }

  @JsonIgnore
  private List<Object> getExceptionArgumentValues() {
    return arguments.stream().map(ExceptionArgument::getValue).collect(Collectors.toList());
  }

  @Data
  public static class ExceptionArgument {

    @NotNull private String className;

    @NotNull private String value;

    @JsonIgnore
    public Class<?> getClassType() {
      try {
        return Class.forName(className);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Class not found for class name: " + className);
      }
    }
  }
}
