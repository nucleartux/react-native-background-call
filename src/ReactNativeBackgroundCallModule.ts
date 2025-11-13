import { NativeModule, requireNativeModule } from 'expo';

import { ReactNativeBackgroundCallModuleEvents } from './ReactNativeBackgroundCall.types';

declare class ReactNativeBackgroundCallModule extends NativeModule<ReactNativeBackgroundCallModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ReactNativeBackgroundCallModule>('ReactNativeBackgroundCall');
