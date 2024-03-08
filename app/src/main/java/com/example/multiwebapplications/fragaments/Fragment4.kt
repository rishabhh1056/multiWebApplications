package com.example.multiwebapplications.fragaments

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.multiwebapplications.R
import com.example.multiwebapplications.databinding.Fragment3Binding
import com.example.multiwebapplications.databinding.Fragment4Binding


class Fragment4 : Fragment() {

    private lateinit var binding: Fragment4Binding
    private val viewModel: Fragment4.web4 by lazy {
        ViewModelProvider(requireActivity()).get(Fragment4.web4::class.java)
    }

    private val fileChooserRequestCode = 123
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = Fragment4Binding.inflate(inflater, container, false)

        // Check internet connectivity
        if (!isInternetAvailable(requireContext())) {
            showDialog()
        }

        val webView = binding.website4

        // Restore WebView state from ViewModel
        if (viewModel.currentUrl.isNotEmpty()) {
            webView.loadUrl(viewModel.currentUrl)
            webView.scrollY = viewModel.webViewScrollPosition
        } else {
            webView.loadUrl("https://pallyy.com/tools/image-caption-generator")
        }

        // Enable JavaScript and File Access
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        // Set WebView Client
        webView.webViewClient = WebViewClient()

        // Set WebChromeClient for file selection
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"

                startActivityForResult(intent, fileChooserRequestCode)
                return true
            }
        }
        webView.setDownloadListener { url, _, _, mimeType, _ ->
            try {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setMimeType(mimeType)
                request.setDescription("Downloading file...")
                request.setTitle(URLUtil.guessFileName(url, null, mimeType))
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    URLUtil.guessFileName(url, null, mimeType)
                )
                val dm =
                    requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(context, "Downloading File", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error- ${e.message.toString()}", Toast.LENGTH_SHORT).show()
            }
        }


        // Check if permission is not granted
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(
                context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Fragment4.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        } else {
            // Permission already granted, proceed with the download
            webView.loadUrl("https://pallyy.com/tools/image-caption-generator")
        }


        // Check and request WRITE_EXTERNAL_STORAGE permission if needed
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Fragment4.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        } else {
            // Permission granted, proceed with loading URL
            webView.loadUrl("https://pallyy.com/tools/image-caption-generator")
        }

        // Handle back press
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })



        return binding.root
    }

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Fragment4.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with download
                    binding.website4.loadUrl("https://pallyy.com/tools/image-caption-generator")
                    Toast.makeText(context, "code in if part", Toast.LENGTH_SHORT).show()
                    Log.e("msg", "msg in if part")
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun downloadFile() {
        val request =
            DownloadManager.Request(Uri.parse("https://pallyy.com/tools/image-caption-generator"))
        // Configure the request
        // ...
        val downloadManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)
    }


    override fun onPause() {
        super.onPause()
        // Save WebView state to ViewModel
        val webView = binding.website4
        viewModel.currentUrl = webView.url ?: ""
        viewModel.webViewScrollPosition = webView.scrollY

        if (!isInternetAvailable(requireContext())) {
            showDialog()
        }
        Log.e(
            "Call OnPUse",
            " Onpuse msg ${viewModel.currentUrl} ,,,  ${viewModel.webViewScrollPosition}"
        )
        // Other WebView state variables can be saved similarly
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }


    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Internet Issue")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setMessage("PLEASE CONNECT WITH INTERNET")
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == fileChooserRequestCode) {

            if (resultCode == Activity.RESULT_OK) {
                val result = data?.data
                fileChooserCallback?.onReceiveValue(arrayOf(result ?: Uri.EMPTY))
                fileChooserCallback = null
            } else {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = null
            }

        }


    }
    class web4 : ViewModel() {
        var currentUrl: String = ""
        var webViewScrollPosition: Int = 0
        // Other WebView state variables you want to save
    }
}