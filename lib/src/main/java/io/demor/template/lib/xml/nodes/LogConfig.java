package io.demor.template.lib.xml.nodes;

import java.util.ArrayList;
import java.util.List;

import io.demor.template.lib.xml.Xml;
import io.demor.template.lib.xml.XmlNode;

@Xml("log_config")
public class LogConfig extends XmlNode {

    @Xml()
    public List<Item> items = new ArrayList<Item>();

}
