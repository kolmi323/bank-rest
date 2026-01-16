package com.example.bankcards.util;

import com.example.bankcards.exception.NumberEncryptorException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
@Converter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CardNumberEncryptor implements AttributeConverter<String, String> {
    String SECRETE_KEY  =  "SecurityApp@2024" ;
    String ENC  =  "AES" ;
    String ALGORITHM  =  "AES/ECB/PKCS5Padding" ;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        String result = null;
        try {
            Key key = new SecretKeySpec(SECRETE_KEY.getBytes(), ENC);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            result = Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new NumberEncryptorException("Ошибка кодирования номера карты", e.getCause());
        }
        return result;
    }

    @Override
    public String convertToEntityAttribute(String attribute) {
        String result = null;
        try {
            Key key = new SecretKeySpec(SECRETE_KEY.getBytes(), ENC);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            result = new String (cipher.doFinal(Base64.getDecoder().decode(attribute)));
        } catch (Exception e) {
            throw new NumberEncryptorException("Ошибка декодирования номера карты", e.getCause());
        }
        return result;
    }
}
