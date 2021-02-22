package com.fragment.mypage

import android.content.Context
import android.os.Bundle
import android.view.View
import com.application.GlobalApplication
import com.base.BaseFragment
import com.jeoksyeo.wet.activity.editprofile.EditProfile
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.MypageBinding

class MyPageFragment: BaseFragment<MypageBinding>(), MypageContract.BaseView {

    override val layoutResID: Int =R.layout.mypage

    private lateinit var presenter:MyPagePresenter
    private lateinit var activityContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = MyPagePresenter().apply {
            baseView = this@MyPageFragment
            activity = requireActivity()
        }

        binding.myPageHeader.root.setOnClickListener {
            GlobalApplication.instance.moveActivity(requireContext(),EditProfile::class.java)
        }

        presenter.initRecyclerView()
        presenter.initTextSize()
    }

    override fun onResume() {
        super.onResume()
        presenter.checkLogin()
    }

    override fun getViewBinding(): MypageBinding {
        return binding
    }

}