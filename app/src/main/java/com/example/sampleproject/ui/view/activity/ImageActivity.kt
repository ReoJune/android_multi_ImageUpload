package com.example.sampleproject.ui.view.activity

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sampleproject.R
import com.example.sampleproject.databinding.ImgActivityBinding
import com.example.sampleproject.util.Status
import com.example.sampleproject.util.getFile
import com.example.sampleproject.viewmodel.ImageViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor.compress
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ImageActivity : AppCompatActivity(){

    private lateinit var binding: ImgActivityBinding
    private val imageViewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImgActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previewImage.setImageResource(R.drawable.ic_launcher_foreground)

        binding.pickImageButton.setOnClickListener {

            Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        ImagePicker.with(this@ImageActivity)
                            .galleryOnly().compress(1024)
                            .createIntent {
                                    intent -> startForProfileImageResult.launch(intent)
                            }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {}

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {}

                }).check()
        }

        lifecycleScope.launchWhenStarted {
            imageViewModel.resource.observe(this@ImageActivity) {
                when (it.status) {

                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE

                        Toast.makeText(applicationContext, "Image uploaded successfully", Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }

            imageViewModel.imageFile.observe(this@ImageActivity) {
                binding.uploadImgBtn.setOnClickListener { _ ->
//                    it.getContentIfNotHandled()?.let {
//                        imageViewModel.uploadImage(it)
//                    }

                    imageViewModel.uploadImage(it.peekContent())
                }
            }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {

                    lifecycleScope.launch {
                        val fileUri = data?.data

                        binding.previewImage.setImageURI(fileUri)

                        var file: File = getFile(data)!!

                        var compressedImageFile = compress(applicationContext, file)
                        imageViewModel.sendImageFile(compressedImageFile)
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(applicationContext, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }else -> {
                    Toast.makeText(applicationContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
}