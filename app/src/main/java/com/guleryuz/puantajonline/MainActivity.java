package guleryuz.puantajonline;

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
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.view.View.OnClickListener;
import android.content.Intent;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import guleryuz.puantajonline.CallBacks.TaskCallback;
import barcodescanner.app.com.barcodescanner.R;
import barcodescanner.app.com.barcodescanner.synchronize.*;
import guleryuz.puantajonline.synchronize.Ekiplideri;
import guleryuz.puantajonline.synchronize.Firma;
import guleryuz.puantajonline.synchronize.FirmaBolge;
import guleryuz.puantajonline.synchronize.Gorev;
import guleryuz.puantajonline.synchronize.Personel;
import guleryuz.puantajonline.synchronize.PersonelBelgeTur;
import guleryuz.puantajonline.synchronize.PersonelFotoThread;
import guleryuz.puantajonline.synchronize.PersonelSGKEvrakThread;
import guleryuz.puantajonline.synchronize.PushDataToServer;
import guleryuz.puantajonline.synchronize.Servis;
import guleryuz.puantajonline.synchronize.SifremiUnuttum;
import guleryuz.puantajonline.synchronize.TarihGuncelle;
import guleryuz.puantajonline.synchronize.Urun;
import guleryuz.puantajonline.synchronize.Users;
import guleryuz.puantajonline.synchronize.UsersUpdate;
import guleryuz.puantajonline.synchronize.Yetkili;


/*
* 6.4.2
* Date: 11.06.2017
* Personel Sorgulamaya fotoğraf ekleme özelliği eklendi.
* ------
* 6.4.3
* Date: 12.06.2017
* Servis Kişi Başı kısmı eklendi.
* ------
* 6.4.4
* Date: 14.06.2017
* Personel Sicilno sorunu düzeltildi.
* ------
* 6.4.5
* Start Date: 15.06.2017
* End Date: 29.06.2017
* Puantaj Listeleme tarih kısmı seçilebilir hale getirildi.
* "Servis yok" seçilmeden geçildiğinde hata verme sorunu çözüldü.
* Temsilciye birden fazla fiş no aralığı atanabilme özelliği aktif edildi.
* Puantaj gönderim sonrasında eski personeller varsa sonrasında güncelleme sicilno güncellemesi yapıldı.
* Personel sorgulama kısmına fotoğraf ekleme gibi nüfus fotokopisi ekleyebilecekler(2 adet arkalı-önlü)
* Versiyon bilgisi sunucu tarafına aktarıldı.
* ------
* 6.4.6
* Start Date: 09.07.2017
* End Date: 12.07.2017
* ?Fiş no ekranında düzenleme özelliği aktif edilmedi(Tarla/ürün kısmı puantaj kısmına eklendiği için revize edilmedi)
* Nüfus fotoğrafını eklerken oluşan hata düzeltildi.
* Puantaj tarla/ürün kısmı ilk ekrandan çalışma ekleme ekranına alındı.
* İş sonu verilen personeli mobil kısmında update edilecek şekilde düzenlendi
* *Versiyon kontrolü aktif edildi ve update edilme özelliği eklendi.Versiyonu gelmeyen kullanıcılar login edilmeyecek.
* ------
* 6.4.8
* Start Date: 19.07.2017
* End Date: 19.07.2017
* Personel güncelleme ekranındaki bug düzeltildi.
* *Versiyon kontrolü aktif edildi ve update edilme özelliği eklendi.Versiyonu gelmeyen kullanıcılar login edilmeyecek.
* ------
* 6.4.9
* Start Date: 22.07.2017
* End Date: 22.07.2017
* Personel foto güncelleme ekranındaki bug düzeltildi.
* Personel eklenirken yaş kontrolü eklendi.
* *Versiyon kontrolü aktif edildi ve update edilme özelliği eklendi.Versiyonu gelmeyen kullanıcılar login edilmeyecek.
* ------
* 6.5.0
* Start Date: 26.07.2017
* End Date: 26.07.2017
* Yeni personel girişine tc kimlik nosuz giriş eklendi. 15+currenttimemilis
* *Versiyon kontrolü aktif edildi ve update edilme özelliği eklendi.Versiyonu gelmeyen kullanıcılar login edilmeyecek.*
* ------
* 6.5.1
* Start Date: 12.10.2017
* End Date: 14.10.2017
* Sistem tarihi değiştirildiğinde o anki tarihi sabitleyebilmeli.
* ------
* 6.5.2
* Start Date: 10.05.2018
* End Date: 11.05.2018
* Puantaj Bekleyenlerde bulunan toplam personel sayısı sorunu düzeltildi.
* Günlük Puantaj ekleme Görev ekranında Ekip Lideri adı eklendi.
* ------
* 6.5.3
* Start Date: 12.05.2018
* End Date: 12.05.2018
* Honeywell Scanpad EDA50 entegrasyonu yapıldı.
* ------
* 6.5.4
* Start Date: 12.05.2018
* End Date: 12.05.2018
* Kullanıcıya Puantaj yetkisine göre puantaj kısımları yetkilendirilmesi yapıldı.
* ------
* 6.5.5
* Start Date: 15.05.2018
* End Date: 15.05.2018
* Fiş zimmet kontrolü kapatıldı.
* ------
* 6.5.6
* Start Date: 22.05.2018
* End Date: 22.05.2018
* Bölgeye göre ekip liderinin listelenmemesi sorunu düzeltildi.
* ------
* 6.5.7
* Start Date: 09.06.2018
* End Date: 10.06.2018
* Puantaj tarihinde oluşan sorunun düzeltilmesi için önceki tarih kontrolü kaldırıldı.
* Fişno ve Ekli döküman kısımları zorunlu alan olmaktan çıkarıldı.
* Puantaj bilgileri guid ile ilişkilendirildi.
* * ------
 * 6.5.8
 * Start Date: 07.08.2018
 * End Date: 07.08.2018
 * Personel, personel foto ve sgk evrak kısımlarındaki progress kısmında oluşan hata düzeltildi.
 * * * ------
 * 6.5.9
 * Start Date: 02.03.2019
 * End Date: 02.03.2019
 * Puantaj .'li mesai aktif edildi.
*/

