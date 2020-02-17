## 空调相关文档

### 设置单个设备的所有控制参数:开关机及风速、制冷模式、温度

#### 请求方式
- `POST`

#### path
> /dc/setAll

#### 请求参数
body
```json
{
	"deviceCode" : "001", #设备编码
	"windSpeedEnum" : "LL", #风速，必填，包括参数（LL：超低，L：低，M：中，H：高，HH：超高，UNDO：不操作）
	"powerOnOffEnum" : "ON", #开关机，必填，包括参数（OFF：关机，ON：开机，UNDO：不控制）
	"modeEnum" : "WIND", #模式，必填，包括参数（WIND：送风，HEAT：制热，COOL：制冷，AUTO：自动，DRY，除湿）
	"temperature" : "36.5" #温度，必填
}
```
#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "操作成功",
    "success": true,
    "data": null
}
```

### 查询适配器状态

#### 请求方式
- `GET`

#### path
> /dc/adapter_state

#### 请求参数
- 无

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "code": "0001", #编码
        "desc": "准备好" #状态
    }
}
```

### 查询室内机连接状态

#### 请求方式
- `GET`

#### path
> /dc/indoor/conn_state

#### 请求参数
- 无

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": [
        true,
        true,
        true
        ...
    ]
}
```

### 查询室内机状态

#### 请求方式
- `GET`

#### path
> /dc/indoor/state?deviceCode=001                    
#### 请求参数
- `deviceCode`：设备编码

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
				"deviceCode": "B1_KT_001",
        "windSpeed": { #风量
            "code": "50",
            "message": "超高"
        },
        "switchStr": { #开关
            "code": "F0",
            "message": "关"
        },
        "cleanDesc": { #清洗描述
            "code": "42",
            "message": "过滤网清洗标志"
        },
        "mode": { # 模式
            "code": "02",
            "message": "制冷"
        },
        "setTemperature": 12.4, #设置温度，单位：°C
        "indoorTemperature": 26, #室内温度，单位：°C
        "tpState": { #温度传感器
            "code": "0800",
            "message": "正常"
        }
    }
}
```

### 开关机及风速设定

#### 请求方式
- `GET`

#### path
> /dc/set/power_wind_speed?deviceCode=001&flag=L

#### 请求参数
- `deviceCOde`：设备编码
- `flag`：具体操作，OFF：关机，ON：开机，LL：风量ll，L：风量l、M：风速m，H：风速h，HH：风速hh，AUTO：自动

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "操作成功",
    "success": true,
    "data": null
}
```

### 模式设定

#### 请求方式
- `GET`

#### path
> /dc/set/mode?deviceCode=001&flag=WIND

#### 请求参数
- `deviceCode`：设备编码
- `flag`：具体操作，WIND：送风，HEAT：制热，COOL：制冷，AUTO：自动，DRY：除湿

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "操作成功",
    "success": true,
    "data": null
}
```

### 温度设定

#### 请求方式
- `GET`

#### path
> /dc/set/temperature?deviceCode=001&temperature=36.5

#### 请求参数
- `deviceCode`：设备编码
- `temperature`：温度

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "操作成功",
    "success": true,
    "data": null
}
```

### 根据设备编号获取空调当天采集数据

#### 请求方式
- `GET`

#### path
> /dc/acquisition/{deviceCode}

#### 请求参数
- `deviceCode`：设备编码

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": [
        {
            "id": 13, # 主键id
            "deviceCode": "3F_KT_001", # 设备编码
            "runningMode": "制热", # 运行模式
            "airVolume": "超高", # 运转风量
            "setTemperature": 30, # 设定温度
            "indoorTemperature": 25, # 室内温度
            "createTime": "2019-12-26 11:00:04" # 采集时间
        },
        {
            "id": 37,
            "deviceCode": "3F_KT_001",
            "runningMode": "制热",
            "airVolume": "超高",
            "setTemperature": 30,
            "indoorTemperature": 25,
            "createTime": "2019-12-26 11:05:02"
        },
        {
            "id": 61,
            "deviceCode": "3F_KT_001",
            "runningMode": "制热",
            "airVolume": "超高",
            "setTemperature": 30,
            "indoorTemperature": 25,
            "createTime": "2019-12-26 11:10:14"
        },
        {
            "id": 85,
            "deviceCode": "3F_KT_001",
            "runningMode": "制热",
            "airVolume": "超高",
            "setTemperature": 30,
            "indoorTemperature": 25,
            "createTime": "2019-12-26 11:15:02"
        }
    ]
}
```
### 获取所有空调状态

