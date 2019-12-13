package com.xh.speech;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    /**
     * assets 赋值文件
     * @param assetsManager
     * @param source 源地址
     * @param dest 目标地址
     * @param isCover 是否需要覆盖
     * @throws IOException
     */
    public static void copyFromAssets(AssetManager assetsManager,
                                      String source, String dest, boolean isCover, FileCopyProgress progressCallback)
    throws IOException {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assetsManager.open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                int totalSize = is.available();
                int hasDone = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                    hasDone += size;
                    progressCallback.onProgress((double)hasDone / (double)totalSize, source);
//                    Log.i("语音", (double)hasDone / (double)totalSize+"");
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }
    }

}
