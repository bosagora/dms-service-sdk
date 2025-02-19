namespace Kios.Service.Sdk.Event;

using Types;

public class TestEventListener : ITaskEventListener
{
    public void OnNewPaymentEvent(
        string type,
        int code,
        string message,
        long sequence,
        PaymentTaskItem paymentTaskItem
    )
    {
        Console.WriteLine($"  -> OnNewPaymentEvent {type} - {code} - {message} - {sequence}");
    }

    public void OnNewShopEvent(
        string type,
        int code,
        string message,
        long sequence,
        ShopTaskItem shopTaskItem
    )
    {
        Console.WriteLine($"  -> OnNewShopEvent {type} - {code} - {message} - {sequence}");
    }
}