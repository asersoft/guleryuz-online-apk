package guleryuz.puantajonline;

/**
 * Created by mehmet_erenoglu on 05.05.2017.
 */

public class AESCrypt {
    private String seedValue="2d3768b627fd4fb488f7969abcacfa05";

    public AESCrypt(){

    }

    public String encryption(String strNormalText){
        String normalTextEnc="";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalTextEnc;
    }

    public String decryption(String strEncryptedText){
        String strDecryptedText="";
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }
}
