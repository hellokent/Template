package io.demor.template.lib.lua;

import android.widget.Toast;

import io.demor.template.lib.Utils;
import io.demor.template.lua.JavaFunction;
import io.demor.template.lua.LuaException;
import io.demor.template.lua.LuaState;

@LuaFunctionName("toast")
public class ToastFunction extends JavaFunction {

    public ToastFunction(final LuaState L) {
        super(L);
    }

    @Override
    public int execute() throws LuaException {

        Toast.makeText(Utils.getApp(), getParam(-1).getString(), Toast.LENGTH_SHORT).show();
        return 0;
    }
}
