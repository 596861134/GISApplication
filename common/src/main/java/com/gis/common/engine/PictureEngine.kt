package com.gis.common.engine

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gis.common.R
import com.gis.common.dialog.impl.WaitDialog
import com.gis.common.extension.*
import com.gis.common.manager.AppActivityManager
import com.gis.common.utils.ImageUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.luck.lib.camerax.SimpleCameraX
import com.luck.lib.camerax.listener.OnSimpleXPermissionDeniedListener
import com.luck.lib.camerax.listener.OnSimpleXPermissionDescriptionListener
import com.luck.lib.camerax.permissions.SimpleXPermissionUtil
import com.luck.picture.lib.basic.PictureSelectionCameraModel
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.*
import com.luck.picture.lib.dialog.RemindDialog
import com.luck.picture.lib.engine.*
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.permissions.PermissionConfig
import com.luck.picture.lib.permissions.PermissionResultCallback
import com.luck.picture.lib.permissions.PermissionUtil
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle
import com.luck.picture.lib.utils.*
import com.luck.picture.lib.widget.MediumBoldTextView
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import top.zibin.luban.CompressionPredicate
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import top.zibin.luban.OnNewCompressListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by chengzf on 2024/8/22.
 *
 * 相册选择工具
 *
 * 单选图片：
 * PictureEngine().create(this) {
 *         setResultMode(PictureEngine.ACTIVITY_RESULT)
 *             .setRequestCode(PictureEngine.REQUEST_COMPLAINT_CODE)
 *             .setDisplayCamera(true)
 *             .setMaxSelectNum(1)
 *             .selectFromGallery()
 *   }
 *
 *
 *  单选视频：
 *  PictureEngine().create(this) {
 *       setResultMode(PictureEngine.ACTIVITY_RESULT)
 *           .setRequestCode(PictureEngine.REQUEST_COMPLAINT_CODE)
 *           .setDisplayCamera(true)
 *           .setMaxSelectNum(1)
 *           .takePhoto()
 *   }
 *
 *
 *  仅拍照：
 *  PictureEngine().create(this) {
 *       setResultMode(PictureEngine.ACTIVITY_RESULT)
 *           .setRequestCode(PictureEngine.REQUEST_COMPLAINT_CODE)
 *           .setDisplayCamera(true)
 *           .setMaxSelectNum(1)
 *           .takePhoto()
 *   }
 *
 *
 *
 *
 *
 *
 * 回调用法:
 *
 * if (resultCode == RESULT_OK){
 *     if (requestCode == REQUEST_COMPLAINT_CODE) {
 *         PictureSelector.obtainSelectorList(data).forEach {
 *                 if (it.width == 0 || it.height == 0) {
 *                     if (PictureMimeType.isHasImage(it.mimeType)) {
 *                         val imageExtraInfo = MediaUtils.getImageSize(getContext(), it.path)
 *                         it.width = imageExtraInfo.width
 *                         it.height = imageExtraInfo.height
 *                     } else if (PictureMimeType.isHasVideo(it.mimeType)) {
 *                         val videoExtraInfo = MediaUtils.getVideoSize(getContext(), it.path)
 *                         it.width = videoExtraInfo.width
 *                         it.height = videoExtraInfo.height
 *                     }
 *                 }
 *                 ("文件名: " + it.fileName).log()
 *                 ("是否压缩:" + it.isCompressed).log()
 *                 ("压缩路径:" + it.compressPath).log()
 *                 ("初始路径:" + it.path).log()
 *                 ("绝对路径:" + it.realPath).log()
 *                 ("是否裁剪:" + it.isCut).log()
 *                 ("裁剪路径:" + it.cutPath).log()
 *                 ("是否开启原图:" + it.isOriginal).log()
 *                 ("原图路径:" + it.originalPath).log()
 *                 ("沙盒路径:" + it.sandboxPath).log()
 *                 ("水印路径:" + it.watermarkPath).log()
 *                 ("视频缩略图:" + it.videoThumbnailPath).log()
 *                 ("原始宽高: " + it.width + "x" + it.height).log()
 *                 ("裁剪宽高: " + it.cropImageWidth + "x" + it.cropImageHeight).log()
 *                 ("文件大小: " + PictureFileUtils.formatAccurateUnitFileSize(it.size)).log()
 *                 ("文件时长: " + it.duration).log()
 *             }
 *     }
 * }
 *
 */
class PictureEngine {

    companion object {

        // forResultActivity
        const val ACTIVITY_RESULT: Int = 1

        // callback
        const val CALLBACK_RESULT: Int = 2

        // launcherResult
        const val LAUNCHER_RESULT: Int = 3

        // 拍照
        const val REQUEST_CAMERA_CODE: Int = 1001

        // 选择图片
        const val REQUEST_SELECT_PHOTO_CODE: Int = 1002

        // 拍视频
        const val REQUEST_VIDEO_CODE: Int = 1003

        // 选择视频
        const val REQUEST_SELECT_VIDEO_CODE: Int = 1004

        /*private val mPictureEngine: PictureEngine by lazy { PictureEngine() }

        @JvmStatic
        fun getInstance(): PictureEngine {
            return mPictureEngine
        }*/

    }


