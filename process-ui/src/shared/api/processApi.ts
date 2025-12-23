import { Process, ProcessSearchForm, PaginationConfig } from '../types/process';

// 模拟流程数据
const mockProcessData: Process[] = [
  {
    id: '1',
    name: '请假审批流程',
    code: 'LEAVE-001',
    description: '员工请假审批流程',
    status: 1,
    createTime: '2023-01-01 12:00:00',
    updateTime: '2023-01-01 12:00:00'
  },
  {
    id: '2',
    name: '报销审批流程',
    code: 'EXPENSE-001',
    description: '员工差旅费用报销流程',
    status: 1,
    createTime: '2023-01-02 12:00:00',
    updateTime: '2023-01-02 12:00:00'
  }
];

// 获取流程列表
export const getProcessList = async (params: {
  page: number;
  pageSize: number;
  search?: ProcessSearchForm;
}): Promise<{ list: Process[]; pagination: PaginationConfig }> => {
  // 模拟API延迟
  await new Promise(resolve => setTimeout(resolve, 500));
  
  const { page, pageSize, search } = params;
  let filteredData = [...mockProcessData];
  
  // 根据搜索条件过滤数据
  if (search?.name) {
    filteredData = filteredData.filter(item => 
      item.name.toLowerCase().includes(search.name.toLowerCase())
    );
  }
  if (search?.code) {
    filteredData = filteredData.filter(item => 
      item.code.toLowerCase().includes(search.code.toLowerCase())
    );
  }
  
  // 分页处理
  const start = (page - 1) * pageSize;
  const end = start + pageSize;
  const paginatedData = filteredData.slice(start, end);
  
  return {
    list: paginatedData,
    pagination: {
      current: page,
      pageSize,
      total: filteredData.length
    }
  };
};

// 获取单个流程
export const getProcessById = async (id: string): Promise<Process | undefined> => {
  return mockProcessData.find(item => item.id === id);
};

// 创建流程
export const createProcess = async (processData: Omit<Process, 'id' | 'createTime' | 'updateTime'>): Promise<Process> => {
  await new Promise(resolve => setTimeout(resolve, 1000));
  
  const newProcess: Process = {
    id: `${mockProcessData.length + 1}`,
    ...processData,
    createTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
    updateTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
  };
  
  mockProcessData.push(newProcess);
  return newProcess;
};

// 更新流程
export const updateProcess = async (id: string, processData: Partial<Process>): Promise<Process> => {
  await new Promise(resolve => setTimeout(resolve, 1000));
  
  const index = mockProcessData.findIndex(item => item.id === id);
  if (index !== -1) {
    const updatedProcess = {
      ...mockProcessData[index],
      ...processData,
      updateTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    };
    mockProcessData[index] = updatedProcess;
    return updatedProcess;
  }
  
  throw new Error('Process not found');
};

// 删除流程
export const deleteProcess = async (id: string): Promise<void> => {
  await new Promise(resolve => setTimeout(resolve, 500));
  const index = mockProcessData.findIndex(item => item.id === id);
  if (index !== -1) {
    mockProcessData.splice(index, 1);
  }
};

// 搜索流程
export const searchProcess = async (searchForm: ProcessSearchForm): Promise<Process[]> => {
  await new Promise(resolve => setTimeout(resolve, 300));
  
  let filteredData = [...mockProcessData];
  
  if (searchForm.name) {
    filteredData = filteredData.filter(item => 
      item.name.toLowerCase().includes(searchForm.name.toLowerCase())
    );
  }
  if (searchForm.code) {
    filteredData = filteredData.filter(item => 
      item.code.toLowerCase().includes(searchForm.code.toLowerCase())
    );
  }
  
  return filteredData;
};