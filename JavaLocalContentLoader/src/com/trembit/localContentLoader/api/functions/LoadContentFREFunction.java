package com.trembit.localContentLoader.api.functions;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.trembit.localContentLoader.base.LoadModel;

import static android.provider.MediaStore.Images.ImageColumns.DATA;

/**
 * Created with IntelliJ IDEA.
 * User: Andrey Assaul
 * Date: 22.09.2015
 * Time: 13:48
 */
public final class LoadContentFREFunction implements FREFunction {

    private static final String[] URI_PROJECTION = new String[]{DATA};

    public static final String KEY = "loadContent";

    private final LoadModel loadModel = LoadModel.getInstance();

    @Override
    public FREObject call(FREContext freContext, FREObject[] freObjects) {
        FREObject result = null;
        Cursor cursor = null;
        try {
            String uriString = freObjects[0].getAsString();
            Uri uri = Uri.parse(uriString);
            if ("content".equals(uri.getScheme())) {
                try{
                    cursor = freContext.getActivity().getContentResolver().query(uri, URI_PROJECTION, null, null, null);
                }catch (Exception e){/*skip query*/}
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String fileUri = cursor.getString(cursor.getColumnIndex(DATA));
                    if (fileUri != null && !"".equals(fileUri)) {
                        result = FREObject.newObject(fileUri);
                    } else {
                        result = FREObject.newObject(loadModel.loadMediaDataAsync(freContext, uri));
                    }
                } else {
                    result = FREObject.newObject(loadModel.loadMediaDataAsync(freContext, uri));
                }
            } else {
                result = FREObject.newObject(uriString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }
}
