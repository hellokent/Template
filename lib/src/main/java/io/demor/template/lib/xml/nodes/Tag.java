package io.demor.template.lib.xml.nodes;

import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import io.demor.template.lib.xml.Xml;
import io.demor.template.lib.xml.XmlNode;

@Xml("tag")
public class Tag extends XmlNode {

    @Xml("max_level")
    public String  maxLevel;

    @Xml("enable")
    public String enable;

    private int mLevel;
    private boolean mEnable;
    private static final HashMap<String, Integer> LOG_LEVEL_MAP = new HashMap<String, Integer>(){
        {
            put("verbose", Log.VERBOSE);
            put("debug", Log.DEBUG);
            put("info", Log.INFO);
            put("warn", Log.WARN);
            put("error", Log.ERROR);
            put("assert", Log.ASSERT);
        }
    };

    @Override
    protected void onFinishMapAttr() {
        super.onFinishMapAttr();
        if (TextUtils.isEmpty(maxLevel) || !LOG_LEVEL_MAP.containsKey(maxLevel.toLowerCase())){
            mLevel = Log.VERBOSE;
        } else {
            mLevel = LOG_LEVEL_MAP.get(maxLevel.toLowerCase());
        }
        mEnable = Boolean.parseBoolean(enable);
    }

    public int getLevel() {
        return mLevel;
    }

    public boolean isEnable() {
        return mEnable;
    }
}
