package jp.co.soramitsu.irohaandroid.presentation.signup.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import jp.co.soramitsu.irohaandroid.di.ViewModelKey
import jp.co.soramitsu.irohaandroid.domain.SignupInteractor
import jp.co.soramitsu.irohaandroid.presentation.signup.SignupViewModel

@Module
class SignupModule {

    @Provides
    @IntoMap
    @ViewModelKey(SignupViewModel::class)
    fun provideViewModel(signupInteractor: SignupInteractor): ViewModel {
        return SignupViewModel(signupInteractor)
    }

    @Provides
    fun provideSignupViewModel(activity: AppCompatActivity, viewmodelFactory: ViewModelProvider.Factory): SignupViewModel {
        return ViewModelProviders.of(activity, viewmodelFactory).get(SignupViewModel::class.java)
    }


}