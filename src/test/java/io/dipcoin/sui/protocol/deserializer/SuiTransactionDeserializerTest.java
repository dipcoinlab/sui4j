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

package io.dipcoin.sui.protocol.deserializer;

import io.dipcoin.sui.model.transaction.kind.SuiTransaction;
import io.dipcoin.sui.protocol.DeserializerTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author : Same
 * @datetime : 2025/6/30 11:37
 * @Description : Test SuiTransaction
 */
public class SuiTransactionDeserializerTest extends DeserializerTest {

    /**
     * Test MakeMoveVec
     * @throws Exception
     */
    @Test
    void shouldDeserializeMakeMoveVec() throws Exception {
        // Prepare test data
        String json1 = "{\"MakeMoveVec\":[\"0x1::coin::Coin<0x1::sui::SUI>\",[{\"Input\":0},{\"Result\":1},{\"NestedResult\":[1,0]}]]}";
        String json2 = "{\"MakeMoveVec\":[null,[{\"Input\":2},{\"Result\":2}]]}";
        String json3 = "{\"MakeMoveVec\":[\"vector<u8>\",[]]}";
        List<String> jsons = Arrays.asList(json1, json2, json3);

        for (String json : jsons) {
            System.out.println("\njson = " + json);
            // Execute deserialization
            SuiTransaction transaction = mapper.readValue(json, SuiTransaction.class);

            // Verify results
            assertNotNull(transaction, "Deserialized object should not be null");
            // Add more assertions here based on actual SuiTransaction structure
            // assertEquals("GasCoin", transaction.getCoinType());
            // assertNotNull(transaction.getArguments());
            // assertEquals(1, transaction.getArguments().size());
            System.out.println(transaction);
        }
    }

    /**
     * Test MergeCoins
     * @throws Exception
     */
    @Test
    void shouldDeserializeMergeCoins() throws Exception {
        // Prepare test data
        String json1 = "{\"MergeCoins\": [\"GasCoin\", [{\"Input\": 0}]]}";
        String json2 = "{\"MergeCoins\": [\"GasCoin\", [{\"Input\": 0},{\"Result\": 0}]]}";
        String json3 = "{\"MergeCoins\": [\"GasCoin\", [{\"Input\": 0},{\"NestedResult\": [1,2]}]]}";

        List<String> jsons = Arrays.asList(json1, json2, json3);

        for (String json : jsons) {
            System.out.println("\njson = " + json);
            // Execute deserialization
            SuiTransaction transaction = mapper.readValue(json, SuiTransaction.class);

            // Verify results
            assertNotNull(transaction, "Deserialized object should not be null");
            // Add more assertions here based on actual SuiTransaction structure
            // assertEquals("GasCoin", transaction.getCoinType());
            // assertNotNull(transaction.getArguments());
            // assertEquals(1, transaction.getArguments().size());
            System.out.println(transaction);
        }
    }

    /**
     * test Publish
     * @throws Exception
     */
    @Test
    void shouldDeserializePublish() throws Exception {
        // Prepare test data
        String json1 = "{\"Publish\": [\"xxx\"]}";
        String json2 = "{\"Publish\": [\"xxx\", \"yyy\"]}";
        String json3 = "{\"Publish\": [\"xxx\", \"yyy\", \"zzzzzzz\"]}";

        List<String> jsons = Arrays.asList(json1, json2, json3);

        for (String json : jsons) {
            System.out.println("\njson = " + json);
            // Execute deserialization
            SuiTransaction transaction = mapper.readValue(json, SuiTransaction.class);

            // Verify results
            assertNotNull(transaction, "Deserialized object should not be null");
            // Add more assertions here based on actual SuiTransaction structure
            // assertEquals("GasCoin", transaction.getCoinType());
            // assertNotNull(transaction.getArguments());
            // assertEquals(1, transaction.getArguments().size());
            System.out.println(transaction);
        }
    }

