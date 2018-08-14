package com.ubcoin.network.security

import java.net.InetAddress
import java.net.Socket
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by Yuriy Aizenberg
 */
@Deprecated(message = "Use chipperSuite instead", level = DeprecationLevel.WARNING)
object SSLFactory : SSLSocketFactory() {

    private val sslContext = SSLContext.getInstance("TLS")

    init {
        val tm: TrustManager = object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            }

            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return null
            }
        }
        sslContext.init(null, arrayOf(tm), null)
    }


    override fun getDefaultCipherSuites(): Array<String> {
        return sslContext.socketFactory.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return sslContext.socketFactory.supportedCipherSuites
    }

    override fun createSocket(p0: Socket?, p1: String?, p2: Int, p3: Boolean): Socket {
        return sslContext.socketFactory.createSocket(p0, p1, p2, p3)
    }

    override fun createSocket(p0: InetAddress?, p1: Int): Socket {
        return sslContext.socketFactory.createSocket(p0, p1)
    }

    override fun createSocket(p0: String?, p1: Int): Socket {
        return sslContext.socketFactory.createSocket(p0, p1)
    }

    override fun createSocket(p0: InetAddress?, p1: Int, p2: InetAddress?, p3: Int): Socket {
        return sslContext.socketFactory.createSocket(p0, p1, p2, p3)
    }

    override fun createSocket(p0: String?, p1: Int, p2: InetAddress?, p3: Int): Socket {
        return sslContext.socketFactory.createSocket(p0, p1, p2, p3)
    }

    override fun createSocket(): Socket {
        return sslContext.socketFactory.createSocket()
    }

}