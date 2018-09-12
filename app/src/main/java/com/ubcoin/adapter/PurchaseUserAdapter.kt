package com.ubcoin.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ubcoin.GlideApp
import com.ubcoin.R
import com.ubcoin.model.FakePurchase
import com.ubcoin.model.IPurchaseObject
import com.ubcoin.model.Purchase
import com.ubcoin.utils.getNickName

/**
 * Created by Yuriy Aizenberg
 */

private const val TYPE_HEADER = 0
private const val TYPE_USER = 1

class PurchaseUserAdapter(context: Context) : BaseRecyclerAdapter<IPurchaseObject, PurchaseUserAdapter.AbsPurchaseUserHolder>(context) {


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): AbsPurchaseUserHolder {
        return if (viewType == TYPE_USER) {
            PurchaseUserHolder(inflate(R.layout.item_puchase, p0))
        } else {
            HeaderPurchaseHolder(inflate(R.layout.item_purchase_header, p0))
        }
    }

    override fun onBindViewHolder(p0: AbsPurchaseUserHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            onBindHeaderHolder(p0 as HeaderPurchaseHolder, getItem(position) as FakePurchase)
        } else {
            onBindUserHolder(p0 as PurchaseUserHolder, position, getItem(position) as Purchase)
        }
    }

    private fun onBindUserHolder(holder: PurchaseUserHolder, position: Int, purchase: Purchase) {
        val avatarUrl = purchase.buyer.avatarUrl
        if (avatarUrl == null) {
            holder.imgPurchaseUserProfile.setImageResource(R.drawable.img_profile_default)
        } else {
            GlideApp.with(context).load(avatarUrl)
                    .override(R.dimen.detailsSubProfileHeight, R.dimen.detailsSubProfileHeight)
                    .centerInside()
                    .transform(RoundedCorners(context.resources.getDimensionPixelSize(R.dimen.detailsSubProfileHeight)))
                    .placeholder(R.drawable.img_profile_default)
                    .error(R.drawable.img_profile_default)
                    .into(holder.imgPurchaseUserProfile)
        }
        holder.txtPurchaseUserName.text = purchase.buyer.name
        holder.txtPurchaseUserNickname.text = purchase.buyer.getNickName()
        bindTouchListener(holder.itemView, position, purchase)
    }

    private fun onBindHeaderHolder(holder: HeaderPurchaseHolder, fakePurchase: FakePurchase) {
        unbindTouchListener(holder.itemView)
        holder.txtPurchaseType.text = fakePurchase.name
    }

    override fun getItemViewType(position: Int) =
            if (getItem(position) is FakePurchase) TYPE_HEADER else TYPE_USER


    abstract class AbsPurchaseUserHolder(itemView: View) : VHolder(itemView)

    class PurchaseUserHolder(itemView: View) : AbsPurchaseUserHolder(itemView) {
        val imgPurchaseUserProfile = findView<ImageView>(R.id.imgPurchaseUserProfile)
        val txtPurchaseUserName = findView<TextView>(R.id.txtPurchaseUserName)
        val txtPurchaseUserNickname = findView<TextView>(R.id.txtPurchaseUserNickname)
    }

    class HeaderPurchaseHolder(itemView: View) : AbsPurchaseUserHolder(itemView) {
        val txtPurchaseType = findView<TextView>(R.id.txtPurchaseType)
    }


}