package com.trembit.localContentLoader.api.functions;

import com.adobe.fre.*;
import com.trembit.localContentLoader.base.LoadModel;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Andrey Assaul
 * Date: 22.09.2015
 * Time: 22:26
 */
public class GetContentBytesByUUIDFREFunction implements FREFunction {

    public static final String KEY = "getContentBytesByUUID";

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        try {
            String uuid = freObjects[0].getAsString();
            ByteBuffer byteBuffer = LoadModel.getInstance().getBytesByUUID(uuid);
            FREByteArray byteArray = FREByteArray.newByteArray();
            byteArray.acquire();
            byteArray.getBytes().put(byteBuffer);
            byteArray.release();
            return byteArray;
        } catch (FRETypeMismatchException e) {
            e.printStackTrace();
        } catch (FREInvalidObjectException e) {
            e.printStackTrace();
        } catch (FREWrongThreadException e) {
            e.printStackTrace();
        } catch (FREASErrorException e) {
            e.printStackTrace();
        }
        return null;
    }
}
