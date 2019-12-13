
# react-native-xf-speech
<del>请忽略本文工地散装英语:cry:</del></br>
我只是一个会点`rn`的前端，在安卓开发和ios开发上属于编写边学程度，所以可能会有很多bug，请酌情使用
# 使用前必读(before use) :warning: :warning: :warning: :warning:
## for android
1. 云知音官方`SDK`我只发现了32位的，并没有文档里所说的64位的，联系他们也联系不上，有64位的可以联系我。There are only 32-bit SDK
2. 由于只是用了32位的`SDK`所以你的项目打包不应该包含64位的，所以你需要修改`项目根目录/android/app/build.greadle`。You should edit the file：`project_root_dir/android/app/build.greadle` like flow to avoid mixed 32bit and 64bit sdks
3. 由于我不是很懂原生开发，有可能下面的并不是最佳配置，你可以使用自己的配置，并告知我</br>
Because I'm good at native development，you can use your own configuration and tell me
```gradle
	......
	......
    splits {
        abi {
            reset()
			// set enableSeparateBuildPerCPUArchitecture = true
			// 将enableSeparateBuildPerCPUArchitecture设置为true
            enable enableSeparateBuildPerCPUArchitecture
            universalApk false
			// delete others and "armeabi-v7a" only 
			// 删除其他的 只保留64位的这个
            include "armeabi-v7a"
        }
    }
	.....
	.....
	applicationVariants.all { variant ->
        variant.outputs.all { output ->
            def versionCodes = ["armeabi-v7a": 1, "x86": 2, "arm64-v8a": 3]
            def abi = output.getFilter(OutputFile.ABI)
            if (abi != null) {  
				universal-release variants
                output.versionCodeOverride =
                        versionCodes.get(abi) * 1048576 + defaultConfig.versionCode
            }

			// rename apk，
			// 这一段是为了将分离打包的文件重新命名 不然使用
			// react-native run-android 会出错 因为它只认识app-debug
            if (output.outputFile != null && output.outputFile.name.endsWith('.apk')) {
                File outputDirectory = new File(output.outputFile.parent);
                def fileName
                if (variant.buildType.name == "release") {
					// release name
                    outputFileName = "app-release.apk"
                } else {
					// debug name
                    outputFileName = "app-debug.apk"
                }
            }
        }
    }
```
3. 如果你有其他的库只能在64位上运行，你只能转用其他的库，这个库也就免费这一点还不错。
## for IOS
1. TODO


## Getting started

`$ npm install react-native-xf-speech --save` or
`$ yarn add react-native-xf-speech`

### Mostly automatic installation

`$ react-native link react-native-xf-speech`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-xf-speech` and add `RNXfSpeech.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNXfSpeech.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.xh.speech.RNXfSpeechPackage;` to the imports at the top of the file
  - Add `new RNXfSpeechPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-xf-speech'
  	project(':react-native-xf-speech').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-xf-speech/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-xf-speech')
  	```


## Usage
### :robot: FOR ANDROID ：
1. 在`你的项目根目录/android/app/src/main/`下面创建目录`assets`</br>
create a folder `assets` in `project_root_dir/android/app/src/main/` 
2. <span id="move-model"></span>移动仓库中[model文件夹](./model)两个文件（`backend_lzl`和`frontend_model`）至`assets`</br>
move files in [this_repository/model](./model) to `assets` you made
3. 添加权限 add permission</br>
在`项目根目录/android/app/src/main/AndroidManifest.xml`中添添加</br>
add follow code in `project_root_dir/android/app/src/main/AndroidManifest.xml`
```xml
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
```
请注意`READ_PHONE_STATE`这个权限在高版本安卓中需要动态获取，虽然此项目会替你申请，但是你最好还是自己在合适的地方申请权限</br>
you should ask the permission `READ_PHONE_STATE` by your self in the right place although we did this

### API:
<a href="#initEngine">1. initEngine() 初始化</a></br>
<a href="#setInitEngineListener">2. setInitEngineListener() 设置初始化监听</a></br>
<a href="#playText">3. playText() 朗读文字</a></br>
<a href="#stopPlay">4. stopPlay() 停止朗读</a></br>
<a href="#utils">5. utils 辅助函数</a></br>
&emsp;<a href="#getVolume">5.1 getVolume() 获取当前系统音量</a></br>
&emsp;<a href="#getMaxVolume">5.2 getMaxVolume() 获取当前系统最大音量</a></br>
&emsp;<a href="#setVolume">5.3 setVolume() 设置音量</a></br>

---

<h2 id="initEngine">initEngine</h2>

```typescript
async function initEngine(appkey:string, secret: string, options?: Options): Promise<void>
```
options 可选Optional
```typescript
	// 音频输出流类型
	// audio output stream type
    streamType: STREAM_TYPE,
	// 0 ~ 100 不是系统音量 是合成的语音音量 感觉没什么效果 默认100
	// not system volume, is speech synthesis volume, defualt 100
    volume: number, 
	//  0 ~ 100 语音音调 不建议调 其他的音调都很怪 就是变声器那种哈哈哈哈哈 默认50
	// defualt 50
    pitch: number,
	//  0 ~ 100 语速 默认52
	// defualt 52
    speed: number,
