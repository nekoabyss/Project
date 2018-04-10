package co.project.client.injection.module

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import co.project.client.injection.ActivityContext
import co.project.client.injection.PerActivity

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    @PerActivity
    internal fun provideActivity(): Activity {
        return activity
    }

    @Provides
    @PerActivity
    @ActivityContext
    internal fun providesContext(): Context {
        return activity
    }
}
