
$(function(){

	$(document).ready(function() {
		$(document).ajaxStart(function () {
			$("#saveSourceGroupBtn,#saveSourceBtn").attr("disabled", true);
		});
		$(document).ajaxComplete(function () {
			$("#saveSourceGroupBtn,#saveSourceBtn").attr("disabled", false);
		});
		createCountryTable();
		createBusinessGroupTable();
		saveUpdateCountry();
		makeCountrySelection();
		saveUpdateCounty();
		callCountyModal();
		callTownModal();
		saveUpdateTown();
		callPostalModal();
		savePostalCodes();
		saveUpdateSourceGroup();
		callSourceModal();
		makeSourceGroupSelection();
		saveUpdateSource();
	});
});

function callCountyModal(){
     $("#btn-add-county").on('click', function(){
    	 $("#country-code").val($("#country-pk-code").val());
    	 $('#countyModal').modal('show');
     });
}




function callTownModal(){
    $("#btn-add-town").on('click', function(){
   	 $("#count-code").val($("#county-pk-code").val());
   	 $('#townModal').modal('show');
    });
}

function makeCountrySelection(){
	var table = $('#countryList').DataTable();
	$('#countryList tbody').on( 'click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = table.rows('.table-primary').data();
		  $("#country-code").val(aData[0].couCode);
		  $("#country-pk-code").val(aData[0].couCode);
		  createCountyTable();
		  makeCountySelection();
    } );
}

function makeCountySelection(){
	var myTable = $('#countyList').DataTable();
	$('#countyList tbody').on( 'click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = myTable.rows('.table-primary').data();
		  $("#count-code").val(aData[0].countyId);
		  $("#county-pk-code").val(aData[0].countyId);
		  createTownTable();
    } );
}


function saveUpdateCountry(){
	var $currForm = $('#country-form');
	var currValidator = $currForm.validate();
	 $('#countryModal').on('hidden.bs.modal', function () {
		 currValidator.resetForm();
		 $('#country-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
     });
	
	$('#saveCountryBtn').click(function(){
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createCountry";
        var request = $.post(url, data );
        request.success(function(){
        	 Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
             });
			$('#countryList').DataTable().ajax.reload();
			currValidator.resetForm();
			$('#country-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
			$('#countryModal').modal('hide');
        });
        request.error(function(jqXHR, textStatus, errorThrown){
        	bootbox.alert(jqXHR.responseText);
		});
		request.always(function(){
			$btn.button('reset');
        });
		
	});
}


