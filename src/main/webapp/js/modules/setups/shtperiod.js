

var UTILITIES = UTILITIES || {};
$(function(){

	$(document).ready(function() {
		
		createShortPeriods();
		addShortPeriodRates();
		saveShtPeriods();
		saveSecShtPeriods();
		createSubclassSectLov();
		addSecShortPeriodRates();
		$("#period-rate,#period-div-factor,#sec-period-rate,#sec-prd-div-factor").number(true, 2);
		
	});
});


function createSubclassSectLov(){
	if($("#sub-sec-frm").filter("div").html() != undefined)
	  {
		  Select2Builder.initAjaxSelect2({
	            containerId : "sub-sec-frm",
	            sort : 'ssId',
	            change: function(e, a, v){
	            	createSecShortPeriods(e.added.ssId);
					$("#sub-sec-id").val(e.added.ssId);
	            },
	            formatResult : function(a)
	            {
	            	return a.subclass.subDesc+" - "+a.section.desc
	            },
	            formatSelection : function(a)
	            {
	            	return a.subclass.subDesc+" - "+a.section.desc
	            },
	            initSelection: function (element, callback) {
	            	
                },
	            id: "ssId",
	            placeholder:"Select Subclass Section",
	        });
	  }
}




function saveShtPeriods(){
	var $paramForm = $('#short-period-form');
	var validator = $paramForm.validate();
	 $('#shortPeriodsModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#short-period-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
		 $("#chk-active").prop("checked", false);
	 });
	 
	 $('#saveShtPeriod').click(function(){
		 if (!$paramForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paramForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createShortPeriod";
	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
               });
				$('#sht-period-tbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#shortPeriodsModal').modal('hide');
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


function addShortPeriodRates(){
	$("#btn-add-sht-period").click(function(){
		 $('#short-period-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
		 $("#chk-active").prop("checked", false);
		 $('#shortPeriodsModal').modal('show');
	})
}


function addSecShortPeriodRates(){
	$("#btn-add-sec-sht-period").click(function(){
		if($("#sub-sec-id").val() != ''){
		 $('#sec-short-period-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
		 $("#sec-chk-active").prop("checked", false);
		 $("#sub-section-id").val($("#sub-sec-id").val());
		 $('#secShortPrdsModal').modal('show');
		}
		else{
			bootbox.alert("Select Subclass Section");
		}
	})
}

function createShortPeriods(){
	var url = "shortperiods";
	
	  var currTable = $('#sht-period-tbl').DataTable( {
			"processing": true,
			"serverSide": true,
			autoWidth: true,
			"ajax": {
				'url': url,
			},
			lengthMenu: [ [5], [5] ],
			pageLength: 5,
			destroy: true,
			"columns": [ 
				{ "data": "periodFrom" },
				{ "data": "periodTo" },
				{ 
					"data": "spCode",
					"render": function ( data, type, full, meta ) {
						return UTILITIES.currencyFormat(full.rate);
					}

				},
				{ 
					"data": "spCode",
					"render": function ( data, type, full, meta ) {
						return UTILITIES.currencyFormat(full.divFactor);
					}

				},
				{ "data": "active" },
				{ 
					"data": "spCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-shortperiods='+encodeURI(JSON.stringify(full)) + '  onclick="editShortPeriods(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "spCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-shortperiods='+encodeURI(JSON.stringify(full)) + '  onclick="confirmShortPeriodDelete(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  

	  
	  return currTable;
}


function editShortPeriods(button){
	var data = JSON.parse(decodeURI($(button).data("shortperiods")));
	$("#period-id").val(data["spCode"]);
	$("#period-from").val(data["periodFrom"]);
	$("#period-to").val(data["periodTo"]);
	$("#period-rate").val(data["rate"]);
	$("#period-div-factor").val(data["divFactor"]);
	$("#chk-active").prop("checked", data["active"]);
	 $('#shortPeriodsModal').modal('show');
}



function confirmShortPeriodDelete(button){
	var data = JSON.parse(decodeURI($(button).data("shortperiods")));
	bootbox.confirm("Are you sure want to delete range "+data["periodFrom"]+" to "+data["periodTo"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteShortPeriod/' + data["spCode"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#sht-period-tbl').DataTable().ajax.reload();
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



function createSecShortPeriods(ssCode){
	var url = "secshortperiods/"+ssCode;
	
	  var currTable = $('#sec-sht-period-tbl').DataTable( {
			"processing": true,
			"serverSide": true,
			autoWidth: true,
			"ajax": {
				'url': url,
			},
			lengthMenu: [ [5], [5] ],
			pageLength: 5,
			destroy: true,
			"columns": [ 
				{ "data": "periodFrom" },
				{ "data": "periodTo" },
				{ 
					"data": "spCode",
					"render": function ( data, type, full, meta ) {
						return UTILITIES.currencyFormat(full.rate);
					}

				},
				{ 
					"data": "spCode",
					"render": function ( data, type, full, meta ) {
						return UTILITIES.currencyFormat(full.divFactor);
					}

				},
				{ "data": "active" },
				{ 
					"data": "spCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-shortperiods='+encodeURI(JSON.stringify(full)) + '  onclick="editSecShortPeriods(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "spCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-shortperiods='+encodeURI(JSON.stringify(full)) + '  onclick="confirmSecShtPeriodDelete(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  

	  
	  return currTable;
}


function editSecShortPeriods(button){
	var data = JSON.parse(decodeURI($(button).data("shortperiods")));
	$("#sec-period-id").val(data["spCode"]);
	$("#sec-period-from").val(data["periodFrom"]);
	$("#sec-period-to").val(data["periodTo"]);
	$("#sec-period-rate").val(data["rate"]);
	$("#sec-prd-div-factor").val(data["divFactor"]);
	$("#sec-chk-active").prop("checked", data["active"]);
	 $("#sub-section-id").val($("#sub-sec-id").val());
	 $('#secShortPrdsModal').modal('show');
}



function confirmSecShtPeriodDelete(button){
	var data = JSON.parse(decodeURI($(button).data("shortperiods")));
	bootbox.confirm("Are you sure want to delete range "+data["periodFrom"]+" to "+data["periodTo"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteSecShortPeriod/' + data["spCode"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#sec-sht-period-tbl').DataTable().ajax.reload();
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



function saveSecShtPeriods(){
	var $paramForm = $('#sec-short-period-form');
	var validator = $paramForm.validate();
	 $('#secShortPrdsModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#sec-short-period-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
		 $("#sec-chk-active").prop("checked", false);
	 });
	 
	 $('#saveSecShtPeriod').click(function(){
		 if (!$paramForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paramForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createSecShortPeriod";
	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#sec-sht-period-tbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#secShortPrdsModal').modal('hide');
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