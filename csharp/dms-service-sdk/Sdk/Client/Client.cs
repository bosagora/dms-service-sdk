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

    protected static async Task<string> GetAsync(string url)
    {
        var client = new HttpClient();
        using var response = await client.GetAsync(url);
    
        return await response.Content.ReadAsStringAsync();
    }

    protected static async Task<string> PostAsync(string url, StringContent body)
    {
        var client = new HttpClient();
        using var response = await client.PostAsync(url, body);
    
        return await response.Content.ReadAsStringAsync();
    }

    private static JObject ParseResponse(string data)
    {
        var jObject = JObject.Parse(data);
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
    
    protected static JObject ParseResponseToJObject(string data)
    {
        var jObject = ParseResponse(data);
        var res = jObject.GetValue("data")!.ToObject<JObject>();
        if (res == null) throw new Exception("Internal Error : Response is null");
        return res;
    }
    
    protected static JArray ParseResponseToJArray(string data)
    {
        var jObject = ParseResponse(data);
        var res = jObject.GetValue("data")!.ToObject<JArray>();
        if (res == null) throw new Exception("Internal Error : Response is null");
        return res;
    }
    
    /**
     * Provide the ID of the chain
     */
    public async Task<long> GetChainId()
    {
        if (_chainId != 0)
            return _chainId;

        var data = await GetAsync($"{RelayEndpoint}/v1/chain/side/id");
        var jObject = ParseResponseToJObject(data);

        _chainId = Convert.ToInt64(jObject["chainId"]!.ToString());
        return _chainId;
    }

    /**
     * Provide the user's points and token balance information
     * @param account User's wallet address
     */
    public async Task<UserBalanceData> GetBalanceAccount(string account)
    {
        var response = await GetAsync($"{RelayEndpoint}/v1/ledger/balance/account/{account.Trim()}");
        var jObject = ParseResponseToJObject(response);
        var point = jObject.GetValue("point")!.ToObject<JObject>();
        var token = jObject.GetValue("token")!.ToObject<JObject>();
        if (point == null || token == null) throw new Exception("Internal Error : Response is null");
        return new UserBalanceData(point, token);
    }

    /**
     * Provide the user's points and token balance information
     * @param phoneNumber User's phone number
     */
    public async Task<UserBalanceData> GetBalancePhone(string phoneNumber)
    {
        var response = await GetAsync($"{RelayEndpoint}/v1/ledger/balance/phone/{phoneNumber.Trim().Replace(" ", "%20")}");
        var jObject = ParseResponseToJObject(response);
        var point = jObject.GetValue("point")!.ToObject<JObject>();
        var token = jObject.GetValue("token")!.ToObject<JObject>();
        if (point == null || token == null) throw new Exception("Internal Error : Response is null");
        return new UserBalanceData(point, token);
    }

    /**
     * Provide the user's points and token balance information
     * @param phoneHash User's phone number hash
     */
    public async Task<UserBalanceData> GetBalancePhoneHash(string phoneHash)
    {
        var response = await GetAsync($"{RelayEndpoint}/v1/ledger/balance/phoneHash/{phoneHash.Trim()}");
        var jObject = ParseResponseToJObject(response);
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
    public async Task<long >GetLedgerNonceOf(string account)
    {
        var response = await GetAsync($"{RelayEndpoint}/v1/ledger/nonce/{account.Trim()}");
        var jObject = ParseResponseToJObject(response);
        return Convert.ToInt64(jObject.GetValue("nonce")!.ToString());
    }

    public async Task<string> GetPhoneHash(string phone)
    {
        if (phone.Trim().Equals("")) return CommonUtils.GetPhoneHash("");
        var response = await GetAsync($"{RelayEndpoint}/v1/phone/hash/{phone.Trim()}");
        var jObject = ParseResponseToJObject(response);
        return jObject.GetValue("phoneHash")!.ToString();
    }
}