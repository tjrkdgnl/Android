package com.jeoksyeo.wet.activity.alcohol_rated

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.google.android.material.tabs.TabLayout
import com.viewmodel.RatedViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.AlcoholRatedBinding

class AlcoholRated :AppCompatActivity(), AlcoholRatedContact.BaseView
    ,TabLayout.OnTabSelectedListener{
    private lateinit var binding:AlcoholRatedBinding
    private var bindObj:AlcoholRatedBinding?=null

    private lateinit var presenter:Presenter
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindObj = DataBindingUtil.setContentView(this, R.layout.alcohol_rated)
        binding =bindObj!!
        binding.lifecycleOwner =this

        presenter = Presenter().apply {
            view=this@AlcoholRated
            context =this@AlcoholRated
        }

        presenter.setNetworkUtil()

        setHeaderinit()

        presenter.initProfile(GlobalApplication.userInfo.getProvider())
        presenter.initTabLayout(this)
        binding.ratedTablayout.addOnTabSelectedListener(this)

        binding.ratedHeader.title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,GlobalApplication.instance.getCalculatorTextSize(16f))


        val viewmodel = ViewModelProvider(this).get(RatedViewModel::class.java)

        viewmodel.reviewCount.observe(this, Observer {
           binding.profileHeader.ratedCountText.text = "총  ${it}개의 주류를 평가하셨습니다."
        })
    }

    override fun onStart() {
        super.onStart()
        GlobalApplication.instance.activityClass = AlcoholRated::class.java
    }
    override fun onResume() {
        super.onResume()
        GlobalApplication.instance.setActivityBackground(true)
    }
    override fun onStop() {
        super.onStop()
        GlobalApplication.instance.setActivityBackground(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        bindObj=null
        presenter.detach()
    }

    override fun getView(): AlcoholRatedBinding {
        return binding
    }

    override fun setHeaderinit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.statusBarColor = resources.getColor(R.color.orange,null)
            }else{
                window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (tab.customView as? TextView)?.setTextColor(resources.getColor(R.color.orange,null))
            } else {
                (tab.customView as? TextView)?.setTextColor(ContextCompat.getColor(this,R.color.orange))
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (tab.customView as? TextView)?.setTextColor(resources.getColor(R.color.tabColor,null))
            } else {
                (tab.customView as? TextView)?.setTextColor(ContextCompat.getColor(this,R.color.tabColor))
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_current,R.anim.current_to_right)
    }


}