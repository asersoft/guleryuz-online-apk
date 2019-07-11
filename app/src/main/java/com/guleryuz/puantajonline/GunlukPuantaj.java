package com.guleryuz.puantajonline;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Key;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guleryuz.puantajonline.CallBacks.ServiceCallBack;
import com.guleryuz.puantajonline.CallBacks.TaskCallback;
import com.guleryuz.puantajonline.OnlineService.DataFromService;
import com.guleryuz.puantajonline.OnlineService.FirmaBolgeOnline;
import com.guleryuz.puantajonline.OnlineService.FirmaOnline;
import com.guleryuz.puantajonline.OnlineService.ServerData;
import com.guleryuz.puantajonline.OnlineService.UploadFileToServer;

/**
 * Created by mehmet_erenoglu on 27.02.2017.
 */

public class GunlukPuantaj extends AppCompatActivity implements View.OnClickListener, TaskCallback, ServiceCallBack {
    private Spinner spnIBasBolge, spnIBasCalisma, spnIBasFirma, spnIBasYetkili, spnIBasEkiplideri, spnIBasUrun;
    private RelativeLayout layoutIBas;
    private ArrayAdapter<String> daIBasBolge, daIBasCalisma;
    private ArrayAdapter<KeyValueP> daIBasFirma, daIBasYetkili, daIBasEkiplideri, daIBasUrun;//, daIBasGorev
    private static String ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasEkipLideri, ibasUrun, ibasEkipLideriAdi;
    private static TextView ibasTarih;
    private int ekipLideriBolgeKisiti;
    private Button ibasBtnIptal, ibasBtnSonraki, ibasBtnCalismaYok;//btnIBasBarkodOku, btnIBasOnay, btnIBasBarkodYeni;
    static final int REQUEST_GP2=1;

