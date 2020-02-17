### Modbus协议Java库 modbus4j 的代码解析 ###
&nbsp;&nbsp;&nbsp;&nbsp;首先，简单介绍一下Modbus协议。这是一个通讯协议，用于工业设备之间传输信息，地位类似于互联网领域中的http协议。

&nbsp;&nbsp;&nbsp;&nbsp;简单理解，通讯协议可以分为两层：硬件层和软件层。硬件层负责传输数据，如 232/485串口协议、tcp协议等。软件层则在硬件层的基础上定义了传输数据的格式。

&nbsp;&nbsp;&nbsp;&nbsp;Modbus采用主-从结构，主机会不断地发送指令给从机，从机执行指令，并返回执行结果。

&nbsp;&nbsp;&nbsp;&nbsp;以一条Modbus报文数据举例，其传输原始数据如下 01 06 00 01 00 17 98 04，对应的解析为：<br>
01——从机地址 <br>
06——功能号 <br>
00 01——数据地址 <br>
00 17——数据 <br>
98 04——CRC校验 <br>
翻译成人话就是：主机对1号从机说“把数据 0x0017(十进制23) 写入 0x0001寄存器”<br>

### Modbus4j——Java版本modbus协议实现 ###

&nbsp;&nbsp;&nbsp;&nbsp;支持的从机transports类型有：ASCII, RTU, TCP, and UDP。

#### 核心类 ####
- 主机Master及其子类：主机的入口，数据流的起点和终点。
- 数据端口类StreamTransport：负责数据的写入和读出。
- Modbus消息类ModbusMessage及其子类：支持Modbus定义的各种方法（FunctionCode）
- 收发数据控制类MessageControl：支持 timeout、retries，默认200ms，1次。
  - 收发等待室WaitingRoom：负责同步收发逻辑。
  - 输出Request消息类：OutgoingRequestMessage 及其子类。
  - 收到Response消息类：IncomingResponseMessage 及其子类。
  - 解析类MessageParser：负责解析收到的消息。
- 协议数据类型定义：DataType
- 协议功能码定义：FunctionCode
- 协议寄存器范围：RegisterRange
#### 数据流程 ####
1. 透过ModbusFactory创建对应的Master对象。
2. 封装需要发送的指令，比如ReadHoldingRegistersRequest，这是一个读寄存器指令，指定寄存器地址和长度即可。
3. Master对象将这个ReadHoldingRegistersRequest转化为OutgoingRequestMessage对象，然后传输给MessageControl。
4. 透过驱动层，MessageControl将这个OutgoingRequestMessage写入对应的通讯硬件外设（串口、网口等），并等待返回数据IncomingResponseMessage。
5. 如果没有等到，就返回null，并提醒超时。
6. 如果等到了有效返回，则MessageControl利用MessageParser将IncomingResponseMessage转化为对应ModbusResponse返回给上层。