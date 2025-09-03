var UWScreen = (function($){
	'use strict';
	var selRiskCode = -2000;

	function getPolicyTerms(binCode,proDesc) {
		$('#pol-term option').remove();
		$('#pol-term').append($('<option>', {
			value: "",
			text : "Select Term"
		}));
		$.ajax({
			type: 'GET',
			url: 'uw/policies/binderPolTerms/'+binCode,
			dataType: 'json',
			async: true,
			success: function (result) {
				console.log(result);
				if(proDesc.toUpperCase().includes("AKIBA")) {
					const akibaTerms = [6, 9, 12, 15, 18];
					for(var res in result) {
						if (akibaTerms.includes(result[res].term)) {
							$('#pol-term').append($('<option>', {
								value: result[res].term,
								text: result[res].termDisplay
							}));
						}
					}
				} else {
					for (var res in result) {
						$('#pol-term').append($('<option>', {
							value: result[res].term,
							text: result[res].termDisplay
						}));
					}
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
			}
		});
	};





	var populatePolicyDetails = function(){

		if(typeof polCode!== 'undefined'){
			if(polCode!==-2000){
				console.log('polCode '+polCode);
				$("#btn-uw-reports").show();
				$("#risk-form").hide();
				$("#btn-save-risk").hide();
				$("#btn-save-cancel").hide();
				$("#btn-add-risk").show();
				$("#risk-div").show();
				$("#sect-div").show();
				$("#prem-rates-div").hide();
				$("#btn-import-risk").show();
				getPolicyDetails();
				getPolicyRemakrs();
                $(".show-output").show();
				$("#myTab, #show-taxes, #show-clauses, #end-remarks, #checks-tab, #interested-parties, #est-ben, #instal-schedule, #show_recpts").show();
				$("#display-client").css('display','block');
				$("#pol-term").css('display','none');
				$("#pol-term-display").css('display','block');
				$(".import-risks").hide();
				$(".computetype").hide();
				$(".premium-disp").show();
				$(".sumassured-disp").show();
				getPolicyInstallments();
				$("#other-pol-details").show();
			}
			else{
			    $(".show-output").css("display","none");
				$("#btn-auth-policy").css("display","none");
				$("#btn-dispatch-trans").hide();
				$("#btn-import-risk").hide();
				$("#btn-uw-reports").hide();
				$("#risk-form").show();
				$("#risk-div").hide();
				$("#other-pol-details").hide();
				$("#prem-rates-div").show();
				$("#btn-add-policy").show();
				$("#pol-term").css('display','block');
				$("#pol-term-display").css('display','none');
				$("#sect-div").hide();
				$("#btn-save-risk").hide();
				$("#btn-save-cancel").hide();
				$("#btn-add-risk").hide();
				$("#myTab #show-taxes,#show-clauses").hide();
				$(".hide-details").hide();
				$("#btn-add-new-section").hide();
				$("#display-client").css('display','none');
				$("#display-binder").css('display','none');
				$("#display-payment-mode").hide();
				$("#display-branch").hide();
				$("#display-currency").hide();
				$(".import-risks").show();
				$(".computetype").show();
				$(".premium-disp").hide();
				$(".sumassured-disp").hide();
				$("#btn-convert-policy").hide();
				$("#btn-assign-trans").hide();
			}
		}
	};

	var getPolicyRemakrs = function(){
		if(typeof polCode!== 'undefined'){
			if(polCode!==-2000){
				$.ajax( {
					url: 'uw/policies/getPolicyRemarks',
					type: 'GET',
					processData: false,
					contentType: false,
					success: function (s ) {

						$("#poli-remarks").val(s.polRemarks);
						$("#pol-remark-pk").val(s.remarksId);
						if(s.endRemarks)
							$("#remark-pk").val(s.endRemarks.remarkId);
						$("#remark-pol-id").val(polCode);
					},
					error: function(xhr, error){
						bootbox.alert(xhr.responseText);
					}
				});
			}
		}
	};

	var getPolicyDetails = function(){
		if(typeof polCode!== 'undefined'){
			if(polCode!==-2000){
				$.ajax( {
					url: 'uw/policies/getPolicyDetails',
					type: 'GET',
					processData: false,
					contentType: false,
					success: function (s ) {
						if(s.product.riskNote != null && s.product.riskNote != ""){
							var reportTemplate = s.product.riskNote;
							var proposalUrl = SERVLET_CONTEXT + '/protected/uw/policies/'+reportTemplate;
						}
						else {
							var productName = "default";

							// Replace spaces with underscores and convert to lowercase
							productName = productName.trim().toLowerCase().replace(/\s+/g, '_');

							// Build the URL based on the modified product name
							var proposalUrl = SERVLET_CONTEXT + '/protected/uw/policies/rpt_' + productName + '_client_quote';

						}
						console.log(s)
						// Set the href attribute of the proposal link
						$('#proposal-link').attr('href', proposalUrl);
						console.log("Updated Proposal URL: " + proposalUrl);

						console.log("to check status being added: " + s.authStatus);
						console.log("Policy task ID: " + mckPolId)
						console.log("Policy checker code: " + mckPolCheckerCode)
						if (s.binder.binType === 'B'){
						    $("#pol-bin-type").html("Contract");
						} else {
						    $("#pol-bin-type").html("Negotiated");
						}
						$("#from-date").html(moment(s.wefDate).format('DD/MM/YYYY'));
                        $("#wet-date").html(moment(s.wetDate).format('DD/MM/YYYY'));
                        if (s.frequency === 'W'){
                            $("#pol-frequency").html("Weekly");
                        } else if (s.frequency === 'M'){
                            $("#pol-frequency").html("Monthly");
                        } else if (s.frequency === 'Q'){
                            $("#pol-frequency").html("Quarterly");
                        } else if (s.frequency === 'S'){
                            $("#pol-frequency").html("Semi-Annually");
                        } else if (s.frequency === 'A'){
                            $("#pol-frequency").html("Annually");
                        } else if (s.frequency === 'SG'){
                            $("#pol-frequency").html("Single");
                        }
                        $("#client-pol-no").html(s.clientPolNo);
                        $("#pol-status").html("Under Review");
						polCode = s.policyId;
						getUWPolicyRisks(s.policyId);
						getUWPolicyReceipts(s.policyId);
						getPolicybeneficiaries();
						getPolicybenefits();
						getPolicyInstallments();
						UTILITIES.getProcessActiveDiagram(s.policyId);
						UTILITIES.getTaskActive(s.policyId);
						UTILITIES.getProcessHistory(s.policyId);
						$("#client-info").text(s.client.fname+" "+s.client.otherNames);
						$("#binder-info").text(s.binder.binName);
						if(s.paymentMode)
						console.log(s)
						$("#pay-mode-info").text(s.paymentMode.pmDesc);
						$("#branch-info").text(s.branch.obName);
						$("#currency-info").text(s.transCurrency.curName);
						console.log("s.totalInstalments="+s.totalInstalments)
						$("#pol-tot-inst").text(s.totalInstalments);
						$("#pol-no").text(s.polNo);
						$("#pol-term-display").css('display','block').text(s.polTerm);
						$("#prop-no").text(s.proposalNo);
						$("#div-pol-no").val(s.polNo);
						$("#div-endos-no").val(s.polRevNo);
						$("#policy-id").val(s.policyId);
						$("#pol-rev-no").text(s.polRevNo);
						$("#pol-bind-age-appli").val(s.product.ageApplicable);
						$("#pol-sub-agent-comm").text(UTILITIES.currencyFormat(s.subAgentComm));
						$("#pol-sum-insured").text(UTILITIES.currencyFormat(s.sumInsured));
						var numberFormatter = new Intl.NumberFormat('en-US', {
                                   style: 'decimal',
                                   minimumFractionDigits: 2,
                                   maximumFractionDigits: 2
                               });
						if (s.negotiatedPremium != null) {
                            $("#negotiated").text(numberFormatter.format(s.negotiatedPremium));
                            $("#pol-premium").text(numberFormatter.format(s.negotiatedPremium));
                        } else {
                            $("#negotiated").text("N/A"); // display a placeholder if no value exists
                            $("#pol-premium").text(UTILITIES.currencyFormat(s.premium));
                        }

						$("#pol-basic-prem").text(UTILITIES.currencyFormat(s.basicPrem));
						$("#pol-rev-bonus").text(UTILITIES.currencyFormat(s.revBonus));
						$("#pol-term-bonus").text(UTILITIES.currencyFormat(s.terminalBonus));
						$("#pol-tax-relief").text(UTILITIES.currencyFormat(s.taxRelief));
						$("#pol-net-prem").text(UTILITIES.currencyFormat(s.taxRelief));
						$("#pol-taxes-amt").text(s.taxes);

						if(s.product.motorProduct){
							$(".motor-disp").show();
							$(".non-motor-disp").hide();
						}
						else{
							$(".motor-disp").hide();
							$(".non-motor-disp").show();
						}
						$("#pol-prod-name").text(s.product.proDesc);
						$("#from-date").val(moment(s.wefDate).format('DD/MM/YYYY'));
						$("#wet-date").val(moment(s.wetDate).format('DD/MM/YYYY'));
						$('#pol-ins-comp').text(s.agent.name)
						$("#pol-interface-type").val(s.interfaceType);
						$("#negotiated-prem").val(s.negotiatedPremium);
						$("#pol-frequency").val(s.frequency);
						$("#client-pol-no").val(s.clientPolNo);
						$("#pol-buss-type").val(s.businessType);
						if(s.subAgent){
							$("#sub-agent-id").val(s.subAgent.acctId);
							$("#sub-agent-name").val(s.subAgent.name);
							populateSubAgentsLov();
						}
						$("#cur-id").val(s.transCurrency.curCode);
						$("#cur-name").val(s.transCurrency.curName);
						populateCurrencyLov();
						$("#client-id").val(s.client.tenId);
						$("#client-f-name").val(s.client.fname);
						$("#client-other-name").val(s.client.otherNames);
						populateClientLov();
						getPolicyTerms(s.binder.binId,s.product.proDesc);
						console.log(s.polTerm);
						$("#pol-term").val(s.polTerm);
						$("#risk-binder").text(s.binder.binName);
						$("#risk-binder-id").val(s.binder.binId);
						$("#risk-binder-code").val(s.binder.binId);
						$("#risk-bind-code").val(s.binder.binId);
						$("#binder-id").val(s.binder.binId);
						$("#product-id").val(s.product.proCode);
						$("#pol-agent-id").val(s.agent.acctId);
						$("#bind-name").val(s.binder.binName);
						$("#pol-binder-policy").val(s.binder.binType);
						populateBinderLov();
						$("#pm-id").val(s.paymentMode.pmId);
						$("#pm-name").val(s.paymentMode.pmDesc);
						populatePaymentModes();
						$("#brn-id").val(s.branch.obId);
						$("#brn-name").val(s.branch.obName);
						$('#pol-comm-amt').text(UTILITIES.currencyFormat(s.commAmt));
						$('#pol-tl').text(UTILITIES.currencyFormat(s.trainingLevy));
						$('#pol-phcf').text(UTILITIES.currencyFormat(s.phcf));
						$('#pol-sd').text(UTILITIES.currencyFormat(s.stampDuty));
						$('#pol-whtx').text(UTILITIES.currencyFormat(s.whtx));
						$('#pol-extras').text(UTILITIES.currencyFormat(s.extras));
						$("#pol-fap").text(UTILITIES.currencyFormat(s.futurePrem));
						$("#pol-bin-type").val(s.binder.binType);
						$('#pol-tran-type-disp').text(s.transType);
						$('#pol-trans-type').val(s.transType);
						$('#pol-prev-policy').val(s.prevPolicy);
						$('#pol-prev-policy').val(s.prevPolicy);
						$('#pol-reuse-contra-policy').val(s.reusecontraPolicy);

						if(s.transType==="EN"){
							if(s.authStatus==="D"){
								$("#btn-endors-risk").show();
							}
							else{
								$("#btn-endors-risk").hide();
							}
							$("ul.reports-links li.endorse-disp").show();

						}
						else{
							$("#btn-endors-risk").hide();
							$("ul.reports-links li.endorse-disp").hide();
						}

						if(s.transType==="CO" || s.transType==="CN"){
							$("#btn-undo-make-ready").css("display","none");
							$("#btn-make-ready-policy").css("display","none");
							$("#btn-negotiate-premium").css("display","none");
						}

						if(s.transType==="RN"){
							$("#renbtn").show();
						}
						else{
							$("#renbtn").hide();
						}
						$("#binder-frm").select2("enable", false);
						$("#pol-bintype").prop("disabled", true);
						populateUserBranches();
						if(s.renewalDate)
							$("#pol-ren-date").text(moment(s.renewalDate).format('DD/MM/YYYY'));
					},
					error: function(xhr, error){
						bootbox.alert(xhr.responseText);
					}
				});
			}
			else{
				$("#display-client").hide();
				$("#display-binder").css('display','none');
				$("#display-payment-mode").hide();
				$("#display-branch").hide();
				$("#pol-term-display").css('display','none')
			}
		}
	};

	var changePolicyWetDt = function(){
		$('#cover-to-date').on('dp.change', function (ev) {
			var curDate = ev.date;
			var dt = moment(curDate).format('DD/MM/YYYY');
			$("#risk-wet-date").val(dt);
		});
		$('#risk-cover-from').on('dp.change', function (ev) {
			var curDate = ev.date;
			var dt = moment(curDate);
			var polwef = $("#from-date").val();
			var wet = $("#wet-date").val();

		});
		$('#risk-cover-to').on('dp.change', function (ev) {
			var curDate = ev.date;
			var dt = moment(curDate).format('DD/MM/YYYY');;
			var polwef = $("#from-date").val();
			var polwet = $("#wet-date").val();

		});
	};

	var validateRisk = function(){
		$('#risk-id').on('change', function () {
			var riskId = $("#risk-id").val();
			var subCode = $("#risk-sub-code").val();
			$.ajax({
				type: 'GET',
				url:  'uw/policies/validateRisk',
				dataType: 'json',
				data: {"riskId":riskId,"sclCode":subCode},
				async: true,
				success: function(result) {

				},
				error: function(jqXHR, textStatus, errorThrown) {
					Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
					$("#risk-id").val("");
				}
			});
		});
	};

	var getPolicyWet = function(){
		$('#wef-date').on('dp.change', function (ev) {
			var curDate = ev.date;
			console.log(curDate);
			var dt = moment(curDate).format('DD/MM/YYYY');
			$("#risk-wef-date").val(dt);
			$.ajax({
				type: 'GET',
				url:  SERVLET_CONTEXT+'/protected/uw/policies/getMaturityDate',
				dataType: 'json',
				data: {"wefDate":dt,"polTerm":$('#pol-term').val()},
				async: true,
				success: function(result) {
					$("#wet-date").val(moment(result).format('DD/MM/YYYY'));
					$("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));


				},
				error: function(jqXHR, textStatus, errorThrown) {

				}
			});
		});
		$("#pol-term").on('change', function(){

			if (!($("#from-date").val()===null)) {
			$.ajax({
				type: 'GET',
				url:  SERVLET_CONTEXT+'/protected/uw/policies/getMaturityDate',
				dataType: 'json',
				data: {"wefDate":$("#from-date").val(),"polTerm":$('#pol-term').val()},
				async: true,
				success: function(result) {
					$("#wet-date").val(moment(result).format('DD/MM/YYYY'));
					$("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));


				},
				error: function(jqXHR, textStatus, errorThrown) {

				}
			});
		}
		});
	};



	var populateInsuredLov = function(){

		if($("#lifeassured-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "lifeassured-frm",
				sort : 'fname',
				change: function(e, a, v){

					if($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val()==="Y"){

						$.ajax({
							type: 'GET',
							url:  'uw/policies/getLifeClientAge',
							dataType: 'json',
							data: {"clientId": e.added.tenId ,"binCode" :$("#binder-id").val()},
							async: true,
							success: function(result) {

								$("#lifeassured-age").val(result);
								$("#lifeassured-code").val(e.added.tenId);
							},
							error: function(jqXHR, textStatus, errorThrown) {
								Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
							}
						});
					}else
						$("#lifeassured-code").val(e.added.tenId);
					//$("#insured-id").val(e.added.tenId);
				},
				formatResult : function(a)
				{
					return a.fname+" "+a.otherNames+" - "+a.idNo;
				},
				formatSelection : function(a)
				{
					return a.fname+" "+a.otherNames+" - "+a.idNo;
				},
				initSelection: function (element, callback) {
					var code = $("#lifeassured-code").val();
					var name = $("#lifeassured-name").val();
					var othernames = $("#lifeassured-other-name").val();
					var data = {fname:name,otherNames:othernames,tenId:code};
					callback(data);
				},
				id: "tenId",
				width:"250px",
				placeholder:"Select Insured"

			});
		}
	}

	var populateClientLov = function(){
		if($("#client-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "client-frm",
				sort : 'fname',
				change: function(e, a, v){
					$("#client-id").val(e.added.tenId);
						$("#lifeassured-age").val("");
						populateInsuredLov();


				},
				formatResult : function(a)
				{
					return a.fname+" "+a.otherNames+" - "+a.idNo;
				},
				formatSelection : function(a)
				{
					return a.fname+" "+a.otherNames+" - "+a.idNo;
				},
				initSelection: function (element, callback) {
					var code = $("#client-id").val();
					var name = $("#client-f-name").val();
					var othernames = $("#client-other-name").val();
					var data = {fname:name,otherNames:othernames,tenId:code};
					callback(data);
				},
				id: "tenId",
				placeholder:"Select Assured"

			});
		}
	};




	var populateSubAgentsLov = function(){
		if($("#sub-agent-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "sub-agent-frm",
				sort : 'name',
				change: function(e, a, v){
					$("#sub-agent-id").val(e.added.acctId);

				},
				formatResult : function(a)
				{
					return a.name;
				},
				formatSelection : function(a)
				{
					return a.name;
				},
				initSelection: function (element, callback) {
					var code  = $('#sub-agent-id').val();
					var name = $("#sub-agent-name").val();
					var data = {name:name,acctId:code};
					callback(data);
				},
				id: "acctId",
				width:"250px",
				placeholder:"Select Sub Agent"

			});

			$("#sub-agent-frm").on("select2-removed", function(e) {
				$("#sub-agent-id").val('');
			})
		}
	};

	var populateCurrencyLov = function(){
		if($("#curr-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "curr-frm",
				sort : 'curName',
				change: function(e, a, v){
					$("#cur-id").val(e.added.curCode);

				},
				formatResult : function(a)
				{
					return a.curName;
				},
				formatSelection : function(a)
				{
					return a.curName;
				},
				initSelection: function (element, callback) {
					var code  = $('#cur-id').val();
					var name = $("#cur-name").val();
					var data = {curName:name,curCode:code};
					callback(data);
				},
				id: "curCode",
				width:"250px",
				placeholder:"Select Currency"

			});
		}
	};

	var populateSubclassLov = function(){
		if($("#subclass-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "subclass-frm",
				sort : 'detId',
				change: function(e, a, v){
					$("#risk-sub-code").val(e.added.subId);
					populateCoverTypesLov();

				},
				formatResult : function(a)
				{
					return a.subDesc;
				},
				formatSelection : function(a)
				{
					return a.subDesc;
				},
				initSelection: function (element, callback) {
					var code  = $('#risk-sub-code').val();
					var name = $("#sub-name").val();
					var data = {subDesc:name,subId:code};
					callback(data);
				},
				id: "subDesc",
				width:"250px",
				params: {bindCode: $("#risk-bind-code").val()},
				placeholder:"Select Sub Class"

			});
		}
	};

	var populateCoverTypesLov = function(){
		if($("#covertypes-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "covertypes-frm",
				sort : 'detId',
				change: function(e, a, v){
					$("#risk-cov-code").val(e.added.covId);
					$("#binder-det-id").val(e.added.detId);
					//getCommissionRate(e.added.detId);
					getPremiumRates(e.added.detId);
				},
				formatResult : function(a)
				{
					return a.covName;
				},
				formatSelection : function(a)
				{
					return a.covName;
				},
				initSelection: function (element, callback) {
					var code  = $('#risk-cov-code').val();
					var name = $("#cover-name").val();
					var data = {covName:name,covId:code};
					callback(data);
				},
				id: "covName",
				width:"250px",
				params: {bindCode: $("#risk-bind-code").val(),subCode: $("#risk-sub-code").val()},
				placeholder:"Select Cover Type"

			});
		}
	};

	var getPremiumRates = function(detId){
		console.log('bind age applicable ',$("#pol-bind-age-appli").val());
		if($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val()==="Y"){
			$.ajax({
				type: 'GET',
				url: 'uw/policies/getBinderClientPremRates',
				dataType: 'json',
				data: {"detId": detId,"age":$("#lifeassured-age").val()},
				async: true,
				success: function (result) {
					$("#section_form_tbl tbody").each(function () {
						$(this).remove();
					});
					for (var res in result) {
						var markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
							"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
							"</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
							"<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" ;
						if($("#pol-bin-type").val()==="B"){
							if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
								markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" value="' + $("#lifeassured-age").val() + '" readonly>' +
									"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>";
							}
							else if (result[res].rider && result[res].rider === "Y") {
								markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
									"</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" ;
							}else {
							markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
								"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
								"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
								"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" ;
							}
						}
						else {
							if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
								markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" value="' + $("#lifeassured-age").val() + '" readonly>' +
									"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" ;
							}
							else if (result[res].rider && result[res].rider === "Y") {
								markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
									"</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" ;
							}
						}
						$("#section_form_tbl").append(markup);
					}

					$("#section_form_tbl tr").find("input[type=text]").number(true, 2);
				},
				error: function (jqXHR, textStatus, errorThrown) {

				}
			});
		}
		else {
			$.ajax({
				type: 'GET',
				url: 'uw/policies/getBinderPremRates',
				dataType: 'json',
				data: {"detId": detId},
				async: true,
				success: function (result) {
					$("#section_form_tbl tbody").each(function () {
						$(this).remove();
					});
					for (var res in result) {
						var markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
							"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
							"</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
							"<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>";
						if($("#pol-bin-type").val()==="B"){
							markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
								"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
								"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
								"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" ;
						}
						else {
							if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
								markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control">' +
									"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" ;
							}
							else if (result[res].rider && result[res].rider === "Y") {
								markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
									"</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" ;
							}
						}
						$("#section_form_tbl").append(markup);
					}

					$("#section_form_tbl tr").find("input[type=text]").number(true, 2);
				},
				error: function (jqXHR, textStatus, errorThrown) {

				}
			});
		}
	};

	var populateBinderLov = function(){
		if($("#binder-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "binder-frm",
				sort : 'binName',
				change: function(e, a, v){
					populateImportSubCovers(e.added.binId);
					$("#binder-id").val(e.added.binId);
					$("#risk-binder-code").val(e.added.binId);
					$("#risk-bind-code").val(e.added.binId);
					$("#pol-ins-comp").text(e.added.account.name);
					$("#pol-prod-name").text(e.added.product.proDesc);
					$("#product-id").val(e.added.product.proCode);
					$("#pol-agent-id").val(e.added.account.acctId);
					$("#client-pol-no").val(e.added.binPolNo);
					$("#risk-binder").text(e.added.binName);
					$("#pol-bind-age-appli").val(e.added.product.ageApplicable);
					$("#pol-buss-type").val("L");
					populateSubclassLov();
					getPolicyTerms(e.added.binId,e.added.product.proDesc);
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
					var code  = $('#binder-id').val();
					var name = $("#bind-name").val();
					var data = {binName:name,binId:code};
					callback(data);
				},
				id: "binId",
				width:"250px",
				params: {bindType: $("#pol-bin-type").val()},
				placeholder:"Select Contract"

			});
		}
	};

	var populatePaymentModes = function(){
		if($("#pm-mode-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "pm-mode-frm",
				sort : 'pmDesc',
				change: function(e, a, v){
					$("#pm-id").val(e.added.pmId);
				},
				formatResult : function(a)
				{
					return a.pmDesc;
				},
				formatSelection : function(a)
				{
					return a.pmDesc;
				},
				initSelection: function (element, callback) {
					var code  = $('#pm-id').val();
					var name = $("#pm-name").val();
					var data = {pmDesc:name,pmId:code};
					callback(data);
				},
				id: "pmId",
				width:"250px",
				placeholder:"Select Payment Mode"

			});
		}
	};

	var populateUserBranches = function(){
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
					var code  = $('#brn-id').val();
					var name = $("#brn-name").val();
					var data = {obName:name,obId:code};
					callback(data);
				},
				id: "obId",
				width:"250px",
				placeholder:"Select Branch"

			});
		}
	};


	var authorizePolicy = function(){
		var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			type: 'GET',
			url:  'uw/policies/authorizeLifePolicy',
			dataType: 'json',
			async: true,
			success: function(result) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Success',
						text: 'Transaction Authorized Successfully',
						icon: 'success'
					 });
				populatePolicyDetails();
			},
			error: function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Error',
						text: jqXHR.responseText,
						icon: 'error'
					});;
			}
		});
	};

	var dispatchDocuments = function(){
		var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			type: 'GET',
			url:  'uw/policies/dispatchDocs',
			dataType: 'json',
			async: true,
			success: function(result) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Success',
						text: 'Document Dispatched Successfully',
						icon: 'success'
					 });
				populatePolicyDetails();
				$("#btn-dispatch-trans").hide();
			},
			error: function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Error',
						text: jqXHR.responseText,
						icon: 'error'
					});;
			}
		});
	};

	var convertToPolicy = function(){
		var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			type: 'POST',
			url:   SERVLET_CONTEXT+'/protected/life/policies/proposalConversion',
			dataType: 'json',
			async: true,
			success: function(result) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Success',
						text: 'Make Ready Process Successfully',
						icon: 'success'
					 });
				populatePolicyDetails();
				$('#polChecksList').DataTable().ajax.reload();
			},
			error: function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Error',
						text: jqXHR.responseText,
						icon: 'error'
					});;
			}
		});
	};

	var makeReady = function(){
		var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			type: 'POST',
			url:  SERVLET_CONTEXT+'/protected/life/policies/createLifePolMakeReady',
			dataType: 'json',
			async: true,
			success: function(result) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Success',
						text: 'Make Ready Process Successfully',
						icon: 'success'
					 });
				populatePolicyDetails();
				$('#polChecksList').DataTable().ajax.reload();
			},
			error: function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Error',
						text: jqXHR.responseText,
						icon: 'error'
					});;
			}
		});
	};

	var undoMakeReady = function(){
		var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			type: 'GET',
			url:  'uw/policies/undoMakeReady',
			dataType: 'json',
			async: true,
			success: function(result) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Success',
						text: 'Make Ready Process Successfully',
						icon: 'success'
					 });
				populatePolicyDetails();
				$('#polChecksList').DataTable().ajax.reload();
			},
			error: function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Error',
						text: jqXHR.responseText,
						icon: 'error'
					});;
			}
		});
	};

	var createRisk = function(){
		var arr = getRskSections();
		if(arr.length==0){
			bootbox.alert("Cannot create Risk without sections")
			return false;
		}
		arr.shift();
		var $currForm = $('#risk-form');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}

		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "uw/policies/createRisk";
		data.sections = arr;
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});

		$.ajax(
			{
				url:url,
				type: "POST",
				data: JSON.stringify(data),
				success: function(s){
					// $('#myPleaseWait').modal('hide');
					Swal.fire({
						title: 'Success',
						text: 'Risk Transaction created Successfully',
						icon: 'success'
					 });
					$('#insured-frm').select2('val', null);
					populateInsuredLov();
					$('#subclass-frm').select2('val', null);
					populateSubclassLov();
					$('#covertypes-frm').select2('val', null);
					populateCoverTypesLov();
					populatePolicyDetails();
					$("#sect-div").show();
				},
				error: function(jqXHR, textStatus, errorThrown){
					// $('#myPleaseWait').modal('hide');
					Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
				},
				dataType: "json",
				contentType: "application/json"
			} );
	};

	var updateRisk = function(){
		var $currForm = $('#risk-form');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}

		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "uw/policies/createRisk";
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});

		$.ajax(
			{
				url:url,
				type: "POST",
				data: JSON.stringify(data),
				success: function(s){
					// $('#myPleaseWait').modal('hide');
					Swal.fire({
						title: 'Success',
						text: 'Policy Transaction created Successfully',
						icon: 'success'
					 });
					$('#insured-frm').select2('val', null);
					populateInsuredLov();
					$('#subclass-frm').select2('val', null);
					populateSubclassLov();
					$('#covertypes-frm').select2('val', null);
					populateCoverTypesLov();
					populatePolicyDetails();
					$("#sect-div").show();
				},
				error: function(jqXHR, textStatus, errorThrown){
					// $('#myPleaseWait').modal('hide');
					Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
				},
				dataType: "json",
				contentType: "application/json"
			} );
	};

	var updateMakeReadyPolicy = function(){
		var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
		var $currForm = $('#policy-form');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}

		$('#policy-form input[type=checkbox]').each(function(e){
			$(this).val($(this).is(':checked'));
		});

		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "uw/policies/createPolicyMakeReady";

		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			url:url,
			type: "POST",
			data: JSON.stringify(data),
			success: function(s){
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Success',
						text: 'Policy Transaction created Successfully',
						icon: 'success'
					 })
				$('#insured-frm').select2('val', null);
				populateInsuredLov();
				$('#subclass-frm').select2('val', null);
				populateSubclassLov();
				$('#covertypes-frm').select2('val', null);
				populateCoverTypesLov();
				polCode = s.policyId;
				populatePolicyDetails();
			},
			error: function(jqXHR, textStatus, errorThrown){
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Error',
						text: jqXHR.responseText,
						icon: 'error'
					});;
			},
			dataType: "json",
			contentType: "application/json"
		} );
	};

	var updatePolicy = function(){
		var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
		var $currForm = $('#policy-form');
		console.log($currForm)
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}

		$('#policy-form input[type=checkbox]').each(function(e){
			$(this).val($(this).is(':checked'));
		});

		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		console.log(data)
		var url = "uw/policies/createLifePolicy";

		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			url:url,
			type: "POST",
			data: JSON.stringify(data),
			success: function(s){
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Success',
						text: 'Policy Transaction created Successfully',
						icon: 'success'
					 })
