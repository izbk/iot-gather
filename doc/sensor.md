# 西门子传感器数据接口 #
- 接口对接人: 张斌可
## 获取数据 ##
### 路径 ###
<pre>/sensor/read </pre>
### 请求方式 ###
* GET
### 请求参数说明 ###
| 参数名字  | 参数类型  | 参数描述  | 是否必须  |
| :------------ | :------------ | :------------ | :------------ |
|  deviceCode | string |  设备编号  |   是 |

### 返回结果示例 ###
<pre>
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
       "sensorType":3,
        "runningState": 1,
        "communicateState": 1,
        "runningTime": 30,
        "faultTime": 2,
        "technicalState": "AAA",
        "evaluationGrade": "IV",
        "uptime": "2019-12-03",
        "co2Concentration": null,
        "temperature": -16,
        "humidity": 19
    }
}
</pre>
#### 结果数据data说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  sensorType | 传感器类型 1:CO2 2:温度 3:湿度|
|  runningState | 运行状态 1:正常 0: 故障|
|  communicateState | 通信状态 1:在线 0: 离线|
|  runningTime | 运行时间 单位:小时|
|  faultTime | 故障次数 |
|  technicalState | 技术状态 |
|  evaluationGrade | 评价等级 |
|  uptime | 上线时间 |
|  co2Concentration | 二氧化碳浓度 |
|  temperature | 温度 |
|  humidity | 湿度 |

#### 传感器列表 ####
| deviceCode | 描述  |
| :------------| :------------ |
| 1F_CO2_001	| 二氧化碳传感器|
| 1F_CO2_002	| 二氧化碳传感器|
| 1F_SNWD_001	| 室内温湿度传感器|
| 1F_WD_002	| 室内温度传感器|
| 3F_WD_001	| 室内温度传感器|
| 4F_CO2_001	| 二氧化碳传感器|
| 4F_CO2_002	| 二氧化碳传感器|
| 4F_SWWD_001	| 室外温湿度传感器|
| B1_CO2_001	| 二氧化碳传感器|
| B1_CO2_002	| 二氧化碳传感器|
| B1_CO2_003	| 二氧化碳传感器|

## 获取历史数据 ##
### 路径 ###
<pre>/sensor/history</pre>
### 请求方式 ###
* GET
### 请求参数说明 ###
| 参数名字  | 参数类型  | 参数描述  | 是否必须  |
| :------------ | :------------ | :------------ | :------------ |
|  deviceCode | string |  设备编号  |   是 |

### 返回结果示例 ###
<pre>
温湿度设备数据：
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "WSD": [
            {
                "id": 693,
                "deviceCode": "1F_SNWD_001",
                "temperature": 20.47,
                "humidity": 16.75,
                "createTime": "2019-12-27 13:35:03"
            }
        ],
        "type": "WSD"
    }
}
CO2设备数据：
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "CO2": [
            {
                "id": 2810,
                "deviceCode": "B1_CO2_002",
                "co2Concentration": 446.72,
                "createTime": "2019-12-27 07:40:13"
            }
        ],
        "type": "CO2"
    }
}
温度设备数据：
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "type": "WD",
        "WD": [
            {
                "id": 912,
                "deviceCode": "3F_WD_001",
                "temperature": 0.89,
                "createTime": "2019-12-27 06:35:07"
            }
        ]
    }
}
</pre>
#### 结果数据data说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  type | 数据类型 CO2: 二氧化碳 WD:温度 WSD:温湿度 |
|  根据数据类型获取对应的数据 | 备注如下 |
|  createTime | 创建时间 |
|  co2Concentration | 二氧化碳浓度 |
|  temperature | 温度 |
|  humidity | 湿度 |