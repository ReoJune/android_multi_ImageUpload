package com.example.sampleproject.ui.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.sampleproject.R
import com.example.sampleproject.adapter.GridAutofitLayoutManager
import com.example.sampleproject.adapter.ImageAdapter
import com.example.sampleproject.databinding.ImgMultiActivityBinding
import com.example.sampleproject.util.Status
import com.example.sampleproject.util.UriUtil
import com.example.sampleproject.viewmodel.ImageViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor.compress
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ImageActivity : AppCompatActivity(){

    private lateinit var binding: ImgMultiActivityBinding
    private lateinit var currentPhotoPath : String

    private val imageViewModel: ImageViewModel by viewModels()
    private var imgAdapter: ImageAdapter? = null
    private var selectedPaths = mutableListOf<File>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImgMultiActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        imgAdapter = ImageAdapter(this)
        binding.progressBar.bringToFront()
        binding.requestProgressBar.bringToFront()

        binding.recyclerView.apply {
            setHasFixedSize(true)
            val columnWidth = context.resources.getDimension(R.dimen.image_size).toInt()
            layoutManager =  GridAutofitLayoutManager(

                this@ImageActivity,
                columnWidth
            )
            adapter = imgAdapter
        }

        lifecycleScope.launchWhenStarted {

            imageViewModel.resource.observe(this@ImageActivity) {
                when (it.status) {

                    Status.SUCCESS -> {
                        Toast.makeText(applicationContext, "파일 업로드 완료", Toast.LENGTH_LONG).show()
                        binding.requestProgressBar.visibility = View.GONE
                        UriUtil.deleteTempFiles(cacheDir)
                        imgAdapter!!.clearAllImage()
                    }

                    Status.LOADING -> {
                        binding.requestProgressBar.visibility = View.VISIBLE
                    }

                    Status.ERROR -> {
                        Toast.makeText(applicationContext, "이미지 업로드 실패", Toast.LENGTH_LONG).show()
                        binding.requestProgressBar.visibility = View.GONE
                        errorAlert(it.message)
                    }
                }
            }

            imageViewModel.imageFile.observe(this@ImageActivity) {
                binding.uploadImgBtn.setOnClickListener { _ ->
                    imageViewModel.uploadImage(it)
                }
            }
        }

        val selectCarmeraActivityResult =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.progressBar.visibility = View.VISIBLE

                val photofile = File(currentPhotoPath)

                lifecycleScope.launch {
                    val file = UriUtil.getImageFromUri(this@ImageActivity, Uri.fromFile(photofile))
                    file?.let {

                        val compressFile = if(UriUtil.isFileCheck(this@ImageActivity,Uri.fromFile(photofile)))
                            compress(applicationContext,it)
                        else
                            it

                        selectedPaths.add(compressFile)
                    }

                    imageViewModel.sendImageFile(selectedPaths as ArrayList<File> /* = java.util.ArrayList<java.io.File> */)
                    imgAdapter!!.addSelectedImages(selectedPaths)
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    binding.progressBar.visibility = View.VISIBLE

                    val data: Intent? = result.data

                    if (data?.clipData != null) {
                        lifecycleScope.launch {
                            val count = data.clipData?.itemCount ?: 0

                            for (i in 0 until count) {
                                val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                                val file = UriUtil.getImageFromUri(this@ImageActivity, imageUri)

                                file?.let {
                                    val compressFile = if(UriUtil.isFileCheck(this@ImageActivity, imageUri))
                                        compress(applicationContext,it)
                                    else
                                        it
                                    selectedPaths.add(compressFile)
                                }
                            }
                            imageViewModel.sendImageFile(selectedPaths as ArrayList<File> /* = java.util.ArrayList<java.io.File> */)
                            imgAdapter!!.addSelectedImages(selectedPaths)
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                    //If single image selected
                    else if (data?.data != null) {
                        lifecycleScope.launch {
                            val imageUri: Uri? = data.data
                            val file = UriUtil.getImageFromUri(this@ImageActivity, imageUri)
                            file?.let {

                                val compressFile = if(UriUtil.isFileCheck(this@ImageActivity,imageUri))
                                    compress(applicationContext,it)
                                else
                                    it

                                selectedPaths.add(compressFile)
                            }

                            imageViewModel.sendImageFile(selectedPaths as ArrayList<File> /* = java.util.ArrayList<java.io.File> */)
                            imgAdapter!!.addSelectedImages(selectedPaths)
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }

        binding.pickImageButton.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,

                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.type = "*/*"
                    selectImagesActivityResult.launch(intent)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {}
            }).check()
        }

        binding.ImgClearBtn.setOnClickListener {
            imgAdapter!!.clearSelectedImages()
        }

        binding.CarmeraBtn.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,

                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                            takePictureIntent.resolveActivity(packageManager)?.also {

                                val photoFile: File? = try{
                                    createImageFile()
                                }catch(e:IOException){
                                    null
                                }
                                photoFile?.also{
                                    val photoURI : Uri = FileProvider.getUriForFile(
                                        this@ImageActivity,
                                        "org.techtown.capturepicture.fileprovider",
                                        it
                                    )
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                    selectCarmeraActivityResult.launch(takePictureIntent)
                                }
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {}
            }).check()
        }
    }

    private fun errorAlert(message: String?) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("에러메세지")
            .setMessage(message)
            .setPositiveButton("확인") { _, _ ->
            }
        builder.show()
    }

    @Throws(IOException::class)
    private fun createImageFile() : File{
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply{
            currentPhotoPath = absolutePath
        }
    }
}
