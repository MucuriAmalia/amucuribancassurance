<%--
  Created by IntelliJ IDEA.
  User: joanrunyiri
  Date: 03/06/2025
  Time: 13:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript">
    var mck_id_refund = "${mckIdRefund}";
</script>

<div class="x_title">
    <h2><i class="fa fa-money"></i> Refund Approval</h2>
    <div class="clearfix"></div>
</div>

<div class="x_panel">
    <div class="x_content">
        <!-- Refund details form -->
        <form id="refund-form" class="form-horizontal form-label-left">
            <div class="x_title">
                <h4>Refund Details</h4>
            </div>

            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label class="label-align col-md-5">Requested By:</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="requested-by"></p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label class="label-align col-md-5">Request Date:</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="request-date"></p>
                    </div>
                </div>
            </div>

            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label class="label-align col-md-5">Transaction Type:</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="original-trans-type"></p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label class="label-align col-md-5">Policy No:</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="policy-no"></p>
                    </div>
                </div>
            </div>

            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label class="label-align col-md-5">Refund Date:</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="refund-date"></p>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label class="label-align col-md-5">Refund Amount:</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="refund-amount" style="font-weight: bold; color: #28a745;"></p>
                    </div>
                </div>
            </div>

            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label class="label-align col-md-5">Client Name:</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="client-name"></p>
                    </div>
                </div>
            </div>

        </form>
    </div>
    <!-- Approve/Reject buttons -->
    <div id="refund-panel">
        <sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
            <input type="button" class="btn btn-primary btn btn-danger float-right"
                   value="Reject Task" id="btn-reject-quot">
        </sec:authorize>

        <sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
            <input type="button" class="btn btn-primary float-right"
                   value="Approve Task" id="btn-approve-quote">
        </sec:authorize>
    </div>
</div>

<!-- Approval Modal -->
<div id="approvalModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Approve Refund</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to approve this refund?</p>
                <input type="hidden" id="approvalTaskId">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" id="approveConfirmButton" class="btn btn-success">Approve</button>
            </div>
        </div>
    </div>
</div>

<!-- Rejection Modal -->
<div id="rejectionModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Reject Refund</h5>
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
<script type="text/javascript">
    var mck_id_refund = "${mckIdRefund}";

    $(document).ready(function() {
        populateRefundDetailsFromModel();

        <c:if test="${errorMessage != null}">
        $('#refund-form').prepend('<div class="alert alert-danger">${errorMessage}</div>');
        </c:if>
    });

    function populateRefundDetailsFromModel() {
        <c:if test="${refundDetails != null}">
        // Refund request details - use pre-formatted values
        $('#request-date').text('${formattedMakerRequestDate}' || '-');
        $('#requested-by').text('${requestedBy}' || '-');
        $('#refund-status').text('${status}' || '-');

        // Original transaction details
        $('#original-trans-no').text('${refundDetails.originalTransId}' || '-');
        $('#original-trans-type').text('${refundDetails.originalTransType}' || '-');
        $('#policy-no').text('${refundDetails.policyNo}' || '-');

        // Refund transaction details
        $('#refund-trans-no').text('${refundDetails.reverseTransId}' || '-');
        $('#refund-amount').text('${formattedRefundAmount}' || '-');
        $('#refund-date').text('${formattedRefundDate}' || '-');

        // Client information
        $('#client-name').text('${refundDetails.clientName}' || '-');
        $('#refund-reason').text('${refundDetails.refundReason}' || '-');
        </c:if>

        // Show task details
        $('#refund-id').text('${mckIdRefund}');

        <c:if test="${taskName != null}">
        console.log('Task Name: ${taskName}');
        </c:if>
    }

    // Handle approve button click
    $('#btn-approve-quote').on('click', function() {
        $('#approvalTaskId').val(mck_id_refund);
        $('#approvalModal').modal('show');
    });

    // Handle reject button click
    $('#btn-reject-quot').on('click', function() {
        $('#rejectionModal').modal('show');
    });

    // Handle approve confirmation
    $('#approveConfirmButton').on('click', function() {
        approveTask();
    });

    // Handle reject confirmation
    $('#rejectConfirmButton').on('click', function() {
        rejectTask();
    });

    function rejectTask(){
        // Display the modal
        $('#rejectionModal').modal('show');

        $('#rejectConfirmButton').off('click').on('click', function() {
            var reason = $('#rejectionReason').val();

            if (reason) {
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/users/rejectTask',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        taskId: mck_id_refund,
                        reason: reason
                    }),
                    success: function(response) {
                        window.location.href = SERVLET_CONTEXT + "/protected/home";

                    },
                    error: function(xhr, status, error) {
                        Swal.fire({
                            title: 'Error',
                            text: xhr.responseText,
                            icon: 'error'
                        });
                    }
                });
            } else {
                Swal.fire({
                    title: 'Error',
                    text: 'No reason provided for rejection',
                    icon: 'error'
                });
            }
            // Hide the modal after submission
            $('#rejectionModal').modal('hide');
        });
    }

    function approveTask(){
        // Display the modal
        // $('#approvalModal').modal('show');
        // $('#approveConfirmButton').off('click').on('click', function() {

            $.ajax({
                url: SERVLET_CONTEXT + '/protected/users/approveTask',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(mck_id_refund),
                success: function(response) {
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                },
                error: function(xhr, status, error) {
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });

            // Hide the modal after submission
            $('#approvalModal').modal('hide');
        // });
    }

</script>