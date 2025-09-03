$(function(){

	$(document).ready(function() {

	    uwBranchCertLots();
	    addCertType();
	    saveBranchCertLots();
	    deallocateWithRemarks();
	    allocateForPrint();
		uwCertLots();
		addCertTypes();
		saveCertLots();

	    $('#cert-no-of-certs,#last-printed-cert').on('keypress', function(e){
          return e.metaKey || // cmd/ctrl
            e.which <= 0 || // arrow keys
            e.which == 8 || // delete key
            /[0-9]/.test(String.fromCharCode(e.which)); // numbers
        })

	    $("#cert-no-of-certs").on('input', function(){
			if($("#cert-no-from").val()!==''){
				var total = parseInt($(this).val())+parseInt($("#cert-no-from").val()-1);
				$("#cert-no-to").val( total);
			}
		})

		$("#cert-no-from").on('input', function(){
			if($("#cert-no-from").val()!==''){
				var total = parseInt($("#cert-no-to").val())-parseInt($("#cert-no-from").val()-1);
				$("#cert-no-of-certs").val( total);
			}
		})

	});

});

function addCertType(){
	$("#btn-add-brn-certs").click(function(){
		$('#cert-no-from').prop('readonly', true);
		$('#cert-no-of-certs').prop('readonly', false);
		$("#branch-def").select2("enable", true);
		$("#brncertlotsModalLabel").text("Allocate Certificates to Branch and User");
		if ($("#selected-cert-pk").val() != '') {
			$('#brn-cert-lots-form').find("input[type=text],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");

			$("#cert-type-pk").val($("#selected-cert-pk").val());
			$("#cert-no-from").val($("#cert-available-from").val());
			$("#reassigning").val("N");
			//createCertLotsLov();
			createBranchLov();
			createUserLov();
			$('#brncertlotsModal').modal('show');
		}
		else {
			bootbox.alert('Select certificate lot to allocate');
		}
	})
}

function reassigncerts(button){

	var certlots = JSON.parse(decodeURI($(button).data("certlots")));
	if(certlots["noTo"]===certlots["lastPrintedNo"] ){
			bootbox.alert("Cannot Allocate re-assign exhausted certificate lot");
		return;
	}
		$('#brn-cert-lots-form').find("input[type=text],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");

			$("#cert-type-pk").val(certlots["certLots"].cerId);
			if (certlots["lastPrintedNo"]){
				$("#cert-no-from").val(certlots["lastPrintedNo"]);
			}else {
				$("#cert-no-from").val(certlots["noFrom"]);
			}

			$("#cert-no-to").val(certlots["noTo"]);
	        $("#cert-brn-code").val(certlots["branch"].obId);
			$("#cert-brn-name").val(certlots["branch"].obName);
			$("#reassigning").val("Y");
			$("#cert-pk-from").val(certlots["brnCertId"]);
			$("#cert-pk").val("");

	//$("#branch-def").disable();
			$('#cert-no-from').prop('readonly', false);
			$('#cert-no-of-certs').prop('readonly', true);
			var total = parseInt($("#cert-no-to").val())-parseInt($("#cert-no-from").val()-1);
			$("#cert-no-of-certs").val( total);
			//createCertLotsLov();
			createBranchLov();
			if ($('#hasCertAllocRole').val()) {
				$("#branch-def").select2("enable", true);
			}else {
				$("#branch-def").select2("enable", false);
			}
			createUserLov();
			$("#brncertlotsModalLabel").text("Re-Assign Certificates to Branch User ");
			$('#brncertlotsModal').modal('show');
}

