package guleryuz.puantajonline.synchronize;

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

import guleryuz.puantajonline.MainActivity;

/**
 * Created by Asersoft on 8.04.2017.
 */

public class PersonelFotoThread implements Runnable{
    int threadNo;
    Handler handler;
    HashMap<String,String> res;
    public static final String TAG = "LongThread";

    public PersonelFotoThread() {
    }

    public PersonelFotoThread(int threadNo, HashMap<String,String> image, Handler handler) {
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
            String fpath = res.get("RESIM");
            File f = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + fpath);
            if (!f.exists()) {
                Log.i(TAG, "Starting Thread : " + fpath);
                //InputStream input = new java.net.URL("http://www.guleryuzcv.net/images2/img/15/evrak/" + fpath).openStream();
                //InputStream input = new java.net.URL("http://sistem.guleryuzcv.net/vtys/rs.php?pname=" + fpath).openStream();
                InputStream input = new java.net.URL("http://www.traderentegre.net/t_istakip/mobilsrv/rs.php?pname=" + fpath).openStream();

                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inSampleSize = 8;
                bitmap = BitmapFactory.decodeStream(input);//,null,options);
                //bitmap = BitmapFactory.decodeStream(input);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + fpath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();
                input.close();
                status = "ok";
            }
        }catch (IOException ex){
            Log.w("pfthread io",(ex!=null?ex.getMessage():"null ex"));
            status="fail-"+(ex!=null?ex.getMessage():"null ex");
        } catch (Exception ex) {
            Log.w("pfthread",(ex!=null?ex.getMessage():"null ex"));
            status="fail-"+(ex!=null?ex.getMessage():"null ex");
        }
        sendMessage(threadNo, "pfoto-"+res.get("ID")+"-"+status);
        Log.i(TAG, "Thread Completed " + threadNo);
    }


    public void sendMessage(int what, String msg) {
        Message message = handler.obtainMessage(what, msg);
        message.sendToTarget();
    }

}
