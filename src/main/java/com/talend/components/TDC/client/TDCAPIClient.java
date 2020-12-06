package com.talend.components.TDC.client;

import org.json.JSONObject;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.service.http.*;

import javax.json.JsonObject;
import java.util.HashMap;
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
