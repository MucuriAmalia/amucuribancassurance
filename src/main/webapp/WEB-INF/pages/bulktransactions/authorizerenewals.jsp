<%--
  Created by IntelliJ IDEA.
  User: Scylla
  Date: 18/06/2025
  Time: 12:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/bulktransactions/bulkpolRNAuth.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Bulk Renewals Authorization </h2>
        <button type="button" class="btn btn-primary float-right"
                id="btn-trans-processing-reports" data-toggle="modal" data-target="#reportsModal">
            <i class="fa fa-print"></i>
        </button>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="x_title">
            <h2> Unauthorized Bulk Renewals Listing </h2>
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
                    <th>Cover Type</th>
                    <th>Policy Reference No</th>
                    <th>Premium</th>
                    <th>Client Name</th>
                    <th>Cover From</th>
                    <th>Cover To</th>
                    <th>Creation Date</th>
                    <th>Policy Status</th>
                    <th></th>
                    <%--                    <th></th>--%>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div style="margin-top: 20px;">
            <button class="btn btn-danger float-right" id="bulk-delete-policy">Bulk Delete</button>
            <button class="btn btn-primary float-right" id="bulk-authorize-policy">Bulk Process</button>
            <%-- <button class="btn btn-danger float-right" id="bulk-reject-policy">Bulk Reject</button> --%>
        </div>
    </div>
</div>


<!-- Rejection Modal -->
<div id="rejectionModal" class="modal fade" tabindex="-1" role="dialog">
    <<div class="modal-dialog" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title">Reject Task</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <form id="rejection-form">
                <div class="form-group">
                    <label>Select Rejection Reason(s) <span class="text-danger">*</span></label>
                    <div id="rejection-reasons-list">
                        <!-- Rejection reasons will be dynamically inserted here -->
                    </div>
                </div>
                <div class="form-group">
                    <label for="rejectionReason">Additional Comments <span class="text-danger">*</span></label>
                    <textarea id="rejectionReason" class="form-control" placeholder="Enter additional comments"></textarea>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            <button type="button" id="rejectConfirmButton" class="btn btn-danger">Reject</button>
        </div>
    </div>
</div>
</div>

<jsp:include page="modals/bulkpolicymodal.jsp"></jsp:include>
