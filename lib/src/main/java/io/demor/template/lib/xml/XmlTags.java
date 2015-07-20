package io.demor.template.lib.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述一个节点的Tag
 * Created by chenyang.coder@gmail.com on 14-1-10 下午2:23.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XmlTags {
    String[] value();
}
