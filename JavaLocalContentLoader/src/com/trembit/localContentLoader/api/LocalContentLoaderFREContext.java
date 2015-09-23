package com.trembit.localContentLoader.api;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.trembit.localContentLoader.api.functions.GetContentBytesByUUIDFREFunction;
import com.trembit.localContentLoader.api.functions.LoadContentFREFunction;
import com.trembit.localContentLoader.api.functions.StopLoaderByUUIDFREFunction;
import com.trembit.localContentLoader.base.LoadModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrey Assaul
 * Date: 14.08.2015
 * Time: 15:51
 */
public class LocalContentLoaderFREContext extends FREContext {

    private static Map<String, FREFunction> functionMap;

    @Override
    public Map<String, FREFunction> getFunctions() {
        if(functionMap == null){
            functionMap = new HashMap<String, FREFunction>();
            functionMap.put(LoadContentFREFunction.KEY, new LoadContentFREFunction());
            functionMap.put(GetContentBytesByUUIDFREFunction.KEY, new GetContentBytesByUUIDFREFunction());
            functionMap.put(StopLoaderByUUIDFREFunction.KEY, new StopLoaderByUUIDFREFunction());
        }
        return functionMap;
    }

    @Override
    public void dispose() {
        LoadModel.getInstance().stopAll();
    }
}
