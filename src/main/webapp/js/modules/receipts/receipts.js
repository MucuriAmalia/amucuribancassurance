var RECEIPTS = RECEIPTS || {};
var UTILITIES = UTILITIES || {};
$(function(){
	$(document).ready(function() {

		$(".datepicker-input").each(function() {
			$(this).datetimepicker({
				format: 'DD/MM/YYYY'
			});
		});
		$(document).ajaxStart(function () {
			$("#btn-add-receipt,#btn-print-receipt").attr("disabled", true);
		});
		$(document).ajaxComplete(function () {
			$("#btn-add-receipt,#btn-print-receipt").attr("disabled", false);
		});
		$("#rec-type").val('');
		RECEIPTS.collectionAcctsLov();
		RECEIPTS.populateUserBranches();
		$("#rct-amount").number(true, 2);
		$("#btn-add-receipt").on('click', function() {
			let paymentStatus = RECEIPTS.validatePayment();
			if (paymentStatus) {
				Swal.fire({
					title: paymentStatus,
					text: `Click "OK" to proceed.`,
					icon: paymentStatus === "Over payment" ? 'warning' : 'info',
					showCancelButton: true,
					confirmButtonText: 'OK',
					cancelButtonText: 'Cancel'
				}).then((result) => {
					if (result.isConfirmed) {
						RECEIPTS.createandPrintReceipt(); // Continue with form submission
					}
				});
			} else {
				RECEIPTS.createandPrintReceipt(); // No discrepancy, submit directly
			}
		});

		$("#btn-print-receipt").on('click', function() {
			// Add processing state to button
			$(this).prop('disabled', true).addClass('btn-processing');

			let paymentStatus = RECEIPTS.validatePayment();
			if (paymentStatus) {
				Swal.fire({
					title: paymentStatus,
					text: `Click "OK" to proceed.`,
					icon: paymentStatus === "Over payment" ? 'warning' : 'info',
					showCancelButton: true,
					confirmButtonText: 'OK',
					cancelButtonText: 'Cancel'
				}).then((result) => {
					if (result.isConfirmed) {
						RECEIPTS.createReceipt(); // Continue with form submission
					} else {
						// User cancelled - remove processing state
						$("#btn-print-receipt").prop('disabled', false).removeClass('btn-processing');
					}
				});
			} else {
				RECEIPTS.createReceipt(); // No discrepancy, submit directly
			}
		});

		$("#add-det-btn").on('click', function () {
			if($("#rec-type").val()){
				RECEIPTS.addReceiptRecord();
			}
			else{
				Swal.fire({
					title: 'Error',
					text: 'Select the Receipt Type to Proceed!!',
					icon: 'error'
				});
				$("#rec-type").val('');
				$("#rct-detail-tbl").clear().draw();
				// console.log("Select A receipt Type to Proceed!!"+$("#receipt-type").val());
			}
		});

		$("#rct-detail-tbl").on('click','.hyperlink-btn',function() {
			$(this).closest('tr').remove();
			var row_index = $('#rct-detail-tbl tr').length;
			if(row_index ===1){
				$("#rec-type").attr('disabled',false);
			}
		});

		$("#printReceipt").on('click', function () {
			RECEIPTS.doAllocation($("#print-receipt-id").val());
		});

		function agentLov() {
			if ($("#insurance-div").filter("div").html() != undefined) {
				// Destroy existing select2 if it exists
				if ($("#insurance-div").data('select2')) {
					$("#insurance-div").select2('destroy');
				}

				// Clear existing values
				$("#insurance-id").val('');
				$("#insurance-div").val('');
				$("#insurance-div").empty();

				// Get receipt type and insurance type before initializing
				var receiptType = $("#rec-type").val();
				var insuranceType = receiptType === 'N' ? 'GENERAL' :
					receiptType === 'L' ? 'LIFE' : '';

				Select2Builder.initAjaxSelect2({
					containerId: "insurance-div",
					sort: 'name',
					change: function(e, a, v) {
						$("#insurance-id").val(e.added.acctId);
						$("#rct-detail-tbl > tbody").empty();
					},
					formatResult: function(a) {
						return a.name;
					},
					formatSelection: function(a) {
						return a.name;
					},
					ajax: {
						url: $("#insurance-div").attr("select2-url"),
						dataType: 'json',
						data: function(term, page) {
							return {
								term: term,
								page: page,
								insuranceType: insuranceType
							};
						},
						results: function(data, page) {
							return { results: data };
						}
					},
					id: "acctId",
					placeholder: "Select Insurance Company",
					width: "100%",
					allowClear: true,
					minimumInputLength: 0,
					params: { insuranceType: insuranceType }  // Add insuranceType to initial params
				});
			}
		}


		$("#rec-type").change(function(){
			var _val = $(this).val();
			$("#receipt-type").val(_val);
			if(_val==='N' || _val==='L'){
				$(".insurance-co").css('display','block');
				$(".collect-acct").css('display','none');
				agentLov();
			}
			else if(_val==='COM'){
				$(".collect-acct").css('display','block');
				$(".insurance-co").css('display', 'none');
			}
		});

		$(document).ajaxStart(function () {
			$("#btn-add-receipt,#btn-print-receipt,#printReceipt").attr("disabled", true);
		});
		$(document).ajaxComplete(function () {
			$("#btn-add-receipt,#btn-print-receipt,#printReceipt").attr("disabled", false);
		});
	})
});

