<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<div class="x_panel" id="acct_model">
  <div class="x_title">
    <h2><i class="fa fa-bars"></i> Payees Listing</h2>
    <div class="clearfix"></div>
  </div>
  <button class="btn btn-success btn btn-info float-left" id="btn-add-payee">New</button>
  <table id="payeesTbl" class="table table-striped" style="width:100%">
    <thead>
    <tr>
      <th>Name</th>
      <th>Email</th>
      <th>Phone No</th>
      <th>Telephone No</th>
      <th>Created By</th>
      <th>Status</th>
      <th width="5%"></th>
    </tr>
    </thead>
  </table>
  <h4>Account Details</h4>
  <hr>
  <button class="btn btn-success btn btn-info float-left" id="btn-add-account">New</button>
  <table id="payeesAccountsTbl" class="table table-striped" style="width:100%">
    <thead>
    <tr>
      <th>Account No</th>
      <th>Bank</th>
      <th>Branch</th>
      <th>Status</th>
      <th width="5%"></th>
    </tr>
    </thead>
  </table>
</div>

<div class="modal fade" id="payeesModal" tabindex="-1" role="dialog"
     aria-labelledby="payeesModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="collectAcctsModalLabel">
          Edit/Add Payee Details
        </h4>
        <button type="button" class="close" data-dismiss="modal"
                aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>

      </div>
      <div class="modal-body">
        <form id="payee-form" class="form-horizontal">
          <input type="hidden" class="form-control" id="pa-id" name="payId">
          <div class="item form-group">
            <label for="full-name" class="col-md-3 label-align">Full Name</label>

            <div class="col-md-8">
              <input type="text" class="form-control" id="full-name"
                     name="fullName"  required>
            </div>
          </div>
          <div class="item form-group">
            <label for="email-address" class="col-md-3 label-align">Email Address</label>

            <div class="col-md-8">
              <input type="text" class="form-control" id="email-address"
                     name="email"  required>
            </div>
          </div>
          <div class="item form-group">
            <label for="email-address" class="col-md-3 label-align">Mobile No</label>

            <div class="col-md-8">
              <input type="text" class="form-control" id="mobile-no"
                     name="mobileNo"  required>
            </div>
          </div>
          <div class="item form-group">
            <label for="tel-no" class="col-md-3 label-align">Telephone No</label>

            <div class="col-md-8">
              <input type="text" class="form-control" id="tel-no"
                     name="telNo">
            </div>
          </div>
          <div class="item form-group acct-status">
            <label for="selstatus" class="label-align col-md-3">Status<span class="required">*</span></label>
            <div class="col-md-8">
              <select class="form-control" id="selstatus" name="status">
                <option value="">Select Status</option>
                <option value="A">Active</option>
                <option value="I">Inactive</option>
              </select>

            </div>
          </div>
          <h4 class="edit-hide">Account Details</h4>
          <hr class="edit-hide">
          <div class="item form-group edit-hide">
            <label for="bank-branch-lov" class="col-md-3 label-align">Bank Branch</label>

            <div class="col-md-8">
              <input type="hidden" id="bank-branch-id" name="bankBranchId"/>
              <input type="hidden" id="bank-branch-name">
              <div id="bank-branch-lov" class="form-control"
                   select2-url="<c:url value="/protected/accounts/selBankBranches"/>" >

              </div>
            </div>
          </div>
          <div class="item form-group edit-hide">
            <label for="acct-number" class="col-md-3 label-align">Account Number</label>

            <div class="col-md-8">
              <input type="text" class="form-control" id="acct-number"
                     name="accountNo"  required>
            </div>
          </div>

        </form>
      </div>
      <div class="modal-footer">
        <button data-loading-text="Saving..." id="savePayeeBtn"
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


