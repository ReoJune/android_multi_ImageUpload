package com.example.sampleproject.ui.view.activity

import android.Manifest
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sampleproject.R
import com.example.sampleproject.adapter.GridAutofitLayoutManager
import com.example.sampleproject.adapter.ImageAdapter
import com.example.sampleproject.databinding.ImgMultiActivityBinding
import com.example.sampleproject.util.Status
import com.example.sampleproject.viewmodel.ImageViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor.compress
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ImageActivity : AppCompatActivity(){

    private lateinit var binding: ImgMultiActivityBinding
    private val imageViewModel: ImageViewModel by viewModels()

    private var imagesArray     = ArrayList<Image>()
    private var imagesArrayFile = ArrayList<File>()
    private var imagesArrayCompressFile = ArrayList<File>()
    private var imgAdapter: ImageAdapter? = null

    private val launcher = registerImagePicker {
        if (it.isNotEmpty()){
            imagesArray = it
            imgAdapter!!.setData(it)

            listClear()
            lifecycleScope.launch {
                for(i in imagesArray.indices){
                    imagesArrayFile.add(i, File(getRealPathFromURI(imagesArray[i].uri)))
                    imagesArrayCompressFile.add(i,compress(applicationContext, imagesArrayFile[i]))
                }
                imageViewModel.sendImageFile(imagesArrayCompressFile)
            }
        }
    }

    private fun listClear() {
        if (imagesArrayFile.isNotEmpty()) {
            imagesArrayFile.clear()
        }

        if (imagesArrayCompressFile.isNotEmpty()){
            imagesArrayCompressFile.clear()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImgMultiActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgAdapter = ImageAdapter(this)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            val columnWidth = context.resources.getDimension(R.dimen.image_size).toInt()
            layoutManager =  GridAutofitLayoutManager(
                this@ImageActivity,
                columnWidth
            )
            adapter = imgAdapter
        }


        binding.progressBar.bringToFront()
        binding.pickImageButton.setOnClickListener {

            Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {

                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                            imageSetting()
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
                        Toast.makeText(applicationContext, "이미지 업로드 완료", Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE

                    }

                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    Status.ERROR -> {
                        Toast.makeText(applicationContext, "이미지 업로드 실패", Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }

            imageViewModel.imageFile.observe(this@ImageActivity) {
                binding.uploadImgBtn.setOnClickListener { _ ->
                    imageViewModel.uploadImage(it)
                }
            }
        }
    }

    private fun imageSetting() {
        val config = ImagePickerConfig(
            statusBarColor = "#00796B",
            isLightStatusBar = false,
            toolbarColor = "#009688",
            toolbarTextColor = "#FFFFFF",
            toolbarIconColor = "#FFFFFF",
            backgroundColor = "#000000",
            progressIndicatorColor = "#009688",
            selectedIndicatorColor = "#2196F3",
            isCameraOnly = false,
            isMultipleMode = true,
            isFolderMode = true,
            doneTitle = "DONE",
            folderTitle = "Albums",
            imageTitle = "Photos",
            isShowCamera = true,
            isShowNumberIndicator = true,
            isAlwaysShowDoneButton = true,
            rootDirectory = RootDirectory.DCIM,
            subDirectory = "Example",
            maxSize = 20,
            limitMessage = "20장의 사진만 선택가능",
            selectedImages = imagesArray
        )

        launcher.launch(config)
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)

            cursor = contentResolver.query(contentUri, proj, null, null, null)

            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            cursor.moveToFirst()

            cursor.getString(columnIndex)

        } catch (e: Exception) {
            ""
        } finally {
            cursor?.close()
        }
    }
}
