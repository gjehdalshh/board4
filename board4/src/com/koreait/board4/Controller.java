package com.koreait.board4;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Controller {
	private static UserController uCont = new UserController();
	public static void goToErr(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsp = "/WEB-INF/view/err.jsp";	
		request.getRequestDispatcher(jsp).forward(request, response);	// 주소값이 바뀌지 않음
	}
	public static void nav(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// request.getRequestURI(); // 요청된 정보가 넘어온다
		
		// 쿼리스트링으로 받아올 수도 있음
		// String[] testArr = "aaa/bbb/ccc/ddd".split("/");	0 : aaa, 1 : bbb, 2 : ccc, 3 : ddd
		
		String[] urlArr = request.getRequestURI().split("/");	// 받아 온 url을 / 기준으로 나눔
		
		switch(urlArr[1]) {
		case "user":
			switch(urlArr[2]) {
			case "login.korea":
				uCont.login(request, response);
				return;
			case "loginProc.korea":
				uCont.loginProc(request, response);
				return;
			}
		break;
		case "board":
			
		break;
		}
		goToErr(request, response);
	}
}
