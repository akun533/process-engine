package com.process.service;

import com.process.dto.ProcessStartRequest;
import com.process.dto.TaskCompleteRequest;
import com.process.vo.ProcessDefinitionVO;
import com.process.vo.ProcessInstanceVO;
import com.process.vo.TaskVO;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程服务
 */
@Service
public class ProcessService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 部署流程定义
     */
    public String deployProcess(String resourceName, InputStream inputStream) {
        Deployment deployment = repositoryService.createDeployment()
                .addInputStream(resourceName, inputStream)
                .name(resourceName)
                .deploy();
        return deployment.getId();
    }

    /**
     * 获取所有流程定义
     */
    public List<ProcessDefinitionVO> listProcessDefinitions() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionKey()
                .asc()
                .list();

        return list.stream().map(pd -> {
            ProcessDefinitionVO vo = new ProcessDefinitionVO();
            vo.setId(pd.getId());
            vo.setKey(pd.getKey());
            vo.setName(pd.getName());
            vo.setVersion(pd.getVersion());
            vo.setDeploymentId(pd.getDeploymentId());
            vo.setResourceName(pd.getResourceName());
            vo.setDescription(pd.getDescription());
            vo.setSuspended(pd.isSuspended());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 启动流程实例
     */
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstanceVO startProcess(ProcessStartRequest request) {
        // 设置流程发起人
        String userId = request.getStartUserId();
        
        // 准备流程变量
        Map<String, Object> variables = request.getVariables();
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("startUserId", userId);
        
        // 启动流程实例
        ProcessInstance processInstance;
        if (request.getBusinessKey() != null) {
            processInstance = runtimeService.startProcessInstanceByKey(
                    request.getProcessDefinitionKey(),
                    request.getBusinessKey(),
                    variables
            );
        } else {
            processInstance = runtimeService.startProcessInstanceByKey(
                    request.getProcessDefinitionKey(),
                    variables
            );
        }
        
        // 设置流程实例名称
        if (request.getProcessInstanceName() != null) {
            runtimeService.setProcessInstanceName(
                    processInstance.getId(),
                    request.getProcessInstanceName()
            );
        }
        
        // 转换为 VO
        return convertToProcessInstanceVO(processInstance);
    }

    /**
     * 获取用户的待办任务
     */
    public List<TaskVO> listUserTasks(String userId) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(userId)
                .orderByTaskCreateTime()
                .desc()
                .list();

        return tasks.stream().map(this::convertToTaskVO).collect(Collectors.toList());
    }

    /**
     * 完成任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(TaskCompleteRequest request) {
        String taskId = request.getTaskId();
        
        // 添加审批意见
        if (request.getComment() != null) {
            taskService.addComment(taskId, null, request.getComment());
        }
        
        // 准备任务变量
        Map<String, Object> variables = request.getVariables();
        if (variables == null) {
            variables = new HashMap<>();
        }
        
        // 如果有审批结果，添加到变量中
        if (request.getApproved() != null) {
            variables.put("approved", request.getApproved());
        }
        
        // 完成任务
        taskService.complete(taskId, variables);
    }

    /**
     * 获取流程实例详情
     */
    public ProcessInstanceVO getProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        
        if (processInstance != null) {
            return convertToProcessInstanceVO(processInstance);
        }
        
        // 如果运行时没有，查询历史
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        
        if (historicProcessInstance != null) {
            return convertToHistoricProcessInstanceVO(historicProcessInstance);
        }
        
        return null;
    }

    /**
     * 获取流程图（PNG 格式）
     */
    public InputStream getProcessDiagram(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        
        // 获取当前活动节点
        List<String> activeActivityIds = new ArrayList<>();
        
        return processEngine.getProcessEngineConfiguration()
                .getProcessDiagramGenerator()
                .generateDiagram(
                        bpmnModel,
                        "png",
                        activeActivityIds,
                        new ArrayList<>(),
                        "宋体",
                        "宋体",
                        "宋体",
                        null,
                        1.0,
                        true
                );
    }

    /**
     * 转换为流程实例 VO
     */
    private ProcessInstanceVO convertToProcessInstanceVO(ProcessInstance pi) {
        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setId(pi.getId());
        vo.setProcessDefinitionId(pi.getProcessDefinitionId());
        vo.setProcessDefinitionKey(pi.getProcessDefinitionKey());
        vo.setProcessDefinitionName(pi.getProcessDefinitionName());
        vo.setBusinessKey(pi.getBusinessKey());
        vo.setName(pi.getName());
        vo.setStartUserId(pi.getStartUserId());
        vo.setStartTime(pi.getStartTime());
        vo.setSuspended(pi.isSuspended());
        vo.setEnded(pi.isEnded());
        return vo;
    }

    /**
     * 转换为历史流程实例 VO
     */
    private ProcessInstanceVO convertToHistoricProcessInstanceVO(HistoricProcessInstance hpi) {
        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setId(hpi.getId());
        vo.setProcessDefinitionId(hpi.getProcessDefinitionId());
        vo.setProcessDefinitionKey(hpi.getProcessDefinitionKey());
        vo.setProcessDefinitionName(hpi.getProcessDefinitionName());
        vo.setBusinessKey(hpi.getBusinessKey());
        vo.setName(hpi.getName());
        vo.setStartUserId(hpi.getStartUserId());
        vo.setStartTime(hpi.getStartTime());
        vo.setEnded(hpi.getEndTime() != null);
        return vo;
    }

    /**
     * 转换为任务 VO
     */
    private TaskVO convertToTaskVO(Task task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setName(task.getName());
        vo.setDescription(task.getDescription());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setAssignee(task.getAssignee());
        vo.setCreateTime(task.getCreateTime());
        vo.setDueDate(task.getDueDate());
        vo.setPriority(task.getPriority());
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        return vo;
    }
}
