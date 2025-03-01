export class Helper {
    static _purchaseId = 0;
    static getPurchaseId(): string {
        const randomIdx = Math.floor(Math.random() * 1000);
        const res = "P" + Helper._purchaseId.toString().padStart(10, "0") + randomIdx.toString().padStart(4, "0");
        Helper._purchaseId++;
        return res;
    }
}
