package com.ubcoin.fragment.messages

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cocosw.bottomsheet.BottomSheet
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.adapter.ChatMessageAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.ChatMessage
import com.ubcoin.model.ChatMessageType
import com.ubcoin.model.response.DealItemWrapper
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import java.util.*
import com.pubnub.api.PubNub
import com.pubnub.api.PNConfiguration
import kotlin.jvm.java
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.enums.PNStatusCategory
import android.widget.EditText
import android.widget.Toast
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.google.gson.JsonObject
import com.pubnub.api.models.consumer.history.PNHistoryResult
import com.ubcoin.model.response.DealItem
import com.ubcoin.model.response.TgLinks
import com.ubcoin.model.response.User
import com.ubcoin.network.DataProvider
import com.ubcoin.network.NetworkModule
import com.ubcoin.network.SilentConsumer
import com.ubcoin.preferences.ThePreferences
import io.reactivex.Emitter
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.*
import kotlin.collections.ArrayList


class ChatFragment : BaseFragment() {

    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var rvMessages: RecyclerView
    private lateinit var progressCenter: View
    private lateinit var progressBottom: View
    var socket: Socket? = null

    private lateinit var id: String
    private lateinit var item: DealItem
    private lateinit var user: User

    private var bottomSheet: BottomSheet? = null
    private var fromCamera = false
    var lastLoadedMessage: Long = -1
    var page = 0
    lateinit var channelName : String
    lateinit var etMessage : EditText
    var historyLoaded : Boolean = false
    var loading : Boolean = false
    var limit = 10

    val SEND_MESSAGE = "sendMessage"
    val HISTORY = "history"
    val TYPING = "typing"
    val ENTER_ROOM = "enterRoom"
    val LEAVE_ROOM = "leaveRoom"

    override fun getLayoutResId() = R.layout.fragment_chat
    override fun getHeaderIcon() = R.drawable.ic_close

    companion object {

        fun getBundle(id: String, item: DealItem, user: User): Bundle {
            val bundle = Bundle()
            bundle.putString("id", id)
            bundle.putSerializable(DealItem::class.java.simpleName, item)
            bundle.putSerializable(User::class.java.simpleName, user)
            return bundle
        }
    }

    override fun isFooterShow() = false

    override fun onIconClick() {
        super.onIconClick()
        activity!!.onBackPressed()
    }

    override fun onViewInflated(view: View) {
        super.onViewInflated(view)

        id = arguments?.getString("id") as String
        item = arguments?.getSerializable(DealItem::class.java.simpleName) as DealItem
        user = arguments?.getSerializable(User::class.java.simpleName) as User
        etMessage = view.findViewById(R.id.etMessage)

        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)