//					 .then(() => {
//                           // Navigate to the new page only after the success alert
//                           window.location.href = SERVLET_CONTEXT + "/protected/uw/policies/policyEnquiry";
//                     });
				$('#insured-frm').select2('val', null);
				populateInsuredLov();
				$('#subclass-frm').select2('val', null);
				populateSubclassLov();
				$('#covertypes-frm').select2('val', null);
				populateCoverTypesLov();
				polCode = s.policyId;
				populatePolicyDetails();
			},
			error: function(jqXHR, textStatus, errorThrown){
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
						title: 'Error',
						text: jqXHR.responseText,
						icon: 'error'
					});;
			},
			dataType: "json",
			contentType: "application/json"
		} );
	};

	var createPolicy = function(){
		var $currForm = $('#policy-form');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}

		$('#policy-form input[type=checkbox]').each(function(e){
			$(this).val($(this).is(':checked'));
		});

		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		console.log("INVESTMENT: " + data.investment);
		var url = "uw/policies/createLifePolicy";
		if(!$('#chk-import-risks').is(':checked')) {
			var riskForm = $("#risk-form");
			var riskValidator = riskForm.validate();
			if (!riskForm.valid()) {
				return;
			}
			var arr = getRskSections();
			if(arr.length==0){
				bootbox.alert("Cannot create policy without sections")
				return false;
			}
			arr.shift();
			data.sections = arr;
			data.riskBean = getRiskDetails();
		}
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax(
			{
				url:url,
				type: "POST",
				data: JSON.stringify(data),
				success: function(s){
					// $('#myPleaseWait').modal('hide');
					$(".risk-detail-tab").show();
					Swal.fire({
						title: 'Success',
						text: 'Policy Transaction created Successfully',
						icon: 'success'
					 });
//					 .then(() => {
//                           // Navigate to the new page only after the success alert
//                           window.location.href = SERVLET_CONTEXT + "/protected/uw/policies/policyEnquiry";
//                     });
					$('#lifeassured-frm').select2('val', null);
					populateInsuredLov();
					$('#subclass-frm').select2('val', null);
					populateSubclassLov();
					$('#covertypes-frm').select2('val', null);
					populateCoverTypesLov();
					polCode = s.policyId;
					populatePolicyDetails();
				},
				error: function(jqXHR, textStatus, errorThrown){
					// $('#myPleaseWait').modal('hide');
					Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
				},
				dataType: "json",
				contentType: "application/json"
			} );
	};

	var getRiskDetails = function(){
		var $currForm = $('#risk-form');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}
		$('#risk-form input[type=checkbox]').each(function(e){
			$(this).val($(this).is(':checked'));
		});
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		return data;
	};

	var getRskSections = function(){
		var arr = [];
		$("#section_form_tbl tr").each(function(row,tr){
			var section   = $(this).find('.section').eq(0).val();
			var ratesApplicable   = $(this).find('.ratesApplicable').eq(0).val();
			var rate   = $(this).find('.rate').eq(0).val();
			var divFactor   = $(this).find('.divFactor').eq(0).val();
			var freeLimit   = $(this).find('.freeLimit').eq(0).val();
			var amount = $(this).find('.amount').eq(0).val();
			var multiplierRate= $(this).find('.multiplierRate').eq(0).val();
			arr.push({
				section: section,
				ratesApplicable: ratesApplicable,
				rate:rate,
				divFactor:divFactor,
				freeLimit:freeLimit,
				amount: amount,
				multiplierRate:multiplierRate
			});
		});
		return arr;
	};

	var getRiskCertificates = function(){
		var url = "uw/policies/riskCerts/"+selRiskCode;
		var currTable = $('#cert_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "cert",
					"render": function ( data, type, full, meta ) {
						return full.cert.certLots.certTypes.certDesc;
					}
				},
				{ "data": "status" ,
					"render": function ( data, type, full, meta ) {
						if(full.status){
							if(full.status==="A"){
								return "Active";
							}
							else if(full.status==="C"){
								return "Cancelled";
							}
							else if(full.status==="I"){
								return "Inactive";
							}
						}else
							return full.status;
					}
				},
				{ "data": "printStatus",
					"render": function ( data, type, full, meta ) {
						if(full.printStatus){
							if(full.printStatus==="P"){
								return "Printed";
							}
							else if(full.printStatus==="R"){
								return "Ready";
							}
						}else
							return full.printStatus;
					}
				},
				{ "data": "certWef" ,
					"render": function ( data, type, full, meta ) {
						return moment(full.certWef).format('DD/MM/YYYY');
					}
				},
				{ "data": "certWet" ,
					"render": function ( data, type, full, meta ) {
						return moment(full.certWet).format('DD/MM/YYYY');
					}
				},
				{ "data": "certNo" },
				{ "data": "reasonCancelled" },

			]
		}));
		return currTable;
	};

	var getRiskSections = function(){
		var url = "uw/policies/risksSections/"+selRiskCode;
		var currTable = $('#section_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "section",
					"render": function ( data, type, full, meta ) {

						return full.section.desc;
					}
				},
				{ "data": "rate" },
				{ "data": "calcprem"  ,
					"render": function ( data, type, full, meta ) {
						return UTILITIES.currencyFormat(full.calcprem);
					}
				},
				{ "data": "prem"  ,
					"render": function ( data, type, full, meta ) {

						return UTILITIES.currencyFormat(full.prem);
					}
				},

			]
		}) );
		return currTable;
	};

	var getCommissionAllocs = function(receiptId){
		var url =SERVLET_CONTEXT+ "/protected/life/policies/allocationCommission/"+receiptId;
		var currTable = $('#receiptalloc_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "allocCommId",
					"render": function ( data, type, full, meta ) {

						return full.installNo;
					}
				},
				{ "data": "allocCommId",
					"render": function ( data, type, full, meta ) {

						return UTILITIES.currencyFormat(full.instalmentPremium);
					}
				},
				{ "data": "allocCommId",
					"render": function ( data, type, full, meta ) {

						return UTILITIES.currencyFormat(full.commissionAmt);
					}
				},
				{
					"data": "allocCommId",
					"render": function (data, type, full, meta) {

						return full.paidToDate;

					}
				},
			]
		}) );
		return currTable;
	};

	var getPolicybeneficiaries  = function(){
		var url = "uw/policies/policyBeneficiary/"+polCode;
		var currTable = $('#benefeciary_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "beneficiaryName",
					"render": function ( data, type, full, meta ) {

						return full.beneficiaryName;
					}
				},
				{ "data": "relationshipType"  ,
					"render": function ( data, type, full, meta ) {
						return full.relationshipType.relationDesc;
					}
				},
				{ "data": "benAllocation"  ,
					"render": function ( data, type, full, meta ) {
						return full.benAllocation;
					}
				},
				{ "data": "beneficiaryregNo"  ,
					"render": function ( data, type, full, meta ) {

						return full.beneficiaryregNo;
					}
				},

			]
		}) );
		return currTable;
	};


	var getPolicybenefits  = function(){
		var url = "uw/policies/policyBenefits/"+polCode;
		var currTable = $('#benefit_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "maturityYear"
				},
				{ "data": "estBenefit"  ,
					"render": function ( data, type, full, meta ) {
						return UTILITIES.currencyFormat(full.estBenefit);
					}
				}
			]
		}) );
		return currTable;
	};

	var getPolicyInstallments  = function(){
		var url = SERVLET_CONTEXT+"/protected/life/policies/policyInstallments";
		var currTable = $('#installments_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "installmentNo"  ,
					"render": function ( data, type, full, meta ) {
						return full.installmentNo;
					}
				},
				{ "data": "installPrem"  ,
					"render": function ( data, type, full, meta ) {
						return UTILITIES.currencyFormat(full.installPrem);
					}
				},
				{ "data": "dueDate" ,
					"render": function ( data, type, full, meta ) {

						return moment(full.dueDate).format('DD/MM/YYYY');
					}
				},
				{ "data": "installPaid"  ,
					"render": function ( data, type, full, meta ) {
					   if(full.installPaid && full.installPaid==='Y')
						   return 'Yes';
					   else
						   return 'No';
					}
				},
				{ "data": "paidDate" ,
					"render": function ( data, type, full, meta ) {
					if(full.paidDate)
						return moment(full.paidDate).format('DD/MM/YYYY');
					else return '';
					}
				},
			]
		}) );
		return currTable;
	};

	var getRiskDocs = function(){
		var url = "uw/policies/riskDocs/"+selRiskCode;
		var currTable = $('#risk_docs_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "rdId",
					"render": function ( data, type, full, meta ) {

						return full.polRevNo;
					}
				},
				{ "data": "rdId",
					"render": function ( data, type, full, meta ) {

						return full.docShtDesc;
					}
				},
				{ "data": "rdId",
					"render": function ( data, type, full, meta ) {

						return full.docDesc;
					}
				},
				{ "data": "uploadedFileName" },
				{ "data": "checkSum" },

			]
		}));
		return currTable;
	};

	var getUWPolicyReceipts = function(policyCode) {
		var url = SERVLET_CONTEXT + "/protected/life/policies/getpolicyReceipts/" + policyCode;
		var currTable = $('#receipts_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl": url,
			"columns": [
				{
					"data": "receiptNo",
					"render": function (data, type, full, meta) {

						return (full.receiptNo);
					}
				},
				{
					"data": "receiptDate",
					"render": function (data, type, full, meta) {

						return moment(full.receiptDate).format('DD/MM/YYYY');
					}
				},
				{
					"data": "dc",
					"render": function (data, type, full, meta) {

						return full.dc;;
					}
				},
				{
					"data": "receiptAmount",
					"render": function (data, type, full, meta) {
						return UTILITIES.currencyFormat(full.receiptAmount);
					}
				},
				{
					"data": "allocationAmount",
					"render": function (data, type, full, meta) {
						if(full.allocationAmount){
							return UTILITIES.currencyFormat(full.allocationAmount);
						}
					}

				},
				{
					"data": "balance",
					"render": function (data, type, full, meta) {
						if(full.balance){
							return UTILITIES.currencyFormat(full.balance);
						}
					}

				},
			]
		}));

		$('#receipts_tbl tbody').on( 'click', 'tr', function () {
			$(this).addClass('table-primary').siblings().removeClass('table-primary');
			var aData = currTable.rows('.table-primary').data();
			if (aData[0] === undefined || aData[0] === null){

			}
			else{
				console.log(aData[0]);
				getCommissionAllocs(aData[0].lifeRctId);
			}
		} );
	}

	var getUWPolicyRisks = function(policyCode){
		console.log('policyCode ',policyCode);
		var url = SERVLET_CONTEXT+"/protected/life/policies/policyRisks/"+policyCode;
		var currTable = $('#risk_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "insured",
					"render": function ( data, type, full, meta ) {

						return (full.fname+" "+full.othernames);
					}
				},
				{ "data": "subclass",
					"render": function ( data, type, full, meta ) {

						return full.subDesc;
					}
				},
				{ "data": "covertype",
					"render": function ( data, type, full, meta ) {

						return full.covName;
					}
				},
				{ "data": "workingAge",
					"render": function ( data, type, full, meta ) {

							return  full.age;
				    }
				},

			]
		}) );

		$('#risk_tbl tbody').on( 'click', 'tr', function () {
			$(this).addClass('table-primary').siblings().removeClass('table-primary');
			var aData = currTable.rows('.table-primary').data();
			if (aData[0] === undefined || aData[0] === null){

			}
			else{
				console.log(aData[0]);
				selRiskCode = aData[0].riskId;
				$("#risk-code-pk").val(selRiskCode);
				getRiskSections();
				//getRiskIntParties();
				getRiskDocs();
				getRiskCertificates();
				//getRiskSchedules();
				getClientDocs(aData[0].tenId);
				$("#risk-det-id-pk").val(aData[0].binderDetId);
				populateRiskSections(aData[0].binderDetId);
				$("#cert-from-date").val(moment(aData[0].wefDate).format('DD/MM/YYYY'));
				$("#cert-wet-date").val(moment(aData[0].wetDate).format('DD/MM/YYYY'));
				console.log("binder="+$("#binder-id").val())
				if($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val()==="Y"){
					$.ajax({
						type: 'GET',
						url:  'uw/policies/getLifeClientAge',
						dataType: 'json',
						data: {"clientId": aData[0].tenId,"binCode" :$("#binder-id").val()},
						async: true,
						success: function(result) {
							$("#lifeassured-age").val(result);
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
			}
		} );
		return currTable;
	};

	var deleteRiskCerts = function(){
		var certs = JSON.parse(decodeURI($(button).data("certs")));
		bootbox.confirm("Are you sure want to delete "+certs['cert'].certLots.certTypes.certDesc+"?", function(result) {
			if(result){
				// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
				$.ajax({
					type: 'GET',
					url:  'uw/policies/deleteRiskCert/' + certs['pcId'],
					dataType: 'json',
					async: true,
					success: function(result) {
						// $('#myPleaseWait').modal('hide');
						Swal.fire({
                    title: 'Success',
                    text: 'Record Deleted Successfully',
                    icon: 'success'
                });
						$('#cert_tbl').DataTable().ajax.reload();

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
	};
	var editRiskCerts = function(button){
			var certs = JSON.parse(decodeURI($(button).data("certs")));
			$("#cert-pc-id").val(certs['pcId']);
			$("#risk-certtype-name").text(certs['cert'].certLots.certTypes.certDesc);
			$("#risk-cert-status").val(certs['status']);
			$("#risk-cert-from-date").val(moment(certs['certWef']).format('DD/MM/YYYY'));
			$("#risk-cert-wet-date").val(moment(certs['certWet']).format('DD/MM/YYYY'));
			$("#risk-canc-date").val(moment(certs['certWet']).format('DD/MM/YYYY'));
			if(certs['status']){
				if(certs['status']==="A"){
					$(".cert-cancellation").hide();
				}
				else if(certs['status']==="C"){
					$(".cert-cancellation").show();
				}
				else{
					$(".cert-cancellation").hide();
				}

			}
			$('#editRiskCertModal').modal({
				backdrop: 'static',
				keyboard: true
			});
	};

	var downloadRiskDoc = function(button){
		var docs = JSON.parse(decodeURI($(button).data("docs")));
		window.open(SERVLET_CONTEXT+"/protected/uw/policies/riskdocument/"+docs['rdId'],
			'_blank' // <- This is what makes it open in a new window.
		);
	}


	var deleteRiskDoc= function(button){
		var docs = JSON.parse(decodeURI($(button).data("docs")));
		bootbox.confirm("Are you sure want to delete "+docs['reqdDocs'].requiredDoc.reqDesc+"?", function(result) {
			if(result){
				// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
				$.ajax({
					type: 'GET',
					url:  'uw/policies/deleteRiskDoc/' + docs['rdId'],
					dataType: 'json',
					async: true,
					success: function(result) {
						// $('#myPleaseWait').modal('hide');
						Swal.fire({
                    title: 'Success',
                    text: 'Record Deleted Successfully',
                    icon: 'success'
                });
						$('#risk_docs_tbl').DataTable().ajax.reload();
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

	var editRiskDocs= function(button){
		var docs = JSON.parse(decodeURI($(button).data("docs")));
		$("#risk-doc-name").text(docs["reqdDocs"].requiredDoc.reqDesc);
		$("#risk-upload-name").text(docs['uploadedFileName']);
		$("#risk-doc-id").val(docs['rdId']);
		$('#riskdocModal').modal({
			backdrop: 'static',
			keyboard: true
		});
	}

	var uploadRiskDocument= function(){
		var $form = $("#risk-doc-form");
		var validator = $form.validate();
		$('form#risk-doc-form')
			.submit( function( e ) {
				e.preventDefault();

				if (!$form.valid()) {
					return;
				}
				// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
				var data = new FormData( this );
				data.append( 'file', $( '#avatar' )[0].files[0] );
				$.ajax( {
					url: 'uw/policies/uploadRequiredDocs',
					type: 'POST',
					data: data,
					processData: false,
					contentType: false,
					success: function (s ) {
						// $('#myPleaseWait').modal('hide');
						Swal.fire({
						title: 'Success',
						text: 'File Uploaded Successfully',
						icon: 'success'
					    });
						$('#riskdocModal').modal('hide');
						var $el = $('#avatar');
						$el.wrap('<form>').closest('form').get(0).reset();
						$el.unwrap();
						$('#risk_docs_tbl').DataTable().ajax.reload();

					},
					error: function(xhr, error){
						// $('#myPleaseWait').modal('hide');
						Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
					}
				});
			});
	}

	var editRiskSection= function(button){
		var section = JSON.parse(decodeURI($(button).data("risksections")));
		console.log(section);
		$("#sect-code-pk").val(section['sectId']);
		$("#sect-limit-amt").val(section['amount']);
		$("#sect-rate").val(section['rate']);
		$("#sect-free-limit").val(section['freeLimit']);
		$("#sect-div-fact").val(section['divFactor']);
		$("#sect-multi-rate").val(section['multiRate']);
		//$("#chk-compute").prop("checked", section["compute"]);
		$("#risk-sect-id").val(section['section'].id);
		$("#risk-sect-name").val(section['section'].desc);
		$("#sect-prem-id-pk").val(section['premRates'].id);
		if(section['risk']){
			$("#risk-sect-code-pk").val(section['risk'].riskId);
		}
		else{
			$("#risk-sect-code-pk").val(section['riskId']);
		}
		populateRiskSections($("#risk-det-id-pk").val());
		$('#sect-limit-amt,#sect-free-limit').number( true, 2 );
		$("#risk-sect-frm").select2("readonly", true);
		if($("#pol-bin-type").val()==="B"){
			$("#sect-rate").prop("readonly", true);
			$("#sect-free-limit").prop("readonly", true);
			$("#sect-div-fact").attr("style", "pointer-events: none;");
		}
		else{
			$("#sect-rate").prop("readonly", false);
			$("#sect-free-limit").prop("readonly", false);
			//$("#sect-div-fact").prop('disabled', false);
		}

		$('#sectModal').modal({
			backdrop: 'static',
			keyboard: true
		});
	}

	var deleteRiskSection= function(button){
		var section = JSON.parse(decodeURI($(button).data("risksections")));
		bootbox.confirm("Are you sure want to delete "+section['section'].desc+"?", function(result) {
			if(result){
				// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
				$.ajax({
					type: 'GET',
					url:  'uw/policies/deleteRiskSection/' + section['sectId'],
					dataType: 'json',
					async: true,
					success: function(result) {
						// $('#myPleaseWait').modal('hide');
						Swal.fire({
                    title: 'Success',
                    text: 'Record Deleted Successfully',
                    icon: 'success'
                });
						$('#section_tbl').DataTable().ajax.reload();
						populatePolicyDetails();
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


	var populateRiskSections= function(detCode){
		if($("#risk-sect-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "risk-sect-frm",
				sort : 'desc',
				change: function(e, a, v){
					//$("#pm-id").val(e.added.pmId);
				},
				formatResult : function(a)
				{
					return a.desc;
				},
				formatSelection : function(a)
				{
					return a.desc;
				},
				initSelection: function (element, callback) {
					var code  = $('#risk-sect-id').val();
					var name = $("#risk-sect-name").val();
					var data = {desc:name,id:code};
					callback(data);
				},
				id: "id",
				width:"250px",
				params: {detId: detCode},
				placeholder:"Select Section"

			});
		}
	}


	var createPolicyTaxes= function(){
		var url = "uw/policies/policyTaxes";
		var currTable = $('#polTaxesList').DataTable( UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "revenueItems",
					"render": function ( data, type, full, meta ) {
						return UTILITIES.getRevDesc(full.revenueItems.item);
					}
				},
				{ "data": "taxRate" },
				{ "data": "divFactor" },
				{ "data": "rateType" },
				{ "data": "taxAmount",
					"render": function ( data, type, full, meta ) {

						return UTILITIES.currencyFormat(full.taxAmount);
					}
				},
				{ "data": "taxLevel",
					"render": function ( data, type, full, meta ) {
						if(full.taxLevel){
							if(full.taxLevel==="R")
								return "Risk";
							else if(full.taxLevel==="P")
								return "Policy";
						}
						else
							return "Policy";

					}
				},
			]
		}));
		return currTable;
	}



	function getNewPremItems(sectdesc){
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		if($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val()==="Y"){
			$.ajax({
				type: 'GET',
				url: 'uw/policies/getNewClientPremiumItems',
				dataType: 'json',
				data: {"detId": $("#risk-det-id-pk").val(), "riskId": $("#risk-code-pk").val(), "secName": sectdesc,"age":$("#lifeassured-age").val()},
				async: true,
				success: function (result) {
					// $('#myPleaseWait').modal('hide');
					$("#new_prem_items_form tbody").each(function () {
						$(this).remove();
					});
					for (var res in result) {
						var markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
							"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
							"</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
							"<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td></tr>";
						if($("#pol-bin-type").val()==="B"){
							markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
								"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control">' +
								"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
								"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
						}
						else {
							if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
								markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" value="' + $("#lifeassured-age").val() + '" readonly>' +
									"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
							}
							else if (result[res].section && result[res].section.type === "RD") {
								markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
									"</td><td><input type='text' class='rate form-control'  value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'   value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control'   value='" + result[res].freeLimit + "'></td></tr>";
							}
						}
						$("#new_prem_items_form").append(markup);
					}

					$("#new_prem_items_form tr").find("input[type=text]").number(true, 2);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					// $('#myPleaseWait').modal('hide');
				}
			});
		}
		else {
			$.ajax({
				type: 'GET',
				url: 'uw/policies/getNewPremiumItems',
				dataType: 'json',
				data: {"detId": $("#risk-det-id-pk").val(), "riskId": $("#risk-code-pk").val(), "secName": sectdesc},
				async: true,
				success: function (result) {
					// $('#myPleaseWait').modal('hide');
					$("#new_prem_items_form tbody").each(function () {
						$(this).remove();
					});
					for (var res in result) {
						var markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
							"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
							"</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
							"<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td></tr>";
						if($("#pol-bin-type").val()==="B"){
							markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
								"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
								"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
								"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
						}
						else {
							if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
								markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
									"</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
							}
							else if (result[res].section && result[res].section.type === "RD") {
								markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
									"'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
									"</td><td><input type='text' class='rate form-control'  value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'   value='" + result[res].divFactor + "'></td></td><td>" +
									"<input type='text' class='freeLimit form-control'   value='" + result[res].freeLimit + "'></td></tr>";
							}
						}
						$("#new_prem_items_form").append(markup);
					}

					$("#new_prem_items_form tr").find("input[type=text]").number(true, 2);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					// $('#myPleaseWait').modal('hide');
				}
			});
		}

	}



	var getNewTaxes= function(){

		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			type: 'GET',
			url:  'uw/policies/getNewTaxes',
			dataType: 'json',
			async: true,
			success: function(result) {
				// $('#myPleaseWait').modal('hide');
				$("#new_taxes_tbl tbody").each(function(){
					$(this).remove();
				});
				for(var res in result){
					var markup = "<tr><td><input type='checkbox' class='tax-check'><input type='hidden' class='tax-id form-control' value='"+result[res].polTaxId+
						"'></td><td>"
						+ UTILITIES.getRevDesc(result[res].revenueItems.item) + "</td><td>"
						+result[res].taxRate +"</td><td>"
						+getRateType(result[res].rateType) +"</td></tr>";
					$("#new_taxes_tbl").append(markup);
				}

			},
			error: function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
			}
		});

	}

	var getNewClauses= function(){
		// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
		$.ajax({
			type: 'GET',
			url:  'uw/policies/getNewClauses',
			dataType: 'json',
			async: true,
			success: function(result) {
				// $('#myPleaseWait').modal('hide');
				$("#new_clause_tbl tbody").each(function(){
					$(this).remove();
				});
				for(var res in result){
					var markup = "<tr><td><input type='checkbox' class='clause-check'><input type='hidden' class='clause-id form-control' value='"+result[res].clause.clauId+
						"'></td><td>"
						+ result[res].clauHeading + "</td></tr>";
					$("#new_clause_tbl").append(markup);
				}

			},
			error: function(jqXHR, textStatus, errorThrown) {
				// $('#myPleaseWait').modal('hide');
			}
		});

	}

	var populateEndorsRisks= function(policyCode){
		if($("#endos-insured-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "endos-insured-frm",
				sort : 'arId',
				change: function(e, a, v){
					$("#endorse-insured-id").val(e.added.risk.insured.tenId);
				},
				formatResult : function(a)
				{
					return a.risk.insured.fname+" "+a.risk.insured.otherNames;
				},
				formatSelection : function(a)
				{
					return a.risk.insured.fname+" "+a.risk.insured.otherNames;
				},
				initSelection: function (element, callback) {

				},
				id: "arId",
				width:"250px",
				params: {polCode: policyCode},
				placeholder:"Select Insured"

			});
		}
	}


	var createActiveRisksTbl= function(policyCode){
		var url = "uw/policies/polactiverisks";
		var currTable = $('#endorserisktbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": {
				'url': url,
				'data':{
					'riskId': $("#endorse-risk-search").val(),
					'insuredId':  $("#endorse-insured-id").val(),
					'policyCode':  policyCode
				},
			},
			autoWidth: true,
			lengthMenu: [ [10], [10] ],
			pageLength: 10,
			destroy: true,
			searching: false,
			"columns": [
				{ "data": "arId",
					"render": function ( data, type, full, meta ) {
						return full.risk.riskShtDesc;
					}
				},
				{ "data": "arId",
					"render": function ( data, type, full, meta ) {
						return full.risk.riskDesc;
					}
				},
				{ "data": "arId" ,
					"render": function ( data, type, full, meta ) {
						return moment(full.risk.wefDate).format('DD/MM/YYYY');
					}
				},
				{ "data": "arId" ,
					"render": function ( data, type, full, meta ) {
						return moment(full.risk.wetDate).format('DD/MM/YYYY');
					}
				},
				{ "data": "arId" ,
					"render": function ( data, type, full, meta ) {
						return '<select class="form-control" id="bin-type" name="binType" required> '+
							' <option value="R">Revise</option> '+
							' <option value="C">Cancel</option>'+
							' <option value="E">Extend</option>'+
							' </select>';
					}
				},
				{ "data": "arId" ,
					"render": function ( data, type, full, meta ) {
						return '<button  class="editor_edit btn btn-success">Endorse</button>';
					}
				},

			]
		} );


		$('#endorserisktbl').on('click', '.editor_edit', function (e) {
			var combo = $(this).closest('tr').find("select");
			var data = currTable.row($(this).closest('tr')).data();
			if(combo.val()==="R"){
				if (data === undefined || data === null){

				}
				else{
					// $('#myPleaseWait').modal({

					$.ajax({
						type: 'GET',
						url:  'uw/policies/endorseRisk',
						data: {"activeRiskCode": data.arId},
						dataType: 'json',
						async: true,
						success: function(result) {
							// $('#myPleaseWait').modal('hide');
							$('#endorserisktbl').DataTable().ajax.reload();
							$('#risk_tbl').DataTable().ajax.reload();
							populatePolicyDetails();
						},
						error: function(jqXHR, textStatus, errorThrown) {
							// $('#myPleaseWait').modal('hide');

							bootbox.alert(jqXHR.responseText);
						}
					});
				}
			}
		} );



		return currTable;
	}

	var newPolicyRemarksModal= function(){
		$("#btn-add-new-remark").on('click', function(){
			createPolicyRemarksTbl(polCode);
			$('#endorseRemarksModal').modal({
				backdrop: 'static',
				keyboard: true
			});
		});

	}

	var createPolicyRemarksTbl= function(policyCode){
		var url = "uw/policies/getNewPolicyRemarks";
		var currTable = $('#remarks_tbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": {
				'url': url,
				'data':{
					'policyCode':  policyCode
				},
			},
			autoWidth: true,
			lengthMenu: [ [10], [10] ],
			pageLength: 10,
			destroy: true,
			searching: true,
			"columns": [
				{ "data": "remarkShtDesc",
					"render": function ( data, type, full, meta ) {
						return full.remarkShtDesc;
					}
				},
				{ "data": "remarks",
					"render": function ( data, type, full, meta ) {
						return full.remarks;
					}
				},


			]
		} );

		$('#remarks_tbl tbody').on( 'click', 'tr', function () {

			$(this).addClass('table-primary').siblings().removeClass('table-primary');

			var d = currTable.row( this ).data();
			if(d){
				$("#poli-remarks").val(d.remarks);
				$("#remark-pk").val(d.remarkId);
				$('#endorseRemarksModal').modal('hide');
				$("#remark-pol-id").val(policyCode);
			}


		} );


		return currTable;
	}


	var saveEndorsementRemakrs= function(){
		var $paymodesForm = $('#frm-pol-remarks');
		var validator = $paymodesForm.validate();

		$('#btn-save-remark').click(function(){
			if (!$paymodesForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paymodesForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "uw/policies/createPolicyRemarks";
			console.log(data);
			var request = $.post(url, data );

			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				getPolicyRemakrs();

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


	var getRiskSchedules= function(){
		$.ajax( {
			url: 'uw/policies/getRiskSchedules/'+selRiskCode,
			type: 'GET',
			processData: false,
			contentType: false,
			success: function (s ) {
				getRiskScheduleDetails(s.mappings);
			},
			error: function(xhr, error){
				bootbox.alert(xhr.responseText);
			}
		});
	}

	var addNewRiskSchedule= function(){
		$("#btn-add-new-sched").on('click', function(){
			$("#schedule-risk-id").val($("#risk-code-pk").val());
			$("#schedule-pk-id").val("");
			$.ajax( {
				url: 'uw/policies/getRiskSchedules/'+$("#risk-code-pk").val(),
				type: 'GET',
				processData: false,
				contentType: false,
				success: function (s ) {
					$("form#new-risk-schedule-form > div ").each(function(){
						$(this).remove();
					});
					for(var i=0;i< s.mappings.length;i++){
						if(s.mappings[i].colType==="T"){
							var data='<div class="form-group"> '+
								' <label for="cou-name" class="col-md-3 control-label">'+ s.mappings[i].column+'</label> '+
								'	<div class="col-md-8"> '+
								'	<input type="text" class="editUserCntrls form-control" name="column'+s.mappings[i].key+'" '+
								' required> '+
								' </div> '+
								' </div>	';
							$("form#new-risk-schedule-form").append(data);
						}
						else if(s.mappings[i].colType==="N"){
							var data='<div class="form-group"> '+
								' <label for="cou-name" class="col-md-3 control-label">'+ s.mappings[i].column+'</label> '+
								'	<div class="col-md-8"> '+
								'	<input type="number" class="editUserCntrls form-control" name="column'+s.mappings[i].key+'" '+
								' required> '+
								' </div> '+
								' </div>	';
							$("form#new-risk-schedule-form").append(data);
						}
						else if(s.mappings[i].colType==="D"){
							var data='<div class="form-group"> '+
								' <label for="cou-name" class="col-md-3 control-label">'+ s.mappings[i].column+'</label> '+
								'	<div class="col-md-8"> '+
								'<div class="input-group date datepicker-input"> '+
								' <input type="text" name="column'+s.mappings[i].key+'" class="form-control float-right"'+
								' required /> '+
								'  <div class="input-group-addon"> '+
								'      <span class="fa fa-calendar"></span> '+
								'     </div> '+
								'    </div>'+
								' </div> '+
								' </div>	';
							$("form#new-risk-schedule-form").append(data);
						}
						else if(s.mappings[i].colType==="O"){
							var arr = s.mappings[i].options.split(",");
							var opts="";
							for(var x=0;x<arr.length;x++){
								opts+=' <option value="'+arr[x]+'">'+arr[x]+'</option> ';
							}
							var data='<div class="form-group"> '+
								' <label for="cou-name" class="col-md-3 control-label">'+ s.mappings[i].column+'</label> '+
								'	<div class="col-md-8"> '+
								' <select class="form-control" name="column' + s.mappings[i].key + '" '+
								' required> '+
								' <option value="">Select Option Value</option> '+opts+
								' </select> '+
								' </div> '+
								' </div>	';
							$("form#new-risk-schedule-form").append(data);
						}

					}
					$(".datepicker-input").each(function() {
						$(this).datetimepicker({
							format: 'DD/MM/YYYY'
						});

					});
				},
				error: function(xhr, error){
					bootbox.alert(xhr.responseText);
				}
			});
			$('#riskscheduleModal').modal({
				backdrop: 'static',
				keyboard: true
			});
		})
	}


	var getRiskScheduleDetails= function(cols){
		var arr = [];
		for(var i=0;i<cols.length;i++){
			arr.push({
				title: cols[i].column,
				data:"column"+(cols[i].key)
			});
		}
		var url = "uw/policies/riskSchedules/"+selRiskCode;
		var currTable = $('#risk-sched_tbl').DataTable(UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": arr
		}));

		$('#risk-sched_tbl tbody').on( 'click', 'tr', function () {
			currTable.$('tr.active').removeClass('active');
			$(this).addClass('active');
		} );
		$("#btn-add-del-sched").on('click', function(){
			var d = currTable.row('.active').data();
			if(d){
				bootbox.confirm("Are you sure want to delete selected Schedule Record?", function(result) {
					if(result){
						$.ajax({
							type: 'GET',
							url:  'deleteRiskSchedule/' + d.scheduleId,
							dataType: 'json',
							async: true,
							success: function(result) {
								Swal.fire({
						title: 'Success',
						text: 'Record Deleted Successfully',
						icon: 'success'
					 });
								$('#risk-sched_tbl').DataTable().ajax.reload();

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
		});

		$("#btn-add-edit-sched").on('click', function(){
			var d = currTable.row('.active').data();
			if(d) {
				$("#schedule-risk-id").val($("#risk-code-pk").val());
				$("#schedule-pk-id").val(d.scheduleId);
				$.ajax({
					url: 'uw/policies/getRiskSchedules/' + $("#risk-code-pk").val(),
					type: 'GET',
					processData: false,
					contentType: false,
					success: function (s) {
						$("form#new-risk-schedule-form > div ").each(function () {
							$(this).remove();
						});
						for (var i = 0; i < s.mappings.length; i++) {
							var value;
							if(s.mappings[i].key==='1') {
								value = d.column1;
							}
							else if(s.mappings[i].key==='2') {
								value = d.column2;
							}
							else if(s.mappings[i].key==='3') {
								value = d.column3;
							}
							else if(s.mappings[i].key==='4') {
								value = d.column4;
							}
							else if(s.mappings[i].key==='5') {
								value = d.column5;
							}
							else if(s.mappings[i].key==='6') {
								value = d.column6;
							}
							else if(s.mappings[i].key==='7') {
								value = d.column7;
							}
							else if(s.mappings[i].key==='8') {
								value = d.column8;
							}
							else if(s.mappings[i].key==='9') {
								value = d.column9;
							}
							else if(s.mappings[i].key==='10') {
								value = d.column10;
							}
							else if(s.mappings[i].key==='11') {
								value = d.column11;
							}
							else if(s.mappings[i].key==='12') {
								value = d.column12;
							}
							else if(s.mappings[i].key==='13') {
								value = d.column13;
							}
							else if(s.mappings[i].key==='14') {
								value = d.column14;
							}
							else if(s.mappings[i].key==='15') {
								value = d.column15;
							}
							else if(s.mappings[i].key==='16') {
								value = d.column16;
							}
							else if(s.mappings[i].key==='17') {
								value = d.column17;
							}
							else if(s.mappings[i].key==='18') {
								value = d.column18;
							}else if(s.mappings[i].key==='19') {
								value = d.column19;
							}
							else if(s.mappings[i].key==='20') {
								value = d.column20;
							}
							else if(s.mappings[i].key==='21') {
								value = d.column21;
							}
							else if(s.mappings[i].key==='22') {
								value = d.column22;
							}
							else if(s.mappings[i].key==='23') {
								value = d.column23;
							}
							else if(s.mappings[i].key==='24') {
								value = d.column24;
							}
							else if(s.mappings[i].key==='25') {
								value = d.column25;
							}
							else if(s.mappings[i].key==='26') {
								value = d.column26;
							}
							else if(s.mappings[i].key==='27') {
								value = d.column27;
							}
							else if(s.mappings[i].key==='28') {
								value = d.column28;
							}
							else if(s.mappings[i].key==='29') {
								value = d.column29;
							}
							else if(s.mappings[i].key==='30') {
								value = d.column30;
							}
							if (s.mappings[i].colType === "T") {
								var data = '<div class="form-group"> ' +
									' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
									'	<div class="col-md-8"> ' +
									'	<input type="text" class="editUserCntrls form-control" value="'+value+'" name="column' + s.mappings[i].key + '" ' +
									' required> ' +
									' </div> ' +
									' </div>	';
								$("form#new-risk-schedule-form").append(data);
							}
							else if (s.mappings[i].colType === "N") {
								var data = '<div class="form-group"> ' +
									' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
									'	<div class="col-md-8"> ' +
									'	<input type="number" class="editUserCntrls form-control" value="'+value+'" name="column' + s.mappings[i].key + '" ' +
									' required> ' +
									' </div> ' +
									' </div>	';
								$("form#new-risk-schedule-form").append(data);
							}
							else if (s.mappings[i].colType === "D") {
								var data = '<div class="form-group"> ' +
									' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
									'	<div class="col-md-8"> ' +
									'<div class="input-group date datepicker-input"> ' +
									' <input type="text" value="'+value+'" name="column' + s.mappings[i].key + '" class="form-control float-right"' +
									' required /> ' +
									'  <div class="input-group-addon"> ' +
									'      <span class="fa fa-calendar"></span> ' +
									'     </div> ' +
									'    </div>' +
									' </div> ' +
									' </div>	';
								$("form#new-risk-schedule-form").append(data);
							}
							else if(s.mappings[i].colType==="O"){
								console.log(s.mappings[i].options);
								var arr = s.mappings[i].options.split(",");
								var opts="";
								for(var x=0;x<arr.length;x++){
									if(arr[x]===value)
										opts+=' <option value="'+arr[x]+'" selected>'+arr[x]+'</option> ';
									else opts+=' <option value="'+arr[x]+'">'+arr[x]+'</option> ';
								}
								var data='<div class="form-group"> '+
									' <label for="cou-name" class="col-md-3 control-label">'+ s.mappings[i].column+'</label> '+
									'	<div class="col-md-8"> '+
									' <select class="form-control" id="opts-column'+ s.mappings[i].key + '"  name="column' + s.mappings[i].key + '" '+
									' required> '+
									' <option value="">Select Option Value</option> '+opts+
									' </select> '+
									' </div> '+
									' </div>	';
								$("form#new-risk-schedule-form").append(data);
							}

						}
						$(".datepicker-input").each(function () {
							$(this).datetimepicker({
								format: 'DD/MM/YYYY'
							});

						});
					},
					error: function (xhr, error) {
						bootbox.alert(xhr.responseText);
					}
				});
				$('#riskscheduleModal').modal({
					backdrop: 'static',
					keyboard: true
				});
			}
		})



		return currTable;
	}




	var saveRiskSchedules= function(){
		var $classForm = $('#new-risk-schedule-form');
		var validator = $classForm.validate();
		$('#saveRiskSchedules').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})

			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "uw/policies/saveRiskSchedule";
			var request = $.post(url, data );
			request.success(function(){
				// $('#myPleaseWait').modal('hide');
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#risk-sched_tbl').DataTable().ajax.reload();
				$('#section_tbl').DataTable().ajax.reload();
				populatePolicyDetails();
				validator.resetForm();
				$('#riskscheduleModal').modal('hide');
			});

			request.error(function(jqXHR, textStatus, errorThrown){
				// $('#myPleaseWait').modal('hide');
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


	var getCommissionRate= function(bindDetCode){
		$.ajax({
			type: 'GET',
			url:  'uw/policies/getCommissionRate',
			dataType: 'json',
			data: {"detId": bindDetCode},
			async: true,
			success: function(result) {
				$("#comm-rate").val(result);
			},
			error: function(jqXHR, textStatus, errorThrown) {

			}
		});
	}

	var getCreateNewIntParties= function(){
		var arr = [];
		$("#interested_party_tbl tr").each(function(row,tr){
			var checked   = $(this).find('.int-check').eq(0).is(":checked");
			var partId   = $(this).find('.int-part-id').eq(0).val();
			if(checked){
				arr.push(partId);
			}

		});

		return arr;
	}

	var populateRelationTypes= function(){
		if($("#relationship-type").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "relationship-type",
				sort : 'typeId',
				change: function(e, a, v){
					$("#relation-type-id").val(e.added.typeId);
				},
				formatResult : function(a)
				{
					return a.relationDesc;
				},
				formatSelection : function(a)
				{
					return a.relationDesc;
				},
				initSelection: function (element, callback) {
					//var code  = $('#risk-cov-code').val();
					//var name = $("#cover-name").val();
					//var data = {covName:name,covId:code};
					//callback(data);
				},
				id: "relationDesc",
				width:"250px",
				placeholder:"Select Relation Type"

			});
		}

	}


	var getNewIntParties= function() {
		$("#btn-add-new-ips").click(function () {
			if ($("#policy-id").val() != '') {
				$("#part-pol-code").val($("#policy-id").val());
				populateRelationTypes();
				$('#beneficiaryModal').modal({
					backdrop: 'static',
					keyboard: true
				});
			}
			else {
				bootbox.alert("Select policy to Add Beneficiaries")
			}
		});

		var $form = $('#beneficiary-form');
		var validator = $form.validate();
		$('#saveBeneficiaryBtn').click(function(){
			if (!$form.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$form.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "uw/policies/createBeneficiary";
			var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#benefeciary_tbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#beneficiaryModal').modal('hide');
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





	var populateImportCoverTypesLov= function(){
		if($("#import-covertypes-frm").filter("div").html() != undefined)
		{
			Select2Builder.initAjaxSelect2({
				containerId : "import-covertypes-frm",
				sort : 'detId',
				change: function(e, a, v){
					$("#excel-binder-det-id").val(e.added.detId);
				},
				formatResult : function(a)
				{
					return a.covName+" - "+ a.subclassName;
				},
				formatSelection : function(a)
				{
					return a.covName+" - "+ a.subclassName;
				},
				initSelection: function (element, callback) {
					//var code  = $('#risk-cov-code').val();
					//var name = $("#cover-name").val();
					//var data = {covName:name,covId:code};
					//callback(data);
				},
				id: "covName",
				width:"250px",
				params: {bindCode: $("#binder-id").val()},
				placeholder:"Select Cover Type"

			});
		}

	}


	var populateImportSubCovers= function(binId){
		if($("#import-pol-covertypes-frm").filter("div").html() != undefined)
		{
			console.log($("#risk-bind-code").val());
			Select2Builder.initAjaxSelect2({
				containerId : "import-pol-covertypes-frm",
				sort : 'detId',
				change: function(e, a, v){
					$("#pol-excel-det-id").val(e.added.detId);
				},
				formatResult : function(a)
				{
					return a.covName+" - "+ a.subclassName;
				},
				formatSelection : function(a)
				{
					return a.covName+" - "+ a.subclassName;
				},
				initSelection: function (element, callback) {
					//var code  = $('#risk-cov-code').val();
					//var name = $("#cover-name").val();
					//var data = {covName:name,covId:code};
					//callback(data);
				},
				id: "covName",
				width:"250px",
				params: {bindCode: binId},
				placeholder:"Select Cover Type"

			});
		}
	}


	var importuWRisks= function(){
		$("#btn-import-risk").on('click', function(){
			populateImportCoverTypesLov();
			$('#importRisksModal').modal({
				backdrop: 'static',
				keyboard: true
			});
		});

		var $form = $("#risks-upload-form");
		var validator = $form.validate();
		$('form#risks-upload-form')
			.submit( function( e ) {
				e.preventDefault();
				if (!$form.valid()) {
					return;
				}
				// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
				var data = new FormData( this );
				data.append( 'file', $( '#file-avatar' )[0].files[0] );
				$.ajax( {
					url: 'uw/policies/importRisks',
					type: 'POST',
					data: data,
					processData: false,
					contentType: false,
					success: function (s ) {
						$('#importRisksModal').modal('hide');
						// $('#myPleaseWait').modal('hide');
						$("#btn-import-logs").show();
						Swal.fire({
						title: 'Success',
						text: 'Risks Uploaded Successfully',
						icon: 'success'
					 });
						populatePolicyDetails();
					},
					error: function(jqXHR, textStatus, errorThrown){
						// $('#myPleaseWait').modal('hide');
						Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
					}
				});
			});
	}


	var getClientDocs= function(clientId){
		var url = SERVLET_CONTEXT+"/protected/clients/setups/clientDocs/"+clientId;
		var currTable = $('#clientDocsList').DataTable( {
			"processing": true,
			"serverSide": true,
			autoWidth: true,
			"ajax": {
				'url': url,
			},
			lengthMenu: [ [10,15,20], [10,15,20] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "requiredDoc",
					"render": function ( data, type, full, meta ) {

						return full.requiredDoc.reqShtDesc;
					}
				},
				{ "data": "requiredDoc",
					"render": function ( data, type, full, meta ) {

						return full.requiredDoc.reqDesc;
					}
				},
				{ "data": "uploadedFileName" },
				{ "data": "checkSum" },
				{
                    "data": "cdId",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.downloadClientDoc(this);"><i class="fa fa-file-archive-o"></button>';

                    }

                },

			]
		} );
		return currTable;
	}

	var downloadClientDoc= function(button){
		var docs = JSON.parse(decodeURI($(button).data("docs")));
		window.open(SERVLET_CONTEXT+"/protected/clients/setups/clientDocument/"+docs['cdId'],
			'_blank' // <- This is what makes it open in a new window.
		);
	}

	var searchReqDocs= function(search){
		if($("#risk-code-pk").val() != ''){
			$.ajax({
				type: 'GET',
				url:  'uw/policies/getriskreqdocs',
				dataType: 'json',
				data: {"riskId": $("#risk-code-pk").val(),"docName":search},
				async: true,
				success: function(result) {
					$("#risksReqDocsTbl tbody").each(function(){
						$(this).remove();
					});
					for(var res in result){
						var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].sclReqrdId+"'></td><td>" + result[res].requiredDoc.reqShtDesc + "</td><td>" + result[res].requiredDoc.reqDesc + "</td></tr>";
						$("#risksReqDocsTbl").append(markup);
					}
					$("#req-risk-code").val($("#risk-code-pk").val());
					$('#riskReqDocsModal').modal({
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
		else{
			bootbox.alert("Select Risk to attach Documents")
		}
	}


	var getRiskImportLogs = function(){
		var url = "uw/policies/riskImportLogs";
		var currTable = $('#riskImportLogTbl').DataTable( UTILITIES.extendsOpts({
			"ajaxUrl":url,
			"columns": [
				{ "data": "errorMessage" },
			]
		}));
		return currTable;
	};

	var rejectTask = function () {
	     $('#rejectionModal').modal('show');

        $('#rejectConfirmButton').off('click').on('click', function() {
            var reason = $('#rejectionReason').val();

            if (reason) {
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/users/rejectTask',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        taskId: mckPolId,
                        reason: reason
                    }),
                    success: function(response) {
                        window.location.href = SERVLET_CONTEXT + "/protected/home";

                    },
                    error: function(xhr, status, error) {
                        Swal.fire({
                            title: 'Error',
                            text: xhr.responseText,
                            icon: 'error'
                        });
                    }
                });
            } else {
                Swal.fire({
                    title: 'Error',
                    text: 'No reason provided for rejection',
                    icon: 'error'
                });
            }
            // Hide the modal after submission
            $('#rejectionModal').modal('hide');
        });
	}

	var approveTask = function () {
	    $('#approvalModal').modal('show');
        $('#approveConfirmButton').off('click').on('click', function() {

            $.ajax({
                url: SERVLET_CONTEXT + '/protected/users/approveTask',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(mckPolId),
                success: function(response) {
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                },
                error: function(xhr, status, error) {
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });

            // Hide the modal after submission
            $('#approvalModal').modal('hide');
        });
	}

	var init = function(){
       var numberFormatter = new Intl.NumberFormat('en-US', {
           style: 'decimal',
           minimumFractionDigits: 2,
           maximumFractionDigits: 2
       });

       $("#btn-approve-quote").on('click', function (){
           approveTask();
       });

       $("#btn-reject-quot").on('click', function (){
           rejectTask();
       });

       // Show modal on button click
       $("#btn-negotiate-premium").click(function() {
           $("#negotiatePremiumModal").modal('show');
       });

       // Save negotiated premium and update Basic Premium field
       $("#save-negotiated-premium").click(function() {
           // Get the negotiated premium from the modal input
           var negotiatedPremium = parseFloat($("#negotiated-premium").val());

           if (negotiatedPremium && !isNaN(negotiatedPremium) && negotiatedPremium > 0) {
                $("#negotiated-prem").val(negotiatedPremium);
               // Format the negotiated premium without currency symbol
               $("#negotiated").text(numberFormatter.format(negotiatedPremium));

               // Close the modal
               $("#negotiatePremiumModal").modal('hide');
               $("#negotiated-premium").val('');
           } else {
               // If invalid input, show an alert
               alert("Please enter a valid premium amount.");
           }
       });
		populatePolicyDetails();
		$('#overrid-prem').number( true, 2 );
		$(".datepicker-input").each(function() {
			$(this).datetimepicker({
				format: 'DD/MM/YYYY'
			});

		});
		$(document).ajaxStart(function () {
			$("#btn-dispatch-trans,#btn-auth-policy,#btn-add-policy,#btn-make-ready-policy,#btn-undo-make-ready,btn-negotiate-premium," +
				"#btn-convert-policy,#btn-save-risk").attr("disabled", true);
		});
		$(document).ajaxComplete(function () {
			$("#btn-dispatch-trans,#btn-auth-policy,#btn-add-policy,#btn-make-ready-policy,#btn-undo-make-ready,#btn-save-remark,btn-negotiate-premium," +
				"#btn-convert-policy,#btn-save-risk").attr("disabled", false);
		});

		populateClientLov();
		$(document).ready(function() {
            $("#pol-bin-type").val("B").trigger('change');
        });
		$("#pol-bin-type").on('change', function(){
			populateBinderLov();
			if($(this).val()==="B"){
				$("#comm-rate").attr("readonly", "true");
			}
			else{
				$("#comm-rate").attr("readonly", "false");
			}

		});

		$("#pol-buss-type").on('change', function(){
			if($(this).val()==="S"){
				$("#prorated-full").val("S");
			}
			else{
				$("#prorated-full").val("P");
			}
		});

		$("#chk-import-risks").change(function() {
			if(this.checked) {
				$(".risk-detail-tab").hide();
			}
			else{
				$(".risk-detail-tab").show();
			}
		});

		$("#section_form_tbl").on('click','.hyperlink-btn',function() {
			var mandatory = $(this).closest('tr').find(".mandatory").val();
			if(mandatory && mandatory==="Y"){
				bootbox.alert("Cannot delete a Mandatory Premium Item");
				return;
			}
			$(this).closest('tr').remove();
		});





		populateCurrencyLov();
		populateSubAgentsLov();
		populatePaymentModes();
		populateUserBranches();
		populateInsuredLov();
		populateSubclassLov();
		populateCoverTypesLov();
		//$("#other-pol-details").hide();
		//getPolicyWet();
		changePolicyWetDt();
		validateRisk();
		getPolicyWet();
		UTILITIES.createAssignee();
		UTILITIES.emailReports();
		getNewIntParties();
		$("#btn-add-docs").click(function(){
			searchReqDocs("");
		});
		$("#btn-import-logs").hide();
		$("#btn-import-logs").click(function(){
			getRiskImportLogs();
			$('#riskimportLogsModal').modal({
				backdrop: 'static',
				keyboard: true
			});
		});
	}

	return {
		init: init,
		//deleteRiskCerts:deleteRiskCerts,
		editRiskDocs:editRiskDocs,
		downloadRiskDoc:downloadRiskDoc,
		getPolicyRemakrs:getPolicyRemakrs,
		downloadClientDoc:downloadClientDoc
	}


})(jQuery);

jQuery(UWScreen.init);