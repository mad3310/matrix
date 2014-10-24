package com.letv.portal.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.letv.common.util.SpringContextUtil;
import com.letv.portal.model.ScheduleJobModel;
import com.letv.portal.proxy.IContainerProxy;
import com.letv.portal.proxy.IMclusterProxy;
import com.letv.portal.proxy.impl.ContainerProxyImpl;

@Component
public class JobFactory implements Job {

	private final static Logger logger = LoggerFactory.getLogger(JobFactory.class);
	
	private IJobExecute jobExecute = (IJobExecute) SpringContextUtil.getBean("jobExecute");
	private IMclusterProxy mclusterProxy = (IMclusterProxy) SpringContextUtil.getBean("mclusterProxy");
	private IContainerProxy containerProxy = (IContainerProxy) SpringContextUtil.getBean("containerProxy");
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		System.out.println("QuartzJobFactory start...");
        ScheduleJobModel scheduleJob = (ScheduleJobModel)context.getMergedJobDataMap().get("scheduleJob");
        String method = scheduleJob.getJobMethod();
        System.out.println("jobName= [" + scheduleJob.getJobName() + "]");
        
        if("test".equals(method)) {
        	this.jobExecute.test();
        }
        if("checkMclusterStatus".equals(method)) {
        	logger.info("check mcluster status");
        	this.mclusterProxy.checkStatus();
        }
        if("checkContainerStatus".equals(method)) {
        	logger.info("check container status");
        	this.containerProxy.checkStatus();
        }
	}

}