package com.guleryuz.puantajonline;

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
import java.util.List;

import android.os.Handler;

import com.guleryuz.puantajonline.OnlineService.ServerData;
import com.squareup.picasso.Picasso;


/**
 * Created by Asersoft on 19.03.2017.
 */

public class PersonelGoruntule extends AppCompatActivity implements View.OnClickListener {
    private TextView peKartno, peSicilno, peTC, peAd, peSoyad, peDogumTarihi, peCinsiyet, peBolge, peCalismaalani, peEkiplideri, peSSKDurumu;
    private Button peBtnIptal;
    private ImageView peFoto;
    private LinearLayout layoutPEFoto;
    private File photoFile;


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
        peSSKDurumu = (TextView) findViewById(R.id.pesskdurumu);
        peFoto=(ImageView)findViewById(R.id.peFoto);
        layoutPEFoto=(LinearLayout)findViewById(R.id.layoutPEFoto);

        Intent intent=getIntent();
        if(intent.hasExtra("kartno")){
            //db=new Database(getApplicationContext());
            //HashMap<String, String> personelbilgileri = db.personelBilgileriGetir(intent.getStringExtra("kartno"),"","","");
            List<HashMap<String, String>> personelbilgileri = new ServerData(this).personelSorgula(MainActivity.userid, intent.getStringExtra("kartno"), "","","","");

            if (personelbilgileri!=null && personelbilgileri.size() > 0) {
                peKartno.setText((personelbilgileri.get(0).get("KARTNO").equals("0") ? "" : personelbilgileri.get(0).get("KARTNO")));
                peSicilno.setText(personelbilgileri.get(0).get("ID"));
                peTC.setText(personelbilgileri.get(0).get("TC"));
                peAd.setText(personelbilgileri.get(0).get("AD") + " " + personelbilgileri.get(0).get("SOYAD"));
                peDogumTarihi.setText(personelbilgileri.get(0).get("DOGUMTARIHI"));
                peCinsiyet.setText(personelbilgileri.get(0).get("CINSIYET"));
                peBolge.setText(personelbilgileri.get(0).get("BOLGE"));
                peCalismaalani.setText(personelbilgileri.get(0).get("BOLGE2"));
                peEkiplideri.setText(personelbilgileri.get(0).get("EKIP_LIDERI"));
                String sskdurumu="";
                if(personelbilgileri.get(0).get("DEVAM").equals("0")){
                    sskdurumu="Personel Pasif";
                }else{
                    switch (personelbilgileri.get(0).get("SSK")){
                        case "2":
                            switch (personelbilgileri.get(0).get("SSK_CIKIS")){
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
                peSSKDurumu.setText(sskdurumu);

                try {
                    if (personelbilgileri.get(0).get("RESIM")!=null && personelbilgileri.get(0).get("RESIM").length()>0) {
                        peFoto.requestLayout();

                        Picasso.get()
                                .load(this.getResources().getString(R.string.docUrl) +personelbilgileri.get(0).get("RESIM"))
                                .into(peFoto);

                        peFoto.setOnClickListener(this);
                        layoutPEFoto.setVisibility(View.VISIBLE);
                    } else {
                        layoutPEFoto.setVisibility(View.GONE);
                        peFoto.setImageBitmap(null);;
                    }
                }catch (Exception ex){
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
