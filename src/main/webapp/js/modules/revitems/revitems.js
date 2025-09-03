var UTILITIES = UTILITIES || {};

$(function(){

	$(document).ready(function() {
		createProdGrpSelect();
		getRevenueItemsModal();
		callSaveBulkRevItems();
		updateRevenueItems();
		createRevenueItemTable();
	});
});


function createGlAccountsLov(){
	if($("#dr-account-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "dr-account-frm",
			sort : 'name',
			change: function(e, a, v){
				$("#dr-account-code").val(e.added.coId);
				$("#dr-account-name").val(e.added.name);
				$("#dr-account-cd").val(e.added.code);
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
				var id  = $('#dr-account-code').val();
				var name = $("#dr-account-name").val();
				var code = $("#dr-account-cd").val();
				var data = {name:name,coId:id,code:code};
				callback(data);
			},
			id: "coId",
			width:"250px",
			placeholder:"Select Account"

		});
	}
	if($("#cr-account-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "cr-account-frm",
			sort : 'name',
			change: function(e, a, v){
				$("#cr-account-code").val(e.added.coId);
				$("#cr-account-name").val(e.added.name);
				$("#cr-account-cd").val(e.added.code);

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
				var id  = $('#cr-account-code').val();
				var name = $("#cr-account-name").val();
				var code = $("#dr-account-cd").val();

				var data = {name:name,coId:id,code:code};
				callback(data);
			},
			id: "coId",
			width:"250px",
			placeholder:"Select Account"

		});
		if ($("#cr-account-frm").attr("data-select2-disabled") === "true") {
			$("#cr-account-frm").select2("enable", false);
		}
	}
}

function createRevenueItems(prgCode){
	var url = "revenueitems/"+prgCode;

	var currTable = $('#revenueItemsList').DataTable( {
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
		},
		lengthMenu: [ [5,10], [5,10] ],
		pageLength: 5,
		destroy: true,
		dom: 'Bfrtip',
		"columns": [
			{ "data": "item",
				"render": function ( data, type, full, meta ) {

					return full.item;
				}
			},
			{ "data": "item",
				"render": function ( data, type, full, meta ) {

					return UTILITIES.getRevDesc(full.item);
				}
			},
			{ "data": "drAccount",
				"render": function ( data, type, full, meta ) {
					if(full.drAccount)
						return full.drAccount.code+" - "+full.drAccount.name;
					else return "";
				}
			},
			{ "data": "crAccount",
				"render": function ( data, type, full, meta ) {
					if(full.crAccount)
						return full.crAccount.code+" - "+full.crAccount.name;
					else return "";
				}
			},
			{
				"data": "revenueId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-revitems='+encodeURI(JSON.stringify(full)) + ' value="Edit" onclick="editRevenueItems(this);"><i class="fa fa-pencil-square-o"></button>';
				}

			},
			{
				"data": "revenueId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-revitems='+encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="deleteRevenueItems(this);"><i class="fa fa-trash-o"></button>';
				}

			},
		]
	} );
	return currTable;
}


function createRevenueItemTable(){
	var url = "revenueitems";

	var currTable = $('#revenueItemsGlobalList').DataTable( {
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
		},
		lengthMenu: [ [5,10], [5,10] ],
		pageLength: 5,
		destroy: true,
		dom: 'Bfrtip',
		"columns": [
			{ "data": "item",
				"render": function ( data, type, full, meta ) {

					return full.item;
				}
			},
			{ "data": "item",
				"render": function ( data, type, full, meta ) {

					return UTILITIES.getRevDesc(full.item);
				}
			},
			{ "data": "drAccount",
				"render": function ( data, type, full, meta ) {
					if(full.drAccount)
						return full.drAccount.code+" - "+full.drAccount.name;
					else return "";
				}
			},
			{ "data": "crAccount",
				"render": function ( data, type, full, meta ) {
					if(full.crAccount)
						return full.crAccount.code+" - "+full.crAccount.name;
					else return "";
				}
			},
			{
				"data": "revenueId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-revitems='+encodeURI(JSON.stringify(full)) + ' value="Edit" onclick="editRevenueItems(this);"><i class="fa fa-pencil-square-o"></button>';
				}

			},
			{
				"data": "revenueId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-revitems='+encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="deleteGrpRevenueItems(this);"><i class="fa fa-trash-o"></button>';
				}

			},
		]
	} );
	return currTable;
}

