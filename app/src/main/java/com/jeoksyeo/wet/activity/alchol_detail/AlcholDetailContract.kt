package com.jeoksyeo.wet.activity.alchol_detail

import com.vuforia.engine.wet.databinding.AlcholDetailBinding

interface AlcholDetailContract {

    interface  BaseView{
        fun getView():AlcholDetailBinding

        fun setLike(isLike:Boolean)

    }

    interface BasePresenter{
        var view:BaseView
        fun executeLike(alcholId:String)

        fun cancelAlcholLike(alcholId:String)

    }
}