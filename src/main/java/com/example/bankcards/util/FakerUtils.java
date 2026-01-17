package com.example.bankcards.util;

import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Component
public class FakerUtils {
    public String generateCardNumber() {
        Faker faker = new Faker();
        return faker.finance().creditCard();
    }
}
