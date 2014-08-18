<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/page/share/taglib.jsp" %>
<html>
<head>
<title>添加类别</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/shop/css/vip.css" type="text/css">
<SCRIPT language=JavaScript src="/shop/js/FoshanRen.js"></SCRIPT>
<script language="JavaScript">
function checkfm(form){
	if (trim(form.name.value)==""){
		alert("类别名称不能为空！");
		form.name.focus();
		return false;
	}
	if (byteLength(form.note.value)>200){
		alert("备注不能大于100字！");
		form.note.focus();
		return false;
	}	
	return true;
}
</script>
</head>
<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form action="/shop/control/product/type/add.do" method="post"  onsubmit="return checkfm(this)">
<input type="hidden" name="parentid" value="${param.parentid}"/>
<br>
  <table width="90%" border="0" cellspacing="2" cellpadding="3" align="center">
    <tr bgcolor="6f8ac4"><td colspan="2"  > <font color="#FFFFFF">添加类别：</font></td>
    </tr>
    <tr bgcolor="f5f5f5"> 
      <td width="22%" > <div align="right">类别名称：</div></td>
      <td width="78%"> <input type="text" name="name" size="50" maxlength="50"/>
        <font color="#FF0000">*</font></td>
    </tr>
	<tr bgcolor="f5f5f5"> 
      <td width="22%" > <div align="right">备注(100字以内)：</div></td>
      <td width="78%"> <input type="text" name="note" size="80" maxlength="100"/>
        </td>
    </tr>
    <tr bgcolor="f5f5f5"> 
      <td colspan="2"> <div align="center"> 
          <input type="submit" name="SYS_SET" value=" 确 定 " class="frm_btn">
        </div></td>
    </tr>
  </table>
</form>
<br>
</body>
</html>