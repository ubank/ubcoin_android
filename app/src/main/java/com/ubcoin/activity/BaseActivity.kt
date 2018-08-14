package com.ubcoin.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ubcoin.fragment.IFragmentBehaviorAware
import com.ubcoin.model.BaseApplicationModel
import com.ubcoin.switcher.FragmentSwitcher


/**
 * Created by Yuriy Aizenberg
 */
abstract class BaseActivity : AppCompatActivity(), IActivity {

    var fragmentSwitcher: FragmentSwitcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getResourceId())
        fragmentSwitcher = FragmentSwitcher(this)
        supportFragmentManager.addOnBackStackChangedListener {
            if (!supportFragmentManager.fragments.isEmpty()) {
                val last = supportFragmentManager.fragments.last()
                if (last != null) {
                    last.onResume()
                } else {
                    supportFragmentManager.fragments[0]?.onResume()
                }
            }
        }
    }

    override fun getContainer(): View {
        return findViewById(getFragmentContainerId())
    }

    override fun getCurrentFragment(): Fragment? {
        if (supportFragmentManager == null) return null
        return supportFragmentManager.findFragmentById(getFragmentContainerId())
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentSwitcher = null
    }

    @Deprecated("Move to fragments")
    open fun showProgress(text: String, header: String) {
        hideDialogs()
    }

    @Deprecated("Move to fragments")
    open fun hideDialogs() {
    }

    @Deprecated("Move to fragments")
    open fun showError(error: String) {
        hideDialogs()
    }

    @Deprecated("Move to fragments")
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
        if (getCurrentFragment() == null) return
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