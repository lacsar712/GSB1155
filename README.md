# 蚁心安巡 - 学校安全检查管理平台

一个以"督导学校安全责任落实"为核心的智能管理平台，通过数字化手段确保安全检查工作的按时、准确执行与上报。

## 🛠 技术栈

- **Frontend**: Vue 3 + Vite + TypeScript + Element Plus
- **Mini Program**: 原生微信小程序（WXML + WXSS + JavaScript）
- **Backend**: Java 17 + Spring Boot 3.2 + Spring Security + JWT
- **Database**: MySQL 8.0
- **Container**: Docker + Docker Compose

## 🚀 启动指南 (How to Run)

### 前置要求
- Docker Desktop 已安装并启动
- 端口 3000、8000、3306 未被占用

### 一键启动
```bash
# 在项目根目录执行
docker compose up --build
```

等待所有容器启动完成（首次启动可能需要5-10分钟下载依赖）。

### 停止服务
```bash
docker compose down
```

### 清理数据（重置数据库）
```bash
docker compose down -v
```

## 🔗 服务地址 (Services)

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:3000 |
| 后端 API | http://localhost:8000 |
| Swagger 文档 | http://localhost:8000/swagger-ui.html |
| 数据库 | localhost:3306 |

## 🧪 测试账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 系统管理员 | admin | 123456 | 全局权限，可管理所有功能 |
| 单位管理员 | school1_admin | 123456 | 第一中学管理员 |
| 单位管理员 | school2_admin | 123456 | 第二中学管理员 |
| 巡检员 | inspector1 | 123456 | 第一中学安全部巡检员 |
| 巡检员 | inspector2 | 123456 | 第一中学后勤部巡检员 |
| 巡检员 | inspector3 | 123456 | 第二中学安全部巡检员 |

以上初始口令均可直接用于登录。登录与创建用户时，前端和小程序会先对口令做 `SHA-256` 摘要，再提交给后端；后端保存的是该摘要的 `BCrypt` 值，因此数据库中不会存放明文密码。

## 📋 功能模块

### 已实现功能（MVP阶段）

#### 1. 用户与权限体系
- ✅ 三级用户角色：系统管理员、单位管理员、巡检员
- ✅ 组织架构树：教育局→学校→部门
- ✅ 基于角色的功能权限控制
- ✅ JWT Token 认证

#### 2. 任务管理后台
- ✅ 检查模板管理：创建标准检查模板
- ✅ 任务派发功能：手动创建任务，指定执行单位、执行人、截止时间
- ✅ 任务列表查看：按状态筛选
- ✅ 任务状态流转：待执行→执行中→待审核→已完成

#### 3. 数据统计面板
- ✅ 工作台统计数据展示
- ✅ 待办事项提醒
- ✅ 基于角色的数据范围控制

#### 4. 移动端上报小程序
- ✅ 原生微信小程序登录
- ✅ 巡检员任务列表查看与状态筛选
- ✅ 移动端开始执行任务
- ✅ 移动端提交巡检上报

### 系统架构

```
├── frontend/          # Vue3 前端项目
│   ├── src/
│   │   ├── api/       # API 接口封装
│   │   ├── layouts/   # 布局组件
│   │   ├── router/    # 路由配置
│   │   ├── stores/    # Pinia 状态管理
│   │   ├── styles/    # 全局样式
│   │   ├── types/     # TypeScript 类型定义
│   │   └── views/     # 页面组件
│   ├── Dockerfile
│   └── nginx.conf
│
├── backend/           # Spring Boot 后端项目
│   ├── src/main/java/com/yixin/patrol/
│   │   ├── config/    # 配置类
│   │   ├── controller/# 控制器
│   │   ├── dto/       # 数据传输对象
│   │   ├── entity/    # 实体类
│   │   ├── exception/ # 异常处理
│   │   ├── repository/# 数据访问层
│   │   ├── security/  # 安全相关
│   │   └── service/   # 业务逻辑层
│   ├── Dockerfile
│   └── pom.xml
│
├── miniProgram/       # 原生微信小程序端
│   ├── pages/         # 登录、任务、上报页面
│   ├── utils/         # 请求与密码摘要工具
│   └── config/        # 小程序接口地址配置
│
├── mysql/             # 数据库初始化
│   └── init/
│       ├── 01-schema.sql  # 表结构
│       └── 02-data.sql    # 初始数据
│
└── docker-compose.yml # Docker 编排配置
```

