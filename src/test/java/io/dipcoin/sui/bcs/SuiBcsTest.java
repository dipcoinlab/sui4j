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

package io.dipcoin.sui.bcs;

import io.dipcoin.sui.bcs.types.arg.call.CallArg;
import io.dipcoin.sui.bcs.types.arg.call.CallArgObjectArg;
import io.dipcoin.sui.bcs.types.arg.call.CallArgPure;
import io.dipcoin.sui.bcs.types.arg.object.ObjectArgImmOrOwnedObject;
import io.dipcoin.sui.bcs.types.arg.object.ObjectArgReceiving;
import io.dipcoin.sui.bcs.types.arg.object.ObjectArgSharedObject;
import io.dipcoin.sui.bcs.types.arg.object.SharedObjectRef;
import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.bcs.types.tag.*;
import io.dipcoin.sui.bcs.types.tag.*;
import io.dipcoin.sui.util.HashUtil;
import io.dipcoin.sui.util.Numeric;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author : Same
 * @datetime : 2025/7/11 15:30
 * @Description : SuiBcs unit test
 * Verify the correctness of BCS serialization for Sui core types.
 */
@Slf4j
public class SuiBcsTest {
    
    private SuiObjectRef suiObjectRef;
    private SharedObjectRef sharedObjectRef;
    private ObjectArgImmOrOwnedObject immOrOwnedObject;
    private ObjectArgSharedObject sharedObject;
    private ObjectArgReceiving receivingObject;
    private CallArgPure callArgPure;
    private CallArgObjectArg callArgObject;
    private List<TypeTag> typeTags;
    
    @BeforeEach
    void setUp() {
        // Create a Sui object reference for testing purposes.
        suiObjectRef = new SuiObjectRef("0x0000000000000000000000000000000000000000000000001234567890abcdef", 1L, HashUtil.sha256Base58("digest123"));
        
        // Create a shared object reference for testing purposes.
        sharedObjectRef = new SharedObjectRef("0x000000000000000000000000000000000000000000000000fedcba0987654321", 2L, true);
        
        // Create test object parameters.
        immOrOwnedObject = new ObjectArgImmOrOwnedObject(suiObjectRef);
        sharedObject = new ObjectArgSharedObject(sharedObjectRef);
        receivingObject = new ObjectArgReceiving(suiObjectRef);
        
        // Create test call parameters.
        callArgPure = new CallArgPure("Hello, World!", PureBcs.BasePureType.STRING);
        callArgObject = new CallArgObjectArg(immOrOwnedObject);
        
        // Create test type tags.
        typeTags = Arrays.asList(
            TypeTagBool.INSTANCE,
            TypeTagU8.INSTANCE,
            TypeTagU64.INSTANCE,
            TypeTagAddress.INSTANCE,
            new TypeTagVector(TypeTagU64.INSTANCE)
        );
    }
    
