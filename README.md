
# react-native-xf-speech

## Getting started

`$ npm install react-native-xf-speech --save`

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
```javascript
import RNXfSpeech from 'react-native-xf-speech';

// TODO: What to do with the module?
RNXfSpeech;
```
  