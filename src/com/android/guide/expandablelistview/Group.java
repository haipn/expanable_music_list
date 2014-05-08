/**
 * 
 */
package com.android.guide.expandablelistview;

import java.util.ArrayList;

public class Group {
	public String groupId;
	public String groupName;
	public ArrayList<Child> childrens;
	public Group(String groupId, String groupName,
			ArrayList<Child> childrens) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.childrens = childrens;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public ArrayList<Child> getChildrens() {
		return childrens;
	}
	public void setChildrens(ArrayList<Child> childrens) {
		this.childrens = childrens;
	}
	
}
