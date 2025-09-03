$(function () {

	$(document).ready(function () {

		getUserTrans();
		populateClientLov();
		populatePolicyLov();
		populateRiskLov();
		createAccountsForSel();
		createProductForSel();
		toggleMedBusinessClass();
		toggleNonMedBusinessClass();
		masterEnquiryFun();
		displayTables();
		getTenantDetails();
		getClientBack();
		getPolicyBack();
		getClaimBack();
		$('#polRisDabl').on('click', '.btn-vpolclaim', function (s) {
			var data = $(this).closest('tr').find('#risenqId').val();
			makeClaimDT(data);
		})

		$('#polTbl').on('click', '.btn-vriskpol', function (s) {
			var data = $(this).closest('tr').find('#polenqId').val();
			makeRiskDT(data);
		})
		/*
  $("#clTbl").on('click', '.btn-vpol', function() {
      var data=$(this).closest('tr').find('#clId').val();
      makeClientDet(data);
      $('#hidePol').show();

  })

  $("#clTbl").on('click', '.btn-vpolclid', function() {
      $('#hidePol').show();
  })
btn-vriskpol
         */

		$("#polTbl").on('click', '.btn-vrisk', function () {
			var data = $(this).closest('tr').find('#uniqueRisk').val();
			makeUniqueRisk(data);

		})

		$("#polTbl").on('click', '.btn-vriskpol', function () {
			var data = $(this).closest('tr').find('#allRisk').val();
			makeUniqueRisk(data);

		})
		$("#polRisDabl").on('click', '.btn-vclaim', function () {

			var data = $(this).closest('tr').find('#polriskenqId').val();
			makeClaimDT(data);

		})
		$('#gBusiness').attr('checked', 'checked');

		$("#btn-search-policies").on('click', function () {
			var unifiedSearch = $("#unified-search").val().trim();

			if (unifiedSearch === '') {
				bootbox.alert("Please Provide a search term.");
				return;
			}

			policyEnquiry(unifiedSearch);

		});

		$("#unified-search").on('input', function () {
			var value = $(this).val().trim();

			if (value === '') {
				console.log("clearing ");
				// Clear the DataTable when input is empty
				//$('#pol_enquiry_tbl').DataTable().clear().draw();
				location.reload();
			}
		});

	});

});

function getPolicyBack() {
	if (typeof polId !== 'undefined') {
		var url = '';
		var idNo = '';
		var polNo = polId;
		var riskId = '';
		$('.hide-col').show();
		backPolNo(url, polNo, idNo, riskId);
	}
}

function getClaimBack() {
	if (typeof claimId !== 'undefined') {

		$('.hide-col').show();
		backClaim(claimId);

	}
}

function getClientBack() {
	if (typeof clntId !== 'undefined') {
		//$('#client-id').val(clntId);
		var url = '';
		var polNo = '';
		var riskId = '';
		$('.hide-col').show();
		makeClientPC(url, polNo, clntId);
		makeBackToPolicyPC(url, polNo, clntId, riskId);
	}
}

function getCollectionAccts() {
	if ($("#coll-div").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "coll-div",
			sort: 'name',
			change: function (e, a, v) {
				$("#coll-id").val(e.added.caId);
				if (e.added.bankBranches)
					$("#bank-desc").text(e.added.bankBranches.branchName + " - " + e.added.bankBranches.bank.bankName);
				else $("#bank-desc").text("No Bank Account");
				$("#currency-desc").text(e.added.currencies.curName);
				$("#pymt-mode").text(e.added.paymentModes.pmDesc);
			},
			formatResult: function (a) {
				return a.name
			},
			formatSelection: function (a) {
				return a.name
			},
			initSelection: function (element, callback) {

			},
			id: "caId",
			placeholder: "Select Collection Account",
			width: "220px"
		});
	}
}

function getBankBranches() {
	if ($("#brn-frm").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "brn-frm",
			sort: 'obName',
			change: function (e, a, v) {
				$("#brn-id").val(e.added.obId);
			},
			formatResult: function (a) {
				return a.obName;
			},
			formatSelection: function (a) {
				return a.obName;
			},
			initSelection: function (element, callback) {

			},
			id: "obId",
			width: "250px",
			placeholder: "Select Branch"

		});
	}
}

function getTenantDetails() {
	if (typeof tenidpkey !== 'undefined') {
		if (tenidpkey !== -2000) {
			$.ajax({
				url: 'tenants/' + tenidpkey,
				type: 'GET',
				processData: false,
				contentType: false,
				success: function (s) {
					populateTenantDetails(s);
					$("#show-docs").css("display", "block");
					$("#tab_content2").css("display", "block");
					$(".clnt-title").css("display", "block");
					$(".display-clnt-title").css("display", "none");
					$("#btn-search-tenant").css("display", "none");

				},
				error: function (xhr, error) {
					bootbox.alert(xhr.responseText);
				}
			});
		} else {
			tenantImage(-2000);
			getSystemDate();
			defaultCountry();
			$("#address").val("P.O. Box");
			$("#show-docs").css("display", "none");
			$("#tab_content2").css("display", "none");
			$("#btn-auth-client").css("display", "none");
			$("#btn-save-tenant").css("display", "block");
			$("#btn-add-client-type").css("display", "block");
			$("#btn-add-sms-prefix").css("display", "block");
			$("#btn-add-phone-prefix").css("display", "block");
			$("#btn-search-tenant").css("display", "block");
			$(".clnt-title").css("display", "block");
			$(".display-clnt-title").css("display", "none");
		}

	}
}

