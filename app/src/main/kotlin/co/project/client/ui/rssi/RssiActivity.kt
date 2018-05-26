package co.project.client.ui.rssi

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import co.project.client.R
import co.project.client.ui.base.BaseActivity
import timber.log.Timber
import javax.inject.Inject

class RssiActivity : BaseActivity(), RssiMvp.View, RssiMvp.Interaction {

    @BindView(R.id.info) lateinit var infoTxt: AppCompatTextView
    @BindView(R.id.toolbar) lateinit var contentToolbar: Toolbar
    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView

//    @BindView(R.menu.menu_main) lateinit var menuMain: Menu
//    @BindView(R.id.action_setting) lateinit var actionSetting: MenuBuilder.ItemInvoker

    @Inject lateinit var presenter: RssiPresenter<RssiMvp.View>

    private var wifiManager: WifiManager? = null

    private var adapter: RssiAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rssi_page)
        setup()
    }

    override fun setup() {
        activityComponent.inject(this)
        unbinder = ButterKnife.bind(this)
        presenter.attachView(this)

        setSupportActionBar(contentToolbar)

        this.wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        adapter = RssiAdapter()
        adapter?.delegate = this
        recyclerView.adapter = adapter
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scanNetwork()
    }

    override fun onDestroy() {
        presenter.detachView()
        adapter?.delegate = null
        super.onDestroy()
    }

    override fun scanNetwork() {
        this.wifiManager?.let {
            it.startScan()
            adapter?.setList(it.scanResults)
            adapter?.notifyDataSetChanged()
        }
    }

    @OnClick(R.id.fab)
    override fun onFabClicked(view: View) {
        scanNetwork()
        Snackbar
                .make(view, "Scanning....", Snackbar.LENGTH_SHORT)
                .show()
    }

    override fun onItemClicked(item: ScanResult) {
        Timber.e(item.SSID)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return (id == R.id.action_setting) || super.onOptionsItemSelected(item)
    }

    private fun connectToWifi(networkSSID: String, networkPassword: String) {
        wifiManager?.isWifiEnabled = true

        val config = WifiConfiguration().apply {
            SSID = String.format("\"%s\"", networkSSID)
            preSharedKey = String.format("\"%s\"", networkPassword)
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            allowedProtocols.set(WifiConfiguration.Protocol.WPA)
        }

        //Toast.makeText(MainActivity.this, "Name of wifi: " + networkSSID + " Password " + networkPassword, Toast.LENGTH_SHORT).show();

        val netId = wifiManager?.addNetwork(config)

        wifiManager?.configuredNetworks?.let {
            for (network in it) {
                if ((network.SSID ?: "") == "\"$networkSSID\"" ) {
                    wifiManager?.disconnect()
                    wifiManager?.enableNetwork(network.networkId, true)
                    val reconnect = wifiManager?.reconnect()

                    break
                }
            }
        }

    }
}