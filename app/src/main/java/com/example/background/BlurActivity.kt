package com.example.background

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.example.background.databinding.ActivityBlurBinding

private const val TAG = "BlurActivity"

class BlurActivity : AppCompatActivity() {

    private val viewModel: BlurViewModel by viewModels {
        BlurViewModel.BlurViewModelFactory(
            application
        )
    }
    private lateinit var binding: ActivityBlurBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getPermissions()
        binding.goButton.setOnClickListener { viewModel.applyBlur(blurLevel) }
        viewModel.outputWorkInfo.observe(this, workInfoObserver())
        binding.seeFileButton.setOnClickListener {
            viewModel.outputUri?.let {
                val actionView = Intent(Intent.ACTION_VIEW, it)
                actionView.resolveActivity(packageManager)?.run {
                    startActivity(actionView)
                }
            }
        }
    }

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }
            val workInfo = it[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()
                val outUri = workInfo.outputData.getString(KEY_IMAGE_URI)
                if (!outUri.isNullOrEmpty()) {
                    viewModel.setOutputUri(outUri)
                    binding.seeFileButton.visibility = View.VISIBLE
                }
            } else {
                showWorkInProgress()
            }
        }
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            goButton.visibility = View.GONE
            seeFileButton.visibility = View.GONE
        }
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        with(binding) {
            progressBar.visibility = View.GONE
            cancelButton.visibility = View.GONE
            goButton.visibility = View.VISIBLE
        }
    }

    private val blurLevel: Int
        get() =
            when (binding.radioBlurGroup.checkedRadioButtonId) {
                R.id.radio_blur_lv_1 -> 1
                R.id.radio_blur_lv_2 -> 2
                R.id.radio_blur_lv_3 -> 3
                else -> 1
            }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getPermissions() {
        val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        Log.d(TAG, "requesting permissions")
        this.requestPermissions(permission, 100)
    }
}
