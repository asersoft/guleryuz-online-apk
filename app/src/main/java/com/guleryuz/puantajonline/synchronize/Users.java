package guleryuz.puantajonline.synchronize;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import barcodescanner.app.com.barcodescanner.*;
import guleryuz.puantajonline.CallBacks.TaskCallback;
import guleryuz.puantajonline.Database;
import guleryuz.puantajonline.MainActivity;
import guleryuz.puantajonline.WebRequest;

/**
 * Created by Asersoft on 25.02.2017.
 */

public class Users extends AsyncTask<Void, Void, Void>{
    private TaskCallback mCallback;
    public String u, p;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;
    private String error="";

    public Users() {
    }

    public Users(TaskCallback callback) {

        mCallback = callback;
    }

    @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mCallback!=null) {
                if (proDialog == null) {
                    proDialog = new ProgressDialog(context);
                    proDialog.setCanceledOnTouchOutside(false);
                    proDialog.setCancelable(false);
                    proDialog.setTitle("Kullanıcı Giriş");
                    proDialog.setMessage("Kontrol ediliyor...");
                }
                proDialog.show();
            }
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            try{
                WebRequest webreq = new WebRequest();

                // Making a request to url and getting response
                HashMap<String, String> params=new HashMap<String, String>();
                params.put("token","6ce304f73ce841efaf1490bb98474eef");
                params.put("op","us");
                params.put("u",u);
                params.put("p",p);
                params.put("ttt",""+System.currentTimeMillis());

                String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

                Log.w("Response: ", "> " + jsonStr);
                if(jsonStr.indexOf("users")>0) {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray values = jsonObj.getJSONArray("users");
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject c = values.getJSONObject(i);

                        HashMap<String, String> tmp = new HashMap<String, String>();

                        tmp.put("id", c.getString("id"));
                        tmp.put("username", c.getString("username"));
                        tmp.put("password", c.getString("password"));
                        tmp.put("passwordtxt", "");
                        tmp.put("name", c.getString("name"));
                        tmp.put("rempass","0");
                        tmp.put("puantaj_yetki",c.getString("puantaj_yetki"));
                        db.resetTables();

                        if(c.has("fis")) {
                            JSONArray fisler = c.getJSONArray("fis");
                            if (fisler != null) {
                                for (int j = 0; j < fisler.length(); j++) {
                                    JSONObject c2 = fisler.getJSONObject(j);
                                    db.usersFisnoGuncelle(c2.getString("bas"), c2.getString("bit"), c.getString("id"));
                                }
                            }
                        }

                        db.usersEkle(tmp);
                        //İndirilen dosyaları siler
                        File file = new File(Environment.getExternalStorageDirectory() + "/"+ MainActivity.rootDir+"", "");
                        if (file != null && file.isDirectory()) {
                            File[] files = file.listFiles();
                            if(files != null) {
                                for(File f : files) {
                                    f.delete();
                                }
                            }
                        }
                        file = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.attachDir+"", "");
                        if (file != null && file.isDirectory()) {
                            File[] files = file.listFiles();
                            if(files != null) {
                                for(File f : files) {
                                    f.delete();
                                }
                            }
                        }

                        file = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"", "");
                        if (file != null && file.isDirectory()) {
                            File[] files = file.listFiles();
                            if(files != null) {
                                for(File f : files) {
                                    f.delete();
                                }
                            }
                        }
                        //----
                        status=true;
                    }
                }else if(jsonStr.indexOf("error")>0){
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    error = jsonObj.getString("error");
                }
            }catch (Exception ex){
                Log.w("Users Class - Users", ex.getMessage());
            }finally {
                //db.close();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            if(mCallback!=null) {
                mCallback.UserAsyncFinish(status, error);

                if (proDialog!=null && proDialog.isShowing())
                    proDialog.dismiss();
            }
        }
}
