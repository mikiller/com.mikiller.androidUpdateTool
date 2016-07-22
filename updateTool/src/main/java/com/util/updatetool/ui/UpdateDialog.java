package com.util.updatetool.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.util.updatetool.R;
import com.util.updatetool.utils.MKUpdateUtil;

/**
 * Created by Mikiller on 2016/7/18.
 */

public class UpdateDialog extends Dialog {
    Context mContext;
    private LinearLayout ll_update_dlg;
    private TextView txtContent;
    private Button btnUpdate;
    private Button btnCancel;

    public enum DialogStyle {
        UPDATE, INSTALL
    }

    public UpdateDialog(Context context, DialogStyle dialogStyle, String content) {
        super(context, R.style.updatedialog);
        mContext = context;
        init(dialogStyle, content);
    }

    private void init(DialogStyle dialogStyle, String content) {
        //        setContentView(dialogStyle.equals(DialogStyle.UPDATE)?R.layout.updatedialog:R.layout.updatedialog_on_wifi);
        setContentView(R.layout.adapter_download_notice);
        initLay();
        switch (dialogStyle) {
            case UPDATE:
                initUpdateDialog(content);
                break;
            case INSTALL:
                //initInstallDialog();
                break;
        }
    }

    private void initLay() {
        ll_update_dlg = (LinearLayout) findViewById(R.id.ll_update_dlg);
        txtContent = (TextView) findViewById(R.id.tv_update_content);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

    }

    private void initUpdateDialog(String content) {
        //buttonLay.setVisibility(View.VISIBLE);
        //buttonLayInstall.setVisibility(View.GONE);
        //Button btnCancel = (Button) findViewById(R.id.dialog_btnCancel);
        //Button btnConfirm = (Button) findViewById(R.id.dialog_btnConfirm);
        ViewGroup.LayoutParams lp = ll_update_dlg.getLayoutParams();
        lp.height = MKUpdateUtil.getSceenHeight(mContext) * 3 / 5;
        lp.width = lp.height * 4 / 5;


        txtContent.setText(content);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //				Intent downloadIntent = new Intent(mContext,DownloadActivity.class);

                //				mContext.startActivity(downloadIntent);
                //FileUtils.setUpdateIsOnWifiPropertiesContent(false);
                MKUpdateUtil.startUpdateService(mContext, MKUpdateUtil.ntfBean);
                dismiss();
                //				startActivityForResult(downloadIntent,REQUEST_DOWNLOAD);
            }
        });
    }

    /*private void initInstallDialog(){
        buttonLay.setVisibility(View.VISIBLE);
        buttonLayInstall.setVisibility(View.GONE);
        Button btnInstall = (Button)findViewById(R.id.dialog_btnInstall);
        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KaQuApkUpdateUtil.getInstance().apkInstall();
            }
        });
    }*/
    public void appUpDate() {
        if (MKUpdateUtil.isNetworkOnWifi(mContext)) {
            //FileUtils.setUpdateIsOnWifiPropertiesContent(true);
            //Log.e("appUpDate","appUpDate-- is-->"+ FileUtils.getUpdateIsOnWifiPropertiesContent());
            MKUpdateUtil.startUpdateService(mContext, MKUpdateUtil.ntfBean);
        } else {
            //FileUtils.setUpdateIsOnWifiPropertiesContent(false);
            this.show();
        }
    }

    public void setBtnCancelListener(View.OnClickListener listener){
        btnCancel.setOnClickListener(listener);
    }

    public void setBtnUpdateListener(View.OnClickListener listener){
        btnUpdate.setOnClickListener(listener);
    }

}