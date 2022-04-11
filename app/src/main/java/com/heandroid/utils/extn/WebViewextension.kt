package com.heandroid.utils.extn

import android.webkit.WebView

fun WebView.loadSetting(url : String?){
    this.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
        builtInZoomControls = true
        displayZoomControls = false
        setSupportZoom(true)
        defaultTextEncodingName = "utf-8"
        loadUrl(url?:"")
    }
}


fun WebView.loadString(url : String?){
    this.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
        builtInZoomControls = true
        displayZoomControls = false
        setSupportZoom(true)
        defaultTextEncodingName = "utf-8"
        loadData(url?:"","text/html; charset=utf-8", "UTF-8")
    }
}