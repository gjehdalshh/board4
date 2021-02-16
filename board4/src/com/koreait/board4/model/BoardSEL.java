package com.koreait.board4.model;
// Entity(model), Domain(sel), DTO(param), VO(값을 넣을 수 없음(setter가 없음)) 를 가장 많이 사용한다
public class BoardSEL extends BoardModel {
	private String writer_nm;
	private int favorite_cnt;
	private int is_favorite;
	private String profile_img;
	
	
	public String getProfile_img() {
		return profile_img;
	}
	public void setProfile_img(String profile_img) {
		this.profile_img = profile_img;
	}
	public int getIs_favorite() {
		return is_favorite;
	}
	public void setIs_favorite(int is_favorite) {
		this.is_favorite = is_favorite;
	}
	public String getWriter_nm() {
		return writer_nm;
	}
	public void setWriter_nm(String writer_nm) {
		this.writer_nm = writer_nm;
	}
	public int getFavorite_cnt() {
		return favorite_cnt;
	}
	public void setFavorite_cnt(int favorite_cnt) {
		this.favorite_cnt = favorite_cnt;
	}
	
}
