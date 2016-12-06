# UsbListener
Android library for listening USB device attached and detached event when in host mode.


## Setup
1.  In root build.gradle:
  ```
  allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
  }
  ````

2.  In target module build.gradle
  ```
  dependencies {
    compile 'com.github.potterhsu:UsbListener:v0.9'
  }
  ```

## Usage
Following sample code shows how to initialize listener with permission request:
```java
private UsbListener usbListener;
usbListener = new UsbListener(this, true, new UsbListener.OnUsbListener() {
    @Override
    public void onAttached(UsbDevice device) {
        Log.d(TAG, "onAttached: " + device);
    }

    @Override
    public void onDetached(UsbDevice device) {
        Log.d(TAG, "onDetached: ");
    }
});
```

Don't forget to dispose it when no longer need:
```java
usbListener.dispose();
```
