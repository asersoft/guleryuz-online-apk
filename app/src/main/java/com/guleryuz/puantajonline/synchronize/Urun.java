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
import com.guleryuz.puantajonline.MainActivity;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.WebRequest;

/**
 * Created by Asersoft on 26.02.2017.
 */

public class Urun extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;

    public Urun() {

    }

    public Urun(TaskCallback callback) {
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
                proDialog.setTitle("Veriler");
                proDialog.setMessage("İndiriliyor... - Ürün");
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
            params.put("op","urun");
            params.put("uid",uid);
            params.put("ttt",""+System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray values = jsonObj.getJSONArray("urun");
            if(values!=null) {
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);

                    HashMap<String, String> tmp = new HashMap<String, String>();

                    tmp.put("id", c.getString("id"));
                    tmp.put("ad", c.getString("ad"));
                    tmp.put("urunid", c.getString("urunid"));
                    tmp.put("urun", c.getString("urun"));
                    tmp.put("firma", c.getString("firma"));
                    tmp.put("bolge", c.getString("bolge"));
                    tmp.put("temsilci", uid);//c.getString("temsilci"));

                    db.urunEkle(tmp);
                }

                db.dbstatDegerEkle("urun","fromserver", MainActivity.userid);
            }
        }catch (Exception ex){
            Log.w("Urun", ex.getMessage());
        }finally {
            //db.close();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void requestresult) {
        super.onPostExecute(requestresult);
        //db.close();
        if(mCallback!=null) {
            mCallback.PersonelAsyncFinish(status);

            if (proDialog.isShowing())
                proDialog.dismiss();
        }
    }

}