public class MainActivity extends AppCompatActivity implements OnClickListener, TaskCallback, Handler.Callback {
    //Klasör adları ve program versiyon ayarları bu kısımdan yapılır.
    //Her uygulama revizyonunda PROGRAM_VERSION değeri değiştirilmelidir.
    public final static String rootDir=".Guleryuz", attachDir=".Ekler", docDir=".Belgeler", sgkDir="SGK", PROGRAM_VERSION="6.5.8";
    private ProgressDialog proDialog;
    private long personelFotoSayisi=0, curCount, pfHatali=0;
    private long personelFotoSayisiSGK=0, curCountSGK, pfHataliSGK=0;

    private Button btnPSorgulama, btnPersonelEkle, btnPuantajListeleme, btnIseBaslama, btnSync, btnSyncAl, btnPuantajWait, btnPuantajSent , loginBtn;
    private Menu menuu;
    private static Activity mactivity;
    private LinearLayout llayoutPersonel, llayoutSync, llayoutWaitNSent;
    private LinearLayout llayoutDuyuru,wvLayout;
    public static TextView txtsyncInfo;
    private TextView txtProgVersion;
    final Handler handler = new Handler();
    Timer timer = new Timer();
    //Personel Ekle
    private static final int REQUEST_PE=40;

    //Personel Sorgulama
    private ScrollView  svScrollSync;//svScrollIBas, svScrollPL
    private LinearLayout layoutSync, svScroll, layoutPSFoto, layoutPSFotoEmpty, layoutPSNufusFoto;//layoutPL
    private RelativeLayout layoutPS;
    private Button btnPSBarkodOku, btnPSBarkodYeni, psNufusIptalet, psNufusKaydet;
    private EditText psBarkod, psTC, psKartno, psAd, psSoyad;
    private TextView psDogumTarihi, psCinsiyet, psGorev, psBolge, psBolge2, psBolge3, psBolge4, psBolge5;
    private TextView psEkiplideri, psEkiplideri2, psEkiplideri3, pssgkevrak;
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
    private static final int REQUEST_GP2=2;
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
    private File imgFile;

    private GoogleApiClient client;

