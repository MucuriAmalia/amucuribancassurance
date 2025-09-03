<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/receipts.js"/>"></script>
<style> .btn-processing {
    opacity: 0.6;
    cursor: not-allowed !important;
    pointer-events: none;
    position: relative;
}

.btn-processing::after {
    content: "🚫";
    position: absolute;
    right: 3px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 10px;
    line-height: 1;
}</style>
<div class="container">
    <div class="x_panel">
        <a href="<c:url value='/protected/uw/receipts/receiptList'/> "
           class="btn btn-primary float-right" style="margin-right: 10px;">Back</a>

        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Receipt Entry</h2>
            <%--<ul class="nav navbar-right panel_toolbox">--%>
            <%--<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>--%>
            <%--</li>--%>
            <%--</ul>--%>
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
                    <select class="form-control" id="rec-type" name="selreceiptType" required>
                        <option value="">Select Receipt Type</option>
                        <option value="N">General Insurance</option>
                        <option value="L">Life Insurance</option>
                        <option value="COM">Commissions</option>
                    </select>
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
                    <div id="coll-div" class="form-control"
                         select2-url="<c:url value="/protected/uw/receipts/collectAccts"/>">
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>

            <div class="item form-group form-required col-md-6 col-xs-12 insurance-co" style="display: none">
                <label for="insurance-div" class="col-md-3 col-xs-12 label-align">Insurance Company<span
                        class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <input type="hidden" id="insurance-id" name="insuranceId"/>
                    <div id="insurance-div" class="form-control"
                         select2-url="<c:url value="/protected/setups/binders/selAccounts"/>">
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
                    <div id="brn-frm" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>


            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="rct-amount" class="label-align col-md-3 col-xs-12">Amount<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <input type="text" class="form-control" id="rct-amount"
                           name="receiptAmount" required>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="paid-by" class="label-align col-md-3 col-xs-12">Paid By<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <input type="text" class="form-control" id="paid-by"
                           name="paidBy" required>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="narration" class="label-align col-md-3 col-xs-12">Narration<span class="required">*</span>
                </label>
                <div class="col-md-6 col-xs-12">
                    <textarea rows="2" cols=30 class="form-control" name="receiptDesc" id="narration"
                              REQUIRED></textarea>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="receipt-date" class="label-align col-md-3 col-xs-12">Receipt Date<span class="required">*</span></label>
                <div class="col-md-6 col-xs-12">
                    <div class='input-group date datepicker-input'>
                        <input type='text' class="form-control required" name="receiptDate" id="receipt-date"/>
                        <span class="input-group-addon">
                        <span class="fa fa-calendar"></span>
                    </span>
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
                    <input type="text" class="form-control" id="payment-ref"
                           name="paymentRef">
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="doc-date" class="label-align col-md-3 col-xs-12">Document Date</label>

                <div class="col-md-6 col-xs-12">
                    <div class='input-group date datepicker-input'>
                        <input type='text' class="form-control" name="documentDate" id="doc-date"/>
                        <span class="input-group-addon">
                        <span class="fa fa-calendar"></span>
                    </span>
                    </div>
                    <div class="col-md-3">
                    </div>
                </div>
                <div class="col-md-3">
                </div>
            </div>
            <div class="item form-group form-required col-md-6 col-xs-12">
                <label for="manual-ref" class="label-align col-md-3 col-xs-12">Manual Ref </label>
                <div class="col-md-6 col-xs-12">
                    <input type="text" class="form-control" id="manual-ref" name="manualRef">
                </div>
                <div class="col-md-3">
                </div>
            </div>
        </form>
    </div>
    <div class="x_panel">
        <div id="rates-details-div">
            <input type="button" id="add-det-btn"
                   class="btn btn-primary float-left" style="margin-right: 10px;"
                   value="Add">
        </div>
        <div class="box-body">
            <div class="table-responsive">
                <input type="hidden" id="receipt-pol-id"/>
                <table id="rct-detail-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>

                        <th>Trans No</th>
                        <th>Policy Number/ Ref No.</th>
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
<sec:authorize access="hasAnyAuthority('CREATE_RECEIPT')">
<%--            <input type="button" class="btn btn-primary float-right"--%>
<%--                   style="margin-right: 10px;" value="Print" id="btn-add-receipt">--%>
    <input type="button" id="btn-print-receipt"
           class="btn btn-primary float-right" style="margin-right: 10px;"
           value="Submit">
</sec:authorize>

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

<div class="modal fade" id="printReceiptModal" tabindex="-1" role="dialog"
     aria-labelledby="printReceiptModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="printReceiptModalLabel">
                    Print Receipt
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <input type="hidden" id="print-receipt-id">
                <div class="media">
                    <iframe class="col-lg-12 col-md-12 col-sm-12" id="receipt-div" height="600">

                    </iframe>
                </div>

            </div>
            <div class="modal-footer">
                <button id="printReceipt"
                        type="button" class="btn btn-success">
                    Printed Successfully
                </button>
                <button type="button" class="btn btn-success" data-dismiss="modal">
                    Receipt Not Printed
                </button>
            </div>
        </div>
    </div>
</div>