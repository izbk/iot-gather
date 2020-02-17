## 基础信息相关文档

### 获取所有基础信息

#### 请求方式
- `GET`

#### path
> /base_info/all

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
            "id": 1, # 主键id
            "deviceCode": "1F_CO2_001", # 设备编码
            "deviceName": "二氧化碳传感器",# 设备名称
            "deviceModel": "QPA2000", # 设备型号
            "operatingVoltage": "DC15V-35V", # 工作电压
            "operatingCurrent": "",# 工作电流
            "powerDissipation": "",# 功耗
            "signalOutput": "DC0V-10V",# 信号输出
            "appearanceSize": "90mm*100mm*36mm", # 外观尺寸
            "manufacturer": "西门子（中国）有限公司",# 生产商,
            "maContactPersonnel": "", # 生产商联系人
            "maContactPhone": "", #生产商联系方式
            "suContactPersonnel": "李涵之",# 供货商联系人
            "suContactPhone": "135-2201-5712",# 供货商联系方式
            "supplier": "北京安电科技有限公司",# 供货商
            "technicalParam": "0-2000 ppm",# 技术参数
            "deviceMaterial": "ASA+PC",# 设备材质
            "installLocation": "1F_CO2_001",# 安装位置
            "pictureUrl": "",# 图片地址
            "installMethod": "86底盒", # 安装方式
            "assetNum": "",# 资产编号
            "floor": 1, # 楼层：-1，1，2，3
            "deviceType": 2, # 设备类型
            "systemType": 1,# 系统类型
            "room": "" # 房间
        },
        {
            "id": 2,
            "deviceCode": "1F_CO2_002",
            "deviceName": "二氧化碳传感器",
            "deviceModel": "QPA2000",
            "operatingVoltage": "DC15V-35V",
            "operatingCurrent": "",
            "powerDissipation": "",
            "signalOutput": "DC0V-10V",
            "appearanceSize": "90mm*100mm*36mm",
            "manufacturer": "西门子（中国）有限公司",
            "contactPersonnel": "李涵之",
            "contactPhone": "135-2201-5712",
            "supplier": "北京安电科技有限公司",
            "technicalParam": "0-2000 ppm",
            "deviceMaterial": "ASA+PC",
            "installLocation": "1F_CO2_002",
            "pictureUrl": "",
            "installMethod": "86底盒",
            "assetNum": "",
            "floor": 1,
            "deviceType": 2,
            "systemType": 1,
            "room": ""
        }
    ]
}
```
### 按设备编号获取基础信息

#### 请求方式
- `GET`

#### path
> /base_info/get/{deviceCode}

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
      "id": 1, # 主键id
      "deviceCode": "1F_CO2_001", # 设备编码
      "deviceName": "二氧化碳传感器",# 设备名称
      "deviceModel": "QPA2000", # 设备型号
      "operatingVoltage": "DC15V-35V", # 工作电压
      "operatingCurrent": "",# 工作电流
      "powerDissipation": "",# 功耗
      "signalOutput": "DC0V-10V",# 信号输出
      "appearanceSize": "90mm*100mm*36mm", # 外观尺寸
      "manufacturer": "西门子（中国）有限公司",# 生产商,
      "maContactPersonnel": "", # 生产商联系人
      "maContactPhone": "", #生产商联系方式
      "suContactPersonnel": "李涵之",# 供货商联系人
      "suContactPhone": "135-2201-5712",# 供货商联系方式
      "supplier": "北京安电科技有限公司",# 供货商
      "technicalParam": "0-2000 ppm",# 技术参数
      "deviceMaterial": "ASA+PC",# 设备材质
      "installLocation": "1F_CO2_001",# 安装位置
      "pictureUrl": "",# 图片地址
      "installMethod": "86底盒", # 安装方式
      "assetNum": "",# 资产编号
      "floor": 1, # 楼层：-1，1，2，3
      "deviceType": 2, # 设备类型
      "systemType": 1,# 系统类型
      "room": "" # 房间
    }
}
```
