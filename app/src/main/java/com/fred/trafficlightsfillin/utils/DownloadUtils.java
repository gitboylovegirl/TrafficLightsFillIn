package com.fred.trafficlightsfillin.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 说明:apk下载
 */
public class DownloadUtils {

    //下载器
    private DownloadManager downloadManager;
    private Context mContext;
    //下载的ID
    private long downloadId;
    private String name;
    private String pathstr;

    public DownloadUtils(Context context, String url, String name) {
        this.mContext = context;
        downloadAPK(url, name);
        this.name = name;
    }

    //下载apk
    private void downloadAPK(String url, String name) {
        if(url.isEmpty()){
         ToastUtil.showShort(mContext, "数据错误不能进行下载");
            return;
        }
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(name);
        request.setDescription("配时中心版本更新");
        request.setVisibleInDownloadsUi(true);


        //设置下载的路径
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name);
        request.setDestinationUri(Uri.fromFile(file));
        pathstr = file.getAbsolutePath();
        //获取DownloadManager
        if (downloadManager == null)
            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            downloadId = downloadManager.enqueue(request);
        }

        //注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    //检查下载状态
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int statusCodeId = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            switch (statusCodeId) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case 7:
                    ToastUtil.showShort(mContext, "下载完成");
                    //下载完成安装APK
                    installAPK();
                    cursor.close();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    ToastUtil.showShort(mContext, "下载失败");
                    cursor.close();
                    mContext.unregisterReceiver(receiver);
                    break;
            }
        }
    }

    private void installAPK() {
        setPermission(pathstr);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Android 7.0以上要使用FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File file = (new File(pathstr));
            Uri apkUri = FileProvider.getUriForFile(mContext, "com.fred.trafficlightsfillin.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name)), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }
    /**
     * 安装 apk
     * */
    public static void installApk(Activity activity, String path) {
        try {
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri data = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
                // 注意 下面参数com.com.ljp.downdemo 为apk的包名加上.fileprovider，
                data = FileProvider.getUriForFile(activity, "com.fred.trafficlightsfillin.fileprovider", file);
            } else {
                data = Uri.fromFile(file);
            }
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //修改文件权限
    private void setPermission(String absolutePath) {
        String command = "chmod " + "777" + " " + absolutePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 带进度数值回调的接口
     */
    public interface downloadPositionListener {
        void onPosition(int pos);
    }

    /**
     * 下载带进度值
     * @param activity
     * @param url
     */
    public static void downLoadApk(Activity activity,String url,downloadPositionListener listener){
        if(url.isEmpty()){
            ToastUtil.showShort(activity, "数据错误不能进行下载");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ToastUtil.showShort(activity,"请同意读写权限，否则无法正常下载");
                //如果没有写sd卡权限
                activity.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            } else {
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("authorization", SharedPreferenceUtils.getInstance().getToken());
                headersMap.put("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken());
                InstallUtils.with(activity)
                        //必须-下载地址
                        .setApkUrl(url)
                        .headers(headersMap)
                        .setCallBack(new InstallUtils.DownloadCallBack() {
                            @Override
                            public void onStart() {
                                //下载开始
                            }

                            @Override
                            public void onComplete(String path) {
                                //下载完成
                                //先判断有没有安装权限---适配8.0
                                InstallUtils.checkInstallPermission(activity, new InstallUtils.InstallPermissionCallBack() {
                                    @Override
                                    public void onGranted() {
                                        //去安装APK
                                        DownloadUtils.installApk(activity,path);
                                    }

                                    @Override
                                    public void onDenied() {
                                        //打开设置页面
                                        InstallUtils.openInstallPermissionSetting(activity, new InstallUtils.InstallPermissionCallBack() {
                                            @Override
                                            public void onGranted() {
                                                //去安装APK
                                                DownloadUtils.installApk(activity,path);
                                            }

                                            @Override
                                            public void onDenied() {
                                                //还是不允许咋搞？
                                                //System.exit(0);
                                            }
                                        });
                                    }
                                });

                            }

                            @Override
                            public void onLoading(long total, long current) {
                                //下载中
                                float pressent = (float) current / total* 100;
                                if (listener!=null){
                                    listener.onPosition((int)pressent);
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                                //下载失败
                                ToastUtil.showShort(activity, "下载失败");
                            }

                            @Override
                            public void cancle() {
                                //下载取消
                            }
                        })
                        //开始下载
                        .startDownload();
            }
            return;
        }else {
            InstallUtils.with(activity)
                    //必须-下载地址
                    .setApkUrl(url)
                    .setCallBack(new InstallUtils.DownloadCallBack() {
                        @Override
                        public void onStart() {
                            //下载开始
                        }

                        @Override
                        public void onComplete(String path) {
                            //下载完成
                            //去安装APK
                            DownloadUtils.installApk(activity,path);
                        }

                        @Override
                        public void onLoading(long total, long current) {
                            //下载中
                            float pressent = (float) current / total* 100;
                            if (listener!=null){
                                listener.onPosition((int)pressent);
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            //下载失败
                            ToastUtil.showShort(activity, "下载失败");
                        }

                        @Override
                        public void cancle() {
                            //下载取消
                        }
                    })
                    //开始下载
                    .startDownload();
        }
    }

}
