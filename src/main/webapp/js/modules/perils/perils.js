var UTILITIES = UTILITIES || {};

$(function(){

	$(document).ready(function() {
		createPerils();
		addPerils();
		createSubclassLov();
		addSclPerils();
		saveSclPerils();
	})
});



function createSubclassLov(){
	if($("#sub-class-def").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "sub-class-def",
			sort : 'subDesc',
			change: function(e, a, v){
				$("#sub-code").val(e.added.subId);
				createSclPerils(e.added.subId);
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
			width:"200px",
			placeholder:"Select Sub Class"
		});

	}
}


function createPerilsLov(subCode){
	if($("#peril-def").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "peril-def",
			sort : 'perilDesc',
			change: function(e, a, v){
				$("#sub-peril-code").val(e.added.perilCode);
			},
			formatResult : function(a)
			{
				return a.perilDesc
			},
			formatSelection : function(a)
			{
				return a.perilDesc
			},
			initSelection: function (element, callback) {
				var code = $("#sub-peril-code").val();
				var name = $("#sub-peril-name").val();
				var data = {perilCode:code,perilDesc:name};
				callback(data);
			},
			id: "perilCode",
			width:"200px",
			placeholder:"Select Peril",
			params: {subCode: subCode}
		});

	}
}

function createPerils(){
	var url = "perilsList";
	var currTable = $('#perilList').DataTable( {
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': function(d) {
				if (d.search && d.search.value) {
					d.search.value = d.search.value.toUpperCase();
				}
				return d;
			}
		},
		lengthMenu: [ [5], [5] ],
		pageLength: 5,
		destroy: true,
		"columns": [
			{ "data": "perilShtDesc" },
			{ "data": "perilDesc" },
			{ "data": "perilType" },
			{
				"data": "perilCode",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-perils='+encodeURI(JSON.stringify(full)) + ' onclick="editPerils(this);"><i class="fa fa-pencil-square-o"></button>';
				}

			},
			{
				"data": "perilCode",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-perils='+encodeURI(JSON.stringify(full)) + ' onclick="deletePerils(this);"><i class="fa fa-trash-o"></button>';
				}

			},
		]
	} );
	return currTable;
}


