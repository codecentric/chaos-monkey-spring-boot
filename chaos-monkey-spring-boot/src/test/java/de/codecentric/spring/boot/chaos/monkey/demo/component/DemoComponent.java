package de.codecentric.spring.boot.chaos.monkey.demo.component;

import org.springframework.stereotype.Component;

/**
 * @author Benjamin Wilms
 */
@Component
public class DemoComponent {

    public String sayHello() {
        return "Hello!";
    }

}
