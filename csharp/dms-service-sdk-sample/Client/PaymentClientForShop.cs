using System.Text;
using System.Net;
using Newtonsoft.Json.Linq;
using Nethereum.Signer;
using Nethereum.ABI;
using Org.BouncyCastle.Crypto.Digests;

namespace Dms.Service.Sdk.Client;

using Utils;
using Types;

public class PaymentClientForShop(NetWorkType network, string privateKey, string shopId) : Client(network)
{
    private readonly EthECKey _keyPair = new(privateKey);
    public string Address => _keyPair.GetPublicAddress();
    public string ShopId { get; } = shopId;

    // region Payment
    public static byte[] GetLoyaltyCancelPaymentMessage(
        string paymentId,
        string purchaseId,
        string account,
        long nonce,
        long chainId
    )
    {
        var abiEncode = new ABIEncode();
        var encodedBytes = abiEncode.GetABIEncoded(
            new ABIValue("bytes32", CommonUtils.ConvertHexStringToByte(paymentId)),
            new ABIValue("string", purchaseId),
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

    public async Task<PaymentTaskItemShort> ApproveCancelPayment(
        string paymentId,
        string purchaseId,
        bool approval
    ) {
        var account = this.Address;
        var nonce = await this.GetLedgerNonceOf(account);
        var message = GetLoyaltyCancelPaymentMessage(
            paymentId,
            purchaseId,
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

        var response = await PostAsync($"{RelayEndpoint}/v2/payment/cancel/approval", body);

        return PaymentTaskItemShort.FromJObject(ParseResponseToJObject(response));
    }
}