function populateTenantDetails(data) {
	//console.log(data);
	$("#tenId-pk").val(data.tenId);
	$("#fname").val(data.fname);
	$("#other-names").val(data.otherNames);
	$("#id-no").val(data.idNo);
	$("#passport-no").val(data.passportNo);
	$("#pin-no").val(data.pinNo);
	$("#client-ref-no").val(data.clientRef);
	$("#email-address").val(data.emailAddress);
	$("#phone-no").val(data.phoneNo);
	$("#sms-no").val(data.smsNumber);
	$("#address").val(data.address);
	$("#sel2").val(data.tenantType);
	$("#dob").val(moment(data.dob).format('DD/MM/YYYY'));
	if (data.status != "T") {
		$("#sel3").val(data.status);
		$("#sel3").prop("disabled", false);
		$("#btn-save-tenant").prop("disabled", false);
	} else {
		$("#sel3").val(data.status);
		$("#sel3").prop("disabled", true);
		$("#btn-save-tenant").prop("disabled", true);
	}

	$("#clnt-type-code").val(data.tenantType.clientType);
	if (data.tenantType.clientType === 'C' || data.tenantType.clientType === '') {
		$("#gender,.gender,#lblGender").hide();
		$("#lbl-dob").html("Date of Incorporation");
		$("#lbl-id-no").html("Registration No.");
		$(".employee-info").hide();

	} else if (data.tenantType.clientType === 'I') {
		$("#gender,.gender,#lblGender").show();
		$("#lbl-dob").html("Date of Birth");
		$("#lbl-id-no").html("ID No.");
		$(".employee-info").show();
	}

	if (data.authStatus && data.authStatus === "Y") {
		$("#clnt-authorised").text("Yes");
		$("#btn-save-tenant").css("display", "none");
		$("#btn-auth-client").css("display", "block");
		$("#btn-auth-client").val("Un-Authorise");
		$("#btn-add-client-type").css("display", "none");
		$("#clnt-title").select2("enable", false);
		$("#fname").attr("disabled", "disabled");
		$("#other-names").attr("disabled", "disabled");
		$("#pin-no").attr("disabled", "disabled");
		$("#email-address").attr("disabled", "disabled");
		$("#address").attr("disabled", "disabled");
		$("#id-no").attr("disabled", "disabled");
		$("#passport-no").attr("disabled", "disabled");
		$("#gender").attr("disabled", "disabled");
		$("#clnt-country,#sms-pref,#town-code-lov,#postal-code,#clnt-client-type,#clnt-title,#sect-def,#ten-branch,#occ-def,#phone-pref").select2("readonly", true);
		$("#office-tel-no").attr("disabled", "disabled");
		$("#sms-no").attr("disabled", "disabled");
		$("#phone-no").attr("disabled", "disabled");
		$("#btn-add-sms-prefix").css("display", "none");
		$("#btn-add-phone-prefix").css("display", "none");
		$("#sel3").attr("disabled", "disabled");
		$("#dob").attr("disabled", "disabled");
		$("#btn-add-docs").hide();

	} else {
		$("#clnt-authorised").text("No");
		$("#btn-auth-client").css("display", "block");
		$("#btn-save-tenant").css("display", "block");
		$("#btn-auth-client").val("Authorise");
		$("#btn-add-client-type").css("display", "block");
		$("#btn-add-sms-prefix").css("display", "block");
		$("#btn-add-phone-prefix").css("display", "block");
		$("#fname").removeAttr('disabled');
		$("#other-names").removeAttr('disabled');
		$("#pin-no").removeAttr('disabled');
		$("#email-address").removeAttr('disabled');
		$("#address").removeAttr('disabled');
		$("#id-no").removeAttr('disabled');
		$("#passport-no").removeAttr('disabled');
		$("#gender").removeAttr('disabled');
		$("#clnt-country,#sms-pref,#town-code-lov,#postal-code,#clnt-client-type,#clnt-title,#sect-def,#ten-branch,#occ-def,#phone-pref").select2("readonly", false);
		$("#office-tel-no").removeAttr('disabled');
		$("#sms-no").removeAttr('disabled');
		$("#phone-no").removeAttr('disabled');

		$("#sel3").removeAttr('disabled');
		$("#dob").removeAttr('disabled');
		$("#btn-add-docs").show();
	}
	$("#gender").val(data.gender);
	$("#ten-id").val(data.tenantNumber);
	$("#office-tel-no").val(data.officeTel);
	$("#date-reg").val(moment(data.dateregistered).format('DD/MM/YYYY'));

	$("#obId").val(data.registeredbrn.obId);
	$("#ob-name").val(data.registeredbrn.obName);
	populateBranchA();

	if (data.country) {
		$("#cou-name").val(data.country.couName);
		$("#cou-id").val(data.country.couCode);
		populateCountryLov();
	}
	if (data.smsPrefix) {
		$("#pref-sms-id").val(data.smsPrefix.prefixId);
		$("#pref-sms-name").val(data.smsPrefix.prefixName);

	}
	if (data.phonePrefix) {
		$("#pref-phone-id").val(data.phonePrefix.prefixId);
		$("#pref-phone-name").val(data.phonePrefix.prefixName);
	}
	populateSmsPrefix(data.country.couCode);

	if (data.tenantType) {
		$("#clnt-type-id").val(data.tenantType.typeId);
		$("#clnt-type-name").val(data.tenantType.typeDesc);
	}
	populateClientTypeLov();
	if (data.clientTitle) {
		$("#clnt-title-id").val(data.clientTitle.titleId);
		$("#clnt-title-name").val(data.clientTitle.titleName);
	}
	populateTitlesLov();
	if (data.clientSector) {
		$("#sect-id").val(data.clientSector.code);
		$("#sect-name").val(data.clientSector.name);

	}
	createSectorSelect();
	if (data.occupation) {
		$("#occ-id").val(data.occupation.code);
		$("#occ-name").val(data.occupation.name);
		populateOccupations(data.clientSector.code);
	}

	if (data.town) {
		$("#clnt-town-id").val(data.town.ctCode);
		$("#clnt-town-name").val(data.town.ctName);
		createClientTownLov();
	}
	if (data.postalCodesDef) {
		$("#postal-code-id").val(data.postalCodesDef.pcode);
		$("#postal-name").val(data.postalCodesDef.zipCode);
		populatePostalCode(data.postalCodesDef.pcosde);
	}
	if (data.dateterminated)
		$("#dt-terminated").val(moment(data.dateterminated).format('DD/MM/YYYY'));
	tenantImage(data.tenId);
	getClientDocs(data.tenId);


}

