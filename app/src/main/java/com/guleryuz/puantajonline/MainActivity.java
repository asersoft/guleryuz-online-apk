package com.guleryuz.puantajonline;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.view.View.OnClickListener;
import android.content.Intent;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.guleryuz.puantajonline.CallBacks.ServiceCallBack;
import com.guleryuz.puantajonline.OnlineService.DataFromService;
import com.guleryuz.puantajonline.OnlineService.PersonelOnline;
import com.guleryuz.puantajonline.OnlineService.UploadFileToServer;
import com.guleryuz.puantajonline.synchronize.Ekiplideri;
import com.guleryuz.puantajonline.synchronize.Firma;
import com.guleryuz.puantajonline.synchronize.FirmaBolge;
import com.guleryuz.puantajonline.synchronize.Gorev;
import com.guleryuz.puantajonline.synchronize.Personel;
import com.guleryuz.puantajonline.synchronize.PersonelBelgeTur;
import com.guleryuz.puantajonline.synchronize.PersonelFotoThread;
import com.guleryuz.puantajonline.synchronize.PersonelSGKEvrakThread;
import com.guleryuz.puantajonline.synchronize.PushDataToServer;
import com.guleryuz.puantajonline.synchronize.Servis;
import com.guleryuz.puantajonline.synchronize.SifremiUnuttum;
import com.guleryuz.puantajonline.synchronize.Urun;
import com.guleryuz.puantajonline.synchronize.UsersUpdate;
import com.guleryuz.puantajonline.synchronize.Yetkili;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import android.os.Handler;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.guleryuz.puantajonline.CallBacks.TaskCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/*
* 1.0
* Date: 04.03.2019
* Personel Sorgulamaya fotoğraf ekleme özelliği eklendi.
* ------
*/

public class MainActivity extends AppCompatActivity implements OnClickListener, TaskCallback, Handler.Callback, ServiceCallBack {
    //Klasör adları ve program versiyon ayarları bu kısımdan yapılır.
    //Her uygulama revizyonunda PROGRAM_VERSION değeri değiştirilmelidir.
    public final static String rootDir=".Guleryuz", attachDir=".Ekler", docDir=".Belgeler", sgkDir="SGK";
    public final static String PROGRAM_VERSION="1.3";
    private Connectivity conn;

    private ProgressDialog proDialog;
    private long personelFotoSayisi=0, curCount, pfHatali=0;
    private long personelFotoSayisiSGK=0, curCountSGK, pfHataliSGK=0;

    private Button btnPSorgulama, btnPersonelEkle, btnPuantajListeleme, btnIseBaslama, btnPuantajWait, btnPuantajSent , loginBtn;
    //private Button btnSync, btnSyncAl;
    private Menu menuu;
    public static Activity mactivity;
    public static RequestQueue serverQueue;
    private LinearLayout llayoutPersonel, llayoutWaitNSent;
    private LinearLayout llayoutDuyuru,wvLayout;
    public static TextView txtBilgilendirme;
    private TextView txtProgVersion;
    final Handler handler = new Handler();
    Timer timer = new Timer();
    //Personel Ekle
    private static final int REQUEST_PE=40;

    //Personel Sorgulama
    //private ScrollView  svScrollSync;//svScrollIBas, svScrollPL
    private LinearLayout svScroll, layoutPSFoto, layoutPSFotoEmpty, layoutPSNufusFoto;//layoutPL
    private RelativeLayout layoutPS;
    private Button btnPSBarkodOku, btnPSBarkodYeni, psNufusIptalet, psNufusKaydet;
    private EditText psBarkod, psTC, psKartno, psAd, psSoyad;
    private TextView psDogumTarihi, psCinsiyet, psGorev, psBolge, psBolge2, psBolge3, psBolge4, psBolge5;
    private TextView psEkiplideri, psEkiplideri2, psEkiplideri3, pssgkevrak, psSSKDurumu;
    private ImageView psFoto, imgChangeKartno, imgCancelKartno, imgAddFoto, nufusFoto1, nufusFoto2;
    public String SGK_Evrak;
    private File psNewFoto, psNufus1, psNufus2;
    private String nufus1Filename, nufus2Filename, nufusKaydedildi;
    private static String sorgulananSicilno="";
    static final int REQUEST_PSTAKE_PHOTO = 50;
    static final int REQUEST_PSTAKE_NUFUS1 = 75;
    static final int REQUEST_PSTAKE_NUFUS2 = 100;
    //----

    //Ise Baslama
    public static final int REQUEST_GP2=2;
    public static GunlukPuantajData gpd;
    //----
    //Is Bitirme
    private LinearLayout layoutIBit;
    private Button btnIBitBarkodOku, btnIBitOnay;
    private static TextView ibitBitTarih;
    private TextView ibitBarkod, ibitBolge, ibitCalisma, ibitFirma, ibitYetkili, ibitEkiplideri, ibitBasTarih, ibitFisno, ibitGorev, ibitUrun;
    private String ibitpersonel;
    //----

    //Puantaj Listeleme
    private static final int REQUEST_PL=3;
    //----

    //Sync
    private Button btnSDFromSync, btnSDToSyncTumu, btnSDToSyncPersonel, btnSDToSyncPuantaj;
    private TextView txtSDFromServerTime, txtSDToServerTimePuantaj, txtSDToServerTimePersonel,txtSDToServerInfo;
    //----

    private EditText username, password;
    private CheckBox chkSifremiHatirla;
    private TextView btnSifremiUnuttum;
    private TextView formatTxt, contentTxt;
    private ViewGroup loginLayout;
    private static String activeButton="";
    private static int exitApp;
    private static long lastSync = 0;
    private Database db;
    private static Context ParentCtxt;
    public static String userid;
    public static String userPuantajYetki;
    private String imgFile;
    private File imgFile2;
    private String barkodIcerik, barkodIslem;

    private GoogleApiClient client;

