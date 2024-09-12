package fr.idnow.imagecapture

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import fr.idnow.imagecapture.databinding.ActivityMainBinding
import fr.idnow.imagecapture.presentation.utils.createPhotoFile
import fr.idnow.imagecapture.presentation.utils.saveImage
import fr.idnow.imagecapture.presentation.utils.showToast
import fr.idnow.imagecapture.presentation.viewmodels.QuoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var quoteViewModel: QuoteViewModel

    private var imageCapture: ImageCapture? = null
    private var capturedImageBitmap: Bitmap? = null

    val appContainer = AppContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initUI()
        checkPermissionsAndStartCamera()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initViewModel() {
        quoteViewModel = QuoteViewModel(appContainer.getSingleQuoteUseCase, Dispatchers.IO)
    }

    private fun initUI() {
        binding.takePictureBtn.setOnClickListener { takePhoto() }
        binding.downloadBtn.setOnClickListener {
            capturedImageBitmap?.let { bitmap ->
                saveImage(this, bitmap)
                resetCamera()
            }
        }
    }

    private fun checkPermissionsAndStartCamera() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && allPermissionsGranted()) {
            startCamera()
        } else {
            showToast(this, "Permissions not granted by the user.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(CAMERA_LOG_TAG, "Binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createPhotoFile(this)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(CAMERA_LOG_TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    capturedImageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    binding.imagePreview.setImageBitmap(capturedImageBitmap)
                    binding.imagePreview.rotation =
                        90F // FIXME: Rotation issue should not be handled here
                    displayQuote()
                    showImagePreview()
                }
            }
        )
    }

    private fun displayQuote() {
        lifecycleScope.launch {
            quoteViewModel.quoteUiState.collect { uiState ->
                when (uiState) {
                    is QuoteViewModel.QuoteUiState.Success -> binding.tvQuote.text =
                        uiState.quote.text

                    is QuoteViewModel.QuoteUiState.Error -> binding.tvQuote.text =
                        uiState.exception.localizedMessage
                }
            }
        }
    }

    private fun showImagePreview() {
        binding.apply {
            previewView.isVisible = false
            takePictureBtn.isVisible = false

            imagePreview.isVisible = true
            downloadBtn.isVisible = true
            tvQuote.isVisible = true
        }
    }

    private fun resetCamera() {
        binding.apply {
            previewView.isVisible = true
            takePictureBtn.isVisible = true

            imagePreview.isVisible = false
            downloadBtn.isVisible = false
            tvQuote.isVisible = false
        }
        capturedImageBitmap = null
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val CAMERA_LOG_TAG = "CAMERA"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}