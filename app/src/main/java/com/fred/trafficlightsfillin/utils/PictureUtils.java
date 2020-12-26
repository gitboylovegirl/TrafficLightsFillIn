package com.fred.trafficlightsfillin.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PictureUtils {
    private static volatile File sSdcardDir;
    public static File saveImage(Bitmap bmp) {
        bmp = ratio(bmp,1000f);
        File file = null;
        FileOutputStream fos = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, os);
        while (os.toByteArray().length / 1024 > 500) {
            os.reset();
            options -= 10;
            if (options<1) break;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, os);
        }
        try {
            file = new File(getFileDir(), "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            fos = new FileOutputStream(file);
            fos.write(os.toByteArray());
        } catch (IOException e) {
            file = null;
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                    if (bmp != null) {
                        bmp.recycle();
                        bmp = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public static Bitmap ratio(Bitmap image,float width) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if( os.toByteArray().length / 1024>500) {//判断如果图片大于500kb,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        float ww = width;
        int be = (int) (newOpts.outWidth / ww);
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        return bitmap;
    }

    public static File getFileDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (sSdcardDir == null) {
                initSdcardDir();
            }
            return sSdcardDir;
        } else {
            sSdcardDir = null;
            return sSdcardDir;
        }
    }

    private static void initSdcardDir() {
        String fileDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getPath() + "/light/";
        ensureDirExists(fileDir);
        sSdcardDir = new File(fileDir);

    }

    public static boolean ensureDirExists(String dirString) {
        File dir = new File(dirString);
        if (!dir.exists()) {
            return dir.mkdirs();
        } else return dir.isDirectory();
    }
}
