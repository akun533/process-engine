<template>
  <div class="process-designer">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <a-button @click="undo" :disabled="!canUndo">
        <template #icon>
          <UndoOutlined />
        </template>
        撤销
      </a-button>
      <a-button @click="redo" :disabled="!canRedo">
        <template #icon>
          <RedoOutlined />
        </template>
        重做
      </a-button>
      <a-button @click="clearCanvas">
        <template #icon>
          <DeleteOutlined />
        </template>
        清空
      </a-button>
      <a-button type="primary" @click="save">
        <template #icon>
          <SaveOutlined />
        </template>
        保存
      </a-button>
      <a-button @click="preview">
        <template #icon>
          <EyeOutlined />
        </template>
        预览
      </a-button>
    </div>

    <div class="designer-content">
      <!-- 左侧组件面板 -->
      <div class="components-panel">
        <div class="panel-title">组件库</div>
        <div class="component-list">
          <div
            class="component-item"
            @mousedown="startDrag($event, 'start')"
          >
            <div class="component-icon start-node"></div>
            <div class="component-label">开始节点</div>
          </div>
          <div
            class="component-item"
            @mousedown="startDrag($event, 'end')"
          >
            <div class="component-icon end-node"></div>
            <div class="component-label">结束节点</div>
          </div>
          <div
            class="component-item"
            @mousedown="startDrag($event, 'task')"
          >
            <div class="component-icon rect-node"></div>
            <div class="component-label">任务节点</div>
          </div>
          <div
            class="component-item"
            @mousedown="startDrag($event, 'decision')"
          >
            <div class="component-icon diamond-node"></div>
            <div class="component-label">决策节点</div>
          </div>
          <div
            class="component-item"
            @mousedown="startDrag($event, 'data')"
          >
            <div class="component-icon parallelogram-node"></div>
            <div class="component-label">数据节点</div>
          </div>
          <div
            class="component-item"
            @mousedown="startDrag($event, 'document')"
          >
            <div class="component-icon document-node"></div>
            <div class="component-label">文档节点</div>
          </div>
          <div
            class="component-item"
            @mousedown="startDrag($event, 'storage')"
          >
            <div class="component-icon storage-node"></div>
            <div class="component-label">存储节点</div>
          </div>
          <div
            class="component-item"
            @mousedown="startDrag($event, 'manual')"
          >
            <div class="component-icon manual-node"></div>
            <div class="component-label">手动操作节点</div>
          </div>
        </div>
      </div>

      <!-- 中间画布区域 -->
      <div class="canvas-area">
        <div ref="containerRef" class="logicflow-container"></div>
      </div>

      <!-- 右侧属性配置面板 -->
      <div class="properties-panel">
        <div class="panel-title">属性配置</div>
        <div class="properties-content">
          <a-form
            :model="nodeProperties"
            layout="vertical"
            v-if="selectedNode"
          >
            <a-form-item label="节点ID">
              <a-input v-model:value="nodeProperties.id" disabled />
            </a-form-item>
            <a-form-item label="节点类型">
              <a-input v-model:value="nodeProperties.type" disabled />
            </a-form-item>
            <a-form-item label="节点文本">
              <a-input v-model:value="nodeProperties.text" />
            </a-form-item>
            <a-form-item label="节点边框颜色" v-if="!['start', 'end'].includes(nodeProperties.type)">
              <a-input v-model:value="nodeProperties.stroke" type="color" />
            </a-form-item>
            <a-form-item label="节点填充颜色" v-if="!['start', 'end'].includes(nodeProperties.type)">
              <a-input v-model:value="nodeProperties.fill" type="color" />
            </a-form-item>
            <a-button type="primary" @click="updateNodeProperties">更新属性</a-button>
          </a-form>
          <div v-else class="no-selection">
            请选择一个节点进行配置
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { message } from 'ant-design-vue';
import {
  UndoOutlined,
  RedoOutlined,
  DeleteOutlined,
  SaveOutlined,
  EyeOutlined
} from '@ant-design/icons-vue';
import LogicFlow from '@logicflow/core';
import '@logicflow/core/dist/index.css';
import { useRoute } from 'vue-router';
import { registerNodes } from '@/shared/components/nodes';

