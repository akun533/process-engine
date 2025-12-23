import LogicFlow, { RectNode, RectNodeModel } from '@logicflow/core'
// 提供节点
class UserNode extends RectNode {
}
// 提供节点的属性
class UserModel extends RectNodeModel {
  constructor(data: any, graphModel: any) {
    super(data, graphModel);
    const { size } = data.properties;
    this.width = size * 80;
    this.height = size * 40;
    this.fill = 'green';
  }
}


// 注册所有自定义节点
export const registerNodes = (lf: LogicFlow) => {
  lf.register({
    type: 'user',
    view: UserNode,
    model: UserModel,
  });
};