// Reexport the native module. On web, it will be resolved to ReactNativeBackgroundCallModule.web.ts
// and on native platforms to ReactNativeBackgroundCallModule.ts
export { default } from './ReactNativeBackgroundCallModule';
export { default as ReactNativeBackgroundCallView } from './ReactNativeBackgroundCallView';
export * from  './ReactNativeBackgroundCall.types';
