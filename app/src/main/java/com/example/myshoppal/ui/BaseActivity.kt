package com.example.myshoppal.ui

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.myshoppal.R
import com.example.myshoppal.utils.MSPTextView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope

open class BaseActivity() : AppCompatActivity() {
    private lateinit var progressDialog: Dialog
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenCreated {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                @Suppress("DEPRECATION")
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }
    }

    protected fun hideStatusBar() {

    }

    fun showSnackBar(message: String, isErrorMessage: Boolean) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (isErrorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.snackBarError
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.snackBarSuccess
                )
            )
        }
        snackBar.show()
    }

    fun showProgressDialog(text: String) {
        progressDialog = Dialog(this@BaseActivity)
        with(progressDialog) {
            setContentView(R.layout.dialog_progress)
            findViewById<MSPTextView>(R.id.tv_progress_text).text = text
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(
            this, getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler(mainLooper).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}