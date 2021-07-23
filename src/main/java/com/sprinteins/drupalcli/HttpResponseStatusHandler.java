package com.sprinteins.drupalcli;

import com.sprinteins.drupalcli.exceptions.ForbiddenException;
import com.sprinteins.drupalcli.exceptions.UnauthorizedException;

import java.net.http.HttpResponse;

public abstract class HttpResponseStatusHandler {

    private HttpResponseStatusHandler() {
    }

    public static void checkStatusCode(HttpResponse<?> httpResponse) {
        switch(httpResponse.statusCode()) {
        case 401:
            throw new UnauthorizedException();
        case 403:
            throw new ForbiddenException();
        default:
            if (httpResponse.statusCode() >= 300) {
                throw new IllegalStateException("Bad Status Code: " + httpResponse.statusCode() + "\nBody: "+ httpResponse.body());
            }
        }
    }

}
