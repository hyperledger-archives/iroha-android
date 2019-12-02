package jp.co.soramitsu.irohaandroid.presentation.main.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import jp.co.soramitsu.irohaandroid.di.ViewModelKey
import jp.co.soramitsu.irohaandroid.domain.MainInteractor
import jp.co.soramitsu.irohaandroid.presentation.main.MainViewModel
import jp.co.soramitsu.irohaandroid.util.QrCodeDecoder
import jp.co.soramitsu.irohaandroid.util.QrCodeGenerator

@Module
class MainModule {

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideViewModel(mainInteractor: MainInteractor, qrCodeGenerator: QrCodeGenerator, qrCodeDecoder: QrCodeDecoder): ViewModel {
        return MainViewModel(mainInteractor, qrCodeGenerator, qrCodeDecoder)
    }

    @Provides
    fun provideMainViewModel(activity: AppCompatActivity, viewmodelFactory: ViewModelProvider.Factory): MainViewModel {
        return ViewModelProviders.of(activity, viewmodelFactory).get(MainViewModel::class.java)
    }


}