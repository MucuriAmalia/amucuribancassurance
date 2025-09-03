$(function(){

	$(document).ready(function() {
		
		uwCertLots();
		addCertType();
		saveCertLots();
		
	});
});

function addCertType(){
	$("#btn-add-cert-lots").click(function(){
		 $('#cert-lots-form').find("input[type=text],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
		 createSubclassLov();
		 createCertTypeLov();
		 createUnderwriterLov();
		 $('#certlotsModal').modal('show');
	})
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
	            $("#cert-type-pk").val(e.added.certId);
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
	            	
                var code = $("#cert-type-pk").val();
                var name = $("#cert-type-name").val();
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
	            $("#userCod").val(e.added.acctId);
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
	$("#cert-pk").val(certlots["cerId"]);
	$("#cert-type-pk").val(certlots["certTypes"].certId);
	$("#cert-type-name").val(certlots["certTypes"].certDesc);
	createCertTypeLov();
	$("#cert-sub-code").val(certlots["subclass"].subId);
	$("#cert-sub-name").val(certlots["subclass"].subDesc);
	createSubclassLov();
	$("#userCod").val(certlots["underwriter"].acctId);
	$("#username").val(certlots["underwriter"].name);
	createUnderwriterLov();
	$("#cert-no-from").val(certlots["noFrom"]);
	$("#cert-no-to").val(certlots["noTo"]);
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
						})
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
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certlots='+encodeURI(JSON.stringify(full)) + ' onclick="editCertLots(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "cerId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-certlots='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCertLots(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	$('#uwcertlots tbody').on( 'click', 'tr', function () {

		$(this).addClass('table-primary').siblings().removeClass('table-primary');

		var d = table.row( this ).data();
		if(d){
			//$("#lab-pk").val(d.labId);
			createActivities(d.labId);

		}
	} );
	  return table;
}