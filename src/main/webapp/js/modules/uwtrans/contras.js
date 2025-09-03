$(function(){

	$(document).ready(function() {
		$("#prg-id"  ).val("L");
		$("#prg-name").val("LIFE");
		$("#prg-type").val("L");
		if ($("#prd-group").data("select2")) {
			$("#prd-group").select2('destroy');
		}
		$("#prd-group").prop('disabled', true)
			.css({ 'background-color':'#eee', 'cursor':'not-allowed' })
			.text("LIFE");
		$.ajaxSetup({
			cache: false
		});

		checkExistingTrans();
		loadContraModal();
		confirmSelectedTrans();
		selectEndorseType();
		// createProdGrpSelect();

		if($("#rev-type").val()==="RE"){

			$("#endorse-details").show();
		}
		else{
			$("#endorse-details").hide();
		}

		function toggleReversalTypeDropdown() {
			const revType = $("#rev-type").val();
			const reversalTypeSelect = $("#reversal-type");

			if(revType === "CO") {
				$("#reversal-type-container").show();
				reversalTypeSelect.val("CONTRA");
				reversalTypeSelect.prop("disabled", true);
				reversalTypeSelect.css({
					'background-color': '#eee',
					'cursor': 'not-allowed'
				});
			} else {
				$("#reversal-type-container").hide();
				reversalTypeSelect.val("");
				reversalTypeSelect.prop("disabled", false);
				reversalTypeSelect.css({
					'background-color': '',
					'cursor': ''
				});
			}
		}

		toggleReversalTypeDropdown();
		$("#rev-type").on("change", function() {
			toggleReversalTypeDropdown();
		});
		$("form").on("submit", function() {
			$("#reversal-type").prop("disabled", false);
		});
	});
});
function handleInsuranceTypeChange() {
	const prgType = $("#prg-type").val(); // Get the insurance type from hidden field
	const selectedText = $("#prg-name").val(); // Get the name from hidden field
	const reuseOption = document.querySelector('#rev-type option[value="RE"]');

	if (prgType === 'L' || (selectedText && selectedText.toLowerCase().includes('life'))) {
		// Show reuse option ONLY for life insurance
		reuseOption.style.display = '';
	} else {
		// Hide reuse option for other insurance types
		reuseOption.style.display = 'none';

		// Clear selection if currently set to reuse
		if (document.querySelector('#rev-type').value === 'RE') {
			document.querySelector('#rev-type').value = '';
		}
	}
}

function createProdGrpSelect() {
	if ($("#prd-group").length) {
		Select2Builder.initAjaxSelect2({
			containerId: "prd-group",
			sort: 'prgDesc',
			resultsFilter: function(content) {
				return content.filter(function(item) {
					return item.prgDesc !== "GENERAL INSURANCE";
				});
			},
			change: function(e) {
				$("#prg-id").val(e.added.prgCode);
				$("#prg-name").val(e.added.prgDesc);
				$("#prg-type").val(e.added.prgType);

				// Call the function to handle reuse option visibility
				handleInsuranceTypeChange();

				createProducts();
				makeProductSelection();
			},
			formatResult: function(item) {
				return item.prgDesc;
			},
			formatSelection: function(item) {
				return item.prgDesc;
			},
			initSelection: function(element, callback) {
				const prgId = $("#prg-id").val();
				const prgName = $("#prg-name").val();
				if (prgId && prgName) {
					callback({ prgCode: prgId, prgDesc: prgName });
					// Handle initial state
					setTimeout(handleInsuranceTypeChange, 100);
				}
			},
			id: "prgCode",
			placeholder: "Select Insurance Type"
		});
	}
}


function selectEndorseType(){
	$("#rev-type").on('change', function(){
		if($("#rev-type").val()==="RE"){

			$("#endorse-details").show();
		}
		else{
			$("#endorse-details").hide();
		}
	})
}


function confirmSelectedTrans(){
	$("#selectauthtrans").on('click', function(){
		if ($("#pol-number").val() != ''){
			$('#contraPoliciesModal').modal('hide');
		}
		else{
			bootbox.alert('Select a Policy to continue');
		}
	});
}

