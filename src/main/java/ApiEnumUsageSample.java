import apienum.ApiEnum;
import lombok.Data;

public class ApiEnumUsageSample {

    private enum Composer {
        CHOPIN,
        LISZT,
        PROKOFIEV
    }

    @Data
    private static class ClassicalWork {
        final String id;
        final ApiEnum<Composer> composer;
        final String title;
    }

    public static void main(String[] args) {

        var work =  getClassicalWork();

        if (Composer.CHOPIN == work.composer.toEnum()) {
            System.out.println("Work by Chopin: " + work.getTitle());
        }
    }

    private static ClassicalWork getClassicalWork() {
        return new ClassicalWork("0001", ApiEnum.of(Composer.CHOPIN), "Ballade");
    }
}
