$(function(){

	$(document).ready(function() {
		
		$(".datepicker-input").each(function() {
		    $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });
		    
		});
		populateClientLov();
		createAccountsForSel();
		createProductForSel();
		populateUserBranches();
		populateBinderLov();
		populateRisksLov();
		makeReadyRenewal();
		authBatchRenewals();

		
		if($("#from-date").val()!=='' && $("#wet-date").val()!==''){
				renewalInProgress();
			}
			
			
		
		
		$("#btn-search-renewals").on('click', function(){
			if($("#from-date").val()===''){
				bootbox.alert("Wef Date is mandatory");
				return;
			}
			if($("#wet-date").val()===''){
				bootbox.alert("Wet Date is mandatory");
				return;
			}
			renewalInProgress();
		})
		
	});
});

function authBatchRenewals(){
	$('#btn-auth-renewal').on('click', function(e){
		var arr = [];
		var currTable = $('#example').dataTable();
		currTable.$('input[type="checkbox"]').each(function(){
			if(this.checked){
				arr.push(this.value);
			}

		});

		if(arr.length==0){
			bootbox.alert("No Record Selected to Authorize..");
			return;
		}
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		var $currForm = $('#renewal-frm');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}
		var data = {};
		$currForm.serializeArray().map(function(x) {
			data[x.name] = x.value;
		});
		var url = "authoriseBatchRenewals";
		data.renPolicies = arr;
		$.ajax({
			url : url,
			type : "POST",
			data : JSON.stringify(data),
			success : function(s) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
					title: 'Success',
					text: 'Renewal Authorised Successfully...This is a background job and will take sometime',
					icon: 'success'
				});
				$('#example').DataTable().ajax.reload(null,false);
				arr = [];

			},
			error : function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
			},
			dataType : "json",
			contentType : "application/json"
		});
	});
}

function makeReadyRenewal(){
	$('#btn-make-ready').on('click', function(e){
		var arr = [];
		var currTable = $('#example').dataTable();
		currTable.$('input[type="checkbox"]').each(function(){
			if(this.checked){
				arr.push(this.value);
			}

		});

		if(arr.length==0){
			bootbox.alert("No Record Selected to Submit..");
			return;
		}
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		var $currForm = $('#renewal-frm');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}
		var data = {};
		$currForm.serializeArray().map(function(x) {
			data[x.name] = x.value;
		});
		var url = "makeReadyBatchRenewals";
		data.renPolicies = arr;
		$.ajax({
			url : url,
			type : "POST",
			data : JSON.stringify(data),
			success : function(s) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
					title: 'Success',
					text: 'Renewal Made Ready in Batch Successfully...This is a background job and will take sometime',
					icon: 'success'
				});
				$('#example').DataTable().ajax.reload(null,false);
				arr = [];

			},
			error : function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
			},
			dataType : "json",
			contentType : "application/json"
		});
	});
}

function renewalInProgress(){
   var url = "searchRenewalProgress";
   var table = $('#example').DataTable({
	        "processing": true,
			"serverSide": true,
			autoWidth: true,
			"searching": false,
			"ajax": {
				'url': url,
				'data':{
					'wefDate': $("#from-date").val(),
					'wetDate':  $("#wet-date").val(),
					'productCode': $("#product-search-number").val(),
					'branchId': $("#brn-id").val(),
					'agentId': $("#agent-search-number").val(),
					'bindCode': $("#risk-binder-code").val(),
					'clientId':$("#rev-search-name").val(),
					'riskId':$("#risk-search-id").val()
				},
			},
			lengthMenu: [ [10, 20,50], [10, 20,50] ],
			pageLength: 10,
			destroy: true,
	       'columnDefs': [{
		   'targets': 0,
		   'searchable':false,
		   'orderable':false,
		   'render': function (data, type, full, meta){
			   return '<input type="checkbox" name="id[]" value="'
				   + $('<div/>').text(data).html() + '">';
		   }
	   }],
        order: [[ 1, 'asc' ]],
			"columns": [
			            { 
					"data": "policyId"

				},
                { "data": "polNo" },
                { "data": "polRevNo" },
				{ "data": "policyId",
				  "render": function ( data, type, full, meta ) {
					  return full.product.proDesc;
				  }
				},
				{ "data": "wefDate",
					"render": function ( data, type, full, meta ) {
				      return moment(full.wefDate).format('DD/MM/YYYY');
				  }
				},
				{ "data": "wetDate" ,
					"render": function ( data, type, full, meta ) {
					      return moment(full.wetDate).format('DD/MM/YYYY');
					  }
				},
				{ "data": "policyId",
				  "render": function ( data, type, full, meta ) {
					  return full.client.fname+" "+full.client.otherNames;
				  }
				},
				{ "data": "agent",
					  "render": function ( data, type, full, meta ) {
							   
							  return full.agent.name;
					   }	
				},
				{ "data": "transCurrency",
					  "render": function ( data, type, full, meta ) {
							   
							  return full.transCurrency.curIsoCode;
					   }	
				},
				{ "data": "authStatus",
					  "render": function ( data, type, full, meta ) {
							   if(full.authStatus){
								   if(full.authStatus==="D")
									   return "Draft";
								   else if(full.authStatus==="R")
									   return "Ready";
								   else if(full.authStatus==="A")
									   return "Authorized";
							   }
							   else
							 return "Draft";
					   }	
				},
				{ 
					"data": "policyId",
					"render": function ( data, type, full, meta ) {
						return '<form action="editrenewals" method="post"><input type="hidden" name="id" value='+full.policyId+'><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="Edit" ></form>';
					 }

               },
			]
		} );

	$('#ren-select-all').on('click', function(){
		var rows = table.rows({ 'search': 'applied' }).nodes();
		$('input[type="checkbox"]', rows).prop('checked', this.checked);
	});

	$('#example tbody').on('change', 'input[type="checkbox"]', function(){
		// If checkbox is not checked
		if(!this.checked){
			var el = $('#ren-select-all').get(0);
			// If "Select all" control is checked and has 'indeterminate' property
			if(el && el.checked && ('indeterminate' in el)){
				// Set visual state of "Select all" control
				// as 'indeterminate'
				el.indeterminate = true;
			}
		}
	});



    
}


