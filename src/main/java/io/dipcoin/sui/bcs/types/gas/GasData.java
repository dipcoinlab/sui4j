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

package io.dipcoin.sui.bcs.types.gas;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * @author : Same
 * @datetime : 2025/7/10 15:08
 * @Description : Gas data, corresponding to TypeScript's GasData type.
 */
public class GasData {

    private final List<SuiObjectRef> payment;
    private final String owner;
    private final long price;
    private final BigInteger budget;
    
    public GasData(List<SuiObjectRef> payment, String owner, long price, BigInteger budget) {
        this.payment = Objects.requireNonNull(payment);
        this.owner = Objects.requireNonNull(owner);
        this.price = price;
        this.budget = budget;
    }
    
    public List<SuiObjectRef> getPayment() {
        return payment;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public long getPrice() {
        return price;
    }
    
    public BigInteger getBudget() {
        return budget;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GasData gasData = (GasData) obj;
        return price == gasData.price &&
               budget == gasData.budget &&
               Objects.equals(payment, gasData.payment) &&
               Objects.equals(owner, gasData.owner);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(payment, owner, price, budget);
    }
    
    @Override
    public String toString() {
        return "GasData{" +
               "payment=" + payment +
               ", owner='" + owner + '\'' +
               ", price=" + price +
               ", budget=" + budget +
               '}';
    }
} 