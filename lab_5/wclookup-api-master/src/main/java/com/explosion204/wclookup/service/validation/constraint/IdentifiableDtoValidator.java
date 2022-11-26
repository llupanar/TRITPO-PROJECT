package com.explosion204.wclookup.service.validation.constraint;

import com.explosion204.wclookup.service.dto.identifiable.IdentifiableDto;
import com.explosion204.wclookup.service.validation.annotation.IdentifiableDtoConstraint;
import com.explosion204.wclookup.service.validation.annotation.Nullable;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

@Component
public class IdentifiableDtoValidator implements ConstraintValidator<IdentifiableDtoConstraint, IdentifiableDto> {
    private static final String ID_FIELD = "id";

    @SneakyThrows
    @Override
    public boolean isValid(IdentifiableDto dto, ConstraintValidatorContext context) {
        if (dto.getId() == null) {
            // check if all fields are not null except for id and those fields that has Nullable annotation
            Field[] fields = dto.getClass().getFields();

            for (Field field : fields) {
                if (field.get(dto) == null
                    && !field.getName().equals(ID_FIELD)
                    && !field.isAnnotationPresent(Nullable.class)) {

                    return false;
                }
            }
        }

        return true;
    }
}
