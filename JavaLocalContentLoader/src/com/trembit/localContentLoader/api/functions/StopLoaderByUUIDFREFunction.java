package com.trembit.localContentLoader.api.functions;

import com.adobe.fre.*;
import com.trembit.localContentLoader.base.LoadModel;

/**
 * Created with IntelliJ IDEA.
 * User: Andrey Assaul
 * Date: 23.09.2015
 * Time: 11:07
 */
public class StopLoaderByUUIDFREFunction implements FREFunction {

    public final static String KEY = "stopLoaderByUUID";

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        try {
            String uuid = freObjects[0].getAsString();
            LoadModel.getInstance().stopLoaderByUUID(uuid);
        } catch (FRETypeMismatchException e) {
            e.printStackTrace();
        } catch (FREInvalidObjectException e) {
            e.printStackTrace();
        } catch (FREWrongThreadException e) {
            e.printStackTrace();
        }
        return null;
    }
}
