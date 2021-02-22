package com.example.myshoppal.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.example.myshoppal.utils.MSPTextView
import com.google.android.material.snackbar.Snackbar

open class BaseActivity() : AppCompatActivity() {
    private lateinit var progressDialog: Dialog

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
}