function saveUpdateCounty(){
	var $currForm = $('#county-form');
	var currValidator = $currForm.validate();
	 $('#countyModal').on('hidden.bs.modal', function () {
		 currValidator.resetForm();
		 $('#county-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
	 });
	
	$('#saveCountyBtn').click(function(){
		if($("#country-code").val() === ''){
			bootbox.alert("Select Country to add County");
			$('#countyModal').modal('hide');
		}
		else{
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createCounty";
        var request = $.post(url, data );
        request.success(function(){
        	 Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
			$('#countyList').DataTable().ajax.reload();
			currValidator.resetForm();
			$('#county-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
			$('#countyModal').modal('hide');
        });
        request.error(function(jqXHR, textStatus, errorThrown){
        	bootbox.alert(jqXHR.responseText);
		});
		request.always(function(){
			$btn.button('reset');
        });
		}
	});
}


function saveUpdateSourceGroup(){
	var $currForm = $('#sourcegroup-form');
	var currValidator = $currForm.validate();
	$('#businessGroupModal').on('hidden.bs.modal', function () {
		currValidator.resetForm();
		$('#sourcegroup-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
	});

	$('#saveSourceGroupBtn').click(function(){
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createSourceGroup";
		var request = $.post(url, data );
		request.success(function(){
			Swal.fire({
					title: 'Success',
					text: 'Record created/updated Successfully',
					icon: 'success'
				});
			$('#groupList').DataTable().ajax.reload();
			currValidator.resetForm();
			$('#sourcegroup-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
			$('#businessGroupModal').modal('hide');
		});
		request.error(function(jqXHR, textStatus, errorThrown){
			bootbox.alert(jqXHR.responseText);
		});
		request.always(function(){
			$btn.button('reset');
		});

	});
}



function saveUpdateSource(){
	var $currForm = $('#source-form');
	var currValidator = $currForm.validate();
	$('#sourceModal').on('hidden.bs.modal', function () {
		currValidator.resetForm();
		$('#source-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
	});

	$('#saveSourceBtn').click(function(){
		if($("#srcGroup-Id-code").val() === ''){
			bootbox.alert("Select Group to add Source");
			$('#sourceModal').modal('hide');
		}
		else{
			if (!$currForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createBusinessSource";
			var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#sourceList').DataTable().ajax.reload();
				currValidator.resetForm();
				$('#source-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
				$('#sourceModal').modal('hide');
			});
			request.error(function(jqXHR, textStatus, errorThrown){
				bootbox.alert(jqXHR.responseText);
			});
			request.always(function(){
				$btn.button('reset');
			});
		}
	});
}


function makeSourceGroupSelection(){
	var table = $('#groupList').DataTable();
	$('#groupList tbody').on( 'click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = table.rows('.table-primary').data();
		$("#srcGroup-Id-code").val(aData[0].srcGroupId);
		$("#sourcegrp-pk-code").val(aData[0].srcGroupId);
		createBusinessSourceTable();
	} );
}

function editSourceGroup(button){
	var sourcegroup = JSON.parse(decodeURI($(button).data("sourcegroup")));
	$("#srcGroup-Id").val(sourcegroup["srcGroupId"]);
	$("#group-shtDesc").val(sourcegroup["shtDesc"]);
	$("#desc-name").val(sourcegroup["desc"]);;
	$('#businessGroupModal').modal('show');
}

function callSourceModal(){
	$("#btn-add-source").on('click', function(){
		$("#srcGroup-Id-code").val($("#sourcegrp-pk-code").val());
		$('#sourceModal').modal('show');
	});
}

function saveUpdateTown(){
	var $currForm = $('#town-form');
	var currValidator = $currForm.validate();
	 $('#townModal').on('hidden.bs.modal', function () {
		 currValidator.resetForm();
		 $('#town-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
	 });
	
	$('#saveTownBtn').click(function(){
		if($("#count-code").val() === ''){
			bootbox.alert("Select County to add Town");
			$('#townModal').modal('hide');
		}
		else{
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createTown";
        var request = $.post(url, data );
        request.success(function(){
        	 Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
			$('#townList').DataTable().ajax.reload();
			currValidator.resetForm();
			$('#county-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
			$('#townModal').modal('hide');
        });
        request.error(function(jqXHR, textStatus, errorThrown){
        	bootbox.alert(jqXHR.responseText);
		});
		request.always(function(){
			$btn.button('reset');
        });
		}
	});
}


function createBusinessGroupTable(){
	var url = "SourceGroups";
	var currTable = $('#groupList').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": url,
		lengthMenu: [ [5, 10], [5, 10] ],
		pageLength: 5,
		destroy: true,
		"columns": [
			{ "data": "shtDesc" },
			{ "data": "desc" },
			{
				"data": "srcGroupId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-sourcegroup='+encodeURI(JSON.stringify(full)) + '  onclick="editSourceGroup(this);"><i class="fa fa-pencil-square-o"></button>';
				}

			},
			{
				"data": "srcGroupId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-sourcegroup='+encodeURI(JSON.stringify(full)) + ' onclick="confirmSourceGrpDelete(this);"><i class="fa fa-trash-o"></button>';
				}

			},
		]
	} );
	return currTable;
}

function createBusinessSourceTable(){
	var url = "BusinessSources/0";
	if($("#srcGroup-Id-code").val() != ''){
		url = "BusinessSources/"+$("#srcGroup-Id-code").val();
	}
	var currTable = $('#sourceList').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": url,
		lengthMenu: [ [5, 10], [5, 10] ],
		pageLength: 5,
		destroy: true,
		"columns": [
			{ "data": "shtDesc" },
			{ "data": "desc" },
			{
				"data": "srcId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-businesssource='+encodeURI(JSON.stringify(full)) + '  onclick="editBusinessSource(this);"><i class="fa fa-pencil-square-o"></button>';
				}

			},
			{
				"data": "srcId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-businesssource='+encodeURI(JSON.stringify(full)) + ' onclick="confirmSourceDelete(this);"><i class="fa fa-trash-o"></button>';
				}

			},
		]
	} );
	return currTable;
}



