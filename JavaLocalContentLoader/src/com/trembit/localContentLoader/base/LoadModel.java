package com.trembit.localContentLoader.base;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import com.adobe.fre.FREContext;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Andrey Assaul
 * Date: 22.09.2015
 * Time: 19:29
 */
public class LoadModel {
    private static final int READ_BUFFER_LENGTH = 16384;

    private final static LoadModel ourInstance = new LoadModel();
    private final Map<String, AsyncTask> loaders = new HashMap<String, AsyncTask>();
    private final Map<String, ByteBuffer> results = new HashMap<String, ByteBuffer>();

    public static LoadModel getInstance() {
        return ourInstance;
    }

    private LoadModel() {
    }

    public String loadMediaDataAsync(final FREContext freContext, final Uri uri) {
        final String uuid = UUID.randomUUID().toString();
        final Context context = freContext.getActivity();
        AsyncTask<Object, Void, ByteBuffer> task = new AsyncTask<Object, Void, ByteBuffer>() {
            @Override
            protected ByteBuffer doInBackground(Object... params) {
                InputStream inputStream = null;
                ByteBuffer byteBuffer = null;
                ByteArrayOutputStream outputStream = null;
                try {
                    inputStream = context.getContentResolver().openInputStream(uri);
                    outputStream = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[READ_BUFFER_LENGTH];
                    while (!isCancelled() && (nRead = inputStream.read(data, 0, data.length)) != -1){
                        outputStream.write(data, 0, nRead);
                    }
                    if (!isCancelled()){
                        outputStream.flush();
                        byteBuffer = ByteBuffer.wrap(outputStream.toByteArray());
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null){
                        try {
                            inputStream.close();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null){
                        try {
                            outputStream.close();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                return byteBuffer;
            }

            @Override
            protected void onPostExecute(ByteBuffer byteBuffer) {
                loaders.remove(uuid);
                if(byteBuffer != null){
                    results.put(uuid, byteBuffer);
                    freContext.dispatchStatusEventAsync("LoadFinished", uuid);
                } else {
                    freContext.dispatchStatusEventAsync("LoadError", uuid);
                }
            }
        };
        loaders.put(uuid, task);
        task.execute();
        return uuid;
    }

    public ByteBuffer getBytesByUUID(String uuid){
        ByteBuffer byteBuffer = results.get(uuid);
        results.remove(uuid);
        return byteBuffer;
    }

    public void stopAll(){
        for(AsyncTask task : loaders.values()){
            stopTask(task);
        }
        loaders.clear();
    }

    public void stopLoaderByUUID(String uuid){
        AsyncTask task = loaders.get(uuid);
        if(task != null){
            stopTask(task);
            loaders.remove(uuid);
        }
    }

    private void stopTask(AsyncTask task){
        if(AsyncTask.Status.PENDING.equals(task.getStatus()) || AsyncTask.Status.RUNNING.equals(task.getStatus())){
            task.cancel(true);
        }
    }
}
