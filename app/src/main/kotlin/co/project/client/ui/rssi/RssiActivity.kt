package co.project.client.ui.rssi

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.view.menu.MenuBuilder
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

class RssiActivity : BaseActivity(), RssiMvp.View {

    @BindView(R.id.info) lateinit var infoTxt: AppCompatTextView
    @BindView(R.id.include) lateinit var includeContent: AppCompatTextView
    @BindView(R.id.toolbar) lateinit var contentToolbar: Toolbar
    @BindView(R.id.listItem) lateinit var netList: RecyclerView
    @BindView(R.menu.menu_main) lateinit var menuMain: Menu
    @BindView(R.id.action_setting) lateinit var actionSetting: MenuBuilder.ItemInvoker

    //@Inject lateinit var presenter: RssiPresenter<RssiMvp.View>

    private var wifiManager: WifiManager? = null

    private var adapter: RssiAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rssi_page)
        //val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(contentToolbar)
        setup()
    }

    override fun setup() {
        //activityComponent.inject(this)
        unbinder = ButterKnife.bind(this)
        //presenter.attachView(this)

        adapter = RssiAdapter()
    }

    override fun onDestroy() {
        adapter = null
        super.onDestroy()
    }

    private fun detectWiFi() {

        this.wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        this.wifiManager?.let {
            it.startScan()
            adapter?.setList(it.scanResults)
            adapter?.notifyDataSetChanged()
        }

//        netList.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
//            var c = 0
//            var k: Int
//            val name = nets[i]!!.getTitle()
//            while (c < 10) {
//                k = 0
//                while (k < wifiList!!.size && name.compareTo(nets[k]!!.getTitle()) != 0) {
//                    k++
//                }
//                list.add(nets[k]!!.getLevel())
//
//                Log.d("round", "roundc" + c + nets[k]!!.getTitle() + nets[k]!!.getLevel())
//                c++
//                SystemClock.sleep(2000)
//                detectWiFi()
//            }
//
//            for (z in list.indices) {
//                Log.d("list", list[z] +"    "+ z)
//            }
//            list.clear()
//        }


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

    @OnClick(R.id.fab)
    fun onFabClicked(view: View) {
        detectWiFi()
        Snackbar
                .make(view, "Scanning....", Snackbar.LENGTH_SHORT)
                .setAction("Action", null)
                .show()
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