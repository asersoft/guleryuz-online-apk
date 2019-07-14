package com.guleryuz.puantajonline.OnlineService;

import android.content.Context;
import android.util.Log;

import com.guleryuz.puantajonline.Connectivity;
import com.guleryuz.puantajonline.KeyValueP;
import com.guleryuz.puantajonline.MainActivity;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.ShowToast;
import com.guleryuz.puantajonline.WebRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerData {
    private Context context;
    public ServerData(Context cntx){
        context=cntx;
    }

    public List<HashMap<String, String>> getDataFromServer(String uid, String reqtype, List<String[]> reqparam, List<String[]> resp){
        List<HashMap<String, String>> data=null;
        try{
            Connectivity conn=new Connectivity();
            if (conn.isConnected(context) || conn.isConnectedMobile(context) || conn.isConnectedWifi(context)) {

                WebRequest webreq = new WebRequest();

                // Making a request to url and getting response
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("token", "6ce304f73ce841efaf1490bb98474eef");
                params.put("uid", uid);
                params.put("ttt", "" + System.currentTimeMillis());
                if (reqparam != null) {
                    for (int i = 0; i < reqparam.size(); i++) {
                        String[] rq = reqparam.get(i);
                        params.put(rq[0], rq[1]);
                        Log.w("params",rq[0]+" "+rq[1]);
                    }
                }


                String jsonStr = webreq.makeWebServiceCall(context.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

                Log.w("Response: ", "> " + jsonStr);

                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray values = jsonObj.getJSONArray("result");
                if (values != null) {
                    data = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject c = values.getJSONObject(i);

                        HashMap<String, String> tmp = new HashMap<String, String>();

                        if (resp != null) {
                            for (int j = 0; j < resp.size(); j++) {
                                String[] rq = resp.get(j);
                                tmp.put(rq[0], c.getString(rq[1]));
                            }
                        }

                        data.add(tmp);
                    }
                }

            }else{
                new ShowToast(context, "İnternet Bağlantınızı Kontrol Ediniz.");
            }
        }catch (Exception ex){
            Log.w(reqtype+"Online", ex.getMessage());
        }finally {
        }

        return data;
    }

    public List<HashMap<String, String>> personelSorgula(String userid, String kartno, String sicilno, String ad, String soyad, String tckimlik, String ekiplideri, int ekipLideriBolgeKisiti){
        List<HashMap<String, String>> personelbilgileri = null;
        try {
            List<String[]> req = new ArrayList<String[]>();
            req.add(new String[]{"op", "persor"});
            req.add(new String[]{"kartno", kartno});
            req.add(new String[]{"sicilno", sicilno});
            req.add(new String[]{"ad", ad});
            req.add(new String[]{"soyad", soyad});
            req.add(new String[]{"tckimlik", tckimlik});

            List<String[]> resp = new ArrayList<String[]>();
            resp.add(new String[]{"ID", "id"});
            resp.add(new String[]{"TC", "tc"});
            resp.add(new String[]{"AD", "ad"});
            resp.add(new String[]{"SOYAD", "soyad"});
            resp.add(new String[]{"DOGUMTARIHI", "dogumtarihi"});
            resp.add(new String[]{"CINSIYET", "cinsiyet"});
            resp.add(new String[]{"GOREV", "gorev"});
            resp.add(new String[]{"BOLGE", "bolge"});
            resp.add(new String[]{"BOLGE2", "bolge2"});
            resp.add(new String[]{"BOLGE3", "bolge3"});
            resp.add(new String[]{"BOLGE4", "bolge4"});
            resp.add(new String[]{"BOLGE5", "bolge5"});
            resp.add(new String[]{"EKIP_LIDERI", "ekip_lideri"});
            resp.add(new String[]{"EKIP_LIDERI2", "ekip_lideri2"});
            resp.add(new String[]{"EKIP_LIDERI3", "ekip_lideri3"});
            resp.add(new String[]{"EKIP_LIDERIID", "ekip_lideriid"});
            resp.add(new String[]{"EKIP_LIDERI2ID", "ekip_lideri2id"});
            resp.add(new String[]{"EKIP_LIDERI3ID", "ekip_lideri3id"});
            resp.add(new String[]{"RESIM", "resim"});
            resp.add(new String[]{"KARTNO", "kartno"});
            resp.add(new String[]{"SGK_EVRAK", "sgk_evrak"});
            resp.add(new String[]{"SSK", "ssk"});
            resp.add(new String[]{"SSK_CIKIS", "ssk_cikis"});
            resp.add(new String[]{"DEVAM", "devam"});
            resp.add(new String[]{"ONAY", "onay"});

            personelbilgileri = this.getDataFromServer(userid, "PersonelBilgisiGetir", req, resp);
            Log.w("s--->",""+ekipLideriBolgeKisiti+"-"+ekiplideri);
            if (ekipLideriBolgeKisiti==1 && !(personelbilgileri.get(0).get("EKIP_LIDERIID").equals(ekiplideri) || personelbilgileri.get(0).get("EKIP_LIDERI2ID").equals(ekiplideri) || personelbilgileri.get(0).get("EKIP_LIDERI3ID").equals(ekiplideri)))
            {
                new ShowToast(MainActivity.mactivity, "Personel Yetki alanınız dışındadır.");
                personelbilgileri=null;
            }
        }catch (Exception ex){
            Log.w("SDPersonel", ex.getStackTrace().toString());
        }

        return personelbilgileri;
    }

    public boolean personelKartnoUpdate(String userid, String kartno, String sicilno, String islem, String prgver){
        boolean stat=false;

        try {
            List<String[]> req = new ArrayList<String[]>();
            req.add(new String[]{"op", "pushdatake"});
            req.add(new String[]{"kartno", kartno});
            req.add(new String[]{"sicilno", sicilno});
            req.add(new String[]{"islem", islem});
            req.add(new String[]{"prgver",prgver});

            List<String[]> resp = new ArrayList<String[]>();
            resp.add(new String[]{"stat", "stat"});

            List<HashMap<String, String>> kartnoislem = this.getDataFromServer(userid, "PerKartUpdate", req, resp);
            if(kartnoislem!=null && kartnoislem.size()>0){
                stat=true;
            }
        }catch (Exception ex){
            Log.w("PerKartUpdate", ex.getStackTrace().toString());
        }

        return stat;
    }

    public KeyValueP[] belgeturGetir(String userid, String prgver){
        boolean stat=false;
        KeyValueP[] items=null;
        try {
            List<String[]> req = new ArrayList<String[]>();
            req.add(new String[]{"op", "belgetur"});
            req.add(new String[]{"prgver",prgver});

            List<String[]> resp = new ArrayList<String[]>();
            resp.add(new String[]{"id", "id"});
            resp.add(new String[]{"ad", "ad"});

            List<HashMap<String, String>> belgetur = this.getDataFromServer(userid, "PerKartUpdate", req, resp);
            items=new KeyValueP[(belgetur!=null?belgetur.size()+1:1)];
            items[0]=new KeyValueP("-1", "Seçiniz");
            if(belgetur!=null && belgetur.size()>0){
                for (int i=0; i<belgetur.size(); i++){
                    items[i+1]=new KeyValueP(""+belgetur.get(i).get("id"), belgetur.get(i).get("ad"));
                }
            }
        }catch (Exception ex){
            Log.w("PerKartUpdate", ex.getStackTrace().toString());
        }

        return items;
    }
}
