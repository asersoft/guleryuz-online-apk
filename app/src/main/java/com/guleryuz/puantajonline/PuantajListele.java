package com.guleryuz.puantajonline;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.guleryuz.puantajonline.CallBacks.DateChangeCallback;
import com.guleryuz.puantajonline.CallBacks.WebAppInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Asersoft on 5.03.2017.
 */

public class PuantajListele  extends AppCompatActivity implements View.OnClickListener, DateChangeCallback {
    private Spinner spnplBolge, spnplCalisma, spnplFirma, spnplYetkili, spnplEkiplideri, spnplGorev;
    private LinearLayout layoutpl;
    private ArrayAdapter<String> daplBolge, daplCalisma;
    private ArrayAdapter<KeyValueP> daplFirma, daplYetkili, daplEkiplideri, daplGorev, daplUrun;
    private static String plBolge, plCalisma, plFirma, plYetkili, plEkipLideri, plGorev;
    private static TextView plTarih, plFisno, plAciklama, plUrun;
    private static LinearLayout llGorevler, llGorevTitle, llGorevlerShort, llGorevTitleShort, llServisler, llServisTitle, llDoc, llDocTitle, llAckTitle;
    private static int selDay, selMonth, selYear;
    private WebView plWebview;
    private Database db;
    private static Context ParentCtxt;
    private static String userid;
    private static String doc1, doc2, doc3;
    private static String selectedDate;
    private Connectivity conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puantaj_listeleme);
        ParentCtxt = this;

        Intent intent= getIntent();
        Bundle b= intent.getExtras();
        if (b!=null){
            userid=b.getString("userid");
        }

        plTarih=(TextView)findViewById(R.id.pltarih);
        plWebview=(WebView)findViewById(R.id.plWebview);
        plWebview.addJavascriptInterface(new WebAppInterface(this), "Android");
        plWebview.getSettings().setJavaScriptEnabled(true);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        selDay=day;
        selMonth=month;
        selYear=year;
        plTarih.setText(day+" / "+(month+1)+" / "+year);
        plTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        conn=new Connectivity();
        if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
            plWebview.loadUrl("https://www.guleryuzcv.net/t_istakip/mobilsrv/pliste.php?ttt="+year+"-"+(month+1)+"-"+day+"&uid="+MainActivity.userid);
        }else{
            new ShowToast(this, R.string.msgInternetNoConnection);
        }
        /*spnplBolge=(Spinner)findViewById(R.id.plbolge);
        spnplCalisma=(Spinner)findViewById(R.id.plcalisma);
        spnplFirma=(Spinner)findViewById(R.id.plfirma);
        spnplYetkili=(Spinner)findViewById(R.id.plyetkili);
        spnplEkiplideri=(Spinner)findViewById(R.id.plekiplideri);
        plUrun=(TextView)findViewById(R.id.plurun);
        plTarih=(TextView)findViewById(R.id.pltarih);
        plFisno=(TextView)findViewById(R.id.plfisno);
        plAciklama=(TextView)findViewById(R.id.plAciklama);

        llGorevler=(LinearLayout) findViewById(R.id.llGorevler);
        llGorevTitle=(LinearLayout) findViewById(R.id.llGorevTitle);
        llGorevlerShort=(LinearLayout) findViewById(R.id.llGorevlerShort);
        llGorevTitleShort=(LinearLayout) findViewById(R.id.llGorevTitleShort);
        llServisler=(LinearLayout) findViewById(R.id.llServisler);
        llServisTitle=(LinearLayout) findViewById(R.id.llServisTitle);
        llDoc=(LinearLayout) findViewById(R.id.llDoc);
        llDocTitle=(LinearLayout) findViewById(R.id.llDocTitle);
        llAckTitle=(LinearLayout) findViewById(R.id.llAckTitle);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        selDay=day;
        selMonth=month;
        selYear=year;
        plTarih.setText(day+" / "+(month+1)+" / "+year);
        plTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        loadData();*/
    }

    private void loadData(){
        db=new Database(getApplicationContext());

        plBolge="";
        plCalisma="";
        plFirma="";
        plYetkili="";
        plEkipLideri="";
        plGorev="";
        // btnplOnay.setEnabled(false);
        daplBolge=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,db.firmaBolgeCAGetir("Bolge","",userid, plTarih.getText().toString()));
        daplBolge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnplBolge.setAdapter(daplBolge);
        spnplBolge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItem().toString()!=""){
                    daplCalisma=new ArrayAdapter<String>(ParentCtxt, android.R.layout.simple_spinner_item,db.firmaBolgeCAGetir("Çalışma Alanı",adapterView.getSelectedItem().toString(),userid, plTarih.getText().toString()));
                    daplCalisma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnplCalisma.setAdapter(daplCalisma);
                    plBolge=adapterView.getSelectedItem().toString();

                    temizle();

                    spnplCalisma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (adapterView.getSelectedItem().toString()!="") {
                                plCalisma=adapterView.getSelectedItem().toString();

                                temizle();

                                daplFirma = new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.firmaGetir(plBolge, plCalisma, userid, plTarih.getText().toString()));
                                daplFirma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnplFirma.setAdapter(daplFirma);
                                spnplFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        plFirma=daplFirma.getItem(adapterView.getSelectedItemPosition()).ID;
                                        Log.w("plFirma: ",plFirma);

                                        temizle();
                                        //fill yetkili
                                        daplYetkili=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.yetkiliGetir(plBolge, plCalisma, userid, plTarih.getText().toString()));
                                        daplYetkili.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spnplYetkili.setAdapter(daplYetkili);
                                        spnplYetkili.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                plYetkili=daplYetkili.getItem(adapterView.getSelectedItemPosition()).ID;
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });

                                        //fill ekiplideri
                                        daplEkiplideri=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.ekiplideriGetir(plCalisma, plFirma, plTarih.getText().toString()));
                                        daplEkiplideri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spnplEkiplideri.setAdapter(daplEkiplideri);
                                        spnplEkiplideri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                plEkipLideri=daplEkiplideri.getItem(adapterView.getSelectedItemPosition()).ID;
