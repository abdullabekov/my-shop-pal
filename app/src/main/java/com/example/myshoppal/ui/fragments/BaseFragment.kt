package com.example.myshoppal.ui.fragments

import android.app.Dialog
import androidx.fragment.app.Fragment
import com.example.myshoppal.R
import com.example.myshoppal.utils.MSPTextView

open class BaseFragment : Fragment() {
    private lateinit var progressDialog: Dialog

    fun showProgressDialog(text: String) {
        progressDialog = Dialog(requireContext())
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.findViewById<MSPTextView>(R.id.tv_progress_text).text = text
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }
}