package co.project.client.data.remote

import co.project.client.data.model.Client
import co.project.client.data.model.Response
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

public interface ServerService {
    @POST("/api/connect")       fun connect(): Observable<Client>
    @PUT("/api/update")         fun update(): Observable<Response>
    @GET("/api/list")           fun list(): Observable<List<String>>
    @DELETE("/api/disconnect")  fun disconnect(): Completable
    @DELETE("/api/reset")       fun reset(): Completable
}