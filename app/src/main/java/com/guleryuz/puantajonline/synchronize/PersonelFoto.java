package com.guleryuz.puantajonline.synchronize;

import android.content.Context;

import com.guleryuz.puantajonline.Database;

import java.io.File;


/**
 * Created by Asersoft on 27.02.2017.
 */

public class PersonelFoto {
    private File photoPath;
    private static Context context;
    private Database db;

   /* public PersonelFoto(Context cxt){
        context=cxt;
        db=new Database(context);
        photoPath = new File(Environment.getExternalStorageDirectory() + "/"+MainActivity.rootDir+"");

        if (!photoPath.exists()) {
            photoPath.mkdirs();
        }
    }

    public void startDownload()
    {
        try{
            ArrayList<HashMap<String,String>> res=db.getMultiResult(new String[]{"ID","RESIM"},"tarim_istakip_personel","RESIM <>'' AND RESIM<>'null' AND RESIM<>'NULL' AND RESIM_INDIRILDI=0");
            Log.w("sD","here - "+res.size());
            for (int i=0; i<res.size(); i++){
                Log.w("sD","here2 "+res.get(i).get("RESIM").toString()+" - "+res.get(i).get("ID").toString());
                String adi=res.get(i).get("RESIM").toString();

                downloadFile("http://www.guleryuzcv.net/images2/img/15/evrak/"+adi, adi);
            }
        }catch (Exception ex){
            Log.w("startDownload", ex.getMessage());
        }
    }

    public void startDownloadAsync()
    {
        try{
            ArrayList<HashMap<String,String>> res=db.getMultiResult(new String[]{"ID","RESIM"},"tarim_istakip_personel","RESIM <>'' AND RESIM<>'null' AND RESIM<>'NULL' AND RESIM_INDIRILDI=0");
            Log.w("sD","here - "+res.size());
            //PersonelFotoAsync pfa=new PersonelFotoAsync(context,res);
            //pfa.execute();

        }catch (Exception ex){
            Log.w("startDownload", ex.getMessage());
        }
    }

    public static void downloadFile(String uRl, String name)
    {
        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Resimler")
                .setDescription("Resim indirme")
                .setDestinationInExternalPublicDir("/"+MainActivity.rootDir+"", name);

        mgr.enqueue(request);
    }*/
}
