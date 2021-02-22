package com.fragment.alcohol_category

import android.content.Context
import androidx.fragment.app.Fragment
import com.vuforia.engine.wet.databinding.AlcoholCategoryBinding

interface AlcoholCategoryContact {

    interface BaseView{
       fun getViewBinding():AlcoholCategoryBinding

        fun changeToggle(toggle:Boolean)

        fun setTotalCount(alcoholCount:Int)

    }

    interface BasePresenter{
        var view: BaseView
        var context:Context

        fun inintTabLayout(fragment: Fragment,currentItem:Int,toggle:String)

        fun getFragement(position:Int): Fragment?

        fun checkSort(position:Int,sort:String)

        fun checkLogin(context: Context)


        fun detach()

    }
}