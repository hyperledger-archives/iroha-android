package jp.co.soramitsu.irohaandroid

import android.app.Application
import jp.co.soramitsu.irohaandroid.di.AppComponent
import jp.co.soramitsu.irohaandroid.di.DaggerAppComponent

class IrohaSampleApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().context(this).build()
    }
}