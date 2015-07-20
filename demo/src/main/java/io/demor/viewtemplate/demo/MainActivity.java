package io.demor.viewtemplate.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.lang.reflect.Proxy;

import io.demor.template.lib.jump.JumperInvokeHandler;

public class MainActivity extends Activity {

    ActivityAPI mAPI;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAPI = (ActivityAPI) Proxy.newProxyInstance(getClassLoader(), new Class[]{ActivityAPI.class}, new JumperInvokeHandler(getApplication()));
    }

    public void showLua(View view) {
    }

    public void showView(View view) {
        mAPI.gotoViewTemplate(this, "aaa", "asdf");
    }
}