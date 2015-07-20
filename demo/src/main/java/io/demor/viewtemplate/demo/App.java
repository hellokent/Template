package io.demor.viewtemplate.demo;

import android.app.Application;

import java.io.File;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import io.demor.template.lib.Utils;
import io.demor.template.lib.view.ClientAblity;

/**
 * Created by chenyang.coder@gmail.com on 13-10-28 下午11:58.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Utils.initUtils(this);

        final File cacheDir = StorageUtils.getOwnCacheDirectory(this, "UniversalImageLoader/Cache");
        final ImageLoaderConfiguration config = new
                ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 3)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2000000)) // You can pass your own memory cache implementation
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();
        ImageLoader.getInstance().init(config);
        ClientAblity.sInstance = ClientAblityImpl.sImpl;
    }
}