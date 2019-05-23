package com.guleryuz.puantajonline.synchronize;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import com.guleryuz.puantajonline.Database;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.WebRequest;

/**
 * Created by mehmet_erenoglu on 16.10.2017.
 */

public class TarihGuncelle extends AsyncTask<Void, Void, Void> {
    public String uid;
    public Context context;
    public Database db;
    private boolean status=false;

    public TarihGuncelle() {

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected Void doInBackground(Void... arg0) {
        try{
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "getdate");
            //params.put("uid",uid);
            params.put("ttt", "" + System.currentTimeMillis());
            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);

            JSONObject jsonObj = new JSONObject(jsonStr);
            Log.w("TarihGuncelle:: ", "> " + jsonObj.get("date"));


            if (jsonObj != null &&  jsonObj.has("date")){
                db.tarihEkle( jsonObj.get("date").toString());
                /*String[] serverdate = jsonObj.get("date").toString().split(" ");
                String[] serverdd  = serverdate[0].split("-");
                res =  Integer.parseInt(serverdd[2])+" / "+Integer.parseInt(serverdd[1])+" / "+Integer.parseInt(serverdd[0]);*/
            }
        }catch (Exception ex){
            Log.w("TarihGuncelle", ex.getMessage());
        }finally {
            //db.close();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void requestresult) {
        super.onPostExecute(requestresult);
    }
}
