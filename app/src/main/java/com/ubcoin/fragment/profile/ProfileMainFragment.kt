package com.ubcoin.fragment.profile

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.R.drawable.ic_back
import com.ubcoin.R.string.balance_placeholder
import com.ubcoin.R.string.eth_balance_placeholder
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.fragment.deals.DealsParentFragment
import com.ubcoin.fragment.transactions.MyBalanceFragment
import com.ubcoin.model.CryptoCurrency
import com.ubcoin.model.response.User
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.ProfileHolder
import com.ubcoin.utils.moneyFormat
import com.ubcoin.utils.moneyFormatETH
import io.reactivex.disposables.Disposable

/**
 * Created by Yuriy Aizenberg
 */
class ProfileMainFragment : FirstLineFragment() {

    private lateinit var txtProfileName: TextView
    //    private lateinit var imgProfilePhoto: ImageView
    private lateinit var txtProfileBalance: TextView
    private lateinit var txtProfileBalanceETH: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var disposable: Disposable? = null

    override fun getLayoutResId() = R.layout.fragment_profile_main


    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        txtProfileBalance = view.findViewById(R.id.txtProfileBalance)
        txtProfileBalanceETH = view.findViewById(R.id.txtProfileBalanceETH)
        txtProfileName = view.findViewById(R.id.txtProfileName)
//        imgProfilePhoto = view.findViewById(R.id.imgProfilePhoto)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            updateUserProfileBackground()
        }
        view.findViewById<View>(R.id.llProfileMain).setOnClickListener {
            getSwitcher()?.addTo(ProfileSettingsFragment::class.java)
        }
        view.findViewById<View>(R.id.llProfileBalance).setOnClickListener {
            getSwitcher()?.addTo(MyBalanceFragment::class.java)
        }

        view.findViewById<View>(R.id.llProfileBalanceETH).setOnClickListener {
            getSwitcher()?.addTo(MyBalanceFragment::class.java, MyBalanceFragment.getBundle(CryptoCurrency.ETH), true)
        }

        view.findViewById<View>(R.id.rlProfileDeals).setOnClickListener {
            getSwitcher()?.addTo(DealsParentFragment::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        setupUser()
        updateUserProfileBackground()
    }

    override fun getHeaderText() = R.string.menu_label_profile

    override fun getHeaderIcon() = ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    private fun updateUserProfileBackground() {
        cancelCurrentLoading()
        disposable = DataProvider.profile(
                object : SilentConsumer<User> {
                    override fun onConsume(t: User) {
                        swipeRefreshLayout.isRefreshing = false
                        ProfileHolder.user = t
                        setupUser()
                    }
                },
                object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                        handleException(t)
                    }
                })
    }

    override fun handleException(t: Throwable) {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        super.handleException(t)
    }

    private fun setupUser() {
        val user = ProfileHolder.user
        if (user == null) {
            activity?.onBackPressed()
            return
        }
        txtProfileName.text = user.name
        txtProfileBalance.text = (ProfileHolder.balance?.effectiveAmount ?: .0).moneyFormat()

        txtProfileBalanceETH.text = (ProfileHolder.balance?.effectiveAmountETH ?: .0).moneyFormatETH()

/*        val avatarUrl = user.avatarUrl
        if (avatarUrl != null && avatarUrl.isNotBlank()) {
            Picasso.get()
                    .load(avatarUrl)
                    .transform(CircleTransformation())
                    .placeholder(img_photo_placeholder)
                    .error(img_photo_placeholder)
                    .into(imgProfilePhoto)
        } else {
            Picasso.get().load(R.drawable.img_photo_placeholder)
                    .transform(CircleTransformation())
                    .into(imgProfilePhoto)
        }*/
    }

    private fun cancelCurrentLoading() {
        disposable?.dispose()
    }

}