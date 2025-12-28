package com.process.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * 任务完成请求 DTO
 */
@Data
public class TaskCompleteRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 任务 ID
     */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
    
    /**
     * 审批意见
     */
    private String comment;
    
    /**
     * 审批结果（通过/拒绝）
     */
    private Boolean approved;
    
    /**
     * 任务变量
     */
    private Map<String, Object> variables;
    
    /**
     * 处理人
     */
    @NotBlank(message = "处理人不能为空")
    private String userId;
}
