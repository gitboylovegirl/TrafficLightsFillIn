package com.fred.trafficlightsfillin.base;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.intersection.bean.ImageResponse;
import com.fred.trafficlightsfillin.utils.GetImagePath;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {
    private Context mContext;
    private List<ImageResponse.ImageBean> mData=new ArrayList<>();
    private LayoutInflater mInflater;
    private OnButtonClickListener listener;
    private int mType;
    private long lastClick=0;
    public PagerAdapter(Context context, int type, List<ImageResponse.ImageBean> data,OnButtonClickListener buttonClickListener) {
        mContext = context;
        mType=type;
        mData = data;
        listener=buttonClickListener;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.layout_pager_adapter_item, null, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        ImageView delete=view.findViewById(R.id.iv_delete);
        ImageResponse.ImageBean imageBean = mData.get(position);
        if(mType==1){
            delete.setVisibility(View.GONE);
        }else {
            delete.setVisibility(View.VISIBLE);
        }
        if (imageBean.getUri() != null && !TextUtils.isEmpty(imageBean.getUri().toString())) {
            String filePath = GetImagePath.getPath(mContext, imageBean.getUri());
            Bitmap bm = GetImagePath.displayImage((Activity) mContext, filePath);
            imageView.setImageBitmap(bm);
        } else {
            String pictureUrl = RequestApi.BASE_OFFICIAL_URL + RequestApi.DOWN_IMG + "/" + imageBean.path;
            GlideUrl glideUrl = new GlideUrl(pictureUrl, new LazyHeaders.Builder()
                    .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                    .build());

            Glide.with(mContext)
                    .load(glideUrl)
                    .into(imageView);
        }
        delete.setOnClickListener(v -> {
            if(lastClick==0||(System.currentTimeMillis()-lastClick>500)){
                lastClick=System.currentTimeMillis();
                if(mData.size()>position){
                    if (listener!=null){
                        listener.onChoiceItem(position);
                    }
                    mData.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    /**
     * 按钮点击回调接口
     */
    public interface OnButtonClickListener {
        /**
         * 选中的item
         */
        void onChoiceItem(int pos);
    }
}
