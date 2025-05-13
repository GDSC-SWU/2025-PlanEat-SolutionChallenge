package com.gdgswu.planeat.domain.user.intolerance;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IntoleranceEnumConverter implements AttributeConverter<IntoleranceEnum, String> {

    @Override
    public String convertToDatabaseColumn(IntoleranceEnum attribute) {
        return attribute == null ? null : attribute.getName();
    }

    @Override
    public IntoleranceEnum convertToEntityAttribute(String dbValue) {
        return IntoleranceEnum.fromName(dbValue);
    }
}
