package com.fivetrue.hangoutbaby.vo;

public class PlaceComment {
	
	private int commentId;

	private String placeId;

	private String comment;
	
	private String imageUrl = null;
	
	private int feeBand;
	
	private String commentAuthor;
	
	private long commentDate;
	
	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getFeeBand() {
		return feeBand;
	}

	public void setFeeBand(int feeBand) {
		this.feeBand = feeBand;
	}

	public String getCommentAuthor() {
		return commentAuthor;
	}

	public void setCommentAuthor(String commentAuthor) {
		this.commentAuthor = commentAuthor;
	}

	public long getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(long commentDate) {
		this.commentDate = commentDate;
	}

	@Override
	public String toString() {
		return "PlaceComment [commentId=" + commentId  + ", comment=" + comment + ", imageUrl="
				+ imageUrl + ", feeBand=" + feeBand + ", commentAuthor=" + commentAuthor + ", commentDate="
				+ commentDate + "]";
	}
}
