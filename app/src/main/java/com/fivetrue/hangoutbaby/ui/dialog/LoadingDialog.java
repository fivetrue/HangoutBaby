package com.fivetrue.hangoutbaby.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import com.fivetrue.hangoutbaby.R;

/**
 * Created by kwonojin on 16. 9. 20..
 */
public class LoadingDialog extends ProgressDialog{
    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public static LoadingDialog create(Context context){
        LoadingDialog dialog = new LoadingDialog(context);
        dialog.setProgressStyle(STYLE_SPINNER);
        dialog.getWindow().setDimAmount(0);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
        dialog.setTitle(null);
        dialog.setMessage(null);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_loading);
    }
}
