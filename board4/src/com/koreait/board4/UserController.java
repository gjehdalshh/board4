package com.koreait.board4;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.koreait.board4.common.SecurityUtils;
import com.koreait.board4.common.Utils;
import com.koreait.board4.db.SQLInterUpdate;
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
		model.setUser_pw(user_pw);
		
		// 에러 : 0, 이상없음 : 1, 아이디 없음 : 2, 비밀번호 틀림 : 3,
		UserModel loginUser = UserDAO.selUser(model);
		
		if(loginUser == null) {	// 아이디 없음
			request.setAttribute("msg", "아이디를 확인해 주세요");
			login(request, response);
			return;
		}
		String userDbPw = loginUser.getUser_pw();
		String encryptPw = SecurityUtils.getSecurePassword(user_pw, loginUser.getSalt());
		if(userDbPw.equals(encryptPw)) {	// 로그인 성공
			loginUser.setSalt(null);
			loginUser.setUser_pw(null);
			
			HttpSession session = request.getSession();
			session.setAttribute("loginUser", loginUser);
			
			response.sendRedirect("/board/list.korea");
			
		} else {	// 비밀번호 틀림
			request.setAttribute("msg", "비밀번호를 확인해 주세요");
			login(request, response);
		}
		
	}
	
	// 화원가입 페이지 띄우기(get)
	public void join(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Utils.forwardTemp("회원가입", "temp/basic_temp", "user/join", request, response);
	}
	
	public void joinProc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		String user_id = request.getParameter("user_id");
		String user_pw = request.getParameter("user_pw");
		String nm = request.getParameter("nm");
		int gender = Utils.getIntParam(request, "gender");
		String ph = request.getParameter("ph");
		
		String salt = SecurityUtils.getSalt();
		String encryptPw = SecurityUtils.getSecurePassword(user_pw, salt);
		
		String sql = " INSERT INTO t_user "
				+ " (user_id, user_pw, nm, gender, ph, salt) "
				+ " VALUES "
				+ " (?, ?, ?, ?, ?, ?)";
		
		int result = UserDAO.executeUpdate(sql, new SQLInterUpdate() {
			
		public void proc(PreparedStatement ps) throws SQLException {
				ps.setString(1, user_id);
				ps.setString(2, encryptPw);
				ps.setString(3, nm);
				ps.setInt(4, gender);
				ps.setString(5, ph);
				ps.setString(6, salt);
			}
		});
		if(result == 0)
		{
			request.setAttribute("msg", "회원가입에 실패하였습니다.");
			join(request,response);
			return;
		}else {
			response.sendRedirect("/user/login.korea");
		}
		
		//회원가입 오류가 발생되면 (아아디가 엄청 길면 등등) 다시 회원가입 페이지로 가면 된다.
		//회원가입 완료되면 로그인 화면으로 이동
	}

	public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		HttpSession hs = request.getSession();
		hs.invalidate(); // 모든 키와 벨류값이 날라감
		login(request, response);
	}
}











