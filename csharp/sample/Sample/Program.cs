namespace Kios.Service.Sdk.Sample;

public static class Program
{
    public static async Task Main(string[] args)
    {
        try
        {
            Console.WriteLine("Start PaymentClient");
            var paymentSample = new PaymentClientSample();
            await paymentSample.TestAll();
            Console.WriteLine("End PaymentClient");

            Console.WriteLine("Start ProviderClient");
            var providerSample = new ProviderClientSample();
            await providerSample.TestAll();
            Console.WriteLine("End ProviderClient");

            Console.WriteLine("Start SavePurchaseClient");
            var savePurchaseSample = new SavePurchaseClientSample();
            await savePurchaseSample.TestAll();
            Console.WriteLine("End SavePurchaseClient");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"오류가 발생했습니다: {ex.Message}");
        }
    }
}