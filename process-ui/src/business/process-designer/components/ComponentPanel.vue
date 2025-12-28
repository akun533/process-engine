<template>
  <div class="components-panel">
    <div class="panel-title">组件库</div>
    <div class="component-list">
      <div
        class="component-item"
        @mousedown="handleStartDrag($event, 'start')"
      >
        <div class="component-icon start-node"></div>
        <div class="component-label">开始节点</div>
      </div>
      <div
        class="component-item"
        @mousedown="handleStartDrag($event, 'end')"
      >
        <div class="component-icon end-node"></div>
        <div class="component-label">结束节点</div>
      </div>
      <div
        class="component-item"
        @mousedown="handleStartDrag($event, 'task')"
      >
        <div class="component-icon rect-node"></div>
        <div class="component-label">任务节点</div>
      </div>
      <div
        class="component-item"
        @mousedown="handleStartDrag($event, 'decision')"
      >
        <div class="component-icon diamond-node"></div>
        <div class="component-label">决策节点</div>
      </div>
      <div
        class="component-item"
        @mousedown="handleStartDrag($event, 'data')"
      >
        <div class="component-icon parallelogram-node"></div>
        <div class="component-label">数据节点</div>
      </div>
      <div
        class="component-item"
        @mousedown="handleStartDrag($event, 'document')"
      >
        <div class="component-icon document-node"></div>
        <div class="component-label">文档节点</div>
      </div>
      <div
        class="component-item"
        @mousedown="handleStartDrag($event, 'storage')"
      >
        <div class="component-icon storage-node"></div>
        <div class="component-label">存储节点</div>
      </div>
      <div
        class="component-item"
        @mousedown="handleStartDrag($event, 'manual')"
      >
        <div class="component-icon manual-node"></div>
        <div class="component-label">手动操作节点</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs } from 'vue';
import type LogicFlow from '@logicflow/core';

const props = defineProps<{
  lf: LogicFlow | null;
}>();

const { lf } = toRefs(props);

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

// 获取节点文本
const getNodeText = (nodeType: string): string => {
  switch (nodeType) {
    case 'start':
      return '开始';
    case 'end':
      return '结束';
    case 'task':
      return '任务';
    case 'decision':
      return '决策';
    case 'data':
      return '数据';
    case 'document':
      return '文档';
    case 'storage':
      return '存储';
    case 'manual':
      return '手动操作';
    default:
      return '节点';
  }
};

// 开始拖拽节点
const handleStartDrag = (event: MouseEvent, nodeType: string) => {
  event.preventDefault();
  if (lf.value) {
    lf.value.dnd.startDrag({
      type: nodeType,
      text: getNodeText(nodeType),
      properties: getDefaultPropertiesByNodeType(nodeType)
    });
  }
};
</script>

<style scoped>
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
</style>
