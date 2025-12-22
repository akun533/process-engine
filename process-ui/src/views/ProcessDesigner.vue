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
            draggable="true"
            @dragstart="onDragStart($event, 'start')"
          >
            <div class="component-icon start-node"></div>
            <div class="component-label">开始节点</div>
          </div>
          <div 
            class="component-item" 
            draggable="true"
            @dragstart="onDragStart($event, 'rect')"
          >
            <div class="component-icon rect-node"></div>
            <div class="component-label">矩形节点</div>
          </div>
          <div 
            class="component-item" 
            draggable="true"
            @dragstart="onDragStart($event, 'circle')"
          >
            <div class="component-icon circle-node"></div>
            <div class="component-label">圆形节点</div>
          </div>
          <div 
            class="component-item" 
            draggable="true"
            @dragstart="onDragStart($event, 'diamond')"
          >
            <div class="component-icon diamond-node"></div>
            <div class="component-label">菱形节点</div>
          </div>
          <div 
            class="component-item" 
            draggable="true"
            @dragstart="onDragStart($event, 'end')"
          >
            <div class="component-icon end-node"></div>
            <div class="component-label">结束节点</div>
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
            <a-form-item label="节点颜色" v-if="nodeProperties.type !== 'start' && nodeProperties.type !== 'end'">
              <a-input v-model:value="nodeProperties.fillColor" type="color" />
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
  fillColor: '#ffffff'
});

let eventListener: any = null;

// 初始化LogicFlow
const initLogicFlow = () => {
  if (containerRef.value) {
    const lf = new LogicFlow({
      container: containerRef.value,
      width: 'auto',
      height: '100%',
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

    // 注册节点
    lf.registerNode('start', {
      width: 100,
      height: 40,
      fillColor: '#52C41A',
      strokeColor: '#52C41A',
    });
    
    lf.registerNode('end', {
      width: 100,
      height: 40,
      fillColor: '#FF4D4F',
      strokeColor: '#FF4D4F',
    });

    // 监听节点选择事件
    eventListener = lf.on('element:click', ({ data }: any) => {
      if (data.type === 'node') {
        selectedNode.value = data;
        nodeProperties.value = {
          id: data.id,
          type: data.type,
          text: data.text?.value || data.text || '',
          fillColor: data.properties?.fillColor || '#ffffff'
        };
      }
    });

    lfRef.value = lf;
    canUndo.value = lf.undoAble;
    canRedo.value = lf.redoAble;
  }
};

// 拖拽开始事件
const onDragStart = (event: DragEvent, nodeType: string) => {
  event.dataTransfer?.setData('node-type', nodeType);
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
    lfRef.value.setProperties(selectedNode.value.id, {
      fillColor: nodeProperties.value.fillColor
    });
    
    lfRef.value.setElementText(selectedNode.value.id, nodeProperties.value.text);
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

.toolbar .ant-btn {
  margin-right: 10px;
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
  background: #52C41A;
  border-radius: 4px;
}

.end-node {
  background: #FF4D4F;
  border-radius: 4px;
}

.rect-node {
  background: #1890FF;
  border-radius: 4px;
}

.circle-node {
  background: #FA8C16;
  border-radius: 50%;
}

.diamond-node {
  background: #722ED1;
  transform: rotate(45deg);
  margin: 5px;
}

.diamond-node::before {
  content: '';
  position: absolute;
  width: 30px;
  height: 30px;
  background: #722ED1;
  transform: rotate(-45deg);
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