package com.guleryuz.puantajonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import 	android.text.method.DigitsKeyListener;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.guleryuz.puantajonline.OnlineService.ServerData;
import com.honeywell.aidc.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mehmet_erenoglu on 28.02.2017.
 */

public class GunlukPuantaj2 extends AppCompatActivity implements View.OnClickListener,  BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener    {
    private LinearLayout ibasLayout;
    private Button ibasBtnSonraki, ibasBtnIptal;
    private ImageView imgBtnBarcode, imgBtnManuelAdd;
    private EditText ibasMesai;
    private Spinner spnGorev, spnIBasUrun;
    private TextView ibasGorevEkipLideri;
    static final int REQUEST_GP2P = 1;
    static final int REQUEST_GP2G = 2;
    static final int REQUEST_GP3 = 3;
    static final int REQUEST_GP4P = 4;
    //private Database db;
    private ArrayAdapter<KeyValueP> daGorev, daIBasUrun;
    private String gorev;
    private static String ibasUrun;
    private static Context ParentCtxt;
    private ArrayList<HeaderInfo> SectionList = new ArrayList<>();
    private HashMap<String, TextView> gorevErkek;
    private HashMap<String, TextView> gorevBayan;
    private HashMap<String, TextView> gorevMesai;
    private HashMap<String, TextView> gorevEKToplam;
    private HashMap<String, ImageView> gorevPersonel;
    private List<KeyValueP> gorevler;

    private LinkedHashMap<String, HeaderInfo> mySection = new LinkedHashMap<>();

    ListView grid;


