package com.xh.speech;

public interface FileCopyProgress {
    void onProgress(Double progress, String filePath);
    void setFileCount(int fileCount);
}