RECEIPTS.validatePayment = function() {
	let rctAmount = parseFloat($("#rct-amount").val());
	let transBalanceCell = $('#rct-detail-tbl tbody tr').find('td:nth-child(5)');
	let transBalance = parseFloat(transBalanceCell.text().replace(/[^0-9.-]+/g, ""));
	// let transBalance = parseFloat($("#trans-balance-").text());

	console.log("Receipt Amount:", rctAmount);
	console.log("Transaction Balance:", transBalance);

	if (isNaN(rctAmount) || isNaN(transBalance)) {
		console.error("Invalid values: Receipt amount or transaction balance is not a number.", rctAmount, transBalance);
		return null;
	}

	if (rctAmount > transBalance) {
		console.log("Detected over payment.");
		return "Over payment";
	} else if (rctAmount < transBalance) {
		console.log("Detected under payment.");
		return "Under payment";
	}
	console.log("Payment is balanced.");
	return null;
};




RECEIPTS.isNullOrUndefined = function (value) {
	if( value === undefined || value === null || value==='') {
		return -2000;
	}
	else return value;
}

RECEIPTS.doAllocation = function(receiptId){
	$.ajax({
		type: 'GET',
		url:  'allocateReceipt',
		dataType: 'json',
		data: {"receiptCode": receiptId},
		async: true,
		success: function(result) {
			$('#printReceiptModal').modal('hide');
			Swal.fire({
				title: 'Success',
				text: 'Receipt Created Successfully',
				icon: 'success'
			});

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

RECEIPTS.createAllocation = function() {
	var arr = [];
	$('#rct-detail-tbl > tbody  > tr').each(
		function() {
			var data = {};
			$(this).find(":input[type='text'],:input[type='hidden']").serializeArray()
				.map(function(x) {
					data[x.name] = x.value;
				});
			arr.push(data);
		});
	return arr;
}

RECEIPTS.addReceiptRecord = function(){
	// Use a selector that will select all the rows and take the length.
	// Note: this approach also counts all trs of every nested table!
	var row_index = $('#rct-detail-tbl tr').length;
	if(row_index > 0){
		$("#rec-type").attr('disabled',true);
	}

	var transType = $("#rec-type").val();
	if(transType ==='N') {
		    var trans = '';
			trans = '<input type="hidden" id="cr-debit-code' + row_index + '" name="transNo">' +
				'<input type="hidden" id="cr-temp-code' + row_index + '" name="transTempNo">' +
				' <div id="debit-frm' + row_index + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/uw/receipts/selclienttrans" </div> ';
			var markup = "<tr><td>" + trans + "</td><td><p class='form-control-static' id='rec-policy-" + row_index + "'> </p></td>" +
				"<td><p class='form-control-static' id='trans-date-" + row_index + "'> </p></td>" +
				"<td><p class='form-control-static' id='trans-client-" + row_index + "'> </p></td>" +
				"<td><p class='form-control-static' id='trans-balance-" + row_index + "'> </p></td>" +
				"<td><input type='text' size='11' id='rctamt-" + row_index + "'  name='rctAmount'/></td><td><button type='button' class='btn btn-danger btn btn-danger btn-sm hyperlink-btn'><i class='fa fa-trash-o'></button></td></tr>";
			$("#rct-detail-tbl").append(markup);
			$('[id^=rctamt-]').number(true, 2);
			Select2Builder.initAjaxSelect2({
				containerId: "debit-frm" + row_index,
				sort: 'refNo',
				counter: row_index,
				change: function (e, a, v) {
					$.each($(this), function () {
						var transId = (e.added.transno)&& e.added.transno!==0?e.added.transno:null;
						var tempId =  (e.added.transTempNo)?e.added.transTempNo:null;
						var key = Object.keys(this)[2];
						var value = this[key];
						var drId = "#cr-debit-code" + value;
						var drIdTemp = "#cr-temp-code" + value;
						$(drId).val(transId);
						$(drIdTemp).val(tempId);
						$("#rec-policy-" + value).text(e.added.polNo);
						$("#trans-date-" + value).text(moment(e.added.transDate).format('DD/MM/YYYY'));
						$("#trans-client-" + value).text(e.added.client);
						$("#trans-balance-" + value).text(UTILITIES.currencyFormat(e.added.balance));
						$("#rctamt-" + value).val(UTILITIES.currencyFormat(e.added.balance));
						console.log("transaction balance" +(e.added.balance))
					});
				},
				formatResult: function (a) {
					return a.refNo+' - '+a.controlAcc+' - '+a.client;
				},
				formatSelection: function (a) {
					return a.refNo+' - '+a.controlAcc+' - '+a.client;
				},
				initSelection: function (element, callback) {

				},
				id: "transno",
				width: "300px",
				params: {acctId: RECEIPTS.isNullOrUndefined($("#insurance-id").val())},
				placeholder: "Select Transaction"

			});

	}
	else if(transType ==='COM') {
		trans = '<input type="hidden" id="cr-debit-code' + row_index + '" name="transNo">' +
			'  <div id="debit-frm' + row_index + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/uw/receipts/creditorcommtrans" </div> ';
		var markup = "<tr><td>" + trans + "</td><td><p class='form-control-static' id='rec-policy-" + row_index + "'> </p></td>" +
			"<td><p class='form-control-static' id='trans-date-" + row_index + "'> </p></td>" +
			"<td><p class='form-control-static' id='trans-client-" + row_index + "'> </p></td>" +
			"<td><p class='form-control-static' id='trans-balance-" + row_index + "'> </p></td>" +
			"<td><input type='text' size='11' id='rctamt-" + row_index + "'  name='rctAmount'/></td><td><button type='button' class='btn btn-danger btn btn-danger btn-sm hyperlink-btn'><i class='fa fa-trash-o'></button></td></tr>";
		$("#rct-detail-tbl").append(markup);
		$('[id^=rctamt-]').number(true, 2);
		Select2Builder.initAjaxSelect2({
			containerId: "debit-frm" + row_index,
			sort: 'refNo',
			counter: row_index,
			change: function (e, a, v) {
				$.each($(this), function () {
					var key = Object.keys(this)[2];
					var value = this[key];
					var drId = "#cr-debit-code" + value;
					$(drId).val(e.added.transno);
					$("#rec-policy-" + value).text(e.added.refNo);
					$("#trans-date-" + value).text(moment(e.added.transDate).format('DD/MM/YYYY'));
					$("#trans-client-" + value).text(e.added.payeeName);
					$("#trans-balance-" + value).text(UTILITIES.currencyFormat(e.added.balance));
					$("#rctamt-" + value).val(UTILITIES.currencyFormat(e.added.balance));
				});
			},
			formatResult: function (a) {
				return a.refNo;
			},
			formatSelection: function (a) {
				return a.refNo;
			},
			initSelection: function (element, callback) {

			},
			id: "transno",
			width: "300px",
			placeholder: "Select Transaction",
			params: {acctId: $("#insurance-id").val()},

		});
	}
	else{
		trans = '<input type="hidden" id="cr-debit-code' + row_index + '" name="transNo">' +
			'  <div id="debit-frm' + row_index + '" class="form-control"  select2-url="' + SERVLET_CONTEXT + '/protected/uw/receipts/lifepolicies" </div> ';
		var markup = "<tr><td>" + trans + "</td><td><p class='form-control-static' id='rec-policy-" + row_index + "'> </p></td>" +
			"<td><p class='form-control-static' id='trans-date-" + row_index + "'> </p></td>" +
			"<td><p class='form-control-static' id='trans-client-" + row_index + "'> </p></td>" +
			"<td><p class='form-control-static' id='trans-balance-" + row_index + "'> </p></td>" +
			"<td><input type='text' size='11' id='rctamt-" + row_index + "'  name='rctAmount'/></td><td><button type='button' class='btn btn-danger btn btn-danger btn-sm hyperlink-btn'><i class='fa fa-trash-o'></button></td></tr>";
		$("#rct-detail-tbl").append(markup);
		$('[id^=rctamt-]').number(true, 2);
		Select2Builder.initAjaxSelect2({
			containerId: "debit-frm" + row_index,
			sort: 'refNo',
			counter: row_index,
			change: function (e, a, v) {
				$.each($(this), function () {
					var key = Object.keys(this)[2];
					var value = this[key];
					var drId = "#cr-debit-code" + value;
					$(drId).val(e.added.policyId);
					$('#receipt-pol-id').val(e.added.policyId);
					console.log(e.added);
					//$("#rec-policy-" + value).text(e.added.polNo);
					if (e.added.polNo){
						$("#rec-policy-" + value).text(e.added.polNo);
					} else {
						$("#rec-policy-" + value).text(e.added.proposalNo);
					}
					if (e.added.authDate) {
						$("#trans-date-" + value).text(moment(e.added.authDate).format('DD/MM/YYYY'));
					} else{
						$("#trans-date-" + value).text(moment(e.added.polCreateddt).format('DD/MM/YYYY'));
					}
					//$("#trans-date-" + value).text(moment(e.added.authDate).format('DD/MM/YYYY'));
					$("#trans-client-" + value).text(e.added.client.fname + " " + e.added.client.otherNames);
					if (e.added.negotiatedPremium){
					$("#trans-balance-" + value).text(UTILITIES.currencyFormat(e.added.negotiatedPremium));
                    $("#rctamt-" + value).val(UTILITIES.currencyFormat(e.added.negotiatedPremium));
					}else{
					$("#trans-balance-" + value).text(UTILITIES.currencyFormat(e.added.netPrem));
					$("#rctamt-" + value).val(UTILITIES.currencyFormat(e.added.netPrem));
					}
				});
			},
			formatResult: function (a) {
				if (a.polNo)
					return a.polNo;
				else return a.proposalNo;
			},
			formatSelection: function (a) {
				if (a.polNo)
					return a.polNo;
				else return a.proposalNo;
			},
			initSelection: function (element, callback) {

			},
			id: "policyId",
			width: "300px",
			placeholder: "Select Transaction"

		});
	}

}



RECEIPTS.printPdf = function(url){
	$("#receipt-div").attr('src', url);
	$('#printReceiptModal').modal({
		backdrop: 'static',
		keyboard: true
	});
}

RECEIPTS.createReceipt = function() {
	var $currForm = $('#receipt-form');
	var currValidator = $currForm.validate();
	if (!$currForm.valid()) {
		// Remove processing state if form is invalid
		$("#btn-print-receipt").prop('disabled', false).removeClass('btn-processing');
		return;
	}

	var data = {};
	$currForm.serializeArray().map(function(x) {
		data[x.name] = x.value;
	});
	var url = "createReceipt";
	var arr = RECEIPTS.createAllocation();
	data.details = arr;
	if (data.receiptType === 'L') {
		data.policyId = $('#receipt-pol-id').val();
	}

	Swal.fire({
		title: 'Processing...',
		text: 'Please wait while the receipt is being created.',
		allowOutsideClick: false,
		allowEscapeKey: false,
		didOpen: () => {
			Swal.showLoading();
		}
	});

	$.ajax({
		url : url,
		type : "POST",
		data : JSON.stringify(data),
		success : function(s) {
			// Keep processing state on success (will be removed on page refresh/redirect)
			$('#receipt-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
			arr = {};
			$('#rct-detail-tbl tbody').remove();
			RECEIPTS.collectionAcctsLov();
			RECEIPTS.populateUserBranches();

			Swal.fire({
				title: 'Success',
				text: 'Receipt Created Successfully',
				icon: 'success'
			});
			window.location.href = SERVLET_CONTEXT + "/protected/home";
		},
		error : function(jqXHR, textStatus, errorThrown) {
			// Remove processing state on error
			$("#btn-print-receipt").prop('disabled', false).removeClass('btn-processing');

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

RECEIPTS.createandPrintReceipt = function() {
	var $currForm = $('#receipt-form');
	var currValidator = $currForm.validate();
	if (!$currForm.valid()) {
		return;
	}

	var data = {};
	$currForm.serializeArray().map(function(x) {
		data[x.name] = x.value;
	});
	var url = "createReceipt";
	var arr = RECEIPTS.createAllocation();
	data.details = arr;

	$.ajax({
		url : url,
		type : "POST",
		data : JSON.stringify(data),
		success : function(s) {
			// $('#myPleaseWait').modal('hide');
			$('#receipt-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
			arr = {};
			$('#rct-detail-tbl tbody').remove();
			RECEIPTS.collectionAcctsLov();
			RECEIPTS.populateUserBranches();
			$("#print-receipt-id").val(s);
			RECEIPTS.printPdf(SERVLET_CONTEXT+"/protected/uw/receipts/receipt_rpt/"+s);
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
}


RECEIPTS.collectionAcctsLov = function() {
	if ($("#coll-div").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId : "coll-div",
			sort : 'name',
			change : function(e, a, v) {
				$("#coll-id").val(e.added.caId);
				if(e.added.bankBranches)
					$("#bank-desc").text(e.added.bankBranches.branchName+" - "+e.added.bankBranches.bank.bankName);
				else $("#bank-desc").text("No Bank Account");
				$("#currency-desc").text(e.added.currencies.curName);
				$("#pymt-mode").text(e.added.paymentModes.pmDesc);
			},
			formatResult : function(a) {
				return a.name
			},
			formatSelection : function(a) {
				return a.name
			},
			initSelection : function(element, callback) {

			},
			id : "caId",
			placeholder:"Select Collection Account",
			width : "220px"
		});
	}
}

RECEIPTS.populateUserBranches = function(){
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
			placeholder:"Select Branch"

		});
	}
}