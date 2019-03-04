package guleryuz.puantajonline.synchronize;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import guleryuz.puantajonline.Database;
import barcodescanner.app.com.barcodescanner.R;
import guleryuz.puantajonline.CallBacks.TaskCallback;
import guleryuz.puantajonline.WebRequest;

/**
 * Created by Asersoft on 19.05.2017.
 */

public class UsersUpdate extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;

    public UsersUpdate() {
    }

    public UsersUpdate(TaskCallback callback) {

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
                proDialog.setTitle("Kullanıcı Bilgileri Güncelleme");
                proDialog.setMessage("Kontrol ediliyor...");
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
            params.put("op","usu");
            params.put("uid",uid);
            params.put("ttt",""+System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("Response: ", "> " + jsonStr);

            JSONObject jsonObj = new JSONObject(jsonStr);
            String uid=jsonObj.get("id").toString();
            JSONArray values = jsonObj.getJSONArray("fis");

            if(values!=null) {
                db.usersFisnoDel();
                for (int i = 0; i < values.length(); i++) {
                    JSONObject c = values.getJSONObject(i);

                    db.usersFisnoGuncelle(c.getString("bas"), c.getString("bit"), uid);
                    status=true;
                }
            }
        }catch (Exception ex){
            Log.w("Users Update", ex.getMessage());
        }finally {
            //db.close();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void requestresult) {
        super.onPostExecute(requestresult);
        if(mCallback!=null) {
            //mCallback.UserAsyncFinish(status);
            if (proDialog!=null && proDialog.isShowing())
                proDialog.dismiss();
        }
    }
}