function saveBranchCertLots(){
	var $paramForm = $('#brn-cert-lots-form');
	var validator = $paramForm.validate();
	 $('#brncertlotsModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#brn-cert-lots-form').find("input[type=text],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
	 });

	 $('#saveBrnCertLots').click(function(){
		 if (!$paramForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paramForm.serializeArray().map(function(x){data[x.name] = x.value;});
			console.log(data);
		 	var url;
		    if ($("#reassigning").val()==='Y'){
				url = "reassignBranchCerts";
			}else{
				url = "createBranchCerts";
			}

	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });

				$('#uwcertlots').DataTable().ajax.reload();
				$('#brncerts').DataTable().ajax.reload();
				$("#cert-type-pk").val("");
				$("#selected-cert-pk").val("")
				$("#cert-no-from").val("");
				$("#cert-available-from").val("");
				$("#reassigning").val("");
				$("#cert-pk-from").val("");
				validator.resetForm();
				$('#brncertlotsModal').modal('hide');
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

function createUserLov(){
	if($("#user-def").filter("div").html() != undefined)
	{
		  Select2Builder.initAjaxSelect2({
	          containerId : "user-def",
	          sort : 'username',
	          change: function(e, a, v){
	        	  $("#userCod").val(e.added.id);
	          },
	          formatResult : function(a)
	          {
	          	return a.name
	          },
	          formatSelection : function(a)
	          {
	          	return a.name
	          },
	          initSelection: function (element, callback) {
	        	   var code = $("#userCod").val();
                var name = $("#username").val();
	              var data = {name:name,id:code};
                  callback(data);
	          },
	          id: "id",
	          width:"200px",
	          placeholder:"Select User"
	      });

	}
}

function createBranchLov(){
	if($("#branch-def").filter("div").html() != undefined)
	{
		  Select2Builder.initAjaxSelect2({
	          containerId : "branch-def",
	          sort : 'obName',
	          change: function(e, a, v){
	        	  $("#cert-brn-code").val(e.added.obId);
	          },
	          formatResult : function(a)
	          {
	          	return a.obName
	          },
	          formatSelection : function(a)
	          {
	          	return a.obName
	          },
	          initSelection: function (element, callback) {
	        	   var code = $("#cert-brn-code").val();
                var name = $("#cert-brn-name").val();
	              var data = {obName:name,obId:code};
                  callback(data);
	          },
	          id: "obId",
	          width:"200px",
	          placeholder:"Select Branch"
	      });

	}
}

function createCertLotsLov(){
	if($("#cert-lots").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "cert-lots",
			sort : 'noFrom',
			change: function (e, a, v) {
				$("#cert-type-pk").val(e.added.cerId);
				$("#cert-no-from").val(e.added.availableNoFrom);
			},
			formatResult : function(a)
			{
				return a.certLotDesc
			},
			formatSelection : function(a)
			{
				return a.certLotDesc
			},
			initSelection: function (element, callback) {

				var code = $("#cert-type-pk").val();
				var name = $("#cert-type-name").val();
				var data = {certLotDesc:name,cerId:code};
				callback(data);
			},
			id: "cerId",
			placeholder:"Select Cert. Lot"
		});
	}
}





