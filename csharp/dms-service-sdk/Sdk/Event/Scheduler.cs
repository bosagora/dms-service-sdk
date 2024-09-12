namespace Dms.Service.Sdk.Event;

public enum ScheduleState {
    None, Starting, Running, Stopping, Stopped
}

public  class Scheduler
{
    private Thread? _thread = null;
    private bool _done = true;
    private ScheduleState _state = ScheduleState.None;

    public void Start() {
        if (this._thread == null) {
            this._state = ScheduleState.Starting;
            this._done = false;
            this._thread = new Thread(Run);
            this._thread.Start();
        }
    }

    public void Stop() {
        this._state = ScheduleState.Stopped;
        this._done = true;
        this._thread?.Join();
    }

    private void Run() {

        this.OnStart();
        while (!this._done) {
            if (this._state == ScheduleState.Stopped) break;

            try {
                this.OnWork();
            } catch (Exception) {
                System.Console.WriteLine("Failed to execute a scheduler");
            }

            Thread.Sleep(100);

            if (this._state == ScheduleState.Stopping) {
                this._state = ScheduleState.Stopped;
            }
        }
        this.OnStop();
    }

    public virtual void OnStart()
    {
    }

    public virtual void OnWork()
    {
    }
    
    public virtual void OnStop()
    {
    }
}