# 用户及权限接口 #
- 接口对接人: 张斌可
## 登录 ##
### 路径 ###
<pre>/auth/login </pre>
### 请求方式 ###
* POST
### 请求参数说明 ###
| 参数名字  | 参数类型  | 参数描述  | 是否必须  |
| :------------ | :------------ | :------------ | :------------ |
|  username | string |  用户名  |   是 |
|  password | string |  密码  |   是 |

### 返回结果示例 ###
<pre>
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": {
        "token": "f4ffd05ac37c4de6a1f6a095fbf9b8d4",
        "userId": 101,
        "clientId": null,
        "lastAccessTime": "2019-12-27 09:48:04",
        "userInfo": {
            "userVo": {
                "id": 101,
                "username": "test",
                "status": 1,
                "trueName": "测试管理员"
            },
            "roles": [
                {
                    "id": 1,
                    "code": "super_admin",
                    "name": "超级管理员",
                    "editTime": "2019-12-25 11:21:25",
                    "createTime": "2019-12-25 11:21:25",
                    "version": 0,
                    "checked": null
                }
            ],
            "menuResources": [],
            "btnResources": [],
            "interResource": [],
            "dataResource": []
        }
    }
}
</pre>
#### 结果数据data说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  token | 调用数据接口需要在请求header携带此参数|
|  userId | 用户ID|
|  clientId | 暂时不用|
|  lastAccessTime | 最后一次访问时间|
|  userInfo | 用户信息 |
|  roles | 角色列表 |
|  menuResources | 菜单资源列表 |
|  btnResources | 按钮资源列表 |
|  interResource | 接口资源列表 |
|  dataResource | 数据资源列表 |

#### userInfo.userVo说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  id | 用户ID|
|  username | 用户登录名|
|  status | 用户状态 0：禁用 1：正常 -1 ：删除|
|  trueName | 用户名称|
#### roles.role说明 ####
| 参数名字 | 参数描述  |
| :------------| :------------ |
|  id | 角色ID|
|  code | admin：管理员，所有权限 employee：普通用户，只能查看不能控制 |
|  name | 角色名称|

#### 测试用户列表 ####
| 用户名 | 描述  |
| :------------| :------------ |
| test	| 管理员|
| zhangsan	| 普通用户|

#### 目前权限使用说明 ####
 1. 权限判断用户角色的code；
 2. admin代表拥有所有权限；
 3. employee代表只能查看不能控制；
 4. 没有菜单和按钮的权限控制，只是设备控制接口的权限限制。
 
## 退出登录 ##
### 路径 ###
<pre>/auth/logout </pre>
### 请求方式 ###
* POST
### **header**参数说明 ###
| 参数名字  | 参数类型  | 参数描述  | 是否必须  |
| :------------ | :------------ | :------------ | :------------ |
|  token | string |  token  |   是 |

### 返回结果示例 ###
<pre>
{
    "code": "04000000",
    "message": "success",
    "success": true,
    "data": true
}
</pre>