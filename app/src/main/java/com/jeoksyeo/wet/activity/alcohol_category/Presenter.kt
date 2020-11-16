package com.jeoksyeo.wet.activity.alcohol_category

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adapter.alcohol_category.GridViewPagerAdapter
import com.adapter.alcohol_category.ListViewPagerAdapter
import com.adapter.navigation.NavigationAdpater
import com.application.GlobalApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.error.ErrorManager
import com.fragment.alcohol_category.Fragment_Grid
import com.fragment.alcohol_category.Fragment_List
import com.google.android.material.tabs.TabLayoutMediator
import com.model.alcohol_category.AlcoholList
import com.model.navigation.NavigationItem
import com.service.ApiGenerator
import com.service.ApiService
import com.service.JWTUtil
import com.vuforia.engine.wet.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alcohol_category.view.*
import kotlinx.android.synthetic.main.main.view.*
import kotlinx.android.synthetic.main.navigation_header.view.*

class Presenter:AlcoholCategoryContact.BasePresenter {
    override lateinit var view: AlcoholCategoryContact.BaseView

    override fun inintTabLayout(context: Context) {
        if(context is FragmentActivity){
            view.getView().viewPager2Container.adapter = GridViewPagerAdapter(context)
            val lst = listOf<String>("전통주","맥주","와인","양주","사케")
            TabLayoutMediator(view.getView().tabLayoutAlcoholList,view.getView().viewPager2Container,
                TabLayoutMediator.TabConfigurationStrategy{ tab, position ->
                    val textView = TextView(context)
                    tab.customView =textView
                    textView.text = lst[position]
                    textView.textSize =16f
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
                    textView.setTextColor(context.resources.getColor(R.color.tabColor,null))
                    textView.gravity = Gravity.CENTER_HORIZONTAL
                }).attach()
        }
    }

    override fun getFragement(position: Int): Fragment? {
        var fragment:Fragment? =null
        if(view.getView().viewPager2Container.adapter is GridViewPagerAdapter){
            fragment= (view.getView().viewPager2Container.adapter as GridViewPagerAdapter)
                .getFragment(view.getView().viewPager2Container.currentItem)
        }
        else if (view.getView().viewPager2Container.adapter is ListViewPagerAdapter){
            fragment= (view.getView().viewPager2Container.adapter as ListViewPagerAdapter)
                .getFragment(view.getView().viewPager2Container.currentItem)
        }

        return fragment
    }

    override fun checkSort(position: Int,sort:String) {
        getFragement(position)?.let {fragment ->
            if(fragment is Fragment_Grid){
                if(fragment.getSort() != sort){
                    fragment.changeSort(sort)
                }
            }
            else if (fragment is Fragment_List){
                if(fragment.getSort() != sort){
                    fragment.changeSort(sort)
                }
            }
        }
    }

    override fun initNavigationItemSet(context: Context,activity:Activity) {
        JWTUtil.settingUserInfo(false)

        val lst = mutableListOf<NavigationItem>()
        lst.add(NavigationItem(R.mipmap.btn_top_setting, "설정"))
        lst.add(NavigationItem(R.mipmap.nv_profile, "내 프로필"))
        lst.add(NavigationItem(R.mipmap.navigation1_img, "내가 평가한 주류"))
        lst.add(NavigationItem(R.mipmap.navigation2_img, "나의 주류 레벨"))
        lst.add(NavigationItem(R.mipmap.navigation3_img, "내가 찜한 주류"))
        lst.add(GlobalApplication.userInfo.getProvider()?.let { NavigationItem(R.mipmap.navigation5_img, "로그아웃") }
            ?: NavigationItem(R.mipmap.navigation5_img, "로그인"))

        view.getView().categoryNavigation.navigationContainer.setHasFixedSize(true)
        view.getView().categoryNavigation.navigationContainer.layoutManager = LinearLayoutManager(context)
        view.getView().categoryNavigation.navigationContainer.adapter = NavigationAdpater(context,activity,lst
            ,GlobalApplication.userInfo.getProvider(),GlobalApplication.ACTIVITY_HANDLING_CATEGORY)
    }


    @SuppressLint("SetTextI18n")
    override fun checkLogin(context: Context) {
        JWTUtil.settingUserInfo(false)

        GlobalApplication.userInfo.getProvider()?.let {
            //유저 프로필 설정하는 화면 필요함
            view.getView().categoryDrawerLayout.category_navigation.navigation_header_Name.text=
                GlobalApplication.userInfo.nickName + "님 안녕하세요" +"\n Lv."+
                        GlobalApplication.userInfo.getLevel()+" "+
                        GlobalApplication.instance.getLevelName(GlobalApplication.userInfo.getLevel())
        }
        GlobalApplication.userInfo.getProfile()?.let {lst->
            Log.e("프로필 변경","변경")
            if(lst.isNotEmpty()){
                Glide.with(context)
                    .load(lst[lst.size-1].mediaResource?.small?.src.toString())
                    .apply(
                        RequestOptions()
                            .signature(ObjectKey("signature"))
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                    )
                    .into(view.getView().categoryNavigation.navigationHeader.navigationHeaderProfile)
            }
        }
    }
}