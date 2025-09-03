//function to display avatar on the screen
function orgDetails(){
	$("#avatar").fileinput({
	    overwriteInitial: true,
	    maxFileSize: 1500,
	    showClose: false,
	    showCaption: false,
	    browseLabel: '',
	    removeLabel: '',
	    browseIcon: '<i class="fa fa-folder-open"></i>',
	    removeIcon: '<i class="fa fa-times"></i>',
	    removeTitle: 'Cancel or reset changes',
	    elErrorContainer: '#kv-avatar-errors',
	    msgErrorClass: 'alert alert-block alert-danger',
	    defaultPreviewContent: '<img src="'+getContextPath()+'/protected/organization/logo"  style="width:160px">',
	    layoutTemplates: {main2: '{preview} ' + ' {remove} {browse}'},
	    allowedFileExtensions: ["jpg", "png", "gif"]
	});
}


function getContextPath() {
	return window.location.pathname.substring(0, window.location.pathname
			.indexOf("/", 2));
}

function openEditBranchModal(button){
	var branch = JSON.parse(decodeURI($(button).data("branch")));	
	$("#brn-code").val(branch["obId"]);
	$("#brn-id").val(branch["obShtDesc"]);
	$("#brn-name").val(branch["obName"]);
	$("#brn-addresss").val(branch["address"]);
	$("#branchRegion").val($("#selOrgReg").val());
	$("#brn-telNumber").val(branch["telNumber"]);
	if(branch['headoffice'] &&  branch['headoffice']==='Y'){
		$("#head-office").prop("checked", true);
	}
	else
		$("#head-office").prop("checked", false);

	//if(branch["branchManager"]){
	    $("#userCod").val(branch["userCode"]);
	    $("#username").val(branch["username"]);
	//}
	
	if($("#branch-user").filter("div").html() != undefined)
	  { 
	  Select2Builder.initAjaxSelect2({
          containerId : "branch-user",
          sort : 'username',
          change: function(e, a, v){
			  $("#userCod").val(e.added.id);
          },
          formatResult : function(a)
          {
          	return a.username
          },
          formatSelection : function(a)
          {
          	return a.username
          },
          initSelection: function (element, callback) {
        	  var usercode;
        	  var username;
             usercode = branch["userCode"];
             username = branch["username"];
            model.organization.user.id = usercode;
            var data = {username:username,id:usercode};
            callback(data);
         },
          id: "id",
          placeholder:"Select Branch Manager"
      });
	  }
	
	$('#branchModal').modal('show');
	
}


function openEditRegionModal(button){
	var branch = JSON.parse(decodeURI($(button).data("region")));	
	$("#reg-id").val(branch["shtDesc"]);
	$("#reg-name").val(branch["regDesc"]);
	if(branch["regWef"])
	$("#reg-wef").val(moment(branch["regWef"]).format('MM/DD/YYYY'));
	if(branch["regWet"])
	$("#reg-wet").val(moment(branch["regWet"]).format('MM/DD/YYYY'));
	$("#reg-code").val(branch["regCode"]);
	$('#regModal').modal('show');
}


