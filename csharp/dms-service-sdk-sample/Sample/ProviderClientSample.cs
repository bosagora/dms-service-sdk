using Nethereum.Util;
using System.Numerics;
using Dms.Service.Sdk.Client;
using Dms.Service.Sdk.Types;
using Dms.Service.Sdk.Utils;

namespace Dms.Service.Sdk.Sample
{
    public class ProviderClientSample
    {
        private ProviderClient providerClient;
        private ProviderClient agentClient;

        public ProviderClientSample()
        {
            providerClient = new ProviderClient(NetWorkType.TestNet,
                "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c");
            agentClient = new ProviderClient(NetWorkType.TestNet,
                "0x44868157d6d3524beb64c6ae41ee6c879d03c19a357dadb038fefea30e23cbab");
        }

        public void Test01_IsProvider()
        {
            Console.WriteLine("Test01_IsProvider");
            var value = providerClient.IsProvider(providerClient.Address);
            if (!value)
            {
                Console.WriteLine("Error");
            }
        }

        public void Test02_ClearAgent()
        {
            Console.WriteLine("Test02_ClearAgent");
            providerClient.SetAgent(AddressUtil.ZERO_ADDRESS);
            if (!providerClient.GetAgent().Equals(AddressUtil.ZERO_ADDRESS))
            {
                Console.WriteLine("Error");
            }
        }

        public void Test03_ProvideToAddress()
        {
            Console.WriteLine("Test03_ProvideToAddress");
            var receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
            var res1 = providerClient.GetBalanceAccount(receiver);
            var oldBalance = res1.Point.Balance;

            var amount = Amount.Make("100").Value;
            providerClient.ProvideToAddress(providerClient.Address, receiver, amount);
            var res2 = providerClient.GetBalanceAccount(receiver);

            if (!res2.Point.Balance.Equals(BigInteger.Add(oldBalance, amount)))
            {
                Console.WriteLine("Error");
            }
        }

        public void Test04_ProvideToPhone()
        {
            Console.WriteLine("Test04_ProvideToPhone");
            var phoneNumber = "+82 10-9000-5000";
            var res1 = providerClient.GetBalancePhone(phoneNumber);
            var oldBalance = res1.Point.Balance;
            var amount = Amount.Make("100").Value;

            providerClient.ProvideToPhone(providerClient.Address, phoneNumber, amount);
            var res2 = providerClient.GetBalancePhone(phoneNumber);

            if (!res2.Point.Balance.Equals(BigInteger.Add(oldBalance, amount)))
            {
                Console.WriteLine("Error");
            }
        }

        public void Test05_SetNewAgent()
        {
            Console.WriteLine("Test05_SetNewAgent");
            providerClient.SetAgent(agentClient.Address);
            if (!providerClient.GetAgent().Equals(agentClient.Address))
            {
                Console.WriteLine("Error");
            }
        }

        public void Test06_ProvideToAddress()
        {
            Console.WriteLine("Test06_ProvideToAddress");
            var receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
            var res1 = providerClient.GetBalanceAccount(receiver);
            var oldBalance = res1.Point.Balance;

            var amount = Amount.Make("100").Value;
            agentClient.ProvideToAddress(providerClient.Address, receiver, amount);
            var res2 = providerClient.GetBalanceAccount(receiver);

            if (!res2.Point.Balance.Equals(BigInteger.Add(oldBalance, amount)))
            {
                Console.WriteLine("Error");
            }
        }

        public void Test07_ProvideToPhone()
        {
            Console.WriteLine("Test07_ProvideToPhone");
            var phoneNumber = "+82 10-9000-5000";
            var res1 = providerClient.GetBalancePhone(phoneNumber);
            var oldBalance = res1.Point.Balance;
            var amount = Amount.Make("100").Value;

            agentClient.ProvideToPhone(providerClient.Address, phoneNumber, amount);
            var res2 = providerClient.GetBalancePhone(phoneNumber);

            if (!res2.Point.Balance.Equals(BigInteger.Add(oldBalance, amount)))
            {
                Console.WriteLine("Error");
            }
        }

        public void TestAll()
        {
            Test01_IsProvider();
            Test02_ClearAgent();
            Test03_ProvideToAddress();
            Test04_ProvideToPhone();
            Test05_SetNewAgent();
            Test06_ProvideToAddress();
            Test07_ProvideToPhone();
        }
    }
}
