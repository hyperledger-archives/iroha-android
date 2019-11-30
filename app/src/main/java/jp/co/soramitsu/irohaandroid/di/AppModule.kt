package jp.co.soramitsu.irohaandroid.di

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3
import jp.co.soramitsu.iroha.java.IrohaAPI
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.data.transfer.TransferRepository
import jp.co.soramitsu.irohaandroid.data.transfer.TransferRepositoryImpl
import jp.co.soramitsu.irohaandroid.data.user.UserRepository
import jp.co.soramitsu.irohaandroid.data.user.UserRepositoryImpl
import jp.co.soramitsu.irohaandroid.data.user.datasource.PrefsUserDatasource
import jp.co.soramitsu.irohaandroid.data.user.datasource.UserDatasource
import jp.co.soramitsu.irohaandroid.presentation.base.IrohaViewModelFactory
import jp.co.soramitsu.irohaandroid.util.PrefsUtil
import jp.co.soramitsu.irohaandroid.util.QrCodeGenerator
import jp.co.soramitsu.irohaandroid.util.DateFormatter
import jp.co.soramitsu.irohaandroid.util.ResourceManager
import jp.co.soramitsu.irohaandroid.util.QrCodeDecoder
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideViewModelFactory(factory: IrohaViewModelFactory): ViewModelProvider.Factory = factory


    @Provides
    @Singleton
    fun provideIrohaApi(): IrohaAPI {
        return IrohaAPI("192.168.1.6", 50051)
    }

    @Provides
    @Singleton
    fun provideEd25519Sha3(): Ed25519Sha3 {
        return Ed25519Sha3()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun providePrefsUtil(context: Context): PrefsUtil {
        return PrefsUtil(context)
    }

    @Provides
    @Singleton
    fun provideUserDatasource(prefsUtil: PrefsUtil): UserDatasource {
        return PrefsUserDatasource(prefsUtil)
    }

    @Provides
    @Singleton
    fun provideQrCodeGenerator(context: Context): QrCodeGenerator {
        return QrCodeGenerator(Color.BLACK, context.resources.getColor(R.color.zxing_transparent))
    }

    @Provides
    @Singleton
    fun provideDateFormatter() = DateFormatter()

    @Provides
    @Singleton
    fun provideUserRepository(
        irohaAPI: IrohaAPI,
        userDatasource: UserDatasource,
        ed25519Sha3: Ed25519Sha3,
        resourceManager: ResourceManager
    ): UserRepository {
        return UserRepositoryImpl(
            irohaAPI,
            userDatasource,
            ed25519Sha3,
            resourceManager
        )
    }

    @Singleton
    @Provides
    fun provideQrCodeDecoder(context: Context) = QrCodeDecoder(context.contentResolver)

    @Singleton
    @Provides
    fun provideResourceManager(context: Context) = ResourceManager(context)

    @Provides
    @Singleton
    fun provideTransferRepository(
        irohaAPI: IrohaAPI,
        gson: Gson,
        userDatasource: UserDatasource,
        dateFormatter: DateFormatter,
        resourceManager: ResourceManager
    ): TransferRepository {
        return TransferRepositoryImpl(irohaAPI, gson, userDatasource, dateFormatter, resourceManager)
    }
}