package com.fred.trafficlightsfillin.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Display;
import android.view.View;
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
        void onChoiceItem(String str);
    }

    /***
     * @param context
     */
    public static void showChoiceDialog(Activity context, List<String> data,OnButtonClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        View view = View.inflate(context, R.layout.layout_str_pictiker, null);
        WheelPicker picker = view.findViewById(R.id.picker);

        picker.setData(data);
        picker.setOnItemSelectedListener((wheelPicker, o, i) -> {
            if(listener!=null){
                listener.onChoiceItem(data.get(i));
            }
        });

        alertDialog.getWindow().setContentView(view);
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        WindowManager manager = context.getWindowManager();
        Display display = manager.getDefaultDisplay();
        lp.width = display.getWidth() - 250;
        alertDialog.getWindow().setAttributes(lp);
    }
}