## 🔧 API 接口

### 认证相关
- `POST /api/auth/login` - 用户登录（请求体使用 `passwordHash`）
- `GET /api/auth/me` - 获取当前用户信息
- `POST /api/auth/logout` - 退出登录

### 任务管理
- `GET /api/tasks` - 获取任务列表（分页）
- `GET /api/tasks/{id}` - 获取任务详情
- `POST /api/tasks` - 创建任务
- `PUT /api/tasks/{id}` - 更新任务
- `PATCH /api/tasks/{id}/status` - 更新任务状态
- `DELETE /api/tasks/{id}` - 删除任务

### 模板管理
- `GET /api/templates` - 获取模板列表
- `POST /api/templates` - 创建模板
- `PUT /api/templates/{id}` - 更新模板
- `DELETE /api/templates/{id}` - 删除模板

### 组织机构
- `GET /api/organizations` - 获取组织列表
- `GET /api/organizations/tree` - 获取组织树
- `GET /api/organizations/schools` - 获取学校列表

### 用户管理
- `GET /api/users` - 获取用户列表
- `GET /api/users/inspectors` - 获取巡检员列表

### 数据统计
- `GET /api/dashboard/stats` - 获取统计数据

## 📝 开发说明

### 本地开发

如需本地开发调试，可以单独启动数据库：

```bash
docker compose up db -d
```

### 微信小程序开发

1. 使用微信开发者工具导入 `miniProgram/` 目录。
2. 项目已在 `miniProgram/project.config.json` 和 `miniProgram/project.private.config.json` 中默认关闭开发环境 `urlCheck`，导入后即可请求本地 `http://127.0.0.1:8000`。
3. 小程序会按环境自动切换接口地址：
   `develop` 使用 `miniProgram/config/index.js` 中的本地地址 `http://127.0.0.1:8000/api`
   `trial` 和 `release` 使用同文件中的生产域名配置
4. 发布体验版或正式版前，请把 `miniProgram/config/index.js` 中的 `trial`、`release` 地址改成微信后台已配置的 HTTPS 合法域名。
5. 如果开发者工具已经打开过该项目，请重新编译一次；若仍然提示域名校验，可在开发者工具本地设置中再次确认“不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书”。
6. 如需临时覆盖地址，可在小程序存储中写入 `baseUrlOverride`。
7. 先启动后端服务，再在小程序中使用巡检员账号登录。

### 小程序上报流程

1. 登录后进入“我的巡检任务”页面。
2. 对 `待执行` 任务点击“开始执行”。
3. 对 `执行中` 或 `已驳回` 任务点击“提交上报”。
4. 填写巡检位置、上报内容、检查结果摘要后提交。

### 数据库连接信息

- Host: localhost
- Port: 3306
- Database: yixin_patrol
- Username: root
- Password: root123

## 🐳 Docker 镜像说明

| 服务 | 镜像 | 说明 |
|------|------|------|
| MySQL | mysql:8.0 | 数据库 |
| Backend | maven:3.9-eclipse-temurin-17 (构建) | Spring Boot 应用 |
| Backend | eclipse-temurin:17-jre (运行) | Spring Boot 应用 |
| Frontend | node:20-alpine (构建) | Vue3 应用构建 |
| Frontend | nginx:alpine (运行) | 静态资源服务 |

## 📌 注意事项

1. 首次启动时，MySQL 需要初始化数据，后端会等待数据库就绪后再启动
2. 登录和创建用户均不再传输明文密码，统一使用 `SHA-256` 摘要入参，服务端采用 `BCrypt` 存储
3. Web 前端通过 Nginx 反向代理转发 API 请求到后端
4. 小程序访问后端时，`BASE_URL` 必须配置为手机或开发者工具可访问的地址
5. 数据持久化在 Docker Volume 中，删除 Volume 会清空数据
