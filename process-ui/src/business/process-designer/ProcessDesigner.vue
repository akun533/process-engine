<template>
  <div class="process-designer">
    <!-- 顶部工具栏 -->
    <Toolbar 
      :can-undo="canUndo" 
      :can-redo="canRedo"
      @undo="undo"
      @redo="redo"
      @clear="clearCanvas"
      @save="save"
      @preview="preview"
    />

    <div class="designer-content">
      <!-- 左侧组件面板 -->
      <ComponentPanel :lf="lfRef" />

      <!-- 中间画布区域 -->
      <div class="canvas-area">
        <div ref="containerRef" class="logicflow-container"></div>
      </div>

      <!-- 右侧属性配置面板 -->
      <PropertiesPanel :selected-node="selectedNode" :lf="lfRef" />
    </div>

    <!-- 预览弹框 -->
    <PreviewModal v-model:open="previewVisible" :graph-data="graphData" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { message } from 'ant-design-vue';
import LogicFlow from '@logicflow/core';
import '@logicflow/core/dist/index.css';
import { Menu } from '@logicflow/extension';
import '@logicflow/extension/lib/style/index.css';
import { useRoute } from 'vue-router';
import { registerNodes } from '@/shared/components/nodes';
import PropertiesPanel from './components/PropertiesPanel.vue';
import ComponentPanel from './components/ComponentPanel.vue';
import Toolbar from './components/Toolbar.vue';
import PreviewModal from './components/PreviewModal.vue';

const route = useRoute();
const containerRef = ref<HTMLDivElement | null>(null);
const lfRef = ref<LogicFlow | null>(null);
const canUndo = ref(false);
const canRedo = ref(false);
const selectedNode = ref<any>(null);
const previewVisible = ref(false);
const graphData = ref<any>(null);

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
      plugins: [Menu] // 启用菜单插件
    });

    // 注册自定义节点
    registerNodes(lf);
    
    // 配置菜单项
    lf.extension.menu.setMenuConfig({
      nodeMenu: [
        {
          text: '复制',
          callback: (node) => {
            // 开始和结束节点不允许复制
            if (node.type === 'start' || node.type === 'end') {
              message.warning('开始节点和结束节点不能复制');
              return;
            }
            
            // 创建新节点数据，位置稍微偏移
            const newNodeData = {
              ...node,
              id: `${node.id}_copy_${Date.now()}`, // 生成新的唯一ID
              x: node.x + 30, // 偏移位置
              y: node.y + 30,
            };
            
            // 处理文本属性
            if (typeof node.text === 'string') {
              newNodeData.text = `${node.text}_copy`; // 文本也加上副本标记
            } else if (node.text && typeof node.text === 'object') {
              newNodeData.text = {
                ...node.text,
                value: `${node.text.value || ''}_copy`,
                x: node.text.x + 30, // 同时偏移文本位置
                y: node.text.y + 30
              };
            } else {
              newNodeData.text = `${node.properties?.text || '节点'}_copy`;
            }

            lf.addNode(newNodeData);
            

            message.success('节点已复制');
          }
        },
        {
          text: '删除',
          callback: (node) => {
            lf.deleteNode(node.id);
            selectedNode.value = null; // 清除选中状态
            message.success('节点已删除');
          }
        },
        {
          text: '编辑文案',
          callback: (node) => {
            // 这里可以打开编辑弹窗或执行其他操作
            message.info('开始编辑文案');
          }
        }
      ],
      edgeMenu: [
        {
          text: '删除',
          callback: (edge) => {
            lf.deleteEdge(edge.id);
            message.success('连线已删除');
          }
        },
        {
          text: '编辑文案',
          callback: (edge) => {
            message.info('开始编辑连线文案');
          }
        }
      ],
      graphMenu: [
        {
          text: '清空',
          callback: () => {
            lf.clearData();
            selectedNode.value = null;
            message.success('画布已清空');
          }
        }
      ]
    });
    
    lf.render(data.value);
    // 监听节点选择事件
    eventListener = lf.on('element:click', ({ data }: any) => {
      selectedNode.value = data;
    });

    lfRef.value = lf;
    canUndo.value = lf.undoAble;
    canRedo.value = lf.redoAble;
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
  graphData.value = lfRef.value?.getGraphData();
  previewVisible.value = true;
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



.designer-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}



.canvas-area {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.logicflow-container {
  flex: 1;
}


</style>