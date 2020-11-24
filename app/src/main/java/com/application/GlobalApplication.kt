package com.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.KakaoSdk
import com.model.user.UserInfo
import com.sharedpreference.UserDB
import com.vuforia.engine.wet.R
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.util.*

class GlobalApplication : Application() {
    private val typeList = listOf<String>("TR", "BE", "WI", "FO", "SA")
    private val ratedList = listOf<String>("ALL", "TR", "BE", "WI", "SA", "FO")
    private val levelList = listOf<String>(
        "마시는 척 하는 사람", "술을 즐기는 사람"
        , "술독에 빠진 사람", "주도를 수련하는 사람", "술로 해탈한 사람"
    )

    override fun onCreate() {
        super.onCreate()
        userBuilder = UserInfo.Builder("")
        KakaoSdk.init(this, getString(R.string.kakaoNativeKey))
        userInfo = UserInfo()
        userDataBase = UserDB.getInstance(this)
        RxjavaErrorHandling()
    }

    companion object {
        //싱글턴 객체 생성
        var instance = GlobalApplication()
            private set

        val context = this

        //유저 정보
        lateinit var userInfo: UserInfo
        lateinit var userBuilder: UserInfo.Builder
        lateinit var userDataBase: UserDB

        //디바이스 사이즈
        var device_width = 0
        var device_height = 0

        const val NICKNAME = "nickname"
        const val BIRTHDAY = "birth"
        const val GENDER = "gender"
        const val LOCATION = "location"
        const val USER_ID = "user_id"
        const val OAUTH_PROVIDER = "oauth_provider"
        const val OAUTH_TOKEN = "oauth_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val ADDRESS = "address"
        const val ACTIVITY_HANDLING = "activity_handling"
        const val USER_BUNDLE = "userBundle"
        const val ACTIVITY_HANDLING_BUNDLE = "activityHandling_bundle"

        const val CATEGORY_BUNDLE = "category_bundle"
        const val ALCHOL_BUNDLE = "alcohol"
        const val CATEGORY_SIZE = 5
        const val MOVE_TYPE = "type"
        const val PAGINATION_SIZE = 20
        const val DEFAULT_SORT = "review"
        const val MOVE_ALCHOL = "alcohol Data"
        const val ALCHOL_LIKE = "likeAndDisLike"
        const val ACTIVITY_HANDLING_MAIN = 0
        const val ACTIVITY_HANDLING_DETAIL = 1
        const val ACTIVITY_HANDLING_CATEGORY = 2
        const val MOVE_MY_COMMENT = "my comment"

        const val DETAIL_NO_REVIEW = 0
        const val DETAIL_REVIEW = 1
        const val DETAIL_MORE_REVIEW = 2

        const val COMPONENT_DEFAULT = 0
        const val COMPONENT_RECYCLERVIEW = 1
        const val COMPONENT_SRM = 2

        const val AGREEMENT = "agreement"
        const val AGREEMENT_INFO = "agreement_info"

        const val PAGE_REVIEW_COUNT =3
    }

    fun getAlcoholType(positon: Int) = typeList[positon]

    fun getRatedType(positon: Int) = ratedList[positon]

    fun getLevelName(positon: Int) = levelList[positon]

    fun checkCount(value: Int, count: Int = 0): String {
        return if (value >= 10000) "9999+"
        else if (value + count < 0) "0"
        else
            (value + count).toString()
    }

    fun keyPadSetting(editText: EditText, context: Context, hide: Boolean = true) {
        if (hide) {
            val inputMethodManager =
                context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        } else {
            editText.post(Runnable {
                editText.requestFocus()
                val inputMethodManager =
                    context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(editText, 0)
            })
        }
    }


    fun removeEditextFocus(editText: EditText, view: View) {
        view.setOnTouchListener { v, event ->
            Log.e("터치", "터치")
            if (event.action == MotionEvent.ACTION_DOWN) {
                val outRect = Rect()
                editText.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    editText.clearFocus()
                    keyPadSetting(editText, view.context)
                }
            }
            false
        }
    }

    //액티비티 전환
    fun moveActivity(
        context: Context,
        activityClass: Class<*>,
        flag: Int = 0,
        bundle: Bundle? = null,
        bundleFlag: String? = null,
        animationFlag: Int = 0
    ) {
        var activity = context as Activity
        var intent = Intent(context, activityClass)

        bundle?.let { bun ->
            bundleFlag?.let { flag ->
                intent.putExtra(flag, bun)
            }
        }

        when (flag) {
            0 -> {
                activity.startActivity(intent)
            }

            Intent.FLAG_ACTIVITY_SINGLE_TOP -> {
                activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }

            Intent.FLAG_ACTIVITY_CLEAR_TOP -> {
                activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        }

        when (animationFlag) {
            0 -> {
                activity.overridePendingTransition(R.anim.right_to_current, R.anim.current_to_left)
            }

            1 -> {
                activity.overridePendingTransition(
                    R.anim.right_to_current,
                    R.anim.current_to_current
                )
            }
        }

    }


    private fun RxjavaErrorHandling() {
        RxJavaPlugins.setErrorHandler { e: Throwable? ->
            var error = e

            if (e is UndeliverableException) {
                error = e.cause
            }
            if (e is IOException) {
                return@setErrorHandler
            }
            if (e is InterruptedException) {
                return@setErrorHandler
            }
            if (e is NullPointerException || e is IllegalArgumentException) {
                Objects.requireNonNull(
                    Thread.currentThread().uncaughtExceptionHandler
                ).uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            if (e is IllegalStateException) {
                Objects.requireNonNull(
                    Thread.currentThread().uncaughtExceptionHandler
                ).uncaughtException(Thread.currentThread(), e)
                return@setErrorHandler
            }
            if (e != null) {
                Log.e(
                    "RxJava_HOOK",
                    "Undeliverable exception received, not sure what to do" + e.message
                )
            }
        }
    }
}