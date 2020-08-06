# Sohotgo_APP

#### 项目背景

一个基于微博新闻热点话题的语音助手（APP版实现）

---

#### Usage

开发环境：Android studio 4.0 

Android: API 27

下载本项目后使用Android studio编译后即可使用

---

#### 项目结构：

聊天界面适配器：

> /app/src/main/java/com/example/sohotgo_test/ItemAdapter.java

对话数据类：

> /app/src/main/java/com/example/sohotgo_test/ListData.java

主要功能实现：

> /app/src/main/java/com/example/sohotgo_test/MainActivity.java

函数及其功能：

- initView()：初始化界面

- initTTS()：初始化百度语音合成SDK

- getRandomUserID()：获取使用CBD API需要的随机uesrID

- start()：开始语音识别调用的函数

- onEvent(String name, String params, byte[] data, int offset, int length)：

  语音识别结果回调函数

- getResFromParams(String params)：解析语音识别结果

- refresh(String content,int flag)：刷新APP界面

- useAPI_withpost(String msg, final Handler handler)： 调用CBD API

- getAnswer(String str)： 解析CBD API返回数据

- readtext(String str)： 播报语音合成结果

- onPause()：暂停语音识别

- onDestroy()：注销语音识别

- initPermission()：动态申请权限

---

#### SDK:

百度语音识别SDK

百度语音合成SDK