function uwBranchCertLots(lotCode){
	var url = "outsafebranchcerts/"+lotCode;
	  var table = $('#brncerts').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 15], [10, 15] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{
					"data": "certLots",
					"render": function ( data, type, full, meta ) {
						return full.certLots.certTypes.certDesc+"("+full.certLots.noFrom+"-"+full.certLots.noTo+")";
					}

				},
				{
					"data": "user",
					"render": function ( data, type, full, meta ) {
						return full.user.name;
					}

				},
				{
					"data": "branch",
					"render": function ( data, type, full, meta ) {
						return full.branch.obName;
					}

				},
				{ "data": "noFrom"},
				{ "data": "noTo"},
				{ "data": "countCerts"},
				{ "data": "lastPrintedNo"},
				{
					"data": "currentLot",
					"render": function ( data, type, full, meta ) {
						if(full.currentLot){
							if(full.currentLot === "Y") return "Yes";
							else return "No";
						}
						else{
							return "No"
						}
					}

				},
				{
					"data": "status",
					"render": function ( data, type, full, meta ) {
						if(full.status){
							if(full.status === "D") return "Dispatched";
							else if (full.status === "A") return "Acknowledged";
							else return "Pending Dispatch";
						}
						else{
							return "Pending Dispatch"
						}
					}

				},
				{
					"data": "brnCertId",
					"render": function ( data, type, full, meta ) {
						if($('#hasCertAllocRole').val()) {
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="deallocateCerts(this);">Deallocate</button>';
						}else{
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="deallocateCerts(this);"disabled>Deallocate</button>';
						}
						}

				},

				{
                    "data": "brnCertId",
                    "render": function ( data, type, full, meta ) {
						if($('#hasCertAllocRole').val()){
						if( full.currentLot ==="Y") {
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="allocatePrint(this);" disabled>Allocate for Print</button>';
						}else {
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="allocatePrint(this);">Allocate for Print</button>';
						}
					}else {
					return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="allocatePrint(this);">Allocate for Print</button>';
}
						}

                },{
					"data": "brnCertId",
					"render": function ( data, type, full, meta ) {
						console.log(full.showUser)
						if (!full.showUser) {
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="acknowledgeCert(this);"disabled>Acknowledge</button>';
						} else {

						if (full.status) {
							if (full.status === "D")
								return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="acknowledgeCert(this);">Acknowledge</button>';
							else if (full.status === "A") return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="acknowledgeCert(this);"disabled>Acknowledge</button>';
													}
						else
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="acknowledgeCert(this);"disabled>Acknowledge</button>';
					}
					}

				},{
					"data": "brnCertId",
					"render": function ( data, type, full, meta ) {

						if (!full.showUser && !$('#hasCertAllocRole').val()) {
							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="reassigncerts(this);"disabled>Re-Assign </button>';
						} else {

							if (full.status) {
								console.log(full.status +" her "+$('#hasCertAllocRole').val() )
								if (full.status === "A")
									return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="reassigncerts(this);">Re-Assign</button>';
								else {
									if (full.status === "D" && $('#hasCertAllocRole').val()) return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="reassigncerts(this);">Re-Assign</button>';
									else return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="reassigncerts(this);"disabled>Re-Assign</button>';
								}
							}
							else
								return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="reassigncerts(this);"disabled>Re-Assign</button>';
						}
					}

				},
			]
		} );
	  return table;
}

function allocatePrint(button){
  var certlots = JSON.parse(decodeURI($(button).data("certlots")));
  if(certlots["deallocated"]){
     if(certlots["deallocated"]==="Y"){
         bootbox.alert("Cannot Allocate For print deallocated lot..Select another lot");
         return;
     }
  }
  $("#alloc-cert-pk").val(certlots["brnCertId"]);
  $("#last-printed-cert").val(certlots["lastPrintedNo"]);
  $("#alloc-for-print").val(certlots["currentLot"]);
   $('#allocprintModal').modal({
                            		  backdrop: 'static',
                            		  keyboard: true
                            		})
}

function deallocateCerts(button){
	var certlots = JSON.parse(decodeURI($(button).data("certlots")));
	$("#dealloc-cert-pk").val(certlots["brnCertId"]);
	$("#cert-remarks").val("");
		  $('#dealloclotsModal').modal({
                          		  backdrop: 'static',
                          		  keyboard: true
                          		})

}

function deallocateWithRemarks(){
   $("#deallocatecert").on('click',function(){
               $.ajax({
       			        type: 'GET',
       			        url:  'deallocateLot/' + $("#dealloc-cert-pk").val(),
       			        dataType: 'json',
       			        data: {"remarks": $("#cert-remarks").val()},
       			        async: true,
       			        success: function(result) {
							Swal.fire({
								title: 'Success',
								text: 'Record Deallocated Successfully',
								icon: 'success'
							});
       			        	$('#brncerts').DataTable().ajax.reload();
       			        	$('#dealloclotsModal').modal('hide');
       			        },
       			        error: function(jqXHR, textStatus, errorThrown) {
       			        	 Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
       			        }
       			    });
   });
}

