package com.ubcoin.fragment.profile

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.TheApplication
import com.ubcoin.adapter.GridItemOffsetDecoration
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.adapter.MarketListAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.fragment.filter.FiltersFragment
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.MarketListResponse
import com.ubcoin.model.response.User
import com.ubcoin.network.Api
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import io.reactivex.functions.Consumer
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


private const val LIMIT = 30

class SellerProfileFragment : BaseFragment() {
    private lateinit var user: User

    private lateinit var recyclerView: RecyclerView
    private lateinit var imgSellerProfile: ImageView
    private lateinit var txtUserName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvOnMarket: TextView
    private lateinit var llHeaderRight: RelativeLayout
    private lateinit var progressCenter: View
    private lateinit var progressBottom: View
    private lateinit var llNoItems: View
    private var marketListAdapter: MarketListAdapter? = null

    private var currentPage = 0
    private var isLoading = false
    private var isEndOfLoading = false

    companion object {
        fun getBundle(user: User): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(User::class.java.simpleName, user)
            return bundle
        }
    }

    private val isFavoriteProcessing = AtomicBoolean(false)

    override fun getLayoutResId() = R.layout.fragment_profile_seller

    fun Long.toRegisterDateFormat() : String {
        if(this <= 30)
            return resources.getQuantityString(R.plurals.txt_days, this.toInt(), this)
        if(this <= 365)
            return resources.getQuantityString(R.plurals.txt_month, this.toInt()/30, this/30)
        return resources.getQuantityString(R.plurals.txt_month, this.toInt()/365, this/365)
    }

    fun getDaysBetweenDate(date: Date): Long{
        val cal = Calendar.getInstance()
        cal.time = date
        val msDiff = Calendar.getInstance().getTimeInMillis() - cal.getTimeInMillis()
        val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff)
        return daysDiff
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        user = arguments?.getSerializable(User::class.java.simpleName) as User
        txtProfileHeader = view.findViewById(R.id.txtProfileHeader)
        txtProfileHeader!!.text = user!!.name!!
        recyclerView = view.findViewById(R.id.rvMarketList)
        txtUserName = view.findViewById(R.id.txtUserName)
        tvLocation = view.findViewById(R.id.tvLocation)
        tvOnMarket = view.findViewById(R.id.tvOnMarket)
        imgSellerProfile = view.findViewById(R.id.imgSellerProfile)
        llHeaderRight = view.findViewById(R.id.llHeaderRight)

        llHeaderRight.setOnClickListener{
            val shareUrl = user.name + " " + getString(R.string.text_on_ubicoin_market) + ": " + user.shareLink?:""
            val message = "$shareUrl"
            TheApplication.instance.openShareIntent(message, activity!!)
        }
        txtUserName.text = user.name!!
        tvLocation.text = user.location
        tvOnMarket.text = getString(R.string.text_on_market) + ": " + getDaysBetweenDate(user!!.createdDate!!).toRegisterDateFormat()

        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)

        llNoItems = view.findViewById(R.id.llNoItems)
        marketListAdapter = MarketListAdapter(activity!!)
        marketListAdapter?.setHasStableIds(true)
        marketListAdapter?.favoriteListener = object : MarketListAdapter.IFavoriteListener {
            override fun onFavoriteTouch(data: MarketItem, position: Int) {
                if (ProfileHolder.isAuthorized()) {
                    processFavorite(data, position, !data.favorite)
                } else {
                    showNeedToRegistration()
                }
            }
        }

        val gridLayoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)

        recyclerView.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(gridLayoutManager) {
            override fun onLoadMore() {
                loadData()
            }

        })

        recyclerView.run {
            setHasFixedSize(true)
            addItemDecoration(GridItemOffsetDecoration(activity!!, R.dimen.decoration_grid_offset))
            adapter = marketListAdapter
            layoutManager = gridLayoutManager
            val fadeInAnimator = FadeInAnimator(OvershootInterpolator(1f))
            fadeInAnimator.addDuration = 300
            itemAnimator = fadeInAnimator
        }

        val avatarUrl = user?.avatarUrl
        if (avatarUrl == null) {
            imgSellerProfile.setImageResource(R.drawable.img_profile_default)
        } else {
            GlideApp.with(activity!!).load(avatarUrl)
                    .override(R.dimen.detailsSubProfileHeight, R.dimen.detailsSubProfileHeight)
                    .centerInside()
                    .transform(RoundedCorners(context!!.resources.getDimensionPixelSize(R.dimen.detailsSubProfileHeight)))
                    .placeholder(R.drawable.img_profile_default)
                    .error(R.drawable.img_profile_default)
                    .into(imgSellerProfile)
        }

        loadData()

        marketListAdapter?.recyclerTouchListener = object : IRecyclerTouchListener<MarketItem> {
            override fun onItemClick(data: MarketItem, position: Int) {
                getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(data, position), true)
            }
        }
    }

    fun loadData() {
        if (isLoading || isEndOfLoading) return



        if (marketListAdapter!!.isEmpty()) {
            progressCenter.visibility = View.VISIBLE
        } else {
            progressBottom.visibility = View.VISIBLE
        }

        val onSuccess = object : SilentConsumer<MarketListResponse> {
            override fun onConsume(t: MarketListResponse) {
                val data = t.data
                progressCenter.visibility = View.GONE
                progressBottom.visibility = View.GONE
                if (data.size < LIMIT) {
                    isEndOfLoading = true
                }
                marketListAdapter!!.addData(t.data)
                if (marketListAdapter!!.isEmpty()) {
                    llNoItems.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    llNoItems.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                currentPage++
            }

        }
        val onError = object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                progressCenter.visibility = View.GONE
                progressBottom.visibility = View.GONE
                handleException(t)
            }

        }

        DataProvider.getSellerMarketItemsList(user!!.id!!, LIMIT, currentPage, onSuccess, onError)
    }

    private fun processFavorite(marketItem: MarketItem, position: Int, markAsFavorite: Boolean) {
        if (isFavoriteProcessing.get()) return
        isFavoriteProcessing.set(true)
        if (markAsFavorite) {
            DataProvider.favorite(marketItem.id, successConsumer(marketItem, position), Consumer { handleException(it) })
        } else {
            DataProvider.unfavorite(marketItem.id, successConsumer(marketItem, position), Consumer { handleException(it) })
        }
    }

    private fun successConsumer(marketItem: MarketItem, position: Int): SilentConsumer<Response<Unit>> {
        return object : SilentConsumer<Response<Unit>> {
            override fun onConsume(t: Response<Unit>) {
                marketItem.favorite = !marketItem.favorite
                try {
                    marketListAdapter?.notifyItemChanged(position)
                } catch (e: Exception) {
                    marketListAdapter?.notifyDataSetChanged()
                }
                isFavoriteProcessing.set(false)
            }

        }
    }

    override fun handleException(t: Throwable) {
        hideViewsQuietly(progressCenter, progressBottom)
        isLoading = false
        isFavoriteProcessing.set(false)
        super.handleException(t)
    }

    override fun onResume() {
        super.onResume()
        callItemRemoved()
    }

    private fun callItemRemoved() {
        val favoriteIdForRemove = TheApplication.instance.favoriteIdForRemove
        if (marketListAdapter == null || favoriteIdForRemove == null) return
        val data = marketListAdapter!!.data
        if (data.isEmpty()) return

        synchronized(data) {
            var index = -1
            for ((i, it) in data.withIndex()) {
                if (it.id == favoriteIdForRemove) {
                    index = i
                    break
                }
            }
            if (index != -1) {
                data[index].favorite = false
                marketListAdapter?.run {
                    if (index == 0) {
                        notifyDataSetChanged()
                    } else {
                        try {
                            notifyItemChanged(index)
                        } catch (e: Exception) {
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

}