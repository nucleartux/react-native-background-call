package expo.modules.backgroundcall

import android.Manifest
import android.content.Context
import android.content.Intent
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import android.os.Build
import android.util.Log
import androidx.core.content.PermissionChecker

class ReactNativeBackgroundCallModule : Module() {
  private val context: Context
    get() = appContext.reactContext ?: throw IllegalStateException("React context not available")


  // Each module class must implement the definition function. The definition consists of components
  // that describes the module's functionality and behavior.
  // See https://docs.expo.dev/modules/module-api for more details about available components.
  override fun definition() = ModuleDefinition {
    // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
    // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
    // The module will be accessible from `requireNativeModule('ReactNativeBackgroundCall')` in JavaScript.
    Name("ReactNativeBackgroundCall")

    Function("startForegroundService") { callInfo: Map<String, Any?> ->
      val cameraPermission =
        PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA)
      val microphonePermission =
        PermissionChecker.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
       if ((cameraPermission != PermissionChecker.PERMISSION_GRANTED) && (microphonePermission != PermissionChecker.PERMISSION_GRANTED)) {
         Log.w("ReactNativeBackgroundCall", "CAMERA or RECORD_AUDIO permissions not granted")
         return@Function false
       }
     } else {
       if (cameraPermission != PermissionChecker.PERMISSION_GRANTED || microphonePermission != PermissionChecker.PERMISSION_GRANTED) {
         Log.w("ReactNativeBackgroundCall", "CAMERA and RECORD_AUDIO permissions not granted")
         return@Function false
       }
     }
      try {
        val intent = Intent(context, CallForegroundService::class.java).apply {
          action = CallForegroundService.ACTION_START
          putExtra("title", callInfo["title"] as? String ?: "Video Call")
          putExtra("message", callInfo["message"] as? String ?: "Call in progress")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

          context.startForegroundService(intent)
        } else {
          context.startService(intent)
        }
        true
      } catch (e: Exception) {
        e.printStackTrace()
        false
      }
    }

    Function("stopForegroundService") {
      try {
        val intent = Intent(context, CallForegroundService::class.java).apply {
          action = CallForegroundService.ACTION_STOP
        }
        context.startService(intent)
        true
      } catch (e: Exception) {
        e.printStackTrace()
        false
      }
    }
  }
}
