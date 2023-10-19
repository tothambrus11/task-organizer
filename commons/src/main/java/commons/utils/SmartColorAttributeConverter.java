package commons.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SmartColorAttributeConverter implements AttributeConverter<SmartColor, String> {
    @Override
    public String convertToDatabaseColumn(SmartColor color){
        return color.toString();
    }

    @Override
    public SmartColor convertToEntityAttribute(String dbData){
        return SmartColor.valueOf(dbData);
    }
}
