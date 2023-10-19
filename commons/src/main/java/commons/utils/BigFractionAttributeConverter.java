package commons.utils;

import com.github.kiprobinson.bigfraction.BigFraction;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * This class is used to convert BigFraction to String and vice versa automatically
 */
@Converter(autoApply = true)
public class BigFractionAttributeConverter implements AttributeConverter<BigFraction, String> {
    @Override
    public String convertToDatabaseColumn(BigFraction attribute) {
        return attribute.toString();
    }

    @Override
    public BigFraction convertToEntityAttribute(String dbData) {
        return BigFraction.valueOf(dbData);
    }
}
