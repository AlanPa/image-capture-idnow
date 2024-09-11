package fr.idnow.imagecapture.presentation

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import fr.idnow.imagecapture.MainActivity
import fr.idnow.imagecapture.databinding.FragmentFirstBinding
import fr.idnow.imagecapture.presentation.viewmodels.QuoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private var capturedImageBitmap: Bitmap? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var quoteViewModel: QuoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        cameraExecutor = Executors.newSingleThreadExecutor()

        val appContainer = (activity as MainActivity).appContainer

        quoteViewModel = QuoteViewModel(appContainer.getSingleQuoteUseCase, Dispatchers.IO)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.takePictureBtn.setOnClickListener {
            takePhoto()
        }
        binding.downloadBtn.setOnClickListener {
            capturedImageBitmap?.let { bitmap ->
                saveImage(bitmap)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this.requireContext(), // FIXME
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        this.activity?.let { activity ->
            ContextCompat.checkSelfPermission(
                activity.baseContext, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext()) // FIXME

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e("CAMERA", "Binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this.requireContext())) // FIXME
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.FRENCH)
            .format(System.currentTimeMillis())

        val photoFile = File(context?.externalCacheDirs?.firstOrNull(), "$name.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(
                this.requireContext() // FIXME
            ),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CAMERA", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

                    capturedImageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    binding.imagePreview.setImageBitmap(capturedImageBitmap)
                    // FIXME image turned 90° to the side

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
                    is QuoteViewModel.QuoteUiState.Success -> {
                        binding.tvQuote.text = uiState.quote.text
                    }

                    is QuoteViewModel.QuoteUiState.Error -> binding.tvQuote.text =
                        uiState.exception.localizedMessage
                }
            }
        }
    }

    private fun showImagePreview() {
        binding.apply {
            previewView.visibility = View.GONE
            imagePreview.visibility = View.VISIBLE
            takePictureBtn.visibility = View.GONE
            downloadBtn.visibility = View.VISIBLE
            tvQuote.visibility = View.VISIBLE
        }
    }

    private fun resetCamera() {
        binding.apply {
            previewView.visibility = View.VISIBLE
            imagePreview.visibility = View.GONE
            takePictureBtn.visibility = View.VISIBLE
            downloadBtn.visibility = View.GONE
            tvQuote.visibility = View.GONE
        }
        capturedImageBitmap = null
    }

    private fun saveImage(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                SimpleDateFormat(FILENAME_FORMAT, Locale.FRENCH)
                    .format(System.currentTimeMillis())
            )
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/IdNow/ImageCapture")
            }
            // TODO deal with Android API <= 28
        }

        val imageUri = this.requireActivity().contentResolver.insert( // FIXME
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        if (imageUri != null) {
            val fos: OutputStream? = this.requireActivity().contentResolver.openOutputStream(imageUri)

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.flush()

                Toast.makeText(
                    this.requireContext(), // FIXME
                    "Image downloaded to gallery",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        else {
            Toast.makeText(
                this.requireContext(), // FIXME
                "Image not downloaded to gallery",
                Toast.LENGTH_LONG
            ).show()
        }
        resetCamera()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}