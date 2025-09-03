<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/processRefund.js"/>"></script>

<sec:authorize access="hasAnyAuthority('MAKE_REFUND_READY')">
    <input id="hasMakeReadyRefundRole" access="hasAnyAuthority('MAKE_REFUND_READY')" type="text" class="hide" value="true"/>
</sec:authorize>
<sec:authorize access="hasAnyAuthority('AUTHORIZE_REFUND')">
    <input id="hasAuthorizeRefundRole" access="hasAnyAuthority('AUTHORIZE_REFUND')" type="text" class="hide" value="true"/>
</sec:authorize>
<%--<sec:authorize access="hasAnyAuthority('MAKE_REFUND_READY')" var="hasMakeReadyRefundRole" ></sec:authorize>--%>
<%--<input id="hasMakeReadyRefundRole" access="!hasAnyAuthority('MAKE_REFUND_READY')" type="text" class="hide" value="false"/>--%>
<%--<input id="hasMakeReadyRefundRole" access="hasAnyAuthority('MAKE_REFUND_READY')" type="text" class="hide" value="true"/>--%>

<div class="x_panel">
    <div class="x_title">
        <h2>Process Refund</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
        <input type="hidden" id="txt-refund-id">
    </div>
    <form id="search-form" class="form-horizontal">
        <div class="item form-group">

            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Date
                    From</label>

                <div class="col-md-7 col-xs-12">
                    <div class='input-group date datepicker-input' id="wef-date">
                        <input type='text' class="form-control float-right"
                               id="from-date" required />
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Date To
                </label>

                <div class="col-md-7 col-xs-12">
                    <div class='input-group date datepicker-input' id="cover-to-date">
                        <input type='text' class="form-control float-right"
                               id="wet-date" required />
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Client
                </label>
                <div class="col-md-7 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="rev-search-name" />
                    <div id="client-frm" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Status</label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="status-search"
                            required>
                        <option value="">Select Status</option>
                        <option value="D">Draft</option>
                        <option value="R">Ready</option>
                        <option value="A">Authorized</option>
                        <option value="J">Rejected</option>
                    </select>
                </div>
            </div>
            </div>
            <div class="item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Policy
                        No.</label>

                    <div class="col-md-7 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="pol-search-number" placeholder="Policy No" />
                    </div>
                </div>
                <div class="col-md-6 col-xs-12" >
                    <label for="brn-id" class="col-md-5 label-align">Ref
                        No.</label>

                    <div class="col-md-7 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="rev-search-number" placeholder="Ref No"/>
                    </div>
                </div>
            </div>

        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
            <sec:authorize access="hasAnyAuthority('PREPARE_REFUND')">
                <input type="button" class="btn btn-info float-left"
                       style="margin-right: 10px;" value="New Refund"
                       id="btn-new-refund">
            </sec:authorize>
        </div>
            <div class="col-md-6 col-xs-12">
            <input type="button" class="btn btn-info float-right"
                   style="margin-right: 10px;" value="Search"
                   id="btn-search-refund">
        </div>

        </div>

    </form>
    <hr>
    <div class="card-box table-responsive">


        <table id="refund-tbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>

                <th>Client</th>
                <th>Policy No</th>
                <th>Capture Date</th>
                <th>Narrative</th>
                <th>Payment Mode</th>
                <th>Status</th>
                <th>Reference</th>
                <th>Payee</th>
                <th>Amount</th>
                <th></th>
                <th></th>
                <th></th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>

    </div>
    <form   class="form-horizontal" id="print-form">


    </form>
    <%--<button class="btn btn-info float-right" id="btn-receipt-cancel">Cancel Receipt</button>--%>
</div>

<div class="modal fade" id="refundModal" tabindex="-1" role="dialog"
     aria-labelledby="refundModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="refundModalLabel">
                    Edit/Add Refund Transactions
                </h4>
            </div>
            <div class="modal-body">
                <form id="new-refund-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="refund-id" name="refId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Client
                        </label>
                        <div class="col-md-8">
                            <input type='hidden' id="refund-client-id" name="client" />
                            <input type="hidden" id="client-f-name">
                            <input type="hidden" id="client-other-name">
                            <div id="refund-client-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Policy
                        </label>
                        <div class="col-md-8">
                            <input type='hidden' id="refund-pol-id" name="policy" />
                            <input type="hidden" id="policy-no">
                            <div id="refund-policy-frm" class="form-control"
                                 select2-url="<c:url value="/protected/accounts/clientAllPolicies"/>" >

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Credit Note/Receipt
                        </label>
                        <div class="col-md-8">
                            <input type='hidden' id="refund-trans-no" name="transNoToRefund" />
                            <input type="hidden" id="refund-ref">
                            <div id="refund-trans-frm" class="form-control"
                                 select2-url="<c:url value="/protected/accounts/policyTrans"/>" >

                            </div>
                        </div>
                    </div>



                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Amount</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="refund-amount"
                                   name="amount"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Narration</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="3" id="refund-narrations" name="narrations" required></textarea>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Payee</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="refund-payee"
                                   name="payee"  required readonly>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Pay Mode
                        </label>
                        <div class="col-md-8">
                            <input type='hidden' id="pm-id" name="paymentMode" />
                            <input type="hidden" id="pm-name">
                            <div id="pm-mode-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>" >

                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveRefund"
                        type="button" class="btn btn-success">
                    Save Refund
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="declineRefundModal" tabindex="-1" role="dialog"
     aria-labelledby="declineRefundModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="declineRefundModalLabel">
                    Reject Refund
                </h4>
            </div>
            <div class="modal-body">
                <form id="frm-decline-remarks" class="form-horizontal">
                    <input type="hidden" id="reject-refund-pk" >
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Remarks</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="5" id="reject-remarks"></textarea>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="rejectRefund"
                        type="button" class="btn btn-success">
                    Reject Refund
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="refundAccountsModal" tabindex="-1" role="dialog" aria-labelledby="refundAccountsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="prodSubclModalLabel">Select Account to Credit</h4>
            </div>
            <div class="modal-body">
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="accountsTbl">
                        <thead>
                        <tr>
                            <th width="12%">Account No</th>
                            <th width="12%">Account Title</th>
                            <th width="12%">Customer</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
                    <input type="hidden" id="client-account-no"/>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="makeRefundPayments"
                        type="button" class="btn btn-success">OK</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel</button>
            </div>
        </div>
    </div>
</div>