    /**
     * test SplitCoins
     * @throws Exception
     */
    @Test
    void shouldDeserializeSplitCoins() throws Exception {
        // Prepare test data
        String json1 = "{\"SplitCoins\": [\"GasCoin\", [{\"Input\": 0}]]}";
        String json2 = "{\"SplitCoins\": [{\"Input\": 0}, [{\"Input\": 0},{\"Result\": 0}]]}";
        String json3 = "{\"SplitCoins\": [\"GasCoin\", [{\"Input\": 0},{\"NestedResult\": [1,2]}]]}";

        List<String> jsons = Arrays.asList(json1, json2, json3);

        for (String json : jsons) {
            // Prepare test data
            System.out.println("\njson = " + json);
            // Execute deserialization
            SuiTransaction transaction = mapper.readValue(json, SuiTransaction.class);

            // Verify results
            assertNotNull(transaction, "Deserialized object should not be null");
            // Add more assertions here based on actual SuiTransaction structure
            // assertEquals("GasCoin", transaction.getCoinType());
            // assertNotNull(transaction.getArguments());
            // assertEquals(1, transaction.getArguments().size());
            System.out.println(transaction);
        }
    }

    /**
     * test TransferObjects
     * @throws Exception
     */
    @Test
    void shouldDeserializeTransferObjects() throws Exception {
        // Prepare test data
        String json1 = "{\"TransferObjects\": [[{\"Input\": 0}], \"GasCoin\"]}";
        String json2 = "{\"TransferObjects\":[[{\"NestedResult\":[0,0]}], {\"Input\":0}]}";
        String json3 = "{\"TransferObjects\": [[{\"Input\": 0},{\"NestedResult\": [1,2]}], \"GasCoin\"]}";

        List<String> jsons = Arrays.asList(json1, json2, json3);

        for (String json : jsons) {
            // Prepare test data
            System.out.println("\njson = " + json);
            // Execute deserialization
            SuiTransaction transaction = mapper.readValue(json, SuiTransaction.class);

            // Verify results
            assertNotNull(transaction, "Deserialized object should not be null");
            // Add more assertions here based on actual SuiTransaction structure
            // assertEquals("GasCoin", transaction.getCoinType());
            // assertNotNull(transaction.getArguments());
            // assertEquals(1, transaction.getArguments().size());
            System.out.println(transaction);
        }
    }

    /**
     * test Upgrade
     * @throws Exception
     */
    @Test
    void shouldDeserializeUpgrade() throws Exception {
        // Prepare test data
        String json1 = "{\"Upgrade\":[[\"0x0000000000000000000000000000000000000001\",\"0x0000000000000000000000000000000000000002\"],\"0x2::sui::SUI\",{\"Input\":1}]}";
        String json2 = "{\"Upgrade\":[[\"0x5d4b302506645c3f1f6a4e5a7315f5a5e5d5c5b5a5958575655545352515049\",\"0x0123456789abcdef0123456789abcdef01234567\",\"0x89abcdef0123456789abcdef0123456789abcdef\"],\"0x3::token::Token\",{\"Result\":0}]}";
        String json3 = "{\"Upgrade\":[[\"0x1\",\"0x2\"],\"0x2::coin::Coin<0x2::sui::SUI>\",\"GasCoin\"]}";

        List<String> jsons = Arrays.asList(json1, json2, json3);

        for (String json : jsons) {
            // Prepare test data
            System.out.println("\njson = " + json);
            // Execute deserialization
            SuiTransaction transaction = mapper.readValue(json, SuiTransaction.class);

            // Verify results
            assertNotNull(transaction, "Deserialized object should not be null");
            // Add more assertions here based on actual SuiTransaction structure
            // assertEquals("GasCoin", transaction.getCoinType());
            // assertNotNull(transaction.getArguments());
            // assertEquals(1, transaction.getArguments().size());
            System.out.println(transaction);
        }
    }
}
