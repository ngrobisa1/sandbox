package apienum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ApiEnumTest {

    private enum DummyStatus {
        IDLE,
        PROCESSING,
        FINISHED
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class DummyEntity {

        private String foo;
        private ApiEnum<DummyStatus> status;

    }

    private static ObjectMapper objMapper;


    @BeforeAll
    @SuppressWarnings("unchecked")
    public static void setUp() {
        var module = new SimpleModule()
                .addSerializer(ApiEnum.class, new ApiEnumSerializer())
                .addDeserializer(ApiEnum.class, new ApiEnumDeserializer<>());

        objMapper = new ObjectMapper();
        objMapper.registerModule(module);
    }


    @Test
    public void serialization_knownConstant() throws Exception {
        // arrange
        var entity = new DummyEntity("bar", ApiEnum.of(DummyStatus.PROCESSING));

        // act
        String serialized = objMapper.writeValueAsString(entity);

        // assert
        String expected = """
                    {
                        "foo": "bar",
                        "status": "processing"
                    }
                """;
        assertJsonEquals(expected, serialized);
    }

    @Test
    public void deserialization_nullValue() throws Exception {
        // arrange
        String serialized = """
                    {
                        "foo": "bar",
                        "status": null
                    }
                """;

        // act
        DummyEntity deserialized = objMapper.readValue(serialized, DummyEntity.class);

        // assert
        assertNull(deserialized.status);
    }

    @Test
    public void deserialization_knownConstant() throws Exception {
        // arrange
        String serialized = """
                    {
                        "foo": "bar",
                        "status": "pROcEsSinG"
                    }
                """;

        // act
        DummyEntity deserialized = objMapper.readValue(serialized, DummyEntity.class);

        // assert
        var expected = new DummyEntity("bar", ApiEnum.of(DummyStatus.PROCESSING));
        assertEquals(expected, deserialized);
        assertEquals(DummyStatus.PROCESSING, deserialized.getStatus().toEnum());
        assertFalse(deserialized.getStatus().isUnknown());
    }

    @Test
    public void deserialization_unknownConstant() throws Exception {
        // arrange
        String serialized = """
                    {
                        "foo": "bar",
                        "status": "some-non-existing-status"
                    }
                """;

        // act
        DummyEntity deserialized = objMapper.readValue(serialized, DummyEntity.class);

        // assert
        var expected = new DummyEntity("bar", ApiEnum.of(DummyStatus.class, "some-non-existing-status"));
        assertEquals(expected, deserialized);
        assertTrue(deserialized.getStatus().isUnknown());
    }

    @Test
    public void serialization_knownConstant_keyWithNonStandardCasing() throws Exception {
        // arrange
        String serialized = """
                    {
                        "foo": "bar",
                        "status": "PROcessing"
                    }
                """;
        DummyEntity deserialized = objMapper.readValue(serialized, DummyEntity.class);

        // act
        String reserialized = objMapper.writeValueAsString(deserialized);

        // assert
        assertJsonEquals(serialized, reserialized);
    }


    @Test
    public void equalsAndHashCode() {
        ApiEnum<DummyStatus> dummyStatus1;
        ApiEnum<DummyStatus> dummyStatus2;

        // reflexive and symmetric properties
        dummyStatus1 = ApiEnum.of(DummyStatus.IDLE);
        dummyStatus2 = ApiEnum.of(DummyStatus.IDLE);
        assertEquals(dummyStatus1, dummyStatus1);
        assertEquals(dummyStatus1, dummyStatus2);
        assertEquals(dummyStatus2, dummyStatus1);
        assertEquals(dummyStatus1.hashCode(), dummyStatus2.hashCode());

        // keys with non-standard casing
        dummyStatus1 = ApiEnum.of(DummyStatus.IDLE);
        dummyStatus2 = ApiEnum.of(DummyStatus.class, "iDlE");
        assertEquals(dummyStatus1, dummyStatus2);
        assertEquals(dummyStatus1.hashCode(), dummyStatus2.hashCode());
    }


    private void assertJsonEquals(String expectedJson, String actualJson) throws JsonProcessingException {
        assertEquals(objMapper.readTree(expectedJson), objMapper.readTree(actualJson));
    }

}
