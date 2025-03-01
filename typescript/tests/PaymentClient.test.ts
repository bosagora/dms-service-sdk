import {
    BOACoin,
    CommonUtils,
    IPaymentTaskItem,
    IShopTaskItem,
    ITaskEventListener,
    NetWorkType,
    PaymentClient,
    TaskEventCollector,
} from "../src";

import { Helper } from "./helper/Helper";
import { PaymentClientForShop } from "./helper/PaymentClientForShop";
import { PaymentClientForUser } from "./helper/PaymentClientForUser";

import * as assert from "assert";

class PaymentEventListener implements ITaskEventListener {
    public onNewPaymentEvent(type: string, code: number, message: string, sequence: bigint, data: IPaymentTaskItem) {
        console.log(`type: ${type.toString()}`);
        console.log(`code: ${code.toString()}`);
        console.log(`message: ${message}`);
        console.log(`sequence: ${sequence.toString()}`);
        console.log(`data: ${JSON.stringify(data)}`);
    }
    public onNewShopEvent(type: string, code: number, message: string, sequence: bigint, data: IShopTaskItem) {
        console.log(`type: ${type.toString()}`);
        console.log(`code: ${code.toString()}`);
        console.log(`message: ${message}`);
        console.log(`sequence: ${sequence.toString()}`);
        console.log(`data: ${JSON.stringify(data)}`);
    }
}

