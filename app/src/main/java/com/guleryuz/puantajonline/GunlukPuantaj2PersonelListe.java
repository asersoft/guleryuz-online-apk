package guleryuz.puantajonline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import barcodescanner.app.com.barcodescanner.R;

/**
 * Created by Asersoft on 1.03.2017.
 */

public class GunlukPuantaj2PersonelListe extends AppCompatActivity implements View.OnClickListener   {
    private static Context ParentCtxt;
    private Database db;
    private LinearLayout ibasLayout;
    private TextView gorev;
    private Button btnKapat;
    private static String gorevId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ise_baslama_personelliste);
        ParentCtxt = this;
        db=new Database(getApplicationContext());

        Intent intent = getIntent();

        ibasLayout = (LinearLayout) findViewById(R.id.ibasLayout);
        gorev = (TextView) findViewById(R.id.gorev);
        btnKapat=(Button)findViewById(R.id.ibasBtnKapat);
        btnKapat.setOnClickListener(this);

            try {
                gorev.setText(intent.getStringExtra("gorev"));
                gorevId = intent.getStringExtra("gorevid");

                loadPersonel();

            }catch (Exception ex){
                Log.w("GP2PListe",ex.getMessage());
            }

    }

    private void loadPersonel(){
        ibasLayout.removeAllViews();
        LinearLayout layouts;
        List<GunlukPersonelData> gper = MainActivity.gpd.getPersonel(gorevId);

        if (gper != null) {
            //Log.w("here4",""+gper.size());
            for (int i = 0; i < gper.size(); i++) {
                layouts = (LinearLayout) this.getLayoutInflater().inflate(R.layout.grid_single_personel, null);
                TextView txt = (TextView) layouts.findViewById(R.id.sequence);
                txt.setText("" + (i + 1) + ".");
                TextView txt2 = (TextView) layouts.findViewById(R.id.childItem);
                HashMap<String, String> urunbilgisi = db.getOneRow(new String[]{"AD", "URUN"}, "muhendis_urun","urunid="+gper.get(i).urun);
                txt2.setText(gper.get(i).adi+ "(M: "+(gper.get(i).mesai.equals("")?"-":gper.get(i).mesai)+")\n"+(urunbilgisi!=null && urunbilgisi.containsKey("AD")?urunbilgisi.get("AD") +" - "+urunbilgisi.get("URUN") :" - "));
                ImageView img = (ImageView) layouts.findViewById(R.id.grid_image);
                img.setImageResource(R.drawable.delete);
                img.setTag(gorevId+";"+gper.get(i).sicilno);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String t=v.getTag().toString();
                        String[] t2=t.split(";");
                        MainActivity.gpd.delPersonel(t2[0], t2[1]);
                        loadPersonel();
                    }
                });
                ibasLayout.addView(layouts);
            }
        }
    }
    public void onClick(View v){
        if (v.getId()==R.id.ibasBtnKapat){
            Intent intent=new Intent();
            intent.putExtra("gorev",gorevId);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
