package io.demor.viewtemplate.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import io.demor.template.lib.L;
import io.demor.template.lib.Utils;
import io.demor.template.lib.lua.LuaUtils;
import io.demor.template.lib.view.ViewFactory;
import io.demor.template.lib.xml.nodes.Root;
import io.demor.template.lua.LuaException;

public class ViewTemplateActivity extends Activity {

    FrameLayout mTemplateLayout;
    EditText mTemplateText;
    Button mShowButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_template_demo);
        mTemplateLayout = (FrameLayout) findViewById(R.id.template_view);
        mTemplateText = (EditText) findViewById(R.id.template_text);
        mShowButton = (Button) findViewById(R.id.show_btn);

        InputStream is = null;
        try {
            is = getAssets().open("template_test1.xml");
            mTemplateText.setText(Utils.Text.readStream2String(is));
        } catch (IOException e) {
            e.printStackTrace();
            mShowButton.setEnabled(false);
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        mShowButton.performClick();
    }

    public void show(View view) {
        Root node = Utils.Xml.parseString(mTemplateText.getText(), Root.class);

        if (node == null){
            Toast.makeText(this, " parse failed ", Toast.LENGTH_SHORT).show();
            return;
        }

        L.i("node:%s", node);
        View v = ViewFactory.createView(node.viewNode, this);
        mTemplateLayout.removeAllViewsInLayout();
        mTemplateLayout.addView(v);
        try {
            LuaUtils.callFunc(node.lua.getValue());
        } catch (LuaException e) {
            e.printStackTrace();
        }
        L.i("v.className:%s", v.getClass().getName());
        L.i("v.measured size:(%d, %d)", v.getMeasuredWidth(), v.getMeasuredHeight());
    }
}
