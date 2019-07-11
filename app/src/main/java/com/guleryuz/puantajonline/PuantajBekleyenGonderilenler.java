package com.guleryuz.puantajonline;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by mehmet_erenoglu on 03.04.2017.
 */

public class PuantajBekleyenGonderilenler extends AppCompatActivity implements View.OnClickListener {
    private TextView wsTitle;
    private LinearLayout wsContent;
    private static Context ParentCtxt;
    private Database db;
    private String title, which;
    static final int REQUEST_GP2=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puantaj_bekleyengonderilen);
        ParentCtxt = this;

        title="Bekleyenler";

        if(getIntent().hasExtra("which")){
            title=getIntent().getStringExtra("which");
        }
        which=(title.equals("Bekleyenler")?" IN (-1, 0)":"=1");//bekleyenler

        wsTitle=(TextView)findViewById(R.id.wsTitle);
        wsTitle.setText(title);
        wsContent=(LinearLayout)findViewById(R.id.wsContent);

        loadPuantajPersonel();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadPuantajPersonel();
    }

    private  void loadPuantajPersonel(){
        wsContent.removeAllViews();

        TextView wsPuantajTitle=new TextView(this);
        wsPuantajTitle.setText("-> Puantaj");
        wsPuantajTitle.setTextColor(getResources().getColor(R.color.black));
        wsPuantajTitle.setBackground(getResources().getDrawable(R.drawable.border_lightgray));
        wsPuantajTitle.setTextSize(18);
        wsPuantajTitle.setTypeface(Typeface.DEFAULT_BOLD);
        wsPuantajTitle.setPadding(10,10,10,0);
        wsPuantajTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        wsContent.addView(wsPuantajTitle);

        loadPuantaj();

        TextView wsBlank=new TextView(this);
        wsBlank.setText("   ");
        wsContent.addView(wsBlank);

        wsPuantajTitle=new TextView(this);
        wsPuantajTitle.setText("-> Personel");
        wsPuantajTitle.setTextColor(getResources().getColor(R.color.black));
        wsPuantajTitle.setBackground(getResources().getDrawable(R.drawable.border_lightgray));
        wsPuantajTitle.setTextSize(18);
        wsPuantajTitle.setTypeface(Typeface.DEFAULT_BOLD);
        wsPuantajTitle.setPadding(10,10,10,0);
        wsPuantajTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        wsContent.addView(wsPuantajTitle);
        loadPersonel();
    }

    private void loadPuantaj(){
        LinearLayout layouts;
        try {
            db = new Database(getApplicationContext());
            ArrayList<HashMap<String, String>> res=db.bekleyenGonderilenGetir(MainActivity.userid, which);
            Log.w("pbg", ""+res.size());
            if(res.size()>0) {
                for (int i = 0; i < res.size(); i++) {
                    layouts = (LinearLayout) ((Activity) ParentCtxt).getLayoutInflater().inflate(R.layout.puantaj_bekleyengonderilen_liste, null);
                    TextView wsTarih = (TextView) layouts.findViewById(R.id.wsTarih);
                    wsTarih.setText("" + res.get(i).get("tarih"));

                    final HashMap<String, String> calisma=db.getOneRow(new String[]{"OID","FIRMA", "BOLGE", "CALISMA", "EKIP_LIDERI", "URUN", "YETKILI","ISE_BASLAMA_TARIHI","SERVISVAR","ACIKLAMA","EKLIDOC1","EKLIDOC2","EKLIDOC3","FISNO","CALISMAVAR","GLOBALID, EL_BOLGEKISITI"},"tarim_istakip_calisma", "GLOBALID='"+ res.get(i).get("globalid")+"' and globalid is not null AND USER_ID="+MainActivity.userid);
                    TextView wsFisno = (TextView) layouts.findViewById(R.id.wsFisno);
                    String fisno=res.get(i).get("fisno");
                    if(fisno.length()==36){
                        fisno=fisno.substring(0,fisno.indexOf('-'));
                    }
                    wsFisno.setText("" + fisno);// res.get(i).get("fisno"));
                    if(title.equals("Bekleyenler")) {
                        if (calisma.get("CALISMAVAR").equals("1")) {
                            wsFisno.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                                    builder.setTitle("Uyarı");
                                    builder.setMessage("Düzenlemek istediğinize emin misiniz?");
                                    builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            String[] ntarih = calisma.get("ISE_BASLAMA_TARIHI").split("-");
                                            Log.w("tarih", ntarih[2] + " / " + ntarih[1] + " / " + ntarih[0]);
                                            MainActivity.gpd = new GunlukPuantajData(MainActivity.userid, calisma.get("BOLGE"), calisma.get("CALISMA"), calisma.get("FIRMA"), calisma.get("YETKILI"), calisma.get("URUN"), calisma.get("EKIP_LIDERI"), (calisma.get("EL_BOLGEKISITI").equals("0")?0:1), ntarih[2] + " / " + ntarih[1] + " / " + ntarih[0], calisma.get("FISNO"), "guncelleme");
                                            MainActivity.gpd.setGlobalid(calisma.get("GLOBALID"));
                                            MainActivity.gpd.setEkliDoc(calisma.get("EKLIDOC1"), calisma.get("EKLIDOC2"), calisma.get("EKLIDOC3"), calisma.get("ACIKLAMA"));

                                            ArrayList<HashMap<String, String>> res2 = db.getMultiResult(new String[]{"OID", "SICILNO", "TICID", "URUNID", "GOREV", "MESAI", "KARTLAEKLENDI"}, "tarim_istakip_calisma_personel", "AKTARILDI<1 AND AKTARILDI_ONAY=0 AND TICID='" + calisma.get("OID") + "' AND FISNO='"+calisma.get("FISNO")+"'");
                                            for (int i = 0; i < res2.size(); i++) {
                                                HashMap<String, String> pers = db.getOneRow(new String[]{"AD", "SOYAD", "CINSIYET"}, "tarim_istakip_personel", "ID='" + res2.get(i).get("SICILNO") + "'");
                                                MainActivity.gpd.addPersonel(res2.get(i).get("GOREV"), res2.get(i).get("SICILNO"), pers.get("AD") + " " + pers.get("SOYAD"), pers.get("CINSIYET"), res2.get(i).get("MESAI"), res2.get(i).get("KARTLAEKLENDI"), res2.get(i).get("URUNID"), pers.get("SOYAD"), pers.get("TC"), pers.get("KARTNO"));
                                            }
                                            Log.w("gp1servis", "" + calisma.get("SERVISVAR"));
                                            if (Integer.parseInt(calisma.get("SERVISVAR")) > 0) {
                                                //HashMap<String, String> res3 = db.getOneRow(new String[]{"OID", "TICID", "SERVIS1", "SERVIS2", "SERVIS3", "SERVIS4", "SERVIS5", "SERVIS6", "SERVIS7", "SERVIS8", "SERVIS9", "SERVIS10", "SERVIS11", "SERVIS12", "SERVIS13", "SERVIS14", "SERVIS15", "SERVIS16", "SERVIS17", "SERVIS18", "SERVIS19", "SERVIS20", "SERVIS1SAYI", "SERVIS2SAYI", "SERVIS3SAYI", "SERVIS4SAYI", "SERVIS5SAYI", "SERVIS6SAYI", "SERVIS7SAYI", "SERVIS8SAYI", "SERVIS9SAYI", "SERVIS10SAYI", "SERVIS11SAYI", "SERVIS12SAYI", "SERVIS13SAYI", "SERVIS14SAYI", "SERVIS15SAYI", "SERVIS16SAYI", "SERVIS17SAYI", "SERVIS18SAYI", "SERVIS19SAYI", "SERVIS20SAYI"}, "tarim_istakip_calisma_servis", "AKTARILDI=0 AND AKTARILDI_ONAY=0 AND TICID='" + calisma.get("OID") + "'");
                                                String[] servis = db.servisGetir(MainActivity.gpd.getCalismaalani(), MainActivity.gpd.getFirma(), MainActivity.gpd.getEkiplideri());
                                                ArrayList<KeyValueP> puantajser = db.puantajServisGetir(calisma.get("OID"), MainActivity.userid, calisma.get("ISE_BASLAMA_TARIHI"), calisma.get("FISNO"));

                                                for (int j = 0; j < servis.length-1; j++) {
                                                    if (servis[j] != null && !servis[j].equals("0")) {
                                                        if (j < puantajser.size() && servis[j].equals(puantajser.get(j).ID)) {
                                                            //tmptxt2.setText("Servis " + servis[j] + " - " + (puantajser.get(j).name.equals("") ? "-" : puantajser.get(j).name) + "");
                                                            MainActivity.gpd.addServis("" + j, puantajser.get(j).name);
                                                            Log.w("gp1servis", "" + j + " " + servis[j] + " " + puantajser.get(j).name);
                                                        }
                                                    }
                                                }
                                            }else if(Integer.parseInt(calisma.get("SERVISVAR"))==-1){
                                                MainActivity.gpd.addServis("KisiBasi","KisiBasi");
                                            }

                                            Intent intent = new Intent(ParentCtxt, GunlukPuantaj2.class);
                                            startActivityForResult(intent, REQUEST_GP2);

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
                            });
                        }else{
                            TextView wsFisnoTitle = (TextView) layouts.findViewById(R.id.wsFisnoTitle);
                            wsFisnoTitle.setText("");
                            wsFisno.setText("Çalışma Yok");
                            wsFisno.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    /*AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                                    builder.setTitle("Uyarı");
                                    builder.setMessage("Düzenlemek istediğinize emin misiniz?");
                                    builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            String[] ntarih = calisma.get("ISE_BASLAMA_TARIHI").split("-");
                                            Log.w("tarih", ntarih[2] + " / " + ntarih[1] + " / " + ntarih[0]);
                                            MainActivity.gpd = new GunlukPuantajData(MainActivity.userid, calisma.get("BOLGE"), calisma.get("CALISMA"), calisma.get("FIRMA"), calisma.get("YETKILI"), calisma.get("URUN"), calisma.get("EKIP_LIDERI"), ntarih[2] + " / " + ntarih[1] + " / " + ntarih[0], calisma.get("FISNO"), "guncelleme");

                                            Intent intent = new Intent(ParentCtxt, GunlukPuantaj.class);
                                            intent.putExtra("calismayok","1");
                                            startActivityForResult(intent, REQUEST_GP2);

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
                                    alert.show();*/
                                }
                            });
                        }
                    }

                    TextView wsToplam = (TextView) layouts.findViewById(R.id.wsToplam);
                    wsToplam.setText("" + res.get(i).get("toplam"));

                    if(title.equals("Bekleyenler")) {
                        ImageView wsImgSTF = (ImageView) layouts.findViewById(R.id.wsImgSTF);
                        if (res.get(i).get("ek1") == null && res.get(i).get("ek2") == null && res.get(i).get("ek3") == null) {
                            wsImgSTF.setImageResource(R.drawable.error);
                            if(calisma.get("CALISMAVAR").equals("1")) {
                                wsImgSTF.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                                        builder.setTitle("Uyarı");
                                        builder.setMessage("Düzenlemek istediğinize emin misiniz?");
                                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                String[] ntarih = calisma.get("ISE_BASLAMA_TARIHI").split("-");
                                                Log.w("tarih", ntarih[2] + " / " + ntarih[1] + " / " + ntarih[0]);
                                                MainActivity.gpd = new GunlukPuantajData(MainActivity.userid, calisma.get("BOLGE"), calisma.get("CALISMA"), calisma.get("FIRMA"), calisma.get("YETKILI"), calisma.get("URUN"), calisma.get("EKIP_LIDERI"),  (calisma.get("EL_BOLGEKISITI").equals("0")?0:1), ntarih[2] + " / " + ntarih[1] + " / " + ntarih[0], calisma.get("FISNO"), "guncelleme");
                                                MainActivity.gpd.setEkliDoc(calisma.get("EKLIDOC1"), calisma.get("EKLIDOC2"), calisma.get("EKLIDOC3"), calisma.get("ACIKLAMA"));

                                                ArrayList<HashMap<String, String>> res2 = db.getMultiResult(new String[]{"OID", "SICILNO", "TICID", "URUNID", "GOREV", "MESAI", "KARTLAEKLENDI"}, "tarim_istakip_calisma_personel", "AKTARILDI<1 AND AKTARILDI_ONAY=0 AND TICID='" + calisma.get("OID") + "' AND FISNO='"+ calisma.get("FISNO")+"'");
                                                Log.w("gpdUpdate",""+res2.size());
                                                for (int i = 0; i < res2.size(); i++) {
                                                    Log.w("gpdUpdate",res2.get(i).get("SICILNO")+" - "+res2.get(i).get("URUNID"));
                                                    HashMap<String, String> pers = db.getOneRow(new String[]{"AD", "SOYAD", "CINSIYET"}, "tarim_istakip_personel", "ID='" + res2.get(i).get("SICILNO") + "'");
                                                    MainActivity.gpd.addPersonel(res2.get(i).get("GOREV"), res2.get(i).get("SICILNO"), pers.get("AD") + " " + pers.get("SOYAD"), pers.get("CINSIYET"), res2.get(i).get("MESAI"), res2.get(i).get("KARTLAEKLENDI"), res2.get(i).get("URUNID"), res2.get(i).get("SOYADI"), res2.get(i).get("TC"), res2.get(i).get("KARTNO"));
                                                }
                                                Log.w("gp1servis", "" + calisma.get("SERVISVAR"));
                                                if (Integer.parseInt(calisma.get("SERVISVAR")) > 0) {
                                                    //HashMap<String, String> res3 = db.getOneRow(new String[]{"OID", "TICID", "SERVIS1", "SERVIS2", "SERVIS3", "SERVIS4", "SERVIS5", "SERVIS6", "SERVIS7", "SERVIS8", "SERVIS9", "SERVIS10", "SERVIS11", "SERVIS12", "SERVIS13", "SERVIS14", "SERVIS15", "SERVIS16", "SERVIS17", "SERVIS18", "SERVIS19", "SERVIS20", "SERVIS1SAYI", "SERVIS2SAYI", "SERVIS3SAYI", "SERVIS4SAYI", "SERVIS5SAYI", "SERVIS6SAYI", "SERVIS7SAYI", "SERVIS8SAYI", "SERVIS9SAYI", "SERVIS10SAYI", "SERVIS11SAYI", "SERVIS12SAYI", "SERVIS13SAYI", "SERVIS14SAYI", "SERVIS15SAYI", "SERVIS16SAYI", "SERVIS17SAYI", "SERVIS18SAYI", "SERVIS19SAYI", "SERVIS20SAYI"}, "tarim_istakip_calisma_servis", "AKTARILDI=0 AND AKTARILDI_ONAY=0 AND TICID='" + calisma.get("OID") + "'");
                                                    String[] servis = db.servisGetir(MainActivity.gpd.getCalismaalani(), MainActivity.gpd.getFirma(), MainActivity.gpd.getEkiplideri());
                                                    ArrayList<KeyValueP> puantajser = db.puantajServisGetir(calisma.get("OID"), MainActivity.userid, calisma.get("ISE_BASLAMA_TARIHI"), calisma.get("FISNO"));

                                                    for (int j = 0; j < servis.length-1; j++) {
                                                        if (servis[j] != null && !servis[j].equals("0")) {
                                                            if (j < puantajser.size() && servis[j].equals(puantajser.get(j).ID)) {
                                                                //tmptxt2.setText("Servis " + servis[j] + " - " + (puantajser.get(j).name.equals("") ? "-" : puantajser.get(j).name) + "");
                                                                MainActivity.gpd.addServis("" + j, puantajser.get(j).name);
                                                                Log.w("gp1servis", "" + j + " " + servis[j] + " " + puantajser.get(j).name);
                                                            }
                                                        }
                                                    }
                                                }else if(Integer.parseInt(calisma.get("SERVISVAR"))==-1){
                                                    MainActivity.gpd.addServis("KisiBasi","KisiBasi");
                                                }

                                                Intent intent = new Intent(ParentCtxt, GunlukPuantaj4.class);
                                                startActivityForResult(intent, REQUEST_GP2);

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
                                });
                            }
                        }
                    }

                    TextView wsEkiplideri = (TextView) layouts.findViewById(R.id.wsEkiplideri);
                    wsEkiplideri.setText("" + res.get(i).get("ekip_lideri"));
                    wsContent.addView(layouts);
                }
            }else{
                TextView aciklama=new TextView(ParentCtxt);
                aciklama.setPadding(0,15,0,0);
                aciklama.setHeight(80);
                aciklama.setVisibility(View.VISIBLE);
                aciklama.setText("Kayıt bulunamamaktadır.");
                aciklama.setTextColor(Color.RED);
                aciklama.setTextSize(15);
                aciklama.setTypeface(null, 1);
                aciklama.setGravity(Gravity.CENTER);
                wsContent.addView(aciklama);
            }
        }catch (Exception ex){

        }
    }

    private void loadPersonel(){
        LinearLayout layouts;
        try {
            db = new Database(getApplicationContext());
            ArrayList<HashMap<String, String>> res=db.bekleyenGonderilenPersonelGetir(MainActivity.userid, which);
            if(res.size()>0) {
                for (int i = 0; i < res.size(); i++) {
                    layouts = (LinearLayout) ((Activity) ParentCtxt).getLayoutInflater().inflate(R.layout.puantaj_bekleyengonderilen_liste_personel, null);

                    TextView wsPETC = (TextView) layouts.findViewById(R.id.wsPETC);
                    wsPETC.setText("" + res.get(i).get("tc"));

                    TextView wsPEKartno = (TextView) layouts.findViewById(R.id.wsPEKartno);
                    wsPEKartno.setText("" + res.get(i).get("kartno"));

                    TextView wsPESicilno = (TextView) layouts.findViewById(R.id.wsPESicilno);
                    wsPESicilno.setText("" + res.get(i).get("id"));

                    TextView wsPEAd = (TextView) layouts.findViewById(R.id.wsPEAd);
                    wsPEAd.setText("" + res.get(i).get("ad"));


                    TextView wsPEIslem = (TextView) layouts.findViewById(R.id.wsPEIslem);
                    wsPEIslem.setText("" + res.get(i).get("islem"));

                    wsContent.addView(layouts);
                }
            }else{
                TextView aciklama=new TextView(ParentCtxt);
                aciklama.setPadding(0,15,0,0);
                aciklama.setHeight(80);
                aciklama.setVisibility(View.VISIBLE);
                aciklama.setText("Kayıt bulunamamaktadır.");
                aciklama.setTextColor(Color.RED);
                aciklama.setTextSize(15);
                aciklama.setTypeface(null, 1);
                aciklama.setGravity(Gravity.CENTER);
                wsContent.addView(aciklama);
            }
        }catch (Exception ex){

        }
    }
}
