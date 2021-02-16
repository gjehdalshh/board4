package com.koreait.board4;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.koreait.board4.common.SecurityUtils;
import com.koreait.board4.common.Utils;
import com.koreait.board4.db.BoardDAO;
import com.koreait.board4.db.SQLInterUpdate;
import com.koreait.board4.db.UserDAO;
import com.koreait.board4.model.UserModel;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

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
			loginUser.setR_dt(null);
			loginUser.setPh(null);
			loginUser.setProfile_img(null);
			loginUser.setUser_id(null);
			
			HttpSession session = request.getSession();
			session.setAttribute("loginUser", loginUser);
			
			response.sendRedirect("/board/list.korea?typ=1");
			
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
	
	//프로필 화면
	public void profile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		UserModel param = new UserModel();
		param.setI_user(SecurityUtils.getLoginUserPk(request));
		
		request.setAttribute("data", UserDAO.selUser(param));
		request.setAttribute("jsList", new String[] {"axios.min","user"});
		Utils.forwardTemp("프로필", "temp/basic_temp", "user/profile", request, response);
	}
	
	//이미지 업로드 proc
	public void profileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		int i_user = SecurityUtils.getLoginUserPk(request);
		String savePath = request.getServletContext().getRealPath("/res/img/"+i_user);
		
		//폴더를 만듬
		File folder = new File(savePath);
		/*
		//파일 삭제
		File imgFile = new File(savePath+"/"+filename);
		if(imgFile.exists()) {
			imgFile.delete();
		}
		*/
		if(folder.exists()){	// 기존에 이미지가 있었다면 삭제처리
			File[] folder_list = folder.listFiles();
			for(File file : folder_list) {
				if(file.isFile()) {
					file.delete();
				}
			}
			folder.delete();
		}
		folder.mkdirs();
		
		int sizeLimit = 104_857_600; // 100mb제한
		 MultipartRequest multi = new MultipartRequest(request, savePath, sizeLimit, "utf-8", new DefaultFileRenamePolicy());
		
		 Enumeration files = multi.getFileNames();
		 if(files.hasMoreElements()) {
			 String eleName = (String) files.nextElement();
			 
			 String fileNm2 = multi.getFilesystemName(eleName);;
			 
			 String sql = " UPDATE t_user SET profile_img =? "
			 		+ " WHERE i_user = ?";
			 
			 UserDAO.executeUpdate(sql, new SQLInterUpdate() {
				
				@Override
				public void proc(PreparedStatement ps) throws SQLException {
					ps.setString(1, fileNm2);
					ps.setInt(2, i_user);
				}
			});
		 }
		
		
		response.sendRedirect("/user/profile.korea");
	}
	
	public void delProfileImg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		int i_user = SecurityUtils.getLoginUserPk(request);
		String savePath = request.getServletContext().getRealPath("/res/img/"+i_user);
		
		File folder = new File(savePath);
		if(folder.exists()){	// 기존에 이미지가 있었다면 삭제처리
			File[] folder_list = folder.listFiles();
			for(File file : folder_list) {
				if(file.isFile()) {
					file.delete();
				}
			}
			folder.delete();
		}
		
		String sql = " UPDATE t_user SET profile_img = null "
		 		+ " WHERE i_user = ?";
		
		UserDAO.executeUpdate(sql, new SQLInterUpdate() {
			
			@Override
			public void proc(PreparedStatement ps) throws SQLException {
				ps.setInt(1, i_user);
			}
		});
		String result = "{\"result\":1}";
		response.setContentType("application/json");
		System.out.println(result);
		response.getWriter().print(result);
	 }

	public void changePw(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Utils.forwardTemp("비밀번호변경", "temp/basic_temp", "user/changePw",request, response);
	}
	
	
	public void changePwProc(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		int i_user = SecurityUtils.getLoginUserPk(request);
		String salt = SecurityUtils.getSalt();
		String curruentPw = request.getParameter("curruentPw");
		String user_pw = request.getParameter("user_pw");
		String encryptPw2 = SecurityUtils.getSecurePassword(user_pw, salt);
		String re_user_pw = request.getParameter("re_user_pw");
		String encryptPw3 = SecurityUtils.getSecurePassword(re_user_pw, salt);
		
		UserModel model = new UserModel();
		model.setI_user(i_user);
	
		UserModel loginUser = UserDAO.selUserchange(model);
		
		String userDbPw = loginUser.getUser_pw();
		String encryptPw = SecurityUtils.getSecurePassword(curruentPw, loginUser.getSalt());
		if(userDbPw.equals(encryptPw)) {	// 현재 비밀번호 같음
			
			if(encryptPw2.equals(encryptPw3)) {
				String sql = " UPDATE t_user SET user_pw = ?, salt = ? "
						+ " WHERE i_user = ? ";
				
				UserDAO.executeUpdate(sql, new SQLInterUpdate() {
					
					@Override
					public void proc(PreparedStatement ps) throws SQLException {
						ps.setString(1, encryptPw2);
						ps.setString(2, salt);
						ps.setInt(3, i_user);
					}
				});
				logout(request, response);
			} else {
				request.setAttribute("msg", "기존 비밀번호를 확인해 주세요");
			}
			
		}
	}
}











