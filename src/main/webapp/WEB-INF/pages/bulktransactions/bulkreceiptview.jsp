<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/bulktransactions/bulkreceiptauth.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Bulk Receipting authorization</h2>
        <button type="button" class="btn btn-primary float-right"
                id="btn-trans-processing-reports" data-toggle="modal" data-target="#reportsModal">
            <i class="fa fa-print"></i>
        </button>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">

        <div class="x_title">
            <h2>UnAuthorized Receipt Listing</h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i> </a></li>
            </ul>
            <div class="clearfix"></div>
        </div>
        <div class="card-box table-responsive">
            <table id="uploadedTransTable" class="table table-striped" style="width: 100%">
                <thead>
                <tr>
                    <th><input type="checkbox" id="selectAll"></th>
                    <th>Task Name</th>
                    <th>Policy Number</th>
                    <th>Task Date</th>
                    <th>Initiated By</th>
                    <th width="5%"></th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div style="margin-top: 20px;">
<sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
            <button class="btn btn-primary float-right" id="bulk-auth-receipt-trans">Approve Receipt</button>
</sec:authorize>

<sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
            <button class="btn btn-danger float-right" id="bulk-auth-reject-trans">Reject Receipt</button>
</sec:authorize>
        </div>
    </div>
</div>


<div id="rejectionModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Reject Task</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <textarea id="rejectionReason" class="form-control" placeholder="Enter the reason for rejection"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" id="rejectConfirmButton" class="btn btn-danger">Reject</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="modals/bulkpolicymodal.jsp"></jsp:include>