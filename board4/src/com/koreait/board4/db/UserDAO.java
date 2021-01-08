package com.koreait.board4.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.koreait.board4.model.UserModel;

public class UserDAO extends CommonDAO{
	public static UserModel selUser(UserModel p) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = " SELECT i_user, user_pw, salt, nm"
				+ " FROM t_user "
				+ " WHERE user_id = ? ";
		
		try {
			con = DbUtils.getCon();	
			ps = con.prepareStatement(sql);
			ps.setNString(1, p.getUser_id());
			rs = ps.executeQuery();
			
			if(rs.next()) {
				UserModel model = new UserModel();
				model.setI_user(rs.getInt("i_user"));
				model.setUser_pw(rs.getString("user_pw"));
				model.setSalt(rs.getString("salt"));
				model.setNm(rs.getString("nm"));
				return model;
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			DbUtils.close(con, ps, rs);
		}
		return null;
	}
	
	
}