    private static BarcodeReader barcodeReader;
    private AidcManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.guleryuz_title);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>&nbsp;&nbsp;Güleryüz Online v2</font>"));
        setContentView(R.layout.activity_main);
        ParentCtxt=this;
        mactivity=this;

        exitApp=3;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //database copy
        /*
    try {
            String currentDBPath = "//data//user//0//com.guleryuz.puantajonline//databases//sqlite_database10.sqlite";
            FileInputStream in = new FileInputStream(currentDBPath);
            FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/sqlite_database10.sqlite");
            FileChannel fromChannel = null, toChannel = null;
            try {
                fromChannel = in.getChannel();
                toChannel = out.getChannel();
                fromChannel.transferTo(0, fromChannel.size(), toChannel);
            } finally {
                if (fromChannel != null)
                    fromChannel.close();
                if (toChannel != null)
                    toChannel.close();
            }
        } catch (Exception e) {
            Log.w("ERROR",e.getMessage());
        }
        try {
            String currentDBPath = "//data//user//0//com.guleryuz.puantajonline//databases//sqlite_database10.sqlite";
            FileInputStream in = new FileInputStream(Environment.getExternalStorageDirectory()+"/sqlite_database10.sqlite");
            FileOutputStream out = new FileOutputStream(currentDBPath);
            FileChannel fromChannel = null, toChannel = null;
            try {
                fromChannel = in.getChannel();
                toChannel = out.getChannel();
                fromChannel.transferTo(0, fromChannel.size(), toChannel);
            } finally {
                if (fromChannel != null)
                    fromChannel.close();
                if (toChannel != null)
                    toChannel.close();
            }
        } catch (Exception e) {
            Log.w("DBERROR",e.getMessage());
        }

*/
        //SyncData
  /*      Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent(this, SyncData.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //for 30 mint 60*60*1000
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pintent);
        startService(new Intent(getBaseContext(), SyncData.class));
*/
        //stopService(new Intent(getBaseContext(), SyncData.class));
        //------

       // db=new Database(getApplicationContext());
        //db.personelDeleteAll();
        //Log.w("here",""+db.personelSayisi());


        //logo=(ImageView)findViewById(R.id.logo);
        txtProgVersion=(TextView)findViewById(R.id.txtProgVersion);
        txtProgVersion.setText(PROGRAM_VERSION);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        btnPSorgulama = (Button)findViewById(R.id.buttonPersonelSorgulama);
        btnPuantajListeleme = (Button)findViewById(R.id.buttonPuantajListeleme);
        btnIseBaslama = (Button)findViewById(R.id.buttonIseBaslama);
        //btnSync = (Button)findViewById(R.id.buttonSync);
        btnPuantajWait=(Button)findViewById(R.id.buttonWait);
        btnPuantajWait.setOnClickListener(this);
        btnPuantajSent=(Button)findViewById(R.id.buttonSent);
        btnPuantajSent.setOnClickListener(this);
        llayoutPersonel=(LinearLayout)findViewById(R.id.llayoutPersonel);
        //llayoutSync=(LinearLayout)findViewById(R.id.llayoutSync);
        llayoutWaitNSent=(LinearLayout)findViewById(R.id.llayoutWaitNSent);
        llayoutDuyuru=(LinearLayout)findViewById(R.id.llayoutDuyuru);
        wvLayout=(LinearLayout)findViewById(R.id.wvLayout);
        txtBilgilendirme=(TextView)findViewById(R.id.txtBilgilendirme);

        conn=new Connectivity();
        if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {

        }


        //Personel Ekle
        btnPersonelEkle=(Button)findViewById(R.id.buttonPersonelEkle);
        btnPersonelEkle.setOnClickListener(this);

        //Personel Sorgulama
        svScroll=(LinearLayout) findViewById(R.id.svScroll);
        psAd=(EditText)findViewById(R.id.psad);
        psSoyad=(EditText)findViewById(R.id.pssoyad);
        psKartno=(EditText)findViewById(R.id.psKartno);
        psKartno.setEnabled(true);
        psBarkod=(EditText)findViewById(R.id.psbarkod);
        psBolge=(TextView)findViewById(R.id.psbolge);
        psBolge2=(TextView)findViewById(R.id.psbolge2);
        psBolge3=(TextView)findViewById(R.id.psbolge3);
        psBolge4=(TextView)findViewById(R.id.psbolge4);
        psBolge5=(TextView)findViewById(R.id.psbolge5);
        psCinsiyet=(TextView)findViewById(R.id.pscinsiyet);
        psDogumTarihi=(TextView)findViewById(R.id.psdogumtarihi);
        psEkiplideri=(TextView)findViewById(R.id.psekiplideri);
        psEkiplideri2=(TextView)findViewById(R.id.psekiplideri2);
        psEkiplideri3=(TextView)findViewById(R.id.psekiplideri3);
        psSSKDurumu=(TextView)findViewById(R.id.pssskdurumu);
        psGorev=(TextView)findViewById(R.id.psgorev);
        psTC=(EditText)findViewById(R.id.pstc);
        pssgkevrak=(TextView)findViewById(R.id.pssgkevrak);
        layoutPS=(RelativeLayout) findViewById(R.id.layoutPS);
        btnPSBarkodOku=(Button)findViewById(R.id.psbarkodoku);
        btnPSBarkodOku.setOnClickListener(this);
        btnPSBarkodYeni=(Button)findViewById(R.id.psbarkodyeni);
        btnPSBarkodYeni.setOnClickListener(this);
        layoutPSFoto=(LinearLayout)findViewById(R.id.layoutPSFoto);
        psFoto=(ImageView)findViewById(R.id.psFoto);
        imgChangeKartno=(ImageView)findViewById(R.id.imgChangeKartno);
        imgChangeKartno.setVisibility(View.GONE);
        imgCancelKartno=(ImageView)findViewById(R.id.imgCancelKartno);
        imgCancelKartno.setVisibility(View.GONE);
        layoutPSFotoEmpty=(LinearLayout)findViewById(R.id.layoutPSFotoEmpty);
        layoutPSFotoEmpty.setVisibility(View.GONE);
        imgAddFoto=(ImageView)findViewById(R.id.addFoto);
        imgAddFoto.setOnClickListener(this);
        layoutPSNufusFoto=(LinearLayout)findViewById(R.id.layoutPSNufusFoto);
        layoutPSNufusFoto.setVisibility(View.GONE);
        nufusFoto1=(ImageView)findViewById(R.id.nufusFoto1);
        nufusFoto1.setOnClickListener(this);
        nufusFoto2=(ImageView)findViewById(R.id.nufusFoto2);
        nufusFoto2.setOnClickListener(this);
        psNufusIptalet=(Button)findViewById(R.id.psNufusIptalet);
        psNufusIptalet.setOnClickListener(this);
        psNufusKaydet=(Button)findViewById(R.id.psNufusKaydet);
        psNufusKaydet.setOnClickListener(this);
        nufusKaydedildi="";
        //----

        //Puantaj Listeleme
        //svScrollPL=(ScrollView)findViewById(R.id.svScrollPL);
        //layoutPL=(LinearLayout)findViewById(R.id.layoutPL);

        //spnplUrun=(Spinner)findViewById(R.id.plurun);
        //----

        //İşe Başlama
        //svScrollIBas=(ScrollView)findViewById(R.id.svScrollIBas);
        //---

        //İş Bitirme
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        layoutIBit=(LinearLayout)findViewById(R.id.layoutIBit);
        btnIBitBarkodOku=(Button)findViewById(R.id.ibitbarkodoku);
        btnIBitBarkodOku.setOnClickListener(this);
        btnIBitOnay=(Button)findViewById(R.id.ibitonay);
        btnIBitOnay.setEnabled(false);
        btnIBitOnay.setOnClickListener(this);
        ibitBarkod=(TextView)findViewById(R.id.ibitbarkod);
        ibitBolge=(TextView)findViewById(R.id.ibitbolge);
        ibitCalisma=(TextView)findViewById(R.id.ibitcalisma);
        ibitFirma=(TextView)findViewById(R.id.ibitfirma);
        ibitYetkili=(TextView)findViewById(R.id.ibityetkili);
        ibitEkiplideri=(TextView)findViewById(R.id.ibitekiplideri);
        ibitBasTarih=(TextView)findViewById(R.id.ibitbastarih);
        ibitBitTarih=(TextView)findViewById(R.id.ibitbittarih);
        ibitBitTarih.setText(day+" / "+month+" / "+year);
        ibitBitTarih.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        ibitFisno=(TextView)findViewById(R.id.ibitfisno);
        ibitGorev=(TextView)findViewById(R.id.ibitgorev);
        ibitUrun=(TextView)findViewById(R.id.ibiturun);
        //---


        //Sync
        /*svScrollSync=(ScrollView)findViewById(R.id.svScrollSync);
        layoutSync=(LinearLayout)findViewById(R.id.layoutSync);
        btnSDFromSync=(Button)findViewById(R.id.btnSDFromSync);
        btnSDFromSync.setOnClickListener(this);
        txtSDFromServerTime=(TextView)findViewById(R.id.txtSDFromServerTime);
        btnSDToSyncPersonel=(Button)findViewById(R.id.btnSDToSyncPersonel);
        btnSDToSyncPersonel.setOnClickListener(this);
        btnSDToSyncPuantaj=(Button)findViewById(R.id.btnSDToSyncPuantaj);
        btnSDToSyncPuantaj.setOnClickListener(this);
        btnSDToSyncTumu=(Button)findViewById(R.id.btnSDToSyncTumu);
        btnSDToSyncTumu.setOnClickListener(this);
        txtSDToServerTimePersonel=(TextView)findViewById(R.id.txtSDToServerTimePersonel);
        txtSDToServerTimePuantaj=(TextView)findViewById(R.id.txtSDToServerTimePuantaj);
        txtSDToServerInfo=(TextView)findViewById(R.id.txtSDToServerInfo);*/
        //---

        //Bilgileri Al
        //btnSyncAl=(Button)findViewById(R.id.buttonSyncAl);
        //btnSyncAl.setOnClickListener(this);
        //--

        //formatTxt = (TextView)findViewById(R.id.scan_format);
        //contentTxt = (TextView)findViewById(R.id.scan_content);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        chkSifremiHatirla=(CheckBox)findViewById(R.id.chkSifremiHatirla);
        btnSifremiUnuttum=(TextView)findViewById(R.id.btnSifremiUnuttum);
        btnSifremiUnuttum.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        db=new Database(getApplicationContext());
        HashMap<String,String> res=db.getOneRow(new String[]{"USERNAME", "REMPASS", "PASSWORDTXT"}, "users", "");
        if(res.size()>0){
            String usrn=res.get("USERNAME");
            String[] usrn2=usrn.split("@");
            username.setText((usrn.length()>0?usrn2[0]:""));

            if(res.get("REMPASS").equals("1")){
                chkSifremiHatirla.setChecked(true);
                AESCrypt aes=new AESCrypt();
                password.setText(aes.decryption(res.get("PASSWORDTXT")));
            }

            password.requestFocus();
        }
        username.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction()==KeyEvent.ACTION_DOWN && keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER){
                    password.requestFocus();
                }
                return false;
            }
        });

        //password.setText("b2017gg");
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction()==KeyEvent.ACTION_DOWN && keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER){
                    loginBtn.callOnClick();
                }
                return false;
            }
        });


        loginLayout=(ViewGroup)findViewById(R.id.loginLayout);
        btnPSorgulama.setOnClickListener(this);
        btnPuantajListeleme.setOnClickListener(this);
        btnIseBaslama.setOnClickListener(this);
        //btnSync.setOnClickListener(this);


        AidcManager.create(this, new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = manager.createBarcodeReader();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            // close BarcodeReader to clean up resources.
            barcodeReader.close();
            barcodeReader = null;
        }

        if (manager != null) {
            // close AidcManager to disconnect from the scanner service.
            // once closed, the object can no longer be used.
            manager.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("Permission","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    public void onBackPressed() {
        //Display alert message when back button has been pressed
        if(activeButton!=""){
                llayoutPersonel.setVisibility(View.VISIBLE);
                llayoutWaitNSent.setVisibility(View.GONE);
                //llayoutSync.setVisibility(View.VISIBLE);
                llayoutDuyuru.setVisibility(View.VISIBLE);
                btnPersonelEkle.setVisibility(View.VISIBLE);
                btnPSorgulama.setVisibility(View.VISIBLE);
                btnPuantajListeleme.setVisibility(View.VISIBLE);
                btnIseBaslama.setVisibility(View.VISIBLE);
                //btnSync.setVisibility(View.VISIBLE);
                //btnSyncAl.setVisibility(View.VISIBLE);
                btnPuantajSent.setVisibility(View.VISIBLE);
                btnPuantajWait.setVisibility(View.VISIBLE);
                menuu.findItem(R.id.homebtn).setVisible(false);
                //layoutIBas.setVisibility(View.GONE);
                layoutIBit.setVisibility(View.GONE);
                layoutPS.setVisibility(View.GONE);
                //layoutPL.setVisibility(View.GONE);
                svScroll.setVisibility(View.GONE);
                //svScrollSync.setVisibility(View.GONE);
                //svScrollPL.setVisibility(View.GONE);
                //svScrollIBas.setVisibility(View.GONE);
                temizle();
                activeButton = "";
        }else{
            if(exitApp==3){
                new ShowToast(this, "Uygulamadan çıkmak için 2 kez geri tuşuna basınız");;
            }else if(exitApp==1){
                finish();
            }
            exitApp--;
        }
        Log.w("logBack",activeButton);
        return;
    }

    public void onClick(View v){
        if(v.getId()==R.id.buttonPersonelSorgulama){
            llayoutPersonel.setVisibility(View.GONE);
            //llayoutSync.setVisibility(View.GONE);
            llayoutWaitNSent.setVisibility(View.GONE);
            llayoutDuyuru.setVisibility(View.GONE);
            btnPuantajListeleme.setVisibility(View.GONE);
            btnPersonelEkle.setVisibility(View.GONE);
            btnIseBaslama.setVisibility(View.GONE);
            //btnSync.setVisibility(View.GONE);
            //btnSyncAl.setVisibility(View.GONE);
            btnPuantajSent.setVisibility(View.GONE);
            btnPuantajWait.setVisibility(View.GONE);
            menuu.findItem(R.id.homebtn).setVisible(true);
            btnPSorgulama.setVisibility(View.GONE);
            //layoutIBas.setVisibility(View.GONE);
            layoutIBit.setVisibility(View.GONE);
            //layoutPL.setVisibility(View.GONE);
            layoutPS.setVisibility(View.VISIBLE);
            //layoutSync.setVisibility(View.GONE);
            svScroll.setVisibility(View.VISIBLE);
            //svScrollPL.setVisibility(View.GONE);
            //svScrollIBas.setVisibility(View.GONE);
            //svScrollSync.setVisibility(View.GONE);

            activeButton="PSorgula";
            /*IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();*/
            psKartno.setText("");
            psBarkod.setText("");
            psTC.setText("");
            psAd.setText("");
            psSoyad.setText("");
            psDogumTarihi.setText("");
            psCinsiyet.setText("");
            psGorev.setText("");
            psBolge.setText("");
            psBolge2.setText("");
            psBolge3.setText("");
            psBolge4.setText("");
            psBolge5.setText("");
            psEkiplideri.setText("");
            psEkiplideri2.setText("");
            psEkiplideri3.setText("");
            pssgkevrak.setText("");
            psSSKDurumu.setText("");
            layoutPSFoto.setVisibility(View.GONE);
            layoutPSFotoEmpty.setVisibility(View.GONE);
            layoutPSNufusFoto.setVisibility(View.GONE);
            imgChangeKartno.setVisibility(View.GONE);
            imgCancelKartno.setVisibility(View.GONE);
            sorgulananSicilno="";
            psNewFoto=null;
            if(nufusKaydedildi.equals("")) {
                if (psNufus1 != null && psNufus1.exists())
                    psNufus1.delete();
                if (psNufus2 != null && psNufus2.exists())
                    psNufus2.delete();
            }
            psNufus1=null;
            psNufus2=null;
            nufus1Filename="";
            nufus2Filename="";
            nufusKaydedildi="";
            nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
            nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
            psNufusKaydet.setVisibility(View.GONE);
            psNufusIptalet.setVisibility(View.GONE);
        }else if(v.getId()==R.id.buttonPersonelEkle){
            Intent i = new Intent(getApplicationContext(),PersonelEkle.class);
            i.putExtra("userid", userid);
            startActivityForResult(i, REQUEST_PE);
            activeButton="PE";


            /*IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();*/
        }else if(v.getId()==R.id.buttonPuantajListeleme){
            if(userPuantajYetki.equals("1")) {
                Intent i = new Intent(getApplicationContext(),PuantajListele.class);
                i.putExtra("userid", userid);
                startActivityForResult(i, REQUEST_PL);
                activeButton="GPuantaj";
            }else{
                new ShowToast(this, "Puantaj yetkiniz bulunmamaktadır.");
            }

            /*IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();*/
        }else if(v.getId()==R.id.buttonIseBaslama){
            if(userPuantajYetki.equals("1")) {
                Intent i = new Intent(getApplicationContext(), GunlukPuantaj.class);
                i.putExtra("userid", userid);
                startActivityForResult(i, REQUEST_GP2);
            }else{
                new ShowToast(this, "Puantaj yetkiniz bulunmamaktadır.");
            }
        /*}else if(v.getId()==R.id.buttonSync){
            llayoutPersonel.setVisibility(View.GONE);
            llayoutSync.setVisibility(View.GONE);
            llayoutWaitNSent.setVisibility(View.GONE);
            llayoutDuyuru.setVisibility(View.GONE);
            btnPSorgulama.setVisibility(View.GONE);
            btnIseBaslama.setVisibility(View.GONE);
            //btnSync.setVisibility(View.GONE);
            //btnSyncAl.setVisibility(View.GONE);
            btnPuantajSent.setVisibility(View.GONE);
            btnPuantajWait.setVisibility(View.GONE);
            btnPersonelEkle.setVisibility(View.GONE);
            menuu.findItem(R.id.homebtn).setVisible(true);
            btnPuantajListeleme.setVisibility(View.GONE);


            //layoutIBas.setVisibility(View.GONE);
            layoutIBit.setVisibility(View.GONE);
            layoutPS.setVisibility(View.GONE);
            layoutSync.setVisibility(View.VISIBLE);
            svScroll.setVisibility(View.GONE);
            //svScrollPL.setVisibility(View.GONE);
            //svScrollIBas.setVisibility(View.GONE);
            svScrollSync.setVisibility(View.VISIBLE);

            db=new Database(getApplicationContext());
            HashMap<String,String> dbstat=db.getDBStatus("fromserver", MainActivity.userid);

            for (String key: dbstat.keySet()){
                Log.w("dbstat",key+" "+dbstat.get(key));
            }
            txtSDFromServerTime.setText(dbstat.get("personel"));

            //btnSDToSync.performClick();

            dbstat=db.getDBStatus("toserverpuantaj", MainActivity.userid);
            if(dbstat.size()>0)
                txtSDToServerTimePuantaj.setText(dbstat.get("pushdata"));

            dbstat=db.getDBStatus("toserverpersonel", MainActivity.userid);
            if(dbstat.size()>0)
                txtSDToServerTimePersonel.setText(dbstat.get("pushdata"));

            loadSyncView();

            activeButton="buttonSync";
        }else if(v.getId()==R.id.buttonSyncAl){
            llayoutPersonel.setVisibility(View.GONE);
            llayoutSync.setVisibility(View.GONE);
            llayoutWaitNSent.setVisibility(View.GONE);
            llayoutDuyuru.setVisibility(View.GONE);
            btnPSorgulama.setVisibility(View.GONE);
            btnIseBaslama.setVisibility(View.GONE);
            //btnSync.setVisibility(View.GONE);
            //btnSyncAl.setVisibility(View.GONE);
            btnPuantajSent.setVisibility(View.GONE);
            btnPuantajWait.setVisibility(View.GONE);
            btnPersonelEkle.setVisibility(View.GONE);
            menuu.findItem(R.id.homebtn).setVisible(true);
            btnPuantajListeleme.setVisibility(View.GONE);


            //layoutIBas.setVisibility(View.GONE);
            layoutIBit.setVisibility(View.GONE);
            layoutPS.setVisibility(View.GONE);
            layoutSync.setVisibility(View.VISIBLE);
            svScroll.setVisibility(View.GONE);
            //svScrollPL.setVisibility(View.GONE);
            //svScrollIBas.setVisibility(View.GONE);
            svScrollSync.setVisibility(View.VISIBLE);

            db=new Database(getApplicationContext());
            HashMap<String,String> dbstat=db.getDBStatus("fromserver", MainActivity.userid);

            for (String key: dbstat.keySet()){
                Log.w("dbstat",key+" "+dbstat.get(key));
            }
            if(dbstat.size()!=db.DBSYNC_TABLES) {
                btnSDFromSync.performClick();
            }else{
                txtSDFromServerTime.setText(dbstat.get("personel"));
            }

            dbstat=db.getDBStatus("toserverpuantaj", MainActivity.userid);
            if(dbstat.size()>0)
                txtSDToServerTimePuantaj.setText(dbstat.get("pushdata"));

            dbstat=db.getDBStatus("toserverpersonel", MainActivity.userid);
            if(dbstat.size()>0)
                txtSDToServerTimePersonel.setText(dbstat.get("pushdata"));

            loadSyncView();

            activeButton="buttonSync";*/
        }else if(v.getId()==R.id.loginBtn){
            username.setText(username.getText().toString().trim().toLowerCase());
            try {
                if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                    AESCrypt aes=new AESCrypt();
                    
                    serverQueue = Volley.newRequestQueue(this);
                    HashMap<String, String> params=new HashMap<String, String>();
                    params.put("token","6ce304f73ce841efaf1490bb98474eef");
                    params.put("op","us");
                    params.put("u",username.getText().toString());
                    params.put("p",Security.generateMD5(password.getText().toString()));
                    params.put("ttt",""+System.currentTimeMillis());

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            this.getResources().getString(R.string.serviceUrl), new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //Success Callback
                                    Log.w("volleyUser",response.toString());
                                    boolean userStat=false;
                                    try {
                                        JSONArray values = response.getJSONArray("users");
                                        for (int i = 0; i < values.length(); i++) {
                                            JSONObject c = values.getJSONObject(i);
                                            AESCrypt aes=new AESCrypt();

                                            HashMap<String, String> tmp = new HashMap<String, String>();
                                            MainActivity.userid = c.getString("id");
                                            MainActivity.userPuantajYetki = c.getString("puantaj_yetki");
                                            tmp.put("id", c.getString("id"));
                                            tmp.put("username", c.getString("username"));
                                            tmp.put("password", c.getString("password"));
                                            tmp.put("passwordtxt", (chkSifremiHatirla.isChecked() ? aes.encryption(password.getText().toString()) : ""));
                                            tmp.put("name", c.getString("name"));
                                            tmp.put("rempass", chkSifremiHatirla.isChecked() ? "1" : "0");
                                            tmp.put("puantaj_yetki", c.getString("puantaj_yetki"));
                                            db.resetTables();

                                            db.usersEkle(tmp);

                                            wvLayout.setVisibility(View.VISIBLE);
                                            txtBilgilendirme.setText("");
                                            //txtsyncInfo.setText(getSyncData());
                                            loginLayout.setVisibility(View.GONE);
                                            llayoutPersonel.setVisibility(View.VISIBLE);
                                            //llayoutSync.setVisibility(View.VISIBLE);
                                            llayoutWaitNSent.setVisibility(View.GONE);
                                            llayoutDuyuru.setVisibility(View.VISIBLE);
                                            btnPSorgulama.setVisibility(View.VISIBLE);
                                            btnPuantajListeleme.setVisibility(View.VISIBLE);
                                            btnIseBaslama.setVisibility(View.VISIBLE);
                                            btnPuantajSent.setVisibility(View.VISIBLE);
                                            btnPuantajWait.setVisibility(View.VISIBLE);
                                            btnPersonelEkle.setVisibility(View.VISIBLE);

                                            layoutIBit.setVisibility(View.GONE);
                                            layoutPS.setVisibility(View.GONE);
                                            //layoutSync.setVisibility(View.GONE);
                                            svScroll.setVisibility(View.GONE);
                                            //svScrollSync.setVisibility(View.GONE);
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(loginBtn.getWindowToken(),
                                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                        }
                                    }catch (Exception ex){
                                        Log.w("Error",ex.getMessage());
                                        new ShowToast(MainActivity.mactivity, "Kullanıcı/Şifre Hatası");
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
                                        message = "Kullanıcı/Şifre Hatası";
                                    }
                                    new ShowToast(MainActivity.mactivity, message);
                                }
                            });


// Add the request to the RequestQueue.
                    serverQueue.add(jsonObjReq);


                }else{
                    new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                }
            }catch (Exception ex){
                new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
            }

            /*db=new Database(gtry {
                if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                    AESCrypt aes=new AESCrypt();

                    RequestQueue queue = Volley.newRequestQueue(this);
                    HashMap<String, String> params=new HashMap<String, String>();
                    params.put("token","6ce304f73ce841efaf1490bb98474eef");
                    params.put("op","us");
                    params.put("u",username.getText().toString());
                    params.put("p",Security.generateMD5(password.getText().toString()));
                    params.put("ttt",""+System.currentTimeMillis());

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            this.getResources().getString(R.string.serviceUrl), new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //Success Callback
                                    Log.w("volleyUser",response.toString());
                                    boolean userStat=false;
                                    try {
                                        JSONArray values = response.getJSONArray("users");
                                        for (int i = 0; i < values.length(); i++) {
                                            JSONObject c = values.getJSONObject(i);
                                            AESCrypt aes=new AESCrypt();

                                            HashMap<String, String> tmp = new HashMap<String, String>();
                                            MainActivity.userid = c.getString("id");
                                            MainActivity.userPuantajYetki = c.getString("puantaj_yetki");
                                            tmp.put("id", c.getString("id"));
                                            tmp.put("username", c.getString("username"));
                                            tmp.put("password", c.getString("password"));
                                            tmp.put("passwordtxt", (chkSifremiHatirla.isChecked() ? aes.encryption(password.getText().toString()) : ""));
                                            tmp.put("name", c.getString("name"));
                                            tmp.put("rempass", chkSifremiHatirla.isChecked() ? "1" : "0");
                                            tmp.put("puantaj_yetki", c.getString("puantaj_yetki"));
                                            db.resetTables();

                                            db.usersEkle(tmp);

                                            wvLayout.setVisibility(View.VISIBLE);
                                            txtBilgilendirme.setText("");
                                            //txtsyncInfo.setText(getSyncData());
                                            loginLayout.setVisibility(View.GONE);
                                            llayoutPersonel.setVisibility(View.VISIBLE);
                                            //llayoutSync.setVisibility(View.VISIBLE);
                                            llayoutWaitNSent.setVisibility(View.GONE);
                                            llayoutDuyuru.setVisibility(View.VISIBLE);
                                            btnPSorgulama.setVisibility(View.VISIBLE);
                                            btnPuantajListeleme.setVisibility(View.VISIBLE);
                                            btnIseBaslama.setVisibility(View.VISIBLE);
                                            btnPuantajSent.setVisibility(View.VISIBLE);
                                            btnPuantajWait.setVisibility(View.VISIBLE);
                                            btnPersonelEkle.setVisibility(View.VISIBLE);

                                            layoutIBit.setVisibility(View.GONE);
                                            layoutPS.setVisibility(View.GONE);
                                            //layoutSync.setVisibility(View.GONE);
                                            svScroll.setVisibility(View.GONE);s
                                            //svScrollSync.setVisibility(View.GONE);
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(loginBtn.getWindowToken(),
                                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                        }
                                    }catch (Exception ex){
                                        Log.w("Error",ex.getMessage());
                                        new ShowToast(MainActivity.mactivity, "Kullanıcı/Şifre Hatası");
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
                                        message = "Kullanıcı/Şifre Hatası";
                                    }
                                    new ShowToast(MainActivity.mactivity, message);
                                }
                            });


// Add the request to the RequestQueue.
                    queue.add(jsonObjReq);


                }else{
                    new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                }
            }catch (Exception ex){
                new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
            }etApplicationContext());
            HashMap<String, String> r=db.getOneRow(new String[]{"id"}, "users","OID>0");
            if(r.get("id")!=null) {
                r = db.getOneRow(new String[]{"id"}, "users", "USERNAME='" + username.getText().toString() + "@guleryuzgroup.com'");
                if(r.get("id")!=null) {
                    r = db.getOneRow(new String[]{"id", "puantaj_yetki"}, "users", "USERNAME='" + username.getText().toString() + "@guleryuzgroup.com' AND PASSWORD='" + Security.generateMD5(password.getText().toString()) + "'");
                    if (r.get("id") != null) {
                        //if(username.getText().toString().equals("com.guleryuz") && password.getText().toString().equals("12345")){
                            Log.w("userid",r.get("puantaj_yetki").toString());

                            userid = r.get("id").toString();
                            userPuantajYetki = r.get("puantaj_yetki").toString();
                            //sifre hatırlama
                            if(chkSifremiHatirla.isChecked()){
                                AESCrypt aes=new AESCrypt();
                                db.usersGuncelle(aes.encryption(password.getText().toString()), "1", userid);
                            }else{
                                db.usersGuncelle("", "0", userid);
                            }
                            //Log.w("uid",userid);

                            wvLayout.setVisibility(View.VISIBLE);
                            txtsyncInfo.setText(getSyncData());
                            loginLayout.setVisibility(View.GONE);
                            llayoutPersonel.setVisibility(View.VISIBLE);
                            llayoutSync.setVisibility(View.VISIBLE);
                            llayoutWaitNSent.setVisibility(View.VISIBLE);
                            llayoutDuyuru.setVisibility(View.VISIBLE);
                            btnPSorgulama.setVisibility(View.VISIBLE);
                            btnPuantajListeleme.setVisibility(View.VISIBLE);
                            btnIseBaslama.setVisibility(View.VISIBLE);
                            //btnSync.setVisibility(View.VISIBLE);
                            //btnSyncAl.setVisibility(View.VISIBLE);
                            btnPuantajSent.setVisibility(View.VISIBLE);
                            btnPuantajWait.setVisibility(View.VISIBLE);
                            btnPersonelEkle.setVisibility(View.VISIBLE);

                            //layoutIBas.setVisibility(View.GONE);
                            layoutIBit.setVisibility(View.GONE);
                            layoutPS.setVisibility(View.GONE);
                            //layoutPL.setVisibility(View.GONE);
                            layoutSync.setVisibility(View.GONE);
                            svScroll.setVisibility(View.GONE);
                            //svScrollPL.setVisibility(View.GONE);
                            //.setVisibility(View.GONE);
                            svScrollSync.setVisibility(View.GONE);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(loginBtn.getWindowToken(),
                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            //HashMap<String, String>  dbstat=db.getDBStatus("fromserver", MainActivity.userid);
                            //Log.w("synctable",""+dbstat.size());
                            //if(dbstat.size()!=db.DBSYNC_TABLES) {
                            //    btnSyncAl.performClick();
                            //}
                    } else {
                        new ShowToast(this, "Kullanıcı/Şifre Hatası");
                    }
                }else{
                    Connectivity conn=new Connectivity();
                    try {
                        if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {

                            Users users = new Users(this);
                            users.context = this;
                            users.db = db;
                            users.u = username.getText().toString();
                            users.p = Security.generateMD5(password.getText().toString());
                            users.execute();
                        }else{
                            new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                        }
                    }catch (Exception ex){
                        new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                    }
                }
            }else{
                Connectivity conn=new Connectivity();
                try {
                    if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {

                        Users users = new Users(this);
                        users.context = this;
                        users.db = db;
                        users.u = username.getText().toString();
                        users.p = Security.generateMD5(password.getText().toString());
                        users.execute();
                    }else{
                        new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                    }
                }catch (Exception ex){
                    new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                }
            }*/
        }else if(v.getId()==R.id.psbarkodoku){
            activeButton="PSorgula";
            if(psKartno.getText().toString().length()==0 && psBarkod.getText().toString().length()==0 && psTC.getText().toString().length()==0&& psAd.getText().toString().length()==0&& psSoyad.getText().toString().length()==0) {
                try {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                    scanIntegrator.initiateScan();
                }catch (Exception ex){
                    Log.w("Barcode", "nothing");
                }
            }else{
                String kartnowithpad=psKartno.getText().toString();
                int padToLength=6;
                if(kartnowithpad.length()<padToLength){
                    kartnowithpad=String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s",0,kartnowithpad);
                }

                getPersonelInfo(psTC.getText().toString(), psAd.getText().toString(), psSoyad.getText().toString(), kartnowithpad, psBarkod.getText().toString());


                /*try {
                    String kartnowithpad=psKartno.getText().toString();
                    int padToLength=6;
                    if(kartnowithpad.length()<padToLength){
                        kartnowithpad=String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s",0,kartnowithpad);
                    }

                    if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                        AESCrypt aes=new AESCrypt();

                        PersonelOnline personel = new PersonelOnline(this);
                        personel.context = this;
                        personel.db = db;
                        personel.tc =psTC.getText().toString();
                        personel.a =psAd.getText().toString();
                        personel.s =psSoyad.getText().toString();
                        personel.kn=kartnowithpad;
                        personel.sc= psBarkod.getText().toString();
                        personel.uid =userid;
                        personel.execute();
                    }else{
                        new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                    }
                }catch (Exception ex){
                    new ShowToast(this, "İnternet Bağlantınızı Kontrol Ediniz.");
                }*/

                /*HashMap<String, String> personelbilgileri = db.personelBilgileriGetir(kartnowithpad, psBarkod.getText().toString(), psTC.getText().toString(), "", psAd.getText().toString(), psSoyad.getText().toString());
                if(personelbilgileri.size()>0) {
                    imgChangeKartno.setVisibility(View.VISIBLE);
                    imgChangeKartno.setOnClickListener(this);

                    kartnowithpad=personelbilgileri.get("KARTNO");
                    if(!kartnowithpad.equals("") && kartnowithpad.length()<padToLength){
                        kartnowithpad=String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s",0,kartnowithpad);
                    }
                    psKartno.setText((personelbilgileri.get("KARTNO").equals("0")?"":kartnowithpad));

                    if(!psKartno.getText().toString().equals("")) {
                        imgCancelKartno.setVisibility(View.VISIBLE);
                        imgCancelKartno.setOnClickListener(this);
                    }else{
                        imgCancelKartno.setVisibility(View.GONE);
                        imgCancelKartno.setOnClickListener(this);
                    }

                    sorgulananSicilno=personelbilgileri.get("ID");
                    psBarkod.setText(personelbilgileri.get("ID"));
                    psTC.setText(personelbilgileri.get("TC"));
                    psAd.setText(personelbilgileri.get("AD"));
                    psSoyad.setText(personelbilgileri.get("SOYAD"));
                    psDogumTarihi.setText(personelbilgileri.get("DOGUMTARIHI"));
                    psCinsiyet.setText(personelbilgileri.get("CINSIYET"));
                    psGorev.setText(personelbilgileri.get("GOREV"));
                    psBolge.setText(personelbilgileri.get("BOLGE"));
                    psBolge2.setText(personelbilgileri.get("BOLGE2"));
                    psBolge3.setText(personelbilgileri.get("BOLGE3"));
                    psBolge4.setText(personelbilgileri.get("BOLGE4"));
                    psBolge5.setText(personelbilgileri.get("BOLGE5"));
                    psEkiplideri.setText(personelbilgileri.get("EKIP_LIDERI"));
                    psEkiplideri2.setText(personelbilgileri.get("EKIP_LIDERI2"));
                    psEkiplideri3.setText(personelbilgileri.get("EKIP_LIDERI3"));
                    Log.w("personelbilgi", personelbilgileri.get("SSK"));
                    Log.w("here",personelbilgileri.get("RESIM_INDIRILDI"));
                    layoutPSNufusFoto.setVisibility(View.VISIBLE);
                    psNewFoto=null;
                    if(nufusKaydedildi.equals("")) {
                        if (psNufus1 != null && psNufus1.exists())
                            psNufus1.delete();
                        if (psNufus2 != null && psNufus2.exists())
                            psNufus2.delete();
                    }
                    psNufus1=null;
                    psNufus2=null;
                    nufus1Filename="";
                    nufus2Filename="";
                    nufusKaydedildi="";
                    nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                    nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                    psNufusKaydet.setVisibility(View.GONE);
                    psNufusIptalet.setVisibility(View.GONE);

                    ArrayList<HashMap<String, String>> personelnufusbelge = db.getMultiResult(new String[]{"RESIMADI"}, "tarim_istakip_personel_belge", "user_id='" + MainActivity.userid + "' and personelid='"+sorgulananSicilno+"' and tur='19'");
                    if(personelnufusbelge.size()==2){
                        nufusKaydedildi="ok";
                        nufus1Filename=personelnufusbelge.get(0).get("RESIMADI");
                        nufus2Filename=personelnufusbelge.get(1).get("RESIMADI");
                        nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                        nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                        psNufus1 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + nufus1Filename);
                        psNufus2 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + nufus2Filename);
                    }

                    try {
                        if (personelbilgileri.get("RESIM_INDIRILDI").equals("1")) {
                            imgFile = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + personelbilgileri.get("RESIM"));
                            if (imgFile.exists()) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

                                psFoto.requestLayout();

                                psFoto.setImageBitmap(myBitmap);
                                psFoto.setOnClickListener(this);
                                layoutPSFoto.setVisibility(View.VISIBLE);
                                layoutPSFotoEmpty.setVisibility(View.GONE);
                            } else {
                                layoutPSFoto.setVisibility(View.GONE);
                                layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                                psFoto.setImageBitmap(null);
                            }
                        } else {
                            imgFile = null;
                            layoutPSFoto.setVisibility(View.GONE);
                            layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                            psFoto.setImageBitmap(null);
                        }
                    }catch (Exception ex){
                        imgFile = null;
                        layoutPSFoto.setVisibility(View.GONE);
                        layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                        psFoto.setImageBitmap(null);
                    }


                    Log.w("here",personelbilgileri.get("RESIM_INDIRILDI"));
                    if (personelbilgileri.get("SGK_EVRAK_INDIRILDI").equals("1")) {
                        SGK_Evrak=personelbilgileri.get("SGK_EVRAK");
                        pssgkevrak.setText("Var. (Görüntüle)");
                        pssgkevrak.setOnClickListener(this);
                    }else{
                        SGK_Evrak="";
                        pssgkevrak.setOnClickListener(null);
                        if(personelbilgileri.containsKey("SGK_EVRAK") && (personelbilgileri.get("SGK_EVRAK")==null || personelbilgileri.get("SGK_EVRAK").equals(""))){
                            pssgkevrak.setText("Yok");
                        }else{
                            pssgkevrak.setText("Evrak var. İndirilmemiş.");
                        }
                    }
                }else{
                    new ShowToast(this, "Personel bilgisi bulunmadı.");
                    }*/
            }
        }else if(v.getId()==R.id.psbarkodyeni) {
            psBarkod.setText("");
            psKartno.setText("");
            psTC.setText("");
            psAd.setText("");
            psSoyad.setText("");
            psDogumTarihi.setText("");
            psCinsiyet.setText("");
            psGorev.setText("");
            psBolge.setText("");
            psBolge2.setText("");
            psBolge3.setText("");
            psBolge4.setText("");
            psBolge5.setText("");
            psEkiplideri.setText("");
            psEkiplideri2.setText("");
            psEkiplideri3.setText("");
            pssgkevrak.setText("");
            psSSKDurumu.setText("");
            layoutPSFoto.setVisibility(View.GONE);
            layoutPSFotoEmpty.setVisibility(View.GONE);
            layoutPSNufusFoto.setVisibility(View.GONE);
            psFoto.setImageBitmap(null);
            imgChangeKartno.setVisibility(View.GONE);
            imgCancelKartno.setVisibility(View.GONE);
            sorgulananSicilno="";
            imgFile="";
        } else if(v.getId()==R.id.imgChangeKartno){
            activeButton="imgChangeKartno";
            AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
            builder.setTitle("Uyarı");
            builder.setMessage("Kart No "+(psKartno.getText().toString().equals("") ?"eklemek":"değiştirmek")+" istediğinizden emin misiniz?");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    try {
                        IntentIntegrator scanIntegrator = new IntentIntegrator(mactivity);
                        scanIntegrator.initiateScan();
                    }catch (Exception ex){
                        Log.w("Barcode", "nothing");
                    }
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
        } else if(v.getId()==R.id.imgCancelKartno){
            barkodIslem = "";
            barkodIcerik = "";
            AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
            builder.setTitle("Uyarı");
            builder.setMessage("Kart No "+psKartno.getText().toString()+" silmek istediğinizden emin misiniz?");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String scanContent=psKartno.getText().toString();
                        barkodIslem = "iptal";
                        barkodIcerik = scanContent;
                        DataFromService kartno = new DataFromService((ServiceCallBack) mactivity);
                        kartno.context=ParentCtxt;
                        kartno.uid=userid;
                        kartno.reqtype="KartNoIptal";
                        kartno.title="Kart No Güncelle";
                        kartno.reqparam=new ArrayList<String[]>();
                        kartno.reqparam.add(new String[]{"op", "persor"});
                        kartno.reqparam.add(new String[]{"kartno", scanContent});
                        kartno.reqparam.add(new String[]{"sicilno", psBarkod.getText().toString()});
                        kartno.reqparam.add(new String[]{"ad", ""});
                        kartno.reqparam.add(new String[]{"soyad", ""});
                        kartno.reqparam.add(new String[]{"tckimlik", ""});
                        kartno.resp=new ArrayList<String[]>();
                        kartno.resp.add(new String[]{"ID","id"});
                        kartno.execute();

                        //HashMap<String, String> kartno = db.getOneRow(new String[]{"ID", "AD", "SOYAD"}, "tarim_istakip_personel", "kartno='" + scanContent + "'");

                    }catch (Exception ex){
                        Log.w("Barcode", "nothing");
                    }
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

        }else if(v.getId()==R.id.ibitbarkodoku){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }else if(v.getId()==R.id.ibitonay){
            if (ibitBarkod.getText()!=""){
                String stat = db.isbitirmeOnay(ibitBarkod.getText().toString(), ibitBasTarih.getText().toString(), ibitBitTarih.getText().toString());
                if(stat=="ok"){
                    new ShowToast(this, "İş bitirme kaydı oluşturuldu");
                    btnIBitOnay.setEnabled(false);
                }else{
                    new ShowToast(this, "İş bitirme kayıt Hatası\n"+(stat.indexOf('-')>0?stat.substring(stat.indexOf('-')+1):""));
                }
            }else{
                new ShowToast(this, "Lütfen barkod okutunuz");
            }
        }else if(v.getId()==R.id.btnSDFromSync){
            db=new Database(getApplicationContext());

            Connectivity conn=new Connectivity();
            try {
                if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                    HashMap<String, String> r=db.getOneRow(new String[]{"id"}, "users","OID>0");
                    if(r.get("id")!=null) {
                        FirmaBolge firmabolge = new FirmaBolge(this);
                        firmabolge.db = db;
                        firmabolge.context = MainActivity.this;
                        firmabolge.uid =r.get("id").toString();
                        firmabolge.execute();

                        Firma firma = new Firma(this);
                        firma.context=MainActivity.this;
                        firma.db=db;
                        firma.uid =r.get("id").toString();
                        firma.execute();

                        Yetkili yetkili = new Yetkili(this);
                        yetkili.context=MainActivity.this;
                        yetkili.db=db;
                        yetkili.uid =r.get("id").toString();
                        yetkili.execute();

                        Ekiplideri ekiplideri=new Ekiplideri(this);
                        ekiplideri.context=MainActivity.this;
                        ekiplideri.db=db;
                        ekiplideri.uid =r.get("id").toString();
                        ekiplideri.execute();

                        Gorev gorev=new Gorev(this);
                        gorev.context=MainActivity.this;
                        gorev.db=db;
                        gorev.uid=r.get("id").toString();
                        gorev.execute();

                        Urun urun=new Urun(this);
                        urun.context=MainActivity.this;
                        urun.db=db;
                        urun.uid=r.get("id").toString();
                        urun.execute();

                        PersonelBelgeTur pbelgetur=new PersonelBelgeTur(this);
                        pbelgetur.context=MainActivity.this;
                        pbelgetur.db=db;
                        pbelgetur.uid=r.get("id").toString();
                        pbelgetur.execute();

                        Servis servis=new Servis(this);
                        servis.context=MainActivity.this;
                        servis.db=db;
                        servis.uid=r.get("id").toString();
                        servis.execute();

                        UsersUpdate usersupdate=new UsersUpdate(this);
                        usersupdate.context=MainActivity.this;
                        usersupdate.db=db;
                        usersupdate.uid=r.get("id").toString();
                        usersupdate.execute();

                        Personel personel=new Personel(this);
                        personel.context=MainActivity.this;
                        personel.db=db;
                        personel.uid=r.get("id").toString();
                        personel.execute();
                    }
                } else {
                    new ShowToast(this, R.string.msgInternetNoConnection);
                    Log.w("Manual Sync", "No connection");
                }
            }catch (Exception ex){
                new ShowToast(this, "Hata:"+ex.getMessage());
                Log.w("Manual Sync", ex.getMessage());
            }finally {
                //db.close();
            }
        }else if(v.getId()==R.id.btnSDToSyncPuantaj) {
            db=new Database(getApplicationContext());

            Connectivity conn=new Connectivity();
            try {
                if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                   HashMap<String, String> r=db.getOneRow(new String[]{"id"}, "users","OID>0");
                    if(r.get("id")!=null ) {
                        PushDataToServer pds = new PushDataToServer(this,"puantaj");
                        pds.db = db;
                        pds.context = MainActivity.this;
                        pds.uid =r.get("id").toString();
                        pds.execute();
                    }

                } else {
                    new ShowToast(this,R.string.msgInternetNoConnection);
                    Log.w("Manual Sync", "No connection");
                }
            }catch (Exception ex){
                new ShowToast(this, "Hata:"+ex.getMessage());
                Log.w("Manual Sync", ex.getMessage());
            }finally {
                ////db.close();
            }
        }else if(v.getId()==R.id.btnSDToSyncPersonel) {
            db=new Database(getApplicationContext());

            Connectivity conn=new Connectivity();
            try {
                if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                    HashMap<String, String> r=db.getOneRow(new String[]{"id"}, "users","OID>0");
                    if(r.get("id")!=null ) {
                        PushDataToServer pds = new PushDataToServer(this,"personel");
                        pds.db = db;
                        pds.context = MainActivity.this;
                        pds.uid =r.get("id").toString();
                        pds.execute();
                    }

                } else {
                    new ShowToast(this, R.string.msgInternetNoConnection);
                    Log.w("Manual Sync", "No connection");
                }
            }catch (Exception ex){
                new ShowToast(this,"Hata:"+ex.getMessage());
                Log.w("Manual Sync", ex.getMessage());
            }finally {
                ////db.close();
            }
        }else if(v.getId()==R.id.btnSDToSyncTumu) {
            db=new Database(getApplicationContext());

            Connectivity conn=new Connectivity();
            try {
                if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                    HashMap<String, String> r=db.getOneRow(new String[]{"id"}, "users","OID>0");
                    if(r.get("id")!=null ) {
                        PushDataToServer pds = new PushDataToServer(this,"tumu");
                        pds.db = db;
                        pds.context = MainActivity.this;
                        pds.uid =r.get("id").toString();
                        pds.execute();
                    }

                } else {
                    new ShowToast(this,R.string.msgInternetNoConnection);
                    Log.w("Manual Sync", "No connection");
                }
            }catch (Exception ex){
                new ShowToast(this, "Hata:"+ex.getMessage());
                Log.w("Manual Sync", ex.getMessage());
            }finally {
                ////db.close();
            }
        }else if(v.getId()==R.id.buttonWait){
                Intent intent = new Intent(getApplicationContext(), PuantajBekleyenGonderilenler.class);
                intent.putExtra("which", "Bekleyenler");
                startActivity(intent);
        }else if(v.getId()==R.id.buttonSent){
            Intent intent = new Intent(getApplicationContext(), PuantajBekleyenGonderilenler.class);
            intent.putExtra("which", "Gönderilenler");
            startActivity(intent);
        }else if(v.getId()==R.id.psFoto){
            if(imgFile!="") {
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", this.getResources().getString(R.string.docUrl) + imgFile);
                startActivity(intent);
            }
        }else if(v.getId()==R.id.pssgkevrak) {
            if (!SGK_Evrak.equals("")){
                if(SGK_Evrak.toLowerCase().indexOf(".pdf")>0){
                    //File sgkevrakFile = new  File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.sgkDir+"/"+SGK_Evrak);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(this.getResources().getString(R.string.docUrl) +SGK_Evrak), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    /*File sgkevrakFile = new  File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.sgkDir+"/"+SGK_Evrak);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(sgkevrakFile), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);*/
                }else{
                    Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                    intent.putExtra("photo", this.getResources().getString(R.string.docUrl) +SGK_Evrak);
                    startActivity(intent);

                    /*File sgkevrakFile = new  File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.sgkDir+"/"+SGK_Evrak);
                    if(sgkevrakFile.isFile()) {
                        Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                        intent.putExtra("photo", sgkevrakFile.getAbsolutePath());
                        startActivity(intent);
                    }else{
                        new ShowToast(this, "SGK Evrak dosyası bulunamadı");
                    }*/
                }
            }
        }else if(v.getId()==R.id.btnSifremiUnuttum){
            if (username.getText()!=null && !username.getText().toString().equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                builder.setTitle("Uyarı");
                builder.setMessage(username.getText().toString()+" kullanıcısı için Şifremi Değişimi yapılacaktır. Emin misiniz?");

                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        db = new Database(getApplicationContext());

                        Connectivity conn = new Connectivity();
                        try {
                            if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                                HashMap<String, String> r = db.getOneRow(new String[]{"id", "islem_tarihi", "status"}, "sifremi_unuttum", "username='"+username.getText().toString()+"'");
                                if (r.get("id") == null) {
                                    HashMap<String, String> tmp = new HashMap<String, String>();

                                    tmp.put("id", "");
                                    tmp.put("username", username.getText().toString());
                                    tmp.put("password", "");

                                    db.sifremiUnuttumEkle(tmp);

                                    SifremiUnuttum sifreunut = new SifremiUnuttum(MainActivity.this);
                                    sifreunut.context=MainActivity.this;
                                    sifreunut.db=db;
                                    sifreunut.u =username.getText().toString();
                                    sifreunut.execute();
                                }else{
                                    String sifremiunutsure = db.sifremiUnuttumSure();
                                    if (!sifremiunutsure.equals("") && Integer.parseInt(sifremiunutsure) > 5) {
                                        SifremiUnuttum sifreunut = new SifremiUnuttum(MainActivity.this);
                                        sifreunut.context = MainActivity.this;
                                        sifreunut.db = db;
                                        sifreunut.u = username.getText().toString();
                                        sifreunut.execute();
                                    } else {
                                        if(r.get("status").toString().equals("0")) {
                                            new ShowToast(ParentCtxt, "Şifremi unuttum'a önceden tıklamışsınız. Kısa süre içinde şifreniz gelmezse yöneticinizle irtibata geçiniz.");
                                        }else if(r.get("status").toString().equals("1")){
                                            new ShowToast(ParentCtxt, "Şifreniz kısa bir süre sonra kayıtlı cep telefonunuza sms olarak gönderilecektir.");
                                        }else{
                                            new ShowToast(ParentCtxt, "Kayıtlı gsm numaranız bulunamadı.");
                                        }
                                    }
                                }

                            } else {
                                new ShowToast(getApplicationContext(), R.string.msgInternetNoConnection);
                                Log.w("ForgotPass", "No connection");
                            }
                        } catch (Exception ex) {
                            new ShowToast(getApplicationContext(), "Hata:" + ex.getMessage());
                            Log.w("ForgotPass", ex.getMessage());
                        } finally {
                            ////db.close();
                        }
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
            }else{
                new ShowToast(this, "Kullanıcı adınızı girmeniz gerekmektedir.");
            }
        }else if(v.getId()==R.id.addFoto){//Personel Sorgulama Foto Ekleme
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "Foto_" + timeStamp + ".jpg";

                psNewFoto = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+ imageFileName);

                if (psNewFoto != null) {
                    Uri photoURI = Uri.fromFile(psNewFoto);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_PSTAKE_PHOTO);
                }
            }
        }else if(v.getId()==R.id.nufusFoto1){//Nüfus Fotokopi 1. yüz
            if(nufus1Filename.equals("")) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "Belgeler_" + timeStamp + ".jpg";

                    psNufus1 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + imageFileName);

                    if (psNufus1 != null) {
                        nufus1Filename = imageFileName;
                        Uri photoURI = Uri.fromFile(psNufus1);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_PSTAKE_NUFUS1);
                    }
                }
            }else{
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", this.getResources().getString(R.string.docUrl) + nufus1Filename);
                startActivity(intent);
            }
        }else if(v.getId()==R.id.nufusFoto2){//Nüfus Fotokopi 2. yüz
            if(nufus2Filename.equals("")) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "Belgeler_" + timeStamp + ".jpg";

                    psNufus2 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + imageFileName);

                    if (psNufus2 != null) {
                        nufus2Filename = imageFileName;
                        Uri photoURI = Uri.fromFile(psNufus2);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_PSTAKE_NUFUS2);
                    }
                }
            }else{
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", this.getResources().getString(R.string.docUrl) + nufus2Filename);
                startActivity(intent);
            }
        }else if(v.getId()==R.id.psNufusKaydet){
            try{
                if(!nufus1Filename.equals("") && !nufus2Filename.equals("")) {
                    HashMap<String, String> tmp = new HashMap<String, String>();
                    tmp.put("sicilno", "" + sorgulananSicilno);
                    tmp.put("belgetur", "19");//Nüfus Cüzdanı Fotokopisi
                    tmp.put("ad", nufus1Filename);
                    tmp.put("user_id", MainActivity.userid);
                    db.personelBelgeEkle(tmp);

                    tmp = new HashMap<String, String>();
                    tmp.put("sicilno", "" + sorgulananSicilno);
                    tmp.put("belgetur", "19");//Nüfus Cüzdanı Fotokopisi
                    tmp.put("ad", nufus2Filename);
                    tmp.put("user_id", MainActivity.userid);
                    db.personelBelgeEkle(tmp);

                    new ShowToast(this, "Nüfus fotoğrafları kaydedilmiştir.");
                    psNufusIptalet.setVisibility(View.GONE);
                    psNufusKaydet.setVisibility(View.GONE);
                    nufusKaydedildi="ok";
                }else{
                    new ShowToast(this, "Nüfus fotoğraflarının ikisi de çekilmelidir.");
                }
            }catch (Exception ex){
                Log.w("psNufusKaydet", ex.getMessage());
            }
        }else if(v.getId()==R.id.psNufusIptalet){
            AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
            builder.setTitle("Uyarı");
            builder.setMessage("Nüfus Fotolarını iptal etmek istediğinize emin misiniz?");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    if(psNufus1!=null && psNufus1.exists())
                        psNufus1.delete();
                    if(psNufus2!=null && psNufus2.exists())
                        psNufus2.delete();
                    psNufus1=null;
                    psNufus2=null;
                    nufus1Filename="";
                    nufus2Filename="";
                    nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                    nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                    psNufusIptalet.setVisibility(View.GONE);
                    psNufusKaydet.setVisibility(View.GONE);
                    nufusKaydedildi="";
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

    private void loadSyncView(){
        ArrayList<HashMap<String,String>> qfpe = db.getMultiResult(new String[]{"OID"},"tarim_istakip_personel","YENI_KAYIT=1 and AKTARILDI=0 and user_id='"+MainActivity.userid+"'");
        int personelSayisi=qfpe.size();
        ArrayList<HashMap<String,String>> qfpe2 = db.getMultiResult(new String[]{"OID"},"tarim_istakip_personel","KARTNO_GUNCELLENDI=1 and KARTNO_GUNCEL_AKTARILDI=0 and user_id='"+MainActivity.userid+"'");
        int personelKartGuncellemeSayisi=qfpe2.size();
        ArrayList<HashMap<String,String>> qfpe3 = db.getMultiResult(new String[]{"OID"},"tarim_istakip_personel","YENI_RESIM=1 and YENI_RESIM_AKTARILDI=0 and user_id='"+MainActivity.userid+"'");
        int personelFotoEklemeSayisi=qfpe3.size();
        ArrayList<HashMap<String,String>> qfpe4 = db.getMultiResult(new String[]{"DISTINCT PERSONELID"},"tarim_istakip_personel_belge","AKTARILDI IN (-1,0) and tur='19' and user_id='"+MainActivity.userid+"'");
        int personelNufusGuncellemeSayisi=qfpe4.size();

        if(personelSayisi>0)
            btnSDToSyncPersonel.setEnabled(true);
        else {
            if(personelKartGuncellemeSayisi+personelFotoEklemeSayisi+personelNufusGuncellemeSayisi==0)
                btnSDToSyncPersonel.setEnabled(false);
            else
                btnSDToSyncPersonel.setEnabled(true);
        }

        ArrayList<HashMap<String,String>> qfpu = db.getMultiResult(new String[]{"OID"},"tarim_istakip_calisma","AKTARILDI IN (-1, 0) and user_id='"+MainActivity.userid+"'");
        int puantajSayisi=qfpu.size();
        if(puantajSayisi>0)
            btnSDToSyncPuantaj.setEnabled(true);
        else
            btnSDToSyncPuantaj.setEnabled(false);

        if (personelSayisi==0 && puantajSayisi==0 && (personelKartGuncellemeSayisi+personelFotoEklemeSayisi+personelNufusGuncellemeSayisi)==0){
            btnSDToSyncTumu.setEnabled(false);
        }else{
            btnSDToSyncTumu.setEnabled(true);
        }

        txtSDToServerInfo.setText("Bek. Yeni/Güncel Personel:"+personelSayisi+"/"+(personelKartGuncellemeSayisi+personelFotoEklemeSayisi+personelNufusGuncellemeSayisi)+" - Puantaj Sayısı:"+puantajSayisi);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try{
            if(requestCode==REQUEST_GP2 && resultCode==RESULT_OK){
                if (intent.hasExtra("exit")){
                    if(intent.hasExtra("status") && intent.getStringExtra("status")=="success")
                    {
                        //Toast.makeText(this, "Günlük Puantaj Başarıyla Oluşturulmuştur.",Toast.LENGTH_SHORT).show();
                    }
                }
                try {
                    //txtsyncInfo.setText(getSyncData());
                } catch (Exception e) {
                    Log.w("pesync",e.getMessage());
                }
            }else if(requestCode==REQUEST_PE) {
                try {
                    //txtsyncInfo.setText(getSyncData());
                } catch (Exception e) {
                    Log.w("pesync", e.getMessage());
                }
            }else if(requestCode==REQUEST_PSTAKE_PHOTO){
                Log.w("PSNewFoto", ""+resultCode);
                try{
                    if(psNewFoto!=null && psNewFoto.exists()) {
                        imgFile2=psNewFoto;
                        String psFotoName=psNewFoto.getAbsolutePath();
                        psFotoName = psFotoName.substring(psFotoName.lastIndexOf('/')+1);
                        db.personelFotoEkle(sorgulananSicilno, psFotoName);
                        layoutPSFotoEmpty.setVisibility(View.GONE);
                        layoutPSFoto.setVisibility(View.VISIBLE);
                        //yeni fotoyu goster
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile2.getAbsolutePath(),options);

                        psFoto.requestLayout();

                        psFoto.setImageBitmap(myBitmap);
                        psFoto.setOnClickListener(this);


                    }else{
                        new ShowToast(this, "Yeni fotoğraf çekilirken hata oluştu.\nTekrar deneyiniz.");
                        layoutPSFoto.setVisibility(View.GONE);
                        psFoto.setImageBitmap(null);
                    }
                }catch (Exception ex){
                    Log.w("PSNewFoto",ex.getMessage());
                }
            }else if(requestCode==REQUEST_PSTAKE_NUFUS1){
                Log.w("PSNufus1", ""+resultCode);
                try{
                    if(psNufus1!=null && psNufus1.exists()) {
                        try {
                            File f = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"", "_"+nufus1Filename);
                            f.createNewFile();

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8;
                            Bitmap myBitmap = BitmapFactory.decodeFile(psNufus1.getAbsolutePath(),options);

                            float ratio = Math.min((float) 1024 / myBitmap.getWidth(),(float) 1024 / myBitmap.getHeight());
                            int width = Math.round((float) ratio * myBitmap.getWidth());
                            int height = Math.round((float) ratio * myBitmap.getHeight());

                            Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, width, height, false);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();

                            psNufus1.delete();
                            nufus1Filename="_"+nufus1Filename;
                            psNufus1=f;
                            nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.nufus));

                            if(psNufus2!=null){
                                psNufusKaydet.setVisibility(View.VISIBLE);
                                psNufusIptalet.setVisibility(View.VISIBLE);
                            }

                            UploadFileToServer f2s = new UploadFileToServer(this);
                            f2s.context = this;
                            f2s.uid = MainActivity.userid;
                            f2s.reqtype = "Nufus::1";
                            f2s.uFile = Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + nufus1Filename;
                            f2s.execute();
                        }catch (IOException ex){

                        }catch (Exception ex){

                        }
                    }else{
                        nufus1Filename="";
                        new ShowToast(this, "Nüfus Fotosu çekilirken hata oluştu.\nTekrar deneyiniz.");
                    }
                }catch (Exception ex){
                    Log.w("PSNufus1",ex.getMessage());
                }
            }else if(requestCode==REQUEST_PSTAKE_NUFUS2){
                Log.w("PSNufus2", ""+resultCode);
                try{
                    if(psNufus2!=null && psNufus2.exists()) {
                        try {
                            File f = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"", "_"+nufus2Filename);
                            f.createNewFile();

                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8;
                            Bitmap myBitmap = BitmapFactory.decodeFile(psNufus2.getAbsolutePath(),options);

                            float ratio = Math.min((float) 1024 / myBitmap.getWidth(),(float) 1024 / myBitmap.getHeight());
                            int width = Math.round((float) ratio * myBitmap.getWidth());
                            int height = Math.round((float) ratio * myBitmap.getHeight());

                            Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, width, height, false);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();

                            psNufus2.delete();
                            nufus2Filename="_"+nufus2Filename;
                            psNufus2=f;
                            nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.nufus));

                            if(psNufus1!=null){
                                psNufusKaydet.setVisibility(View.VISIBLE);
                                psNufusIptalet.setVisibility(View.VISIBLE);
                            }

                            UploadFileToServer f2s = new UploadFileToServer(this);
                            f2s.context = this;
                            f2s.uid = MainActivity.userid;
                            f2s.reqtype = "Nufus::2";
                            f2s.uFile = Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + nufus2Filename;
                            f2s.execute();
                        }catch (IOException ex){

                        }catch (Exception ex){

                        }
                    }else{
                        nufus2Filename="";
                        new ShowToast(this, "Nüfus Fotosu çekilirken hata oluştu.\nTekrar deneyiniz.");
                    }
                }catch (Exception ex){
                    Log.w("PSNufus2",ex.getMessage());
                }
            }else {
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    String scanFormat = scanningResult.getFormatName();

                    if(activeButton.equals("imgChangeKartno")) {
                        if (scanContent != null) {
                            db = new Database(getApplicationContext());
                            db.cntxt=this;
                            String kartnowithpad = scanContent;
                            int padToLength = 6;
                            if (kartnowithpad.length() < padToLength) {
                                kartnowithpad = String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s", 0, kartnowithpad);
                            }

                            scanContent = kartnowithpad;


                            try {
                                barkodIslem = "esleme";
                                barkodIcerik = scanContent;
                                DataFromService kartno = new DataFromService((ServiceCallBack) mactivity);
                                kartno.context=ParentCtxt;
                                kartno.uid=userid;
                                kartno.reqtype="KartNoGuncelle";
                                kartno.title="Kart No Güncelle";
                                kartno.reqparam=new ArrayList<String[]>();
                                kartno.reqparam.add(new String[]{"op", "persor"});
                                kartno.reqparam.add(new String[]{"kartno", scanContent});
                                kartno.reqparam.add(new String[]{"sicilno", ""});
                                kartno.reqparam.add(new String[]{"ad", ""});
                                kartno.reqparam.add(new String[]{"soyad", ""});
                                kartno.reqparam.add(new String[]{"tckimlik", ""});
                                kartno.resp=new ArrayList<String[]>();
                                kartno.resp.add(new String[]{"id","id"});
                                kartno.execute();

                                //HashMap<String, String> kartno = db.getOneRow(new String[]{"ID", "AD", "SOYAD"}, "tarim_istakip_personel", "kartno='" + scanContent + "'");

                            }catch (Exception ex){
                                Log.w("Barcode", "nothing");
                            }


                            //HashMap<String, String> kartno = db.getOneRow(new String[]{"ID", "AD", "SOYAD"}, "tarim_istakip_personel", "kartno='" + scanContent + "'");
                            //if (kartno.size() == 0) {
                            /*ServerData sd=new ServerData(this);
                            List<HashMap<String, String>> kartno=sd.personelSorgula(userid, scanContent, "","","","");
                            if(kartno==null || kartno.size()==0){
                                psKartno.setText("");
                                if(sd.personelKartnoUpdate(userid, scanContent, psBarkod.getText().toString(),"esleme", MainActivity.PROGRAM_VERSION )) {
                                    new ShowToast(this, psAd.getText() + " " + psSoyad.getText() + " için " + scanContent + " kart nosu tanımlanmıştır.");
                                    psKartno.setText(scanContent);
                                }else
                                    new ShowToast(this, "Eşleştirmede hata oluştu");
                            } else {
                                new ShowToast(this, "Hata: Okutulan kart no " + kartno.get(0).get("AD").toString() + " " + kartno.get(0).get("SOYAD").toString() + " tanımlı.");
                            }*/
                        }
                    }else if (activeButton.equals("IsBitirme")) {
                        ibitpersonel = scanContent;
                        db = new Database(getApplicationContext());
                        if (db.barkodDogrula(scanContent, userid)) {
                            db = new Database(getApplicationContext());
                            HashMap<String, String> isbilgileri = db.isbitirmeBilgiGetir(scanContent);
                            if (isbilgileri.size() != 0) {
                                ibitBarkod.setText(isbilgileri.get("personel"));
                                ibitBolge.setText(isbilgileri.get("bolge"));
                                ibitCalisma.setText(isbilgileri.get("calisma"));
                                ibitFirma.setText(isbilgileri.get("firma"));
                                ibitYetkili.setText(isbilgileri.get("yetkili"));
                                ibitEkiplideri.setText(isbilgileri.get("ekiplideri"));
                                ibitBasTarih.setText(isbilgileri.get("isebaslamatarihi"));
                                ibitFisno.setText(isbilgileri.get("fisno"));
                                ibitGorev.setText(isbilgileri.get("gorev"));
                                ibitUrun.setText(isbilgileri.get("urun"));
                                btnIBitOnay.setEnabled(true);
                            } else {
                                new ShowToast(this, "Barkoda ait iş başlama bilgisi bulunamadı!");
                            }
                        } else {
                            temizle();
                            new ShowToast(this, "Barkod hatası 2");
                        }
                    } else if (activeButton.equals("PSorgula")) {
                        psKartno.setText(scanContent);

                        getPersonelInfo("", "", "", scanContent, "");

                        /*PersonelOnline personel = new PersonelOnline(this);
                        personel.context = this;
                        personel.db = db;
                        personel.tc ="";
                        personel.a ="";
                        personel.s ="";
                        personel.kn=scanContent;
                        personel.sc="";
                        personel.uid =userid;
                        personel.execute();
                        */
                        /*db = new Database(getApplicationContext());
                        if (db.barkodDogrula(scanContent, userid)) {
                            HashMap<String, String> personelbilgileri = db.personelBilgileriGetir(scanContent);
                            if (personelbilgileri.size() > 0) {
                                imgChangeKartno.setVisibility(View.VISIBLE);
                                imgChangeKartno.setOnClickListener(this);

                                imgCancelKartno.setVisibility(View.VISIBLE);
                                imgCancelKartno.setOnClickListener(this);

                                sorgulananSicilno=personelbilgileri.get("ID");
                                psBarkod.setText(personelbilgileri.get("ID"));
                                psTC.setText(personelbilgileri.get("TC"));
                                psAd.setText(personelbilgileri.get("AD"));
                                psSoyad.setText(personelbilgileri.get("SOYAD"));
                                psDogumTarihi.setText(personelbilgileri.get("DOGUMTARIHI"));
                                psCinsiyet.setText(personelbilgileri.get("CINSIYET"));
                                psGorev.setText(personelbilgileri.get("GOREV"));
                                psBolge.setText(personelbilgileri.get("BOLGE"));
                                psBolge2.setText(personelbilgileri.get("BOLGE2"));
                                psBolge3.setText(personelbilgileri.get("BOLGE3"));
                                psBolge4.setText(personelbilgileri.get("BOLGE4"));
                                psBolge5.setText(personelbilgileri.get("BOLGE5"));
                                psEkiplideri.setText(personelbilgileri.get("EKIP_LIDERI"));
                                psEkiplideri2.setText(personelbilgileri.get("EKIP_LIDERI2"));
                                psEkiplideri3.setText(personelbilgileri.get("EKIP_LIDERI3"));
                                layoutPSNufusFoto.setVisibility(View.VISIBLE);

                                if(nufusKaydedildi.equals("")) {
                                    if (psNufus1 != null && psNufus1.exists())
                                        psNufus1.delete();
                                    if (psNufus2 != null && psNufus2.exists())
                                        psNufus2.delete();
                                }
                                psNufus1=null;
                                psNufus2=null;
                                nufus1Filename="";
                                nufus2Filename="";
                                nufusKaydedildi="";
                                nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                                nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                                psNufusKaydet.setVisibility(View.GONE);
                                psNufusIptalet.setVisibility(View.GONE);

                                ArrayList<HashMap<String, String>> personelnufusbelge = db.getMultiResult(new String[]{"RESIMADI"}, "tarim_istakip_personel_belge", "user_id='" + MainActivity.userid + "' and personelid='"+sorgulananSicilno+"' and tur='19'");
                                if(personelnufusbelge.size()==2){
                                    nufusKaydedildi="ok";
                                    nufus1Filename=personelnufusbelge.get(0).get("RESIMADI");
                                    nufus2Filename=personelnufusbelge.get(1).get("RESIMADI");
                                    nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                                    nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                                    psNufus1 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + nufus1Filename);
                                    psNufus2 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + nufus2Filename);
                                }



                                if (personelbilgileri.get("RESIM_INDIRILDI").equals("1")) {
                                    imgFile2 = new  File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+personelbilgileri.get("RESIM"));

                                    if(imgFile2.exists()){
                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inSampleSize = 8;
                                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile2.getAbsolutePath(),options);
                                        psFoto.setImageBitmap(myBitmap);
                                        psFoto.setOnClickListener(this);
                                        layoutPSFoto.setVisibility(View.VISIBLE);
                                        layoutPSFotoEmpty.setVisibility(View.GONE);
                                    }else{
                                        layoutPSFoto.setVisibility(View.GONE);
                                        layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                                        psFoto.setImageBitmap(null);
                                    }
                                }else{
                                    imgFile2=null;
                                    layoutPSFoto.setVisibility(View.GONE);
                                    layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                                    psFoto.setImageBitmap(null);
                                }

                                Log.w("here",personelbilgileri.get("RESIM_INDIRILDI"));
                                if (personelbilgileri.get("SGK_EVRAK_INDIRILDI").equals("1")) {
                                    SGK_Evrak=personelbilgileri.get("SGK_EVRAK");
                                    pssgkevrak.setText("Var. (Görüntüle)");
                                    pssgkevrak.setOnClickListener(this);
                                }else{
                                    SGK_Evrak="";
                                    pssgkevrak.setOnClickListener(null);
                                    if(personelbilgileri.containsKey("SGK_EVRAK") && (personelbilgileri.get("SGK_EVRAK")==null || personelbilgileri.get("SGK_EVRAK").equals(""))){
                                        pssgkevrak.setText("Yok");
                                    }else{
                                        pssgkevrak.setText("Evrak var. İndirilmemiş.");
                                    }
                                }
                            } else {
                                imgChangeKartno.setVisibility(View.GONE);
                                imgCancelKartno.setVisibility(View.GONE);
                                //Toast toast = Toast.makeText(this, "Personel bilgisi bulunmadı.", Toast.LENGTH_SHORT);
                                //toast.show();

                                AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                                builder.setTitle("Uyarı");
                                builder.setMessage("Kart No ya personel eşleştirmek ister misiniz?");

                                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getApplicationContext(), PersonelKartEsle.class);
                                        intent.putExtra("userid", userid);
                                        intent.putExtra("kartno", psKartno.getText().toString());
                                        startActivity(intent);
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
                            imgChangeKartno.setVisibility(View.GONE);
                            imgCancelKartno.setVisibility(View.GONE);
                            if(!scanContent.equals("")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                                builder.setTitle("Uyarı");
                                builder.setMessage("Kart No ya personel eşleştirmek ister misiniz?");

                                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getApplicationContext(), PersonelKartEsle.class);
                                        intent.putExtra("userid", userid);
                                        intent.putExtra("kartno", psKartno.getText().toString());
                                        startActivity(intent);
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
                            }else{
                                new ShowToast(this, "Barkod hatası");
                            }
                        }*/
                    }else {
                    }
                } else {

                }
            }
        }catch (Exception ex){
            //Log.w("onActivityResult err:",ex.getMessage());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menufile, menu);
        menuu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homebtn:
                /*btnPSorgulama.setVisibility(View.VISIBLE);
                btnPuantajListeleme.setVisibility(View.VISIBLE);
                btnIseBaslama.setVisibility(View.VISIBLE);
                menuu.findItem(R.id.homebtn).setVisible(false);
                layoutIBit.setVisibility(View.GONE);
                layoutPS.setVisibility(View.GONE);
                svScroll.setVisibility(View.GONE);
                svScrollSync.setVisibility(View.GONE);*/

                loginLayout.setVisibility(View.GONE);
                llayoutPersonel.setVisibility(View.VISIBLE);
                //llayoutSync.setVisibility(View.VISIBLE);
                llayoutWaitNSent.setVisibility(View.GONE);
                llayoutDuyuru.setVisibility(View.VISIBLE);
                btnPSorgulama.setVisibility(View.VISIBLE);
                btnPuantajListeleme.setVisibility(View.VISIBLE);
                btnIseBaslama.setVisibility(View.VISIBLE);
                //btnSync.setVisibility(View.VISIBLE);
                //btnSyncAl.setVisibility(View.VISIBLE);
                btnPuantajSent.setVisibility(View.VISIBLE);
                btnPuantajWait.setVisibility(View.VISIBLE);
                btnPersonelEkle.setVisibility(View.VISIBLE);

                layoutIBit.setVisibility(View.GONE);
                layoutPS.setVisibility(View.GONE);
                //layoutSync.setVisibility(View.GONE);
                svScroll.setVisibility(View.GONE);
                //svScrollSync.setVisibility(View.GONE);


                temizle();
                activeButton="";
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void temizle(){
        ibitBolge.setText("");
        ibitCalisma.setText("");
        ibitFirma.setText("");
        ibitYetkili.setText("");
        ibitEkiplideri.setText("");
        ibitBasTarih.setText("");
        ibitFisno.setText("");
        ibitGorev.setText("");
        ibitUrun.setText("");
        ibitBarkod.setText("");
        //btnIBasOnay.setEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.guleryuz.puantajonline/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.guleryuz.puantajonline/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private void getPersonelInfo(String tc, String a, String s, String kn, String sc){
        //Send data to server
        try {
            conn=new Connectivity();
            if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                if (proDialog == null) {
                    proDialog = new ProgressDialog(this);
                    proDialog.setCanceledOnTouchOutside(false);
                    proDialog.setCancelable(false);
                    proDialog.setTitle("Personel Sorgulanıyor.");
                    proDialog.setMessage("Personel Bilgileri");
                }
                proDialog.show();

                HashMap<String, String> params=new HashMap<String, String>();
                params.put("token","6ce304f73ce841efaf1490bb98474eef");
                params.put("op","persor");
                params.put("uid",userid);
                params.put("prgver",MainActivity.PROGRAM_VERSION);
                params.put("ttt",""+System.currentTimeMillis());
                params.put("kartno",(kn!=null?kn:""));
                params.put("sicilno",(sc!=null?sc:""));
                params.put("tckimlik",(tc!=null?tc:""));
                params.put("ad",(a!=null?a:""));
                params.put("soyad",(s!=null?s:""));

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        this.getResources().getString(R.string.serviceUrl), new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Success Callback
                                Log.w("volleyPersonel",response.toString());
                                try {
                                    JSONArray values = response.getJSONArray("result");
                                    if(values!=null && values.length()>0){
                                        JSONObject value = values.getJSONObject(0);
                                        personelInfo2Elements(value);
                                    } else {
                                        new ShowToast(mactivity, "Personel bilgisi bulunmadı.");
                                    }
                                }catch (Exception ex){
                                    Log.w("Error",ex.getMessage());
                                    new ShowToast(MainActivity.mactivity, "Sunucuya bağlantıda hata oluştu.");
                                }
                                if (proDialog != null && proDialog.isShowing())
                                    proDialog.dismiss();
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
                                    message = "Personel bilgisi bulunmadı.";
                                }
                                new ShowToast(MainActivity.mactivity, message);
                                MainActivity.gpd = null;
                                if (proDialog != null && proDialog.isShowing())
                                    proDialog.dismiss();
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

    private void personelInfo2Elements(JSONObject value)
    {
        try {
            if (value != null) {
                SGK_Evrak = "";
                sorgulananSicilno = "";
                db = new Database(getApplicationContext());
                //str_pad
                String kartnowithpad = psKartno.getText().toString();
                int padToLength = 6;
                if (kartnowithpad.length() < padToLength) {
                    kartnowithpad = String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s", 0, kartnowithpad);
                }

                imgChangeKartno.setVisibility(View.VISIBLE);
                imgChangeKartno.setOnClickListener(this);

                kartnowithpad = value.getString("kartno");
                if (!kartnowithpad.equals("") && kartnowithpad.length() < padToLength) {
                    kartnowithpad = String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s", 0, kartnowithpad);
                }
                psKartno.setText((value.getString("kartno").equals("0") ? "" : kartnowithpad));

                if (!psKartno.getText().toString().equals("")) {
                    imgCancelKartno.setVisibility(View.VISIBLE);
                    imgCancelKartno.setOnClickListener(this);
                } else {
                    imgCancelKartno.setVisibility(View.GONE);
                    imgCancelKartno.setOnClickListener(this);
                }

                sorgulananSicilno = value.getString("id");
                psBarkod.setText(value.getString("id"));
                psTC.setText(value.getString("tc"));
                psAd.setText(value.getString("ad"));
                psSoyad.setText(value.getString("soyad"));
                psDogumTarihi.setText(value.getString("dogumtarihi"));
                psCinsiyet.setText(value.getString("cinsiyet"));
                psGorev.setText(value.getString("gorev"));
                psBolge.setText(value.getString("bolge"));
                psBolge2.setText(value.getString("bolge2"));
                psBolge3.setText(value.getString("bolge3"));
                psBolge4.setText(value.getString("bolge4"));
                psBolge5.setText(value.getString("bolge5"));
                psEkiplideri.setText(value.getString("ekip_lideri"));
                psEkiplideri2.setText(value.getString("ekip_lideri2"));
                psEkiplideri3.setText(value.getString("ekip_lideri3"));
                String sskdurumu = "";
                if (value.getString("devam").equals("0")) {
                    sskdurumu = "Personel Pasif";
                } else {
                    switch (value.getString("ssk")) {
                        case "2":
                            switch (value.getString("ssk_cikis")) {
                                case "1":
                                    sskdurumu = "Çıkış Yapıldı.";
                                    break;
                                case "0":
                                    sskdurumu = "Çıkış Beklemede.";
                            }
                            break;
                        case "1":
                            sskdurumu = "SSK Yapıldı.";
                            break;
                        case "0":
                            sskdurumu = "Giriş Beklemede.";
                            break;
                        case "-1":
                            sskdurumu = "Giriş Beklemede.";
                            break;
                    }

                }

                psSSKDurumu.setText(sskdurumu);


                layoutPSNufusFoto.setVisibility(View.VISIBLE);
                psNewFoto = null;
                if (nufusKaydedildi.equals("")) {
                    if (psNufus1 != null && psNufus1.exists())
                        psNufus1.delete();
                    if (psNufus2 != null && psNufus2.exists())
                        psNufus2.delete();
                }
                psNufus1 = null;
                psNufus2 = null;
                nufus1Filename = "";
                nufus2Filename = "";
                nufusKaydedildi = "";
                nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                psNufusKaydet.setVisibility(View.GONE);
                psNufusIptalet.setVisibility(View.GONE);

                if (value.has("nufus1") && !value.getString("nufus1").equals("")) {
                    nufusKaydedildi = "ok";
                    nufus1Filename = value.getString("nufus1");
                    nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                }

                if (value.has("nufus2") && !value.getString("nufus2").equals("")) {
                    nufus1Filename = value.getString("nufus2");
                    nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                }
                try {
                    if (value.getString("resim") != null && value.getString("resim").length() > 0) {
                        psFoto.requestLayout();
                        imgFile = value.getString("resim");
                        Picasso.get()
                                .load(MainActivity.mactivity.getString(R.string.docUrl) + value.getString("resim"))
                                .into(psFoto);

                        psFoto.setOnClickListener(this);
                        layoutPSFoto.setVisibility(View.VISIBLE);
                        layoutPSFotoEmpty.setVisibility(View.GONE);


                    } else {
                        imgFile = null;
                        layoutPSFoto.setVisibility(View.GONE);
                        layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                        psFoto.setImageBitmap(null);
                    }
                } catch (Exception ex) {
                    imgFile = "";
                    layoutPSFoto.setVisibility(View.GONE);
                    layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                    psFoto.setImageBitmap(null);
                }


                Log.w("here", value.getString("resim"));


                if (value.getString("sgk_evrak") != null && value.getString("sgk_evrak").length() > 0) {
                    SGK_Evrak = value.getString("sgk_evrak");
                    pssgkevrak.setText("Var. (Görüntüle)");
                    pssgkevrak.setOnClickListener(this);
                } else {
                    SGK_Evrak = "";
                    pssgkevrak.setOnClickListener(null);
                    if (value.has("sgk_evrak") && (value.getString("sgk_evrak") == null || value.getString("sgk_evrak").equals(""))) {
                        pssgkevrak.setText("Yok");
                    } else {
                        pssgkevrak.setText("Evrak var. İndirilmemiş.");
                    }
                }
            }
        }catch (Exception ex){
            new ShowToast(MainActivity.mactivity, "Personel bilgileri görüntülenirken hata oluştu.");
        }
    }

    public static void sendPersonels2Server()
    {
        //Send data to server
        try {
            Connectivity conn=new Connectivity();
            if (conn.isConnected(mactivity) || conn.isConnectedMobile(mactivity) || conn.isConnectedWifi(mactivity)) {
                String jsonservis="";
                if(MainActivity.gpd.getServisSayisi()>0) {
                    HashMap<String, String> servis = MainActivity.gpd.getServis();
                    for (int i = 1; i < 21; i++) {
                        jsonservis += "\"servis" + i + "sayi\":\"" + servis.get("" + (i - 1)) + "\"," + "\"servis" + i + "\":\"" + MainActivity.gpd.servisBilgileri[i - 1] + "\"";
                        if (i < 20) {
                            jsonservis += ",";
                        }
                    }
                }

                jsonservis="{"+jsonservis+"}";

                HashMap<String, List<GunlukPersonelData>> gorevler = MainActivity.gpd.getGorevler();
                String jsongorev = "";
                for ( String key : gorevler.keySet() ) {
                    List<GunlukPersonelData> gperd = gorevler.get(key);
                    for (int i=0; i<gperd.size(); i++) {
                        jsongorev += "{\"globalid\":\"" + MainActivity.gpd.getGlobalid() + "\",\"sicilno\":\"" + gperd.get(i).sicilno + "\",\"urunid\":\"" + gperd.get(i).urun + "\",\"gorev\":\"" + key + "\",\"mesai\":\"" + gperd.get(i).mesai + "\",\"kartokutma\":\"" + gperd.get(i).kartlaeklendi + "\",\"kartno\":\"" + gperd.get(i).kartno + "\",\"tc\":\"" + gperd.get(i).tc + "\",\"ad\":\"" + gperd.get(i).adi.replace(" "+gperd.get(i).soyadi,"") + "\",\"soyad\":\"" + gperd.get(i).soyadi + "\",\"cinsiyet\":\"" + gperd.get(i).cinsiyet + "\"},";
                    }
                }
                jsongorev = "["+jsongorev.substring(0,jsongorev.length()-1)+"]";

                HashMap<String, String> params=new HashMap<String, String>();
                params.put("token","6ce304f73ce841efaf1490bb98474eef");
                params.put("op","pushdata3");
                params.put("uid",MainActivity.userid);
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
                params.put("servisvar",""+MainActivity.gpd.getServisSayisi());
                params.put("gorevjson",jsongorev);
                params.put("servisjson",jsonservis);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        mactivity.getResources().getString(R.string.serviceUrl), new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Success Callback
                                Log.w("volleyGunlukPuantaj2",response.toString());
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
                new ShowToast(mactivity, "İnternet Bağlantınızı Kontrol Ediniz.");
            }
        }catch (Exception ex){
            new ShowToast(mactivity, "İnternet Bağlantınızı Kontrol Ediniz.");
        }
        //----
    }

    public void UserAsyncFinish(boolean userStat, String uid, String py, String error){
        if (userStat) {
            userid=uid;
            userPuantajYetki=py;
            wvLayout.setVisibility(View.VISIBLE);
            txtBilgilendirme.setText("");
            //txtsyncInfo.setText(getSyncData());
            loginLayout.setVisibility(View.GONE);
            llayoutPersonel.setVisibility(View.VISIBLE);
            //llayoutSync.setVisibility(View.VISIBLE);
            llayoutWaitNSent.setVisibility(View.GONE);
            llayoutDuyuru.setVisibility(View.VISIBLE);
            btnPSorgulama.setVisibility(View.VISIBLE);
            btnPuantajListeleme.setVisibility(View.VISIBLE);
            btnIseBaslama.setVisibility(View.VISIBLE);
            btnPuantajSent.setVisibility(View.VISIBLE);
            btnPuantajWait.setVisibility(View.VISIBLE);
            btnPersonelEkle.setVisibility(View.VISIBLE);

            layoutIBit.setVisibility(View.GONE);
            layoutPS.setVisibility(View.GONE);
            //layoutSync.setVisibility(View.GONE);
            svScroll.setVisibility(View.GONE);
            //svScrollSync.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(loginBtn.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }else{
            new ShowToast(this, (error!=""?error:"Kullanıcı/Şifre Hatası"));
        }
    }

    @Override
    public void PersonelAsyncFinish(boolean stat, HashMap<String, String> personelbilgileri, String error) {
        try {
            if (stat) {
                if (personelbilgileri != null) {
                    SGK_Evrak = "";
                    sorgulananSicilno = "";
                    db = new Database(getApplicationContext());
                    //str_pad
                    String kartnowithpad = psKartno.getText().toString();
                    int padToLength = 6;
                    if (kartnowithpad.length() < padToLength) {
                        kartnowithpad = String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s", 0, kartnowithpad);
                    }

                    imgChangeKartno.setVisibility(View.VISIBLE);
                    imgChangeKartno.setOnClickListener(this);

                    kartnowithpad = personelbilgileri.get("KARTNO");
                    if (!kartnowithpad.equals("") && kartnowithpad.length() < padToLength) {
                        kartnowithpad = String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s", 0, kartnowithpad);
                    }
                    psKartno.setText((personelbilgileri.get("KARTNO").equals("0") ? "" : kartnowithpad));

                    if (!psKartno.getText().toString().equals("")) {
                        imgCancelKartno.setVisibility(View.VISIBLE);
                        imgCancelKartno.setOnClickListener(this);
                    } else {
                        imgCancelKartno.setVisibility(View.GONE);
                        imgCancelKartno.setOnClickListener(this);
                    }

                    sorgulananSicilno = personelbilgileri.get("ID");
                    psBarkod.setText(personelbilgileri.get("ID"));
                    psTC.setText(personelbilgileri.get("TC"));
                    psAd.setText(personelbilgileri.get("AD"));
                    psSoyad.setText(personelbilgileri.get("SOYAD"));
                    psDogumTarihi.setText(personelbilgileri.get("DOGUMTARIHI"));
                    psCinsiyet.setText(personelbilgileri.get("CINSIYET"));
                    psGorev.setText(personelbilgileri.get("GOREV"));
                    psBolge.setText(personelbilgileri.get("BOLGE"));
                    psBolge2.setText(personelbilgileri.get("BOLGE2"));
                    psBolge3.setText(personelbilgileri.get("BOLGE3"));
                    psBolge4.setText(personelbilgileri.get("BOLGE4"));
                    psBolge5.setText(personelbilgileri.get("BOLGE5"));
                    psEkiplideri.setText(personelbilgileri.get("EKIP_LIDERI"));
                    psEkiplideri2.setText(personelbilgileri.get("EKIP_LIDERI2"));
                    psEkiplideri3.setText(personelbilgileri.get("EKIP_LIDERI3"));
                    String sskdurumu="";
                    if(personelbilgileri.get("DEVAM").equals("0")){
                        sskdurumu="Personel Pasif";
                    }else{
                        switch (personelbilgileri.get("SSK")){
                            case "2":
                                switch (personelbilgileri.get("SSK_CIKIS")){
                                    case "1":
                                        sskdurumu="Çıkış Yapıldı.";
                                        break;
                                    case "0":
                                        sskdurumu="Çıkış Beklemede.";
                                }
                                break;
                            case "1":
                                sskdurumu="SSK Yapıldı.";
                                break;
                            case "0":
                                sskdurumu="Giriş Beklemede.";
                                break;
                            case "-1":
                                sskdurumu="Giriş Beklemede.";
                                break;
                        }

                    }

                    psSSKDurumu.setText(sskdurumu);


                    layoutPSNufusFoto.setVisibility(View.VISIBLE);
                    psNewFoto=null;
                    if(nufusKaydedildi.equals("")) {
                        if (psNufus1 != null && psNufus1.exists())
                            psNufus1.delete();
                        if (psNufus2 != null && psNufus2.exists())
                            psNufus2.delete();
                    }
                    psNufus1=null;
                    psNufus2=null;
                    nufus1Filename="";
                    nufus2Filename="";
                    nufusKaydedildi="";
                    nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                    nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.addnufus));
                    psNufusKaydet.setVisibility(View.GONE);
                    psNufusIptalet.setVisibility(View.GONE);

                    if (!personelbilgileri.get("NUFUS1").equals("")) {
                        nufusKaydedildi="ok";
                        nufus1Filename=personelbilgileri.get("NUFUS1");
                        nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                    }

                    if (!personelbilgileri.get("NUFUS2").equals("")) {
                        nufus1Filename=personelbilgileri.get("NUFUS2");
                        nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                    }
                    /*ArrayList<HashMap<String, String>> personelnufusbelge = db.getMultiResult(new String[]{"RESIMADI"}, "tarim_istakip_personel_belge", "user_id='" + MainActivity.userid + "' and personelid='"+sorgulananSicilno+"' and tur='19'");
                    if(personelnufusbelge.size()==2){
                        nufusKaydedildi="ok";
                        nufus1Filename=personelnufusbelge.get(0).get("RESIMADI");
                        nufus2Filename=personelnufusbelge.get(1).get("RESIMADI");
                        nufusFoto1.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                        nufusFoto2.setImageDrawable(getResources().getDrawable(R.drawable.nufus));
                        psNufus1 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + nufus1Filename);
                        psNufus2 = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + nufus2Filename);
                    }*/

                try {
                    if (personelbilgileri.get("RESIM")!=null && personelbilgileri.get("RESIM").length()>0) {
                        psFoto.requestLayout();
                        imgFile=personelbilgileri.get("RESIM");
                        Picasso.get()
                                .load(this.getResources().getString(R.string.docUrl) + personelbilgileri.get("RESIM"))
                                .into(psFoto);

                        psFoto.setOnClickListener(this);
                        layoutPSFoto.setVisibility(View.VISIBLE);
                        layoutPSFotoEmpty.setVisibility(View.GONE);


                    } else {
                        imgFile = null;
                        layoutPSFoto.setVisibility(View.GONE);
                        layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                        psFoto.setImageBitmap(null);
                    }
                }catch (Exception ex){
                    imgFile = "";
                    layoutPSFoto.setVisibility(View.GONE);
                    layoutPSFotoEmpty.setVisibility(View.VISIBLE);
                    psFoto.setImageBitmap(null);
                }


                Log.w("here",personelbilgileri.get("RESIM"));


                if (personelbilgileri.get("SGK_EVRAK")!=null && personelbilgileri.get("SGK_EVRAK").length()>0) {
                    SGK_Evrak = personelbilgileri.get("SGK_EVRAK");
                    pssgkevrak.setText("Var. (Görüntüle)");
                    pssgkevrak.setOnClickListener(this);
                }else{
                    SGK_Evrak="";
                    pssgkevrak.setOnClickListener(null);
                    if(personelbilgileri.containsKey("SGK_EVRAK") && (personelbilgileri.get("SGK_EVRAK")==null || personelbilgileri.get("SGK_EVRAK").equals(""))){
                        pssgkevrak.setText("Yok");
                    }else{
                        pssgkevrak.setText("Evrak var. İndirilmemiş.");
                    }
                }
                } else {
                    new ShowToast(this, "Personel bilgisi bulunmadı.");
                }
            } else {
                new ShowToast(this, "Personel bilgisi bulunmadı.");
            }
        }catch (Exception ex){
            Log.w("PersonelAsyncFinish", ex.getMessage());
        }
    }

    public void SifremiUnuttumAsyncFinish(String userStat, String uid, String p){
        if (userStat.equals("1")) {
            new ShowToast(this, "Şifreniz kısa bir süre sonra kayıtlı cep telefonunuza sms olarak gönderilecektir.");
            db.usersGuncelle2(p, uid);
        }else if (userStat.equals("-1")) {
            new ShowToast(this, "Kayıtlı gsm numaranız bulunamadı.");
        }else{
            new ShowToast(this, "Şifre değişimi sırarında bir hata oluştu. Sistem yönetinicizle iletişime geçiniz.");
        }
    }


    public void PersonelAsyncFinish(boolean userStat){
        try{
            if (userStat) {
                //For create rootFolder
                try{
                    File photoPath = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir);

                    if (!photoPath.exists()) {
                        photoPath.mkdirs();
                    }

                }catch (Exception ex){
                    Log.w("MainActivity",ex.getMessage());
                }

                db = new Database(getApplicationContext());
                ArrayList<HashMap<String, String>> res = db.getMultiResult(new String[]{"ID", "RESIM"}, "tarim_istakip_personel", "RESIM <>'' AND RESIM<>'null' AND RESIM<>'NULL' AND RESIM_INDIRILDI=0");
                Log.w("sD", "here - " + res.size());
                if(res.size()>0) {
                        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
                        if (proDialog == null) {
                            proDialog = new ProgressDialog(this);
                            proDialog.setCanceledOnTouchOutside(false);
                            proDialog.setCancelable(false);
                            proDialog.setTitle("Veriler");
                            proDialog.setMessage("İndiriliyor... - Personel Foto");
                            proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            proDialog.setMax(100);
                        }
                        proDialog.show();
                        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                                NUMBER_OF_CORES * 2,
                                NUMBER_OF_CORES * 2,
                                60L,
                                TimeUnit.SECONDS,
                                new LinkedBlockingQueue<Runnable>()
                        );

                        curCount = 0;
                        pfHatali = 0;
                        personelFotoSayisi = res.size();
                        for (int i = 0; i < res.size(); i++) {
                            executor.execute(new PersonelFotoThread(i, res.get(i), new Handler(this)));
                        }
                }else{
                    db.dbstatDegerEkle("personelfoto", "fromserver", MainActivity.userid);
                    getPersonelSGKEvrak();
                }

                /*PersonelFotoAsync pfa = new PersonelFotoAsync(this, res);
                pfa.db = db;
                pfa.context = MainActivity.this;
                pfa.execute();
                */
                if (db.getDBStatus("fromserver", MainActivity.userid).containsKey("personel"))
                    txtSDFromServerTime.setText(db.getDBStatus("fromserver", MainActivity.userid).get("personel"));

            }
        }catch (Exception ex){
            new ShowToast(this, "Hata: "+ex.getMessage());
        }
    }

    public void PushDataAsyncFinish(boolean userStat){
        if(userStat){
            db = new Database(getApplicationContext());

            if (db.getDBStatus("toserverpuantaj", MainActivity.userid).containsKey("pushdata"))
                txtSDToServerTimePuantaj.setText(db.getDBStatus("toserverpuantaj", MainActivity.userid).get("pushdata"));

            if (db.getDBStatus("toserver", MainActivity.userid).containsKey("pushdata"))
                txtSDToServerTimePersonel.setText(db.getDBStatus("toserverpersonel", MainActivity.userid).get("pushdata"));

            //txtsyncInfo.setText(getSyncData());

           loadSyncView();
        }
    }

    @Override
    public void SendDataAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        String[] message=msg.obj.toString().split("-");
        if(message[0].equals("pfoto")) {
            final int per = (int) (100 * curCount / personelFotoSayisi);
            Log.w("personel sayisi", "" + curCount + " " + per + " " + personelFotoSayisi);
            Handler progressBarHandler = new Handler();
            progressBarHandler.post(new Runnable() {
                public void run() {
                    proDialog.setProgress((int) per);
                }
            });
            //proDialog.setProgress((int) per);
            if (message[2].equals("ok")) {
                curCount++;
                String personid = message[1];
                //personid = personid.substring(0, personid.indexOf('-'));
                //Log.w("personid", personid);
                List<String> foto = new ArrayList<String>();
                foto.add(personid);
                db.personelFotoUpdate(foto, "RESIM_INDIRILDI");
            } else {
                pfHatali++;
                Log.w("personid hatalı", message[1]);
            }

            Log.w("handleMessage "+message[0]," "+curCount+" + "+pfHatali+" = "+personelFotoSayisi);

            if (curCount+pfHatali==personelFotoSayisi) {
                if (pfHatali > 0) {
                    new ShowToast(this, "" + pfHatali + " tane fotoğraf indirilirken hata oluştu. Daha sonra tekrar deneyiniz");
                }

                db.dbstatDegerEkle("personelfoto", "fromserver", MainActivity.userid);
                if (proDialog.isShowing())
                    proDialog.dismiss();

                getPersonelSGKEvrak();
            }
        }else{
            final int per = (int) (100 * curCountSGK / personelFotoSayisiSGK);
            Log.w("personel sayisi", "" + curCountSGK + " " + per + " " + personelFotoSayisiSGK);
            Handler progressBarHandler = new Handler();
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                    proDialog.setProgress((int) per);
                }
            });
            //proDialog.setProgress((int) per);
            if (msg.obj.toString().indexOf("ok") > 0) {
                curCountSGK++;
                String personid = message[1];
                //personid = personid.substring(0, personid.indexOf('-'));
                Log.w("personid - sgk", personid);
                List<String> foto = new ArrayList<String>();
                foto.add(personid);
                db.personelFotoUpdate(foto,"SGK_EVRAK_INDIRILDI");
            } else {
                pfHataliSGK++;
            }

            Log.w("handleMessage " + msg.obj.toString()," "+curCountSGK+" + "+pfHataliSGK+" = "+personelFotoSayisiSGK);
            if (curCountSGK+pfHataliSGK==personelFotoSayisiSGK) {
                if (pfHataliSGK > 0) {
                    new ShowToast(this, "" + pfHataliSGK + " tane fotoğraf indirilirken hata oluştu. Daha sonra tekrar deneyiniz");
                }

                db.dbstatDegerEkle("personelsgkevrak", "fromserver", MainActivity.userid);
                if (proDialog.isShowing())
                    proDialog.dismiss();
            }

        }
        /*if (per < 100)
            tvStatus.setText("Downloaded [" + curCount + "/" + (int)totalCount + "]");
        else
            tvStatus.setText("All images downloaded.");*/
        return true;
    }

    private void getPersonelSGKEvrak(){
        try {
            try {
                File photoPath = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/"+MainActivity.sgkDir);

                if (!photoPath.exists()) {
                    photoPath.mkdirs();
                }

            } catch (Exception ex) {
                Log.w("MainActivity", ex.getMessage());
            }

            db = new Database(getApplicationContext());
            ArrayList<HashMap<String, String>> res = db.getMultiResult(new String[]{"ID", "SGK_EVRAK"}, "tarim_istakip_personel", "SGK_EVRAK <>'' AND SGK_EVRAK<>'null' AND SGK_EVRAK<>'NULL' AND SGK_EVRAK_INDIRILDI=0");
            Log.w("sgkEvrak", "here - " + res.size());
            if (res.size() > 0) {
                int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
                if (proDialog == null) {
                    proDialog = new ProgressDialog(this);
                    proDialog.setCanceledOnTouchOutside(false);
                    proDialog.setCancelable(false);
                    proDialog.setTitle("Veriler");
                    proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    proDialog.setMax(100);
                }
                proDialog.setMessage("İndiriliyor... - Personel Sgk Evrak");
                proDialog.show();
                ThreadPoolExecutor executor = new ThreadPoolExecutor(
                        NUMBER_OF_CORES * 2,
                        NUMBER_OF_CORES * 2,
                        60L,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>()
                );

                curCountSGK = 0;
                pfHataliSGK  = 0;
                personelFotoSayisiSGK  = res.size();
                for (int i = 0; i < res.size(); i++) {
                    executor.execute(new PersonelSGKEvrakThread(i, res.get(i), new Handler(this)));
                }
            }else{
                db.dbstatDegerEkle("personelsgkevrak", "fromserver", MainActivity.userid);
            }
        } catch (Exception ex) {
            new ShowToast(this, "Hata: " + ex.getMessage());
        }
    }

    @Override
    public void PipeAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error) {
        if (type.equals("KartNoIptalOnline")) {
            if (data != null && data.size() > 0 && data.get(0).get("ID").equals(psBarkod.getText().toString())) {
                psKartno.setText("");
                imgCancelKartno.setVisibility(View.GONE);
                try {
                    DataFromService kartno = new DataFromService((ServiceCallBack) mactivity);
                    kartno.context = ParentCtxt;
                    kartno.uid = userid;
                    kartno.reqtype = "KartNoIptal2";
                    kartno.title = "KartNo İptal";
                    kartno.reqparam = new ArrayList<String[]>();
                    kartno.reqparam.add(new String[]{"op", "pushdatake"});
                    kartno.reqparam.add(new String[]{"kartno", barkodIcerik});
                    kartno.reqparam.add(new String[]{"sicilno", data.get(0).get("ID")});
                    kartno.reqparam.add(new String[]{"islem", barkodIslem});
                    kartno.reqparam.add(new String[]{"prgver", MainActivity.PROGRAM_VERSION});
                    kartno.resp = new ArrayList<String[]>();
                    kartno.resp.add(new String[]{"stat", "stat"});
                    kartno.execute();
                } catch (Exception ex) {
                    Log.w("PerKartUpdate", ex.getStackTrace().toString());
                }
            } else {
                new ShowToast(ParentCtxt, "Hata: Kart no " + psAd.getText().toString() + " " + psSoyad.getText().toString() + " tanımlı değil.");
            }
        }else if (type.equals("KartNoGuncelleOnline")) {
            if (data == null || data.size() == 0) {
                psKartno.setText("");
                imgCancelKartno.setVisibility(View.GONE);
                try {
                    DataFromService kartno = new DataFromService((ServiceCallBack) mactivity);
                    kartno.context = ParentCtxt;
                    kartno.uid = userid;
                    kartno.reqtype = "KartNoGuncelle2";
                    kartno.title = "KartNo Güncelle";
                    kartno.reqparam = new ArrayList<String[]>();
                    kartno.reqparam.add(new String[]{"op", "pushdatake"});
                    kartno.reqparam.add(new String[]{"kartno", barkodIcerik});
                    kartno.reqparam.add(new String[]{"sicilno",  psBarkod.getText().toString()});
                    kartno.reqparam.add(new String[]{"islem", barkodIslem});
                    kartno.reqparam.add(new String[]{"prgver", MainActivity.PROGRAM_VERSION});
                    kartno.resp = new ArrayList<String[]>();
                    kartno.resp.add(new String[]{"stat", "stat"});
                    kartno.execute();
                } catch (Exception ex) {
                    Log.w("PerKartUpdate", ex.getStackTrace().toString());
                }
            } else {
                new ShowToast(this, "Hata: Okutulan kart no " + data.get(0).get("AD").toString() + " " + data.get(0).get("SOYAD").toString() + " tanımlı.");
            }
        }else if (type.equals("KartNoIptal2Online")){
            if(data!=null && data.size()>0 && data.get(0).get("stat")=="true"){
                new ShowToast(ParentCtxt, psAd.getText() + " " + psSoyad.getText() + " için " + barkodIcerik + " kart nosu iptal edilmiştir.");
            }
        }else if (type.equals("KartNoGuncelle2Online")){
            if(data!=null && data.size()>0 && data.get(0).get("stat").equals("true")){
                new ShowToast(this, psAd.getText() + " " + psSoyad.getText() + " için " + barkodIcerik + " kart nosu tanımlanmıştır.");
                psKartno.setText(barkodIcerik);
            }else{
                new ShowToast(this, "Eşleştirmede hata oluştu");
            }
        }else if(type.equals("File2Server")){
            if(stat) {
                if (error.indexOf("Nufus::")>=0) {
                    String f=error.replace("Nufus::","");
                    String perbelbilgi = "{\"pbelid\":[";
                    perbelbilgi += "{\"personelid\":\"" + psBarkod.getText() + "\",\"ad\":\"" + (f.equals("1")?nufus1Filename:nufus2Filename) + "\",\"uid\":\"" + MainActivity.userid + "\",\"tur\":\"19\"},";
                    perbelbilgi = perbelbilgi.substring(0, perbelbilgi.length() - 1);
                    perbelbilgi += "]}";

                    DataFromService perbelkaydet = new DataFromService((ServiceCallBack) this);
                    perbelkaydet.context = ParentCtxt;
                    perbelkaydet.uid = MainActivity.userid;
                    perbelkaydet.reqtype = "PersonelBelgeKaydet";
                    perbelkaydet.title = "Personel Belge Kaydet";
                    perbelkaydet.reqparam = new ArrayList<String[]>();
                    perbelkaydet.reqparam.add(new String[]{"op", "pushdataperbel"});
                    perbelkaydet.reqparam.add(new String[]{"req", perbelbilgi});
                    perbelkaydet.resp = new ArrayList<String[]>();
                    perbelkaydet.resp.add(new String[]{"stat", "stat"});
                    perbelkaydet.execute();
                }
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //ibasTarih.setText(day+" / "+month+" / "+year);
            ibitBitTarih.setText(day+" / "+(month+1)+" / "+year);
        }
    }
}
