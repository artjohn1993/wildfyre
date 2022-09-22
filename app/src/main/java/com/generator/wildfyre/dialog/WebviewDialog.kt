package com.generator.wildfyre.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Message
import android.view.Window
import android.webkit.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.generator.wildfyre.R
import com.generator.wildfyre.adapter.LoaderAdapter

class WebviewDialog(var activity: Activity) {
    var dialog = Dialog(activity, android.R.style.Theme_NoTitleBar_Fullscreen)
    init {
        dialog.window?.setBackgroundDrawableResource(R.color.transparent_black)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.webview_dialog)
    }

    fun showDialog( url : String) {

        var cancel = dialog.findViewById<TextView>(R.id.closeButton)
        var webview = dialog.findViewById<WebView>(R.id.webviewDialog)
        webview.webViewClient = LoaderAdapter.MyBrowser(activity, null)
        webview.webChromeClient = WebChromeClient()
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.javaScriptEnabled = true
        webview.clearCache(true)
        webview.clearHistory()
        webview.clearFormData()
        webview.clearSslPreferences()

        var setting = webview.settings
        setting.cacheMode = WebSettings.LOAD_NO_CACHE
        setting.javaScriptCanOpenWindowsAutomatically = true

        webview.loadUrl(url)
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun createWindow(resultMsg: Message?) {
        var webview = dialog.findViewById<WebView>(R.id.webviewDialog)
        webview.webViewClient = LoaderAdapter.MyBrowser(activity, null)
        webview.webChromeClient = WebChromeClient()
        webview.settings.javaScriptCanOpenWindowsAutomatically = true
        webview.settings.javaScriptEnabled = true
        webview.settings.pluginState = WebSettings.PluginState.ON
        webview.clearCache(true)
        webview.clearHistory()
        webview.clearFormData()
        webview.clearSslPreferences()

        var setting = webview.settings
        setting.cacheMode = WebSettings.LOAD_NO_CACHE
        setting.javaScriptCanOpenWindowsAutomatically = true
        setting.setSupportMultipleWindows(true)
        setting.javaScriptEnabled = true

        var transport : WebView.WebViewTransport = resultMsg?.obj as WebView.WebViewTransport
        transport.webView = webview
        resultMsg.sendToTarget()
        webview.webViewClient = LoaderAdapter.MyBrowser(activity, null)
        webview.webChromeClient = WebChromeClient()

        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    class MyBrowser : WebViewClient() {

        override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: String
        ): Boolean {
            view?.loadUrl(request)
            return true
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            println(request.toString())
            println(error?.description)
            println(error?.errorCode)
        }
    }
}