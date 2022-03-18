package de.codecentric.spring.boot.chaos.monkey.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

class AssaultExceptionTest {
  @Test
  void testStandardCase() {
    AssaultException assaultException = new AssaultException();

    RuntimeException exception =
        assertThrows(RuntimeException.class, assaultException::throwExceptionInstance);
    assertThat(exception.getMessage()).isEqualTo("Chaos Monkey - RuntimeException");
  }

  @Test
  void testFaultyTypeConfiguration() {
    AssaultException assaultException = new AssaultException();
    assaultException.setType("NonExistentClass");

    RuntimeException exception =
        assertThrows(RuntimeException.class, assaultException::throwExceptionInstance);
    assertThat(exception.getMessage()).isEqualTo("Chaos Monkey - RuntimeException (Fallback)");
  }

  @Test
  void testFaultyMethodConfiguration() {
    AssaultException assaultException = new AssaultException();
    assaultException.setMethod("NonExistentMethod");

    RuntimeException exception =
        assertThrows(RuntimeException.class, assaultException::throwExceptionInstance);
    assertThat(exception.getMessage()).isEqualTo("Chaos Monkey - RuntimeException (Fallback)");
  }

  @Test
  void testFaultyArgumentTypeConfiguration() {
    AssaultException assaultException = new AssaultException();
    assaultException.setArguments(
        Collections.singletonList(
            new AssaultException.ExceptionArgument("NonExistentClass", "value")));

    RuntimeException exception =
        assertThrows(RuntimeException.class, assaultException::throwExceptionInstance);
    assertThat(exception.getMessage()).isEqualTo("Chaos Monkey - RuntimeException (Fallback)");
  }

  @Test
  void testFaultyArgumentValueConfiguration() {
    AssaultException assaultException = new AssaultException();
    assaultException.setArguments(
        Collections.singletonList(new AssaultException.ExceptionArgument("int", "notAnInteger")));

    RuntimeException exception =
        assertThrows(RuntimeException.class, assaultException::throwExceptionInstance);
    assertThat(exception.getMessage()).isEqualTo("Chaos Monkey - RuntimeException (Fallback)");
  }

  @RequiredArgsConstructor
  private static class IntException extends RuntimeException {
    private final int anInt;
  }

  @Test
  void testPrimitiveArgumentValueConfiguration() {
    AssaultException assaultException = new AssaultException();
    assaultException.setType(IntException.class.getName());
    assaultException.setArguments(
        Collections.singletonList(new AssaultException.ExceptionArgument("int", "1")));

    IntException exception =
        assertThrows(IntException.class, assaultException::throwExceptionInstance);
    assertThat(exception.anInt).isEqualTo(1);
  }

  @RequiredArgsConstructor
  private static class EnumException extends RuntimeException {
    private final ChaosTarget enumValue;
  }

  @Test
  void testEnumArgumentValueConfiguration() {
    AssaultException assaultException = new AssaultException();
    assaultException.setType(EnumException.class.getName());
    assaultException.setArguments(
        Collections.singletonList(
            new AssaultException.ExceptionArgument(ChaosTarget.class.getName(), "CONTROLLER")));

    EnumException exception =
        assertThrows(EnumException.class, assaultException::throwExceptionInstance);
    assertThat(exception.enumValue).isEqualTo(ChaosTarget.CONTROLLER);
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class ComplexObject {
    private int i;
    private ComplexObject complexObject;
  }

  @RequiredArgsConstructor
  private static class ComplexObjectException extends RuntimeException {
    private final ComplexObject complexObject;
  }

  @Test
  void testComplexObjectArgumentValueConfiguration() {
    AssaultException assaultException = new AssaultException();
    assaultException.setType(ComplexObjectException.class.getName());
    assaultException.setArguments(
        Collections.singletonList(
            new AssaultException.ExceptionArgument(
                ComplexObject.class.getName(),
                "{\"i\": 1, \"complexObject\": {\"i\": 2, \"complexObject\": null}}")));

    ComplexObjectException exception =
        assertThrows(ComplexObjectException.class, assaultException::throwExceptionInstance);
    assertThat(exception.complexObject).isEqualTo(new ComplexObject(1, new ComplexObject(2, null)));
  }

  @SuppressWarnings("unused")
  public static Throwable createThrowable(String message) {
    return new RuntimeException(message);
  }

  @Test
  void testStaticInitializerConfiguration() {
    AssaultException assaultException = new AssaultException();
    assaultException.setType(AssaultExceptionTest.class.getName());
    assaultException.setMethod("createThrowable");
    assaultException.setArguments(
        Collections.singletonList(
            new AssaultException.ExceptionArgument(
                String.class.getName(), "test static initializer")));

    RuntimeException exception =
        assertThrows(RuntimeException.class, assaultException::throwExceptionInstance);
    assertThat(exception.getMessage()).isEqualTo("test static initializer");
  }
}