function deleteRevenueItems(button){
	var revitems = JSON.parse(decodeURI($(button).data("revitems")));
	bootbox.confirm("Are you sure want to delete "+UTILITIES.getRevDesc(revitems["item"])+"?", function(result) {
		if(result){
			$.ajax({
				type: 'GET',
				url:  'deleteRevenueItem/' + revitems["revenueId"],
				dataType: 'json',
				async: true,
				success: function(result) {
					Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
					$('#revenueItemsList').DataTable().ajax.reload();
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

function deleteGrpRevenueItems(button){
	var revitems = JSON.parse(decodeURI($(button).data("revitems")));
	bootbox.confirm("Are you sure want to delete "+UTILITIES.getRevDesc(revitems["item"])+"?", function(result) {
		if(result){
			$.ajax({
				type: 'GET',
				url:  'deleteRevenueItem/' + revitems["revenueId"],
				dataType: 'json',
				async: true,
				success: function(result) {
					Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
					$('#revenueItemsGlobalList').DataTable().ajax.reload();
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



function editRevenueItems(button){
	var revitems = JSON.parse(decodeURI($(button).data("revitems")));
	$("#rev-code").val(revitems["revenueId"]);
	if(revitems["prodGroup"])
		$("#rev-prgcode-code").val(revitems["prodGroup"].prgCode);
	else $("#rev-prgcode-code").val('');
	$("#rev-item-id").val(revitems["item"]);
	$("#rev-item-desc").val(UTILITIES.getRevDesc(revitems["item"]));

	var isDisabled = revitems["item"] === "WHTX" || revitems["item"] === "UC";
	$("#cr-account-frm").attr("data-select2-disabled", isDisabled);
	createGlAccountsLov();
	//$("#rev-dr-acc").val(revitems["drAccount"]);
	//$("#rev-cr-acc").val(revitems["crAccount"]);
	$('#revitemsModal').modal({
		backdrop: 'static',
		keyboard: true
	});
}



function createProdGrpSelect(){
	if($("#prd-group").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "prd-group",
			sort : 'subDesc',
			change: function(e, a, v){
				$("#prg-pk").val(e.added.subId);
				createRevenueItems(e.added.subId);
			},
			formatResult : function(a)
			{
				return a.subDesc
			},
			formatSelection : function(a)
			{
				return a.subDesc
			},
			initSelection: function (element, callback) {
			},
			id: "subId",
			placeholder:"Select Sub Class",
		});
	}
}

function getRevenueItemsModal(){

	$("#btn-add-rev-item").click(function(){
		if($("#prg-pk").val() === ''){
			bootbox.alert('Select Sub Class to continue...');
			return;
		}
		$("#saverevItemsBtn").hide();
		$("#saverevItemsBtn2").show();
		populateRevItems("N");
	});

	$("#btn-add-bulk-rev-item").click(function () {
		$("#saverevItemsBtn").show();
		$("#saverevItemsBtn2").hide();
		$("#rev-prg-code").val('');
		populateRevItems("Y");
	})
}

function populateRevItems(edit){
	$.ajax({
		type: 'GET',
		url:  'unassigrevitems',
		dataType: 'json',
		data: {"prgCode": $("#prg-pk").val()},
		async: true,
		success: function(result) {
			console.log(result);
			$("#revItemsTbl tbody").each(function(){
				$(this).remove();
			});
			var counter =1;
			for(var res in result){
				var checked = result[res].checked;
				var drAccountsLov = "";
				if(result[res].drAccount) {
					drAccountsLov = '<input type="hidden" id="dr-acc-id' + counter + '" class="debitAcc" value="' + result[res].drAccount.coId + '">' +
						'<input type="hidden" id="dr-acc-name' + counter + '" value="' + result[res].drAccount.name + '">' +
						'<input type="hidden" id="dr-acc-code' + counter + '" value="' + result[res].drAccount.code + '">' +
						'  <div id="dr-acc-frm' + counter + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/setups/revitems/selGlAccount" </div> ';
				}
				else{
					drAccountsLov = '<input type="hidden" id="dr-acc-id' + counter + '" class="debitAcc" value="">' +
						'<input type="hidden" id="dr-acc-name' + counter + '" value="">' +
						'<input type="hidden" id="dr-acc-code' + counter + '" value="">' +
						'  <div id="dr-acc-frm' + counter + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/setups/revitems/selGlAccount" </div> ';
				}
				var isDisabled = result[res].code === "WHTX" || result[res].code === "UC";
				var crAccountsLov = "";
				if(result[res].crAccount) {
					crAccountsLov = '<input type="hidden" id="cr-acc-id' + counter + '" class="credAcc" value="' + result[res].crAccount.coId + '">' +
						'<input type="hidden" id="cr-acc-name' + counter + '"  value="' + result[res].crAccount.name + '">' +
						'<input type="hidden" id="cr-acc-code' + counter + '"  value="' + result[res].crAccount.code + '">' +
						'  <div id="cr-acc-frm' + counter + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/setups/revitems/selGlAccount" </div> ';
				}
				else{
					crAccountsLov = '<input type="hidden" id="cr-acc-id' + counter + '" class="credAcc" value="">' +
						'<input type="hidden" id="cr-acc-name' + counter + '"  value="">' +
						'<input type="hidden" id="cr-acc-code' + counter + '"  value="">' +
						'  <div id="cr-acc-frm' + counter + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/setups/revitems/selGlAccount" </div> ';
				}
				if(checked) {
					var markup = "<tr><td><input type='hidden' class='revcode' value='" + result[res].revCode + "'><input type='checkbox' class='checked-val' checked></td><td><input type='text' class='code form-control' value='" + result[res].code + "' readonly></td><td>" + result[res].value + "</td><td>"+drAccountsLov+"</td><td>"+crAccountsLov+"</td></tr>";
					$("#revItemsTbl").append(markup);
				}
				else{
					var markup = "<tr><td><input type='hidden' class='revcode' value='" + result[res].revCode + "'><input type='checkbox' class='checked-val'></td><td><input type='text' class='code form-control' value='" + result[res].code + "' readonly></td><td>" + result[res].value + "</td><td>"+drAccountsLov+"</td><td>"+crAccountsLov+"</td></tr>";
					$("#revItemsTbl").append(markup);
				}
				Select2Builder.initAjaxSelect2({
					containerId : "dr-acc-frm"+counter,
					sort : 'name',
					counter: counter,
					change: function(e, a, v){
						$.each($(this), function() {
							var key = Object.keys(this)[2];
							var value = this[key];
							var drId = "#dr-acc-id"+value;
							$(drId).val(e.added.coId);
						});
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
						var code  = $('#dr-acc-id'+counter).val();
						var name = $("#dr-acc-name"+counter).val();
						var acccode = $("#dr-acc-code"+counter).val();
						var data = {code:acccode,coId:code,name:name};
						callback(data);

					},
					id: "coId",
					width:"200px",
					placeholder:"Select Account"

				});

					Select2Builder.initAjaxSelect2({
						containerId: "cr-acc-frm" + counter,
						sort: 'name',
						counter: counter,
						change: function (e, a, v) {
							$.each($(this), function () {
								var key = Object.keys(this)[2];
								var value = this[key];
								var drId = "#cr-acc-id" + value;
								$(drId).val(e.added.coId);
							});
						},
						formatResult: function (a) {
							return a.code + " - " + a.name;
						},
						formatSelection: function (a) {
							return a.code + " - " + a.name;
						},
						initSelection: function (element, callback) {
							var code = $('#cr-acc-id' + counter).val();
							var name = $("#cr-acc-name" + counter).val();
							var acccode = $("#cr-acc-code" + counter).val();
							var data = {code: acccode, coId: code, name: name};
							callback(data);
						},
						id: "coId",
						width: "200px",
						placeholder: "Select Account"

					});
					if (isDisabled) {
						$("#cr-acc-frm" + counter).select2("enable", false);
					}


				counter = counter+1;

			}
			if(edit==='N') {
				$("#rev-prg-code").val($("#prg-pk").val());
			}
			else{
				$("#rev-prg-code").val('');
			}
			$('#bulkRevItemsModal').modal({
				backdrop: 'static',
				keyboard: true
			})
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

function getRevenueItemsModel(){
	var arr = [];
	$("#revItemsTbl tr").each(function(row,tr){
		var checked =$(this).find('.checked-val').is(':checked');
		var code   = $(this).find('.code').eq(0).val();
		var drAccount   = $(this).find('.debitAcc').eq(0).val();
		var crAccount   = $(this).find('.credAcc').eq(0).val();
		var revCode =  $(this).find('.revcode').eq(0).val();
		arr.push({
			checked: checked,
			code: code,
			drAccountId: drAccount,
			crAccountId:crAccount,
			revCode:revCode
		});
	});

	console.log(arr);

	return arr;

}

function callSaveBulkRevItems(){

	$("#saverevItemsBtn").click(function(){
		saveBulkRevItems("N");
	})
	$("#saverevItemsBtn2").click(function(){
		saveBulkRevItems("Y");
	})

}


function saveBulkRevItems(type){

	var arr = getRevenueItemsModel();
	if(arr.length==0){
		bootbox.alert("No Revenue Items to save")
		return false;
	}
	arr.shift();
	console.log(arr);
	var $currForm = $('#bulk-rev-items-form');
	var currValidator = $currForm.validate();
	if (!$currForm.valid()) {
		return;
	}
	var data = {};
	$currForm.serializeArray().map(function(x) {
		data[x.name] = x.value;
	});
	var url = "createBulkRevItems";
	data.items = arr;


	$.ajax({
		url : url,
		type : "POST",
		data : JSON.stringify(data),
		success : function(s) {
			Swal.fire({
				title: 'Success',
				text: 'Record Created Successfully',
				icon: 'success'
			});
			if(type && type==='N'){
				$('#revenueItemsGlobalList').DataTable().ajax.reload();
			}
			else
				$('#revenueItemsList').DataTable().ajax.reload();
			$('#bulkRevItemsModal').modal('hide');
			arr = [];
		},
		error : function(jqXHR, textStatus, errorThrown) {
			Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
		},
		dataType : "json",
		contentType : "application/json"
	});

}





function updateRevenueItems(){
	var $currForm = $('#update-rev-items-form');
	var currValidator = $currForm.validate();


	$('#updateRevItemsBtn').click(function(){
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createRevenueItem";
		var request = $.post(url, data );
		request.success(function(){
			Swal.fire({
					title: 'Success',
					text: 'Record created/updated Successfully',
					icon: 'success'
				});
			currValidator.resetForm();
			if($("#rev-prgcode-code").val() !== ''){
				$('#revenueItemsList').DataTable().ajax.reload();
			}
			else $('#revenueItemsGlobalList').DataTable().ajax.reload();

			$('#revitemsModal').modal('hide');
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




