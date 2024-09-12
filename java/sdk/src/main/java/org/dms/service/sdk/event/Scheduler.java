package org.dms.service.sdk.event;

enum ScheduleState {
    NONE, STARTING, RUNNING, STOPPING, STOPPED
}

public abstract class Scheduler implements Runnable {
    private Thread thread;
    private Boolean done;
    private ScheduleState state;

    public Scheduler() {
        this.state = ScheduleState.NONE;
    }

    public void start() {
        if (this.thread == null) {
            this.state = ScheduleState.STARTING;
            this.done = false;
            this.thread = new Thread(this);
            this.thread.start();
        }
    }

    public void stop() {
        try {
            this.state = ScheduleState.STOPPING;
            this.done = true;
            this.thread.join();
        } catch (Exception ignore) {
        }
    }

    public void run() {

        this.onStart();

        while (!this.done) {
            if (this.state == ScheduleState.STOPPED) break;

            try {
                this.onWork();
            } catch (Exception e) {
                System.out.println("Failed to execute a scheduler");
            }

            try {
                Thread.sleep(100);
            } catch (Exception ignore) {
            }

            if (this.state == ScheduleState.STOPPING) {
                this.state = ScheduleState.STOPPED;
            }
        }

        this.onStop();
    }

    public abstract void onStart();
    public abstract void onWork();
    public abstract void onStop();
}