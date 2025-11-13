import * as React from 'react';

import { ReactNativeBackgroundCallViewProps } from './ReactNativeBackgroundCall.types';

export default function ReactNativeBackgroundCallView(props: ReactNativeBackgroundCallViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
