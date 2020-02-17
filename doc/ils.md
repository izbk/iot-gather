# 照明系统数据接口 #
- 接口对接人: 张斌可
## 功能控制 ##
### 路径 ###
<pre>/ils/exec </pre>
### 请求方式 ###
* POST
### 请求参数说明 ###
| 参数名字  | 参数类型  | 参数描述  | 是否必须  |
| :------------ | :------------ | :------------ | :------------ |
|  deviceCode | string |  设备编号  |   是 |
|  command | int |  指令编号  |   是 |

#### command 列表 ###
| command | 描述  |
| :------------| :------------ |
| 4001   | 系统灯全开 |     
| 4002   | 系统灯全关 |
| 4003   | 楼层灯全开 |
| 4004   | 楼层灯全关 |
| 4005   | 房间灯全开 |
| 4006   | 房间灯全关 |
| 4007   | 吊灯开 |
| 4008   | 吊灯关 |
| 4009   | 背景灯开 |
| 4011   | 背景灯关 |
| 4012   | 射灯开 |
| 4013   | 射灯关 |
| 4014   | 射灯1开 |
| 4015   | 射灯1关 |
| 4016   | 筒灯开 |
| 4017   | 筒灯关 |
| 4018   | 灯带开 |
| 4019   | 灯带关 |
| 4020   | 灯带1开 |
| 4021   | 灯带1关 |
| 4022   | 灯带2开 |
| 4023   | 灯带2关 |
| 10001  | 启动“度假情景” |
| 10002  | 启动“离家情景” |
| 10003  | 启动“回家情景” |
| 10004  | 启动目标房间“迎宾情景” |
| 10005  | 启动目标房间“会客情景” |
| 10006  | 启动目标房间“送客情景” |
| 10007  | 启动目标房间“炊事情景” |
| 10008  | 启动目标房间“就餐情景” |
| 10009  | 启动目标房间“睡眠情景” |
| 10010  | 启动目标房间“起夜情景” |
| 10011  | 启动目标房间“早起情景” |
| 10012  | 启动目标房间“阅读情景” |
| 10013  | 启动目标房间“浪漫情景” |
| 10014  | 启动目标房间“娱乐情景” |
| 10015  | 启动目标房间“电视情景” |
| 10016  | 启动目标房间“影院情景” |
| 10017  | 启动目标房间“音乐情景” |
| 10018  | 启动目标房间“游戏情景” |
| 10019  | 启动目标房间“K 歌情景” |
| 10020  | 启动目标房间“沐浴情景” |
| 10021  | 启动目标房间“监护情景” |
| 10022  | 启动目标房间“进门情景” |
| 10023  | 启动目标房间“出门情景” |
| 10024  | 启动目标房间“备餐情景” |
| 10025  | 启动目标房间“入场情景” |
| 10026  | 启动目标房间“离场情景” |
| 10027  | 启动目标房间“会议情景” |
| 10028  | 启动目标房间“幻灯情景” |
| 10029  | 启动目标房间“演讲情景” |
| 10030  | 启动目标房间“休会情景” |
| 10031  | 启动目标房间“工作情景” |
| 10032  | 启动目标房间“休息情景” |

### 请求参数示例 ###
<pre>
{
    "deviceCode": "2F_ZM_001",
    "command": "4006"
}
</pre>
### 返回结果示例 ###
<pre>
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": [
        {
            "floor": "2F",
            "room": "会议室",
            "deviceName": "筒灯",
            "state": 0,
            "updateTime": 1576735984000
        },
        {
            "floor": "2F",
            "room": "会议室",
            "deviceName": "灯带",
            "state": 0,
            "updateTime": 1576735984000
        },
        {
            "floor": "2F",
            "room": "会议室",
            "deviceName": "背景灯",
            "state": 0,
            "updateTime": 1576735984000
        }
    ]
}
</pre>
#### 结果数据data说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  floor | 楼层|
|  room | 房间|
|  deviceName | 设备名称|
|  state | 开关状态 1:开 0: 关|
|  updateTime | 更新时间|

## 获取设备最新状态 ##
### 路径 ###
<pre>/ils/deviceState </pre>
### 请求方式 ###
* GET
### 请求参数说明 ###
| 参数名字  | 参数类型  | 参数描述  | 是否必须  |
| :------------ | :------------ | :------------ | :------------ |
|  floor | string |  设备编号  |   否 |
|  room | string |  设备编号  |   否 |

