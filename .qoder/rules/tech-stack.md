---
trigger: always_on
---
# 技术栈与架构约束（Tech Stack Rules）

本项目的技术栈、工程结构以及部署方式必须遵循以下约束，所有代码、配置示例与自动化流程设计均需以此为基础。

---

## 一、技术栈

### 1. 前端

- 框架：Vue 3
- 语言：TypeScript
- 构建工具 / 工程化：
  - 使用与 Vue 3 + TS 生态兼容的主流方案（如 Vite 等）
  - 所有示例配置均以 TS + Vue 3 单文件组件（SFC）为基础
- 编码规范：
  - 组件推荐使用 `<script setup lang="ts">` 或标准 TS 语法
  - 优先使用组合式 API（Composition API）
  - 类型声明必须使用 TypeScript 类型系统，避免滥用 `any`（除非确有必要且需说明）

### 2. 后端

- 语言：Java 17
- 构建工具：Maven
- 框架：Spring Boot 3.x
- 数据库与持久化：
  - 数据库：MySQL
  - ORM / 持久层：如需示例，优先使用 Spring Data JPA 或 MyBatis（需与 Spring Boot 3 兼容）
- 接口风格：
  - 后端 HTTP 接口**必须采用标准 RESTful 风格**
    - 使用 HTTP 方法语义化：`GET`（查询）、`POST`（创建）、`PUT`/`PATCH`（更新）、`DELETE`（删除）
    - 资源路径采用名词复数形式，如：`/api/users`、`/api/orders/{id}`
    - 状态码使用语义化约定（2xx 成功、4xx 客户端错误、5xx 服务端错误）
    - 请求与响应数据统一使用 JSON
    - 错误返回统一结构（如包含 `code`、`message`、`details` 等字段）
  - Spring Web 层应基于 `@RestController`、`@RequestMapping` 等注解，无视图渲染逻辑
- 后端代码相关要求：
  - 示例中可使用 Java 17 语言特性（如 `record`、增强 `switch` 等），但需确保与 Spring Boot 3 兼容
  - Maven 配置示例以 Spring Boot 3 + Java 17 + MySQL 为基础
  - 数据库相关 DDL / SQL 必须为 MySQL 方言

---

## 二、仓库与工程结构

- 前后端代码共用**一个项目目录**和**一个 GitHub 仓库**（Mono-repo）：
  - 推荐目录结构（示例）：
    - `frontend/`：Vue 3 + TypeScript 前端工程
    - `backend/`：Java 17 + Maven + Spring Boot 3 后端工程（RESTful API）
    - `charts/`：
      - `charts/frontend/`：前端应用 Helm Chart
      - `charts/backend/`：后端应用 Helm Chart（含 MySQL 部署）
    - `.github/workflows/`：GitHub Actions 工作流配置
- 所有 CI/CD 配置、脚本、文档需默认以**单仓库多工程**（Mono-repo）为前提进行设计。

---

## 三、CI/CD 与 GitHub Actions

### 1. 工作流拆分与触发规则

- 必须为前后端分别创建独立的 GitHub Actions 工作流：
  - 前端构建工作流（示例名：`frontend-ci.yml`）
    - 仅在前端相关代码或核心配置变动时触发
    - 推荐触发路径：
      - `frontend/**`
      - 前端构建相关根目录文件（如 `package.json`、`pnpm-lock.yaml`、`yarn.lock`、`vite.config.*` 等）
  - 后端构建工作流（示例名：`backend-ci.yml`）
    - 仅在后端相关代码或核心配置变动时触发
    - 推荐触发路径：
      - `backend/**`
      - 后端构建相关根目录文件（如根 `pom.xml`、`.mvn/**` 等）
- Helm Charts 发布需使用**独立的工作流**（示例名：`charts-publish.yml`）：
  - 针对 `charts/**` 路径变更触发
  - 负责将前后端 Helm Chart 打包并发布到 `gh-pages` 分支

### 2. 工作流步骤建议

- 前端 CI 工作流：
  - 使用 Node LTS 版本（精简镜像或 `actions/setup-node`）
  - 安装依赖（统一使用 npm / pnpm / yarn 中的一种）
  - 执行：
    - 代码检查（lint）
    - 单元测试 / 端到端测试（按需）
    - 生产构建（build）
  - 如在 CI 中构建镜像：
    - 使用精简 Node/NGINX 等基础镜像构建与推送前端镜像

- 后端 CI 工作流：
  - 使用 Java 17 运行环境（如 `actions/setup-java` 配置为 17）
  - 使用 Maven 执行 `clean verify` / `test` / `package`
  - 如在 CI 中构建镜像：
    - 使用包含 Maven 的 JDK 镜像进行构建
    - 使用精简 JRE 镜像作为运行时基础镜像
  - API 层测试应围绕 RESTful 接口（包括控制器测试、集成测试等）