describe("Test of PaymentClient", function () {
    this.timeout(1000 * 60 * 5);

    // 포인트 결제를 위해 필요한 키
    // 네트워크 별로 가지고 있어야 한다
    // 메인넷의 키는 담당자에게 직접요청하여야 함
    // ---------------------------------------------------------------------------------------
    const KeysOfPayment: Map<number, string> = new Map([
        [NetWorkType.acc_testnet, "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276"],
        [NetWorkType.acc_mainnet, "0x0000000000000000000000000000000000000000000000000000000000000000"],
        [NetWorkType.kios_testnet, "0xa0dcffca22f13363ab5d109f3a51ca99754cff4ce4c71dccc0c5df7f6492beee"],
        [NetWorkType.kios_mainnet, "0x0000000000000000000000000000000000000000000000000000000000000000"], // 비밀키 생성후 주소만 시스템에 등록해야함
        [NetWorkType.localhost, "0x2c93e943c0d7f6f1a42f53e116c52c40fe5c1b428506dc04b290f2a77580a342"],
    ]);
    // ---------------------------------------------------------------------------------------

    // 사용자앱의 정보
    // 이것은 사용자 모바일앱을 대신해서 테스트 코드에서 신규 결제 승인을 하기 위해 필요한 것임
    // 키오스크 서버에는 구현할 필요 없음
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    // ---------------------------------------------------------------------------------------
    // 사용자 앱의 지갑의 비밀키
    const userPrivateKey: string = "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c";
    // ---------------------------------------------------------------------------------------

    // 상점앱의 정보
    // 이것은 사용자 모바일앱을 대신해서 테스트 코드에서 신규 결제 승인을 하기 위해 필요한 것임
    // 키오스크 서버에는 구현할 필요 없음
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    // ---------------------------------------------------------------------------------------
    // 상정앱의 지갑의 비밀키
    const shopPrivateKey: string = "0xa237d68cbb66fd5f76e7b321156c46882546ad87d662dec8b82703ac31efbf0a";
    // 상정앱의 상점아이디
    const shopId: string = "0x0003be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874";
    // ---------------------------------------------------------------------------------------

    // 테스트를 할 네트워크
    const network: NetWorkType = CommonUtils.getNetWorkType(shopId);

    // 키오스크의 고유번호 (옵션이며 ""로 처리되어도 됨)
    const terminalID = "POS001";
    // 키오스크에 표시되었던 포인트 구매금액
    const amount = BOACoin.make("1_000").value;
    // 키오스크에 표시되었던 환률 심벌
    const currency = CommonUtils.getDefaultCurrencySymbol(network);

    it("New Payment", async () => {
        // Create Client
        // 키오스크는 이 객체를 가지고 있어야 함
        // ---------------------------------------------------------------------------------------
        console.log("[ Create Client ]");
        const paymentClient: PaymentClient = new PaymentClient(network, KeysOfPayment.get(network) || "");
        // ---------------------------------------------------------------------------------------

        // Create Event Collector
        // 키오스크 또는 서버는 이 객체를 가지고 있어야 함
        // ---------------------------------------------------------------------------------------
        console.log("[ Create Event Collector ]");
        const eventCollector: TaskEventCollector = new TaskEventCollector(paymentClient, new PaymentEventListener());
        // ---------------------------------------------------------------------------------------

        // Start Event Collector
        // 키오스크 또는 서버는 이 객체를 가지고 있어야 함
        // ---------------------------------------------------------------------------------------
        console.log("[ Start Event Collector ]");
        await eventCollector.start();
        // ---------------------------------------------------------------------------------------

        // Create User Client
        // 이것은 사용자 모바일앱을 대신해서 테스트 코드에서 신규 결제 승인을 하기 위해 필요한 것임
        // 키오스크 서버에는 구현할 필요 없음
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Create User Client ]");
        const userClient: PaymentClientForUser = new PaymentClientForUser(network, userPrivateKey);
        // ---------------------------------------------------------------------------------------

        // Create Shop Client
        // 이것은 상점용 모바일앱을 대신해서 테스트 코드에서 취소 결제 승인을 하기 위해 필요한 것임
        // 키오스크 서버에는 구현할 필요 없음
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Create Shop Client ]");
        const shopClient: PaymentClientForShop = new PaymentClientForShop(network, shopPrivateKey, shopId);
        // ---------------------------------------------------------------------------------------

        // Create Temporary Account
        // 사용자 모바일 앱에서 실행되는 내용임
        // 키오스크 서버에는 구현할 필요 없음
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log(`[1. Begin - Temporary Account] temporaryAccount`);
        const temporaryAccount: string = await userClient.getTemporaryAccount();
        console.log(`[1. End - Temporary Account] temporaryAccount: ${temporaryAccount}`);
        // ---------------------------------------------------------------------------------------

        // Open New Payment (키오스크 시스템에서 실행해야 한다)
        // 결제를 오픈한다.
        // 키오스크 서버에서 실행해야 한다
        // ---------------------------------------------------------------------------------------
        console.log(`[2. Begin - Open New Payment] paymentId`);
        const purchaseId = Helper.getPurchaseId();
        const paymentItem = await paymentClient.openNewPayment(
            purchaseId,
            temporaryAccount,
            amount,
            currency,
            shopClient.getShopId(),
            terminalID
        );
        assert.deepStrictEqual(paymentItem.purchaseId, purchaseId);
        assert.deepStrictEqual(paymentItem.account, userClient.address);
        console.log(`[2. End - Open New Payment] paymentId: ${paymentItem.paymentId}`);
        // ---------------------------------------------------------------------------------------

        // Waiting...
        // 결제가 오픈 될 때까지 잠시 대기한다
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[   Waiting... ]");
        await CommonUtils.delay(2000);
        // ---------------------------------------------------------------------------------------

        // Approval New Payment (사용자 앱에서 실행되기 때문 실제에는 필요없음)
        // 실제는 사용자가 푸쉬메세지를 받고 사용자용 앱에서 승인을 하나,
        // 이 코드에서는 사용자앱이 없기 때문에 테스트를 위해 수동을 승인한다
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log(`[3. Begin - Approval New Payment]`);
        const res = await userClient.approveNewPayment(
            paymentItem.paymentId,
            paymentItem.purchaseId,
            paymentItem.amount,
            paymentItem.currency,
            paymentItem.shopId,
            true
        );
        assert.deepStrictEqual(res.paymentId, paymentItem.paymentId);
        console.log(`[3. End - Approval New Payment]`);
        // ---------------------------------------------------------------------------------------

        // Waiting...
        // 결제가 승인 완료 될 때까지 잠시 대기한다
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[   Waiting... ]");
        await CommonUtils.delay(2000);
        // ---------------------------------------------------------------------------------------

        // Close New Payment (키오스크 시스템에서 실행해야 한다)
        // 결제를 닫는다
        // --------------------------------------------------------------------------------------
        console.log(`[4. Begin - Close New Payment]`);
        const res2 = await paymentClient.closeNewPayment(paymentItem.paymentId, true);
        assert.deepStrictEqual(res2.paymentId, paymentItem.paymentId);
        console.log(`[4. End - Close New Payment]`);
        // ---------------------------------------------------------------------------------------

        // Waiting...
        // 직전 프로세스가 완료되기 까지 대기
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[   Waiting... ]");
        await CommonUtils.delay(2000);
        // ---------------------------------------------------------------------------------------

        // Open Cancel Payment (키오스크 시스템에서 실행해야 한다)
        // 결제를 취소를 오픈한다
        // 키오스크 서버에서 실행해야 한다
        // ---------------------------------------------------------------------------------------
        console.log(`[5. Begin - Open Cancel Payment]`);
        const res3 = await paymentClient.openCancelPayment(paymentItem.paymentId, terminalID);
        assert.deepStrictEqual(res3.paymentId, paymentItem.paymentId);
        console.log(`[5. End - Open Cancel Payment]`);
        // ---------------------------------------------------------------------------------------

        // Waiting...
        // 직전 프로세스가 완료되기 까지 대기
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[   Waiting... ]");
        await CommonUtils.delay(2000);
        // ---------------------------------------------------------------------------------------

        // Approval Cancel Payment (키오스크 시스템에서 실행해야 한다)
        // 실제는 상점주가 푸쉬메세지를 받고 상점용 앱에서 승인을 하나,
        // 이 코드에서는 상점용 앱이 없기 때문에 테스트를 위해 수동을 승인한다
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log(`[6. Begin - Approval Cancel Payment]`);
        const res4 = await shopClient.approveCancelPayment(paymentItem.paymentId, paymentItem.purchaseId, true);
        assert.deepStrictEqual(res4.paymentId, paymentItem.paymentId);
        console.log(`[6. End - Approval Cancel Payment]`);
        // ---------------------------------------------------------------------------------------

        // Waiting...
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[   Waiting... ]");
        await CommonUtils.delay(2000);
        // ---------------------------------------------------------------------------------------

        // Close Cancel Payment (키오스크 시스템에서 실행해야 한다)
        // 결제를 취소를 닫는다
        // 키오스크 서버에서 실행해야 한다
        // ---------------------------------------------------------------------------------------
        console.log(`[7. Begin - Close Cancel Payment]`);
        const res5 = await paymentClient.closeCancelPayment(paymentItem.paymentId, true);
        assert.deepStrictEqual(res5.paymentId, paymentItem.paymentId);
        console.log(`[7. End - Close Cancel Payment]`);
        // ---------------------------------------------------------------------------------------

        // Waiting...
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[   Waiting... ]");
        await CommonUtils.delay(2000);
        // ---------------------------------------------------------------------------------------

        // Stop Event Collector
        // 키오스크 또는 서버는 이 객체를 가지고 있어야 함
        // ---------------------------------------------------------------------------------------
        console.log("[ Stop Event Collector ]");
        await eventCollector.stop();
        // ---------------------------------------------------------------------------------------
    });
});
