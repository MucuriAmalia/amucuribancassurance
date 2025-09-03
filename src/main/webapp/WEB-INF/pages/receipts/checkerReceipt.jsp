<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/checkerReceipts.js"/>"></script>
<script type="text/javascript">
    var mck_id_receipt = "${mckIdReceipt}";
    function unlockForm() {
        if (mck_id_receipt !== -2000 && mck_id_receipt !== 'undefined') {
            navigator.sendBeacon(SERVLET_CONTEXT + '/protected/home/unlockForm',
                new URLSearchParams({ "formName": "RECEIPT", "formId": mck_id_receipt })
            );
            console.log("Form unlock request sent.");
        }
    }

    // Unlock the form only when the user fully leaves the page (not when they switch tabs)
    window.addEventListener("beforeunload", unlockForm);
</script>
<div class="container">
    <div class="x_panel">


        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Verify Receipt</h2>
            <div class="clearfix"></div>
        </div>
    </div>

    <div class="x_panel">
        <form id="receipt-form" class="form-horizontal">
            <div class="item form-group" style="display: none">
                <input type="hidden" id="receipt-type" name="receiptType" class="form-control">
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="rec-type" class="label-align col-md-3 col-xs-12"> Receipt Type: <span
                        class="required">*</span></label>
                <div class="col-md-6 col-xs-12">
                    <p id="selreceiptType"></p>

                </div>
                <div class="col-md-3">
                </div>
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12 collect-acct">
                <label for="coll-id" class="col-md-3 col-xs-12  label-align">Collection Account<span
                        class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <input type="hidden" id="coll-id" name="payId"/>
                    <div id="coll-div" class="form-control">
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>


            <div class="item form-group form-required col-md-6 col-xs-12 collect-acct">
                <label for="pymt-mode" class="label-align col-md-3 col-xs-12">Payment Mode</label>
                <div class="col-md-6 col-xs-12">
                    <p class="form-control-static" id="pymt-mode"></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12 collect-acct">
                <label for="bank-desc" class="label-align col-md-3 col-xs-12">Currency
                </label>
                <div class="col-md-6 col-xs-12">
                    <p class="form-control-static" id="currency-desc"></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12 collect-acct">
                <label for="bank-desc" class="label-align col-md-3 col-xs-12">Bank Info<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <p readonly class="form-control-static" id="bank-desc"></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>


            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="bank-desc" class="label-align col-md-3 col-xs-12">Branch
                </label>
                <div class="col-md-6 col-xs-12">
                    <input type="hidden" id="brn-id" name="brnCode"/>
                    <p readonly class="form-control-static" id="brn-frm"></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>


            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="rct-amount" class="label-align col-md-3 col-xs-12">Amount<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12" id="rct-amount">
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="paid-by" class="label-align col-md-3 col-xs-12">Paid By<span class="required">*</span>
                </label>
                <p id="paid-by">
                </p>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="narration" class="label-align col-md-3 col-xs-12">Narration<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <p id="narration"></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="receipt-date" class="label-align col-md-3 col-xs-12">Receipt Date<span class="required">*</span></label>
                <div class="col-md-6 col-xs-12">
                    <div class='input-group date datepicker-input'>
                        <p type='text' id="receipt-date"></p>

                    </div>
                    <div class="col-md-3">
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="payment-ref" class="label-align col-md-3 col-xs-12">Reference</label>

                <div class="col-md-6 col-xs-12">
                    <p type="text"  id="payment-ref"
                           ></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="doc-date" class="label-align col-md-3 col-xs-12">Document Date</label>

                <div class="col-md-6 col-xs-12">
                    <div class='input-group date datepicker-input'>
                        <p type='text'id="doc-date"></p>

                    </div>
                    <div class="col-md-3">
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="insurer" class="label-align col-md-3 col-xs-12">Insurer </label>
                <div class="col-md-6 col-xs-12">
                    <p type="text"  id="insurer" ></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="manual-ref" class="label-align col-md-3 col-xs-12">Manual Ref </label>
                <div class="col-md-6 col-xs-12">
                    <p type="text"  id="manual-ref" ></p>
                </div>
                <div class="col-md-3">
                </div>
            </div>
