/**
 * Created by yaokuo on 2014/12/14.
 */
define(function(require,exports,module){
    var jQuery = $ = require('jquery');
    require('zclip');
    var common = require('../../common');
    var cn = new common();
   
    var DataHandler = function(){
    };

    module.exports = DataHandler;

    DataHandler.prototype = {
    		cacheInfoHandler : function(data){
                var cacheInfo = data.data;

                function GetNetAddr(containers){ //获取container IP地址,并拼成字符串返回
                    var ips='';
                    for(var i= 0,len=containers.length;i<len;i++){
                        if(cacheInfo.containers[i].type == "mclustervip"){
                            ips = ips+cacheInfo.containers[i].ipAddr+' ';
                        }
                        /*注释掉非VIP的IP
                         else{
                            ips = ips+cacheInfo.containers[i].ipAddr+' ';
                        }*/
                    }
                    return ips;
                }

    			$("#cache_info_cache_id").html(cacheInfo.bucketName);
    			$("#cache_info_cache_name").html(cacheInfo.bucketName);
    			if(cacheInfo.hcluster) {
    				$("#db_info_available_region").html(cacheInfo.hcluster.hclusterNameAlias);
    			}
    			$("#cache_info_net_addr").html(GetNetAddr(cacheInfo.containers));
                $("#cache_info_running_state").html(cn.TranslateStatus(cacheInfo.status));
                $("#cache_info_create_time").html(cn.TransDate('Y-m-d H:i:s',cacheInfo.createTime));
                $("#cache_info_remain_days").html(cn.RemainAvailableTime(cacheInfo.createTime));
                
                
                var cacheconfig = new DataHandler();
                $("#showConfigInfo").click(function(){
                	//cn.GetData("/db/gbConfig/" + $("#cacheId").val(),cacheconfig.getInfo);
                    cn.GetData("/cache/gbConfig/" + $("#cacheId").val(),cacheconfig.getInfo);
                });
        },
        getInfo: function(data){
        	var $tby = $("#cacheconfigTby");
            $tby.find("tr").remove();
        	$('#cacheconfigModal').modal({
                backdrop:false,
                show:true
            });
        	
        	$("#cacheconfigModalLabel").html("配置信息");
        	var data = data.data;
        	
        	/*添加配置信息*/
        	$("#cacheconfigInfo").html("<br/>" + cn.formatJson(JSON.stringify(data)));
        	
        	/*初始化tooltip*/
        	$('#zclipCopy').hover(function(){
    			$("#zeroclipboardTooltip").data("placement", "top").attr("data-original-title", "复制到剪贴板").tooltip("show");
    		},function(){
    			$("#zeroclipboardTooltip").tooltip('hide');
    		})
    		
        	 /*初始化复制功能按钮*/        	
        	$('#zclipCopy').zclip({
                	path: '/static/modules/jquery/zclip/ZeroClipboard.swf',
                	copy: function(){
                		return $('#cacheconfigInfo').text();
                	},
            		afterCopy:function(){
            			$("#zeroclipboardTooltip").attr("data-original-title", "复制成功").tooltip("show");
            		}
            });
        }
    }
});