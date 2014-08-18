<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/page/share/taglib.jsp" %>
<html>
<head>
<title>产品列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/shop/css/vip.css" type="text/css">
<script language="JavaScript">
<!--
	//到指定的分页页面
	function topage(page){
		var form = document.forms[0];
		form.page.value=page;
		form.submit();
	}
	
	function actionEvent(methodname){
		var form = document.forms[0];
		if(validateIsSelect(form.all, form.productids)){
			form.action='/shop/control/product/'+methodname+".do";
			form.submit();
		}else{
			alert("请选择要操作的记录");
		}
	}
	
	function allselect(allobj,items){
	    var state = allobj.checked;
	    if(items.length){
	    	for(var i=0;i<items.length;i++){
	    		if(!items[i].disabled) items[i].checked=state;
	    	}
	    }else{
	    	if(!items[i].disabled) items.checked=state;
	    }
	}
	/*
	 * 判断是否选择了记录
     */
	function validateIsSelect(allobj,items){
	    var state = allobj.checked;
	    if(items.length){
	    	for(var i=0;i<items.length;i++){
	    		if(items[i].checked) return true;
	    	}
	    }else{
	    	if(items.checked) return true;
	    }
	    return false;
	}
//-->
</script>
<SCRIPT language=JavaScript src="/shop/js/FoshanRen.js"></SCRIPT>
</head>

<body bgcolor="#FFFFFF" text="#000000" marginwidth="0" marginheight="0">
<form action="/shop/control/product/list" method="post">
<input type="hidden" name="method" value="">
<input type="hidden" name="page"/>
<input type="hidden" name="query"/>
<input type="hidden" name="name"/>
<input type="hidden" name="typeid"/>
<input type="hidden" name="startsellprice"/>
<input type="hidden" name="endsellprice"/>
<input type="hidden" name="startbaseprice"/>
<input type="hidden" name="endbaseprice"/>
<input type="hidden" name="code"/>
<input type="hidden" name="brandid"/>

  <table width="98%" border="0" cellspacing="1" cellpadding="2" align="center">
    <tr ><td colspan="10"  bgcolor="6f8ac4" align="right">
    	<%@ include file="/WEB-INF/page/share/fenye.jsp" %>
   </td></tr>
    <tr>
      <td width="7%" bgcolor="6f8ac4"> <div align="center"><font color="#FFFFFF">产品ID</font></div></td>
      <td width="8%" bgcolor="6f8ac4"> <div align="center"><font color="#FFFFFF">货号</font></div></td>
      <td width="5%" nowrap bgcolor="6f8ac4"> <div align="center"><font color="#FFFFFF">修改</font></div></td>
      <td width="30%" bgcolor="6f8ac4"> <div align="center"><font color="#FFFFFF">产品名称</font></div></td>
	  <td width="12%" nowrap bgcolor="6f8ac4"> <div align="center"><font color="#FFFFFF">所属分类</font></div></td>
	  <td width="7%" bgcolor="6f8ac4"><div align="center"><font color="#FFFFFF">底价</font></div></td>
	  <td width="7%" bgcolor="6f8ac4"><div align="center"><font color="#FFFFFF">销售价</font></div></td>
	  <td width="6%" bgcolor="6f8ac4"><div align="center"><font color="#FFFFFF"></font></div></td>
	  <td width="6%" bgcolor="6f8ac4"><div align="center"><font color="#FFFFFF">推荐</font></div></td>
	  <td width="12%" bgcolor="6f8ac4"><div align="center"><font color="#FFFFFF"></font></div></td>
    </tr>
<!---------------------------LOOP START------------------------------>
<c:forEach items="${pageView.records}" var="entry">
    <tr>
      <td bgcolor="f5f5f5"> <div align="center"><INPUT TYPE="checkbox" NAME="productids" value="${entry.id}">${entry.id }</div></td>
      <td bgcolor="f5f5f5"> <div align="center">${entry.code }</div></td>
      <td bgcolor="f5f5f5"> 
        <itcast:permission privilege="update" module="product">
         <div align="center"><a href="/shop/control/product/editUI.do?productid=${entry.id}"><img src="/shop/images/edit.gif" width="15" height="16" border="0"></a></div>
        </itcast:permission></td>
	  <td bgcolor="f5f5f5"> <div align="center">${entry.name }</div></td>
	  <td bgcolor="f5f5f5"> <div align="center">${entry.type.name }</div></td>
	  <td bgcolor="f5f5f5"> <div align="center">${entry.baseprice }</div></td>
	  <td bgcolor="f5f5f5"> <div align="center">${entry.sellprice }</div></td>
	  <td bgcolor="f5f5f5" align="center"><c:if test="${entry.visible}">在售</c:if><c:if test="${!entry.visible}">停售</c:if></td>
	  <td bgcolor="f5f5f5" align="center"><c:if test="${entry.commend}">推荐</c:if><c:if test="${!entry.commend}">--</c:if></td>
	   <td bgcolor="f5f5f5"> 
	    <itcast:permission privilege="view" module="product">
	      <div align="center"><a href="/shop/control/product/style/list.do?productid=${entry.id}">产品图片管理</a></div>
	    </itcast:permission></td>
	</tr>
</c:forEach>
    <!----------------------LOOP END------------------------------->
    <tr> 
      <td bgcolor="f5f5f5" colspan="10" align="center"><table width="100%" border="0" cellspacing="1" cellpadding="3">
          <tr> 
            <td width="15%">
               <input type="checkbox" name="all" <c:if test="${fn:length(pageView.records)<1}">disabled="disabled"</c:if> onclick="javascript:allselect(this, this.form.productids)">全选</td>
            <td width="85%">
              <itcast:permission privilege="insert" module="product">
               <input type="button" class="frm_btn" onClick="javascript:window.location.href='/shop/control/product/addUI.do'" value="添加产品"> &nbsp;&nbsp;
              </itcast:permission>
              <itcast:permission privilege="view" module="product">
			   <input name="query" type="button" class="frm_btn" id="query" onClick="javascript:window.location.href='/shop/control/product/queryUI.do'" value=" 查 询 "> &nbsp;&nbsp;
			  </itcast:permission>
              <itcast:permission privilege="visible" module="product">
               <input name="visible" type="button"  <c:if test="${fn:length(pageView.records)<1}">disabled="disabled"</c:if> class="frm_btn" onClick="javascript:actionEvent('enable')" value=" 上 架 "> &nbsp;&nbsp;
               <input name="disable" type="button" class="frm_btn" <c:if test="${fn:length(pageView.records)<1}">disabled="disabled"</c:if> onClick="javascript:actionEvent('disable')" value=" 下 架 "> &nbsp;&nbsp;
              </itcast:permission>
              <itcast:permission privilege="commend" module="product">
               <input name="commend" type="button" class="frm_btn" <c:if test="${fn:length(pageView.records)<1}">disabled="disabled"</c:if> onClick="javascript:actionEvent('recommend')" value=" 推 荐 "> &nbsp;&nbsp;
               <input name="uncommend" type="button" class="frm_btn" <c:if test="${fn:length(pageView.records)<1}">disabled="disabled"</c:if> onClick="javascript:actionEvent('unrecommend')" value=" 不推荐 "> &nbsp;&nbsp;
              </itcast:permission>
            </td>
          </tr>
        </table></td>
    </tr>
  </table>
</form>
</body>
</html>