/*
function makeClientDet(data){
	$.ajax({
		type:'GET',
		url:'viewClients',
		data:{
			'clId':data
		}
	}).done(function (s) {
		console.log(s.idNo);

		$("#tenId-pk").val(s.tenId);
		$("#fname").val(s.fname);
		$("#other-names").val(s.otherNames);
		$("#id-no").val(s.idNo);
		$("#passport-no").val(s.passportNo);
		$("#pin-no").val(s.pinNo);
		$("#client-ref-no").val(s.clientRef);
		$("#email-address").val(s.emailAddress);
		$("#phone-no").val(s.phoneNo);
		$("#sms-no").val(s.smsNumber);
		$("#address").val(s.address);
		$("#sel2").val(s.tenantType);
		$("#dob").val(moment(s.dob).format('DD/MM/YYYY'));
		if (data.status != "T") {
			$("#sel3").val(s.status);
			$("#sel3").prop("disabled", false);
			$("#btn-save-tenant").prop("disabled", false);
		} else {
			$("#sel3").val(s.status);
			$("#sel3").prop("disabled", true);
			$("#btn-save-tenant").prop("disabled", true);
		}

		$("#clnt-type-code").val(s.tenantType.clientType);
		if (s.tenantType.clientType === 'C' || s.tenantType.clientType === '') {
			$("#gender,.gender,#lblGender").hide();
			$("#lbl-dob").html("Date of Incorporation");
			$("#lbl-id-no").html("Registration No.");
			$(".employee-info").hide();

		} else if (s.tenantType.clientType === 'I') {
			$("#gender,.gender,#lblGender").show();
			$("#lbl-dob").html("Date of Birth");
			$("#lbl-id-no").html("ID No.");
			$(".employee-info").show();
		}

		if (s.authStatus && s.authStatus === "Y") {
			$("#clnt-authorised").text("Yes");
			$("#btn-save-tenant").css("display", "none");
			$("#btn-auth-client").css("display", "block");
			$("#btn-auth-client").val("Un-Authorise");
			$("#btn-add-client-type").css("display", "none");
			$("#clnt-title").select2("enable", false);
			$("#fname").attr("disabled", "disabled");
			$("#other-names").attr("disabled", "disabled");
			$("#pin-no").attr("disabled", "disabled");
			$("#email-address").attr("disabled", "disabled");
			$("#address").attr("disabled", "disabled");
			$("#id-no").attr("disabled", "disabled");
			$("#passport-no").attr("disabled", "disabled");
			$("#gender").attr("disabled", "disabled");
			$("#clnt-country,#sms-pref,#town-code-lov,#postal-code,#clnt-client-type,#clnt-title,#sect-def,#ten-branch,#occ-def,#phone-pref").select2("readonly", true);
			$("#office-tel-no").attr("disabled", "disabled");
			$("#sms-no").attr("disabled", "disabled");
			$("#phone-no").attr("disabled", "disabled");
			$("#btn-add-sms-prefix").css("display", "none");
			$("#btn-add-phone-prefix").css("display", "none");
			$("#sel3").attr("disabled", "disabled");
			$("#dob").attr("disabled", "disabled");
			$("#btn-add-docs").hide();

		} else {
			$("#clnt-authorised").text("No");
			$("#btn-auth-client").css("display", "block");
			$("#btn-save-tenant").css("display", "block");
			$("#btn-auth-client").val("Authorise");
			$("#btn-add-client-type").css("display", "block");
			$("#btn-add-sms-prefix").css("display", "block");
			$("#btn-add-phone-prefix").css("display", "block");
			$("#fname").removeAttr('disabled');
			$("#other-names").removeAttr('disabled');
			$("#pin-no").removeAttr('disabled');
			$("#email-address").removeAttr('disabled');
			$("#address").removeAttr('disabled');
			$("#id-no").removeAttr('disabled');
			$("#passport-no").removeAttr('disabled');
			$("#gender").removeAttr('disabled');
			$("#clnt-country,#sms-pref,#town-code-lov,#postal-code,#clnt-client-type,#clnt-title,#sect-def,#ten-branch,#occ-def,#phone-pref").select2("readonly", false);
			$("#office-tel-no").removeAttr('disabled');
			$("#sms-no").removeAttr('disabled');
			$("#phone-no").removeAttr('disabled');

			$("#sel3").removeAttr('disabled');
			$("#dob").removeAttr('disabled');
			$("#btn-add-docs").show();
		}
		$("#gender").val(s.gender);
		$("#ten-id").val(s.tenantNumber);
		$("#office-tel-no").val(s.officeTel);
		$("#date-reg").val(moment(s.dateregistered).format('DD/MM/YYYY'));

		$("#reg-brn-name").val(s.registeredbrn.obName);
		$("#obId").val(s.registeredbrn.obId);
		populateBranchA();
		if (s.country) {
			$("#cou-name").val(s.country.couName);
			$("#cou-id").val(s.country.couCode);
			populateCountryLov();
		}
		if (s.smsPrefix) {
			$("#pref-sms-id").val(s.smsPrefix.prefixId);
			$("#pref-sms-name").val(s.smsPrefix.prefixName);

		}
		if (s.phonePrefix) {
			$("#pref-phone-id").val(s.phonePrefix.prefixId);
			$("#pref-phone-name").val(s.phonePrefix.prefixName);
		}
		populateSmsPrefix(s.country.couCode);

		if (s.tenantType) {
			$("#clnt-type-id").val(s.tenantType.typeId);
			$("#clnt-type-name").val(s.tenantType.typeDesc);
		}
		populateClientTypeLov();
		if (s.clientTitle) {
			$("#clnt-title-id").val(s.clientTitle.titleId);
			$("#clnt-title-name").val(s.clientTitle.titleName);
		}
		populateTitlesLov();
		if (s.clientSector) {
			$("#sect-id").val(s.clientSector.code);
			$("#sect-name").val(s.clientSector.name);

		}
		createSectorSelect();
		if (s.occupation) {
			$("#occ-id").val(s.occupation.code);
			$("#occ-name").val(s.occupation.name);
			populateOccupations(s.clientSector.code);
		}

		if (s.town) {
			$("#clnt-town-id").val(s.town.ctCode);
			$("#clnt-town-name").val(s.town.ctName);
			createClientTownLov();
		}
		if (s.postalCodesDef) {
			$("#postal-code-id").val(s.postalCodesDef.pcode);
			$("#postal-name").val(s.postalCodesDef.zipCode);
			populatePostalCode(s.postalCodesDef.pcosde);
		}
		if (s.dateterminated)
			$("#dt-terminated").val(moment(s.dateterminated).format('DD/MM/YYYY'));
		tenantImage(s.tenId);
		getClientDocs(s.tenId);

		$.ajax({
			type: 'GET',
			url: 'getClientPage'
		}).done(function (s) {
			console.log('done!');
		}).fail({

		})
	}).fail({

	})
}*/
function displayTables() {
	$('.hide-col').hide();
	//$('#clTbl').hide();
	//$('#polRisDabl').hide();
	//$('#polTbl').hide();
	//$('#claimTbl').hide();
	//var polNo=$('#pol-search-number').val('');
	//var idNo=$('#id-client-number').val('');
	//var riskId=$('#risk-id-number').val('');
}

