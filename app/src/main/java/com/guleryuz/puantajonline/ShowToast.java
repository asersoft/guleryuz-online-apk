package com.guleryuz.puantajonline;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Asersoft on 29.04.2017.
 */

public class ShowToast {
    public ShowToast(Context context, String info) {
        Toast toast = Toast.makeText(context, info, Toast.LENGTH_SHORT); //Toast.makeText(context, Html.fromHtml("<font color='#e3f2fd' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL, 10, 10);
        toast.show();
    }
    public ShowToast(Context context, int resID) {
        Toast toast = Toast.makeText(context, context.getString(resID), Toast.LENGTH_SHORT); //Toast.makeText(context, Html.fromHtml("<font color='#e3f2fd' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL, 10, 10);
        toast.show();
    }
}
