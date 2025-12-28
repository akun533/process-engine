package com.process.controller;

import com.process.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("流程引擎后端服务运行正常！");
    }
    
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "process-engine-backend");
        info.put("version", "1.0.0");
        info.put("flowable", "6.8.0");
        info.put("status", "running");
        return Result.success(info);
    }
}
