<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/page/share/taglib.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>订单查看</title>
<link rel="stylesheet" href="/shop/css/vip.css" type="text/css"/>
<script language="javascript" src="/shop/js/FoshanRen.js"></script>
<style type="text/css">
<!--
body {font-size: 12px;line-height:16px}
a:link { color: #3300FF; 
     text-decoration: underline;  }    
    
a {color: #3300FF; 
     text-decoration: underline; }
     
a:hover { color: #FF6600; 
           text-decoration: underline; }

A.subnav:link {
	FONT-SIZE: 12px; COLOR: #330000; LINE-HEIGHT: 155%; TEXT-DECORATION: none
}
A.subnav:visited {
	FONT-SIZE: 12px; COLOR: #330000; LINE-HEIGHT: 155%; TEXT-DECORATION: none
}
A.subnav:active {
	FONT-SIZE: 12px; COLOR: #330000; LINE-HEIGHT: 155%; TEXT-DECORATION: none
}
A.subnav:hover {
	FONT-SIZE: 12px; COLOR: #330000; LINE-HEIGHT: 155%; TEXT-DECORATION: underline
}
-->
</style>

<script language="javascript">
<!--
function ActionEvent(methodname, orderid){
	window.location.href = '/shop/control/order/'+ methodname+ '.do?orderid='+ orderid;
}
function deleteOrderItem(orderItemid, orderid){
	if(confirm('\n您确认删除该项吗?')){
		window.location.href ="/shop/control/order/deleteOrderItem.do?orderitemid="+ orderItemid+"&orderid="+orderid;
	}
}
//-->
</script>
</head>
<body><br/>
<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0" bgcolor="#333333">
  <tr>
    <td>
    <table width="100%" height="25" border="0" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
      <tr>
        <td width="59%"><strong>订单号:</strong>${order.orderid } <font color="red">(${order.state.name })</font></td>
        <td width="41%" align="right"><strong>订购时间:</strong>${order.createDate }</td>
      </tr>
    </table>
    <table width="100%" border="0" align="center" cellpadding="3" cellspacing="2">
        <tr>
          <td colspan="4" bgcolor="#FFFFFF"><strong>订购者信息 </strong> 
          <itcast:permission privilege="modifyContactInfo" module="order">
            <a href="/shop/control/order/modifyContactInfoUI.do?contactid=${order.orderContactInfo.contactid }">修改</a>
          </itcast:permission></td>
          <td align="center" bgcolor="#FFFFFF">支付方式</td>
          <td colspan="2" bgcolor="#FFFFFF">${order.paymentWay.name }  
          <itcast:permission privilege="modifyPaymentWay" module="order">
            <a href="/shop/control/order/modifyPaymentWayUI.do?orderid=${order.orderid }">修改</a>
          </itcast:permission></td>
        </tr>
        <tr>
          <td width="13%" align="center" bgcolor="#FFFFFF">姓名</td>
          <td width="24%" bgcolor="#FFFFFF">${order.orderContactInfo.buyerName }（${order.orderContactInfo.gender.chinese}）</td>
          <td width="12%" align="center" bgcolor="#FFFFFF">联系电话</td>
          <td width="18%" bgcolor="#FFFFFF">${order.orderContactInfo.tel }</td>
          <td width="12%" align="center" bgcolor="#FFFFFF">联系手机</td>
          <td colspan="2" bgcolor="#FFFFFF">${order.orderContactInfo.mobile }</td>
        </tr>
        <tr>
          <td align="center" bgcolor="#FFFFFF">地址</td>
          <td colspan="3" bgcolor="#FFFFFF">${order.orderContactInfo.address }</td>
          <td align="center" bgcolor="#FFFFFF">邮编</td>
          <td colspan="2" bgcolor="#FFFFFF">${order.orderContactInfo.postalcode }</td>
        </tr>
        <tr>
          <td align="center" bgcolor="#FFFFFF">其他要求</td>
          <td colspan="6" bgcolor="#FFFFFF">${order.note}</td>
        </tr>
        <tr>
          <td colspan="4" bgcolor="#FFFFFF"><strong>收货人信息</strong>
          <itcast:permission privilege="modifyDeliverInfo" module="order">
           <a href="/shop/control/order/modifyDeliverInfoUI.do?deliverid=${order.orderDeliverInfo.deliverid }">修改</a>
           </itcast:permission></td>
          <td align="center" bgcolor="#FFFFFF">送货方式</td>
          <td colspan="2" bgcolor="#FFFFFF">${order.orderDeliverInfo.deliverWay.name }
          <itcast:permission privilege="modifyDeliverWay" module="order">
            <a href="/shop/control/order/modifyDeliverWayUI.do?orderid=${order.orderid }">修改</a>
          </itcast:permission></td>
        </tr>
        <tr>
          <td align="center" bgcolor="#FFFFFF">姓名</td>
          <td bgcolor="#FFFFFF">${order.orderDeliverInfo.recipients }</td>
          <td align="center" bgcolor="#FFFFFF">联系电话</td>
          <td bgcolor="#FFFFFF">${order.orderDeliverInfo.tel }</td>
          <td align="center" bgcolor="#FFFFFF">联系手机</td>
          <td colspan="2" bgcolor="#FFFFFF">${order.orderDeliverInfo.mobile }</td>
        </tr>
        <tr>
          <td align="center" bgcolor="#FFFFFF">地址</td>
          <td colspan="3" bgcolor="#FFFFFF">${order.orderDeliverInfo.address }</td>
          <td align="center" bgcolor="#FFFFFF">邮编</td>
          <td colspan="2" bgcolor="#FFFFFF">${order.orderDeliverInfo.postalcode }</td>
        </tr>
        <tr>
          <td align="center" bgcolor="#FFFFFF">时间要求</td>
          <td colspan="6" bgcolor="#FFFFFF">${order.orderDeliverInfo.requirement }</td>
        </tr>
        <tr>
          <td colspan="4" bgcolor="#FFFFFF"><strong>订购的商品</strong></td>
          <td align="center" bgcolor="#FFFFFF"></td>
          <td colspan="2" bgcolor="#FFFFFF">
		  </td>
        </tr>
        <tr>
          <td align="center" bgcolor="#FFFFFF">商品编号</td>
          <td colspan="3" align="center" bgcolor="#FFFFFF">商品名称</td>
          <td align="center" bgcolor="#FFFFFF">单价</td>
          <td width="16%" align="center" bgcolor="#FFFFFF">数量</td>
          <td width="5%" align="center" bgcolor="#FFFFFF">&nbsp;</td>
        </tr>
