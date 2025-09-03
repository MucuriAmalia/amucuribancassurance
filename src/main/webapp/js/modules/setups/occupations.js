$(function(){

	$(document).ready(function() {
		
		createSectorSelect();
		addSector();
		deleteSector();
		addOccupation();
		
	})
});


function addOccupation(){
	
	$("#btn-add-occup").click(function(){
		if($("#sector-code").val() != ''){
			$('#occup-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
			$("#occup-sect-code").val($("#sector-code").val());
			$('#occupModal').modal({
				  backdrop: 'static',
				  keyboard: true
				})
		}
		else{
			bootbox.alert("Select Sector to Add Occupation");
		}
	});
	
	var $classForm = $('#occup-form');
	 var validator = $classForm.validate();
	 $('#saveOccupBtn').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createOccupation";
    var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record Created/Updated Successfully',
                icon: 'success'
            });
				validator.resetForm();
				$('#occup-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$('#occupModal').modal('hide');
				$('#occupList').DataTable().ajax.reload();
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

function addSector(){
	
	$("#btn-add-sector").click(function(){
		$('#sector-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$('#delSectorBtn').hide();
		$('#sectorModal').modal({
			  backdrop: 'static',
			  keyboard: true
			});
		
	});
	
	$("#btn-edit-sector").click(function(){
		if($("#sector-code").val() != ''){
			$('#delSectorBtn').show();
			$('#sectorModal').modal({
				  backdrop: 'static',
				  keyboard: true
				})
		}
		else{
			bootbox.alert("Select Sector to Edit");
		}
	});
	
	var $classForm = $('#sector-form');
	 var validator = $classForm.validate();
	 $('#saveSectorBtn').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createSector";
     var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record Created/Updated Successfully',
                icon: 'success'
            });
				validator.resetForm();
				$('#sector-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$('#sectorModal').modal('hide');
				createSectorSelect();
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

function createOccupations(sectCode){
	var url = "occupations/"+sectCode;
	
	  var currTable = $('#occupList').DataTable( {
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
				{ "data": "shortDesc" },
				{ "data": "name" },
				{ 
					"data": "code",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-occupation='+encodeURI(JSON.stringify(full)) + ' value="Edit" onclick="editOccupation(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "code",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-occupation='+encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="deleteOccupation(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  

	  
	  return currTable;
}

function deleteOccupation(button){
	var occupation = JSON.parse(decodeURI($(button).data("occupation")));
	bootbox.confirm("Are you sure want to delete "+occupation["name"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteOccupation/' + occupation["code"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#occupList').DataTable().ajax.reload();
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


function editOccupation(button){
	var occupation = JSON.parse(decodeURI($(button).data("occupation")));
	$("#occup-code").val(occupation["code"]);
	$("#occup-sect-code").val(occupation["sector"].code);
	$("#occup-id").val(occupation["shortDesc"]);
	$("#occup-name").val(occupation["name"]);
	$('#occupModal').modal({
		  backdrop: 'static',
		  keyboard: true
		});
}


function createSectorSelect(){
	if($("#sect-def").filter("div").html() != undefined)
	  {
		  Select2Builder.initAjaxSelect2({
	            containerId : "sect-def",
	            sort : 'name',
	            change: function(e, a, v){
//	            	 $("#prg-pk").val(e.added.prgCode);
	            	 $("#sector-code").val(e.added.code);
	            	 $("#sector-id").val(e.added.shortDesc);
	            	 $("#sector-name").val(e.added.name);
	            	 createOccupations(e.added.code);
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
	            	
                },
	            id: "code",
	            placeholder:"Select Sector",
	        });
	  }
}


function deleteSector(){
	$("#delSectorBtn").click(function(){
	if($("#sector-code").val() != ''){
	bootbox.confirm("Are you sure want to delete the record?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteSector/' + $("#sector-code").val(),
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
                title: 'Success',
                text: 'Record Deleted Successfully',
                icon: 'success'
            });
			        	createOccupations(-2000);
						$('#sect-def').select2('val', null);
						createSectorSelect();
						$('#sector-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
						$('#sectorModal').modal('hide');
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