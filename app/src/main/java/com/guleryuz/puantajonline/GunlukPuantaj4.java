package com.guleryuz.puantajonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.guleryuz.puantajonline.CallBacks.ServiceCallBack;
import com.guleryuz.puantajonline.OnlineService.DataFromService;
import com.guleryuz.puantajonline.OnlineService.UploadFileToServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by mehmet_erenoglu on 01.03.2017.
 */

public class GunlukPuantaj4 extends AppCompatActivity implements View.OnClickListener, ServiceCallBack {
    private LinearLayout ibasLayout;
    private Button btnIbas3Kontrol1,btnIbas3Kontrol2,btnIbas3Kontrol3;
    private Button ibasBtnSonraki,ibasBtnIptal,ibasBtnOnceki;
    private ImageView ibas3Image1, ibas3Image2, ibas3Image3;
    private ImageView ibasEk1del, ibasEk2del, ibasEk3del;
    private EditText ibas4Aciklama;
    //private Database db;
    private EditText ibasFisno;
    private static Context ParentCtxt;
    static final int REQUEST_TAKE_PHOTO1 = 1;
    static final int REQUEST_TAKE_PHOTO2 = 2;
    static final int REQUEST_TAKE_PHOTO3 = 3;
    private File photoFile1, photoFile2, photoFile3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ise_baslama4);
        ParentCtxt = this;

        ibasFisno=(EditText)findViewById(R.id.ibasfisno);

        ibasBtnIptal=(Button)findViewById(R.id.ibasBtnIptal);
        ibasBtnIptal.setOnClickListener(this);

        ibasBtnSonraki=(Button)findViewById(R.id.ibasBtnSonraki);
        ibasBtnSonraki.setOnClickListener(this);

        btnIbas3Kontrol1=(Button)findViewById(R.id.btnIbas3Kontrol1);
        btnIbas3Kontrol1.setOnClickListener(this);
        btnIbas3Kontrol2=(Button)findViewById(R.id.btnIbas3Kontrol2);
        btnIbas3Kontrol2.setOnClickListener(this);
        btnIbas3Kontrol3=(Button)findViewById(R.id.btnIbas3Kontrol3);
        btnIbas3Kontrol3.setOnClickListener(this);

        ibas3Image1=(ImageView)findViewById(R.id.ibas3Image1);
        ibas3Image1.setOnClickListener(this);
        ibas3Image2=(ImageView)findViewById(R.id.ibas3Image2);
        ibas3Image2.setOnClickListener(this);
        ibas3Image3=(ImageView)findViewById(R.id.ibas3Image3);
        ibas3Image3.setOnClickListener(this);

        ibasEk1del=(ImageView)findViewById(R.id.ibasek1del);
        ibasEk1del.setVisibility(View.INVISIBLE);
        ibasEk1del.setImageResource(R.drawable.fotodel);
        ibasEk1del.setOnClickListener(this);

        ibasEk2del=(ImageView)findViewById(R.id.ibasek2del);
        ibasEk2del.setVisibility(View.INVISIBLE);
        ibasEk2del.setImageResource(R.drawable.fotodel);
        ibasEk2del.setOnClickListener(this);

        ibasEk3del=(ImageView)findViewById(R.id.ibasek3del);
        ibasEk3del.setVisibility(View.INVISIBLE);
        ibasEk3del.setImageResource(R.drawable.fotodel);
        ibasEk3del.setOnClickListener(this);
        ibas4Aciklama=(EditText)findViewById(R.id.ibas4Aciklama);

        if (MainActivity.gpd.getKayitdurumu().equals("guncelleme")){
            if(MainActivity.gpd.getFisno().length()!=36){
                ibasFisno.setText(MainActivity.gpd.getFisno());
            }

            if (MainActivity.gpd.getSayiDoc1()!=null && !MainActivity.gpd.getSayiDoc1().equals("")){
                photoFile1 =  new File(MainActivity.gpd.getSayiDoc1());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile1.getAbsolutePath(),options);
                ibas3Image1.setImageBitmap(imageBitmap);
                ibasEk1del.setVisibility(View.VISIBLE);
            }
            if (MainActivity.gpd.getSayiDoc2()!=null && !MainActivity.gpd.getSayiDoc2().equals("")){
                photoFile2 =  new File(MainActivity.gpd.getSayiDoc2());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile2.getAbsolutePath(),options);
                ibas3Image2.setImageBitmap(imageBitmap);
                ibasEk2del.setVisibility(View.VISIBLE);
            }
            if (MainActivity.gpd.getSayiDoc3()!=null && !MainActivity.gpd.getSayiDoc3().equals("")){
                photoFile3 =  new File(MainActivity.gpd.getSayiDoc3());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile3.getAbsolutePath(),options);
                ibas3Image3.setImageBitmap(imageBitmap);
                ibasEk3del.setVisibility(View.VISIBLE);
            }

            if (MainActivity.gpd.getAciklama()!=null && !MainActivity.gpd.getAciklama().equals("null")){
                ibas4Aciklama.setText(MainActivity.gpd.getAciklama());
            }
        }

        try {
            File photoPath = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"");
            File photoPath2 = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.attachDir+"");

            if (!photoPath.exists()) {
                photoPath.mkdirs();
            }

            if (!photoPath2.exists()) {
                photoPath2.mkdirs();
            }
        }catch (Exception ex){
            new ShowToast(ParentCtxt, "Klasör hatası");
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

    public void onClick(View v) {
        if (v.getId()==R.id.ibasBtnIptal){
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
        }else if (v.getId()==R.id.btnIbas3Kontrol1){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "Ekler_"+MainActivity.userid+"_" + timeStamp + ".jpg";

                photoFile1 =  new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.attachDir+"/"+imageFileName);

                // Continue only if the File was successfully created
                if (photoFile1 != null) {
                    Uri photoURI =   Uri.fromFile(photoFile1);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO1);
                }
            }
        }else if(v.getId()==R.id.ibas3Image1){
            if(photoFile1!=null) {
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", photoFile1.getAbsolutePath());
                startActivity(intent);
            }
        }else if (v.getId()==R.id.btnIbas3Kontrol2){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "Ekler_"+MainActivity.userid+"_" + timeStamp + ".jpg";

                photoFile2 =  new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.attachDir+"/"+imageFileName);

                // Continue only if the File was successfully created
                if (photoFile2 != null) {
                    Uri photoURI =   Uri.fromFile(photoFile2);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO2);
                }
            }
        }else if(v.getId()==R.id.ibas3Image2){
            if(photoFile2!=null) {
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", photoFile2.getAbsolutePath());
                startActivity(intent);
            }
        }else if (v.getId()==R.id.btnIbas3Kontrol3){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "Ekler_"+MainActivity.userid+"_" + timeStamp + ".jpg";

                photoFile3 =  new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.attachDir+"/"+imageFileName);

                // Continue only if the File was successfully created
                if (photoFile3 != null) {
                    Uri photoURI =   Uri.fromFile(photoFile3);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO3);
                }
            }
        }else if(v.getId()==R.id.ibas3Image3){
            if(photoFile3!=null) {
                Intent intent = new Intent(getApplicationContext(), PhotoZoom.class);
                intent.putExtra("photo", photoFile3.getAbsolutePath());
                startActivity(intent);
            }
        }else if(v.getId()==R.id.ibasek1del){
            if(photoFile1!=null) {
                ibas3Image1.setImageBitmap(null);
                photoFile1.delete();
                photoFile1=null;
                ibasEk1del.setVisibility(View.INVISIBLE);
            }
        }else if(v.getId()==R.id.ibasek2del){
            if(photoFile2!=null) {
                ibas3Image2.setImageBitmap(null);
                photoFile2.delete();
                photoFile2=null;
                ibasEk2del.setVisibility(View.INVISIBLE);
            }
        }else if(v.getId()==R.id.ibasek3del){
            if(photoFile3!=null) {
                ibas3Image3.setImageBitmap(null);
                photoFile3.delete();
                photoFile3=null;
                ibasEk3del.setVisibility(View.INVISIBLE);
            }
        }else if(v.getId()==R.id.ibasBtnSonraki)
        {
            Log.w("gp4 fisno",MainActivity.gpd.getFisno());


            if(!ibasFisno.getText().toString().equals("")){
                MainActivity.gpd.setFisno(ibasFisno.getText().toString());
            }

            if (photoFile1!=null || photoFile2!=null || photoFile3!=null){
                String ekPath=Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.attachDir+"/";
                String imgFile1="", imgFile2="",imgFile3="";
                if(photoFile1!=null){
                    imgFile1=photoFile1.getAbsolutePath();
                    photoFile1=null;
                    /*imgFile1=imgFile1.substring(imgFile1.lastIndexOf('/')+1);
                    if(scaleImage(imgFile1)!=imgFile1) {
                        photoFile1.delete();
                        photoFile1=null;
                    }

                    imgFile1=ekPath+imgFile1;*/
                }

                if(photoFile2!=null){
                    imgFile2=photoFile2.getAbsolutePath();
                    photoFile2=null;
                    /*imgFile2=imgFile1.substring(imgFile2.lastIndexOf('/')+1);
                    if(scaleImage(imgFile2)!=imgFile2) {
                        photoFile2.delete();
                        photoFile2=null;
                    }

                    imgFile2=ekPath+imgFile2;*/
                }

                if(photoFile3!=null){
                    imgFile3=photoFile3.getAbsolutePath();
                    photoFile3=null;
                    /*imgFile3=imgFile3.substring(imgFile3.lastIndexOf('/')+1);
                    if(scaleImage(imgFile3)!=imgFile3) {
                        photoFile3.delete();
                        photoFile3=null;
                    }

                    imgFile3=ekPath+imgFile3;*/
                }
                MainActivity.gpd.setEkliDoc(imgFile1, imgFile2, imgFile3, ibas4Aciklama.getText().toString());



            }
            /*else{
                db=new Database(getApplicationContext());
                MainActivity.gpd.setEkliDoc(null, null, null, ibas4Aciklama.getText().toString());
                String res=db.isbaslamaOnay(MainActivity.gpd, 0);
                Intent intent = new Intent();
                intent.putExtra("exit", "exit");
                intent.putExtra("status", "success");
                setResult(RESULT_OK, intent);
                finish();
            }*/

            puantajGonder();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO1 && resultCode == RESULT_OK) {
            if (photoFile1 != null) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile1.getAbsolutePath());
                ibas3Image1.setImageBitmap(imageBitmap);
                ibasEk1del.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO2 && resultCode == RESULT_OK) {
            if (photoFile2 != null) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile2.getAbsolutePath());
                ibas3Image2.setImageBitmap(imageBitmap);
                ibasEk2del.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO3 && resultCode == RESULT_OK) {
            if (photoFile3 != null) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile3.getAbsolutePath());
                ibas3Image3.setImageBitmap(imageBitmap);
                ibasEk3del.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO1 && resultCode == RESULT_CANCELED) {
            photoFile1 = null;
        } else if (requestCode == REQUEST_TAKE_PHOTO2 && resultCode == RESULT_CANCELED) {
            photoFile2 = null;
        } else if (requestCode == REQUEST_TAKE_PHOTO3 && resultCode == RESULT_CANCELED) {
            photoFile3 = null;
        }
    }

    private String scaleImage(String imgFile){
        String imgf=imgFile;
        try {
            File f = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"/"+MainActivity.attachDir+"/", "_"+imgFile);
            f.createNewFile();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile1.getAbsolutePath(),options);

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

            imgf="_"+imgFile;
        }catch (IOException ex){

        }catch (Exception ex){

        }
        return imgf;
    }

    private void puantajGonder(){
        try {
            DataFromService puantajsrv = new DataFromService(this);
            puantajsrv.context=ParentCtxt;
            puantajsrv.uid=MainActivity.userid;
            puantajsrv.reqtype="PuantajGonder";
            puantajsrv.title="Puantaj";
            puantajsrv.reqparam=new ArrayList<String[]>();
            puantajsrv.reqparam.add(new String[]{"op", "pushdata2"});
            puantajsrv.reqparam.add(new String[]{"prgver", MainActivity.PROGRAM_VERSION});
            puantajsrv.reqparam.add(new String[]{"firmaid", MainActivity.gpd.getFirma()});
            puantajsrv.reqparam.add(new String[]{"bolge2", MainActivity.gpd.getBolge()});
            puantajsrv.reqparam.add(new String[]{"bolge", MainActivity.gpd.getCalismaalani()});
            puantajsrv.reqparam.add(new String[]{"yetkili", MainActivity.gpd.getYetkili()});
            puantajsrv.reqparam.add(new String[]{"ekiplideri", MainActivity.gpd.getEkiplideri()});
            puantajsrv.reqparam.add(new String[]{"calismavar", ""+MainActivity.gpd.getCalismavar()});
            puantajsrv.reqparam.add(new String[]{"globalid", MainActivity.gpd.getGlobalid()});
            puantajsrv.reqparam.add(new String[]{"fisno", MainActivity.gpd.getFisno()});
            puantajsrv.reqparam.add(new String[]{"isebastarihi", MainActivity.gpd.getTarih()});
            puantajsrv.reqparam.add(new String[]{"urun", MainActivity.gpd.getUrun()});

            String puantaj = "{\"psayi\":\"1\",\"puantaj\":[";

            String jsonservis="";
            HashMap<String,String> servis=MainActivity.gpd.getServis();
            for(int i=1; i<21; i++){
                jsonservis+="\"servis"+i+"sayi\":\""+servis.get(""+(i-1))+"\","+"\"servis"+i+"\":\""+MainActivity.gpd.servisBilgileri[i-1]+"\"";
                if (i < 20) {
                    jsonservis += ",";
                }
            }

            HashMap<String, List<GunlukPersonelData>> gorevler = MainActivity.gpd.getGorevler();
            String jsongorev = "";
            for ( String key : gorevler.keySet() ) {
                List<GunlukPersonelData> gperd = gorevler.get(key);
                for (int i=0; i<gperd.size(); i++) {
                    jsongorev += "{\"globalid\":\"" + MainActivity.gpd.getGlobalid() + "\",\"sicilno\":\"" + gperd.get(i).sicilno + "\",\"urunid\":\"" + gperd.get(i).urun + "\",\"gorev\":\"" + key + "\",\"mesai\":\"" + gperd.get(i).mesai + "\",\"kartokutma\":\"" + gperd.get(i).kartlaeklendi + "\",\"kartno\":\"" + gperd.get(i).kartno + "\",\"tc\":\"" + gperd.get(i).tc + "\",\"ad\":\"" + gperd.get(i).adi.replace(" "+gperd.get(i).soyadi,"") + "\",\"soyad\":\"" + gperd.get(i).soyadi + "\",\"cinsiyet\":\"" + gperd.get(i).cinsiyet + "\"},";
                }
            }
            jsongorev = jsongorev.substring(0,jsongorev.length()-1);

            String ek1 = "", ek2 = "", ek3 = "";
            if (MainActivity.gpd.getSayiDoc1() != null && !MainActivity.gpd.getSayiDoc1().equals("")) {
                ek1 = MainActivity.gpd.getSayiDoc1();
                ek1 = ek1.substring(ek1.lastIndexOf('/') + 1);
                UploadFileToServer f2s=new UploadFileToServer(this);
                f2s.context=this;
                f2s.uid=MainActivity.userid;
                f2s.reqtype="Sayı Kontrol 1";
                f2s.uFile=MainActivity.gpd.getSayiDoc1();
                f2s.execute();
            }

            if (MainActivity.gpd.getSayiDoc2() != null && !MainActivity.gpd.getSayiDoc2().equals("")) {
                ek2 = MainActivity.gpd.getSayiDoc2();
                ek2 = ek2.substring(ek2.lastIndexOf('/') + 1);
                UploadFileToServer f2s=new UploadFileToServer(this);
                f2s.context=this;
                f2s.uid=MainActivity.userid;
                f2s.reqtype="Sayı Kontrol 2";
                f2s.uFile=MainActivity.gpd.getSayiDoc2();
                f2s.execute();
            }

            if (MainActivity.gpd.getSayiDoc3() != null && !MainActivity.gpd.getSayiDoc3().equals("")) {
                ek3 = MainActivity.gpd.getSayiDoc3();
                ek3 = ek3.substring(ek3.lastIndexOf('/') + 1);
                UploadFileToServer f2s=new UploadFileToServer(this);
                f2s.context=this;
                f2s.uid=MainActivity.userid;
                f2s.reqtype="Sayı Kontrol 3";
                f2s.uFile=MainActivity.gpd.getSayiDoc3();
                f2s.execute();
            }
            String[] ntarih = MainActivity.gpd.getTarih().toString().split(" / ");
            String ttarih= ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0];

            puantaj += "{\"globalid\":\""+ MainActivity.gpd.getGlobalid() +"\",\"ibt\":\"" +  MainActivity.gpd.getTarih() + "\",\"ibt2\":\""+ttarih+"\",\"calismavar\":\"" + MainActivity.gpd.getCalismavar() + "\",\"uid\":\"" + MainActivity.userid + "\",\"firma\":\"" + MainActivity.gpd.getFirma() + "\",\"bolge\":\"" + MainActivity.gpd.getBolge() + "\",\"calisma\":\"" + MainActivity.gpd.getCalismaalani() + "\",\"fisno\":\"" + MainActivity.gpd.getFisno() + "\",\"ekiplideri\":\"" + MainActivity.gpd.getEkiplideri() + "\",\"yetkili\":\"" + MainActivity.gpd.getYetkili() + "\",\"servisvar\":\"" + MainActivity.gpd.getServisSayisi() + "\",\"aciklama\":\"" + MainActivity.gpd.getAciklama() + "\",\"ek1\":\"" + ek1 + "\",\"ek2\":\"" + ek2 + "\",\"ek3\":\"" + ek3 + "\",\"urunid\":\"" + MainActivity.gpd.getUrun() + "\",\"gorev\":[" + jsongorev + "],\"servis\":{" + jsonservis + "}}]}";
            puantajsrv.reqparam.add(new String[]{"gorevjson", "["+jsongorev+"]"});
            puantajsrv.reqparam.add(new String[]{"servisjson", "{"+jsonservis+"}"});
            puantajsrv.reqparam.add(new String[]{"puantaj", puantaj});
            Log.w("puantajGonder", puantaj);
            puantajsrv.resp=new ArrayList<String[]>();
            puantajsrv.resp.add(new String[]{"stat", "stat"});
            puantajsrv.execute();

        }catch (Exception ex){
            Log.w("puantajGonder", "nothing");
        }

    }

    @Override
    public void PipeAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error) {
        if (type.equals("PuantajGonderOnline")){
            if(data!=null && data.size()>0 && data.get(0).get("stat").equals("true")) {
                new ShowToast(this, "Günlük Puantaj Başarıyla Oluşturulmuştur.");
                Intent intent = new Intent();
                intent.putExtra("exit", "exit");
                intent.putExtra("status", "success");
                setResult(RESULT_OK, intent);
                finish();
            }else{
                new ShowToast(ParentCtxt, "Günlük Puantaj Kaydedilirken Hata Oluştu.");
            }
        }else if (type.equals("File2Server")){
            if (stat)
                new ShowToast(ParentCtxt, error+" dosyası sisteme aktarıldı.");
            else
                new ShowToast(ParentCtxt, error+" dosyası sisteme aktarılırken hata oluştu.");

        }
    }
}
