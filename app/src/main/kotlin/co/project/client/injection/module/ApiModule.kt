package co.project.client.injection.module

import co.project.client.data.remote.ServerService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    // SERVER IP

    @Provides
    @Singleton
    fun provideServerService(okHttpClient: OkHttpClient, gson: Gson): ServerService {
        return Retrofit.Builder()
                .client(okHttpClient)
//                .baseUrl("http://172.20.10.5:4444/") //172.20.10.5 , 4444  http(s)://[ip]:[port]/
                .baseUrl("https://wifi-locator-mock.herokuapp.com/") //172.20.10.2 , 8080
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ServerService::class.java)
    }
}
