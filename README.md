# 一个纯Kotlin编写的工具库
## 前言

## 引入

### 1.引入JitPack仓库

> 因为这个库是在jitpack的仓库里面 所以请添加jitpack的仓库源

```kotlin
//KotlinDSL
repositories {
    //xxxxxx
    maven("https://www.jitpack.io")
}
```

> 因为考虑到kotlin项目基本都是用Grade构建的，所以就不写maven了，可以自行查询maven添加jitpack源，并且现在Grade默认支持KotlinDSL进行脚本编写，所以也不写Groovy脚本的引入了，就是加一个地址，可以自行查询，不建议java调用这个库，虽然能用，但是比较麻烦

### 2.选择性导入要用的模块
```kotlin
implementation("com.github.setruth.SetruthTools:模块名称:x.x.x(tag的版本号)")
```
## 当前工具集

- KLogr(一个日志库框架)
