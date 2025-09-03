<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/mob_money.js"/>"></script>
<div class="x_panel">
    <div class="card-box table-responsive">
        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Mpesa Integration Transactions</h2>
            <div class="clearfix"></div>
        </div>
        <form id="prg-group-form" class="form-horizontal">
            <div class="item form-group">
                <label for="cou-name" class="col-md-3 label-align">Receipted</label>

                <div class="col-md-3">
                    <select class="form-control" id="receipt-type">
                        <option value="N">No</option>
                        <option value="Y">Yes</option>
                    </select>
                </div>
            </div>


        </form>
        <table id="mpesa_trans_tbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>First Name</th>
                <th>Middle Name</th>
                <th>Last Name</th>
                <th>Mpesa Ref No</th>
                <th>Phone No</th>
                <th>Account No</th>
                <th>New Account</th>
                <th>Trans Amount</th>
                <th>Processed</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<div class="modal fade" id="mpesaRefModal" tabindex="-1" role="dialog"
     aria-labelledby="banksModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="mpesaRefModalLabel">
                    Edit Mpesa Account
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="mpesa-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="mpesa-code-pk" name="bidId">

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">New Account No</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="mpesa-ref-no"
                                   name="billNewRfNumber"  required>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveMpesaRef"
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
