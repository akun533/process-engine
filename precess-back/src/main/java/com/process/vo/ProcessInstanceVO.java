package com.process.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程实例 VO
 */
@Data
public class ProcessInstanceVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 流程实例 ID
     */
    private String id;
    
    /**
     * 流程定义 ID
     */
    private String processDefinitionId;
    
    /**
     * 流程定义 Key
     */
    private String processDefinitionKey;
    
    /**
     * 流程定义名称
     */
    private String processDefinitionName;
    
    /**
     * 业务 Key
     */
    private String businessKey;
    
    /**
     * 流程实例名称
     */
    private String name;
    
    /**
     * 发起人
     */
    private String startUserId;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 是否挂起
     */
    private Boolean suspended;
    
    /**
     * 是否结束
     */
    private Boolean ended;
}