<div class="modal fade" id="payeesAccountsModal" tabindex="-1" role="dialog"
     aria-labelledby="payeesAccountsModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="payeesAccountsModalLabel">
          Edit/Add Payee Accounts Details
        </h4>
        <button type="button" class="close" data-dismiss="modal"
                aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>

      </div>
      <div class="modal-body">
        <form id="payee-accts-form" class="form-horizontal">
          <input type="hidden" class="form-control" id="payc-id" name="paycId">
          <input type="hidden" class="form-control" id="pay-id" name="payId">
          <div class="item form-group">
            <label for="bank-branch-lov" class="col-md-3 label-align">Bank Branch</label>

            <div class="col-md-8">
              <input type="hidden" id="bankbranch-id" name="bankBranchId"/>
              <input type="hidden" id="bankbranch-name">
              <div id="bankbranch-lov" class="form-control"
                   select2-url="<c:url value="/protected/accounts/selBankBranches"/>" >

              </div>
            </div>
          </div>
          <div class="item form-group edit-hide">
            <label for="acct-number" class="col-md-3 label-align">Account Number</label>

            <div class="col-md-8">
              <input type="text" class="form-control" id="acctnumber"
                     name="accountNo"  required>
            </div>
          </div>
          <div class="item form-group acct-status">
            <label for="selstatus" class="label-align col-md-3">Status<span class="required">*</span></label>
            <div class="col-md-8">
              <select class="form-control" id="selstatuss" name="status">
                <option value="">Select Status</option>
                <option value="A">Active</option>
                <option value="I">Inactive</option>
              </select>

            </div>
          </div>

        </form>
      </div>
      <div class="modal-footer">
        <button data-loading-text="Saving..." id="savePayeeAcctsBtn"
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

