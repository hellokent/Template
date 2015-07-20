package io.demor.template.lib.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.demor.template.lib.xml.nodes.BaseViewNode;

/**
 * 标识View处理对象要处理的Class
 * Created by chenyang.coder@gmail.com on 13-10-31 上午2:26.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewNodeClass {
    Class<? extends BaseViewNode> value();
}
