# 📱 ComposeWanAndroid

一个基于 **Jetpack Compose** 的学习项目，主要用于练习 **Compose UI** 和 **WanAndroid API** 的使用。  
项目功能会逐步完善，包括文章列表、搜索、收藏、登录注册等功能。

---

## 🚀 技术栈

- **Kotlin**
- **Jetpack Compose** (现代化声明式 UI 框架)
- **AndroidX Lifecycle & ViewModel**
- **Kotlin Coroutines + Flow**
- **Retrofit2 + OkHttp3** (网络请求)
- **Coil** (图片加载)
- **Material3** (UI 组件)

---

## 📡 数据来源

- [WanAndroid API](https://www.wanandroid.com/blog/show/2)

---

## 🛠 功能规划

- [x] 首页文章列表  
- [ ] 文章搜索  
- [ ] 收藏 & 取消收藏  
- [ ] 登录 / 注册  
- [ ] Banner 轮播图  
- [ ] 项目 Tab & 公众号 Tab  

---

## 📂 项目结构

ComposeWanAndroid/
├── data/ # 数据层（网络请求、数据模型）
│ ├── model/ # 数据模型，例如 Article、Banner
│ ├── remote/ # Retrofit 接口 & 网络配置
│ └── repository/ # 数据仓库，封装业务逻辑
│
├── ui/ # 界面层（Compose UI）
│ ├── home/ # 首页模块
│ ├── search/ # 搜索模块
│ ├── login/ # 登录 / 注册模块
│ ├── components/ # 可复用的 UI 组件
│ └── theme/ # 主题配置（颜色、字体、形状）
│
├── viewmodel/ # ViewModel 层，负责状态管理
│
├── common/ # 通用工具类 & 扩展函数
│
├── MainActivity.kt # 应用入口
└── App.kt # Compose 应用根节点


---

## 🏃 如何运行

1. 克隆仓库
   ```bash
   git clone https://github.com/你的用户名/ComposeWanAndroid.git


打开 Android Studio (建议最新版本)

同步 Gradle 并运行到模拟器或真机

📸 截图 (未来添加)

(这里可以放几张应用的运行截图)

📖 学习目标

熟悉 Jetpack Compose 基础语法

掌握 网络请求 & 状态管理

学习 Android 应用架构设计

通过练习 WanAndroid API 实现完整的应用功能

📜 License

本项目仅用于学习交流，不做商业用途。
API 来自 WanAndroid
，感谢鸿洋大佬提供的开放 API。
