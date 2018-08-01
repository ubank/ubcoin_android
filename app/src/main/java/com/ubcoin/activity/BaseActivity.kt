package com.ubcoin.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.ubcoin.fragment.IFragmentBehaviorAware
import com.ubcoin.model.BaseApplicationModel
import com.ubcoin.switcher.FragmentSwitcher


/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseActivity : AppCompatActivity(), IActivity {

    var fragmentSwitcher: FragmentSwitcher? = null
    private var progressDialog: SweetAlertDialog? = null
    var errorDialog: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentSwitcher = FragmentSwitcher(this)
        supportFragmentManager.addOnBackStackChangedListener {
            if (!supportFragmentManager.fragments.isEmpty()) {
                supportFragmentManager.fragments.last()?.onResume()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentSwitcher = null
    }

    open fun showProgress(text: String, header: String) {
        hideDialogs()
        progressDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        with(progressDialog) {
            this!!.setCancelable(false)
            this.setTitleText(titleText).setContentText(confirmText).show()
        }
    }

    open fun hideDialogs() {
        progressDialog?.dismissWithAnimation()
        progressDialog = null
        errorDialog?.dismissWithAnimation()
        errorDialog = null
    }

    open fun showError(error: String) {
        hideDialogs()
        errorDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
        with(errorDialog) {
            this!!.setTitleText("Error").setContentText(error).showCancelButton(true).show()
        }
    }

    open fun processError(baseApplicationModel: BaseApplicationModel<Any>?) {
        hideDialogs()
        if (baseApplicationModel != null) {
            var errors = ""
            val errorValidations = baseApplicationModel.error?.errorValidations
            for (e in errorValidations!!) {
                errors += e.message + "\n"
            }
            showError(errors)
        }
    }

    override fun onBackPressed() {
        val iFragmentBehaviorAware = getCurrentFragment() as IFragmentBehaviorAware
        if (!iFragmentBehaviorAware.onBackPressed()) {
            performBack()
        }
    }

    private fun performBack() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        if (backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

}