function createCountryTable(){
	var url = "allCountries";
	  var currTable = $('#countryList').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [5, 10], [5, 10] ],
			pageLength: 5,
			destroy: true,
			"columns": [
				{ "data": "couShtDesc" },
				{ "data": "couName" },
				{ "data": "prefix" },
				{ 
					"data": "couCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-country='+encodeURI(JSON.stringify(full)) + '  onclick="editCountry(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "couCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-country='+encodeURI(JSON.stringify(full)) + ' onclick="confirmCountryDelete(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}


function confirmCountryDelete(button){
	var country = JSON.parse(decodeURI($(button).data("country")));
	bootbox.confirm("Are you sure want to delete "+country["couName"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteCountry/' + country["couCode"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#countryList').DataTable().ajax.reload();
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


function confirmSourceGrpDelete(button){
	var sourcegroup = JSON.parse(decodeURI($(button).data("sourcegroup")));
	bootbox.confirm("Are you sure want to delete "+sourcegroup["desc"]+"? This will also delete the sources under this group", function(result) {
		if(result){
			$.ajax({
				type: 'GET',
				url:  'deleteSourceGroup/' + sourcegroup["srcGroupId"],
				dataType: 'json',
				async: true,
				success: function(result) {
					Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
					$('#groupList').DataTable().ajax.reload();
					$('#sourceList').DataTable().ajax.reload();
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


function confirmSourceDelete(button){
	var businesssources = JSON.parse(decodeURI($(button).data("businesssource")));
	bootbox.confirm("Are you sure want to delete "+businesssources["desc"]+"?", function(result) {
		if(result){
			$.ajax({
				type: 'GET',
				url:  'deleteBusinessSource/' + businesssources["srcId"],
				dataType: 'json',
				async: true,
				success: function(result) {
					Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
					$('#sourceList').DataTable().ajax.reload();
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
function editCountry(button){
	var country = JSON.parse(decodeURI($(button).data("country")));
	$("#cou-code").val(country["couCode"]);
	$("#country-id").val(country["couShtDesc"]);
	$("#cou-name").val(country["couName"]);
	$("#cou-prefix").val(country["prefix"]);
	$('#countryModal').modal('show');
}

function editCounty(button){
	var country = JSON.parse(decodeURI($(button).data("county")));
	$("#county-code").val(country["countyId"]);
	$("#country-code").val($("#country-pk-code").val());
	$("#county-id").val(country["countyCode"]);
	$("#county-name").val(country["countyName"]);
	$('#countyModal').modal('show');
}

function editTown(button){
	var country = JSON.parse(decodeURI($(button).data("town")));
	$("#town-code").val(country["ctCode"]);
	$("#town-id").val(country["ctShtDesc"]);
	$("#town-name").val(country["ctName"]);
	//$("#town-postal-code").val(country["ctPostalCode"]);
	$("#count-code").val($("#county-pk-code").val());
	$('#townModal').modal('show');
}

function confirmTownDelete(button){
	var country = JSON.parse(decodeURI($(button).data("town")));
	bootbox.confirm("Are you sure want to delete "+country["ctName"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteTown/' + country["ctCode"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#townList').DataTable().ajax.reload();
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


function confirmCountyDelete(button){
	var country = JSON.parse(decodeURI($(button).data("county")));
	bootbox.confirm("Are you sure want to delete "+country["countyName"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteCounty/' + country["countyId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#countyList').DataTable().ajax.reload();
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



function createCountyTable(){
	var url = "allCounties/0";
	if($("#country-code").val() != ''){
		url = "allCounties/"+$("#country-code").val();
	}
	  var currTable = $('#countyList').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			destroy: true,
			lengthMenu: [ [5, 10], [5, 10] ],
			pageLength: 5,
			destroy: true,
			"columns": [
				{ "data": "countyCode" },
				{ "data": "countyName" },
				{ 
					"data": "countyId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-county='+encodeURI(JSON.stringify(full)) + '  onclick="editCounty(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "countyId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-county='+encodeURI(JSON.stringify(full)) + '  onclick="confirmCountyDelete(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}

function createTownTable(){
	var url = "allTowns/0";
	if($("#count-code").val() != ''){
		url = "allTowns/"+$("#count-code").val();
	}
	  var currTable = $('#townList').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			destroy: true,
			lengthMenu: [ [5, 10], [5, 10] ],
			pageLength: 5,
			destroy: true,
			"columns": [
				{ "data": "ctShtDesc" },
				{ "data": "ctName" },
				{ 
					"data": "ctCode",
					"render": function ( data, type, full, meta ) {
						return '<a href="#" class="btn btn-link" onclick="showPostalModal('+full.ctCode+')">View Postal Codes</a>';
					}

				},
				{ 
					"data": "ctCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-town='+encodeURI(JSON.stringify(full)) + ' onclick="editTown(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "ctCode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-town='+encodeURI(JSON.stringify(full)) + ' onclick="confirmTownDelete(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}


function showPostalModal(townCode){
	createPostalCodes(townCode);
	$("#town-postal-pk-code").val(townCode);
	$('#postalListModal').modal('show');
}


function createPostalCodes(townCode){
		url = "postalcodes/"+townCode;
	
	  var currTable = $('#postalCodesList').DataTable( {
			"processing": true,
			"serverSide": true,
			"sDom": "<'row'<'span8'l><'span12'f>r>t<'row'<'span6'i><'span6'p>>",
			"ajax": url,
			destroy: true,
			lengthMenu: [ [5, 10], [5, 10] ],
			pageLength: 5,
			destroy: true,
			autoWidth: true,
			"columns": [
				{ "data": "postalName" },
				{ "data": "zipCode" },
				{ 
					"data": "pcode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-postalcodes='+encodeURI(JSON.stringify(full)) + ' onclick="editPostalCodes(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "pcode",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-postalcodes='+encodeURI(JSON.stringify(full)) + ' onclick="confirmPostalDel(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}

function editPostalCodes(button){
	var postal = JSON.parse(decodeURI($(button).data("postalcodes")));
	$("#ps-code").val(postal["pcode"]);
	$("#postal-town-code").val(postal["town"].ctCode);
	$("#postal-name").val(postal["postalName"]);
	$("#postal-code").val(postal["zipCode"]);
	$('#savePostalModal').modal('show');
}

function confirmPostalDel(button){
	var postal = JSON.parse(decodeURI($(button).data("postalcodes")));
	bootbox.confirm("Are you sure want to delete "+postal["postalName"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deletePostalCode/' + postal["pcode"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#postalCodesList').DataTable().ajax.reload();
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
	

function callPostalModal(){
    $("#btn-add-postal-code").on('click', function(){
     	$('#postal-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
   	 $("#postal-town-code").val($("#town-postal-pk-code").val());
  	$('#savePostalModal').modal({
		  backdrop: 'static',
		  keyboard: true
		});
    });
}

function editBusinessSource(button){
	var sources = JSON.parse(decodeURI($(button).data("businesssource")));
	$("#src-Id").val(sources["srcId"]);
	$("#srcGroup-Id-code").val($("#sourcegrp-pk-code").val());
	$("#source-shtDesc").val(sources["shtDesc"]);
	$("#source-desc").val(sources["desc"]);
	$('#sourceModal').modal('show');
}

function savePostalCodes(){
	var $currForm = $('#postal-form');
	var currValidator = $currForm.validate();
	$('#savePostalCodeBtn').click(function(){
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createPostalCode";
        var request = $.post(url, data );
        request.success(function(){
        	 Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
			$('#postalCodesList').DataTable().ajax.reload();
			currValidator.resetForm();
			$('#postal-form').find("input[type=text],input[type=mobileNumber],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
			$('#savePostalModal').modal('hide');
			
		});
        request.error(function(jqXHR, textStatus, errorThrown){
        	bootbox.alert(jqXHR.responseText);
		});
		request.always(function(){
			$btn.button('reset');
        });
	});
}



	