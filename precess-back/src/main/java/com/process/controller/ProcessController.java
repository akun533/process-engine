package com.process.controller;

import com.process.common.Result;
import com.process.dto.ProcessStartRequest;
import com.process.dto.TaskCompleteRequest;
import com.process.service.ProcessService;
import com.process.vo.ProcessDefinitionVO;
import com.process.vo.ProcessInstanceVO;
import com.process.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 流程管理控制器
 */
@RestController
@RequestMapping("/api/process")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    /**
     * 部署流程定义
     */
    @PostMapping("/deploy")
    public Result<String> deployProcess(@RequestParam("file") MultipartFile file) {
        try {
            String deploymentId = processService.deployProcess(
                    file.getOriginalFilename(),
                    file.getInputStream()
            );
            return Result.success(deploymentId);
        } catch (Exception e) {
            return Result.error("部署流程失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有流程定义列表
     */
    @GetMapping("/definitions")
    public Result<List<ProcessDefinitionVO>> listProcessDefinitions() {
        try {
            List<ProcessDefinitionVO> list = processService.listProcessDefinitions();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取流程定义列表失败: " + e.getMessage());
        }
    }

    /**
     * 启动流程实例
     */
    @PostMapping("/start")
    public Result<ProcessInstanceVO> startProcess(@Validated @RequestBody ProcessStartRequest request) {
        try {
            ProcessInstanceVO processInstance = processService.startProcess(request);
            return Result.success(processInstance);
        } catch (Exception e) {
            return Result.error("启动流程失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户待办任务
     */
    @GetMapping("/tasks/{userId}")
    public Result<List<TaskVO>> listUserTasks(@PathVariable String userId) {
        try {
            List<TaskVO> tasks = processService.listUserTasks(userId);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取待办任务失败: " + e.getMessage());
        }
    }

    /**
     * 完成任务
     */
    @PostMapping("/task/complete")
    public Result<Void> completeTask(@Validated @RequestBody TaskCompleteRequest request) {
        try {
            processService.completeTask(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error("完成任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取流程实例详情
     */
    @GetMapping("/instance/{processInstanceId}")
    public Result<ProcessInstanceVO> getProcessInstance(@PathVariable String processInstanceId) {
        try {
            ProcessInstanceVO instance = processService.getProcessInstance(processInstanceId);
            if (instance == null) {
                return Result.error("流程实例不存在");
            }
            return Result.success(instance);
        } catch (Exception e) {
            return Result.error("获取流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 获取流程图
     */
    @GetMapping(value = "/diagram/{processDefinitionId}", produces = MediaType.IMAGE_PNG_VALUE)
    public void getProcessDiagram(@PathVariable String processDefinitionId, HttpServletResponse response) {
        try {
            InputStream inputStream = processService.getProcessDiagram(processDefinitionId);
            OutputStream outputStream = response.getOutputStream();
            
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
