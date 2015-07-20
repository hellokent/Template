package io.demor.template.lib.view;

import io.demor.template.lib.view.clientablity.IImageLoaderCallback;

public abstract class ClientAblity {
    public static ClientAblity sInstance;

    protected static void setInstance(ClientAblity instance){
        sInstance = instance;
    }

    public abstract void sendImageLoadCallback(final String url, final IImageLoaderCallback callback);
}
