<%--
  Created by IntelliJ IDEA.
  User: Envy
  Date: 8/3/2024
  Time: 9:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/users/userroles.js"/>"></script>

<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i>Permissions</h2>
        <div class="clearfix"></div>
    </div>
    <div class="table-responsive">
        <table id="permissions-tbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Module Name</th>
                <th>Permission Name</th>
                <th>Access Type</th>
                <th>Maker Checker Required</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>


