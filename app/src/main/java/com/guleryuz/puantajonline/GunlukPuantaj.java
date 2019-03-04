package guleryuz.puantajonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import barcodescanner.app.com.barcodescanner.R;

/**
 * Created by mehmet_erenoglu on 27.02.2017.
 */

public class GunlukPuantaj extends AppCompatActivity implements View.OnClickListener {
    private Spinner spnIBasBolge, spnIBasCalisma, spnIBasFirma, spnIBasYetkili, spnIBasEkiplideri, spnIBasUrun;
    private RelativeLayout layoutIBas;
    private ArrayAdapter<String> daIBasBolge, daIBasCalisma;
    private ArrayAdapter<KeyValueP> daIBasFirma, daIBasYetkili, daIBasEkiplideri, daIBasUrun;//, daIBasGorev
    private static String ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasEkipLideri, ibasUrun;
    private static TextView ibasTarih;
    private Button ibasBtnIptal, ibasBtnSonraki, ibasBtnCalismaYok;//btnIBasBarkodOku, btnIBasOnay, btnIBasBarkodYeni;
    static final int REQUEST_GP2=1;

    private String ibasFisno;
    private Database db;
    private static Context ParentCtxt;
    private static String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ise_baslama);
        ParentCtxt=this;

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
        db=new Database(getApplicationContext());

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

        daIBasFirma = new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.firmaGetir(userid,""));
        daIBasFirma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIBasFirma.setAdapter(daIBasFirma);
        spnIBasFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ibasFirma=daIBasFirma.getItem(adapterView.getSelectedItemPosition()).ID;
                Log.w("iFirma: ",ibasFirma);
                //fill yetkili
                daIBasBolge=new ArrayAdapter<String>(ParentCtxt, android.R.layout.simple_spinner_item, db.firmaBolgeCAGetir("Bolge","", ibasFirma,userid,""));
                daIBasBolge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnIBasBolge.setAdapter(daIBasBolge);
                spnIBasBolge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (adapterView.getSelectedItem().toString()!=""){
                            daIBasCalisma=new ArrayAdapter<String>(ParentCtxt, android.R.layout.simple_spinner_item,db.firmaBolgeCAGetir("Çalışma Alanı",adapterView.getSelectedItem().toString(), ibasFirma, userid, ""));
                            daIBasCalisma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnIBasCalisma.setAdapter(daIBasCalisma);
                            ibasBolge=adapterView.getSelectedItem().toString();
                            spnIBasCalisma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (adapterView.getSelectedItem().toString()!="" && adapterView.getSelectedItem().toString()!="Seçiniz") {
                                        ibasCalisma=adapterView.getSelectedItem().toString();
                                        Log.w("iCalisma",ibasCalisma);
                                        ibasBtnCalismaYok.setEnabled(false);
                                        ibasBtnSonraki.setEnabled(false);
                                        daIBasYetkili=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.yetkiliGetir(ibasBolge, ibasCalisma, userid));
                                        daIBasYetkili.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spnIBasYetkili.setAdapter(daIBasYetkili);
                                        spnIBasYetkili.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                ibasYetkili=daIBasYetkili.getItem(adapterView.getSelectedItemPosition()).ID;

                                                //fill ürün
                                                daIBasUrun=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.urunGetir(ibasCalisma, ibasFirma, ibasYetkili));
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
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });//yetkili

                                        //fill ekiplideri
                                        daIBasEkiplideri=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.ekiplideriGetir(ibasCalisma, ibasFirma));
                                        daIBasEkiplideri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spnIBasEkiplideri.setAdapter(daIBasEkiplideri);
                                        spnIBasEkiplideri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                ibasEkipLideri=daIBasEkiplideri.getItem(adapterView.getSelectedItemPosition()).ID;
                                                //ibasBtnCalismaYok.setEnabled(true);
                                                //ibasBtnSonraki.setEnabled(true);

                                                final HashMap<String, String> seciligorev=db.gorevGetir(ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasEkipLideri,ibasTarih.getText().toString());
                                                Log.w("gpuantaj",""+seciligorev.size()+" "+seciligorev.get("aktarildi") );
                                                if(seciligorev!=null && seciligorev.size()>0) {
                                                    //ibasBtnCalismaYok.setEnabled(true);
                                                    ibasBtnSonraki.setEnabled(true);
                                                    /*if(!seciligorev.get("aktarildi").equals("1")) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                                                        builder.setTitle("Uyarı");
                                                        builder.setMessage("Bu bilgilere ait önceden puantaj oluşturulmuş. Düzenlemek istediğinize emin misiniz?");

                                                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                                            public void onClick(DialogInterface dialog, int which) {
                                                                ibasFisno.setText(seciligorev.get("fisno"));
                                                                MainActivity.gpd = new GunlukPuantajData(userid, ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasUrun, ibasEkipLideri, ibasTarih.getText().toString(), ibasFisno.getText().toString(), "guncelleme");
                                                                ibasBtnCalismaYok.setEnabled(true);
                                                                ibasBtnSonraki.setEnabled(true);
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

                                                        ibasFisno.setText(seciligorev.get("fisno"));
                                                    }else{
                                                        Toast.makeText(ParentCtxt, "Bu bilgilere göre önceden kayıt eklenmiş ve sunucuya gönderilmiştir.",Toast.LENGTH_SHORT).show();
                                                        ibasBtnCalismaYok.setEnabled(false);
                                                        ibasBtnSonraki.setEnabled(false);
                                                    }*/
                                                }else{
                                                    ibasBtnCalismaYok.setEnabled(true);
                                                    ibasBtnSonraki.setEnabled(true);
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });//ekiplideri
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });//Calisma
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });//bölge
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });//firma
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ibasBtnIptal) {
            finish();
        }else if(v.getId()==R.id.ibasBtnCalismaYok) {
            db=new Database(getApplicationContext());
            if(MainActivity.gpd==null || (MainActivity.gpd!=null && !MainActivity.gpd.getKayitdurumu().equals("guncelleme"))) {
                String[] ntarih = ibasTarih.getText().toString().split(" / ");
                final HashMap<String, String> calisma = db.getOneRow(new String[]{"OID"}, "tarim_istakip_calisma", "bolge='" + ibasBolge + "' and calisma='" + ibasCalisma + "' and firma='" + ibasFirma + "' and  yetkili='" + ibasYetkili + "' and ekip_lideri='" + ibasEkipLideri + "'and ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "'");
                if (calisma.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                    builder.setTitle("Uyarı");
                    builder.setMessage("Çalışma Yok seçtiniz. Emin misiniz?");

                    builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.gpd = new GunlukPuantajData(userid, ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasUrun, ibasEkipLideri, ibasTarih.getText().toString(), ibasFisno, "yenikayit");
                            MainActivity.gpd.setGlobalid(UUID.randomUUID().toString());
                            MainActivity.gpd.setCalismavar(0);
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
                } else {
                    //Toast.makeText(ParentCtxt, "Bu bilgilere göre önceden kayıt eklenmiş", Toast.LENGTH_SHORT).show();
                    if(MainActivity.gpd.getKayitdurumu().equals("guncelleme")){
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
                    }
                }
            }
        }else if(v.getId()==R.id.ibasBtnSonraki){
            if(!ibasFirma.equals("") && !ibasBolge.equals("") && !ibasCalisma.equals("") && !ibasYetkili.equals("")){

                if(false) {//Fiş kontrolü iptal edildi. - 14.05.2018
                    db = new Database(getApplicationContext());
                    //HashMap<String, String> resfisused=db.getOneRow(new String[]{"OID"},"tarim_istakip_calisma","FISNO='"+ibasFisno.getText()+"'");
                    //if(resfisused.size()==0) {
                    HashMap<String, String> resfis3 = db.getOneRow(new String[]{"FISBAS", "FISBIT"}, "user_fistanim", "USER_ID=" + userid + " AND FISBAS=0 AND FISBIT=0");
                    if (resfis3 != null && resfis3.size() == 0) {
                        HashMap<String, String> resfis = db.getOneRow(new String[]{"FISBAS", "FISBIT"}, "user_fistanim", "USER_ID=" + userid + " AND FISBAS<="); //+ ibasFisno.getText() + " AND FISBIT>=" + ibasFisno.getText() + "");
                        //if ( Float.parseFloat(ibasFisno.getText().toString())>=Float.parseFloat(resfis.get("FISBAS")) &&  Float.parseFloat(ibasFisno.getText().toString())<=Float.parseFloat(resfis.get("FISBIT"))) {
                        if (resfis.size() != 0) {
                            nextToPuantaj2Screen();
                        } else {//Tanımlı fişno sınırlarının dışında
                            ArrayList<HashMap<String, String>> resfis2 = db.getMultiResult(new String[]{"FISBAS", "FISBIT"}, "user_fistanim", "USER_ID=" + userid);
                            String fisnolar = "";
                            for (int i = 0; i < resfis2.size(); i++) {
                                fisnolar += "\n" + resfis2.get(i).get("FISBAS") + " - " + resfis2.get(i).get("FISBIT");
                            }
                            new ShowToast(this, "Kullanıcıya ait tanımlı fiş numaraları: " + fisnolar);
                        }
                    } else { //Fiş no tanımlı değil
                        new ShowToast(this, "Kullanıcıya ait fiş numaraları tanımlı değil");
                    }
                    //}else {//Fişno önceden kullanılmış
                    //    new ShowToast(this,"Fiş numarası önceden kullanılmış");
                    //}
                }else{
                    nextToPuantaj2Screen();
                }
            }else{
                new ShowToast(this, "Boş alanları kontrol ediniz.");
            }
        }
    }

    private void nextToPuantaj2Screen(){
        //Log.w("fisbasbit", resfis.get("FISBAS") + " " + resfis.get("FISBIT") + " " + ibasFisno.getText().toString() + " " + (Float.parseFloat(resfis.get("FISBAS")) >= Float.parseFloat(ibasFisno.getText().toString())));
        String[] ntarih = ibasTarih.getText().toString().split(" / ");
        HashMap<String, String> calisma = db.getOneRow(new String[]{"OID", "ISE_BASLAMA_TARIHI", "CALISMAVAR", "USER_ID", "FIRMA", "BOLGE", "CALISMA", "FISNO", "EKIP_LIDERI", "YETKILI", "SERVISVAR", "ACIKLAMA", "EKLIDOC1", "EKLIDOC2", "EKLIDOC3"}, "tarim_istakip_calisma", "bolge='" + ibasBolge + "' and calisma='" + ibasCalisma + "' and firma='" + ibasFirma + "' and  yetkili='" + ibasYetkili + "' and ekip_lideri='" + ibasEkipLideri + "'and ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "' and fisno='" + ibasFisno + "' ");
        if (MainActivity.gpd == null) {
            if (calisma.size() == 0) {
                //GunlukPuantajData gpd = new GunlukPuantajData(userid, ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasEkipLideri, ibasTarih.getText().toString(), ibasFisno.getText().toString());
                MainActivity.gpd = new GunlukPuantajData(userid, ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasUrun, ibasEkipLideri, ibasTarih.getText().toString(), ibasFisno, "yenikayit");
                MainActivity.gpd.setGlobalid(UUID.randomUUID().toString());

                KeyValueP[] gorevler = db.gorevGetir(ibasBolge, ibasFirma);//ibasCalisma, ibasFirma);
                for (int i = 0; i < gorevler.length; i++) {
                    MainActivity.gpd.addGorev("" + gorevler[i]);
                }

                ////db.close();

                //Log.w("here2",""+gpd.getGorevSize());

                Intent i = new Intent(this, GunlukPuantaj2.class);
                    /*Bundle mBundle = new Bundle();
                    mBundle.putSerializable("GPData", gpd);
                    i.putExtras(mBundle);*/
                startActivityForResult(i, REQUEST_GP2);

            } else {
                //Toast.makeText(ParentCtxt, "Bu bilgilere göre önceden kayıt eklenmiş", Toast.LENGTH_SHORT).show();

                final HashMap<String, String> seciligorev = db.gorevGetir(ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasEkipLideri, ibasTarih.getText().toString());
                Log.w("gpuantaj", "" + seciligorev.size() + " " + seciligorev.get("aktarildi"));
                if (seciligorev != null && seciligorev.size() > 0) {
                    if (!seciligorev.get("aktarildi").equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                        builder.setTitle("Uyarı");
                        builder.setMessage("Bu bilgilere ait önceden puantaj oluşturulmuş. Düzenlemek istediğinize emin misiniz?");

                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                //ibasFisno.setText(seciligorev.get("fisno"));
                                MainActivity.gpd = new GunlukPuantajData(userid, ibasBolge, ibasCalisma, ibasFirma, ibasYetkili, ibasUrun, ibasEkipLideri, ibasTarih.getText().toString(), ibasFisno, "guncelleme");
                                //ibasBtnCalismaYok.setEnabled(true);
                                //ibasBtnSonraki.setEnabled(true);
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

                        //ibasFisno.setText(seciligorev.get("fisno"));
                    } else {
                        new ShowToast(ParentCtxt, "Bu bilgilere göre önceden kayıt eklenmiş ve sunucuya gönderilmiştir.");
                        ibasBtnCalismaYok.setEnabled(false);
                        ibasBtnSonraki.setEnabled(false);
                    }
                } else {
                    ibasBtnCalismaYok.setEnabled(true);
                    ibasBtnSonraki.setEnabled(true);
                }
            }
        } else if (MainActivity.gpd.getKayitdurumu().equals("guncelleme")) {
                //MainActivity.gpd.setFisno(ibasFisno.getText().toString());
            Log.w("GP globalid",MainActivity.gpd.getGlobalid());
            MainActivity.gpd.setFisno(ibasFisno);

            MainActivity.gpd.setEkliDoc(calisma.get("EKLIDOC1"), calisma.get("EKLIDOC2"), calisma.get("EKLIDOC3"), calisma.get("ACIKLAMA"));

            ArrayList<HashMap<String, String>> res2 = db.getMultiResult(new String[]{"OID", "SICILNO", "TICID", "URUNID", "GOREV", "MESAI", "KARTLAEKLENDI"}, "tarim_istakip_calisma_personel", "AKTARILDI<1 AND AKTARILDI_ONAY=0 AND TICID='" + calisma.get("OID") + "' AND FISNO='"+ibasFisno+"'");//ibasFisno.getText().toString()+"'");
            for (int i = 0; i < res2.size(); i++) {
                HashMap<String, String> pers = db.getOneRow(new String[]{"AD", "SOYAD", "CINSIYET"}, "tarim_istakip_personel", "ID='" + res2.get(i).get("SICILNO") + "'");
                MainActivity.gpd.addPersonel(res2.get(i).get("GOREV"), res2.get(i).get("SICILNO"), pers.get("AD") + " " + pers.get("SOYAD"), pers.get("CINSIYET"), res2.get(i).get("MESAI"), res2.get(i).get("KARTLAEKLENDI"), res2.get(i).get("URUNID"));
            }
            Log.w("gp1servis", "" + calisma.get("SERVISVAR"));
            if (Integer.parseInt(calisma.get("SERVISVAR")) > 0) {
                String[] servis = db.servisGetir(MainActivity.gpd.getCalismaalani(), MainActivity.gpd.getFirma(), MainActivity.gpd.getEkiplideri());
                ArrayList<KeyValueP> puantajser = db.puantajServisGetir(calisma.get("OID"), MainActivity.userid, ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0], ibasFisno);//ibasFisno.getText().toString());

                for (int j = 0; j < servis.length - 1; j++) {
                    if (servis[j] != null && !servis[j].equals("0")) {
                        if (j < puantajser.size() && servis[j].equals(puantajser.get(j).ID)) {
                            //tmptxt2.setText("Servis " + servis[j] + " - " + (puantajser.get(j).name.equals("") ? "-" : puantajser.get(j).name) + "");
                            MainActivity.gpd.addServis("" + j, puantajser.get(j).name);
                            Log.w("gp1servis", "" + j + " " + servis[j] + " " + puantajser.get(j).name);
                        }
                    }
                }
            } else if (Integer.parseInt(calisma.get("SERVISVAR")) == -1) {
                MainActivity.gpd.addServis("KisiBasi", "KisiBasi");
            }

            Intent intent = new Intent(this, GunlukPuantaj2.class);
            startActivityForResult(intent, REQUEST_GP2);

        }
    }

    private String getDate(){
        final Calendar c2 = Calendar.getInstance();
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH)+1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);

        String res = ""+day2+" / "+month2+" / "+year2;

        try{
            Connectivity conn=new Connectivity();
            if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                WebRequest webreq = new WebRequest();

                // Making a request to url and getting response
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("token", "6ce304f73ce841efaf1490bb98474eef");
                params.put("op", "getdate");
                //params.put("uid",uid);
                params.put("ttt", "" + System.currentTimeMillis());
                Log.w("----",this.getResources().getString(R.string.serviceUrl));
                String jsonStr = webreq.makeWebServiceCall(this.getResources().getString(R.string.serviceUrl), WebRequest.POSTRequest, params);

                Log.w("Response: ", "> " + jsonStr);

                JSONObject jsonObj = new JSONObject(jsonStr);
                Log.w("Response: ", "> " + jsonObj.get("date"));


                if (jsonObj != null &&  jsonObj.has("date")){
                    db.tarihEkle(jsonObj.get("date").toString());
                    String[] serverdate = jsonObj.get("date").toString().split(" ");
                    String[] serverdd  = serverdate[0].split("-");
                    res =  Integer.parseInt(serverdd[2])+" / "+Integer.parseInt(serverdd[1])+" / "+Integer.parseInt(serverdd[0]);
                }else{
                    res = getDateFromDB(""+day2, ""+month2, ""+year2);
                }
            }else{
                Log.w("getDate", "-no internet");
                res = getDateFromDB(""+day2, ""+month2, ""+year2);
            }
        }catch (Exception ex){
            res = getDateFromDB(""+day2, ""+month2, ""+year2);
            Log.w("getDate", ex.getMessage());
        }finally {

        }

        return res;
    }

    private String getDateFromDB(String sday, String smon, String syear){
        String res = sday+" / "+smon+" / "+syear;

        try{
            HashMap<String,String> ress = db.getOneRow(new String[]{"SERVERDATE"},"gunceltarih","OID>0");
            if(ress!=null && !ress.isEmpty() &&  ress.get("SERVERDATE")!=null){
                String[] res2 = ress.get("SERVERDATE").split(" ");
                Log.w("getOneRow val", res2[0]);
                String[] serverdd  = res2[0].split("-");
                res =  Integer.parseInt(serverdd[2])+" / "+Integer.parseInt(serverdd[1])+" / "+Integer.parseInt(serverdd[0]);
            }
        }catch (Exception ex){
            Log.w("getDate error:", ex.getMessage());
        }
        Log.w("getDate", ""+res);
        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_GP2 && resultCode==RESULT_OK){
            if(data.hasExtra("exit")){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
