import LogicFlow, { 
  RectNode, 
  RectNodeModel, 
  CircleNode, 
  CircleNodeModel, 
  PolygonNode, 
  PolygonNodeModel,
} from '@logicflow/core'

// 开始节点
class StartNode extends CircleNode {
}
class StartModel extends CircleNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    this.stroke = data.properties?.stroke || '#52c41a';
    this.fill = data.properties?.fill || '#f6ffed';
    this.r = 30;
  }
  
  setAttributes() {
    super.setAttributes();
    this.stroke = this.data?.properties?.stroke || '#52c41a';
    this.fill = this.data?.properties?.fill || '#f6ffed';
  }
  
  getNodeStyle() {
    const style = super.getNodeStyle();
    style.strokeWidth = 2;
    style.stroke = this.data?.properties?.stroke || this.stroke;
    style.fill = this.data?.properties?.fill || this.fill;
    return style;
  }
}

// 结束节点
class EndNode extends CircleNode {
}
class EndModel extends CircleNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    this.stroke = data.properties?.stroke || '#ff4d4f';
    this.fill = data.properties?.fill || '#fff1f0';
    this.r = 30;
  }
  
  setAttributes() {
    super.setAttributes();
    this.stroke = this.data?.properties?.stroke || '#ff4d4f';
    this.fill = this.data?.properties?.fill || '#fff1f0';
  }
  
  getNodeStyle() {
    const style = super.getNodeStyle();
    style.strokeWidth = 2;
    style.stroke = this.data?.properties?.stroke || this.stroke;
    style.fill = this.data?.properties?.fill || this.fill;
    return style;
  }
}

// 任务节点
class TaskNode extends RectNode {
}
class TaskModel extends RectNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    this.width = 100;
    this.height = 50;
    this.stroke = data.properties?.stroke || '#1890ff';
    this.fill = data.properties?.fill || '#e6f7ff';
  }
  
  setAttributes() {
    super.setAttributes();
    this.stroke = this.data?.properties?.stroke || '#1890ff';
    this.fill = this.data?.properties?.fill || '#e6f7ff';
  }
  
  getNodeStyle() {
    const style = super.getNodeStyle();
    style.strokeWidth = 2;
    style.stroke = this.data?.properties?.stroke || this.stroke;
    style.fill = this.data?.properties?.fill || this.fill;
    return style;
  }
}

// 决策节点（菱形）
class DecisionNode extends PolygonNode {
  getAnchorStyle() {
    return {
      stroke: '#1890ff',
      strokeWidth: 2,
    };
  }
}
class DecisionModel extends PolygonNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    this.stroke = data.properties?.stroke || '#ff7875';
    this.fill = data.properties?.fill || '#fff0f6';
    this.points = [
      [50, 0],
      [100, 50],
      [50, 100],
      [0, 50],
    ];
  }
  
  setAttributes() {
    super.setAttributes();
    this.stroke = this.data?.properties?.stroke || '#ff7875';
    this.fill = this.data?.properties?.fill || '#fff0f6';
  }
  
  getNodeStyle() {
    const style = super.getNodeStyle();
    style.strokeWidth = 2;
    style.stroke = this.data?.properties?.stroke || this.stroke;
    style.fill = this.data?.properties?.fill || this.fill;
    return style;
  }
}

// 数据节点（平行四边形）
class DataNode extends PolygonNode {
}
class DataModel extends PolygonNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    this.stroke = data.properties?.stroke || '#9254de';
    this.fill = data.properties?.fill || '#f9f0ff';
    this.points = [
      [20, 0],
      [100, 0],
      [80, 50],
      [0, 50],
    ];
  }
  
  setAttributes() {
    super.setAttributes();
    this.stroke = this.data?.properties?.stroke || '#9254de';
    this.fill = this.data?.properties?.fill || '#f9f0ff';
  }
  
  getNodeStyle() {
    const style = super.getNodeStyle();
    style.strokeWidth = 2;
    style.stroke = this.data?.properties?.stroke || this.stroke;
    style.fill = this.data?.properties?.fill || this.fill;
    return style;
  }
}

