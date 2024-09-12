using System.Text;
using System.Net;
using Newtonsoft.Json.Linq;
using Nethereum.Signer;
using System.Numerics;

namespace Dms.Service.Sdk.Client;

using Utils;
using Types;

public class PaymentClient(NetWorkType network, string privateKey) : Client(network)
{
    private readonly EthECKey _keyPair = new(privateKey);
    public string Address => _keyPair.GetPublicAddress();
    
    
    /**
     * It calculates the amount required for payment.
     * @param account   User's wallet address or temporary address
     * @param amount    Purchase amount
     * @param currency  This is currency symbol (case letter)
     */
    public PaymentInfo GetPaymentInfo(string account, BigInteger amount, string currency)  {
        var request = GetHttpRequest($"{RelayEndpoint}/v2/payment/info?account={account.Trim()}&amount={amount.ToString()}&currency={currency.Trim()}");
        var response = (HttpWebResponse)request.GetResponse();
        var data = GetJObjectResponse(response);
        return new PaymentInfo(
                data["account"]!.ToString(),
                BigInteger.Parse(data["amount"]!.ToString()),
                data["currency"]!.ToString(),
                BigInteger.Parse(data["balance"]!.ToString()),
                BigInteger.Parse(data["balanceValue"]!.ToString()),
                BigInteger.Parse(data["paidPoint"]!.ToString()),
                BigInteger.Parse(data["paidValue"]!.ToString()),
                BigInteger.Parse(data["feePoint"]!.ToString()),
                BigInteger.Parse(data["feeValue"]!.ToString()),
                BigInteger.Parse(data["totalPoint"]!.ToString()),
                BigInteger.Parse(data["totalValue"]!.ToString())
        );
    }

    /**
     * Start a new payment
     * @param purchaseId    Purchase ID
     * @param account       User's wallet address or temporary address
     * @param amount        Purchase amount
     * @param currency      This is currency symbol (case letter)
     * @param shopId        Shop ID
     * @param terminalId    Terminal ID
     */
    public PaymentTaskItem OpenNewPayment(string purchaseId, string account, BigInteger amount, string currency, string shopId, string terminalId)  {
        var message = CommonUtils.GetOpenNewPaymentMessage(
                purchaseId,
                amount,
                currency,
                shopId,
                account,
                terminalId
        );
        var signature = CommonUtils.SignMessage(this._keyPair, message);
        
        var request =
            GetHttpRequest($"{RelayEndpoint}/v2/payment/new/open", "POST");

        var body = Encoding.UTF8.GetBytes(new JObject
        {
            { "purchaseId", purchaseId },
            { "amount", amount.ToString() },
            { "currency", currency },
            { "shopId", shopId },
            { "account", account },
            { "terminalId", terminalId },
            { "signature", signature }
        }.ToString());

        using (var stream = request.GetRequestStream())
        {
            stream.Write(body, 0, body.Length);
        }

        var response = (HttpWebResponse)request.GetResponse();
        return PaymentTaskItem.FromJObject(GetJObjectResponse(response));
    }

    /**
     * Close the new payment
     * @param paymentId Payment ID
     * @param confirm If this value is true, the payment will be terminated normally, otherwise the payment will be canceled.
     */
    public PaymentTaskItem CloseNewPayment(string paymentId, bool confirm)  {
        var message = CommonUtils.GetCloseNewPaymentMessage(
                paymentId,
                confirm
        );
        var signature = CommonUtils.SignMessage(this._keyPair, message);
        
        var request =
            GetHttpRequest($"{RelayEndpoint}/v2/payment/new/close", "POST");

        var body = Encoding.UTF8.GetBytes(new JObject
        {
            { "paymentId", paymentId },
            { "confirm", confirm },
            { "signature", signature }
        }.ToString());

        using (var stream = request.GetRequestStream())
        {
            stream.Write(body, 0, body.Length);
        }

        var response = (HttpWebResponse)request.GetResponse();
        return PaymentTaskItem.FromJObject(GetJObjectResponse(response));
    }

    /**
     * Start processing cancellation of previously completed new payments
     * @param paymentId  Payment ID
     * @param terminalId Terminal ID
     */
    public PaymentTaskItem OpenCancelPayment(string paymentId, string terminalId)  {
        var message = CommonUtils.GetOpenCancelPaymentMessage(
            paymentId,
            terminalId
        );
        var signature = CommonUtils.SignMessage(this._keyPair, message);
        
        var request =
            GetHttpRequest($"{RelayEndpoint}/v2/payment/cancel/open", "POST");

        var body = Encoding.UTF8.GetBytes(new JObject
        {
            { "paymentId", paymentId },
            { "terminalId", terminalId },
            { "signature", signature }
        }.ToString());

        using (var stream = request.GetRequestStream())
        {
            stream.Write(body, 0, body.Length);
        }

        var response = (HttpWebResponse)request.GetResponse();
        return PaymentTaskItem.FromJObject(GetJObjectResponse(response));
    }

    /**
     * Close the cancellation payment
     * @param paymentId Payment ID
     * @param confirm If this value is true, the payment will be terminated normally, otherwise the payment will be canceled.
     */
    public PaymentTaskItem CloseCancelPayment(string paymentId, bool confirm)  {
        var message = CommonUtils.GetCloseCancelPaymentMessage(
            paymentId,
            confirm
        );
        var signature = CommonUtils.SignMessage(this._keyPair, message);
        
        var request =
            GetHttpRequest($"{RelayEndpoint}/v2/payment/cancel/close", "POST");

        var body = Encoding.UTF8.GetBytes(new JObject
        {
            { "paymentId", paymentId },
            { "confirm", confirm },
            { "signature", signature }
        }.ToString());

        using (var stream = request.GetRequestStream())
        {
            stream.Write(body, 0, body.Length);
        }

        var response = (HttpWebResponse)request.GetResponse();
        return PaymentTaskItem.FromJObject(GetJObjectResponse(response));
    }

    /**
     * Provide detailed information on the payment
     * @param paymentId Payment ID
     */
    public PaymentTaskItem GetPaymentItem(string paymentId)  {
        var request = GetHttpRequest($"{RelayEndpoint}/v2/payment/item?paymentId={paymentId.Trim()}");
        var response = (HttpWebResponse)request.GetResponse();

        return PaymentTaskItem.FromJObject(GetJObjectResponse(response));
    }

    public long GetLatestTaskSequence()  {
        var request = GetHttpRequest($"{RelayEndpoint}/v1/task/sequence/latest");
        var response = (HttpWebResponse)request.GetResponse();
        
        var data = GetJObjectResponse(response);
        return Convert.ToInt64(data["sequence"]!.ToString());
    }

    public JArray GetTasks(long sequence)  {
        var request = GetHttpRequest($"{RelayEndpoint}/v1/task/list/{sequence.ToString()}");
        var response = (HttpWebResponse)request.GetResponse();
        return GetJArrayResponse(response);
    }
}