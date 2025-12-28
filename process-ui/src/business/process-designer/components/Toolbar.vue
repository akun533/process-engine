<template>
  <div class="toolbar">
    <!-- 左侧占位 -->
    <div class="toolbar-left"></div>
    
    <!-- 中间常用工具 -->
    <div class="toolbar-center">
      <a-button @click="handleUndo" :disabled="!canUndo">
        <template #icon>
          <UndoOutlined />
        </template>
        撤销
      </a-button>
      <a-button @click="handleRedo" :disabled="!canRedo">
        <template #icon>
          <RedoOutlined />
        </template>
        重做
      </a-button>
      <a-button @click="handleClear">
        <template #icon>
          <DeleteOutlined />
        </template>
        清空
      </a-button>
      <a-button @click="handlePreview">
        <template #icon>
          <EyeOutlined />
        </template>
        预览
      </a-button>
    </div>
    
    <!-- 右侧保存按钮 -->
    <div class="toolbar-right">
      <a-button type="primary" @click="handleSave">
        <template #icon>
          <SaveOutlined />
        </template>
        保存
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs } from 'vue';
import {
  UndoOutlined,
  RedoOutlined,
  DeleteOutlined,
  SaveOutlined,
  EyeOutlined
} from '@ant-design/icons-vue';

const props = defineProps<{
  canUndo: boolean;
  canRedo: boolean;
}>();

const emit = defineEmits<{
  undo: [];
  redo: [];
  clear: [];
  save: [];
  preview: [];
}>();

const { canUndo, canRedo } = toRefs(props);

const handleUndo = () => {
  emit('undo');
};

const handleRedo = () => {
  emit('redo');
};

const handleClear = () => {
  emit('clear');
};

const handleSave = () => {
  emit('save');
};

const handlePreview = () => {
  emit('preview');
};
</script>

<style scoped>
.toolbar {
  padding: 10px 20px;
  border-bottom: 1px solid #e8e8e8;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toolbar-left {
  flex: 1;
}

.toolbar-center {
  display: flex;
  gap: 8px;
  align-items: center;
}

.toolbar-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}
</style>
