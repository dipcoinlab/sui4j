/*
 * Copyright 2019 Web3 Labs Ltd.
 * Copyright 2025 Dipcoin LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications:
 * - Adapted for Sui RPC calls by Dipcoin LLC, 2025
 */

package io.dipcoin.sui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dipcoin.sui.protocol.SuiService;
import io.dipcoin.sui.protocol.core.DefaultIdProvider;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author : Same
 * @datetime : 2025/6/25 18:29
 * @Description : Sui jsonrpc request
 */
public class Request<S, T extends Response> {

    private String jsonrpc = "2.0";
    private String method;
    private List<S> params;
    private long id;

    private SuiService suiService;

    // Unfortunately require an instance of the type too, see
    // http://stackoverflow.com/a/3437930/3211687
    private Class<T> responseType;

    public Request() {}

    public Request(String method, List<S> params, SuiService suiService, Class<T> type) {
        this.method = method;
        this.params = params;
        this.id = DefaultIdProvider.getNextId();
        this.suiService = suiService;
        this.responseType = type;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<S> getParams() {
        return params;
    }

    public void setParams(List<S> params) {
        this.params = params;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public Class<T> getResponseType() {
        return responseType;
    }

    public T send() throws IOException {
        return suiService.send(this, responseType);
    }

    public CompletableFuture<T> sendAsync() {
        return suiService.sendAsync(this, responseType);
    }

}