function loadContraModal(){
	$("#btn-show-search-pol").on('click', function(){
		if($("#rev-type").val() != ''){
			// Check insurance type and load appropriate policies
			var prgType = $("#prg-type").val();
			var selectedText = $("#prg-name").val();

			if(prgType === 'L' || (selectedText && selectedText.toLowerCase().includes('life'))) {
				// Load life policies only
				createContraLifePolicies($("#rev-type").val());
			} else {
				// Load non-life policies
				createContraPolicies($("#rev-type").val());
			}

			$('#contraPoliciesModal').modal({
				backdrop: 'static',
				keyboard: true
			})
		}
		else{
			bootbox.alert('Select Transaction Type');
		}
	});

	// $("#btn-search-policies").on('click', function(){
	// 	createContraPolicies($("#rev-type").val());
	//
	// });



	$("#btn-search-policies").on('click', function(){
		var prgType = $("#prg-type").val();
		var selectedText = $("#prg-name").val();

		if(prgType === 'L' || (selectedText && selectedText.toLowerCase().includes('life'))) {
			createContraLifePolicies($("#rev-type").val());  // Life policies only
		} else {
			createContraPolicies($("#rev-type").val());      // Non-life policies
		}
	});


}

function createContraPolicies(transtype){
	var url = "contraPolicies";
	if(transtype ==="CO"){
		url = "contraPolicies";
	}
	else if(transtype ==="RE"){
		url = "reuseofcontraPolicies";
	}
	var currTable = $('#revtranstbl').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": {
			'url': url,
			'data':{
				'clientName': $("#rev-search-name").val(),
				'policyNo':  $("#pol-search-number").val(),
				'agent':  $("#agent-search-number").val(),
				'endorseNumber':  $("#rev-search-number").val(),
				'refno': $("#dr-search-number").val(),
			},
		},
		autoWidth: true,
		lengthMenu: [ [10], [10] ],
		pageLength: 10,
		destroy: true,
		searching: false,
		"columns": [
			{ "data": "policyNo" },
			{ "data": "clientPolNo" },
			{ "data": "polRevNo" },
			{ "data": "clientName",
				"render": function ( data, type, full, meta ) {
					return full.clientName;
				}
			},
			{ "data": "agentName",
				"render": function ( data, type, full, meta ) {
					return full.agentName;
				}
			},
			{ "data": "polUwYr" },
			{ "data": "binderName",
				"render": function ( data, type, full, meta ) {
					return full.binderName;
				}
			},

		]
	} );

	$('#revtranstbl tbody').on( 'click', 'tr', function () {

		$(this).addClass('table-primary').siblings().removeClass('table-primary');

		var d = currTable.row( this ).data();
		if(d){
			$("#rev-pol-id").val(d.policyId);
			$("#pol-number").val(d.policyNo);
			$("#pol-endorse-no").val(d.polRevNo);
			$.ajax({
				type: 'GET',
				url:  'countUnauthPolicies',
				dataType: 'json',
				data: {"policyNumber": $("#pol-number").val()},
				async: true,
				success: function(result) {
					if(result > 0){
						createUnauthPolicies();
						$("#existing-trans").show();
					}
					else{
						$("#existing-trans").hide();
					}
				},
				error: function(jqXHR, textStatus, errorThrown) {

				}
			});


		}


	} );

	return currTable;
}
function createContraLifePolicies(transtype){
	var url = "contraLifePolicies";


	var currTable = $('#revtranstbl').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": {
			'url': url,
			'data':{
				'clientName': $("#rev-search-name").val(),
				'policyNo':  $("#pol-search-number").val(),
				'agent':  $("#agent-search-number").val(),
				'endorseNumber':  $("#rev-search-number").val(),
				'refno': $("#dr-search-number").val(),
			},
		},
		autoWidth: true,
		lengthMenu: [ [10], [10] ],
		pageLength: 10,
		destroy: true,
		searching: false,
		"columns": [
			{ "data": "policyNo" },
			{ "data": "clientPolNo" },
			{ "data": "polRevNo" },
			{ "data": "clientName",
				"render": function ( data, type, full, meta ) {
					return full.clientName;
				}
			},
			{ "data": "agentName",
				"render": function ( data, type, full, meta ) {
					return full.agentName;
				}
			},
			{ "data": "polUwYr" },
			{ "data": "binderName",
				"render": function ( data, type, full, meta ) {
					return full.binderName;
				}
			},

		]
	} );

	$('#revtranstbl tbody').on( 'click', 'tr', function () {

		$(this).addClass('table-primary').siblings().removeClass('table-primary');

		var d = currTable.row( this ).data();
		if(d){
			$("#rev-pol-id").val(d.policyId);
			$("#pol-number").val(d.policyNo);
			$("#pol-endorse-no").val(d.polRevNo);
			$.ajax({
				type: 'GET',
				url:  'countUnauthPolicies',
				dataType: 'json',
				data: {"policyNumber": $("#pol-number").val()},
				async: true,
				success: function(result) {
					if(result > 0){
						createUnauthPolicies();
						$("#existing-trans").show();
					}
					else{
						$("#existing-trans").hide();
					}
				},
				error: function(jqXHR, textStatus, errorThrown) {

				}
			});


		}


	} );

	return currTable;
}


