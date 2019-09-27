package com.future.platform.account.handler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: zhaoyong.
 * @Description: account register
 * @Date:Created in 2019/9/25 16:33.
 * @Modified By:
 */
@Controller
public class RegisterHandler {

    @RequestMapping("/register.htm")
    public String registerView(Model model){

        return "register";
    }



}
