package com.guleryuz.puantajonline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.guleryuz.puantajonline.CallBacks.ServiceCallBack;
import com.guleryuz.puantajonline.OnlineService.DataFromService;
import com.guleryuz.puantajonline.OnlineService.ServerData;
import com.guleryuz.puantajonline.OnlineService.UploadFileToServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by mehmet_erenoglu on 17.03.2017.
 */

/*
* v6.4.1 - 08.06.2017
* Personel ekleme kısmından fotoğraf ekleme zorunluluğu kaldırıldı.
* */

public class PersonelEkle extends AppCompatActivity implements View.OnClickListener, ServiceCallBack {
    private LinearLayout pelayoutBelgeler;
    private CheckedTextView peChkSgk, peChkOnay;
    private static TextView peDogumTarihi;
    private TextView peBelgelerTitle;
    private EditText peTC, peAd, peSoyad, peKartno, peBabaAdi, peGSM;
    private ImageView peFotoDel, peFotoZoom, imgChangeKartno, btnFromCamera, btnFromDrive;
    private Spinner spnCinsiyet, spnOzelDurum, spnBolge, spnCalisma, spnEkiplideri, spnBelgeTur;
    private ArrayAdapter<String> daBolge, daCalisma, daCinsiyet, daOzelDurum;
    private ArrayAdapter<KeyValueP> daEkiplideri, daBelgeTur;
    private static String Bolge, Calisma,  EkipLideri, Cinsiyet, BelgeTur, BelgeTurAdi, OzelDurum;
    private Button peBtnIptal, peBtnKaydet, peBtnFoto;
    private File photoFile1, photoFile1tmp, photoFile2;
    static final int REQUEST_TAKE_PHOTO1 = 1;
    static final int REQUEST_TAKE_PHOTO2 = 2;
    static final int REQUEST_TAKE_PHOTO3 = 3;
    static final int SELECT_FOLDER=4;
    private String activeButton;
    private ArrayList<String> Belgeler;
    private int BelgelerGonderilen;
    private ArrayList<String> BelgelerTur;
    private ArrayList<Integer> belgelerLayoutIds;
    private String imgFile = "";
    private String scanContent;
    private String sicilno;
    //private Database db;
    private static Context ParentCtxt;
    private static Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personel_ekleme);
        ParentCtxt = this;
        thisActivity=this;

        pelayoutBelgeler=(LinearLayout)findViewById(R.id.pelayoutBelgeler);
        belgelerLayoutIds=new ArrayList<Integer>();

        try {
            File photoPath = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"");

            if (!photoPath.exists()) {
                photoPath.mkdirs();
            }
        }catch (Exception ex){
            new ShowToast(ParentCtxt, "Klasör hatası");
        }


        peKartno=(EditText)findViewById(R.id.peKartno);
        //Max Kart No uzunlugu 10'dur.
        peKartno.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
        imgChangeKartno=(ImageView)findViewById(R.id.imgChangeKartno);
        imgChangeKartno.setOnClickListener(this);
        peAd=(EditText)findViewById(R.id.pead);
        //Max Ad uzunlugu 50. Sadece alfebedeki harfler geçerli.Düzenlemesi layoutta.
        peAd.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
        peSoyad=(EditText)findViewById(R.id.pesoyad);
        //Max SoyAd uzunlugu 50. Sadece alfebedeki harfler geçerli.Düzenlemesi layoutta.
        peSoyad.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
        peTC=(EditText)findViewById(R.id.petc);
        //TC No max uzunluk 11'dir.
        peTC.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
        spnCinsiyet=(Spinner)findViewById(R.id.pecinsiyet);
        peBabaAdi=(EditText)findViewById(R.id.pebabaadi);
        //Max BabaAdi uzunlugu 50. Sadece alfebedeki harfler geçerli.Düzenlemesi layoutta.
        peBabaAdi.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
        peGSM=(EditText)findViewById(R.id.pegsm);
        //Max GSM uzunlugu 20.
        peGSM.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        peGSM.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        spnOzelDurum=(Spinner)findViewById(R.id.peozeldurum);
        spnBolge=(Spinner)findViewById(R.id.pebolge);
        spnCalisma=(Spinner)findViewById(R.id.pecalismaalani);
        spnEkiplideri=(Spinner)findViewById(R.id.peekiplideri);
        spnBelgeTur=(Spinner)findViewById(R.id.spnBelgeTur);
        peBelgelerTitle=(TextView)findViewById(R.id.pebelgelertitle);
        peFotoDel=(ImageView)findViewById(R.id.pefotodelete);
        peFotoDel.setVisibility(View.INVISIBLE);
        peFotoDel.setImageResource(R.drawable.fotodel);
        peFotoDel.setOnClickListener(this);

        peFotoZoom=(ImageView)findViewById(R.id.pefotozoom);
        peFotoZoom.setVisibility(View.INVISIBLE);
        peFotoZoom.setImageResource(R.drawable.fotozoom);
        peFotoZoom.setOnClickListener(this);

        btnFromCamera=(ImageView)findViewById(R.id.btnFromCamera);
        btnFromCamera.setOnClickListener(this);
        btnFromDrive=(ImageView)findViewById(R.id.btnFromDrive);
        btnFromDrive.setOnClickListener(this);

        peBtnFoto=(Button)findViewById(R.id.peBtnFoto);
        peBtnFoto.setText("Foto Ekle");
        peBtnFoto.setOnClickListener(this);

        peBtnKaydet=(Button)findViewById(R.id.peBtnKaydet);
        peBtnKaydet.setOnClickListener(this);
        peBtnIptal=(Button)findViewById(R.id.peBtnIptal);
        peBtnIptal.setOnClickListener(this);

        peChkSgk=(CheckedTextView)findViewById(R.id.peChkSgk);
        peChkSgk.setOnClickListener(this);

        peChkOnay=(CheckedTextView)findViewById(R.id.peChkOnay);
        peChkOnay.setOnClickListener(this);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR)-16;
        int month = 0;
        int day = 1;
        peDogumTarihi=(TextView)findViewById(R.id.pedogumtarihi);
        peDogumTarihi.setText(day+" / "+(month+1)+" / "+year);
        peDogumTarihi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        //db=new Database(getApplicationContext());
        Bolge="";
        Calisma="";
        EkipLideri="";
        Belgeler=new ArrayList<String>();
        BelgelerGonderilen = 0;
        BelgelerTur=new ArrayList<String>();
        peBelgelerTitle.setText("Belgeler (0)");

        String[] cinsiyet=new String[]{"Seçiniz","Erkek", "Bayan"};
        daCinsiyet=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,cinsiyet);
        daCinsiyet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCinsiyet.setAdapter(daCinsiyet);
        spnCinsiyet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cinsiyet = daCinsiyet.getItem(adapterView.getSelectedItemPosition());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] ozeldurum=new String[]{"-","Asker Yardımı", "Babadan Yetim Maaşı", "Bağ-Kur Emekli", "Başbakanlık Bursu", "Emekli", "Kredi Yurtlar Kurumu", "Öğrenci Bursu", "Öğrenim Kredisi", "Özel Aylık Bağlı", "Sosyal Yardım", "Yeşil Kartlı"};
        daOzelDurum=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,ozeldurum);
        daOzelDurum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnOzelDurum.setAdapter(daOzelDurum);
        OzelDurum="";
        spnOzelDurum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                OzelDurum = daOzelDurum.getItem(adapterView.getSelectedItemPosition());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DataFromService belgetur = new DataFromService((ServiceCallBack) this);
        belgetur.context=ParentCtxt;
        belgetur.uid=MainActivity.userid;
        belgetur.reqtype="BelgeTur";
        belgetur.title="Belge Tür";
        belgetur.reqparam=new ArrayList<String[]>();
        belgetur.reqparam.add(new String[]{"op", "belgetur"});
        belgetur.reqparam.add(new String[]{"prgver", MainActivity.PROGRAM_VERSION});
        belgetur.resp=new ArrayList<String[]>();
        belgetur.resp.add(new String[]{"id","id"});
        belgetur.resp.add(new String[]{"ad","ad"});
        belgetur.execute();



        DataFromService bolgeler = new DataFromService((ServiceCallBack) this);
        bolgeler.context=ParentCtxt;
        bolgeler.uid=MainActivity.userid;
        bolgeler.reqtype="Bolgeler";
        bolgeler.title="Bölgeler";
        bolgeler.reqparam=new ArrayList<String[]>();
        bolgeler.reqparam.add(new String[]{"op", "firma_bolge2"});
        bolgeler.reqparam.add(new String[]{"prgver", MainActivity.PROGRAM_VERSION});
        bolgeler.resp=new ArrayList<String[]>();
        bolgeler.resp.add(new String[]{"bolge","bolge"});
        bolgeler.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    public void onClick(View v) {
        //Log.w("pe2","here "+peBtnFoto.getText()+" "+v.getId());
        if (v.getId()==R.id.peChkSgk) {
            if (peChkSgk.isChecked()) {
                peChkSgk.setChecked(false);
            } else {
                peChkSgk.setChecked(true);
            }
        }else if (v.getId()==R.id.peChkOnay){
            if(peChkOnay.isChecked()){
                peChkOnay.setChecked(false);
            }else{
                peChkOnay.setChecked(true);
            }
        }else if(v.getId()==R.id.imgChangeKartno) {
            try {
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
            }catch (Exception ex){
                Log.w("Barcode", "nothing");
            }
        }else if(v.getId()==R.id.peBtnFoto) {
            activeButton="Foto";
            if (peBtnFoto.getText().equals("Foto Değiştir")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                builder.setTitle("Uyarı");
                builder.setMessage("Fotoğrafı Değiştirmek istediğinize emin misiniz?");

                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "Foto_" + timeStamp + ".jpg";

                            //peFotoZoom.setVisibility(View.GONE);
                            //peFotoDel.setVisibility(View.GONE);

                            photoFile1tmp = photoFile1;

                            photoFile1 = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/" + imageFileName);

                            if (photoFile1 != null) {
                                Uri photoURI = Uri.fromFile(photoFile1);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO2);
                            }
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
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "Foto_" + timeStamp + ".jpg";

                    photoFile1 = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/" + imageFileName);

                    if (photoFile1 != null) {
                        Uri photoURI = Uri.fromFile(photoFile1);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO1);
                    }
                }
            }
        }else if(v.getId()==R.id.pefotozoom){
            if(photoFile1!=null) {
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", photoFile1.getAbsolutePath());
                intent.putExtra("local", "1");
                startActivity(intent);
            }
        }else if(v.getId()==R.id.pefotodelete){
            AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
            builder.setTitle("Uyarı");
            builder.setMessage("Fotoğrafı silmek istediğinize emin misiniz?");

            builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    if(photoFile1!=null) {
                        photoFile1.delete();
                    }
                    peFotoZoom.setVisibility(View.GONE);
                    peFotoDel.setVisibility(View.GONE);
                    peBtnFoto.setText("Foto Ekle");
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
        }else if(v.getId()==R.id.btnFromCamera) {
            activeButton="Belgeler";
            if(BelgeTur!="-1") {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "Belgeler_" + timeStamp + ".jpg";

                    Belgeler.add(imageFileName);
                    BelgelerTur.add(""+BelgeTur);

                    photoFile2 = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"/" + imageFileName);

                    if (photoFile2 != null) {
                        Uri photoURI = Uri.fromFile(photoFile2);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO3);
                    }
                }
            }else{
                new ShowToast(ParentCtxt, "Belge Türü Seçmelisiniz.");
            }
        }else if(v.getId()==R.id.btnFromDrive) {
            activeButton="Belgeler";
            if(BelgeTur!="-1") {
                myOpenImagePicker();
            }else{
                new ShowToast(ParentCtxt, "Belge Türü Seçmelisiniz.");
            }
        }else if(v.getId()==R.id.peBtnIptal){
            peKartno.setText("");
            peTC.setText("");
            peAd.setText("");
            peSoyad.setText("");
            peBabaAdi.setText("");
            peGSM.setText("");
            OzelDurum="";
            spnOzelDurum.setSelection(0);

            photoFile1=null;
            peBtnFoto.setText("Foto Ekle");
            peFotoZoom.setVisibility(View.GONE);
            peFotoDel.setVisibility(View.GONE);
            finish();
        }else if(v.getId()==R.id.peBtnKaydet){
            //&& photoFile1!=null kaldırıldı
            String dtarih = peDogumTarihi.getText().toString();
            //final Calendar c = Calendar.getInstance();
            String [] dt = dtarih.split("/");
            int year = Integer.parseInt(dt[2].trim());
            int month = Integer.parseInt(dt[1].trim())-1;
            int day = Integer.parseInt(dt[0].trim());


            if (getAge(year, month, day)>=16) {

                if (!peAd.getText().toString().equals("") && !peSoyad.getText().toString().equals("") && !Cinsiyet.equals("") && !Cinsiyet.equals("Seçiniz") && !peBabaAdi.getText().toString().equals("") && !peGSM.getText().toString().equals("") && !Bolge.equals("") && !Bolge.equals("Seçiniz") && !Calisma.equals("") && !Calisma.equals("Seçiniz")) {

                    if (peTC.getText().toString().equals("") || peTC.getText().toString().length() != 11) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                        builder.setTitle("Uyarı");
                        builder.setMessage("TC No doğru girilmedi. Kaydı bu şekilde eklemek istediğinizden emin misiniz?");

                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                //String tcNo = "15" + System.currentTimeMillis();
                                /*int padToLength = 15;
                                if (tcNo.length() < padToLength) {
                                    tcNo = String.format("%0" + String.valueOf(padToLength - tcNo.length()) + "d%s", 0, tcNo);
                                }*/
                                personelKaydet();
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
                    } else if (peTC.getText().toString().length() == 11) {
                        personelKaydet();
                    }
                } else {
                    String uyari = "";
                    if (peAd.getText().toString().equals("")) {
                        uyari = "Ad";
                    }

                    if (peSoyad.getText().toString().equals("")) {
                        uyari += (uyari.equals("") ? "" : ", ") + "Soyad";
                    }

                    /*
                    if (peTC.getText().toString().equals("") || peTC.getText().toString().length() != 11) {
                        uyari += (uyari.equals("") ? "" : ", ") + "TC No";
                    }*/

                    if (Cinsiyet.equals("") || Cinsiyet.equals("Seçiniz")) {
                        uyari += (uyari.equals("") ? "" : ", ") + "Cinsiyet";
                    }

                    if (peBabaAdi.getText().toString().equals("")) {
                        uyari += (uyari.equals("") ? "" : ", ") + "Baba Adı";
                    }

                    if (peGSM.getText().toString().equals("")) {
                        uyari += (uyari.equals("") ? "" : ", ") + "Cep Tel";
                    }

                    if (Bolge.equals("") || Bolge.equals("Seçiniz")) {
                        uyari += (uyari.equals("") ? "" : ", ") + "Bölge";
                    }

                    if (Calisma.equals("") || Calisma.equals("Seçiniz")) {
                        uyari += (uyari.equals("") ? "" : ", ") + "Çalışma";
                    }

                /*08.06.2017 tarihinde kaldırılmıştır.
                if(photoFile1==null){
                    uyari+=(uyari.equals("")?"":", ")+"Fotoğraf";
                }*/

                    new ShowToast(ParentCtxt, uyari + " kısımlarını kontrol ediniz!!");
                }
            }else{
                new ShowToast(ParentCtxt, "16 yaşından küçük personel girişi yapamazsınız.");
            }
        }
    }

    private void personelKaydet(){
        imgFile="";
        if (photoFile1 != null) {//fotoğraf sonradan eklenecekmiş.
            imgFile = photoFile1.getAbsolutePath();
            imgFile = imgFile.substring(imgFile.lastIndexOf('/') + 1);
        }
        /*if (photoFile1 != null) {//fotoğraf sonradan eklenecekmiş.
            imgFile = photoFile1.getAbsolutePath();
            imgFile = imgFile.substring(imgFile.lastIndexOf('/') + 1);

            try {
                File f = new File(Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/", "_" + imgFile);
                f.createNewFile();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile1.getAbsolutePath(), options);

                float ratio = Math.min((float) 1024 / myBitmap.getWidth(), (float) 1024 / myBitmap.getHeight());
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

                photoFile1.delete();
                imgFile = "_" + imgFile;
            } catch (IOException ex) {

            } catch (Exception ex) {

            }
        }*/

        //Sicil no hesaplama
                /*Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH)+1;
                String sicilnostr = MainActivity.userid + "" + (day < 10 ? "0" + day : day) + "" + (month < 10 ? "0" + month : month) + "000";
                long sicilno = Long.parseLong(sicilnostr);

                String pid = db.personelMax();
                if (pid != null && !pid.equals("")) {
                    if (Long.parseLong(pid) >= Long.parseLong(sicilnostr)) {
                        sicilno = Long.parseLong(pid) + 1;
                    }
                }

                Log.w("pekaydet", "" + pid+" - "+sicilnostr+" - "+sicilno+" - "+Long.parseLong(sicilnostr));*/
        try {
            DataFromService persor = new DataFromService((ServiceCallBack) this);
            persor.context=ParentCtxt;
            persor.uid=MainActivity.userid;
            persor.reqtype="PersonelSorgula";
            persor.title="Personel Kontrol";
            persor.reqparam=new ArrayList<String[]>();
            persor.reqparam.add(new String[]{"op", "persor"});
            persor.reqparam.add(new String[]{"kartno", ""});
            persor.reqparam.add(new String[]{"sicilno", ""});
            persor.reqparam.add(new String[]{"ad", ""});
            persor.reqparam.add(new String[]{"soyad", ""});
            persor.reqparam.add(new String[]{"tckimlik", peTC.getText().toString()});
            persor.resp=new ArrayList<String[]>();
            persor.resp.add(new String[]{"id","id"});
            persor.resp.add(new String[]{"ad","ad"});
            persor.execute();
        } catch (Exception ex) {
            new ShowToast(ParentCtxt, "Personel sorgulamada hata oluştu.\n" + ex.getMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.w("pe3",""+requestCode+ "--" +resultCode);
        if (requestCode == REQUEST_TAKE_PHOTO1 && resultCode == RESULT_OK) {
            if (photoFile1 != null) {
                peBtnFoto.setText("Foto Değiştir");
                peFotoZoom.setVisibility(View.VISIBLE);
                peFotoDel.setVisibility(View.VISIBLE);
            }
        }else if (requestCode == REQUEST_TAKE_PHOTO2 && resultCode == RESULT_OK) {
            if (photoFile1 != null) {
                //Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile1.getAbsolutePath());
                peBtnFoto.setText("Foto Değiştir");
                peFotoZoom.setVisibility(View.VISIBLE);
                peFotoDel.setVisibility(View.VISIBLE);

                if(photoFile1tmp!=null){
                    photoFile1tmp.delete();
                    photoFile1tmp=null;
                }
            }
        }else if (requestCode == REQUEST_TAKE_PHOTO2 && resultCode == RESULT_CANCELED) {
            photoFile1=photoFile1tmp;
            photoFile1tmp=null;
        }else if (requestCode == REQUEST_TAKE_PHOTO3 && resultCode == RESULT_OK) {
            if (photoFile2 != null) {

                try {
                    File f = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"", "_"+Belgeler.get(Belgeler.size()-1));
                    f.createNewFile();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile2.getAbsolutePath(),options);

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

                    photoFile2.delete();
                    Belgeler.set(Belgeler.size()-1,"_"+Belgeler.get(Belgeler.size()-1));
                }catch (IOException ex){

                }catch (Exception ex){

                }

                LinearLayout layouts=(LinearLayout)this.getLayoutInflater().inflate(R.layout.personel_ekleme_belgeler,null);
                int fId=View.generateViewId();
                belgelerLayoutIds.add(fId);
                layouts.setId(fId);
                TextView txt=(TextView)layouts.findViewById(R.id.childItem);
                txt.setText(BelgeTurAdi);
                peBelgelerTitle.setText("Belgeler ("+Belgeler.size()+")");
                ImageView imageView = (ImageView)layouts.findViewById(R.id.fotozoom);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                        intent.putExtra("photo", Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"/" +Belgeler.get(Belgeler.size()-1));
                        intent.putExtra("local", "1");
                        startActivity(intent);
                    }
                });

                imageView = (ImageView)layouts.findViewById(R.id.fotodel);
                imageView.setTag(fId);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int fid=Integer.parseInt(""+v.getTag());
                        int index=belgelerLayoutIds.indexOf(fid);
                        Log.w("belgerler","index:"+index+" - belgesayisi:"+Belgeler.size());
                        if(index>-1){
                            pelayoutBelgeler.removeViewAt(index);
                            belgelerLayoutIds.remove(index);
                            String pfname=Belgeler.get(index);
                            File pfile=new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"/",pfname);
                            if(pfile.exists()){
                                pfile.delete();
                                pfile=null;
                            }
                            Belgeler.remove(index);
                            BelgelerTur.remove(index);
                            peBelgelerTitle.setText("Belgeler ("+Belgeler.size()+")");
                        }
                    }
                });

                pelayoutBelgeler.addView(layouts);

                spnBelgeTur.requestFocus();
            }
        }else if (requestCode == REQUEST_TAKE_PHOTO3 && resultCode == RESULT_CANCELED) {
            photoFile2=null;
        }else if (requestCode == SELECT_FOLDER && resultCode == RESULT_OK) {
            if(data!=null) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "Belgeler_" + timeStamp + ".jpg";

                Belgeler.add(imageFileName);
                BelgelerTur.add(""+BelgeTur);

                Uri content_describer = data.getData();
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = getContentResolver().openInputStream(content_describer);
                    // open the output-file:
                    out = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"/",imageFileName));
                    // copy the content:
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    in.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    in=null;
                    out=null;
                }

                try {
                    photoFile2=new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"/",Belgeler.get(Belgeler.size()-1));
                    File f = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"", "_"+Belgeler.get(Belgeler.size()-1));
                    f.createNewFile();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile2.getAbsolutePath(),options);

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

                    photoFile2.delete();
                    Belgeler.set(Belgeler.size()-1,"_"+Belgeler.get(Belgeler.size()-1));
                }catch (IOException ex){

                }catch (Exception ex){

                }

                LinearLayout layouts=(LinearLayout)this.getLayoutInflater().inflate(R.layout.personel_ekleme_belgeler,null);
                int fId=View.generateViewId();
                belgelerLayoutIds.add(fId);
                layouts.setId(fId);
                TextView txt=(TextView)layouts.findViewById(R.id.childItem);
                txt.setText(BelgeTurAdi);
                peBelgelerTitle.setText("Belgeler ("+Belgeler.size()+")");
                ImageView imageView = (ImageView)layouts.findViewById(R.id.fotozoom);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                        intent.putExtra("photo", Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"/" +Belgeler.get(Belgeler.size()-1));
                        startActivity(intent);
                    }
                });

                imageView = (ImageView)layouts.findViewById(R.id.fotodel);
                imageView.setTag(fId);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int fid=Integer.parseInt(""+v.getTag());
                        int index=belgelerLayoutIds.indexOf(fid);
                        Log.w("belgerler","index:"+index+" - belgesayisi:"+Belgeler.size());
                        if(index>-1){
                            pelayoutBelgeler.removeViewAt(index);
                            belgelerLayoutIds.remove(index);
                            String pfname=Belgeler.get(index);
                            File pfile=new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.docDir+"/",pfname);
                            if(pfile.exists()){
                                pfile.delete();
                                pfile=null;
                            }
                            Belgeler.remove(index);
                            BelgelerTur.remove(index);
                            peBelgelerTitle.setText("Belgeler ("+Belgeler.size()+")");
                        }
                    }
                });

                pelayoutBelgeler.addView(layouts);
                spnBelgeTur.requestFocus();
            }
        }else{
            scanContent="";
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                scanContent = scanningResult.getContents();
                DataFromService persor = new DataFromService((ServiceCallBack) this);
                persor.context=ParentCtxt;
                persor.uid=MainActivity.userid;
                persor.reqtype="PersonelSorgula2";
                persor.title="Personel Kontrol";
                persor.reqparam=new ArrayList<String[]>();
                persor.reqparam.add(new String[]{"op", "persor"});
                persor.reqparam.add(new String[]{"kartno", scanContent});
                persor.reqparam.add(new String[]{"sicilno", ""});
                persor.reqparam.add(new String[]{"ad", ""});
                persor.reqparam.add(new String[]{"soyad", ""});
                persor.reqparam.add(new String[]{"tckimlik",""});
                persor.resp=new ArrayList<String[]>();
                persor.resp.add(new String[]{"id","id"});
                persor.resp.add(new String[]{"ad","ad"});
                persor.resp.add(new String[]{"soyad","soyad"});

                persor.execute();

                /*HashMap<String, String> val = db.getOneRow(new String[]{"ID","AD","SOYAD"}, "tarim_istakip_personel","kartno='"+scanContent+"'");
                if (val.size()==0)
                    peKartno.setText(scanContent);
                else{
                    new ShowToast(ParentCtxt,"Kart no "+val.get("AD")+" "+val.get("SOYAD")+" personeline tanımlıdır!");
                    peKartno.setText("");
                }*/
            }
        }
    }

    private int getAge (int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            throw new IllegalArgumentException("Age < 0");
        return a;
    }

    @SuppressLint("InlinedApi")
    public void myOpenImagePicker() {

        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    SELECT_FOLDER);

        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_FOLDER);
        }
    }

    @Override
    public void PipeAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error) {
        if (type.equals("BelgeTurOnline")){
            KeyValueP[] items=new KeyValueP[(data!=null?data.size()+1:1)];
            items[0]=new KeyValueP("-1", "Seçiniz");
            if(data!=null && data.size()>0){
                for (int i=0; i<data.size(); i++){
                    items[i+1]=new KeyValueP(""+data.get(i).get("id"), data.get(i).get("ad"));
                }
            }

            daBelgeTur=new ArrayAdapter<KeyValueP>(this, android.R.layout.simple_spinner_item, items);
            daBelgeTur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnBelgeTur.setAdapter(daBelgeTur);
            spnBelgeTur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    BelgeTur = daBelgeTur.getItem(adapterView.getSelectedItemPosition()).ID;
                    BelgeTurAdi=daBelgeTur.getItem(adapterView.getSelectedItemPosition()).name;
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }else if(type.equals("BolgelerOnline")){
            String[] bolgeler = new String[(data!=null?data.size()+1:1)];
            bolgeler[0]="Seçiniz";
            if(data!=null){
                for(int i=0; i<data.size(); i++){
                    bolgeler[i+1]=data.get(i).get("bolge");
                }
            }

            daBolge=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bolgeler);
            daBolge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnBolge.setAdapter(daBolge);
            spnBolge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (adapterView.getSelectedItem().toString()!=""){
                        Bolge=adapterView.getSelectedItem().toString();
                        DataFromService bolgeler = new DataFromService((ServiceCallBack) thisActivity);
                        bolgeler.context=ParentCtxt;
                        bolgeler.uid=MainActivity.userid;
                        bolgeler.reqtype="CalismaAlani";
                        bolgeler.title="Çalışma Alanı";
                        bolgeler.reqparam=new ArrayList<String[]>();
                        bolgeler.reqparam.add(new String[]{"op", "bolge_calisma2"});
                        bolgeler.reqparam.add(new String[]{"bolge", adapterView.getSelectedItem().toString()});
                        bolgeler.resp=new ArrayList<String[]>();
                        bolgeler.resp.add(new String[]{"calismaalani","bolge"});
                        bolgeler.execute();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }else if (type.equals("CalismaAlaniOnline")){
            String[] calismaa=new String[(data!=null?data.size()+1:1)];
            calismaa[0]="Seçiniz";
            for (int k=0; data!=null && k<data.size(); k++){
                calismaa[k+1]=data.get(k).get("calismaalani");
            }

            daCalisma=new ArrayAdapter<String>(ParentCtxt, android.R.layout.simple_spinner_item,calismaa);
            daCalisma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnCalisma.setAdapter(daCalisma);
            spnCalisma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (adapterView.getSelectedItem().toString()!="" && adapterView.getSelectedItem().toString()!="Seçiniz") {
                        Calisma=adapterView.getSelectedItem().toString();

                        DataFromService ekiplideri = new DataFromService((ServiceCallBack) thisActivity);
                        ekiplideri.context=ParentCtxt;
                        ekiplideri.uid=MainActivity.userid;
                        ekiplideri.reqtype="EkipLideri";
                        ekiplideri.title="Ekip Lideri";
                        ekiplideri.reqparam=new ArrayList<String[]>();
                        ekiplideri.reqparam.add(new String[]{"op", "ekiplideri2"});
                        ekiplideri.reqparam.add(new String[]{"calisma", adapterView.getSelectedItem().toString()});
                        ekiplideri.resp=new ArrayList<String[]>();
                        ekiplideri.resp.add(new String[]{"id","id"});
                        ekiplideri.resp.add(new String[]{"name","name"});
                        ekiplideri.execute();
                    }else{
                        daEkiplideri=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item);
                        spnEkiplideri.setAdapter(daEkiplideri);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }else if(type.equals("EkipLideriOnline")){
            KeyValueP[] ekiplideri=new KeyValueP[(data!=null?data.size():0)];
            for (int k=0; data!=null && k<data.size(); k++){
                ekiplideri[k]=new KeyValueP(data.get(k).get("id"),data.get(k).get("name"));
            }

            daEkiplideri=new ArrayAdapter<KeyValueP>(ParentCtxt, android.R.layout.simple_spinner_item, ekiplideri);
            daEkiplideri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEkiplideri.setAdapter(daEkiplideri);
            spnEkiplideri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    EkipLideri = daEkiplideri.getItem(adapterView.getSelectedItemPosition()).ID;
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }else if(type.equals("PersonelSorgulaOnline")){
            if (data==null || data.size() == 0) {
                HashMap<String, String> tmp = new HashMap<String, String>();
                //userid+day+month+000

                String kartnowithpad = peKartno.getText().toString();
                int padToLength = 6;
                if (kartnowithpad.length() < padToLength) {
                    kartnowithpad = String.format("%0" + String.valueOf(padToLength - kartnowithpad.length()) + "d%s", 0, kartnowithpad);
                }

                DataFromService perkaydet = new DataFromService((ServiceCallBack) this);
                perkaydet.context=ParentCtxt;
                perkaydet.uid=MainActivity.userid;
                perkaydet.reqtype="PersonelKaydet";
                perkaydet.title="Personel Kaydet";
                perkaydet.reqparam=new ArrayList<String[]>();
                perkaydet.reqparam.add(new String[]{"op", "pushdataper"});
                perkaydet.reqparam.add(new String[]{"kartno", (peKartno.getText().toString().equals("") ? "" : kartnowithpad)});
                perkaydet.reqparam.add(new String[]{"tc", peTC.getText().toString() });
                perkaydet.reqparam.add(new String[]{"ad", peAd.getText().toString() });
                perkaydet.reqparam.add(new String[]{"soyad", peSoyad.getText().toString() });
                perkaydet.reqparam.add(new String[]{"dogumtarihi", peDogumTarihi.getText().toString() });
                perkaydet.reqparam.add(new String[]{"cinsiyet", Cinsiyet });
                perkaydet.reqparam.add(new String[]{"ozeldurum", (OzelDurum.equals("-") ? "" : OzelDurum)});
                perkaydet.reqparam.add(new String[]{"babaadi", peBabaAdi.getText().toString() });
                perkaydet.reqparam.add(new String[]{"gsm", peGSM.getText().toString() });
                perkaydet.reqparam.add(new String[]{"bolge", Bolge });
                perkaydet.reqparam.add(new String[]{"bolge2", Calisma});
                perkaydet.reqparam.add(new String[]{"ekip_lideri", EkipLideri});
                perkaydet.reqparam.add(new String[]{"sgk", (peChkSgk.isChecked() ? "1" : "0") });
                perkaydet.reqparam.add(new String[]{"onay", (peChkOnay.isChecked() ? "1" : "0") });
                perkaydet.reqparam.add(new String[]{"resim", imgFile });
                String json= "{\"kno\":\"" + (peKartno.getText().toString().equals("") ? "" : kartnowithpad) + "\",\"uid\":\"" + MainActivity.userid + "\",\"tc\":\"" + peTC.getText().toString() + "\",\"ad\":\"" + peAd.getText().toString() + "\",\"soyad\":\"" + peSoyad.getText().toString() + "\",\"cinsiyet\":\"" + Cinsiyet + "\",\"dtarih\":\"" + peDogumTarihi.getText().toString() + "\",\"babaadi\":\"" + peBabaAdi.getText().toString() + "\",\"gsm\":\"" + peGSM.getText().toString() + "\",\"ozeldurum\":\""+ (OzelDurum.equals("-") ? "" : OzelDurum) +"\",\"bolge\":\"" + Bolge + "\",\"calismaalani\":\"" + Calisma + "\",\"ekiplideri\":\"" + EkipLideri+ "\",\"sgk\":\"" + (peChkSgk.isChecked() ? "1" : "0") + "\",\"onay\":\"" + (peChkOnay.isChecked() ? "1" : "0") + "\",\"foto\":\"" + imgFile + "\"}";
                perkaydet.reqparam.add(new String[]{"perjson", json });
                perkaydet.resp=new ArrayList<String[]>();
                perkaydet.resp.add(new String[]{"sicilno","sicilno"});
                perkaydet.resp.add(new String[]{"tc","tc"});
                perkaydet.resp.add(new String[]{"stat","stat"});
                perkaydet.execute();
            } else {
                new ShowToast(ParentCtxt, "Personel önceden eklenmiş.");
            }
        }else if(type.equals("PersonelKaydetOnline")) {
            Log.w(">", " Belgeler"+(Belgeler!=null?Belgeler.size():"null")+" data:"+(data!=null?data.size():"null"));
            if (data != null && data.size() > 0) {
                sicilno=data.get(0).get("sicilno");
                BelgelerGonderilen = 0;
                //upload foto && belge
                //fotoğraf gönderiliyor
                if (photoFile1 != null) {
                    UploadFileToServer f2s = new UploadFileToServer(this);
                    f2s.context = this;
                    f2s.uid = MainActivity.userid;
                    f2s.reqtype = "Fotoğraf";
                    f2s.uFile = photoFile1.getAbsolutePath();
                    f2s.execute();
                }else{
                    if(Belgeler!=null && Belgeler.size()>0) {
                        sendDocs();
                    }else{
                        temizle();
                    }
                }
            } else {
                new ShowToast(ParentCtxt, "Kayıt sırasında hata oluştu.");
            }
        } else if (type.equals("PersonelSorgula2Online")) {
            if (data != null && data.size() > 0) {
                new ShowToast(ParentCtxt, "Kart no " + data.get(0).get("AD") + " " + data.get(0).get("SOYAD") + " personeline tanımlıdır!");
                peKartno.setText("");
            } else {
                peKartno.setText(scanContent);

            }
        }else if(type.equals("File2Server")){
            if(stat){
                if (error.equals("Belgeler")){
                    BelgelerGonderilen++;
                    if (BelgelerGonderilen==Belgeler.size()){
                        String perbelbilgi = "{\"pbelid\":[";
                        for (int i = 0; i < Belgeler.size(); i++) {
                            perbelbilgi += "{\"personelid\":\"" + sicilno + "\",\"ad\":\"" + Belgeler.get(i) + "\",\"uid\":\"" + MainActivity.userid + "\",\"tur\":\"" + BelgelerTur.get(i) + "\"},";
                        }
                        perbelbilgi=perbelbilgi.substring(0, perbelbilgi.length()-1);
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
                }else if(error.equals("Fotoğraf")){
                    if(Belgeler!=null && Belgeler.size()>0) {
                        sendDocs();
                    }else{
                        temizle();
                    }
                }
            }
        } else if (type.equals("PersonelBelgeKaydetOnline")) {
            Log.w("here",data!=null?""+data.size():"data is null");
            if (data != null && data.size()>0 && data.get(0).get("stat").equals("true")){
                temizle();
            }else{
                new ShowToast(ParentCtxt, "Personele ait bilgiler sisteme gönderilirken hata oluştu.");
            }
        }
    }

    private void sendDocs()
    {
        //Belgeler gönderiliyor.
        for (int i = 0; i < Belgeler.size(); i++) {
            UploadFileToServer f2s = new UploadFileToServer(this);
            f2s.context = this;
            f2s.uid = MainActivity.userid;
            f2s.reqtype = "Belgeler";
            f2s.uFile = Environment.getExternalStorageDirectory() + "/" + MainActivity.rootDir + "/" + MainActivity.docDir + "/" + Belgeler.get(i);
            f2s.execute();
        }
    }

    private void temizle()
    {
        new ShowToast(ParentCtxt, "Kaydedildi.");
        peKartno.setText("");
        peTC.setText("");
        peAd.setText("");
        peSoyad.setText("");
        peBabaAdi.setText("");
        peGSM.setText("");
        OzelDurum = "";
        spnOzelDurum.setSelection(0);
        sicilno="";
        peChkSgk.setChecked(false);
        peChkOnay.setChecked(false);
        Belgeler = new ArrayList<String>();
        BelgelerGonderilen = 0;
        BelgelerTur = new ArrayList<String>();
        pelayoutBelgeler.removeAllViews();
        peBelgelerTitle.setText("Belgeler (0)");
        photoFile1 = null;
        peBtnFoto.setText("Foto Ekle");
        peFotoZoom.setVisibility(View.GONE);
        peFotoDel.setVisibility(View.GONE);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            String tarih = peDogumTarihi.getText().toString();
            //final Calendar c = Calendar.getInstance();
            String [] t = tarih.split("/");
            int year = Integer.parseInt(t[2].trim());
            int month = Integer.parseInt(t[1].trim())-1;
            int day = Integer.parseInt(t[0].trim());

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            //ibasTarih.setText(day+" / "+month+" / "+year);
            if(getAge(year, month, day)<16){
                AlertDialog.Builder builder = new AlertDialog.Builder(ParentCtxt);
                builder.setTitle("Uyarı");
                builder.setMessage("16 yaşından küçük personel girişi yapamazsınız");

                builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
            peDogumTarihi.setText(day+" / "+(month+1)+" / "+year);
        }

        private int getAge (int _year, int _month, int _day) {

            GregorianCalendar cal = new GregorianCalendar();
            int y, m, d, a;

            y = cal.get(Calendar.YEAR);
            m = cal.get(Calendar.MONTH);
            d = cal.get(Calendar.DAY_OF_MONTH);
            cal.set(_year, _month, _day);
            a = y - cal.get(Calendar.YEAR);
            if ((m < cal.get(Calendar.MONTH))
                    || ((m == cal.get(Calendar.MONTH)) && (d < cal
                    .get(Calendar.DAY_OF_MONTH)))) {
                --a;
            }
            if(a < 0)
                throw new IllegalArgumentException("Age < 0");
            return a;
        }
    }
}

