<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
 <div class="x_panel">
     <button class="btn btn-success btn btn-info float-right" id="btn-add-acctypes">New</button>
	 <div class="x_title">
		 <h2><i class="fa fa-bars"></i> Account Types List</h2>
		 <div class="clearfix"></div>
	</div>
	<div class="x_content">
		<div class="table-responsive">
	<table id="accounttbl" class="table table-striped" style="width:100%">
		<thead>
			<tr>
				<th>Type</th>
				<th>Sht Desc</th>
				<th>Account Type</th>
				<th>Commission Rate</th>
				<th>VAT Applic.</th>
				<th>VAT Value</th>
				<th>WITHX Applic.</th>
				<th>WITHX Value</th>
				<th style="width: 5px"></th>
				<th width="5%"></th>
				<!-- <th width="5%"></th>  -->
			</tr>
		</thead>
	</table>
			</div>
	</div>
  </div>
  <div class="modal fade" id="acctTypesModal" tabindex="-1" role="dialog"
		aria-labelledby="acctTypesModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
				    <h4 class="modal-title" id="acctTypesModalLabel">
                        Edit/Add Account Types
                    </h4>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>

				</div>
				<div class="modal-body" id="branch_model">
					<form id="acct-types-form" class="form-horizontal">
						<input type="hidden" class="form-control" id="acc-id" name="accId">
						<div class="item form-group">
							<label for="cou-name" class="col-md-3 label-align">Type</label>

							<div class="col-md-8">
								<select class="form-control" id="acc-type" name="accountType" required>
									<option value="">Select Type</option>
<%--									<option value="DR">Direct</option>--%>
									<option value="INS">Insurance Company</option>
<%--									<option value="BRK">Brokers</option>--%>
<%--									<option value="RN">Reinsurer</option>--%>
									<option value="INT">Introducer</option>
									<option value="MRK">Marketer</option>
									<option value="SUB">Sub-Agents</option>
<%--									<option value="IA">Inhouse Agents</option>--%>
<%--									<option value="SF">Self Fund</option>--%>
								</select>
							</div>
						</div>
						<div class="item form-group">
							<label for="sht-desc" class="col-md-3 label-align">Sht Desc</label>

							<div class="col-md-8">
								<input type="text" class="form-control" id="sht-desc"
									name="accShtDesc"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="description" class="col-md-3 label-align">Account Type Desc.</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="description"
									name="accName"  required>
							</div>
						</div>
						<div class="item form-group">
							<label for="comm-rate" class="col-md-3 label-align">Commission Rate</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="comm-rate"
									name="commRate">
							</div>
						</div>
						<div class="item form-group">
							<label for="vat-appli" class="col-md-3 label-align">
								Vat Applicable</label>
			
							<div class="col-md-4">
								<input type="checkbox" id="vat-appli" name="vatAppli">
							</div>
						</div>
						<div class="item form-group">
							<label for="vat-rate" class="col-md-3 label-align">VAT Rate</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="vat-rate"
									name="vatRate" disabled>
							</div>
						</div>
						<div class="item form-group">
							<label for="whtx-appli" class="col-md-3 label-align">
								WHTX Applicable</label>
			
							<div class="col-md-4">
								<input type="checkbox" id="whtx-appli" name="whtxAppl">
							</div>
						</div>
						<div class="item form-group">
							<label for="whtx-rate" class="col-md-3 label-align">WHTX Rate</label>

							<div class="col-md-8">
							    <input type="text" class="form-control" id="whtx-rate"
									name="whtaxVal" disabled>
							</div>
						</div>
						
					</form>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="saveAccountTypes"
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

