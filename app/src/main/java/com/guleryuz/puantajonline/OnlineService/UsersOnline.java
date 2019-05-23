package com.guleryuz.puantajonline.OnlineService;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.guleryuz.puantajonline.CallBacks.TaskCallback;
import com.guleryuz.puantajonline.Database;
import com.guleryuz.puantajonline.MainActivity;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.WebRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class UsersOnline extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String u, p, uid, py;
    public String ps=null;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;
    private String error="";

    public UsersOnline() {
    }

    public UsersOnline(TaskCallback callback) {

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
                    uid= c.getString("id");
                    py=c.getString("puantaj_yetki");
                    tmp.put("id", c.getString("id"));
                    tmp.put("username", c.getString("username"));
                    tmp.put("password", c.getString("password"));
                    tmp.put("passwordtxt", (ps!=null?ps:""));
                    tmp.put("name", c.getString("name"));
                    tmp.put("rempass",ps!=null?"1":"0");
                    tmp.put("puantaj_yetki",c.getString("puantaj_yetki"));
                    db.resetTables();

                    db.usersEkle(tmp);
                    status=true;
                }
            }else if(jsonStr.indexOf("error")>0){
                JSONObject jsonObj = new JSONObject(jsonStr);
                error = jsonObj.getString("error");
            }
        }catch (Exception ex){
            Log.w("UsersOnlineClass", ex.getMessage());
        }finally {
            //db.close();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void requestresult) {
        super.onPostExecute(requestresult);
        if(mCallback!=null) {
            mCallback.UserAsyncFinish(status, uid, py, error);

            if (proDialog!=null && proDialog.isShowing())
                proDialog.dismiss();
        }
    }
}
