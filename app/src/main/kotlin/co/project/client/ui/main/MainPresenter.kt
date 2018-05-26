package co.project.client.ui.main

import co.project.client.data.DataManager
import co.project.client.injection.ConfigPersistent
import co.project.client.ui.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ConfigPersistent
class MainPresenter<V : MainMvp.View> @Inject
constructor(private val dataManager : DataManager) : BasePresenter<V>(), MainMvp.Presenter<V> {
    override fun connectToServer() {
        view.showLoading()
        dataManager.serverService
                .connect()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { client ->
                            view.onConnected(client)
                            view.hideLoading()
                        },
                        {
                            err ->
                            err.printStackTrace()
                            view.hideLoading()
                            view.showMessage(err.message ?: "Client Reached?")
                        }
                )
    }

    override fun resetServerConnection() {
        view.showLoading()
        dataManager.serverService
                .reset()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onErrorComplete { err ->
                    err.printStackTrace()
                    view.onError(err.message)
                    true
                }
                .subscribe {
                    view.onServerConnectionResetted()
                    view.hideLoading()
                }
    }

}