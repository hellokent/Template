package io.demor.template.lib.view;

import android.text.TextUtils;

import java.util.HashMap;

import io.demor.template.lib.Utils;
import io.demor.template.lib.annotations.Scanned;
import io.demor.template.lib.classscanner.ScannerListener;
import io.demor.template.lib.view.template.BaseViewRender;
import io.demor.template.lib.xml.Xml;

@Scanned
public class RenderScanner extends ScannerListener {

    public static final HashMap<String, Class<?>> NODE_NAME_MAP = new HashMap<String, Class<?>>();

    @Override
    public void onScan(final Class<?> clazz) {
        if (!Utils.Reflect.isSubclassOf(clazz, BaseViewRender.class)){
            return;
        }

        ViewNodeClass nodeClzAnnotation = clazz.getAnnotation(ViewNodeClass.class);

        if (nodeClzAnnotation == null){
            return;
        }

        final Class<?> nodeClz = nodeClzAnnotation.value();
        ViewFactory.NODE_COMP_MAP.put(nodeClzAnnotation.value(), (Class<? extends BaseViewRender>)clazz);
        assert nodeClz.getAnnotation(Xml.class) != null;
        final String localName = Utils.Reflect.getLocalNameFromClz(clazz);
        if (!TextUtils.isEmpty(localName)){
            NODE_NAME_MAP.put(localName, nodeClz);

        }
    }

}
