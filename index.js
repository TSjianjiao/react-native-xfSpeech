
// @ts-check

import { NativeModules, NativeEventEmitter } from 'react-native';
import {
    PermissionsAndroid
} from 'react-native'

const { RNXfSpeech } = NativeModules;
const eventEmitter = new NativeEventEmitter(RNXfSpeech)

/**
 * 初始化状态码
 */
const INIT_ENGINE_STATE = {
    START: 0,
    PROGRESS: 1,
    FINISH: 4,
    SDK_NOT_FOUND_ERROR: 2,
    SDK_COPY_ERROR: 3,
}

/**
 * 音频流类型
 */
const STREAM_TYPE = {
    SYSTEM: RNXfSpeech.SYSTEM,
    RING: RNXfSpeech.RING,
    MUSIC: RNXfSpeech.MUSIC,
    ALARM: RNXfSpeech.ALARM,
    NOTIFICATION: RNXfSpeech.NOTIFICATION
}

/**
 * 音量调节flags（附带效果）
 */
const VOLUME_FLAGS = {
    // 音量设置为0时 铃声设置会变成震动
    ALLOW_RINGER_MODES: RNXfSpeech.ALLOW_RINGER_MODES,
    // 改变音量的时候播放声音
    PLAY_SOUND: RNXfSpeech.PLAY_SOUND,
    REMOVE_SOUND_AND_VIBRATE: RNXfSpeech.REMOVE_SOUND_AND_VIBRATE,
    SHOW_UI: RNXfSpeech.SHOW_UI,
    VIBRATE: RNXfSpeech.VIBRATE
}

const defualtOptions = {
    streamType: STREAM_TYPE.MUSIC,
    volume: 100,
    pitch: 50,
    speed: 52,
}
/**
 * 初始化引擎
 */
async function initEngine(appkey, secret, options) {
    // 获取权限
    await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE)
    RNXfSpeech.initSpeech(appkey, secret, {...defualtOptions, ...options})
}

/**
 * 防止重复注册事件
 * @param {string} eventType
 */
function preventDuplication(eventType) {
    if(eventEmitter.listeners(eventType).length > 0) {
        eventEmitter.removeAllListeners(eventType)
    }
}

/**
 * 初始引擎进度监听
 */
function setInitEngineListener(callback) {
    preventDuplication("InitEngine")
    eventEmitter.addListener('InitEngine', callback)
}

/**
 * 开始播放事件
 */
function setPlayStartListener(callback) {
    preventDuplication("playingStart")
    eventEmitter.addListener('playingStart', callback)
}

/**
 * 播放结束事件
 */
function setPlayEndListener(callback) {
    preventDuplication("playingEnd")
    eventEmitter.addListener('playingEnd', callback)
}

/**
 * 暂停事件
 */
function setPauseListener(callback) {
    preventDuplication("pause")
    eventEmitter.addListener('pause', callback)
}

/**
 * 停止事件
 */
function setStopListener(callback) {
    preventDuplication("stop")
    eventEmitter.addListener('stop', callback)
}

/**
 * 恢复事件
 */
function setResumeListener(callback) {
    preventDuplication("resume")
    eventEmitter.addListener('resume', callback)
}

/**
 * 释放事件
 */
function setReleaseListener(callback) {
    preventDuplication("release")
    eventEmitter.addListener('release', callback)
}

/**
 * 合成错误事件
 */
function setErrorListener(callback) {
    preventDuplication("error")
    eventEmitter.addListener('error', callback)
}


/**
 * 语音合成
 * @param {string} text 
 */
function playText(text) {
    RNXfSpeech.playText(text)
}

/**
 * 停止语音播放
 */
function stopPlay() {
    RNXfSpeech.stopPlay()
}

/**
 * 暂停
 */
function pause() {
    RNXfSpeech.pause()
}

/**
 * 恢复
 */
function resume() {
    RNXfSpeech.resume()
}

/**
 * 释放资源
 */
function release() {
    RNXfSpeech.release()
}

/**
 * 获取当前音量
 */
function getVolume() {
    return new Promise((resolve, reject) => {
        RNXfSpeech.getVolume(volume => {
            resolve(volume)
        })
    })
}

/**
 * 获取最高音量
 */
function getMaxVolume() {
    return new Promise((resolve, reject) => {
        RNXfSpeech.getMaxVolume(volume => {
            resolve(volume)
        })
    })
}

/**
 * 设置音量
 * @param {number} value 音量值
 * @param {number} flag 设置音量的效果
 */
function setVolume(value, flag = 0) {
    RNXfSpeech.setVolume(value, flag)
}

export default {
    initEngine,
    setInitEngineListener,
    setPlayStartListener,
    setPlayEndListener,
    setPauseListener,
    setStopListener,
    setResumeListener,
    setReleaseListener,
    setErrorListener,
    playText,
    stopPlay,
    pause,
    resume,
    release,
    utils: {
        getVolume,
        getMaxVolume,
        setVolume
    },
    INIT_ENGINE_STATE,
    STREAM_TYPE,
    VOLUME_FLAGS
}

