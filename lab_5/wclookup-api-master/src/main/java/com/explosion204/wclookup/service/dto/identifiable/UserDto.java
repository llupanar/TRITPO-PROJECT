package com.explosion204.wclookup.service.dto.identifiable;

import com.explosion204.wclookup.model.entity.User;
import com.explosion204.wclookup.service.validation.annotation.DtoClass;
import com.explosion204.wclookup.service.validation.annotation.IdentifiableDtoConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@IdentifiableDtoConstraint
@DtoClass
public class UserDto extends IdentifiableDto {
    @Size(max = 32)
    private String nickname = StringUtils.EMPTY;

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();

        userDto.id = user.getId();
        userDto.nickname = user.getNickname();

        return userDto;
    }
}
