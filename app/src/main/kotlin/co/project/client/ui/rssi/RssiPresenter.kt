package co.project.client.ui.rssi

import co.project.client.data.DataManager
import co.project.client.injection.ConfigPersistent
import co.project.client.ui.base.BasePresenter
import javax.inject.Inject

@ConfigPersistent
class RssiPresenter<V : RssiMvp.View> @Inject
constructor(private val dataManager : DataManager) : BasePresenter<V>(), RssiMvp.Presenter<V> {


}