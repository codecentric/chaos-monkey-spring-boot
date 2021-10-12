/*
 * Copyright 2021 the original author or authors.
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
 *
 */
package de.codecentric.spring.boot.chaos.monkey.assaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/*
 * This Status class is used as an example of a class with no public constructors
 * but instead leverages static "factory" methods for creation.
 *
 * This particular example is based off of exceptions thrown by the grpc-java project.
 */
public final class Status {

  public static Status fromCodeValue(int codeValue) {
    return STATUS_LIST.get(codeValue);
  }

  private static List<Status> buildStatusList() {
    TreeMap<Integer, Status> canonicalizer = new TreeMap<>();
    for (Code code : Code.values()) {
      Status replaced = canonicalizer.put(code.value(), new Status(code));
      if (replaced != null) {
        throw new IllegalStateException(
            "Code value duplication between " + replaced.getCode().name() + " & " + code.name());
      }
    }
    return Collections.unmodifiableList(new ArrayList<>(canonicalizer.values()));
  }

  public static final List<Status> STATUS_LIST = buildStatusList();

  public enum Code {
    OK(0),
    UNKNOWN(1),
    NOT_FOUND(2);

    private final int value;

    Code(int value) {
      this.value = value;
    }

    public int value() {
      return value;
    }

    public Status toStatus() {
      return STATUS_LIST.get(value);
    }
  }

  private final Code code;

  private Status(Code code) {
    this.code = code;
  }

  public Code getCode() {
    return code;
  }

  public static final Status OK = Code.OK.toStatus();
  public static final Status UNKNOWN = Code.UNKNOWN.toStatus();
  public static final Status NOT_FOUND = Code.NOT_FOUND.toStatus();


}