- Helm Charts 发布工作流：
  - 对 `charts/frontend` 与 `charts/backend` 执行：
    - `helm lint`
    - `helm package`
  - 将打包生成的 `.tgz` 上传至 `gh-pages` 分支指定目录
  - 使用 `helm repo index` 生成/更新 `index.yaml`
  - `gh-pages` 分支作为 Helm 仓库（可通过 `helm repo add` 使用）

---

## 四、容器镜像规范（Kubernetes 部署）

生产部署环境为 Kubernetes（K8S），前后端应用以及 MySQL 数据库均需打包为容器镜像。

### 1. 通用要求

- 基础镜像应尽可能精简，在保证稳定性与兼容性的前提下最小化体积
- 推荐使用多阶段构建（multi-stage build）：
  - 第一阶段：构建环境（包含编译工具链）
  - 第二阶段：运行环境（仅保留运行必需文件）
- 镜像内避免硬编码环境配置（如数据库地址、凭证等），统一通过环境变量、K8S ConfigMap/Secret 注入

### 2. 前端镜像

- 构建阶段：
  - 使用精简 Node 镜像，例如：`node:<version>-alpine`
  - 在构建镜像中完成依赖安装与打包（`npm run build` 等）
- 运行阶段：
  - 使用精简 HTTP 服务器镜像，例如：`nginx:alpine`
  - 将打包后的静态资源拷贝至 `/usr/share/nginx/html` 或自定义目录
- 配置方式：
  - 优先采用构建时注入的方式（build-time env）
  - 如需运行时注入，可通过启动脚本将环境变量写入配置文件或 JS 片段

### 3. 后端镜像（RESTful API 服务）

- 构建阶段：
  - 使用带 Maven 的 JDK 镜像（如 `maven:3.x-eclipse-temurin-17`）进行构建
  - 使用 Maven 打出 Spring Boot 可执行 jar，建议开启分层（layered jars）优化镜像缓存
- 运行阶段：
  - 使用精简 JRE 镜像，例如：
    - `eclipse-temurin:17-jre-alpine`
    - 或其他官方精简、与 Spring Boot 3 兼容的 Java 17 JRE 镜像
- 配置：
  - 应用服务以 RESTful API 形式对外提供服务
  - 数据库连接信息使用环境变量或 K8S Secret / ConfigMap 注入
  - 避免将敏感信息写死在镜像中

### 4. MySQL 镜像（精简最小兼容版）

- 必须使用官方 MySQL 镜像中**体积尽可能小且与项目兼容的精简版**：
  - 示例：基于 `-oraclelinux` 或 `-debian` 的官方精简变体（具体版本由实际需求确定）
  - 不允许使用非官方或不明来源的第三方 MySQL 镜像
- 要求：
  - 所选 MySQL 版本应与应用中使用的 JDBC 驱动版本、方言配置完全兼容
  - 默认启用数据持久化（PVC）
  - 适当配置字符集、连接数、缓冲等参数以满足生产需要
  - 遵循安全最佳实践（限制访问、合理配置用户权限等）

---

## 五、Helm Charts 规范

### 1. Chart 结构

- 必须为前后端分别创建独立 Helm Chart：
  - `charts/frontend/`：前端应用 Chart
  - `charts/backend/`：后端应用 Chart（包含 MySQL 部署）
- 每个 Chart 必须提供：
  - `values.yaml` 用于配置：
    - 镜像仓库、镜像名称、tag、拉取策略
    - 副本数、资源请求/限制（CPU、内存）
    - Service 类型、端口
    - 环境变量与配置项
  - 模板中默认不创建 Ingress（见后文 Ingress 约束）

### 2. 后端 Chart（含 RESTful API 与 MySQL）

- 后端 Chart 必须包含：
  - 后端 Spring Boot 应用部署（Deployment/StatefulSet + Service）
    - Service 默认暴露 RESTful API 端口（如 8080）
    - Service 类型推荐 `ClusterIP`
  - MySQL 数据库部署（可采用以下任一方式）：
    - 自定义 MySQL Deployment/StatefulSet + Service + PVC 模板
    - 或使用子 Chart 管理 MySQL（但必须在同一仓库内可见与维护）
- MySQL 部署要求：
  - 使用官方精简版 MySQL 镜像，镜像信息在 `values.yaml` 中可配置：
    - 默认值需指向官方精简、最小兼容版镜像 tag
  - 使用 Secret 管理数据库凭证（`username`、`password`、`database` 等）
  - 后端应用通过环境变量获取数据库连接信息（主机、端口、库名、用户、密码）

### 3. Helm Charts 发布至 gh-pages

- 使用独立的 Helm Charts Actions 工作流负责：
  - 对 `charts/frontend` 与 `charts/backend` 执行 `helm lint`、`helm package`
  - 将 `.tgz` 包推送到 `gh-pages` 分支（可置于 `./` 或 `./charts/` 目录）
  - 使用 `helm repo index` 生成或更新 `index.yaml`
- `gh-pages` 分支作为 Helm 仓库，部署环境可以通过：
  - `helm repo add <name> https://<user>.github.io/<repo>` 进行引用与安装

---

