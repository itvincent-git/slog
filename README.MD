# ProtoQueue
### 说明

ProtoQueue提供以下功能：

- 接口为对proto进行发送和接收，用户不用再面向byte[]进行处理
- 提供发送协议-接收协议，一一对应关系能力，如发送AProto {uri = 100, seqId = 1}，则对应接收 AProto{uri = 101, seqId = 1}，指定回调某个uri的协议及跟发送时seqId一致的结果数据。
- 非发送协议-接收协议（如广播），则通过广播的接口通知
- 支持protobuff-nano实现
- 对seqId, Proto进行类型识别，免于重复编写这些逻辑，可支持不同的header实现，不同的seqId实现，支持ProtoPacker/ProtoUnpacker的二次封装
- 发送能力的封装，提供发送前处理能力，可便于为proto添加头部
- 可取消接收协议回调（待实现）
- 超时处理
- 支持协程方式的调用，可返回Deferred对象
- 重试能力（待实现）
- 缓存能力（待考虑）