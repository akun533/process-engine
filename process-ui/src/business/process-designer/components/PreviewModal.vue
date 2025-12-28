<template>
  <a-modal
    v-model:open="visible"
    title="流程预览"
    width="90%"
    :footer="null"
    @cancel="handleClose"
  >
    <div class="preview-container">
      <!-- 上方流程图画布 -->
      <div class="preview-canvas">
        <div ref="previewContainerRef" class="preview-logicflow"></div>
      </div>
      
      <!-- 下方节点信息表格 -->
      <div class="preview-table">
        <a-table
          :columns="columns"
          :data-source="nodeTableData"
          :pagination="false"
          size="small"
          bordered
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag v-if="record.status === 'running'" color="processing">
                <template #icon>
                  <SyncOutlined :spin="true" />
                </template>
                运行中
              </a-tag>
              <a-tag v-else-if="record.status === 'failed'" color="error">
                <template #icon>
                  <CloseCircleOutlined />
                </template>
                失败
              </a-tag>
              <a-tag v-else-if="record.status === 'success'" color="success">
                <template #icon>
                  <CheckCircleOutlined />
                </template>
                成功
              </a-tag>
              <a-tag v-else color="default">待执行</a-tag>
            </template>
            <template v-else-if="column.key === 'actions'">
              <a-button type="link" size="small" @click="toggleNodeStatus(record)">
                切换状态
              </a-button>
            </template>
          </template>
        </a-table>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';
import { SyncOutlined, CloseCircleOutlined, CheckCircleOutlined } from '@ant-design/icons-vue';
import LogicFlow, { HtmlNode, HtmlNodeModel } from '@logicflow/core';
import '@logicflow/core/dist/index.css';
import { registerNodes } from '@/shared/components/nodes';

interface NodeData {
  id: string;
  type: string;
  text: string;
  status: 'pending' | 'running' | 'success' | 'failed';
}

const props = defineProps<{
  open: boolean;
  graphData: any;
}>();

const emit = defineEmits<{
  'update:open': [value: boolean];
}>();

const visible = ref(false);
const previewContainerRef = ref<HTMLDivElement | null>(null);
const previewLfRef = ref<LogicFlow | null>(null);
const nodeTableData = ref<NodeData[]>([]);

const columns = [
  {
    title: '节点ID',
    dataIndex: 'id',
    key: 'id',
    width: 150,
  },
  {
    title: '节点类型',
    dataIndex: 'type',
    key: 'type',
    width: 120,
  },
  {
    title: '节点文本',
    dataIndex: 'text',
    key: 'text',
    width: 150,
  },
  {
    title: '状态',
    key: 'status',
    dataIndex: 'status',
    width: 150,
  },
  {
    title: '操作',
    key: 'actions',
    width: 100,
  },
];

// 监听 open 属性
watch(() => props.open, (newVal) => {
  visible.value = newVal;
  if (newVal) {
    nextTick(() => {
      initPreviewCanvas();
    });
  }
});

// 监听 visible 变化，同步到父组件
watch(visible, (newVal) => {
  emit('update:open', newVal);
});

// 初始化预览画布
const initPreviewCanvas = () => {
  if (!previewContainerRef.value || !props.graphData) return;

  // 清理之前的实例
  if (previewLfRef.value) {
    previewLfRef.value.destroy();
  }

  const lf = new LogicFlow({
    container: previewContainerRef.value,
    grid: {
      size: 10,
      visible: true,
    },
    background: {
      color: '#F0F0F0',
    },
    keyboard: {
      enabled: false,
    },
    // 预览模式，禁用编辑功能
    isSilentMode: true,
  });

  // 注册自定义节点
  registerNodes(lf);

  // 渲染流程图
  lf.render(props.graphData);

  // 初始化节点表格数据
  nodeTableData.value = props.graphData.nodes.map((node: any) => ({
    id: node.id,
    type: node.type,
    text: typeof node.text === 'string' ? node.text : (node.text?.value || ''),
    status: 'pending',
  }));

  previewLfRef.value = lf;

  // 居中显示
  lf.fitView();
};

// 切换节点状态
const toggleNodeStatus = (record: NodeData) => {
  const statusFlow: Array<'pending' | 'running' | 'success' | 'failed'> = ['pending', 'running', 'success', 'failed'];
  const currentIndex = statusFlow.indexOf(record.status);
  const nextIndex = (currentIndex + 1) % statusFlow.length;
  record.status = statusFlow[nextIndex];

  // 更新节点显示状态
  updateNodeVisualStatus(record.id, record.status);
};

