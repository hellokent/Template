package io.demor.template.lib.view.clientablity;

import android.graphics.Bitmap;

public interface IImageLoaderCallback {
    /**
     * 开始加载图片的回调
     * @param url 图片url地址
     */
    void onLoadingStarted(String url);
    void onLoadingFailed();
    void onLoadingComplete(Bitmap bitmap);
}