<c:forEach items="${order.items}" var="item">
        <tr>
          <td align="center" bgcolor="#FFFFFF">${item.productid }</td>
          <td colspan="3" align="center" bgcolor="#FFFFFF">${item.productName } <font color="red">[${item.styleName }]</font></td>
          <td align="center" bgcolor="#FFFFFF">￥${item.productPrice }</td>
          <td align="center" bgcolor="#FFFFFF">${item.amount } 
           <itcast:permission privilege="modifyProductAmount" module="order">
             <a href="/shop/control/order/modifyProductAmountUI.do?orderitemid=${item.itemid}">修改</a>
            </itcast:permission></td>
          <td align="center" bgcolor="#FFFFFF">
           <itcast:permission privilege="deleteOrderItem" module="order">
             <a href="JavaScript:deleteOrderItem('${item.itemid }','${order.orderid }')">删除</a>
          </itcast:permission></td>
        </tr>
</c:forEach>
        <tr>
          <td colspan="7" align="right" bgcolor="#FFFFFF"><p>商品合计：￥${order.productTotalPrice }元&nbsp;&nbsp;配送费：￥${order.deliverFee }元 
            <itcast:permission privilege="modifyDeliverFee" module="order">
              <a href="/shop/control/order/modifyDeliverFeeUI.do?orderid=${order.orderid}">修改</a>&nbsp;&nbsp;订单合计：￥${order.totalPrice }元<br />
            </itcast:permission>
			&nbsp;&nbsp;<strong>应付金额：</strong>￥${order.payablefee }元</p></td>
        </tr>
      </table></td>
  </tr>
