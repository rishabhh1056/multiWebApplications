package com.example.multiwebapplications.fragaments



import androidx.lifecycle.ViewModel
import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.multiwebapplications.databinding.Fragment1Binding
import androidx.lifecycle.ViewModelProvider


class Fragment1 : Fragment() {

    private lateinit var binding: Fragment1Binding
    private val viewModel: YourViewModel by lazy {
        ViewModelProvider(requireActivity()).get(YourViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = Fragment1Binding.inflate(layoutInflater, container, false)

        if (!isInternetAvailable(requireContext()))
        {
            showDialog()
        }



        val webView = binding.gpt

        if (viewModel.currentUrl.isNotEmpty()) {
            webView.loadUrl(viewModel.currentUrl)
            webView.scrollY = viewModel.webViewScrollPosition

            Log.e(
                "msg",
                "msg in if url ${viewModel.currentUrl},   or ${viewModel.webViewScrollPosition}"
            )
        } else {
            webView.loadUrl("https://www.freepik.com/free-photos-vectors/wallpaper")
        }


        // binding.gpt.loadUrl("https://www.freepik.com/free-photos-vectors/wallpaper")
        binding.gpt.settings.javaScriptEnabled = true
        binding.gpt.settings.allowFileAccess = true
        binding.gpt.settings.allowFileAccessFromFileURLs = true
        binding.gpt.settings.allowUniversalAccessFromFileURLs = true
        binding.gpt.webViewClient = WebViewClient()

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
                REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        } else {
            // Permission already granted, proceed with the download
            binding.gpt.loadUrl("https://www.freepik.com/free-photos-vectors/wallpaper")
        }






        binding.gpt.setDownloadListener { url, _, _, mimeType, _ ->
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


        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.gpt.canGoBack()) {
                        binding.gpt.goBack()
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
            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with download
                    binding.gpt.loadUrl("https://www.freepik.com/free-photos-vectors/wallpaper")
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun downloadFile() {
        val request =
            DownloadManager.Request(Uri.parse("https://indianmemetemplates.com/meme-clips-for-youtube-video-editing/"))
        // Configure the request
        // ...
        val downloadManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)
    }


    override fun onPause() {
        super.onPause()
        // Save WebView state to ViewModel
        val webView = binding.gpt
        viewModel.currentUrl = webView.url ?: ""
        viewModel.webViewScrollPosition = webView.scrollY

        if (!isInternetAvailable(requireContext()))
        {
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
}






class YourViewModel : ViewModel() {
    var currentUrl: String = ""
    var webViewScrollPosition: Int = 0
    // Other WebView state variables you want to save
}