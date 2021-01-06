package com.koreait.board4;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.koreait.board4.common.Utils;

public class UserController {
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Utils.forwardTemp("로그인", "temp/basic_temp" ,"user/login",  request, response);
	}
}
