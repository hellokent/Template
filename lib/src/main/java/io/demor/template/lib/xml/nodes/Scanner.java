package io.demor.template.lib.xml.nodes;

import java.util.ArrayList;

import io.demor.template.lib.Utils;
import io.demor.template.lib.classscanner.ScannerListener;
import io.demor.template.lib.xml.Xml;
import io.demor.template.lib.xml.XmlNode;

@Xml("scanner")
public class Scanner extends XmlNode {

    @Xml("package")
    public ArrayList<XmlNode> packageNames = new ArrayList<XmlNode>();

    @Xml("processor")
    String processor;

    Class<?> mProcessorClass;

    @Override
    protected void onFinishMapAttr() {
        super.onFinishMapAttr();
        final String name = ((Scanners)getParentNode()).name;
        final String processorClassName = (processor.startsWith(".") ? name : "") + processor;

        try {
            mProcessorClass = Class.forName(processorClassName);
            if (!Utils.Reflect.isSubclassOf(mProcessorClass, ScannerListener.class)){
                mProcessorClass = null;
            }
        } catch (ClassNotFoundException e) {
            mProcessorClass = null;
            e.printStackTrace();
        }
    }

    public ScannerListener getIScanner(){
        if (mProcessorClass == null){
            return null;
        }

        try {
            return (ScannerListener)mProcessorClass.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
