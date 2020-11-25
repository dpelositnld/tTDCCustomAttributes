package com.talend.components.TDC.client;

import org.talend.sdk.component.api.service.http.*;

import javax.json.JsonObject;

public interface TDCAPIClient extends HttpClient {
    @Request(path = "/auth/login", method = "GET")
    Response<JsonObject> login(@Query("user") String user,
                               @Query("password") String password,
                               @Query("forceLogin") boolean forceLogin
    );

    @Request(path = "/auth/logout", method = "POST")
    Response<JsonObject> logout(
            @Header("Content-Type") String contentType,
            @Header("api_key") String token
    );


}
