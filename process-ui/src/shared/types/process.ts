// 流程类型定义
export interface Process {
  id: string;
  name: string;
  code: string;
  description: string;
  status: number; // 1: 启用, 0: 禁用
  createTime: string;
  updateTime: string;
}

// 流程搜索表单类型
export interface ProcessSearchForm {
  name: string;
  code: string;
}

// 分页配置类型
export interface PaginationConfig {
  current: number;
  pageSize: number;
  total: number;
}

// 表单状态类型
export interface ProcessFormState {
  name: string;
  code: string;
  description: string;
  status: boolean;
}