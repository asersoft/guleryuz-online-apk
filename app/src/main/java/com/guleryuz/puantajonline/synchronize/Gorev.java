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

public class Gorev extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;

    public Gorev() {

    }

    public Gorev(TaskCallback callback) {
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
                proDialog.setMessage("İndiriliyor... - Görev");
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
            params.put("op","gorev");
            params.put("uid",uid);
            params.put("ttt",""+System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);
            if(!jsonStr.equals("[]")) {
                JSONObject jsonObj = new JSONObject(jsonStr);

                JSONArray values = jsonObj.getJSONArray("gorev");

                if (values != null) {
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject c = values.getJSONObject(i);

                        HashMap<String, String> tmp = new HashMap<String, String>();

                        tmp.put("id", c.getString("id"));
                        tmp.put("gorev", c.getString("gorev"));
                        tmp.put("firma", c.getString("firma"));
                        tmp.put("bolge", c.getString("bolge"));
                        tmp.put("temsilci", uid);//c.getString("temsilci"));

                        db.gorevEkle(tmp);
                    }
                }
            }
            db.dbstatDegerEkle("gorev","fromserver", MainActivity.userid);
        }catch (Exception ex){
            Log.w("Gorev", ex.getMessage());
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