function checkExistingTrans(){
	$.ajax({
		type: 'GET',
		url:  'countUnauthPolicies',
		dataType: 'json',
		data: {"policyNumber": $("#pol-number").val()},
		async: true,
		success: function(result) {

			if(result > 0){
				createUnauthPolicies();
				$("#existing-trans").show();
			}
			else{
				$("#existing-trans").hide();
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {

		}
	});
}


function createUnauthPolicies(){
	var url = "unauthPolicies";
	var currTable = $('#poltrans').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": {
			'url': url,
			'data':{
				'policyNumber': $("#pol-number").val(),
			},
		},
		autoWidth: true,
		lengthMenu: [ [10], [10] ],
		pageLength: 10,
		destroy: true,
		searching: false,
		"columns": [
			{ "data": "polNo" },
			{ "data": "polCreateddt",
				"render": function ( data, type, full, meta ) {
					return moment(full.polCreateddt).format('DD/MM/YYYY');
				}
			},
			{ "data": "wefDate",
				"render": function ( data, type, full, meta ) {
					return moment(full.wefDate).format('DD/MM/YYYY');
				}
			},
			{ "data": "wetDate",
				"render": function ( data, type, full, meta ) {
					return moment(full.wetDate).format('DD/MM/YYYY');
				}
			},
			{ "data": "policyId",
				"render": function ( data, type, full, meta ) {
					return full.createdUser.username;
				}
			},
			{ "data": "currentStatus",
				"render": function ( data, type, full, meta ) {
					if(full.currentStatus ==='D') return "Draft";
					else  return full.currentStatus;
				}
			},
			{
				"data": "policyId",
				"render": function ( data, type, full, meta ) {
					if(full.status==="A"){
						return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value='+full.policyId+'><input type="submit"  class="btn btn-success" value="View" ></form>';

					}else
						return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value='+full.policyId+'><input type="submit"  class="btn btn-success" value="Edit" ></form>';

				}

			},
			{
				"data": "policyId",
				"render": function ( data, type, full, meta ) {
					return '<input type="button" class="btn btn-success" data-policy='+encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="confirmPolicyDelete(this);"/>';
				}

			},


		]
	} );
	return currTable;
}


function confirmPolicyDelete(button){
	var policy = JSON.parse(decodeURI($(button).data("policy")));
	bootbox.confirm("Are you sure want to delete "+policy["polNo"]+"?", function(result) {
		if(result){
			// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
			$.ajax({
				type: 'GET',
				url:  'deletePolRecord',
				data: {"policyId": policy["policyId"]},
				dataType: 'json',
				async: true,
				success: function(result) {
					// $('#myPleaseWait').modal('hide');
					Swal.fire({
                title: 'Success',
                text: 'Transaction Deleted Successfully',
                icon: 'success'
            });
					$('#poltrans').DataTable().ajax.reload();
					$("#existing-trans").hide();
				},
				error: function(jqXHR, textStatus, errorThrown) {
					// $('#myPleaseWait').modal('hide');
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