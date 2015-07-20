package io.demor.viewtemplate.demo;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import io.demor.template.lib.view.ClientAblity;
import io.demor.template.lib.view.clientablity.IImageLoaderCallback;

public final class ClientAblityImpl extends ClientAblity {
    static final ClientAblityImpl sImpl = new ClientAblityImpl();
    static {
        setInstance(sImpl);
    }

    ImageLoader mImageLoader;

    private ClientAblityImpl(){
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public void sendImageLoadCallback(final String url, final IImageLoaderCallback callback) {
        mImageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(final String imageUri, final View view) {
                callback.onLoadingStarted(imageUri);
            }

            @Override
            public void onLoadingFailed(final String imageUri, final View view, final FailReason failReason) {
                callback.onLoadingFailed();
            }

            @Override
            public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {
                callback.onLoadingComplete(loadedImage);
            }

            @Override
            public void onLoadingCancelled(final String imageUri, final View view) {

            }
        });
    }
}