function confirmBranchDelete(button){
	var branch = JSON.parse(decodeURI($(button).data("branch")));
	bootbox.confirm("Are you sure want to delete "+branch["obName"]+"?", function(result) {
	      if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteBranch/' + branch["obId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#confirm-delete').modal('hide');
			        	$('#orgBranches').DataTable().ajax.reload();
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


function confirmRegionDelete(button){
	var branch = JSON.parse(decodeURI($(button).data("region")));
	bootbox.confirm("Are you sure want to delete "+branch["regDesc"]+"?", function(result) {
	      if(result){
			 $.ajax({
			        type: 'GET',
			        url:  'deleteRegion/' + branch["regCode"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#orgRegion').DataTable().ajax.reload();
			        },
			        error: function(jqXHR, textStatus, errorThrown) {
			        	Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
			        	//bootbox.alert(jqXHR.responseText, function() {
			        		
			        	//});
			        }
			    });
	      }
	});
	
}

function createRegionTable(){
	var regionsUrl = "regions/0";
	  if ($("#orgCodepk").val() != ''){
		  regionsUrl = "regions/"+$("#orgCodepk").val();
		}
	  var regionTable = $('#orgRegion').DataTable( {
			"processing": true,
			"serverSide": true,
			autoWidth: true,
			"ajax": regionsUrl,
			lengthMenu: [ [5, 10, 15], [5, 10, 15] ],
			pageLength: 5,
			destroy: true,
			"columns": [
				{ "data": "shtDesc" },
				{ "data": "regDesc" },
				{ "data": "regWef" },
				{ "data": "regWet" },
				{ 
					"data": "regCode",
					"render": function ( data, type, full, meta ) {
						if(action =="A"){
							return '<button class="btn btn-success btn btn-info btn-sm" type="button" data-region='+encodeURI(JSON.stringify(full)) + '  onclick="openEditRegionModal(this);"><i class="fa fa-pencil-square-o"></button>';
						}
						else{
							return '<button class="btn btn-success btn btn-info btn-sm" type="button" data-region='+encodeURI(JSON.stringify(full)) + '   onclick="openEditRegionModal(this);" disabled><i class="fa fa-pencil-square-o"></button>';
						}
						
					}

				},
				{ 
					"data": "regCode",
					"render": function ( data, type, full, meta ) {
						if(action =="A"){
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-region='+encodeURI(JSON.stringify(full)) + '  onclick="confirmRegionDelete(this);"><i class="fa fa-trash-o"></button>';
						}
						else{
							return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-region='+encodeURI(JSON.stringify(full)) + ' onclick="confirmRegionDelete(this);" disabled><i class="fa fa-trash-o"></button>';
								
						}
					}

				},
			]
		} );
	  
	  $('#orgRegion tbody').on( 'click', 'tr', function () {
			 
			 $(this).addClass('table-primary').siblings().removeClass('table-primary');
			
			  var aData = regionTable.rows('.table-primary').data();
			  $("#selOrgReg").val(aData[0].regCode);
			  createBranchTable();
			  if($("#selOrgReg"))
					$("#branchRegion").val($("#selOrgReg").val());
		    
	  } );
	  
	  
	 
	  
	  return regionTable;
}


function createBranchTable(){
	var branchesUrl = "branches/0";
	  if ($("#selOrgReg").val() != ''){
		  branchesUrl = "branches/"+$("#selOrgReg").val();
		}
	  var branchTable = $('#orgBranches').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": branchesUrl,
			lengthMenu: [ [5, 10, 15], [5, 10, 15] ],
			pageLength: 5,
			autoWidth: true,
			destroy: true,
			"columns": [
				{ "data": "obShtDesc" },
				{ "data": "obName" },
				{ "data": "address" },
				{ "data": "telNumber" },
				{ "data": "userCode",
				  "render": function ( data, type, full, meta ) {
						  return full.username;
					}
				},
				{ "data": "headoffice",
					"render": function ( data, type, full, meta ) {
					if(full.headoffice && full.headoffice==='Y'){
						return "Yes";
					}
					else return "No";
					}
				},
				{ 
					"data": "obId",
					"render": function ( data, type, full, meta ) {
						if(action =="A"){
						return '<button class="btn btn-success btn btn-info btn-sm" type="button" data-branch='+encodeURI(JSON.stringify(full)) + ' onclick="openEditBranchModal(this);"><i class="fa fa-pencil-square-o"></button>';
						}
						else{
							return '<button class="btn btn-success btn btn-info btn-sm" type="button" data-branch='+encodeURI(JSON.stringify(full)) + ' onclick="openEditBranchModal(this);" disabled><i class="fa fa-pencil-square-o"></button>';
								
						}
					}

				},
				{ 
					"data": "obId",
					"render": function ( data, type, full, meta ) {
						if(action =="A"){
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-branch='+encodeURI(JSON.stringify(full)) + '  onclick="confirmBranchDelete(this);"><i class="fa fa-trash-o"></button>';
						}
						else{
							return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-branch='+encodeURI(JSON.stringify(full)) + ' onclick="confirmBranchDelete(this);" disabled><i class="fa fa-trash-o"></button>';
								
						}
						
					}

				},
			]
		} );
	  return branchTable;
}

