package com.example.myshoppal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.myshoppal.R
import com.example.myshoppal.databinding.ActivityAddEditAddressBinding
import com.example.myshoppal.firestore.FirestoreClass
import com.example.myshoppal.model.Address
import com.example.myshoppal.utils.Constants

class AddEditAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityAddEditAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnSubmitAddress.setOnClickListener {
            saveAddressToFirestore()
        }
        binding.rgType.setOnCheckedChangeListener{ _, checkedId ->
            if(checkedId == R.id.rb_other) {
                binding.tilOtherDetails.visibility = View.VISIBLE
            } else {
                binding.tilOtherDetails.visibility = View.GONE
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddEditAddressActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24)
        }
        binding.toolbarAddEditAddressActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(binding.etFullName.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etAddress.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFirestore() {
        // Here we get the text from editText and trim the space
        val fullName: String = binding.etFullName.text.toString().trim { it <= ' ' }
        val phoneNumber: String = binding.etPhoneNumber.text.toString().trim { it <= ' ' }
        val address: String = binding.etAddress.text.toString().trim { it <= ' ' }
        val zipCode: String = binding.etZipCode.text.toString().trim { it <= ' ' }
        val additionalNote: String = binding.etAdditionalNote.text.toString().trim { it <= ' ' }
        val otherDetails: String = binding.etOtherDetails.text.toString().trim { it <= ' ' }

        if (validateData()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                binding.rbHome.isChecked -> {
                    Constants.HOME
                }
                binding.rbOffice.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            FirestoreClass().addAddress(this, addressModel)
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.address_was_added_successfully_message), Toast.LENGTH_LONG).show()
        finish()
    }
}