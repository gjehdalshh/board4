package com.koreait.board4;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.koreait.board4.common.SecurityUtils;
import com.koreait.board4.db.CommonDAO;

public class Controller {
	private static UserController uCont = new UserController();
	private static BoardController bCont = new BoardController();
	
	public static void goToErr(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsp = "/WEB-INF/view/err.jsp";	
		request.getRequestDispatcher(jsp).forward(request, response);	// 주소값이 바뀌지 않음
	}
	
	public static void nav(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// request.getRequestURI(); // 요청된 정보가 넘어온다
		
		// 쿼리스트링으로 받아올 수도 있음
		// String[] testArr = "aaa/bbb/ccc/ddd".split("/");	0 : aaa, 1 : bbb, 2 : ccc, 3 : ddd
		request.setCharacterEncoding("UTF-8");
		String[] urlArr = request.getRequestURI().split("/");	// 받아 온 url을 / 기준으로 나눔
		
		//메뉴 리스트 가져오기
		ServletContext application = request.getServletContext();
		if(application.getAttribute("menus") == null) {
			application.setAttribute("menus", CommonDAO.selManageBoardList());
		}
		
		
		switch(urlArr[1]) {
		case "user":
			switch(urlArr[2]) {
			case "login.korea":
				uCont.login(request, response);
				return;
			case "loginProc.korea":
				uCont.loginProc(request, response);
				return;
			case "join.korea":
				uCont.join(request, response);
				return;
			case "joinProc.korea":
				uCont.joinProc(request, response);
				return;
			case "logout.korea":
				uCont.logout(request, response);
				return;
			}
		break;
		case "board":
			switch(urlArr[2]) {
			case "list.korea":
				bCont.list(request, response);
				return;
			}
		break;
		}
		
		if(SecurityUtils.getLoginUserPk(request) > 0) { // 로그인이 되어 있는 상태
			switch(urlArr[1]) {
			case "board":
				switch(urlArr[2]) {
				case "reg.korea":
					bCont.reg(request, response);
					return;
				case "regProc.korea":
					bCont.regProc(request, response);
					return;
				case "modProc.korea":
					bCont.modProc(request, response);
					return;
				}
			}
		}
		
		goToErr(request, response);
	}
}




