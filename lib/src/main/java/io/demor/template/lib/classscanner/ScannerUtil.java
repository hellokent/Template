package io.demor.template.lib.classscanner;

import android.app.Application;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import dalvik.system.DexFile;
import io.demor.template.lib.Config;
import io.demor.template.lib.L;
import io.demor.template.lib.Utils;
import io.demor.template.lib.xml.XmlNode;
import io.demor.template.lib.xml.nodes.Scanner;
import io.demor.template.lib.xml.nodes.Scanners;

public enum ScannerUtil {
    INSTANCE(Utils.getApp());

    final Application mAPP;
    final List<Scanner> mScannerList = new ArrayList<Scanner>();
    final HashMap<String, ScannerListener> mScannerMap = new HashMap<String, ScannerListener>();

    private ScannerUtil(Application app){
        mAPP = app;
        try {
            String scannerLog = Utils.App.getMetaString(Config.SCNNER_LOG_TAG);
            final Scanners scanners;
            if (!TextUtils.isEmpty(scannerLog)){
                scanners = Utils.Xml.parseStream(
                        app.getAssets().open(scannerLog),
                        Scanners.class);
                if (scanners != null){
                    mScannerList.addAll(scanners.scanners);
                } else {
                    return;
                }
            } else {
                return;
            }
            for (Scanner scanner : mScannerList){
                final ScannerListener scannerListener = scanner.getIScanner();
                if (scannerListener != null){
                    for(XmlNode node : scanner.packageNames){
                        String packageName = node.getValue();
                        if (packageName.startsWith(".")){
                            packageName = scanners.name + packageName;
                        }
                        mScannerMap.put(packageName, scannerListener);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scanClass(final Application app) throws IOException {
        if (mScannerList.isEmpty() || mScannerMap.isEmpty()){
            return;
        }

        final LinkedList<String> appSrcpaths = new LinkedList<String>();
        final LinkedList<String> matchedPaths = new LinkedList<String>();

        for (ScannerListener sl : mScannerMap.values()){
            sl.onScanBegin();
        }

        //get class pathes
        String sourcePath = app.getApplicationInfo().sourceDir;
        DexFile dexfile = new DexFile(sourcePath);
        Enumeration<String> entries = dexfile.entries();
        while (entries.hasMoreElements()) {
            appSrcpaths.add(entries.nextElement());
        }

        if (appSrcpaths.isEmpty()){
            return;
        }

        for (String path : appSrcpaths){
            if (mScannerMap.containsKey(path.substring(0, path.lastIndexOf(".")))){
                matchedPaths.add(path);
            }
        }

        L.v("get MatchedPath:%s", matchedPaths);

        for (String matchedPath : matchedPaths){
            scanClass(matchedPath);
        }


        for (ScannerListener sl : mScannerMap.values()){
            sl.onScanEnd();
        }
    }

    private void scanClass(final String className){
        ScannerListener scanner = mScannerMap.get(className.substring(0, className.lastIndexOf(".")));
        if (scanner == null){
            return;
        }

        try {
            scanner.onScan(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void scanPath(String path, List<String> matchedList){
        if (mScannerMap.containsKey(path.substring(0, path.lastIndexOf(".")))){
            matchedList.add(path);
        }
    }

}
