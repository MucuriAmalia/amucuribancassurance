
var UTILITIES = UTILITIES || {};
$(function(){

	$(document).ready(function() {
		createSubclassLov();
		addTaxRates();
		//createRevenueItemsForSel();
	});
});



function createTaxRates(subCode,prodCode){
	var url = "taxRates";
	  var currTable = $('#tax-rates-tbl').DataTable( {
			"processing": true,
			"serverSide": true,
			autoWidth: true,
		  "ajax": {
			  'url': url,
			  'data':{
				  'subCode':subCode,
				  'prodCode':prodCode
			  },
		  },
			lengthMenu: [ [5,10], [5,10] ],
			pageLength: 5,
			destroy: true,
			"columns": [
				{ "data": "rateType" },
				{ "data": "basedOn"},
				{ "data": "rangeFrom" },
				{ "data": "rangeTo" },
				{ "data": "taxRate" },
				{ "data": "divFactor" },
				{ "data": "taxLevel" },
				{ "data": "revenueItems",
					  "render": function ( data, type, full, meta ) {
							   
							  return UTILITIES.getRevDesc(full.revenueItems.item);
					   }	
				},
				{
					"data": "mandatory",
					"render": function ( data, type, full, meta ) {
						if(full.mandatory){
							return "Yes";
						}
						else{
							return "No";
						}
					}
				},
				{
					"data": "active",
					"render": function ( data, type, full, meta ) {
						if(full.active){
							return "Yes";
						}
						else{
							return "No";
						}
					}
				},
				{
					"data": "taxId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-taxes='+encodeURI(JSON.stringify(full)) + ' onclick="editTaxes(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "taxId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-taxes='+encodeURI(JSON.stringify(full)) + ' onclick="deleteTaxes(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}

function editTaxes(button){
	var taxes = JSON.parse(decodeURI($(button).data("taxes")));
	$("#tax-code").val(taxes["taxId"]);
	$("#tax-sub-det").val(taxes["subclass"].subId);
	$("#rev-item-id").val(taxes["revenueItems"].revenueId);
	$("#rev-item-name").val(taxes["revenueItems"].item);
	$("#tax-prod-det").val(taxes["productsDef"].proCode);
	createRevenueItemsForSel(taxes["subclass"].subId);
	$("#tax-range-from").val(taxes["rangeFrom"]);
	$("#tax-range-to").val(taxes["rangeTo"]);
	$("#tax-rate-type").val(taxes["rateType"]);
	$("#tax-based-on").val(taxes["basedOn"]);
	$("#tax-rate").val(taxes["taxRate"]);
	$("#tax-div-fact").val(taxes["divFactor"]);
	$("#tax-level").val(taxes["taxLevel"]);
	$("#tax-active").prop("checked", taxes["active"]);
	$("#tax-mandatory").prop("checked", taxes["mandatory"]);
	$('#taxRatesModal').modal({
		  backdrop: 'static',
		  keyboard: true
		})
}

function deleteTaxes(button){
	var taxes = JSON.parse(decodeURI($(button).data("taxes")));
	bootbox.confirm("Are you sure want to delete "+ UTILITIES.getRevDesc(taxes["revenueItems"].item)+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deletTaxRates/' + taxes["taxId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
                title: 'Success',
                text: 'Record Deleted Successfully',
                icon: 'success'
            });
			        	$('#tax-rates-tbl').DataTable().ajax.reload();
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


function createSubclassLov(){
	if($("#sub-class-def").filter("div").html() != undefined)
	{
		  Select2Builder.initAjaxSelect2({
	          containerId : "sub-class-def",
	          sort : 'psId',
	          change: function(e, a, v){
	        	  $("#sub-code").val(e.added.subclass.subId);
				  $("#prod-code").val(e.added.product.proCode);
				  console.log("Sub code "+e.added.subclass.subId+" Prod Code..."+e.added.product.proCode)
	        	  createTaxRates(e.added.subclass.subId,e.added.product.proCode);
	          },
	          formatResult : function(a)
	          {
	          	return a.product.proDesc+" - ("+ a.subclass.subDesc+")";
	          },
	          formatSelection : function(a)
	          {
	          	return a.product.proDesc+" - ("+ a.subclass.subDesc+")";
	          },
	          initSelection: function (element, callback) {
	        	  
	          },
	          id: "psId",
	          width:"200px",
	          placeholder:"Select Product Sub Classes"
	      });
		  
	}
}


function addTaxRates(){
	$("#btn-add-tax-rates").click(function(){
		if($("#sub-code").val() != ''){
		$('#tax-rates-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$("#tax-sub-det").val($("#sub-code").val());
		$("#tax-prod-det").val($("#prod-code").val());
		createRevenueItemsForSel($("#sub-code").val());
		$('#taxRatesModal').modal({
			  backdrop: 'static',
			  keyboard: true
			})
		}
		else{
			bootbox.alert("Select Sub Class to Add Tax Rates");
		}
		
	});
	var $classForm = $('#tax-rates-form');
	 var validator = $classForm.validate();
	 $('#savetaxratesBtn').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			
			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createTaxRates";
   var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record Created/Updated Successfully',
                icon: 'success'
            });
				$('#tax-rates-tbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#tax-rates-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$("#tax-rates-form input:checkbox").prop("checked", false);
				$('#taxRatesModal').modal('hide');
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


function createRevenueItemsForSel(prodCode){
	if($("#rev-item-def").filter("div").html() != undefined)
	  {
		  Select2Builder.initAjaxSelect2({
	            containerId : "rev-item-def",
	            sort : 'item',
	            change: function(e, a, v){
	            	$("#rev-item-id").val(e.added.revenueId);
	            },
	            formatResult : function(a)
	            {
	            	return UTILITIES.getRevDesc(a.item)
	            },
	            formatSelection : function(a)
	            {
	            	return UTILITIES.getRevDesc(a.item)
	            },
	            initSelection: function (element, callback) {
	            	var code = $("#rev-item-id").val();
	                var name = $("#rev-item-name").val();
	                var data = {item:name,revenueId:code};
	                callback(data);
                },
	            id: "revenueId",
			    params: {prodCode: prodCode},
	            placeholder:"Select Revenue Item",
	        });
	  }
}