<script>

  $(function() {

    $(document).ready(function () {

      ACCOUNTS.createPayees();
      ACCOUNTS.savePayee();
      ACCOUNTS.savePayeeAccounts();
      ACCOUNTS.createPayeesAccounts();



      $("#btn-add-account").on("click", function(){
        if(ACCOUNTS.payId===-2000){
          bootbox.alert("Select Payee to add Account...")
          return;
        }
        $(".acct-status").hide();
        $('#payee-accts-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        ACCOUNTS.bankBranchLov();
        $("#pay-id").val(ACCOUNTS.payId);
        $('#payeesAccountsModal').modal('show');
      });

      $("#btn-add-payee").on("click", function(){
        $(".acct-status").hide();
        $(".edit-hide").show();
        $('#payee-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        ACCOUNTS.bankBranchLov();
        $('#payeesModal').modal('show');
      });

    });
  });

  var ACCOUNTS = ACCOUNTS || {};
  ACCOUNTS.payId = -2000;

  ACCOUNTS.savePayee = function (){
      var $currForm = $('#payee-form');
      var currValidator = $currForm.validate();

      $('#savePayeeBtn').click(function(){
        if (!$currForm.valid()) {
          return;
        }
        var $btn = $(this).text('Saving');
        var data = {};
        $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createPayee";
        var request = $.post(url, data );
        request.success(function(){
           Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
          currValidator.resetForm();
          $('#payeesTbl').DataTable().ajax.reload();
          $('#payeesModal').modal('hide');
        });
        request.error(function(jqXHR, textStatus, errorThrown){
          Swal.fire({
            title: 'Error',
            text: jqXHR.responseText,
            icon: 'error'
          })
        });
        request.always(function(){
          $btn.text('Save');
        });

      });
  }
  ACCOUNTS.savePayeeAccounts = function (){
    var $currForm = $('#payee-accts-form');
    var currValidator = $currForm.validate();

    $('#savePayeeAcctsBtn').click(function(){
      if (!$currForm.valid()) {
        return;
      }
      var $btn = $(this).text('Saving');
      var data = {};
      $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
      var url = "createPayeeAccount";
      var request = $.post(url, data );
      request.success(function(){
         Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
        currValidator.resetForm();
        $('#payeesAccountsTbl').DataTable().ajax.reload();
        $('#payeesAccountsModal').modal('hide');
      });
      request.error(function(jqXHR, textStatus, errorThrown){
        Swal.fire({
          title: 'Error',
          text: jqXHR.responseText,
          icon: 'error'
        })
      });
      request.always(function(){
        $btn.text('Save');
      });

    });
  }
  ACCOUNTS.createPayees= function(){
    var url = "payees";
    var currTable =  $('#payeesTbl').DataTable({
      "processing": true,
      "serverSide": true,
      "ajax": {
        'url': url,
      },
      lengthMenu: [[10, 15], [10, 15]],
      pageLength: 10,
      destroy: true,
      "columns": [
        {"data": "fullName"},
        {"data": "email"},
        {"data": "mobileNo"},
        {"data": "telNo"},
        {"data": "createdUser"},
        {
          "data": "status",
          "render": function (data, type, full, meta) {
            if (full.status && full.status === 'A') {
              return 'Active';
            } else {
              return 'Inactive';
            }
          }
        },
        {
          "data": "payId",
          "render": function (data, type, full, meta) {
            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-payees=' + encodeURI(JSON.stringify(full)) + ' onclick="ACCOUNTS.editPayee(this);"><i class="fa fa-pencil-square-o"></button>';
          }

        },
      ]
    });

    $('#payeesTbl tbody').on( 'click', 'tr', function () {
      $(this).addClass('table-primary').siblings().removeClass('table-primary');
      var aData = currTable.rows('.table-primary').data();
      if (aData[0] === undefined || aData[0] === null) {

      } else {
        ACCOUNTS.payId = aData[0].payId;
        $('#payeesAccountsTbl').DataTable().ajax.url( "payeesaccounts/"+aData[0].payId).load();
      }
    });

    return currTable;
  }

  ACCOUNTS.createPayeesAccounts= function (){
    var url = "payeesaccounts/-2000";
    var currTable =  $('#payeesAccountsTbl').DataTable({
      "processing": true,
      "serverSide": true,
      "ajax": {
        'url': url,
      },
      lengthMenu: [[10, 15], [10, 15]],
      pageLength: 10,
      destroy: true,
      "columns": [
        {"data": "accountNo"},
        {"data": "bank"},
        {"data": "bankBranch"},
        {
          "data": "status",
          "render": function (data, type, full, meta) {
            if (full.status && full.status === 'A') {
              return 'Active';
            } else {
              return 'Inactive';
            }
          }
        },
        {
          "data": "paycId",
          "render": function (data, type, full, meta) {
            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-payeesaccounts=' + encodeURI(JSON.stringify(full)) + ' onclick="ACCOUNTS.editPayeeAccount(this);"><i class="fa fa-pencil-square-o"></button>';
          }

        },
      ]
    });
  }

  ACCOUNTS.editPayeeAccount = function (button){
    var btn = JSON.parse(decodeURI($(button).data("payeesaccounts")));
    $(".acct-status").show();
    $("#payc-id").val(btn["paycId"]);
    $("#pay-id").val(ACCOUNTS.payId);
    $("#bankbranch-id").val(btn["bankBranchId"]);
    $("#bankbranch-name").val(btn["bank"]+'-'+btn["bankBranch"]);
    ACCOUNTS.bankBranchLov();
    $("#acctnumber").val(btn["accountNo"]);
    $("#selstatuss").val(btn["status"]);
    $('#payeesAccountsModal').modal('show');
  }

  ACCOUNTS.editPayee = function (button){
    var btn = JSON.parse(decodeURI($(button).data("payees")));
    $("#pa-id").val(btn["payId"]);
    $("#full-name").val(btn["fullName"]);
    $("#email-address").val(btn["email"]);
    $("#mobile-no").val(btn["mobileNo"]);
    $("#tel-no").val(btn["telNo"]);
    $("#selstatus").val(btn["status"]);
    $(".acct-status").show();
    $(".edit-hide").hide();
    $('#payeesModal').modal('show');
  }

  ACCOUNTS.bankBranchLov = function(){
      if($("#bank-branch-lov").filter("div").html() != undefined)
      {
        Select2Builder.initAjaxSelect2({
          containerId : "bank-branch-lov",
          sort : 'branchName',
          change: function(e, a, v){
            $("#bank-branch-id").val(e.added.bbId);
            $("#bank-branch-name").val(e.added.branchName);
          },
          formatResult : function(a)
          {
            return a.branchName;
          },
          formatSelection : function(a)
          {
            return a.branchName;
          },
          initSelection: function (element, callback) {
            var code = $("#bank-branch-id").val();
            var name = $("#bank-branch-name").val();
            var data = {branchName:name,bbId:code};
            callback(data);
          },
          id: "bbId",
          placeholder:"Select Bank Branch",
        });
      }
    if($("#bankbranch-lov").filter("div").html() != undefined)
    {
      Select2Builder.initAjaxSelect2({
        containerId : "bankbranch-lov",
        sort : 'branchName',
        change: function(e, a, v){
          $("#bankbranch-id").val(e.added.bbId);
          $("#bankbranch-name").val(e.added.branchName);
        },
        formatResult : function(a)
        {
          return a.branchName;
        },
        formatSelection : function(a)
        {
          return a.branchName;
        },
        initSelection: function (element, callback) {
          var code = $("#bankbranch-id").val();
          var name = $("#bankbranch-name").val();
          var data = {branchName:name,bbId:code};
          callback(data);
        },
        id: "bbId",
        placeholder:"Select Bank Branch",
      });
    }
  }
</script>