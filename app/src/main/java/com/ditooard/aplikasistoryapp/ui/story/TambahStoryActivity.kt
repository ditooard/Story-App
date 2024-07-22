package com.ditooard.aplikasistoryapp.ui.story

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ditooard.aplikasistoryapp.R
import com.ditooard.aplikasistoryapp.databinding.ActivityTambahStoryBinding
import com.ditooard.aplikasistoryapp.ui.home.HomeActivity
import com.ditooard.aplikasistoryapp.ui.main.UserViewModel
import com.ditooard.aplikasistoryapp.ui.main.UserPreferences
import com.ditooard.aplikasistoryapp.ui.main.dataStore
import com.ditooard.aplikasistoryapp.viewmodel.CustomViewModelFactory
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class TambahStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTambahStoryBinding
    private lateinit var token: String
    private val viewModel: TambahStoryViewModel by lazy {
        ViewModelProvider(this)[TambahStoryViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = resources.getString(R.string.post_users)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupListeners()

        val preferences = UserPreferences.getInstance(dataStore)
        val userViewModel =
            ViewModelProvider(this, CustomViewModelFactory(preferences))[UserViewModel::class.java]

        userViewModel.getToken().observe(this) {
            token = it
        }

        userViewModel.getNama().observe(this) {
            binding.tvUsers.text = StringBuilder(getString(R.string.post_as)).append(" ").append(it)
        }

        viewModel.message.observe(this) {
            showToast(it)
        }

        viewModel.loadingStatus.observe(this) {
            showLoading(it)
        }
    }

    private var currentFile: File? = null

    private fun setupListeners() {
        binding.btnPostStory.setOnClickListener {
            if (currentFile == null) {
                showToast(resources.getString(R.string.warningAddImage))
                return@setOnClickListener
            }

            val description = binding.tvDes.text.toString().trim()
            if (description.isEmpty()) {
                binding.tvDes.error = resources.getString(R.string.warningAddDescription)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val compressedFile = compressImage(currentFile!!)
                val requestImageFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    compressedFile.name,
                    requestImageFile
                )

                val descriptionPart = description.toRequestBody("text/plain".toMediaType())
                viewModel.uploadImage(imageMultipart, descriptionPart, token)
            }
        }

        binding.cameraButton.setOnClickListener {
            openCamera()
        }

        binding.galleryButton.setOnClickListener {
            openGallery()
        }
    }

    private suspend fun compressImage(file: File): File {
        var compressedFile = file
        var fileSize = file.length()

        while (fileSize > 1 * 1024 * 1024) {
            compressedFile = withContext(Dispatchers.Default) {
                Compressor.compress(applicationContext, file)
            }
            fileSize = compressedFile.length()
        }

        return compressedFile
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@TambahStoryActivity,
                getString(R.string.package_name),
                it
            )
            currentFile = it
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherCamera.launch(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherGallery.launch(chooser)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val file = currentFile ?: return@registerForActivityResult
            val result = BitmapFactory.decodeFile(file.path)
            binding.imageStoryUpload.setImageBitmap(result)
            binding.tvDes.requestFocus()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data ?: return@registerForActivityResult
            currentFile = uriToFile(selectedImage, this@TambahStoryActivity)
            binding.imageStoryUpload.setImageURI(selectedImage)
            binding.tvDes.requestFocus()
        }
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val tempFile = createTempFile(context)

        contentResolver.openInputStream(selectedImg)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }
        return tempFile
    }


    private fun showToast(message: String) {
        Toast.makeText(
            this@TambahStoryActivity,
            StringBuilder(getString(R.string.message)).append(message),
            Toast.LENGTH_SHORT
        ).show()

        if (message == "Story created successfully") {
            val intent = Intent(this@TambahStoryActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarAddStory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp: String = SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    companion object {
        const val FILENAME_FORMAT = "MMddyyyy"
    }
}
