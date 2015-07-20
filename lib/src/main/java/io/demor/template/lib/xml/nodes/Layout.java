package io.demor.template.lib.xml.nodes;

import java.util.LinkedList;

import io.demor.template.lib.xml.Tags;
import io.demor.template.lib.xml.Xml;

@Xml("layout")
public class Layout extends BaseViewNode {

    @Xml("orientation")
    public String orientation;

    @Xml(tag = Tags.VIEW)
    public LinkedList<BaseViewNode> texts = new LinkedList<BaseViewNode>();

}
