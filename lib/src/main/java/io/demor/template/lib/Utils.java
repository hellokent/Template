package io.demor.template.lib;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;

import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.demor.template.lib.classscanner.ScannerUtil;
import io.demor.template.lib.xml.IParseCallback;
import io.demor.template.lib.xml.NodeScanner;
import io.demor.template.lib.xml.XmlNode;
import io.demor.template.lib.xml.XmlParser;

/**
 * 工具类
 * 原则：
 * 1. 工具类本身尽量少处理异常，异常由调用方处理
 * Created by chenyang.coder@gmail.com on 13-10-14 上午12:33.
 */
public final class Utils {

    private static Application sApp;

    public static Application getApp() {
        return sApp;
    }

    public static void initUtils(final Application app) {
        if (sApp != null){
            throw new IllegalStateException("sApp should init once");
        }
        sApp = app;
        try {
            ScannerUtil.INSTANCE.scanClass(sApp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DisplayMetrics metric = new DisplayMetrics();

        ((WindowManager)(sApp.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getMetrics(metric);
        App.sDensity = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5 / 2.0）
        App.sScreenWidth = metric.widthPixels;
        App.sScreenHeight = metric.heightPixels;

    }

    public static final class Reflect{

		/**
		 * 判断type是否是superType的子类型
		 * @param type 子类型
		 * @param superClass 父类型
		 * @return 假如有父子关系，或者子类型实现了父类型的接口，返回true，否则返回false
		 */
		public static boolean isSubclassOf(Class<?> type, Class<?> superClass) {
			if (type == null){
				return false;
			}
			if (type.equals(superClass)){
				return true;
			}
			Class[] interfaces = type.getInterfaces();
			for (Class i : interfaces){
				if (isSubclassOf(i, superClass)){
					return true;
				}
			}
			Class superType = type.getSuperclass();
			return superType != null && isSubclassOf(superType, superClass);
		}

        public static <T> T newInstance(final Class<T> clazz, Object... arg) throws
                IllegalAccessException,
                InstantiationException,
                NoSuchMethodException,
                InvocationTargetException {
            if (clazz == null){
                return null;
            }

            if(arg == null){
                return clazz.newInstance();
            }

            Class[] argumentClasses = new Class[arg.length];

            for (int i = 0, n = arg.length; i < n; ++i){
                argumentClasses[i] = arg[i].getClass();
            }

            return (T) clazz.getDeclaredConstructor(argumentClasses).newInstance(arg);
        }

        public static Class getCollectionTypeFromField(final Field field){
            Type t = field.getGenericType();
            return getCollectionType(t);
        }

        public static Class getCollectionType(final Type t){
            if (t instanceof ParameterizedType){
                ParameterizedType pt = (ParameterizedType)t;
                Type actual = pt.getActualTypeArguments()[0];
                if (actual instanceof Class) {
                    return (Class) actual;
                } else if (actual instanceof WildcardType){
                    WildcardType wildcardType = (WildcardType) actual;
                    Type[] typeArray = wildcardType.getLowerBounds() ;
                    if (typeArray != null && typeArray.length != 0) {
                        return (Class)typeArray[0];
                    }
                    typeArray = wildcardType.getUpperBounds();
                    if (typeArray != null && typeArray.length != 0) {
                        return (Class)typeArray[0];
                    }
                }
            }
            return null;
        }

        /**
         * 检查类型是否符合范型的要求
         * @param paramType 范型Type
         * @param clz 待检查类型
         */
        public static boolean checkGen(final Type paramType, final Class clz) {
            if (paramType == null ||
                    !(paramType instanceof ParameterizedType)){ // paramType不是范形
                return false;
            }
            ParameterizedType pt = (ParameterizedType) paramType;
            Type actual = pt.getActualTypeArguments()[0];
            if (actual instanceof Class) {
                return actual == clz;
            } else if (actual instanceof WildcardType){
                WildcardType wildcardType = (WildcardType) actual;
                Type[] typeArray = wildcardType.getLowerBounds() ;
                if (typeArray != null && typeArray.length != 0) {
                    return isSubclassOf((Class) typeArray[0], clz);
                }
                typeArray = wildcardType.getUpperBounds();
                if (typeArray != null && typeArray.length != 0) {
                    return isSubclassOf(clz, (Class) typeArray[0]);
                }
            }
            return false;
        }

        public static String getLocalNameFromClz(Class<?> nodeClz){
            if (NodeScanner.LOCAL_NODE_MAP.containsValue(nodeClz)){
                return NodeScanner.LOCAL_NODE_MAP.getKey(nodeClz);
            }
            if (nodeClz == null
                    || !Reflect.isSubclassOf(nodeClz, XmlNode.class)
                    || nodeClz.getAnnotation(io.demor.template.lib.xml.Xml.class) == null){
                return null;
            }

            String result = nodeClz.getAnnotation(io.demor.template.lib.xml.Xml.class).value();
            NodeScanner.LOCAL_NODE_MAP.put(result, nodeClz);
            return result;
        }

        public static String getLocalNameFromField(Field field){
            Class<?> nodeClz = field.getType();

            if (isSubclassOf(nodeClz, Collection.class)){
                nodeClz = getCollectionTypeFromField(field);
            }

            if (nodeClz == String.class || nodeClz == XmlNode.class){
                return field.getAnnotation(io.demor.template.lib.xml.Xml.class).value();
            }
            return getLocalNameFromClz(nodeClz);
        }



		/**
		 * 1.将字符串的数据，根据Field里面的不同Type，解析成相应的对象
		 * 2. 通过反射，将解析后的数据塞入obj里面
		 * Type目前支持Java原生类型
		 */
		public static Object setField(final Field f, final Object obj, String str) throws IllegalAccessException {
			assert f != null;
			assert !TextUtils.isEmpty(str);

			final Class type = f.getType();
			IParsable iParsable = PARSE_MAP.get(type);
			final Object dstObj;
			if (iParsable != null){
				try {
					dstObj = iParsable.parse(str);
				} catch (Throwable throwable){
					throwable.printStackTrace();
					return null;
				}
			} else if (isSubclassOf(type, Enum.class)){
				dstObj = Enum.valueOf((Class<? extends Enum>) type, str);
			} else {
				dstObj = f.get(obj);
			}

			f.set(obj, dstObj);
            return dstObj;
		}


		private interface IParsable{
			Object parse(String src);
		}

		static final HashMap<Class, IParsable> PARSE_MAP = new HashMap<Class, IParsable>(){
			{
				put(Byte.class, new ByteParsable());
				put(byte.class, new ByteParsable());
				put(Short.class, new ShortParsable());
				put(short.class, new ShortParsable());
				put(Integer.class, new IntegerParsable());
				put(int.class, new IntegerParsable());
				put(Float.class, new FloatParsable());
				put(float.class, new FloatParsable());
				put(Double.class, new DoubleParsable());
				put(double.class, new DoubleParsable());
				put(String.class, new StringParsable());
			}
		};

		private static class StringParsable implements IParsable{

			@Override
			public Object parse(String src) {
				return src;
			}
		}

		private static class DoubleParsable implements IParsable{

			@Override
			public Object parse(String src) {
				return Double.parseDouble(src);
			}
		}

		private static class FloatParsable implements IParsable{

			@Override
			public Object parse(String src) {
				return Float.parseFloat(src);
			}
		}

		private static class IntegerParsable implements IParsable{

			@Override
			public Object parse(String src) {
				return Integer.parseInt(src);
			}
		}

		private static class ShortParsable implements IParsable{

			@Override
			public Object parse(String src) {
				return Short.parseShort(src);
			}
		}

		private static class ByteParsable implements IParsable{

			@Override
			public Object parse(String src) {
				return Byte.decode(src);
			}
		}


	}

	public static final class Text {
		public static final HashMap<String, String> ESCAPE_MAP = new HashMap<String, String>(){
			{
				put("<", "&lt;");
				put(">", "&gt;");
				put("&", "&amp;");
				put("'", "&apos;");
				put("\"", "&quot;");
				put("®", "&reg;");
				put("©", "&copy;");
				put("™", "&trade;");
			}
		};

		/**
		 * XML编码
		 */
		public static String escape(String src){
			for (Map.Entry<String, String> entry : ESCAPE_MAP.entrySet()){
				src = src.replaceAll(entry.getKey(), entry.getValue());
			}
			return src;
		}

		public static String unescape(String src){
			for (Map.Entry<String, String> entry : ESCAPE_MAP.entrySet()){
				src = src.replaceAll(entry.getValue(), entry.getKey());
			}
			return src;
		}

		public static String multiText(final CharSequence chars, final int count){
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < count; ++i){
				result.append(chars);
			}
			return result.toString();
		}

        public static String layoutSizeToString(int size) {
            if (size == ViewGroup.LayoutParams.WRAP_CONTENT) {
                return "wrap-content";
            }
            if (size == ViewGroup.LayoutParams.MATCH_PARENT) {
                return "match-parent";
            }
            return String.valueOf(size);
        }

        public static String readStream2String(final InputStream is) throws IOException {
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = null;
            StringBuilder result = new StringBuilder();
            while ((str = br.readLine())!= null){
                result.append(str).append('\n');
            }
            return result.toString();
        }

	}

	public static final class Xml{

		final static SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();

		public static <T extends XmlNode> T parseString(final CharSequence str, final Class<T> nodeClass){
			return parseString(str, nodeClass, null);
		}

        public static <T extends XmlNode> T parseStream(final InputStream stream, final Class<T> nodeClass){
            return parseStream(stream, nodeClass, null);
        }


        public static <T extends XmlNode> T parseStream(final InputStream stream, final Class<T> nodeClass, IParseCallback parseCallback){
            final XmlParser<T> parser = new XmlParser<T>(nodeClass, null);
            try {
                SAX_PARSER_FACTORY.newSAXParser().parse(stream, parser);
                return parser.getRootNode();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

		public static <T extends XmlNode> T parseString(final CharSequence str, final Class<T> nodeClass, IParseCallback parseCallback){
            if( str == null){
                return null;
            }
            return parseStream(new ByteArrayInputStream(str.toString().getBytes()), nodeClass, parseCallback);
		}

	}

    public static final class App{

        public static float sDensity = 0f;
        public static int sScreenWidth = 0;
        public static int sScreenHeight = 0;

        public static String getMetaString(String name) {
            try {
                final ApplicationInfo ai = sApp.getPackageManager().getApplicationInfo(sApp.getPackageName(),
                        PackageManager.GET_META_DATA);
                if (ai == null || ai.metaData == null){
                    return null;
                }
                return ai.metaData.getString(name);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static int getPXFromDP(int dp){
            return (int)(sDensity * dp);
        }
    }
}
