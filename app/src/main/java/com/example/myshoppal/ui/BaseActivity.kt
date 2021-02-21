package com.example.myshoppal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.myshoppal.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity(layout: Int) : AppCompatActivity(layout) {
    fun showErrorSnackBar(message: String, isErrorMessage: Boolean) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if(isErrorMessage) {
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
}