package com.ubcoin.fragment

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.Crashlytics
import com.ubcoin.R
import com.ubcoin.preferences.ThePreferences
import com.ubcoin.activity.BaseActivity
import com.ubcoin.activity.IActivity
import com.ubcoin.activity.MainActivity
import com.ubcoin.model.ChatItem
import com.ubcoin.model.event.MessagesUpdateWrapper
import com.ubcoin.network.HttpRequestException
import com.ubcoin.network.NetworkConnectivityException
import com.ubcoin.network.RxUtils
import com.ubcoin.pub.devrel.easypermissions.EasyPermissions
import com.ubcoin.pub.devrel.easypermissions.PermissionRequest
import com.ubcoin.switcher.FragmentSwitcher
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.collapse
import com.ubcoin.utils.expand
import com.ubcoin.view.menu.MenuBottomView
import io.reactivex.Maybe
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED


/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseFragment : Fragment(), IFragmentBehaviorAware {

    private val agreementUrl: String = "https://ubcoin.io/user-agreement"
    private val expandCollapseDuration = 200L

    val noHeaderObject = -1

    open fun getHeaderText() = noHeaderObject

    open fun getHeaderIcon() = noHeaderObject

    private var materialDialog: MaterialDialog? = null
    private var progressDialog: MaterialDialog? = null

    private var headerIcon: ImageView? = null
    private var llHeaderImage: View? = null
    var txtProfileHeader: TextView? = null

    //Media support here
    companion object {
        private const val PERMISSIONS_REQUEST = 30000
        private const val CAMERA_INTENT = 30001
        private const val GALLERY_INTENT = 30002

        private var fileName: String? = null

        private val perms = arrayOf(CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    }

    fun checkPermissions(): Boolean {
        return EasyPermissions.hasPermissions(activity!!, perms)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, object : EasyPermissions.PermissionCallbacks {
            override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
                if (checkPermissions()) {
                    onPermissionsGranted()
                }
            }

            override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
            }

            override fun onRequestPermissionsResult(p0: Int, p1: Array<out String>, p2: IntArray) {
            }

        })
    }

    fun requestPermissionsInternal() {
        val build = PermissionRequest.Builder(this, PERMISSIONS_REQUEST, perms).build()
        EasyPermissions.requestPermissions(build)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSIONS_REQUEST && resultCode == Activity.RESULT_OK) {
            if (checkPermissions()) onPermissionsGranted()
        }
        if (requestCode == CAMERA_INTENT && resultCode == Activity.RESULT_OK) {
            val file = File(Environment.getExternalStorageDirectory(), fileName)
            if (file.exists() && file.canRead()) {
                onCameraCaptured(file.absolutePath)
            } else {
                onCameraFailure()
            }
        }
        if (requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                onGalleryFailure()
            } else {
                resolveFilePath(data)
            }
        }
    }

    private fun resolveFilePath(data: Intent) {
        Maybe.create<String> {
            try {
                val pickedImage = data.data
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = activity?.contentResolver?.query(pickedImage, filePath, null, null, null)
                cursor?.moveToFirst()
                val imagePath = cursor?.getString(cursor.getColumnIndex(filePath[0])) ?: ""
                cursor?.close()
                it.onSuccess(imagePath)
            } catch (e: Exception) {
                it.onError(e)
            } finally {
                it.onComplete()
            }
        }
                .compose(RxUtils.applyMaybe())
                .subscribe({ onGalleryCaptured(it) }, { onCameraFailure() })
    }

    open fun onPermissionsGranted() {

    }

    fun startGalleryIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, GALLERY_INTENT)
    }

    fun startCameraIntent() {
        val packageName = activity!!.applicationContext.packageName
        val imageName = "$packageName.${System.currentTimeMillis()}.jpg"
        val file = File(Environment.getExternalStorageDirectory(), imageName)
        val uri = FileProvider.getUriForFile(activity!!, "$packageName.provider", file)
        fileName = imageName

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)

        startActivityForResult(intent, CAMERA_INTENT)
    }

    open fun onGalleryCaptured(filePath: String) {

    }

    open fun onCameraCaptured(filePath: String) {

    }

    open fun onGalleryFailure() {

    }

    open fun onCameraFailure() {

    }

    private fun checkPermissionInternal(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED
    }

    open fun isFirstLineFragment() = false

    abstract fun getLayoutResId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutResId(), container, false)
        onViewInflated(view)
        onViewInflated(view, savedInstanceState)
        if (getTopHeaderTextId() != noHeaderObject) {
            txtProfileHeader = view.findViewById(getTopHeaderTextId())
        }
        if (getTopLeftIconId() != noHeaderObject) {
            headerIcon = view.findViewById(getTopLeftIconId())
        }
        if (getTopLeftLayoutId() != noHeaderObject) {
            llHeaderImage = view.findViewById(getTopLeftLayoutId())
        }

        return view
    }

    open fun onViewInflated(view: View) {

    }

    open fun onViewInflated(view: View, savedInstanceState: Bundle?) {

    }

    open fun getTopLeftIconId() = R.id.imgHeaderLeft

    open fun getTopLeftLayoutId() = R.id.llHeaderLeft

    open fun getTopHeaderTextId() = R.id.txtHeader

    override fun onResume() {
        super.onResume()
        hideKeyboard()
        changeActivityAttributes()
        if (isFooterShow()) {
            showFooter()
        } else {
            hideFooter()
        }
    }

    fun getSwitcher(): FragmentSwitcher? {
        val activity = activity
        if (activity != null && activity is BaseActivity) return activity.fragmentSwitcher
        return null
    }

    fun showUserAgreement() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(agreementUrl)
        if (intent.resolveActivity(activity?.packageManager) != null) {
            startActivity(intent)
        } else {
            TODO()
        }
    }

    fun hideFooter() {
        toggleFooter(false, activity as IActivity)
    }

    fun showFooter() {
        toggleFooter(true, activity as IActivity)
    }

    open fun isFooterShow() = true

    private fun toggleFooter(isVisible: Boolean, iActivity: IActivity) {
        val footer = iActivity.getFooter()
        val container = iActivity.getContainer()
        if (footer == null) return
        (footer as MenuBottomView).run {
            if (!isExpanded && isVisible) {
                expand(expandCollapseDuration)
                isExpanded = true
            } else if (isExpanded && !isVisible) {
                collapse(expandCollapseDuration)
                isExpanded = false
            }
            container.requestLayout()
        }
    }

    private fun changeActivityAttributes() {
        if (getHeaderText() != noHeaderObject && txtProfileHeader != null) {
            txtProfileHeader?.text = getString(getHeaderText())
        }
        if (getHeaderIcon() != noHeaderObject) {
            headerIcon?.setImageResource(getHeaderIcon())
            llHeaderImage?.setOnClickListener {
                onIconClick() }
        }
    }

    fun hideKeyboard() {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun showKeyboard(edtText: EditText, context: Context) {
        edtText.requestFocus()
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    open fun onIconClick() {

    }

    protected open fun handleException(t: Throwable) {
        Crashlytics.logException(t)
        try {
            hideSweetAlertDialog()
            when (t) {
                is HttpRequestException -> {
                    val errorCode = t.errorCode
                    if (errorCode == HTTP_UNAUTHORIZED || errorCode == HTTP_BAD_REQUEST) {
                        if (onUnauthorized(t)) return
                        if (errorCode == HTTP_UNAUTHORIZED) {
                            ThePreferences().setToken(null)
                            ThePreferences().clearProfile()
                            ProfileHolder.logout()
                            return
                        }
                    }
                    if (!handleByChild(t)) {
                        processHttpRequestException(t)
                    }
                }
                is NetworkConnectivityException -> {
                    onNoNetworkException(t)
                }
                else -> processThrowable(t)
            }
        } catch (e: Exception) {
            Log.e(javaClass.name, """${e.message}""", e)
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onNoNetworkException(exception: NetworkConnectivityException) {
        showSweetAlertDialog(getString(R.string.error), getString(R.string.no_network_error))
    }

    protected open fun onUnauthorized(httpRequestException: HttpRequestException) : Boolean {
        ThePreferences().clearProfile()
        ThePreferences().clearPrefs()
        ProfileHolder.logout()
        return false;
    }

    protected open fun handleByChild(httpRequestException: HttpRequestException) = false

    private fun processHttpRequestException(httpRequestException: HttpRequestException) {
        if (httpRequestException.isServerError()) {
            showSweetAlertDialog(getString(R.string.server_says_error_title), httpRequestException.toServerErrorString())
        } else {
            processThrowable(RuntimeException(httpRequestException.throwable))
        }
    }

    private fun processThrowable(throwable: Throwable) {
        Crashlytics.logException(throwable)
        showSweetAlertDialog(getString(R.string.no_network_error))
    }

    private fun hideSweetAlertDialog() {
        materialDialog?.hide()
    }

    fun showSweetAlertDialog(title: String, message: String) {
        activity?.run {
            materialDialog = MaterialDialog.Builder(this).title(title).content(message).build()
            materialDialog?.show()
        }

    }

    fun showSweetAlertDialog(message: String) {
        activity?.run {
            materialDialog = MaterialDialog.Builder(this).content(message).build()
            materialDialog?.show()
        }

    }

    protected fun hideViewsQuietly(vararg v: View?) {
        v.run {
            v.forEach {
                it?.visibility = View.GONE
            }
        }
    }

    fun showSweetAlertDialog(@StringRes titleRes: Int, @StringRes messageRes: Int) {
        showSweetAlertDialog(getString(titleRes), getString(messageRes))
    }

    protected fun showProgressDialog(@StringRes titleRes: Int, @StringRes messageRes: Int) {
        showProgressDialog(getString(titleRes), getString(messageRes))
    }

    protected fun showProgressDialog(title: String, message: String) {
        activity?.run {
            hideProgressDialog()
            progressDialog = MaterialDialog.Builder(this)
                    .title(title)
                    .content(message)
                    .progress(true, 0)
                    .cancelable(false)
                    .build()
            progressDialog?.show()

        }
    }

    protected fun hideProgressDialog() {
        progressDialog?.hide()
    }

    protected fun showNeedToRegistration() {
        activity?.run {
            MaterialDialog.Builder(this)
                    .title(getString(R.string.title))
                    .content(R.string.need_to_logged_in)
                    .build()
                    .show()
        }
    }

    open fun subscribeOnMessageUpdate(messageUpdateWrapper: MessagesUpdateWrapper){
        (activity as MainActivity).menuBottomView.setNeedsUpdate()
    }

    open fun subscribeOnDealUpdate(id: String){
        (activity as MainActivity).menuBottomView.setNeedsUpdate()
    }
}