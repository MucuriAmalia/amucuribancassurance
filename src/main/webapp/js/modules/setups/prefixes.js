



$(function(){

	$(document).ready(function() {
		
		populateCountryLov();
		addPrefix();
	
		
	});
});


function addPrefix(){
	$("#btn-add-prefix").click(function(){
		if($("#prefix-cou-pk").val() != ''){
		$('#prefix-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$("#prefix-country").val($("#prefix-cou-pk").val());
			createMobProviderSel();
		$('#prefixModal').modal({
			  backdrop: 'static',
			  keyboard: true
			})
		}
		else{
			bootbox.alert("Select Country to continue");
		}
	});
	
	var $classForm = $('#prefix-form');
	 var validator = $classForm.validate();
	 $('#savePrefixBtn').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			
			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createPrefix";
     var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record Created/Updated Successfully',
                icon: 'success'
            });
				$('#prefixList').DataTable().ajax.reload();	
				$('#prefix-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$('#prefixModal').modal('hide');
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


function populateCountryLov(){
	if($("#cou-frm").filter("div").html() != undefined)
	  {
		  Select2Builder.initAjaxSelect2({
	            containerId : "cou-frm",
	            sort : 'couName',
	            change: function(e, a, v){
	            	$("#prefix-country").val(e.added.couCode);
	            	$("#prefix-cou-pk").val(e.added.couCode);
	            	
	            	createPrefixTbl(e.added.couCode);
	            },
	            formatResult : function(a)
	            {
	            	return a.couName
	            },
	            formatSelection : function(a)
	            {
	            	return a.couName
	            },
	            initSelection: function (element, callback) {
//	            	var code = $("#cou-id").val();
//	                var name = $("#cou-name").val();
//	                var data = {couName:name,couCode:code};
//	                callback(data);
                },
	            id: "couCode",
	            placeholder:"Select Country",
	            
	        });
	  }
}

function editPrefixes(button){
	var prefix = JSON.parse(decodeURI($(button).data("prefixes")));
	$("#prefix-id").val(prefix["prefixId"]);
	$("#prefix-country").val(prefix["country"].couCode);
	$("#prefix-name").val(prefix["prefixName"]);
	if(prefix["providers"]){
		$("#mob-prefix-provd-id").val(prefix["providers"].providerId);
		$("#mob-prefix-name").val(prefix["providers"].providerName);
	}
	else{
		$("#mob-prefix-provd-id").val("");
		$("#mob-prefix-name").val("");
	}
	createMobProviderSel();
	$('#prefixModal').modal({
		  backdrop: 'static',
		  keyboard: true
		})
}


function createMobProviderSel(){
	if($("#mob-provider").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "mob-provider",
			sort : 'providerName',
			change: function(e, a, v){
				$("#mob-prefix-provd-id").val(e.added.providerId);
			},
			formatResult : function(a)
			{
				return a.providerName
			},
			formatSelection : function(a)
			{
				return a.providerName
			},
			initSelection: function (element, callback) {
				var code = $("#mob-prefix-provd-id").val();
				var name = $("#mob-prefix-name").val();
				var data = {providerName:name,providerId:code};
				callback(data);
			},
			id: "providerId",
			placeholder:"Select Provider",
		});
	}
}

function deletePrefixes(button){
	var prefix = JSON.parse(decodeURI($(button).data("prefixes")));
	bootbox.confirm("Are you sure want to delete "+prefix["prefixName"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deletePrefix/' + prefix["prefixId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#prefixList').DataTable().ajax.reload();
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

function createPrefixTbl(coucode){
	
		var url = "mobprefixes/"+coucode;
	
	  var currTable = $('#prefixList').DataTable( {
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
				{ "data": "prefixName" },
				{
					"data": "prefixId",
					"render": function ( data, type, full, meta ) {
				        if(full.providers){
							return full.providers.providerName;
						}
						else return "";
					}

				},
				{ 
					"data": "prefixId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-prefixes='+encodeURI(JSON.stringify(full)) + ' onclick="editPrefixes(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "prefixId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-prefixes='+encodeURI(JSON.stringify(full)) + ' onclick="deletePrefixes(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}