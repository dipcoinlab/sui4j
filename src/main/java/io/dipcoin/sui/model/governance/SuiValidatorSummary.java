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

package io.dipcoin.sui.model.governance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author : Same
 * @datetime : 2025/8/29 11:47
 * @Description :
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuiValidatorSummary {

    private Long commissionRate;
    private String description;

    private String exchangeRatesId;
    private Long exchangeRatesSize;

    private Long gasPrice;
    private String imageUrl;
    private String name;

    private String netAddress;
    private String networkPubkeyBytes;

    private Long nextEpochCommissionRate;
    private Long nextEpochGasPrice;
    private String nextEpochNetAddress;
    private String nextEpochNetworkPubkeyBytes;
    private String nextEpochP2pAddress;
    private String nextEpochPrimaryAddress;
    private String nextEpochProofOfPossession;
    private String nextEpochProtocolPubkeyBytes;
    private Long nextEpochStake;
    private String nextEpochWorkerAddress;
    private String nextEpochWorkerPubkeyBytes;

    private String operationCapId;
    private String p2pAddress;

    private Long pendingPoolTokenWithdraw;
    private Long pendingStake;
    private Long pendingTotalSuiWithdraw;

    private Long poolTokenBalance;
    private String primaryAddress;

    private String projectUrl;
    private String proofOfPossessionBytes;
    private String protocolPubkeyBytes;

    private Long rewardsPool;

    private Long stakingPoolActivationEpoch;
    private Long stakingPoolDeactivationEpoch;
    private String stakingPoolId;
    private Long stakingPoolSuiBalance;

    private String suiAddress;
    private Long votingPower;

    private String workerAddress;
    private String workerPubkeyBytes;

}
