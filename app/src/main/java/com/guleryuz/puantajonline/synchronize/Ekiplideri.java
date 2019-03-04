package guleryuz.puantajonline.synchronize;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import barcodescanner.app.com.barcodescanner.*;
import guleryuz.puantajonline.CallBacks.TaskCallback;
import guleryuz.puantajonline.Database;
import guleryuz.puantajonline.MainActivity;
import guleryuz.puantajonline.WebRequest;

/**
 * Created by Asersoft on 26.02.2017.
 */

public class Ekiplideri extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;

    public Ekiplideri() {

    }

    public Ekiplideri(TaskCallback callback) {
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
                proDialog.setMessage("İndiriliyor... - Ekip Lideri");
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
            params.put("op","ekiplideri");
            params.put("uid",uid);
            params.put("ttt",""+System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray values = jsonObj.getJSONArray("ekiplideri");
            if(values!=null) {
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);

                    HashMap<String, String> tmp = new HashMap<String, String>();

                    tmp.put("id", c.getString("id"));
                    tmp.put("ad", c.getString("ad"));
                    tmp.put("ekalan2", c.getString("ekalan2"));
                    tmp.put("ekalan3", c.getString("ekalan3"));
                    tmp.put("ekalan4", c.getString("ekalan4"));
                    tmp.put("ekalan6", c.getString("ekalan6"));
                    tmp.put("ekalan7", c.getString("ekalan7"));
                    tmp.put("ekalan8", c.getString("ekalan8"));
                    tmp.put("firma", c.getString("firma"));
                    tmp.put("bolge", c.getString("bolge"));
                    tmp.put("devam", c.getString("devam"));
                    tmp.put("aktif", c.getString("aktif"));

                    JSONArray c2 = c.getJSONArray("firmabolge");
                    if(c2!=null){
                        for (int j=0; j<c2.length(); j++){
                            JSONObject c3 = c2.getJSONObject(j);

                            HashMap<String, String> tmp2 = new HashMap<String, String>();

                            tmp2.put("firma",c3.getString("firma"));
                            tmp2.put("bolge",c3.getString("bolge"));
                            tmp2.put("bolge2",c3.getString("bolge2"));
                            tmp2.put("ekiplideri",c.getString("id"));

                            db.ekiplideriBolgeEkle(tmp2);
                        }
                    }

                    db.ekiplideriEkle(tmp);
                }
                db.dbstatDegerEkle("ekiplideri","fromserver", MainActivity.userid);
            }
        }catch (Exception ex){
            Log.w("EkipLideri", ex.getMessage());
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
