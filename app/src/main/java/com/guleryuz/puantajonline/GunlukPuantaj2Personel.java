package guleryuz.puantajonline;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.HashMap;

import barcodescanner.app.com.barcodescanner.R;

/**
 * Created by Asersoft on 28.02.2017.
 */

public class GunlukPuantaj2Personel extends AppCompatActivity implements View.OnClickListener   {
    private Database db;
    private static Context ParentCtxt;
    private RelativeLayout layoutPS;
    private LinearLayout layoutPL, layoutSync, svScroll, layoutPSFoto;
    private Button btnPSBarkodOku, btnPSBarkodYeni,ibasGorevekle;
    private EditText psBarkod, psTC, psKartno, psAd, psSoyad;
    private TextView psDogumTarihi, psCinsiyet, psGorev, psBolge, psBolge2, psBolge3, psBolge4, psBolge5;
    private TextView psEkiplideri, psEkiplideri2, psEkiplideri3,personelSorgulama, pssgkevrak;
    private ImageView psFoto, imgChangeKartno, imgAddPerson;
    private File imgFile;
    private static Activity mactivity;
    private static String activeButton;
    private static boolean sorgulamaYapildi=false;
    public String SGK_Evrak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personel_sorgulama);
        ParentCtxt = this;
        mactivity=this;

        Intent intent =getIntent();
        sorgulamaYapildi=false;
        personelSorgulama=(TextView)findViewById(R.id.personelSorgulama);
        personelSorgulama.setText("Personel Görev Ekle");
        svScroll=(LinearLayout) findViewById(R.id.svScroll);
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
        psAd=(EditText)findViewById(R.id.psad);
        psSoyad=(EditText)findViewById(R.id.pssoyad);
        pssgkevrak=(TextView)findViewById(R.id.pssgkevrak);
        layoutPS=(RelativeLayout)findViewById(R.id.layoutPS);
        btnPSBarkodOku=(Button)findViewById(R.id.psbarkodoku);
        btnPSBarkodOku.setOnClickListener(this);
        btnPSBarkodYeni=(Button)findViewById(R.id.psbarkodyeni);
        btnPSBarkodYeni.setOnClickListener(this);
        layoutPSFoto=(LinearLayout)findViewById(R.id.layoutPSFoto);
        psFoto=(ImageView)findViewById(R.id.psFoto);
        ibasGorevekle=(Button)findViewById(R.id.ibasGorevEkle);
        ibasGorevekle.setOnClickListener(this);
        ibasGorevekle.setVisibility(View.VISIBLE);

        psKartno=(EditText)findViewById(R.id.psKartno);
        //psKartno.setEnabled(false);
        imgChangeKartno=(ImageView)findViewById(R.id.imgChangeKartno);
        imgChangeKartno.setVisibility(View.GONE);

        imgAddPerson=(ImageView)findViewById(R.id.imgAddPerson);
        imgAddPerson.setVisibility(View.VISIBLE);
        imgAddPerson.setOnClickListener(this);

        db=new Database(getApplicationContext());

    }

    public void onClick(View v){
        if (v.getId()==R.id.ibasBtnIptal){
            finish();
        }else if(v.getId()==R.id.ibasGorevEkle){
            if (sorgulamaYapildi){//!psAd.getText().toString().equals("") && ){
                Intent intent=new Intent();
                intent.putExtra("sicilno",psBarkod.getText().toString());
                intent.putExtra("adi",psAd.getText().toString()+" "+psSoyad.getText().toString());
                intent.putExtra("cinsiyet",psCinsiyet.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
            }else{
                new ShowToast(this, "Lütfen Personel Seçiniz");
            }
        }else if(v.getId()==R.id.psbarkodoku){
            try {
                activeButton="PSorgula";

                if (psKartno.getText().toString().length()==0 && psBarkod.getText().toString().length() == 0 && psTC.getText().toString().length() == 0 && psAd.getText().toString().length() == 0 && psSoyad.getText().toString().length() == 0) {
                        try {
                        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                        scanIntegrator.initiateScan();
                    } catch (Exception ex) {
                        Log.w("Barcode", "nothing");
                    }
                } else {
                    db = new Database(getApplicationContext());

                    String kartnowithpad=psKartno.getText().toString();
                    int padToLength=6;
                    if(kartnowithpad.length()<padToLength){
                        kartnowithpad=String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s",0,kartnowithpad);
                    }

                    HashMap<String, String> personelbilgileri = db.personelBilgileriGetir(kartnowithpad, psBarkod.getText().toString(), psTC.getText().toString(), "", psAd.getText().toString(), psSoyad.getText().toString());
                    if (personelbilgileri.size() > 0) {
                        sorgulamaYapildi=true;
                        imgChangeKartno.setVisibility(View.VISIBLE);
                        imgChangeKartno.setOnClickListener(this);
                        psKartno.setText((personelbilgileri.get("KARTNO").equals("0")?"":personelbilgileri.get("KARTNO")));
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

                        if (personelbilgileri.get("RESIM_INDIRILDI").equals("1")) {
                            imgFile = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/" + personelbilgileri.get("RESIM"));

                            if (imgFile.exists()) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
                                psFoto.setImageBitmap(myBitmap);
                                psFoto.setOnClickListener(this);
                                layoutPSFoto.setVisibility(View.VISIBLE);
                            } else {
                                layoutPSFoto.setVisibility(View.GONE);
                                psFoto.setImageBitmap(null);
                            }
                        } else {
                            imgFile=null;
                            layoutPSFoto.setVisibility(View.GONE);
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
                        new ShowToast(this, "Personel bilgisi bulunmadı.");
                    }
                }
            }catch (Exception ex){
                Log.w("GPP err", ex.getMessage());
            }
        }else if(v.getId()==R.id.psbarkodyeni) {
            sorgulamaYapildi=false;
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
            psFoto.setImageBitmap(null);
            imgChangeKartno.setVisibility(View.GONE);
            activeButton="";
        }else if(v.getId()==R.id.imgChangeKartno){
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

        }else  if(v.getId()==R.id.ibasGorevEkle){

        }else if(v.getId()==R.id.psFoto){
            if(imgFile!=null) {
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", imgFile.getAbsolutePath());
                startActivity(intent);
            }
        }else if(v.getId()==R.id.imgAddPerson){
            Intent i = new Intent(getApplicationContext(),PersonelEkle.class);
            i.putExtra("userid", MainActivity.userid);
            startActivity(i);
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
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                if (activeButton.equals("PSorgula")) {
                    psKartno.setText(scanContent);
                    db = new Database(getApplicationContext());
                    //Log.w("onActivityResult", scanContent+" "+userid);
                    if (db.barkodDogrula(scanContent, MainActivity.userid)) {
                        HashMap<String, String> personelbilgileri = db.personelBilgileriGetir(scanContent);
                        if (personelbilgileri.size() > 0) {
                            sorgulamaYapildi=true;
                            imgChangeKartno.setVisibility(View.VISIBLE);
                            imgChangeKartno.setOnClickListener(this);
                            psKartno.setText((personelbilgileri.get("KARTNO").equals("0") ? "" : personelbilgileri.get("KARTNO")));
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
                            try {
                                if (personelbilgileri.get("RESIM_INDIRILDI").equals("1")) {
                                    imgFile = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + personelbilgileri.get("RESIM"));

                                    if (imgFile.exists()) {
                                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                        psFoto.setImageBitmap(myBitmap);
                                        psFoto.setOnClickListener(this);
                                        layoutPSFoto.setVisibility(View.VISIBLE);
                                    } else {
                                        layoutPSFoto.setVisibility(View.GONE);
                                        psFoto.setImageBitmap(null);
                                    }
                                } else {
                                    imgFile = null;
                                    layoutPSFoto.setVisibility(View.GONE);
                                    psFoto.setImageBitmap(null);
                                }
                            }catch (Exception ex){
                                imgFile = null;
                                layoutPSFoto.setVisibility(View.GONE);
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
                            //Toast toast = Toast.makeText(this, "Personel bilgisi bulunmadı.", Toast.LENGTH_SHORT);
                            //toast.show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                            builder.setTitle("Uyarı");
                            builder.setMessage("Kart No ya personel eşleştirmek ister misiniz?");

                            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), PersonelKartEsle.class);
                                    intent.putExtra("userid", MainActivity.userid);
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
                        //Toast toast = Toast.makeText(this, "Barkod hatası", Toast.LENGTH_SHORT);
                        //toast.show();
                        //imgChangeKartno.setVisibility(View.GONE);
                        if (!scanContent.equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                            builder.setTitle("Uyarı");
                            builder.setMessage("Kart No ya personel eşleştirmek ister misiniz?");

                            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

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
                        } else {
                            new ShowToast(this, "Barkod hatası");
                        }
                    }

                } else if (activeButton.equals("imgChangeKartno")) {
                    if(scanContent!=null) {
                        db = new Database(getApplicationContext());
                        HashMap<String, String> kartno = db.getOneRow(new String[]{"ID", "AD", "SOYAD"}, "tarim_istakip_personel", "kartno='" + scanContent + "'");
                        if (kartno.size() == 0) {
                            psKartno.setText(scanContent);
                            db.personelKartnoUpdate(scanContent, psBarkod.getText().toString(), MainActivity.userid);
                            new ShowToast(this, psAd.getText()+ " "+psSoyad.getText() + " için " + scanContent + " kart nosu tanımlanmıştır.");
                        } else {
                            new ShowToast(this, "Hata: Okutulan kart no " + kartno.get("AD").toString() + " " + kartno.get("SOYAD").toString() + " tanımlı.");
                        }
                    }
                }
            }
        }catch (Exception ex){
            //Log.w("onActivityResult", ex.getMessage());
        }
    }
}
