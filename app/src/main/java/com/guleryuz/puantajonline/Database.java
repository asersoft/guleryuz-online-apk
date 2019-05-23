package com.guleryuz.puantajonline;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.guleryuz.puantajonline.OnlineService.ServerData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Asersoft on 9.12.2016.
 */

public class Database extends SQLiteOpenHelper{
    public Context cntxt;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "sqlite_database10.sqlite";//database adı

    public static final int DBSYNC_TABLES=11;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private List<String> personelBilgi;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE  GUNCELTARIH ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "SERVERDATE TIMESTAMP,"
                +  "SYSTEMDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  users ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "USERNAME TEXT,"
                +  "PASSWORD TEXT,"
                +  "PASSWORDTXT TEXT,"
                +  "REMPASS TEXT,"
                +  "NAME TEXT,"
                +  "PUANTAJ_YETKI INTEGER,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  user_fistanim ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "USER_ID INTEGER,"
                +  "FISBAS TEXT,"
                +  "FISBIT TEXT,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  sifremi_unuttum ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "USERNAME TEXT,"
                +  "PASSWORD TEXT,"
                +  "ISLEM INTEGER,"
                +  "STATUS TEXT,"
                +  "ISLEM_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_firma_bolge ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "YETKILI TEXT,"
                +  "BOLGE TEXT,"
                +  "BOLGE2 TEXT,"
                +  "FIRMA_ID INTEGER,"
                +  "YETKILI_ID INTEGER,"
                +  "DEVAM INTEGER,"
                +  "TEMSILCI INTEGER,"
                +  "TUR TEXT,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_firma_bolge_lider ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "EKIP_LIDERI INTEGER,"
                +  "BOLGE TEXT,"
                +  "BOLGE2 TEXT,"
                +  "FIRMA INTEGER,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);


        CREATE_TABLE = "CREATE TABLE  firmalar ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "FIRMA TEXT,"
                +  "DEVAM INTEGER,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  firmalar_yetkili_kisi ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "YETKILI TEXT,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_ekiplideri ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "AD TEXT,"
                +  "EKALAN2 TEXT,"
                +  "EKALAN3 TEXT,"
                +  "EKALAN4 TEXT,"
                +  "EKALAN6 TEXT,"
                +  "EKALAN7 TEXT,"
                +  "EKALAN8 TEXT,"
                +  "FIRMA INTEGER,"
                +  "BOLGE TEXT,"
                +  "DEVAM INTEGER,"
                +  "AKTIF INTEGER,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  gorev ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "GOREV TEXT,"
                +  "FIRMA INTEGER,"
                +  "BOLGE TEXT,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  muhendis_urun ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "AD TEXT,"
                +  "URUNID INTEGER,"
                +  "URUN TEXT,"
                +  "FIRMA INTEGER,"
                +  "BOLGE TEXT,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_personel ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "TC TEXT, "
                +  "AD TEXT,"
                +  "SOYAD TEXT,"
                +  "CINSIYET TEXT,"
                +  "DOGUMTARIHI TEXT,"
                +  "BABAADI TEXT,"
                +  "GSM TEXT,"
                +  "OZELDURUM TEXT,"
                +  "GOREV TEXT,"
                +  "BOLGE TEXT,"
                +  "BOLGE2 TEXT,"
                +  "BOLGE3 TEXT,"
                +  "BOLGE4 TEXT,"
                +  "BOLGE5 TEXT,"
                +  "EKIP_LIDERI INTEGER,"
                +  "EKIP_LIDERI2 INTEGER,"
                +  "EKIP_LIDERI3 INTEGER,"
                +  "SGK INTEGER,"
                +  "ONAY INTEGER,"
                +  "USER_ID INTEGER,"
                +  "KARTNO TEXT,"
                +  "KARTNO_GUNCELLENDI INTEGER,"
                +  "KARTNO_GUNCEL_AKTARILDI INTEGER,"
                +  "RESIM TEXT,"
                +  "RESIM_INDIRILDI INTEGER,"
                +  "YENI_RESIM INTEGER DEFAULT 0,"
                +  "YENI_RESIM_AKTARILDI INTEGER,"
                +  "SSK INTEGER,"
                +  "SGK_EVRAK TEXT,"
                +  "SGK_EVRAK_INDIRILDI INTEGER DEFAULT 0,"
                +  "YENI_KAYIT INTEGER,"
                +  "GUNCELKONTROL INTEGER,"
                +  "AKTARILDI INTEGER,"
                +  "AKTARILMA_TARIHI TIMESTAMP,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_personel_belge ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "USER_ID INTEGER,"
                +  "PERSONELID INTEGER,"
                +  "TUR TEXT, "
                +  "RESIMADI TEXT,"
                +  "EKLENME_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                +  "AKTARILDI INTEGER,"
                +  "AKTARILMA_TARIHI TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_belge ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ID INTEGER,"
                +  "AD TEXT,"
                +  "SIRA INTEGER,"
                +  "ZORUNLU INTEGER,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE dbstatus ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "TABLENAME TEXT,"
                +  "WHICH TEXT,"
                +  "USER_ID TEXT,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_calisma ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "ISE_BASLAMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                +  "GLOBALID TEXT,"
                +  "CALISMAVAR INTEGER,"
                +  "USER_ID TEXT,"
                +  "FIRMA TEXT,"
                +  "BOLGE TEXT,"
                +  "CALISMA TEXT,"
                +  "FISNO TEXT,"
                +  "EKIP_LIDERI TEXT,"
                +  "URUN TEXT,"
                +  "YETKILI TEXT,"
                +  "SERVISVAR INTEGER,"
                +  "ACIKLAMA TEXT,"
                +  "EKLIDOC1 TEXT,"
                +  "EKLIDOC2 TEXT,"
                +  "EKLIDOC3 TEXT,"
                +  "AKTARILDI INTEGER,"
                +  "AKTARILDI_ONAY INTEGER,"
                +  "AKTARILMA_TARIHI TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_calisma_personel ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "GLOBALID TEXT,"
                +  "SICILNO INTEGER,"
                +  "TC TEXT,"
                +  "KARTNO TEXT,"
                +  "TICID INTEGER,"
                +  "URUNID INTEGER,"
                +  "GOREV TEXT,"
                +  "MESAI TEXT,"
                +  "KARTLAEKLENDI TEXT,"
                +  "USER_ID TEXT,"
                +  "FISNO TEXT,"
                +  "PUANTAJ_TARIHI TIMESTAMP,"
                +  "AKTARILDI INTEGER,"
                +  "AKTARILDI_ONAY INTEGER,"
                +  "AKTARILMA_TARIHI TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE  tarim_istakip_calisma_servis ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "GLOBALID TEXT,"
                +  "TICID INTEGER,"
                +  "FISNO TEXT,"
                +  "PUANTAJ_TARIHI TIMESTAMP,"
                +  "USER_ID TEXT,"
                +  "SERVIS1 TEXT,"
                +  "SERVIS2 TEXT,"
                +  "SERVIS3 TEXT,"
                +  "SERVIS4 TEXT,"
                +  "SERVIS5 TEXT,"
                +  "SERVIS6 TEXT,"
                +  "SERVIS7 TEXT,"
                +  "SERVIS8 TEXT,"
                +  "SERVIS9 TEXT,"
                +  "SERVIS10 TEXT,"
                +  "SERVIS11 TEXT,"
                +  "SERVIS12 TEXT,"
                +  "SERVIS13 TEXT,"
                +  "SERVIS14 TEXT,"
                +  "SERVIS15 TEXT,"
                +  "SERVIS16 TEXT,"
                +  "SERVIS17 TEXT,"
                +  "SERVIS18 TEXT,"
                +  "SERVIS19 TEXT,"
                +  "SERVIS20 TEXT,"
                +  "SERVIS1SAYI TEXT,"
                +  "SERVIS2SAYI TEXT,"
                +  "SERVIS3SAYI TEXT,"
                +  "SERVIS4SAYI TEXT,"
                +  "SERVIS5SAYI TEXT,"
                +  "SERVIS6SAYI TEXT,"
                +  "SERVIS7SAYI TEXT,"
                +  "SERVIS8SAYI TEXT,"
                +  "SERVIS9SAYI TEXT,"
                +  "SERVIS10SAYI TEXT,"
                +  "SERVIS11SAYI TEXT,"
                +  "SERVIS12SAYI TEXT,"
                +  "SERVIS13SAYI TEXT,"
                +  "SERVIS14SAYI TEXT,"
                +  "SERVIS15SAYI TEXT,"
                +  "SERVIS16SAYI TEXT,"
                +  "SERVIS17SAYI TEXT,"
                +  "SERVIS18SAYI TEXT,"
                +  "SERVIS19SAYI TEXT,"
                +  "SERVIS20SAYI TEXT,"
                +  "AKTARILDI INTEGER,"
                +  "AKTARILDI_ONAY INTEGER,"
                +  "AKTARILMA_TARIHI TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE servis ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "BOLGE TEXT,"
                +  "FIRMA INTEGER,"
                +  "EKIP_LIDERI TEXT,"
                +  "TEMSILCI INTEGER,"
                +  "SERVIS1 TEXT,"
                +  "SERVIS2 TEXT,"
                +  "SERVIS3 TEXT,"
                +  "SERVIS4 TEXT,"
                +  "SERVIS5 TEXT,"
                +  "SERVIS6 TEXT,"
                +  "SERVIS7 TEXT,"
                +  "SERVIS8 TEXT,"
                +  "SERVIS9 TEXT,"
                +  "SERVIS10 TEXT,"
                +  "SERVIS11 TEXT,"
                +  "SERVIS12 TEXT,"
                +  "SERVIS13 TEXT,"
                +  "SERVIS14 TEXT,"
                +  "SERVIS15 TEXT,"
                +  "SERVIS16 TEXT,"
                +  "SERVIS17 TEXT,"
                +  "SERVIS18 TEXT,"
                +  "SERVIS19 TEXT,"
                +  "SERVIS20 TEXT,"
                +  "SERVIS_PERSONEL TEXT,"
                +  "ALINMA_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

