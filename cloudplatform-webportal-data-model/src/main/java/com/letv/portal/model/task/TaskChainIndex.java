package com.letv.portal.model.task;

import java.util.Date;

import com.letv.common.model.BaseModel;

public class TaskChainIndex extends BaseModel {

	private static final long serialVersionUID = -3034786226461925814L;
	
	public Long taskId;
	private TaskExecuteStatus status;
	
	private Date startTime;
	private Date endTime;
	
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public TaskExecuteStatus getStatus() {
		return status;
	}
	public void setStatus(TaskExecuteStatus status) {
		this.status = status;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
}