        val socketUrl = "https://qa.ubcoin.io"
        val hostnameVerifier = object : HostnameVerifier {
            override fun verify(p0: String?, p1: SSLSession?): Boolean {
                return true
            }
        }
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager{
            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }

            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {

            }

            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {

            }
        })
        val trustManager = trustAllCerts[0] as X509TrustManager

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, null)
        val sslSocketFactory = sslContext.getSocketFactory()

        val okHttpClient = OkHttpClient.Builder()
                .hostnameVerifier(hostnameVerifier)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build()

        val opts = IO.Options()
        opts.callFactory = okHttpClient
        opts.webSocketFactory = okHttpClient
        socket = IO.socket(socketUrl, opts)
        socket!!.on(Socket.EVENT_CONNECT, {
            var k = 0
            k++

        }).on(SEND_MESSAGE, {
            var k = 0
            k++
        }).on(HISTORY, {
            var k = 0
            k++
        }).on(TYPING, {
            var k = 0
            k++
        }).on(Socket.EVENT_DISCONNECT, {
            var k = 0
            k++
        })

        socket!!.connect()

        val messageJsonObject = JsonObject()
        messageJsonObject.addProperty("token", "eyJhbGciOiJkaXIiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2In0..-gofj4ux938dpFqzRDRAgw.T9DtglumDfp1MtZ_gLNGvrqzVf3nyv7JhDNneWiUhX96RpUb031TVdl6smmQ9aTS3bY8vHex4BdojP09Xy562FXqnm5zTFnmvgqT1lP-lN5S_tJy2M9XfiuFKD3FEJ7p.x0sraTD3X9sWHEnpqtp4zQ")
        messageJsonObject.addProperty("purchaseId", "purchase131323")

        socket!!.emit(ENTER_ROOM, messageJsonObject.toString(),  {
            var k = 0
            k++
        })

        rvMessages = view.findViewById<RecyclerView>(R.id.rvChatMessages)

        chatMessageAdapter = ChatMessageAdapter(context!!)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        rvMessages.layoutManager = layoutManager
        rvMessages.adapter = chatMessageAdapter

        channelName = id

        view.findViewById<TextView>(R.id.txtHeader)?.text = user.name
        rvMessages.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(layoutManager) {
            override fun onLoadMore() {
                loadData()
            }

        })

        if (chatMessageAdapter!!.isEmpty()) {
            progressCenter.visibility = View.VISIBLE
        } else {
            progressBottom.visibility = View.VISIBLE
        }

        GlideApp.with(context!!).load(item!!.images?.get(0))
                .centerCrop()
                .placeholder(R.drawable.img_profile_default)
                .error(R.drawable.img_profile_default)
                .into(view.findViewById<ImageView>(R.id.ivImage))
        view.findViewById<TextView>(R.id.tvName).text = item!!.title
        view.findViewById<View>(R.id.rlPhoto).setOnClickListener {openPhotoDialog()}
        view.findViewById<View>(R.id.rlSend).setOnClickListener {send()}
        view.findViewById<View>(R.id.llItem).setOnClickListener {
            //getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(deal.item), true)
        }


        scrollToBottom()

        initPubnub()
    }

    fun loadData() {
        if(!historyLoaded) {

            if(loading)
                return
            startLoading()

            var rev = false
            if(lastLoadedMessage > -1)
                rev = true


        }
    }

    fun createMessage(date: Date, data: String, type:ChatMessageType) : ChatMessage{
        var message = ChatMessage()
        message.date = date
        message.type = type
        message.data = data
        return message
    }

    override fun getHeaderText(): Int {
        return super.getHeaderText()
    }

    fun openPhotoDialog() {
        bottomSheet?.dismiss()
        bottomSheet = BottomSheet.Builder(activity!!)
                .title(getString(R.string.select_action))
                .darkTheme()
                .sheet( R.menu.menu_pick_new_photo)
                .listener { dialog, which ->
                    when (which) {
                        R.id.camera, R.id.gallery -> {
                            dialog?.dismiss()
                            fromCamera = which == R.id.camera
                            if (checkPermissions()) {
                                takeImage()
                            } else {
                                requestPermissionsInternal()
                            }
                        }
                        R.id.cancel -> {
                            dialog?.dismiss()
                        }
                    }
                }.build()
        bottomSheet?.show()
    }

    private fun takeImage() {
        if (fromCamera) {
            startCameraIntent()
        } else {
            startGalleryIntent()
        }
    }

    override fun onCameraCaptured(filePath: String) {
        super.onCameraCaptured(filePath)
        getImageUrl(filePath)
    }

    override fun onGalleryCaptured(filePath: String) {
        super.onGalleryCaptured(filePath)
        getImageUrl(filePath)
    }

    fun getImageUrl(filePath : String){
        var imageUrls = ArrayList<String>()
        imageUrls.add(filePath)

        DataProvider.uploadFiles(imageUrls,
                object : SilentConsumer<TgLinks> {
                    override fun onConsume(t: TgLinks) {
                        sendImage(t.tgLinks.get(0).url)
                    }
                },
                object : SilentConsumer<Throwable> {
                    override fun onConsume(t: Throwable) {
                        handleException(t)
                    }
                })
    }

    fun sendImage(url : String){
        if(loading)
            return
        startLoading()

        val messageJsonObject = JsonObject()
        messageJsonObject.addProperty("type", "image")
        messageJsonObject.addProperty("content", url)
        messageJsonObject.addProperty("publisher", ProfileHolder.user!!.id)

        socket!!.emit(SEND_MESSAGE, messageJsonObject,  {
            var k = 0
            k++
        })
    }

    fun send(){
        if(etMessage.text.toString().length == 0)
            return;

        if(loading)
            return
        startLoading()

        val messageJsonObject = JsonObject()
        messageJsonObject.addProperty("type", "message")
        messageJsonObject.addProperty("content", etMessage.text.toString())
        messageJsonObject.addProperty("publisher", ProfileHolder.user!!.id)

        socket!!.emit(SEND_MESSAGE, messageJsonObject,  {
            var k = 0
            k++
        })
    }

    fun initPubnub(){
        if(loading)
            return
        startLoading()

    }

    fun addMessageFromJSON(message : JsonObject, timetoken : Long, history : Boolean)
    {
        if(!message.has("publisher") || !message.has("type") || !message.has("content"))
            return
        val content = message.get("content").asString
        val type = message.get("type").asString
        val publisher = message.get("publisher").asString

        var chatMessageType: ChatMessageType? = null

        if (publisher != null && type != null) {
            when(type) {
                "message" -> {
                    if (publisher.equals(ProfileHolder.user!!.id))
                        chatMessageType = ChatMessageType.MyMessage
                    else
                        chatMessageType = ChatMessageType.OpponentMessage
                }

                "image" -> {
                    if (publisher.equals(ProfileHolder.user!!.id))
                        chatMessageType = ChatMessageType.MyImage
                    else
                        chatMessageType = ChatMessageType.OpponentImage
                }
            }
            if (chatMessageType != null)
                addMessage(createMessage(Date(Math.ceil((timetoken.toDouble() / 10000)).toLong()), content, chatMessageType), history)
        }
    }

    private fun checkAddDate(messageDate : Date, history : Boolean){
        if(history)
        {
            if(chatMessageAdapter.itemCount > 0) {
                val previousDate = chatMessageAdapter.getItem(chatMessageAdapter.itemCount - 1).date!!
                if(!DateFormat.format("dd", messageDate).equals(DateFormat.format("dd", previousDate)))
                {
                    chatMessageAdapter.addData(createMessage(previousDate, "", ChatMessageType.Date))
                }
            }
        }
        else
        {
            if(chatMessageAdapter.itemCount == 0)
                chatMessageAdapter.addData(createMessage(messageDate, "", ChatMessageType.Date), 0)
            else {
                val previousDate = chatMessageAdapter.getItem(0).date!!
                if(!DateFormat.format("dd", messageDate).equals(DateFormat.format("dd", previousDate))) {
                    chatMessageAdapter.addData(createMessage(messageDate, "", ChatMessageType.Date), 0)
                }
            }
        }
    }

    fun addMessage(message : ChatMessage, history : Boolean)
    {
        if(rvMessages != null)
            rvMessages.post(Runnable {
                if(rvMessages != null) {
                    checkAddDate(message.date!!, history)
                    if (history) {
                        chatMessageAdapter.addData(message)
                    } else {
                        chatMessageAdapter.addData(message, 0)
                        scrollToBottom()
                    }
                }
            })
    }

    private fun scrollToBottom() {
        if(rvMessages != null)
            rvMessages.post(Runnable { if(rvMessages != null) rvMessages.scrollToPosition(0) })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun startLoading(){
        loading = true
        if (chatMessageAdapter!!.isEmpty()) {
            progressCenter.visibility = View.VISIBLE
        } else {
            progressBottom.visibility = View.VISIBLE
        }
    }

    fun endLoading(){
        hideViewsQuietly(progressCenter, progressBottom)
        loading = false
    }
}