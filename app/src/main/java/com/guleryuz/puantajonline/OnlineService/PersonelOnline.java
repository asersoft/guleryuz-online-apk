package com.guleryuz.puantajonline.OnlineService;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.guleryuz.puantajonline.CallBacks.TaskCallback;
import com.guleryuz.puantajonline.Database;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.WebRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class PersonelOnline  extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid, sc, kn, tc, a, s;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status = false;
    HashMap<String, String> tmp =null;

    public PersonelOnline() {

    }

    public PersonelOnline(TaskCallback callback) {
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mCallback != null) {
            if (proDialog == null) {
                proDialog = new ProgressDialog(context);
                proDialog.setCanceledOnTouchOutside(false);
                proDialog.setCancelable(false);
                proDialog.setTitle("Personel Sorgulanıyor.");
                proDialog.setMessage("Personel Bilgileri");
            }
            proDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
        try {
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "persor");
             params.put("kartno", (kn!=null?kn:""));
            params.put("sicilno", (sc!=null?sc:""));
            params.put("tckimlik", (tc!=null?tc:""));
            params.put("ad",(a!=null?a:""));
            params.put("soyad",(s!=null?s:""));
            params.put("uid", uid);
            params.put("ttt", "" + System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);

            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray values = jsonObj.getJSONArray("result");
            //db.personelQueueEmpty();

            if (values != null) {
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);
                    tmp=new HashMap<String, String>();
                    tmp.put("ID", c.getString("id"));
                    tmp.put("TC", c.getString("tc"));
                    tmp.put("AD", c.getString("ad"));
                    tmp.put("SOYAD", c.getString("soyad"));
                    tmp.put("DOGUMTARIHI", c.getString("dogumtarihi"));
                    tmp.put("CINSIYET", c.getString("cinsiyet"));
                    tmp.put("GOREV", c.getString("gorev"));
                    tmp.put("BOLGE", c.getString("bolge"));
                    tmp.put("BOLGE2", c.getString("bolge2"));
                    tmp.put("BOLGE3", c.getString("bolge3"));
                    tmp.put("BOLGE4", c.getString("bolge4"));
                    tmp.put("BOLGE5", c.getString("bolge5"));
                    tmp.put("EKIP_LIDERI", c.getString("ekip_lideri"));
                    tmp.put("EKIP_LIDERI2", c.getString("ekip_lideri2"));
                    tmp.put("EKIP_LIDERI3", c.getString("ekip_lideri3"));
                    tmp.put("user_id", c.getString("user_id"));
                    tmp.put("RESIM", c.getString("resim"));
                    tmp.put("SSK", c.getString("ssk"));
                    tmp.put("SGK_EVRAK", c.getString("sgk_evrak"));
                    String kartno="";
                    if (c.getString("kartno")!=null && !c.getString("kartno").equals("") && !c.getString("kartno").equals("000000"))
                        kartno=c.getString("kartno");
                    tmp.put("KARTNO", kartno);
                    tmp.put("ONAY", c.getString("onay"));
                    tmp.put("SSK_CIKIS", c.getString("ssk_cikis"));
                    tmp.put("DEVAM", c.getString("devam"));
                    tmp.put("NUFUS1", c.has("nufus1")?c.getString("nufus1"):"");
                    tmp.put("NUFUS2", c.has("nufus2")?c.getString("nufus2"):"");

                    //db.personelEkle(tmp);
                    //db.personelQueue(tmp);
                }
                //db.personelStore();
                status = true;

            }

            //db.personelVersionEkle(1);
        } catch (Exception ex) {
            Log.w("Personel", ex.getMessage());
        } finally {
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
                mCallback.PersonelAsyncFinish(status, tmp, "");

                if (proDialog != null && proDialog.isShowing())
                    proDialog.dismiss();
            }
        } catch (Exception ex) {
            Log.w("Personel PostEx", ex.getMessage());
        }
    }
}

