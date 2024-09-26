namespace Acc.Service.Sdk.Event;

public enum ScheduleState 
{
    None, Starting, Running, Stopping, Stopped
}

public  class Scheduler
{
    private bool _done = true;
    private ScheduleState _state = ScheduleState.None;

    public void Start() 
    {
        this._state = ScheduleState.Starting;
        this._done = false;
        Task.Run(Run);
    }

    public void Stop() 
    {
        this._state = ScheduleState.Stopped;
        this._done = true;
    }


    private async Task Run()
    {
        await this.OnStart();
        
        while (!this._done) 
        {
            if (this._state == ScheduleState.Stopped) break;

            try {
                await this.OnWork();
            } catch (Exception) {
                Console.WriteLine("Failed to execute a scheduler");
            }
            
            Thread.Sleep(1000);

            if (this._state == ScheduleState.Stopping) 
            {
                this._state = ScheduleState.Stopped;
            }
        }
        await this.OnStop();
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
