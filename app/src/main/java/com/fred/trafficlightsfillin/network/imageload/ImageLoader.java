package com.fred.trafficlightsfillin.network.imageload;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.fred.trafficlightsfillin.utils.AppUtils;

import java.io.File;

/**
 * 图片加载库
 */
public class ImageLoader {

    private static RequestManager glide;

    /**
     * 网络图片加载
     *
     * @param context
     * @param imageUrl
     * @param targetImageView
     */
    public static void loadNetImage(Context context, String imageUrl, ImageView targetImageView) {
        if (context == null) {
            return;
        }
        try {
            glide = Glide.with(context);
            glide.load(imageUrl).fitCenter().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
        } catch (Exception e) {

        }
    }

    public static void loadNetImage(Context context, String imageUrl, int errorDefaultImg, ImageView targetImageView) {
        try {
            glide = Glide.with(context);
            glide.load(imageUrl).fitCenter().diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorDefaultImg).into(targetImageView);
        } catch (Exception e) {

        }
    }

    public static void loadNetImage(Context context, String imageUrl, ImageView targetImageView, RequestListener listener) {
        try {
            glide = Glide.with(context);
            glide.load(imageUrl).fitCenter().listener(listener).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
        } catch (Exception e) {
        }
    }

    public static void loadNetImage(Context context, String imageUrl, int waittingImageId, int defaultImageId, ImageView targetImageView) {
        try {
            glide = Glide.with(context);
            glide.load(imageUrl).asBitmap().placeholder(waittingImageId).error(defaultImageId).fitCenter().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);

        }catch (Exception e) {

        }
    }

    public static void loadNetImage(Context context, String imageUrl, int waittingImageId, int defaultImageId, ImageView targetImageView, RequestListener listener) {
        try {
            glide = Glide.with(context);
            glide.load(imageUrl).asBitmap().placeholder(waittingImageId).error(defaultImageId).fitCenter().listener(listener).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
        }catch (Exception e) {

        }
    }

    /**
     * 自定义尺寸
     *
     * @param context
     * @param imageUrl
     * @param width
     * @param height
     * @param targetImageView
     */
    public static void loadNetResizeImage(Context context, String imageUrl, int width, int height, int waittingImageId, int defaultImageId, ImageView targetImageView) {
        glide = Glide.with(context);
        glide.load(imageUrl).override(width, height).placeholder(waittingImageId).error(defaultImageId).centerCrop().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
    }

    public static void loadLocalResizeImage(Context context, int resourceId, int width, int height, int waittingImageId, int defaultImageId, ImageView targetImageView) {
        glide = Glide.with(context);
        glide.load(resourceId).override(width, height).placeholder(waittingImageId).error(defaultImageId).centerCrop().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
    }

    /**
     * 加载网络gif图片(加载一次)
     *
     * @param context
     * @param imageUrl
     * @param defaultImageId
     * @param targetImageView
     */
    public static void loadGifImage(Context context, String imageUrl, int waittingImageId, int defaultImageId, ImageView targetImageView) {
        glide = Glide.with(context);
        glide.load(imageUrl).placeholder(waittingImageId).error(defaultImageId).fitCenter().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(targetImageView, 1));
    }

    /**
     * 加载网络gif图片(循环)
     *
     * @param context
     * @param imageUrl
     * @param defaultImageId
     * @param targetImageView
     */
    public static void loadCircleGifImage(Context context, String imageUrl, int waittingImageId, int defaultImageId, ImageView targetImageView) {
        try {
            glide = Glide.with(context);
            glide.load(imageUrl).asGif().placeholder(waittingImageId).error(defaultImageId).fitCenter().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
        } catch (Exception e) {

        }
    }

    /**
     * 加载本地gif图片(循环)
     *
     * @param context
     * @param resourceId
     * @param targetImageView
     */
    public static void loadResourceCircleGifImage(Context context, int resourceId, int waittingImageId, int defaultImageId, ImageView targetImageView) {
        glide = Glide.with(context);
        glide.load(resourceId).asGif().placeholder(waittingImageId).error(defaultImageId).fitCenter().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
    }

    /**
     * 加载本地gif图片(加载一次)
     *
     * @param context
     * @param resourceId
     * @param targetImageView
     */
    public static void loadResourceGifImage(Context context, int resourceId, int waittingImageId, int defaultImageId, ImageView targetImageView) {
        glide = Glide.with(context);
        glide.load(resourceId).placeholder(waittingImageId).error(defaultImageId).fitCenter().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(targetImageView, 1));
    }

    /**
     * 设置是否缓存
     *
     * @param context
     * @param imageUrl
     * @param targetImageView DiskCacheStrategy.NONE 什么都不缓存
     *                        DiskCacheStrategy.SOURCE 只缓存最高解析图的image
     *                        DiskCacheStrategy.RESULT 缓存最后一次那个image,比如有可能你对image做了转化
     *                        DiskCacheStrategy.ALL image的所有版本都会缓存
     */
    public static void loadNetImageWithCache(Context context, String imageUrl, DiskCacheStrategy diskCacheStrategy, int waittingImageId, int defaultImageId, ImageView targetImageView) {
        glide = Glide.with(context);
        glide.load(imageUrl).diskCacheStrategy(diskCacheStrategy).placeholder(waittingImageId).error(defaultImageId).fitCenter().dontAnimate().into(targetImageView);
    }

    /**
     * 本地图片加载
     *
     * @param context
     * @param file
     * @param targetImageView
     */
    public static void loadLocalImage(Context context, File file, int waittingImageId, int defaultImageId, ImageView targetImageView) {
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Test.jpg");
        glide = Glide.with(context);
        glide.load(file).placeholder(waittingImageId).error(defaultImageId).fitCenter().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
    }

    /**
     * 通过资源ID
     *
     * @param context
     * @param resourceId
     * @param targetImageView
     */
    public static void loadResourceImage(Context context, int resourceId, int waittingImageId, int defaultImageId, ImageView targetImageView) {
//        int resourceId = R.mipmap.ic_launcher;
        glide = Glide.with(context);
        glide.load(resourceId).placeholder(waittingImageId).error(defaultImageId).fitCenter().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
    }

    /**
     * uri
     *
     * @param context
     * @param uri
     * @param targetImageView
     */
    public static void loadResourceImage(Context context, Uri uri, int waittingImageId, int defaultImageId, ImageView targetImageView) {
//        int resourceId = R.mipmap.ic_launcher;
        glide = Glide.with(context);
        glide.load(uri).placeholder(waittingImageId).error(defaultImageId).fitCenter().dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(targetImageView);
    }

    /**
     * 根据图片宽高比计算控件大小
     *
     * @param context
     * @param width
     * @param height
     * @param margin
     * @return
     */
    public static int getImageHeight(Activity context, int width, int height, int margin) {
        int widthTemp = AppUtils.getWidthPixels(context) - margin;
        return widthTemp * height / width;
    }

    public static void destroyGlide(){
        if (glide != null) {
//            glide.pauseRequests();
        }
    }

}
