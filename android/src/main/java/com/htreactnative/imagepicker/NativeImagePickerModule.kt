package com.htreactnative.imagepicker

import android.graphics.BitmapFactory
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.module.annotations.ReactModule
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.SdkVersionUtils
import java.io.File
import java.util.ArrayList


@ReactModule(name = NativeImagePickerModule.NAME)
class NativeImagePickerModule(reactContext: ReactApplicationContext) :
  NativeImagePickerSpec(reactContext) {
    val imageEngine = GlideEngine.createGlideEngine()
    var pickerOptions: ReadableMap? = null
  override fun getName(): String {
    return NAME
  }

    override fun asyncShowImagePicker(
        options: ReadableMap?,
        promise: Promise?
    ) {
        pickerOptions = options;
        PictureSelector.create(reactApplicationContext.currentActivity)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(imageEngine)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(localMediaList: ArrayList<LocalMedia?>?) {
                    var data = WritableNativeArray()
                    localMediaList?.forEach { media -> media?.let {
                        data.pushMap(getImageResult(media))
                    } }
                    promise?.resolve(data)
                }

                override fun onCancel() {

                }

            })
    }

    private fun getImageResult(media: LocalMedia): WritableMap {
        val imageMap: WritableMap = WritableNativeMap()
        var path: String = media.path

        if (media.isCompressed || media.isCut) {
            path = media.compressPath
        }

        if (media.isCut) {
            path = media.cutPath
        }

        val isAndroidQ = SdkVersionUtils.isQ()
        val isAndroidR = SdkVersionUtils.isR()
        if (isAndroidQ) {
            path = media.availablePath
        }
        if (isAndroidR) {
            path = media.realPath;
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        imageMap.putDouble("width", options.outWidth.toDouble())
        imageMap.putDouble("height", options.outHeight.toDouble())
        imageMap.putString("type", "image")
        imageMap.putString("path", path)
        imageMap.putString("uri", "file://$path")
        imageMap.putString("original_uri", "file://" + media.path)
        imageMap.putInt("size", File(path).length().toInt())


        return imageMap
    }
    companion object {
    const val NAME = "NativeImagePicker"
  }
}
