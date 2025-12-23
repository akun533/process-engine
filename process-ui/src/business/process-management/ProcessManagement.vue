<template>
  <div class="process-management">
    <a-card style="flex: 1; display: flex; flex-direction: column;">
      <div class="search-section">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-input-search 
              v-model:value="searchForm.name" 
              placeholder="请输入流程名称" 
              enter-button 
              @search="handleSearch" />
          </a-col>
          <a-col :span="6">
            <a-input-search 
              v-model:value="searchForm.code" 
              placeholder="请输入流程编码" 
              enter-button 
              @search="handleSearch" />
          </a-col>
          <a-col :span="12" class="align-right">
            <a-button type="primary" @click="showAddModal">
              <template #icon>
                <PlusOutlined />
              </template>
              新增流程
            </a-button>
          </a-col>
        </a-row>
      </div>

      <div class="table-section" style="flex: 1; overflow: auto;">
        <a-table 
          :columns="columns" 
          :data-source="processList" 
          :pagination="pagination"
          :loading="loading"
          row-key="id"
          @change="handleTableChange"
          :scroll="{ y: 'calc(100vh - 220px)' }">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="record.status === 1 ? 'green' : 'red'">
                {{ record.status === 1 ? '启用' : '禁用' }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space>
                <a-button type="link" @click="editProcess(record)">编辑</a-button>
                <a-button type="link" @click="designProcess(record.id)">设计</a-button>
                <a-popconfirm
                  title="确定要删除这个流程吗?"
                  @confirm="deleteProcess(record.id)"
                >
                  <a-button type="link" danger>删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-card>

    <!-- 新增/编辑流程弹窗 -->
    <a-modal 
      v-model:open="modalVisible" 
      :title="editingProcess.id ? '编辑流程' : '新增流程'"
      @ok="handleOk" 
      @cancel="handleCancel"
      :confirm-loading="confirmLoading"
      width="600px">
      <a-form 
        :model="formState" 
        :label-col="{ span: 6 }" 
        :wrapper-col="{ span: 16 }"
        ref="formRef">
        <a-form-item 
          label="流程名称" 
          name="name"
          :rules="[{ required: true, message: '请输入流程名称!' }]">
          <a-input v-model:value="formState.name" />
        </a-form-item>
        
        <a-form-item 
          label="流程编码" 
          name="code"
          :rules="[{ required: true, message: '请输入流程编码!' }]">
          <a-input v-model:value="formState.code" />
        </a-form-item>
        
        <a-form-item 
          label="流程描述" 
          name="description">
          <a-textarea v-model:value="formState.description" :rows="4" />
        </a-form-item>
        
        <a-form-item 
          label="状态" 
          name="status">
          <a-switch 
            v-model:checked="formState.status" 
            checked-children="启用" 
            un-checked-children="禁用" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import type { TableProps } from 'ant-design-vue';
import { Process, ProcessSearchForm, PaginationConfig, ProcessFormState } from '@/shared/types/process';
import { getProcessList, createProcess, updateProcess, deleteProcess, searchProcess } from '@/shared/api/processApi';

// 搜索表单
const searchForm = reactive<ProcessSearchForm>({
  name: '',
  code: ''
});

// 表格列定义
const columns = [
  {
    title: '流程名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '流程编码',
    dataIndex: 'code',
    key: 'code',
  },
  {
    title: '流程描述',
    dataIndex: 'description',
    key: 'description',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
];

// 流程数据
const processList = ref<Process[]>([]);

const loading = ref(false);
const modalVisible = ref(false);
const confirmLoading = ref(false);
const editingProcess = ref<Partial<Process>>({});
const formRef = ref();

// 分页配置
const pagination = reactive<PaginationConfig>({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 表单数据
const formState = reactive<ProcessFormState>({
  name: '',
  code: '',
  description: '',
  status: true,
});

// 加载流程列表
const loadProcessList = async () => {
  loading.value = true;
  try {
    const result = await getProcessList({
      page: pagination.current,
      pageSize: pagination.pageSize,
      search: searchForm
    });
    processList.value = result.list;
    pagination.total = result.pagination.total;
  } catch (error) {
    console.error('加载流程列表失败:', error);
    message.error('加载流程列表失败');
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = async () => {
  console.log('搜索:', searchForm);
  await loadProcessList();
  message.success('搜索完成');
};

// 显示添加弹窗
const showAddModal = () => {
  editingProcess.value = {};
  Object.assign(formState, {
    name: '',
    code: '',
    description: '',
    status: true,
  });
  modalVisible.value = true;
};

// 编辑流程
const editProcess = (record: Process) => {
  editingProcess.value = { ...record };
  Object.assign(formState, {
    name: record.name,
    code: record.code,
    description: record.description,
    status: record.status === 1,
  });
  modalVisible.value = true;
};

// 设计流程
const designProcess = (id: string) => {
  // 在新标签页中打开流程设计器
  const url = `/designer?id=${id}`;
  window.open(url, '_blank');
};

// 删除流程
const deleteProcess = async (id: string) => {
  try {
    await deleteProcess(id);
    message.success('删除成功');
    await loadProcessList(); // 重新加载数据
  } catch (error) {
    console.error('删除流程失败:', error);
    message.error('删除流程失败');
  }
};

// 提交表单
const handleOk = async () => {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;

    if (editingProcess.value.id) {
      // 更新操作
      await updateProcess(editingProcess.value.id, {
        name: formState.name,
        code: formState.code,
        description: formState.description,
        status: formState.status ? 1 : 0
      });
      message.success('更新成功');
    } else {
      // 新增操作
      await createProcess({
        name: formState.name,
        code: formState.code,
        description: formState.description,
        status: formState.status ? 1 : 0
      });
      message.success('添加成功');
    }

    confirmLoading.value = false;
    modalVisible.value = false;
    await loadProcessList(); // 重新加载数据
  } catch (error) {
    console.error('保存失败:', error);
    message.error('保存失败');
    confirmLoading.value = false;
  }
};

// 取消表单
const handleCancel = () => {
  modalVisible.value = false;
};

// 表格分页变化
const handleTableChange: TableProps['onChange'] = async (pager) => {
  pagination.current = pager?.current || 1;
  pagination.pageSize = pager?.pageSize || 10;
  await loadProcessList();
};

// 组件挂载后初始化数据
onMounted(async () => {
  await loadProcessList();
});
</script>

<style scoped>
.process-management {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
}

.search-section {
  margin-bottom: 20px;
}

.align-right {
  text-align: right;
}

.table-section {
  margin-top: 20px;
}
</style>