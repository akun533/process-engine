package com.process.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * 流程启动请求 DTO
 */
@Data
public class ProcessStartRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 流程定义 Key
     */
    @NotBlank(message = "流程定义Key不能为空")
    private String processDefinitionKey;
    
    /**
     * 业务 Key
     */
    private String businessKey;
    
    /**
     * 流程实例名称
     */
    private String processInstanceName;
    
    /**
     * 流程变量
     */
    private Map<String, Object> variables;
    
    /**
     * 发起人
     */
    @NotBlank(message = "发起人不能为空")
    private String startUserId;
}
