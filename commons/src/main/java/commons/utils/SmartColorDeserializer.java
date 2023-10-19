package commons.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class SmartColorDeserializer extends JsonDeserializer<SmartColor> {
    @Override

    public SmartColor deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return SmartColor.valueOf(parser.getValueAsString());
        }
    }
