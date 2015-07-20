package io.demor.viewtemplate.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import io.demor.template.lib.annotations.jumper.ActivityInfo;
import io.demor.template.lib.annotations.jumper.Extra;
import io.demor.template.lib.annotations.jumper.IntentUri;

/**
 * Activity跳转的API
 * Created by chenyang.coder@gmail.com on 14-3-2 下午2:42.
 */
public interface ActivityAPI {

    @ActivityInfo(action = Intent.ACTION_VIEW)
    void browser(@IntentUri Uri uri);

    @ActivityInfo(clz = ViewTemplateActivity.class)
    void gotoViewTemplate(Activity activity, @Extra("aaa") String aaa, @Extra("sdafa") String asdf);

}
