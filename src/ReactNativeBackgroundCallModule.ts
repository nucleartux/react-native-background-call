import { NativeModule, requireNativeModule } from "expo";

declare class ReactNativeBackgroundCallModule extends NativeModule {
  startForegroundService(value: CallInfo): void;
  stopForegroundService(): void;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ReactNativeBackgroundCallModule>(
  "ReactNativeBackgroundCall"
);

export interface CallInfo {
  title: string;
  message: string;
}
