package com.gis.common.manager

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.gis.common.extension.LogEnum
import com.gis.common.extension.log

/**
 * Created by chengzf on 2023/4/27.
 * 监听Fragment生命周期
 */
class FragmentLifecycle : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentPreAttached(fm, f, context)
        String.format("%s - onFragmentPreAttached", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)
        String.format("%s - onFragmentAttached", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentPreCreated(fm, f, savedInstanceState)
        String.format("%s - onFragmentPreCreated", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        String.format("%s - onFragmentCreated", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState)
        String.format("%s - onFragmentActivityCreated", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
        String.format("%s - onFragmentViewCreated", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        String.format("%s - onFragmentStarted", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        String.format("%s - onFragmentResumed", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        super.onFragmentPaused(fm, f)
        String.format("%s - onFragmentPaused", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        super.onFragmentStopped(fm, f)
        String.format("%s - onFragmentStopped", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        super.onFragmentSaveInstanceState(fm, f, outState)
        String.format("%s - onFragmentSaveInstanceState", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)
        String.format("%s - onFragmentViewDestroyed", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        String.format("%s - onFragmentDestroyed", f.javaClass.simpleName).log(LogEnum.INFO)
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        String.format("%s - onFragmentDetached", f.javaClass.simpleName).log(LogEnum.INFO)
    }


}