package io.demor.template.lib.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import io.demor.template.lib.L;
import io.demor.template.lib.view.template.EmptyView;
import io.demor.template.lib.xml.XmlNode;
import io.demor.template.lib.xml.nodes.BaseViewNode;

public final class ViewFactory {

	private ViewFactory(){}

    protected static final HashMap<Class<? extends BaseViewNode>, Class<? extends AbstractViewRender>> NODE_COMP_MAP =
            new HashMap<Class<? extends BaseViewNode>, Class<? extends AbstractViewRender>>();

    public static Class<? extends BaseViewNode> getExistsViewNode(final String name){
        for (Class<? extends BaseViewNode> clz : NODE_COMP_MAP.keySet()){
            if (XmlNode.newXMLNodeInstance(clz).getLocalName().equals(name)){
                return clz;
            }
        }
        return null;
    }

    public static Class<? extends AbstractViewRender> getComponent(final BaseViewNode node){
        assert node != null;
        final Class<? extends AbstractViewRender> result = NODE_COMP_MAP.get(node.getClass());
        return result == null ? EmptyView.class : result;
    }

    public static View createView(final BaseViewNode node, final Context context){
        if (node == null){
            return null;
        }
        Class<? extends AbstractViewRender> componentClass = getComponent(node);
        if (componentClass == null){
            return null;
        }

        L.d("creatView, Node.class:%s, ViewComponment.class:%s", node.getClass().getSimpleName(), componentClass.getSimpleName());

        try {
            return componentClass
                    .getConstructor(Context.class, node.getClass(), ViewGroup.class)
                    .newInstance(context, node, null).getView();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
