package com.guleryuz.puantajonline.synchronize;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.guleryuz.puantajonline.CallBacks.TaskCallback;
import com.guleryuz.puantajonline.Database;
import com.guleryuz.puantajonline.MainActivity;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.WebRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Asersoft on 26.02.2017.
 */

public class Personel extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;

    public Personel() {

    }

    public Personel(TaskCallback callback) {
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
                proDialog.setMessage("İndiriliyor... - Personel");
            }
            proDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
        try{
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "perlisttum");
            params.put("uid",uid);
            params.put("ttt", "" + System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);

            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray values = jsonObj.getJSONArray("personel");
            db.personelQueueEmpty();

            if(values!=null) {
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);

                    HashMap<String, String> tmp = new HashMap<String, String>();

                    tmp.put("id", c.getString("id"));
                    tmp.put("tc", c.getString("tc"));
                    tmp.put("ad", c.getString("ad"));
                    tmp.put("soyad", c.getString("soyad"));
                    tmp.put("dogumtarihi", c.getString("dogumtarihi"));
                    tmp.put("cinsiyet", c.getString("cinsiyet"));
                    tmp.put("gorev", c.getString("gorev"));
                    tmp.put("bolge", c.getString("bolge"));
                    tmp.put("bolge2", c.getString("bolge2"));
                    tmp.put("bolge3", c.getString("bolge3"));
                    tmp.put("bolge4", c.getString("bolge4"));
                    tmp.put("bolge5", c.getString("bolge5"));
                    tmp.put("ekip_lideri", c.getString("ekip_lideri"));
                    tmp.put("ekip_lideri2", c.getString("ekip_lideri2"));
                    tmp.put("ekip_lideri3", c.getString("ekip_lideri3"));
                    tmp.put("user_id", c.getString("user_id"));
                    tmp.put("resim", c.getString("resim"));
                    tmp.put("ssk", c.getString("ssk"));
                    tmp.put("sgk_evrak", c.getString("sgk_evrak"));
                    tmp.put("kartno", (c.getString("kartno")!=null && !c.getString("kartno").equals("000000")?c.getString("kartno"):""));
                    tmp.put("onay", c.getString("onay"));

                    //db.personelEkle(tmp);
                    db.personelQueue(tmp);
                }
                db.personelStore();
                status=true;
                db.dbstatDegerEkle("personel","fromserver", MainActivity.userid);

            }

            //db.personelVersionEkle(1);
        }catch (Exception ex){
            Log.w("Personel", ex.getMessage());
        }finally {
            //db.close();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void requestresult) {
        super.onPostExecute(requestresult);
        //db.close();
        try {
            if (mCallback != null) {
                mCallback.PersonelAsyncFinish(status);

                if (proDialog != null && proDialog.isShowing())
                    proDialog.dismiss();
            }
        }catch (Exception ex){
            Log.w("Personel PostEx", ex.getMessage());
        }
    }

}