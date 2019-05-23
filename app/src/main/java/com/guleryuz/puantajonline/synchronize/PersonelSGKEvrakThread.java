package com.guleryuz.puantajonline.synchronize;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.guleryuz.puantajonline.MainActivity;

/**
 * Created by Asersoft on 8.04.2017.
 */

public class PersonelSGKEvrakThread implements Runnable{
    int threadNo;
    Handler handler;
    HashMap<String,String> res;
    public static final String TAG = "LongThreadSGK";

    public PersonelSGKEvrakThread() {
    }

    public PersonelSGKEvrakThread(int threadNo, HashMap<String,String> image, Handler handler) {
        this.threadNo = threadNo;
        this.handler = handler;
        this.res=image;
    }

    @Override
    public void run() {
        String status="ok";
        Log.i(TAG, "Starting Thread : " + threadNo);
        Bitmap bitmap = null;
        try {
            String fpath = res.get("SGK_EVRAK");
            File f = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/"+MainActivity.sgkDir+"/"+ fpath);
            if (!f.exists()) {
                Log.i(TAG, "Starting Thread : " + fpath);
                InputStream input = new java.net.URL("http://www.guleryuzcv.net/images2/img/15/evrak/" + fpath).openStream();
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/"+MainActivity.sgkDir+"/"+ fpath);
                if(fpath.toLowerCase().indexOf(".pdf")>0) {
                    byte data[] = new byte[1024];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        output.write(data, 0, count);
                    }
                }else{
                    bitmap = BitmapFactory.decodeStream(input);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                }
                output.flush();
                output.close();
                input.close();
                status = "ok";
            }
        }catch (IOException ex){
            Log.w("pfthread io",(ex!=null?ex.getMessage():"null ex"));
            status="fail-"+(ex!=null?ex.getMessage():"null ex");
        } catch (Exception ex) {
            Log.w("pfthread error",(ex!=null?ex.getMessage():"null ex"));
            status="fail-"+(ex!=null?ex.getMessage():"null ex");
        }
        sendMessage(threadNo, "psgk-"+res.get("ID")+"-"+status);
        Log.i(TAG, "Thread Completed " + threadNo);
    }


    public void sendMessage(int what, String msg) {
        Message message = handler.obtainMessage(what, msg);
        message.sendToTarget();
    }

}
