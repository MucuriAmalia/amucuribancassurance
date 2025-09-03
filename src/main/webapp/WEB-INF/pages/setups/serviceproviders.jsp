<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<div class="x_panel">
   <div class="x_title">
		<h2><i class="fa fa-bars"></i> Service Provider Types</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
	<form id="prg-grp-form" class="form-horizontal">
	    <div class="item form-group form-required">
				<div class="col-md-6">
				   <label for="prg-id" class="col-md-5 label-align">Select
					   Service Provider Type</label>

				<div class="col-md-7">
		                     <input type="hidden" id="prg-id" rv-value="prggrp.prgCode"/>
		                     <input type="hidden" id="prg-name">
		                        <div id="serv-provider_types" class="form-control"
				                                 select2-url="<c:url value="/protected/claims/selprovidertypes"/>" >
				                                 
				               </div> 
				               
				</div>
				</div>
				<div class="col-md-2">
				    <button type="button" class="btn btn-info" id="btn-add-group">New</button>
			        <button type="button" class="btn btn-info" id="btn-edit-group">Edit</button>
				    
				</div>
				
				<div class="col-md-2">
				    
				</div>
				
				</div>
			</form>
			</div>
		</div>
		<div class="x_panel">
		<div class="x_title">
		<h2>Service Providers</h2>
		<ul class="nav navbar-right panel_toolbox">
			<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
	<div class="x_content">
         <input type="hidden" id="prg-pk">
		<button type="button" class="btn btn-info" id="btn-add-prod">New</button>
		<div class="table-responsive">
		<table id="providList" class="table table-striped" style="width: 100%">
			<thead>
				<tr class="headings">

					<th>Name</th>
					<th>Phone No</th>
					<th>Email</th>
					<th>Created By</th>
					<th>Created Date</th>
					<th width="5%"></th>
					<th width="5%"></th>
				</tr>
			</thead>
		</table>
			</div>
	</div>
	</div>

<div class="modal fade" id="servProviderTypesModal" tabindex="-1" role="dialog"
	 aria-labelledby="servProviderTypesLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			    <h4 class="modal-title" id="servProviderTypesLabel">
                    Add Service Provider Type
                </h4>
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body">

				<form id="serv-provider-form" class="form-horizontal">
					<input type="hidden" class="form-control" id="type-code" name="typeId">
					<div class="item form-group">
						<label for="provider-type" class="col-md-3 label-align">Provider Type:</label>

						<div class="col-md-8">
							<input type="text" class="form-control" id="provider-type"
								   name="providerType"  required>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button  id="delproviderGrpBtn"
						 type="button" class="btn btn-danger">
					Delete
				</button>
				<button data-loading-text="Saving..." id="saveserviceprovidertypeBtn"
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

<div class="modal fade" id="editServProvidersModal" tabindex="-1" role="dialog"
	 aria-labelledby="editServProvidersModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			    <h4 class="modal-title" id="editServProvidersModalLabel">
                    Add/Edit Service Providers
                </h4>
				<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>

			</div>
			<div class="modal-body">

				<form id="provider-form" class="form-horizontal">
					<input type="hidden" id="providerId-pk" name="providerId">
					<input type="hidden" class="form-control" id="providerTypeId-pk" name="providerTypeId">
					<div class="item form-group">
						<label for="provider-name" class="col-md-3 label-align">Name</label>

						<div class="col-md-8">
							<input type="text" class="form-control" id="provider-name" name="name"
								   required>
						</div>
					</div>
					<div class="item form-group">
						<label for="phoneNumber" class="col-md-3 label-align">Phone No</label>

						<div class="col-md-8">
							<input type="text" class="form-control" id="phoneNumber" name="phoneNumber" required>
						</div>
					</div>
					<div class="item form-group">
						<label for="email" class="col-md-3 label-align">Email</label>

						<div class="col-md-8">
							<input type="email" class="form-control" id="email" name="email">
						</div>
					</div>


				</form>
			</div>
			<div class="modal-footer">
				<button data-loading-text="Saving..." id="saveServiceProviderBtn"
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

