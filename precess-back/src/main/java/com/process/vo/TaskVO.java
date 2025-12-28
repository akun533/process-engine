package com.process.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务 VO
 */
@Data
public class TaskVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 任务 ID
     */
    private String id;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 流程实例 ID
     */
    private String processInstanceId;
    
    /**
     * 流程定义 ID
     */
    private String processDefinitionId;
    
    /**
     * 任务办理人
     */
    private String assignee;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 到期时间
     */
    private Date dueDate;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 任务定义 Key
     */
    private String taskDefinitionKey;
}
