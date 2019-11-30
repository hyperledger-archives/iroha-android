package jp.co.soramitsu.irohaandroid.presentation.splash.di

import dagger.Subcomponent
import jp.co.soramitsu.irohaandroid.presentation.splash.SplashActivity

@Subcomponent(
    modules = [
        SplashModule::class
    ]
)

interface SplashComponent {

    @Subcomponent.Builder
    interface Builder {

        fun build(): SplashComponent

    }

    fun inject(splashActivity: SplashActivity)
}