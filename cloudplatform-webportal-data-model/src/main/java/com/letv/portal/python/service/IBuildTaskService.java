package com.letv.portal.python.service;

import com.letv.portal.model.MclusterModel;


/**Program Name: IBuildTaskService <br>
 * Description:  mcluster db等创建调度<br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年9月14日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IBuildTaskService { 
	
	/**Methods Name: buildMcluster <br>
	 * Description: 创建mcluster<br>
	 * @author name: liuhao1
	 * @param mclusterModel
	 */
	public void buildMcluster(MclusterModel mclusterModel);

	/**Methods Name: buildUser <br>
	 * Description: 创建用户<br>
	 * @author name: liuhao1
	 * @param ids
	 */
	public void buildUser(String ids);
}