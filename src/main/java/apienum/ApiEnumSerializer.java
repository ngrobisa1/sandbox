package apienum;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ApiEnumSerializer<E extends Enum<E>> extends JsonSerializer<ApiEnum<E>> {

    @Override
    public void serialize(ApiEnum<E> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toString());
    }

}
