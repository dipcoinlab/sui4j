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

package io.dipcoin.sui.model.transaction.kind.block;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dipcoin.sui.model.transaction.kind.SuiEndOfEpochTransactionKind;
import io.dipcoin.sui.model.transaction.kind.TransactionBlockKind;
import lombok.Data;

import java.util.List;

/**
 * @author : Same
 * @datetime : 2025/6/27 15:43
 * @Description : EndOfEpochTransaction response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndOfEpochTransaction extends TransactionBlockKind {

    private List<SuiEndOfEpochTransactionKind> transactions;

    public EndOfEpochTransaction() {
        this.kind = "EndOfEpochTransaction";
    }
}
