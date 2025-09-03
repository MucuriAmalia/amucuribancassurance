$(function(){

	$(document).ready(function() {

		$.ajaxSetup({
			cache: false
		});


		$('#rev-type').val("");

		loadrevisionModal();

		confirmSelectedRevision();

		populateClientLov();

		createAccountsForSel();


	})
});

function checkExistingTrans(){
	$.ajax({
		type: 'GET',
		url:  SERVLET_CONTEXT+ '/protected/uw/endorsements/countUnauthPolicies',
		dataType: 'json',
		data: {"policyNumber": $("#pol-number").val()},
		async: true,
		success: function(result) {
			console.log(result);
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


function getPolicyWet(){
	$('#eff-date-from').on('dp.change', function (ev) {
		var curDate = ev.date;
		var dt = moment(curDate).format('DD/MM/YYYY');
		$.ajax({
			type: 'GET',
			url:  'getWetDate',
			dataType: 'json',
			data: {"wefDate":dt},
			async: true,
			success: function(result) {
				$("#eff-to-date").val(moment(result).format('DD/MM/YYYY'));


			},
			error: function(jqXHR, textStatus, errorThrown) {

			}
		});
	});
}


function loadrevisionModal(){
	$("#btn-show-search-pol").on('click', function(){

		// Set all search parameters to empty

		$("#rev-search-name").val("");
		$("#pol-search-number").val("");
		$("#agent-search-number").val("");
		$("#rev-search-number").val("");
		$("#dr-search-number").val("");
		$('#revPoliciesModal').modal({
			backdrop: 'static',
			keyboard: true
		})
	});


	$("#btn-search-policies").on('click', function () {
		var clientCode = $("#rev-search-name").val();
		var policyNo = $("#pol-search-number").val();
		var agent = $("#agent-search-number").val();
		var riskCode = $("#rev-search-number").val();
		var refNo = $("#dr-search-number").val();
		if (clientCode === "" && policyNo === "" && agent === "" && riskCode === "" && refNo === "") {
			bootbox.alert("Provide At least one Search Parameter");
			return;
		}
		createRevisionTrans();

	});


}

function confirmSelectedRevision(){
	$("#selectauthtrans").on('click', function(){
		if ($("#pol-number").val() != ''){
			$('#revPoliciesModal').modal('hide');
		}
		else{
			bootbox.alert('Select a Policy to continue');
		}
		checkExistingTrans();
	});
}



function createRevisionTrans(){
	var url = "cancelledPolicies";
	var currTable = $('#revtranstbl').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": {
			'url': url,
			'data':{
				'client': $("#client-id").val(),
				'policyNo':  $("#pol-search-number").val(),
				'agent':  $("#agent-search-number").val(),
				'endorseNumber':  $("#rev-search-number").val(),
				'refno': $("#dr-search-number").val(),
				'endorsetype': $("#rev-type").val(),
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
			{ "data": "clientName"},
			{ "data": "agentName"},
			{ "data": "polUwYr" },
			{ "data": "binderName"}
		]
	} );

	$('#revtranstbl tbody').on('click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = currTable.rows('.table-primary').data();
		if (aData[0] === undefined || aData[0] === null) {

		} else {
			$("#rev-pol-id").val( aData[0].policyId);
			$("#pol-number").val( aData[0].policyNo);
			$.ajax({
				type: 'GET',
				url:  'countUnauthPolicies',
				dataType: 'json',
				data: {"policyNumber": $("#pol-number").val()},
				async: true,
				success: function(result) {
					console.log("Four");
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
	});


	return currTable;
}

function createUnauthPolicies() {
    var url = SERVLET_CONTEXT + "/protected/uw/endorsements/unauthPolicies";
    var currTable = $('#poltrans').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
            'data': {
                'policyNumber': $("#pol-number").val(),
            },
        },
        autoWidth: true,
        lengthMenu: [[10], [10]],
        pageLength: 10,
        destroy: true,
        searching: false,
        "columns": [
            { "data": "polNo" },
            {
                "data": "polCreateddt",
                "render": function (data, type, full, meta) {
                    return moment(full.polCreateddt).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wef",
                "render": function (data, type, full, meta) {
                    return moment(full.wef).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wet",
                "render": function (data, type, full, meta) {
                    return moment(full.wet).format('DD/MM/YYYY');
                }
            },
            {
                "data": "transactionId",
                "render": function (data, type, full, meta) {
                    return full.user;
                }
            },
            {
                "data": "currentStatus",
                "render": function (data, type, full, meta) {
                    if (full.currentStatus === 'D') return "Draft";
                    else if (full.currentStatus === 'NT') return "New Transaction";
                    else if (full.currentStatus === 'RV') return "Revision Invoice";
                }
            },
            {
                "data": "transactionId",
                "render": function (data, type, full, meta) {
                    var action = full.status === "A" ? "View" : "Edit";
                    return '<form action="' + SERVLET_CONTEXT + '/protected/uw/reinstate/edituwtrans" method="post">' +
                           '<input type="hidden" name="id" value="' + full.transactionId + '">' +
                           '<input type="submit" class="btn btn-success" value="' + action + '">' +
                           '</form>';
                }
            },
            {
                "data": "transactionId",
                "render": function (data, type, full, meta) {
                    return '<input type="button" class="btn btn-danger" data-policy="' + encodeURI(JSON.stringify(full)) + '" value="Delete" onclick="confirmPolicyDelete(this);"/>';
                }
            },
        ]
    });
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
				data: {"policyId": policy["transactionId"]},
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

function populateClientLov() {
	if ($("#client-frm").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "client-frm",
			sort: 'fname',
			change: function (e, a, v) {
				$("#rev-search-name").val(e.added.fname.concat(e.added.otherNames));
				$('#client-id').val(e.added.tenId);
			},
			formatResult: function (a) {
				if (a.idNo !== null) {
					return a.fname + " " + a.otherNames + " - " + a.idNo;
				}
				else {
					return a.fname + " " + a.otherNames
				}
			},
			formatSelection: function (a) {
				if (a.idNo !== null) {
					return a.fname + " " + a.otherNames + " - " + a.idNo;
				}
				else {
					return a.fname + " " + a.otherNames
				}
			},
			initSelection: function (element, callback) {

			},
			id: "tenId",
			placeholder: "Select Client"

		});
	}

	$("#client-frm").on("select2-removed", function (e) {
		$("#rev-search-name").val('');
		$("#client-id").val('');
	})
}

function createAccountsForSel() {
	if ($("#acc-frm").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "acc-frm",
			sort: 'name',
			change: function (e, a, v) {
				$("#agent-search-number").val(e.added.acctId);
				$("#agent-search-name").val(e.added.name);
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