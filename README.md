# Pet-Management-Platform
# 🐾 宠物领养平台

> 一个基于 Spring Boot 3 + Vue 3 的宠物领养管理系统，支持领养申请、爱心捐赠、临时寄养、后台管理等核心功能。

## 📋 项目简介

本项目旨在为流浪宠物提供一个线上领养平台，连接爱心人士与需要帮助的宠物。系统分为前台用户端（宠物浏览、领养申请、捐赠、寄养）和后台管理端（宠物管理、领养审核、用户管理、捐赠项目管理等）。

## 🛠️ 技术栈

### 后端

- **Java 17**
- **Spring Boot 3.5.x**
- **Spring Security**（身份认证与权限控制）
- **Spring Data JPA**（数据持久化）
- **MySQL 8.0**（主数据库）
- **Maven**（项目构建）

### 前端

- **Vue 3**（Composition API）
- **Vue Router 4**（路由管理）
- **Pinia**（状态管理）
- **Element Plus**（UI 组件库）
- **Axios**（HTTP 请求）
- **Vite**（构建工具）

### 开发工具

- **VS Code** / **IntelliJ IDEA**
- **Git**（版本控制）
- **Postman**（接口测试）

## 🚀 快速开始

### 前置条件

- JDK 17 或更高版本
- Node.js 18+ 与 npm
- MySQL 8.0+
- Git

### 克隆项目

bash

```
git clone https://github.com/your-username/pet-adoption-platform.git
cd pet-adoption-platform
```



### 2. 数据库配置

- 创建数据库：`pet_care_platform`（字符集 utf8mb4）
- 执行 `backend/src/main/resources/db/schema.sql` 建表
- 执行 `backend/src/main/resources/db/data.sql` 导入初始数据（可选）
- 修改 `backend/src/main/resources/application.properties` 中的数据库连接信息

### 3. 启动后端

bash

```
cd backend
./mvnw spring-boot:run   # Linux/macOS
# 或
mvnw.cmd spring-boot:run # Windows
```



后端默认运行在 `http://localhost:8080`

### 4. 启动前端

bash

```
cd frontend
npm install
npm run dev
```



前端默认运行在 `http://localhost:5173`

### 5. 访问系统

- 前台：`http://localhost:5173`
- 后台：登录管理员账号（初始：admin / admin123）后，通过导航栏进入“管理员控制台”

## 🧩 主要功能模块

### 前台用户

- 宠物列表浏览（分类筛选、关键词搜索）
- 宠物详情查看
- 提交领养申请
- 爱心捐赠（金钱 / 物资 / 志愿服务）
- 临时寄养需求发布与申请
- 用户注册 / 登录 / 登出
- 个人中心：我的领养申请、我的寄养、我的捐赠

### 后台管理员

- 仪表盘：统计数据（待处理申请、可领养宠物数、总领养数、注册用户数）
- 宠物管理：添加、编辑、删除宠物，管理宠物状态
- 领养申请管理：审核（批准/拒绝）、完成领养
- 用户管理：查看用户列表、禁用/启用用户
- 捐赠项目管理：创建、编辑、删除捐赠项目，查看捐赠记录