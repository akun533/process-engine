# 流程引擎后端服务

基于 Spring Boot + Flowable 的流程引擎后端服务，实现了完整的审批流程管理功能。

## 技术栈

- Spring Boot 2.7.18
- Flowable 6.8.0
- MyBatis Plus 3.5.3.1
- H2 Database（开发测试）
- MySQL（生产环境）
- Lombok

## 项目结构

```
precess-back/
├── src/main/java/com/process/
│   ├── ProcessEngineApplication.java      # 应用主类
│   ├── config/                            # 配置类
│   │   ├── FlowableConfig.java           # Flowable 配置
│   │   └── WebConfig.java                # Web 配置
│   ├── common/                            # 公共类
│   │   └── Result.java                   # 统一响应结果
│   ├── controller/                        # 控制器
│   │   ├── ProcessController.java        # 流程控制器
│   │   └── TestController.java           # 测试控制器
│   ├── service/                           # 服务层
│   │   └── ProcessService.java           # 流程服务
│   ├── dto/                               # 数据传输对象
│   │   ├── ProcessStartRequest.java      # 流程启动请求
│   │   └── TaskCompleteRequest.java      # 任务完成请求
│   ├── vo/                                # 视图对象
│   │   ├── ProcessDefinitionVO.java      # 流程定义VO
│   │   ├── ProcessInstanceVO.java        # 流程实例VO
│   │   └── TaskVO.java                   # 任务VO
│   └── delegate/                          # 流程委托类
│       ├── ApprovalSuccessDelegate.java  # 审批通过处理
│       └── ApprovalRejectDelegate.java   # 审批拒绝处理
├── src/main/resources/
│   ├── processes/                         # 流程定义文件
│   │   └── leave_approval.bpmn20.xml     # 请假审批流程
│   └── application.yml                    # 应用配置
└── pom.xml                                # Maven 配置
```

## 快速开始

### 1. 编译项目

```bash
mvn clean install
```

### 2. 运行项目

```bash
mvn spring-boot:run
```

或者运行主类：
```bash
java -jar target/process-engine-backend-1.0.0.jar
```

### 3. 访问服务

- 服务地址: http://localhost:8080
- H2 控制台: http://localhost:8080/h2-console
- 测试接口: http://localhost:8080/api/test/hello

## API 接口说明

### 流程管理接口

#### 1. 获取流程定义列表
```
GET /api/process/definitions
```

#### 2. 部署流程定义
```
POST /api/process/deploy
Content-Type: multipart/form-data
参数: file (BPMN文件)
```

#### 3. 启动流程实例
```
POST /api/process/start
Content-Type: application/json

请求体示例:
{
  "processDefinitionKey": "leaveProcess",
  "businessKey": "LEAVE-20231201-001",
  "processInstanceName": "张三的请假申请",
  "startUserId": "user001",
  "variables": {
    "leaveType": "annual",
    "startDate": "2023-12-01",
    "endDate": "2023-12-03",
    "days": 3,
    "reason": "家庭事务",
    "managerUserId": "manager001",
    "hrUserId": "hr001"
  }
}
```

#### 4. 获取用户待办任务
```
GET /api/process/tasks/{userId}
```

#### 5. 完成任务
```
POST /api/process/task/complete
Content-Type: application/json

请求体示例:
{
  "taskId": "12345",
  "userId": "manager001",
  "comment": "同意请假",
  "approved": true,
  "variables": {
    "approved": true
  }
}
```

#### 6. 获取流程实例详情
```
GET /api/process/instance/{processInstanceId}
```

#### 7. 获取流程图
```
GET /api/process/diagram/{processDefinitionId}
```

## 请假审批流程说明

### 流程步骤

1. **提交请假申请**: 员工提交请假申请，填写请假类型、开始日期、结束日期、天数、原因
2. **部门经理审批**: 部门经理审批请假申请
3. **判断是否需要HR审批**: 
   - 如果请假天数 ≤ 3天，直接通过
   - 如果请假天数 > 3天，需要HR审批
4. **HR审批**: HR进行最终审批
5. **流程结束**: 审批通过或拒绝

### 流程变量

- `leaveType`: 请假类型（annual-年假, sick-病假, personal-事假）
- `startDate`: 开始日期
- `endDate`: 结束日期
- `days`: 请假天数
- `reason`: 请假原因
- `approved`: 是否同意（true/false）
- `managerUserId`: 部门经理用户ID
- `hrUserId`: HR用户ID

## 使用示例

### 完整的请假审批流程示例

```bash
# 1. 启动流程实例
curl -X POST http://localhost:8080/api/process/start \
  -H "Content-Type: application/json" \
  -d '{
    "processDefinitionKey": "leaveProcess",
    "businessKey": "LEAVE-001",
    "processInstanceName": "张三的年假申请",
    "startUserId": "zhangsan",
    "variables": {
      "leaveType": "annual",
      "startDate": "2023-12-10",
      "endDate": "2023-12-15",
      "days": 5,
      "reason": "旅游度假",
      "managerUserId": "manager001",
      "hrUserId": "hr001"
    }
  }'

# 2. 查询经理的待办任务
curl http://localhost:8080/api/process/tasks/manager001

# 3. 经理完成审批（同意）
curl -X POST http://localhost:8080/api/process/task/complete \
  -H "Content-Type: application/json" \
  -d '{
    "taskId": "任务ID",
    "userId": "manager001",
    "comment": "同意请假申请",
    "approved": true,
    "variables": {
      "approved": true
    }
  }'

# 4. 查询HR的待办任务（因为请假天数>3）
curl http://localhost:8080/api/process/tasks/hr001

# 5. HR完成审批（同意）
curl -X POST http://localhost:8080/api/process/task/complete \
  -H "Content-Type: application/json" \
  -d '{
    "taskId": "任务ID",
    "userId": "hr001",
    "comment": "HR审批通过",
    "approved": true,
    "variables": {
      "approved": true
    }
  }'
```

## 数据库配置

### 使用 H2 内存数据库（默认配置，用于开发测试）

无需额外配置，项目启动时自动创建数据库。

### 使用 MySQL（生产环境）

1. 创建数据库：
```sql
CREATE DATABASE flowable CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `application.yml` 配置：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/flowable?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: your_password
```

## 注意事项

1. 首次运行时，Flowable 会自动创建所需的数据库表
2. 流程定义文件放在 `src/main/resources/processes/` 目录下，应用启动时会自动部署
3. 流程图中文显示需要安装"宋体"字体
4. 生产环境建议使用 MySQL 数据库，并配置数据库连接池

## 扩展开发

### 添加新的流程

1. 在 `src/main/resources/processes/` 目录下创建 BPMN 文件
2. 如需自定义业务逻辑，在 `delegate` 包下创建委托类实现 `JavaDelegate` 接口
3. 重启应用，流程会自动部署

### 自定义审批逻辑

修改 `ApprovalSuccessDelegate` 和 `ApprovalRejectDelegate` 类，添加业务逻辑。

## 许可证

MIT License
