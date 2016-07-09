package com.mck.localweathernow.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.mck.localweathernow.service.OpenWeatherMapService;

import java.io.File;

/**
 *
 * Created by Michael on 7/7/2016.
 */
public class GetWeatherIconAsyncTask extends AsyncTask<Object,Integer,Bitmap> {
    private static final String TAG = "GetWeatherIconAsyncTask";

    private Context context;
    private Callback callback;
    private int reqId;
    private String iconId;

    public GetWeatherIconAsyncTask(Context context, Callback callback, int requestId, String iconId){
        this.context = context;
        this.callback = callback;
        this.reqId = requestId;
        this.iconId = iconId;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        if (context == null) return null;
        File file = getFile(context, iconId);
        if (!file.exists()){
            Log.v(TAG, "Icon file does not exist, returning null.");
            return null;
        }
        Log.v(TAG, "Icon file exists, getting and returning bitmap.");
        return BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/icon" + iconId + ".png" );
    }

    private static synchronized File getFile(Context context, String iconId) {
        File file = new File(context.getFilesDir(), "icon" + iconId + ".png");
        if (!file.exists()){
            Log.v(TAG, "Icon file does not exist, using OpenWeatherMapService to get and save.");
            OpenWeatherMapService.getAndSaveIconFromNetwork(context, iconId);
        }
        file = new File(context.getFilesDir(), "icon" + iconId + ".png");
        return file;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (!isCancelled()){
            callback.onWeatherIconResult(bitmap, reqId);
        }
    }

    public interface Callback {
        void onWeatherIconResult(Bitmap icon, Integer requestId);
    }
}
