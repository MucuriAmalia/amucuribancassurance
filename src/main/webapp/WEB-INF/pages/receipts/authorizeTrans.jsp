<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/authTrans.js"/>"></script>
<div class="x_panel">
        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Accounts Transactions</h2>
            <ul class="nav navbar-right panel_toolbox">
                <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                </li>
            </ul>
            <div class="clearfix"></div>
        </div>
 </div>
<div class="col-md-12 col-xs-12 table-responsive">
    <table id="accounts-tbl" class="table table-striped" style="width:100%">
        <thead>
        <tr>
            <th><input type="checkbox" id="selectAll" data-select-all="false"></th>
            <th>Trans Date</th>
            <th>Account</th>
            <th>Ref No</th>
            <th>Tran Type</th>
            <th>Debit/Credit</th>
            <th>Amount</th>
            <th>Net Due</th>
            <th>Payee Name</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
    </table>


    <div class="row mb-3">
        <div class="col-md-12">
            <button id="btn-approve-all" class="btn btn-success">
                <i class="fa fa-check"></i> Authorize Selected
            </button>
            <button id="btn-reject-all" class="btn btn-danger">
                <i class="fa fa-times"></i> Reject Selected
            </button>
        </div>
    </div>
</div>