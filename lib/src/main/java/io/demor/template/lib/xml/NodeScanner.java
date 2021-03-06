package io.demor.template.lib.xml;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import io.demor.template.lib.L;
import io.demor.template.lib.TwowayMap;
import io.demor.template.lib.Utils;
import io.demor.template.lib.annotations.Scanned;
import io.demor.template.lib.classscanner.ScannerListener;
import io.demor.template.lib.xml.nodes.Item;
import io.demor.template.lib.xml.nodes.LogConfig;
import io.demor.template.lib.xml.nodes.Scanner;
import io.demor.template.lib.xml.nodes.Scanners;
import io.demor.template.lib.xml.nodes.Tag;

@Scanned
public class NodeScanner extends ScannerListener {

    public static final TwowayMap<String, ArrayList<Class>> TAG_NODE_MAP = new TwowayMap<String, ArrayList<Class>>();
    public static final TwowayMap<String, Class> LOCAL_NODE_MAP = new TwowayMap<String, Class>(){
        {
            putNode(Item.class);
            putNode(LogConfig.class);
            putNode(Scanner.class);
            putNode(Scanners.class);
            putNode(Tag.class);
        }

        /**
         * 预先加载几个基础的节点
         */
        public void putNode(Class<? extends XmlNode> nodeClz){
            put(nodeClz.getAnnotation(Xml.class).value(), nodeClz);
        }
    };

    @Override
    public void onScan(final Class<?> clazz) {
        L.i("node scanner:%s", clazz.getName());
        if ((clazz.getModifiers() & Modifier.ABSTRACT) > 0){
            L.i("get abstract class:%s", clazz.getName());
            return;
        }

        if (!Utils.Reflect.isSubclassOf(clazz, XmlNode.class)){
            L.i("class:%s, is not assign from xmlnode", clazz.getName());
            return;
        }

        final Xml xml = clazz.getAnnotation(Xml.class);
        final XmlTags tags = clazz.getAnnotation(XmlTags.class);

        if (xml == null && tags == null){
            return;
        }

        if (xml != null){
            LOCAL_NODE_MAP.put(xml.value(), clazz);
        }

        if (tags != null && tags.value() != null && tags.value().length > 0){
            for (String tag : tags.value()){
                ArrayList<Class> nodeList = TAG_NODE_MAP.getValue(tag);
                if (nodeList == null){
                    nodeList = new ArrayList<Class>();
                }
                nodeList.add(clazz);
                TAG_NODE_MAP.put(tag, nodeList);
            }
        }

        //初始化Node里面关于Field的Cache
        XmlNode.NODE_MAP_CACHE.get(clazz);
    }

}
