package com.mvffg.btwolib

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference


class BTwoLib(context: Context?) : WebView(context!!) {
    private val INPUT_FILE_REQUEST_CODE = 1
    protected var mRequestCodeFilePicker: Int = INPUT_FILE_REQUEST_CODE
    var mActivity: WeakReference<Activity>? = null
    var filePathCallbacks: ValueCallback<Array<Uri>>? = null
     var mListener: Listener? = null


    interface Listener {
        fun onPageStarted(url: String?, favicon: Bitmap?)
        fun onPageFinished(url: String?)
        fun onPageError(errorCode: Int, description: String?, failingUrl: String?)
        fun onDownloadRequested(
            url: String?,
            suggestedFilename: String?,
            mimeType: String?,
            contentLength: Long,
            contentDisposition: String?,
            userAgent: String?
        )
        fun onExternalPageRequest(url: String?)
    }



    init {
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        val webViewSet = settings
        webViewSet.javaScriptEnabled = true
        webViewSet.useWideViewPort = true
        webViewSet.loadWithOverviewMode = true
        webViewSet.allowFileAccess = true
        webViewSet.domStorageEnabled = true
        webViewSet.userAgentString = webViewSet.userAgentString.replace("; wv", "")
        webViewSet.javaScriptCanOpenWindowsAutomatically = true
        webViewSet.setSupportMultipleWindows(false)
        webViewSet.displayZoomControls = false
        webViewSet.builtInZoomControls = true
        webViewSet.allowFileAccess = true
        webViewSet.allowContentAccess = true
        webViewSet.setSupportZoom(true)
        webViewSet.pluginState = WebSettings.PluginState.ON
        webViewSet.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webViewSet.cacheMode = WebSettings.LOAD_DEFAULT
        webViewSet.allowContentAccess = true
        webViewSet.mediaPlaybackRequiresUserGesture = false




        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {

                val pm = context!!.packageManager
                val isInstalled = isPackageInstalled("org.telegram.messenger", pm)

                try {
                    if (URLUtil.isNetworkUrl(url)) {
                        return false
                    }
                    if (isInstalled) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "Application is not installed",
                            Toast.LENGTH_LONG
                        ).show()
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")
                            )
                    }
                    return true
                } catch (e: Exception) {
                    return false
                }
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                saveUrl(url)
            }
        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams
            ):Boolean {

                filePathCallbacks?.onReceiveValue(null)
                filePathCallbacks = filePathCallback

                try {
                    openChooser(fileChooserParams)
                } catch (e: java.lang.Exception) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                }
                return true
            }
        })
    }

    private fun openChooser(params: WebChromeClient.FileChooserParams) {

        val chooserIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        val intent = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, chooserIntent)
            putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        }
        (context as Activity).startActivityForResult(Intent.createChooser(intent, "File Chooser"), INPUT_FILE_REQUEST_CODE);
    }

    fun setListener(activity: Activity?, listener: Listener?) {
        setListener(activity, listener, INPUT_FILE_REQUEST_CODE)
    }

    private fun setListener(activity: Activity?, listener: Listener?, requestCodeFilePicker: Int) {
        mActivity = if (activity != null) {
            WeakReference(activity)
        } else {
            null
        }
        if (listener != null) {
            setListener(listener, requestCodeFilePicker)
        }
    }

    private fun setListener(listener: Listener, requestCodeFilePicker: Int) {
        mListener = listener
        mRequestCodeFilePicker = requestCodeFilePicker
    }

    fun profiter(link:String) {
        super.loadUrl(link)
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun onBackPressed(): Boolean {
        return if (canGoBack()) {
            if (exitexitexitexit) {
                stopLoading()
                loadUrl(urlfififif)
            }
            exitexitexitexit = true
            goBack()
            Handler(Looper.getMainLooper()).postDelayed({
                exitexitexitexit = false
            }, 2000)
            false
        } else {
            true
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == INPUT_FILE_REQUEST_CODE && (resultCode == RESULT_OK)) {

            if ((null == filePathCallbacks )) {
                return;
            } else {
                val dataString: String? = data?.dataString

                if (dataString != null) {
                    val result = arrayOf(Uri.parse(dataString))
                    filePathCallbacks?.onReceiveValue(result)
                    filePathCallbacks = null
                }
            }
        }
    }

    var exitexitexitexit = false
    var urlfififif = ""
    fun saveUrl(lurlurlurlurlur: String?) {
        if (!lurlurlurlurlur!!.contains("t.me")) {

            if (urlfififif == "") {
                if (context != null) {
                    urlfififif = context.getSharedPreferences(
                        "SP_WEBVIEW_PREFS",
                        AppCompatActivity.MODE_PRIVATE
                    ).getString(
                        "SAVED_URL",
                        lurlurlurlurlur
                    ).toString()
                }

                val spspspspsppspspsp =
                    context.getSharedPreferences(
                        "SP_WEBVIEW_PREFS",
                        AppCompatActivity.MODE_PRIVATE
                    )
                val ededededededed = spspspspsppspspsp?.edit()
                ededededededed?.putString("SAVED_URL", lurlurlurlurlur)
                ededededededed?.apply()
            }
        }
    }



}



