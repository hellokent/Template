package io.demor.template.lib.annotations.jumper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * startActivityForResult需要的RequestCode
 * Created by chenyang.coder@gmail.com on 14-3-3 下午3:59.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestCode {
}