function toggleMedBusinessClass() {
	var table = $('#pol_enquiry_tbl').DataTable();
	$("#pol-search-number").val("");
	$("#rev-search-number").val("");
	$("#dr-search-number").val("");
	$("#rev-search-name").val("");
	$("#agent-search-number").val("");
	$("#product-search-number").val("");
	populateClientLov();
	createAccountsForSel();
	createProductForSel();
	$(".riskIdgroup").hide();
	//$('#pol_enquiry_tbl > tr').remove();
	//$('#pol_enquiry_tbl').empty();
	//policyEnquiry();
	//table.fnClearTable();
	$('#pol_enquiry_tbl tbody').remove();
}

function toggleNonMedBusinessClass() {
	var table = $('#pol_enquiry_tbl').DataTable();
	$("#pol-search-number").val("");
	$("#rev-search-number").val("");
	$("#dr-search-number").val("");
	$("#rev-search-name").val("");
	$("#agent-search-number").val("");
	$("#product-search-number").val("");
	populateClientLov();
	createAccountsForSel();
	createProductForSel();
	$(".riskIdgroup").show();
	//$('#pol_enquiry_tbl > tr').remove();
	//$('#pol_enquiry_tbl').empty();
	$('#pol_enquiry_tbl tbody').remove();
	//policyEnquiry();
	//table.fnClearTable();
}

function hideElements() {
	$('#hideRisk').hide();
}

/*
function makePolAllSelection() {
	var table = $('#polTbl').DataTable();
	$('#polTbl tbody').on( 'click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = table.rows('.table-primary').data();
		var data = aData[0].riskId;
		if (aData[0] === undefined || aData[0] === null){

		}
		else{
			makeUniqueRisk(data);
			makeRiskSelection();
		}
	} );
}

function makePolSelection(){
	var table = $('#polTbl').DataTable();
	$('#polTbl tbody').on( 'click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = table.rows('.table-primary').data();
		var polId = aData[0].policyId;
		if (aData[0] === undefined || aData[0] === null){

		}
		else{
			makeRiskDT(polId);
			makeRiskSelection();
		}
	} );
}
function makeRiskSelection(){
	var table = $('#polRisDabl').DataTable();

	$('#polRisDabl tbody').on( 'click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = table.rows('.table-primary').data();
		var riskId = aData[0].riskId;

		if (aData[0] === undefined || aData[0] === null){

		}
		else{
			makeClaimDT(riskId);
		}
	} );
}
*/

function masterEnquiryFun() {
	$('.btn-master').on('click', function () {
		console.log('Button clicked!');

		var polNo = $('#pol-id').val();
		var idNo = $('#client-id').val();
		var riskId = $('#risk-id').val();
		var url = "";
		if (polNo !== '' && idNo !== '' && riskId !== '') {
			url = 'masterEnqAll';
			searchMasterAll(url, polNo, idNo, riskId);
		} else if (polNo !== '' && idNo !== '' && riskId === '') {
			url = "masterEnqPI";
			searchMasterPolAndID(url, polNo, idNo);
		} else if (polNo !== '' && idNo === '' && riskId !== '') {
			url = "masterEnqPR";
			searchMasterPolAndRisk(url, polNo, idNo, riskId)
		} else if (polNo === '' && idNo !== '' && riskId !== '') {
			url = "masterEnqRI";
			searchMasterIdandRisk(url, polNo, idNo, riskId);
		} else if (polNo !== '' && idNo === '' && riskId === '') {
			url = "masterEnqPol";
			searchPolNo(url, polNo, idNo, riskId);
		} else if (polNo === '' && idNo !== '' && riskId === '') {
			url = "masterEnqIdNo";
			searchIdNo(url, polNo, idNo, riskId);
		} else if (polNo === '' && idNo === '' && riskId !== '') {
			url = "masterEnqRisk";
			searchRiskPol(url, polNo, idNo, riskId);
		} else if (polNo === '' && idNo === '' && riskId === '') {
			bootbox.alert("Provide a search Parameter to continue");
		}

	})
}

