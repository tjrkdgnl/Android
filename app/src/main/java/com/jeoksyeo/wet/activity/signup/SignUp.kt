package com.jeoksyeo.wet.activity.signup

import android.content.Intent
import com.adapter.signup.SignUpViewPagerAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.GlobalApplication
import com.jeoksyeo.wet.activity.login.Login
import com.jeoksyeo.wet.activity.main.MainActivity
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.viewmodel.SignUpViewModel
import com.vuforia.engine.wet.R
import com.vuforia.engine.wet.databinding.ActivitySignupBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignUp : AppCompatActivity(), View.OnClickListener, SignUpContract.BaseView {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var mutableList: MutableList<String>
    private var idx = 0
    private var backIdx = 0
    private lateinit var signUpViewPagerAdapter: SignUpViewPagerAdapter
    private lateinit var viewModel: SignUpViewModel
    private lateinit var presenter: SignUpPresenter
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.lifecycleOwner = this

        init()

        binding.infoConfirmButton.setOnClickListener(this)
        binding.signupHeader.signupHeaderBackButton.setOnClickListener(this)

        //확인 enable setting
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        viewModel.buttonState.observe(this, Observer {
            binding.infoConfirmButton.isEnabled = it
        })
    }

    private fun init() {
        presenter = SignUpPresenter().apply {
            view = this@SignUp
        }

        mutableList = mutableListOf()
        mutableList.add(GlobalApplication.NICKNAME)
        intent?.let {
            val bundle = it.getBundleExtra("userBundle")
            bundle?.let { bun ->
                if (!bun.getBoolean(GlobalApplication.BIRTHDAY, false)) mutableList.add(
                    GlobalApplication.BIRTHDAY
                )
                if (!bun.getBoolean(GlobalApplication.GENDER, false)) mutableList.add(
                    GlobalApplication.GENDER
                )
            }
        }
        mutableList.add("location")

        //progressbar init
        binding.signupHeader.signUpHeaderProgressbar.max = mutableList.size

        //viewPager2 init
        signUpViewPagerAdapter = SignUpViewPagerAdapter(this, mutableList)
        binding.viewPager2.adapter = signUpViewPagerAdapter
        binding.viewPager2.isUserInputEnabled = false //viewpager2 스와이프off
        binding.viewPager2.offscreenPageLimit = 1

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.infoConfirmButton ->nextView()

            R.id.signup_header_backButton -> finish()

            else -> {
            }
        }
    }

    override fun nextView() {
        viewModel.buttonState.value = false
        binding.viewPager2.currentItem = ++idx
        binding.signupHeader.signUpHeaderProgressbar.progress = idx + 1
        presenter.hideKeypad(this, binding.infoConfirmButton)

        if (viewModel.checkRequest) {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
        else if (viewModel.lock) {
            GlobalApplication.userBuilder.setAddress(

                viewModel.stateArea.value?.name + " " + viewModel.countryArea.value?.name + " " +
                        viewModel.townArea.value?.name
            )
            GlobalApplication.userInfo = GlobalApplication.userBuilder.build()
            disposable = ApiGenerator.retrofit.create(ApiService::class.java).signUp(
                GlobalApplication.userBuilder.createUUID,
                GlobalApplication.userInfo.getMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //내장 디비에 토큰 값들 저장
                    GlobalApplication.userDataBase.setAccessToken(it.data?.token?.accessToken.toString())
                    GlobalApplication.userDataBase.setRefreshToken(it.data?.token?.refreshToken.toString())
                    JWTUtil.decodeAccessToken(GlobalApplication.userDataBase.getAccessToken())
                    JWTUtil.decodeRefreshToken(GlobalApplication.userDataBase.getRefreshToken())
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "회원가입을 축하드립니다!", Toast.LENGTH_SHORT).show()
                    finish()
                }, { t: Throwable -> t.stackTrace })
        }
    }
}