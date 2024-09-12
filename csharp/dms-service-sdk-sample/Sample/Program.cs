
namespace Dms.Service.Sdk.Sample
{
    public static class Program
    {
        public static void Main(string[] args)
        {
            Console.WriteLine("Start PaymentClient");
            var paymentSample = new PaymentClientSample();
            paymentSample.TestAll();
            Console.WriteLine("End PaymentClient");
            
            
            Console.WriteLine("Start ProviderClient");
            var providerSample = new ProviderClientSample();
            providerSample.TestAll();
            Console.WriteLine("End ProviderClient");
        }
    }



}