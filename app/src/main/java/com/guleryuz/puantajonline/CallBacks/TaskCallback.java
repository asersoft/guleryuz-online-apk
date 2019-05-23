package com.guleryuz.puantajonline.CallBacks;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Asersoft on 25.02.2017.
 */

public interface TaskCallback {
    void UserAsyncFinish(boolean stat, String userid, String puantajyetki, String error);
    void PersonelAsyncFinish(boolean stat, HashMap<String, String> tmp, String error);
    void SifremiUnuttumAsyncFinish(String stat,String uid, String p);
    void PersonelAsyncFinish(boolean stat);
    void PushDataAsyncFinish(boolean stat);
    void SendDataAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error);
}
