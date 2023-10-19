package commons.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class SmartColorSerializer extends JsonSerializer<SmartColor> {
    @Override
    public void serialize(SmartColor color, JsonGenerator generator, SerializerProvider serializer) throws IOException {
        generator.writeString(color.toString());
    }
}
