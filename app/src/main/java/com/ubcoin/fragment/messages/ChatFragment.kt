package com.ubcoin.fragment.messages

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import com.cocosw.bottomsheet.BottomSheet
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.adapter.ChatMessageAdapter
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.ChatMessage
import com.ubcoin.model.ChatMessageType
import com.ubcoin.utils.EndlessRecyclerViewOnScrollListener
import com.ubcoin.utils.ProfileHolder
import java.util.*
import kotlin.jvm.java
import com.google.gson.JsonObject
import com.ubcoin.fragment.market.MarketDetailsFragment
import com.ubcoin.model.response.DealItem
import com.ubcoin.model.response.MarketItem
import com.ubcoin.model.response.TgLinks
import com.ubcoin.model.response.User
import com.ubcoin.network.DataProvider
import com.ubcoin.network.NetworkModule
import com.ubcoin.network.SilentConsumer
import com.ubcoin.preferences.ThePreferences
import com.ubcoin.utils.getDateWithServerTimeStamp
import io.reactivex.functions.Consumer
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.security.cert.X509Certificate
import java.text.ParseException
import java.text.SimpleDateFormat
import javax.net.ssl.*
import kotlin.collections.ArrayList


class ChatFragment : BaseFragment() {

    private lateinit var rlPhoto: View
    private lateinit var rlSend: View
    private lateinit var tvName: TextView
    private lateinit var txtHeader: TextView
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var rvMessages: RecyclerView
    private lateinit var progressCenter: View
    private lateinit var progressBottom: View
    private lateinit var llItem: LinearLayout
    private lateinit var ivImage: ImageView
    lateinit var etMessage : EditText
    private var bottomSheet: BottomSheet? = null

    var socket: Socket? = null

    var itemId:String? = null
    private lateinit var item: MarketItem
    var opponent: User? = null

    private lateinit var layoutManager: LinearLayoutManager
    private var fromCamera = false
    var lastLoadedMessageDate: String? = null
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
        fun getBundle(itemId: String, opponent: User?): Bundle {
            val bundle = Bundle()
            bundle.putSerializable("itemId", itemId)
            bundle.putSerializable("opponent", opponent)
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

        itemId = arguments?.getString("itemId")
        opponent = arguments?.getSerializable("opponent") as User?

        etMessage = view.findViewById(R.id.etMessage)
        progressCenter = view.findViewById(R.id.progressCenter)
        progressBottom = view.findViewById(R.id.progressBottom)
        llItem = view.findViewById(R.id.llItem)
        txtHeader = view.findViewById(R.id.txtHeader)
        tvName = view.findViewById(R.id.tvName)
        rlPhoto = view.findViewById(R.id.rlPhoto)
        rlSend = view.findViewById(R.id.rlSend)
        ivImage = view.findViewById(R.id.ivImage)

        rvMessages = view.findViewById<RecyclerView>(R.id.rvChatMessages)

        chatMessageAdapter = ChatMessageAdapter(context!!)
        layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        rvMessages.layoutManager = layoutManager
        rvMessages.adapter = chatMessageAdapter

        loadItem()
    }

    fun loadItem(){
        startLoading()
        DataProvider.getMarketItemById(itemId!!, Consumer {
            item = it
            initItem()
            endLoading()
        }, Consumer {
            handleException(it)
            endLoading()
        })
    }

    fun initItem(){
        GlideApp.with(context!!).load(item.images?.get(0))
                .centerCrop()
                .placeholder(R.drawable.img_profile_default)
                .error(R.drawable.img_profile_default)
                .into(ivImage)
        tvName.text = item.title
        llItem.setOnClickListener {
            getSwitcher()?.addTo(MarketDetailsFragment::class.java, MarketDetailsFragment.getBundle(item.id), true)
        }
        llItem.visibility = View.VISIBLE
        initChat()
    }