    private static BarcodeReader barcodeReader;
    private AidcManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.guleryuz_title);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>&nbsp;&nbsp;Güleryüz Puantaj Takibi</font>"));
        setContentView(R.layout.activity_main);
        ParentCtxt=this;
        mactivity=this;

        exitApp=3;

        //For create rootFolder
        try{
            File photoPath = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir);

            if (!photoPath.exists()) {
                photoPath.mkdirs();
            }

            File photoPath2 = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir +"/"+MainActivity.sgkDir+"/");

            if (!photoPath2.exists()) {
                photoPath2.mkdirs();
            }

            File photoPath3 = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir +"/"+MainActivity.docDir+"/");

            if (!photoPath3.exists()) {
                photoPath3.mkdirs();
            }

            File photoPath4 = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir +"/"+MainActivity.attachDir+"/");

            if (!photoPath4.exists()) {
                photoPath4.mkdirs();
            }
        }catch (Exception ex){
            Log.w("MainActivity",ex.getMessage());
        }

        /*File oldfolder = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir );
        File newfolder = new File(Environment.getExternalStorageDirectory() + "/Guleryuz");
        oldfolder.renameTo(newfolder);

        oldfolder = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.sgkDir );
        newfolder = new File(Environment.getExternalStorageDirectory() + "/Guleryuz/SGK");
        oldfolder.renameTo(newfolder);*/

