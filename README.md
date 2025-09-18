# Sui4J - Java SDK for Sui Blockchain

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Apache_2.0-green.svg)](LICENSE)

Sui4J is a Java SDK designed specifically for the Sui blockchain, providing complete RPC client, BCS serialization, cryptocurrency functionality, and Pyth oracle integration.

## 📋 Table of Contents

- [🚀 Environment Requirements](#-environment-requirements)
- [⚙️ Installation and Configuration](#-installation-and-configuration)
- [🎯 Basic Usage](#-basic-usage)
- [💎 Core Features](#-core-features)
  - [🔗 RPC Client](#-rpc-client)
  - [🔐 Cryptocurrency Module](#-cryptocurrency-module)
  - [📦 BCS Serialization Module](#-bcs-serialization-module)
- [🧪 Test Cases](#-test-cases)
- [📚 API Reference](#-api-reference)
- [🤝 Contributing Guide](#-contributing-guide)
- [📄 License](#-license)
- [🔗 Official Documentation Links](#-official-documentation-links)
- [💬 Support](#-support)

## 🚀 Environment Requirements

- **JDK**: 21+
- **Maven**: 3.6+
- **Network**: HTTPS connection support

## ⚙️ Installation and Configuration

### 📦 Maven Dependency

```xml
<dependency>
    <groupId>io.dipcoin</groupId>
    <artifactId>sui4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 🎯 Gradle Dependency

```gradle
implementation 'com.dipcoin:sui4j:1.0.0'
```

## 🎯 Basic Usage

### 🚀 Complete Example: From HTTP Instance Creation to Pyth Price Update

The following is a complete usage example showing how to start from creating an HTTP service instance, generate Ed25519 key pairs, and finally call Pyth updatePrice PTB operations:

```java
package io.dipcoin.sui;

import io.dipcoin.sui.bcs.types.gas.GasData;
import io.dipcoin.sui.bcs.types.gas.SuiObjectRef;
import io.dipcoin.sui.bcs.types.transaction.Argument;
import io.dipcoin.sui.bcs.types.transaction.Command;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableMoveCall;
import io.dipcoin.sui.bcs.types.transaction.ProgrammableTransaction;
import io.dipcoin.sui.client.TransactionBuilder;
import io.dipcoin.sui.crypto.Ed25519KeyPair;
import io.dipcoin.sui.crypto.SuiKeyPair;
import io.dipcoin.sui.protocol.SuiClient;
import io.dipcoin.sui.protocol.http.HttpService;
import io.dipcoin.sui.pyth.core.PythClient;
import io.dipcoin.sui.pyth.model.PythNetwork;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author : Same
 * @datetime : 2025/9/18 16:37
 * @Description : Sui4jBasicExample
 */
public class Sui4jBasicExample {

  public static void main(String[] args) throws IOException {
    // 1. Create HTTP service instance
    HttpService httpService = new HttpService("https://fullnode.testnet.sui.io:443");

    // 2. Build Sui client
    SuiClient suiClient = SuiClient.build(httpService);

    // 3. Create Pyth client
    PythClient pythClient = new PythClient(suiClient);

    // 4. Generate Ed25519 key pair
    SuiKeyPair keyPair = Ed25519KeyPair.generate();
    System.out.println("Generated address: " + keyPair.address());
    System.out.println("Private key: " + keyPair.encodePrivateKey());

    // 5. Prepare Pyth price update parameters
    String feedId = "0xf9c0172ba10dfa4d19088d94f5bf61d3b54d5bd7483a322a982e1373ee8ea31b";

    // 6. Build PTB update price transaction
    ProgrammableTransaction programmableTx = pythClient.updatePrice(feedId, PythNetwork.TESTNET);

    // 7. Add custom Move call (optional)
    ProgrammableMoveCall moveCall = new ProgrammableMoveCall(
            "0xb5f4bf7d29a8e82eb5de521cf556f1953c9fdd3d73e7f371a156c6e1ee64f24b",
            "main",
            "use_pyth_price",
            new ArrayList<>(),
            Arrays.asList(Argument.ofInput(2), Argument.ofInput(6))
    );

    Command useUpdateMoveCall = new Command.MoveCall(moveCall);
    programmableTx.addCommand(useUpdateMoveCall);

    // 8. Prepare Gas data (actual Gas object needed here)
    SuiObjectRef gasObject = new SuiObjectRef(
            "0x0b50fe6d7b86730f0f8d2e389d22d63c30a73a1034720a8a43bb5e322a9588e1",
            500452832L,
            "FZSJfo8uZMLjeppm4XSd1peiEnox8FYxMxPbZVmuUjqw"
    );

    GasData gasData = new GasData(
            Arrays.asList(gasObject),
            keyPair.address(),
            1000L,
            BigInteger.valueOf(10000000L)
    );

    // 9. Serialize transaction (using local BCS encoding)
    String transactionDataBase64 = TransactionBuilder.serializeTransactionBytes(
            programmableTx,
            keyPair.address(),
            gasData
    );

    System.out.println("Serialized transaction data: " + transactionDataBase64);

    // 10. Execute transaction (optional, requires actual Gas object)
    // suiClient.executeTransactionBlock(transactionDataBase64, keyPair);

    System.out.println("Pyth price update PTB transaction construction completed!");
  }
}
```

### ✨ Key Feature Description

1. **Local BCS Encoding**: All transactions use local BCS serialization without relying on external services
2. **Type Safety**: Uses strongly-typed Java objects to avoid string concatenation errors
3. **Modular Design**: Each functional module is independent and can be used as needed
4. **High Performance**: Optimized serialization performance supporting large-scale transaction processing

## 💎 Core Features

### 🔗 RPC Client

#### Write Operations (Write RPC)

##### PythClient PTB Update Price
- **Function**: Pyth oracle PTB update price operation (local bcs encoding)
- **Test Case**: [`PythClientTest.testPtbUpdatePrice()`](src/test/java/com/dipcoin/sui/protocol/PythClientTest.java#L70)

##### Perp Trade MoveCall
- **Function**: Perp Move call on-chain transaction (local bcs encoding)
- **Test Case**: [`PythClientTest.testMoveCallTrade()`](src/test/java/com/dipcoin/sui/protocol/PythClientTest.java#L111)

##### Perp Deposit MoveCall
- **Function**: Perp Move call deposit operation (local bcs encoding)
- **Test Case**: [`PythClientTest.testMoveCallDeposit()`](src/test/java/com/dipcoin/sui/protocol/PythClientTest.java#L304)

#### Read Operations (Read RPC)

##### Dynamic Field Object Query
- **Function**: Get dynamic field object information
- **Test Case**: 
  - [`SuiClientTest.testGetDynamicFieldObject()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L94)
  - [`PythClientTest.testGetDynamicFieldObject()`](src/test/java/com/dipcoin/sui/protocol/PythClientTest.java#L59)

##### Owned Object Query
- **Function**: Get list of objects owned by address
- **Test Case**: [`SuiClientTest.testGetOwnedObjects()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L137)

##### Single Owned Object Query
- **Function**: Get single owned object information
- **Test Case**: [`SuiClientTest.testGetOwnedObject()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L174)

##### Gas Price Query
- **Function**: Get reference gas price
- **Test Case**: [`SuiClientTest.testGetReferenceGasPrice()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L207)

##### Object Information Query
- **Function**: Get detailed information of specified object
- **Test Case**: [`SuiClientTest.testGetObject()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L228)

##### Batch Object Query
- **Function**: Batch get multiple object information
- **Test Case**: [`SuiClientTest.testMultiGetObjects()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L256)

##### Object Base Update Fee Query
- **Function**: Get object base update fee
- **Test Case**: [`SuiClientTest.testGetObjectBaseUpdateFee()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L279)

##### Pyth Package ID Query
- **Function**: Get Pyth package ID information
- **Test Case**: [`SuiClientTest.testGetPythPackageId()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L300)

##### ZkLogin Signature Verification
- **Function**: Verify ZkLogin signature
- **Test Case**: [`SuiClientTest.testVerifyZkLoginSignature()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L327)

##### Move Function Parameter Type Query
- **Function**: Get Move function parameter types
- **Test Case**: [`SuiClientTest.testGetMoveFunctionArgTypes()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L358)

##### Normalized Move Function Query
- **Function**: Get normalized Move function information
- **Test Case**: [`SuiClientTest.testGetNormalizedMoveFunction()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L386)

##### Move Module Query
- **Function**: Get Sui Move normalized module information
- **Test Case**: 
  - [`SuiClientTest.testGetSuiMoveNormalizedModule()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L407)
  - [`SuiClientTest.testGetSuiMoveNormalizedModuleByPackage()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L427)

##### Move Struct Query
- **Function**: Get normalized Move struct information
- **Test Case**: [`SuiClientTest.testGetNormalizedMoveStruct()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L448)

##### Chain Identifier Query
- **Function**: Get chain identifier
- **Test Case**: [`SuiClientTest.testGetChainIdentifier()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L471)

##### Batch Transactions
- **Function**: Execute batch transaction requests
- **Test Case**: [`SuiClientTest.testBatchTransaction()`](src/test/java/com/dipcoin/sui/protocol/SuiClientTest.java#L494)

### 🔐 Cryptocurrency Module

#### Ed25519KeyPair

##### Key Pair Generation
- **Function**: Generate new Ed25519 key pair
- **Test Case**: [`Ed25519KeyPairTest.createSuiKeyPair()`](src/test/java/com/dipcoin/sui/crypto/Ed25519KeyPairTest.java#L25)

##### Private Key Decoding
- **Function**: Decode key pair from hexadecimal private key
- **Test Case**: [`Ed25519KeyPairTest.decodeBase64_shouldWorkWithValidKey()`](src/test/java/com/dipcoin/sui/crypto/Ed25519KeyPairTest.java#L46)

##### Address Generation
- **Function**: Generate correctly formatted address
- **Test Case**: [`Ed25519KeyPairTest.address_shouldGenerateCorrectFormat()`](src/test/java/com/dipcoin/sui/crypto/Ed25519KeyPairTest.java#L56)

##### Private Key Encoding
- **Function**: Encode private key to hexadecimal format
- **Test Case**: [`Ed25519KeyPairTest.encodePrivateKey_shouldReturnOriginalHex()`](src/test/java/com/dipcoin/sui/crypto/Ed25519KeyPairTest.java#L71)

##### Public Key Bytes
- **Function**: Get 32-byte public key
- **Test Case**: [`Ed25519KeyPairTest.publicKeyBytes_shouldReturn32Bytes()`](src/test/java/com/dipcoin/sui/crypto/Ed25519KeyPairTest.java#L85)

##### Boundary Key Testing
- **Function**: Boundary condition key testing
- **Test Case**: [`Ed25519KeyPairTest.boundaryKeyTests()`](src/test/java/com/dipcoin/sui/crypto/Ed25519KeyPairTest.java#L140)

#### Secp256k1KeyPair

##### Key Pair Generation
- **Function**: Generate new Secp256k1 key pair
- **Test Case**: [`Scep256k1KeyPairTest.createSuiKeyPair()`](src/test/java/com/dipcoin/sui/crypto/Scep256k1KeyPairTest.java#L25)

##### Private Key Decoding
- **Function**: Decode key pair from hexadecimal private key
- **Test Case**: [`Scep256k1KeyPairTest.decodeBase64_shouldWorkWithValidKey()`](src/test/java/com/dipcoin/sui/crypto/Scep256k1KeyPairTest.java#L46)

##### Address Generation
- **Function**: Generate correctly formatted address
- **Test Case**: [`Scep256k1KeyPairTest.address_shouldGenerateCorrectFormat()`](src/test/java/com/dipcoin/sui/crypto/Scep256k1KeyPairTest.java#L56)

##### Private Key Encoding
- **Function**: Encode private key to hexadecimal format
- **Test Case**: [`Scep256k1KeyPairTest.encodePrivateKey_shouldReturnOriginalHex()`](src/test/java/com/dipcoin/sui/crypto/Scep256k1KeyPairTest.java#L99)

### 📦 BCS Serialization Module

#### Primitive type serialization

##### U8 Type Serialization
- **Function**: Serialize 8-bit unsigned integer
- **Test Case**: [`BcsSerializerTest.testWriteU8()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L31)

##### U16 Type Serialization
- **Function**: Serialize 16-bit unsigned integer
- **Test Case**: [`BcsSerializerTest.testWriteU16()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L48)

##### U32 Type Serialization
- **Function**: Serialize 32-bit unsigned integer
- **Test Case**: [`BcsSerializerTest.testWriteU32()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L68)

##### U64 Type Serialization
- **Function**: Serialize 64-bit unsigned integer
- **Test Case**: [`BcsSerializerTest.testWriteU64()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L91)

##### U128 Type Serialization
- **Function**: Serialize 128-bit unsigned integer
- **Test Case**: [`BcsSerializerTest.testWriteU128()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L118)

##### U256 Type Serialization
- **Function**: Serialize 256-bit unsigned integer
- **Test Case**: [`BcsSerializerTest.testWriteU256()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L134)

##### Boolean Type Serialization
- **Function**: Serialize boolean values
- **Test Case**: [`BcsSerializerTest.testWriteBool()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L150)

##### String Serialization
- **Function**: Serialize strings
- **Test Case**: [`BcsSerializerTest.testWriteString()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L175)

##### Byte Array Serialization
- **Function**: Serialize byte arrays
- **Test Case**: [`BcsSerializerTest.testWriteBytes()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L196)

##### Vector Serialization
- **Function**: Serialize vector types
- **Test Case**: [`BcsSerializerTest.testWriteVector()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L221)

##### Optional Type Serialization
- **Function**: Serialize optional values
- **Test Case**: [`BcsSerializerTest.testWriteOption()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L242)

##### Complex Serialization
- **Function**: Complex type combination serialization
- **Test Case**: [`BcsSerializerTest.testComplexSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L268)

##### Serializer Reset
- **Function**: Reset serializer state
- **Test Case**: [`BcsSerializerTest.testReset()`](src/test/java/com/dipcoin/sui/bcs/BcsSerializerTest.java#L288)

#### Sui Object Serialization

##### Basic Type Serialization
- **Function**: Basic BCS type serialization
- **Test Case**: [`BcsIndexTest.testBasicTypesSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L117)

##### Address and Object Serialization
- **Function**: Address and Sui object serialization
- **Test Case**: [`BcsIndexTest.testAddressAndObjectSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L158)

##### Type Tag Serialization
- **Function**: Type tag serialization
- **Test Case**: [`BcsIndexTest.testTypeTagSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L196)

##### Transaction Serialization
- **Function**: Transaction data serialization
- **Test Case**: [`BcsIndexTest.testTransactionSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L233)

#### Test complex transaction serialization

##### Move Call Deposit Serialization
- **Function**: Move call deposit transaction serialization
- **Test Case**: [`BcsIndexTest.testMoveCallDepositSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L319)

##### Move Call Transaction Serialization
- **Function**: Move call transaction serialization
- **Test Case**: [`BcsIndexTest.testMoveCallTradeSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L468)

##### PTB Update Price Serialization
- **Function**: PTB update price transaction serialization
- **Test Case**: [`BcsIndexTest.testPtbUpdatePriceSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L709)

##### Test complex transaction serialization
- **Function**: Complex transaction combination serialization
- **Test Case**: [`BcsIndexTest.testComplexTransactionSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L965)

#### Signature Message Serialization

##### Intent Serialization
- **Function**: Intent message serialization
- **Test Case**: [`BcsIndexTest.testIntentSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L804)

##### Intent String Serialization
- **Function**: Intent string serialization
- **Test Case**: [`BcsIndexTest.testIntentStringSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L850)

##### Signature Serialization
- **Function**: Signature data serialization
- **Test Case**: [`BcsIndexTest.testSignatureSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L864)

##### Owner Serialization
- **Function**: Owner information serialization
- **Test Case**: [`BcsIndexTest.testOwnerSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L908)

##### Authentication Serialization
- **Function**: Authentication information serialization
- **Test Case**: [`BcsIndexTest.testAuthSerialization()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L949)

#### Transaction Data Message Serialization

##### Transaction Data Message
- **Function**: Transaction data message serialization
- **Test Case**: [`IntentBcsTest.testTransactionDataMessage()`](src/test/java/com/dipcoin/sui/bcs/IntentBcsTest.java#L31)

##### Personal Message
- **Function**: Personal message serialization
- **Test Case**: [`IntentBcsTest.testPersonalMessage()`](src/test/java/com/dipcoin/sui/bcs/IntentBcsTest.java#L58)

#### Transaction Effects Serialization

##### Gas Cost Summary Serialization
- **Function**: Gas cost summary serialization
- **Test Case**: [`EffectsBcsTest.testGasCostSummarySerialization()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L61)

##### Transaction Effects V1 Serialization
- **Function**: Transaction effects V1 serialization
- **Test Case**: [`EffectsBcsTest.testTransactionEffectsV1Serialization()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L95)

##### Transaction Effects Serialization
- **Function**: Transaction effects serialization
- **Test Case**: [`EffectsBcsTest.testTransactionEffectsSerialization()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L125)

##### Serialize to Base64
- **Function**: Serialize results to Base64
- **Test Case**: [`EffectsBcsTest.testSerializeToBase64()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L150)

##### Deserialize from Base64
- **Function**: Deserialize from Base64
- **Test Case**: [`EffectsBcsTest.testDeserializeFromBase64()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L167)

##### Failed Execution Status
- **Function**: Failed execution status serialization
- **Test Case**: [`EffectsBcsTest.testFailedExecutionStatus()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L197)

##### Empty Object Lists
- **Function**: Empty object lists serialization
- **Test Case**: [`EffectsBcsTest.testEmptyObjectLists()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L231)

##### Large Object Lists
- **Function**: Large object lists serialization
- **Test Case**: [`EffectsBcsTest.testLargeObjectLists()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L266)

##### Unsupported Version
- **Function**: Unsupported version handling
- **Test Case**: [`EffectsBcsTest.testUnsupportedVersion()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L329)

##### Round-trip Serialization
- **Function**: Serialization-deserialization round-trip testing
- **Test Case**: [`EffectsBcsTest.testRoundTripSerialization()`](src/test/java/com/dipcoin/sui/bcs/EffectsBcsTest.java#L346)

#### Sui BCS Serialization

##### Sui Object Reference Serialization
- **Function**: Sui object reference serialization
- **Test Case**: [`SuiBcsTest.testSuiObjectRefSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L75)

##### Shared Object Reference Serialization
- **Function**: Shared object reference serialization
- **Test Case**: [`SuiBcsTest.testSharedObjectRefSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L104)

##### Object Parameter Serialization
- **Function**: Object parameter serialization
- **Test Case**: 
  - [`SuiBcsTest.testObjectArgImmOrOwnedObjectSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L135)
  - [`SuiBcsTest.testObjectArgSharedObjectSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L160)
  - [`SuiBcsTest.testObjectArgReceivingSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L185)

##### Call Parameter Serialization
- **Function**: Call parameter serialization
- **Test Case**: 
  - [`SuiBcsTest.testCallArgPureSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L210)
  - [`SuiBcsTest.testCallArgObjectArgSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L237)

##### Type Tag Serialization
- **Function**: Type tag serialization
- **Test Case**: [`SuiBcsTest.testTypeTagSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L262)

##### Serialize to Base64
- **Function**: Serialize results to Base64
- **Test Case**: [`SuiBcsTest.testSerializeToBase64()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L283)

##### Complex Type tag serialization
- **Function**: Complex type tag serialization
- **Test Case**: [`SuiBcsTest.testComplexTypeTagSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L300)

##### Large Parameter List Serialization
- **Function**: Large parameter list serialization
- **Test Case**: [`SuiBcsTest.testLargeArgumentsList()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L322)

##### Empty Parameter List Serialization
- **Function**: Empty parameter list serialization
- **Test Case**: [`SuiBcsTest.testEmptyArgumentsList()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L351)

##### Invalid Base64 Deserialization
- **Function**: Invalid Base64 deserialization handling
- **Test Case**: [`SuiBcsTest.testInvalidBase64Deserialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L373)

##### Round-trip Serialization
- **Function**: Serialization-deserialization round-trip testing
- **Test Case**: [`SuiBcsTest.testRoundTripSerialization()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L393)

##### Special Character String Serialization
- **Function**: Special character string serialization
- **Test Case**: [`SuiBcsTest.testSpecialCharactersInStrings()`](src/test/java/com/dipcoin/sui/bcs/SuiBcsTest.java#L417)

#### Pure BCS Serialization

##### Basic Type Serialization
- **Function**: Primitive type serialization.
- **Test Case**: [`PureBcsTest.testBasicTypeSerialization()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L24)

##### Vector Type Serialization
- **Function**: Vector type serialization
- **Test Case**: [`PureBcsTest.testVectorTypeSerialization()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L55)

##### Serialize to Base64
- **Function**: Serialize results to Base64
- **Test Case**: [`PureBcsTest.testSerializeToBase64()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L76)

##### Deserialize from Base64
- **Function**: Deserialize from Base64
- **Test Case**: [`PureBcsTest.testDeserializeFromBase64()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L93)

##### Empty Vector Serialization
- **Function**: Empty vector serialization
- **Test Case**: [`PureBcsTest.testEmptyVectorSerialization()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L110)

##### Large Vector Serialization
- **Function**: Large vector serialization
- **Test Case**: [`PureBcsTest.testLargeVectorSerialization()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L130)

##### Round-trip Serialization
- **Function**: Serialization-deserialization round-trip testing
- **Test Case**: [`PureBcsTest.testRoundTripSerialization()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L174)

##### Special Character String Serialization
- **Function**: Special character string serialization
- **Test Case**: [`PureBcsTest.testSpecialCharactersInStrings()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L204)

##### Zero Value Serialization
- **Function**: Zero value serialization
- **Test Case**: [`PureBcsTest.testZeroValues()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L224)

##### Maximum Value Serialization
- **Function**: Maximum value serialization
- **Test Case**: [`PureBcsTest.testMaxValues()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L249)

##### Multiple Serializations
- **Function**: Multiple serialization testing
- **Test Case**: [`PureBcsTest.testMultipleSerializations()`](src/test/java/com/dipcoin/sui/bcs/PureBcsTest.java#L276)

#### Performance Testing

##### Performance Testing
- **Function**: BCS serialization performance testing
- **Test Case**: [`BcsIndexTest.testPerformance()`](src/test/java/com/dipcoin/sui/bcs/BcsIndexTest.java#L1041)

## 🧪 Test Cases

### 🏃‍♂️ Run All Tests

```bash
mvn test
```

### 🎯 Run Specific Module Tests

```bash
# RPC client tests
mvn test -Dtest=SuiClientTest
mvn test -Dtest=PythClientTest

# Cryptocurrency tests
mvn test -Dtest=Ed25519KeyPairTest
mvn test -Dtest=Scep256k1KeyPairTest

# BCS serialization tests
mvn test -Dtest=BcsIndexTest
mvn test -Dtest=SuiBcsTest
mvn test -Dtest=EffectsBcsTest
mvn test -Dtest=IntentBcsTest
mvn test -Dtest=PureBcsTest
mvn test -Dtest=BcsSerializerTest
```

## 📚 API Reference

### 📋 Main Classes

- `SuiClient` - Sui RPC client
- `PythClient` - Pyth oracle client
- `BcsSerializer` - BCS serializer
- `BcsDeserializer` - BCS deserializer
- `Ed25519KeyPair` - Ed25519 key pair
- `Secp256k1KeyPair` - Secp256k1 key pair
- `CommandBuilder` - Command builder
- `TransactionBuilder` - Transaction builder
- `QueryBuilder` - Query builder

### 🌐 Network Configuration

- **Testnet**: `https://fullnode.testnet.sui.io:443`
- **Mainnet**: `https://fullnode.mainnet.sui.io:443`

## 🤝 Contributing Guide

We welcome community contributions! Please follow these steps:

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 🛠️ Development Environment Setup

```bash
# Clone the project
git clone https://github.com/dipcoinlab/sui4j.git
cd sui4j

# Install dependencies
mvn install

# Run tests
mvn test
```

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 📃 Acknowledgements
This project includes code derived from [Web3j](https://github.com/LFDT-web3j/web3j),
licensed under the Apache License 2.0.

## 🔗 Official Documentation Links

- [Sui Official Documentation](https://docs.sui.io/)
- [Pyth Network Documentation](https://docs.pyth.network/)
- [BCS Serialization Specification](https://github.com/MystenLabs/ts-sdks/tree/main/packages/bcs)

## 💬 Support

If you encounter problems or have questions, please:

1. Check [Issues](https://github.com/dipcoinlab/sui4j/issues)
2. Create a new Issue
3. Contact the maintenance team

---

**Note**: This is a project under development, and APIs may change. Please test thoroughly before using in production environments.