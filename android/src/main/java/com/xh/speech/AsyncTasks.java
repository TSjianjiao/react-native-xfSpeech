package com.xh.speech;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.content.Context;
import android.telecom.Call;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

public class AsyncTasks {
    static class copyModels extends AsyncTask<Void, Void, Boolean> {
        private ReactContext reactContext;
        private Callback callback;
        private Models[] modelList;
        private AssetManager assetManager;

        copyModels(ReactContext reactContext, Models[] modelList, Callback callback) {
            this.reactContext = reactContext;
            this.modelList = modelList;
            this.callback = callback;
            this.assetManager = reactContext.getAssets();
        }

        interface Callback {
            void onCopyModelComplete();
        }

        private void sendEvent(ReactContext reactContext,
                               String eventName,
                               @Nullable WritableMap params) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            FileCopyProgress processCallback = new FileCopyProgress() {
                private String lastProgress = "";
                private int fileCount = 0;
                @Override
                public void onProgress(Double progress, String filePath) {
                    String progressStr = String.format("%.2f", progress);

                    if(!lastProgress.equals(progressStr)) {
                        WritableMap params_2 = Arguments.createMap();
                        params_2.putDouble("progress", Double.parseDouble(progressStr));
                        params_2.putInt("fileCount", fileCount);
                        params_2.putInt("initState", 1);
                        sendEvent(reactContext, "InitEngine", params_2);
                        lastProgress = progressStr;
                    }
                }
                @Override
                public void setFileCount(int fileCount) {
                    this.fileCount = fileCount;
                }
            };

            for(int i = 0; i < modelList.length; i++) {
                Models model = modelList[i];
                WritableMap params_3 = Arguments.createMap();
                try {
                    assetManager.open(model.source);
                    processCallback.setFileCount(i + 1);
                    FileUtils.copyFromAssets(assetManager, model.source, model.dest, model.isOver, processCallback);
                }catch (FileNotFoundException err) {
                    params_3.putDouble("progress", Double.parseDouble("0"));
                    params_3.putInt("fileCount", i + 1);
                    params_3.putInt("initState", 2);
                    sendEvent(reactContext, "InitEngine", params_3);
                    return false;
                }catch (IOException err) {
                    err.printStackTrace();
                    params_3.putDouble("progress", Double.parseDouble("0"));
                    params_3.putInt("fileCount", i + 1);
                    params_3.putInt("initState", 3);
                    sendEvent(reactContext, "InitEngine", params_3);
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) callback.onCopyModelComplete();
        }
    }
}
