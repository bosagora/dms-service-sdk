import { PaymentClient } from "../client/PaymentClient";
import { Scheduler } from "../scheduler/Scheduler";
import { IPaymentTaskItem, IShopTaskItem } from "../types";

export interface ITaskEventListener {
    onNewPaymentEvent: (type: string, code: number, message: string, sequence: bigint, data: IPaymentTaskItem) => void;
    onNewShopEvent: (type: string, code: number, message: string, sequence: bigint, data: IShopTaskItem) => void;
}

export class TaskEventCollector extends Scheduler {
    protected client: PaymentClient;
    protected listener: ITaskEventListener;
    private sequence: bigint = 0n;

    constructor(client: PaymentClient, listener: ITaskEventListener) {
        super("*/2 * * * * *");
        this.client = client;
        this.listener = listener;
    }

    public async onStart() {
        console.info("TaskEventCollector.onStart");
        this.sequence = await this.client.getLatestTaskSequence();
        console.info(`Received sequence: ${this.sequence.toString()}`);
    }

    protected async work(): Promise<void> {
        const tasks = await this.client.getTasks(this.sequence);
        for (const task of tasks) {
            if (task.sequence > this.sequence) this.sequence = task.sequence;
            console.info(`Received sequence: ${this.sequence.toString()}`);
            if (this.listener !== undefined) {
                try {
                    if (task.type === "pay_new" || task.type === "pay_cancel") {
                        this.listener.onNewPaymentEvent(
                            task.type,
                            task.code,
                            task.message,
                            task.sequence,
                            task.data as IPaymentTaskItem
                        );
                    } else {
                        this.listener.onNewShopEvent(
                            task.type,
                            task.code,
                            task.message,
                            task.sequence,
                            task.data as IShopTaskItem
                        );
                    }
                } catch (error) {
                    console.error(`Error Callback Handler - sequence: ${task.sequence}`);
                }
            }
        }
    }

    public async onStop() {
        console.info("TaskEventCollector:onStop");
    }
}
