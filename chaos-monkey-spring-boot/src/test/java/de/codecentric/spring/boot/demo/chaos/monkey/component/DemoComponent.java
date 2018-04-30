package de.codecentric.spring.boot.demo.chaos.monkey.component;

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
