package jp.co.soramitsu.irohaandroid.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import jp.co.soramitsu.irohaandroid.presentation.main.MainActivity
import jp.co.soramitsu.irohaandroid.presentation.main.di.MainComponent
import jp.co.soramitsu.irohaandroid.presentation.signup.di.SignupComponent
import jp.co.soramitsu.irohaandroid.presentation.splash.SplashActivity
import jp.co.soramitsu.irohaandroid.presentation.splash.di.SplashComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun mainComponentBuilder(): MainComponent.Builder

    fun signupComponentBuilder(): SignupComponent.Builder

    fun splashComponentBuilder(): SplashComponent.Builder

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent

    }
}