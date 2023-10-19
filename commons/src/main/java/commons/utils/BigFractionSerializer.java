package commons.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.kiprobinson.bigfraction.BigFraction;

import java.io.IOException;

public class BigFractionSerializer extends JsonSerializer<BigFraction> {
    @Override
    public void serialize(BigFraction value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }
}
