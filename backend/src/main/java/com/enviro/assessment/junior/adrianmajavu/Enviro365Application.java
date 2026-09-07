package com.enviro.assessment.junior.adrianmajavu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. @SpringBootApplication bundles three annotations:
 *  - @Configuration: this class can define beans
 *  - @EnableAutoConfiguration: Spring Boot wires up the DB, web server,
 *    JSON converter etc. based on what's on the classpath (pom.xml)
 *  - @ComponentScan: Spring scans this package and sub-packages for
 *    @Service/@Repository/@RestController classes and registers them as
 *    beans automatically - which is why every class above just needed
 *    the annotation, with zero manual wiring.
 */
@SpringBootApplication
public class Enviro365Application {
    public static void main(String[] args) {
        SpringApplication.run(Enviro365Application.class, args);
    }
}
