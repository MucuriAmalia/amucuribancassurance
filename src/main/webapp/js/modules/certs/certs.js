$(function(){

	$(document).ready(function() {
		
		createCertTypes();
		addCertType();
		saveCertTypes();
		addSubclasCertType();
		saveSubclassCertType();
		
	});
});


function getUnAssignedSubclasses(){
	if($("#certtype-code").val() != ''){
		$.ajax({
			type: 'GET',
			url:  'getCertUnassignedSubclasses',
			dataType: 'json',
			async: true,
			success: function(result) {
				$("#subclassestbl tbody").each(function(){
					$(this).remove();
				});
				for(var res in result){
					var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].subId+"'></td><td>" + result[res].subShtDesc + "</td><td>" + result[res].subDesc+ "</td></tr>";
					$("#subclassestbl").append(markup);
				}
				//$("#sub-scl-pro-code").val($("#prg-prod-code").val());
				$('#subclasscerttypeModal').modal({
					backdrop: 'static',
					keyboard: true
				})
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
	else{
		bootbox.alert("Select certificate type to attach subclasses")
	}
}


function addCertType(){
	$("#btn-add-cert-types").click(function(){
		 $('#cert-type-form').find("input[type=text],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
		 $('#certtypeModal').modal('show');
	})
}


function addSubclasCertType(){
	$("#btn-add-subclass-certs").click(function(){

		getUnAssignedSubclasses();
			//$('#subclasscerttypeModal').modal('show');
	})
}

function saveCertTypes(){
	var $paramForm = $('#cert-type-form');
	var validator = $paramForm.validate();
	 $('#certtypeModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#cert-type-form').find("input[type=text],input[type=number],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
	 });
	 
	 $('#saveCertType').click(function(){
		 if (!$paramForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paramForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createCertType";
	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#certtbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#certtypeModal').modal('hide');
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

function certTypesSubclasses(certTypecode){
	var url = "certTypesSubclasses/"+certTypecode;
	var table = $('#certsubclasses').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": url,
		lengthMenu: [ [10, 15], [10, 15] ],
		pageLength: 10,
		destroy: true,
		"columns": [
			{
				"data": "subclass",
				"render": function ( data, type, full, meta ) {
					return full.subclass.subShtDesc;
				}

			},
			{
				"data": "subclass",
				"render": function ( data, type, full, meta ) {
					return full.subclass.subDesc;
				}

			},
			{
				"data": "subclasscertId",
				"render": function ( data, type, full, meta ) {
					return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-subclasscerttypes='+encodeURI(JSON.stringify(full)) + ' onclick="deletesubclassCertTypes(this);"><i class="fa fa-trash-o"></button>';
				}

			},


		]
	} );
	return table;
}


function createCertTypes(){
	var url = "allcerttypes";

	  var table = $('#certtbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 15], [10, 15] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "certShtDesc" },
				{ "data": "certDesc" },
				{ "data": "certPrefix"},
				{ "data": "certTemplate"},
				{ "data": "minCapacity"},
				{ "data": "maxCapacity"},
				{ "data": "reorderLevel"},
				{ 
					"data": "certId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certtypes='+encodeURI(JSON.stringify(full)) + ' onclick="editCertTypes(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "certId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-certtypes='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCertTypes(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} )
	$('#certtbl tbody').on( 'click', 'tr', function () {

		$(this).addClass('table-primary').siblings().removeClass('table-primary');

		var d = table.row( this ).data();
		if(d){
			$("#certtype-code").val(d.certId);
			certTypesSubclasses(d.certId);

		}else {$("#certtype-code").val("");}
	} );
	  return table;
}



function saveSubclassCertType(){
	var arr = [];
	$("#saveSubclassBtn").click(function(){
		$("#subclassestbl tbody").find('input[name="record"]').each(function(){
			if($(this).is(":checked")){
				arr.push($(this).attr("id"));
			}
		});
		if(arr.length==0){
			bootbox.alert("No Clauses Selected to attach..");
			return;
		}

		var $currForm = $('#sub-class-form');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}

		var data = {};
		$currForm.serializeArray().map(function(x) {
			data[x.name] = x.value;
		});
		var url = "createSubclassCertTypes";
		data.subclasses = arr;


		$.ajax({
			url : url,
			type : "POST",
			data : JSON.stringify(data),
			success : function(s) {
				Swal.fire({
					title: 'Success',
					text: 'Record Created Successfully',
					icon: 'success'
				});
				$('#certsubclasses').DataTable().ajax.reload();
				$('#subclasscerttypeModal').modal('hide');
				arr = [];
			},
			error : function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR);
				Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
			},
			dataType : "json",
			contentType : "application/json"
		});
	})
}


function deletesubclassCertTypes(button){
	var certtypes = JSON.parse(decodeURI($(button).data("subclasscerttypes")));
	bootbox.confirm("Are you sure want to delete "+certtypes["subclass"].subDesc+"?", function(result) {
		if(result){
			$.ajax({
				type: 'GET',
				url:  'deleteSubclassCertType/' + certtypes["subclasscertId"],
				dataType: 'json',
				async: true,
				success: function(result) {
					Swal.fire({
						title: 'Success',
						text: 'Record Deleted Successfully',
						icon: 'success'
					});
					$('#certsubclasses').DataTable().ajax.reload();
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

function deleteCertTypes(button){
	var certtypes = JSON.parse(decodeURI($(button).data("certtypes")));
	bootbox.confirm("Are you sure want to delete "+certtypes["certDesc"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteCertType/' + certtypes["certId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
						Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#certtbl').DataTable().ajax.reload();
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

function editCertTypes(button){
	var certtypes = JSON.parse(decodeURI($(button).data("certtypes")));
	$("#cert-pk").val(certtypes["certId"]);
	$("#cert-id").val(certtypes["certShtDesc"]);
	$("#cert-desc").val(certtypes["certDesc"]);
	$("#cert-prefix").val(certtypes["certPrefix"]);
	$("#cert-template").val(certtypes["certTemplate"]);
	$("#cert-min-cap").val(certtypes["minCapacity"]);
	$("#cert-max-cap").val(certtypes["maxCapacity"]);
	$("#cert-reorder-level").val(certtypes["reorderLevel"]);
	$('#certtypeModal').modal({
		  backdrop: 'static',
		  keyboard: true
		})
}