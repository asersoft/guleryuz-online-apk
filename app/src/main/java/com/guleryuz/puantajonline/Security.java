package guleryuz.puantajonline;

import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * Created by Asersoft on 25.02.2017.
 */

public class Security {
    public static String generateMD5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return password;
    }
}
