package guleryuz.puantajonline.synchronize;

/**
 * Created by Asersoft on 27.02.2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import guleryuz.puantajonline.Database;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import guleryuz.puantajonline.MainActivity;
import guleryuz.puantajonline.CallBacks.TaskCallback;

/**
 * Created by Asersoft on 27.02.2017.
 */

public class PersonelFotoAsync extends AsyncTask<String, String, String> {
    private TaskCallback mCallback;
    private int current=0;
    private ArrayList<HashMap<String,String>> res;
    private File photoPath;
    public Context context;
    public Database db;
    private List<String> completed;
    private ProgressDialog proDialog;

    public PersonelFotoAsync(TaskCallback callback, ArrayList<HashMap<String,String>> r){
        mCallback=callback;
        res=r;
        photoPath = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"");

        if (!photoPath.exists()) {
            photoPath.mkdirs();
        }

        completed=new ArrayList<String>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mCallback!=null) {
            if (proDialog == null) {
                proDialog = new ProgressDialog(context);
                proDialog.setCanceledOnTouchOutside(false);
                proDialog.setCancelable(false);
                proDialog.setTitle("Veriler");
                proDialog.setMessage("İndiriliyor... - Personel Foto");
            }
            proDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... aurl) {
        for(int i=0; i<res.size(); i++)
        {
            int count;
            try {
                String fpath = res.get(i).get("RESIM");

                File f=new File(Environment.getExternalStorageDirectory()+"/"+MainActivity.rootDir+"/"+fpath);
                if(!f.exists()) {
                    URL url = new URL("http://www.guleryuzcv.net/images2/img/15/evrak/" + fpath);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream(), 512);
                    OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/" + fpath);
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }else{
                    completed.add(res.get(i).get("ID"));
                }
                current=i;
            } catch (Exception ex) {
                //Log.w("PersonelFotoAsync", ex.getMessage());
            }

        }
        db.dbstatDegerEkle("personelfoto","fromserver", MainActivity.userid);
        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        completed.add(res.get(current).get("ID"));
        /*try{
            db=new Database(context);
            db.personelFotoUpdate(res.get(current-1).get("ID"));
            //db.close();
        }catch (Exception ex){
            Log.w("PersonelFotoAsync ", ex.getMessage());
        }*/
    }

    @Override
    protected void onPostExecute(String unused) {
        try {
            db = new Database(context);
            db.personelFotoUpdate(completed, "RESIM_INDIRILDI");
            //db.close();
        } catch (Exception ex) {
            Log.w("PersonelFotoAsync ", ex.getMessage());
        }
        if (mCallback != null) {
            //mCallback.PersonelAsyncFinish(status);

            if (proDialog!=null && proDialog.isShowing())
                proDialog.dismiss();
        }
    }
}