function deleteSubPerils(button){
	var perils = JSON.parse(decodeURI($(button).data("perils")));
	bootbox.confirm("Are you sure want to delete "+perils["peril"].perilDesc+"?", function(result) {
		if(result){
			$.ajax({
				type: 'GET',
				url:  'deleteSubPeril/' + perils["sperId"],
				dataType: 'json',
				async: true,
				success: function(result) {
					Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
					$('#subperilsTbl').DataTable().ajax.reload();
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

function deletePerils(button){
	var perils = JSON.parse(decodeURI($(button).data("perils")));
	bootbox.confirm("Are you sure want to delete "+perils["perilShtDesc"]+"?", function(result) {
		 if(result){
			  $.ajax({
			        type: 'GET',
			        url:  'deletePeril/' + perils["perilCode"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
						Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#perilList').DataTable().ajax.reload();		
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

function editPerils(button){
	var perils = JSON.parse(decodeURI($(button).data("perils")));
	$("#peril-code").val(perils["perilCode"]);
	$("#peril-id").val(perils["perilShtDesc"]);
	$("#peril-name").val(perils["perilDesc"]);
	$("#peril-type").val(perils["perilType"]);
	$('#perilsModal').modal({
		  backdrop: 'static',
		  keyboard: true
		})
}

function editSubPerils(button){
	var perils = JSON.parse(decodeURI($(button).data("perils")));
	$("#lbl-sper-id").val(perils["sperId"]);
	$("#txt-sub-id").val(perils["subclass"].subId);
	$("#sub-peril-code").val(perils["peril"].perilCode);
	$("#sub-peril-name").val(perils["peril"].perilDesc);
	createPerilsLov(perils["subclass"].subId);
	$("#sper-type").val(perils["perilType"]);
	$("#si-limit").val(perils["siOrLimit"]);
	$("#limit-desc").val(perils["description"]);
	$("#person-limit").val(perils["limit"]);
	$("#dep-perce").val(perils["deprecPercent"]);
	$("#limit-amount").val(perils["claimLimit"]);
	$("#excess-type").val(perils["excessType"]);
	$("#depend-loss-type").prop("checked", perils["dependLossType"]);
	$("#expire-clm").prop("checked", perils["expireOnClaim"]);
	$("#mandatory").prop("checked", perils["mandatory"]);
	$("#excess-amount").val(perils["excess"]);
	$("#excess-min").val(perils["excessMin"]);
	$("#excess-max").val(perils["excessMax"]);
	$("#clm-excess-type").val(perils["claimExcessType"]);
	$("#clm-excess-amount").val(perils["claimExcess"]);
	$("#clm-excess-min").val(perils["claimExcessMin"]);
	$("#clm-excess-max").val(perils["claimExcessMax"]);
	$('#subperilsModal').modal({
		backdrop: 'static',
		keyboard: true
	})
}


function addSclPerils(){
	$("#btn-add-sub-perils").click(function(){
		if($("#sub-code").val() != '') {
			$('#sub-perils-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
			$("#txt-sub-id").val($("#sub-code").val());
			createPerilsLov($("#sub-code").val());
			$('#subperilsModal').modal({
				backdrop: 'static',
				keyboard: true
			})
		}
		else{
			bootbox.alert("Select Sub Class to Add Peril....");
			return;
		}
	});
}

function addPerils(){
	$("#btn-add-perils").click(function(){
		$('#perils-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$('#perilsModal').modal({
			  backdrop: 'static',
			  keyboard: true
			})
	});
	
	 var $classForm = $('#perils-form');
	 var validator = $classForm.validate();
	 $('#savePerilsBtn').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createPerilsDef";
       var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#perilList').DataTable().ajax.reload();		
				validator.resetForm();
				$('#perils-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$('#perilsModal').modal('hide');
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


function createSclPerils(subCode){
	var url = "subperilsList/"+subCode;
	var currTable = $('#subperilsTbl').DataTable( {
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
			'data': function(d) {
				if (d.search && d.search.value) {
					d.search.value = d.search.value.toUpperCase();
				}
				return d;
			}
		},
		lengthMenu: [ [5], [5] ],
		pageLength: 5,
		destroy: true,
		"columns": [
			{ "data": "peril",
				"render": function ( data, type, full, meta ) {
					return full.peril.perilDesc;
				}
			},
			{ "data": "perilType" },
			{ "data": "siOrLimit" },
			{ "data": "description" },
			{ "data": "personLimit",
				"render": function ( data, type, full, meta ) {
					return UTILITIES.currencyFormat(full.personLimit);
				}
			},
			{ "data": "claimLimit",
				"render": function ( data, type, full, meta ) {
					return UTILITIES.currencyFormat(full.claimLimit);
				}
			},
			{ "data": "excessType" },
			{ "data": "excess",
				"render": function ( data, type, full, meta ) {
					return UTILITIES.currencyFormat(full.excess);
				}
			},
			{ "data": "expireOnClaim" },
			{
				"data": "sperId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-perils='+encodeURI(JSON.stringify(full)) + ' onclick="editSubPerils(this);"><i class="fa fa-pencil-square-o"></button>';
				}
			},
			{
				"data": "sperId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-perils='+encodeURI(JSON.stringify(full)) + ' onclick="deleteSubPerils(this);"><i class="fa fa-trash-o"></button>';
				}
			},
		]
	} );
	return currTable;
}


function saveSclPerils(){
	var $classForm = $('#sub-perils-form');
	var validator = $classForm.validate();
	$('#saveSubPerilsBtn').click(function(){
		if (!$classForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createSubPeril";
		var request = $.post(url, data );
		request.success(function(){
			Swal.fire({
					title: 'Success',
					text: 'Record created/updated Successfully',
					icon: 'success'
				});
			$('#subperilsTbl').DataTable().ajax.reload();
			validator.resetForm();
			$('#sub-perils-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
			$('#subperilsModal').modal('hide');
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