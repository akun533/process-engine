<template>
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
      </a-form>
      <div v-else class="no-selection">
        请选择一个节点进行配置
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, toRefs } from 'vue';
import type LogicFlow from '@logicflow/core';

interface NodeProperties {
  id: string;
  type: string;
  text: string;
  stroke: string;
  fill: string;
}

const props = defineProps<{
  selectedNode: any;
  lf: LogicFlow | null;
}>();

const { selectedNode, lf } = toRefs(props);

const nodeProperties = ref<NodeProperties>({
  id: '',
  type: '',
  text: '',
  stroke: '#1890ff',
  fill: '#ffffff'
});

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

// 监听选中节点变化，更新属性面板
watch(selectedNode, (newNode) => {
  if (newNode) {
    nodeProperties.value = {
      id: newNode.id,
      type: newNode.type,
      text: typeof newNode.text === 'string' ? newNode.text : (newNode.text?.value || ''),
      stroke: newNode.properties?.stroke || getDefaultPropertiesByNodeType(newNode.type).stroke,
      fill: newNode.properties?.fill || getDefaultPropertiesByNodeType(newNode.type).fill
    };
  }
}, { immediate: true });

// 监听文本变化，立即更新节点
watch(() => nodeProperties.value.text, (newText) => {
  if (selectedNode.value && lf.value) {
    lf.value.updateText(selectedNode.value.id, newText);
  }
});

// 监听边框颜色变化，立即更新节点
watch(() => nodeProperties.value.stroke, (newStroke) => {
  if (selectedNode.value && lf.value) {
    lf.value.setProperties(selectedNode.value.id, {
      stroke: newStroke
    });
    // 重新渲染以确保样式更新
    const graphData = lf.value.getGraphData();
    lf.value.render(graphData);
  }
});

// 监听填充颜色变化，立即更新节点
watch(() => nodeProperties.value.fill, (newFill) => {
  if (selectedNode.value && lf.value && !['start', 'end'].includes(nodeProperties.value.type)) {
    lf.value.setProperties(selectedNode.value.id, {
      fill: newFill
    });
    // 重新渲染以确保样式更新
    const graphData = lf.value.getGraphData();
    lf.value.render(graphData);
  }
});
</script>

<style scoped>
.properties-panel {
  width: 300px;
  border-left: 1px solid #e8e8e8;
  background: #fafafa;
  display: flex;
  flex-direction: column;
}

.panel-title {
  padding: 12px 16px;
  font-weight: bold;
  border-bottom: 1px solid #e8e8e8;
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
