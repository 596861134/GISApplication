package com.czf.gis

import android.Manifest
import android.app.Application
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewbinding.ViewBindings
import com.czf.gis.databinding.ActivityMainBinding
import com.gis.common.extension.showToast
import com.gis.common.manager.LifecycleOwnerManager
import com.gis.common.mvvm.view.BaseVMRepositoryActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

class MainActivity: BaseVMRepositoryActivity<MainViewModel, ActivityMainBinding>(){

    private val mLifecycle: LifecycleOwnerManager by lazy { LifecycleOwnerManager() }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mBinding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mBinding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mBinding.mapView.gestures.focalPoint = mBinding.mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    override fun onViewInit() {
        super.onViewInit()
        XXPermissions.with(this).permission(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {
                if (!all) return
                initMap()
            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                permissions.let {
                    "权限请求失败".showToast()
                }
            }
        })
        setOnClickListener(mBinding.btnOk)
        lifecycle.addObserver(mLifecycle)
    }

    override fun onEvent() {
        super.onEvent()
        val mapView = ViewBindings.findChildViewById<MapView>(mBinding.root, R.id.mapView)

        mRealVM.getUpdateBusinessLiveData().observe(this){
            // 执行回调操作，避免页面关闭操作View
        }

        mRealVM.userLogout()
    }

    private fun initMap() {
        mBinding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mBinding.mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
        }

        /*mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            mapView.location.updateSettings {
                enabled = true
                pulsingEnabled = true
            }
        }*/

    }

    private fun setupGesturesListener() {
        mBinding.mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mBinding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_baseline_location_on_24,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_baseline_lens_24,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed() {
        "onCameraTrackingDismissed".showToast()
        mBinding.mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mBinding.mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mBinding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onStart() {
        super.onStart()
        mBinding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mBinding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBinding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mBinding.mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mBinding.mapView.gestures.removeOnMoveListener(onMoveListener)
        mBinding.mapView.onDestroy()
    }

    override fun getViewModel(app: Application) = MainViewModel(app)
    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onClick(view: View) {
        super.onClick(view)
        when(view){
            mBinding.btnOk -> startActivity(ScendActivity::class.java)
        }
    }

}