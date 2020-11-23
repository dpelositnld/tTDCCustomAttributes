package com.talend.components.TDC.client;

import org.talend.sdk.component.api.service.http.*;

import javax.json.JsonObject;

public interface LogoutClient extends HttpClient {
    @Request(path = "/auth/logout", method = "POST")
    Response<JsonObject> logout(
            @Header("Content-Type") String contentType,
            @Header("api_key") String token
    );
}
