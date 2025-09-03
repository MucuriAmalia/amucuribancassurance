<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/bankreceipts.js"/>"></script>
<div class="x_panel">
    <div class="card-box table-responsive">
        <div class="x_title">
            <h4>Bank Integration Receipts</h4>
        </div>
        <form id="prg-group-form" class="form-horizontal">

              <label class="col-md-3 label-align">Processed </label>
              <div class="col-md-3">
                  <select class="form-control" id="receipt-type">
                      <option value="N">No</option>
                      <option value="S">Yes</option>
                  </select>
              </div>

        </form>
        <table id="staging_trans_tbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Account No</th>
                <th>Transaction Code</th>
                <th>Received From</th>
                <th>Payment Mode</th>
                <th>Trans Time</th>
                <th>Reference</th>
                <th>New Reference</th>
                <th>Trans Amount</th>
                <th>Branch</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<div class="modal fade" id="bankRefModal" tabindex="-1" role="dialog"   aria-labelledby="banksModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"  aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="banksModalLabel"> Edit Receipt Account </h4>
            </div>
            <div class="modal-body">
                <form id="bank-form" class="form-horizontal">

                    <input type="hidden" class="form-control" id="bank-code-pk" name="srId">

                    <div class="item form-group">
                        <label  class="col-md-3 label-align">New Reference No</label>

                        <div class="col-md-8 item form-group">
                            <input type="text" class="form-control" id="bank-ref-no" name="newTransRefNumber"  required>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBankRef"
                        type="button" class="btn btn-info">
                    Submit
                </button>
                <button type="button" class="btn btn-danger" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>
