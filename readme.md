## 基于springboot2.2+jdk11 实现简单的1对1聊天功能
### 提供了前端界面，使用的模板引擎是 `thymeleaf`
### 使用的springbootstarter是 `spring-boot-starter-websocket`
### 使用的注解是 `@ServerEndpoint("/chat/{fromUser}")`
  `@OnOpen` `@OnMessage` `@OnClose` ` @OnError`
### 添加了群发的示例代码