        CREATE_TABLE = "CREATE TABLE kartno_iptal ("
                +  "OID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +  "KARTNO TEXT,"
                +  "PERSONELID INTEGER,"
                +  "USER_ID TEXT,"
                +  "AKTARILDI INTEGER,"
                +  "AKTARILMA_TARIHI TIMESTAMP,"
                +  "ISLEM_TARIHI TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);

    }

    public void syncData(){ //id si belli olan row u silmek için


        /*SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KITAP_ID + " = ?",
                new String[] { String.valueOf(id) });
        //db.close();*/
    }

    public void tarihEkle(String tarih){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("gunceltarih", null, null);

        ContentValues values = new ContentValues();
        values.put("SERVERDATE", tarih);

        db.insert("gunceltarih", null, values);
    }

    public void usersEkle(HashMap<String,String> tmp) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", null, null);

        ContentValues values = new ContentValues();
        values.put("ID", tmp.get("id"));
        values.put("USERNAME", tmp.get("username"));
        values.put("PASSWORD", tmp.get("password"));
        values.put("PASSWORDTXT", tmp.get("passwordtxt"));
        values.put("REMPASS", tmp.get("rempass"));
        values.put("NAME", tmp.get("name"));
        values.put("PUANTAJ_YETKI", tmp.get("puantaj_yetki"));

        db.insert("users", null, values);
        //db.close();
    }

    public void usersFisnoDel() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("user_fistanim", null, null);
    }

    public void usersFisnoGuncelle(String fisbas, String fisbit, String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into user_fistanim (user_id, fisbas, fisbit) values ('"+userid+"','"+fisbas+"','"+fisbit+"')");
    }

    public void usersGuncelle(String ptxt, String rem, String userid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("PASSWORDTXT", ptxt);
        values.put("REMPASS", rem);

        db.update("users", values, "id='"+userid+"'", null);
        //db.close();
    }

    public void usersGuncelle2(String ptxt, String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PASSWORD", ptxt);
        db.update("users", values, "id='"+userid+"'", null);
    }

    public void sifremiUnuttumEkle(HashMap<String,String> tmp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ID", tmp.get("id"));
        values.put("USERNAME", tmp.get("username"));
        values.put("PASSWORD", tmp.get("password"));
        values.put("ISLEM", 0);
        values.put("STATUS", 0);
        db.insert("sifremi_unuttum", null, values);
        //db.close();
    }

    public void sifremiUnuttumGuncelle(HashMap<String,String> tmp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID", tmp.get("id"));
        values.put("PASSWORD", tmp.get("password"));
        values.put("ISLEM", tmp.get("islem"));
        values.put("STATUS", tmp.get("status"));
        db.update("sifremi_unuttum", values, "username='"+tmp.get("username")+"'", null);
    }


    public String sifremiUnuttumSure() {
        String res="";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select current_timestamp, Cast ((JulianDay(current_timestamp) - JulianDay(islem_tarihi)) * 24 * 60 As Integer) from sifremi_unuttum", null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            Log.w("suz", cursor.getString(0)+" - "+cursor.getString(1));
            res=cursor.getString(1);
        }
        cursor.close();

        return res;
    }

    public void firmaBolgeEkle(HashMap<String,String> tmp) {
        if(!firmaBolgeVarmi(tmp)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("YETKILI", tmp.get("yetkili"));
            values.put("BOLGE", tmp.get("bolge"));
            values.put("BOLGE2", tmp.get("bolge2"));
            values.put("FIRMA_ID", tmp.get("firma_id"));
            values.put("YETKILI_ID", tmp.get("yetkili_id"));
            values.put("TEMSILCI", tmp.get("temsilci"));
            values.put("DEVAM", tmp.get("devam"));

            db.insert("tarim_istakip_firma_bolge", null, values);
            ////db.close();
        }
    }

    public boolean firmaBolgeVarmi(HashMap<String,String> tmp){
        boolean stat=false;
        String selectQuery = "SELECT YETKILI FROM tarim_istakip_firma_bolge WHERE YETKILI='"+tmp.get("yetkili")+"' AND BOLGE='"+tmp.get("bolge")+"' AND BOLGE2='"+tmp.get("bolge2")+"' AND FIRMA_ID='"+tmp.get("firma_id")+"' AND YETKILI_ID='"+tmp.get("yetkili_id")+"' AND DEVAM='"+tmp.get("devam")+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            stat=true;
        }
        cursor.close();
        //db.close();
        return stat;
    }

    public String[] firmaBolgeCAGetir(String which, String bolge, String uid){
        return this.firmaBolgeCAGetir(which, bolge,uid,"");
    }

    public String[] firmaBolgeCAGetir(String which, String bolge, String uid, String tarih){
        String selectQuery="";
        List<String> res = new ArrayList<String>();

        if(which=="Çalışma Alanı"){
            selectQuery = "SELECT BOLGE FROM tarim_istakip_firma_bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TRIM(BOLGE2)='"+bolge+"' AND TEMSILCI='"+uid+"' GROUP BY BOLGE ORDER BY BOLGE";
            if (!tarih.equals("")) {
                String[] ntarih = tarih.split(" / ");
                selectQuery = "SELECT fb.BOLGE as BLG FROM tarim_istakip_firma_bolge fb inner join tarim_istakip_calisma fc on fb.bolge=fc.calisma WHERE DEVAM=1 AND BOLGE2<>'' AND TRIM(BOLGE2)='"+bolge+ "' AND TEMSILCI='" + uid + "' AND ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "' GROUP BY BLG ORDER BY BLG";
            }
        }else {
            selectQuery = "SELECT TRIM(BOLGE2) as BLG FROM tarim_istakip_firma_bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TEMSILCI='" + uid + "' GROUP BY BLG ORDER BY BLG";

            if (!tarih.equals("")) {
                String[] ntarih = tarih.split(" / ");
                selectQuery = "SELECT TRIM(BOLGE2) as BLG FROM tarim_istakip_firma_bolge fb inner join tarim_istakip_calisma fc on fb.bolge2=fc.bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TEMSILCI='" + uid + "' AND ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "' GROUP BY BLG ORDER BY BLG";
            }
        }
        //Log.w("firmaBolgeCAGetir", selectQuery);
        if(selectQuery!="") {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                res.add("Seçiniz");
                do {
                    res.add(cursor.getString(0));
                } while (cursor.moveToNext());
                cursor.close();
            }
            //db.close();
        }
        Log.w("firmalaBolgeGetir", Arrays.toString(res.toArray()));
        return res.toArray(new String[res.size()]);
    }

    public String[] firmaBolgeCAGetir(String which, String bolge, String firma, String uid, String tarih){
        String selectQuery="";
        List<String> res = new ArrayList<String>();

        if(which=="Çalışma Alanı"){
            selectQuery = "SELECT BOLGE FROM tarim_istakip_firma_bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TRIM(BOLGE2)='"+bolge+"' AND FIRMA_ID='"+firma+"' AND TEMSILCI='"+uid+"' GROUP BY BOLGE ORDER BY BOLGE";
            if (!tarih.equals("")) {
                String[] ntarih = tarih.split(" / ");
                selectQuery = "SELECT fb.BOLGE as BLG FROM tarim_istakip_firma_bolge fb inner join tarim_istakip_calisma fc on fb.bolge=fc.calisma WHERE DEVAM=1 AND BOLGE2<>'' AND TRIM(BOLGE2)='"+bolge+ "' AND FIRMA_ID='"+firma+"' AND TEMSILCI='" + uid + "' AND ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "' GROUP BY BLG ORDER BY BLG";
            }
        }else {
            selectQuery = "SELECT TRIM(BOLGE2) as BLG FROM tarim_istakip_firma_bolge WHERE DEVAM=1 AND BOLGE2<>'' AND FIRMA_ID='"+firma+"' AND TEMSILCI='" + uid + "' GROUP BY BLG ORDER BY BLG";

            if (!tarih.equals("")) {
                String[] ntarih = tarih.split(" / ");
                selectQuery = "SELECT TRIM(BOLGE2) as BLG FROM tarim_istakip_firma_bolge fb inner join tarim_istakip_calisma fc on fb.bolge2=fc.bolge WHERE DEVAM=1 AND BOLGE2<>'' AND FIRMA_ID='"+firma+"' AND TEMSILCI='" + uid + "' AND ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "' GROUP BY BLG ORDER BY BLG";
            }
        }
        //Log.w("firmaBolgeCAGetir", selectQuery);
        if(selectQuery!="") {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                res.add("Seçiniz");
                do {
                    res.add(cursor.getString(0));
                } while (cursor.moveToNext());
                cursor.close();
            }
            //db.close();
        }
        Log.w("firmalaBolgeGetir", Arrays.toString(res.toArray()));
        return res.toArray(new String[res.size()]);
    }

    public void firmaEkle(HashMap<String,String> tmp) {
        if(!kayitVarmi("firmalar", "ID", "ID='"+tmp.get("id")+"'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID", tmp.get("id"));
            values.put("FIRMA", tmp.get("firma"));
            values.put("DEVAM", tmp.get("devam"));

            db.insert("firmalar", null, values);
            ////db.close();
        }
    }

    public boolean kayitVarmi(String tablo, String kolon, String where){
        boolean stat=false;
        String selectQuery = "SELECT "+kolon+" FROM "+tablo+" WHERE "+where;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            stat=true;
        }
        cursor.close();
        ////db.close();
        return stat;
    }

    public KeyValueP[] firmaGetir(String uid, String tarih){
        String selectQuery = "SELECT DISTINCT FIRMA_ID FROM tarim_istakip_firma_bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TEMSILCI='"+uid+"'";

        if (!tarih.equals("")) {
            String[] ntarih = tarih.split(" / ");
            selectQuery = "SELECT  DISTINCT FIRMA_ID FROM tarim_istakip_firma_bolge fb inner join tarim_istakip_calisma fc on trim(fb.bolge2)=fc.bolge WHERE DEVAM=1 AND BOLGE2<>'' AND ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "'";
        }


        //Log.w("firmaGetir",selectQuery);
        List<KeyValueP> res=new ArrayList<KeyValueP>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                String firmaid="";
                int i=0;
                cursor.moveToFirst();
                do {
                    firmaid+=cursor.getString(0);
                    if(i<cursor.getCount()-1){
                        firmaid+=",";
                    }
                    i++;
                } while (cursor.moveToNext());
                cursor.close();
                if(firmaid!="") {
                    String query = "SELECT ID, FIRMA FROM firmalar where ID in (" + firmaid + ") order by FIRMA";
                    Log.w("firmaGetir", query);
                    Cursor cursor2 = db.rawQuery(query, null);
                    if (cursor2 != null) {
                        cursor2.moveToFirst();

                        do{

                            KeyValueP tmp = new KeyValueP();
                            tmp.ID = cursor2.getString(0);
                            tmp.name = cursor2.getString(1);
                            res.add(tmp);
                        }while (cursor2.moveToNext());
                        cursor2.close();
                    }
                }
            }
        }catch (Exception ex){
            Log.w("firmaGetir",ex.getMessage());
        }finally {
            ////db.close();
        }

        Log.w("firmaGetir", Arrays.toString(res.toArray()));
        return res.toArray(new KeyValueP[res.size()]);
    }

    public KeyValueP[] firmaGetir(String bolge, String calisma, String uid) {
        return firmaGetir(bolge, calisma, uid, "");
    }

    public KeyValueP[] firmaGetir(String bolge, String calisma, String uid, String tarih){
        String selectQuery = "SELECT FIRMA_ID, YETKILI_ID FROM tarim_istakip_firma_bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TRIM(BOLGE2)='"+bolge+"' AND BOLGE='"+calisma+"' AND TEMSILCI='"+uid+"'";

        if (!tarih.equals("")) {
            String[] ntarih = tarih.split(" / ");
            selectQuery = "SELECT  DISTINCT FIRMA_ID, YETKILI_ID FROM tarim_istakip_firma_bolge fb inner join tarim_istakip_calisma fc on trim(fb.bolge2)=fc.bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TRIM(BOLGE2)='"+bolge+ "' AND TEMSILCI='" + uid + "' AND fb.BOLGE='"+calisma+"' AND ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "'";
        }


        //Log.w("firmaGetir",selectQuery);
        List<KeyValueP> res=new ArrayList<KeyValueP>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor!=null  && cursor.getCount()>0) {
            cursor.moveToFirst();
            do{
                String query="SELECT ID, FIRMA FROM firmalar where ID='"+cursor.getString(0)+"'";
                Log.w("firmaGetir",query);
                Cursor cursor2 = db.rawQuery(query, null);
                if (cursor2!=null) {
                    if(cursor2.moveToFirst()) {
                        KeyValueP tmp=new KeyValueP();
                        tmp.ID=cursor2.getString(0);
                        tmp.name=cursor2.getString(1);
                        res.add(tmp);
                    }
                    cursor2.close();
                }
            }while(cursor.moveToNext());
            cursor.close();
        }
        //db.close();
        Log.w("firmaGetir", Arrays.toString(res.toArray()));
        return res.toArray(new KeyValueP[res.size()]);
    }

    public void yetkiliEkle(HashMap<String,String> tmp) {
        if(!kayitVarmi("firmalar_yetkili_kisi", "ID", "ID='"+tmp.get("id")+"'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID", tmp.get("id"));
            values.put("YETKILI", tmp.get("yetkili"));

            db.insert("firmalar_yetkili_kisi", null, values);
            ////db.close();
        }
    }

    public KeyValueP[] yetkiliGetir(String bolge, String calisma, String uid){
        return yetkiliGetir(bolge, calisma, uid, "");
    }

    public KeyValueP[] yetkiliGetir(String bolge, String calisma, String uid, String tarih){
        String selectQuery = "SELECT FIRMA_ID, YETKILI_ID FROM tarim_istakip_firma_bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TRIM(BOLGE2)='"+bolge+"' AND BOLGE='"+calisma+"' AND TEMSILCI='"+uid+"'";

        if (!tarih.equals("")) {
            String[] ntarih = tarih.split(" / ");
            selectQuery = "SELECT  DISTINCT FIRMA_ID, YETKILI_ID FROM tarim_istakip_firma_bolge fb inner join tarim_istakip_calisma fc on trim(fb.bolge2)=fc.bolge WHERE DEVAM=1 AND BOLGE2<>'' AND TRIM(BOLGE2)='"+bolge+ "' AND TEMSILCI='" + uid + "' AND fb.BOLGE='"+calisma+"' AND ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "'";
        }

        //Log.w("yetkiliGetir",selectQuery);
        List<KeyValueP> res=new ArrayList<KeyValueP>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor!=null  && cursor.getCount()>0) {
            cursor.moveToFirst();
            do{
                String query="SELECT ID, YETKILI FROM firmalar_yetkili_kisi where ID='"+cursor.getString(1)+"'";
                Log.w("firmaGetir",query);
                Cursor cursor2 = db.rawQuery(query, null);
                if (cursor2!=null) {
                    if(cursor2.moveToFirst()) {
                        KeyValueP tmp=new KeyValueP();
                        tmp.ID=cursor2.getString(0);
                        tmp.name=cursor2.getString(1);
                        res.add(tmp);
                    }
                    cursor2.close();
                }
            }while(cursor.moveToNext());
            cursor.close();
        }
        //db.close();
        Log.w("yetkiliGetir", Arrays.toString(res.toArray()));
        return res.toArray(new KeyValueP[res.size()]);
    }

    public void ekiplideriEkle(HashMap<String,String> tmp) {
        if(!kayitVarmi("tarim_istakip_ekiplideri", "ID", "ID='"+tmp.get("id")+"'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID", tmp.get("id"));
            values.put("AD", tmp.get("ad"));
            values.put("EKALAN2", tmp.get("ekalan2"));
            values.put("EKALAN3", tmp.get("ekalan3"));
            values.put("EKALAN4", tmp.get("ekalan4"));
            values.put("EKALAN6", tmp.get("ekalan6"));
            values.put("EKALAN7", tmp.get("ekalan7"));
            values.put("EKALAN8", tmp.get("ekalan8"));
            values.put("FIRMA", tmp.get("firma"));
            values.put("BOLGE", tmp.get("bolge"));
            values.put("DEVAM", tmp.get("devam"));
            values.put("AKTIF", tmp.get("aktif"));

            db.insert("tarim_istakip_ekiplideri", null, values);
            ////db.close();
        }
    }

    public KeyValueP[] ekiplideriGetir(String bolge) {
        return ekiplideriGetir(bolge, "", "");
    }

    public KeyValueP[] ekiplideriGetir(String bolge, String firma) {
        return ekiplideriGetir(bolge, firma, "");
    }

    public KeyValueP[] ekiplideriGetir(String bolge, String firma, String tarih){
        Log.w("ekiplideri","bolge:"+bolge+" - firma:"+firma);
        String selectQuery = "SELECT ID, AD, EKALAN2, EKALAN3, EKALAN4, EKALAN6, EKALAN7, EKALAN8," +
                             " fb.FIRMA, fb.BOLGE FROM tarim_istakip_ekiplideri el inner join tarim_istakip_firma_bolge_lider fb on el.id=fb.ekip_lideri WHERE AKTIF>0 AND DEVAM=1 AND fb.BOLGE='"+bolge+"'"+(firma.equals("")?"":" AND fb.FIRMA="+firma)+" ORDER BY AD";

        if (!tarih.equals("")) {
            String[] ntarih = tarih.split(" / ");
            selectQuery = "SELECT DISTINCT ID, AD, EKALAN2, EKALAN3, EKALAN4, EKALAN6, EKALAN7, EKALAN8, fb.FIRMA, fb.BOLGE FROM tarim_istakip_ekiplideri ekip "
                          + "inner join tarim_istakip_firma_bolge_lider fb on ekip.id=fb.ekip_lideri inner join tarim_istakip_calisma fc on fb.bolge=fc.calisma WHERE DEVAM=1  AND fb.BOLGE='"+bolge+"' AND fb.FIRMA='"+ firma +"' AND ISE_BASLAMA_TARIHI='" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "' order by ad";
        }

        Log.w("ekiplideriGetir",selectQuery);
        List<KeyValueP> res=new ArrayList<KeyValueP>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    KeyValueP tmp = new KeyValueP();
                    tmp.ID = cursor.getString(0);
                    tmp.name = cursor.getString(1);
                    res.add(tmp);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("ekiplideriGetir", ex.getMessage());
        }finally {
            //db.close();
        }

        Log.w("ekiplideriGetir", Arrays.toString(res.toArray()));
        return res.toArray(new KeyValueP[res.size()]);
    }

    public void ekiplideriBolgeEkle(HashMap<String,String> tmp) {
        if(!kayitVarmi("tarim_istakip_firma_bolge_lider", "OID", "ekip_lideri='"+tmp.get("ekiplideri")+"' and firma="+tmp.get("firma")+" and bolge='"+tmp.get("bolge")+"' and bolge2='"+tmp.get("bolge2")+"'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("EKIP_LIDERI", tmp.get("ekiplideri"));
            values.put("FIRMA", tmp.get("firma"));
            values.put("BOLGE", tmp.get("bolge"));
            values.put("BOLGE2", tmp.get("bolge2"));

            db.insert("tarim_istakip_firma_bolge_lider", null, values);
            ////db.close();
        }
    }

    public void gorevEkle(HashMap<String,String> tmp) {
        if(!kayitVarmi("gorev", "ID", "ID='"+tmp.get("id")+"'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID", tmp.get("id"));
            values.put("GOREV", tmp.get("gorev"));
            values.put("FIRMA", tmp.get("firma"));
            values.put("BOLGE", tmp.get("bolge"));

            db.insert("gorev", null, values);
            ////db.close();
        }
    }

    public KeyValueP[] gorevGetir(String bolge, String firma){
        String selectQuery = "SELECT ID, GOREV FROM gorev WHERE BOLGE='"+bolge+"' AND FIRMA="+firma;
        Log.w("gorevGetir",selectQuery);
        List<KeyValueP> res=new ArrayList<KeyValueP>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null  && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    KeyValueP tmp = new KeyValueP();
                    tmp.ID = cursor.getString(0);
                    tmp.name = cursor.getString(1);
                    res.add(tmp);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("gorevGetir", ex.getMessage());
        }finally {
            //db.close();
        }

        Log.w("gorevGetir", Arrays.toString(res.toArray()));
        return res.toArray(new KeyValueP[res.size()]);
    }

    public ArrayList<HashMap<String, String>> gorevGetir(String bolge, String firma, String ekiplideri, String tarih){
        String selectQuery = "SELECT ID, GOREV FROM gorev WHERE BOLGE='"+bolge+"' AND FIRMA="+firma;
        ArrayList<HashMap<String, String>> res= new ArrayList<HashMap<String, String>>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null  && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> rr=new HashMap<String, String>();
                    rr.put("GOREV",cursor.getString(1));
                    String[] ntarih = tarih.split(" / ");
                    Cursor cursor2 = db.rawQuery("select CINSIYET, count(*) from tarim_istakip_calisma_personel where ISE_BASLAMA_TARIHI='"+ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0]+"' and CALISMA='"+bolge+"' and FIRMA='"+firma+"' and EKIP_LIDERI='"+ekiplideri+"' and GOREV='"+cursor.getString(0)+"' group by CINSIYET",null);
                    //Log.w("--","select CINSIYET, count(*) from tarim_istakip_personel_calisma where ISE_BASLAMA_TARIHI='"+ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0]+"' and CALISMA='"+bolge+"' and FIRMA='"+firma+"' and EKIP_LIDERI='"+ekiplideri+"' and GOREV='"+cursor.getString(0)+"' group by CINSIYET");
                    if (cursor2 != null && cursor2.getCount() > 0) {
                        cursor2.moveToFirst();
                        do {
                            //Log.w("--",cursor2.getString(0)+" "+cursor2.getString(1));
                            rr.put(cursor2.getString(0), cursor2.getString(1));
                        }while (cursor2.moveToNext());
                    }
                    cursor2.close();

                    Cursor cursor3= db.rawQuery("select sum(MESAI) from tarim_istakip_calisma_personel where ISE_BASLAMA_TARIHI='"+ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0]+"' and CALISMA='"+bolge+"' and FIRMA='"+firma+"' and EKIP_LIDERI='"+ekiplideri+"' and GOREV='"+cursor.getString(0)+"'",null);
                    //HashMap<String, String> r=getOneRow(new String[]{"sum(MESAI)"},"tarim_istakip_personel_calisma", "ISE_BASLAMA_TARIHI='"+ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0]+"' and CALISMA='"+bolge+"' and FIRMA='"+firma+"' and EKIP_LIDERI='"+ekiplideri+"' and GOREV='"+cursor.getString(0)+"' group by CINSIYET");
                    String mesai="";
                    if(cursor3!=null && cursor3.getCount()>0){
                        cursor3.moveToFirst();
                        mesai=cursor3.getString(0);
                    }
                    cursor3.close();
                    rr.put("MESAI", mesai);
                    res.add(rr);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("gorevGetir", ex.getMessage());
        }finally {
            //db.close();
        }

        //Log.w("gorevGetir", Arrays.toString(res));
        return res;
    }

    public HashMap<String, String>  gorevGetir(String bolge, String calisma, String firma, String yetkili, String ekiplideri, String tarih) {
        String[] ntarih = tarih.split(" / ");
        String selectQuery = "SELECT OID, FISNO, SERVISVAR, ACIKLAMA, EKLIDOC1, EKLIDOC2, EKLIDOC3, CALISMAVAR, AKTARILDI,URUN FROM tarim_istakip_calisma WHERE BOLGE='" + bolge + "' and calisma='"+calisma+"' AND FIRMA='" + firma+"' and yetkili='"+yetkili+"' and ekip_lideri='"+ekiplideri+"' and ISE_BASLAMA_TARIHI='"+ntarih[2]+"-"+ntarih[1]+"-"+ntarih[0]+"'";
        //selectQuery="select fisno from tarim_istakip_calisma";
        HashMap<String, String> res = new HashMap<String, String>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Log.w("gorevGetir",selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                res.put("oid",cursor.getString(0));
                res.put("fisno",cursor.getString(1));
                res.put("servisvar",cursor.getString(2));
                res.put("aciklama",cursor.getString(3));
                res.put("eklidoc1",cursor.getString(4));
                res.put("eklidoc2",cursor.getString(5));
                res.put("eklidoc3",cursor.getString(6));
                res.put("calismavar",cursor.getString(7));
                res.put("aktarildi",cursor.getString(8));
                res.put("urun",cursor.getString(9));
                //Log.w("gorevGetir",cursor.getString(1));
            }
        } catch (Exception ex) {
        }
        return res;
    }

    public ArrayList<HashMap<String, String>>  gorevPersonelGetir(String ticid, String gorev, String userid, String tarih, String fisno) {
        String[] ntarih = tarih.split(" / ");
        String selectQuery = "SELECT sicilno, urunid, mesai FROM tarim_istakip_calisma_personel WHERE ticid='" + ticid + "' and gorev='"+gorev+"' and fisno='"+fisno+"'";
        //selectQuery="select fisno from tarim_istakip_calisma";
        ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Log.w("gorevGetir",selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    HashMap<String,String> tmp=new HashMap<String, String>();
                    tmp.put("sicilno", cursor.getString(0));
                    HashMap<String, String> urunbilgisi = getOneRow(new String[]{"AD", "URUN"}, "muhendis_urun","urunid="+cursor.getString(1));
                    tmp.put("urun", (urunbilgisi!=null && urunbilgisi.containsKey("AD")? urunbilgisi.get("AD") +" - "+urunbilgisi.get("URUN") :""));//cursor.getString(1));
                    tmp.put("mesai", cursor.getString(2));

                    HashMap<String,String> personel = getOneRow(new String[] {"AD", "SOYAD", "CINSIYET"}, "tarim_istakip_personel","id='"+cursor.getString(0)+"'");
                    tmp.put("adi", personel.get("AD")+" "+personel.get("SOYAD"));
                    tmp.put("cinsiyet", personel.get("CINSIYET"));
                    res.add(tmp);
                }while (cursor.moveToNext());
                //Log.w("gorevGetir",cursor.getString(1));
            }
        } catch (Exception ex) {
        }
        return res;
    }

    public ArrayList<KeyValueP>  puantajServisGetir(String ticid, String userid, String tarih, String fisno) {
        String[] ntarih = tarih.split(" / ");
        String selectQuery = "SELECT SERVIS1, SERVIS2, servis3, servis4, servis5, servis6, servis7, servis8, servis9, servis10, servis11, servis12, servis13, servis14, servis15, servis16, servis17, servis18, servis19, servis20, SERVIS1sayi, SERVIS2sayi, servis3sayi, servis4sayi, servis5sayi, servis6sayi, servis7sayi, servis8sayi, servis9sayi, servis10sayi, servis11sayi, servis12sayi, servis13sayi, servis14sayi, servis15sayi, servis16sayi, servis17sayi, servis18sayi, servis19sayi, servis20sayi FROM tarim_istakip_calisma_servis WHERE ticid='" + ticid +"' AND user_id='"+userid+"' AND fisno='"+fisno+"'";
        //selectQuery="select fisno from tarim_istakip_calisma";
        ArrayList<KeyValueP> res = new ArrayList<KeyValueP>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                for(int i=0; i<20; i++){
                    KeyValueP tmp=new KeyValueP();
                    tmp.ID=cursor.getString(i);
                    tmp.name=cursor.getString(i+20);
                    res.add(tmp);
                }while (cursor.moveToNext());
                //Log.w("gorevGetir",cursor.getString(1));
            }
        } catch (Exception ex) {
        }
        return res;
    }


    public void urunEkle(HashMap<String,String> tmp) {
        if(!kayitVarmi("muhendis_urun","ID", "URUNID='"+tmp.get("urunid")+"'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID", tmp.get("id"));
            values.put("AD", tmp.get("ad"));
            values.put("URUNID", tmp.get("urunid"));
            values.put("URUN", tmp.get("urun"));
            values.put("FIRMA", tmp.get("firma"));
            values.put("BOLGE", tmp.get("bolge"));

            db.insert("muhendis_urun", null, values);
            ////db.close();
        }
    }

    public void belgeTurEkle(HashMap<String,String> tmp) {
        if(!kayitVarmi("tarim_istakip_belge","ID", "ID='"+tmp.get("id")+"'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("ID", tmp.get("id"));
            values.put("AD", tmp.get("ad"));
            values.put("SIRA", tmp.get("sira"));
            values.put("ZORUNLU", tmp.get("zorunlu"));

            db.insert("tarim_istakip_belge", null, values);
            ////db.close();
        }
    }

    public KeyValueP[] urunGetir(String bolge, String firma, String yetkili){
        String selectQuery = "SELECT OID, AD, URUNID, URUN FROM muhendis_urun WHERE BOLGE='"+bolge+"' AND FIRMA="+firma;//and UPPER(Replace(Replace(Replace(Replace(Replace(Replace(Replace(Replace(AD, \"ü\", \"Ü\"), 'i', 'İ'), 'ğ', 'Ğ'), 'ı', 'I'), 'ö', 'Ö'), 'ç', 'Ç'), 'ş', 'Ş'), 'ü', 'Ü'))=UPPER(Replace(Replace(Replace(Replace(Replace(Replace(Replace(Replace('"+yetkili+"', \"ü\", \"Ü\"), 'i', 'İ'), 'ğ', 'Ğ'), 'ı', 'I'), 'ö', 'Ö'), 'ç', 'Ç'), 'ş', 'Ş'), 'ü', 'Ü'))";
        Log.w("urunGetir",selectQuery);
        List<KeyValueP> res=new ArrayList<KeyValueP>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            KeyValueP t = new KeyValueP();
            t.ID="-1";
            t.name="Seçiniz";
            res.add(t);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    KeyValueP tmp = new KeyValueP();
                    tmp.ID = cursor.getString(2);
                    tmp.name = cursor.getString(1) + " - " + cursor.getString(3);
                    res.add(tmp);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("urunGetir", ex.getMessage());
        }finally {
            //db.close();
        }

        Log.w("urunGetir", Arrays.toString(res.toArray()));
        return res.toArray(new KeyValueP[res.size()]);
    }

    public KeyValueP[] belgeturGetir(){
        String selectQuery = "SELECT ID, AD, SIRA FROM tarim_istakip_belge order by SIRA";
        Log.w("belgeturGetir",selectQuery);
        List<KeyValueP> res=new ArrayList<KeyValueP>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            KeyValueP t = new KeyValueP();
            t.ID="-1";
            t.name="Seçiniz";
            res.add(t);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    KeyValueP tmp = new KeyValueP();
                    tmp.ID = cursor.getString(0);
                    tmp.name = cursor.getString(1);
                    res.add(tmp);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("belgeturGetir", ex.getMessage());
        }finally {
            //db.close();
        }

        Log.w("urunGetir", Arrays.toString(res.toArray()));
        return res.toArray(new KeyValueP[res.size()]);
    }


    public void servisEkle(HashMap<String,String> tmp) {
        if(!kayitVarmi("servis","OID", "FIRMA='"+tmp.get("firma")+"' AND BOLGE='"+tmp.get("bolge")+"' AND TEMSILCI='"+tmp.get("temsilci")+"' AND EKIP_LIDERI='"+tmp.get("ekiplideri")+"'")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("TEMSILCI", tmp.get("temsilci"));
            values.put("FIRMA", tmp.get("firma"));
            values.put("BOLGE", tmp.get("bolge"));
            values.put("EKIP_LIDERI", tmp.get("ekiplideri"));
            values.put("SERVIS1", tmp.get("servis1"));
            values.put("SERVIS2", tmp.get("servis2"));
            values.put("SERVIS3", tmp.get("servis3"));
            values.put("SERVIS4", tmp.get("servis4"));
            values.put("SERVIS5", tmp.get("servis5"));
            values.put("SERVIS6", tmp.get("servis6"));
            values.put("SERVIS7", tmp.get("servis7"));
            values.put("SERVIS8", tmp.get("servis8"));
            values.put("SERVIS9", tmp.get("servis9"));
            values.put("SERVIS10", tmp.get("servis10"));
            values.put("SERVIS11", tmp.get("servis11"));
            values.put("SERVIS12", tmp.get("servis12"));
            values.put("SERVIS13", tmp.get("servis13"));
            values.put("SERVIS14", tmp.get("servis14"));
            values.put("SERVIS15", tmp.get("servis15"));
            values.put("SERVIS16", tmp.get("servis16"));
            values.put("SERVIS17", tmp.get("servis17"));
            values.put("SERVIS18", tmp.get("servis18"));
            values.put("SERVIS19", tmp.get("servis19"));
            values.put("SERVIS20", tmp.get("servis20"));
            values.put("SERVIS_PERSONEL", tmp.get("servis_personel"));
            db.insert("servis", null, values);
            ////db.close();
        }
    }

    public String[] servisGetir(String bolge, String firma, String ekiplideri){
        //ekip_lideri='$ekip_lideri' && firma='$firma_id' && bolge='$bolge' limit 0,1
        String selectQuery = "SELECT SERVIS1, SERVIS2, servis3, servis4, servis5, servis6, servis7, servis8, servis9, servis10, servis11, servis12, servis13, servis14, servis15, servis16, servis17, servis18, servis19, servis20, servis_personel FROM servis WHERE BOLGE='"+bolge+"' AND FIRMA="+firma+" and EKIP_LIDERI='"+ekiplideri+"'";
        Log.w("servisGetir",selectQuery);
        String[] res=new String[21];

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                for (int i=0; i<21; i++){
                      res[i]=cursor.getString(i).toString();
                    Log.w("servis"+i, ""+cursor.getString(i).toString());
                }
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("servisGetir", ex.getMessage());
        }finally {
            ////db.close();
        }

        return res;
    }


    public String isbaslamaOnay(String bolge, String calisma, String firma, String yetkili, String urunid, String ekiplideri, String tarih, String fisno, String gorev, String mesai, String barkod, String tc, String cinsiyet)
    {
        String res="";

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            HashMap<String,String> person = getOneRow(new String[]{"ID"},"tarim_istakip_personel","ID='"+barkod+"' and TC='"+tc+"'");
            //Log.w("-",person.get("ID")+" "+barkod);
            if(person.get("ID").equals(barkod)) {
                String[] ntarih = tarih.split(" / ");
                Cursor cursor = db.rawQuery("select personel_id from tarim_istakip_personel_calisma where personel_id='" + person.get("ID") + "' and date(ISE_BASLAMA_TARIHI)=date('" + ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0] + "')", null);
                Log.w("-","select personel_id from tarim_istakip_personel_calisma where personel_id='"+person.get("ID")+"' and date(ISE_BASLAMA_TARIHI)=date('"+ntarih[2]+"-"+ntarih[1]+"-"+ntarih[0]+"')");
                if (cursor != null && cursor.getCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put("PERSONEL_ID", barkod);
                    values.put("CINSIYET", cinsiyet);
                    values.put("ISE_BASLAMA_TARIHI", ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0]);
                    values.put("BOLGE", bolge);
                    values.put("CALISMA", calisma);
                    values.put("FIRMA", firma);
                    values.put("EKIP_LIDERI", ekiplideri);
                    values.put("GOREV", gorev);
                    values.put("MESAI", mesai);
                    values.put("YETKILI", yetkili);
                    values.put("URUNID", urunid);
                    values.put("FISNO", fisno);
                    values.put("BASBITISONAY", 1);
                    values.put("AKTARILDI", 0);
                    values.put("AKTARILDI_ONAY", 0);

                    db.insert("tarim_istakip_personel_calisma", null, values);
                    res = "ok";
                } else {
                    res = "exists";
                }
            }else{
                throw new Exception("Sicil No - TC uyuşmuyor.");
            }
        } catch (Exception ex) {
            res="error -"+ex.getMessage();
                Log.w("isbaslamaOnay", ex.getMessage());
        }finally {
            //db.close();
        }

        return res;
    }

    //aktarilmadurumu = 0 => aktarım yapalabilir.
    //                = 1 => aktarıldı.
    //                = -1 => sayı formlarının yüklenmesi bekleniyor.
    public String isbaslamaOnay(GunlukPuantajData tmp, int aktarilmadurumu)
    {
        String res="";

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] ntarih = tmp.getTarih().split(" / ");
            /*
            HashMap<String,String> calisma = getOneRow(new String[]{"OID"},"tarim_istakip_calisma","bolge='"+tmp.getBolge()+"' and calisma='"+tmp.getCalismaalani()+"' and firma='"+tmp.getFirma()+"' and  yetkili='"+tmp.getYetkili()+"' and ekip_lideri='"+tmp.getEkiplideri()+"'and ISE_BASLAMA_TARIHI='"+ntarih[2]+"-"+ntarih[1]+"-"+ntarih[0]+"' and fisno='"+tmp.getFisno()+"'");

            if(tmp.getKayitdurumu().equals("guncelleme") && calisma.size()>0){
                db.execSQL("delete from tarim_istakip_calisma where OID='" + calisma.get("OID") + "'");
                db.execSQL("delete from tarim_istakip_calisma_personel where ticid='" + calisma.get("OID") + "'");
                db.execSQL("delete from tarim_istakip_calisma_servis where ticid='" + calisma.get("OID") + "'");
                calisma=new HashMap<String,String>();
            }*/

            HashMap<String,String> calisma = getOneRow(new String[]{"OID"},"tarim_istakip_calisma","globalid='"+tmp.getGlobalid()+"'");

            if(tmp.getKayitdurumu().equals("guncelleme") && calisma.size()>0){
                //db.execSQL("delete from tarim_istakip_calisma where OID='" + calisma.get("OID") + "'");
                //db.execSQL("delete from tarim_istakip_calisma_personel where ticid='" + calisma.get("OID") + "'");
                //db.execSQL("delete from tarim_istakip_calisma_servis where ticid='" + calisma.get("OID") + "'");

                db.execSQL("delete from tarim_istakip_calisma where globalid='" + tmp.getGlobalid() + "'");
                db.execSQL("delete from tarim_istakip_calisma_personel where globalid='" + tmp.getGlobalid() + "'");
                db.execSQL("delete from tarim_istakip_calisma_servis where globalid='" + tmp.getGlobalid() + "'");
                calisma=new HashMap<String,String>();
            }

            if(calisma.size()==0) {
                ContentValues values = new ContentValues();
                values.put("ISE_BASLAMA_TARIHI", ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0]);
                values.put("USER_ID", tmp.getUserid());
                values.put("GLOBALID", tmp.getGlobalid());
                values.put("BOLGE", tmp.getBolge());
                values.put("FIRMA", tmp.getFirma());
                values.put("EKIP_LIDERI", tmp.getEkiplideri());
                values.put("CALISMA", tmp.getCalismaalani());
                values.put("YETKILI", tmp.getYetkili());
                values.put("FISNO", tmp.getFisno());
                values.put("URUN", tmp.getUrun());
                values.put("SERVISVAR", tmp.getServisSayisi());
                values.put("CALISMAVAR", tmp.getCalismavar());
                values.put("ACIKLAMA", tmp.getAciklama());
                values.put("EKLIDOC1",tmp.getSayiDoc1());
                values.put("EKLIDOC2",tmp.getSayiDoc2());
                values.put("EKLIDOC3",tmp.getSayiDoc3());
                values.put("AKTARILDI", aktarilmadurumu);
                values.put("AKTARILDI_ONAY", 0);

                db.insert("tarim_istakip_calisma", null, values);

                //calisma = getOneRow(new String[]{"OID"},"tarim_istakip_calisma","bolge='"+tmp.getBolge()+"' and calisma='"+tmp.getCalismaalani()+"' and firma='"+tmp.getFirma()+"' and  yetkili='"+tmp.getYetkili()+"' and ekip_lideri='"+tmp.getEkiplideri()+"'and ISE_BASLAMA_TARIHI='"+ntarih[2]+"-"+ntarih[1]+"-"+ntarih[0]+"' and fisno='"+tmp.getFisno()+"'");
                calisma = getOneRow(new String[]{"OID"},"tarim_istakip_calisma","globalid='"+tmp.getGlobalid()+"'");
                if(calisma.size()==1){
                    Log.w("personelVeri", "here");
                    for ( String key : tmp.getGorevler().keySet() ) {
                        List<GunlukPersonelData> gperd = tmp.getPersonel(key);
                        if(gperd!=null) {
                            for (int i = 0; i < gperd.size(); i++) {
                                Log.w("personelVeri", gperd.get(i).sicilno+" "+gperd.get(i).urun);
                                ContentValues values2 = new ContentValues();
                                values2.put("SICILNO", gperd.get(i).sicilno);
                                HashMap<String,String> perbilgisi=getOneRow(new String[]{"KARTNO","TC"},"tarim_istakip_personel","id="+gperd.get(i).sicilno);
                                //Log.w("isonay", perbilgisi.get("KARTNO")+" - "+perbilgisi.get("TC"));
                                values2.put("KARTNO", perbilgisi.get("KARTNO"));
                                values2.put("GLOBALID", tmp.getGlobalid());
                                values2.put("TC", perbilgisi.get("TC"));
                                values2.put("TICID", calisma.get("OID"));
                                values2.put("URUNID", gperd.get(i).urun);
                                values2.put("GOREV", key);
                                values2.put("MESAI", gperd.get(i).mesai);
                                values2.put("KARTLAEKLENDI", gperd.get(i).kartlaeklendi);
                                values2.put("USER_ID", tmp.getUserid());
                                values2.put("FISNO", tmp.getFisno());
                                values2.put("AKTARILDI", aktarilmadurumu);
                                values2.put("AKTARILDI_ONAY", 0);

                                db.insert("tarim_istakip_calisma_personel", null, values2);
                                Log.w("personelVeri2","here");
                            }
                        }
                    }

                    if (tmp.getServisSayisi()>0){
                        ContentValues values2 = new ContentValues();
                        values2.put("TICID", calisma.get("OID"));
                        String[] servisliste=servisGetir(tmp.getCalismaalani(), tmp.getFirma(), tmp.getEkiplideri());
                        HashMap<String,String> servis=tmp.getServis();
                        Log.w("servissx",""+servisliste.length);
                        for(int i=1; i<=20; i++){
                            if(servis.get(""+(i-1))!=null && !servis.get(""+(i-1)).equals("0")) {
                                values2.put("SERVIS" + i, servisliste[i-1]);
                                values2.put("SERVIS" + i + "SAYI", servis.get(""+(i-1)));
                            }
                            //Log.w("servissx"+i,servisliste[i-1]+" "+servis.get(""+(i-1)));
                        }
                        values2.put("USER_ID", tmp.getUserid());
                        values2.put("GLOBALID", tmp.getGlobalid());
                        values2.put("FISNO", tmp.getFisno());
                        values2.put("AKTARILDI", aktarilmadurumu);
                        values2.put("AKTARILDI_ONAY", 0);

                        db.insert("tarim_istakip_calisma_servis", null, values2);
                    }
                }
                res = "ok";
            } else {
                res = "exists";
            }
        } catch (Exception ex) {
            res="error -"+ex.getMessage();
            Log.w("isbaslamaOnay2", ex.getMessage());
        }finally {
            //db.close();
        }

        return res;
    }

    public String isbitirmeOnay(String barkod, String isbastarih, String isbittarih)
    {
        String res="";

        if(barkod.indexOf(' ')>0) {
            SQLiteDatabase db = this.getReadableDatabase();
            try {
                String[] str = barkod.split(" ");
                String id = str[0];
                String ad = str[1];
                String soyad = str[2];

                String[] ntarih=isbastarih.split(" / ");
                String[] etarih=isbittarih.split(" / ");
                Cursor cursor = db.rawQuery("select personel_id from tarim_istakip_personel_calisma where personel_id='"+id+"' and date(ISE_BASLAMA_TARIHI)=date('"+ntarih[2]+"-"+ntarih[1]+"-"+ntarih[0]+"') and BASBITISONAY=1",null);
                Log.w("ibitOnay","select personel_id from tarim_istakip_personel_calisma where personel_id='"+id+"' and date(ISE_BASLAMA_TARIHI)=date('"+ntarih[2]+"-"+ntarih[1]+"-"+ntarih[0]+"') and BASBITISONAY=1");
                if (cursor != null && cursor.getCount()>0) {
                    Log.w("ibitOnay","here");
                    ContentValues values = new ContentValues();
                    values.put("PERSONEL_ID", id);
                    values.put("IS_BITIS_TARIHI", etarih[2]+"-"+etarih[1]+"-"+etarih[0]);
                    values.put("BASBITISONAY", 2);

                    db.update("tarim_istakip_personel_calisma", values, "personel_id='"+id+"' and date(ISE_BASLAMA_TARIHI)=date('"+ntarih[2]+"-"+ntarih[1]+"-"+ntarih[0]+"') and BASBITISONAY=1", null);
                    res = "ok";
                    Log.w("ibitOnay","here2");
                }
            } catch (Exception ex) {
                res="error -"+ex.getMessage();
                Log.w("ibitOnay", ex.getMessage());
            }finally {
                //db.close();
            }        }

        return res;
    }

    public String gunlukPuantajPersonelServisSil(String ticid, String tarih)
    {
        String res="";

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] ntarih=tarih.split(" / ");
            HashMap<String, String> gunlukpuantaj=getOneRow(new String[]{"OID"},"tarim_istakip_calisma","OID='"+ticid+"' and aktarildi=0 and ISE_BASLAMA_TARIHI='"+ntarih[2]+"-"+ntarih[1]+"-"+ntarih[0]+"'");
            if(gunlukpuantaj.get("OID").equals(ticid)) {
                db.execSQL("delete from tarim_istakip_calisma_personel where ticid='" + ticid + "'");
                db.execSQL("delete from tarim_istakip_calisma_servis where ticid='" + ticid + "'");
                res="ok";
            }
        } catch (Exception ex) {
            res="error -"+ex.getMessage();
            Log.w("gunlukPuantajG error", ""+ex.getMessage());
        }finally {
            //db.close();
        }

        return res;
    }

    public ArrayList<HashMap<String, String>> bekleyenGonderilenGetir(String userid, String bekleyen){
        String selectQuery = "SELECT OID, ISE_BASLAMA_TARIHI, EKIP_LIDERI, FISNO, EKLIDOC1, EKLIDOC2, EKLIDOC3, AKTARILDI, GLOBALID FROM tarim_istakip_calisma WHERE user_id='"+userid+"' and globalid is not null AND AKTARILDI "+bekleyen+" order by ISE_BASLAMA_TARIHI, FISNO";
        ArrayList<HashMap<String, String>> res= new ArrayList<HashMap<String, String>>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null  && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> rr=new HashMap<String, String>();
                    rr.put("oid",cursor.getString(0));
                    rr.put("tarih",cursor.getString(1));
                    HashMap<String, String> ekipl=getOneRow(new String[]{"AD"},"tarim_istakip_ekiplideri", "ID="+cursor.getString(2));
                    rr.put("ekip_lideri",ekipl.get("AD"));
                    rr.put("fisno",cursor.getString(3));
                    rr.put("ek1",cursor.getString(4));
                    rr.put("ek2",cursor.getString(5));
                    rr.put("ek3",cursor.getString(6));
                    rr.put("aktarildi",cursor.getString(7));
                    rr.put("globalid",cursor.getString(8));

                    Log.w("bekleyenGonderilenGetir", cursor.getString(8));

                    //Cursor cursor2 = db.rawQuery("select tc from tarim_istakip_calisma_personel where ticid="+cursor.getString(0)+" and fisno='"+cursor.getString(3)+"' and user_id="+MainActivity.userid,null);
                    Cursor cursor2 = db.rawQuery("select tc from tarim_istakip_calisma_personel where globalid='"+cursor.getString(8)+"' and user_id="+MainActivity.userid,null);
                    rr.put("toplam",""+(cursor2!=null?cursor2.getCount():0));
                    cursor2.close();
                    res.add(rr);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("bekleyenGonderilenGetir", ex.getMessage());
        }finally {
            //db.close();
        }

        return res;
    }

    public ArrayList<HashMap<String, String>> bekleyenGonderilenPersonelGetir(String userid, String bekleyen){

        String selectQuery = "SELECT ID, TC, AD, SOYAD, KARTNO FROM tarim_istakip_personel WHERE user_id='"+userid+"' AND YENI_KAYIT=1 AND AKTARILDI "+bekleyen;
        ArrayList<HashMap<String, String>> res= new ArrayList<HashMap<String, String>>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null  && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> rr=new HashMap<String, String>();
                    rr.put("id",cursor.getString(0));
                    rr.put("tc",cursor.getString(1));
                    rr.put("ad",cursor.getString(2)+" "+cursor.getString(3));
                    rr.put("kartno",cursor.getString(4));
                    rr.put("islem","Yeni Kayıt");
                    res.add(rr);
                } while (cursor.moveToNext());
                cursor.close();
            }
            //Kartno Güncellendi
            selectQuery = "SELECT ID, TC, AD, SOYAD, KARTNO FROM tarim_istakip_personel WHERE user_id='"+userid+"' AND KARTNO_GUNCELLENDI=1 AND KARTNO_GUNCEL_AKTARILDI "+bekleyen;
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null  && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> rr=new HashMap<String, String>();
                    rr.put("id",cursor.getString(0));
                    rr.put("tc",cursor.getString(1));
                    rr.put("ad",cursor.getString(2)+" "+cursor.getString(3));
                    rr.put("kartno",cursor.getString(4));
                    rr.put("islem","Kart No Güncelleme");
                    res.add(rr);
                } while (cursor.moveToNext());
                cursor.close();
            }

            //Personel Foto ekleme
            selectQuery = "SELECT ID, TC, AD, SOYAD, KARTNO FROM tarim_istakip_personel WHERE user_id='"+userid+"' AND YENI_RESIM=1 AND YENI_RESIM_AKTARILDI "+bekleyen;
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null  && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> rr=new HashMap<String, String>();
                    rr.put("id",cursor.getString(0));
                    rr.put("tc",cursor.getString(1));
                    rr.put("ad",cursor.getString(2)+" "+cursor.getString(3));
                    rr.put("kartno",cursor.getString(4));
                    rr.put("islem","Resim Ekleme");
                    res.add(rr);
                } while (cursor.moveToNext());
                cursor.close();
            }

            //Personel Nüfus Foto ekleme
            selectQuery = "SELECT distinct p.id, p.TC, p.AD, p.SOYAD, p.KARTNO FROM tarim_istakip_personel p, tarim_istakip_personel_belge pb WHERE p.id=pb.personelid and pb.user_id='"+userid+"' and pb.tur='19' AND pb.AKTARILDI "+bekleyen;
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null  && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> rr=new HashMap<String, String>();
                    rr.put("id",cursor.getString(0));
                    rr.put("tc",cursor.getString(1));
                    rr.put("ad",cursor.getString(2)+" "+cursor.getString(3));
                    rr.put("kartno",cursor.getString(4));
                    rr.put("islem","Nüfus Ekleme");
                    res.add(rr);
                } while (cursor.moveToNext());
                cursor.close();
            }

        }catch (Exception ex){
            Log.w("bekleyenGondPeGetir", ex.getMessage());
        }finally {
            //db.close();
        }

        return res;
    }



    public HashMap<String, String> getOneRow(String[] cols, String table, String where){
        HashMap<String, String> res=new HashMap<String, String>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String colss="";
            for (int i=0; i<cols.length; i++){
                colss+=cols[i]+",";
            }

            String selectQuery = "SELECT "+colss.substring(0, colss.length()-1)+" FROM "+table+(where!=""?" WHERE "+where:"");
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                for (int i=0; i<cursor.getColumnCount(); i++){
                    res.put(cols[i], cursor.getString(i));
                 }
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("getOneRow", ex.getMessage());
        }finally {
            ////db.close();
        }

        return res;
    }

   public ArrayList<HashMap<String, String>> getMultiResult(String[] cols, String table, String where){
        ArrayList<HashMap<String, String>> res=new ArrayList<HashMap<String, String>>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String colss="";
            for (int i=0; i<cols.length; i++){
                colss+=cols[i]+",";
            }

            String selectQuery = "SELECT "+colss.substring(0, colss.length()-1)+" FROM "+table+" WHERE "+where;
            //Log.w("getMultiResult", selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                do{
                    HashMap<String, String> r=new HashMap<String,String>();
                    for (int i=0; i<cursor.getColumnCount(); i++){
                        r.put(cols[i], cursor.getString(i));
                    }
                    res.add(r);
                } while (cursor.moveToNext());
                cursor.close();
                //Log.w("getMultiResult", ""+res.size());
            }
        }catch (Exception ex){
            Log.w("getMultiResult", ex.getMessage());
        }finally {
            ////db.close();
        }

        return res;
    }

    public void personelFotoUpdate(List<String> ids, String which){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String idsc= TextUtils.join(",", ids);
            db.execSQL(String.format("update tarim_istakip_personel set "+which+"=1 where ID IN (%s);", idsc));
            /*ContentValues values = new ContentValues();
            values.put("RESIM_INDIRILDI", "1");
            for (int i=0; i<ids.size(); i++)
                db.update("tarim_istakip_personel", values, "id='"+ids.get(i)+"'", null);*/
        }catch (Exception ex){
            Log.w("personelFotoUpdate", ex.getMessage());
        }finally {
            ////db.close();
        }
    }

    public void personelFotoEkle(String personelid, String resimadi){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            HashMap<String, String>res=getOneRow(new String[]{"ID"}, "tarim_istakip_personel", "YENI_KAYIT=1 AND AKTARILDI=0 AND ID="+personelid);
            if(res.size()==0) {
                db.execSQL(String.format("update tarim_istakip_personel set YENI_RESIM=1,YENI_RESIM_AKTARILDI=0, RESIM='%s', RESIM_INDIRILDI=1  where ID=%s;", resimadi, personelid));
            }else{
                db.execSQL(String.format("update tarim_istakip_personel set RESIM='%s', RESIM_INDIRILDI=1  where ID=%s;", resimadi, personelid));
            }
        }catch (Exception ex){
            Log.w("personelFotoEkle", ex.getMessage());
        }finally {
        }
    }


    public void personelKartnoIptal(String kartno, String sicilno, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Log.w("personelKartnoIptal", String.format("insert into kartno_iptal(kartno, personel_id, user_id, aktarildi) values ('%s','%s','%s','0')", kartno, sicilno, userid));
            db.execSQL(String.format("insert into kartno_iptal(kartno, personelid, user_id, aktarildi) values ('%s','%s','%s','0')", kartno, sicilno, userid));
        }catch (Exception ex){
            Log.w("personelKartnoIptal", ex.getMessage());
        }finally {
            //db.close();
        }
    }

    public void personelKartnoIptalAktarildi(String kartno, String sicilno, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Log.w("personelKIptalAktarildi",String.format("update kartno_iptal set KARTNO_GUNCEL_AKTARILDI=1 where kartno='%s' and ID ='%s' and user_id='%s';", kartno, sicilno, userid));
            db.execSQL(String.format("update kartno_iptal set AKTARILDI=1 where kartno='%s' and personelid ='%s' and user_id='%s';", kartno, sicilno, userid));
        }catch (Exception ex){
            Log.w("personelKIptalAktarildi", ex.getMessage());
        }finally {
            //db.close();
        }
    }


    /*public boolean personelKartnoUpdate(String kartno, String sicilno, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Log.w("personelFotoUpdate", String.format("update tarim_istakip_personel set KARTNO='%s', KARTNO_GUNCELLENDI=1, KARTNO_GUNCEL_AKTARILDI=0 where ID ='%s' and user_id='%s';", kartno, sicilno, userid));
            db.execSQL(String.format("update tarim_istakip_personel set KARTNO='%s', KARTNO_GUNCELLENDI=1, KARTNO_GUNCEL_AKTARILDI=0 where ID ='%s' and user_id='%s';", kartno, sicilno, userid));
        }catch (Exception ex){
            Log.w("personelFotoUpdate", ex.getMessage());
        }finally {
            //db.close();
        }
        boolean stat=false;

        try {
            List<String[]> req = new ArrayList<String[]>();
            req.add(new String[]{"op", "pushdatake"});
            req.add(new String[]{"kartno", kartno});
            req.add(new String[]{"sicilno", sicilno});
            req.add(new String[]{"islem", "esleme"});
            req.add(new String[]{"prgver", MainActivity.PROGRAM_VERSION});

            List<String[]> resp = new ArrayList<String[]>();
            resp.add(new String[]{"stat", "stat"});

            List<HashMap<String, String>>  kartnoislem = new ServerData(cntxt).getDataFromServer(MainActivity.userid, "PerKartUpdate", req, resp);
            if(kartnoislem!=null && kartnoislem.size()>0){
                stat=true;
            }
        }catch (Exception ex){
            Log.w("PerKartUpdate", ex.getStackTrace().toString());
        }

        return stat;
    }*/

    public void puantajBilgileriAktarildi(String table, String id, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

            db.execSQL(String.format("update "+table+" set AKTARILDI=1, AKTARILDI_ONAY=1, AKTARILMA_TARIHI='"+timeStamp+"' where OID ='%s' and user_id='%s';", id, userid));
        }catch (Exception ex){
            Log.w("puantajktarildi", ex.getMessage());
        }finally {
            //db.close();
        }
    }

    public void personelKartnoUpdateAktarildi(String kartno, String sicilno, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Log.w("asdad",String.format("update tarim_istakip_personel set KARTNO_GUNCEL_AKTARILDI=1 where kartno='%s' and ID ='%s' and user_id='%s';", kartno, sicilno, userid));
            db.execSQL(String.format("update tarim_istakip_personel set KARTNO_GUNCEL_AKTARILDI=1 where kartno='%s' and ID ='%s' and user_id='%s';", kartno, sicilno, userid));
        }catch (Exception ex){
            Log.w("personelFotoUpdate", ex.getMessage());
        }finally {
            //db.close();
        }
    }

    public void personelFotoEkleAktarildi(String sicilno, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            //Log.w("asdad",String.format("update tarim_istakip_personel set KARTNO_GUNCEL_AKTARILDI=1,AKTARILDI=1 where ID ='%s' and user_id='%s';", sicilno, userid));
            db.execSQL(String.format("update tarim_istakip_personel set YENI_RESIM_AKTARILDI=1, YENI_RESIM=1 where ID ='%s' and user_id='%s';",sicilno, userid));
        }catch (Exception ex){
            Log.w("personelFotoAktar", ex.getMessage());
        }finally {
            //db.close();
        }
    }

    public void personelYeniAktarildi(String sicilno, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            //Log.w("asdad",String.format("update tarim_istakip_personel set KARTNO_GUNCEL_AKTARILDI=1,AKTARILDI=1 where ID ='%s' and user_id='%s';", sicilno, userid));
            db.execSQL(String.format("update tarim_istakip_personel set KARTNO_GUNCEL_AKTARILDI=1,AKTARILDI=1 where ID ='%s' and user_id='%s';",sicilno, userid));
        }catch (Exception ex){
            Log.w("personelYeniAktarildi", ex.getMessage());
        }finally {
            //db.close();
        }
    }

    public void personelYeniAktarildi(String sicilno, String tc, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            //Log.w("asdad",String.format("update tarim_istakip_personel set KARTNO_GUNCEL_AKTARILDI=1,AKTARILDI=1 where ID ='%s' and user_id='%s';", sicilno, userid));
            db.execSQL(String.format("update tarim_istakip_personel set id='%s', KARTNO_GUNCEL_AKTARILDI=1,AKTARILDI=1 where tc ='%s' and user_id='%s';",sicilno, tc, userid));
            db.execSQL(String.format("update tarim_istakip_personel_belge set PERSONELID='%s' where PERSONELID ='%s' and user_id='%s';", sicilno, tc, userid));
        }catch (Exception ex){
            Log.w("personelYeniAktarildi", ex.getMessage());
        }finally {
            //db.close();
        }
    }

    public void personelBelgeAktarildi(String id, String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            //Log.w("asdad",String.format("update tarim_istakip_personel set KARTNO_GUNCEL_AKTARILDI=1,AKTARILDI=1 where ID ='%s' and user_id='%s';", sicilno, userid));
            db.execSQL(String.format("update tarim_istakip_personel_belge set AKTARILDI=1 where OID ='%s' and user_id='%s';",id, userid));
        }catch (Exception ex){
            Log.w("personelBelgeAktarildi", ex.getMessage());
        }finally {
            //db.close();
        }
    }


    public HashMap<String,String> isbitirmeBilgiGetir(String barkod)
    {
        HashMap<String,String> res=new HashMap<String,String>();

        if(barkod.indexOf('-')>0) {
            SQLiteDatabase db = this.getReadableDatabase();
            try {
                String[] str = barkod.split("-");
                String id = str[0];
                String ad = str[1];
                String soyad = str[2];

                Cursor cursor = db.rawQuery("select personel_id,ISE_BASLAMA_TARIHI, bolge, calisma, firma, ekip_lideri, gorev," +
                        "urun, yetkili, fisno from tarim_istakip_personel_calisma where personel_id='"+id+"' and BASBITISONAY=1 and AKTARILDI=0 order by OID desc",null);
                if (cursor != null && cursor.getCount()>0) {
                    cursor.moveToFirst();
                    //Log.w("isbitBilgiGetir", "here2 "+ cursor.getString(7));

                    res.put("personel", barkod.replace('-', ' '));
                    String tarih=cursor.getString(1);
                    String[] ntarih=tarih.split("-");
                    res.put("isebaslamatarihi", ntarih[2]+" / "+ntarih[1]+" / "+ntarih[0]);
                    res.put("bolge", cursor.getString(2));
                    res.put("calisma", cursor.getString(3));
                    HashMap<String, String> r=getOneRow(new String[]{"firma"}, "firmalar","ID='"+cursor.getString(4)+"'");
                    res.put("firma", r.get("firma"));
                    r=getOneRow(new String[]{"ad"}, "tarim_istakip_ekiplideri","ID='"+cursor.getString(5)+"'");
                    res.put("ekiplideri", r.get("ad"));
                    r=getOneRow(new String[]{"gorev"}, "gorev","ID='"+cursor.getString(6)+"'");
                    res.put("gorev", r.get("gorev"));
                    r=getOneRow(new String[]{"ad", "urun"}, "muhendis_urun","OID='"+cursor.getString(7)+"'");
                    res.put("urun", r.get("ad")+"-"+r.get("urun"));
                    r=getOneRow(new String[]{"yetkili"}, "firmalar_yetkili_kisi","ID='"+cursor.getString(8)+"'");
                    res.put("yetkili", r.get("yetkili"));
                    res.put("fisno", cursor.getString(9));
                   // Log.w("isbitBilgiGetir", "here3");
                }
            } catch (Exception ex) {
                Log.w("isbitBilgiGetir", ex.getMessage());
            }finally {
                //db.close();
            }        }

        return res;
    }

    public HashMap<String, String> getDBStatus(String which, String userid)
    {
        HashMap<String, String> res=new HashMap<String, String>();

        SQLiteDatabase db = this.getReadableDatabase();
        try{
            String selectQuery = "SELECT tablename, alinma_tarihi FROM dbstatus where WHICH='"+which+"' and USER_ID='"+userid+"'";

            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if(cursor.getCount() > 0){
                do{
                    res.put(cursor.getString(0),cursor.getString(1));
                    Log.w("dbstat", cursor.getString(0));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception ex){
            Log.w("getDBStatus", ex.getMessage());
        }finally {
            //db.close();
        }
        return res;
    }

    public void dbstatDegerEkle(String table, String which, String userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if(!db.isOpen()){
                db = this.getWritableDatabase();
            }
            Cursor cursor = db.rawQuery("select oid, tablename from dbstatus where tablename='"+table+"' and WHICH='"+which+"' and user_id='"+userid+"'", null);

            String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            if(cursor!=null && cursor.getCount()>0){
                cursor.moveToFirst();
                Log.w("dbstat u"," "+table+" "+which+" "+cursor.getInt(0)+" "+cursor.getString(1));
                db.execSQL("update dbstatus set alinma_tarihi='"+timeStamp+"' where tablename='"+table+"' and which='"+which+"' and user_id='"+userid+"'");
                cursor.close();
            }else{
                Log.w("dbstat i"," "+table+" "+which);
                db.execSQL("insert into dbstatus (tablename, alinma_tarihi, which,user_id) values ('"+table+"','"+timeStamp+"','"+which+"','"+userid+"')");
            }
        }catch (Exception ex){
            Log.w("dbstatDegerEkle", ex.getMessage());
        }finally{
            //db.close();
        }
    }

    public String personelMax(){
        String sicilno="";

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String selectQuery = "SELECT max(id) FROM tarim_istakip_personel";
            Log.w("getOneRow",selectQuery);
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                sicilno = cursor.getString(0);
            }
        }catch (Exception ex){
            Log.w("getOneRow", ex.getMessage());
        }finally {
            //db.close();
        }

        return sicilno;
    }

    public boolean personelEkle(HashMap<String, String> tmp){
        boolean status=false;

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("ID", tmp.get("id"));
            values.put("TC", tmp.get("tc"));
            values.put("AD", tmp.get("ad"));
            values.put("SOYAD", tmp.get("soyad"));
            String[] ntarih=tmp.get("dogumtarihi").split("/");
            values.put("DOGUMTARIHI", ntarih[2].trim()+"/"+ntarih[1].trim()+"/"+ntarih[0].trim());
            values.put("CINSIYET", tmp.get("cinsiyet"));
            values.put("BABAADI", tmp.get("babaadi"));
            values.put("GSM", tmp.get("gsm"));
            values.put("OZELDURUM", tmp.get("ozeldurum"));
            values.put("GOREV", tmp.get("gorev"));
            values.put("BOLGE", tmp.get("bolge"));
            values.put("BOLGE2", tmp.get("bolge2"));
            values.put("BOLGE3", tmp.get("bolge3"));
            values.put("BOLGE4", tmp.get("bolge4"));
            values.put("BOLGE5", tmp.get("bolge5"));
            values.put("EKIP_LIDERI", tmp.get("ekip_lideri"));
            values.put("EKIP_LIDERI2", tmp.get("ekip_lideri2"));
            values.put("EKIP_LIDERI3", tmp.get("ekip_lideri3"));
            values.put("USER_ID", tmp.get("user_id"));
            values.put("KARTNO", tmp.get("kartno"));
            values.put("KARTNO_GUNCELLENDI", 0);
            values.put("KARTNO_GUNCEL_AKTARILDI", 0);
            values.put("RESIM", tmp.get("resim"));
            values.put("RESIM_INDIRILDI", tmp.get("resim_indirildi"));
            values.put("YENI_RESIM", 0);
            values.put("YENI_RESIM_AKTARILDI", 0);
            values.put("YENI_KAYIT", tmp.get("yeni_kayit"));
            values.put("SSK", tmp.get("ssk"));
            values.put("AKTARILDI", 0);
            values.put("ONAY", tmp.get("onay"));

            db.insert("tarim_istakip_personel", null, values);
            status=true;
        }catch (Exception ex){
            Log.w("personelEkle", ex.getMessage());
            status=false;
        }finally {
           //db.close();
        }
        return status;
    }

    public int personelSayisi(){
        int version = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            String selectQuery = "SELECT count(id) FROM tarim_istakip_personel";

            Cursor cursor = db.rawQuery(selectQuery, null);
            // Move to first row
            cursor.moveToFirst();
            if(cursor.getCount() > 0){
                version=cursor.getInt(0);
            }
            cursor.close();
        }catch (Exception ex){
            Log.w("personelSayisi", ex.getMessage());
        }finally {
            //db.close();
        }
        return version;
    }

    public int personelDeleteAll(){
        int version = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            db.delete("tarim_istakip_personel",null,null);
        }catch (Exception ex){
            Log.w("personelDeleteAll", ex.getMessage());
        }finally {
            //db.close();
        }
        return version;
    }

    public void personelQueueEmpty(){
        personelBilgi=null;
    }

    public void personelQueue(HashMap<String, String> tmp){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=null, cursor2=null;
        try {
            if(personelBilgi==null) {
                db.execSQL("update tarim_istakip_personel set guncelkontrol=-1");
                db.execSQL("update tarim_istakip_personel set guncelkontrol=1 where YENI_KAYIT=1 and AKTARILDI=0");
                db.execSQL("update tarim_istakip_personel set guncelkontrol=1 where YENI_RESIM=1 and YENI_RESIM_AKTARILDI=0");
                db.execSQL("update tarim_istakip_personel set guncelkontrol=1 where KARTNO_GUNCELLENDI=1 and KARTNO_GUNCEL_AKTARILDI=0");

                personelBilgi = new ArrayList<String>();
        }

            cursor =db.rawQuery("select ID, guncelkontrol from tarim_istakip_personel where ID='"+tmp.get("id")+"'", null);
            if(cursor.getCount()==0) {
                Log.w("perQ",tmp.get("ad")+" "+tmp.get("soyad"));
                personelBilgi.add("('" + tmp.get("id") + "','" + tmp.get("tc") + "','" + tmp.get("ad") + "','" + tmp.get("soyad") + "','" + tmp.get("dogumtarihi") + "','" + tmp.get("cinsiyet") + "','" + tmp.get("gorev") + "','" + tmp.get("bolge") + "','" + tmp.get("bolge2") + "','" + tmp.get("bolge3") + "','" + tmp.get("bolge4") + "','" + tmp.get("bolge5") + "','" + tmp.get("ekip_lideri") + "','" + tmp.get("ekip_lideri2") + "','" + tmp.get("ekip_lideri3") + "','" + tmp.get("user_id") + "','"+tmp.get("kartno")+"','" + tmp.get("resim") + "','0','" + tmp.get("onay") + "','0','0','" + tmp.get("ssk") + "','"+tmp.get("sgk_evrak")+"','0','1')");
            }else{
                cursor.moveToFirst();
                if(cursor.getInt(1)==-1) {//personel değiştirilebilir
                    //personel bilgisi değişmişse
                    cursor2 = db.rawQuery("select id from tarim_istakip_personel where id='" + tmp.get("id") + "' and kartno='" + tmp.get("kartno") + "' and resim='" + tmp.get("resim") + "' and ad='" + tmp.get("ad") + "' and soyad='" + tmp.get("soyad") + "' and tc='" + tmp.get("tc") + "'", null);
                    if (cursor2.getCount() == 0) {
                        db.execSQL("delete from tarim_istakip_personel where id='" + tmp.get("id") + "'");
                        personelBilgi.add("('" + tmp.get("id") + "','" + tmp.get("tc") + "','" + tmp.get("ad") + "','" + tmp.get("soyad") + "','" + tmp.get("dogumtarihi") + "','" + tmp.get("cinsiyet") + "','" + tmp.get("gorev") + "','" + tmp.get("bolge") + "','" + tmp.get("bolge2") + "','" + tmp.get("bolge3") + "','" + tmp.get("bolge4") + "','" + tmp.get("bolge5") + "','" + tmp.get("ekip_lideri") + "','" + tmp.get("ekip_lideri2") + "','" + tmp.get("ekip_lideri3") + "','" + tmp.get("user_id") + "','" + tmp.get("kartno") + "','" + tmp.get("resim") + "','0','" + tmp.get("onay") + "','0','0','" + tmp.get("ssk") + "','" + tmp.get("sgk_evrak") + "','0', '1')");
                    } else {
                        db.execSQL("update tarim_istakip_personel set guncelkontrol='1' where id='" + tmp.get("id") + "'");
                    }
                }else{
                    //mobilde personel bilgisin değiştirilmiş gönderilmesi gerek.
                }
            }
        }catch (Exception ex){
            Log.w("personelQueue", ex.getMessage());
        }finally {
            if (cursor!=null) cursor.close();
            if (cursor2!=null) cursor2.close();
            cursor=null;
            cursor2=null;
            //db.close();
        }
    }

    public void personelStore(){
        SQLiteDatabase db = this.getWritableDatabase();
        int maxSize=450;
        try {
            if(personelBilgi!=null && personelBilgi.size()>0) {
                Log.w("personelStore","here - "+personelBilgi.size()+" - "+(personelBilgi.size()/maxSize));
                for (int i=0; i<(int)(personelBilgi.size()/maxSize)+1; i++) {
                    int max=(i+1)*maxSize;
                    if(i==(int)(personelBilgi.size()/maxSize))
                        max=personelBilgi.size();

                    Log.w("personelStore","here - "+i*maxSize+" - "+max);
                    List<String> tmp = personelBilgi.subList(i*maxSize, max);
                    String lines = TextUtils.join(",", tmp);
                    String cols = "ID, TC, AD, SOYAD, DOGUMTARIHI, CINSIYET, GOREV, BOLGE, BOLGE2, BOLGE3, BOLGE4, BOLGE5, EKIP_LIDERI, EKIP_LIDERI2, EKIP_LIDERI3, USER_ID, KARTNO, RESIM, RESIM_INDIRILDI, ONAY, AKTARILDI, YENI_KAYIT, SSK, SGK_EVRAK, SGK_EVRAK_INDIRILDI, GUNCELKONTROL";
                    Log.w("personelStore", String.format("insert into tarim_istakip_personel (%s) values %s", cols, lines));

                    db.execSQL(String.format("insert into tarim_istakip_personel (%s) values %s", cols, lines));
                }
            }else{
                throw new Exception("No value");
            }
        }catch (Exception ex){
            Log.w("personelStore", ex.getMessage());
        }finally {
            //db.close();
        }
        //Sunucuda yok(iş çıkışı yapılmış) olanlar siliniyor.
        db.execSQL("delete from tarim_istakip_personel where guncelkontrol='-1'");
        int aaa=0;
        Cursor cursor = db.rawQuery("select id from tarim_istakip_personel where guncelkontrol='-1'", null);
        if (cursor != null && cursor.getCount() > 0) {
            aaa=cursor.getCount();
            cursor.moveToFirst();
            do {
                Log.w("Delpers","Sicil No:"+cursor.getString(0));
            } while (cursor.moveToNext());

        }

        Log.w("PERSONEL", "Total="+personelSayisi()+" - "+ aaa);
    }


    public void personelVersionEkle(int version){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("VERSION", version);

            db.insert("tarim_istakip_personel_version", null, values);
        }catch (Exception ex){
            Log.w("personelEkle", ex.getMessage());
        }finally {
            //db.close();
        }
    }

    public boolean personelBelgeEkle(HashMap<String, String> tmp){
        boolean status=false;

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Log.w("personelbelge","insert into tarim_istakip_personel_belge (PERSONELID, TUR, RESIMADI, AKTARILDI, USER_ID) values ('"+tmp.get("sicilno")+"','"+ tmp.get("belgetur")+"','"+tmp.get("ad")+"',0,'"+tmp.get("user_id")+"')");
            db.execSQL("insert into tarim_istakip_personel_belge (PERSONELID, TUR, RESIMADI, AKTARILDI, USER_ID) values ('"+tmp.get("sicilno")+"','"+ tmp.get("belgetur")+"','"+tmp.get("ad")+"',0,'"+tmp.get("user_id")+"')");
            status=true;
        }catch (Exception ex){
            Log.w("personelBelgeEkle", ex.getMessage());
            status=false;
        }finally {
            //db.close();
        }
        return status;
    }


    public boolean barkodDogrula(String barkod, String userid){
        boolean stat=false;

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String selectQuery = "SELECT ID, TC, AD, SOYAD, DOGUMTARIHI, CINSIYET, GOREV, BOLGE, BOLGE2, BOLGE3, BOLGE4, BOLGE5," +
                    " EKIP_LIDERI, EKIP_LIDERI2, EKIP_LIDERI3" +
                    " FROM tarim_istakip_personel WHERE KARTNO='" + barkod + "' and user_id='"+userid+"'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                stat = true;
                cursor.close();
            }
        } catch (Exception ex) {
            Log.w("barkodDogrula", ex.getMessage());
        } finally {
            //db.close();
        }
    return stat;
    }

    public HashMap<String,String> personelBilgileriGetir(String barkod){
        HashMap<String,String> res=new HashMap<String,String>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            /*String[] str = barkod.split("-");
            String id = str[0];*/

            String selectQuery = "SELECT ID, TC, AD, SOYAD, DOGUMTARIHI, CINSIYET, GOREV, BOLGE, BOLGE2, BOLGE3, BOLGE4, BOLGE5," +
                    " EKIP_LIDERI, EKIP_LIDERI2, EKIP_LIDERI3,RESIM,RESIM_INDIRILDI,KARTNO,SSK" +
                    " FROM tarim_istakip_personel WHERE KARTNO='"+barkod+"'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    res.put("ID", ""+cursor.getString(0));
                    res.put("TC", cursor.getString(1));
                    res.put("AD", cursor.getString(2));
                    res.put("SOYAD", cursor.getString(3));
                    res.put("DOGUMTARIHI", cursor.getString(4));
                    res.put("CINSIYET", cursor.getString(5));
                    res.put("GOREV", cursor.getString(6));
                    res.put("BOLGE", cursor.getString(7));
                    res.put("BOLGE2", cursor.getString(8));
                    res.put("BOLGE3", cursor.getString(9));
                    res.put("BOLGE4", cursor.getString(10));
                    res.put("BOLGE5", cursor.getString(11));

                    HashMap<String,String> ekipl = this.getOneRow(new String[]{"AD"},"tarim_istakip_ekiplideri","ID="+cursor.getString(12));

                    res.put("EKIP_LIDERI", ""+ekipl.get("AD"));
                    res.put("EKIP_LIDERI2", cursor.getString(13));
                    res.put("EKIP_LIDERI3", cursor.getString(14));
                    res.put("RESIM", cursor.getString(15));
                    res.put("RESIM_INDIRILDI", cursor.getString(16));
                    res.put("KARTNO", cursor.getString(17));
                    res.put("SSK", cursor.getString(18));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("personelBilgileriGetir",ex.getMessage());
        }finally {
            //db.close();
        }
        return res;
    }
    public HashMap<String,String> personelBilgileriGetir(String kartno, String sicilno, String tckimlik, String ekiplideri){
        return personelBilgileriGetir(kartno, sicilno, tckimlik, ekiplideri,"","");
    }

    public HashMap<String,String> personelBilgileriGetir(String kartno, String sicilno, String tckimlik, String ekiplideri, String ad, String soyad){
        HashMap<String,String> res=new HashMap<String,String>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String wh="";
            if(kartno.length()!=0 && !kartno.equals("000000")){
                wh="Kartno='"+kartno+"'";
            }
            if(sicilno.length()!=0){
                wh+=(!wh.equals("")?" AND ":"")+"ID='"+sicilno+"'";
            }

            if(tckimlik.length()!=0){
                wh+=(!wh.equals("")?" AND ":"") +"TC='"+tckimlik+"'";
            }

            if(ekiplideri.length()!=0){
                wh+=(!wh.equals("")?" AND ":"") +"EKIP_LIDERI='"+ekiplideri+"'";
            }

            if(ad.length()!=0){
                wh+=(!wh.equals("")?" AND ":"")+"UPPER(Replace(Replace(Replace(Replace(Replace(Replace(Replace(Replace(AD, 'ü', 'Ü'), 'i', 'İ'), 'ğ', 'Ğ'), 'ı', 'I'), 'ö', 'Ö'), 'ç', 'Ç'), 'ş', 'Ş'), 'ü', 'Ü')) like '"+ad.toUpperCase()+"%'";
            }

            if(soyad.length()!=0){
                wh+=(!wh.equals("")?" AND ":"") +"UPPER(Replace(Replace(Replace(Replace(Replace(Replace(Replace(Replace(SOYAD, 'ü', 'Ü'), 'i', 'İ'), 'ğ', 'Ğ'), 'ı', 'I'), 'ö', 'Ö'), 'ç', 'Ç'), 'ş', 'Ş'), 'ü', 'Ü')) like '"+soyad.toUpperCase()+"%'";
            }


            Log.w("persorgu",sicilno+" "+tckimlik+" "+wh);
            String selectQuery = "SELECT ID, TC, AD, SOYAD, DOGUMTARIHI, CINSIYET, GOREV, BOLGE, BOLGE2, BOLGE3, BOLGE4, BOLGE5," +
                    " EKIP_LIDERI, EKIP_LIDERI2, EKIP_LIDERI3, RESIM, RESIM_INDIRILDI,KARTNO, SGK_EVRAK, SGK_EVRAK_INDIRILDI, SSK" +
                    " FROM tarim_istakip_personel WHERE "+wh;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount()>0) {
                cursor.moveToFirst();
                do {
                    res.put("ID", ""+cursor.getString(0));
                    res.put("TC", cursor.getString(1));
                    res.put("AD", cursor.getString(2));
                    res.put("SOYAD", cursor.getString(3));
                    res.put("DOGUMTARIHI", cursor.getString(4));
                    res.put("CINSIYET", cursor.getString(5));
                    res.put("GOREV", cursor.getString(6));
                    res.put("BOLGE", cursor.getString(7));
                    res.put("BOLGE2", cursor.getString(8));
                    res.put("BOLGE3", cursor.getString(9));
                    res.put("BOLGE4", cursor.getString(10));
                    res.put("BOLGE5", cursor.getString(11));
                    res.put("RESIM", cursor.getString(15));
                    res.put("RESIM_INDIRILDI", cursor.getString(16));
                    res.put("KARTNO", cursor.getString(17));
                    res.put("SGK_EVRAK", cursor.getString(18));
                    res.put("SGK_EVRAK_INDIRILDI", cursor.getString(19));
                    res.put("SSK", cursor.getString(20));
                    String el="";
                    if(cursor.getString(12)!="0" && cursor.getString(12)!="-1"){
                        HashMap<String, String>  ekipl=this.getOneRow(new String[]{"AD"},"tarim_istakip_ekiplideri","ID='"+cursor.getString(12)+"'");
                        el=ekipl.get("AD");
                    }
                    res.put("EKIP_LIDERI", el);

                    el="";
                    if(cursor.getString(12)!="0" && cursor.getString(13)!="-1"){
                        HashMap<String, String>  ekipl=this.getOneRow(new String[]{"AD"},"tarim_istakip_ekiplideri","ID='"+cursor.getString(13)+"'");
                        el=ekipl.get("AD");
                    }
                    res.put("EKIP_LIDERI2", el);

                    el="";
                    if(cursor.getString(12)!="0" && cursor.getString(14)!="-1"){
                        HashMap<String, String>  ekipl=this.getOneRow(new String[]{"AD"},"tarim_istakip_ekiplideri","ID='"+cursor.getString(14)+"'");
                        el=ekipl.get("AD");
                    }
                    res.put("EKIP_LIDERI3", el);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception ex){
            Log.w("personelBilgileriGetir",ex.getMessage());
        }finally {
            //db.close();
        }
        return res;
    }

    public int getRowCount() {
        int rowCount=0;
        // Bu method bu uygulamada kullanılmıyor ama her zaman lazım olabilir.Tablodaki row sayısını geri döner.
        //Login uygulamasında kullanacağız
        /*String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        //db.close();
        cursor.close();*/
        // return row count
        return rowCount;
    }

    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", null, null);
        db.delete("user_fistanim", null, null);
        db.delete("sifremi_unuttum",null,null);
        db.delete("tarim_istakip_firma_bolge", null, null);
        db.delete("tarim_istakip_firma_bolge_lider", null, null);
        db.delete("firmalar", null, null);
        db.delete("firmalar_yetkili_kisi", null, null);
        db.delete("tarim_istakip_ekiplideri", null, null);
        db.delete("gorev", null, null);
        db.delete("muhendis_urun", null, null);
        db.delete("tarim_istakip_personel", null, null);
        db.delete("tarim_istakip_personel_belge", null, null);
        db.delete("tarim_istakip_belge", null, null);
        db.delete("dbstatus", null, null);
        db.delete("tarim_istakip_calisma", null, null);
        db.delete("tarim_istakip_calisma_personel", null, null);
        db.delete("tarim_istakip_calisma_servis", null, null);
        db.delete("servis", null, null);
        //db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        /*SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS tarim_istakip_personel");
        onCreate(db);*/
    }
}
