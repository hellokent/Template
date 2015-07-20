package io.demor.template.lib.xml.nodes;

import java.util.ArrayList;

import io.demor.template.lib.xml.Xml;
import io.demor.template.lib.xml.XmlNode;

@Xml("scanners")
public class Scanners extends XmlNode {

    @Xml("name")
    public String name;

    @Xml()
    public ArrayList<Scanner> scanners = new ArrayList<Scanner>();
}