    private String ibasFisno;
    private Connectivity conn;
    private static Context ParentCtxt;
    private static String userid;
    private static GunlukPuantaj thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ise_baslama);
        ParentCtxt=this;
        thisActivity=this;

        Intent intent= getIntent();
        Bundle b= intent.getExtras();
        if (b!=null){
            userid=b.getString("userid");
        }

        if (!intent.hasExtra("calismayok")){//bekleyen puantajlar çalıştığında
            MainActivity.gpd=null;
        }

        ibasBtnIptal=(Button)findViewById(R.id.ibasBtnIptal);
        ibasBtnIptal.setOnClickListener(this);
        ibasBtnCalismaYok=(Button)findViewById(R.id.ibasBtnCalismaYok);
        ibasBtnCalismaYok.setOnClickListener(this);

        layoutIBas=(RelativeLayout) findViewById(R.id.layoutIBas);
        spnIBasBolge=(Spinner)findViewById(R.id.ibasbolge);
        spnIBasCalisma=(Spinner)findViewById(R.id.ibascalisma);
        spnIBasFirma=(Spinner)findViewById(R.id.ibasfirma);
        spnIBasYetkili=(Spinner)findViewById(R.id.ibasyetkili);
        spnIBasEkiplideri=(Spinner)findViewById(R.id.ibasekiplideri);
        spnIBasUrun=(Spinner)findViewById(R.id.ibasurun);
        ibasTarih=(TextView)findViewById(R.id.ibastarih);

        final Calendar c2 = Calendar.getInstance();
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH)+1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        ibasTarih.setText(day2+" / "+month2+" / "+year2);
        //db=new Database(getApplicationContext());

        //ibasTarih.setText(getDate());
        ibasFisno = UUID.randomUUID().toString();
        Log.w("gp fisno",ibasFisno);
        ibasBtnSonraki=(Button)findViewById(R.id.ibasBtnSonraki);
        ibasBtnSonraki.setOnClickListener(this);


        ibasBolge="";
        ibasCalisma="";
        ibasFirma="";
        ibasYetkili="";
        ibasEkipLideri="";
        ibasUrun="";
        ekipLideriBolgeKisiti=0;

        FirmaOnline firma=new FirmaOnline(this);
        firma.context = this;
        //firma.db=db;
        firma.uid=userid;
        firma.execute();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ibasBtnIptal) {
            finish();
        }else if(v.getId()==R.id.ibasBtnCalismaYok) {
            //db=new Database(getApplicationContext());
            if(MainActivity.gpd==null || (MainActivity.gpd!=null && !MainActivity.gpd.getKayitdurumu().equals("guncelleme"))) {
                List<String[]> req=new ArrayList<String[]>();
                req.add(new String[]{"op","calisma_getir"});
                req.add(new String[]{"firmaid", ibasFirma});
                req.add(new String[]{"bolge", ibasBolge});
                req.add(new String[]{"calisma", ibasCalisma});
                req.add(new String[]{"yetkili", ibasYetkili});
                req.add(new String[]{"ekiplideri", ibasEkipLideri});
                req.add(new String[]{"tarih", ibasTarih.getText().toString()});

                List<String[]> resp=new ArrayList<String[]>();
                resp.add(new String[]{"id","id"});

                List<HashMap<String, String>> calisma = new ServerData(this).getDataFromServer(userid,"CalismaGetir2", req, resp);


                //String[] ntarih = ibasTarih.getText().toString().split(" / ");
                //final HashMap<String, String> calisma = db.getOneRow(new String[]{"OID"}, "tarim_istakip_calisma", "bolge='" + ibasBolge + "' and calisma='" + ibasCalisma + "' and firma='" + ibasFirma + "' and  yetkili='" + ibasYetkili + "' and ekip_lideri='" + ibasEkipLideri + "'and ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "'");
                if (calisma==null || calisma.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                    builder.setTitle("Uyarı");
                    builder.setMessage("Çalışma Yok seçtiniz. Emin misiniz?");

                    builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.gpd = new GunlukPuantajData(userid, ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasUrun, ibasEkipLideri, ekipLideriBolgeKisiti, ibasTarih.getText().toString(), ibasFisno, "yenikayit");
                            MainActivity.gpd.setGlobalid(UUID.randomUUID().toString());
                            MainActivity.gpd.setCalismavar(0);

                            try {
                                DataFromService puantajsrv = new DataFromService(thisActivity);
                                puantajsrv.context=ParentCtxt;
                                puantajsrv.uid=MainActivity.userid;
                                puantajsrv.reqtype="PuantajGonder";
                                puantajsrv.title="Puantaj";
                                puantajsrv.reqparam=new ArrayList<String[]>();
                                puantajsrv.reqparam.add(new String[]{"op", "pushdata2"});
                                puantajsrv.reqparam.add(new String[]{"prgver", MainActivity.PROGRAM_VERSION});
                                puantajsrv.reqparam.add(new String[]{"firmaid", MainActivity.gpd.getFirma()});
                                puantajsrv.reqparam.add(new String[]{"bolge2", MainActivity.gpd.getBolge()});
                                puantajsrv.reqparam.add(new String[]{"bolge", MainActivity.gpd.getCalismaalani()});
                                puantajsrv.reqparam.add(new String[]{"yetkili", MainActivity.gpd.getYetkili()});
                                puantajsrv.reqparam.add(new String[]{"ekiplideri", MainActivity.gpd.getEkiplideri()});
                                puantajsrv.reqparam.add(new String[]{"calismavar", ""+MainActivity.gpd.getCalismavar()});
                                puantajsrv.reqparam.add(new String[]{"globalid", MainActivity.gpd.getGlobalid()});
                                puantajsrv.reqparam.add(new String[]{"fisno", MainActivity.gpd.getFisno()});
                                puantajsrv.reqparam.add(new String[]{"isebastarihi", MainActivity.gpd.getTarih()});
                                puantajsrv.reqparam.add(new String[]{"urun", MainActivity.gpd.getUrun()});

                                String puantaj = "{\"psayi\":\"1\",\"puantaj\":[";

                                String jsonservis="";
                                String jsongorev="";
                                String[] ntarih = MainActivity.gpd.getTarih().toString().split(" / ");
                                String ttarih= ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0];

                                puantaj += "{\"globalid\":\""+ MainActivity.gpd.getGlobalid() +"\",\"ibt2\":\"" +  ttarih  + "\",\"ibt\":\"" +  MainActivity.gpd.getTarih() + "\",\"calismavar\":\"" + MainActivity.gpd.getCalismavar() + "\",\"uid\":\"" + MainActivity.userid + "\",\"firma\":\"" + MainActivity.gpd.getFirma() + "\",\"bolge\":\"" + MainActivity.gpd.getBolge() + "\",\"calisma\":\"" + MainActivity.gpd.getCalismaalani() + "\",\"fisno\":\"" + MainActivity.gpd.getFisno() + "\",\"ekiplideri\":\"" + MainActivity.gpd.getEkiplideri() + "\",\"yetkili\":\"" + MainActivity.gpd.getYetkili() + "\",\"servisvar\":\"" + MainActivity.gpd.getServisSayisi() + "\",\"aciklama\":\"" + MainActivity.gpd.getAciklama() + "\",\"ek1\":\"\",\"ek2\":\"\",\"ek3\":\"\",\"urunid\":\"" + MainActivity.gpd.getUrun() + "\",\"gorev\":[" + jsongorev + "],\"servis\":{" + jsonservis + "}}]}";
                                puantajsrv.reqparam.add(new String[]{"gorevjson", "[]"});
                                puantajsrv.reqparam.add(new String[]{"servisjson", "{}"});
                                puantajsrv.reqparam.add(new String[]{"puantaj", puantaj});
                                Log.w("puantajGonder", puantaj);
                                puantajsrv.resp=new ArrayList<String[]>();
                                puantajsrv.resp.add(new String[]{"stat", "stat"});
                                puantajsrv.execute();

                            }catch (Exception ex){
                                Log.w("puantajGonder", "nothing");
                            }

                            dialog.dismiss();
                            //db.isbaslamaOnay(MainActivity.gpd, 0);
                        }
                    });
                    builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ibasBtnSonraki.setEnabled(true);
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    //Toast.makeText(ParentCtxt, "Bu bilgilere göre önceden kayıt eklenmiş", Toast.LENGTH_SHORT).show();
                    /*if(MainActivity.gpd.getKayitdurumu().equals("guncelleme")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                        builder.setTitle("Uyarı");
                        builder.setMessage("Çalışma Yok seçtiniz. Emin misiniz?");

                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.gpd.setCalismavar(0);
                                db.gunlukPuantajPersonelServisSil(calisma.get("oid"),ibasTarih.getText().toString());
                                db.isbaslamaOnay(MainActivity.gpd, 0);
                                ibasBtnSonraki.setEnabled(false);
                                finish();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ibasBtnSonraki.setEnabled(true);
                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }*/
                }
            }
        }else if(v.getId()==R.id.ibasBtnSonraki){
            if(!ibasFirma.equals("") && !ibasBolge.equals("") && !ibasCalisma.equals("") && !ibasYetkili.equals("")){
                    nextToPuantaj2Screen();
            }else{
                new ShowToast(this, "Boş alanları kontrol ediniz.");
            }
        }
    }

    private void nextToPuantaj2Screen(){

        try{
            List<String[]> req=new ArrayList<String[]>();
            req.add(new String[]{"op","calisma_getir"});
            req.add(new String[]{"firmaid", ibasFirma});
            req.add(new String[]{"bolge", ibasBolge});
            req.add(new String[]{"calisma", ibasCalisma});
            req.add(new String[]{"yetkili", ibasYetkili});
            req.add(new String[]{"ekiplideri", ibasEkipLideri});
            req.add(new String[]{"tarih", ibasTarih.getText().toString()});

            List<String[]> resp=new ArrayList<String[]>();
            resp.add(new String[]{"id","id"});
            resp.add(new String[]{"calismavar","calismavar"});
            resp.add(new String[]{"servisvar","servisvar"});
            resp.add(new String[]{"aciklama","aciklama"});
            resp.add(new String[]{"eklidoc1","eklidoc1"});
            resp.add(new String[]{"eklidoc2","eklidoc2"});
            resp.add(new String[]{"eklidoc3","eklidoc3"});
            resp.add(new String[]{"islendi","islendi"});
            resp.add(new String[]{"fisno","fisno"});
            resp.add(new String[]{"gorevjson","gorevjson"});
            resp.add(new String[]{"servisjson","servisjson"});

            List<HashMap<String, String>> calisma = new ServerData(this).getDataFromServer(userid,"CalismaGetir2", req, resp);

            Log.w("nextToPuantaj2Screen", "here - gpd:"+(MainActivity.gpd==null?"null":"not null:"+MainActivity.gpd.getKayitdurumu())+" - calisma:"+(calisma!=null?calisma.size():""));
            if (MainActivity.gpd == null) {
                if (calisma != null && calisma.size() > 0) {
                    if (calisma.get(0).get("islendi").equals("1")) {
                        new ShowToast(ParentCtxt, "Bu bilgilere göre önceden kayıt eklenmiş ve işlenmiştir.");
                        ibasBtnCalismaYok.setEnabled(false);
                        ibasBtnSonraki.setEnabled(false);
                    } else { //-1, 0 durumlarında//if(calisma.get(0).get("islendi").equals("0")) { //güncellenebilir
                        AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                        builder.setTitle("Uyarı");
                        builder.setMessage("Bu bilgilere ait önceden puantaj oluşturulmuş. Düzenlemek istediğinize emin misiniz?");

                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.gpd = new GunlukPuantajData(userid, ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasUrun, ibasEkipLideri, ekipLideriBolgeKisiti, ibasTarih.getText().toString(), ibasFisno, "guncelleme");

                                ibasBtnSonraki.callOnClick();
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
                } else {
                    MainActivity.gpd = new GunlukPuantajData(userid, ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasUrun, ibasEkipLideri, ekipLideriBolgeKisiti, ibasTarih.getText().toString(), ibasFisno, "yenikayit");
                    MainActivity.gpd.setGlobalid(UUID.randomUUID().toString());
                    req=new ArrayList<String[]>();
                    req.add(new String[]{"op","gorev"});
                    req.add(new String[]{"firmaid", ibasFirma});
                    req.add(new String[]{"bolge", ibasBolge});

                    resp=new ArrayList<String[]>();
                    resp.add(new String[]{"id","id"});
                    resp.add(new String[]{"gorev","gorev"});
                    List<HashMap<String, String>> data2 = new ServerData(this).getDataFromServer(userid,"GorevGetir", req, resp);
                    //List<KeyValueP> gorevler = new ArrayList<KeyValueP>();
                    if(data2!=null) {
                            for (int i = 0; i < data2.size(); i++) {
                                //gorevler.add(new KeyValueP(data2.get(i).get("id"),data2.get(i).get("gorev")));
                                MainActivity.gpd.addGorev("" + data2.get(i).get("gorev"));
                            }
                    }

                    //Send data to server
                    try {
                        conn=new Connectivity();
                        if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                            HashMap<String, String> params=new HashMap<String, String>();
                            params.put("token","6ce304f73ce841efaf1490bb98474eef");
                            params.put("op","pushdata3");
                            params.put("uid",userid);
                            params.put("prgver",MainActivity.PROGRAM_VERSION);
                            params.put("ttt",""+System.currentTimeMillis());
                            params.put("globalid",MainActivity.gpd.getGlobalid());
                            params.put("fisno",MainActivity.gpd.getFisno());
                            params.put("firmaid",MainActivity.gpd.getFirma());
                            params.put("bolge",MainActivity.gpd.getBolge());
                            params.put("calisma",MainActivity.gpd.getCalismaalani());
                            params.put("yetkili",MainActivity.gpd.getYetkili());
                            params.put("ekiplideri",MainActivity.gpd.getEkiplideri());
                            params.put("calismavar",""+MainActivity.gpd.getCalismavar());
                            params.put("ibt",MainActivity.gpd.getTarih());
                            params.put("ibt2",MainActivity.gpd.getTarihFormatli());
                            params.put("urun",MainActivity.gpd.getUrun());

                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    this.getResources().getString(R.string.serviceUrl), new JSONObject(params),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            //Success Callback
                                            Log.w("volleyGunlukPuantaj",response.toString());
                                            try {
                                                JSONArray values = response.getJSONArray("result");
                                                if(values.length()>0){
                                                    JSONObject value = values.getJSONObject(0);
                                                    if (value!=null && value.getBoolean("stat") && value.get("globalid").equals(MainActivity.gpd.getGlobalid())) {
                                                        Intent intent = new Intent(thisActivity, GunlukPuantaj2.class);
                                                        intent.putExtra("EkipLideri", ibasEkipLideriAdi);
                                                        startActivityForResult(intent, REQUEST_GP2);
                                                    }
                                                }
                                            }catch (Exception ex){
                                                Log.w("Error",ex.getMessage());
                                                new ShowToast(MainActivity.mactivity, "Sunucuya bağlantıda hata oluştu.");
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {
                                            String message = null;
                                            if (volleyError instanceof NetworkError || volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                                                message = MainActivity.mactivity.getString(R.string.msgInternetNoConnection);
                                            } else if (volleyError instanceof ServerError) {
                                                message = "The server could not be found. Please try again after some time!!";
                                            } else if (volleyError instanceof ParseError) {
                                                message = "Sunucuya bağlantıda hata oluştu.";
                                            }
                                            new ShowToast(MainActivity.mactivity, message);
                                            MainActivity.gpd = null;
                                        }
                                    });


                            // Add the request to the RequestQueue.
                            MainActivity.serverQueue.add(jsonObjReq);


                        }else{
                            new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                        }
                    }catch (Exception ex){
                        new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                    }
                    //----
                }
            }else if (MainActivity.gpd.getKayitdurumu().equals("guncelleme")) {
                req=new ArrayList<String[]>();
                req.add(new String[]{"op","calisma_getir"});
                req.add(new String[]{"firmaid", ibasFirma});
                req.add(new String[]{"bolge", ibasBolge});
                req.add(new String[]{"calisma", ibasCalisma});
                req.add(new String[]{"yetkili", ibasYetkili});
                req.add(new String[]{"ekiplideri", ibasEkipLideri});
                req.add(new String[]{"tarih", ibasTarih.getText().toString()});
                req.add(new String[]{"durum", "guncelle"});
                req.add(new String[]{"globalid", calisma.get(0).get("id")});

                resp=new ArrayList<String[]>();
                resp.add(new String[]{"stat","stat"});

                List<HashMap<String, String>> calismaGunceleme = new ServerData(this).getDataFromServer(userid,"CalismaGetir3", req, resp);
                if(calismaGunceleme!=null && calismaGunceleme.size()>0 && calismaGunceleme.get(0).get("stat").equals("true")) {
                    MainActivity.gpd.setGlobalid(calisma.get(0).get("id"));
                    MainActivity.gpd.setFisno(calisma.get(0).get("fisno"));
                    MainActivity.gpd.setEkliDoc(calisma.get(0).get("eklidoc1"), calisma.get(0).get("eklidoc2"), calisma.get(0).get("eklidoc3"), calisma.get(0).get("aciklama"));
                    try {
                        JSONObject jsonObj = new JSONObject("{\"gorev\":" + calisma.get(0).get("gorevjson") + "}");
                        JSONArray values = jsonObj.getJSONArray("gorev");
                        for (int i = 0; i < values.length(); i++) {
                            JSONObject c = values.getJSONObject(i);
                            MainActivity.gpd.addPersonel(c.getString("gorev"), c.getString("sicilno"), c.getString("ad") + " " + c.getString("soyad"), c.getString("cinsiyet"), c.getString("mesai"), c.getString("kartokutma"), c.getString("urunid"), c.getString("soyad"), c.getString("tc"), c.getString("kartno"));
                        }

                        Log.w("gp1servis", "" + calisma.get(0).get("servisvar"));
                        if (Integer.parseInt(calisma.get(0).get("servisvar")) > 0) {
                            JSONObject servis = new JSONObject(calisma.get(0).get("servisjson"));
                            for (int i = 0; i < 20; ++i) {
                                if (servis.getString("servis"+(i+1)+"sayi") != null && !servis.getString("servis"+(i+1)+"sayi").equals("0")) {
                                    MainActivity.gpd.addServis("" + i, servis.getString("servis"+(i+1)+"sayi"));
                                }
                            }
                            /*JSONArray keys = servis.names();
                            for (int i = 0; i < keys.length(); ++i) {
                                if (servis.getString(keys.getString(i)) != null && !servis.getString(keys.getString(i)).equals("0")) {
                                    MainActivity.gpd.addServis("" + i, servis.getString(keys.getString(i)));
                                }
                            }*/
                        } else if (Integer.parseInt(calisma.get(0).get("servisvar")) == -1) {
                            MainActivity.gpd.addServis("KisiBasi", "KisiBasi");
                        }
                        Intent intent = new Intent(this, GunlukPuantaj2.class);
                        intent.putExtra("EkipLideri", ibasEkipLideri);
                        startActivityForResult(intent, REQUEST_GP2);
                    } catch (Exception ex) {
                        Log.w("GPD Güncelleme", ex.getMessage());
                    }
                }else{
                    new ShowToast(this, "Puantaj bilgileri sisteme aktarılmış olduğundan güncellenememektedir.");
                }
            }

        }catch (Exception ex){
            Log.w("nextToPuantaj2Screen", ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_GP2 && resultCode==RESULT_OK){
            if(data.hasExtra("exit")){
                if (data.hasExtra("status") && data.getStringExtra("status").equals("discard")){
                    if (MainActivity.gpd.getKayitdurumu().equals("guncelleme")) {
                        List<String[]> req = new ArrayList<String[]>();
                        req.add(new String[]{"op", "calisma_getir"});
                        req.add(new String[]{"firmaid", ibasFirma});
                        req.add(new String[]{"bolge", ibasBolge});
                        req.add(new String[]{"calisma", ibasCalisma});
                        req.add(new String[]{"yetkili", ibasYetkili});
                        req.add(new String[]{"ekiplideri", ibasEkipLideri});
                        req.add(new String[]{"tarih", ibasTarih.getText().toString()});
                        req.add(new String[]{"durum", "iptal"});
                        req.add(new String[]{"globalid", MainActivity.gpd.getGlobalid()});

                        List<String[]> resp = new ArrayList<String[]>();
                        resp.add(new String[]{"stat", "stat"});

                        List<HashMap<String, String>> calismaGuncellemeIptal = new ServerData(this).getDataFromServer(userid, "CalismaGetir4", req, resp);
                        if (calismaGuncellemeIptal != null && calismaGuncellemeIptal.size() > 0 && calismaGuncellemeIptal.get(0).get("stat").equals("true")) {
                            Log.w("calismaGuncellemeIptal","success");
                        }
                    }
                }
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void PushDataAsyncFinish(boolean stat) {

    }

    @Override
    public void SendDataAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error) {
        if (stat){
            if (type.equals("FirmaOnline")){
                List<KeyValueP> res=new ArrayList<KeyValueP>();

                for (int i=0; i<data.size(); i++){
                    KeyValueP tmp = new KeyValueP();
                    tmp.ID=data.get(i).get("id");
                    tmp.name=data.get(i).get("firma");
                    res.add(tmp);
                    Log.w("FirmaOnline Finish",data.get(i).get("firma"));
                }

                daIBasFirma = new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, res);
                daIBasFirma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnIBasFirma.setAdapter(daIBasFirma);
                spnIBasFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ibasFirma = daIBasFirma.getItem(adapterView.getSelectedItemPosition()).ID;
                        Log.w("iFirma: ", ibasFirma);
                        //fill bölge
                        FirmaBolgeOnline firmabolge=new FirmaBolgeOnline(thisActivity);
                        firmabolge.context=ParentCtxt;
                        firmabolge.uid=userid;
                        firmabolge.firmaid=ibasFirma;
                        firmabolge.reqtype="Bolge";
                        firmabolge.reqtitle="Bölgeler";
                        firmabolge.reqop="firma_bolge";
                        firmabolge.resp1=new String[]{"bolge","bolge"};
                        firmabolge.execute();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });//firma
            }else if (type.equals("BolgeOnline")) {
                List<String> res = new ArrayList<String>();
                res.add("Seçiniz");
                for (int i = 0; i < data.size(); i++) {
                    res.add(data.get(i).get("bolge"));
                }

                daIBasBolge = new ArrayAdapter<String>(ParentCtxt, android.R.layout.simple_spinner_item, res);
                daIBasBolge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnIBasBolge.setAdapter(daIBasBolge);
                spnIBasBolge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spnIBasCalisma.setAdapter(null);
                        spnIBasEkiplideri.setAdapter(null);
                        spnIBasUrun.setAdapter(null);
                        spnIBasYetkili.setAdapter(null);
                        if (adapterView.getSelectedItem().toString() != "") {
                            ibasBolge=adapterView.getSelectedItem().toString();
                            //fill calisma
                            FirmaBolgeOnline firmabolge=new FirmaBolgeOnline(thisActivity);
                            firmabolge.context=ParentCtxt;
                            firmabolge.uid=userid;
                            firmabolge.firmaid=ibasFirma;
                            firmabolge.reqtype="Calisma";
                            firmabolge.reqtitle="Çalışma Alanı";
                            firmabolge.reqop="bolge_calisma";
                            firmabolge.param1=new String[]{"bolge",adapterView.getSelectedItem().toString()};
                            firmabolge.resp1=new String[]{"bolge","bolge"};
                            firmabolge.execute();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });//bölge
            }else if (type.equals("CalismaOnline")) {
                List<String> res = new ArrayList<String>();
                res.add("Seçiniz");
                for (int i = 0; i < data.size(); i++) {
                    res.add(data.get(i).get("bolge"));
                }

                daIBasCalisma=new ArrayAdapter<String>(ParentCtxt, android.R.layout.simple_spinner_item,res);
                daIBasCalisma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnIBasCalisma.setAdapter(daIBasCalisma);
                spnIBasCalisma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spnIBasEkiplideri.setAdapter(null);
                        spnIBasUrun.setAdapter(null);
                        spnIBasYetkili.setAdapter(null);
                        if (adapterView.getSelectedItem().toString()!="" && adapterView.getSelectedItem().toString()!="Seçiniz") {
                            ibasCalisma=adapterView.getSelectedItem().toString();
                            ibasBtnCalismaYok.setEnabled(false);
                            ibasBtnSonraki.setEnabled(false);

                            //fill yetkili
                            FirmaBolgeOnline firmabolge=new FirmaBolgeOnline(thisActivity);
                            firmabolge.context=ParentCtxt;
                            firmabolge.uid=userid;
                            firmabolge.firmaid=ibasFirma;
                            firmabolge.reqtype="Yetkili";
                            firmabolge.reqtitle="Yetkili";
                            firmabolge.reqop="calisma_yetkili";
                            firmabolge.param1=new String[]{"bolge",ibasBolge};
                            firmabolge.param2=new String[]{"calisma",ibasCalisma};
                            firmabolge.resp1=new String[]{"id","id"};
                            firmabolge.resp2=new String[]{"name","name"};
                            firmabolge.execute();

                            //fill ekiplideri
                            FirmaBolgeOnline ekiplideri=new FirmaBolgeOnline(thisActivity);
                            ekiplideri.context=ParentCtxt;
                            ekiplideri.uid=userid;
                            ekiplideri.firmaid=ibasFirma;
                            ekiplideri.reqtype="EkipLideri";
                            ekiplideri.reqtitle="Ekip Lideri";
                            ekiplideri.reqop="ekiplideri";
                            ekiplideri.param1=new String[]{"calisma",ibasCalisma};
                            ekiplideri.resp1=new String[]{"id","id"};
                            ekiplideri.resp2=new String[]{"name","name"};
                            ekiplideri.resp3=new String[]{"bolgekisiti","bolgekisiti"};
                            ekiplideri.execute();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });//Calisma
            }else if (type.equals("YetkiliOnline")) {
                List<KeyValueP> res = new ArrayList<KeyValueP>();
                //res.add("Seçiniz");
                for (int i = 0; i < data.size(); i++) {
                    res.add(new KeyValueP(data.get(i).get("id"),data.get(i).get("name")));
                }

                daIBasYetkili=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, res);
                daIBasYetkili.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnIBasYetkili.setAdapter(daIBasYetkili);
                spnIBasYetkili.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spnIBasUrun.setAdapter(null);
                        ibasYetkili=daIBasYetkili.getItem(adapterView.getSelectedItemPosition()).ID;

                        //fill ürün
                        FirmaBolgeOnline firmabolge=new FirmaBolgeOnline(thisActivity);
                        firmabolge.context=ParentCtxt;
                        firmabolge.uid=userid;
                        firmabolge.firmaid=ibasFirma;
                        firmabolge.reqtype="Urun";
                        firmabolge.reqtitle="Ürün";
                        firmabolge.reqop="yetkili_urun";
                        firmabolge.param1=new String[]{"calisma",ibasCalisma};
                        firmabolge.param2=new String[]{"yetkili",ibasYetkili};
                        firmabolge.resp1=new String[]{"id","id"};
                        firmabolge.resp2=new String[]{"name","name"};
                        firmabolge.execute();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });//yetkili
            }else if (type.equals("UrunOnline")) {
                List<KeyValueP> res = new ArrayList<KeyValueP>();
                //res.add("Seçiniz");
                for (int i = 0; i < data.size(); i++) {
                    res.add(new KeyValueP(data.get(i).get("id"),data.get(i).get("name")));
                }

                //fill ürün
                daIBasUrun=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, res);
                daIBasUrun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnIBasUrun.setAdapter(daIBasUrun);
                spnIBasUrun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ibasUrun = daIBasUrun.getItem(adapterView.getSelectedItemPosition()).ID;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }else if (type.equals("EkipLideriOnline")) {
                List<KeyValueP> res = new ArrayList<KeyValueP>();
                //res.add("Seçiniz");
                for (int i = 0; i < data.size(); i++) {
                    res.add(new KeyValueP(data.get(i).get("id"),data.get(i).get("name"),(data.get(i).get("bolgekisiti").equals("0")?0:1)));
                }

                //fill ekiplideri
                daIBasEkiplideri=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, res);
                daIBasEkiplideri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnIBasEkiplideri.setAdapter(daIBasEkiplideri);
                spnIBasEkiplideri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ibasEkipLideri=daIBasEkiplideri.getItem(adapterView.getSelectedItemPosition()).ID;
                        ibasEkipLideriAdi=daIBasEkiplideri.getItem(adapterView.getSelectedItemPosition()).name;
                        ekipLideriBolgeKisiti=daIBasEkiplideri.getItem(adapterView.getSelectedItemPosition()).param;

                        DataFromService gorevGetir=new DataFromService(thisActivity);
                        gorevGetir.context=ParentCtxt;
                        gorevGetir.uid=userid;
                        gorevGetir.reqtype="CalismaGetir";
                        gorevGetir.title="Çalışma";
                        gorevGetir.reqparam=new ArrayList<String[]>();
                        gorevGetir.reqparam.add(new String[]{"op","calisma_getir"});
                        gorevGetir.reqparam.add(new String[]{"firmaid", ibasFirma});
                        gorevGetir.reqparam.add(new String[]{"bolge", ibasBolge});
                        gorevGetir.reqparam.add(new String[]{"calisma", ibasCalisma});
                        gorevGetir.reqparam.add(new String[]{"yetkili", ibasYetkili});
                        gorevGetir.reqparam.add(new String[]{"ekiplideri", ibasEkipLideri});
                        gorevGetir.reqparam.add(new String[]{"tarih", ibasTarih.getText().toString()});
                        gorevGetir.resp=new ArrayList<String[]>();
                        gorevGetir.resp.add(new String[]{"id","id"});
                        gorevGetir.execute();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });//ekiplideri
            }
        }
    }

    @Override
    public void UserAsyncFinish(boolean stat, String userid, String puantajyetki, String error){

    }

    @Override
    public void PersonelAsyncFinish(boolean stat) {

    }

    @Override
    public void SifremiUnuttumAsyncFinish(String stat,String uid, String p) {

    }

    @Override
    public void PersonelAsyncFinish(boolean stat, HashMap<String, String> data, String error){

    }

    @Override
    public void PipeAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error) {
        if (type.equals("CalismaGetirOnline")) {
            if (data != null && data.size() > 0) {
                ibasBtnSonraki.setEnabled(true);
            } else {
                ibasBtnCalismaYok.setEnabled(true);
                ibasBtnSonraki.setEnabled(true);
            }
        }else if (type.equals("PuantajGonderOnline")) {
            if(data!=null && data.size()>0 && data.get(0).get("stat").equals("true")) {
                new ShowToast(ParentCtxt, "Günlük Puantaj Başarıyla Oluşturulmuştur.");
            }else{
                new ShowToast(ParentCtxt, "Günlük Puantaj Kaydedilirken Hata Oluştu.");
            }

            ibasBtnSonraki.setEnabled(false);
            finish();
        }
    }
}


