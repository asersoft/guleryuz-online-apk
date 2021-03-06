package com.guleryuz.puantajonline;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * Created by mehmet_erenoglu on 03.03.2017.
 */

public class PhotoZoom extends AppCompatActivity implements View.OnClickListener {
    private ImageView expanded_image;
    private Button btnKapat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_zoom);

        btnKapat=(Button)findViewById(R.id.ibasBtnKapat);
        btnKapat.setOnClickListener(this);

        expanded_image = (ImageView) findViewById(R.id.expanded_image);

        if(getIntent().hasExtra("photo")) {
            if(getIntent().hasExtra("local") && getIntent().getStringExtra("local").equals("1")) {
                Bitmap bmp = BitmapFactory.decodeFile(getIntent().getStringExtra("photo"));

                expanded_image.setImageBitmap(bmp );
            }else {
                Picasso.get()
                        .load(getIntent().getStringExtra("photo"))
                        .into(expanded_image);
            }
        }


    }

    public void onClick(View v) {
        if (v.getId()==R.id.ibasBtnKapat){
            finish();
        }
    }
}