    private val selectorStyle: PictureSelectorStyle = PictureSelectorStyle()


    private var aspect_ratio_x: Int = -1
    private var aspect_ratio_y: Int = -1

    // 是否线上拍照
    private var isDisplayCamera: Boolean = true

    // 是否支持裁剪
    private var isEnableCrop: Boolean = false

    // 是否开启圆形裁剪
    private var isEnableCircleCrop: Boolean = false

    // 是否开启图片编辑
    private var isEnableEditor: Boolean = false

    // 最大选择张数
    private var maxSelect: Int = 1

    // 回调模式
    private var resultMode: Int = ACTIVITY_RESULT

    // 模式1
    private var requestCode: Int = PictureConfig.CHOOSE_REQUEST

    // 模式2
    private lateinit var callback: OnResultCallbackListener<LocalMedia?>

    // 模式3
    private lateinit var launcher: ActivityResultLauncher<Intent>

    // 控制器
    private lateinit var mPictureSelector: PictureSelector

    private val TAG_EXPLAIN_VIEW: String = "TAG_EXPLAIN_VIEW"

    /**
     * Start PictureSelector for context.
     *
     * @param context
     * @return PictureSelector instance.
     */
    fun create(context: Context, onPermissionsGranted: (PictureEngine.() -> Unit)? = null) {
        XXPermissions.with(context).permission(
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    mPictureSelector = PictureSelector.create(context as Activity)
                    onPermissionsGranted?.invoke(this@PictureEngine)
                } else {
                    R.string.granted_faild.getResString().showToast()
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                R.string.granted_faild.getResString().showToast()
            }
        })
    }

    /**
     * Start PictureSelector for Activity.
     *
     * @param activity
     * @return PictureSelector instance.
     */
    fun create(
        activity: AppCompatActivity,
        onPermissionsGranted: (PictureEngine.() -> Unit)? = null
    ) {
        XXPermissions.with(activity).permission(
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    mPictureSelector = PictureSelector.create(activity)
                    onPermissionsGranted?.invoke(this@PictureEngine)
                } else {
                    R.string.granted_faild.getResString().showToast()
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                R.string.granted_faild.getResString().showToast()
            }
        })
    }

    /**
     * Start PictureSelector for Activity.
     *
     * @param activity
     * @return PictureSelector instance.
     */
    fun create(
        activity: FragmentActivity,
        onPermissionsGranted: (PictureEngine.() -> Unit)? = null
    ) {
        XXPermissions.with(activity).permission(
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    mPictureSelector = PictureSelector.create(activity)
                    onPermissionsGranted?.invoke(this@PictureEngine)
                } else {
                    R.string.granted_faild.getResString().showToast()
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                R.string.granted_faild.getResString().showToast()
            }
        })
    }

    /**
     * Start PictureSelector for Fragment.
     *
     * @param fragment
     * @return PictureSelector instance.
     */
    fun create(fragment: Fragment, onPermissionsGranted: (PictureEngine.() -> Unit)? = null) {
        XXPermissions.with(fragment).permission(
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (all) {
                    mPictureSelector = PictureSelector.create(fragment)
                    onPermissionsGranted?.invoke(this@PictureEngine)
                } else {
                    R.string.granted_faild.getResString().showToast()
                }
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                R.string.granted_faild.getResString().showToast()
            }
        })
    }


    /**
     * 回调模式
     * ACTIVITY_RESULT 必须传requestCode
     * CALLBACK_RESULT 必须传callback
     * LAUNCHER_RESULT 必须传launcher
     */
    fun setResultMode(model: Int) = also {
        resultMode = model
    }

    /**
     * 回调方式
     */
    fun setRequestCode(code: Int) = also {
        requestCode = code
    }

    /**
     * 回调方式
     */
    fun setCallBck(call: OnResultCallbackListener<LocalMedia?>) = also {
        callback = call
    }

    /**
     * 回调方式
     */
    fun setLauncher(launch: ActivityResultLauncher<Intent>) = also {
        launcher = launch
    }

    /**
     * 最大可选数量
     */
    fun setMaxSelectNum(max: Int = 1) = also {
        maxSelect = max
    }

    /**
     * 选择图片or视频时否可拍摄
     */
    fun setDisplayCamera(display: Boolean = true) = also {
        isDisplayCamera = display
    }

    /**
     * 是否开启裁剪
     */
    fun setEnableCrop(crop: Boolean = true) = also {
        isEnableCrop = crop
    }

    /**
     * 是否开启圆形裁剪
     */
    fun setEnableCircleCrop(crop: Boolean = true) = also {
        if (crop) setEnableCrop(true)
        isEnableCircleCrop = crop
    }

    /**
     * 是否开启裁编辑
     */
    fun setEnableEditor(crop: Boolean = true) = also {
        isEnableEditor = crop
    }

    /**
     * 从图库选择图片or视频
     */
    fun selectFromGallery(isCamera: Boolean = true) {
        if (mPictureSelector.isNull()) {
            throw IllegalStateException("PictureSelector can not be null")
        }

        val max = maxSelect
        val select = if (max > 1) SelectModeConfig.MULTIPLE else SelectModeConfig.SINGLE
        val selectionModel: PictureSelectionModel = mPictureSelector
            .openGallery(if (isCamera) SelectMimeType.ofImage() else SelectMimeType.ofVideo())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setMaxSelectNum(max)
            .setMaxVideoSelectNum(max)
            .setSelectorUIStyle(PictureSelectorStyle())
            .setEditMediaInterceptListener(getCustomEditMediaEvent())
            .isPreviewImage(true)
            .setCropEngine(getCropFileEngine())
            .isDisplayCamera(isDisplayCamera)
            .setCameraImageFormat(PictureMimeType.JPEG)
            .setSelectionMode(select) // 多选 or 单选
            .setCompressEngine(getCompressFileEngine()) // 是否压缩
            .setPermissionDescriptionListener(getPermissionDescriptionListener())
            .setPermissionDeniedListener(getPermissionDeniedListener())
        selectionModel.forResult(requestCode)
        when (resultMode) {
            ACTIVITY_RESULT -> forSelectResult(selectionModel, requestCode)
            CALLBACK_RESULT -> forSelectResult(selectionModel, callback)
            LAUNCHER_RESULT -> forSelectResult(selectionModel, launcher)
            else -> {}
        }
    }

    /**
     * 仅拍照
     */
    fun takePhoto() {
        if (mPictureSelector.isNull()) {
            throw IllegalStateException("PictureSelector can not be null")
        }
        // 单独拍照
        /*val cameraModel: PictureSelectionCameraModel = mPictureSelector
            .openCamera(if (isCamera) SelectMimeType.ofImage() else SelectMimeType.ofAll())
            .setCameraInterceptListener(getCustomCameraEvent())
            .setRecordAudioInterceptListener(MeOnRecordAudioInterceptListener())
            .setCropEngine(getCropFileEngine())
            .setCompressEngine(getCompressFileEngine())
            .setSelectLimitTipsListener(MeOnSelectLimitTipsListener())
            .setAddBitmapWatermarkListener(getAddBitmapWatermarkListener())
            .setVideoThumbnailListener(getVideoThumbnailEventListener())
            .setCustomLoadingListener(getCustomLoadingListener())
            .setSandboxFileEngine(MeSandboxFileEngine())
            .isOriginalControl(true)
            .setPermissionDescriptionListener(getPermissionDescriptionListener())
            .setOutputAudioDir(getSandboxPath())*/
        val cameraModel: PictureSelectionCameraModel = mPictureSelector
            .openCamera(SelectMimeType.ofImage())
            .setCropEngine(getCropFileEngine())
            .setCompressEngine(getCompressFileEngine())
            .setCustomLoadingListener(getCustomLoadingListener())
            .setSandboxFileEngine(MeSandboxFileEngine())
            .isOriginalControl(true)
            .setPermissionDescriptionListener(getPermissionDescriptionListener())
            .setPermissionDeniedListener(getPermissionDeniedListener())
            .setOutputAudioDir(getSandboxPath())
        when (resultMode) {
            ACTIVITY_RESULT -> forSelectResult(cameraModel, requestCode)
            CALLBACK_RESULT -> forSelectResult(cameraModel, callback)
            LAUNCHER_RESULT -> forSelectResult(cameraModel, launcher)
            else -> {}
        }
    }

    /**
     * 仅拍视频
     */
    fun takeVideo() {
        if (mPictureSelector.isNull()) {
            throw IllegalStateException("PictureSelector can not be null")
        }
        // 单独拍照
        val cameraModel: PictureSelectionCameraModel = mPictureSelector
            .openCamera(SelectMimeType.ofVideo())
            .setRecordAudioInterceptListener(MeOnRecordAudioInterceptListener())
            .setCropEngine(getCropFileEngine())
            .setCompressEngine(getCompressFileEngine())
            .setSelectLimitTipsListener(MeOnSelectLimitTipsListener())
            .setVideoThumbnailListener(getVideoThumbnailEventListener())
            .isOriginalControl(true)
            .setPermissionDescriptionListener(getPermissionDescriptionListener())
            .setPermissionDeniedListener(getPermissionDeniedListener())
            .setOutputAudioDir(getSandboxPath())
        when (resultMode) {
            ACTIVITY_RESULT -> forSelectResult(cameraModel, requestCode)
            CALLBACK_RESULT -> forSelectResult(cameraModel, callback)
            LAUNCHER_RESULT -> forSelectResult(cameraModel, launcher)
            else -> {}
        }
    }

    private fun forSelectResult(model: PictureSelectionModel, requestCode: Int) {
        model.forResult(requestCode)
    }

    private fun forSelectResult(
        model: PictureSelectionModel,
        callback: OnResultCallbackListener<LocalMedia?>
    ) {
        model.forResult(callback)
    }

    private fun forSelectResult(
        model: PictureSelectionModel,
        launcher: ActivityResultLauncher<Intent>
    ) {
        model.forResult(launcher)
    }

    private fun forSelectResult(model: PictureSelectionCameraModel, requestCode: Int) {
        model.forResultActivity(requestCode)
    }

    private fun forSelectResult(model: PictureSelectionCameraModel, callback: OnResultCallbackListener<LocalMedia?>) {
        model.forResultActivity(callback)
    }

    private fun forSelectResult(model: PictureSelectionCameraModel, launcher: ActivityResultLauncher<Intent>) {
        model.forResultActivity(launcher)
    }

    /**
     * 选择结果
     */
    private class MeOnResultCallbackListener : OnResultCallbackListener<LocalMedia?> {
        override fun onResult(result: java.util.ArrayList<LocalMedia?>) {
        }

        override fun onCancel() {
        }
    }


    /**
     * 自定义沙盒文件处理
     */
    private inner class MeSandboxFileEngine : UriToFileTransformEngine {
        override fun onUriToFileAsyncTransform(
            context: Context,
            srcPath: String,
            mineType: String,
            call: OnKeyValueResultCallbackListener
        ) {
            if (call != null) {
                call.onCallback(
                    srcPath,
                    SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                )
            }
        }
    }

    /**
     * 压缩引擎
     *
     * @return
     */
    private fun getCompressFileEngine(): ImageFileCompressEngine? {
        return ImageFileCompressEngine()
    }


    /**
     * 压缩引擎
     *
     * @return
     */
    @Deprecated("")
    private fun getCompressEngine(): ImageCompressEngine? {
        return ImageCompressEngine()
    }

    /**
     * 裁剪引擎
     *
     * @return
     */
    private fun getCropEngine(): ImageCropEngine? {
        return if (isEnableCrop) ImageCropEngine() else null
    }

    /**
     * 裁剪引擎
     *
     * @return
     */
    private fun getCropFileEngine(): ImageFileCropEngine? {
        return if (isEnableCrop) ImageFileCropEngine() else null
    }

    /**
     * 自定义相机事件
     *
     * @return
     */
    private fun getCustomCameraEvent(): OnCameraInterceptListener {
        return MeOnCameraInterceptListener()
    }

    /**
     * 自定义loading
     *
     * @return
     */
    private fun getCustomLoadingListener(): OnCustomLoadingListener {
        return OnCustomLoadingListener { context -> WaitDialog.Builder(context).getDialog() }
    }

    /**
     * 处理视频缩略图
     */
    private fun getVideoThumbnailEventListener(): OnVideoThumbnailEventListener {
        return MeOnVideoThumbnailEventListener(getSandboxPath())
    }

    /**
     * 处理视频缩略图
     */
    private inner class MeOnVideoThumbnailEventListener(private val targetPath: String) :
        OnVideoThumbnailEventListener {
        override fun onVideoThumbnail(
            context: Context,
            videoPath: String,
            call: OnKeyValueResultCallbackListener
        ) {
            Glide.with(context).asBitmap().sizeMultiplier(0.6f).load(videoPath)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val stream = ByteArrayOutputStream()
                        resource.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                        var fos: FileOutputStream? = null
                        var result: String? = null
                        try {
                            val targetFile = File(
                                targetPath,
                                "thumbnails_" + System.currentTimeMillis() + ".jpg"
                            )
                            fos = FileOutputStream(targetFile)
                            fos.write(stream.toByteArray())
                            fos.flush()
                            result = targetFile.absolutePath
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            PictureFileUtils.close(fos)
                            PictureFileUtils.close(stream)
                        }
                        if (call != null) {
                            call.onCallback(videoPath, result)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        if (call != null) {
                            call.onCallback(videoPath, "")
                        }
                    }
                })
        }
    }

    /**
     * 给图片添加水印
     */
    private fun getAddBitmapWatermarkListener(): OnBitmapWatermarkEventListener? {
        return MeBitmapWatermarkEventListener(getSandboxPath())
    }

    /**
     * 给图片添加水印
     */
    private inner class MeBitmapWatermarkEventListener(private val targetPath: String) :
        OnBitmapWatermarkEventListener {
        override fun onAddBitmapWatermark(
            context: Context,
            srcPath: String,
            mimeType: String,
            call: OnKeyValueResultCallbackListener
        ) {
            if (PictureMimeType.isHasHttp(srcPath) || PictureMimeType.isHasVideo(mimeType)) {
                // 网络图片和视频忽略，有需求的可自行扩展
                call.onCallback(srcPath, "")
            } else {
                // 暂时只以图片为例
                Glide.with(context).asBitmap().sizeMultiplier(0.6f).load(srcPath)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val stream = ByteArrayOutputStream()
                            val watermark = BitmapFactory.decodeResource(
                                context.resources,
                                R.mipmap.default_loading
                            )
                            val watermarkBitmap: Bitmap? = ImageUtils.createWaterMaskRightTop(
                                context,
                                resource,
                                watermark,
                                15,
                                15
                            )
                            watermarkBitmap?.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                            watermarkBitmap?.recycle()
                            var fos: FileOutputStream? = null
                            var result: String? = null
                            try {
                                val targetFile =
                                    File(targetPath, DateUtils.getCreateFileName("Mark_") + ".jpg")
                                fos = FileOutputStream(targetFile)
                                fos.write(stream.toByteArray())
                                fos.flush()
                                result = targetFile.absolutePath
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                PictureFileUtils.close(fos)
                                PictureFileUtils.close(stream)
                            }
                            if (call != null) {
                                call.onCallback(srcPath, result)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            if (call != null) {
                                call.onCallback(srcPath, "")
                            }
                        }
                    })
            }
        }
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private fun getSandboxPath(): String {
        /*val externalFilesDir: File? = PApplication.getContext().getExternalFilesDir("")
        val customFile = File(externalFilesDir?.absolutePath, "Sandbox")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator*/
        return AppActivityManager.getInstance().getResumedActivity()?.getOutputDirectory()?.absolutePath + File.separator
    }

    private fun getNotSupportCrop(): Array<String> {
        return arrayOf(PictureMimeType.ofGIF(), PictureMimeType.ofWEBP())
    }

    /**
     * 自定义压缩
     */
    private inner class ImageFileCompressEngine : CompressFileEngine {
        override fun onStartCompress(
            context: Context,
            source: ArrayList<Uri>,
            call: OnKeyValueResultCallbackListener
        ) {
            Luban.with(context).load(source).ignoreBy(100).setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_") + postfix
            }.filter(CompressionPredicate { path ->
                if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                    return@CompressionPredicate true
                }
                !PictureMimeType.isUrlHasGif(path)
            }).setCompressListener(object : OnNewCompressListener {
                override fun onStart() {
                }

                override fun onSuccess(source: String, compressFile: File) {
                    if (call != null) {
                        call.onCallback(source, compressFile.absolutePath)
                    }
                }

                override fun onError(source: String, e: Throwable) {
                    if (call != null) {
                        call.onCallback(source, null)
                    }
                }
            }).launch()
        }
    }

    /**
     * 自定义压缩
     */
    @Deprecated("")
    private inner class ImageCompressEngine : CompressEngine {
        override fun onStartCompress(
            context: Context, list: ArrayList<LocalMedia>,
            listener: OnCallbackListener<ArrayList<LocalMedia>>
        ) {
            // 自定义压缩
            val compress: MutableList<Uri> = ArrayList()
            for (i in list.indices) {
                val media = list[i]
                val availablePath = media.availablePath
                val uri = if (PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(
                        availablePath
                    )
                )
                    Uri.parse(availablePath)
                else
                    Uri.fromFile(File(availablePath))
                compress.add(uri)
            }
            if (compress.size == 0) {
                listener.onCall(list)
                return
            }
            Luban.with(context)
                .load(compress)
                .ignoreBy(100)
                .filter { path ->
                    PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(
                        path
                    )
                }
                .setRenameListener { filePath ->
                    val indexOf = filePath.lastIndexOf(".")
                    val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                    DateUtils.getCreateFileName("CMP_") + postfix
                }
                .setCompressListener(object : OnCompressListener {
                    override fun onStart() {
                    }

                    override fun onSuccess(index: Int, compressFile: File) {
                        val media = list[index]
                        if (compressFile.exists() && !TextUtils.isEmpty(compressFile.absolutePath)) {
                            media.isCompressed = true
                            media.compressPath = compressFile.absolutePath
                            media.sandboxPath =
                                if (SdkVersionUtils.isQ()) media.compressPath else null
                        }
                        if (index == list.size - 1) {
                            listener.onCall(list)
                        }
                    }

                    override fun onError(index: Int, e: Throwable) {
                        if (index != -1) {
                            val media = list[index]
                            media.isCompressed = false
                            media.compressPath = null
                            media.sandboxPath = null
                            if (index == list.size - 1) {
                                listener.onCall(list)
                            }
                        }
                    }
                }).launch()
        }
    }


    /**
     * 自定义裁剪
     */
    private inner class ImageCropEngine : CropEngine {
        override fun onStartCrop(
            fragment: Fragment, currentLocalMedia: LocalMedia,
            dataSource: java.util.ArrayList<LocalMedia>, requestCode: Int
        ) {
            val currentCropPath = currentLocalMedia.availablePath
            val inputUri =
                if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(
                        currentCropPath
                    )
                ) {
                    Uri.parse(currentCropPath)
                } else {
                    Uri.fromFile(File(currentCropPath))
                }
            val fileName = DateUtils.getCreateFileName("CROP_") + ".jpg"
            val destinationUri = Uri.fromFile(File(getSandboxPath(), fileName))
            val options: UCrop.Options = buildOptions()
            val dataCropSource = ArrayList<String>()
            for (i in dataSource.indices) {
                val media = dataSource[i]
                dataCropSource.add(media.availablePath)
            }
            val uCrop = UCrop.of(inputUri, destinationUri, dataCropSource)
            //options.setMultipleCropAspectRatio(buildAspectRatios(dataSource.size()));
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine {
                override fun loadImage(context: Context, url: String, imageView: ImageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView)
                }

                override fun loadImage(
                    context: Context,
                    url: Uri,
                    maxWidth: Int,
                    maxHeight: Int,
                    call: UCropImageEngine.OnCallbackListener<Bitmap>
                ) {
                }
            })
            uCrop.start(fragment.requireActivity(), fragment, requestCode)
        }
    }

    /**
     * 自定义裁剪
     */
    private inner class ImageFileCropEngine : CropFileEngine {
        override fun onStartCrop(fragment: Fragment, srcUri: Uri, destinationUri: Uri,
                                 dataSource: ArrayList<String>, requestCode: Int) {
            val options: UCrop.Options = buildOptions()
            val uCrop = UCrop.of(srcUri, destinationUri, dataSource)
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine {
                override fun loadImage(context: Context, url: String, imageView: ImageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView)
                }

                override fun loadImage(context: Context, url: Uri, maxWidth: Int,
                                       maxHeight: Int, call: UCropImageEngine.OnCallbackListener<Bitmap>) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                if (call != null) {
                                    call.onCall(resource)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                if (call != null) {
                                    call.onCall(null)
                                }
                            }
                        })
                }
            })
            uCrop.start(fragment.requireActivity(), fragment, requestCode)
        }
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private fun buildOptions(): UCrop.Options {
        val options = UCrop.Options()
        // 是否显示裁剪菜单栏
        options.setHideBottomControls(true)
        // 裁剪框or图片拖动
        options.setFreeStyleCropEnabled(true)
        // 是否显示裁剪边框
        options.setShowCropFrame(!isEnableCircleCrop)
        // 是否显示裁剪框网格
        options.setShowCropGrid(!isEnableCircleCrop)
        // 圆形头像裁剪模式
        options.setCircleDimmedLayer(isEnableCircleCrop)
        options.withAspectRatio(aspect_ratio_x.toFloat(), aspect_ratio_y.toFloat())
        options.setCropOutputPathDir(getSandboxPath())
        options.isCropDragSmoothToCenter(false)
        options.setSkipCropMimeType(*getNotSupportCrop())
        // 禁止裁剪gif
        options.isForbidCropGifWebp(true)
        options.isForbidSkipMultipleCrop(true)
        options.setMaxScaleMultiplier(100f)
        if (selectorStyle.getSelectMainStyle().getStatusBarColor() != 0) {
            val mainStyle: SelectMainStyle = selectorStyle.getSelectMainStyle()
            val isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack
            val statusBarColor = mainStyle.statusBarColor
            options.isDarkStatusBarBlack(isDarkStatusBarBlack)
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor)
                options.setToolbarColor(statusBarColor)
            } else {
                options.setStatusBarColor(R.color.color_999999.getResColor())
                options.setToolbarColor(R.color.color_999999.getResColor())
            }
            val titleBarStyle: TitleBarStyle = selectorStyle.getTitleBarStyle()
            if (StyleUtils.checkStyleValidity(titleBarStyle.titleTextColor)) {
                options.setToolbarWidgetColor(titleBarStyle.titleTextColor)
            } else {
                options.setToolbarWidgetColor(R.color.white.getResColor())
            }
        } else {
            options.setStatusBarColor(R.color.color_999999.getResColor())
            options.setToolbarColor(R.color.color_999999.getResColor())
            options.setToolbarWidgetColor(R.color.white.getResColor())
        }
        return options
    }

    /**
     * 自定义编辑事件
     *
     * @return
     */
    private fun getCustomEditMediaEvent(): OnMediaEditInterceptListener? {
        return if (isEnableEditor) MeOnMediaEditInterceptListener(
            getSandboxPath(),
            buildOptions()
        ) else null
    }

    /**
     * 自定义编辑
     */
    private inner class MeOnMediaEditInterceptListener(
        private val outputCropPath: String,
        private val options: UCrop.Options
    ) :
        OnMediaEditInterceptListener {
        override fun onStartMediaEdit(
            fragment: Fragment,
            currentLocalMedia: LocalMedia,
            requestCode: Int
        ) {
            val currentEditPath = currentLocalMedia.availablePath
            val inputUri = if (PictureMimeType.isContent(currentEditPath))
                Uri.parse(currentEditPath)
            else
                Uri.fromFile(File(currentEditPath))
            val destinationUri = Uri.fromFile(
                File(outputCropPath, DateUtils.getCreateFileName("CROP_") + ".jpeg")
            )
            val uCrop = UCrop.of<Any>(inputUri, destinationUri)
            options.setHideBottomControls(false)
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine {
                override fun loadImage(context: Context, url: String, imageView: ImageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView)
                }

                override fun loadImage(
                    context: Context,
                    url: Uri,
                    maxWidth: Int,
                    maxHeight: Int,
                    call: UCropImageEngine.OnCallbackListener<Bitmap>
                ) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                if (call != null) {
                                    call.onCall(resource)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                if (call != null) {
                                    call.onCall(null)
                                }
                            }
                        })
                }
            })
            uCrop.startEdit(fragment.requireActivity(), fragment, requestCode)
        }
    }

    /**
     * 自定义拍照
     */
    private inner class MeOnCameraInterceptListener : OnCameraInterceptListener {
        override fun openCamera(fragment: Fragment, cameraMode: Int, requestCode: Int) {
            val camera = SimpleCameraX.of()
            camera.isAutoRotation(true)
            camera.setCameraMode(cameraMode)
            camera.setVideoFrameRate(25)
            camera.setVideoBitRate(3 * 1024 * 1024)
            camera.isDisplayRecordChangeTime(true)
            camera.isManualFocusCameraPreview(true)
            camera.isZoomCameraPreview(true)
            camera.setOutputPathDir(getSandboxPath())
            camera.setPermissionDeniedListener(getSimpleXPermissionDeniedListener())
            camera.setPermissionDescriptionListener(getSimpleXPermissionDescriptionListener())
            camera.setImageEngine { context, url, imageView ->
                Glide.with(context).load(url).into(imageView)
            }
            camera.start(fragment.requireActivity(), fragment, requestCode)
        }
    }

    /**
     * 录音回调事件
     */
    private inner class MeOnRecordAudioInterceptListener : OnRecordAudioInterceptListener {
        override fun onRecordAudio(fragment: Fragment, requestCode: Int) {
            val recordAudio = arrayOf(Manifest.permission.RECORD_AUDIO)
            if (PermissionChecker.isCheckSelfPermission(fragment.context, recordAudio)) {
                startRecordSoundAction(fragment, requestCode)
            } else {
                addPermissionDescription(
                    false,
                    fragment.requireView() as ViewGroup,
                    recordAudio
                )
                PermissionChecker.getInstance().requestPermissions(fragment,
                    arrayOf<String>(Manifest.permission.RECORD_AUDIO),
                    object : PermissionResultCallback {
                        override fun onGranted() {
                            removePermissionDescription(fragment.requireView() as ViewGroup)
                            startRecordSoundAction(fragment, requestCode)
                        }

                        override fun onDenied() {
                            removePermissionDescription(fragment.requireView() as ViewGroup)
                        }
                    })
            }
        }
    }

    /**
     * 启动录音意图
     *
     * @param fragment
     * @param requestCode
     */
    private fun startRecordSoundAction(fragment: Fragment, requestCode: Int) {
        val recordAudioIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        if (recordAudioIntent.resolveActivity(fragment.requireActivity().packageManager) != null) {
            fragment.startActivityForResult(recordAudioIntent, requestCode)
        } else {
            ToastUtils.showToast(fragment.context, "The system is missing a recording component")
        }
    }

    /**
     * 权限拒绝后回调
     *
     * @return
     */
    private fun getPermissionDeniedListener(): OnPermissionDeniedListener? {
        return MeOnPermissionDeniedListener()
    }

    /**
     * 权限拒绝后回调
     */
    private inner class MeOnPermissionDeniedListener : OnPermissionDeniedListener {
        override fun onDenied(
            fragment: Fragment, permissionArray: Array<String>,
            requestCode: Int, call: OnCallbackListener<Boolean>
        ) {
            val tips = if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
                "缺少相机权限\n可能会导致不能使用摄像头功能"
            } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
                "缺少录音权限\n访问您设备上的音频、媒体内容和文件"
            } else {
                "缺少存储权限\n访问您设备上的照片、媒体内容和文件"
            }
            val dialog = RemindDialog.buildDialog(fragment.context, tips)
            dialog.setButtonText("去设置")
            dialog.setButtonTextColor(Color.parseColor("#7D7DFF"))
            dialog.setContentTextColor(Color.parseColor("#333333"))
            dialog.setOnDialogClickListener {
                PermissionUtil.goIntentSetting(fragment, requestCode)
                dialog.dismiss()
            }
            dialog.show()
        }
    }


    /**
     * SimpleCameraX权限拒绝后回调
     *
     * @return
     */
    private fun getSimpleXPermissionDeniedListener(): OnSimpleXPermissionDeniedListener? {
        return MeOnSimpleXPermissionDeniedListener()
    }

    /**
     * SimpleCameraX添加权限说明
     */
    private inner class MeOnSimpleXPermissionDeniedListener : OnSimpleXPermissionDeniedListener {
        override fun onDenied(context: Context, permission: String, requestCode: Int) {
            var tips = if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                "缺少麦克风权限\n可能会导致录视频无法采集声音"
            } else {
                "缺少相机权限\n可能会导致不能使用摄像头功能"
            }
            val dialog = RemindDialog.buildDialog(context, tips)
            dialog.setButtonText("去设置")
            dialog.setButtonTextColor(Color.parseColor("#7D7DFF"))
            dialog.setContentTextColor(Color.parseColor("#333333"))
            dialog.setOnDialogClickListener {
                SimpleXPermissionUtil.goIntentSetting(context as Activity, requestCode)
                dialog.dismiss()
            }
            dialog.show()
        }
    }


    /**
     * SimpleCameraX权限说明
     *
     * @return
     */
    private fun getSimpleXPermissionDescriptionListener(): OnSimpleXPermissionDescriptionListener? {
        return MeOnSimpleXPermissionDescriptionListener()
    }

    /**
     * SimpleCameraX添加权限说明
     */
    private inner class MeOnSimpleXPermissionDescriptionListener :
        OnSimpleXPermissionDescriptionListener {
        override fun onPermissionDescription(
            context: Context,
            viewGroup: ViewGroup,
            permission: String
        ) {
            addPermissionDescription(true, viewGroup, arrayOf<String>(permission))
        }

        override fun onDismiss(viewGroup: ViewGroup) {
            removePermissionDescription(viewGroup)
        }
    }

    /**
     * 权限说明
     *
     * @return
     */
    private fun getPermissionDescriptionListener(): OnPermissionDescriptionListener? {
        return MeOnPermissionDescriptionListener()
    }

    /**
     * 添加权限说明
     */
    private inner class MeOnPermissionDescriptionListener : OnPermissionDescriptionListener {
        override fun onPermissionDescription(fragment: Fragment, permissionArray: Array<String>) {
            val rootView = fragment.requireView()
            if (rootView is ViewGroup) {
                addPermissionDescription(false, rootView, permissionArray)
            }
        }

        override fun onDismiss(fragment: Fragment) {
            removePermissionDescription(fragment.requireView() as ViewGroup)
        }
    }


    /**
     * 添加权限说明
     *
     * @param viewGroup
     * @param permissionArray
     */
    private fun addPermissionDescription(
        isHasSimpleXCamera: Boolean,
        viewGroup: ViewGroup,
        permissionArray: Array<String>
    ) {
        val dp10 = DensityUtil.dip2px(viewGroup.context, 10f)
        val dp15 = DensityUtil.dip2px(viewGroup.context, 15f)
        val view = MediumBoldTextView(viewGroup.context)
        view.tag = TAG_EXPLAIN_VIEW
        view.textSize = 14f
        view.setTextColor(Color.parseColor("#333333"))
        view.setPadding(dp10, dp15, dp10, dp15)

        val title: String
        val explain: String

        if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
            title = "相机权限使用说明"
            explain = "相机权限使用说明\n用户app用于拍照/录视频"
        } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
            if (isHasSimpleXCamera) {
                title = "麦克风权限使用说明"
                explain = "麦克风权限使用说明\n用户app用于录视频时采集声音"
            } else {
                title = "录音权限使用说明"
                explain = "录音权限使用说明\n用户app用于采集声音"
            }
        } else {
            title = "存储权限使用说明"
            explain = "存储权限使用说明\n用户app写入/下载/保存/读取/修改/删除图片、视频、文件等信息"
        }
        val startIndex = 0
        val endOf = startIndex + title.length
        val builder = SpannableStringBuilder(explain)
        builder.setSpan(
            AbsoluteSizeSpan(DensityUtil.dip2px(viewGroup.context, 16f)),
            startIndex,
            endOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            ForegroundColorSpan(Color.parseColor("#333333")),
            startIndex,
            endOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        view.text = builder
        view.background =
            ContextCompat.getDrawable(viewGroup.context, R.drawable.shape_search_bar)

        if (isHasSimpleXCamera) {
            val layoutParams =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
            layoutParams.topMargin = DensityUtil.getStatusBarHeight(viewGroup.context)
            layoutParams.leftMargin = dp10
            layoutParams.rightMargin = dp10
            viewGroup.addView(view, layoutParams)
        } else {
            val layoutParams =
                ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
//            layoutParams.topToBottom = R.id.title_bar
            layoutParams.topMargin = DensityUtil.getStatusBarHeight(viewGroup.context)
            layoutParams.leftToLeft = ConstraintSet.PARENT_ID
            layoutParams.leftMargin = dp10
            layoutParams.rightMargin = dp10
            viewGroup.addView(view, layoutParams)
        }
    }

    /**
     * 移除权限说明
     *
     * @param viewGroup
     */
    private fun removePermissionDescription(viewGroup: ViewGroup) {
        val tagExplainView = viewGroup.findViewWithTag<View>(TAG_EXPLAIN_VIEW)
        viewGroup.removeView(tagExplainView)
    }


    /**
     * 拦截自定义提示
     */
    private inner class MeOnSelectLimitTipsListener : OnSelectLimitTipsListener {
        override fun onSelectLimitTips(
            context: Context,
            media: LocalMedia?,
            config: SelectorConfig,
            limitType: Int
        ): Boolean {
            if (limitType == SelectLimitType.SELECT_MIN_SELECT_LIMIT) {
                ToastUtils.showToast(context, "图片最少不能低于" + config.minSelectNum + "张")
                return true
            } else if (limitType == SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT) {
                ToastUtils.showToast(context, "视频最少不能低于" + config.minVideoSelectNum + "个")
                return true
            } else if (limitType == SelectLimitType.SELECT_MIN_AUDIO_SELECT_LIMIT) {
                ToastUtils.showToast(context, "音频最少不能低于" + config.minAudioSelectNum + "个")
                return true
            }
            return false
        }
    }

}