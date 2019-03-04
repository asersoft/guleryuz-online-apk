package guleryuz.puantajonline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;

import barcodescanner.app.com.barcodescanner.R;

/**
 * Created by Asersoft on 8.03.2017.
 */

public class PersonelKartEsle extends AppCompatActivity implements View.OnClickListener {
    private static TextView psad, psdogumtarihi;
    private EditText psKartno, psbarkod, pstc;
    private Button psbarkodyeni, psbarkodoku, pskartiptal, pskartesle;

    private Database db;
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
            db=new Database(getApplicationContext());
            HashMap<String, String> personelbilgileri = db.personelBilgileriGetir("", psbarkod.getText().toString(), pstc.getText().toString(), "");
            if(personelbilgileri.size()>0) {
                psbarkod.setText(personelbilgileri.get("ID"));
                pstc.setText(personelbilgileri.get("TC"));
                psad.setText(personelbilgileri.get("AD") + " " + personelbilgileri.get("SOYAD"));
                psdogumtarihi.setText(personelbilgileri.get("DOGUMTARIHI"));
                pskartesle.setEnabled(true);
            }
        }else if(v.getId()==R.id.pskartiptal){
            finish();
        }else if(v.getId()==R.id.pskartesle){
            db=new Database(getApplicationContext());
            db.personelKartnoUpdate(psKartno.getText().toString(), psbarkod.getText().toString(), userid);
            new ShowToast(this, "Eşleme yapılmıştır.");
            finish();
        }
    }
}
