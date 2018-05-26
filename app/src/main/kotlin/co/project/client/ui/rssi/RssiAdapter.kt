package co.project.client.ui.rssi

import android.net.wifi.ScanResult
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.project.client.R

class RssiAdapter: RecyclerView.Adapter<RssiAdapter.RssiViewHolder>() {

    private var wifiList: List<ScanResult>? = null
    var delegate: RssiMvp.Interaction? = null

    override fun onBindViewHolder(holder: RssiViewHolder, pos: Int) {
        wifiList?.get(pos)?.let { item ->
            holder.apply {
                setItem(item)
                itemView.setOnClickListener { delegate?.onItemClicked(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RssiViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.items, parent, false)
        return RssiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return wifiList?.size ?: 0
    }

    fun setList(list: List<ScanResult>?) {
        wifiList = list
    }

    class RssiViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        private var ssidTxt: AppCompatTextView? = itemView?.findViewById(R.id.ssid_lbl)
        private var securityTxt: AppCompatTextView? = itemView?.findViewById(R.id.security_lbl)
        private var strengthTxt: AppCompatTextView? = itemView?.findViewById(R.id.signal_lbl)

        internal fun setItem(network: ScanResult?) {
            network.let {
                ssidTxt?.text = "SSID: ${network?.SSID ?: "HIDDEN"}"
                securityTxt?.text = "Security: ${network?.capabilities}"
                strengthTxt?.text = "Signal Level: ${network?.level}"
            }
        }
    }

}