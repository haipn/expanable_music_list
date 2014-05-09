package com.android.guide.expandablelistview;

public class MusicFile {

	private String id;
	private String title;
	private String album;
	private String artist;
	private int duration;
	private String data;
	private String action;
	private String status;
	private String extension;
	private boolean isCheck;

	public MusicFile() {
	}

	public MusicFile(String id, String title, String album, String artist,
			int duration, String data, String action) {
		this.title = title;
		this.album = album;
		this.artist = artist;
		this.duration = duration;
		this.setData(data);
		this.id = id;
		this.action = action;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String t) {
		this.title = t;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String al) {
		this.album = al;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String ar) {
		this.artist = ar;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int dur) {
		this.duration = dur;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {

		this.data = data;
		if (data != null) {
			int i = data.lastIndexOf(".");
			if (i > 0 && data.length() > (i + 2))
				this.extension = data.substring(i);
		}
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

}
