package com.trembit.localContentLoader.api;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

/**
 * Created with IntelliJ IDEA.
 * User: Andrey Assaul
 * Date: 14.08.2015
 * Time: 16:00
 */
public class LocalContentLoaderFREExtension implements FREExtension {

    private static LocalContentLoaderFREContext context;

    @Override
    public FREContext createContext(String s) {
        if(context == null){
            context = new LocalContentLoaderFREContext();
        }
        return context;
    }

    @Override
    public void dispose() {
        if(context != null){
            context.dispose();
        }
    }

    @Override
    public void initialize() {}
}
