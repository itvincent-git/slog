# SLog
An Android log tools, with logcat and file log used mmap. 

## 中文
一个Android的日志工具，会输出logcat及写入文件。目标是高性能、轻量化、接口化。

### 初始化
在自定义的Application.onCreate()中初始化：
```kotlin
SLoggerFactory.initialize(
    ComposorBinderBuilder()
        .addDispatcher(LogcatDispatcher())//使用logcat输出
        .addDispatcher(LogFileDispatcher(File("/sdcard/slog")))//使用文件输出
        .logLevel(LogLevel.Verbose)
        .build())
```

### 支持功能
[√] 崩溃时日志不丢失：保证崩溃时把所有日志都写完

[√] logcat日志中添加上发生的时间点：例如 21:09:03.680 V/SimpleLogActivity: verbose log

[√] 自动按当前时间生成日志文件名：例如logs_2018_12_18_02_36_43.zip

[√] 日志文件自动分文件：例如1M一个文件，写入超过上限时则会新开一个文件

[√] 可定义日志等级输出到文件：默认是info以上级别的日志才输出到文件

[√] 自动压缩日志文件：每次开新的日志文件时，会把之前的.txt日志压缩为.zip

[√] 日志文件管理接口：包括查询所有日志文件

[ ] 没有写文件权限的保护：如果设置的日志目录是在sdcard，但访问外部存储权限时，不会崩溃，当然也写不了日志

[ ] 自动清理超时7天的日志，避免日志过多

### 性能测试


