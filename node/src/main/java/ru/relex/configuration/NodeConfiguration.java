package ru.relex.configuration;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.relex.utils.CryptoTool;

@Configuration
public class NodeConfiguration {

    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool cryptoTool() {
        var minHashLength = 10;
        return new CryptoTool(salt);
    }
}