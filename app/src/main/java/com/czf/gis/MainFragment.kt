package com.czf.gis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.czf.gis.databinding.FragmentMainBinding
import com.gis.common.mvvm.view.BaseViewModelFragment
import com.gis.common.mvvm.viewmodel.BaseLayoutViewModel

/**
 * Created by chengzf on 2023/4/27.
 */
class MainFragment:BaseViewModelFragment<BaseLayoutViewModel, FragmentMainBinding>(BaseLayoutViewModel::class.java) {

    private val mLabel:String by lazy { arguments?.getString("viewSource") ?: "" }
    companion object{

        @JvmStatic
        fun newInstance(viewSource: String) = MainFragment().apply {
            val bundle = Bundle().apply {
                putString("viewSource", viewSource)
            }
            arguments = bundle
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onEvent() {
        super.onEvent()
        mBinding.tvView.text = mLabel
    }
}