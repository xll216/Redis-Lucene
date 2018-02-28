package com.ssm.controller;

import com.ssm.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
@Controller
public class MainController {
    @Resource
    private StudentService studentService;

    /**
     * 配置起始页
     * http://localhost:8080
     * http://localhost:8080/
     * http://localhost:8080/index
     **/
    @RequestMapping(
            value = {"/", "/index"})
    public String index() {
        return "index";
    }

}