function allocateForPrint(){
   $("#allocforprint").on('click',function(){
               $.ajax({
       			        type: 'GET',
       			        url:  'allocateForPrint/' + $("#alloc-cert-pk").val(),
       			        dataType: 'json',
       			        data: {"allocated": $("#alloc-for-print").val(), "lastPrintedCert": $("#last-printed-cert").val()},
       			        async: true,
       			        success: function(result) {
							Swal.fire({
								title: 'Success',
								text: 'Record Deallocated Successfully',
								icon: 'success'
							});
       			        	$('#brncerts').DataTable().ajax.reload();
       			        	$('#allocprintModal').modal('hide');
       			        },
       			        error: function(jqXHR, textStatus, errorThrown) {
       			        	 Swal.fire({
								title: 'Error',
								text: jqXHR.responseText,
								icon: 'error'
							});
       			        }
       			    });
   });
}

function acknowledgeCert(button){
	var certlots = JSON.parse(decodeURI($(button).data("certlots")));
	bootbox.confirm("Are you sure want to acknowledge the selected certificates?", function(result) {
		if(result) {
			// // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
			$.ajax({
				type: 'GET',
				url: 'acknowledgeCert/' + $("#alloc-cert-pk").val(),
				dataType: 'json',
				async: true,
				success: function (result) {
					// $('#myPleaseWait').modal('hide');
					Swal.fire({
						title: 'Success',
						text: 'Certificate Acknowledged and its now available for printing',
						icon: 'success'
					});
					$('#brncerts').DataTable().ajax.reload();
				},
				error: function (jqXHR, textStatus, errorThrown) {
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

function addCertTypes(){
	$("#btn-add-cert-lots").click(function(){
		$('#cert-lots-form').find("input[type=text],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
		//createSubclassLov();
		createCertTypeSubclassLov();
		createCertTypeLov();
		createUnderwriterLov();
		$('#certlotsModal').modal('show');
	})
}


function createCertTypeSubclassLov(){
	if($("#sub-class-def").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "sub-class-def",
			sort : 'subclasscertId',
			change: function(e, a, v){

				$("#subcls-cert-typepk").val(e.added.subclasscertId);
				$("#cert-sub-code").val(e.added.subclass.subId);
				$("#cert-typepk").val(e.added.certType.certId);
			},
			formatResult : function(a)
			{
				return a.subclassDesc
			},
			formatSelection : function(a)
			{
				return a.subclassDesc
			},
			initSelection: function (element, callback) {
				var code = $("#subcls-cert-typepk").val();
				var name = $("#cert-sub-name").val();
				var data = {subclassDesc:name,subclasscertId:code};
				callback(data);
			},
			id: "subclasscertId",
			width:"200px",
			placeholder:"Select Sub Class"
		});

	}
}


function createSubclassLov(){
	if($("#sub-class-def").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "sub-class-def",
			sort : 'subDesc',
			change: function(e, a, v){
				$("#cert-sub-code").val(e.added.subId);
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
				var code = $("#cert-sub-code").val();
				var name = $("#cert-sub-name").val();
				var data = {subDesc:name,subId:code};
				callback(data);
			},
			id: "subId",
			width:"200px",
			placeholder:"Select Sub Class"
		});

	}
}

function createCertTypeLov(){
	if($("#cert-type").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "cert-type",
			sort : 'certDesc',
			change: function (e, a, v) {
				$("#cert-typepk").val(e.added.certId);
			},
			formatResult : function(a)
			{
				return a.certDesc
			},
			formatSelection : function(a)
			{
				return a.certDesc
			},
			initSelection: function (element, callback) {

				var code = $("#cert-typepk").val();
				var name = $("#cert-typename").val();
				var data = {certDesc:name,certId:code};
				callback(data);
			},
			id: "certId",
			placeholder:"Select Cert Type"
		});
	}
}


function createUnderwriterLov(){
	if($("#underwriter-def").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "underwriter-def",
			sort : 'name',
			change: function (e, a, v) {
				$("#userCode").val(e.added.acctId);
			},
			formatResult : function(a)
			{
				return a.name
			},
			formatSelection : function(a)
			{
				return a.name
			},
			initSelection: function (element, callback) {

				var code = $("#userCode").val();
				var name = $("#user-name").val();
				var data = {name:name,acctId:code};
				callback(data);
			},
			id: "acctId",
			placeholder:"Select Underwriter"
		});
	}
}



