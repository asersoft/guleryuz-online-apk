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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirmaBolgeOnline extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid;
    public String firmaid;
    public Context context;
    public Database db;
    public String title;
    public String reqtype, reqtitle, reqop;
    public String[] param1=null, param2=null, param3=null;
    public String[] resp1=null, resp2=null, resp3=null;
    private ProgressDialog proDialog;
    private boolean status=false;
    List<HashMap<String, String>> data = null;

    public FirmaBolgeOnline() {

    }

    public FirmaBolgeOnline(TaskCallback callback) {
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
                proDialog.setTitle(title);
                proDialog.setMessage("Yükleniyor...");
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
            params.put("op",reqop);
            params.put("uid",uid);
            params.put("firmaid",firmaid);
            if (param1!=null)
                params.put(param1[0],param1[1]);
            if (param2!=null)
                params.put(param2[0],param2[1]);
            if (param3!=null)
                params.put(param3[0],param3[1]);
            params.put("ttt",""+System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);

            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray values = jsonObj.getJSONArray("result");
            if(values!=null) {
                data=new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);

                    HashMap<String, String> tmp = new HashMap<String, String>();

                    //tmp.put("bolge", c.getString("bolge2"));
                    if(resp1!=null)
                        tmp.put(resp1[0], c.getString(resp1[1]));
                    if(resp2!=null)
                        tmp.put(resp2[0], c.getString(resp2[1]));
                    if(resp3!=null)
                        tmp.put(resp3[0], c.getString(resp3[1]));

                    data.add(tmp);
                    //db.firmaBolgeEkle(tmp);
                }
                status=true;
                //db.dbstatDegerEkle("firmabolge","fromserver", MainActivity.userid);
            }

        }catch (Exception ex){
            Log.w("FirmaBolgeOnline", ex.getMessage());
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
            mCallback.SendDataAsyncFinish(status, reqtype+"Online", data, "");

            if (proDialog.isShowing())
                proDialog.dismiss();
        }
    }

}

