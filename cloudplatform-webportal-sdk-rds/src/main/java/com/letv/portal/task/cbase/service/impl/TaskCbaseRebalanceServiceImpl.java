package com.letv.portal.task.cbase.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.portal.model.cbase.CbaseClusterModel;
import com.letv.portal.model.cbase.CbaseContainerModel;
import com.letv.portal.model.task.TaskResult;
import com.letv.portal.model.task.service.IBaseTaskService;
import com.letv.portal.python.service.ICbasePythonService;

@Service("taskCbaseRebalanceService")
public class TaskCbaseRebalanceServiceImpl extends BaseTask4CbaseServiceImpl
		implements IBaseTaskService {

	@Autowired
	private ICbasePythonService cbasePythonService;

	private final static Logger logger = LoggerFactory
			.getLogger(TaskCbaseRebalanceServiceImpl.class);

	@Override
	public TaskResult execute(Map<String, Object> params) throws Exception {
		TaskResult tr = super.execute(params);
		if (!tr.isSuccess())
			return tr;

		// 执行业务
		List<CbaseContainerModel> containers = super.getContainers(params);
		String nodeIp1 = containers.get(0).getHostIp();
		String nodeIp2 = containers.get(1).getHostIp();
		String nodeIp3 = containers.get(2).getHostIp();
		StringBuffer knownNodes = new StringBuffer();
		knownNodes.append("ns_1@").append(nodeIp1).append(",").append("ns_1@")
				.append(nodeIp2).append(",").append("ns_1@").append(nodeIp3);

		CbaseClusterModel cluster = super.getCbaseCluster(params);

		String result = this.cbasePythonService.rebalanceCluster(nodeIp1,
				super.getCbaseManagePort(), knownNodes.toString(),
				cluster.getAdminUser(), cluster.getAdminPassword());
		tr = analyzeRestServiceResult(result);

		tr.setParams(params);
		return tr;
	}
}