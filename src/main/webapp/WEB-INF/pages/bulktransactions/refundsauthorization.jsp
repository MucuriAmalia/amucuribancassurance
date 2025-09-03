<%--
  Created by IntelliJ IDEA.
  User: Scylla
  Date: 18/06/2025
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/bulktransactions/bulkPolRefundAuth.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Bulk Refunds Authorization </h2>
        <button type="button" class="btn btn-primary float-right"
                id="btn-trans-processing-reports" data-toggle="modal" data-target="#reportsModal">
            <i class="fa fa-print"></i>
        </button>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="x_title">
            <h2> Unauthorized Bulk Refunds Listing </h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
            </ul>
            <div class="clearfix"></div>
        </div>
        <div class="card-box table-responsive">
            <table id="loadedPolicyTable" class="table table-striped" style="width: 100%">
                <thead>
                <tr>
                    <th><input type="checkbox" id="selectAll"></th>
                    <th>Pol No</th>
                    <th>Proposal No</th>
                    <th>Client Name</th>
                    <th></th>
                    <%--                    <th></th>--%>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div style="margin-top: 20px;">
            <sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
                <button class="btn btn-primary float-right" id="bulk-auth-receipt-trans">Approve refund</button>
            </sec:authorize>

            <sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
                <button class="btn btn-danger float-right" id="bulk-auth-reject-trans">Reject refund</button>
            </sec:authorize>
        </div>
    </div>
</div>

<jsp:include page="modals/bulkpolicymodal.jsp"></jsp:include>
