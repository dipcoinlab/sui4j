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

package io.dipcoin.sui.bcs.types.arg.call;

import io.dipcoin.sui.bcs.types.tag.TypeTag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author : Same
 * @datetime : 2025/7/2
 * @Description : {@code CallArg::FundsWithdrawal} (SIP-58 address balance withdrawal reservation).
 *
 * <p>Declares, inside a PTB, a withdrawal reservation against an address balance (the
 * funds accumulator). At execution time it is converted into a
 * {@code sui::funds_accumulator::Withdrawal<Balance<T>>}, which must then be redeemed via
 * {@code 0x2::coin::redeem_funds<T>} / {@code 0x2::balance::redeem_funds<T>} into a
 * {@code Coin<T>} / {@code Balance<T>} before it can be used (the balance is left unchanged
 * if it is never redeemed).</p>
 *
 * <p>BCS layout, matching the Rust {@code sui-types} definition (the 3rd {@code CallArg}
 * variant, index = 2):
 * <pre>
 * FundsWithdrawalArg {
 *   reservation:   Reservation       // enum: MaxAmountU64(u64) = variant 0
 *   type_arg:      WithdrawalTypeArg // enum: Balance(TypeTag)  = variant 0
 *   withdraw_from: WithdrawFrom      // enum: Sender = 0 / Sponsor = 1
 * }
 * </pre>
 * The {@link TypeTag} in {@code type_arg} is the type parameter {@code T} of
 * {@code Balance<T>} (for example {@code 0x2::sui::SUI} for the SUI balance).</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CallArgFundsWithdrawal extends CallArg {

    /** Maximum reserved withdrawal amount ({@code Reservation::MaxAmountU64}, u64, in MIST). */
    private long amount;

    /** Type parameter {@code T} of {@code Balance<T>} (e.g. {@code 0x2::sui::SUI}). */
    private TypeTag balanceType;

    /** Withdrawal source: {@code false} = transaction sender (default), {@code true} = gas sponsor. */
    private boolean fromSponsor;

    public CallArgFundsWithdrawal(long amount, TypeTag balanceType) {
        this(amount, balanceType, false);
    }

    public CallArgFundsWithdrawal(long amount, TypeTag balanceType, boolean fromSponsor) {
        this.amount = amount;
        this.balanceType = balanceType;
        this.fromSponsor = fromSponsor;
    }
}
