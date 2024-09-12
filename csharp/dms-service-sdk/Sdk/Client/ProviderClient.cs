using System.Text;
using System.Net;
using Newtonsoft.Json.Linq;
using Nethereum.Signer;
using System.Numerics;

namespace Dms.Service.Sdk.Client;

using Utils;
using Types;

public class ProviderClient(NetWorkType network, string privateKey) : Client(network)
{
    private readonly EthECKey _keyPair = new(privateKey);
    public string Address => _keyPair.GetPublicAddress();

    /**
     * Provide a nonce corresponding to the user's wallet address. It provides a nonce corresponding to the user's wallet address.
     * This ensures that the same signature is not repeated. And this value is recorded in Contract and automatically increases by 1.
     * @param account User's wallet address
     */
    public bool IsProvider(string account)
    {
        var request = GetHttpRequest($"{RelayEndpoint}/v1/provider/status/{account.Trim()}");
        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        return Convert.ToBoolean(jObject.GetValue("enable")!.ToString());
    }

    /**
     * Register the address of the assistant who directly delivers points for the registered wallet(this.wallet).
     * The assistant's wallet can be registered and used on the server.
     * The assistant does not have the authority to deposit and withdraw, only has the authority to provide points.
     * @param account Address of wallet for the agent
     */
    public string SetAgent(string account)
    {
        var nonce = GetLedgerNonceOf(Address);
        var message = CommonUtils.GetRegisterAssistanceMessage(
            Address,
            account,
            nonce,
            GetChainId()
        );
        var signature = CommonUtils.SignMessage(_keyPair, message);

        var request =
            GetHttpRequest($"{RelayEndpoint}/v1/provider/assistant/register", "POST");

        var body = Encoding.UTF8.GetBytes(new JObject
        {
            { "provider", Address },
            { "assistant", account },
            { "signature", signature }
        }.ToString());

        using (var stream = request.GetRequestStream())
        {
            stream.Write(body, 0, body.Length);
        }

        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        return jObject.GetValue("txHash")!.ToString();
    }

    /**
     * Provide the agent's address for the registered wallet(this.wallet)
     * @param provider This is provider's wallet address
     */
    public string GetAgent(string provider)
    {
        var request = GetHttpRequest($"{RelayEndpoint}/v1/provider/assistant/{provider.Trim()}");
        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        return jObject.GetValue("assistant")!.ToString();
    }

    /**
     * Provide the agent's address for the registered wallet(this.wallet)
     * @param provider This is provider's wallet address
     */
    public string GetAgent()
    {
        return GetAgent(Address);
    }

    /**
     * Points are provided to the specified address.
     * Registered wallets are used for signatures. Registered wallet(this.wallet) may be providers or helpers.
     * @param provider - wallet address of the resource provider
     * @param receiver - wallet address of the person who will receive the points
     * @param amount - amount of points
     */
    public string ProvideToAddress(string provider, string receiver, BigInteger amount)
    {
        var message =
            CommonUtils.GetProvidePointToAddressMessage(provider, receiver, amount, GetLedgerNonceOf(Address),
                GetChainId());
        var signature = CommonUtils.SignMessage(_keyPair, message);

        var request =
            GetHttpRequest($"{RelayEndpoint}/v1/provider/send/account", "POST");

        var body = Encoding.UTF8.GetBytes(new JObject
        {
            { "provider", provider },
            { "receiver", receiver },
            { "amount", amount.ToString() },
            { "signature", signature }
        }.ToString());

        using (var stream = request.GetRequestStream())
        {
            stream.Write(body, 0, body.Length);
        }

        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        return jObject.GetValue("txHash")!.ToString();
    }

    /**
     * Points are provided to the specified phone number.
     * Registered wallets are used for signatures. Registered wallet(this.wallet) may be providers or helpers.
     * @param provider - wallet address of the resource provider
     * @param receiver - phone number of the person who will receive the points
     * @param amount - amount of points
     */
    public string ProvideToPhone(string provider, string receiver, BigInteger amount)
    {
        var phoneHash = GetPhoneHash(receiver);
        var message =
            CommonUtils.GetProvidePointToPhoneMessage(provider, phoneHash, amount, GetLedgerNonceOf(Address),
                GetChainId());
        var signature = CommonUtils.SignMessage(_keyPair, message);

        var request =
            GetHttpRequest($"{RelayEndpoint}/v1/provider/send/phoneHash", "POST");

        var body = Encoding.UTF8.GetBytes(new JObject
        {
            { "provider", provider },
            { "receiver", phoneHash },
            { "amount", amount.ToString() },
            { "signature", signature }
        }.ToString());

        using (var stream = request.GetRequestStream())
        {
            stream.Write(body, 0, body.Length);
        }

        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        return jObject.GetValue("txHash")!.ToString();
    }
}
