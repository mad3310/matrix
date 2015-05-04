package com.letv.portal.service.gce.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.portal.dao.gce.IGceServerDao;
import com.letv.portal.enumeration.GceStatus;
import com.letv.portal.model.gce.GceCluster;
import com.letv.portal.model.gce.GceContainer;
import com.letv.portal.model.gce.GceImage;
import com.letv.portal.model.gce.GceServer;
import com.letv.portal.service.gce.IGceClusterService;
import com.letv.portal.service.gce.IGceContainerService;
import com.letv.portal.service.gce.IGceImageService;
import com.letv.portal.service.gce.IGceServerService;
import com.letv.portal.service.impl.BaseServiceImpl;

@Service("gceServerService")
public class GceServerServiceImpl extends BaseServiceImpl<GceServer> implements IGceServerService{
	
	private final static Logger logger = LoggerFactory.getLogger(GceServerServiceImpl.class);
	
	@Resource
	private IGceServerDao gceServerDao;
	@Autowired
	private IGceClusterService gceClusterService;
	@Autowired
	private IGceContainerService gceContainerService;
	@Autowired
	private IGceImageService gceImageService;
	@Value("${gce.code}")
	private String GCE_CODE;
	
	public GceServerServiceImpl() {
		super(GceServer.class);
	}

	@Override
	public IBaseDao<GceServer> getDao() {
		return this.gceServerDao;
	}
	
	@Override
	public void insert(GceServer t) {
		if(t == null)
			throw new ValidateException("参数不合法");
		t.setStatus(GceStatus.NORMAL.getValue());
		super.insert(t);
	}

	@Override
	public Map<String,Object> save(GceServer gceServer) {
		gceServer.setStatus(GceStatus.BUILDDING.getValue());
		
		StringBuffer clusterName = new StringBuffer();
		clusterName.append(gceServer.getCreateUser()).append("_").append(GCE_CODE).append("_").append(gceServer.getGceName());
		
		/*function 验证clusterName是否存在*/
		Boolean isExist= this.gceClusterService.isExistByName(clusterName.toString());
		int i = 0;
		while(!isExist) {
			isExist= this.gceClusterService.isExistByName(clusterName.toString() +(i+1));
			i++;
		}
		if(i>0)
			clusterName.append(i);
		
		GceCluster gceCluster = new GceCluster();
		gceCluster.setHclusterId(gceServer.getHclusterId());
		gceCluster.setClusterName(clusterName.toString());
		gceCluster.setStatus(GceStatus.BUILDDING.getValue());
		gceCluster.setCreateUser(gceServer.getCreateUser());
		gceCluster.setAdminUser("root");
		gceCluster.setAdminPassword(clusterName.toString());
		
		this.gceClusterService.insert(gceCluster);
		
		gceServer.setGceClusterId(gceCluster.getId());
		
		//处理gce image.
		String imageId = gceServer.getGceImageName();
		if(!StringUtils.isEmpty(imageId)) {
			GceImage image = this.gceImageService.selectById(Long.parseLong(imageId));
			gceServer.setGceImageName(image!=null?image.getUrl():"");
		}
		
		this.gceServerDao.insert(gceServer);
		
		Map<String,Object> params = new HashMap<String,Object>();
    	params.put("gceClusterId", gceCluster.getId());
    	params.put("gceId", gceServer.getId());
    	params.put("serviceName", gceServer.getGceName());
    	params.put("clusterName", gceCluster.getClusterName());
		return params;
	}
	
	public <K, V> Page selectPageByParams(Page page, Map<K, V> params){
		page = super.selectPageByParams(page, params);
		List<GceServer> gceServers = (List<GceServer>) page.getData();
		
		for(GceServer gceServer : gceServers){
			List<GceContainer> gceContainers = this.gceContainerService.selectByGceClusterId(gceServer.getGceClusterId());
			gceServer.setGceContainers(gceContainers);
		}
		page.setData(gceServers);
		return page;
	}
	
	public GceServer selectById(Long id){
		GceServer gceServer = this.gceServerDao.selectById(id);
		List<GceContainer> gceContainers = this.gceContainerService.selectByGceClusterId(gceServer.getGceClusterId());
		gceServer.setGceContainers(gceContainers);
		return gceServer;
	}
}
