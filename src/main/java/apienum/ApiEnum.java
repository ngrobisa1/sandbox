package apienum;

import java.util.Objects;

/**
 * Features:
 * - case-insensitive deserialization
 * - resilient deserialization of unknown constants
 * - unknown constant serialization support
 * - can replace enums seamlessly
 * - honors the original deserialized value when re-serializing
 * - easy convertion from and to enums
 */
public final class ApiEnum<E extends Enum<E>> {

    private final String key;
    private final boolean unknown;
    private final Enum<E> enumConstant;


    public static <E extends Enum<E>> ApiEnum<E> of(Enum<E> enumConstant) {
        return new ApiEnum(enumConstant, normalizeKey(enumConstant.name()), false);
    }


    public static <E extends Enum<E>> ApiEnum<E> of(Class<E> enumType, String key) {
        String requestedKey = normalizeKey(key);
        E[] enumConstants = enumType.getEnumConstants();
        boolean unknown = true;
        E constant = null;

        for (E enumConstant : enumConstants) {
            String constantKey = normalizeKey(enumConstant.name());

            if (constantKey.equals(requestedKey)) {
                unknown = false;
                constant = enumConstant;
                break;
            }
        }

        return new ApiEnum<>(constant, key, unknown);
    }

    private static String normalizeKey(String key) {
        return key.toLowerCase();
    }


    private ApiEnum(E enumConstant, String key, boolean unknown) {
        this.key = key;
        this.unknown = unknown;
        this.enumConstant = enumConstant;
    }


    public boolean isUnknown() {
        return unknown;
    }

    public Enum<E> toEnum() {
        return enumConstant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiEnum<?> apiEnum = (ApiEnum<?>) o;
        return unknown == apiEnum.unknown
                && (key == apiEnum.key || (key != null && normalizeKey(key).equals(normalizeKey(apiEnum.key))))
                && Objects.equals(enumConstant, apiEnum.enumConstant);
    }

    @Override
    public int hashCode() {
        String keyToHash = key != null ? normalizeKey(key) : null;

        return Objects.hash(keyToHash, unknown, enumConstant);
    }

    @Override
    public String toString() {
        return key;
    }

}