function searchMasterAll(url, polNo, idNo, riskId) {
	$.ajax({
		type: 'GET',
		url: "checkAllParam",
		data: {
			'idNo': idNo,
			'polNo': polNo,
			'riskId': riskId
		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No record matches the client,policy or risk');
		} else {
			idNo = s.insured.tenId;
			$('.hide-col').show();
			makeClientPC(url, polNo, idNo);
			makePolicyDTAll(url, polNo, idNo, riskId);
			makeRiskDTAll(url, polNo, idNo, riskId);
			hideElements();
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No record matches the client,policy and risk');
	});

}

function searchMasterPolAndID(url, polNo, idNo) {

	$.ajax({
		type: 'GET',
		url: "checkPolAndIdParam",
		data: {
			'idNo': idNo,
			'polNo': polNo,

		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No record matches the client and policy');
		} else {
			idNo = s.client.tenId;
			$('.hide-col').show();
			makeClientPC(url, polNo, idNo);
			makePolicyPC(url, polNo, idNo);
			hideElements();
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No record matches the client and policy');
	});

}

function searchMasterPolAndRisk(url, polNo, idNo, riskId) {
	$.ajax({
		type: 'GET',
		url: "checkPolAndRiskParam",
		data: {
			'riskId': riskId,
			'polNo': polNo

		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No record matches the policy and risk');
		} else {
			idNo = s.insured.tenId;
			$('.hide-col').show();
			makeClientPC(url, polNo, idNo);
			makePolicyDTAll(url, polNo, idNo, riskId);
			makeRiskDTAll(url, polNo, idNo, riskId);
			hideElements();
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No record matches the policy and risk');
	});

}

function searchMasterIdandRisk(url, polNo, idNo, riskId) {
	$.ajax({
		type: 'GET',
		url: "checkIdAndRiskParam",
		data: {
			'riskId': riskId,
			'idNo': idNo

		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No record matches the client and risk');
		} else {
			idNo = s.insured.tenId;
			$('.hide-col').show();
			makeClientPC(url, polNo, idNo);
			makePolicyDTAll(url, polNo, idNo, riskId);
			makeRiskDTAll(url, polNo, idNo, riskId);
			hideElements();
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No record matches the client and risk');
	});

}

function searchIdNo(url, polNo, idNo, riskId) {
	$.ajax({
		type: 'GET',
		url: "checkPolId",
		data: {
			'idNo': idNo
		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No Policies exist for this Client');
		} else {
			$('.hide-col').show();
			idNo = s.client.tenId;
			makeClientPC(url, polNo, idNo);
			makePolicyPC(url, polNo, idNo);
			makeReceiptPC(idNo);
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No Policies exist for this Client');
	});


}

function searchPolNo(url, polNo, idNo, riskId) {
	$.ajax({
		type: 'GET',
		url: "checkPol",
		data: {
			'polNo': polNo
		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No such policy exists');
		} else {
			idNo = s.client.tenId;

			$('.hide-col').show();
			makeClientPC(url, polNo, idNo);
			makePolicyPC(url, polNo, idNo);
			makeReceiptPC(idNo);
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No such policy exists');
	});

}

function backClaim(claim) {
	$.ajax({
		type: 'GET',
		url: "checkClaim",
		data: {
			'claim': claim

		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No such claim exists');
		} else {
			var polNo = s.risk.policy.policyId;
			var idNo = s.risk.insured.tenId;
			var riskId = s.risk.riskId;
			var url = '';
			$('.hide-col').show();
			makePolicyPC('masterEnqPol', polNo, idNo, '');
			makeClientPC(url, polNo, idNo);
			makeReceiptPC(idNo, polNo);
			makeRiskDT(polNo);
			makeClaimDT(riskId);
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No such policy exists');
	});

}

function backPolNo(url, polNo, idNo, riskId) {
	$.ajax({
		type: 'GET',
		url: "checkPol",
		data: {
			'polNo': polNo

		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No such policy exists');
		} else {
			idNo = s.client.tenId;

			$('.hide-col').show();
			makePolicyPC('masterEnqPol', polNo, idNo, riskId);
			makeClientPC(url, polNo, idNo);
			makeReceiptPC(idNo, polNo);
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No such policy exists');
	});

}

function searchRiskPol(url, polNo, idNo, riskId) {

	$.ajax({
		type: 'GET',
		url: "checkRiskId",
		data: {
			'riskId': riskId
		},
	}).done(function (s) {
		if (!$.trim(s)) {
			displayTables();
			bootbox.alert('No such risk exists');
		} else {
			idNo = s.insured.tenId;
			$('.hide-col').show();
			makeClientPC(url, polNo, idNo);
			makePolicyDTAll(url, polNo, idNo, riskId);
			makeReceiptPC(idNo);
		}
	}).fail(function (xhr, error) {
		bootbox.alert('No such risk exists');
	});

}

/*
function makeClientDTAll(url,polNo,idNo,riskId){
	var url = "searchPC";
	var currTable = $('#clTbl').DataTable( {
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"deferRender": true,
		"order": [[ 9, "desc" ]],
		"ajax": {
			'url': url,
			'data':{
				'idNo':idNo,
				'polNo': polNo,
				'riskId':riskId

			},
		},
		lengthMenu: [ [10,15,20], [10,15,20] ],
		pageLength: 10,
		destroy: true,
		"columns": [
			{ "data": "insured",
				"render": function ( data, type, full, meta ) {

					return full.insured.tenantNumber;
				}
			},
			{ "data": "insured",
				"render": function ( data, type, full, meta ) {

					return full.insured.fname;
				}
			},
			{ "data": "insured",
				"render": function ( data, type, full, meta ) {

					return full.insured.otherNames;
				}
			},
			{ "data":  "insured",
				"render": function ( data, type, full, meta ) {

					return full.insured.idNo;
				}
				},
			{ "data": "insured",
				"render": function ( data, type, full, meta ) {

					return full.insured.tenantType.clientType
				}
			},
			{ "data": "insured",
				"render": function ( data, type, full, meta ) {

					return full.insured.pinNo;
				}
			},
			{ "data": "insured" ,
				"render": function ( data, type, full, meta ) {
					return full.insured.registeredbrn.obName;
				}
			},
			{ "data":"insured" ,
				"render": function ( data, type, full, meta ) {
					return full.insured.phoneNo;
				}
			},
			{ "data":"insured" ,
				"render": function ( data, type, full, meta ) {
					return full.insured.emailAddress;
				}
			},

			{"data":"insured",
				"render": function ( data, type, full, meta ) {
					return '<form action="viewClients" method="post" enctype="multipart/form-data"><input type="hidden" id="clId" name="id" value=' + full.insured.tenId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View"></form>';
				}
				},
		]
	} );

	$('#clTbl').show();
	$('#hidePol').hide();
	$('#hideRisk').hide();
	return currTable;

}
*/
function makeClientPC(url, polNo, idNo) {
	var url = 'searchPC';
	var currTable = $('#clTbl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"deferRender": true,
		"order": [[9, "desc"]],
		"ajax": {
			'url': url,
			'data': {
				'idNo': idNo,
				'polNo': polNo

			},
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{
				"data": "tenantNumber"
			},
			{
				"data": "fname"
			},
			{
				"data": "otherNames"
			},
			{
				"data": "idNo"
			},
			{
				"data": "tenantType",
				"render": function (data, type, full, meta) {

					return full.tenantType.clientType
				}
			},
			{
				"data": "pinNo"
			},
			{
				"data": "registeredbrn",
				"render": function (data, type, full, meta) {
					return full.registeredbrn.obName;
				}
			},
			{
				"data": "phoneNo"
			},
			{
				"data": "emailAddress"
			},

			{
				"data": "tenId",
				"render": function (data, type, full, meta) {

					return '<form action="viewClients" method="post" enctype="multipart/form-data"><input type="hidden" id="clId" name="id" value=' + full.tenId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View"></form>';
				}
			},
		]
	});

	return currTable;

}

function makePolicyDTAll(url, polNo, idNo, riskId) {
	var url = url;
	var currTable = $('#polTbl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': {
				'idNo': idNo,
				'polNo': polNo,
				'riskId': riskId

			}
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [

			{
				"data": "policy",
				"render": function (data, type, full, meta) {

					return full.policy.polNo;
				}
			},
			{
				"data": "policy",
				"render": function (data, type, full, meta) {

					return full.policy.branch.obName;
				}
			},
			{
				"data": "policy",
				"render": function (data, type, full, meta) {

					return full.policy.product.proDesc;
				}
			},
			{
				"data": "policy",
				"render": function (data, type, full, meta) {

					return moment(full.policy.wefDate).format('DD/MM/YYYY');

				}
			},
			{
				"data": "policy",
				"render": function (data, type, full, meta) {
					return moment(full.policy.wetDate).format('DD/MM/YYYY');
				}
			},
			{
				"data": "policy",
				"render": function (data, type, full, meta) {

					return full.policy.status;
				}
			},
			{
				"data": "policy",
				"render": function (data, type, full, meta) {

					return full.policy.sumInsured;
				}
			},
			{
				"data": "policy",
				"render": function (data, type, full, meta) {

					return full.policy.premium;
				}
			},
			{
				"data": "policy",
				"render": function (data, type, full, meta) {
					return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policy.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
				}

			},
			{
				"data": "riskId",
				"render": function (data, type, full, meta) {
					return '<form id="editForm" method="post" enctype="multipart/form-data"><input type="hidden" id="uniqueRisk" name="id" value=' + full.riskId + '></form><button class="btn btn-success btn-sm btn-vrisk" >Risk</button>';

				}

			}
		]
	});
	$('#hideRisk').hide();
	$('#hideClaim').hide();
	$('#hidePol').show();
	$('#polTbl').show();
	return currTable;
}

function makePolicyPC(url, polNo, idNo, riskId) {

	var currTable = $('#polTbl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': {
				'idNo': idNo,
				'polNo': polNo,
			},
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{"data": "polNo"},
			{
				"data": "branch",
				"render": function (data, type, full, meta) {

					return full.branch.obName;
				}
			},
			{
				"data": "product",
				"render": function (data, type, full, meta) {

					return full.product.proDesc;
				}
			},

			{
				"data": "wefDate",
				"render": function (data, type, full, meta) {

					return moment(full.wefDate).format('DD/MM/YYYY');

				}
			},
			{
				"data": "wetDate",
				"render": function (data, type, full, meta) {
					return moment(full.wetDate).format('DD/MM/YYYY');
				}
			},
			{"data": "status"},
			{"data": "sumInsured"},
			{"data": "premium"},
			{
				"data": "policyId",
				"render": function (data, type, full, meta) {
					if ($('#pol-id').val() !== '') {
						return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
					} else {
						return '<form action="groupedituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';

					}
				}

			},
			{
				"data": "policyId",
				"render": function (data, type, full, meta) {

					return '<form id="editForm" method="post" enctype="multipart/form-data"><input type="hidden" id="polenqId" name="id" value=' + full.policyId + '></form><button class="btn btn-success btn-sm btn-vriskpol" >Risk</button>';

				}

			}
		]
	});
	console.log('inside method');
	$('#hidePol').show();
	$('#polTbl').show();
	$('#hideRisk').hide();
	$('#hideClaim').hide();
	return currTable;

}

function makeBackToPolicyPC(url, polNo, idNo, riskId) {
	url = 'masterEnqIdNo';

	var currTable = $('#polTbl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': {
				'idNo': idNo,
				'polNo': polNo
			},
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{"data": "polNo"},
			{
				"data": "branch",
				"render": function (data, type, full, meta) {

					return full.branch.obName;
				}
			},
			{
				"data": "product",
				"render": function (data, type, full, meta) {
					return full.product.proDesc;
				}
			},

			{
				"data": "wefDate",
				"render": function (data, type, full, meta) {

					return moment(full.wefDate).format('DD/MM/YYYY');

				}
			},
			{
				"data": "wetDate",
				"render": function (data, type, full, meta) {
					return moment(full.wetDate).format('DD/MM/YYYY');
				}
			},
			{"data": "status"},
			{"data": "sumInsured"},
			{"data": "premium"},
			{
				"data": "policyId",
				"render": function (data, type, full, meta) {
					return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
				}

			},
			{
				"data": "policyId",
				"render": function (data, type, full, meta) {
					return '<form id="editForm" method="post" enctype="multipart/form-data"><input type="hidden" id="polenqId" name="id" value=' + full.policyId + '></form><button class="btn btn-success btn-sm btn-vriskpol" >Risk</button>';

				}

			}
		]
	});
	$('#hidePol').show();
	$('#polTbl').show();
	$('#hideRisk').hide();
	$('#hideClaim').hide();
	console.log("End of table");
	return currTable;

}

function makeReceiptPC(idNo, polNo) {
	var url = "getReceipts";

	var currTable = $('#rcptTbl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': {
				'idNo': idNo,
				'polNo': polNo
			},
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{"data": "receiptNo"},
			{"data": "receiptDesc"},
			{"data": "receiptAmount"},
			// { "data": "client",
			// 	"render": function ( data, type, full, meta ) {
			//
			// 		return full.client.fname+" "+full.client.otherNames;
			// 	}
			// },
			{
				"data": "branch",
				"render": function (data, type, full, meta) {

					return full.branch.obName;
				}
			},


			{
				"data": "receiptDate",
				"render": function (data, type, full, meta) {

					return moment(full.receiptDate).format('DD/MM/YYYY');

				}
			},
			{
				"data": "receiptTransDate",
				"render": function (data, type, full, meta) {
					return moment(full.receiptTransDate).format('DD/MM/YYYY');
				}
			}
		]
	});
	$('#hidePol').show();
	$('#polTbl').show();
	$('#hideRisk').hide();
	$('#hideClaim').hide();
	$('#hideDet').hide();

	$('#rcptTbl tbody').on('click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = currTable.rows('.table-primary').data();
		if (aData[0] === undefined || aData[0] === null) {

		} else {
			makeReceiptDetails(aData[0].receiptId);

		}
	})
	return currTable;

}