#### 楼层房间参数列表 ####
| 楼层 | 房间  |
| :------------| :------------ |
| 1F  | 前台 |
| 1F  | 展厅 |
| 1F  | 接待室 |
| 2F  | 会议室 |
| B1 | 餐厅 |
| B1 | 中餐厅 |
| B1 | 影音室 |
### 返回结果示例 ###
<pre>
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "2F": {
            "会议室": [
                {
                    "floor": "2F",
                    "room": "会议室",
                    "deviceName": "筒灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "2F",
                    "room": "会议室",
                    "deviceName": "灯带",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "2F",
                    "room": "会议室",
                    "deviceName": "背景灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                }
            ]
        },
        "1F": {
            "接待室": [
                {
                    "floor": "1F",
                    "room": "接待室",
                    "deviceName": "吊灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "接待室",
                    "deviceName": "灯带",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "接待室",
                    "deviceName": "筒灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                }
            ],
            "前台": [
                {
                    "floor": "1F",
                    "room": "前台",
                    "deviceName": "筒灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "前台",
                    "deviceName": "筒灯1",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "前台",
                    "deviceName": "射灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "前台",
                    "deviceName": "射灯1",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "前台",
                    "deviceName": "背景灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "前台",
                    "deviceName": "灯带",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                }
            ],
            "展厅": [
                {
                    "floor": "1F",
                    "room": "展厅",
                    "deviceName": "筒灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "展厅",
                    "deviceName": "筒灯1",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "展厅",
                    "deviceName": "射灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "展厅",
                    "deviceName": "射灯1",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "展厅",
                    "deviceName": "灯带",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "展厅",
                    "deviceName": "灯带1",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "1F",
                    "room": "展厅",
                    "deviceName": "灯带2",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                }
            ]
        },
        "B1": {
            "餐厅": [
                {
                    "floor": "B1",
                    "room": "餐厅",
                    "deviceName": "吊灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "B1",
                    "room": "餐厅",
                    "deviceName": "灯带",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "B1",
                    "room": "餐厅",
                    "deviceName": "筒灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                }
            ],
            "中餐厅": [
                {
                    "floor": "B1",
                    "room": "中餐厅",
                    "deviceName": "吊灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "B1",
                    "room": "中餐厅",
                    "deviceName": "灯带",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "B1",
                    "room": "中餐厅",
                    "deviceName": "筒灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "B1",
                    "room": "中餐厅",
                    "deviceName": "背景灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                }
            ],
            "影音室": [
                {
                    "floor": "B1",
                    "room": "影音室",
                    "deviceName": "灯带",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                },
                {
                    "floor": "B1",
                    "room": "影音室",
                    "deviceName": "筒灯",
                    "state": 0,
                    "updateTime": "2019-12-19 14:13:04"
                }
            ]
        }
    }
}
</pre>
#### 结果数据data说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  floor | 楼层|
|  room | 房间|
|  deviceName | 设备名称|
|  state | 开关状态 1:开 0: 关|
|  updateTime | 更新时间|


## 获取设备最新状态 ##
### 路径 ###
<pre>/ils/oneState </pre>
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
    "data": [
        {
            "floor": "F1",
            "room": "前台",
            "deviceName": "筒灯",
            "state": 0,
            "updateTime": "2020-01-06 11:15:37"
        },
        {
            "floor": "F1",
            "room": "前台",
            "deviceName": "筒灯1",
            "state": 0,
            "updateTime": "2020-01-06 11:15:37"
        },
        {
            "floor": "F1",
            "room": "前台",
            "deviceName": "射灯",
            "state": 0,
            "updateTime": "2020-01-06 11:15:37"
        },
        {
            "floor": "F1",
            "room": "前台",
            "deviceName": "射灯1",
            "state": 0,
            "updateTime": "2020-01-06 11:15:37"
        },
        {
            "floor": "F1",
            "room": "前台",
            "deviceName": "背景灯",
            "state": 0,
            "updateTime": "2020-01-06 11:15:37"
        },
        {
            "floor": "F1",
            "room": "前台",
            "deviceName": "灯带",
            "state": 0,
            "updateTime": "2020-01-06 11:15:37"
        }
    ]
}
</pre>
## 获取环境参数 ##
### 路径 ###
<pre>/ils/env </pre>
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
        "deviceCode": "1F_ZM_002",
        "floor": 1,
        "room": "展厅",
        "runningState": 1,
        "communicateState": 1,
        "runningTime": 0,
        "faultTime": null,
        "technicalState": null,
        "evaluationGrade": null,
        "uptime": "2020-01-02 11:28:30",
        "temperature": 9,
        "humidity": 23,
        "pm25": null,
        "updateTime": "2020-01-02 11:43:29"
    }
}
</pre>
#### 结果数据data说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  deviceCode | 设备编号 |
|  floor | 楼层 |
|  room | 房间 |
|  runningState | 运行状态 1:正常 0: 故障|
|  communicateState | 通信状态 1:在线 0: 离线|
|  runningTime | 运行时间 单位:小时|
|  faultTime | 故障次数 |
|  technicalState | 技术状态 |
|  evaluationGrade | 评价等级 |
|  uptime | 上线时间 |
|  temperature | 温度( ℃ )|
|  humidity | 湿度(%) |
|  pm25 | PM2.5 |


## 获取温湿度历史曲线 ##
### 路径 ###
<pre>/ils/queryTh </pre>
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
    "data": [
        {
            "id": 4437,
            "deviceCode": "1F_ZM_002",
            "temperature": 8,
            "humidity": 22,
            "createTime": "2020-01-02 00:00:00"
        },
        {
            "id": 4442,
            "deviceCode": "1F_ZM_002",
            "temperature": 8,
            "humidity": 22,
            "createTime": "2020-01-02 00:05:00"
        },
        {
            "id": 4447,
            "deviceCode": "1F_ZM_002",
            "temperature": 8,
            "humidity": 22,
            "createTime": "2020-01-02 00:10:13"
        }]
}
</pre>
#### 结果数据data说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  deviceCode | 设备编号 |
|  temperature | 温度( ℃ )|
|  humidity | 湿度(%) |
|  createTime | 创建时间 |

## 获取PM2.5历史曲线 ##
### 路径 ###
<pre>/ils/queryPm </pre>
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
    "data": [
    {
       "deviceCode":1F_ZM_001,
        "pm25": 10,
        "createTime": "2019-12-19 14:10:00"
    },
    {
           "deviceCode":1F_ZM_001,
            "pm25": 10,
            "createTime": "2019-12-19 14:10:00"
        }
    ]
}
</pre>
#### 结果数据data说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  deviceCode | 设备编号 |
|  pm25 | PM2.5 |
|  createTime | 创建时间 |