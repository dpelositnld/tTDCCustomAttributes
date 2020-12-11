package com.talend.components.TDC.client;

import com.talend.components.TDC.datastore.BasicAuthDataStore;
import org.talend.sdk.component.api.service.http.*;

import javax.json.JsonObject;
import java.util.Map;

public interface TDCAPIClient extends HttpClient {
    @Request(path = "/MM/rest/v1/auth/login", method = "GET")
    Response<JsonObject> login(@Query("user") String user,
                               @Query("password") String password,
                               @Query("forceLogin") boolean forceLogin
    );

    @Request(path = "/MM/rest/v1/auth/logout", method = "POST")
    Response<JsonObject> logout(
            @Header("Content-Type") String contentType,
            @Header("api_key") String token
    );

    @Request(path = "/MM/rest/v1/entities/{objectId}")
    Response<JsonObject> getEntity(
            @Header("Content-Type") String contentType,
            @Header("api_key") String token,
            @Path("objectId") String objectId,
            @Query("includeAttributes") boolean includeAttributes
    );

    @Request(path = "/MM/rest/v1/entities/executeMQLQuery", method = "POST" )
    Response<JsonObject> executeMQLQuery(
            @Header("Content-Type") String contentType,
            @Header("api_key") String token,
            JsonObject payload
    );

    @Request(path = "/MM/rest/v1/operations/repositoryBrowse", method = "GET" )
    Response<JsonObject> repositoryBrowse(
            @Header("accept") String accept,
            @Header("api_key") String token,
            @Query("objectTypes") String objectTypes,
            @Query("repositoryPath") String repositoryPath
    );

    @Request(path = "/MM/rest/v1/repository/setCustomAttributes", method = "PUT")
    Response<JsonObject> setCustomAttributes(
            @Header("Content-Type") String contentType,
            @Header("api_key") String token,
            JsonObject payload
    );

    @Request(path = "/MM/api/LoginWithCredentials", method = "POST")
    Response<JsonObject> loginWithCredentials(
            @Header("Content-Type") String contentType,
            @QueryParams(format = QueryFormat.CSV) Map<String, String> params
    );

    @Request(path = "/MM/api/ListCustomAttributes", method = "POST")
    Response<String> listCustomAttributes(
            @Header("Content-Type") String contentType,
            @Header("Cookie") String cookie
    );

}