//fill görev
                                                temizle();
                                                //String calisma, String firma, String yetkili, String ekiplideri, String tarih) {
                                                HashMap<String, String> seciligorev=db.gorevGetir(plBolge,plCalisma, plFirma, plYetkili, plEkipLideri,plTarih.getText().toString());
                                                if(seciligorev!=null && seciligorev.size()>0 && seciligorev.get("calismavar").toString().equals("1")) {
                                                    String fisno=seciligorev.get("fisno");
                                                    if(fisno.length()==36){
                                                        fisno=fisno.substring(0,fisno.indexOf('-'));
                                                    }
                                                    plFisno.setText(fisno);//seciligorev.get("fisno"));4
                                                    plUrun.setText((!seciligorev.get("urun").equals("-1")?seciligorev.get("urun"):"-"));
                                                    try {
                                                        if (!seciligorev.get("aciklama").equals("")) {
                                                            llAckTitle.setVisibility(View.VISIBLE);
                                                            plAciklama.setVisibility(View.VISIBLE);
                                                            plAciklama.setText(seciligorev.get("aciklama"));
                                                        }

                                                        if (!seciligorev.get("eklidoc1").equals("")) {
                                                            llDocTitle.setVisibility(View.VISIBLE);
                                                            llDoc.setVisibility(View.VISIBLE);
                                                            ImageView idoc1 = new ImageView(getApplicationContext());
                                                            idoc1.setPadding(5,5,5,5);
                                                            idoc1.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
                                                            doc1 = seciligorev.get("eklidoc1");
                                                            File photoFile1 = new File(doc1);
                                                            BitmapFactory.Options options = new BitmapFactory.Options();
                                                            options.inSampleSize = 8;
                                                            Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile1.getAbsolutePath(),options);
                                                            idoc1.setImageBitmap(imageBitmap);
                                                            idoc1.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                                                                    intent.putExtra("photo", doc1);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                            llDoc.addView(idoc1);
                                                        }

                                                        if (!seciligorev.get("eklidoc2").equals("")) {
                                                            llDocTitle.setVisibility(View.VISIBLE);
                                                            llDoc.setVisibility(View.VISIBLE);
                                                            ImageView idoc2 = new ImageView(getApplicationContext());
                                                            idoc2.setPadding(5,5,5,5);
                                                            idoc2.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
                                                            doc2 = seciligorev.get("eklidoc2");
                                                            File photoFile2 = new File(doc2);
                                                            BitmapFactory.Options options = new BitmapFactory.Options();
                                                            options.inSampleSize = 8;
                                                            Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile2.getAbsolutePath(),options);
                                                            idoc2.setImageBitmap(imageBitmap);
                                                            idoc2.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                                                                    intent.putExtra("photo", doc2);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                            llDoc.addView(idoc2);
                                                        }

                                                        if (!seciligorev.get("eklidoc3").equals("")) {
                                                            llDocTitle.setVisibility(View.VISIBLE);
                                                            llDoc.setVisibility(View.VISIBLE);
                                                            ImageView idoc3 = new ImageView(getApplicationContext());
                                                            idoc3.setPadding(5,5,5,5);
                                                            idoc3.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
                                                            doc3 = seciligorev.get("eklidoc3");
                                                            File photoFile3 = new File(doc3);
                                                            BitmapFactory.Options options = new BitmapFactory.Options();
                                                            options.inSampleSize = 8;
                                                            Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile3.getAbsolutePath(),options);
                                                            idoc3.setImageBitmap(imageBitmap);
                                                            idoc3.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                                                                    intent.putExtra("photo", doc3);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                            llDoc.addView(idoc3);
                                                        }
                                                    }catch (Exception ex){
                                                        Log.w("eklidoc", ex.getMessage());
                                                    }

                                                    KeyValueP[] gorevler = db.gorevGetir(plBolge, plFirma);//plCalisma, plFirma);
                                                    LinearLayout layouts, layoutshort;
                                                    llGorevTitle.setVisibility(View.VISIBLE);
                                                    llGorevTitleShort.setVisibility(View.VISIBLE);
                                                    for (int j = 0; j < gorevler.length; j++) {
                                                        layouts = (LinearLayout) ((Activity) ParentCtxt).getLayoutInflater().inflate(R.layout.puantaj_listeleme_gorev, null);
                                                        TextView tmptxt = (TextView) layouts.findViewById(R.id.gorev);
                                                        tmptxt.setText(gorevler[j].name);

                                                        int kadin=0, erkek=0, mesai=0;
                                                        ArrayList<HashMap<String, String>> gorevper =db.gorevPersonelGetir(seciligorev.get("oid").toString(),gorevler[j].ID, userid, plTarih.getText().toString(),  seciligorev.get("fisno"));

                                                        if(gorevper!=null && gorevper.size()>0) {

                                                            TableLayout tmptbl = (TableLayout) layouts.findViewById(R.id.tblPersonel);
                                                            for(int k=0; k<gorevper.size(); k++) {
                                                                TableRow tr = new TableRow(getApplicationContext());
                                                                //tr.setBackground(getResources().getDrawable(R.drawable.border_puantajliste));
                                                                tr.setMinimumHeight(40);
                                                                TextView tmptxt2 = new TextView(getApplicationContext());
                                                                tmptxt2.setText(""+(k+1)+". "+gorevper.get(k).get("adi")+"("+gorevper.get(k).get("sicilno")+") (M: "+(gorevper.get(k).get("mesai").equals("")?"-":gorevper.get(k).get("mesai"))+")\n"+gorevper.get(k).get("urun"));
                                                                //tmptxt2.setTypeface(null, 1);
                                                                tr.addView(tmptxt2);
                                                                tmptxt2.setTextColor(Color.BLACK);
                                                                tr.setPadding(10, 10, 0, 10);
                                                                tmptbl.addView(tr);

                                                                if(gorevper.get(k).get("cinsiyet").equals("Erkek"))
                                                                    erkek++;
                                                                else
                                                                    kadin++;

                                                                mesai+=(gorevper.get(k).get("mesai").equals("")?0: Integer.parseInt(gorevper.get(k).get("mesai")));
                                                            }
                                                        }else{
                                                            TableLayout tmptbl = (TableLayout) layouts.findViewById(R.id.tblPersonel);

                                                            TableRow tr = new TableRow(getApplicationContext());
                                                            //tr.setBackground(getResources().getDrawable(R.drawable.border_puantajliste));
                                                            tr.setMinimumHeight(40);
                                                            TextView tmptxt2 = new TextView(getApplicationContext());
                                                            tmptxt2.setText(" - - - ");
                                                            //tmptxt2.setTypeface(null, 1);
                                                            tr.addView(tmptxt2);
                                                            //tmptxt2.setTextColor(Color.BLACK);
                                                            tr.setPadding(10, 10, 0, 10);
                                                            tmptbl.addView(tr);
                                                        }
                                                        llGorevler.addView(layouts);

                                                        layoutshort = (LinearLayout) ((Activity) ParentCtxt).getLayoutInflater().inflate(R.layout.puantaj_listeleme_gorevkisa, null);
                                                        TextView tmptxtsh = (TextView) layoutshort.findViewById(R.id.gorev);
                                                        tmptxtsh.setText(gorevler[j].name);
                                                        tmptxtsh = (TextView) layoutshort.findViewById(R.id.ibasBayan);
                                                        tmptxtsh.setText((kadin==0?"-":""+kadin));
                                                        tmptxtsh = (TextView) layoutshort.findViewById(R.id.ibasErkek);
                                                        tmptxtsh.setText((erkek==0?"-":""+erkek));
                                                        tmptxtsh = (TextView) layoutshort.findViewById(R.id.ibasToplam);
                                                        tmptxtsh.setText(""+(erkek+kadin));
                                                        tmptxtsh = (TextView) layoutshort.findViewById(R.id.ibasMesai);
                                                        tmptxtsh.setText((mesai==0?"-":""+mesai));

                                                        llGorevlerShort.addView(layoutshort);
                                                    }

                                                    llServisTitle.setVisibility(View.VISIBLE);
                                                    layouts = (LinearLayout) ((Activity) ParentCtxt).getLayoutInflater().inflate(R.layout.puantaj_listeleme_gorev, null);
                                                    TextView tmptxt = (TextView) layouts.findViewById(R.id.gorev);
                                                    tmptxt.setVisibility(View.GONE);
                                                    if(Integer.parseInt(seciligorev.get("servisvar"))>0) {
                                                        String[] servis = db.servisGetir(plCalisma, plFirma, plEkipLideri);

                                                        TableLayout tmptbl = (TableLayout) layouts.findViewById(R.id.tblPersonel);
                                                        ArrayList<KeyValueP> puantajser = db.puantajServisGetir(seciligorev.get("oid").toString(), userid, plTarih.getText().toString(), seciligorev.get("fisno"));

                                                        for (int j = 0; j < servis.length-1; j++) {
                                                            //Log.w("servisss",servis[j]+" "+puantajser.get(j).ID+" "+puantajser.get(j).name);
                                                            if (servis[j] != null && !servis[j].equals("0")) {
                                                                TableRow tr = new TableRow(getApplicationContext());
                                                                //tr.setBackground(getResources().getDrawable(R.drawable.border_puantajliste));
                                                                tr.setMinimumHeight(40);
                                                                TextView tmptxt2 = new TextView(getApplicationContext());
                                                                if (j < puantajser.size() && servis[j].equals(puantajser.get(j).ID)) {
                                                                    tmptxt2.setText("Servis " + servis[j] + " - " + (puantajser.get(j).name.equals("") ? "-" : puantajser.get(j).name) + "");
                                                                } else {
                                                                    tmptxt2.setText("Servis " + servis[j] + " - -");
                                                                }
                                                                //tmptxt2.setTypeface(null, 1);
                                                                tr.addView(tmptxt2);
                                                                tmptxt2.setTextColor(Color.BLACK);
                                                                tr.setPadding(10, 10, 0, 10);
                                                                tmptbl.addView(tr);

                                                            }
                                                        }
                                                    }else if(Integer.parseInt(seciligorev.get("servisvar"))==-1){
                                                        String[] servis = db.servisGetir(plCalisma, plFirma, plEkipLideri);
                                                        TableLayout tmptbl = (TableLayout) layouts.findViewById(R.id.tblPersonel);
                                                        TableRow tr = new TableRow(getApplicationContext());
                                                        //tr.setBackground(getResources().getDrawable(R.drawable.border_puantajliste));
                                                        tr.setMinimumHeight(40);
                                                        TextView tmptxt2 = new TextView(getApplicationContext());
                                                        tmptxt2.setText("Servis "+servis[servis.length-1]+"/kişi");
                                                        //tmptxt2.setTypeface(null, 1);
                                                        tr.addView(tmptxt2);
                                                        tmptxt2.setTextColor(Color.BLACK);
                                                        tr.setPadding(10, 10, 0, 10);
                                                        tmptbl.addView(tr);
                                                    }else{
                                                        TableLayout tmptbl = (TableLayout) layouts.findViewById(R.id.tblPersonel);
                                                        TableRow tr = new TableRow(getApplicationContext());
                                                        //tr.setBackground(getResources().getDrawable(R.drawable.border_puantajliste));
                                                        tr.setMinimumHeight(40);
                                                        TextView tmptxt2 = new TextView(getApplicationContext());
                                                        tmptxt2.setText("Servis Yok");
                                                        //tmptxt2.setTypeface(null, 1);
                                                        tr.addView(tmptxt2);
                                                        tmptxt2.setTextColor(Color.BLACK);
                                                        tr.setPadding(10, 10, 0, 10);
                                                        tmptbl.addView(tr);
                                                    }
                                                    llServisler.addView(layouts);
                                                /*tlplGorev.removeAllViews();

                                                TableRow tr = new TableRow(getApplicationContext());
                                                tr.setBackground(getResources().getDrawable(R.drawable.border_puantajliste));
                                                tr.setMinimumHeight(40);

                                                TextView tmptxt=new TextView(getApplicationContext());
                                                tmptxt.setText("Görev");
                                                tmptxt.setTypeface(null, 1);
                                                tmptxt.setGravity(Gravity.CENTER);
                                                tmptxt.setTextColor(Color.BLACK);
                                                tr.addView(tmptxt);

                                                tmptxt=new TextView(getApplicationContext());
                                                tmptxt.setText("Bayan");
                                                tmptxt.setTypeface(null, 1);
                                                tmptxt.setGravity(Gravity.CENTER);
                                                tmptxt.setTextColor(Color.BLACK);
                                                tr.addView(tmptxt);

                                                tmptxt=new TextView(getApplicationContext());
                                                tmptxt.setText("Erkek");
                                                tmptxt.setTypeface(null, 1);
                                                tmptxt.setGravity(Gravity.CENTER);
                                                tmptxt.setTextColor(Color.BLACK);
                                                tr.addView(tmptxt);

                                                tmptxt=new TextView(getApplicationContext());
                                                tmptxt.setText("Mesai");
                                                tmptxt.setTypeface(null, 1);
                                                tmptxt.setGravity(Gravity.CENTER);
                                                tmptxt.setTextColor(Color.BLACK);

                                                tr.setPadding(0, 12, 0, 12);
                                                tr.addView(tmptxt);
                                                tlplGorev.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                                                ArrayList<HashMap<String, String>> gorevler = db.gorevGetir(plCalisma, plFirma, plEkipLideri, plTarih.getText().toString());
                                                for (int ind=0; ind<gorevler.size(); ind++)
                                                {
                                                    tr = new TableRow(getApplicationContext());
                                                    tr.setBackground(getResources().getDrawable(R.drawable.border_puantajliste));
                                                    tr.setPadding(0, 12, 0, 12);
                                                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                                    tmptxt=new TextView(getApplicationContext());
                                                    tmptxt.setText(gorevler.get(ind).get("GOREV"));
                                                    tmptxt.setTextColor(Color.BLACK);
                                                    tr.addView(tmptxt);

                                                    tmptxt=new TextView(getApplicationContext());
                                                    tmptxt.setText(gorevler.get(ind).get("Bayan"));
                                                    tmptxt.setGravity(Gravity.CENTER);
                                                    tmptxt.setTextColor(Color.BLACK);
                                                    tr.addView(tmptxt);

                                                    tmptxt=new TextView(getApplicationContext());
                                                    tmptxt.setText(gorevler.get(ind).get("Erkek"));
                                                    tmptxt.setGravity(Gravity.CENTER);
                                                    tmptxt.setTextColor(Color.BLACK);
                                                    tr.addView(tmptxt);

                                                    tmptxt=new TextView(getApplicationContext());
                                                    tmptxt.setText(gorevler.get(ind).get("MESAI"));
                                                    tmptxt.setGravity(Gravity.CENTER);
                                                    tmptxt.setTextColor(Color.BLACK);
                                                    tr.addView(tmptxt);
                                                    tlplGorev.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                                    //gorevler[ind].name;
                                                }
                                                tlplGorev.setVisibility(View.VISIBLE);
                                                */
                                                }else{
                                                    plAciklama.setVisibility(View.VISIBLE);
                                                    plAciklama.setText((seciligorev!=null && seciligorev.size()==0?"Kayıt bulunamadı":"Çalışma Yok Olarak İşaretlenmiş."));
                                                    plAciklama.setTextColor(Color.RED);
                                                    plAciklama.setTextSize(14);
                                                    plAciklama.setTypeface(null, 1);
                                                    plAciklama.setGravity(Gravity.CENTER);
                                                }



                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });



                                            /*daplGorev=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.gorevGetir(plCalisma, plFirma));
                                            daplGorev.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spnplGorev.setAdapter(daplGorev);
                                            spnplGorev.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    plGorev=daplGorev.getItem(adapterView.getSelectedItemPosition()).ID;
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });*/

                                        //fill ürün
                                            /*daplUrun=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, db.urunGetir(plBolge, plFirma));
                                            daplUrun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spnplUrun.setAdapter(daplUrun);
                                            spnplUrun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    plUrun=daplUrun.getItem(adapterView.getSelectedItemPosition()).ID;
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });*/
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void DateChangeFinish(boolean stat){//Date changed
        if (stat) {
            //loadData();
            if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                plWebview.loadUrl("https://www.guleryuzcv.net/t_istakip/mobilsrv/pliste.php?ttt="+selectedDate+"&uid="+MainActivity.userid);
            }else{
                new ShowToast(this, R.string.msgInternetNoConnection);
            }
        }
    }
    private void temizle(){
        plFisno.setText("");
        plUrun.setText("");
        KeyValueP[] list= new KeyValueP[0];
        /*daplEkiplideri=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, list);
        spnplEkiplideri.setAdapter(daplEkiplideri);*/
        llGorevTitle.setVisibility(View.GONE);
        llGorevler.removeAllViews();
        llGorevTitleShort.setVisibility(View.GONE);
        llGorevlerShort.removeAllViews();
        llServisTitle.setVisibility(View.GONE);
        llServisler.removeAllViews();
        llDocTitle.setVisibility(View.GONE);
        llDoc.removeAllViews();
        llDoc.setVisibility(View.GONE);
        llAckTitle.setVisibility(View.GONE);
        plAciklama.setVisibility(View.GONE);
        plAciklama.setTextColor(Color.BLACK);
        plAciklama.setTextSize(14);
        plAciklama.setTypeface(null, 0);
        plAciklama.setGravity(Gravity.NO_GRAVITY);
        doc1="";
        doc2="";
        doc3="";
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private DateChangeCallback callback=null;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            /*final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);*/
            callback=(DateChangeCallback)getActivity();
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth, this, selYear, selMonth, selDay);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //ibasTarih.setText(day+" / "+month+" / "+year);
            selYear=year;
            selMonth=month;
            selDay=day;
            selectedDate = year+"-"+(month+1)+"-"+day;
            plTarih.setText(day+" / "+(month+1)+" / "+year);

            if(callback!=null) callback.DateChangeFinish(true);
        }
    }
}