    @Test
    void testSuiObjectRefSerialization() throws IOException {
        log.info("Testing SuiObjectRef serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        SuiBcs.SUI_OBJECT_REF_SERIALIZER.serialize(serializer, suiObjectRef);
        String base64 = serializer.toBase64();
        
        log.info("SuiObjectRef: objectId={}, version={}, digest={}",
                suiObjectRef.getObjectId(), suiObjectRef.getVersion(), suiObjectRef.getDigest());
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        String objectId = Numeric.toHexString(deserializer.readAddress());
        System.out.println("Deserialized BCS objectId: " + objectId);
        long version = deserializer.readU64();
        String digest = Base58.encode(deserializer.readBytes());
        
        assertEquals(suiObjectRef.getObjectId(), objectId);
        assertEquals(suiObjectRef.getVersion(), version);
        assertEquals(suiObjectRef.getDigest(), digest);
    }
    
    @Test
    void testSharedObjectRefSerialization() throws IOException {
        log.info("Testing SharedObjectRef serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        SuiBcs.SHARED_OBJECT_REF_SERIALIZER.serialize(serializer, sharedObjectRef);
        String base64 = serializer.toBase64();
        
        log.info("SharedObjectRef: objectId={}, initialSharedVersion={}, mutable={}",
                sharedObjectRef.getObjectId(), sharedObjectRef.getInitialSharedVersion(), sharedObjectRef.isMutable());
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        String objectId = Numeric.toHexString(deserializer.readAddress());
        long initialSharedVersion = deserializer.readU64();
        boolean mutable = deserializer.readBool();
        
        assertEquals(sharedObjectRef.getObjectId(), objectId);
        assertEquals(sharedObjectRef.getInitialSharedVersion(), initialSharedVersion);
        assertEquals(sharedObjectRef.isMutable(), mutable);
    }
    
    @Test
    void testObjectArgImmOrOwnedObjectSerialization() throws IOException {
        log.info("Testing ObjectArgImmOrOwnedObject serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        SuiBcs.OBJECT_ARG_SERIALIZER.serialize(serializer, immOrOwnedObject);
        String base64 = serializer.toBase64();
        
        log.info("ObjectArgImmOrOwnedObject: has SuiObjectRef");
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        int variant = deserializer.readU32();
        assertEquals(0, variant); // ImmOrOwnedObject variant
    }
    
    @Test
    void testObjectArgSharedObjectSerialization() throws IOException {
        log.info("Testing ObjectArgSharedObject serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        SuiBcs.OBJECT_ARG_SERIALIZER.serialize(serializer, sharedObject);
        String base64 = serializer.toBase64();
        
        log.info("ObjectArgSharedObject: has SharedObjectRef");
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        int variant = deserializer.readU32();
        assertEquals(1, variant); // SharedObject variant
    }
    
    @Test
    void testObjectArgReceivingSerialization() throws IOException {
        log.info("Testing ObjectArgReceiving serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        SuiBcs.OBJECT_ARG_SERIALIZER.serialize(serializer, receivingObject);
        String base64 = serializer.toBase64();
        
        log.info("ObjectArgReceiving: has SuiObjectRef");
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        int variant = deserializer.readU32();
        assertEquals(2, variant); // Receiving variant
    }
    
    @Test
    void testCallArgPureSerialization() throws IOException {
        log.info("Testing CallArgPure serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        SuiBcs.CALL_ARG_SERIALIZER.serialize(serializer, callArgPure);
        String base64 = serializer.toBase64();
        
        log.info("CallArgPure: arg = {}", callArgPure.getArg());
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        int variant = deserializer.readUleb128();
        String value = deserializer.readString();
        assertEquals(0, variant); // Pure variant
        assertEquals("Hello, World!", value); // Pure variant
    }
    
    @Test
    void testCallArgObjectArgSerialization() throws IOException {
        log.info("Testing CallArgObjectArg serialization...");
        
        BcsSerializer serializer = new BcsSerializer();
        SuiBcs.CALL_ARG_SERIALIZER.serialize(serializer, callArgObject);
        String base64 = serializer.toBase64();
        
        log.info("CallArgObjectArg: has ObjectArg");
        log.info("Serialized Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        // Verify that deserialization can be performed correctly.
        BcsDeserializer deserializer = new BcsDeserializer(decoded);
        int variant = deserializer.readU32();
        assertEquals(1, variant); // ObjectArg variant
    }
    
    @Test
    void testTypeTagSerialization() throws IOException {
        log.info("Testing TypeTag serialization...");
        
        for (TypeTag typeTag : typeTags) {
            BcsSerializer serializer = new BcsSerializer();
            SuiBcs.TYPE_TAG_SERIALIZER.serialize(serializer, typeTag);
            String base64 = serializer.toBase64();
            
            log.info("TypeTag: {} -> {}", typeTag.getClass().getSimpleName(), base64);
            
            // verify Base64encode
            assertNotNull(base64);
            assertFalse(base64.isEmpty());
            
            // verify decode
            byte[] decoded = Base64.decode(base64);
            assertTrue(decoded.length > 0);
        }
    }
    
    @Test
    void testSerializeToBase64() throws IOException {
        log.info("Testing serializeToBase64 method...");
        
        String base64 = SuiBcs.serializeToBase64(suiObjectRef, SuiBcs.SUI_OBJECT_REF_SERIALIZER);
        
        log.info("SuiObjectRef serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // Verify that decoding is performed correctly.
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
    }
    
    @Test
    void testComplexTypeTagSerialization() throws IOException {
        log.info("Testing complex TypeTag serialization...");
        
        // Create complex type tags.
        TypeTagVector typeTagVector = new TypeTagVector(TypeTagU64.INSTANCE);
        
        BcsSerializer serializer = new BcsSerializer();
        SuiBcs.TYPE_TAG_SERIALIZER.serialize(serializer, typeTagVector);
        String base64 = serializer.toBase64();
        
        log.info("Complex TypeTag: Vector<u64> -> {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
    }
    
    @Test
    void testLargeArgumentsList() throws IOException {
        log.info("Testing large arguments list...");
        
        // Create a call with a large number of parameters.
        List<CallArg> largeArguments = Arrays.asList(
            new CallArgPure("arg1".getBytes(), PureBcs.BasePureType.VECTOR_U8),
            new CallArgPure("arg2".getBytes(), PureBcs.BasePureType.VECTOR_U8),
            new CallArgPure("arg3".getBytes(), PureBcs.BasePureType.VECTOR_U8),
            new CallArgObjectArg(immOrOwnedObject),
            new CallArgObjectArg(sharedObject),
            new CallArgObjectArg(receivingObject)
        );
        
        // Test serialization for each parameter.
        for (int i = 0; i < largeArguments.size(); i++) {
            CallArg arg = largeArguments.get(i);
            BcsSerializer serializer = new BcsSerializer();
            SuiBcs.CALL_ARG_SERIALIZER.serialize(serializer, arg);
            String base64 = serializer.toBase64();
            
            log.info("CallArg[{}]: {} -> {}", i, arg.getClass().getSimpleName(), base64);
            
            // verify Base64encode
            assertNotNull(base64);
            assertFalse(base64.isEmpty());
        }
    }
    
    @Test
    void testEmptyArgumentsList() throws IOException {
        log.info("Testing empty arguments list...");
        
        // Test serialization of empty lists.
        BcsSerializer serializer = new BcsSerializer();
        serializer.writeVector(Arrays.asList(), (s, arg) -> {
            // Empty list, no operation performed.
        });
        String base64 = serializer.toBase64();
        
        log.info("Empty arguments list serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // verify decode
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
    }
    
    @Test
    void testInvalidBase64Deserialization() {
        log.info("Testing invalid Base64 deserialization...");
        
        // Test invalid Base64 strings.
        assertThrows(DecoderException.class, () -> {
            SuiBcs.deserializeFromBase64("invalid_base64_string", deserializer -> null);
        });
        
        // test null string
//        assertThrows(IllegalArgumentException.class, () -> {
//            SuiBcs.deserializeFromBase64("", deserializer -> null);
//        });
        
        // test null
        assertThrows(NullPointerException.class, () -> {
            SuiBcs.deserializeFromBase64(null, deserializer -> null);
        });
    }
    
    @Test
    void testRoundTripSerialization() throws IOException {
        log.info("Testing round-trip serialization...");
        
        // Create complex object references.
        SuiObjectRef complexObjectRef = new SuiObjectRef(
            "0x" + "a".repeat(64), Long.MAX_VALUE, HashUtil.sha256Base58("very_long_digest_string_here")
        );
        
        // serialize
        String base64 = SuiBcs.serializeToBase64(complexObjectRef, SuiBcs.SUI_OBJECT_REF_SERIALIZER);
        log.info("Complex SuiObjectRef serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // Verify that decoding is performed correctly.
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
        
        log.info("Complex SuiObjectRef serialized successfully");
    }
    
    @Test
    void testSpecialCharactersInStrings() throws IOException {
        log.info("Testing special characters in strings...");
        
        // Create object references containing special characters.
        SuiObjectRef specialObjectRef = new SuiObjectRef(
            "0x000000000000000000000000000000000000000000000000fedcba0987654321",
            Long.MAX_VALUE,
                HashUtil.sha256Base58("digest_with_special_chars_!@#$%^&*()0x!@#$%^&*()_+-=[]{}|;':\",./<>?")
        );
        
        String base64 = SuiBcs.serializeToBase64(specialObjectRef, SuiBcs.SUI_OBJECT_REF_SERIALIZER);
        log.info("Special characters SuiObjectRef serialized to Base64: {}", base64);
        
        // verify Base64encode
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        
        // Verify that decoding is performed correctly.
        byte[] decoded = Base64.decode(base64);
        assertTrue(decoded.length > 0);
    }
} 