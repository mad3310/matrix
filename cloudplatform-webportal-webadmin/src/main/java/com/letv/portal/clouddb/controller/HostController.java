	package com.letv.portal.clouddb.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.portal.constant.Constant;
import com.letv.portal.model.HostModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.service.IHostService;
import com.letv.portal.view.DbAuditView;
import com.letv.portal.view.HostView;
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping("/host")
public class HostController {
	
	@Resource
	private IHostService hostService;
	
	private final static Logger logger = LoggerFactory.getLogger(HostController.class);
	/**
	 * Methods Name: toList <br>
	 * Description: <br>
	 * @author name: wujun
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/",method=RequestMethod.GET)
	public String toList(HttpServletRequest request){
		return "/clouddb/mgr_host_list";
	}
	/**
	 * Methods Name: list <br>
	 * Description: <br>
	 * @author name: wujun
	 * @param currentPage
	 * @param recordsPerPage
	 * @param dbName
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{currentPage}/{recordsPerPage}/{hostName}",method=RequestMethod.GET)  
	public @ResponseBody ResultObject list(@PathVariable int currentPage,@PathVariable int recordsPerPage,@PathVariable String hostName,HttpServletRequest request) {
		Page page = new Page();
		page.setCurrentPage(currentPage);
		page.setRecordsPerPage(recordsPerPage);
	
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("hostName", hostName);
		
		ResultObject obj = new ResultObject();
		obj.setData(this.hostService.findPagebyParams(params, page));
		return obj;
	}
	
	
	/**Methods Name: list <br>
	 * Description: 根据查询条件及分页信息获取分页数据  http://localhost:8080/host/list<br>
	 * @author name: liuhao1
	 * @param request
	 * @return
	 */
	@RequestMapping(value ="/{hostName}",method=RequestMethod.GET)   
	
	public @ResponseBody ResultObject list(@PathVariable String hostName,HttpServletRequest request) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("hostName", hostName);
		ResultObject obj = new ResultObject();
		obj.setData(this.hostService.selectByMap(map));
		return obj;
	}
	/**
	 * Methods Name: save <br>
	 * Description: 保存host信息
	 * @author name: wujun
	 * @param dav
	 * @param request
	 */
	@RequestMapping(value="/",method=RequestMethod.POST)   
	public void saveHost(HostModel hostModel,HttpServletRequest request) {
		try {
			this.hostService.insert(hostModel);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}	
	}	
   /**
    * Methods Name: delteHostByID <br>
    * Description: 删除host信息通过hostID
    * @author name: wujun
    * @param hv
    * @param request
    */
   @RequestMapping(value="/{id}",method=RequestMethod.DELETE)   
   public void delteHostByID(@PathVariable String id,HttpServletRequest request) {
	HostModel hostModel = new HostModel();
	try {
		hostModel.setId(id);
		this.hostService.delete(hostModel);
	} catch (Exception e) {
		logger.debug(e.getMessage());
	}	
   }	
  
  /**
   *  Methods Name: updateHost <br>
   * Description: 修改host的相关信息
   * @author name: wujun
   * @param hv
   * @param request
   */
  @RequestMapping(value="/{id}/{hostName}",method=RequestMethod.PUT)   
  public void updateHost(@PathVariable String id,@PathVariable String hostName,HttpServletRequest request) {
	  HostModel hostModel = new HostModel();
	  hostModel.setId(id);
	  hostModel.setHostName(hostName); 
	try {
		this.hostService.update(hostModel);
	} catch (Exception e) {
		logger.debug(e.getMessage());
	}	
}	
}
