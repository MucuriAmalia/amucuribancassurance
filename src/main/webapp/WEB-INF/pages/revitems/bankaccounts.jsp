<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
  <div class="x_panel">
     <button class="btn btn-success btn btn-info float-right" id="btn-add">New</button>
       <div class="x_title">
	<h2><i class="fa fa-bars"></i> Bank Accounts Definition</h2>
    <div class="clearfix"></div>
	</div>
	<table id="bank-accts-tbl" class="table table-striped" style="width:100%">
		<thead>
			<tr>

				<th>Acct Name</th>
				<th>Acct Number</th>
				<th>GL Account</th>
				<th>Bank Branch</th>
				<th>Branch</th>
				<th>Current Cheque No</th>
				<th>Min Amount</th>
				<th>Max Amount</th>
				<th>Status</th>
				<th width="5%"></th>
				<th width="5%"></th>
			</tr>
		</thead>
	</table>
	</div>
  <div class="modal fade" id="bankAcctsModal" tabindex="-1" role="dialog"
		aria-labelledby="bankAcctsModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="clntTitleModesModalLabel">
                        Edit/Add Bank Account
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body" id="branch_model">
					<form id="bank-accts-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="ba-id" name="baId">
						
						<div class="item form-group">
							<label for="acct-name" class="col-md-3 label-align">Account Name</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="acct-name"
									name="bankAcctName"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="acct-number" class="col-md-3 label-align">Account Number</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="acct-number"
									   name="bankAcctNumber"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="acct-number" class="col-md-3 label-align">Branch</label>

							<div class="col-md-8">
								<input type="hidden" id="brn-id" name="branchId"/>
								<input type="hidden" id="brn-name">
								<div id="brn-frm" class="form-control"
									 select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">

								</div>
							</div>
						</div>
						<div class="item form-group">
							<label for="acct-number" class="col-md-3 label-align">Bank Branch</label>

							<div class="col-md-8">
								<input type="hidden" id="bank-branch-id" name="bankBranchId"/>
								<input type="hidden" id="bank-branch-name">
								<div id="bank-branch-lov" class="form-control"
									 select2-url="<c:url value="/protected/accounts/selBankBranches"/>" >

								</div>
							</div>
						</div>
						<div class="item form-group">
							<label for="dr-account-code" class="col-md-3 label-align">
								GL Account</label>

							<div class="col-md-8">
								<input type="hidden" name="glId" id="dr-account-code">
								<input type="hidden" id="gl-acct-code">
								<input type="hidden" id="gl-acct-name">
								<div id="dr-account-frm" class="form-control"
									 select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>" >

								</div>
							</div>
						</div>
						<div class="item form-group">
							<label for="chq-number" class="col-md-3 label-align">Current Cheque Number</label>

							<div class="col-md-8">
								<input type="number" class="form-control" id="chq-number"
									   name="currentChequeNo"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="acct-number" class="col-md-3 label-align">Minimum Amount</label>

							<div class="col-md-8">
								<input type="text" class="form-control amount" id="min-amt"
									   name="minimumAmt">
							</div>
						</div>
						<div class="item form-group">
							<label for="acct-number" class="col-md-3 label-align">Maximum Amount</label>

							<div class="col-md-8">
								<input type="text" class="form-control amount" id="max-amt"
									   name="maximumAmt">
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
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveBankAccount"
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

			createBankAccounts();
			$('.amount').number( true, 2 );

			saveBankAccount();

			$("#btn-add").on("click", function(){
				$(".acct-status").hide();
				$('#bank-accts-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
				populateUserBranches();
				populateBankBranches();
				populateAccounts();
				$('#bankAcctsModal').modal('show');
			});

		});



	});

	function saveBankAccount(){
		var $currForm = $('#bank-accts-form');
		var currValidator = $currForm.validate();


		$('#saveBankAccount').click(function(){
			if (!$currForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createBankAccount";
			var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				currValidator.resetForm();
				$('#bank-accts-tbl').DataTable().ajax.reload();
				$('#bankAcctsModal').modal('hide');
			});
			request.error(function(jqXHR, textStatus, errorThrown){
				Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
			});
			request.always(function(){
				$btn.button('reset');
			});

		});
	}

	function populateAccounts(){
		if($("#dr-account-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "dr-account-frm",
				sort : 'name',
				change: function(e, a, v){
					$("#dr-account-code").val(e.added.coId);
					$("#gl-acct-code").val(e.added.code);
					$("#gl-acct-name").val(e.added.name);

				},
				formatResult : function(a)
				{
					return a.code+" - "+ a.name;
				},
				formatSelection : function(a)
				{
					return a.code+" - "+ a.name;
				},
				initSelection: function (element, callback) {
					var codeId  = $('#dr-account-code').val();
					var code  = $('#gl-acct-code').val();
					var name = $("#gl-acct-name").val();
					var data = {name:name,code:code,coId:codeId};
					callback(data);
				},
				id: "coId",
				width:"250px",
				placeholder:"Select Account"

			});
		}
	}


	function populateBankBranches(){
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
	};

	function populateUserBranches(){
		if($("#brn-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "brn-frm",
				sort : 'obName',
				change: function(e, a, v){
					$("#brn-id").val(e.added.obId);
					$("#brn-name").val(e.added.obName);
				},
				formatResult : function(a)
				{
					return a.obName;
				},
				formatSelection : function(a)
				{
					return a.obName;
				},
				initSelection: function (element, callback) {
					var code = $("#brn-id").val();
					var name = $("#brn-name").val();
					var data = {obName:name,obId:code};
					callback(data);
				},
				id: "obId",
				width:"250px",
				placeholder:"Select Branch"

			});
		}
		$("#brn-frm").on("select2-removed", function(e) {
			$("#brn-id").val('');
		});
	}

		function createBankAccounts(){
			var url = "bankaccounts";
			return $('#bank-accts-tbl').DataTable({
				"processing": true,
				"serverSide": true,
				"ajax": url,
				lengthMenu: [[10, 15], [10, 15]],
				pageLength: 10,
				destroy: true,
				"columns": [
					{"data": "bankAcctName"},
					{"data": "bankAcctNumber"},
					{
						"data": "glName",
						"render": function (data, type, full, meta) {
							return full.glCode+" - "+full.glName;
						}
					},
					{"data": "bankBranchName"},
					{"data": "branchName"},
					{"data": "currentChequeNo"},
					{
						"data": "minimumAmt",
						"render": function (data, type, full, meta) {
							return UTILITIES.currencyFormat(full.minimumAmt);
						}
					},
					{
						"data": "maximumAmt",
						"render": function (data, type, full, meta) {
							return UTILITIES.currencyFormat(full.maximumAmt);
						}
					},
					{
						"data": "status",
						"render": function (data, type, full, meta) {
							if(full.status && full.status==='A'){
								return 'Active';
							}
							else{
								return 'Inactive';
							}
						}
					},
					{
						"data": "baId",
						"render": function (data, type, full, meta) {
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-bankaccounts=' + encodeURI(JSON.stringify(full)) + ' onclick="editBankAccount(this);"><i class="fa fa-pencil-square-o"></button>';
						}

					},
					{
						"data": "baId",
						"render": function (data, type, full, meta) {
							return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-bankaccounts=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteBankAccount(this);"><i class="fa fa-trash-o"></button>';
						}

					},
				]
			});
		}

		function editBankAccount(button){
			var bankAccount = JSON.parse(decodeURI($(button).data("bankaccounts")));
			$("#ba-id").val( bankAccount["baId"]);
			$("#acct-name").val( bankAccount["bankAcctName"]);
			$("#acct-number").val( bankAccount["bankAcctNumber"]);
			$("#brn-id").val( bankAccount["branchId"]);
			$("#brn-name").val( bankAccount["branchName"]);
			populateUserBranches();
			$("#bank-branch-id").val( bankAccount["bankBranchId"]);
			$("#bank-branch-name").val( bankAccount["bankBranchName"]);
			populateBankBranches();
			$("#dr-account-code").val( bankAccount["glId"]);
			$("#gl-acct-code").val( bankAccount["glCode"]);
			$("#gl-acct-name").val( bankAccount["glName"]);
			populateAccounts();
			$("#chq-number").val( bankAccount["currentChequeNo"]);
			$("#min-amt").val( bankAccount["minimumAmt"]);
			$("#max-amt").val( bankAccount["maximumAmt"]);
			$("#selstatus").val( bankAccount["status"]);
			$(".acct-status").show();
			$('#bankAcctsModal').modal('show');
		}

	function deleteBankAccount(button){
		var bankAccount = JSON.parse(decodeURI($(button).data("bankaccounts")));

		bootbox.confirm("Are you sure want to delete bank account "+ bankAccount["bankAcctName"]+ "?", function(result) {
			if(result){
				$.ajax({
					type: 'GET',
					url:  'deleteBankAccount/' + bankAccount["baId"],
					dataType: 'json',
					async: true,
					success: function(result) {
						Swal.fire({
                    title: 'Success',
                    text: 'Record Deleted Successfully',
                    icon: 'success'
                 });
						$('#bank-accts-tbl').DataTable().ajax.reload();
					},
					error: function(jqXHR, textStatus, errorThrown) {
						Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
					}
				});
			}
		});
	}


</script>