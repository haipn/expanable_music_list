/**
 * 
 */
package com.android.guide.expandablelistview;

import java.util.ArrayList;

import android.R.bool;

public class MusicAlbum {
	private String albumId;
	private String albumName;
	private boolean isCheck;
	public ArrayList<MusicFile> childrens;
	public MusicAlbum(String groupId, String groupName,
			ArrayList<MusicFile> childrens) {
		super();
		this.albumId = groupId;
		this.albumName = groupName;
		this.childrens = childrens;
	}
	public String getGroupId() {
		return albumId;
	}
	public void setAlbumId(String groupId) {
		this.albumId = groupId;
	}
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String groupName) {
		this.albumName = groupName;
	}
	public ArrayList<MusicFile> getChildrens() {
		return childrens;
	}
	public void setChildrens(ArrayList<MusicFile> childrens) {
		this.childrens = childrens;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	
}
