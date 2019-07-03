package com.example.activiti.controller;

import com.example.activiti.model.User;
import com.example.activiti.service.UserService;
import com.example.activiti.utils.Constant;
import com.example.activiti.utils.ReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	public ReturnValue login(User user, HttpServletRequest request, HttpSession session) {

		User userInfo = userService.userLogin(user);
		if (userInfo != null) {
			session.setAttribute("username", userInfo.getUsername());
			System.err.println("用户:" + request.getSession().getAttribute("userName"));
			return new ReturnValue(Constant.SUCCESS, "登录成功", userInfo);
		} else {
			return new ReturnValue(Constant.ERROR, "登录失败", null);
		}
	}

	@PostMapping("/getUserInfo")
	public ReturnValue getUser(HttpServletRequest request, HttpSession session) {
		String userName = session.getAttribute("username") + "";
		System.out.println("用户名：" + userName);
		return new ReturnValue(Constant.SUCCESS, "获取用户信息成功", userName);
	}

}
