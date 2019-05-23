package com.guleryuz.puantajonline.synchronize;

/**
 * Created by Asersoft on 26.02.2017.
 */

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

public class Servis extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;

    public Servis() {

    }

    public Servis(TaskCallback callback) {
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
                proDialog.setMessage("İndiriliyor... - Servis");
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
            params.put("op","servis");
            params.put("uid",uid);
            params.put("ttt",""+System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray values = jsonObj.getJSONArray("servis");
            if(values!=null) {
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);

                    HashMap<String, String> tmp = new HashMap<String, String>();

                    tmp.put("temsilci", uid);//c.getString("temsilci"));
                    tmp.put("firma", c.getString("firma"));
                    tmp.put("bolge", c.getString("bolge"));
                    tmp.put("ekiplideri", c.getString("ekiplideri"));
                    tmp.put("servis1", c.getString("servis1"));
                    tmp.put("servis2", c.getString("servis2"));
                    tmp.put("servis3", c.getString("servis3"));
                    tmp.put("servis4", c.getString("servis4"));
                    tmp.put("servis5", c.getString("servis5"));
                    tmp.put("servis6", c.getString("servis6"));
                    tmp.put("servis7", c.getString("servis7"));
                    tmp.put("servis8", c.getString("servis8"));
                    tmp.put("servis9", c.getString("servis9"));
                    tmp.put("servis10", c.getString("servis10"));
                    tmp.put("servis11", c.getString("servis11"));
                    tmp.put("servis12", c.getString("servis12"));
                    tmp.put("servis13", c.getString("servis13"));
                    tmp.put("servis14", c.getString("servis14"));
                    tmp.put("servis15", c.getString("servis15"));
                    tmp.put("servis16", c.getString("servis16"));
                    tmp.put("servis17", c.getString("servis17"));
                    tmp.put("servis18", c.getString("servis18"));
                    tmp.put("servis19", c.getString("servis19"));
                    tmp.put("servis20", c.getString("servis20"));
                    tmp.put("servis_personel", c.getString("servis_personel"));
                    db.servisEkle(tmp);
                }
                db.dbstatDegerEkle("servis","fromserver", MainActivity.userid);
            }
        }catch (Exception ex){
            Log.w("Servis", ex.getMessage());
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
