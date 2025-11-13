import { registerWebModule, NativeModule } from 'expo';

import { ReactNativeBackgroundCallModuleEvents } from './ReactNativeBackgroundCall.types';

class ReactNativeBackgroundCallModule extends NativeModule<ReactNativeBackgroundCallModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(ReactNativeBackgroundCallModule, 'ReactNativeBackgroundCallModule');
