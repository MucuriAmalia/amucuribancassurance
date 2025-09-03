
$(function(){

	$(document).ready(function() {
		
		createProdGrpSelect();
		addProductGroup();
		addProduct();
		addEditProductGroup();
		deleteProductGroup();
		getProductSubclassesModal();
		saveProdSubclasses();
		updateProdSubclass();
		//$('#pro-min-prem').number( true, 2 );
		 $("#prg-pk").val("");
		 
		 $("#searchSubclasses").click(function(){
			 searchSubclasses($("#sub-name-search").val());
		 })
		
	});
});


function makeProductSelection(){
	var table = $('#prodList').DataTable();
	$('#prodList tbody').on( 'click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = table.rows('.table-primary').data();
		if (aData[0] === undefined || aData[0] === null){
			 
		}
		else{
			 $("#prg-prod-code").val(aData[0].proCode);
			  createProductSubclasses();
		}
		 
    } );
}


function addProduct(){
	
	$("#btn-add-prod").click(function(){
		if($("#prg-pk").val() != ''){
		
		$('#product-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$("#product-form input:checkbox").prop("checked", false);
		$("#prg-group-pks").val($("#prg-pk").val());
		$('#prodModal').modal({
			  backdrop: 'static',
			  keyboard: true
			});
			createSapCodeSel();
		}
		else{
			bootbox.alert("Select Product group to continue");
		}
	});

	$('form#product-form')
		.submit( function( e ) {
			var $form = $("#product-form");
			var validator = $form.validate();
			e.preventDefault();
			if (!$form.valid()) {
				return;
			}
			var data = new FormData( this );
			$.ajax( {
				url: 'createProduct',
				type: 'POST',
				data: data,
				processData: false,
				contentType: false,
				success: function (s ) {
					
					Swal.fire({
						title: 'Success',
						text: 'Record Created/Updated Successfully',
						icon: 'success'
					});
					$('#prodList').DataTable().ajax.reload();
					validator.resetForm();
					$("#prg-prod-code").val("");
					createProductSubclasses();
					$('#product-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
					$("#product-form input:checkbox").prop("checked", false);
					$('#prodModal').modal('hide');

				},
				error: function(jqXHR, textStatus, errorThrown){
					Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
				}
			});
		});
}
function createSapCodeSel() {

	if ($("#sap-frm").filter("div").html() != undefined) {
		Select2Builder.initAjaxSelect2({
			containerId: "sap-frm",
			sort: 'productDescription',
			change: function (e, a, v) {
				$("#prod-sap-id").val(e.added.prodId);
				$("#prod-sap-code").val(e.added.productCode);
				$("#sap-desc").val(e.added.productDescription);
			},
			formatResult: function (a) {
				return a.productCode + "-" + a.productDescription;
			},
			formatSelection: function (a) {
				return a.productCode + "-" + a.productDescription;
			},
			initSelection: function (element, callback) {
				var code = $("#prod-sap-code").val();
				var id = $("#prod-sap-id").val();
				var name = $("#sap-desc").val();
				var data = {productDescription: name, productCode: code, prodId: id};
				callback(data);
			},
			id: "prodId",
			placeholder: "Select SAP Code",
			params: {cardId: ""}
		});
	}
}

function addProductGroup(){
	$("#btn-add-group").click(function(){
		$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$('#delproductGrpBtn').hide();
		$('#prodGrpModal').modal({
			  backdrop: 'static',
			  keyboard: true
			})
	});
	
	$("#btn-edit-group").click(function(){
		if($("#prg-code").val() != ''){
			$('#delproductGrpBtn').show();
			$('#prodGrpModal').modal({
				  backdrop: 'static',
				  keyboard: true
				})
		}
		else{
			bootbox.alert("Select Product Group to Edit");
		}
	});
	 
}

function deleteProductGroup(){
	$("#delproductGrpBtn").click(function(){
	if($("#prg-code").val() != ''){
	bootbox.confirm("Are you sure want to delete the record?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteProductGroup/' + $("#prg-code").val(),
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
                title: 'Success',
                text: 'Record Deleted Successfully',
                icon: 'success'
            });
			        	$("#prg-pk").val("");
						createProducts();
						$('#prd-group').select2('val', null);
						createProdGrpSelect();
						$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
						$('#prodGrpModal').modal('hide');
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
	else{
		bootbox.alert("No Record to delete");
	}
	});
}

function addEditProductGroup(){
	var $currForm = $('#prg-group-form');
	var currValidator = $currForm.validate();
	
	
	$('#saveproductGrpBtn').click(function(){
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createGroup";
        var request = $.post(url, data );
        request.success(function(){
        	 Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
			currValidator.resetForm();
			
			$("#prg-pk").val("");
			createProducts();
			$('#prd-group').select2('val', null);
			createProdGrpSelect();
			$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
			$('#prodGrpModal').modal('hide');
        });
        request.error(function(jqXHR, textStatus, errorThrown){
        	 Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
		});
		request.always(function(){
			$btn.button('reset');
        });
		
	});
}


function createProductSubclasses(){
	var url = "subclasses/0";
	if($("#prg-prod-code").val() != ''){
		url = "subclasses/"+$("#prg-prod-code").val();
	}
	  var currTable = $('#prodSubclassList').DataTable( {
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
                { "data": "subclass",
					  "render": function ( data, type, full, meta ) {
							   
							  return full.subclass.subShtDesc;
					   }	
				},
				{ "data": "subclass",
					  "render": function ( data, type, full, meta ) {
							   
							  return full.subclass.subDesc;
					   }	
				},
				{ "data": "mandatory",
					"render": function ( data, type, full, meta ) {
					    if(full.mandatory){
							return 'Yes';
						}
						else{
							return 'No';
						}
					}
				},
				{ "data": "active",
					"render": function ( data, type, full, meta ) {
						if(full.active){
							return 'Yes';
						}
						else{
							return 'No';
						}
					}
				},
				{
					"data": "proCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-subclass='+encodeURI(JSON.stringify(full)) + ' value="Edit" onclick="editProductSubclass(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "proCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-subclass='+encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="deleteProdSubclass(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}

function createProducts(){
	var url = "products/0";
	if($("#prg-pk").val() != ''){
		url = "products/"+$("#prg-pk").val();
	}
	var currTable = $('#prodList').DataTable( {
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
			{
				"class":          "details-control",
				"orderable":      false,
				"data":           "proShtDesc",
				"defaultContent": ""
			},
			{ "data": "proDesc" },
			{ "data": "sapCode",
				"render": function ( data, type, full, meta ) {
					if (full.sapCode) {
						return full.sapCode.productCode;
					}
				}
			},
			{ "data": "proPolPrefix" },
			{ "data": "installAllowed",
				"render": function ( data, type, full, meta ) {
					if(full.installAllowed){
						return "Yes";
					}
					else
						return "No";
				}
			},
			{ "data": "minPrem" },
			{ "data": "riskNote" },
			{ "data": "renewable",
				"render": function ( data, type, full, meta ) {
					if(full.renewable){
						return "Yes";
					}
					else
						return "No";
				}
			},
			{ "data": "active",
				"render": function ( data, type, full, meta ) {
					if(full.active){
						return "Yes";
					}
					else
						return "No";
				}
			},
			{ "data": "ageApplicable",
				"render": function ( data, type, full, meta ) {
					if(full.ageApplicable){
						return (full.ageApplicable==="Y")?"Yes":"No";
					}
					else
						return "No";
				}
			},
			{
				"data": "proCode",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-success btn btn-info btn-sm"  data-product='+encodeURI(JSON.stringify(full)) + ' onclick="editProduct(this);"><i class="fa fa-pencil-square-o"></button>';
				}
			},
			{
				"data": "proCode",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-product='+encodeURI(JSON.stringify(full)) + ' onclick="confirmProdDel(this);"><i class="fa fa-trash-o"></button>';
				}
			},
		]
	} );
	return currTable;
}



function createProdGrpSelect(){
	if($("#prd-group").filter("div").html() != undefined)
	  {
		  Select2Builder.initAjaxSelect2({
	            containerId : "prd-group",
	            sort : 'prgDesc',
	            change: function(e, a, v){
	            	 $("#prg-pk").val(e.added.prgCode);
	            	 $("#prg-code").val(e.added.prgCode);
	            	 $("#grp-desc").val(e.added.prgDesc);
	            	 $("#prg-type").val(e.added.prgType);
	            	createProducts();
	            	makeProductSelection();
	            },
	            formatResult : function(a)
	            {
	            	return a.prgDesc
	            },
	            formatSelection : function(a)
	            {
	            	return a.prgDesc
	            },
	            initSelection: function (element, callback) {
//	            	var code = $("#obId").val();
//	                var name = $("#ob-name").val();
//	                model.accounts.branch.brnCode = code;
//	                var data = {obName:name,obId:code};
//	                callback(data);
                },
	            id: "prgCode",
	            placeholder:"Select Product Group",
	        });
	  }
}

function editProduct(button){
	var product = JSON.parse(decodeURI($(button).data("product")));
	$("#pro-code").val(product["proCode"]);
	$("#pro-sht-desc").val(product["proShtDesc"]);
	$("#pro-name").val(product["proDesc"]);
	$("#pro-pol-prefix").val(product["proPolPrefix"]);
	$("#pro-clm-prefix").val(product["proClmPrefix"]);
	$("#pro-min-prem").val(product["minPrem"]);
	$("#pro-risk-note").val(product["riskNote"]);
	$("#pro-inter-type").val(product["interfaceType"]);
	if(product["sapCode"]) {
		$("#prod-sap-id").val(product["sapCode"].prodId);
		$("#prod-sap-code").val(product["sapCode"].productCode);
		$("#sap-desc").val(product["sapCode"].productDescription);
	}
	createSapCodeSel();
	$("#chk-multi-class").prop("checked", product["multiSubClass"]);
	$("#chk-renewable").prop("checked", product["renewable"]);
	$("#chk-motor-prod").prop("checked", product["motorProduct"]);
	$("#chk-mid-exp").prop("checked", product["midnightExp"]);
	$("#chk-motor-install-allowd").prop("checked", product["installAllowed"]);
	$("#chk-active").prop("checked", product["active"]);
	if(product["ageApplicable"]){
		if(product["ageApplicable"]==="Y"){
			$("#chk-age-applicable").prop("checked", true);
		}
		else $("#chk-age-applicable").prop("checked", false);
	}else{
		$("#chk-age-applicable").prop("checked", false);
	}
	if(product["wibaProduct"]){
		if(product["wibaProduct"]==="Y"){
			$("#chk-wibaProduct").prop("checked", true);
		}
		else $("#chk-wibaProduct").prop("checked", false);
	}else{
		$("#chk-wibaProduct").prop("checked", false);
	}
	$("#prg-group-pks").val($("#prg-pk").val());
	$('#prodModal').modal({
		  backdrop: 'static',
		  keyboard: true
		});
}

function editProductSubclass(button){
	var subclass = JSON.parse(decodeURI($(button).data("subclass")));
	$("#prod-sub-pk").val(subclass["psId"]);
	$("#prod-sub-pro-code").val(subclass["product"].proCode);
	$("#prod-sub-code-pk").val(subclass["subclass"].subId);
	$("#prod-sub-class-id").val(subclass["subclass"].subShtDesc);
	$("#prod-sub-class-name").val(subclass["subclass"].subDesc);
	$("#prod-sub-mandatory").prop("checked", subclass["mandatory"]);
	$("#prod-sub-active").prop("checked", subclass["active"]);
	$('#editProdSubclassModal').modal({
		  backdrop: 'static',
		  keyboard: true
		});
}

function deleteProdSubclass(button){
	var subclass = JSON.parse(decodeURI($(button).data("subclass")));
	bootbox.confirm("Are you sure want to delete "+subclass["subclass"].subDesc+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteProductSubclass/' + subclass["psId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
                title: 'Success',
                text: 'Record Deleted Successfully',
                icon: 'success'
            });
			        	$('#prodSubclassList').DataTable().ajax.reload();
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

function confirmProdDel(button){
	var product = JSON.parse(decodeURI($(button).data("product")));
	bootbox.confirm("Are you sure want to delete "+product["proDesc"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteProduct/' + product["proCode"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#prodList').DataTable().ajax.reload();
			        	$("#prg-prod-code").val("");
						  createProductSubclasses();
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

// Enhanced searchSubclasses function to show mapping status
function searchSubclasses(search) {
	if ($("#prg-prod-code").val() != '') {
		$.ajax({
			type: 'GET',
			url: 'prodsubclasses', // returns SubclassWithMappingStatus directly
			dataType: 'json',
			data: {"prodCode": $("#prg-prod-code").val(), "subName": search},
			async: true,
			success: function(result) {
				$("#prodsubclassTbl tbody").each(function(){
					$(this).remove();
				});

				for (var res in result) {
					var subclass = result[res];
					var hasMapping = subclass.hasRevenueMapping;

					var statusIcon = hasMapping ?
						'<span class="text-success"><i class="fa fa-check-circle"></i></span>' :
						'<span class="text-danger"><i class="fa fa-exclamation-triangle"></i></span>';

					var disabledAttr = hasMapping ? '' : 'disabled';
					var titleAttr = hasMapping ? '' : 'title="No revenue mapping configured"';

					var markup = "<tr><td><input type='checkbox' name='record' id='" +
						subclass.subId + "' " + disabledAttr + " " + titleAttr +
						"></td><td>" + subclass.subShtDesc + " " + statusIcon +
						"</td><td>" + subclass.subDesc + "</td></tr>";
					$("#prodsubclassTbl").append(markup);
				}

				$("#sub-scl-pro-code").val($("#prg-prod-code").val());

				$('#prodSubclModal').modal({
					backdrop: 'static',
					keyboard: true
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
	} else {
		bootbox.alert("Select Product to attach sub classes");
	}
}

function getProductSubclassesModal(){
	
	$("#btn-add-prod-sub").click(function(){
		searchSubclasses("");
	});
}

function saveProdSubclasses(){
	var arr = [];
	$("#saveProdSubclassesBtn").click(function() {
		// Reset the array for multiple clicks
		arr = [];

		$("#prodsubclassTbl tbody").find('input[name="record"]').each(function() {
			if ($(this).is(":checked")) {
				arr.push(parseInt($(this).attr("id")));
			}
		});

		if (arr.length == 0) {
			bootbox.alert("No Sub Class Selected to attach..");
			return;
		}

		var $currForm = $('#prod-subcl-form');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}

		// Show loading state
		$("#saveProdSubclassesBtn").prop('disabled', true).text('Saving...');

		var data = {};
		$currForm.serializeArray().map(function(x) {
			data[x.name] = x.value;
		});
		var url = "createProductSubclass"; // Server-side validation happens here
		data.subclasses = arr;

		$.ajax({
			url: url,
			type: "POST",
			data: JSON.stringify(data),
			success: function(s) {
				Swal.fire({
					title: 'Success',
					text: 'Product subclasses attached successfully',
					icon: 'success'
				});
				$('#prodSubclassList').DataTable().ajax.reload();
				$('#prodSubclModal').modal('hide');
				arr = [];
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR);
				Swal.fire({
					title: 'Error',
					text: jqXHR.responseText,
					icon: 'error'
				});
			},
			dataType: "json",
			contentType: "application/json",
			complete: function() {
				$("#saveProdSubclassesBtn").prop('disabled', false).text('Save Subclasses');
			}
		});
	});
}

// Enhanced updateProdSubclass function with quick validation
function updateProdSubclass() {
	var $currForm = $('#update-prod-sub-form');
	var currValidator = $currForm.validate();

	$('#updateprodSubclassBtn').click(function () {
		if (!$currForm.valid()) {
			return;
		}

		var subclassId = parseInt($("#prod-sub-code-pk").val());

		// Show loading state
		var $btn = $(this);
		$btn.prop('disabled', true).text('Validating...');

		// Quick validation check using the has-mapping endpoint
		$.ajax({
			url: 'subclass/' + subclassId + '/has-mapping',
			type: 'GET',
			success: function (validationResult) {
				if (!validationResult.hasRevenueMapping) {
					Swal.fire({
						title: 'Revenue Mapping Required',
						text: 'This subclass does not have revenue item/GL mappings configured. Please configure the mappings before updating.',
						icon: 'warning',
						confirmButtonText: 'OK'
					});
					$btn.prop('disabled', false).text('Update');
					return;
				}

				// Proceed with update if mapping exists
				$btn.text('Updating...');
				var data = {};
				$currForm.serializeArray().map(function (x) {
					data[x.name] = x.value;
				});
				var url = "createProductSubcl";

				$.post(url, data)
					.done(function () {
						Swal.fire({
							title: 'Success',
							text: 'Record updated successfully',
							icon: 'success'
						});
						currValidator.resetForm();
						$('#prodSubclassList').DataTable().ajax.reload();
						$('#editProdSubclassModal').modal('hide');
					})
					.fail(function (jqXHR, textStatus, errorThrown) {
						Swal.fire({
							title: 'Error',
							text: jqXHR.responseText,
							icon: 'error'
						});
					})
					.always(function () {
						$btn.prop('disabled', false).text('Update');
					});
			},
			error: function (jqXHR, textStatus, errorThrown) {
				Swal.fire({
					title: 'Validation Error',
					text: 'Failed to validate subclass mapping: ' + jqXHR.responseText,
					icon: 'error'
				});
				$btn.prop('disabled', false).text('Update');
			}
		});
	});
}

var model = {
		prggrp: {
			prgCode:"",
	    }
	};