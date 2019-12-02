package jp.co.soramitsu.irohaandroid.presentation.main.di

import androidx.appcompat.app.AppCompatActivity
import dagger.BindsInstance
import dagger.Subcomponent
import jp.co.soramitsu.irohaandroid.presentation.main.MainActivity
import jp.co.soramitsu.irohaandroid.presentation.main.history.HistoryFragment
import jp.co.soramitsu.irohaandroid.presentation.main.receive.ReceiveFragment
import jp.co.soramitsu.irohaandroid.presentation.main.send.SendFragment

@Subcomponent(
    modules = [
        MainModule::class
    ]
)

interface MainComponent {

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun withActivity(activity: AppCompatActivity): Builder

        fun build(): MainComponent

    }

    fun inject(mainActivity: MainActivity)

    fun inject(receiveFragment: ReceiveFragment)

    fun inject(sendFragment: SendFragment)

    fun inject(historyFragment: HistoryFragment)
}