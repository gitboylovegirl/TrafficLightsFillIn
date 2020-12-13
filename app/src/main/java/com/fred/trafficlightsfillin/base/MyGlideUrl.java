package com.fred.trafficlightsfillin.base;

import com.bumptech.glide.load.model.GlideUrl;

    public class MyGlideUrl extends GlideUrl {

        private String mUrl;

        public MyGlideUrl(String url) {
            super(url);
            mUrl = url;
        }

        @Override
        public String getCacheKey() {
            return mUrl.replace(findTokenParam(), "");
        }

        private String findTokenParam() {
            String tokenParam = "";
            int tokenKeyIndex = mUrl.indexOf("?token=") >= 0 ? mUrl.indexOf("?token=") : mUrl.indexOf("&token=");
            if (tokenKeyIndex != -1) {
                int nextAndIndex = mUrl.indexOf("&", tokenKeyIndex + 1);
                if (nextAndIndex != -1) {
                    tokenParam = mUrl.substring(tokenKeyIndex + 1, nextAndIndex + 1);
                } else {
                    tokenParam = mUrl.substring(tokenKeyIndex);
                }
            }
            return tokenParam;
        }

    }
