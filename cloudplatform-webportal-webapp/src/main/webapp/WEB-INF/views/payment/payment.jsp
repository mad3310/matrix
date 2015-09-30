<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="renderer" content="webkit">
	<title>乐视云计算-最专业的VaaS云平台</title>
	<meta name="Keywords" content="乐视云计算，云计算，VaaS，视频存储，免费空间，企业视频，云主机，开放平台">
	<link rel="shortcut icon" href="/static/staticPage/img/favicon.ico">
	<link rel="stylesheet" href="/static/staticPage/css/common.css">
	<link rel="stylesheet" href="/static/staticPage/css/style.css">
</head>
<body>
	<input id="userId" type="text" class="hide" value="${sessionScope.userSession.userId}">
	<input id="orderNum" type="text" class="hide" value="${orderNum}">
	<div class="main-body">
		<div class="order">
			<div class="order-title">订单支付</div>
			<div class="order-pay">
				<div class="pay-item">账号名称：<span class="item-desc account">letvcloud@letv.com</span></div>
				<div class="pay-item">
					<span>可用余额：</span><span class="item-desc remain">¥100</span>
					<button class="btn btn-le-red item-recharge hide">充值</buttom>
				</div>
				<div class="pay-item">本次需支付：<span class="text-red item-desc" id="orderpay" style="padding-left:15px;">¥1000</span></div>
				<div class="pay-item">
					<span>支付方式：</span>
					<button class="payoption active"><img src="/static/staticPage/img/zhifubao.png"></button>
					<!-- <button class="payoption"><img src="/static/staticPage/img/wechat.png"></button> -->
				</div>
				<div class="pay-item">
					<button class="btn btn-le-blue item-pay" id="pay">确认支付</button>
				</div>
			</div>
		</div>
		<div class="order">
			<div class="order-title">
				<span>订单详情</span>
				<span class="title-rollup">
					<span class="rollup-text">收起</span>
					<span class="iconfont icon-arrow01"></span>
					<span class="clearfix"></span>
				</span>
				<div class="clearfix"></div>
			</div>
			<div class="price-table ordertable opacity clearfix">
				<table class="col-md-12">
					<thead>
						<tr>
							<th width="12.5%">订单号</th>
							<th width="37.5%">配置</th>
							<th width="12.5%">数量</th>
							<!-- <th width="12.5%">单价</th> -->
							<th width="12.5%">使用时长</th>
							<th width="12.5%">支付费用</th>
						</tr>
					</thead>
					<tbody id="order-tbody"></tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="modal-container hide">
		<div class="modal">
			<div class="modal-top">
				<div class="modal-title"><span>充值完成前请不要关闭此窗口</span>
				<span class="iconfont icon-add"></span>
				<span class="clearfix"></span>
				</div>
			</div>
			<div class="modal-content">
				<div>请在新开充值页面上完成付款后，再继续支付。</div>
				<div class="buttons clearfix text-center">
					<div class="col-md-offset-2 col-md-4"><button class="btn btn-le-blue paybtn">充值完成</buttom></div>
					<div class="col-md-4"><button class="btn btn-le-red paybtn">充值遇到问题</buttom></div>
				</div>
			</div>
		</div>
	</div>
</body>
<script src="/static/javascripts/jquery-1.11.3.js"></script>
<script src="${ctx}/static/page-js/payment/payment.js"></script>
<script>
rollup();//展开&收起
userInfo();//用户名&余额
goPay();//支付
orderDetail();//订单详情
$(window).resize(function(event) {
	var width=document.body.scrollWidth;
    var height=document.body.scrollHeight;
    $('.modal-container').css({
        width:width,
        height:height
    })
});
</script>
</html>