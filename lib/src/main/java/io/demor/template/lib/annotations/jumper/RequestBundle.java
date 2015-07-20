package io.demor.template.lib.annotations.jumper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * startActivityForResult需要的Bundle
 * Created by chenyang.coder@gmail.com on 14-3-3 下午4:00.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestBundle {
}
