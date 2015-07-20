package io.demor.template.lib.lua;

import io.demor.template.lib.Utils;
import io.demor.template.lib.annotations.Scanned;
import io.demor.template.lib.classscanner.ScannerListener;
import io.demor.template.lua.JavaFunction;

/**
 * Lua函数扫描类，在scanner里面注册好了的^_^
 * Created by chenyang.coder@gmail.com on 13-11-19 下午8:38.
 */
@Scanned
public class LuaFunctionScanner extends ScannerListener {

    @Override
    public void onScan(final Class<?> clazz) {
        if (!Utils.Reflect.isSubclassOf(clazz, JavaFunction.class)){
            return;
        }

        final LuaFunctionName nameAnnotation = clazz.getAnnotation(LuaFunctionName.class);

        if (nameAnnotation == null){
            return;
        }

        try {
            JavaFunction function = Utils.Reflect.newInstance((Class<JavaFunction>)clazz, LuaUtils.L);
            function.register(nameAnnotation.value());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