const route = useRoute();
const containerRef = ref<HTMLDivElement | null>(null);
const lfRef = ref<LogicFlow | null>(null);
const canUndo = ref(false);
const canRedo = ref(false);
const selectedNode = ref<any>(null);
const nodeProperties = ref({
  id: '',
  type: '',
  text: '',
  stroke: '#1890ff',
  fill: '#ffffff'
});

let eventListener: any = null;
const data = ref({
  // 节点
  nodes: [
    {
      id: 'start-node',
      type: 'start',
      x: 100,
      y: 150,
      text: '开始',
    },
    {
      id: 'task-node',
      type: 'task',
      x: 250,
      y: 150,
      text: '任务',
    },
    {
      id: 'decision-node',
      type: 'decision',
      x: 400,
      y: 150,
      text: '决策',
    },
    {
      id: 'end-node',
      type: 'end',
      x: 550,
      y: 150,
      text: '结束',
    },
  ],
  // 边
  edges: [
    {
      type: 'polyline',
      sourceNodeId: 'start-node',
      targetNodeId: 'task-node',
    },
    {
      type: 'polyline',
      sourceNodeId: 'task-node',
      targetNodeId: 'decision-node',
    },
    {
      type: 'polyline',
      sourceNodeId: 'decision-node',
      targetNodeId: 'end-node',
    },
  ],
});
// 初始化LogicFlow
const initLogicFlow = () => {
  if (containerRef.value) {
    const lf = new LogicFlow({
      container: containerRef.value,
      allowDrag: true,
      enableSelection: true,
      enableZoom: true,
      enableMove: true,
      grid: {
        size: 10,
        visible: true,
      },
      background: {
        color: '#F0F0F0',
      },
    });

    // 注册自定义节点
    registerNodes(lf);
    
    lf.render(data.value);
    // 监听节点选择事件
    eventListener = lf.on('element:click', ({ data }: any) => {
      selectedNode.value = data;
      nodeProperties.value = {
        id: data.id,
        type: data.type,
        text: typeof data.text === 'string' ? data.text : (data.text?.value || ''),
        stroke: data.properties?.stroke || getDefaultPropertiesByNodeType(data.type).stroke,
        fill: data.properties?.fill || getDefaultPropertiesByNodeType(data.type).fill
      };
    });

    lfRef.value = lf;
    canUndo.value = lf.undoAble;
    canRedo.value = lf.redoAble;
  }
};

// 获取节点类型的默认属性
const getDefaultPropertiesByNodeType = (nodeType: string) => {
  switch (nodeType) {
    case 'start':
      return { stroke: '#52c41a', fill: '#f6ffed' };
    case 'end':
      return { stroke: '#ff4d4f', fill: '#fff1f0' };
    case 'task':
      return { stroke: '#1890ff', fill: '#e6f7ff' };
    case 'decision':
      return { stroke: '#ff7875', fill: '#fff0f6' };
    case 'data':
      return { stroke: '#9254de', fill: '#f9f0ff' };
    case 'document':
      return { stroke: '#52c41a', fill: '#f6ffed' };
    case 'storage':
      return { stroke: '#fa8c16', fill: '#fff7e6' };
    case 'manual':
      return { stroke: '#ffa940', fill: '#fff2e8' };
    default:
      return { stroke: '#1890ff', fill: '#ffffff' };
  }
};

// 开始拖拽节点
const startDrag = (event: MouseEvent, nodeType: string) => {
  event.preventDefault(); // 阻止默认行为
  if (lfRef.value) {
    let nodeText: string;
    
    switch (nodeType) {
      case 'start':
        nodeText = '开始';
        break;
      case 'end':
        nodeText = '结束';
        break;
      case 'task':
        nodeText = '任务';
        break;
      case 'decision':
        nodeText = '决策';
        break;
      case 'data':
        nodeText = '数据';
        break;
      case 'document':
        nodeText = '文档';
        break;
      case 'storage':
        nodeText = '存储';
        break;
      case 'manual':
        nodeText = '手动操作';
        break;
      default:
        nodeText = '节点';
    }
    
    lfRef.value.dnd.startDrag({
      type: nodeType,
      text: nodeText,
      properties: getDefaultPropertiesByNodeType(nodeType)
    });
  }
};

// 撤销操作
const undo = () => {
  lfRef.value?.undo();
  canUndo.value = lfRef.value?.undoAble || false;
  canRedo.value = lfRef.value?.redoAble || false;
};

