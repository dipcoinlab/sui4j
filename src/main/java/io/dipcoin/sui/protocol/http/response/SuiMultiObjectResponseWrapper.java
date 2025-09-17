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

package io.dipcoin.sui.protocol.http.response;

import io.dipcoin.sui.model.Response;
import io.dipcoin.sui.model.object.SuiObjectResponse;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/7/23 10:20
 * @Description : sui_multiGetObjects wrapper class response
 */
public class SuiMultiObjectResponseWrapper extends Response<List<SuiObjectResponse>> {

    @Override
    public void setResult(List<SuiObjectResponse> result) {
        super.setResult(result);
    }

    @Override
    public List<SuiObjectResponse> getResult() {
        return super.getResult();
    }

}
