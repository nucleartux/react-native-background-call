import { requireNativeView } from 'expo';
import * as React from 'react';

import { ReactNativeBackgroundCallViewProps } from './ReactNativeBackgroundCall.types';

const NativeView: React.ComponentType<ReactNativeBackgroundCallViewProps> =
  requireNativeView('ReactNativeBackgroundCall');

export default function ReactNativeBackgroundCallView(props: ReactNativeBackgroundCallViewProps) {
  return <NativeView {...props} />;
}