```
STREAM_TYPE
```typescript
// 我也没搞清楚区别 详情可以百度`安卓audio stream type`默认MUSIC
// 大概区别就是`MUSIC`对应媒体音量 其他的基本上就是铃声，闹钟，通知
// 设置这个的目的是有的输出流声音比较小，还有插耳机和不插耳机的区别，但是我没有测试过插耳机的情况
// 请自己测试
enum STREAM_TYPE {
	// 系统默认
    SYSTEM,
	// 铃声
    RING,
	// 媒体
    MUSIC,
	// 闹钟
    ALARM,
	// 通知
    NOTIFICATION
}
```
	
```javascript
import tts from 'react-native-xf-speech';
// initial
const options = {
					streamType: tts.STREAM_TYPE.MUSIC,
					volume: 100,
					pitch: 50,
					speed: 52
				}
await tts.initEngine("app_key","app_secret", options)
```
这一步非常重要,所有的操作都应该在初始化之后，除了初始化监听函数</br>
`app_key` 和`app_secret`随便填好像都可以用，如果提示错误，请移步云知音官网自己申请。</br>
Operation must be performed after initialization, except listener function</br>
you can use any strings apply to `app_key` and `app_secret`,if there are any problems you need to get key and secret by yourself [云知音hompage](https://dev.hivoice.cn/)
> 申请注意</br>
> 1. 申请通用解决方案，同时勾选离线语音合成
> 2. 下载完成后将`USCDemo\libs\armeabi`中的文件替换掉本仓库`android/libs`中的文件
> 3. 将下载完成的sdk`USCDemo\assets\OfflineTTSModels`中的文件复制替换掉上面前面创建的`assets`文件夹中的文件

:warning:其他注意</br>
>1. 初始化需要加载模型，需要将`assets`的文件复制到存储中，所以需要几秒的时间，期间会导致`app`卡顿（因为没有用单独的线程加载），所以建议使用监听函数做相应的提示<br/>
> because there are no separate threads, when excuse the `initEngine` function may cause application to jam. you should use initial listener
>2. 再次提醒，一定要在初始化之后进行其他操作，不然会报错
---
<h2 id="setInitEngineListener">setInitEngineListener</h2>

```typescript
function setInitEngineListener(callback: (event: InitEvent) => any): void
```
InitEvent 事件参数
```typescript
{
	// 初始化大部分工作就是复制文件至内存 只会做一次
	// Most of the initialization work is to copy files to memory, only once
	// 文件复制进度
	progress: number, 
	// 文件个数
	fileCount: number, 
	// 初始化状态
    initState: INIT_ENGINE_STATE
}
```
INIT_ENGINE_STATE  初始化状态
```typescript
enum INIT_ENGINE_STATE {
	// 刚开始 萨摩耶没做
	START,
	// 正在初始化
	PROGRESS,
	// 完成
	FINISH,
	// 文件未找到 请检查model是否复制正确
	SDK_NOT_FOUND_ERROR,
	// 文件操作失败 检查存储空间或其他错误
    SDK_COPY_ERROR
}
```

初始化带监听
```javascript
	async function init() {
		// 注册监听函数一定要在initEngine之前 不然没有效果
		// must set listener before initEngine
		tts.setInitEngineListener(({progress, fileCount, initState}) => {
				console.log(`正在复制第${fileCount}个文件`, `当前进度${progress}`)
			   if(initState == tts.INIT_ENGINE_STATE.FINISH) {
				   
			   }else {
   
			   }
		   })
	   await tts.initEngine("123","123")
	}
```
---
<h2 id="playText">playText 朗读文字</h2>

```typescript
function playText(text: string): void
```
text 需要朗读的文本
```javascript
	tts.playText(`啊啊啊啊嗯嗯嗯啊啊啊啊啊嗯啊啊啊啊`)
```
---
<h2 id="stopPlay">stopPlay 停止朗读</h2>

```typescript
function stopPlay(): void
```
---
<h2 id="release">release 释放资源</h2>

```typescript
function release(): void
```
释放后需要重新初始化

---
<h2 id="utils">utils 辅助函数</h2>
<div id="getVolume">utils.getVolume</div>
<div>获取当前音量 和你初始化的输出流类型有关</div>

```typescript
function getVolume(): Promise<number>
```
<div id="getMaxVolume">utils.getMaxVolume</div>
<div>获取当前输出流最大音量</div>

```typescript
function getMaxVolume(): Promise<number>
```
<div id="setVolume">utils.setVolume</div>
<div>设置音量</div>

```typescript
function setVolume(value: number, flag: VOLUME_FLAGS): void
```
value 设定值

flag 设定音量的一些反馈
```typescript
enum VOLUME_FLAGS {
	// 输出流是ring时有效
	ALLOW_RINGER_MODES,
	// 输出流是铃声之类的才有效
	PLAY_SOUND,
	// 不知道什么意思
	REMOVE_SOUND_AND_VIBRATE,
	// 展示音量ui
	SHOW_UI,
	// 震动 有的输出流不生效
    VIBRATE
}
```

```javascript
	async function setAndShowUI() => {
		const volume = await tts.utils.getVolume()
		tts.utils.setVolume(volume, tts.VOLUME_FLAGS.SHOW_UI)
	}
```


### :apple: FOR IOS:
TODO....

  