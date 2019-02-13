package com.ubcoin.fragment.messages

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ubcoin.R
import com.ubcoin.adapter.ChatListAdapter
import com.ubcoin.adapter.IRecyclerTouchListener
import com.ubcoin.fragment.FirstLineFragment
import com.ubcoin.fragment.transactions.MyBalanceFragment
import com.ubcoin.model.ChatItem
import com.ubcoin.model.CryptoCurrency
import com.ubcoin.model.response.DealItem
import com.ubcoin.model.response.DealItemWrapper
import com.ubcoin.model.response.DealsListResponse
import com.ubcoin.model.response.RelatedPurchasesResponse
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.preferences.ThePreferences
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import kotlinx.android.synthetic.main.fragment_messages.*

private const val LIMIT = 30

class MessagesParentFragment : FirstLineFragment() {

    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var rvMessages: RecyclerView
    private lateinit var progressCenter: View
    private lateinit var progressBottom: View

    private var currentPage = 0
    private var isLoading = false
    private var isEndOfLoading = false

    override fun getLayoutResId() = R.layout.fragment_messages
    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)
        rvMessages = view.findViewById<RecyclerView>(R.id.rvMessages)

        chatListAdapter = ChatListAdapter(context!!)
        chatListAdapter.setHasStableIds(true)

        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)
        val layoutManager = LinearLayoutManager(activity)
        rvMessages.layoutManager = layoutManager
        rvMessages.adapter = chatListAdapter

        rvMessages.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(layoutManager) {
            override fun onLoadMore() {
                //loadData()
            }
        })


        chatListAdapter.recyclerTouchListener = object : IRecyclerTouchListener<ChatItem> {
            override fun onItemClick(data: ChatItem, position: Int) {
                getSwitcher()?.addTo(ChatFragment::class.java, ChatFragment.getBundle(data.item!!.id, data.user), true)
            }
        }

        loadData()
    }

    fun loadData() {
        if (isLoading || isEndOfLoading) return

        if (chatListAdapter.isEmpty()) {
            progressCenter.visibility = View.VISIBLE
        } else {
            progressBottom.visibility = View.VISIBLE
        }

        val onSuccess = object : SilentConsumer<List<ChatItem>> {
            override fun onConsume(t: List<ChatItem>) {
                hideProgress()
                chatListAdapter.addData(t)
                if (chatListAdapter.isEmpty()) {
                    llNoItems.visibility = View.VISIBLE
                    rvMessages.visibility = View.GONE
                } else {
                    llNoItems.visibility = View.GONE
                    rvMessages.visibility = View.VISIBLE
                }
            }

        }
        val onError = object : SilentConsumer<Throwable> {
            override fun onConsume(t: Throwable) {
                handleException(t)
            }

        }
        //DataProvider.getPurchases(LIMIT, currentPage, onSuccess, onError)
        DataProvider.getChatList("accessToken=" + ThePreferences().getToken(), onSuccess, onError)
    }

    private fun hideProgress() {
        hideProgressDialog()
        hideViewsQuietly(progressCenter, progressBottom)
        isLoading = false
    }

    override fun handleException(t: Throwable) {
        hideProgress()
        super.handleException(t)
    }

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }
}