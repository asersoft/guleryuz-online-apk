package guleryuz.puantajonline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import android.os.Handler;

import barcodescanner.app.com.barcodescanner.R;

/**
 * Created by Asersoft on 19.03.2017.
 */

public class PersonelGoruntule extends AppCompatActivity implements View.OnClickListener {
    private TextView peKartno, peSicilno, peTC, peAd, peSoyad, peDogumTarihi, peCinsiyet, peBolge, peCalismaalani, peEkiplideri;
    private Button peBtnIptal;
    private ImageView peFoto;
    private LinearLayout layoutPEFoto;
    private File photoFile;

    private Database db;
    private static Context ParentCtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personel_goruntule);
        ParentCtxt = this;

        peAd = (TextView) findViewById(R.id.pead);
        peTC = (TextView) findViewById(R.id.petc);
        peKartno = (TextView) findViewById(R.id.pekartno);
        peSicilno = (TextView) findViewById(R.id.pesicilno);
        peDogumTarihi = (TextView) findViewById(R.id.pedogumtarihi);
        peCinsiyet = (TextView) findViewById(R.id.pecinsiyet);
        peBolge = (TextView) findViewById(R.id.pebolge);
        peCalismaalani = (TextView) findViewById(R.id.pecalismaalani);
        peEkiplideri = (TextView) findViewById(R.id.peekiplideri);
        peFoto=(ImageView)findViewById(R.id.peFoto);
        layoutPEFoto=(LinearLayout)findViewById(R.id.layoutPEFoto);

        Intent intent=getIntent();
        if(intent.hasExtra("kartno")){
            db=new Database(getApplicationContext());
            HashMap<String, String> personelbilgileri = db.personelBilgileriGetir(intent.getStringExtra("kartno"),"","","");
            if (personelbilgileri.size() > 0) {
                peKartno.setText((personelbilgileri.get("KARTNO").equals("0") ? "" : personelbilgileri.get("KARTNO")));
                peSicilno.setText(personelbilgileri.get("ID"));
                peTC.setText(personelbilgileri.get("TC"));
                peAd.setText(personelbilgileri.get("AD") + " " + personelbilgileri.get("SOYAD"));
                peDogumTarihi.setText(personelbilgileri.get("DOGUMTARIHI"));
                peCinsiyet.setText(personelbilgileri.get("CINSIYET"));
                peBolge.setText(personelbilgileri.get("BOLGE"));
                peCalismaalani.setText(personelbilgileri.get("BOLGE2"));
                peEkiplideri.setText(personelbilgileri.get("EKIP_LIDERI"));
                if (personelbilgileri.get("RESIM_INDIRILDI").equals("1")) {
                    photoFile = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/" + personelbilgileri.get("RESIM"));

                    if (photoFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        peFoto.setImageBitmap(myBitmap);
                        peFoto.setOnClickListener(this);
                        layoutPEFoto.setVisibility(View.VISIBLE);
                    } else {
                        layoutPEFoto.setVisibility(View.GONE);
                        peFoto.setImageBitmap(null);
                    }
                } else {
                    photoFile=null;
                    layoutPEFoto.setVisibility(View.GONE);
                    peFoto.setImageBitmap(null);
                }
                if(intent.hasExtra("shortdisplay")){
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 1500);
                }
            }else{
                finish();
                new ShowToast(ParentCtxt, "Kartnoya ait personel bulunamadı");
            }
        }else{
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClick(View v) {

    }
}
