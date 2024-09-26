using System.Net;
using Newtonsoft.Json.Linq;
using static System.Console;

namespace Acc.Service.Sdk.Event;

using Client;
using Types;

public interface ITaskEventListener
{
    void OnNewPaymentEvent(
        string type,
        int code,
        string message,
        long sequence,
        PaymentTaskItem paymentTaskItem
    );

    void OnNewShopEvent(
        string type,
        int code,
        string message,
        long sequence,
        ShopTaskItem shopTaskItem
    );
}

public class TaskEventCollector(PaymentClient client, ITaskEventListener listener) : Scheduler
{
    private long _sequence = 0;

    protected override async Task OnStart() 
    {
        WriteLine("TaskEventCollector:OnStart");
        try {
            this._sequence = await client.GetLatestTaskSequence();
            WriteLine($"Received sequence = {this._sequence}");
        } catch (Exception) {
            //
        }
    }

    protected override async Task OnWork() 
    {
        try
        {
            var tasks = await client.GetTasks(this._sequence);
            
            foreach (var t in tasks)
            {
                var dataType = t["type"]!.ToString();
                var code = Convert.ToInt32(t["code"]!.ToString());
                var message = t["message"]!.ToString();
                var sequence = Convert.ToInt64(t["sequence"]!.ToString());
                if (sequence > this._sequence) this._sequence = sequence;
                var data = t["data"]!.ToObject<JObject>();
                if (data == null) continue;
                if (dataType.Equals("pay_new") || dataType.Equals("pay_cancel")) {
                    var payment = PaymentTaskItem.FromJObject(data);
                    listener.OnNewPaymentEvent(
                        dataType,
                        code,
                        message,
                        sequence,
                        payment
                    );
                } else {
                    var shop = ShopTaskItem.FromJObject(data);
                    listener.OnNewShopEvent(
                        dataType,
                        code,
                        message,
                        sequence,
                        shop
                    );
                }
            }
            Thread.Sleep(2000);
        } catch (Exception) {
            //
        }
    }

    protected override async Task OnStop()
    {
        await Task.Delay(0);
        WriteLine("TaskEventCollector:OnStop");
    }
}
