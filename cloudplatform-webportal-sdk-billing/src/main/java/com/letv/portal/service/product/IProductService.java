package com.letv.portal.service.product;

import java.util.Map;

import com.letv.portal.model.product.Product;
import com.letv.portal.service.IBaseService;

/**Program Name: IBaseElementService <br>
 * Description:  产品<br>
 * @author name: liuhao1 <br>
 * Written Date: 2015年7月30日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public interface IProductService extends IBaseService<Product> {
	Map<String, Object> queryProductDetailById(Long id);
	Double queryProductPrice(Long id, Map<String,Object> map);
}