</table>
<br />
<table width="90%" border="0" align="center" cellpadding="0" cellspacing="2">
  <tr>
    <td width="15%" bgcolor="#FFFFFF">
    <c:if test="${order.state!='RECEIVED' && order.state!='CANCEL'}">
     <itcast:permission privilege="cancelOrder" module="order">
	 <input type="button" value="取消订单" class="frm_btn" onclick="JavaScript:ActionEvent('cancelOrder', '${order.orderid }')"/>&nbsp;
	 </itcast:permission>
    </c:if>
    <c:if test="${order.state=='WAITCONFIRM'}">
      <itcast:permission privilege="confirmOrder" module="order">
      <input type="button" value="审核通过" class="frm_btn" onclick="JavaScript:ActionEvent('confirmOrder', '${order.orderid }')"/>&nbsp;	
      </itcast:permission>
    </c:if>
    <c:if test="${order.state=='WAITPAYMENT' || (order.state=='DELIVERED' && order.paymentWay=='COD')}">
    <itcast:permission privilege="confirmPayment" module="order">
      <input type="button" value="财务确认已付款" class="frm_btn" onclick="JavaScript:ActionEvent('confirmPayment', '${order.orderid }')"/>&nbsp;	
    </itcast:permission>
    </c:if>
    <c:if test="${order.state=='ADMEASUREPRODUCT'}">
    <itcast:permission privilege="turnWaitdeliver" module="order">
       <input type="button" value="等待发货" class="frm_btn" onclick="JavaScript:ActionEvent('turnWaitdeliver', '${order.orderid }')"/>&nbsp;	
    </itcast:permission>
    </c:if>
    <c:if test="${order.state=='WAITDELIVER'}">
    <itcast:permission privilege="turnDelivered" module="order">
     <input type="button" value="已经发货" class="frm_btn" onclick="JavaScript:ActionEvent('turnDelivered', '${order.orderid }')"/>&nbsp;	
    </itcast:permission>
    </c:if>
    <c:if test="${order.state=='DELIVERED' && order.paymentWay!='COD'}">
    <itcast:permission privilege="turnReceived" module="order">
    <input type="button" value="已经收货" class="frm_btn" onclick="JavaScript:ActionEvent('turnReceived', '${order.orderid }')"/>&nbsp;	
    </itcast:permission>
    </c:if>
    <input type="button" value="打印订单" class="frm_btn" onclick="JavaScript:winOpen('/shop/control/order/printOrder.do?&orderid=${order.orderid }','打印',700,450)"/>&nbsp;
	<input type="button" value="解锁退出" class="frm_btn" onclick="JavaScript:window.location.href='/shop/control/order/employeeUnlockOrder.do?orderid=${order.orderid }'"/></td>
  </tr>
</table>
<br />
<table width="90%" border="0" align="center" cellpadding="2" cellspacing="2">
  <tr>
    <td colspan="2"  bgcolor="6f8ac4"><font color="#FFFFFF">客服留言</font> &nbsp; <input type="button" value="客服留言" onclick="JavaScript:window.location.href='/shop/control/order/addMessageUI.do?orderid=${order.orderid}'"/></td>
  </tr>
  <tr>
    <td width="30%" align="center" bgcolor="#FFFFCC">留言者/时间</td>
    <td width="70%" align="left" bgcolor="#FFFFCC">内容</td>
  </tr>
  <c:forEach items="${order.msgs}" var="msg">
  <tr>
    <td align="center" >${msg.username } / ${msg.createtime}</td>
    <td align="left" >${msg.content }</td>
  </tr>
  <tr><td colspan="2" height="1" bgcolor="#BBC9FF"></td></tr></c:forEach>
</table>
<p>&nbsp;</p>
</body>
</html>