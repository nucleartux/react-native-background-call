# react-native-background-call

If you’re making video calls (with services like LiveKit), you probably need to keep calls running in the background.

For iOS, there’s a wonderful library called react-native-callkeep.

In theory, it should also work on Android, but for some reason, it doesn’t. There are other libraries like Notifee, but they didn’t work for me either.

That’s why this library is currently Android-only (though that might change soon).

## Installation

```bash
npm install --save react-native-background-call
# or
yarn add react-native-background-call

# If you are using Expo prebuild
run:npx expo prebuild --clean
```

## Usage

There are only two functions: `startForegroundService()` and `stopForegroundService()`. You need to call `startForegroundService()` when your call begins — it will keep the call active in the background. Then, call `stopForegroundService()` when your call finishes.

You can do this using useEffect, or any other approach that suits you better.

Note: You should call `startForegroundService()` only after the user has granted microphone access; otherwise, the function won’t do anything. If the user grants camera permission later, you’ll need to call `startForegroundService()` again.

```javascript
import BackgroundCall from "react-native-background-call";

useEffect(() => {
  BackgroundCall.startForegroundService({
    title: "Video Call",
    message: "Call in progress",
  });

  return () => {
    BackgroundCall.stopForegroundService();
  };
}, []);
```

That's it.
