package io.demor.template.lib.jump;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import io.demor.template.lib.Utils;
import io.demor.template.lib.annotations.jumper.ActivityInfo;
import io.demor.template.lib.annotations.jumper.Extra;
import io.demor.template.lib.annotations.jumper.IntentFlag;
import io.demor.template.lib.annotations.jumper.IntentType;
import io.demor.template.lib.annotations.jumper.IntentUri;
import io.demor.template.lib.annotations.jumper.RequestBundle;
import io.demor.template.lib.annotations.jumper.RequestCode;

/**
 * ActivityJumper模块的反射代理实现
 * Created by chenyang.coder@gmail.com on 14-3-2 下午3:03.
 */
public class JumperInvokeHandler implements InvocationHandler {

    static final HashMap<Class, Method> INTENT_PUT_EXTRA_MAP = new HashMap<Class, Method>() {

        final Map<Class, Class> mCastMap = Collections.unmodifiableMap(
                new HashMap<Class, Class>() {
                    {
                        put(Byte.class, byte.class);
                        put(Byte[].class, byte[].class);

                        put(Boolean.class, boolean.class);
                        put(Boolean[].class, boolean[].class);

                        put(Character.class, char.class);
                        put(Character[].class, char[].class);

                        put(Short.class, short.class);
                        put(Short[].class, short[].class);

                        put(Integer.class, int.class);
                        put(Integer[].class, int[].class);

                        put(Long.class, long.class);
                        put(Long[].class, long[].class);

                        put(Float.class, float.class);
                        put(Float[].class, float[].class);

                        put(Double.class, double.class);
                        put(Double[].class, double[].class);
                    }});

        @Override
        public Method get(final Object key) {
            Method result = super.get(key);
            return result == null ? super.get(mCastMap.get(key)) : result;
        }
    };

    static {
        Method[] methods = Intent.class.getMethods();
        for (Method m : methods) {
            if ("putExtra".equals(m.getName())) {
                INTENT_PUT_EXTRA_MAP.put(m.getParameterTypes()[1], m);
            }
        }
    }

    final Intent INVALID_INTENT = new Intent();

    final HashMap<Method, Intent> INTENT_CACHE = new HashMap<Method, Intent>() {

        Intent invalidIntent(Method method) {
            put(method, INVALID_INTENT);
            return INVALID_INTENT;
        }

        @Override
        public Intent get(final Object key) {
            Intent result = super.get(key);
            if (result != null) {
                return new Intent(result);
            }
            final Method method = (Method) key;
            assert method != null;

            final ActivityInfo info  = method.getAnnotation(ActivityInfo.class);
            if (info == null) {
                return invalidIntent(method);
            }

            final Intent intent;
            if (info.clz() != null ) {
                intent = new Intent(mContext, info.clz());
            } else if (!TextUtils.isEmpty(info.action())) {
                intent = new Intent(info.action());
            } else {
                return invalidIntent(method);
            }

            for (int flag : info.defalutFlags()) {
                intent.addFlags(flag);
            }
            if (info.debug()) {
                intent.putExtra("DEBUG_INVOKED_FROM", Thread.currentThread().getStackTrace()[4].toString());
            }
            put(method, intent);
            return new Intent(intent);
        }
    };

    Context mContext;

    public JumperInvokeHandler(Application application) {
        mContext = application;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        final Intent intent = INTENT_CACHE.get(method);
        if (intent == null || intent == INVALID_INTENT) {
            return null;
        }

        final boolean needReturnIntent = method.getReturnType() == Intent.class;

        Activity activity = null;
        int activityIndex = 0;
        for (Object o : args){
            if (o instanceof Activity) {
                activity = (Activity) o;
                break;
            }
            ++activityIndex;
        }
        if (activity == null && needReturnIntent){
            return intent;
        }

        Annotation[][] paramAnno = method.getParameterAnnotations();
        LinkedList<ParamHolder> holderList = new LinkedList<ParamHolder>();

        Type[] genericTypes = method.getGenericParameterTypes();
        boolean startForResult = false;
        int requestCode = 0;
        Bundle requestBundle = null;
        for (int i = 0, n = paramAnno.length; i < n; ++i) {
            if (i == activityIndex || paramAnno[i] == null) {
                continue;
            }
            final Annotation annotation = paramAnno[i][0];
            if (annotation == null) {
                continue;
            }
            final Object arg = args[i];
            Class<?> argClz= arg == null ? null : arg.getClass();

            if (arg == null) {
                holderList.add(new ParamHolder(paramAnno[i], i, genericTypes[i]));
            } else if (annotation instanceof IntentFlag &&
                    (argClz == Integer.class || argClz == int.class)) {
                intent.addFlags((Integer) arg);
            } else if (annotation instanceof IntentUri) {
                if (argClz == Uri.class) {
                    intent.setData((Uri) arg);
                } else if (argClz == File.class) {
                    intent.setData(Uri.fromFile((File) arg));
                } else if (argClz == String.class) {
                    intent.setData(Uri.parse((String) arg));
                }
            } else if (annotation instanceof IntentType && argClz == String.class) {
                intent.setType((String) arg);
            } else if (annotation instanceof RequestCode &&
                    (argClz == int.class || argClz == Integer.class)) {
                startForResult = true;
                requestCode = (Integer) arg;
            } else if (annotation instanceof RequestBundle && argClz == Bundle.class) {
                requestBundle = (Bundle) arg;
            } else if (annotation instanceof Extra){
                holderList.add(new ParamHolder(paramAnno[i], i, genericTypes[i]));
            }
        }

        for (ParamHolder holder : holderList) {
            Object o = args[holder.index];
            if (o == null) {
                if (holder.option) {
                    continue;
                } else {
                    new Exception("...").printStackTrace(); //TODO
                    return needReturnIntent ? intent : null;
                }
            }
            final Class<?> clz = o.getClass();
            final String key = holder.key;

            final Method m = INTENT_PUT_EXTRA_MAP.get(clz);
            if (m != null) {
                m.invoke(intent, key, o);
            } else if (Utils.Reflect.isSubclassOf(clz, ArrayList.class)) {
                final Type listType = holder.genericType;
                ArrayList list = (ArrayList) o;
                if (Utils.Reflect.checkGen(listType, String.class)) {
                    intent.putStringArrayListExtra(key, list);
                } else if (Utils.Reflect.checkGen(listType, Integer.class)) {
                    intent.putIntegerArrayListExtra(key, list);
                } else if (Utils.Reflect.checkGen(listType, CharSequence.class)) {
                    intent.putCharSequenceArrayListExtra(key, list);
                } else if (Utils.Reflect.checkGen(listType, o.getClass())) {
                    intent.putParcelableArrayListExtra(key, list);
                }
            }
        }

        if (activity != null) {
            if (startForResult) {
                activity.startActivityForResult(intent, requestCode, requestBundle);
            } else {
                activity.startActivity(intent);
            }
            return null;
        } else {
            return intent;
        }
    }

    static class ParamHolder{
        String key;
        int index;
        boolean option = false;
        Type genericType;

        ParamHolder(Annotation[] annotations, int index, Type type) {
            for (Annotation a : annotations) {
                if (a instanceof Extra) {
                    Extra extra = (Extra) a ;
                    this.key = extra.value();
                    this.option = extra.option();
                    break;
                }
            }
            this.index = index;
            this.genericType = type;
        }
    }
}
