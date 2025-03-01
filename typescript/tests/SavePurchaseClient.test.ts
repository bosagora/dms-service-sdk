import { BOACoin, CommonUtils, NetWorkType, SavePurchaseClient } from "../src";
import { Helper } from "./helper/Helper";

import * as assert from "assert";

describe("Test of SavePurchaseClient For New Purchase", function () {
    this.timeout(1000 * 60 * 5);

    // 구매정보 저장을 위해 필요한 키
    // 네트워크 별로 가지고 있어야 한다
    // 메인넷의 키는 담당자에게 직접요청하여야 함
    // ---------------------------------------------------------------------------------------
    const KeysOfCollector: Map<number, string> = new Map([
        [NetWorkType.acc_testnet, "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276"],
        [NetWorkType.acc_mainnet, "0x0000000000000000000000000000000000000000000000000000000000000000"],
        [NetWorkType.kios_testnet, "0xa0dcffca22f13363ab5d109f3a51ca99754cff4ce4c71dccc0c5df7f6492beee"],
        [NetWorkType.kios_mainnet, "0x0000000000000000000000000000000000000000000000000000000000000000"], // 비밀키 생성후 주소만 시스템에 등록해야함
        [NetWorkType.localhost, "0x2c93e943c0d7f6f1a42f53e116c52c40fe5c1b428506dc04b290f2a77580a342"],
    ]);
    // ---------------------------------------------------------------------------------------

    // 포인트를 자산을 소유한 주소
    // ---------------------------------------------------------------------------------------
    const AssetAddresses: Map<number, string> = new Map([
        [NetWorkType.acc_testnet, "0x85EeBb1289c0d0C17eFCbadB40AeF0a1c3b46714"],
        [NetWorkType.acc_mainnet, "0xCB2e8ebBF4013164161d7F2297be25d4A9dC6b17"],
        [NetWorkType.kios_testnet, "0x153f2340807370855092D04E0e0abe4f2b634240"],
        [NetWorkType.kios_mainnet, "0xf077c9CfFa387E35de72b68448ceD5382CbC5D7D"],
        [NetWorkType.localhost, "0x4501F7aF010Cef3DcEaAfbc7Bfb2B39dE57df54d"],
    ]);
    // ---------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------
    // 키오스크의 상점 아이디
    const shopId = "0x0003be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874";

    // 테스트를 할 네트워크, 상점아이디에 따른 네트워크를 선택한다
    const network = CommonUtils.getNetWorkType(shopId);

    // 키오스크에 표시되었던 환률 심벌
    const currency = CommonUtils.getDefaultCurrencySymbol(network);

    // 구매 아이디
    const purchaseId = Helper.getPurchaseId();

    // 구매 발생 timestamp
    const timestamp = CommonUtils.getTimeStampBigInt();

    // 사용자앱에서 키오스크로 전달받은 지갑주소 (처음에는 임시주소이나, 서버에 정보요청 후 정상주소로 변환해야 함)
    // 입력되지 않았다면 ""
    const userAccount = "0x64D111eA9763c93a003cef491941A011B8df5a49";

    // 사용자가 전화번호를 입력했을 때 사용되며, 입력되지 않았다면 ""
    const userPhone = "";

    // 전체 결제 금액
    const totalAmount = 10000;

    // 포인트 사용금액
    const cacheAmount = 10000;

    // 포인트 지급까지의 대기시간, 단위는 초이다.
    // 0이면 블록생성과 기타 작업등으로 인해 테스트넷은 10초내외 메인넷은 30초 정도 후에 포인트가 제공된다.
    const waiting = 0n;
    // ---------------------------------------------------------------------------------------

    it("Save New Purchase", async () => {
        // 구매데이터를 전송하는 클라이언트를 생성한다
        // ---------------------------------------------------------------------------------------
        const savePurchaseClient = new SavePurchaseClient(
            network,
            KeysOfCollector.get(network) || "",
            AssetAddresses.get(network) || ""
        );
        // ---------------------------------------------------------------------------------------

        // 초기 잔고를 화인한다.
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Check Balance ]");
        const res = await savePurchaseClient.getBalanceAccount(userAccount);
        const balance0 = res.point.balance;

        // 신규결제에 대한 구매데이터 전송입니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Save New Purchase ]");
        await savePurchaseClient.saveNewPurchase(
            purchaseId,
            timestamp,
            waiting,
            totalAmount,
            cacheAmount,
            currency,
            shopId,
            userAccount,
            userPhone,
            [
                {
                    productId: "2020051310000000",
                    amount: cacheAmount,
                    providePercent: 10,
                },
            ]
        );
        // ---------------------------------------------------------------------------------------

        // 구매정보가 전송된 후 포인트가 지급될 때 까지 대기합니다.
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Waiting for providing... ]");
        const t1 = CommonUtils.getTimeStamp();
        while (true) {
            const res1 = await savePurchaseClient.getBalanceAccount(userAccount);
            const balance1 = res1.point.balance;
            if (balance1.toString() === balance0.add(BOACoin.make(1000).value).toString()) break;
            else if (CommonUtils.getTimeStamp() - t1 > 120) break;
            await CommonUtils.delay(1000);
        }
        // ---------------------------------------------------------------------------------------

        // 이전 잔고와 비교하여 증가된것을 확인 할 수 있습니다.
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Check Balance ]");
        const res2 = await savePurchaseClient.getBalanceAccount(userAccount);
        const balance2 = res2.point.balance;
        assert.deepStrictEqual(balance2.toString(), balance0.add(BOACoin.make(1000).value).toString());
        // ---------------------------------------------------------------------------------------
    });
});

