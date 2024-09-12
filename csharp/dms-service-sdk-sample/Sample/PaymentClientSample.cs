using Dms.Service.Sdk.Client;
using Dms.Service.Sdk.Types;
using Dms.Service.Sdk.Utils;
using Dms.Service.Sdk.Event;

namespace Dms.Service.Sdk.Sample
{
    public class PaymentClientSample
    {
        private readonly PaymentClient _paymentClient;
        private readonly PaymentClientForUser _userClient;
        private readonly PaymentClientForShop _shopClient;
        private readonly TaskEventCollector _collector;
        private string _temporaryAccount = "";
        private readonly string _terminalId = "POS001";
        private PaymentTaskItem? _paymentItem = null;

        public PaymentClientSample()
        {
            string privateKeyForPayment = "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276";
            _paymentClient = new PaymentClient(NetWorkType.TestNet, privateKeyForPayment);
            var listener = new TestEventListener();
            _collector = new TaskEventCollector(_paymentClient, listener);
            _collector.Start();
            _userClient = new PaymentClientForUser(NetWorkType.TestNet,
                "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c");
            _shopClient = new PaymentClientForShop(NetWorkType.TestNet,
                "0xa237d68cbb66fd5f76e7b321156c46882546ad87d662dec8b82703ac31efbf0a",
                "0x0001be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874");
        }

        private void Test01_Waiting()
        {
            Console.WriteLine("Test01_Waiting");
            Thread.Sleep(3000);
        }

        private void Test02_CreateTemporaryAccount()
        {
            Console.WriteLine("Test02_CreateTemporaryAccount");
            _temporaryAccount = _userClient.GetTemporaryAccount();
            System.Console.WriteLine($"Temporary Account: {_temporaryAccount}");
        }

        private void Test03_OpenNewPayment()
        {
            Console.WriteLine("Test03_OpenNewPayment");
            var purchaseId = CommonUtils.GetSamplePurchaseId();
            _paymentItem = _paymentClient.OpenNewPayment(
                purchaseId,
                _temporaryAccount,
                Amount.Make("1_000").Value,
                "php",
                _shopClient.ShopId,
                _terminalId
            );
            if (!_paymentItem.PurchaseId.Equals(purchaseId))
            {
                Console.WriteLine("Error");
            }
            if (!_paymentItem.Account.Equals(_userClient.Address))
            {
                Console.WriteLine("Error");
            }
        }

        private void Test04_Waiting()
        {
            Console.WriteLine("Test04_Waiting");
            Thread.Sleep(1000);
        }

        private void Test05_ApproveNewPayment()
        {
            Console.WriteLine("Test05_ApproveNewPayment");
            var res = _userClient.ApproveNewPayment(
                _paymentItem.PaymentId,
                _paymentItem.PurchaseId,
                _paymentItem.Amount,
                _paymentItem.Currency,
                _paymentItem.ShopId,
                true
            );
            if (!res.PaymentId.Equals(_paymentItem.PaymentId))
            {
                Console.WriteLine("Error");
            }
        }

        private void Test06_Waiting()
        {
            Console.WriteLine("Test06_Waiting");
            Thread.Sleep(3000);
        }

        private void Test07_CloseNewPayment()
        {
            Console.WriteLine("Test07_CloseNewPayment");
            var res = _paymentClient.CloseNewPayment(
                _paymentItem.PaymentId,
                true
            );
            if (!res.PaymentId.Equals(_paymentItem.PaymentId))
            {
                Console.WriteLine("Error");
            }
        }

        private void Test08_Waiting()
        {
            Console.WriteLine("Test08_Waiting");
            Thread.Sleep(3000);
        }

        private void Test09_OpenCancelPayment()
        {
            Console.WriteLine("Test09_OpenCancelPayment");
            var res = _paymentClient.OpenCancelPayment(
                _paymentItem.PaymentId,
                _terminalId
            );
            if (!res.PurchaseId.Equals(_paymentItem.PurchaseId))
            {
                Console.WriteLine("Error");
            }
            if (!res.Account.Equals(_userClient.Address))
            {
                Console.WriteLine("Error");
            }
        }

        private void Test10_Waiting()
        {
            Console.WriteLine("Test10_Waiting");
            Thread.Sleep(1000);
        }

        private void Test11_ApproveCancelPayment()
        {
            Console.WriteLine("Test11_ApproveCancelPayment");
            var res = _shopClient.ApproveCancelPayment(
                _paymentItem.PaymentId,
                _paymentItem.PurchaseId,
                true
            );
            if (!res.PaymentId.Equals(_paymentItem.PaymentId))
            {
                Console.WriteLine("Error");
            }
        }

        private void Test12_Waiting()
        {
            Console.WriteLine("Test12_Waiting");
            Thread.Sleep(3000);
        }

        private void Test13_CloseCancelPayment()
        {
            Console.WriteLine("Test13_CloseCancelPayment");
            var res = _paymentClient.CloseCancelPayment(
                _paymentItem.PaymentId,
                true
            );
            if (!res.PaymentId.Equals(_paymentItem.PaymentId))
            {
                Console.WriteLine("Error");
            }
        }

        private void Test14_Waiting()
        {
            Console.WriteLine("Test14_Waiting");
            Thread.Sleep(3000);
            _collector.Stop();
        }

        public void TestAll()
        {
            Test01_Waiting();
            Test02_CreateTemporaryAccount();
            Test03_OpenNewPayment();
            Test04_Waiting();
            Test05_ApproveNewPayment();
            Test06_Waiting();
            Test07_CloseNewPayment();
            Test08_Waiting();
            Test09_OpenCancelPayment();
            Test10_Waiting();
            Test11_ApproveCancelPayment();
            Test12_Waiting();
            Test13_CloseCancelPayment();
            Test14_Waiting();
        }
    }
}
