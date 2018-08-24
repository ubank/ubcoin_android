package com.ubcoin.fragment.sell

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.ubcoin.R
import com.ubcoin.model.response.SingleLocation
import com.ubcoin.network.DataProvider

/**
 * Created by Yuriy Aizenberg
 */
class SellLocationAutocompleteAdapter(context: Context, private val resource: Int) : ArrayAdapter<SingleLocation>(context, resource) {

    val items: ArrayList<SingleLocation> = ArrayList()
    private val suggestions: ArrayList<SingleLocation> = ArrayList()

    override fun getCount() = items.size

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView
        if (v == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = layoutInflater.inflate(resource, null)
        }
        val singleLocation = getItem(position)

        val textView = v!!.findViewById<TextView>(R.id.txtItemLocation)
        textView.text = singleLocation.text
        return v
    }

    override fun getFilter(): Filter {
        return LocationFilter()
    }


    internal inner class LocationFilter : Filter() {

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as SingleLocation).text
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint == null || constraint.isBlank()) {
                FilterResults()
            } else {
                val findLocationSync = DataProvider.findLocationSync(constraint.toString())

                suggestions.clear()
                suggestions.addAll(findLocationSync)

                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            }
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            if (p1?.values != null && p1.count > 0) {
                items.clear()
                items.addAll(suggestions)
                notifyDataSetChanged()
            }
        }


    }
}