// 文档节点（底部带弧线的矩形）
class DocumentNode extends RectNode {
}
class DocumentModel extends RectNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    this.width = 100;
    this.height = 60;
    this.stroke = data.properties?.stroke || '#52c41a';
    this.fill = data.properties?.fill || '#f6ffed';
  }
  
  setAttributes() {
    super.setAttributes();
    this.stroke = this.data?.properties?.stroke || '#52c41a';
    this.fill = this.data?.properties?.fill || '#f6ffed';
  }
  
  getShape() {
    const { x, y, width, height } = this;
    const r = 5;
    const points = [
      { x: x - width / 2, y: y - height / 2 },
      { x: x + width / 2, y: y - height / 2 },
      { x: x + width / 2, y: y + height / 2 - r },
      { x: x, y: y + height / 2 },
      { x: x - width / 2, y: y + height / 2 - r },
    ];
    
    return {
      type: 'polygon',
      points,
      style: this.getNodeStyle(),
    };
  }
  getNodeStyle() {
    const style = super.getNodeStyle();
    style.strokeWidth = 2;
    style.stroke = this.data?.properties?.stroke || this.stroke;
    style.fill = this.data?.properties?.fill || this.fill;
    return style;
  }
}

// 存储节点（圆角矩形）
class StorageNode extends RectNode {
}
class StorageModel extends RectNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    this.width = 100;
    this.height = 60;
    this.stroke = data.properties?.stroke || '#fa8c16';
    this.fill = data.properties?.fill || '#fff7e6';
  }
  
  setAttributes() {
    super.setAttributes();
    this.stroke = this.data?.properties?.stroke || '#fa8c16';
    this.fill = this.data?.properties?.fill || '#fff7e6';
  }
  
  getShape() {
    const { x, y, width, height } = this;
    const r = 15;
    
    return {
      type: 'path',
      d: [
        'M', x - width / 2 + r, y - height / 2,
        'L', x + width / 2 - r, y - height / 2,
        'Q', x + width / 2, y - height / 2, x + width / 2, y - height / 2 + r,
        'L', x + width / 2, y + height / 2 - r,
        'Q', x + width / 2, y + height / 2, x + width / 2 - r, y + height / 2,
        'L', x - width / 2 + r, y + height / 2,
        'Q', x - width / 2, y + height / 2, x - width / 2, y + height / 2 - r,
        'L', x - width / 2, y - height / 2 + r,
        'Q', x - width / 2, y - height / 2, x - width / 2 + r, y - height / 2,
        'Z'
      ].join(' '),
      style: this.getNodeStyle(),
    };
  }
  getNodeStyle() {
    const style = super.getNodeStyle();
    style.strokeWidth = 2;
    style.stroke = this.data?.properties?.stroke || this.stroke;
    style.fill = this.data?.properties?.fill || this.fill;
    return style;
  }
}

// 手动操作节点（矩形，顶部有操作线）
class ManualNode extends RectNode {
}
class ManualModel extends RectNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    this.width = 100;
    this.height = 60;
    this.stroke = data.properties?.stroke || '#ffa940';
    this.fill = data.properties?.fill || '#fff2e8';
  }
  
  setAttributes() {
    super.setAttributes();
    this.stroke = this.data?.properties?.stroke || '#ffa940';
    this.fill = this.data?.properties?.fill || '#fff2e8';
  }
  
  getShape() {
    const { x, y, width, height } = this;
    const lineY = y - height / 2 + 15;
    
    return {
      type: 'group',
      children: [
        {
          type: 'rect',
          x: x,
          y: y,
          width,
          height,
          style: this.getNodeStyle(),
        },
        {
          type: 'line',
          x1: x - width / 2,
          y1: lineY,
          x2: x + width / 2,
          y2: lineY,
          style: {
            stroke: this.data?.properties?.stroke || this.stroke,
            strokeWidth: 2,
          },
        }
      ]
    };
  }
  getNodeStyle() {
    const style = super.getNodeStyle();
    style.strokeWidth = 2;
    style.stroke = this.data?.properties?.stroke || this.stroke;
    style.fill = this.data?.properties?.fill || this.fill;
    return style;
  }
}

// 注册所有自定义节点
export const registerNodes = (lf: LogicFlow) => {
  lf.register({
    type: 'start',
    view: StartNode,
    model: StartModel,
  });
  
  lf.register({
    type: 'end',
    view: EndNode,
    model: EndModel,
  });
  
  lf.register({
    type: 'task',
    view: TaskNode,
    model: TaskModel,
  });
  
  lf.register({
    type: 'decision',
    view: DecisionNode,
    model: DecisionModel,
  });
  
  lf.register({
    type: 'data',
    view: DataNode,
    model: DataModel,
  });
  
  lf.register({
    type: 'document',
    view: DocumentNode,
    model: DocumentModel,
  });
  
  lf.register({
    type: 'storage',
    view: StorageNode,
    model: StorageModel,
  });
  
  lf.register({
    type: 'manual',
    view: ManualNode,
    model: ManualModel,
  });
};