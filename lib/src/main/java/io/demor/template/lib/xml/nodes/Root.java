package io.demor.template.lib.xml.nodes;

import io.demor.template.lib.xml.Tags;
import io.demor.template.lib.xml.Xml;
import io.demor.template.lib.xml.XmlNode;

@Xml("root")
public class Root extends XmlNode {

    @Xml(tag = Tags.VIEW)
    public BaseViewNode viewNode;

    @Xml()
    public Lua lua;
}
