package com.guleryuz.puantajonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.guleryuz.puantajonline.CallBacks.ServiceCallBack;
import com.guleryuz.puantajonline.CallBacks.TaskCallback;
import com.guleryuz.puantajonline.OnlineService.DataFromService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Asersoft on 1.03.2017.
 */

public class GunlukPuantaj3 extends AppCompatActivity implements View.OnClickListener, ServiceCallBack {
    private LinearLayout ibasLayout;
    private Button ibasBtnSonraki,ibasBtnIptal,ibasBtnOnceki;
    static final int REQUEST_GP4 = 1;
    private static Context ParentCtxt;
    private HashMap<String, EditText> servisEdit;
    private RadioButton rd1, rdYok, rdKisiBasi;
    private String[] servis;
    private LinearLayout layouts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ise_baslama3);
        ParentCtxt = this;

        //gpd=GunlukPuantaj2.gpd;

        //db=new Database(getApplicationContext());
        ibasLayout=(LinearLayout)findViewById(R.id.ibasLayout);
        ibasBtnSonraki=(Button)findViewById(R.id.ibasBtnSonraki);
        ibasBtnSonraki.setOnClickListener(this);
        ibasBtnIptal=(Button)findViewById(R.id.ibasBtnIptal);
        ibasBtnIptal.setOnClickListener(this);


        servisEdit=new HashMap<String, EditText>();
        rd1=null;
        rdKisiBasi=null;

        try {
            DataFromService servis = new DataFromService(this);
            servis.context=ParentCtxt;
            servis.uid=MainActivity.userid;
            servis.reqtype="ServisGetir";
            servis.title="Servis Getir";
            servis.reqparam=new ArrayList<String[]>();
            servis.reqparam.add(new String[]{"op", "servis"});
            servis.reqparam.add(new String[]{"bolge", MainActivity.gpd.getCalismaalani()});
            servis.reqparam.add(new String[]{"firmaid", MainActivity.gpd.getFirma()});
            servis.reqparam.add(new String[]{"ekiplideri", MainActivity.gpd.getEkiplideri()});
            servis.resp=new ArrayList<String[]>();
            for (int i=1; i<21; i++) {
                servis.resp.add(new String[]{"servis"+i, "servis"+i});
            }
            servis.execute();
        }catch (Exception ex){
            Log.w("Servis", "nothing");
        }
        //------
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
         if(v.getId()==R.id.ibasBtnIptal){
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
        }else if(v.getId()==R.id.ibasBtnSonraki)
        {
            MainActivity.gpd.emptyServis();
            boolean stat=false;
            if(rd1!=null){
                if (rd1.isChecked()){
                    stat=true;
                    for (int i=0; i<servis.length-1; i++){
                        if (servis[i]!=null && !servis[i].equals("0")){
                            MainActivity.gpd.addServis(""+i, servisEdit.get(servis[i]).getText().toString());
                            Log.w("servisss",servis[i]+" "+servisEdit.get(servis[i]).getText().toString());
                        }else{
                            MainActivity.gpd.addServis(""+i, "0");
                        }
                    }
                }
            }

            if(!stat){
                if(rdYok.isChecked()) {
                    stat = true;
                    MainActivity.gpd.addServis("Yok", "Yok");
                }else if(rdKisiBasi!=null && rdKisiBasi.isChecked()){
                    stat=true;
                    MainActivity.gpd.addServis("KisiBasi","KisiBasi");
                }else{
                    new ShowToast(this, "Seçim Yapmalısınız");
                }
            }

            if (stat){
                MainActivity.sendPersonels2Server();
                Intent intent = new Intent(getApplicationContext(), GunlukPuantaj4.class);

                startActivityForResult(intent,REQUEST_GP4);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode==REQUEST_GP4 && resultCode==RESULT_OK){
            if(intent.hasExtra("exit")){
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void PipeAsyncFinish(boolean stat, String type, List<HashMap<String, String>> data, String error) {
        if (type.equals("ServisGetirOnline")){
            try {
                if (data != null && data.size() > 0) {
                    //servis = db.servisGetir(MainActivity.gpd.getCalismaalani(), MainActivity.gpd.getFirma(), MainActivity.gpd.getEkiplideri());
                    servis = new String[20];
                    for (int i = 0; i < 20; i++) {
                        servis[i] = data.get(0).get("servis" + (i + 1));
                    }
                    MainActivity.gpd.servisBilgileri = servis;

                    for (int i = 0; i < servis.length - 1; i++) {
                        layouts = (LinearLayout) this.getLayoutInflater().inflate(R.layout.grid_single_servis, null);
                        TextView txt = (TextView) layouts.findViewById(R.id.childItem);
                        if (servis[i] != null && !servis[i].equals("0")) {
                            txt.setText("Servis" + servis[i]);
                            EditText ed = (EditText) layouts.findViewById(R.id.servis);
                            ed.setInputType(InputType.TYPE_CLASS_NUMBER);
                            servisEdit.put(servis[i], ed);
                            if (rd1 == null) {
                                rd1 = (RadioButton) layouts.findViewById(R.id.radiobtn);
                                rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (rd1.isChecked()) {
                                            if (rdKisiBasi != null) rdKisiBasi.setChecked(false);
                                            rdYok.setChecked(false);
                                        }
                                    }
                                });
                                rd1.setVisibility(View.VISIBLE);
                            }

                            ibasLayout.addView(layouts);
                        }
                    }

                    if (MainActivity.gpd.getKayitdurumu().equals("guncelleme")) {
                        HashMap<String, String> srv = MainActivity.gpd.getServis();
                        for (int i = 0; i < servis.length - 1; i++) {
                            //if(srv.get(""+i)!=null && !srv.get(""+i).equals("0"))
                            //  Log.w("gp3servis",srv.get(""+i));
                            if (srv.containsKey("" + i))
                                servisEdit.get(servis[i]).setText(srv.get("" + i));
                        }
                    }

                    //Kişi başı servis ekleme
                    if (!servis[servis.length - 1].equals("0") && !servis[servis.length - 1].equals("")) {
                        layouts = (LinearLayout) this.getLayoutInflater().inflate(R.layout.grid_single_servis, null);
                        TextView txt2 = (TextView) layouts.findViewById(R.id.childItem);
                        txt2.setText("Servis " + servis[servis.length - 1] + "/kişi");
                        EditText ed2 = (EditText) layouts.findViewById(R.id.servis);
                        ed2.setVisibility(View.GONE);
                        servisEdit.put("KisiBasi", ed2);
                        rdKisiBasi = (RadioButton) layouts.findViewById(R.id.radiobtn);
                        rdKisiBasi.setVisibility(View.VISIBLE);
                        rdKisiBasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                 if (rdKisiBasi.isChecked()) {
                                    if (rd1 != null) rd1.setChecked(false);
                                    rdYok.setChecked(false);
                                }
                            }
                        });
                        ibasLayout.addView(layouts);
                    }

                    layouts = (LinearLayout) this.getLayoutInflater().inflate(R.layout.grid_single_servis, null);
                    TextView txt = (TextView) layouts.findViewById(R.id.childItem);
                    txt.setText("Servis Yok");
                    EditText ed = (EditText) layouts.findViewById(R.id.servis);
                    ed.setVisibility(View.GONE);
                    servisEdit.put("Yok", ed);
                    rdYok = (RadioButton) layouts.findViewById(R.id.radiobtn);
                    rdYok.setVisibility(View.VISIBLE);
                    rdYok.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (rdYok.isChecked()) {
                                if (rd1 != null) rd1.setChecked(false);
                                if (rdKisiBasi != null) rdKisiBasi.setChecked(false);
                            }
                        }
                    });
                    ibasLayout.addView(layouts);


                    if (MainActivity.gpd.getKayitdurumu().equals("guncelleme")) {
                        Log.w("Servis", "" + MainActivity.gpd.getServisSayisi());
                        if (MainActivity.gpd.getServisSayisi() == 0) {
                            if (rd1 != null) rd1.setChecked(false);
                            if (rdKisiBasi != null) rdKisiBasi.setChecked(false);
                            rdYok.setChecked(true);
                        } else if (MainActivity.gpd.getServisSayisi() == -1) {
                            if (rd1 != null) rd1.setChecked(false);
                            rdYok.setChecked(false);
                            rdKisiBasi.setChecked(true);
                        } else {
                            if (rd1 != null) rd1.setChecked(true);
                            if (rdKisiBasi != null) rdKisiBasi.setChecked(false);
                            rdYok.setChecked(false);
                        }
                    }
                }
            }catch (Exception ex){
                Log.w("ServisErr",ex.getMessage());
            }
        }
    }
}
