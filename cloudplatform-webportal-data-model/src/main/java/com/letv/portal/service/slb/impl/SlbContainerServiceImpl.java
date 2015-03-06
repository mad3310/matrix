package com.letv.portal.service.slb.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.slb.ISlbContainerDao;
import com.letv.portal.model.slb.SlbContainer;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.slb.ISlbContainerService;

@Service("slbContainerService")
public class SlbContainerServiceImpl extends BaseServiceImpl<SlbContainer> implements ISlbContainerService{
	
	private final static Logger logger = LoggerFactory.getLogger(SlbContainerServiceImpl.class);
	
	@Resource
	private ISlbContainerDao slbContainerDao;

	public SlbContainerServiceImpl() {
		super(SlbContainer.class);
	}

	@Override
	public IBaseDao<SlbContainer> getDao() {
		return this.slbContainerDao;
	}

}
