package com.guleryuz.puantajonline.synchronize;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import com.guleryuz.puantajonline.CallBacks.TaskCallback;
import com.guleryuz.puantajonline.Database;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.WebRequest;

/**
 * Created by mehmet_erenoglu on 16.05.2017.
 */

public class SifremiUnuttum  extends AsyncTask<Void, Void, Void>{
    private TaskCallback mCallback;
    public String u, p, uid;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private String status="-2";

    public SifremiUnuttum() {
    }

    public SifremiUnuttum(TaskCallback callback) {

        mCallback = callback;
        p="";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mCallback!=null) {
            if (proDialog == null) {
                proDialog = new ProgressDialog(context);
                proDialog.setCanceledOnTouchOutside(false);
                proDialog.setCancelable(false);
                proDialog.setTitle("Şifremi Unuttum");
                proDialog.setMessage("İşlem gerçekleştiriliyor...");
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
            params.put("op","usfp");
            params.put("u",u);
            params.put("p3","nsF898sXL29D");
            params.put("ttt",""+System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);

            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray values = jsonObj.getJSONArray("user");

            if(values!=null) {
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);

                    HashMap<String, String> tmp = new HashMap<String, String>();

                    tmp.put("id", c.getString("id"));
                    tmp.put("username", c.getString("username"));
                    tmp.put("password", c.getString("password"));
                    tmp.put("status", c.getString("status"));

                    db.sifremiUnuttumGuncelle(tmp);

                    status=c.getString("status");
                    p=c.getString("password");
                    uid=c.getString("id");
                }
            }
        }catch (Exception ex){
            Log.w("SifremiUnuttum - Users", ex.getMessage());
        }finally {
            //db.close();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void requestresult) {
        super.onPostExecute(requestresult);
        if(mCallback!=null) {
            mCallback.SifremiUnuttumAsyncFinish(status, uid, p);

            if (proDialog!=null && proDialog.isShowing())
                proDialog.dismiss();
        }
    }
}