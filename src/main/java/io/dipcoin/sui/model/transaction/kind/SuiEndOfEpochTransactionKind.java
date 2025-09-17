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

package io.dipcoin.sui.model.transaction.kind;

import io.dipcoin.sui.model.transaction.kind.epoch.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dipcoin.sui.model.transaction.kind.epoch.*;
import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/6/27 16:59
 * @Description : SuiEndOfEpochTransactionKind response
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StringEndOfEpoch.class, name = "StringType"),
        @JsonSubTypes.Type(value = ChangeEpoch.class, name = "ChangeEpoch"),
        @JsonSubTypes.Type(value = AuthenticatorStateExpire.class, name = "AuthenticatorStateExpire"),
        @JsonSubTypes.Type(value = BridgeStateCreate.class, name = "BridgeStateCreate"),
        @JsonSubTypes.Type(value = BridgeCommitteeUpdate.class, name = "BridgeCommitteeUpdate")
})
public abstract class SuiEndOfEpochTransactionKind {

    protected String type;

}