describe("Test of SavePurchaseClient For Cancel Purchase", function () {
    this.timeout(1000 * 60 * 5);

    // 구매정보 저장을 위해 필요한 키
    // 네트워크 별로 가지고 있어야 한다
    // 메인넷의 키는 담당자에게 직접요청하여야 함
    // ---------------------------------------------------------------------------------------
    const KeysOfCollector: Map<number, string> = new Map([
        [NetWorkType.acc_testnet, "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276"],
        [NetWorkType.acc_mainnet, "0x0000000000000000000000000000000000000000000000000000000000000000"],
        [NetWorkType.kios_testnet, "0xa0dcffca22f13363ab5d109f3a51ca99754cff4ce4c71dccc0c5df7f6492beee"],
        [NetWorkType.kios_mainnet, "0x0000000000000000000000000000000000000000000000000000000000000000"], // 비밀키 생성후 주소만 시스템에 등록해야함
        [NetWorkType.localhost, "0x2c93e943c0d7f6f1a42f53e116c52c40fe5c1b428506dc04b290f2a77580a342"],
    ]);
    // ---------------------------------------------------------------------------------------

    // 포인트를 자산을 소유한 주소
    // ---------------------------------------------------------------------------------------
    const AssetAddresses: Map<number, string> = new Map([
        [NetWorkType.acc_testnet, "0x85EeBb1289c0d0C17eFCbadB40AeF0a1c3b46714"],
        [NetWorkType.acc_mainnet, "0xCB2e8ebBF4013164161d7F2297be25d4A9dC6b17"],
        [NetWorkType.kios_testnet, "0x153f2340807370855092D04E0e0abe4f2b634240"],
        [NetWorkType.kios_mainnet, "0xf077c9CfFa387E35de72b68448ceD5382CbC5D7D"],
        [NetWorkType.localhost, "0x4501F7aF010Cef3DcEaAfbc7Bfb2B39dE57df54d"],
    ]);
    // ---------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------
    // 키오스크의 상점 아이디
    const shopId = "0x0003be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874";

    // 테스트를 할 네트워크, 상점아이디에 따른 네트워크를 선택한다
    const network = CommonUtils.getNetWorkType(shopId);

    // 키오스크에 표시되었던 환률 심벌
    const currency = CommonUtils.getDefaultCurrencySymbol(network);

    // 구매 아이디
    const purchaseId = Helper.getPurchaseId();

    // 구매 발생 timestamp
    const timestamp = CommonUtils.getTimeStampBigInt();

    // 사용자앱에서 키오스크로 전달받은 지갑주소 (처음에는 임시주소이나, 서버에 정보요청 후 정상주소로 변환해야 함)
    // 입력되지 않았다면 ""
    const userAccount = "0x64D111eA9763c93a003cef491941A011B8df5a49";

    // 사용자가 전화번호를 입력했을 때 사용되며, 입력되지 않았다면 ""
    const userPhone = "";

    // 전체 결제 금액
    const totalAmount = 10000;

    // 포인트 사용금액
    const cacheAmount = 10000;

    // 포인트 지급까지의 대기시간, 단위는 초이다.
    // 0이면 블록생성과 기타 작업등으로 인해 테스트넷은 10초내외 메인넷은 30초 정도 후에 포인트가 제공된다.
    const waiting = 0n;
    // ---------------------------------------------------------------------------------------

    it("Save New & Cancel Purchase", async () => {
        // 구매데이터를 전송하는 클라이언트를 생성한다
        // ---------------------------------------------------------------------------------------
        const savePurchaseClient = new SavePurchaseClient(
            network,
            KeysOfCollector.get(network) || "",
            AssetAddresses.get(network) || ""
        );
        // ---------------------------------------------------------------------------------------

        // 초기 잔고를 화인한다.
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Check Balance ]");
        const res = await savePurchaseClient.getBalanceAccount(userAccount);
        const balance0 = res.point.balance;

        // 신규결제에 대한 구매데이터 전송입니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Save New Purchase ]");
        await savePurchaseClient.saveNewPurchase(
            purchaseId,
            timestamp,
            60n,
            totalAmount,
            cacheAmount,
            currency,
            shopId,
            userAccount,
            userPhone,
            [
                {
                    productId: "2020051310000000",
                    amount: cacheAmount,
                    providePercent: 10,
                },
            ]
        );
        // ---------------------------------------------------------------------------------------

        // 잠시 대기 합니다.
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Waiting... ]");
        await CommonUtils.delay(3000);
        // ---------------------------------------------------------------------------------------

        // 취소결제에 대한 데이터 전송입니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Save Cancel ]");
        await savePurchaseClient.saveCancelPurchase(purchaseId, timestamp, 60n);
        // ---------------------------------------------------------------------------------------

        // 잠시 대기 합니다.
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("[ Waiting... ]");
        await CommonUtils.delay(50000);
        // ---------------------------------------------------------------------------------------

        // 이전 잔고와 비교하여 변경되지 않은 것을 확인 할 수 있습니다.
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        // ---------------------------------------------------------------------------------------
        console.log("Check...");
        const res3 = await savePurchaseClient.getBalanceAccount(userAccount);
        const balance1 = res3.point.balance;
        assert.deepStrictEqual(balance1.toString(), balance0.toString());
        // ---------------------------------------------------------------------------------------
    });
});
