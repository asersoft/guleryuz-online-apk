package com.guleryuz.puantajonline.OnlineService;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.guleryuz.puantajonline.CallBacks.ServiceCallBack;
import com.guleryuz.puantajonline.CallBacks.TaskCallback;
import com.guleryuz.puantajonline.MainActivity;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.WebRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataFromService extends AsyncTask<Void, Void, Void> {
    private ServiceCallBack mCallback;
    public String uid;
    public Context context;
    public String title;
    public String reqtype;
    public List<String[]> reqparam=null;
    public List<String[]> resp=null;
    private ProgressDialog proDialog;
    private boolean status=false;
    List<HashMap<String, String>> data = null;

    public DataFromService() {

    }

    public DataFromService(ServiceCallBack callback) {
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
            params.put("uid",uid);
            params.put("prgver",MainActivity.PROGRAM_VERSION);
            if(reqparam!=null) {
                for (int i = 0; i < reqparam.size(); i++) {
                    String[] rq = reqparam.get(i);
                    params.put(rq[0], rq[1]);
                    Log.w("RRR: ", ">< " +rq[0]+" - "+ rq[1]);
                }
            }

            params.put("ttt",""+System.currentTimeMillis());
            Log.w("DataFromSrv: ", ">>> " + reqtype);
            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);

            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray values = jsonObj.getJSONArray("result");
            if(values!=null) {
                data=new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);

                    HashMap<String, String> tmp = new HashMap<String, String>();

                    if(resp!=null) {
                        for (int j = 0; j < resp.size(); j++) {
                            String[] rq = resp.get(j);
                            tmp.put(rq[0], c.getString(rq[1]));
                        }
                    }

                    data.add(tmp);
                }
                status=true;
            }

        }catch (Exception ex){
            Log.w(reqtype+"Online", ex.getMessage());
        }finally {
            status=true;
        }

        return null;
    }
    @Override
    protected void onPostExecute(Void requestresult) {
        super.onPostExecute(requestresult);
        //db.close();
        if(mCallback!=null) {
            mCallback.PipeAsyncFinish(status, reqtype+"Online", data, "");

            if (proDialog.isShowing())
                proDialog.dismiss();
        }else{

        }
    }

}

