# dms-service-sdk

## 1. Features available
A standard development kit provided for interworking with a decentralized loyalty point system.
This SDK can be used in the following places.
1. It can be used when implementing the function of delivering purchase information paid by a KIOSK or POS.
2. It can be used when implementing the ability to purchase products using loyalty points.
3. It can be used when implementing a method in which partners deposit tokens and then provide points to users.

---

## 2. Supported Program Languages
- TypeScript
- Java
- C#

---

## 3. Using with TypeScript

See) https://www.npmjs.com/package/dms-sdk-client

---

## 4. Using with Java

### Install gradle

See) https://gradle.org/install

### Publish to MavenLocal

```shell
./gradlew clean publishToMavenLocal
```
You can verify that the library is distributed in the storage `~/.m2/repository`.

### How to use SDK in your project

Please set the contents of the file `build.gradle` as below

``` 
repositories {
    mavenLocal()
}

ependencies {
    implementation "org.json:json:20231013"
    implementation "com.googlecode.libphonenumber:libphonenumber:8.13.44"
    implementation "org.web3j:core:4.12.1"
    implementation 'org.dms.service.sdk:core:1.0.0-SNAPSHOT'
}
```

---

## 5. Using with C#


See) https://www.nuget.org/packages/dms-service-sdk


---

## 1. 사용가능한 기능들
탈중앙화된 로열티 포인트 시스템과 연동을 위해 제공되는 표준 개발 킷입니다.
이 SDK 는 다음과 같은 곳에서 사용될 수 있습니다.
1. 키오스크 또는 POS 에서 결제한 구매 정보를 전달하는 기능을 구현할 때 사용할 수 있습니다.
2. 로열티 포인트를 사용하여 제품을 구매할 수 있는 기능을 구현할 때 사용할 수 있습니다.
3. 파트너사가 토큰을 입금한 후 사용자들에게 포인트를 제공하는 방식을 구현할 때 사용할 수 있습니다.

---

## 2. 지원하는 프로그램 언어
- TypeScript
- Java
- C#

---

## 3. TypeScript 에서 사용하기

참조) https://www.npmjs.com/package/dms-sdk-client

---

## 4. Java 에서 사용하기

### gradle 설치하기

See) https://gradle.org/install

### MavenLocal 에 배포하기

```shell
./gradlew clean publishToMavenLocal
```
이 저장소 `~/.m2/repository` 에서 배포된것을 확인 할 수 있습니다.

### 프로젝트에서 SDK 사용하는 방법

파일 `build.gradle` 에 아래 내용을 추가해 주세요

``` 
repositories {
    mavenLocal()
}

ependencies {
    implementation "org.json:json:20231013"
    implementation "com.googlecode.libphonenumber:libphonenumber:8.13.44"
    implementation "org.web3j:core:4.12.1"
    implementation 'org.dms.service.sdk:core:1.0.0-SNAPSHOT'
}
```

---

## 5. C# 에서 사용하기

참조) https://www.nuget.org/packages/dms-service-sdk