<script type="application/javascript">


	$(function() {

		$(document).ready(function () {

			addEditServiceProviderType();
			createServiceProviderTypes();
			createServiceProviders();
			saveProviders();
			deleteServiceProviderType();

			$("#btn-add-prod").click(function(){
				if($("#type-code").val() != ''){

					$('#provider-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password], textarea,select").val("");
					$('#editServProvidersModal').modal({
						backdrop: 'static',
						keyboard: true
					});
				}
				else{
					bootbox.alert("Select Service Provider Type to continue");
				}
			});

			$("#btn-add-group").click(function(){
				$('#serv-provider-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$('#delproviderGrpBtn').hide();
				$('#servProviderTypesModal').modal({
					backdrop: 'static',
					keyboard: true
				});
			});

			$("#btn-edit-group").click(function(){
				if($("#type-code").val() != ''){
					$('#delproviderGrpBtn').show();
					$('#servProviderTypesModal').modal({
						backdrop: 'static',
						keyboard: true
					})
				}
				else{
					bootbox.alert("Select Service Provider to Edit");
				}
			});


		})
	});

	function deleteServiceProviderType(){
		$("#delproviderGrpBtn").click(function(){
			if($("#type-code").val() !== ''){
				bootbox.confirm("Are you sure want to delete the record?", function(result) {
					if(result){
						$.ajax({
							type: 'GET',
							url:  'deleteServiceProviderType/' + $("#type-code").val(),
							dataType: 'json',
							async: true,
							success: function(result) {
								Swal.fire({
								title: 'Success',
								text: 'Record Deleted Successfully',
								icon: 'success'
							  });
								$("#type-code").val("");
								createServiceProviderTypes();
								$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],textarea,select").val("");
								$('#servProviderTypesModal').modal('hide');
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
			else{
				bootbox.alert("No Record to delete");
			}
		});
	}

	function createServiceProviderTypes(){
		if($("#serv-provider_types").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "serv-provider_types",
				sort : 'providerType',
				change: function(e, a, v){
					$("#type-code").val(e.added.typeId);
					$("#providerTypeId-pk").val(e.added.typeId);
					$("#provider-type").val(e.added.providerType);
					var url = "getServiceProviders/"+$("#type-code").val();
					$('#providList').DataTable().ajax.url( url ).load();
				},
				formatResult : function(a)
				{
					return a.providerType
				},
				formatSelection : function(a)
				{
					return a.providerType
				},
				initSelection: function (element, callback) {

				},
				id: "typeId",
				placeholder:"Select Service Provider Type",
			});
		}
	}

	function saveProviders(){
		var $classForm = $('#provider-form');
		var validator = $classForm.validate();
		$('#saveServiceProviderBtn').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createServProviders";
			var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#providList').DataTable().ajax.reload();
				validator.resetForm();
				$('#product-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password], textarea,select").val("");
				$('#editServProvidersModal').modal('hide');
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

	function addEditServiceProviderType(){
		var $currForm = $('#serv-provider-form');
		var currValidator = $currForm.validate();


		$('#saveserviceprovidertypeBtn').click(function(){
			if (!$currForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createServProviderTypes";
			var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				currValidator.resetForm();

				$("#type-code").val("");
				// createProducts();
				$('#serv-provider_types').select2('val', null);
				createServiceProviderTypes();
				$('#serv-provider-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$('#servProviderTypesModal').modal('hide');
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

	function createServiceProviders(){
		var url = "getServiceProviders/0";
		if($("#type-code").val() != ''){
			url = "getServiceProviders/"+$("#type-code").val();
		}
		return $('#providList').DataTable({
			"processing": true,
			"serverSide": true,
			autoWidth: true,
			"ajax": {
				'url': url,
			},
			lengthMenu: [[5], [5]],
			pageLength: 5,
			destroy: true,
			"columns": [
				{"data": "name"},
				{"data": "phoneNumber"},
				{"data": "email"},
				{"data": "createdBy"},
				{
					"data": "createdDate",
					"render": function (data, type, full, meta) {
						if (full.createdDate)
							return moment(full.createdDate).format('DD/MM/YYYY');
						else return "";
					}
				},
				{
					"data": "providerId",
					"render": function (data, type, full, meta) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm"  data-providers=' + encodeURI(JSON.stringify(full)) + ' onclick="editProviders(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{
					"data": "providerId",
					"render": function (data, type, full, meta) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-providers=' + encodeURI(JSON.stringify(full)) + ' onclick="confirmServiceProviderDel(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		});
	}

	function editProviders(button){
		var providers = JSON.parse(decodeURI($(button).data("providers")));
		$("#providerId-pk").val(providers['providerId']);
		$("#providerTypeId-pk").val(providers['providerTypeId']);
		$("#provider-name").val(providers['name']);
		$("#phoneNumber").val(providers['phoneNumber']);
		$("#email").val(providers['email']);
		$('#editServProvidersModal').modal({
			backdrop: 'static',
			keyboard: true
		});
	}

	function confirmServiceProviderDel(button){
		var providers = JSON.parse(decodeURI($(button).data("providers")));
		bootbox.confirm("Are you sure want to delete "+providers["name"]+"?", function(result) {
			if(result){
				$.ajax({
					type: 'GET',
					url:  'deleteServiceProvider/' + providers["providerId"],
					dataType: 'json',
					async: true,
					success: function(result) {
						Swal.fire({
                    title: 'Success',
                    text: 'Record Deleted Successfully',
                    icon: 'success'
                });
						$('#providList').DataTable().ajax.reload();
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