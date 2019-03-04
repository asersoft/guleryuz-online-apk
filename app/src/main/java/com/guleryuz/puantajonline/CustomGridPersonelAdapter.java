package guleryuz.puantajonline;

/**
 * Created by mehmet_erenoglu on 28.02.2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import barcodescanner.app.com.barcodescanner.R;

public class CustomGridPersonelAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] personel;

    public CustomGridPersonelAdapter(Context c, String[] web ) {
        mContext = c;
        this.personel = web;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return personel.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single_personel, null);
            TextView textView = (TextView) grid.findViewById(R.id.childItem);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            imageView.setTag(personel[position]);
            textView.setText(personel[position]);
            imageView.setImageResource(R.drawable.delete);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ShowToast(mContext, "click - "+v.getTag());
                }
            });
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}
