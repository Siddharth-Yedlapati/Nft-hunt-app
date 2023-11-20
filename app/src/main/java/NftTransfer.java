import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransaction;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import java.math.BigInteger;

public class NftTransfer {

    public static void main(String[] args) throws Exception {
        
        // Set up Web3j instance using the Mumbai testnet endpoint
        String infuraEndpoint = "https://polygon-mumbai.infura.io/v3/c4d6e0f87f764c9f9d0c9e0c7a8d15ab";
        Web3j web3j = Web3j.build(new HttpService(infuraEndpoint));
        
        // Set up credentials for the sender account
        String privateKey = "ecef7634790105ded52c8e066a429b5c31ffb2e881906ce71b8c0228ca570e76";
        Credentials credentials = Credentials.create(privateKey);
        
        // Set up transaction manager
        TransactionManager transactionManager = new org.web3j.tx.gas.DefaultTransactionManager(web3j, credentials);
        
        // Set up NFT contract address and token ID
        String contractAddress = "0xA07e45A987F7D02FdAe2078A760eE1CfD7d4Bf20";
        BigInteger tokenId = BigInteger.valueOf(1); // change to your desired token ID
        
        // Set up sender and recipient addresses
        String senderAddress = "0x6934B79fe2a6d7110eFE6934Fc44D48932fE3bfb";
        String recipientAddress = "0x7398K579fe2a6d5568hFE6934Fc44D12649dbkj0b";
        
        // Set up NFT transfer function parameters
        Address recipient = new Address(recipientAddress);
        Uint256 tokenIdParam = new Uint256(tokenId);
        
        // Create NFT transfer function call
        String functionSignature = "safeTransferFrom(address,address,uint256)";
        String recipientParamHex = Strings.cleanHexPrefix(recipient.getValue());
        String data = functionSignature + recipientParamHex + Numeric.toHexStringWithPrefix(tokenId);
        
        // Get sender nonce
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(senderAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        
        // Create raw transaction object
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                new BigInteger("4700000"),
                new BigInteger("1000000000"),
                contractAddress,
                new BigInteger("0"),
                data);
        
        // Sign the transaction with sender's private key
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        
        // Send the signed transaction to the Mumbai testnet
        String txHash = web3j.ethSendRawTransaction(hexValue).send().getTransactionHash();
        
        // Print transaction hash as confirmation
        System.out.println("Transaction hash: " + txHash);
    }
}