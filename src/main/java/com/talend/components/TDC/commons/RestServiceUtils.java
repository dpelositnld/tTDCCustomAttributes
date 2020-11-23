package com.talend.components.TDC.commons;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Scanner;

public class RestServiceUtils {
    private static String token;

    static public String TDCRest_login(String TDCEndpoint, String TDCUsername, String TDCPassword, boolean isUseProxy, String proxyAddress, int proxyPort) throws Exception {
        String token;
        String api_path = "/auth/login";
        String urlString = TDCEndpoint + api_path + "?user=" + TDCUsername + "&password=" + TDCPassword + "&forceLogin=true";
        URL url = new URL(urlString);

        HttpURLConnection con = getHttpConnection(url, isUseProxy, proxyAddress, proxyPort);

        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {

            String inline = "";
            Scanner scanner = new Scanner(con.getInputStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            //Close the scanner
            scanner.close();
            con.disconnect();

            JSONObject jsonObject = new JSONObject(inline);

            token = jsonObject.getJSONObject("result").getString("token");
        }

        System.out.println("User " + TDCUsername + " connected with Token: " + token);

        setToken(token);

        return token;
    }

    public static String getToken() {
        return token;
    }

    private static void setToken(String t) {
        token = t;
    }

    private static HttpURLConnection getHttpConnection(URL url, boolean isUseProxy, String proxyAddress, int proxyPort) throws Exception{
        HttpURLConnection con = null;

        if (isUseProxy) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, proxyPort));
            con = (HttpURLConnection) url.openConnection(proxy);
        } else
            con = (HttpURLConnection) url.openConnection();

        return con;
    }
}
