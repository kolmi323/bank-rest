package com.example.bankcards.util.converter;

import com.example.bankcards.dto.response.user.UserResponse;
import com.example.bankcards.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserResponseConverter implements Converter<UserEntity, UserResponse> {
    @Override
    public UserResponse convert(UserEntity source) {
        return new UserResponse(
                source.getId(),
                formatFullName(source),
                source.getEmail()
        );
    }

    private String formatFullName(UserEntity userEntity) {
        return String.format("%s %s %s",
                userEntity.getLastName(),
                userEntity.getFirstName(),
                userEntity.getMiddleName() == null ? "" : userEntity.getMiddleName()
        ).trim();
    }
}
