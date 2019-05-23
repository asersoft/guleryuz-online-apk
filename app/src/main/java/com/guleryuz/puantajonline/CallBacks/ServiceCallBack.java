package com.guleryuz.puantajonline.CallBacks;

import java.util.HashMap;
import java.util.List;

public interface ServiceCallBack {
    void PipeAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error);
}
