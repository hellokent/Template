package io.demor.template.lib.xml.nodes;

import io.demor.template.lib.xml.Xml;
import io.demor.template.lib.xml.XmlNode;

@Xml("item")
public class Item extends XmlNode {

    @Xml("package")
    public String packageName;

    @Xml()
    public Tag tag;

}
