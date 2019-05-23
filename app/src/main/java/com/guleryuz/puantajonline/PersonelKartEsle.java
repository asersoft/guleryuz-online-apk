package com.guleryuz.puantajonline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.guleryuz.puantajonline.OnlineService.ServerData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Asersoft on 8.03.2017.
 */

public class PersonelKartEsle extends AppCompatActivity implements View.OnClickListener {
    private static TextView psad, psdogumtarihi;
    private EditText psKartno, psbarkod, pstc;
    private Button psbarkodyeni, psbarkodoku, pskartiptal, pskartesle;


    private static Context ParentCtxt;
    private static String userid, kartno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personel_sorgulama_kartno);
        ParentCtxt = this;

        Intent intent= getIntent();
        Bundle b= intent.getExtras();
        if (b!=null){
            userid=b.getString("userid");
            kartno=b.getString("kartno");
        }

        psad=(TextView)findViewById(R.id.psad);
        psdogumtarihi=(TextView)findViewById(R.id.psdogumtarihi);
        psKartno=(EditText)findViewById(R.id.psKartno);
        psKartno.setEnabled(false);
        psKartno.setText(kartno);
        psbarkod=(EditText)findViewById(R.id.psbarkod);
        pstc=(EditText)findViewById(R.id.pstc);

        psbarkodyeni=(Button)findViewById(R.id.psbarkodyeni);
        psbarkodyeni.setOnClickListener(this);
        psbarkodoku=(Button)findViewById(R.id.psbarkodoku);
        psbarkodoku.setOnClickListener(this);
        pskartiptal=(Button)findViewById(R.id.pskartiptal);
        pskartiptal.setOnClickListener(this);
        pskartesle=(Button)findViewById(R.id.pskartesle);
        pskartesle.setOnClickListener(this);
        pskartesle.setEnabled(false);

    }

    public void onClick(View v) {
        if (v.getId()==R.id.psbarkodyeni){
            psad.setText("");
            psdogumtarihi.setText("");
            pstc.setText("");
            psbarkod.setText("");
            pskartesle.setEnabled(false);
        }else if(v.getId()==R.id.psbarkodoku){
            //db=new Database(getApplicationContext());
            //HashMap<String, String> personelbilgileri = db.personelBilgileriGetir("", psbarkod.getText().toString(), pstc.getText().toString(), "");
            ServerData sd =new ServerData(ParentCtxt);
            List<HashMap<String, String>> personelbilgileri = sd.personelSorgula(userid,"",psbarkod.getText().toString(),"","",pstc.getText().toString());
            if(personelbilgileri!=null && personelbilgileri.size()>0) {
                psbarkod.setText(personelbilgileri.get(0).get("ID"));
                pstc.setText(personelbilgileri.get(0).get("TC"));
                psad.setText(personelbilgileri.get(0).get("AD") + " " + personelbilgileri.get(0).get("SOYAD"));
                psdogumtarihi.setText(personelbilgileri.get(0).get("DOGUMTARIHI"));
                pskartesle.setEnabled(true);
            }
        }else if(v.getId()==R.id.pskartiptal){
            finish();
        }else if(v.getId()==R.id.pskartesle){
            //db=new Database(getApplicationContext());
            //db.cntxt=ParentCtxt;
            //db.personelKartnoUpdate(psKartno.getText().toString(), psbarkod.getText().toString(), userid);
            ServerData sd =new ServerData(ParentCtxt);
            sd.personelKartnoUpdate(userid, psKartno.getText().toString(), psbarkod.getText().toString(), "esleme",MainActivity.PROGRAM_VERSION);
            new ShowToast(this, "Eşleme yapılmıştır.");
            finish();
        }
    }
}
