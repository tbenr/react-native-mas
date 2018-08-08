
# react-native-mas

An experimental React Native module to use CA Technologies MAS SDK.

## Getting started

`$ npm install "git+https://github.com/tbenr/react-native-mas.git" --save`

### Mostly automatic installation

`$ react-native link react-native-mas`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-mas` and add `RNMas.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNMas.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNMasPackage;` to the imports at the top of the file
  - Add `new RNMasPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-mas'
  	project(':react-native-mas').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-mas/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-mas')
  	```


## Usage
```javascript
import RNMas from 'react-native-mas';

// TODO: What to do with the module?
RNMas;
```

## TODO

- Integrate iOS SDK in the module.
- Include more APIs.
