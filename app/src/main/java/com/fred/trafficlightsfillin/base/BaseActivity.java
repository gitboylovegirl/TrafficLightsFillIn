package com.fred.trafficlightsfillin.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;


import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.SoftApplication;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.StatusBarUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;

import java.util.Locale;


/**
 * 说明:
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    public SoftApplication softApplication;
    public SharedPreferenceUtils mSharedPrefHelper;
    public TextView mTitle,right;
    public ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        softApplication = (SoftApplication) getApplicationContext();
        mSharedPrefHelper = SharedPreferenceUtils.getInstance();
        StatusBarUtils.setColor(this, getResources().getColor(R.color.white));

        setContentLayout();
        dealLogicBeforeInitView();
        setTitleBarLayout();
        initView();
        dealLogicAfterInitView();
    }

    /**
     * 字体不变
     * @return
     */
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale.SIMPLIFIED_CHINESE);//设置简体中文
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;//设置简体中文
        }
        //resources.updateConfiguration(config, dm);
        return resources;
    }

    /**
     * 包含基础布局
     *
     * @param layoutResID
     */
    protected void setContainerView(int layoutResID) {
        setContentView(layoutResID);
//        mTitle = findViewById(R.id.titleContent);
//        mBack = findViewById(R.id.titleBack);
//        right=findViewById(R.id.titleRight);
       // mBack.setOnClickListener(this);
       // right.setOnClickListener(this);
    }

    public void setTitleText(String titleText){
        mTitle.setText(titleText);
    }

    /**
     * 点击事件分发
     *
     * @param event
     */
    protected void handleTitleBarEvent(int event) {

    }

    /**
     * 设置布局，在onCreate()生命周期中回调
     */
    public abstract void setContentLayout();

    /**
     * 设置标题栏
     */
    public abstract void setTitleBarLayout();

    /**
     * 初始化VIEW，在onCreate()生命周期中回调
     */
    public abstract void initView();

    /**
     * 在实例化布局之前处理的逻辑
     */
    public abstract void dealLogicBeforeInitView();

    /**
     * 在实例化布局之后处理的逻辑
     */
    public abstract void dealLogicAfterInitView();

    /**
     * 解决软键盘与底部输入框冲突问题
     *
     * @param color
     */
//    public void setStatusColorKeyBoard(int color) {
//        if (mImmersionBar == null) {
//            mImmersionBar = ImmersionBar.with(this);
//        }
//        mImmersionBar.keyboardEnable(true).fitsSystemWindows(true).statusBarColor(color).init();
//    }

    /**
     * 本段代码用来处理如果输入法还显示的话就消失掉输入键盘
     */
    protected void dismissSoftKeyboard(Activity activity) {
        try {
            if (activity != null && activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManage = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManage.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示键盘
     */
    protected void showKeyboard(View view) {
        try {
            InputMethodManager inputMethodManage = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManage.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过类名启动Activity
     *
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        if (!isFinishing()) {
            openActivity(pClass, null);
        }
    }

    /**
     * 通过类名启动Activity，并且含有Bundle数据
     *
     * @param pClass
     * @param pBundle
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        if (!isFinishing()) {
            Intent intent = new Intent(this, pClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (pBundle != null) {
                intent.putExtras(pBundle);
            }
            startActivity(intent);
        }
    }

    /**
     * 通过类名启动Activity
     *
     * @param pClass
     */
    protected void openActivityForResult(Class<?> pClass, int req) {
        if (!isFinishing()) {
            openActivityForResult(pClass, null, req);
        }
    }

    /**
     * 通过类名启动Activity，并且含有Bundle数据
     *
     * @param pClass
     * @param pBundle
     */
    protected void openActivityForResult(Class<?> pClass, Bundle pBundle, int req) {
        if (!isFinishing()) {
            Intent intent = new Intent(this, pClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (pBundle != null) {
                intent.putExtras(pBundle);
            }
            startActivityForResult(intent, req);
        }
    }

    /**
     * 通过Action启动Activity
     *
     * @param pAction
     */
    protected void openActivity(String pAction) {
        if (!isFinishing()) {
            openActivity(pAction, null);
        }
    }

    /**
     * 通过Action启动Activity，并且含有Bundle数据
     *
     * @param pAction
     * @param pBundle
     */
    protected void openActivity(String pAction, Bundle pBundle) {
        if (!isFinishing()) {
            Intent intent = new Intent(pAction);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (pBundle != null) {
                intent.putExtras(pBundle);
            }
            startActivity(intent);
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param info
     */
    public void showToast(String info) {
        if (!isFinishing()) {
            ToastUtil.show(this,info, Toast.LENGTH_LONG);
        }
    }

    public boolean checkNetWork() {
//        if (!NetWorkUtil.isNetDeviceAvailable(this)) {
//            showToast("网络中断");
//            return false;
//        }
        return true;
    }

    /**
     * onClick方法的封装，在此方法中处理点击事件
     *
     * @param view
     */
    abstract public void onClickEvent(View view);

    @Override
    public void onClick(View v) {
//        if (RepeatedClickHandler.isDoubleClick(v.getId())) {
//            return;
//        }
//        if (v.getId() == R.id.titleBack) {
//            finish();
//        } else {
//            onClickEvent(v);
//        }

        dismissSoftKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissSoftKeyboard(this);
        // 当前界面到后台，取消请求
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    //获取版本号
    public String getVersionName()  {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    public static String deleteString(String str, char delChar){
        String delStr = "";
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != delChar){
                delStr += str.charAt(i);
            }
        }
        return delStr;
    }
}
