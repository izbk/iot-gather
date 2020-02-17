## 监控相关文档

### 根据设备编码获取监控基础信息

#### 请求方式
- `GET`

#### path
> /camera/get/{deviceCode}

#### 请求参数
- `deviceCode`: 设备编码

#### 返回结果示例
正常响应
```json
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "id": 11, # 主键id
        "deviceCode": "3F_WLBQ_002", # 设备编码
        "cameraIndexCode": "1cb8e61731c94bd683b7926d96261566",# 监控点唯一标识
        "cameraName": "半球3F-3", # 监控点名称
        "cameraTypeName": "枪机", # 监控点类型说明
        "channelTypeName": "数字通道", # 通道类型说明
        "cameraUrl": "rtsp://192.168.4.100:554/openUrl/C53d6QE" # 监控点视频地址
    }
}
```

### 获取所有监控基础信息

#### 请求方式
- `GET`

#### path
> /camera/all

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
        {
            "id": 1,
            "deviceCode": "1F_QJ_001",
            "cameraIndexCode": "2b1ca1942d6b4e2b84191ee44cf8f564",
            "cameraName": "户外双目智能球机",
            "cameraTypeName": "枪机",
            "channelTypeName": "数字通道",
        },
        {
            "id": 2,
            "deviceCode": "1F_KL_001",
            "cameraIndexCode": "2f5652c0d7c0453bb3b61ae40ed2d67a",
            "cameraName": "客流统计-南",
            "cameraTypeName": "枪机",
            "channelTypeName": "数字通道",
        }
    ]
}
```
