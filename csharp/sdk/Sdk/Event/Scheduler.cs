namespace Acc.Service.Sdk.Event;

public enum ScheduleState
{
    None,
    Starting,
    Running,
    Stopping,
    Stopped
}

public class Scheduler
{
    private bool _done = true;
    private ScheduleState _state = ScheduleState.None;

    public void Start()
    {
        _state = ScheduleState.Starting;
        _done = false;
        Task.Run(Run);
    }

    public void Stop()
    {
        _state = ScheduleState.Stopped;
        _done = true;
    }


    private async Task Run()
    {
        await OnStart();

        while (!_done)
        {
            if (_state == ScheduleState.Stopped) break;

            try
            {
                await OnWork();
            }
            catch (Exception)
            {
                Console.WriteLine("Failed to execute a scheduler");
            }

            Thread.Sleep(1000);

            if (_state == ScheduleState.Stopping) _state = ScheduleState.Stopped;
        }

        await OnStop();
    }

    protected virtual async Task OnStart()
    {
        await Task.Delay(1);
    }

    protected virtual async Task OnWork()
    {
        await Task.Delay(1);
    }

    protected virtual async Task OnStop()
    {
        await Task.Delay(1);
    }
}