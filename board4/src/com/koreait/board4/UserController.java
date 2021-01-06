package com.koreait.board4;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.security.SecurityUtil;

import com.koreait.board4.common.SecurityUtils;
import com.koreait.board4.common.Utils;
import com.koreait.board4.db.UserDAO;
import com.koreait.board4.model.UserModel;

public class UserController {
	//로그인 페이지 띄우기(get)
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Utils.forwardTemp("로그인", "temp/basic_temp" ,"user/login",  request, response);
	}
	
	//로그인 처리(post)
	public void loginProc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		String user_id = request.getParameter("user_id");
		String user_pw = request.getParameter("user_pw");
		UserModel model = new UserModel();
		model.setUser_id(user_id);
		
		// 에러 : 0, 이상없음 : 1, 아이디 없음 : 2, 비밀번호 틀림 : 3,
		UserModel loginUser = UserDAO.selUser(model);
		
		System.out.println(loginUser.getI_user());
		// 아이디 없음
		if(loginUser == null) {
			
		}
		
		
		
		
	}
}
