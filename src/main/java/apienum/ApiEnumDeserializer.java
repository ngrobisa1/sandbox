package apienum;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;

public class ApiEnumDeserializer<E extends Enum<E>> extends JsonDeserializer<ApiEnum<E>> implements ContextualDeserializer {

    private JavaType valueType;

    @Override
    @SuppressWarnings("unchecked")
    public ApiEnum<E> deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {

        String enumKey = ctx.readValue(parser, String.class);

        return ApiEnum.of((Class<E>) valueType.getRawClass(), enumKey);
    }

    @Override
    public JsonDeserializer<ApiEnum<E>> createContextual(DeserializationContext deserializationContext, BeanProperty property) {

        JavaType wrapperType = property.getType();
        JavaType valueType = wrapperType.containedType(0);
        ApiEnumDeserializer<E> deserializer = new ApiEnumDeserializer<>();
        deserializer.valueType = valueType;

        return deserializer;
    }


}
