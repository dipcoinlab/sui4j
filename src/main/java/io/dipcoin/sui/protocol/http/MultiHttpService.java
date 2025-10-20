/*
 * Copyright 2025 Dipcoin LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with
 * the License.You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software distributed under the License is distributed on
 * an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.dipcoin.sui.protocol.http;

import io.dipcoin.sui.protocol.Service;
import io.dipcoin.sui.protocol.exceptions.ClientConnectionException;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/10/17 13:56
 * @Description :
 */
public class MultiHttpService extends Service {

    public static final MediaType JSON_MEDIA_TYPE =
            MediaType.parse("application/json; charset=utf-8");

    private static final Logger log = LoggerFactory.getLogger(HttpService.class);

    private OkHttpClient httpClient;

    private List<String> urls = new ArrayList<>();

    private HashMap<String, String> headers = new HashMap<>();

    public MultiHttpService(List<String> urls, OkHttpClient httpClient) {
        super();
        if (urls == null || urls.isEmpty()) {
            throw new IllegalArgumentException("endpoints cannot be empty");
        }
        this.urls.addAll(urls);
        this.httpClient = httpClient;
    }

    public MultiHttpService(List<String> urls) {
        this(urls, createOkHttpClient());
    }

    public static OkHttpClient.Builder getOkHttpClientBuilder() {
        final OkHttpClient.Builder builder =
                new OkHttpClient.Builder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .readTimeout(Duration.ofSeconds(60));
        configureLogging(builder);
        return builder;
    }

    private static OkHttpClient createOkHttpClient() {
        return getOkHttpClientBuilder().build();
    }

    private static void configureLogging(OkHttpClient.Builder builder) {
        if (log.isDebugEnabled()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(log::debug);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
    }

    @Override
    protected InputStream performIO(String request) throws IOException {
        IOException lastEx = null;
        for (String url : urls) {
            try {
                return doRequest(url, request);
            } catch (IOException e) {
                lastEx = e;
                log.warn("[FailoverHttpService] Request failed on {}: {}", url, e.getMessage());
            }
        }
        throw new ClientConnectionException(
                "All RPC endpoints failed: " + urls, lastEx);
    }

    private InputStream doRequest(String url, String request) throws IOException {
        RequestBody requestBody = RequestBody.create(request, JSON_MEDIA_TYPE);
        Headers headers = buildHeaders();

        Request httpRequest =
                new Request.Builder().url(url).headers(headers).post(requestBody).build();
        try (Response response = httpClient.newCall(httpRequest).execute()) {
            processHeaders(response.headers());
            ResponseBody responseBody = response.body();
            if (response.isSuccessful()) {
                if (responseBody != null) {
                    return buildInputStream(responseBody);
                } else {
                    return null;
                }
            } else {
                int code = response.code();
                String text = responseBody == null ? "N/A" : responseBody.string();

                throw new ClientConnectionException(
                        "Invalid response received: " + code + "; " + text);
            }
        }
    }

    protected void processHeaders(Headers headers) {
        // Default implementation is empty
    }

    private InputStream buildInputStream(ResponseBody responseBody) throws IOException {
        return new ByteArrayInputStream(responseBody.bytes());
    }

    private Headers buildHeaders() {
        return Headers.of(headers);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headersToAdd) {
        headers.putAll(headersToAdd);
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public List<String> getUrls() {
        return urls;
    }

    @Override
    public void close() throws IOException {}

}