function saveCertLots(){
	var $paramForm = $('#cert-lots-form');
	var validator = $paramForm.validate();
	$('#certlotsModal').on('hidden.bs.modal', function () {
		validator.resetForm();
		$('#cert-lots-form').find("input[type=text],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
	});

	$('#saveCertLots').click(function(){
		if (!$paramForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$paramForm.serializeArray().map(function(x){data[x.name] = x.value;});
		console.log(data);
		var url = "createCertLots";
		var request = $.post(url, data );
		request.success(function(){
			Swal.fire({
				title: 'Success',
				text: 'Record created/updated Successfully',
				icon: 'success'
			});
			$('#uwcertlots').DataTable().ajax.reload();
			validator.resetForm();
			$('#certlotsModal').modal('hide');
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

function editCertLots(button){
	var certlots = JSON.parse(decodeURI($(button).data("certlots")));
	$("#certpk").val(certlots["cerId"]);
	$("#cert-typepk").val(certlots["certTypes"].certId);
	$("#cert-typename").val(certlots["certTypes"].certDesc);
	createCertTypeLov();
	$("#cert-sub-code").val(certlots["subclass"].subId);
	$("#cert-sub-name").val(certlots["subclass"].subDesc);
	$("#subcls-cert-typepk").val(certlots["subclassCertType"].subclasscertId);
	//createSubclassLov();
	createCertTypeSubclassLov();
	$("#userCode").val(certlots["underwriter"].acctId);
	$("#user-name").val(certlots["underwriter"].name);
	createUnderwriterLov();
	$("#cert-nofrom").val(certlots["noFrom"]);
	$("#cert-noto").val(certlots["noTo"]);
	$('#certlotsModal').modal({
		backdrop: 'static',
		keyboard: true
	})
}

function deleteCertLots(button){
	var certlots = JSON.parse(decodeURI($(button).data("certlots")));
	bootbox.confirm("Are you sure want to delete "+certlots["certTypes"].certDesc+"?", function(result) {
		if(result){
			$.ajax({
				type: 'GET',
				url:  'deleteCertLot/' + certlots["cerId"],
				dataType: 'json',
				async: true,
				success: function(result) {
					Swal.fire({
						title: 'Success',
						text: 'Record Deleted Successfully',
						icon: 'success'
					});
					$('#uwcertlots').DataTable().ajax.reload();
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


function uwCertLots(){
	var url = "insafecerts";
	var table = $('#uwcertlots').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": url,
		lengthMenu: [ [10, 15], [10, 15] ],
		pageLength: 10,
		destroy: true,
		"columns": [
			{
				"data": "underwriter",
				"render": function ( data, type, full, meta ) {
					return full.underwriter.name;
				}

			},
			{
				"data": "subclass",
				"render": function ( data, type, full, meta ) {
					return full.subclass.subDesc;
				}

			},
			{
				"data": "certTypes",
				"render": function ( data, type, full, meta ) {
					return full.certTypes.certDesc;
				}

			},
			{ "data": "noFrom"},
			{ "data": "noTo"},
			{ "data": "certCount"},
			{
				"data": "cerId",
				"render": function ( data, type, full, meta ) {
					if($('#hasCertAllocRole').val()) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="editCertLots(this);"><i class="fa fa-pencil-square-o"></button>';
					}else{
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="editCertLots(this);"disabled><i class="fa fa-pencil-square-o"></button>';
					}
					}

			},
			{
				"data": "cerId",
				"render": function ( data, type, full, meta ) {
					if($('#hasCertAllocRole').val()) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteCertLots(this);"><i class="fa fa-trash-o"></button>';
					}else{
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-certlots=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteCertLots(this);" disabled><i class="fa fa-trash-o"></button>';
					}
					}

			},
		]
	} );
	$('#uwcertlots tbody').on( 'click', 'tr', function () {

		$(this).addClass('table-primary').siblings().removeClass('table-primary');

		var d = table.row( this ).data();
		if(d){
			$("#selected-cert-pk").val(d.cerId);
			$("#cert-available-from").val(d.availableNoFrom);
			uwBranchCertLots(d.cerId);

		}
	} );
	return table;
}
