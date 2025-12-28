package com.process.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 审批通过处理委托
 */
@Component
public class ApprovalSuccessDelegate implements JavaDelegate {
    
    private static final Logger log = LoggerFactory.getLogger(ApprovalSuccessDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        // 获取流程变量
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessInstanceBusinessKey();
        
        log.info("审批通过 - 流程实例ID: {}, 业务Key: {}", processInstanceId, businessKey);
        
        // 这里可以执行审批通过后的业务逻辑，例如：
        // 1. 更新业务表状态
        // 2. 发送通知给申请人
        // 3. 记录审批日志
        
        // 设置流程变量标识审批结果
        execution.setVariable("finalResult", "approved");
        execution.setVariable("approvalTime", System.currentTimeMillis());
        
        log.info("审批通过处理完成");
    }
}
