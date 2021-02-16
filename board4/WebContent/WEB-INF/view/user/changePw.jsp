<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="centerCont">
	<div>
		<h1>비밀번호 변경</h1>
		<form action="/user/changePwProc.korea" method="post">
			<div><input type="password" name="curruentPw" placeholder="현재 비밀번호"></div>
			<div><input type="password" name="user_pw" placeholder="변경 비밀번호"></div>
			<div><input type="password" name="re_user_pw" placeholder="확인 비밀번호"></div>
			<input type="submit" value="변경">
		</form>
	</div>
</div>