function makeReceiptDetails(receiptId) {
	var url = "getReceiptsDets/" + receiptId;

	var currTable = $('#rcptdtlsTbl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{
				"data": "receipt",
				"render": function (data, type, full, meta) {

					return full.receipt.receiptNo;
				}
			},
			{
				"data": "transaction",
				"render": function (data, type, full, meta) {

					return full.transaction != null ? full.transaction.refNo : full.transactionsTemp.refNo;
				}
			},
			{
				"data": "transaction",
				"render": function (data, type, full, meta) {

					return full.transaction != null ? full.transaction.transdc : full.transactionsTemp.transdc;
				}
			},
			{
				"data": "transaction",
				"render": function (data, type, full, meta) {

					return full.transaction != null ? full.transaction.amount : full.transactionsTemp.amount;
				}
			},
			{
				"data": "transaction",
				"render": function (data, type, full, meta) {

					return full.transaction != null ? moment(full.transaction.transDate).format('DD/MM/YYYY') : moment(full.transactionsTemp.transDate).format('DD/MM/YYYY');
				}
			},

			{
				"data": "policy",
				"render": function (data, type, full, meta) {
					if (full.policy != null)
						return full.policy.polNo;
					else
						return "Not Available";

				}
			},

			{
				"data": "narration"

			},
		]
	});
	$('#hidePol').show();
	$('#polTbl').show();
	$('#hideRisk').hide();
	$('#hideClaim').hide();
	$('#hideDet').show();

	return currTable;

}

