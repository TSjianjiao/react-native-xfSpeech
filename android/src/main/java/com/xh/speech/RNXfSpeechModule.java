
package com.xh.speech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.Nullable;

import android.content.res.Resources;
import android.media.AudioManager;
import android.telecom.Call;
import android.widget.Toast;
import android.util.Log;
import android.content.res.AssetManager;
import android.content.Context;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

public class RNXfSpeechModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

  private final ReactApplicationContext reactContext;
  private SpeechSynthesizer mTTSPlayer;
  private String mFrontendModel;
  private String mBackendModel;
  private AudioManager audioManager;
  private ReadableMap mOptions;

  public RNXfSpeechModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.mFrontendModel = reactContext.getExternalFilesDir("tts").toString() + "/frontend_model";
    this.mBackendModel = reactContext.getExternalFilesDir("tts").toString() + "/backend_lzl";
    this.audioManager = (AudioManager) reactContext.getSystemService(reactContext.AUDIO_SERVICE);
  }

  @Override
  public String getName() {
    return "RNXfSpeech";
  }

  private void log_i(String log) {
    Log.i("语音朗读", log);
  }

  private void toastMessage(String message) {
    Toast.makeText(this.reactContext, message, Toast.LENGTH_SHORT).show();
  }

  /**
   * 发送事件到js
   * @param reactContext
   * @param eventName
   * @param params
   */
  private void sendEvent(ReactContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }

  /**
   * 供js使用的常量
   * @return constants 常量map
   */
  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("SYSTEM", AudioManager.STREAM_SYSTEM);
    constants.put("RING", AudioManager.STREAM_RING);
    constants.put("MUSIC", AudioManager.STREAM_MUSIC);
    constants.put("ALARM", AudioManager.STREAM_ALARM);
    constants.put("NOTIFICATION", AudioManager.STREAM_NOTIFICATION);

    constants.put("ALLOW_RINGER_MODES", AudioManager.FLAG_ALLOW_RINGER_MODES);
    constants.put("PLAY_SOUND", AudioManager.FLAG_PLAY_SOUND);
    constants.put("REMOVE_SOUND_AND_VIBRATE", AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    constants.put("SHOW_UI", AudioManager.FLAG_SHOW_UI);
    constants.put("VIBRATE", AudioManager.FLAG_VIBRATE);
    return constants;
  }

  @Override
  public void onHostPause() {
    Log.e(getName(), "onHostPause");
  }

  @Override
  public void onHostResume() {
    Log.e(getName(), "onHostResume");
  }

  @Override
  public void onHostDestroy() {
    release();
  }

  /**
   * 初始化引擎
   */
  @ReactMethod
  public void initSpeech(String appKey, String secret, @Nullable ReadableMap options) {
    mOptions = options;
    release();

    // 初始化语音合成对象
    mTTSPlayer = new SpeechSynthesizer(this.reactContext, appKey, secret);
    // 设置本地合成
    mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
    // 设置输出流
    mTTSPlayer.setOption(SpeechConstants.TTS_KEY_STREAM_TYPE, options.getInt("streamType"));
    //音调
    mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_PITCH, options.getInt("pitch"));
    //语速
    mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_SPEED, options.getInt("speed"));
    // 音量
    mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_VOLUME, options.getInt("volume"));

    WritableMap params_1 = Arguments.createMap();
    params_1.putDouble("progress", Double.parseDouble("0"));
    params_1.putInt("fileCount", 0);
    params_1.putInt("initState", 0);
    sendEvent(reactContext, "InitEngine", params_1);

    // TODO 读取文件列表 动态获取长度
    Models list[] = new Models[2];
    list[0] = new Models("tts/backend_lzl", mBackendModel, false);
    list[1] = new Models("tts/frontend_model", mFrontendModel, false);
    AsyncTasks.copyModels task = new AsyncTasks.copyModels(reactContext, list, new AsyncTasks.copyModels.Callback() {
        @Override
        public void onCopyModelComplete() {
            // 设置前端模型
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, mFrontendModel);
            // 设置后端模型
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, mBackendModel);
            // 设置回调监听
            mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {

                @Override
                public void onEvent(int type) {
                    switch (type) {
                        case SpeechConstants.TTS_EVENT_INIT:
                            // 初始化成功回调！！！
                            WritableMap params = Arguments.createMap();
                            params.putDouble("progress", Double.parseDouble("0"));
                            params.putInt("fileCount", 2);
                            params.putInt("initState", 4);
                            sendEvent(reactContext, "InitEngine", params);
                            log_i("onInitFinish");
                            break;
                        case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                            // 开始合成回调
                            log_i("beginSynthesizer");
                            break;
                        case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                            // 合成结束回调
                            log_i("endSynthesizer");
                            break;
                        case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                            // 开始缓存回调
                            log_i("beginBuffer");
                            break;
                        case SpeechConstants.TTS_EVENT_BUFFER_READY:
                            // 缓存完毕回调
                            log_i("bufferReady");
                            break;
                        case SpeechConstants.TTS_EVENT_PLAYING_START:
                            // 开始播放回调
                            sendEvent(reactContext, "playingStart", Arguments.createMap());
                            log_i("onPlayBegin");
                            break;
                        case SpeechConstants.TTS_EVENT_PLAYING_END:
                            // 播放完成回调
                            sendEvent(reactContext, "playingEnd", Arguments.createMap());
                            log_i("onPlayEnd");
                            break;
                        case SpeechConstants.TTS_EVENT_PAUSE:
                            sendEvent(reactContext, "pause", Arguments.createMap());
                            // 暂停回调
                            log_i("pause");
                            break;
                        case SpeechConstants.TTS_EVENT_RESUME:
                            sendEvent(reactContext, "resume", Arguments.createMap());
                            // 恢复回调
                            log_i("resume");
                            break;
                        case SpeechConstants.TTS_EVENT_STOP:
                            sendEvent(reactContext, "stop", Arguments.createMap());
                            // 停止回调
                            log_i("stop");
                            break;
                        case SpeechConstants.TTS_EVENT_RELEASE:
                            sendEvent(reactContext, "release", Arguments.createMap());
                            // 释放资源回调
                            log_i("release");
                            break;
                        default:
                            break;
                    }

                }

                @Override
                public void onError(int type, String errorMSG) {
                    WritableMap error = Arguments.createMap();
                    error.putString("msg", errorMSG);
                    sendEvent(reactContext, "onError", error);
                    // 语音合成错误回调
                    log_i("onError");
//        toastMessage(errorMSG);
                }
            });
            // 初始化合成引擎
            mTTSPlayer.init("");
        }
    });
    task.execute();
  }

  /**
   * 语音合成
   * @param text
   */
  @ReactMethod
  public void playText(String text) {
    if(mTTSPlayer == null) return;
    mTTSPlayer.playText(text);
  }

  /**
   * 暂停播放
   */
  @ReactMethod
  public void pause() {
    if(mTTSPlayer == null) return;
    mTTSPlayer.pause();
  }

  /**
   * 恢复播放
   */
  @ReactMethod
  public void resume() {
    if(mTTSPlayer == null) return;
    mTTSPlayer.resume();
  }

  /**
   * 停止语音
   */
  @ReactMethod
  public void stopPlay() {
    if(mTTSPlayer == null) return;
    mTTSPlayer.stop();
  }

  /**
   * 释放资源
   */
  @ReactMethod
  public void release() {
    // 释放资源
    if (mTTSPlayer != null) {
      mTTSPlayer.stop();
      mTTSPlayer.release(SpeechConstants.TTS_RELEASE_ENGINE, null);
    }
  }

  /////////////////////////////
  /// 辅助函数
  /// 音量操作相关
  /////////////////////////////
  /**
   * 获取当前音量
   */
  @ReactMethod
  public void getVolume(Callback cb) {
    if(audioManager == null || mTTSPlayer == null || mOptions == null) {
      cb.invoke(0);
    }
    int currentStreamType = mOptions.getInt("streamType");
    cb.invoke(audioManager.getStreamVolume(currentStreamType));
  }

  /**
   * 获取最高音量
   */
  @ReactMethod
  public void getMaxVolume(Callback cb) {
    if(audioManager == null || mTTSPlayer == null || mOptions == null) {
      cb.invoke(0);
    }
    int currentStreamType = mOptions.getInt("streamType");
    cb.invoke(audioManager.getStreamMaxVolume(currentStreamType));
  }

  /**
   * 设置音量
   */
  @ReactMethod
  public void setVolume(int value, int flag) {
    if(audioManager != null && mTTSPlayer != null && mOptions != null) {
      audioManager.setStreamVolume(mOptions.getInt("streamType"), value, flag);
    }
  }

}