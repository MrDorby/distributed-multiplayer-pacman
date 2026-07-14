package it.unibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    // TODO: Create tests for the service by doing
    // simple http request to the service.
    // https://github.com/mohyehia/spring-boot-testing/blob/main/src/test/java/com/moh/yehia/testing/repository/BaseMongoContainer.java
}