<%--            <div class="item form-group form-required col-md-6 col-xs-12">--%>
<%--                <label for="chk-cl-drreceipt" class="label-align col-md-3 col-xs-12"> Direct Receipt?</label>--%>
<%--                <div class="col-md-6 col-xs-12 checkbox">--%>
<%--                    <label>--%>
<%--                        <input type="checkbox" name="directReceipt" id="chk-cl-drreceipt">--%>
<%--                    </label>--%>
<%--                </div>--%>
<%--                <div class="col-md-3">--%>
<%--                </div>--%>
<%--            </div>--%>
<%--            <div class="item form-group form-required col-md-6 col-xs-12">--%>
<%--                <label for="chk-fund-receipt" class="label-align col-md-3 col-xs-12">Fund Receipt?</label>--%>
<%--                <div class="col-md-6 col-xs-12 checkbox">--%>
<%--                    <label>--%>
<%--                        <input type="checkbox" name="fundReceipt" id="chk-fund-receipt">--%>
<%--                    </label>--%>
<%--                </div>--%>
<%--                <div class="col-md-3">--%>
<%--                </div>--%>
<%--            </div>--%>
        </form>
    </div>
    <div class="x_panel">

        <div class="box-body">
            <div class="table-responsive">
                <table id="rct-detail-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>

                        <th>Trans No</th>
                        <th>Policy Number</th>
                        <th>Date</th>
                        <th>Client</th>
                        <th>Balance</th>
                        <th>Allocated Amount</th>
                        <th></th>
                    </tr>
                    </thead>

                </table>
            </div>
        </div>
    </div>
</div>
<div id="quot-panel">
    <sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
        <input type="button" class="btn btn-primary btn btn-danger float-right"
               value="Reject Task" id="btn-reject-quot">
    </sec:authorize>

    <sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
        <input type="button" class="btn btn-primary float-right"
               value="Approve Task" id="btn-approve-quote">
    </sec:authorize>
</div>

<div class="modal fade" id="clientTransModal" tabindex="-1" role="dialog"
     aria-labelledby="clientTransModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="clientTransModalLabel">
                    Add new Debit Transaction
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="search-form" class="form-horizontal float-left">
                    <div class="item form-group">

                        <div class="col-md-12">
                            <label for="brn-id" class="col-md-6 label-align">Debit
                                Number</label>

                            <div class="col-md-6">
                                <input type='text' class="form-control float-right"
                                       id="inv-search-number"/>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12">
                            <label for="brn-id" class="col-md-6 label-align">Client
                                First Name</label>

                            <div class="col-md-6">
                                <input type='text' class="form-control float-right"
                                       id="inv-search-name"/>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-12">
                            <label for="brn-id" class="col-md-6 label-align">Client
                                Other Names</label>

                            <div class="col-md-6">
                                <input type='text' class="form-control float-right"
                                       id="inv-search-other-names"/>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <input type="button" class="btn btn-info float-right"
                               style="margin-right: 10px;" value="Search"
                               id="btn-search-invoice">
                    </div>


                </form>
                <div class="cutom-container">
                    <table id="modal-rct-detail-tbl" class="table" style="width:100%">
                        <thead>
                        <tr>
                            <th></th>
                            <th>Trans No</th>
                            <th>Ref No</th>
                            <th>Date</th>
                            <th>Client</th>
                            <th>Amount</th>
                            <th>Balance</th>

                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="btn-add-selected-exit"
                        type="button" class="btn btn-primary">
                    Add Selected and Close
                </button>
                <button data-loading-text="Saving..." id="btn-add-selected"
                        type="button" class="btn btn-primary">
                    Add Selected
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div id="approvalModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Approve Task</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to approve this task?</p>
                <input type="hidden" id="approvalTaskId">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" id="approveConfirmButton" class="btn btn-primary">Approve</button>
            </div>
        </div>
    </div>
</div>

<!-- Rejection Modal -->
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