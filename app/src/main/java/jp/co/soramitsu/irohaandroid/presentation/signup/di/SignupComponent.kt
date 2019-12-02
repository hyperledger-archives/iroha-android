package jp.co.soramitsu.irohaandroid.presentation.signup.di

import androidx.appcompat.app.AppCompatActivity
import dagger.BindsInstance
import dagger.Subcomponent
import jp.co.soramitsu.irohaandroid.presentation.signup.SignupActivity

@Subcomponent(
    modules = [
        SignupModule::class
    ]
)

interface SignupComponent {

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun withActivity(activity: AppCompatActivity): Builder

        fun build(): SignupComponent

    }

    fun inject(signupActivity: SignupActivity)
}