// copy db to sdcard


    try {
            String currentDBPath = "//data//user//0//barcodescanner.app.com.barcodescanner//databases//sqlite_database10.sqlite";
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
            String currentDBPath = "//data//user//0//barcodescanner.app.com.barcodescanner//databases//sqlite_database10.sqlite";
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
        btnSync = (Button)findViewById(R.id.buttonSync);
        btnPuantajWait=(Button)findViewById(R.id.buttonWait);
        btnPuantajWait.setOnClickListener(this);
        btnPuantajSent=(Button)findViewById(R.id.buttonSent);
        btnPuantajSent.setOnClickListener(this);
        llayoutPersonel=(LinearLayout)findViewById(R.id.llayoutPersonel);
        llayoutSync=(LinearLayout)findViewById(R.id.llayoutSync);
        llayoutWaitNSent=(LinearLayout)findViewById(R.id.llayoutWaitNSent);
        llayoutDuyuru=(LinearLayout)findViewById(R.id.llayoutDuyuru);
        wvLayout=(LinearLayout)findViewById(R.id.wvLayout);
        txtsyncInfo=(TextView)findViewById(R.id.txtsyncInfo);
        //Main footer info task
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            txtsyncInfo.setText(getSyncData());
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000);
        //--

        //Date sync
        TimerTask dateAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Log.w("dateAsyncTask", "active");
                            Connectivity conn=new Connectivity();
                            if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                                TarihGuncelle tg=new TarihGuncelle();
                                tg.context=MainActivity.this;
                                tg.db=db;
                                tg.uid=userid;
                                tg.execute();
                            }
                        } catch (Exception e) {
                            Log.w("dateAsyncTask err:", e.getMessage());
                        }
                    }
                });
            }
        };
        timer.schedule(dateAsyncTask, 0, 300000);
        //--

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
        svScrollSync=(ScrollView)findViewById(R.id.svScrollSync);
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
        txtSDToServerInfo=(TextView)findViewById(R.id.txtSDToServerInfo);
        //---

        //Bilgileri Al
        btnSyncAl=(Button)findViewById(R.id.buttonSyncAl);
        btnSyncAl.setOnClickListener(this);
        //--

        //formatTxt = (TextView)findViewById(R.id.scan_format);
        //contentTxt = (TextView)findViewById(R.id.scan_content);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        chkSifremiHatirla=(CheckBox)findViewById(R.id.chkSifremiHatirla);
        btnSifremiUnuttum=(TextView)findViewById(R.id.btnSifremiUnuttum);
        btnSifremiUnuttum.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        //username.setText("bergama");
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
        btnSync.setOnClickListener(this);


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
                llayoutWaitNSent.setVisibility(View.VISIBLE);
                llayoutSync.setVisibility(View.VISIBLE);
                llayoutDuyuru.setVisibility(View.VISIBLE);
                btnPersonelEkle.setVisibility(View.VISIBLE);
                btnPSorgulama.setVisibility(View.VISIBLE);
                btnPuantajListeleme.setVisibility(View.VISIBLE);
                btnIseBaslama.setVisibility(View.VISIBLE);
                btnSync.setVisibility(View.VISIBLE);
                btnSyncAl.setVisibility(View.VISIBLE);
                btnPuantajSent.setVisibility(View.VISIBLE);
                btnPuantajWait.setVisibility(View.VISIBLE);
                menuu.findItem(R.id.homebtn).setVisible(false);
                //layoutIBas.setVisibility(View.GONE);
                layoutIBit.setVisibility(View.GONE);
                layoutPS.setVisibility(View.GONE);
                //layoutPL.setVisibility(View.GONE);
                svScroll.setVisibility(View.GONE);
                svScrollSync.setVisibility(View.GONE);
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
            llayoutSync.setVisibility(View.GONE);
            llayoutWaitNSent.setVisibility(View.GONE);
            llayoutDuyuru.setVisibility(View.GONE);
            btnPuantajListeleme.setVisibility(View.GONE);
            btnPersonelEkle.setVisibility(View.GONE);
            btnIseBaslama.setVisibility(View.GONE);
            btnSync.setVisibility(View.GONE);
            btnSyncAl.setVisibility(View.GONE);
            btnPuantajSent.setVisibility(View.GONE);
            btnPuantajWait.setVisibility(View.GONE);
            menuu.findItem(R.id.homebtn).setVisible(true);
            btnPSorgulama.setVisibility(View.GONE);
            //layoutIBas.setVisibility(View.GONE);
            layoutIBit.setVisibility(View.GONE);
            //layoutPL.setVisibility(View.GONE);
            layoutPS.setVisibility(View.VISIBLE);
            layoutSync.setVisibility(View.GONE);
            svScroll.setVisibility(View.VISIBLE);
            //svScrollPL.setVisibility(View.GONE);
            //svScrollIBas.setVisibility(View.GONE);
            svScrollSync.setVisibility(View.GONE);

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
        }else if(v.getId()==R.id.buttonSync){
            llayoutPersonel.setVisibility(View.GONE);
            llayoutSync.setVisibility(View.GONE);
            llayoutWaitNSent.setVisibility(View.GONE);
            llayoutDuyuru.setVisibility(View.GONE);
            btnPSorgulama.setVisibility(View.GONE);
            btnIseBaslama.setVisibility(View.GONE);
            btnSync.setVisibility(View.GONE);
            btnSyncAl.setVisibility(View.GONE);
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
            btnSync.setVisibility(View.GONE);
            btnSyncAl.setVisibility(View.GONE);
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

            activeButton="buttonSync";
        }else if(v.getId()==R.id.loginBtn){
            username.setText(username.getText().toString().trim().toLowerCase());
            db=new Database(getApplicationContext());
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
                            btnSync.setVisibility(View.VISIBLE);
                            btnSyncAl.setVisibility(View.VISIBLE);
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
                            HashMap<String, String>  dbstat=db.getDBStatus("fromserver", MainActivity.userid);
                            Log.w("synctable",""+dbstat.size());
                            if(dbstat.size()!=db.DBSYNC_TABLES) {
                                btnSyncAl.performClick();
                            }
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
            }
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
                SGK_Evrak="";
                sorgulananSicilno="";
                db=new Database(getApplicationContext());
                //str_pad
                String kartnowithpad=psKartno.getText().toString();
                int padToLength=6;
                if(kartnowithpad.length()<padToLength){
                    kartnowithpad=String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s",0,kartnowithpad);
                }
                //---
                HashMap<String, String> personelbilgileri = db.personelBilgileriGetir(kartnowithpad, psBarkod.getText().toString(), psTC.getText().toString(), "", psAd.getText().toString(), psSoyad.getText().toString());
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
                           /* if(myBitmap.getWidth()/2048>=1){
                                psFoto.getLayoutParams().height = 150;
                            }else{
                                psFoto.getLayoutParams().height = 300;
                            }*/

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
                    }
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
            layoutPSFoto.setVisibility(View.GONE);
            layoutPSFotoEmpty.setVisibility(View.GONE);
            layoutPSNufusFoto.setVisibility(View.GONE);
            psFoto.setImageBitmap(null);
            imgChangeKartno.setVisibility(View.GONE);
            imgCancelKartno.setVisibility(View.GONE);
            sorgulananSicilno="";
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
            builder.setTitle("Uyarı");
            builder.setMessage("Kart No "+psKartno.getText().toString()+" silmek istediğinizden emin misiniz?");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String scanContent=psKartno.getText().toString();
                        HashMap<String, String> kartno = db.getOneRow(new String[]{"ID", "AD", "SOYAD"}, "tarim_istakip_personel", "kartno='" + scanContent + "'");
                        if (kartno.get("ID").equals(psBarkod.getText().toString())) {
                            psKartno.setText("");
                            imgCancelKartno.setVisibility(View.GONE);
                            db.personelKartnoUpdate("", psBarkod.getText().toString(), userid);
                            db.personelKartnoIptal(scanContent, psBarkod.getText().toString(), userid);
                            new ShowToast(ParentCtxt, psAd.getText() + " " + psSoyad.getText() + " için " + scanContent + " kart nosu iptal edilmiştir.");
                        } else {
                            new ShowToast(ParentCtxt, "Hata: Kart no " + psAd.getText().toString() + " " + psSoyad.getText().toString() + " tanımlı değil.");
                        }
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
            if(imgFile!=null) {
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", imgFile.getAbsolutePath());
                startActivity(intent);
            }
        }else if(v.getId()==R.id.pssgkevrak) {
            if (!SGK_Evrak.equals("")){
                if(SGK_Evrak.toLowerCase().indexOf(".pdf")>0){
                    File sgkevrakFile = new  File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.sgkDir+"/"+SGK_Evrak);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(sgkevrakFile), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }else{
                    File sgkevrakFile = new  File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.sgkDir+"/"+SGK_Evrak);
                    if(sgkevrakFile.isFile()) {
                        Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                        intent.putExtra("photo", sgkevrakFile.getAbsolutePath());
                        startActivity(intent);
                    }else{
                        new ShowToast(this, "SGK Evrak dosyası bulunamadı");
                    }
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
                intent.putExtra("photo", psNufus1.getAbsolutePath());
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
                intent.putExtra("photo", psNufus2.getAbsolutePath());
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
                    txtsyncInfo.setText(getSyncData());
                } catch (Exception e) {
                    Log.w("pesync",e.getMessage());
                }
            }else if(requestCode==REQUEST_PE) {
                try {
                    txtsyncInfo.setText(getSyncData());
                } catch (Exception e) {
                    Log.w("pesync", e.getMessage());
                }
            }else if(requestCode==REQUEST_PSTAKE_PHOTO){
                Log.w("PSNewFoto", ""+resultCode);
                try{
                    if(psNewFoto!=null && psNewFoto.exists()) {
                        imgFile=psNewFoto;
                        String psFotoName=psNewFoto.getAbsolutePath();
                        psFotoName = psFotoName.substring(psFotoName.lastIndexOf('/')+1);
                        db.personelFotoEkle(sorgulananSicilno, psFotoName);
                        layoutPSFotoEmpty.setVisibility(View.GONE);
                        layoutPSFoto.setVisibility(View.VISIBLE);
                        //yeni fotoyu goster
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);

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
                            String kartnowithpad = scanContent;
                            int padToLength = 6;
                            if (kartnowithpad.length() < padToLength) {
                                kartnowithpad = String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s", 0, kartnowithpad);
                            }

                            scanContent = kartnowithpad;

                            HashMap<String, String> kartno = db.getOneRow(new String[]{"ID", "AD", "SOYAD"}, "tarim_istakip_personel", "kartno='" + scanContent + "'");
                            if (kartno.size() == 0) {
                                psKartno.setText(scanContent);
                                db.personelKartnoUpdate(scanContent, psBarkod.getText().toString(), userid);
                                new ShowToast(this, psAd.getText() + " " + psSoyad.getText() + " için " + scanContent + " kart nosu tanımlanmıştır.");
                            } else {
                                new ShowToast(this, "Hata: Okutulan kart no " + kartno.get("AD").toString() + " " + kartno.get("SOYAD").toString() + " tanımlı.");
                            }
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
                        db = new Database(getApplicationContext());
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
                                    imgFile = new  File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+personelbilgileri.get("RESIM"));

                                    if(imgFile.exists()){
                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inSampleSize = 8;
                                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
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
                                    imgFile=null;
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
                        }
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
                llayoutSync.setVisibility(View.VISIBLE);
                llayoutWaitNSent.setVisibility(View.VISIBLE);
                llayoutDuyuru.setVisibility(View.VISIBLE);
                btnPSorgulama.setVisibility(View.VISIBLE);
                btnPuantajListeleme.setVisibility(View.VISIBLE);
                btnIseBaslama.setVisibility(View.VISIBLE);
                btnSync.setVisibility(View.VISIBLE);
                btnSyncAl.setVisibility(View.VISIBLE);
                btnPuantajSent.setVisibility(View.VISIBLE);
                btnPuantajWait.setVisibility(View.VISIBLE);
                btnPersonelEkle.setVisibility(View.VISIBLE);

                layoutIBit.setVisibility(View.GONE);
                layoutPS.setVisibility(View.GONE);
                layoutSync.setVisibility(View.GONE);
                svScroll.setVisibility(View.GONE);
                svScrollSync.setVisibility(View.GONE);


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
                Uri.parse("android-app://barcodescanner.app.com.barcodescanner/http/host/path")
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
                Uri.parse("android-app://barcodescanner.app.com.barcodescanner/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void UserAsyncFinish(boolean userStat, String error){
        if (userStat) {
            loginBtn.callOnClick();
        }else{
            new ShowToast(this, (error!=""?error:"Kullanıcı/Şifre Hatası"));
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

            txtsyncInfo.setText(getSyncData());

           loadSyncView();
        }
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

    //Ekranın alt kısmındaki senkronizasyon bekleyen veri sayılarını döndürür
    private String getSyncData(){
        String res="";
        int personelSayisi=0;
        int personelKartGuncellemeSayisi=0;
        int personelFotoGuncellemeSayisi=0;//Mevcut fotosu olmayan personeller için
        int personelNufusGuncellemeSayisi=0;//Nüfusu olmayan personeller için
        int puantajSayisi=0;
        try{
            db = new Database(getApplicationContext());
            ArrayList<HashMap<String,String>> qfpe = db.getMultiResult(new String[]{"OID"},"tarim_istakip_personel","YENI_KAYIT=1 and AKTARILDI=0 and user_id='"+MainActivity.userid+"'");
            personelSayisi=qfpe.size();

            ArrayList<HashMap<String,String>> qfpe2 = db.getMultiResult(new String[]{"OID"},"tarim_istakip_personel","KARTNO_GUNCELLENDI=1 and KARTNO_GUNCEL_AKTARILDI=0 and user_id='"+MainActivity.userid+"'");
            personelKartGuncellemeSayisi=qfpe2.size();

            ArrayList<HashMap<String,String>> qfpe3 = db.getMultiResult(new String[]{"OID"},"tarim_istakip_personel","YENI_RESIM=1 and YENI_RESIM_AKTARILDI=0 and user_id='"+MainActivity.userid+"'");
            personelFotoGuncellemeSayisi=qfpe3.size();

            ArrayList<HashMap<String,String>> qfpe4 = db.getMultiResult(new String[]{"DISTINCT PERSONELID"},"tarim_istakip_personel_belge","AKTARILDI IN (-1,0) and tur='19' and user_id='"+MainActivity.userid+"'");
            personelNufusGuncellemeSayisi=qfpe4.size();

            ArrayList<HashMap<String,String>> qfpu = db.getMultiResult(new String[]{"OID"},"tarim_istakip_calisma","AKTARILDI IN (-1, 0) and user_id='"+MainActivity.userid+"'");
            puantajSayisi=qfpu.size();

            Log.w("getSyncData",""+qfpe.size()+" "+qfpu.size());

        }catch (Exception e){

        }

        return String.format("Bekleyen Yeni/Güncel Personel: %s/%s - Puantaj: %s",personelSayisi, personelKartGuncellemeSayisi+personelFotoGuncellemeSayisi+personelNufusGuncellemeSayisi, puantajSayisi);
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
