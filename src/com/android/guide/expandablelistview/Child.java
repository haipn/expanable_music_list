/**
 * 
 */
package com.android.guide.expandablelistview;


public class Child {
	public String chiledId;
	public String childName;
	public Child(String chiledId, String childName) {
		super();
		this.chiledId = chiledId;
		this.childName = childName;
	}
	public String getChiledId() {
		return chiledId;
	}
	public void setChiledId(String chiledId) {
		this.chiledId = chiledId;
	}
	public String getChildName() {
		return childName;
	}
	public void setChildName(String childName) {
		this.childName = childName;
	}
}
