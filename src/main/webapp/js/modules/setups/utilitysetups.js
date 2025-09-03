var UTILITIES = UTILITIES || {};
$(function(){
	$(document).ready(function() {
		createPaymentModes();
		newPaymentModes();
		savePaymentModes();
		$('#min-val,#max-val').number( true, 2 );
		createClientTypes();
		createClientTitles();
		newClientTypes();
		saveClientTypes();
		newClientTitles();
		saveClientTitle();
		$(document).ajaxStart(function () {
			$("#savePaymentModes,#saveClientType,#saveClientTitle").attr("disabled", true);
		});
		$(document).ajaxComplete(function () {
			$("#savePaymentModes,#saveClientType,#saveClientTitle").attr("disabled", false);
		});
	})
	
});


function savePaymentModes(){
	var $paymodesForm = $('#pay-modes-form');
	var validator = $paymodesForm.validate();
	 $('#paymentModesModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#pay-modes-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
	 });
	 
	 $('#savePaymentModes').click(function(){
		 if (!$paymodesForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paymodesForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createPaymentModes";
	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
               });
				$('#paymodetbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#paymentModesModal').modal('hide');
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


function newPaymentModes(){
	$("#btn-add-modes").on("click", function(){
		$('#pay-modes-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
		$('#paymentModesModal').modal('show');
	});
}

function newClientTypes(){
	$("#btn-add-clnt-types").on("click", function(){
		$('#client-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
		$('#clntTypeModesModal').modal('show');
	});
}

function newClientTitles(){
	$("#btn-add-clnt-title").on("click", function(){
		$('#client-title-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
		$('#clntTitleModesModal').modal('show');
	});
}

function saveClientTitle(){
	var $paymodesForm = $('#client-title-form');
	var validator = $paymodesForm.validate();
	 $('#clntTitleModesModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#client-title-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
	 });
	 
	 $('#saveClientTitle').click(function(){
		 if (!$paymodesForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paymodesForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "creatClientTitle";
	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#client-title-tbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#clntTitleModesModal').modal('hide');
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


function saveClientTypes(){
	var $paymodesForm = $('#client-type-form');
	var validator = $paymodesForm.validate();
	 $('#clntTypeModesModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#client-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
	 });
	 
	 $('#saveClientType').click(function(){
		 if (!$paymodesForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paymodesForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "creatClientType";
	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#client-type-tbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#clntTypeModesModal').modal('hide');
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


function createPaymentModes(){
	var url = "allpaymentModes";
	  var table = $('#paymodetbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 15], [10, 15] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "pmShtDesc" },
				{ "data": "pmDesc" },
				{ "data": "pmMinValue",
				  "render":function(data,type,full,meta){
					  return UTILITIES.currencyFormat(full.pmMinValue);
				  }
				},
				{ "data": "pmMaxValue",
				  "render":function(data,type,full,meta){
					  return UTILITIES.currencyFormat(full.pmMaxValue);
				  }
				},
				{ "data": "supportsCheque",
					"render":function(data,type,full,meta){
						if(full.supportsCheque && full.supportsCheque==='Y'){
							return "Yes"
						}
						else{
							return 'No';
						}
					}
				},
				{ 
					"data": "pmId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-modes='+encodeURI(JSON.stringify(full)) + ' onclick="editPaymentModes(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "pmId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-modes='+encodeURI(JSON.stringify(full)) + ' onclick="confirmModesDel(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return table;
}


function editPaymentModes(button){
	var modes = JSON.parse(decodeURI($(button).data("modes")));
	$("#sht-desc").val(modes["pmShtDesc"]);
	$("#pm-id").val(modes["pmId"]);
	$("#description").val(modes["pmDesc"]);
	$("#min-val").val(modes["pmMinValue"]);
	$("#max-val").val(modes["pmMaxValue"]);
	$("#py-support-check").val(modes["supportsCheque"]);
	$('#paymentModesModal').modal('show');
}

function editClientType(button){
	var clntTypes = JSON.parse(decodeURI($(button).data("clienttypes")));
	$("#type-id").val(clntTypes["typeId"]);
	$("#clnt-type").val(clntTypes["clientType"]);
	$("#type-desc").val(clntTypes["typeDesc"]);
	$('#clntTypeModesModal').modal('show');
}

function editClientTitle(button){
	var clntTitle = JSON.parse(decodeURI($(button).data("clienttitle")));
	$("#title-id").val(clntTitle["titleId"]);
	$("#title-desc").val(clntTitle["titleName"]);
	$('#clntTitleModesModal').modal('show');
}

function confirmClientTypeDel(button){
	var clntTypes = JSON.parse(decodeURI($(button).data("clienttypes")));
	bootbox.confirm("Are you sure want to delete "+clntTypes["typeDesc"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteClientType/' + clntTypes["typeId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#client-type-tbl').DataTable().ajax.reload();
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

function confirmClientTitleDel(button){
	var clntTitle = JSON.parse(decodeURI($(button).data("clienttitle")));
	bootbox.confirm("Are you sure want to delete "+clntTitle["titleName"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteClientTitle/' + clntTitle["titleId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#client-title-tbl').DataTable().ajax.reload();
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

function confirmModesDel(button){
	var modes = JSON.parse(decodeURI($(button).data("modes")));
	bootbox.confirm("Are you sure want to delete "+modes["pmShtDesc"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deletePayMode/' + modes["pmId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#paymodetbl').DataTable().ajax.reload();
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


function createClientTitles(){
	var url = "clienttitleslist";
	  var table = $('#client-title-tbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 15], [10, 15] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "titleName" },
				{ 
					"data": "titleId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-clienttitle='+encodeURI(JSON.stringify(full)) + ' onclick="editClientTitle(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "titleId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clienttitle='+encodeURI(JSON.stringify(full)) + ' onclick="confirmClientTitleDel(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return table;
}

function createClientTypes(){
	var url = "clienttypeslist";
	  var table = $('#client-type-tbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 15], [10, 15] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "clientType",
					"render": function ( data, type, full, meta ) {
						if(full.clientType){
							if(full.clientType ==='I'){
								return "Individual";
							}
							else if(full.clientType ==='C'){
								return "Corporate";
							}
						}
						else{
							return 'Invalid Client Type'
						}
					}
				},
				{ "data": "typeDesc" },
				{ 
					"data": "typeId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-clienttypes='+encodeURI(JSON.stringify(full)) + ' onclick="editClientType(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "typeId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clienttypes='+encodeURI(JSON.stringify(full)) + ' onclick="confirmClientTypeDel(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return table;
}