function makeUniqueRisk(riskId) {
	var url = "masterEnqUniqueId";
	var currTable = $('#polRisDabl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': {
				'riskId': riskId
			},
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{
				"data": "riskShtDesc"
			},
			{
				"data": "riskDesc",
			},
			{
				"data": "wefDate"
			},
			{
				"data": "wetDate"
			},

			{
				"data": "sumInsured"
			},
			{
				"data": "riskId",
				"render": function (data, type, full, meta) {
					return '<form><input type="hidden" id="risenqId" value=' + full.riskId + '></form><button  class="btn btn-success btn btn-info btn-sm btn-vpolclaim">Claim</button>';
				}

			}
		]
	});
	$('#hideRisk').show();
	$('#polRisDabl').show();
	return currTable;
}

function makeRiskDTAll(url, polNo, idNo, riskId) {
	var url = url;
	var currTable = $('#polRisDabl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': {
				'polNo': polNo,
				'idNo': idNo,
				'riskId': riskId
			},
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{
				"data": "riskShtDesc"
			},
			{
				"data": "riskDesc",
			},
			{
				"data": "wefDate"
			},
			{
				"data": "wetDate"
			},

			{
				"data": "sumInsured"
			},
			{
				"data": "riskId",
				"render": function (data, type, full, meta) {
					return '<form><input type="hidden" id="risenqId" value=' + full.riskId + '></form><button  class="btn btn-success btn btn-info btn-sm btn-vpolclaim">Claim</button>';
				}

			}
		]
	});
	$('#hideRisk').show();
	$('#polRisDabl').show();
	return currTable;
}

/*
function makeUniqueRisk(riskId) {
	var url="masterEnqUniqueId";
	var currTable = $('#polRisDabl').DataTable( {
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data':{
				'riskId':riskId
			},
		},
		lengthMenu: [ [10,15,20], [10,15,20] ],
		pageLength: 10,
		destroy: true,
		"columns": [
			{ "data": "riskShtDesc"
			},
			{ "data": "riskDesc",
			},
			{ "data": "wefDate"
			},
			{ "data": "wetDate"
			},

			{ "data": "sumInsured"
			},
			{ "data": "premium"
			},
			{"data":"riskId",
				"render": function ( data, type, full, meta ) {
					return '<form id="editForm" method="post" enctype="multipart/form-data"><input type="hidden" id="polriskenqId" name="riskId" value='+full.riskId+'></form><button class="btn btn-success btn-sm btn-vclaim">View Claim</button>';

				}
			}
		]
	} );
	$('#hideRisk').show();
	$('#polRisDabl').show();
	return currTable;
}
*/
function makeRiskDT(policyId) {
	var url = 'searchPolRisk';
	var currTable = $('#polRisDabl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': {
				'policyId': policyId
			}
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{"data": "riskShtDesc"},
			{"data": "riskDesc"},
			{
				"data": "wefDate",
				"render": function (data, type, full, meta) {

					return moment(full.wefDate).format('DD/MM/YYYY');
				}
			},
			{
				"data": "wetDate",
				"render": function (data, type, full, meta) {

					return moment(full.wetDate).format('DD/MM/YYYY');
				}
			},
			{"data": "sumInsured"},
			{
				"data": "riskId",
				"render": function (data, type, full, meta) {
					return '<form><input type="hidden" id="risenqId" value=' + full.riskId + '></form><button  class="btn btn-success btn btn-info btn-sm btn-vpolclaim">Claim</button>';
				}

			}
		]
	});
	$("#hideClaim").hide();
	$('#hideRisk').show();
	$('#polRisDabl').show();
	return currTable;
}

function makeClaimDT(riskId) {
	var url = "masterEnqUniqueClaim";

	var currTable = $('#claimTbl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': {
				'riskId': riskId
			}
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{
				"data": "claimBookings",
				"render": function (data, type, full, meta) {

					return full.claimBookings.claimNo;
				}
			},
			{
				"data": "claimBookings",
				"render": function (data, type, full, meta) {

					return full.claimBookings.lossDesc;
				}
			},
			{
				"data": "claimBookings",
				"render": function (data, type, full, meta) {

					return moment(full.claimBookings.lossDate).format('DD/MM/YYYY');
				}
			},
			{
				"data": "claimBookings",
				"render": function (data, type, full, meta) {

					return full.claimBookings.risk.riskShtDesc;
				}
			},
			{
				"data": "claimBookings",
				"render": function (data, type, full, meta) {

					return moment(full.claimBookings.clmDate).format('DD/MM/YYYY');
				}
			},
			{"data": "reserve"},
			{
				"data": "perilsDef",
				"render": function (data, type, full, meta) {

					return full.perilsDef.perilDesc;
				}
			},
			{
				"data": "claimBookings",
				"render": function (data, type, full, meta) {

					return full.claimBookings.claimStatus;
				}

			},
			{
				"data": "claimBookings",
				"render": function (data, type, full, meta) {
					return '<form action="claimtrans" method="post"><input type="hidden" name="id" value=' + full.claimBookings.clmId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="View" ></form>';
				}

			},

		]
	});
	$('#hideClaim').show();
	$('#claimTbl').show();
	return currTable;
}

