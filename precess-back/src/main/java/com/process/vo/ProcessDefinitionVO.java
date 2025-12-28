package com.process.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程定义 VO
 */
@Data
public class ProcessDefinitionVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 流程定义 ID
     */
    private String id;
    
    /**
     * 流程定义 Key
     */
    private String key;
    
    /**
     * 流程定义名称
     */
    private String name;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 部署 ID
     */
    private String deploymentId;
    
    /**
     * 资源名称
     */
    private String resourceName;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 是否挂起
     */
    private Boolean suspended;
}
