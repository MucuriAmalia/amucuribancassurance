$(function(){

	$(document).ready(function() {
		
		$.ajaxSetup({
		    cache: false
		});

		$('#mBusiness').attr('checked','checked');
		loadrenewalsModal();
		
		confirmSelectedRenewal();
		
		checkExistingTrans();

		populateClientLov();

		createAccountsForSel();
	});
});


function checkExistingTrans(){
	 $.ajax({
				        type: 'GET',
				        url:  'countUnauthPolicies',
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

function loadrenewalsModal(){
	$("#btn-show-search-pol").on('click', function(){
		createRenewalTrans();
		$('#renPoliciesModal').modal({
			  backdrop: 'static',
			  keyboard: true
			})
	});
	
	$("#btn-search-policies").on('click', function(){
		createRenewalTrans();
		
	});
	
	
}

function confirmSelectedRenewal(){
	   $("#selectauthtrans").on('click', function(){
		   if ($("#pol-number").val() != ''){
			   $('#renPoliciesModal').modal('hide');
		   }
		   else{
			   bootbox.alert('Select a Policy to continue');
		   }
	   });
	}


function populateClientLov(){
	if($("#client-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "client-frm",
			sort : 'fname',
			change: function(e, a, v){
				$("#rev-search-name").val(e.added.tenId);

			},
			formatResult : function(a)
			{
				return a.fname+" "+a.otherNames;
			},
			formatSelection : function(a)
			{
				return a.fname+" "+a.otherNames;
			},
			initSelection: function (element, callback) {

			},
			id: "tenId",
			placeholder:"Select Client"

		});
	}

	$("#client-frm").on("select2-removed", function(e) {
		$("#rev-search-name").val('');
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
			placeholder: "Select Insurance Company",
		});
	}
}


function createRenewalTrans(){
	var url = "renewalPolicies";
	  var currTable = $('#revtranstbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": {
				'url': url,
				'data':{
					'clientName': $("#rev-search-name").val(),
					'policyNo':  $("#pol-search-number").val(),
					'agent':  $("#agent-search-number").val(),
					'endorseNumber':  $("#rev-search-number").val(),
					'refno': $("#dr-search-number").val(),
					'businessType': 'G',
				},
			},
			autoWidth: true,
			lengthMenu: [ [10], [10] ],
			pageLength: 10,
			destroy: true,
			searching: false,
			"columns": [
				{ "data": "policyNo" },
				{ "data": "polRevNo" },
				{ "data": "policyId",
					   "render": function ( data, type, full, meta ) {
					console.log(full);
						      return full.clientName;
						  }
				},
				{ "data": "policyId",
					   "render": function ( data, type, full, meta ) {
						      return full.agentName;
						  }
				}
				
			]
		} );
	  
	  $('#revtranstbl tbody').on( 'click', 'tr', function () {
			 
			 $(this).addClass('table-primary').siblings().removeClass('table-primary');
			
			 var d = currTable.row( this ).data();
			 if(d){
				 $("#rev-pol-id").val(d.policyId);
				 $("#rev-buss-type").val(d.businessType || 'G');
				 $("#pol-number").val(d.policyNo);

				 $.ajax({
				        type: 'GET',
				        url:  'countUnauthPolicies',
				        dataType: 'json',
				        data: {"policyNumber": $("#pol-number").val()},
				        async: true,
				        success: function(result) {
				        
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
			   
		    
	  } );
	 
	  return currTable;
}


function createUnauthPolicies(){
	var url = "unauthPolicies";
	  var currTable = $('#poltrans').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": {
				'url': url,
				'data':{
					'policyNumber': $("#pol-number").val(),
				},
			},
			autoWidth: true,
			lengthMenu: [ [10], [10] ],
			pageLength: 10,
			destroy: true,
			searching: false,
			"columns": [
				{ "data": "polNo",
					"render": function ( data, type, full, meta ) {
						if(full.polNo){
							return full.polNo;
						}
						else{
							return full.proposalNo;
						}
					}
				},
				{ "data": "polCreateddt",
					"render": function ( data, type, full, meta ) {
						return moment(full.polCreateddt).format('DD/MM/YYYY');
					}
				},
				{ "data": "wef",
					"render": function ( data, type, full, meta ) {
						return moment(full.wef).format('DD/MM/YYYY');
					}
				},
				{ "data": "wet",
					"render": function ( data, type, full, meta ) {
						return moment(full.wet).format('DD/MM/YYYY');
					}
				},
				{ "data": "transactionId",
					"render": function ( data, type, full, meta ) {
						return full.user;
					}
				},

				{ "data": "currentStatus",
					"render": function ( data, type, full, meta ) {
						if(full.currentStatus ==='D') return "Draft";
						else  if(full.currentStatus ==='NT') return "New Transaction";
						else  if(full.currentStatus ==='RV') return "Revision Invoice";
					}
				},
				{ 
					"data": "transactionId",
					"render": function ( data, type, full, meta ) {
						if(full.status==="A"){
							return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value='+full.transactionId+'><input type="submit"  class="btn btn-success" value="View" ></form>';
							
						}else
						return '<form action="edituwtrans" method="post"><input type="hidden" name="id" value='+full.transactionId+'><input type="submit"  class="btn btn-success" value="Edit" ></form>';
						
					 }

				},
				{ 
					"data": "transactionId",
					"render": function ( data, type, full, meta ) {
						return '<input type="button" class="btn btn-danger" data-policy='+encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="confirmPolicyDelete(this);"/>';
					}

				},
				
				
			]
		} );
	  return currTable;
}

function confirmPolicyDelete(button){
	var policy = JSON.parse(decodeURI($(button).data("policy")));
	bootbox.confirm("Are you sure want to delete "+policy["polNo"]+"?", function(result) {
		 if(result){
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