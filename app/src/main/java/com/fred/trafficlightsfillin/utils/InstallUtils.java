package com.fred.trafficlightsfillin.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;

import com.maning.updatelibrary.MNUpdateApkFileProvider;
import com.maning.updatelibrary.http.AbsFileProgressCallback;
import com.maning.updatelibrary.http.DownloadFileUtils;
import com.maning.updatelibrary.utils.ActForResultCallback;
import com.maning.updatelibrary.utils.ActResultRequest;
import com.maning.updatelibrary.utils.MNUtils;
import java.io.File;
import java.util.Map;

public class InstallUtils {
    private static final String TAG = InstallUtils.class.getSimpleName();
    private static InstallUtils mInstance;
    private static Context mContext;
    private String httpUrl;
    private String filePath;
    private static InstallUtils.DownloadCallBack mDownloadCallBack;
    private static boolean isDownloading = false;
    private Map<String, String> headersMap;

    private InstallUtils() {
    }

    public static boolean isDownloading() {
        return isDownloading;
    }

    public static void setDownloadCallBack(InstallUtils.DownloadCallBack downloadCallBack) {
        if (isDownloading) {
            mDownloadCallBack = downloadCallBack;
        }

    }

    public static InstallUtils with(Context context) {
        mContext = context.getApplicationContext();
        if (mInstance == null) {
            mInstance = new InstallUtils();
        }

        return mInstance;
    }

    public InstallUtils setApkUrl(String apkUrl) {
        this.httpUrl = apkUrl;
        return mInstance;
    }

    public InstallUtils setApkPath(String apkPath) {
        this.filePath = apkPath;
        return mInstance;
    }
    public InstallUtils headers(Map<String, String> headersMap) {
        mInstance.headersMap = headersMap;
        return mInstance;
    }

    public InstallUtils setCallBack(InstallUtils.DownloadCallBack downloadCallBack) {
        mDownloadCallBack = downloadCallBack;
        return mInstance;
    }

    public void startDownload() {
        if (isDownloading) {
            cancleDownload();
        }

        if (TextUtils.isEmpty(this.filePath)) {
            this.filePath = MNUtils.getCachePath(mContext) + "/update.apk";
        }

        MNUtils.changeApkFileMode(new File(this.filePath));
        DownloadFileUtils.with().headers(headersMap).downloadPath(this.filePath).url(this.httpUrl).tag(InstallUtils.class).execute(new AbsFileProgressCallback() {
            int currentProgress = 0;

            public void onSuccess(String result) {
                InstallUtils.isDownloading = false;
                if (InstallUtils.mDownloadCallBack != null) {
                    InstallUtils.mDownloadCallBack.onComplete(InstallUtils.this.filePath);
                }

            }

            public void onProgress(long bytesRead, long contentLength, boolean done) {
                InstallUtils.isDownloading = true;
                if (InstallUtils.mDownloadCallBack != null) {
                    int progress = (int)(bytesRead * 100L / contentLength);
                    if (progress - this.currentProgress >= 1) {
                        InstallUtils.mDownloadCallBack.onLoading(contentLength, bytesRead);
                    }

                    this.currentProgress = progress;
                }

            }

            public void onFailed(String errorMsg) {
                InstallUtils.isDownloading = false;
                if (InstallUtils.mDownloadCallBack != null) {
                    InstallUtils.mDownloadCallBack.onFail(new Exception(errorMsg));
                }

            }

            public void onStart() {
                InstallUtils.isDownloading = true;
                if (InstallUtils.mDownloadCallBack != null) {
                    InstallUtils.mDownloadCallBack.onStart();
                }

            }

            public void onCancle() {
                InstallUtils.isDownloading = false;
                if (InstallUtils.mDownloadCallBack != null) {
                    InstallUtils.mDownloadCallBack.cancle();
                }

            }
        });
    }

    public static void cancleDownload() {
        DownloadFileUtils.cancle(InstallUtils.class);
    }

    public static void installAPKWithBrower(Context context, String httpUrlApk) {
        Uri uri = Uri.parse(httpUrlApk);
        Intent viewIntent = new Intent("android.intent.action.VIEW", uri);
        context.startActivity(viewIntent);
    }

    public static void checkInstallPermission(Activity activity, InstallUtils.InstallPermissionCallBack installPermissionCallBack) {
        if (hasInstallPermission(activity)) {
            if (installPermissionCallBack != null) {
                installPermissionCallBack.onGranted();
            }
        } else {
            openInstallPermissionSetting(activity, installPermissionCallBack);
        }

    }

    public static boolean hasInstallPermission(Context context) {
        return VERSION.SDK_INT >= 26 ? context.getPackageManager().canRequestPackageInstalls() : true;
    }

    public static void openInstallPermissionSetting(Activity activity, final InstallUtils.InstallPermissionCallBack installPermissionCallBack) {
        if (VERSION.SDK_INT >= 26) {
            Uri packageURI = Uri.parse("package:" + activity.getPackageName());
            Intent intent = new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", packageURI);
            (new ActResultRequest(activity)).startForResult(intent, new ActForResultCallback() {
                public void onActivityResult(int resultCode, Intent data) {
                    Log.i(InstallUtils.TAG, "onActivityResult:" + resultCode);
                    if (resultCode == -1) {
                        if (installPermissionCallBack != null) {
                            installPermissionCallBack.onGranted();
                        }
                    } else if (installPermissionCallBack != null) {
                        installPermissionCallBack.onDenied();
                    }

                }
            });
        } else if (installPermissionCallBack != null) {
            installPermissionCallBack.onGranted();
        }

    }

    public interface InstallPermissionCallBack {
        void onGranted();

        void onDenied();
    }

    public interface InstallCallBack {
        void onSuccess();

        void onFail(Exception var1);
    }

    public interface DownloadCallBack {
        void onStart();

        void onComplete(String var1);

        void onLoading(long var1, long var3);

        void onFail(Exception var1);

        void cancle();
    }
}
