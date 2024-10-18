package ru.relex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.relex.configuration.RabbitConfiguration;

@SpringBootApplication
@Import(RabbitConfiguration.class)
public class RestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class);
    }
}