$(function(){

	$(document).ready(function() {
		

		  orgDetails();
		  var dataTableUrl = "currencies";
		  var usersTable = $('#currencies').DataTable( {
				"processing": true,
				"serverSide": true,
				"ajax": dataTableUrl,
				"columns": [
					{ "data": "curIsoCode" },
					{ "data": "curName" },
					{
						"data": "curCode",
						 "render": function ( data, type, full, meta ) {
								return '<a href="javascript:void(0)" data-currency='+encodeURI(JSON.stringify(full)) + ' onclick="selectCurrency(this);">Select</a>';
					    }

					},
				]
			} );
		  
		  
		  $(".datepicker-input").each(function() {
			    $(this).datetimepicker({
                    format: 'DD/MM/YYYY'
                });
			    
			});
		  
		  createRegionTable();
		  
		  
		   createBranchTable();
		   
		   
		   
		   var $regForm = $('#reg-form');
		   var regvalidator = $regForm.validate();
		   $('#regModal').on('hidden.bs.modal', function () {
			   regvalidator.resetForm();
			   $(".errordiv").hide();
			   $('#reg-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
				if($("#orgCodepk"))
					$("#regOrgCode").val($("#orgCodepk").val());
	        });
		   
		   $('#saveRegionBtn').click(function(){
				if (!$regForm.valid()) {
					return;
				}
				var $btn = $(this).button('Saving');
				if($("#reg-wet")){
					if(Date.parse(from) > Date.parse(to)){
						 $(".errordiv").show();
						 $(".errordiv").html("Wef Date cannot be greater than Wet Date");
					}
					else{
						 $(".errordiv").hide();
					}
				}
				else{
					$("#reg-wet").val(null);
				}
				var data = {};
				$regForm.serializeArray().map(function(x){data[x.name] = x.value;});
				var from = $("#reg-wef").val();
				var to = $("#reg-wet").val();
              
				
				var url = "createRegion";
	            var request = $.post(url, data );
				request.success(function(){
					$('#orgRegion').DataTable().ajax.reload();
					branchvalidator.resetForm();
					$('#reg-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
					$('#regModal').modal('hide');
				});

				request.error(function(jqXHR, textStatus, errorThrown){
					$(".errordiv").html(jqXHR.responseText);
					$(".errordiv").show();
				});
				request.always(function(){
					$btn.button('reset');
	            });
			});
		   
		   
		  
		  
		  var $branchForm = $('#branch-form');
		  var branchvalidator = $branchForm.validate();
		  $('#branchModal').on('hidden.bs.modal', function () {
			    branchvalidator.resetForm();
			    $("#errorDiv").hide();
				$('#branch-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=checkbox], textarea").val("");
				$('#branch-user').select2('val', null);
				if($("#selOrgReg"))
					$("#branchRegion").val($("#selOrgReg").val());
	        });
		  
		  $('#saveBranchBtn').click(function(){
				if (!$branchForm.valid()) {
					return;
				}
				var $btn = $(this).button('Saving');
				var data = {};
				$branchForm.serializeArray().map(function(x){data[x.name] = x.value;});
				var url = "createRegionBranch";
	            var request = $.post(url, data );
				request.success(function(){
					$('#orgBranches').DataTable().ajax.reload();
					branchvalidator.resetForm();
					$('#branch-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
					$('#branchModal').modal('hide');
					$('#branch-user').select2('val', null);
				});

				request.error(function(jqXHR, textStatus, errorThrown){
					$("#errorId").html(jqXHR.responseText);
					$("#errorDiv").show();
				});
				request.always(function(){
					$btn.button('reset');
	            });
			});
		  
		  
		 
		  
		  var $reportForm= $('#report-form');
		  
		  $('#printBtn').click(function(){
			  console.log("here....");
			     var data = {};
				$reportForm.serializeArray().map(function(x){data[x.name] = x.value;});
				var url = "generateReport";
	            var request = $.post(url, data );
	            request.success(function(){
	            	console.log("Report printed successfully");
	            });
	            
	            request.error(function(jqXHR, textStatus, errorThrown){
					console.log(jqXHR.responseText);
				//	$("#errorbankDiv").show();
				});
				request.always(function(){
	            });
		  });
		  
		  //01-March Update
		  rivets.bind($("#organization_model"), model);
		  //10-March Update
		  if($("#orgCodepk"))
				$("#regOrgCode").val($("#orgCodepk").val());
		  
		  
		  if($("#country").filter("div").html() != undefined)
		  {
			  Select2Builder.initAjaxSelect2({
		            containerId : "country",
		            sort : 'couName',
		            change: countryChanged,
		            formatResult : function(a)
		            {
		            	return a.couName
		            },
		            formatSelection : function(a)
		            {
		            	return a.couName
		            },
		            initSelection: function (element, callback) {
	                      var countryCode = $("#countryCode").val();
	                      var countryName = $("#countryName").val();
		            	 model.organization.country.couCode = countryCode;
		            	 model.organization.country.county.countyId = -2000;
		            	var data = {couName:countryName,couCode:countryCode};
	                    callback(data);
	                }, 
		            id: "couCode",
		            placeholder:"Select Country"
		        });
		  }
		  
		  
		 
		  if($("#currency").filter("div").html() != undefined)
		  {
			  Select2Builder.initAjaxSelect2({
		            containerId : "currency",
		            sort : 'curName',
		            change: currencyChanged,
		            formatResult : function(a)
		            {
		            	return a.curName
		            },
		            formatSelection : function(a)
		            {
		            	return a.curName
		            },
		            initSelection: function (element, callback) {
	                  var currCode = $("#txtCurrencyCode").val();
	                  var currName = $("#txtCurrency").val();
		              model.organization.currency.curCode = currCode;
		              var data = {curName:currName,curCode:currCode};
	                  callback(data);
	                },
		            id: "curCode",
		            placeholder:"Select Currency"
		        });
		  }
			  
			  
	
		  if($("#branch-user").filter("div").html() != undefined)
		  { 
		  Select2Builder.initAjaxSelect2({
	            containerId : "branch-user",
	            sort : 'username',
	            change: userChanged,
	            formatResult : function(a)
	            {
	            	return a.username
	            },
	            formatSelection : function(a)
	            {
	            	return a.username
	            },
	            initSelection: function (element, callback) {
	            	
                var usercode = $("#userCod").val();
                var username = $("#username").val();
	              var data = {username:username,id:usercode};
                  callback(data);
               },
	            id: "id",
	            placeholder:"Select Branch User"
	        });
		  }
		  
		  
		  function currencyChanged(e, a, v) {
	            model.organization.currency = e.added || {};
	      }
		  function userChanged(e, a, v) {
	            $("#userCod").val(e.added.id);
	        }
		  function countryChanged(e, a, v) {
	            model.organization.country = e.added || {};
	           // countyChanged(e, a, v);
	        }
		  function countyChanged(e, a, v) {
	            model.organization.country.county = e.added || {};
	        }
		  function townChanged(e, a, v) {
			  $("#zipCode").val(e.added.ctPostalCode);
	            model.organization.country.county.town = e.added || {};
	        }
	});
	
	
});

var model = {
		organization: {
			country:{
				couCode:"",
				county:{
					countyId:"",
					town:{
						ctCode:"",
					}
				}
			},
			currency:{
				curCode:"",
			},
			user:{
				id:"",
			}
	    }
	};
