package jm.service;

import jm.model.Event;
import org.apache.commons.codec.digest.Md5Crypt;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Created by vk on 30.03.17.
 */

@Stateless
public class EventHashCalcer {

    private static final String SECRET_KEY = "0A602BBA5EF829A92395DF0D48115EAB";

    @Inject Logger logger;

    public String calc(Event event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(event.getDate());
        stringBuilder.append(event.getPerson());
        stringBuilder.append(event.getEvent());
        String string = stringBuilder.toString();
//        logger.info(string);
        return calcHash(string);
    }

    private String calcHash(String collected) {
        return toSHA1(SECRET_KEY.concat(toSHA1(SECRET_KEY.concat(collected).getBytes()).toLowerCase()).getBytes());
    }

    private String toSHA1(byte[] convertme) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            logger.severe(e.getMessage());
        }
        return byteArrayToHexString(messageDigest.digest(convertme));
    }

    private String byteArrayToHexString(byte[] b) {
        String result = "";
        for (byte aB : b) {
            result += Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }


}
