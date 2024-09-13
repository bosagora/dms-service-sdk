using System.Text;
using System.Net;
using Newtonsoft.Json.Linq;
using Nethereum.Signer;
using System.Numerics;
using Nethereum.ABI;
using Nethereum.Signer;
using Org.BouncyCastle.Crypto.Digests;
using Nethereum.Hex.HexConvertors.Extensions;

namespace Dms.Service.Sdk.Client;

using Utils;
using Types;

public class PaymentClientForUser(NetWorkType network, string privateKey) : Client(network)
{
    private readonly EthECKey _keyPair = new(privateKey);
    public string Address => _keyPair.GetPublicAddress();
    
    // region Payment
    public static byte[] GetLoyaltyNewPaymentMessage(
        string paymentId,
        string purchaseId,
        BigInteger amount,
        string currency,
        string shopId,
        string account,
        long nonce,
        long chainId
    )
    {
        var abiEncode = new ABIEncode();
        var encodedBytes = abiEncode.GetABIEncoded(
            new ABIValue("bytes32", CommonUtils.ConvertHexStringToByte(paymentId)),
            new ABIValue("string", purchaseId),
            new ABIValue("uint256", amount),
            new ABIValue("string", currency),
            new ABIValue("bytes32", CommonUtils.ConvertHexStringToByte(shopId)),
            new ABIValue("address", account),
            new ABIValue("uint256", chainId),
            new ABIValue("uint256", nonce)
        );
        var message = new byte[32];
        var digest = new KeccakDigest(256);
        digest.BlockUpdate(encodedBytes, 0, encodedBytes.Length);
        digest.DoFinal(message, 0);
        return message;
    }
    
    
    public async Task<string> GetTemporaryAccount() {
        var account = Address;
        var nonce = await this.GetLedgerNonceOf(account);
        var message = CommonUtils.GetAccountMessage(
                account,
                nonce,
                await this.GetChainId()
        );
        var signature = CommonUtils.SignMessage(this._keyPair, message);
        
        var body = new StringContent(new JObject
        {
            { "account", account },
            { "signature", signature }
        }.ToString(), Encoding.UTF8, "application/json");

        var response = await PostAsync($"{RelayEndpoint}/v2/payment/account/temporary", body);
        var jObject = ParseResponseToJObject(response);

        return jObject["temporaryAccount"]!.ToString();
    }

    public async Task<PaymentTaskItemShort> ApproveNewPayment(
            string paymentId,
            string purchaseId,
            BigInteger amount,
            string currency,
            string shopId,
            bool approval
    ) {
        var account = this.Address;
        var nonce = await this.GetLedgerNonceOf(account);
        var message = GetLoyaltyNewPaymentMessage(
                paymentId,
                purchaseId,
                amount,
                currency,
                shopId,
                account,
                nonce,
                await this.GetChainId()
        );
        var signature = CommonUtils.SignMessage(this._keyPair, message);
        
        var body = new StringContent(new JObject
        {
            { "paymentId", paymentId },
            { "approval", approval },
            { "signature", signature }
        }.ToString(), Encoding.UTF8, "application/json");

        var response = await PostAsync($"{RelayEndpoint}/v2/payment/new/approval", body);
        return PaymentTaskItemShort.FromJObject(ParseResponseToJObject(response));
    }
}