// 更新节点视觉状态
const updateNodeVisualStatus = (nodeId: string, status: string) => {
  if (!previewLfRef.value) return;

  const nodeModel = previewLfRef.value.getNodeModelById(nodeId);
  if (!nodeModel) return;

  // 通过设置节点属性来存储状态
  previewLfRef.value.setProperties(nodeId, {
    ...nodeModel.properties,
    status,
  });

  // 使用CSS类来更新节点显示
  nextTick(() => {
    const graphElement = previewLfRef.value?.graphModel.rootEl;
    if (!graphElement) return;
    
    // 查找节点元素
    const nodes = graphElement.querySelectorAll('.lf-node');
    nodes.forEach((node: any) => {
      const nodeData = node.__data__;
      if (nodeData && nodeData.id === nodeId) {
        // 移除所有状态类
        node.classList.remove('node-status-running', 'node-status-failed', 'node-status-success');
        
        // 移除旧的状态图标
        const oldIcon = node.querySelector('.node-status-icon');
        if (oldIcon) {
          oldIcon.remove();
        }
        
        // 添加新的状态类和图标
        if (status !== 'pending') {
          node.classList.add(`node-status-${status}`);
          
          // 创建状态图标
          const iconDiv = document.createElement('div');
          iconDiv.className = 'node-status-icon';
          
          if (status === 'running') {
            iconDiv.innerHTML = `
              <svg class="status-icon-svg rotating" viewBox="0 0 1024 1024" width="20" height="20">
                <circle cx="512" cy="512" r="400" fill="none" stroke="#1890ff" stroke-width="60" 
                  stroke-dasharray="1256" stroke-dashoffset="314" stroke-linecap="round"/>
              </svg>
            `;
          } else if (status === 'failed') {
            iconDiv.innerHTML = `
              <svg class="status-icon-svg" viewBox="0 0 1024 1024" width="20" height="20">
                <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z m165.4 618.2l-66-0.3L512 563.4l-99.3 118.4-66.1 0.3c-4.4 0-8-3.5-8-8 0-1.9 0.7-3.7 1.9-5.2l130.1-155L340.5 359a8.32 8.32 0 0 1-1.9-5.2c0-4.4 3.6-8 8-8l66.1 0.3L512 464.6l99.3-118.4 66-0.3c4.4 0 8 3.5 8 8 0 1.9-0.7 3.7-1.9 5.2L553.5 514l130 155c1.2 1.5 1.9 3.3 1.9 5.2 0 4.4-3.6 8-8 8z" fill="#ff4d4f"/>
              </svg>
            `;
          } else if (status === 'success') {
            iconDiv.innerHTML = `
              <svg class="status-icon-svg" viewBox="0 0 1024 1024" width="20" height="20">
                <path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z m193.5 301.7l-210.6 292a31.8 31.8 0 0 1-51.7 0L318.5 484.9c-3.8-5.3 0-12.7 6.5-12.7h46.9c10.2 0 19.9 4.9 25.9 13.3l71.2 98.8 157.2-218c6-8.3 15.6-13.3 25.9-13.3H699c6.5 0 10.3 7.4 6.5 12.7z" fill="#52c41a"/>
              </svg>
            `;
          }
          
          node.appendChild(iconDiv);
        }
      }
    });
  });
};

const handleClose = () => {
  visible.value = false;
  // 清理实例
  if (previewLfRef.value) {
    previewLfRef.value.destroy();
    previewLfRef.value = null;
  }
};
</script>

<style scoped>
.preview-container {
  display: flex;
  flex-direction: column;
  height: 75vh;
  gap: 16px;
}

.preview-canvas {
  flex: 1;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
  background: #f0f0f0;
}

.preview-logicflow {
  width: 100%;
  height: 100%;
}

.preview-table {
  height: 250px;
  overflow-y: auto;
}

:deep(.node-status-icon) {
  position: absolute;
  top: -10px;
  right: -10px;
  width: 20px;
  height: 20px;
  z-index: 100;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

:deep(.status-icon-svg) {
  display: block;
}

:deep(.rotating) {
  animation: rotating 1.5s linear infinite;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
