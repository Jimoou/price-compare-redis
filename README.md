# Price-Compare-Redis

✅ 이 프로젝트는 Redis를 간단히 사용하는 코드를 구현했습니다.  
✅ Service에 사용되는 Redis의 구조와 목적, 사용방법을 이해하는 것을 목표로 하였습니다.

## Start

```yml
spring.redis.host=<your-master-redis-host>
spring.redis.port=<your-redis-port>
```

application.properties 토는 application.yaml에
인스턴스에 생성한 redis의 master host ip 주소와 port를 넣어줍니다.

❗️ 실사용이라면 master host ip는 변경될 수 있음을 염두해야 합니다.

## Overview

### Redis 구조

이 프로젝트에서는 Redis Sentinel 구조를 사용했습니다. Redis Sentinel은 기본 Redis 구조와 비교하여 다음과 같은 차이점과 장단점을 갖습니다.

<img width="573" alt="스크린샷 2023-03-29 오전 1 41 12" src="https://user-images.githubusercontent.com/109801772/228318309-46436dba-7cb0-4665-8b20-e40067b3eb7c.png">

### 차이점

- **고가용성**: Redis Sentinel은 고가용성(High Availability)을 제공합니다. 마스터 서버가 다운되면 자동으로 슬레이브 중 하나를 마스터로 승격시켜 서비스의 중단 없이 운영을 계속할 수 있습니다.

- **자동 장애 감지 및 복구**: Redis Sentinel은 마스터 및 슬레이브 서버를 모니터링하여 장애를 자동으로 감지합니다. 또한, 마스터 서버에 문제가 발생하면 슬레이브 서버 중 하나를 새로운 마스터로 승격시킵니다.

- **Sentinel 클라이언트 서비스 지원**: Redis Sentinel은 마스터 서버의 위치를 알려주는 서비스를 제공합니다. 클라이언트는 Sentinel을 통해 현재의 마스터 서버 주소와 포트를 얻을 수 있습니다.

### 장단점

<details>
<summary>접기/펼치기</summary>

#### 장점

- **자동 장애 복구**: 마스터 서버에 장애가 발생하더라도 자동으로 복구되어 서비스의 지속성이 보장됩니다.

- **고가용성**: 서비스 중단 없이 운영할 수 있어 전체 서비스의 가용성이 향상됩니다.

- **구성의 유연성**: Redis Sentinel 구성을 사용하면 여러 슬레이브 서버와 함께 마스터 서버를 사용할 수 있어, 읽기 쿼리 처리 성능을 향상시킬 수 있습니다.

#### 단점

- **구성의 복잡성**: Sentinel 구성을 설정하고 관리하기 위해서는 기본 Redis 구성에 비해 추가적인 설정과 지식이 필요합니다.

- **쓰기 지연**: 마스터 서버와 슬레이브 서버 사이에 데이터를 동기화하는 데 약간의 지연이 발생할 수 있습니다. 이로 인해 최신 데이터에 대한 일관성이 보장되지 않을 수 있습니다.

</details>

### Redis 설정

이 프로젝트에서는 AWS EC2 Instance를 사용하여 두 개의 Redis 서버(1개의 마스터, 1개의 슬레이브)와 세 개의 Sentinel을 설정했습니다. 아래 이미지는 이러한 설정을 나타냅니다.

<img width="940" alt="스크린샷 2023-03-29 오전 1 55 40" src="https://user-images.githubusercontent.com/109801772/228320348-5f23142c-7133-4cbe-8210-7c3b157a4d27.png">

- **Redis** 서버: AWS EC2 Instance를 사용하여 두 개의 Redis 서버를 구축했습니다. 이 중 하나는 마스터 서버로, 다른 하나는 슬레이브 서버로 구성되어 있습니다. 슬레이브 서버는 마스터 서버의 데이터를 동기화하여 데이터의 안정성을 높입니다.

- **Redis Sentinel**: AWS EC2 Instance를 사용하여 세 개의 Redis Sentinel을 구축했습니다. 이들 Sentinel은 마스터 서버와 슬레이브 서버를 모니터링하며, 자동 장애 복구와 고가용성을 위한 역할을 수행합니다.

#### config-overview

<details>
<summary>접기/펼치기</summary>

#### redis-sentinel

<img width="1064" alt="스크린샷 2023-03-29 오전 2 11 26" src="https://user-images.githubusercontent.com/109801772/228324634-51d6b598-39db-4cc2-9ab1-69dae97b5dd0.png">

#### AWS EC2 Instance

<img width="1164" alt="스크린샷 2023-03-29 오전 1 02 50" src="https://user-images.githubusercontent.com/109801772/228324641-6e03fa9b-f9ea-42d5-b2b2-36f5b52878d1.png">

#### role

##### my-first-redis

<img width="356" alt="스크린샷 2023-03-29 오전 1 10 40" src="https://user-images.githubusercontent.com/109801772/228325093-83e8f264-d1eb-4c56-857c-83ddb2f14093.png">

##### my-second-redis

<img width="374" alt="스크린샷 2023-03-29 오전 1 05 00" src="https://user-images.githubusercontent.com/109801772/228325107-9be431bd-fcea-4d39-a85c-5146d70e7d97.png">

</details>

### API (with.Swagger)

<img width="1209" alt="스크린샷 2023-03-29 오전 2 14 57" src="https://user-images.githubusercontent.com/109801772/228325323-27de481c-d158-494d-bd45-388a9e1fba1a.png">

<details>
<summary>request/response example</summary>

```php
curl -X 'GET' \
  'http://localhost:8080/get-zset-value?key=keyword1' \
  -H 'accept: */*'
```

```json
[
  {
    "score": 10772,
    "value": "FPG0004"
  },
  {
    "score": 10880,
    "value": "FPG0006"
  },
  {
    "score": 11707,
    "value": "FPG0007"
  },
  {
    "score": 12355,
    "value": "FPG0005"
  },
  {
    "score": 12980,
    "value": "FPG0009"
  },
  {
    "score": 13225,
    "value": "FPG0002"
  },
  {
    "score": 13715,
    "value": "FPG0001"
  },
  {
    "score": 14328,
    "value": "FPG0003"
  },
  {
    "score": 14734,
    "value": "FPG0008"
  },
  {
    "score": 19378,
    "value": "FPG0010"
  }
]
```

</details>

## Data

이 프로젝트는 세개의 VO 이루어져 있습니다.

<img width="849" alt="스크린샷 2023-03-29 오전 1 22 46" src="https://user-images.githubusercontent.com/109801772/228323083-f96df811-2658-4cd2-a9a5-407f7f00f32a.png">

#### Bonus

이 프로젝트 VO에 맞추어 redise에 더미데이터를 삽입하는 [util code](https://github.com/Jimoou/price-compare-redis/blob/main/src/main/java/com/example/pricecompare/Utils/DummyDataInsert.java) 가 있습니다.

### Stack

<img width="200" alt="logo" src="https://user-images.githubusercontent.com/109801772/228326544-80afab05-b37b-48bf-a2fd-fa961ef5382f.png">

<img width="200" alt="logo" src="https://user-images.githubusercontent.com/109801772/228326558-1d3393ab-dd6d-4d12-b139-0203ece9bed6.png">

<img width="200" alt="logo" src="https://user-images.githubusercontent.com/109801772/228326561-f9650f1f-37d8-4b2a-8bf3-240b4876e362.png">
