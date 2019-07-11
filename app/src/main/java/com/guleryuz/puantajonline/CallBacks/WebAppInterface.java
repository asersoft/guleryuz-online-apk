package com.guleryuz.puantajonline.CallBacks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.guleryuz.puantajonline.GunlukPuantaj2;
import com.guleryuz.puantajonline.GunlukPuantajData;
import com.guleryuz.puantajonline.MainActivity;
import com.guleryuz.puantajonline.OnlineService.DataFromService;
import com.guleryuz.puantajonline.OnlineService.ServerData;
import com.guleryuz.puantajonline.PhotoZoom;
import com.guleryuz.puantajonline.R;
import com.guleryuz.puantajonline.ShowToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WebAppInterface implements ServiceCallBack {
    Context mContext;
    private WebAppInterface wai;
    private List<HashMap<String, String>> calisma;
    private String ibt;
    private String gid;

    public WebAppInterface(Context c) {
        mContext = c;
        wai=this;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String t) {
        String msg ="";
        switch (t){
            case "connErr":
                msg=mContext.getResources().getString(R.string.msgInternetNoConnection);
                break;
        }
        if(!msg.equals("")) {
            new ShowToast(mContext, msg);
        }
    }

    @JavascriptInterface
    public void zoomDoc(String id, String filename) {
        //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, PhotoZoom.class);
        intent.putExtra("photo",  mContext.getResources().getString(R.string.docUrl)+filename);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void editPuantaj(final String globalid, final String fisno){
        gid=globalid;
        List<String[]> req=new ArrayList<String[]>();
        req.add(new String[]{"op","calisma_getir2"});
        req.add(new String[]{"globalid", globalid});
        req.add(new String[]{"fisno", fisno});

        List<String[]> resp=new ArrayList<String[]>();
        resp.add(new String[]{"id","id"});
        resp.add(new String[]{"firma","firma"});
        resp.add(new String[]{"bolge","bolge"});
        resp.add(new String[]{"calisma","calisma"});
        resp.add(new String[]{"yetkili","yetkili"});
        resp.add(new String[]{"ekiplideri","ekiplideri"});
        resp.add(new String[]{"bolgekisiti","bolgekisiti"});
        resp.add(new String[]{"calismavar","calismavar"});
        resp.add(new String[]{"servisvar","servisvar"});
        resp.add(new String[]{"aciklama","aciklama"});
        resp.add(new String[]{"eklidoc1","eklidoc1"});
        resp.add(new String[]{"eklidoc2","eklidoc2"});
        resp.add(new String[]{"eklidoc3","eklidoc3"});
        resp.add(new String[]{"islendi","islendi"});
        resp.add(new String[]{"ibt","ibt"});
        resp.add(new String[]{"urun","urunid"});
        resp.add(new String[]{"gorevjson","gorevjson"});
        resp.add(new String[]{"servisjson","servisjson"});

        calisma = new ServerData(mContext).getDataFromServer(MainActivity.userid,"CalismaGetir2", req, resp);
        if (calisma != null && calisma.size() > 0) {
            if (calisma.get(0).get("calismavar").equals("1")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Uyarı");
                builder.setMessage("Düzenlemek istediğinize emin misiniz?");
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //String[] ntarih = calisma.get(0).get("ibt").split("-");
                        //String ibt = ntarih[2] + " / " + ntarih[1] + " / " + ntarih[0];
                        ibt=calisma.get(0).get("ibt");
                        //Log.w("tarih", ntarih[2] + " / " + ntarih[1] + " / " + ntarih[0]);
                        MainActivity.gpd = new GunlukPuantajData(MainActivity.userid, calisma.get(0).get("bolge"), calisma.get(0).get("calisma"), calisma.get(0).get("firma"), calisma.get(0).get("yetkili"), calisma.get(0).get("urun"),  calisma.get(0).get("ekiplideri"), (calisma.get(0).get("bolgekisiti").equals("0")?0:1), ibt,  fisno, "guncelleme");
                        MainActivity.gpd.setGlobalid(globalid);
                        MainActivity.gpd.setFisno(fisno);

                        /*List<String[]> req=new ArrayList<String[]>();
                        req.add(new String[]{"op","gorev"});
                        req.add(new String[]{"firmaid", calisma.get(0).get("firma")});
                        req.add(new String[]{"bolge", calisma.get(0).get("bolge")});

                        List<String[]> resp=new ArrayList<String[]>();
                        resp.add(new String[]{"id","id"});
                        resp.add(new String[]{"gorev","gorev"});
                        List<HashMap<String, String>> data2 = new ServerData(mContext).getDataFromServer(MainActivity.userid,"GorevGetir", req, resp);
                        if(data2!=null) {
                            for (int i = 0; i < data2.size(); i++) {
                                //gorevler.add(new KeyValueP(data2.get(i).get("id"),data2.get(i).get("gorev")));
                                MainActivity.gpd.addGorev("" + data2.get(i).get("gorev"));
                            }
                        }*/

                        DataFromService gorevler = new DataFromService(wai);
                        gorevler.context=mContext;
                        gorevler.uid=MainActivity.userid;
                        gorevler.reqtype="Gorevler";
                        gorevler.title="Görevler";
                        gorevler.reqparam=new ArrayList<String[]>();
                        gorevler.reqparam.add(new String[]{"op", "gorev"});
                        gorevler.reqparam.add(new String[]{"firmaid", calisma.get(0).get("firma")});
                        gorevler.reqparam.add(new String[]{"bolge", calisma.get(0).get("bolge")});
                        gorevler.resp=new ArrayList<String[]>();
                        gorevler.resp.add(new String[]{"id","id"});
                        gorevler.resp.add(new String[]{"gorev","gorev"});
                        gorevler.execute();

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    @Override
    public void PipeAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error) {
        if (type.equals("GorevlerOnline")){
            if(data!=null) {
                for (int i = 0; i < data.size(); i++) {
                    MainActivity.gpd.addGorev("" + data.get(i).get("gorev"));
                }

                DataFromService gorevler = new DataFromService(wai);
                gorevler.context=mContext;
                gorevler.uid=MainActivity.userid;
                gorevler.reqtype="CalismaGetir";
                gorevler.title="Çalışma Getir";
                gorevler.reqparam=new ArrayList<String[]>();
                gorevler.reqparam.add(new String[]{"op", "calisma_getir"});
                gorevler.reqparam.add(new String[]{"firmaid", calisma.get(0).get("firma")});
                gorevler.reqparam.add(new String[]{"bolge", calisma.get(0).get("bolge")});
                gorevler.reqparam.add(new String[]{"calisma", calisma.get(0).get("calisma")});
                gorevler.reqparam.add(new String[]{"yetkili", calisma.get(0).get("yetkili")});
                gorevler.reqparam.add(new String[]{"ekiplideri", calisma.get(0).get("ekiplideri")});
                gorevler.reqparam.add(new String[]{"tarih", ibt});
                gorevler.reqparam.add(new String[]{"durum", "guncelle"});
                gorevler.reqparam.add(new String[]{"globalid", gid});
                gorevler.resp=new ArrayList<String[]>();
                gorevler.resp.add(new String[]{"stat","stat"});
                gorevler.execute();


                /*List<String[]> req=new ArrayList<String[]>();
                req.add(new String[]{"op","calisma_getir"});
                req.add(new String[]{"firmaid", calisma.get(0).get("firma")});
                req.add(new String[]{"bolge", calisma.get(0).get("bolge")});
                req.add(new String[]{"calisma", calisma.get(0).get("calisma")});
                req.add(new String[]{"yetkili", calisma.get(0).get("yetkili")});
                req.add(new String[]{"ekiplideri", calisma.get(0).get("ekiplideri")});
                req.add(new String[]{"tarih", ibt});
                req.add(new String[]{"durum", "guncelle"});
                req.add(new String[]{"globalid", gid});

                List<String[]> resp=new ArrayList<String[]>();
                resp.add(new String[]{"stat","stat"});*/

                //List<HashMap<String, String>> calismaGunceleme = new ServerData(mContext).getDataFromServer(MainActivity.userid,"CalismaGetir3", req, resp);
            }else{
                new ShowToast(mContext, "Puantaj bilgileri alınırken hata oluştu.");
            }
        }else if(type.equals("CalismaGetirOnline")){
            if(data!=null && data.size()>0 && data.get(0).get("stat").equals("true")) {
                //MainActivity.gpd.setGlobalid(calisma.get(0).get("id"));
                //MainActivity.gpd.setFisno(calisma.get(0).get("fisno"));
                MainActivity.gpd.setEkliDoc(calisma.get(0).get("eklidoc1"), calisma.get(0).get("eklidoc2"), calisma.get(0).get("eklidoc3"), calisma.get(0).get("aciklama"));
                try {
                    JSONObject jsonObj = new JSONObject("{\"gorev\":" + calisma.get(0).get("gorevjson") + "}");
                    JSONArray values = jsonObj.getJSONArray("gorev");
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject c = values.getJSONObject(i);
                        MainActivity.gpd.addPersonel(c.getString("gorev"), c.getString("sicilno"), c.getString("ad") + " " + c.getString("soyad"), c.getString("cinsiyet"), c.getString("mesai"), c.getString("kartokutma"), c.getString("urunid"), c.getString("soyad"), c.getString("tc"), c.getString("kartno"));
                    }

                    Log.w("gp1servis", "" + calisma.get(0).get("servisvar"));
                    /*
                    if (Integer.parseInt(calisma.get(0).get("servisvar")) > 0) {
                        JSONObject servis = new JSONObject(calisma.get(0).get("servisjson"));
                        JSONArray keys = servis.names();
                        for (int i = 0; i < keys.length(); ++i) {
                            if (servis.getString(keys.getString(i)) != null && !servis.getString(keys.getString(i)).equals("0")) {
                                MainActivity.gpd.addServis("" + i, servis.getString(keys.getString(i)));
                            }
                        }
                    } else if (Integer.parseInt(calisma.get(0).get("servisvar")) == -1) {
                        MainActivity.gpd.addServis("KisiBasi", "KisiBasi");
                    }
                    */
                     if (Integer.parseInt(calisma.get(0).get("servisvar")) > 0) {
                        JSONObject servis = new JSONObject(calisma.get(0).get("servisjson"));
                        for (int i = 0; i < 20; ++i) {
                            if (servis.getString("servis"+(i+1)+"sayi") != null && !servis.getString("servis"+(i+1)+"sayi").equals("0")) {
                                MainActivity.gpd.addServis("" + i, servis.getString("servis"+(i+1)+"sayi"));
                            }
                        }
                    } else if (Integer.parseInt(calisma.get(0).get("servisvar")) == -1) {
                        MainActivity.gpd.addServis("KisiBasi", "KisiBasi");
                    }



                    Intent intent = new Intent(mContext, GunlukPuantaj2.class);
                    intent.putExtra("EkipLideri",calisma.get(0).get("ekiplideri"));
                    MainActivity.mactivity.startActivityForResult(intent, MainActivity.REQUEST_GP2);
                } catch (Exception ex) {
                    Log.w("GPD Güncelleme", ex.getMessage());
                }
            }else{
                new ShowToast(mContext, "Puantaj bilgileri sisteme aktarılmış olduğundan güncellenememektedir.");
            }
        }
    }
}
