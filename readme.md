# DRPC - 分布式 RPC 框架

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Netty](https://img.shields.io/badge/Netty-4.1.100-green.svg)](https://netty.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

DRPC 是一个基于 Netty 的高性能、轻量级分布式 RPC（远程过程调用）框架，支持同步、异步和单向调用模式。

## ✨ 核心特性

- 🚀 **高性能通信**：基于 Netty NIO 框架，提供高吞吐量和低延迟的网络通信
- 🔄 **多种调用模式**：支持同步调用、异步调用和单向调用（oneway）
- 🎯 **灵活的代理机制**：支持 JDK 动态代理、CGLIB 和 Javassist 代理
- � **多自定义协议**：设计了高效的二进制协议，包含魔数、消息类型、状态码等完整的协议头
- 🔌 **可扩展序列化**：支持多种序列化方式（JDK、JSON、Protostuff、Hessian、Kryo、FST）
- 🎨 **注解驱动**：通过 `@RpcService` 和 `@RpcReference` 注解简化服务发布和引用
- 🔍 **服务扫描**：自动扫描并注册带有 `@RpcService` 注解的服务实现
- 🧵 **线程池管理**：客户端和服务端分别使用独立的线程池处理请求
- 🔧 **反射优化**：支持 JDK 反射和 CGLIB FastMethod 两种反射方式
- 📊 **服务分组与版本**：支持服务分组和版本管理，实现服务的多版本共存

## 🏗️ 架构设计

### 模块结构

```
drpc/
├── drpc-annotation          # 注解模块 - 定义 @RpcService 和 @RpcReference
├── drpc-codec              # 编解码模块 - RPC 协议的编码和解码
├── drpc-common             # 公共模块 - 工具类、扫描器、线程池等
├── drpc-constants          # 常量模块 - 系统常量定义
├── drpc-consumer           # 消费者模块 - RPC 客户端实现
│   ├── drpc-consumer-common    # 消费者公共组件
│   └── drpc-consumer-native    # 原生消费者实现
├── drpc-protocol           # 协议模块 - RPC 协议定义
├── drpc-provider           # 提供者模块 - RPC 服务端实现
│   ├── drpc-provider-common    # 提供者公共组件
│   └── drpc-provider-native    # 原生提供者实现
├── drpc-proxy              # 代理模块 - 动态代理实现
│   ├── drpc-proxy-api          # 代理 API 定义
│   └── drpc-proxy-jdk          # JDK 动态代理实现
├── drpc-serialization      # 序列化模块 - 序列化接口定义
│   ├── drpc-serialization-api  # 序列化 API
│   └── drpc-serialization-jdk  # JDK 序列化实现
└── drpc-test               # 测试模块
    ├── drpc-test-api           # 测试服务接口
    ├── drpc-test-consumer      # 消费者测试
    ├── drpc-test-provider      # 提供者测试
    └── drpc-test-scanner       # 扫描器测试
```

### 协议设计

DRPC 使用自定义的二进制协议，协议头固定 32 字节：

```
+---------------------------------------------------------------+
| 魔数 2byte | 消息类型 1byte | 状态 1byte | 请求ID 8byte      |
+---------------------------------------------------------------+
| 序列化类型 16byte                    | 数据长度 4byte        |
+---------------------------------------------------------------+
```

**协议字段说明：**
- **魔数（Magic）**：0x10，用于协议识别
- **消息类型（Message Type）**：REQUEST(1)、RESPONSE(2)、HEARTBEAT(3)
- **状态（Status）**：SUCCESS(0)、FAIL(1)
- **请求ID（Request ID）**：唯一标识一次请求，用于请求响应匹配
- **序列化类型（Serialization Type）**：如 "jdk"、"json" 等，不足 16 字节用 0 填充
- **数据长度（Data Length）**：消息体的字节长度

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+

### 1. 定义服务接口

```java
public interface DemoService {
    String hello(String name);
}
```

### 2. 实现服务提供者

```java
@RpcService(
    interfaceClass = DemoService.class,
    version = "1.0.0",
    group = "default"
)
public class DemoServiceImpl implements DemoService {
    @Override
    public String hello(String name) {
        return "Hello " + name;
    }
}
```

### 3. 启动服务提供者

```java
public class ProviderApplication {
    public static void main(String[] args) {
        // 服务地址：host:port
        String serverAddress = "127.0.0.1:27880";
        // 要扫描的服务包路径
        String scanPackage = "com.rain.rpc.test";
        // 反射类型：jdk 或 cglib
        String reflectType = "cglib";
        
        RpcSingleServer server = new RpcSingleServer(
            serverAddress, 
            scanPackage, 
            reflectType
        );
        server.startNettyServer();
    }
}
```

### 4. 创建服务消费者

```java
public class ConsumerApplication {
    public static void main(String[] args) {
        // 创建 RPC 客户端
        RpcClient rpcClient = new RpcClient(
            "1.0.0",        // 服务版本
            "default",      // 服务分组
            "jdk",          // 序列化类型
            3000,           // 超时时间（毫秒）
            false,          // 是否异步
            false           // 是否单向调用
        );
        
        // 创建服务代理
        DemoService demoService = rpcClient.create(DemoService.class);
        
        // 调用远程服务
        String result = demoService.hello("World");
        System.out.println("Result: " + result);
        
        // 关闭客户端
        rpcClient.shutdown();
    }
}
```

## 📖 高级特性

### 异步调用

DRPC 支持异步调用模式，通过 `RpcFuture` 实现非阻塞调用：

```java
RpcClient rpcClient = new RpcClient("1.0.0", "default", "jdk", 3000, false, false);

// 创建异步代理
IAsyncObjectProxy asyncProxy = rpcClient.createAsync(DemoService.class);

// 异步调用
RpcFuture future = asyncProxy.call("hello", "World");

// 添加回调
future.addCallback(new AsyncRpcCallback() {
    @Override
    public void onSuccess(Object result) {
        System.out.println("Success: " + result);
    }
    
    @Override
    public void onException(Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
});

// 或者阻塞获取结果
Object result = future.get();
```

### 单向调用（Oneway）

单向调用不等待服务端响应，适用于日志记录等场景：

```java
RpcClient rpcClient = new RpcClient(
    "1.0.0", 
    "default", 
    "jdk", 
    3000, 
    false, 
    true  // 启用单向调用
);

DemoService service = rpcClient.create(DemoService.class);
service.hello("World");  // 发送后立即返回，不等待响应
```

### 服务分组与版本

支持同一服务的多版本和分组管理：

```java
// 提供者端
@RpcService(
    interfaceClass = DemoService.class,
    version = "2.0.0",
    group = "premium"
)
public class DemoServiceV2Impl implements DemoService {
    // ...
}

// 消费者端
RpcClient rpcClient = new RpcClient("2.0.0", "premium", "jdk", 3000, false, false);
```

### 反射类型选择

支持两种反射方式：

- **JDK 反射**：标准的 Java 反射机制
- **CGLIB FastMethod**：性能更优的字节码增强反射

```java
// 使用 CGLIB 反射
RpcSingleServer server = new RpcSingleServer(
    "127.0.0.1:27880", 
    "com.rain.rpc.test", 
    "cglib"  // 或 "jdk"
);
```

## 🔧 核心组件

### 1. 协议层（Protocol）

- **RpcProtocol**：协议封装类，包含 Header 和 Body
- **RpcHeader**：协议头，包含魔数、消息类型、请求ID等元信息
- **RpcRequest**：请求消息体，包含类名、方法名、参数等
- **RpcResponse**：响应消息体，包含结果或错误信息

### 2. 编解码层（Codec）

- **RpcEncoder**：将 RpcProtocol 对象编码为字节流
- **RpcDecoder**：将字节流解码为 RpcProtocol 对象
- 支持可插拔的序列化方式

### 3. 传输层（Transport）

- **Provider 端**：
  - `BaseServer`：服务端基础实现，配置 Netty 服务器
  - `RpcProviderHandler`：处理客户端请求，执行服务方法
  
- **Consumer 端**：
  - `RpcConsumer`：管理客户端连接，发送请求
  - `RpcConsumerHandler`：处理服务端响应，匹配请求

### 4. 代理层（Proxy）

- **JdkProxyFactory**：基于 JDK 动态代理创建服务代理
- **ObjectProxy**：实现 InvocationHandler，拦截方法调用并转换为 RPC 请求
- **RpcFuture**：异步调用的 Future 实现，支持回调机制

### 5. 服务发现（Scanner）

- **ClassScanner**：扫描指定包下的所有类
- **RpcServiceScanner**：扫描并注册 @RpcService 注解的服务
- **RpcReferenceScanner**：扫描 @RpcReference 注解的服务引用

## 📊 工作流程

### 服务调用流程

```
客户端应用
    ↓
RpcClient.create()
    ↓
JdkProxyFactory 创建代理
    ↓
ObjectProxy 拦截方法调用
    ↓
构建 RpcRequest
    ↓
RpcConsumer 发送请求
    ↓
RpcEncoder 编码
    ↓
Netty 网络传输
    ↓
RpcDecoder 解码
    ↓
RpcProviderHandler 处理请求
    ↓
反射调用实际服务方法
    ↓
构建 RpcResponse
    ↓
RpcEncoder 编码响应
    ↓
Netty 返回响应
    ↓
RpcDecoder 解码响应
    ↓
RpcConsumerHandler 匹配请求
    ↓
RpcFuture 完成
    ↓
返回结果给客户端
```

### 服务注册流程

```
RpcSingleServer 启动
    ↓
RpcServiceScanner 扫描包
    ↓
查找 @RpcService 注解的类
    ↓
创建服务实例
    ↓
构建服务键（接口#版本#分组）
    ↓
注册到 handlerMap
    ↓
启动 Netty 服务器
    ↓
等待客户端连接
```

## 🎯 设计亮点

1. **协议设计**：自定义二进制协议，固定长度的协议头便于解析，支持多种消息类型
2. **连接复用**：客户端维护连接池，复用已建立的连接，减少连接开销
3. **请求匹配**：通过请求ID实现请求响应的精确匹配，支持并发调用
4. **异步支持**：基于 Future 和 Callback 实现真正的异步调用
5. **线程模型**：服务端使用线程池处理业务逻辑，避免阻塞 I/O 线程
6. **扩展性**：通过接口和 SPI 机制，支持序列化、代理、反射等组件的扩展

## 🛠️ 技术栈

- **网络框架**：Netty 4.1.100
- **序列化**：JDK、Jackson、FastJSON2、Protostuff、Hessian、Kryo、FST
- **代理**：JDK Dynamic Proxy、CGLIB、Javassist
- **日志**：SLF4J + Logback
- **工具库**：Apache Commons Lang3、Apache Commons Collections4
- **构建工具**：Maven

## 📝 待实现特性

- [ ] 服务注册中心集成（ZooKeeper、Nacos、Etcd）
- [ ] 负载均衡策略（随机、轮询、一致性哈希）
- [ ] 服务限流与熔断
- [ ] 心跳检测与连接保活
- [ ] 服务监控与统计
- [ ] Spring Boot Starter 支持
- [ ] 更多序列化方式的完整实现

## 📄 许可证

本项目采用 Apache License 2.0 许可证。

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题或建议，请通过 GitHub Issues 联系。