// 重做操作
const redo = () => {
  lfRef.value?.redo();
  canUndo.value = lfRef.value?.undoAble || false;
  canRedo.value = lfRef.value?.redoAble || false;
};

// 清空画布
const clearCanvas = () => {
  lfRef.value?.clearData();
  selectedNode.value = null;
};

// 保存流程
const save = () => {
  const graphData = lfRef.value?.getGraphData();
  console.log('保存流程数据:', graphData);
  message.success('流程已保存');
};

// 预览流程
const preview = () => {
  message.info('预览功能');
};

// 更新节点属性
const updateNodeProperties = () => {
  if (selectedNode.value && lfRef.value) {
    // 只对非开始和结束节点更新颜色
    if (!['start', 'end'].includes(nodeProperties.value.type)) {
      lfRef.value.setProperties(selectedNode.value.id, {
        stroke: nodeProperties.value.stroke,
        fill: nodeProperties.value.fill
      });
    } else {
      // 对于开始和结束节点，只更新颜色的stroke（边框）
      lfRef.value.setProperties(selectedNode.value.id, {
        stroke: nodeProperties.value.stroke
      });
    }
    
    // 更新节点文本
    lfRef.value.updateText(selectedNode.value.id, nodeProperties.value.text);
    
    message.success('属性已更新');
  }
};

onMounted(() => {
  initLogicFlow();

  // 获取URL中的流程ID
  const processId = route.query.id;
  console.log('正在设计的流程ID:', processId);
});

onBeforeUnmount(() => {
  if (eventListener) {
    eventListener.remove();
  }
});
</script>

<style scoped>
.process-designer {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.toolbar {
  padding: 10px 20px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
}

.designer-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.components-panel {
  width: 200px;
  border-right: 1px solid #e8e8e8;
  background: #fafafa;
  display: flex;
  flex-direction: column;
}

.panel-title {
  padding: 12px 16px;
  font-weight: bold;
  border-bottom: 1px solid #e8e8e8;
}

.component-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.component-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  margin-bottom: 10px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  cursor: grab;
  transition: all 0.2s;
}

.component-item:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.component-icon {
  width: 40px;
  height: 40px;
  margin-bottom: 8px;
}

.start-node {
  width: 60px;
  height: 60px;
  border: 2px solid #52c41a;
  background: #f6ffed;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.end-node {
  width: 60px;
  height: 60px;
  border: 2px solid #ff4d4f;
  background: #fff1f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.rect-node {
  width: 80px;
  height: 40px;
  border: 2px solid #1890ff;
  background: #e6f7ff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.diamond-node {
  border: 2px solid #ff7875;
  background: #fff0f6;
  width: 50px;
  height: 50px;
  transform: rotate(45deg);
  margin: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.diamond-node::before {
  content: '';
  position: absolute;
  width: 50px;
  height: 50px;
  border: 2px solid #ff7875;
  background: #fff0f6;
  transform: rotate(-45deg);
}

.parallelogram-node {
  width: 80px;
  height: 40px;
  background: #f9f0ff;
  border: 2px solid #9254de;
  transform: skew(-15deg);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.document-node {
  width: 80px;
  height: 40px;
  background: #f6ffed;
  border: 2px solid #52c41a;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.document-node::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 10px;
  right: 10px;
  height: 10px;
  border: 2px solid #52c41a;
  border-top: none;
  border-radius: 0 0 4px 4px;
  background: #f6ffed;
}

.storage-node {
  width: 80px;
  height: 40px;
  background: #fff7e6;
  border: 2px solid #fa8c16;
  position: relative;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.manual-node {
  width: 80px;
  height: 40px;
  background: #fff2e8;
  border: 2px solid #ffa940;
  border-radius: 4px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.manual-node::before {
  content: '';
  position: absolute;
  top: 10px;
  left: 10px;
  right: 10px;
  height: 3px;
  background: #ffa940;
  border-radius: 2px;
}

.component-label {
  font-size: 12px;
}

.canvas-area {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.logicflow-container {
  flex: 1;
}

.properties-panel {
  width: 300px;
  border-left: 1px solid #e8e8e8;
  background: #fafafa;
  display: flex;
  flex-direction: column;
}

.properties-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.no-selection {
  text-align: center;
  color: #999;
  margin-top: 50px;
}
</style>