function getUserTrans() {

	var url = "policyTrans";
	var currTable = $('#pol_tbl').DataTable({
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"deferRender": true,
		"order": [[9, "desc"]],
		"ajax": {
			'url': url,
		},
		lengthMenu: [[10, 15, 20], [10, 15, 20]],
		pageLength: 10,
		destroy: true,
		"columns": [
			{"data": "taskId"},
			{"data": "activeProcess"},
			{
				"data": "policyTrans",
				"render": function (data, type, full, meta) {

					return full.policyTrans.polNo;
				}
			},
			{
				"data": "policyTrans",
				"render": function (data, type, full, meta) {

					return full.policyTrans.polRevNo;
				}
			},
			{
				"data": "policyTrans",
				"render": function (data, type, full, meta) {

					return full.policyTrans.product.proDesc;
				}
			},
			{
				"data": "policyTrans",
				"render": function (data, type, full, meta) {
					return moment(full.policyTrans.wefDate).format('DD/MM/YYYY');
				}
			},
			{
				"data": "policyTrans",
				"render": function (data, type, full, meta) {
					return moment(full.policyTrans.wetDate).format('DD/MM/YYYY');
				}
			},
			{
				"data": "policyTrans",
				"render": function (data, type, full, meta) {

					return full.policyTrans.client.fname + " " + full.policyTrans.client.otherNames;
				}
			},
			{
				"data": "policyTrans",
				"render": function (data, type, full, meta) {

					return full.policyTrans.createdUser.username;
				}
			},
			{
				"data": "policyTrans",
				"render": function (data, type, full, meta) {
					if (canAccess) {
						return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyTrans.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="Edit" ></form>';
					} else return "";
				}

			},
		]
	});
	return currTable;
}

function populateClientLov() {
	if ($("#client-frm").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "client-frm",
			sort: 'fname',
			change: function (e, a, v) {
				$("#rev-search-name").val(e.added.tenId);
				$('#client-id').val(e.added.tenId);
			},
			formatResult: function (a) {
				return a.fname + " " + a.otherNames;
			},
			formatSelection: function (a) {
				return a.fname + " " + a.otherNames;
			},
			initSelection: function (element, callback) {

			},
			id: "tenId",
			placeholder: "Select Client"

		});
	}

	$("#client-frm").on("select2-removed", function (e) {
		$("#rev-search-name").val('');
	})
}

function populatePolicyLov() {
	if ($("#pol-cont").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "pol-cont",
			sort: 'polNo',
			change: function (e, a, v) {
				$("#pol-id").val(e.added.policyId);
			},
			formatResult: function (a) {
				return a.polNo;
			},
			formatSelection: function (a) {
				return a.polNo;
			},
			initSelection: function (element, callback) {

			},
			id: "policyId",
			placeholder: "Select Policy"

		});
	}

	$("#pol-cont").on("select2-removed", function (e) {
		$("#pol-id").val('');
	})
}

function populateRiskLov() {
	if ($("#risk-cont").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "risk-cont",
			sort: 'riskShtDesc',
			change: function (e, a, v) {
				$("#risk-id").val(e.added.riskId);
			},
			formatResult: function (a) {
				return a.riskShtDesc;
			},
			formatSelection: function (a) {
				return a.riskShtDesc;
			},
			initSelection: function (element, callback) {

			},
			id: "riskId",
			placeholder: "Select Risk"

		});
	}

	$("#risk-cont").on("select2-removed", function (e) {
		$("#risk-id").val('');
	})
}

function createAccountsForSel() {
	if ($("#acc-frm").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "acc-frm",
			sort: 'name',
			change: function (e, a, v) {
				$("#agent-search-number").val(e.added.acctId);
			},
			formatResult: function (a) {
				return a.name
			},
			formatSelection: function (a) {
				return a.name
			},
			initSelection: function (element, callback) {

			},
			id: "acctId",
			placeholder: "Select Account",
		});
	}

	$("#acc-frm").on("select2-removed", function (e) {
		$("#agent-search-number").val('');
	})
}


function createProductForSel() {
	if ($("#prd-code").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "prd-code",
			sort: 'proDesc',
			change: function (e, a, v) {
				$("#product-search-number").val(e.added.proCode);
			},
			formatResult: function (a) {
				return a.proDesc
			},
			formatSelection: function (a) {
				return a.proDesc
			},
			initSelection: function (element, callback) {

			},
			id: "proCode",
			placeholder: "Select Product",
		});
	}

	$("#prd-code").on("select2-removed", function (e) {
		$("#product-search-number").val('');
	})
}

function policyEnquiry(unifiedSearch) {
	var url = "enquiryPolicies";
	return $('#pol_enquiry_tbl').DataTable({
			"processing": true,
			"serverSide": true,
			autoWidth: true,
			searching: false,
		    scrollX: true,
			columnDefs: [
				{ width: 150, targets: 3 },
				{ width: 150, targets: 6 },
				{ width: 150, targets: 7 },
			],
			"deferRender": true,
			"ajax": {
				'url': url,
				'data': {
					'searchTerm': unifiedSearch
				},
			},
			lengthMenu: [[10, 15, 20], [10, 15, 20]],
			pageLength: 10,
			destroy: true,
			"columns": [
				{
					"data": "policyId",
					"render": function (data, type, full, meta) {
						return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value=' + full.policyId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="More Details" ></form>';
					}
				},
				{"data": "policyNo"},
				{"data": "polRevNo"},
				{"data": "product"},
				{
					"data": "wefDate",
					"render": function (data, type, full, meta) {
						return moment(full.wefDate).format('DD/MM/YYYY');
					}
				},
				{
					"data": "wetDate",
					"render": function (data, type, full, meta) {
						return moment(full.wetDate).format('DD/MM/YYYY');
					}
				},
				{"data": "clientName"},
				{"data": "agentName"},
				{
					"data": "transType",
					"render": function (data, type, full, meta) {
						// Example: title case or label mapping
						return full.transType ? full.transType.toUpperCase() : '';
					}
				},

				{"data": "currency"},
				{"data": "username"},
				{"data": "authComments"}
			]
		});

}




