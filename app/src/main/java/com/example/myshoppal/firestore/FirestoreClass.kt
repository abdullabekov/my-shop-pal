package com.example.myshoppal.firestore

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myshoppal.model.CartItem
import com.example.myshoppal.model.Product
import com.example.myshoppal.model.User
import com.example.myshoppal.ui.*
import com.example.myshoppal.ui.main.DashboardFragment
import com.example.myshoppal.ui.main.ProductsFragment
import com.example.myshoppal.utils.Constants.CART_ITEMS
import com.example.myshoppal.utils.Constants.LOGGED_IN_USERNAME
import com.example.myshoppal.utils.Constants.MY_PREFS
import com.example.myshoppal.utils.Constants.PRODUCTS
import com.example.myshoppal.utils.Constants.PRODUCT_ID
import com.example.myshoppal.utils.Constants.USERS
import com.example.myshoppal.utils.Constants.USER_ID
import com.example.myshoppal.utils.Constants.USER_PROFILE_IMAGE_PREFIX
import com.example.myshoppal.utils.Constants.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(registerActivity: RegisterActivity, user: User) {
        mFirestore.collection(USERS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnCompleteListener {
                registerActivity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                registerActivity.hideProgressDialog()
                Log.e(
                    registerActivity.javaClass.simpleName,
                    "Error while registering user", e
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }

    fun getCurrentUser(activity: Activity) {
        mFirestore.collection(USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, it.toString())
                val user = it.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    MY_PREFS, Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString(LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is BaseActivity -> activity.hideProgressDialog()
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while updating user details", it)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, uri: Uri, imageType: String) {
        val sRef = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis()
                    + "." + getFileExtension(activity, uri)
        )
        sRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata?.reference?.downloadUrl.toString()
                )
                taskSnapshot.metadata?.reference?.downloadUrl
                    ?.addOnSuccessListener {
                        Log.e("Downloadable Image URL", it.toString())
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(it.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(it.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener {
                when (activity) {
                    is UserProfileActivity -> activity.hideProgressDialog()
                    is AddProductActivity -> activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, it.message, it)
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, product: Product) {
        mFirestore.collection(PRODUCTS)
            .document()
            .set(product, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName, "Error while uploading the product details",
                    e
                )
            }
    }

    fun getProductsList(fragment: Fragment) {
        mFirestore.collection(PRODUCTS)
            .whereEqualTo(USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.e("ProductsList", querySnapshot.documents.toString())
                val productList = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Product::class.java).also {
                        it?.product_id = documentSnapshot.id
                    }
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productList)
                    }
                }
            }.addOnFailureListener {
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                        Log.e(
                            fragment.javaClass.simpleName,
                            "Error while loading products list.",
                            it
                        )
                    }
                }
            }
    }

    fun getDashboardItems(fragment: Fragment) {
        mFirestore.collection(PRODUCTS)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val products = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Product::class.java).also {
                        it?.product_id = documentSnapshot.id
                    }
                }

                when (fragment) {
                    is DashboardFragment -> {
                        fragment.successDashboardItemsList(products)
                    }
                }
            }
            .addOnFailureListener {
                when (fragment) {
                    is DashboardFragment -> {
                        fragment.hideProgressDialog()
                        Log.e(
                            fragment.javaClass.simpleName,
                            "Error while loading dashboard items.",
                            it
                        )
                    }
                }
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFirestore.collection(PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while deleting the product.", e)
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection(PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot.toObject(Product::class.java)?.let {
                    activity.productDetailsSuccess(it)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while loading product details.", e)
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, cart: CartItem) {
        mFirestore.collection(CART_ITEMS)
            .document()
            .set(cart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while loading product details.", e)
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {
        mFirestore.collection(CART_ITEMS)
            .whereEqualTo(USER_ID, getCurrentUserID())
            .whereEqualTo(PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNotEmpty()) activity.productExistsInCart() else activity.hideProgressDialog()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while loading product details.", e)
            }
    }

    fun getCartList(activity: CartListActivity) {
        mFirestore.collection(CART_ITEMS)
            .whereEqualTo(USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val cartItems = querySnapshot
                    .documents.mapNotNull { documentSnapshot ->
                        documentSnapshot.toObject(CartItem::class.java)
                            ?.also { it.id = documentSnapshot.id }
                    }
                activity.successCartItemsList(cartItems)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while loading cart items.", e)
            }
    }

    fun getAllProductsList(activity: CartListActivity) {
        activity.hideProgressDialog()
        mFirestore.collection(PRODUCTS)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val products = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Product::class.java)?.also {
                        it.product_id = documentSnapshot.id
                    }
                }
                activity.successProductsList(products)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while loading cart items.", e)
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFirestore.collection(CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(context.javaClass.simpleName, "Error while loading cart items.", e)
            }
    }

    fun updateCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFirestore.collection(CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.cartItemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(context.javaClass.simpleName, "Error while updating cart.", e)

            }
    }
}