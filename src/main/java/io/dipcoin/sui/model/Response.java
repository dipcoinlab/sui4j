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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dipcoin.sui.protocol.deserializer.KeepAsJsonDeserializer;

/**
 * @author : Same
 * @datetime : 2025/6/25 18:48
 * @Description : Sui jsonrpc response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {

    private long id;
    private String jsonrpc;
    private T result;
    private Error error;

    public Response() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    public static class Error {
        
        /**
         * JSON parse error (-32700).
         */
        public static final Error PARSE_ERROR = new Error(-32700, "JSON parse error");
        /**
         * Invalid JSON-RPC 2.0 request error (-32600).
         */
        public static final Error INVALID_REQUEST = new Error(-32600, "Invalid request");
        /**
         * Method not found error (-32601).
         */
        public static final Error METHOD_NOT_FOUND = new Error(-32601, "Method not found");
        /**
         * Invalid parameters error (-32602).
         */
        public static final Error INVALID_PARAMS = new Error(-32602, "Invalid parameters");
        /**
         * Internal JSON-RPC 2.0 error (-32603).
         */
        public static final Error INTERNAL_ERROR = new Error(-32603, "Internal error");
        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = 4682571044532698806L;
        
        private int code;
        private String message;

        @JsonDeserialize(using = KeepAsJsonDeserializer.class)
        private String data;

        public Error() {}

        public Error(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Error)) {
                return false;
            }

            Error error = (Error) o;

            if (getCode() != error.getCode()) {
                return false;
            }
            if (getMessage() != null
                    ? !getMessage().equals(error.getMessage())
                    : error.getMessage() != null) {
                return false;
            }
            return getData() != null ? getData().equals(error.getData()) : error.getData() == null;
        }

        @Override
        public int hashCode() {
            int result = getCode();
            result = 31 * result + (getMessage() != null ? getMessage().hashCode() : 0);
            result = 31 * result + (getData() != null ? getData().hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
