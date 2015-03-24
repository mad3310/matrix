<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page-content-area">
	<div class="row">
		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<div class="widget-header">
				<h5 class="widget-title">任务单元列表</h5>
				<div class="widget-toolbar no-border">
					<button id="create_job_stream" class="btn btn-white btn-primary btn-xs" data-toggle="modal" data-target="#create-job-unit-modal">
						<i class="ace-icont fa fa-plus"></i>
						 创建单元
					</button>
				</div>
			</div>
			<div class="widget-body">
				<div class="widget-main no-padding">
					<table class="table table-bordered" id="db_detail_table" >
						<thead>
							<tr>
								<th class="center">
									<label class="position-relative">
										<input type="checkbox" id="titleCheckbox" class="ace" />
										<span class="lbl"></span>
									</label>
								</th>
								<th>任务单元名称</th>
								<th>业务类型</th>
								<th>功能描述</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody id="tby">
							<tr><td class="center"><label class="position-relative"><input type="checkbox" class="ace"><span class="lbl"></span></label></td>
								<td>初始化cluster</td>
								<td>RDS</td>
								<td>为RDS准备集群</td>
								<td>管理|详情</td>
							</tr>
							<tr><td class="center"><label class="position-relative"><input type="checkbox" class="ace"><span class="lbl"></span></label></td>
								<td>启动mysqlServer</td>
								<td>RDS</td>
								<td>为RDS准备server</td>
								<td>管理|详情</td>
							</tr>
							<tr><td class="center"><label class="position-relative"><input type="checkbox" class="ace"><span class="lbl"></span></label></td>
								<td>创建数据库</td>
								<td>RDS</td>
								<td>为RDS用户创建默认账户</td>
								<td>管理|详情</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div id="pageControlBar">
			<input type="hidden" id="totalPage_input" />
			<ul class="pager">
				<li><a href="javascript:void(0);" id="firstPage">&laquo首页</a></li>
				<li><a href="javascript:void(0);" id="prevPage">上一页</a></li>
				<li><a href="javascript:void(0);" id="nextPage">下一页</a></li>
				<li><a href="javascript:void(0);" id="lastPage">末页&raquo</a></li>
	
				<li><a>共<lable id="totalPage"></lable>页</a>
				</li>
				<li><a>第<lable id="currentPage"></lable>页</a>
				</li>
				<li><a>共<lable id="totalRows"></lable>条记录</a>
				</li>
			</ul>
		</div>
		<div class="modal fade" id="create-job-unit-modal" tabindex="-1" aria-labelledby="myModalLabel" style="margin-top:157px">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
           				<button type="button" class="close" data-dismiss="modal">
           					<span aria-hidden="true"><i class="ace-icon fa fa-times-circle"></i></span>
           					<span class="sr-only">关闭</span>
           				</button>
           				<h4 class="modal-title">创建任务单元 </h4>
            		</div>
					<form id="create-hcluster-form" name="create-hcluster-form" class="form-horizontal" role="form">
						<div class="modal-body">            				
            				<div class="form-group">
								<label class="col-sm-4 control-label" for="hcluster_name">任务单元名称</label>
								<div class="col-sm-6">
									<input class="form-control" name="hclusterNameAlias" id="hclusterNameAlias" type="text" />
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="任务单元名称应能概括此任务的信息，可用汉字!" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
							<div class="form-group">
								<label class="col-sm-4 control-label" for="hcluster_name">业务类型</label>
								<div class="col-sm-6">
									<select class="form-control" name="jobType" id="jobType">
										<option value="">RDS</option>
										<option value="">SLB</option>
										<option value="">GCE</option>
										<option value="">消息中间件</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-4 control-label" for="descn">描述</label>
								<div class="col-sm-6">
									<textarea id="iplist-textarea" name="iplist-textarea" class="form-control" rows="4"></textarea>
								</div>
								<label class="control-label">
									<a name="popoverHelp" rel="popover" data-container="body" data-toggle="popover" data-placement="right" data-trigger='hover' data-content="输入字母、数字或_,最多100字符!" style="cursor:pointer; text-decoration:none;">
										<i class="ace-icon fa fa-question-circle blue bigger-125"></i>
									</a>
								</label>
							</div>
            			</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-sm btn-default" data-dismiss="modal">关闭</button>
						<button id="create-hcluster-botton" type="button" class="btn btn-sm disabled btn-primary" onclick="">创建</button>
					</div>
				</form>
				</div>
			</div>
		</div>
	</div>
</div>