    fun initChat(){
        val socketUrl = NetworkModule.getChatUrl()
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
        socket!!.on(SEND_MESSAGE, {
            var jsonObj = it[0] as JSONObject
            var userName = jsonObj.get("userName").toString()
            if(!userName.equals("Server")) {
                endLoading()
                addMessageFromJSON(jsonObj.get("msg") as JSONObject, jsonObj.get("date").toString().getDateWithServerTimeStamp(), false)
            }
            else {
                endLoading()
                loadData()
            }
        }).on(HISTORY, {
            var array = it[0] as JSONArray

            for(i in 0..(array.length() - 1)) {
                var jsonObj = array[i] as JSONObject
                lastLoadedMessageDate = jsonObj.get("date").toString()
                var str = jsonObj.get("msg").toString()
                try {
                    val obj = JSONObject(str)
                    addMessageFromJSON(obj, jsonObj.get("date").toString().getDateWithServerTimeStamp(), true)

                } catch (t: Throwable) {
                }
            }

            if(array.length() < limit)
            {
                historyLoaded = true
                if(lastLoadedMessageDate != null){
                    rvMessages.post(Runnable {
                        chatMessageAdapter.addData(createMessage(lastLoadedMessageDate!!.getDateWithServerTimeStamp(), "", ChatMessageType.Date))
                    })
                }
            }

            endLoading()
        })

        rlPhoto.setOnClickListener {openPhotoDialog()}
        rlSend.setOnClickListener {send()}

        txtHeader.text = opponent?.name
        rvMessages.addOnScrollListener(object : EndlessRecyclerViewOnScrollListener(layoutManager) {
            override fun onLoadMore() {
                loadData()
            }
        })

        scrollToBottom()

        socket!!.connect()
        startLoading()

        val messageJsonObject = JSONObject()
        messageJsonObject.put("token", ThePreferences().getToken())
        messageJsonObject.put("itemId", item.id)

        var array = JSONArray()
        array.put(ProfileHolder.user!!.id)
        array.put(opponent!!.id)
        messageJsonObject.put("users", array)
        socket!!.emit(ENTER_ROOM, messageJsonObject,  {
        })
    }

    fun loadData() {
        if(!historyLoaded) {

            if(loading)
                return
            startLoading()

            val messageJsonObject = JSONObject()
            if(lastLoadedMessageDate != null)
                messageJsonObject.put("fromDate", lastLoadedMessageDate!!)
            messageJsonObject.put("limit", limit)

            socket!!.emit(HISTORY, messageJsonObject,  {
            })
        }
    }

    fun createMessage(date: Date, data: String, type:ChatMessageType) : ChatMessage{
        var message = ChatMessage()
        message.date = date
        message.type = type
        message.data = data
        return message
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

        activity?.runOnUiThread {
            progressCenter.visibility = View.VISIBLE
        }

        DataProvider.uploadFiles(imageUrls,
                object : SilentConsumer<TgLinks> {
                    override fun onConsume(t: TgLinks) {
                        activity?.runOnUiThread { hideViewsQuietly(progressCenter, progressBottom) }
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

        val messageJsonObject = JSONObject()
        messageJsonObject.put("type", "image")
        messageJsonObject.put("content", url)
        messageJsonObject.put("publisher", ProfileHolder.user!!.id)

        socket!!.emit(SEND_MESSAGE, messageJsonObject,  {})
    }

    fun send(){
        if(etMessage.text.toString().length == 0)
            return;

        if(loading)
            return
        startLoading()

        val messageJsonObject = JSONObject()
        messageJsonObject.put("type", "message")
        messageJsonObject.put("content", etMessage.text.toString())
        messageJsonObject.put("publisher", ProfileHolder.user!!.id)

        etMessage.setText("")

        socket!!.emit(SEND_MESSAGE, messageJsonObject,  {})
    }

    fun addMessageFromJSON(message : JSONObject, time : Date, history : Boolean)
    {
        if(!message.has("publisher") || !message.has("type") || !message.has("content"))
            return
        val content = message.get("content").toString()
        val type = message.get("type").toString()
        val publisher = message.get("publisher").toString()

        var chatMessageType: ChatMessageType? = null

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
            addMessage(createMessage(time, content, chatMessageType), history)
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
        rvMessages.post(Runnable { if(rvMessages != null) rvMessages.scrollToPosition(0) })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun startLoading(){
        loading = true
        activity?.runOnUiThread {
            if (chatMessageAdapter!!.isEmpty())
                progressCenter.visibility = View.VISIBLE
            else
                progressBottom.visibility = View.VISIBLE
        }
    }

    fun endLoading(){
        activity?.runOnUiThread { hideViewsQuietly(progressCenter, progressBottom) }
        loading = false
    }
}