<div class="modal fade" id="subAcctTypesModal" tabindex="-1" role="dialog"
	 aria-labelledby="subAcctTypesModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="subAcctTypesModalLabel">
					Sub Account Types
				</h4>
				<input type="hidden" class="form-control" id="acctype-pk-code">
			</div>
			<div class="modal-body">

				<div class="cutom-container">
					<button class="btn btn-success btn btn-info float-left" id="btn-add-sub-acct-type">New</button>
					<table id="subaccounttypestbl" class="table table-striped" style="width:100%">
						<thead>
						<tr>
							<th>Sht Desc</th>
							<th>Description</th>
							<th>Commission</th>
							<th>Binder</th>
							<th width="5%"></th>
							<th width="5%"></th>
							<!-- <th width="5%"></th>  -->
						</tr>
						</thead>
					</table>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">
					Cancel
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="addsubaccttypeModal" tabindex="-1" role="dialog"
	 aria-labelledby="addsubaccttypeModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="addsubaccttypeModalLabel">
					Define Sub Account Type
				</h4>
			</div>
			<div class="modal-body">
				<form id="sub-account-form" class="form-horizontal">
					<input type="hidden" class="form-control" id="sub-acct-id" name="subAcctId">
					<input type="hidden" class="form-control" id="sub-acct-type-id" name="accountTypes">

					<div class="item form-group">
						<label for="acc-sht-desc" class="col-md-3 label-align">Sht Desc</label>
						<div class="col-md-8">
							<input type="text" class="form-control" id="acc-sht-desc"
								   name="accShtDesc"  required>
						</div>
					</div>
					<div class="item form-group">
						<label for="acc-desc" class="col-md-3 label-align">Description</label>
						<div class="col-md-8">
							<input type="text" class="form-control" id="acc-desc"
								   name="accName"  required>
						</div>
					</div>
					<div class="item form-group">
						<label for="sub-comm-rate" class="col-md-3 label-align">Commission Rate</label>
						<div class="col-md-8">
							<input type="number" class="form-control" id="sub-comm-rate" min="0" max="100"
								   name="commRate">
						</div>
					</div>
					<div class="item form-group">
						<label for="binder-frm" class="col-md-3 label-align">Binder</label>
						<div class="col-md-8">
							<input type="hidden" id="binder-id" name="bindersDef"/>
							<input type="hidden" id="bind-name">
							<div id="binder-frm" class="form-control"
								 select2-url="<c:url value="/protected/setups/binders/allBinders"/>">

							</div>
						</div>
					</div>

				</form>
			</div>
			<div class="modal-footer">
				<button data-loading-text="Saving..." id="savesubaccttype"
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
	var UTILITIES = UTILITIES || {};

	$(function(){

		$(document).ready(function() {
			createAccTypesTable();
			newAccountTypes();
			saveAccountTypes();
			checkTaxes();
			$('#vat-rate,#whtx-rate,#comm-rate').number( true, 2 );
			$(document).ajaxStart(function () {
				$("#savesubaccttype,#saveAccountTypes").attr("disabled", true);
			});
			$(document).ajaxComplete(function () {
				$("#savesubaccttype,#saveAccountTypes").attr("disabled", false);
			});
			//$('#btn-add-acctypes').hide();
		});
	});

	function newAccountTypes(){
		$("#btn-add-acctypes").on("click", function(){
			$('#acct-types-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
			$('#acct-types-form').find("input[type=checkbox]").attr("checked", false);
			refreshTaxes();
			$('#acctTypesModal').modal('show');
		});
	}


	function saveAccountTypes(){
		var $accTypesFrm= $('#acct-types-form');
		var validator = $accTypesFrm.validate();
		$('#acctTypesModal').on('hidden.bs.modal', function () {
			validator.resetForm();
			$('#acct-types-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
			$('#acct-types-form').find("input[type=checkbox]").attr("checked", false);
		});

		$('#saveAccountTypes').click(function(){
			if (!$accTypesFrm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$accTypesFrm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createAcctTypes";
			var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#accounttbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#acctTypesModal').modal('hide');
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

	function editAcctTypes(button){
		var acctype = JSON.parse(decodeURI($(button).data("acctype")));
		$("#acc-id").val(acctype["accId"]);
		$("#sht-desc").val(acctype["accShtDesc"]);
		$("#acc-type").val(acctype["accountType"]);
		$("#description").val(acctype["accName"]);
		if(acctype["vatAppli"])
			$("#vat-appli").prop("checked", acctype["vatAppli"]);
		else
			$("#vat-appli").prop("checked", false);
		if(acctype["whtxAppl"])
			$("#whtx-appli").prop("checked", acctype["vatAppli"]);
		else
			$("#whtx-appli").prop("checked", false);
		$("#vat-rate").val(acctype["vatRate"]);
		$("#whtx-rate").val(acctype["whtaxVal"]);
		$("#comm-rate").val(acctype["commRate"]);
		refreshTaxes();
		$('#acctTypesModal').modal('show');
	}

	function checkTaxes(){
		$('#vat-appli').click(function() {
			if($('#vat-appli').prop('checked')) {
				$("#vat-rate").attr("required", true);
				$("#vat-rate").removeAttr('disabled');

			}
			else{
				$("#vat-rate").val("");
				$("#vat-rate").prop("disabled", true);
				$("#vat-rate").removeAttr('required');

			}
		});
		$('#whtx-appli').click(function() {
			if($('#whtx-appli').prop('checked')) {
				$("#whtx-rate").removeAttr('disabled');
				$("#whtx-rate").attr("required", true);
			}
			else{
				$("#whtx-rate").val("");
				$("#whtx-rate").prop("disabled", true);
				$("#whtx-rate").removeAttr('required');

			}
		});
	}
	function refreshTaxes()
	{
		if($('#vat-appli').prop('checked')) {
			$("#vat-rate").removeAttr('disabled');
			$("#vat-rate").prop("required", true);
		}
		else{
			$("#vat-rate").val("");
			$("#vat-rate").prop("disabled", true);
			$("#vat-rate").removeAttr('required');

		}
		if($('#whtx-appli').prop('checked')) {
			$("#whtx-rate").removeAttr('disabled');
			$("#whtx-rate").prop("required", true);
		}
		else{
			$("#whtx-rate").val("");
			$("#whtx-rate").prop("disabled", true);
			$("#whtx-rate").removeAttr('required');

		}

	}

	function confirmAcctypeDel(button){
		var acctype = JSON.parse(decodeURI($(button).data("acctype")));
		bootbox.confirm("Are you sure want to delete "+acctype["accShtDesc"]+"?", function(result) {
			if(result){
				$.ajax({
					type: 'GET',
					url:  'deleteAcctype/' + acctype["accId"],
					dataType: 'json',
					async: true,
					success: function(result) {
						Swal.fire({
                    title: 'Success',
                    text: 'Record Deleted Successfully',
                    icon: 'success'
                });
						$('#accounttbl').DataTable().ajax.reload();
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



	function createAccTypesTable(){
		var url = "acctypes";
		var table = $('#accounttbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 20, 30], [10, 20, 30] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "accountType",
					"render":function(data,type,full,meta){
						if(full.accountType){
							if(full.accountType==="INS"){
								return "Insurance Company";
							}
							else if(full.accountType==="IA"){
								return "Inhouse Agents";
							}
							else if(full.accountType==="SF"){
								return "Self Fund";
							}
							else if(full.accountType==="DR"){
								return "Direct Business";
							}
							else if(full.accountType==="SUB"){
								return "Sub Agents";
							}
							else if(full.accountType==="RN"){
								return "Reinsurance Company";
							}
							else if(full.accountType==="BRK"){
								return "Broker";
							}
							else if(full.accountType==="INT"){
								return "Introducer";
							}
							else if(full.accountType==="MRK"){
								return "Marketer";
							}

						}
						else{
							return "No Account Type";
						}
					}

				},
				{ "data": "accShtDesc" },
				{ "data": "accName" },
				{ "data": "commRate",
					"render":function(data,type,full,meta){
						return UTILITIES.currencyFormat(full.commRate);
					}
				},
				{ "data": "vatAppli",
					"render":function(data,type,full,meta){
						if(full.vatAppli) return "Yes";
						else
							return "No";
					}

				},
				{ "data": "vatRate",
					"render":function(data,type,full,meta){
						return UTILITIES.currencyFormat(full.vatRate);
					}
				},
				{ "data": "whtxAppl",
					"render":function(data,type,full,meta){
						if(full.whtxAppl) return "Yes";
						else
							return "No";
					}
				},
				{ "data": "whtaxVal" ,
					"render":function(data,type,full,meta){
						return UTILITIES.currencyFormat(full.whtaxVal);
					}
				},
				{
					"data": "accId",
					"render": function ( data, type, full, meta ) {
						console.log(full);
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-acctype='+encodeURI(JSON.stringify(full)) + '  onclick="editAcctTypes(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
//				{
//					"data": "accId",
//					"render": function ( data, type, full, meta ) {
//						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-acctype='+encodeURI(JSON.stringify(full)) + '  onclick="confirmAcctypeDel(this);" disabled><i class="fa fa-trash-o"></button>';
//					}
//
//				},
			]
		} );
		return table;
	}

</script>