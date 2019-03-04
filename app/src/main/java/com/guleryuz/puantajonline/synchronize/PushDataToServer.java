package guleryuz.puantajonline.synchronize;

/**
 * Created by Asersoft on 26.02.2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import barcodescanner.app.com.barcodescanner.*;
import guleryuz.puantajonline.CallBacks.TaskCallback;
import guleryuz.puantajonline.Database;
import guleryuz.puantajonline.MainActivity;
import guleryuz.puantajonline.WebRequest;

/**
 * Created by Asersoft on 26.02.2017.
 */

public class PushDataToServer extends AsyncTask<Void, Void, Void> {
    private TaskCallback mCallback;
    public String uid;
    public Context context;
    public Database db;
    private ProgressDialog proDialog;
    private boolean status=false;
    private String whichSender;

    public PushDataToServer() {

    }

    public PushDataToServer(TaskCallback callback, String which) {
        mCallback = callback;
        whichSender=which;
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
                proDialog.setMessage("Sunucuya Gönderiliyor...");
            }
            proDialog.show();
        }
    }
    @Override
    protected Void doInBackground(Void... arg0) {
        status=true;

        try{
            if(whichSender.equals("puantaj"))
                sendPuantaj();
            else if(whichSender.equals("personel"))
                sendPersonel();
            else{
                sendPuantaj();
                sendPersonel();
            }
        }catch (Exception ex){
            Log.w("PushData", ex.getMessage());
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
            mCallback.PushDataAsyncFinish(status);

            if (proDialog.isShowing())
                proDialog.dismiss();
        }
    }

    private void sendPersonel(){
        String whereIndex="kart";
        try {
            //personel kart bilgisi
            WebRequest webreq = new WebRequest();

            ArrayList<HashMap<String, String>> res = db.getMultiResult(new String[]{"ID", "KARTNO", "USER_ID"}, "tarim_istakip_personel", "KARTNO_GUNCEL_AKTARILDI=0 AND KARTNO_GUNCELLENDI=1 and user_id='" + uid + "'");
            String perkart = "{\"pid\":[";
            for (int i = 0; i < res.size(); i++) {
                perkart += "{\"perid\":\"" + res.get(i).get("ID") + "\",\"kno\":\"" + res.get(i).get("KARTNO") + "\",\"uid\":\"" + res.get(i).get("USER_ID") + "\"}";
                if (i < res.size() - 1) {
                    perkart += ",";
                }
            }
            perkart += "]}";

            //Log.w("pushdata",perkart);

            // Making a request to url and getting response
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "pushdata");
            params.put("req", perkart);
            params.put("uid", uid);
            params.put("prgver", MainActivity.PROGRAM_VERSION);
            params.put("ttt", "" + System.currentTimeMillis());

            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.getString("pushdata").equals("true")) {
                for (int i = 0; i < res.size(); i++) {
                    db.personelKartnoUpdateAktarildi(res.get(i).get("KARTNO"), res.get(i).get("ID"), uid);
                }
            }


            //kartno iptal log
            webreq = new WebRequest();

            res = db.getMultiResult(new String[]{"KARTNO", "PERSONELID", "USER_ID","ISLEM_TARIHI"}, "kartno_iptal", "AKTARILDI=0 and user_id='" + uid + "'");
            perkart = "{\"pid\":[";
            for (int i = 0; i < res.size(); i++) {
                perkart += "{\"perid\":\"" + res.get(i).get("PERSONELID") + "\",\"kno\":\"" + res.get(i).get("KARTNO") + "\",\"uid\":\"" + res.get(i).get("USER_ID") + "\",\"itar\":\"" + res.get(i).get("ISLEM_TARIHI") + "\"}";
                if (i < res.size() - 1) {
                    perkart += ",";
                }
            }
            perkart += "]}";

            Log.w("pushdataki",perkart);

            params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "pushdataki");
            params.put("req", perkart);
            params.put("uid", uid);
            params.put("prgver", MainActivity.PROGRAM_VERSION);
            params.put("ttt", "" + System.currentTimeMillis());

             jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            jsonObj = new JSONObject(jsonStr);
            if (jsonObj.getString("pushdataki").equals("true")) {
                Log.w("pushdataki","ok");
                for (int i = 0; i < res.size(); i++) {
                    db.personelKartnoIptalAktarildi(res.get(i).get("KARTNO"), res.get(i).get("PERSONELID"), uid);
                }
            }
            //---

            //Personel Foto Ekleme
            webreq = new WebRequest();

            whereIndex = "personelfotoekle";
            ArrayList<HashMap<String, String>> resper = db.getMultiResult(new String[]{"ID", "USER_ID", "RESIM"}, "tarim_istakip_personel", "YENI_RESIM_AKTARILDI=0 AND YENI_RESIM=1 AND user_id='" + uid + "'");
            String perbilgi = "{\"pid\":[";
            for (int i = 0; i < resper.size(); i++) {
                perbilgi += "{\"perid\":\"" + resper.get(i).get("ID") + "\",\"uid\":\"" + resper.get(i).get("USER_ID") + "\",\"foto\":\"" + resper.get(i).get("RESIM") + "\"}";
                if (i < resper.size() - 1) {
                    perbilgi += ",";
                }

                if (resper.get(i).get("RESIM") != null && !resper.get(i).get("RESIM").toString().equals("")) {
                    uploadFile(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + resper.get(i).get("RESIM"));
                }
            }
            perbilgi += "]}";


            params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "pushdataperfoto");
            params.put("req", perbilgi);
            params.put("uid", uid);
            params.put("prgver", MainActivity.PROGRAM_VERSION);
            params.put("ttt", "" + System.currentTimeMillis());

            jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            jsonObj = new JSONObject(jsonStr);
            Log.w("pushdataperfoto", jsonObj.getString("pushdataperfoto"));
            if (jsonObj.getString("pushdataperfoto").equals("true")) {
                for (int i = 0; i < resper.size(); i++) {
                    db.personelFotoEkleAktarildi(resper.get(i).get("ID"), uid);
                }
            }
            //--

            webreq = new WebRequest();

            whereIndex = "yenipersonel";
            resper = db.getMultiResult(new String[]{"ID", "KARTNO", "USER_ID", "TC", "AD", "SOYAD", "CINSIYET", "DOGUMTARIHI", "BABAADI", "GSM", "OZELDURUM", "BOLGE", "BOLGE2", "EKIP_LIDERI", "RESIM", "SGK"}, "tarim_istakip_personel", "AKTARILDI=0 AND YENI_KAYIT=1 AND user_id='" + uid + "'");
            perbilgi = "{\"pid\":[";
            for (int i = 0; i < resper.size(); i++) {
                perbilgi += "{\"perid\":\"" + resper.get(i).get("ID") + "\",\"kno\":\"" + resper.get(i).get("KARTNO") + "\",\"uid\":\"" + resper.get(i).get("USER_ID") + "\",\"tc\":\"" + resper.get(i).get("TC") + "\",\"ad\":\"" + resper.get(i).get("AD") + "\",\"soyad\":\"" + resper.get(i).get("SOYAD") + "\",\"cinsiyet\":\"" + resper.get(i).get("CINSIYET") + "\",\"dtarih\":\"" + resper.get(i).get("DOGUMTARIHI") + "\",\"babaadi\":\"" + resper.get(i).get("BABAADI") + "\",\"gsm\":\"" + resper.get(i).get("GSM") + "\",\"ozeldurum\":\""+ resper.get(i).get("OZELDURUM") +"\",\"bolge\":\"" + resper.get(i).get("BOLGE") + "\",\"calismaalani\":\"" + resper.get(i).get("BOLGE2") + "\",\"ekiplideri\":\"" + resper.get(i).get("EKIP_LIDERI") + "\",\"sgk\":\"" + resper.get(i).get("SGK") + "\",\"foto\":\"" + resper.get(i).get("RESIM") + "\"}";
                if (i < resper.size() - 1) {
                    perbilgi += ",";
                }

                if (resper.get(i).get("RESIM") != null && !resper.get(i).get("RESIM").toString().equals("")) {
                    uploadFile(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + resper.get(i).get("RESIM"));
                }
            }
            perbilgi += "]}";


            params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "pushdataper");
            params.put("req", perbilgi);
            params.put("uid", uid);
            params.put("prgver", MainActivity.PROGRAM_VERSION);
            params.put("ttt", "" + System.currentTimeMillis());

            jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            jsonObj = new JSONObject(jsonStr);
            Log.w("pushdataper", jsonObj.getString("pushdataper"));
            if (jsonObj.getString("pushdataper").equals("true")) {
                if(jsonObj.has("exper")) {
                    JSONArray perArr=jsonObj.getJSONArray("exper");
                    for (int i = 0; i < perArr.length(); i++) {
                        JSONObject c = perArr.getJSONObject(i);
                        Log.w("pushdataper", c.get("sicilno") + " - " + c.get("tc"));
                        db.personelYeniAktarildi(c.getString("sicilno"), c.getString("tc"), uid);
                    }
                }

                for (int i = 0; i < resper.size(); i++) {
                    db.personelYeniAktarildi(resper.get(i).get("ID"), uid);
                }
            }

            webreq = new WebRequest();

            whereIndex = "personelbelge";
            ArrayList<HashMap<String, String>> resperbel = db.getMultiResult(new String[]{"OID", "PERSONELID", "RESIMADI", "TUR", "USER_ID"}, "tarim_istakip_personel_belge", "AKTARILDI=0 AND user_id='" + uid + "'");
            String perbelbilgi = "{\"pbelid\":[";
            for (int i = 0; i < resperbel.size(); i++) {
                perbelbilgi += "{\"personelid\":\"" + resperbel.get(i).get("PERSONELID") + "\",\"ad\":\"" + resperbel.get(i).get("RESIMADI") + "\",\"uid\":\"" + resperbel.get(i).get("USER_ID") + "\",\"tur\":\"" + resperbel.get(i).get("TUR") + "\"}";
                if (i < resperbel.size() - 1) {
                    perbelbilgi += ",";
                }
                Log.w("pbel", perbelbilgi);
                uploadFile(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + resperbel.get(i).get("RESIMADI"));


            }
            perbelbilgi += "]}";

            Log.w("pbel", perbelbilgi);


            params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "pushdataperbel");
            params.put("req", perbelbilgi);
            params.put("uid", uid);
            params.put("prgver", MainActivity.PROGRAM_VERSION);
            params.put("ttt", "" + System.currentTimeMillis());

            jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            jsonObj = new JSONObject(jsonStr);
            Log.w("pushdataperbel", jsonObj.getString("pushdataperbel"));
            if (jsonObj.getString("pushdataperbel").equals("true")) {
                for (int i = 0; i < resperbel.size(); i++) {
                    db.personelBelgeAktarildi(resperbel.get(i).get("OID"), uid);
                }
                db.dbstatDegerEkle("pushdata", "toserverpersonel", MainActivity.userid);
            }
        }catch (Exception ex){
            Log.w("PushData - Personel", ex.getMessage()+" "+whereIndex);
        }

    }

    private void sendPuantaj(){
        String whereIndex="puantaj";
        try {
            ArrayList<HashMap<String, String>> res2 = null;
            HashMap<String, String> res3 = null;
            ArrayList<HashMap<String, String>> res = db.getMultiResult(new String[]{"OID", "ISE_BASLAMA_TARIHI", "CALISMAVAR", "USER_ID", "FIRMA", "BOLGE", "CALISMA", "FISNO", "EKIP_LIDERI", "YETKILI", "SERVISVAR", "ACIKLAMA", "EKLIDOC1", "EKLIDOC2", "EKLIDOC3", "URUN", "GLOBALID"}, "tarim_istakip_calisma", "AKTARILDI=0 AND AKTARILDI_ONAY=0 and user_id='" + uid + "'");
            String puantaj = "{\"psayi\":\"" + res.size() + "\",\"puantaj\":[";
            for (int i = 0; i < res.size(); i++) {
                res2 = db.getMultiResult(new String[]{"OID", "SICILNO", "TICID", "URUNID", "GOREV", "MESAI", "KARTLAEKLENDI","TC","KARTNO", "GLOBALID"}, "tarim_istakip_calisma_personel", "AKTARILDI=0 AND AKTARILDI_ONAY=0 AND GLOBALID='" + res.get(i).get("GLOBALID") + "'");
                String gorev = "";
                for (int j = 0; j < res2.size(); j++) {
                    HashMap<String,String> perbilgisi=db.getOneRow(new String[]{"AD","SOYAD"},"tarim_istakip_personel","id="+res2.get(j).get("SICILNO") );
                    gorev += "{\"globalid\":\""+res2.get(j).get("GLOBALID")+"\",\"oid\":\"" + res2.get(j).get("OID") + "\",\"sicilno\":\"" + res2.get(j).get("SICILNO") + "\",\"ticid\":\"" + res2.get(j).get("TICID") + "\",\"urunid\":\"" + res2.get(j).get("URUNID") + "\",\"gorev\":\"" + res2.get(j).get("GOREV") + "\",\"mesai\":\"" + res2.get(j).get("MESAI") + "\",\"kartokutma\":\"" + res2.get(j).get("KARTLAEKLENDI") + "\",\"kartno\":\""+res2.get(j).get("KARTNO")+"\",\"tc\":\""+res2.get(j).get("TC")+"\",\"ad\":\""+perbilgisi.get("AD")+"\",\"soyad\":\""+perbilgisi.get("SOYAD")+"\"}";
                    if (j < res2.size() - 1)
                        gorev += ",";
                }
                res3 = db.getOneRow(new String[]{"OID", "TICID", "SERVIS1", "SERVIS2", "SERVIS3", "SERVIS4", "SERVIS5", "SERVIS6", "SERVIS7", "SERVIS8", "SERVIS9", "SERVIS10", "SERVIS11", "SERVIS12", "SERVIS13", "SERVIS14", "SERVIS15", "SERVIS16", "SERVIS17", "SERVIS18", "SERVIS19", "SERVIS20", "SERVIS1SAYI", "SERVIS2SAYI", "SERVIS3SAYI", "SERVIS4SAYI", "SERVIS5SAYI", "SERVIS6SAYI", "SERVIS7SAYI", "SERVIS8SAYI", "SERVIS9SAYI", "SERVIS10SAYI", "SERVIS11SAYI", "SERVIS12SAYI", "SERVIS13SAYI", "SERVIS14SAYI", "SERVIS15SAYI", "SERVIS16SAYI", "SERVIS17SAYI", "SERVIS18SAYI", "SERVIS19SAYI", "SERVIS20SAYI", "GLOBALID"}, "tarim_istakip_calisma_servis", "AKTARILDI=0 AND AKTARILDI_ONAY=0 AND GLOBALID='" + res.get(i).get("GLOBALID") + "'");
                String servis = "";
                if (res3.size() > 0) {
                    servis = "\"globalid\":\""+res3.get("GLOBALID")+"\",\"oid\":\"" + res3.get("OID") + "\",\"ticid\":\"" + res3.get("TICID") + "\",\"uid\":\"" + uid + "\"";
                    String tmp = "", tmpsayi = "";
                    for (int k = 1; k < 21; k++) {
                        tmp += "\"servis" + k + "\":\"" + res3.get("SERVIS" + k) + "\"";
                        tmpsayi += "\"servis" + k + "sayi\":\"" + res3.get("SERVIS" + k + "SAYI") + "\"";
                        if (k < 20) {
                            tmp += ",";
                            tmpsayi += ",";
                        }
                    }
                    //servis = "\"oid\":\"" + res3.get("OID") + "\",\"ticid\":\""+res3.get("TICID")+"\","+tmp+","+tmpsayi;
                    servis += "," + tmp + "," + tmpsayi;
                }

                String ek1 = "", ek2 = "", ek3 = "";
                if (res.get(i).get("EKLIDOC1") != null && !res.get(i).get("EKLIDOC1").toString().equals("")) {
                    ek1 = res.get(i).get("EKLIDOC1").toString();
                    ek1 = ek1.substring(ek1.lastIndexOf('/') + 1);
                    uploadFile(res.get(i).get("EKLIDOC1"));
                }

                if (res.get(i).get("EKLIDOC2") != null && !res.get(i).get("EKLIDOC2").toString().equals("")) {
                    ek2 = res.get(i).get("EKLIDOC2").toString();
                    ek2 = ek2.substring(ek2.lastIndexOf('/') + 1);
                    uploadFile(res.get(i).get("EKLIDOC2"));
                }

                if (res.get(i).get("EKLIDOC3") != null && !res.get(i).get("EKLIDOC3").toString().equals("")) {
                    ek3 = res.get(i).get("EKLIDOC3").toString();
                    ek3 = ek3.substring(ek3.lastIndexOf('/') + 1);
                    uploadFile(res.get(i).get("EKLIDOC3"));
                }

                //Log.w("pushdata",ek1+" "+ek2+" "+ek3);
                puantaj += "{\"globalid\":\""+res.get(i).get("GLOBALID")+"\",\"ticid\":\"" + res.get(i).get("OID") + "\",\"ibt\":\"" + res.get(i).get("ISE_BASLAMA_TARIHI") + "\",\"calismavar\":\"" + res.get(i).get("CALISMAVAR") + "\",\"uid\":\"" + res.get(i).get("USER_ID") + "\",\"firma\":\"" + res.get(i).get("FIRMA") + "\",\"bolge\":\"" + res.get(i).get("BOLGE") + "\",\"calisma\":\"" + res.get(i).get("CALISMA") + "\",\"fisno\":\"" + res.get(i).get("FISNO") + "\",\"ekiplideri\":\"" + res.get(i).get("EKIP_LIDERI") + "\",\"yetkili\":\"" + res.get(i).get("YETKILI") + "\",\"servisvar\":\"" + res.get(i).get("SERVISVAR") + "\",\"aciklama\":\"" + res.get(i).get("ACIKLAMA") + "\",\"ek1\":\"" + ek1 + "\",\"ek2\":\"" + ek2 + "\",\"ek3\":\"" + ek3 + "\",\"urunid\":\"" + res.get(i).get("URUN") + "\",\"gorev\":[" + gorev + "],\"servis\":{" + servis + "}}";
                if (i < res.size() - 1) {
                    puantaj += ",";
                }
            }
            puantaj += "]}";

            Log.w("sync", puantaj);

            // Making a request to url and getting response
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("token", "6ce304f73ce841efaf1490bb98474eef");
            params.put("op", "pushdata2");
            params.put("req", puantaj);
            params.put("uid", uid);
            params.put("prgver", MainActivity.PROGRAM_VERSION);
            params.put("ttt", "" + System.currentTimeMillis());

            WebRequest webreq = new WebRequest();;
            String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

            Log.w("pushdata2", jsonStr);
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.getString("pushdata2").equals("true")) {
                for (int i = 0; i < res.size(); i++) {
                    if (res2 != null) {
                        for (int j = 0; j < res2.size(); j++) {
                            db.puantajBilgileriAktarildi("tarim_istakip_calisma_personel", res2.get(j).get("OID"), uid);
                        }
                    }
                    db.puantajBilgileriAktarildi("tarim_istakip_calisma", res.get(i).get("OID"), uid);
                    db.puantajBilgileriAktarildi("tarim_istakip_calisma_servis", res.get(i).get("OID"), uid);
                }

                db.dbstatDegerEkle("pushdata", "toserverpuantaj", MainActivity.userid);
            }
        }catch (Exception ex){
            Log.w("PushData - Puantaj", ex.getMessage()+" "+whereIndex);
        }
    }
    //
    //  String name=file.substring(file.lastIndexOf('/')+1);
    public int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (selectedFile.isFile()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                //URL url = new URL("http://sistem.guleryuzcv.net/vtys/vtys.php?token=6ce304f73ce841efaf1490bb98474eef&op=uploadek&uid=uid&tt="+System.currentTimeMillis());
                URL url = new URL( "http://www.guleryuzcv.net/t_istakip/mobilsrv/vtys.php?token=6ce304f73ce841efaf1490bb98474eef&op=uploadek&uid=uid&tt="+System.currentTimeMillis());
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i("uploadfile", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
                        }
                    });*/
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (FileNotFoundException e) {
                Log.w("uploadFile", "File Not Found");
                /*e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });*/
            } catch (MalformedURLException e) {
                Log.w("uploadFile", "URL error!");
                /*e.printStackTrace();
                Toast.makeText(MainActivity.this, "URL error!", Toast.LENGTH_SHORT).show();
*/
            } catch (IOException e) {
                Log.w("uploadFile", "Cannot Read/Write File!");
  /*              e.printStackTrace();
                Toast.makeText(MainActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();*/
            }
        }
        return serverResponseCode;
    }
}
