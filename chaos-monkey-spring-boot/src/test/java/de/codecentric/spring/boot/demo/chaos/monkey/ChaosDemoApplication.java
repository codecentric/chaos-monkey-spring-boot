package de.codecentric.spring.boot.demo.chaos.monkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Benjamin Wilms
 */
@SpringBootApplication
public class ChaosDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChaosDemoApplication.class, args);
    }
}
