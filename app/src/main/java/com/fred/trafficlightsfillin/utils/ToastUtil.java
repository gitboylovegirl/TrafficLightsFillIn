package com.fred.trafficlightsfillin.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast统一管理类
 *
 */
public class ToastUtil
{

    public static final String TAG = "Toast";
    private static int WaitTime = (int) (1.5 * 1000);
    private static Toast mToast = null;
    static int ShowTime[] = { Toast.LENGTH_SHORT, Toast.LENGTH_LONG };
    private static Boolean mToastbool = true;
    private ToastUtil()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message)
    {
        if (isShow)
            //ToastUtil.MakeTextAndShow(context, message + "" , 0);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    public static void MakeTextAndShow(Context context, String text,
                                       int duration) {

        mToast = Toast.makeText(context, text, ShowTime[duration]);
        mToast.setGravity(Gravity.BOTTOM, 0, 0);
        Show();

    }

    private static void Show() {


        if (mToastbool == true) {


            mToast.show();

            mToastbool = false;

            new Thread() {

                public void run() {

                    try {

                        Thread.sleep(WaitTime);

                        mToastbool = true;

                    } catch (InterruptedException e) {

                        e.printStackTrace();

                    }
                }

            }.start();

        } else {

            Log.i(TAG, "Toast正在等待再次加载显示");

        }

    }


    public static void Cancel() {

        if (mToast != null) {

            mToast.cancel();

        }
    }

    /**
     * 不排队的msg
     * @param context
     * @param msg
     */
    public static void showMsg(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context,msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}