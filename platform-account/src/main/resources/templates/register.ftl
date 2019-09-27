<html>
<head>
</head>
<body>
<form method="post">
    <div style="height: 35px;margin-top: 20px;" dir="ltr">
        开始时间：<input type="text" class="easyui-datebox" editable="false" name="startDate">-
        <input type="text" class="easyui-datebox" editable="false" name="endDate">
        <button type="button" onclick="search_click(this)">查询</button>
    </div>
</form>
</div>


<div id="dlg" class="easyui-dialog"
     style="width:400px;height:400px;top:100px;padding:10px 20px"
     closed="true" buttons="#dlg-buttons">
    <form id="fm" method="post" url="" novalidate>
        <table>
            <tr>
                <td>品牌ID:</td>
                <td><input name="brandId" class="easyui-textbox" required="true"></td>
            </tr>
            <tr>
                <td>品牌名称:</td>
                <td><input name="brandName" class="easyui-textbox" required="true"></td>
            </tr>
            <tr>
                <td>品牌Logo:</td>
                <td><input name="brandLogo" class="easyui-textbox"></td>
            </tr>
            <tr>
                <td>品牌描述:</td>
                <td><input name="brandDesc" class="easyui-textbox"></td>
            </tr>
        </table>
    </form>
</div>
<div id="dlg-buttons">
    <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save(this)" style="width:90px">保存</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">取消</a>
</div>

</body>
</html>