    private com.honeywell.aidc.BarcodeReader barcodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.guleryuz.puantajonline.R.layout.ise_baslama2);
        ParentCtxt=this;

        ibasLayout=(LinearLayout)findViewById(R.id.ibasLayout);
        ibasBtnIptal=(Button)findViewById(R.id.ibasBtnIptal);
        ibasBtnIptal.setOnClickListener(this);
        ibasBtnSonraki=(Button)findViewById(R.id.ibasBtnSonraki);
        ibasBtnSonraki.setOnClickListener(this);

        spnGorev=(Spinner)findViewById(R.id.spnGorev);
        spnIBasUrun=(Spinner)findViewById(R.id.ibasurun);
        ibasMesai=(EditText)findViewById(R.id.ibaspMesai);
        imgBtnBarcode=(ImageView)findViewById(R.id.btnBarcode);
        imgBtnBarcode.setImageResource(R.drawable.barcode);
        imgBtnBarcode.setOnClickListener(this);
        imgBtnManuelAdd=(ImageView)findViewById(R.id.btnManuelAdd);
        imgBtnManuelAdd.setImageResource(R.drawable.searchadd);
        imgBtnManuelAdd.setOnClickListener(this);
        ibasGorevEkipLideri=(TextView) findViewById(R.id.ibasGorevEkipLideri);

        initalizeBarcode();

        String ekiplideri = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ekiplideri = extras.getString("EkipLideri");
            //The key argument here must match that used in the other activity
        }

        //db=new Database(getApplicationContext());

        ibasGorevEkipLideri.setText(ekiplideri);

        //gorevleri getir
        gorevler = new ArrayList<KeyValueP>();
        try {
            List<String[]> req = new ArrayList<String[]>();
            req.add(new String[]{"op", "gorev"});
            req.add(new String[]{"firmaid", MainActivity.gpd.getFirma()});
            req.add(new String[]{"bolge", MainActivity.gpd.getBolge()});

            List<String[]> resp = new ArrayList<String[]>();
            resp.add(new String[]{"id", "id"});
            resp.add(new String[]{"gorev", "gorev"});
            List<HashMap<String, String>> data = new ServerData(this).getDataFromServer(MainActivity.userid, "GorevGetir", req, resp);

            for (int i = 0; i < data.size(); i++) {
                gorevler.add(new KeyValueP(data.get(i).get("id"), data.get(i).get("gorev")));
            }
        }catch (Exception ex){
            Log.w("GunlukPuantaj2", ex.getStackTrace().toString());
        }


        daGorev=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, gorevler);
        daGorev.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGorev.setAdapter(daGorev);
        spnGorev.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gorev=daGorev.getItem(adapterView.getSelectedItemPosition()).ID;
                ibasMesai.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<KeyValueP> urunler = new ArrayList<KeyValueP>();
        try {
            urunler.add(new KeyValueP("-1","Seçiniz"));
            List<String[]> req = new ArrayList<String[]>();
            req.add(new String[]{"op", "urun"});
            req.add(new String[]{"firmaid", MainActivity.gpd.getFirma()});
            req.add(new String[]{"bolge", MainActivity.gpd.getBolge()});

            List<String[]> resp = new ArrayList<String[]>();
            resp.add(new String[]{"id", "id"});
            resp.add(new String[]{"urun", "urun"});
            List<HashMap<String, String>> data = new ServerData(this).getDataFromServer(MainActivity.userid, "UrunGetir", req, resp);

            for (int i = 0; i < data.size(); i++) {
                urunler.add(new KeyValueP(data.get(i).get("id"), data.get(i).get("urun")));
            }
        }catch (Exception ex){
            Log.w("GunlukPuantaj2Urun", ex.getStackTrace().toString());
        }

        daIBasUrun=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, urunler);
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


        LinearLayout layouts;
        gorevBayan=new HashMap<String, TextView>();
        gorevErkek=new HashMap<String, TextView>();
        gorevEKToplam=new HashMap<String, TextView>();
        gorevMesai=new HashMap<String, TextView>();




        //gorevler=db.gorevGetir(MainActivity.gpd.getBolge(), MainActivity.gpd.getFirma());//MainActivity.gpd.getCalismaalani(), MainActivity.gpd.getFirma());
        for (int i=0; i<gorevler.size(); i++){
            layouts=(LinearLayout)this.getLayoutInflater().inflate(R.layout.grid_single_gorev,null);
            TextView txt=(TextView)layouts.findViewById(R.id.gorev);
            txt.setText(gorevler.get(i).name);
            txt.setTag(gorevler.get(i).ID);
            String g=gorevler.get(i).name;
            String gid=gorevler.get(i).ID;

            ImageView imageViewtmg = (ImageView)layouts.findViewById(R.id.toplumesaiguncell);
            imageViewtmg.setImageResource(R.drawable.refresh);
            imageViewtmg.setTag(g+";"+gid);
            imageViewtmg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        String t=v.getTag().toString();
                        String[] t2=t.split(";");
                        final String gorvid=t2[1];
                        if(MainActivity.gpd.getPersonel(gorvid).size()!=0) {
                            final EditText taskEditText = new EditText(ParentCtxt);
                            taskEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
                            //taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            AlertDialog dialog = new AlertDialog.Builder(ParentCtxt)
                                    .setTitle("Toplu Puantaj Güncelleme")
                                    .setMessage(t2[0] + " gorevine ait mesai girin")
                                    .setView(taskEditText)
                                    .setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String mesai = String.valueOf(taskEditText.getText());
                                            Log.w("tmg", mesai);
                                            MainActivity.gpd.setPersonelTopluMesai(gorvid, mesai);
                                            float toplammesai = 0;
                                            List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(gorvid);
                                            for (int i = 0; i < personels.size(); i++) {
                                                toplammesai += (personels.get(i).mesai.equals("") ? 0 : Float.parseFloat(personels.get(i).mesai));
                                            }
                                            gorevMesai.get(gorvid).setText("" + (toplammesai == 0 ? "-" : toplammesai));
                                        }
                                    })
                                    .setNegativeButton("İptal", null)
                                    .create();
                            dialog.show();
                        }
                    }catch (Exception ex){
                        Log.w("heretmg",ex.getMessage());
                    }
                }
            });


            ImageView imageView = (ImageView)layouts.findViewById(R.id.personelliste);
            imageView.setImageResource(R.drawable.list);

            imageView.setTag(g+";"+gid);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        String t=v.getTag().toString();
                        String[] t2=t.split(";");
                        Intent i = new Intent(ParentCtxt, GunlukPuantaj2PersonelListe.class);
                        i.putExtra("gorev", t2[0]);
                        i.putExtra("gorevid", t2[1]);
                        startActivityForResult(i,REQUEST_GP4P);
                    }catch (Exception ex){
                        Log.w("herepl",ex.getMessage());
                    }
                }
            });
            TextView tmp=(TextView)layouts.findViewById(R.id.ibasBayan);
            tmp.setText("-");
            gorevBayan.put(gorevler.get(i).ID, tmp);
            tmp=(TextView)layouts.findViewById(R.id.ibasErkek);
            tmp.setText("-");
            gorevErkek.put(gorevler.get(i).ID, tmp);
            tmp=(TextView)layouts.findViewById(R.id.ibasToplam);
            tmp.setText("-");
            gorevEKToplam.put(gorevler.get(i).ID, tmp);
            TextView tmp2=(TextView) layouts.findViewById(R.id.ibasMesai);
            tmp2.setText("-");
            gorevMesai.put(gorevler.get(i).ID, tmp2);
            ibasLayout.addView(layouts);

            if (MainActivity.gpd.getKayitdurumu().equals("guncelleme")){
                int[] v = MainActivity.gpd.getPersonelBayanErkek(gorevler.get(i).ID);

                gorevBayan.get(gorevler.get(i).ID).setText("" + (v[1] != 0 ? v[1] : "-"));
                gorevErkek.get(gorevler.get(i).ID).setText("" + (v[0] != 0 ? v[0] : "-"));
                gorevEKToplam.get(gorevler.get(i).ID).setText("" + (v[0]+v[1]));
                float toplammesai = 0;
                List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(gorevler.get(i).ID);
                if(personels!=null) {
                    for (int j = 0; j < personels.size(); j++) {
                        toplammesai += (personels.get(j).mesai.equals("") ? 0 : Float.parseFloat(personels.get(j).mesai));
                    }
                }
                gorevMesai.get(gorevler.get(i).ID).setText("" + (toplammesai == 0 ? "-" : toplammesai));
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
        builder.setTitle("Uyarı");
        builder.setMessage("Puantaj ekranından çıkmak istediğinize emin misiniz?");

        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.putExtra("exit", "exit");
                intent.putExtra("status", "discard");
                setResult(RESULT_OK, intent);
                finish();
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

    public void onClick(View v){
        if (v.getId()== R.id.ibasBtnIptal){
            AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
            builder.setTitle("Uyarı");
            builder.setMessage("Puantaj ekranından çıkmak istediğinize emin misiniz?");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra("exit", "exit");
                    intent.putExtra("status", "discard");
                    setResult(RESULT_OK, intent);
                    finish();
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
        }else if(v.getId()== R.id.ibasBtnSonraki){
            boolean gstatus=false;
            for (int i=0; i<gorevler.size(); i++){
                if (MainActivity.gpd.getPersonelSize(gorevler.get(i).ID)>0){
                    gstatus=true;
                    break;
                }
            }
            if(gstatus) {
                Intent intent = new Intent(getApplicationContext(), GunlukPuantaj3.class);
/*
                for (int i = 0; i < gorevler.length; i++) {
                    MainActivity.gpd.addMesai(gorevler[i].ID, gorevMesai.get(gorevler[i].ID).getText().toString());
                }
*/
                startActivityForResult(intent, REQUEST_GP3);
            }else{
                new ShowToast(ParentCtxt, "Görev Personel ataması yapılmamış.");
            }
        }else if(v.getId()== R.id.btnManuelAdd)
        {
            Intent i = new Intent(getApplicationContext(), GunlukPuantaj2Personel.class);
            i.putExtra("userid", MainActivity.userid);
            i.putExtra("bolge", MainActivity.gpd.getBolge());
            i.putExtra("calismaalani", MainActivity.gpd.getCalismaalani());
            i.putExtra("firma", MainActivity.gpd.getFirma());
            i.putExtra("yetkili", MainActivity.gpd.getYetkili());
            startActivityForResult(i,REQUEST_GP2P);
        }else if (v.getId()== R.id.btnBarcode){
            try {
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.setTitle("Güleryüz");
                scanIntegrator.initiateScan();
            } catch (Exception ex) {
                Log.w("Barcode", "nothing");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            Log.w("here2", "" + requestCode + " :" + REQUEST_GP2P + " - " + resultCode);
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

            if (requestCode == REQUEST_GP2P && resultCode == RESULT_OK)//GunlukPuantaj2Personel
            {
                Log.w("gp2p", intent.getStringExtra("sicilno")+" - "+intent.getStringExtra("cinsiyet"));
                GunlukPersonelData tmp = new GunlukPersonelData(intent.getStringExtra("sicilno"), intent.getStringExtra("adi"), intent.getStringExtra("cinsiyet"), ibasMesai.getText().toString(), "0", ibasUrun, intent.getStringExtra("soyad"), intent.getStringExtra("tc"), intent.getStringExtra("kartno"));
                //ibasMesai.setText("");
                String gorev2=MainActivity.gpd.addPersonel(gorev, tmp);
                if (!gorev2.equals(gorev)) {
                    int[] v = MainActivity.gpd.getPersonelBayanErkek(gorev2);

                    gorevBayan.get(gorev2).setText("" + (v[1] != 0 ? v[1] : "-"));
                    gorevErkek.get(gorev2).setText("" + (v[0] != 0 ? v[0] : "-"));
                    gorevEKToplam.get(gorev2).setText("" + (v[0]+v[1]));
                    float toplammesai = 0;
                    List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(gorev2);
                    for (int i = 0; i < personels.size(); i++) {
                        toplammesai += (personels.get(i).mesai.equals("") ? 0 : Float.parseFloat(personels.get(i).mesai));
                        Log.w("mesai",""+personels.get(i).mesai+" - "+Float.parseFloat(personels.get(i).mesai));
                    }
                    gorevMesai.get(gorev2).setText("" + (toplammesai == 0 ? "-" : toplammesai));
                }
                int[] v = MainActivity.gpd.getPersonelBayanErkek(gorev);

                gorevBayan.get(gorev).setText("" + (v[1] != 0 ? v[1] : "-"));
                gorevErkek.get(gorev).setText("" + (v[0] != 0 ? v[0] : "-"));
                gorevEKToplam.get(gorev).setText("" + (v[0]+v[1]));
                float toplammesai = 0;
                List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(gorev);
                for (int i = 0; i < personels.size(); i++) {
                    toplammesai += (personels.get(i).mesai.equals("") ? 0 : Float.parseFloat(personels.get(i).mesai));
                    //Log.w("mesai2",""+personels.get(i).mesai+" - "+Float.parseFloat(personels.get(i).mesai));
                }
                gorevMesai.get(gorev).setText("" + (toplammesai == 0 ? "-" : toplammesai));
                 /*   int toplammesai=0;
                    List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(gorev2);
                    for (int i=0; i<personels.size(); i++){
                        toplammesai += (personels.get(i).mesai.equals("")?0:Integer.parseInt(personels.get(i).mesai));
                    }
                    gorevMesai.get(gorev2).setText(""+(toplammesai==0?"-":toplammesai));

                    if (intent.getStringExtra("cinsiyet").equals("Erkek")) {
                        String val = gorevErkek.get(gorev2).getText().toString();
                        if (val.equals("-")) {
                            val = "0";
                        }
                        int valInt = Integer.parseInt(val);
                        gorevErkek.get(gorev2).setText("" + ((valInt - 1)==0?"-":(valInt - 1)));
                    } else {
                        String val = gorevBayan.get(gorev2).getText().toString();
                        if (val.equals("-")) {
                            val = "0";
                        }
                        int valInt = Integer.parseInt(val);
                        gorevBayan.get(gorev2).setText("" + ((valInt - 1)==0?"-":(valInt - 1)));
                    }
                }

                int toplammesai=0;
                List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(gorev);
                for (int i=0; i<personels.size(); i++){
                    toplammesai += (personels.get(i).mesai.equals("")?0:Integer.parseInt(personels.get(i).mesai));
                }
                gorevMesai.get(gorev2).setText(""+(toplammesai==0?"-":toplammesai));

                if (intent.getStringExtra("cinsiyet").equals("Erkek")) {
                    String val = gorevErkek.get(gorev).getText().toString();
                    if (val.equals("-")) {
                        val = "0";
                    }
                    int valInt = Integer.parseInt(val);
                    gorevErkek.get(gorev).setText("" + (valInt + 1));
                } else {
                    String val = gorevBayan.get(gorev).getText().toString();
                    if (val.equals("-")) {
                        val = "0";
                    }
                    int valInt = Integer.parseInt(val);
                    gorevBayan.get(gorev).setText("" + (valInt + 1));
                }*/
                Log.w("here2", "" + MainActivity.gpd.getPersonelSize(gorev2));
            } else if (requestCode == REQUEST_GP2G && resultCode == RESULT_OK) {
                for (int i = 0; i < gorevler.size(); i++) {
                    int[] v = MainActivity.gpd.getPersonelBayanErkek(gorevler.get(i).ID);

                    gorevBayan.get(gorevler.get(i).ID).setText("" + (v[1] != 0 ? v[1] : "-"));
                    gorevErkek.get(gorevler.get(i).ID).setText("" + (v[0] != 0 ? v[0] : "-"));
                    gorevEKToplam.get(gorevler.get(i).ID).setText("" + (v[0] + v[1]));
                }
            } else if (requestCode == REQUEST_GP3 && resultCode == RESULT_OK) {
                if (intent.hasExtra("exit")) {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else if (requestCode == REQUEST_GP4P && resultCode == RESULT_OK) {
                if (intent.hasExtra("gorev")) {
                    int[] v = MainActivity.gpd.getPersonelBayanErkek(intent.getStringExtra("gorev"));

                    gorevBayan.get(intent.getStringExtra("gorev")).setText("" + (v[1] != 0 ? v[1] : "-"));
                    gorevErkek.get(intent.getStringExtra("gorev")).setText("" + (v[0] != 0 ? v[0] : "-"));
                    gorevEKToplam.get(intent.getStringExtra("gorev")).setText("" + (v[0] +v[1]));
                    float toplammesai = 0;
                    List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(intent.getStringExtra("gorev"));
                    for (int i = 0; i < personels.size(); i++) {
                        toplammesai += (personels.get(i).mesai.equals("") ? 0 : Float.parseFloat(personels.get(i).mesai));
                    }
                    gorevMesai.get(intent.getStringExtra("gorev")).setText("" + (toplammesai == 0 ? "-" : toplammesai));
                }
            } else {
                if (scanningResult != null) {
                    //String scanContent = scanningResult.getContents();

                    barcodeScanResult(scanningResult.getContents());

                    //ibasMesai.setText("");
                }
            }
        }catch (Exception ex){
            Log.w("onActivityResult", "error"+ ex.getStackTrace());
        }
    }

    private void barcodeScanResult(String scanContent){
        if (scanContent!=null && !scanContent.equals("")) {
            /*List<HashMap<String, String>> personelbilgileri = null;
            try {
                List<String[]> req = new ArrayList<String[]>();
                req.add(new String[]{"op", "persor"});
                req.add(new String[]{"kartno", scanContent});
                req.add(new String[]{"sicilno", ""});
                req.add(new String[]{"ad", ""});
                req.add(new String[]{"soyad", ""});

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
                resp.add(new String[]{"RESIM", "resim"});
                resp.add(new String[]{"KARTNO", "kartno"});
                resp.add(new String[]{"SGK_EVRAK", "sgk_evrak"});
                resp.add(new String[]{"SSK", "ssk"});
                resp.add(new String[]{"SSK_CIKIS", "ssk_cikis"});
                resp.add(new String[]{"DEVAM", "devam"});
                resp.add(new String[]{"ONAY", "onay"});

                personelbilgileri = new ServerData(this).getDataFromServer(MainActivity.userid, "PersonelBilgisiGetir", req, resp);
            }catch (Exception ex){
                Log.w("GunlukPuantaj2Urun", ex.getStackTrace().toString());
            }*/

            List<HashMap<String, String>> personelbilgileri = new ServerData(this).personelSorgula(MainActivity.userid, scanContent,"","","","");
            if (personelbilgileri!=null && personelbilgileri.size() > 0) {
                Log.w("puantaj", scanContent);
                Intent intPersonel = new Intent(getApplicationContext(), PersonelGoruntule.class);
                intPersonel.putExtra("kartno", scanContent);
                intPersonel.putExtra("shortdisplay", scanContent);
                startActivity(intPersonel);

                GunlukPersonelData tmp = new GunlukPersonelData(personelbilgileri.get(0).get("ID"), personelbilgileri.get(0).get("AD") + " " + personelbilgileri.get(0).get("SOYAD"), personelbilgileri.get(0).get("CINSIYET"), ibasMesai.getText().toString(), "1", ibasUrun, personelbilgileri.get(0).get("SOYAD"),  personelbilgileri.get(0).get("TC"),  personelbilgileri.get(0).get("KARTNO"));
                String gorev2 = MainActivity.gpd.addPersonel(gorev, tmp);
                if (!gorev2.equals(gorev)) {
                    int[] v = MainActivity.gpd.getPersonelBayanErkek(gorev2);

                    gorevBayan.get(gorev2).setText("" + (v[1] != 0 ? v[1] : "-"));
                    gorevErkek.get(gorev2).setText("" + (v[0] != 0 ? v[0] : "-"));
                    gorevEKToplam.get(gorev2).setText("" + (v[0] +v[1]));
                    float toplammesai = 0;
                    List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(gorev2);
                    for (int i = 0; i < personels.size(); i++) {
                        toplammesai += (personels.get(i).mesai.equals("") ? 0 : Float.parseFloat(personels.get(i).mesai));
                    }
                    gorevMesai.get(gorev2).setText("" + (toplammesai == 0 ? "-" : toplammesai));
                }
                int[] v = MainActivity.gpd.getPersonelBayanErkek(gorev);

                gorevBayan.get(gorev).setText("" + (v[1] != 0 ? v[1] : "-"));
                gorevErkek.get(gorev).setText("" + (v[0] != 0 ? v[0] : "-"));
                gorevEKToplam.get(gorev).setText("" + (v[0] + v[1]));
                float toplammesai = 0;
                List<GunlukPersonelData> personels = MainActivity.gpd.getPersonel(gorev);
                for (int i = 0; i < personels.size(); i++) {
                    toplammesai += (personels.get(i).mesai.equals("") ? 0 : Float.parseFloat(personels.get(i).mesai));
                }
                gorevMesai.get(gorev).setText("" + (toplammesai == 0 ? "-" : toplammesai));
            } else {
                new ShowToast(ParentCtxt, "Kartnoya ait personel bulunamadı");
            }
        }
    }

    private void initalizeBarcode(){
        barcodeReader = MainActivity.getBarcodeObject();

        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);

            // set the trigger mode to client control
            try {
                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Hata oluştu", Toast.LENGTH_SHORT).show();
            }
            // register trigger state change listener
            barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Enable bad read response
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
            // Apply the settings
            barcodeReader.setProperties(properties);
        }
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(ParentCtxt, event.getBarcodeData(), Toast.LENGTH_SHORT).show();
                barcodeScanResult(event.getBarcodeData());
                // update UI to reflect the data
               /* List<String> list = new ArrayList<String>();
                list.add("Barcode data: " + event.getBarcodeData());
                list.add("Character Set: " + event.getCharset());
                list.add("Code ID: " + event.getCodeId());
                list.add("AIM ID: " + event.getAimId());
                list.add("Timestamp: " + event.getTimestamp());

                txtAdSoyad.setText("");
                txtFirma.setText("");
                txtFuar.setText("");
                txtKarttipi.setText("");
                txtBarkod.setText("");
                imgStatus.setVisibility(View.VISIBLE);
                imgStatus.setImageDrawable(null);
                readBarcode="";

                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        AutomaticBarcodeActivity.this, android.R.layout.simple_list_item_1, list);

                Connectivity conn = new Connectivity();

                if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                    QueryData qd = new QueryData(prntAct);
                    qd.context = parentActivity;
                    qd.barkod = event.getBarcodeData();
                    qd.brdcdcode = MainActivity.usercode;
                    readBarcode=event.getBarcodeData();
                    qd.girisCikis=MainActivity.selectedButton;
                    qd.execute();
                }else{
                    Toast.makeText(parentActivity, "Internet bağlantınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    // When using Automatic Trigger control do not need to implement the
    // onTriggerEvent function
    @Override
    public void onTriggerEvent(TriggerStateChangeEvent event) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);

            // unregister trigger state change listener
            barcodeReader.removeTriggerListener(this);
        }
    }

}

