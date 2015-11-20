package com.letv.portal.controller.cloudgce;

import com.letv.common.result.ResultObject;
import com.letv.portal.proxy.IGceProxy;
import com.letv.portal.service.adminoplog.ClassAoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@ClassAoLog(module="后台任务",ignore=true)
@Controller("gceCronJobs")
@RequestMapping("/cronJobs/gce")
public class CronJobsController {

    @Autowired
    private IGceProxy gceProxy;

    @RequestMapping(value="/cluster/status",method= RequestMethod.GET)
	public @ResponseBody ResultObject checkClusterStatus(ResultObject obj) {
        this.gceProxy.checkStatus();
		return obj;
	}

}
