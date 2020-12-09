package com.jeoksyeo.wet.activity.splash

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.application.GlobalApplication
import com.error.ErrorManager
import com.jeoksyeo.wet.activity.main.MainActivity
import com.model.user.UserInfo
import com.service.ApiGenerator
import com.service.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SplashPresenter : SplashContract.BasePresenter {
    private val compositedisposable =CompositeDisposable()
    override lateinit var view: SplashContract.BaseView
    override lateinit var activity: Activity
    private var retrofit = ApiGenerator.retrofit.create(ApiService::class.java)
    private val handler = Handler(Looper.getMainLooper())

    override fun moveActivity() {
        handler.postDelayed({
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
        }, 4000)
    }

    override fun setUserInfo() {
        compositedisposable.add(ApiGenerator.retrofit.create(ApiService::class.java)
            .getUserInfo(GlobalApplication.userBuilder.createUUID, "Bearer " + GlobalApplication.userDataBase.getAccessToken())
            .subscribeOn(Schedulers.io())
            .subscribe({ user ->
                GlobalApplication.userInfo = UserInfo.Builder().apply {
                    setProvider(user.data?.userInfo?.provider)
                    setNickName(user.data?.userInfo?.nickname ?: "")
                    setBirthDay(user.data?.userInfo?.birth ?: "1970-01-01")
                    setProfile(user.data?.userInfo?.profile)
                    setGender(user.data?.userInfo?.gender ?: "M")
                    setAddress("") //추후에 셋팅하기
                    setLevel(user.data?.userInfo?.level ?: 0)
                    setAccessToken("Bearer " + GlobalApplication.userDataBase.getAccessToken())
                }.build()
            }, {})
        )
    }

    override suspend fun versionCheck():Boolean = suspendCoroutine  {coroutineResult ->
        val packageManager = activity.packageManager.getPackageInfo(activity.packageName, PackageManager.GET_META_DATA)
        val currentVersion = packageManager.versionName.split(".")
        var latestVersion = "1.0.0".split(".")

        compositedisposable.add(retrofit
            .getVersion(GlobalApplication.userBuilder.createUUID, GlobalApplication.userInfo.getAccessToken(), "android")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.data?.platform?.let { platform ->
                    if (platform == "android") {
                        it.data?.version?.let { version ->
                            latestVersion = version.split(".")

                            try {
                                //메이저 버전체크
                                when {
                                    latestVersion[0].toInt() > currentVersion[0].toInt() -> {
                                        coroutineResult.resume(false)
                                    }
                                    //마이너 버전체크
                                    latestVersion[1].toInt() > currentVersion[1].toInt() -> {
                                        coroutineResult.resume(false)
                                    }
                                    //패치 버전체크
                                    latestVersion[2].toInt() > currentVersion[2].toInt() -> {
                                        coroutineResult.resume(false)
                                    }
                                    else -> {
                                        coroutineResult.resume(true)
                                    }
                                }

                            } catch (e: java.lang.Exception) {
                                coroutineResult.resume(false)
                                Log.e(ErrorManager.VERSION, e.message.toString())
                                Toast.makeText(activity, "버전을 확인하는데 문제가 생겼습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }, { t ->
                Log.e(ErrorManager.VERSION, t.message.toString())
            })
        )
    }

    override fun detach() {
        compositedisposable.dispose()
    }
}