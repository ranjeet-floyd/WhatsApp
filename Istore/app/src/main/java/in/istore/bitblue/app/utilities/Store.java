package in.istore.bitblue.app.utilities;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class Store {

    public static int generateStoreId() {

        int min = 11111;
        int max = 99999;
        Random random = new Random();

        // nextInt is normally exclusive so add 1 to make it inclusive
        return random.nextInt((max - min) + 1) + min;
    }

    public static int generateStaffId() {

        int min = 11;
        int max = 99;
        Random random = new Random();

        return random.nextInt((max - min) + 1) + min;
    }

    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(5);
    }

    public static long generateInVoiceNumber() {

        int min = 111111111;
        int max = 999999999;
        Random random = new Random();

        return random.nextInt((max - min) + 1) + min;
    }

    public static String generateProdId(String CategoryName, String ProSubcatName) {

        int min = 1111;
        int max = 9999;
        Random random = new Random();

        int id = random.nextInt((max - min) + 1) + min;

        return CategoryName + "_" + ProSubcatName + "_" + id;
    }
}