function populateClientLov(){
	if($("#client-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "client-frm",
			sort : 'fname',
			change: function(e, a, v){
				$("#rev-search-name").val(e.added.tenId);

			},
			formatResult : function(a)
			{
				return a.fname+" "+a.otherNames;
			},
			formatSelection : function(a)
			{
				return a.fname+" "+a.otherNames;
			},
			initSelection: function (element, callback) {

			},
			id: "tenId",
			placeholder:"Select Client"

		});
	}

	$("#client-frm").on("select2-removed", function(e) {
		$("#rev-search-name").val('');
	})
}

function createAccountsForSel(){
	if($("#acc-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "acc-frm",
			sort : 'name',
			change: function(e, a, v){
				$("#agent-search-number").val(e.added.acctId);
			},
			formatResult : function(a)
			{
				return a.name
			},
			formatSelection : function(a)
			{
				return a.name
			},
			initSelection: function (element, callback) {

			},
			id: "acctId",
			placeholder:"Select Account",
		});
	}

	$("#acc-frm").on("select2-removed", function(e) {
		$("#agent-search-number").val('');
	})
}


function createProductForSel(){
	if($("#prd-code").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "prd-code",
			sort : 'proDesc',
			change: function(e, a, v){
				$("#product-search-number").val(e.added.proCode);
			},
			formatResult : function(a)
			{
				return a.proDesc
			},
			formatSelection : function(a)
			{
				return a.proDesc
			},
			initSelection: function (element, callback) {

			},
			id: "proCode",
			placeholder:"Select Product",
		});
	}

	$("#prd-code").on("select2-removed", function(e) {
		$("#product-search-number").val('');
	})
}


function populateUserBranches(){
	if($("#brn-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "brn-frm",
			sort : 'obName',
			change: function(e, a, v){
				$("#brn-id").val(e.added.obId);
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

			},
			id: "obId",
			width:"250px",
			placeholder:"All Branches"

		});
	}
	$("#brn-frm").on("select2-removed", function(e) {
		$("#brn-id").val('');
	});
}


function populateBinderLov(){
	if($("#binder-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "binder-frm",
			sort : 'binName',
			change: function(e, a, v){
				$("#risk-binder-code").val(e.added.binId);
			},
			formatResult : function(a)
			{
				return a.binName;
			},
			formatSelection : function(a)
			{
				return a.binName;
			},
			initSelection: function (element, callback) {

			},
			id: "binId",
			width:"250px",
			placeholder:"All Binders/Masks"

		});
	}
	$("#binder-frm").on("select2-removed", function(e) {
		$("#risk-binder-code").val('');
	});
}

function populateRisksLov(){
	if($("#risk-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "risk-frm",
			sort : 'riskShtDesc',
			change: function(e, a, v){
				$("#risk-search-id").val(e.added.riskId);
			},
			formatResult : function(a)
			{
				return "Risk: "+a.riskShtDesc +" Policy: "+ a.policy.polNo+" Endorsement: "+ a.policy.polRevNo
			},
			formatSelection : function(a)
			{
				return a.riskShtDesc+" "+a.riskDesc
			},
			initSelection: function (element, callback) {

			},
			id: "riskId",
			width:"250px",
			placeholder:"Select A Risk",

		});
	}
	$("#risk-frm").on("select2-removed", function(e) {
		$("#risk-search-id").val('');
	});
}