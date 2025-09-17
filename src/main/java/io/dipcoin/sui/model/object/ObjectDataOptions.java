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

package io.dipcoin.sui.model.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/7/4 15:42
 * @Description : ObjectDataOptions response
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectDataOptions {

    private Boolean showBcs;

    private Boolean showContent;

    private Boolean showDisplay;

    private Boolean showOwner;

    private Boolean showPreviousTransaction;

    private Boolean showStorageRebate;

    private Boolean showType;

    /**
     * Default all to true
     * @return
     */
    public static ObjectDataOptions allTrue() {
        ObjectDataOptions options = new ObjectDataOptions();
        options.setShowBcs(true);
        options.setShowContent(true);
        options.setShowDisplay(true);
        options.setShowOwner(true);
        options.setShowPreviousTransaction(true);
        options.setShowStorageRebate(true);
        options.setShowType(true);
        return options;
    }

    /**
     * All to false
     * @return
     */
    public static ObjectDataOptions allFalse() {
        ObjectDataOptions options = new ObjectDataOptions();
        options.setShowBcs(false);
        options.setShowContent(false);
        options.setShowDisplay(false);
        options.setShowOwner(false);
        options.setShowPreviousTransaction(false);
        options.setShowStorageRebate(false);
        options.setShowType(false);
        return options;
    }

    /**
     * content and bcs to false
     * @return
     */
    public static ObjectDataOptions contentAndBcsFalse() {
        ObjectDataOptions options = new ObjectDataOptions();
        options.setShowBcs(false);
        options.setShowContent(false);
        options.setShowDisplay(true);
        options.setShowOwner(true);
        options.setShowPreviousTransaction(true);
        options.setShowStorageRebate(true);
        options.setShowType(true);
        return options;
    }

    /**
     * Only content to true
     * @return
     */
    public static ObjectDataOptions contentTrue() {
        ObjectDataOptions options = new ObjectDataOptions();
        options.setShowBcs(false);
        options.setShowContent(true);
        options.setShowDisplay(false);
        options.setShowOwner(false);
        options.setShowPreviousTransaction(false);
        options.setShowStorageRebate(false);
        options.setShowType(false);
        return options;
    }

    /**
     * Only owner to true
     * @return
     */
    public static ObjectDataOptions ownerTrue() {
        ObjectDataOptions options = new ObjectDataOptions();
        options.setShowBcs(false);
        options.setShowContent(false);
        options.setShowDisplay(false);
        options.setShowOwner(true);
        options.setShowPreviousTransaction(false);
        options.setShowStorageRebate(false);
        options.setShowType(false);
        return options;
    }

    /**
     * Only type to true
     * @return
     */
    public static ObjectDataOptions typeTrue() {
        ObjectDataOptions options = new ObjectDataOptions();
        options.setShowBcs(false);
        options.setShowContent(false);
        options.setShowDisplay(false);
        options.setShowOwner(false);
        options.setShowPreviousTransaction(false);
        options.setShowStorageRebate(false);
        options.setShowType(true);
        return options;
    }

    /**
     * Only owner and type to true
     * @return
     */
    public static ObjectDataOptions ownerAndTypeTrue() {
        ObjectDataOptions options = new ObjectDataOptions();
        options.setShowBcs(false);
        options.setShowContent(false);
        options.setShowDisplay(false);
        options.setShowOwner(true);
        options.setShowPreviousTransaction(false);
        options.setShowStorageRebate(false);
        options.setShowType(true);
        return options;
    }

    /**
     * owner content and type to true
     * @return
     */
    public static ObjectDataOptions ownerAndTypeAndContentTrue() {
        ObjectDataOptions options = new ObjectDataOptions();
        options.setShowBcs(false);
        options.setShowContent(true);
        options.setShowDisplay(false);
        options.setShowOwner(true);
        options.setShowPreviousTransaction(false);
        options.setShowStorageRebate(false);
        options.setShowType(true);
        return options;
    }
}
