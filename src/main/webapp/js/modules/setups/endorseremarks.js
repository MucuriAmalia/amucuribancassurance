$(function(){

	$(document).ready(function() {
		
		createEndRemarks();
		newEndRemarks();
		saveEndorsementRemakrs();
	});
});


function newEndRemarks(){
	$("#btn-add-end-remarks").on("click", function(){
		$('#end-remarks-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
		$('#endRemarksModal').modal('show');
	});
}


function saveEndorsementRemakrs(){
	var $paymodesForm = $('#end-remarks-form');
	var validator = $paymodesForm.validate();
	 $('#endRemarksModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#end-remarks-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
	 });
	 
	 $('#saveEndorseRemarks').click(function(){
		 if (!$paymodesForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$paymodesForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createEndRemarks";
	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
              });
				$('#endorse-remarks-tbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#endRemarksModal').modal('hide');
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

function createEndRemarks(){
	var url = "endRemarks";
	  var table = $('#endorse-remarks-tbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 15], [10, 15] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "remarkShtDesc" },
				{ "data": "remarks" },
				{ 
					"data": "remarkId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-endremarks='+encodeURI(JSON.stringify(full)) + ' onclick="editEndorseRemarks(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "remarkId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-endremarks='+encodeURI(JSON.stringify(full)) + ' onclick="confirmEndRemDel(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return table;
}


function editEndorseRemarks(button){
	var modes = JSON.parse(decodeURI($(button).data("endremarks")));
	$("#remark-pk").val(modes["remarkId"]);
	$("#remark-id").val(modes["remarkShtDesc"]);
	$("#remarks").val(modes["remarks"]);
	$('#endRemarksModal').modal('show');
}



function confirmEndRemDel(button){
	var modes = JSON.parse(decodeURI($(button).data("endremarks")));
	bootbox.confirm("Are you sure want to delete "+modes["remarkShtDesc"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteEndRemarks/' + modes["remarkId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#endorse-remarks-tbl').DataTable().ajax.reload();
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