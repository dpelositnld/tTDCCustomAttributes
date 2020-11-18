package com.talend.components.input;


import org.talend.sdk.component.api.service.http.*;

import javax.json.JsonObject;

public interface TTDCLoginClient extends HttpClient {
    @Request(path = "/auth/login", method = "GET")
    Response<JsonObject> login(@Query("user") String user,
                               @Query("password") String password
    );
}
