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


    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(5);
    }
}
