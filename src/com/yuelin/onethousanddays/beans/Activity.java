package com.yuelin.onethousanddays.beans;

import java.util.Date;

public class Activity {
	private int id;
	private int categoryId;
	private long day;
	private Date date;
	private int dayOfWeek;
	private double hours;
	private String description;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public long getDay() {
		return day;
	}
	public void setDay(long day) {
		this.day = day;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getDayOfWeek(){
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek){
		this.dayOfWeek = dayOfWeek;
	}
	public double getHours() {
		return hours;
	}
	public void setHours(double hours) {
		this.hours = hours;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
