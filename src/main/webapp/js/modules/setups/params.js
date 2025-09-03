$(function(){

	$(document).ready(function() {
		
		createParameters();
		addParams();
		saveParams();
		
	})
});

function addParams(){
	$("#btn-add-parameter").click(function(){
		 $('#param-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
		 $("#param-name").prop("readonly",false);
		 $('#parametersModal').modal('show');
	})
}

function saveParams(){
	var $paramForm = $('#param-form');
	var validator = $paramForm.validate();
	 $('#parametersModal').on('hidden.bs.modal', function () {
		 validator.resetForm();
		 $('#param-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea").val("");
	 });
	 
	 $('#saveParam').click(function(){
		 if (!$paramForm.valid()) {
				return;
			}
		 $("#param-hidden-id").val($("#param-name").val());
			var $btn = $(this).button('Saving');
			var data = {};
			$paramForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createParameter";
	        var request = $.post(url, data );
	        request.success(function(){
	        	Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
             });
				$('#paramtbl').DataTable().ajax.reload();
				validator.resetForm();
				$('#parametersModal').modal('hide');
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

function editParameters(button){
	var modes = JSON.parse(decodeURI($(button).data("params")));
	$("#param-id").val(modes["paramId"]);
	$("#param-name").val(modes["paramName"]);
	$("#param-hidden-id").val(modes["paramName"]);
	$("#param-value").val(modes["paramValue"]);
	$("#param-desc").val(modes["paramDesc"]);
	$("#chk-active").prop("checked", modes["active"]);
	$("#param-name").prop("readonly",true);
	$('#parametersModal').modal('show');
}


function createParameters(){
	var url = "paramsList";
	  var table = $('#paramtbl').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 15], [10, 15] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "paramName" },
				{ "data": "paramValue" },
				{ "data": "active"},
				{ "data": "paramDesc"},
				{ 
					"data": "paramId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-params='+encodeURI(JSON.stringify(full)) + ' onclick="editParameters(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
			]
		} );
	  return table;
}