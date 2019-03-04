package guleryuz.puantajonline.CallBacks;

/**
 * Created by Asersoft on 25.02.2017.
 */

public interface TaskCallback {
    void UserAsyncFinish(boolean stat, String error);
    void SifremiUnuttumAsyncFinish(String stat,String uid, String p);
    void PersonelAsyncFinish(boolean stat);
    void PushDataAsyncFinish(boolean stat);
}