## 六、Ingress 约束

- 前后端 Chart 中**都不应创建 Ingress 资源**：
  - 默认模板中不得生成 Ingress
  - 如未来需要对外暴露策略，可由平台或运维团队通过独立 Chart / 网关 / Ingress 统一管理
- 服务暴露方式建议：
  - 在集群内部使用 `ClusterIP` 类型 Service
  - 对外访问由集群层网络组件（如 Ingress Controller、API Gateway 等）统一承接

---

## 七、总体约束总结

- 技术栈固定：
  - 前端：Vue 3 + TypeScript
  - 后端：Java 17 + Maven + Spring Boot 3 + MySQL
  - 后端 HTTP 接口必须遵循 RESTful 风格
- 仓库与工程：
  - 单仓库（Mono-repo），前后端分工程
  - 前后端代码共用一个 GitHub 仓库，但构建流程（GitHub Actions）相互独立
- 部署与运维：
  - 生产环境为 Kubernetes
  - 前后端分别构建精简容器镜像
  - MySQL 使用官方“精简最小兼容版”镜像
  - 前后端具备独立 Helm Charts，后端 Chart 内必须包含 MySQL 部署
  - Helm Charts 通过独立的 Actions 工作流在 `gh-pages` 分支构建并托管
  - 前后端 Chart 均不自带 Ingress 配置，由平台侧统一管理入口


---

## 八、开发环境与调试约束

### 1. 开发环境

- 默认开发环境为 **WSL (Windows Subsystem for Linux) 下的 Ubuntu 24.04**
- 必备工具已预装并配置：
  - Java 17（用于后端开发与构建）
  - Node.js + npm（用于前端开发与构建）
  - Maven（用于后端项目构建与依赖管理）
  - Docker（用于本地容器构建与调试）
- 开发者应确保上述工具版本与生产环境技术栈保持一致（Java 17、Node LTS、Maven 3.x）

### 2. 单元测试与集成测试

#### 前端测试

- 单元测试推荐使用 Vue 3 生态兼容的测试框架（如 Vitest、Jest + Vue Test Utils 等）
- 端到端测试可使用 Playwright、Cypress 等主流工具
- **前端功能调试可配置 MCP (Model Context Protocol) 服务**：
  - MCP 服务用于辅助前端功能测试与调试
  - 具体配置方式与调用规范需在项目文档中说明
  - 测试时应确保 MCP 服务可访问且响应正常

#### 后端测试

- 单元测试使用 JUnit 5（与 Spring Boot 3 兼容）
- 集成测试推荐使用 Spring Boot Test + TestContainers（如需真实数据库环境）
- **后端 RESTful API 调试使用 `curl` 命令**：
  - 所有 HTTP 接口测试应通过 `curl` 进行手动或自动化验证
  - 测试脚本中应包含完整的 `curl` 命令示例（包括请求方法、Headers、Body 等）
  - 示例：
    # GET 请求
    curl -X GET http://localhost:8080/api/users
    
    # POST 请求
    curl -X POST http://localhost:8080/api/users \
      -H "Content-Type: application/json" \
      -d '{"name":"张三","email":"zhangsan@example.com"}'
    
    # PUT 请求
    curl -X PUT http://localhost:8080/api/users/1 \
      -H "Content-Type: application/json" \
      -d '{"name":"李四","email":"lisi@example.com"}'
    
    # DELETE 请求
    curl -X DELETE http://localhost:8080/api/users/1

### 3. 本地容器化调试

- 使用 Docker 在本地构建与运行容器镜像：
  - 前端镜像构建与调试：
    cd frontend
    docker build -t gpfwzs-frontend:dev .
    docker run -p 80:80 gpfwzs-frontend:dev
  - 后端镜像构建与调试：
    cd backend
    docker build -t gpfwzs-backend:dev .
    docker run -p 8080:8080 \
      -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/gpfwzs \
      -e SPRING_DATASOURCE_USERNAME=root \
      -e SPRING_DATASOURCE_PASSWORD=password \
      gpfwzs-backend:dev
  - MySQL 容器启动（用于本地测试）：
    docker run -d \
      --name mysql-dev \
      -p 3306:3306 \
      -e MYSQL_ROOT_PASSWORD=password \
      -e MYSQL_DATABASE=gpfwzs \
      mysql:8.0-debian

### 4. 开发环境配置建议

- 推荐使用 Docker Compose 管理本地开发环境（前端、后端、MySQL）：
  - 在项目根目录提供 `docker-compose.dev.yml` 用于一键启动本地开发环境
  - 示例配置应包含：
    - 前端服务（带热重载）
    - 后端服务（连接本地 MySQL）
    - MySQL 服务（持久化数据卷）
- IDE 推荐：
  - 前端：VSCode + Volar + TypeScript 插件
  - 后端：IntelliJ IDEA / Eclipse + Spring Tools
- 代码格式化与检查工具：
  - 前端：ESLint + Prettier
  - 后端：Checkstyle / SpotBugs（可选）

---
