<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/setups/transmapping.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-mapping">New</button>
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Transaction Mapping</h2>
        <div class="clearfix"></div>
    </div>
    <div class="table-responsive">
    <table id="trans-mapping-tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">

            <th>Transaction Type</th>
            <th>Debit Code</th>
            <th>Credit Code</th>
            <th width="5%"></th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
</div>
<div class="modal fade" id="transMappingModal" tabindex="-1" role="dialog"
     aria-labelledby="transMappingModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="transMappingModalLabel">
                    Edit/Add Transaction Types Mapping
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="mapping-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="mapping-id-pk" name="tmNo">

                    <div class="item form-group">
                        <label for="trans-type" class="col-md-3 label-align">Transaction Type</label>
                        <div class="col-md-8">
                            <select class="form-control" id="trans-type" name="transType" required>
                                <option value="">Select Transaction Type</option>
                                <option value="NB">New Business</option>
                                <option value="RN">Renewal</option>
                                <option value="EN">Endorsement</option>
                                <option value="CN">Cancellation</option>
                                <option value="RF">Refund</option>
                                <option value="BU">Bulk Uploads</option>
                                <option value="LD">Loaded Transactions</option>
                                <option value="JV">Journals</option>
                                <option value="EX">Extension</option>
                                <option value="BNK">Bank Charges</option>
                                <option value="JVC">Claim Payment by Journal</option>
                                <option value="CF">Claim Fee</option>
                                <option value="CO">Contra</option>
                                <option value="PM">Payments</option>
                                <option value="COM">Commission Posting</option>
                                <option value="CP">Claim Payment</option>
                                <option value="CR">Claim Recovery</option>
                                <option value="CS">Claim Salvage</option>
                                <option value="DC">Declaration</option>
                                <option value="LO">Claim Opening</option>
                                <option value="RP">Claim Reopening</option>
                                <option value="LR">Claim Revision</option>
                                <option value="ME">Mid Term Endorsement</option>
                                <option value="RC">Receipts</option>
                                <option value="RE">Reinstatement of a Policy</option>
                                <option value="RI">Reinsurance Transaction</option>
                                <option value="SP">Short Period</option>

                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="debit-code" class="col-md-3 label-align">Debit Code</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control" id="debit-code"
                                   name="debitCode"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="credit-code" class="col-md-3 label-align">Credit Code</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control" id="credit-code"
                                   name="creditCode"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveMapping"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>
