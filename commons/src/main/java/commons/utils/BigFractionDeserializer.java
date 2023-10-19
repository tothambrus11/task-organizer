package commons.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.kiprobinson.bigfraction.BigFraction;

import java.io.IOException;

public class BigFractionDeserializer extends JsonDeserializer<BigFraction> {
    @Override
    public BigFraction deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return BigFraction.valueOf(p.getValueAsString());
    }
}