#### 请求方式
- `GET`

#### path
> /dc/indoor/all

#### 请求参数
- 无

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "1": [
            {
							 "deviceCode": "B1_KT_001",
                "locationDesc": "前台",
                "windSpeed": {
                    "code": "50",
                    "message": "超高"
                },
                "switchStr": {
                    "code": "F0", #F0关，F1开
                    "message": "关"
                },
                "cleanDesc": {
                    "code": "42",
                    "message": "过滤网清洗标志"
                },
                "mode": {
                    "code": "01",
                    "message": "制热"
                },
                "setTemperature": 32,
                "indoorTemperature": 13,
                "tpState": {
                    "code": "0000",
                    "message": "正常"
                }
            },
            {
								"deviceCode": "B1_KT_002",
                "locationDesc": "前台",
                "windSpeed": {
                    "code": "50",
                    "message": "超高"
                },
                "switchStr": {
                    "code": "F0",
                    "message": "关"
                },
                "cleanDesc": {
                    "code": "42",
                    "message": "过滤网清洗标志"
                },
                "mode": {
                    "code": "01",
                    "message": "制热"
                },
                "setTemperature": 30,
                "indoorTemperature": 12,
                "tpState": {
                    "code": "0000",
                    "message": "正常"
                }
            }
        ]
    }
}
```

### 按楼层获取空调状态

#### 请求方式
- `GET`

#### path
> /dc/indoor/{floor}

#### 请求参数
- `floor`：楼层，# 1，2，3

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "1": [
            {
							 "deviceCode": "B1_KT_001",
                "locationDesc": "前台",
                "windSpeed": {
                    "code": "50",
                    "message": "超高"
                },
                "switchStr": {
                    "code": "F0", #F0关，F1开
                    "message": "关"
                },
                "cleanDesc": {
                    "code": "42",
                    "message": "过滤网清洗标志"
                },
                "mode": {
                    "code": "01",
                    "message": "制热"
                },
                "setTemperature": 32,
                "indoorTemperature": 13,
                "tpState": {
                    "code": "0000",
                    "message": "正常"
                }
            },
            {
								"deviceCode": "B1_KT_002",
                "locationDesc": "前台",
                "windSpeed": {
                    "code": "50",
                    "message": "超高"
                },
                "switchStr": {
                    "code": "F0",
                    "message": "关"
                },
                "cleanDesc": {
                    "code": "42",
                    "message": "过滤网清洗标志"
                },
                "mode": {
                    "code": "01",
                    "message": "制热"
                },
                "setTemperature": 30,
                "indoorTemperature": 12,
                "tpState": {
                    "code": "0000",
                    "message": "正常"
                }
            }
        ]
    }
}
```

### 设置所有空调开关

#### 请求方式
- `GET`

#### path
> /dc/set/indoor/state/all?flag=ON

#### 请求参数
- `flag`: 开关 #ON：开 OFF：关

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "操作成功",
    "success": true,
    "data": null
}
```

### 按楼层设置空调开关

#### 请求方式
- `GET`

#### path
> /dc/set/indoor/state?flag=ON&floor=1
#### 请求参数
- `flag`: 开关 #ON：开 OFF：关
- `floor`：楼层 #1，2，3，4

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "操作成功",
    "success": true,
    "data": null
}
```
