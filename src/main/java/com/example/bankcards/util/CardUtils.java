package com.example.bankcards.util;

import org.springframework.stereotype.Component;

@Component
public class CardUtils {
    public String maskingNumber(String number) {
        int endIndex = number.length();
        int beginIndex = endIndex - 4;

        StringBuilder maskedNumber = new StringBuilder();
        maskedNumber.append("**** **** **** ");
        maskedNumber.append(number, beginIndex, endIndex);

        return maskedNumber.toString();
    }
}
