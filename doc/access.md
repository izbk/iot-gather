## 闸机相关文档

### 获取当天闸机通过人员列表

#### 请求方式
- `GET`

#### path
> /access/real

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
            "name": "张武士", # 姓名
            "jobNo": null, # 工号
            "carNo": 12345653, # 卡号
            "personId": "3699ff8ef9ad4c869a58ae8a943ea66b", # 人员id
            "orgPathName": "综合管理部", # 所属组织
            "srcName": "一层闸机人脸_门1", # 门禁点
            "access": "入", # 出入
            "eventType": 196893, # 事件类型码
            "eventTypeName": "acs.acs.eventType.successFace", # 事件类型名称
            "extEventPictureUrl": "http://192.168.4.100:6040/pic?=d41i206e*9f0i005-a88839--c1dd24239add4i5b1*=ids1*=idp6*=tdpe*m5i19=14751c8-03z0a8s=2726d8&AccessKeyId=PX8wGtxFeHf9J7G/&Expires=1577096362&Signature=uft91vs24Thbzr136dE+tF6MbRY=", # 抓拍图片url
            "photoUrl": "http://192.168.4.100:6040/pic?=d41i206e*9f0i005-a88839--c1dd24239add4i5b1*=ids1*=idp6*=tdpe*m5i19=14751c8-03z0a8s=2726d8&AccessKeyId=PX8wGtxFeHf9J7G/&Expires=1577096362&Signature=uft91vs24Thbzr136dE+tF6MbRY=", # 大头贴url
            "happenTime": "2019-12-12 10:41:04", # 事件产生时间
            "createTime": "2019-12-23 17:19:17" # 创建时间
        }
    ]
}
```
