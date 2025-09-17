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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Same
 * @datetime : 2025/8/29 11:47
 * @Description :
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuiSystemStateSummary {

    private List<SuiValidatorSummary> activeValidators = new ArrayList<>();

    private Map<String, Long> atRiskValidators = new HashMap<>();

    private Long epoch;
    private Long epochDurationMs;
    private Long epochStartTimestampMs;

    private String inactivePoolsId;
    private Long inactivePoolsSize;

    private Long maxValidatorCount;
    private Long minValidatorJoiningStake;

    private String pendingActiveValidatorsId;
    private Long pendingActiveValidatorsSize;

    private List<Long> pendingRemovals = new ArrayList<>();

    private Long protocolVersion;
    private Long referenceGasPrice;

    private Boolean safeMode;
    private Long safeModeComputationRewards;
    private Long safeModeNonRefundableStorageFee;
    private Long safeModeStorageRebates;
    private Long safeModeStorageRewards;

    private Long stakeSubsidyBalance;
    private Long stakeSubsidyCurrentDistributionAmount;
    private Integer stakeSubsidyDecreaseRate;
    private Long stakeSubsidyDistributionCounter;
    private Long stakeSubsidyPeriodLength;
    private Long stakeSubsidyStartEpoch;

    private String stakingPoolMappingsId;
    private Long stakingPoolMappingsSize;

    private Long storageFundNonRefundableBalance;
    private Long storageFundTotalObjectStorageRebates;

    private Long systemStateVersion;
    private Long totalStake;

    private String validatorCandidatesId;
    private Long validatorCandidatesSize;

    private Long validatorLowStakeGracePeriod;
    private Long validatorLowStakeThreshold;
    private Long validatorVeryLowStakeThreshold;

    private Map<String, List<String>> validatorReportRecords = new HashMap<>();
}
