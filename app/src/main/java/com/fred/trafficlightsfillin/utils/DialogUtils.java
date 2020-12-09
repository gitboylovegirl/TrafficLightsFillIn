package com.fred.trafficlightsfillin.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fred.trafficlightsfillin.R;
import com.itheima.wheelpicker.WheelPicker;

import java.util.List;

public class DialogUtils {
    /**
     * 按钮点击回调接口
     */
    public interface OnButtonClickListener {
        /**
         * 确定按钮点击回调方法
         */
        void onPositiveButtonClick();

        /**
         * 取消按钮点击回调方法
         */
        void onNegativeButtonClick();

        /**
         * 选中的item
         */
        void onChoiceItem(String str,int pos);
    }

    /***
     * @param context
     */
    public static void showChoiceDialog(Activity context, List<String> data,OnButtonClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(true);
        dialog.show();

        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.layout_str_pictiker);
        WheelPicker picker = window.findViewById(R.id.picker);

        picker.setData(data);
        picker.setOnItemSelectedListener((wheelPicker, o, i) -> {
            if(listener!=null){
                listener.onChoiceItem(data.get(i),i);
            }
        });

        //给弹窗设置宽高
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        WindowManager manager = context.getWindowManager();
        Display display = manager.getDefaultDisplay();
        lp.width = 850;
        dialog.getWindow().setAttributes(lp);

//        dialog.setOnDismissListener(dialog1 -> {
//            Log.e("fred","选择的数据2："+data.get(picker.getCurrentItemPosition()));
//            if(listener!=null){
//                listener.onChoiceItem(data.get(picker.getCurrentItemPosition()),picker.getCurrentItemPosition());
//            }
//        });
    }
}
