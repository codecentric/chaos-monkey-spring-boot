package de.codecentric.spring.boot.demo.chaos.monkey.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Benjamin Wilms
 */
@org.springframework.web.bind.annotation.RestController
public class DemoRestController {

    @GetMapping("/rest/hello")
    public ResponseEntity<String> sayHello()
    {
        return new ResponseEntity<String>("Hello", HttpStatus.OK);
    }
}
