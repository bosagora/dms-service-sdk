using System.Net;
using Dms.Service.Sdk.Utils;
using Newtonsoft.Json.Linq;

namespace Dms.Service.Sdk.Client;

using Types;

public class Client
{
    protected readonly string RelayEndpoint;
    protected readonly string SaveEndpoint;
    private long _chainId;

    public Client(NetWorkType network)
    {
        switch (network)
        {
            case NetWorkType.MainNet:
                RelayEndpoint = "https://relay.main.acccoin.io";
                SaveEndpoint = "https://save.main.acccoin.io";
                break;
            case NetWorkType.TestNet:
                RelayEndpoint = "https://relay.test.acccoin.io";
                SaveEndpoint = "https://save.test.acccoin.io";
                break;
            case NetWorkType.LocalHost:
                RelayEndpoint = "http://127.0.0.1:7070";
                SaveEndpoint = "http://127.0.0.1:3030";
                break;
            default:
                RelayEndpoint = "https://relay.main.acccoin.io";
                SaveEndpoint = "https://save.main.acccoin.io";
                break;
        }

        _chainId = 0;
    }

    [Obsolete("Obsolete")]
    protected static HttpWebRequest GetHttpRequest(string url, string method = "GET")
    {
        var request =
            (HttpWebRequest)WebRequest.Create(url);
        request.Method = method;
        request.ContentType = "application/json; charset=utf-8";
        return request;
    }

    protected static JObject GetResponse(HttpWebResponse response)
    {
        var responseData = new StreamReader(response.GetResponseStream()).ReadToEnd();

        var jObject = JObject.Parse(responseData);
        var code = Convert.ToInt32(jObject["code"]!.ToString());
        if (code != 0)
        {
            string errorMessage;
            try
            {
                errorMessage = $"{jObject["error"]!["message"]} {code}";
            }
            catch (Exception)
            {
                errorMessage = $"{code}";
            }

            throw new Exception("Internal Error : " + errorMessage);
        }

        return jObject;
    }

    protected static JObject GetJObjectResponse(HttpWebResponse response)
    {
        var jObject = GetResponse(response);
        var res = jObject.GetValue("data")!.ToObject<JObject>();
        if (res == null) throw new Exception("Internal Error : Response is null");
        return res;
    }
    
    protected static JArray GetJArrayResponse(HttpWebResponse response)
    {
        var jObject = GetResponse(response);
        var res = jObject.GetValue("data")!.ToObject<JArray>();
        if (res == null) throw new Exception("Internal Error : Response is null");
        return res;
    }

    /**
     * Provide the ID of the chain
     */
    public long GetChainId()
    {
        if (_chainId != 0)
            return _chainId;

        var request = GetHttpRequest($"{RelayEndpoint}/v1/chain/side/id");
        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);

        _chainId = Convert.ToInt64(jObject["chainId"]!.ToString());
        return _chainId;
    }

    /**
     * Provide the user's points and token balance information
     * @param account User's wallet address
     */
    public UserBalanceData GetBalanceAccount(string account)
    {
        var request = GetHttpRequest($"{RelayEndpoint}/v1/ledger/balance/account/{account.Trim()}");
        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        var point = jObject.GetValue("point")!.ToObject<JObject>();
        var token = jObject.GetValue("token")!.ToObject<JObject>();
        if (point == null || token == null) throw new Exception("Internal Error : Response is null");
        return new UserBalanceData(point, token);
    }

    /**
     * Provide the user's points and token balance information
     * @param phoneNumber User's phone number
     */
    public UserBalanceData GetBalancePhone(string phoneNumber)
    {
        var request =
            GetHttpRequest($"{RelayEndpoint}/v1/ledger/balance/phone/{phoneNumber.Trim().Replace(" ", "%20")}");
        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        var point = jObject.GetValue("point")!.ToObject<JObject>();
        var token = jObject.GetValue("token")!.ToObject<JObject>();
        if (point == null || token == null) throw new Exception("Internal Error : Response is null");
        return new UserBalanceData(point, token);
    }

    /**
     * Provide the user's points and token balance information
     * @param phoneHash User's phone number hash
     */
    public UserBalanceData GetBalancePhoneHash(string phoneHash)
    {
        var request = GetHttpRequest($"{RelayEndpoint}/v1/ledger/balance/phoneHash/{phoneHash.Trim()}");
        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        var point = jObject.GetValue("point")!.ToObject<JObject>();
        var token = jObject.GetValue("token")!.ToObject<JObject>();
        if (point == null || token == null) throw new Exception("Internal Error : Response is null");
        return new UserBalanceData(point, token);
    }

    /**
     * Provide a nonce corresponding to the user's wallet address. It provides a nonce corresponding to the user's wallet address.
     * This ensures that the same signature is not repeated. And this value is recorded in Contract and automatically increases by 1.
     * @param account User's wallet address
     */
    public long GetLedgerNonceOf(string account)
    {
        var request = GetHttpRequest($"{RelayEndpoint}/v1/ledger/nonce/{account.Trim()}");
        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        return Convert.ToInt64(jObject.GetValue("nonce")!.ToString());
    }

    public string GetPhoneHash(string phone)
    {
        if (phone.Trim().Equals("")) return CommonUtils.GetPhoneHash("");
        var request = GetHttpRequest($"{RelayEndpoint}/v1/phone/hash/{phone.Trim()}");
        var response = (HttpWebResponse)request.GetResponse();
        var jObject = GetJObjectResponse(response);
        return jObject.GetValue("phoneHash")!.ToString();
    }
}