package com.guleryuz.puantajonline;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.guleryuz.puantajonline.synchronize.Ekiplideri;
import com.guleryuz.puantajonline.synchronize.Firma;
import com.guleryuz.puantajonline.synchronize.FirmaBolge;
import com.guleryuz.puantajonline.synchronize.Gorev;
import com.guleryuz.puantajonline.synchronize.Personel;
import com.guleryuz.puantajonline.synchronize.PersonelBelgeTur;
import com.guleryuz.puantajonline.synchronize.Servis;
import com.guleryuz.puantajonline.synchronize.Urun;
import com.guleryuz.puantajonline.synchronize.Yetkili;

import java.util.HashMap;

/**
 * Created by Asersoft on 9.12.2016.
 */

public class SyncData extends Service{
    private Database db;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.w("service","created");
        db=new Database(getApplicationContext());

        //Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_LONG).show();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.w("service","destroy");
        //db.close();
        //Toast.makeText(getApplicationContext(), "Service Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.w("service","runnig");
        //Toast.makeText(getApplicationContext(), "Service Running ", Toast.LENGTH_LONG).show();
        Connectivity conn=new Connectivity();
        try {
            if (conn.isConnected(getApplicationContext()) || conn.isConnectedMobile(getApplicationContext()) || conn.isConnectedWifi(getApplicationContext())) {
                HashMap<String, String> r=db.getOneRow(new String[]{"id"}, "users","OID>0");
                if(r.get("id")!=null) {
                    FirmaBolge firmabolge = new FirmaBolge();
                    firmabolge.db = db;
                    firmabolge.context = getApplicationContext();
                    firmabolge.uid =r.get("id").toString();
                    firmabolge.execute();

                    Firma firma = new Firma();
                    firma.context=getApplicationContext();
                    firma.db=db;
                    firma.uid =r.get("id").toString();
                    firma.execute();

                    Yetkili yetkili = new Yetkili();
                    yetkili.context=getApplicationContext();
                    yetkili.db=db;
                    yetkili.uid =r.get("id").toString();
                    yetkili.execute();

                    Ekiplideri ekiplideri=new Ekiplideri();
                    ekiplideri.context=getApplicationContext();
                    ekiplideri.db=db;
                    ekiplideri.uid =r.get("id").toString();
                    ekiplideri.execute();

                    Gorev gorev=new Gorev();
                    gorev.context=getApplicationContext();
                    gorev.db=db;
                    gorev.uid=r.get("id").toString();
                    gorev.execute();

                    Urun urun=new Urun();
                    urun.context=getApplicationContext();
                    urun.db=db;
                    urun.uid=r.get("id").toString();
                    urun.execute();

                    PersonelBelgeTur pbelgetur=new PersonelBelgeTur();
                    pbelgetur.context=getApplicationContext();
                    pbelgetur.db=db;
                    pbelgetur.uid=r.get("id").toString();
                    pbelgetur.execute();

                    Servis servis=new Servis();
                    servis.context=getApplicationContext();
                    servis.db=db;
                    servis.uid=r.get("id").toString();
                    servis.execute();

                    Personel personel=new Personel();
                    personel.context=getApplicationContext();
                    personel.db=db;
                    personel.uid=r.get("id").toString();
                    personel.execute();
                }
            } else {
                Log.w("SyncData", "No connection");
            }
        }catch (Exception ex){
            Log.w("SyncData", ex.getMessage());
        }

        return super.onStartCommand(intent, flags, startId);
    }

}