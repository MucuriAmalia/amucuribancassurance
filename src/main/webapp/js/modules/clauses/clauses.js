
$(function(){

	$(document).ready(function() {
		$('#txt-clause-type').on('change', function() {
			createClauses(this.value);
		});


		addClauses();
		createSubclassLov();
		getSubclassClausesModal();
		saveSubclassClauses();
		saveSubclassClause();
		searchSubClassClause();

	});
});

function searchSubClassClause(){
	console.log("testing")
	$("#searchclause").click(function(){
		console.log("testing click"+$("#clauses-name-search").val())
		console.log("testing click 2")
		createSubclassClauses($("#sub-code").val(),$("#clauses-name-search").val());
	})
}
function addClauses(){
	$("#btn-add-clauses").click(function(){
		$('#clause-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$("#chk-cl-editable").prop("checked", false);
		$("#clause-type").val($('#txt-clause-type').val());
		$('#clauseModal').modal({
			  backdrop: 'static',
			  keyboard: true
			})
	});
	
	 var $classForm = $('#clause-form');
	 var validator = $classForm.validate();
	 $('#saveClauseBtn').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createClauseDef";
       var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#clauseList').DataTable().ajax.reload();		
				validator.resetForm();
				$('#clause-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$("#chk-cl-editable").prop("checked", false);
				$('#clauseModal').modal('hide');
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

function editClause(button){
	var clause = JSON.parse(decodeURI($(button).data("clauses")));
	$("#clause-code").val(clause["clauId"]);
	$("#clau-id").val(clause["clauShtDesc"]);
	$("#clause-name").val(clause["clauHeading"]);
	$("#clause-type").val(clause["clauseType"]);
	$("#cla-wording").val(clause["clauWording"]);
	$("#chk-cl-editable").prop("checked", clause["editable"]);
	$('#clauseModal').modal({
		  backdrop: 'static',
		  keyboard: true
		});
}

function deleteClause(button){
	var clause = JSON.parse(decodeURI($(button).data("clauses")));
	bootbox.confirm("Are you sure want to delete "+clause["clauShtDesc"]+"?", function(result) {
		 if(result){
			  $.ajax({
			        type: 'GET',
			        url:  'deleteClause/' + clause["clauId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
                title: 'Success',
                text: 'Record Deleted Successfully',
                icon: 'success'
            });
			        	$('#clauseList').DataTable().ajax.reload();		
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


function createSubclassLov(){
	if($("#sub-class-def").filter("div").html() != undefined)
	{
		  Select2Builder.initAjaxSelect2({
	          containerId : "sub-class-def",
	          sort : 'subDesc',
	          change: function(e, a, v){
	        	  $("#sub-code").val(e.added.subId);
	        	  createSubclassClauses(e.added.subId,"");
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
	        	  
	          },
	          id: "subId",
	          width:"200px",
	          placeholder:"Select Sub Class"
	      });
		  
	}
}

function createClauses(clauseType){
	var url = "clausesList/"+clauseType;
	  var currTable = $('#clauseList').DataTable( {
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
				{ "data": "clauShtDesc" },
				{ "data": "clauHeading" },
				{
					"data": "clauseType",
					"render": function ( data, type, full, meta ) {
						if(full.clauseType){
							if(full.clauseType ==='E') return "Excess";
							else if(full.clauseType ==='L') return "Limits";
							else if(full.clauseType ==='C') return "Clause";
							else if(full.clauseType ==='X') return "Exclusions";
							else return "";
						}
						else{
							return "";
						}
					}

				},
				{
					"data": "editable",
					"render": function ( data, type, full, meta ) {
						if(full.editable){
							return "Yes";
						}
						else{
							return "No";
						}
					}

				},
				{ "data": "clauWording" },
				{ 
					"data": "clauId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="editClause(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "clauId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClause(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}

function createSubclassClauses(subCode,search){
	var url = "subclassClauses/"+subCode;
	  var currTable = $('#subclausesList').DataTable( {
			"processing": true,
			"serverSide": true,
		  	searching: false,
			autoWidth: true,
			"ajax": {
				'url': url,
				'data':{
					'search': search}
			},
			lengthMenu: [ [5], [5] ],
			pageLength: 5,
			destroy: true,
			"columns": [
				{ "data": "clause",
					"render": function ( data, type, full, meta ) {
						return full.clause.clauShtDesc;
					}
				},
				{ "data": "clause",
					"render": function ( data, type, full, meta ) {
						return full.clause.clauHeading;
					}
				},
				{
					"data": "clause",
					"render": function ( data, type, full, meta ) {
						if(full.clause.clauseType){
							if(full.clause.clauseType ==='E') return "Excess";
							else if(full.clause.clauseType ==='L') return "Limits";
							else if(full.clause.clauseType ==='C') return "Clause";
							else if(full.clause.clauseType ==='X') return "Exclusions";
							else return "";
						}
						else{
							return "";
						}
					}

				},
				{
					"data": "clause",
					"render": function ( data, type, full, meta ) {
						if(full.clause.editable){
							return "Yes";
						}
						else{
							return "No";
						}
					}
				},
				{
					"data": "mandatory",
					"render": function ( data, type, full, meta ) {
						if(full.mandatory){
							return "Yes";
						}
						else{
							return "No";
						}
					}
				},
				{
					"data": "clauId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="editSubClause(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "clauId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="deleteSubClause(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}

function editSubClause(button){
	var clause = JSON.parse(decodeURI($(button).data("clauses")));
	$("#sub-clause-code").val(clause["clauId"]);
	$("#sub-clause-pk").val(clause["clause"].clauId);
	$("#sub-sub-code").val(clause["subclass"].subId);
	$("#sub-clau-id").val(clause["clause"].clauShtDesc);
	$("#sub-clause-name").val(clause["clause"].clauHeading);
	$("#chk-cl-mandatory").prop("checked", clause["mandatory"]);
	$('#editSubclauseModal').modal({
		  backdrop: 'static',
		  keyboard: true
		});
}

function deleteSubClause(button){
	var clause = JSON.parse(decodeURI($(button).data("clauses")));
	bootbox.confirm("Are you sure want to delete "+clause["clause"].clauShtDesc+"?", function(result) {
		 if(result){
			  $.ajax({
			        type: 'GET',
			        url:  'deleteSubClause/' + clause["clauId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
							title: 'Success',
							text: 'Record Deleted Successfully',
							icon: 'success'
						});
			        	$('#subclausesList').DataTable().ajax.reload();		
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



function searchClauses(search){
	if($("#sub-code").val() != ''){
		$.ajax({
	        type: 'GET',
	        url:  'unassignedClauses',
	        dataType: 'json',
	        data: {"subCode": $("#sub-code").val(),"search":search},
	        async: true,
	        success: function(result) {
	        	$("#subclausestbl tbody").each(function(){
	        		$(this).remove();
	        	});
				console.log('result ',result);
	        	for(var res in result){
	        		var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].clauId+"'></td><td>" + result[res].clauShtDesc + "</td><td>" + result[res].clauHeading+ "</td><td>" + (result[res].editable) + "</td></tr>";
		             $("#subclausestbl").append(markup);
	        	}
	        	//$("#sub-scl-pro-code").val($("#prg-prod-code").val());
	        	$('#subclauseModal').modal({
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
		bootbox.alert("Select Sub Class to attach clauses")
	}
}


function getSubclassClausesModal(){
	
	$("#btn-add-sub-clauses").click(function(){
		$("#cla-sub-code").val($("#sub-code").val());
		searchClauses("");
	});
	
	$("#searchClauses").click(function(){
		searchClauses($("#clause-name-search").val());
	 })
}


function saveSubclassClauses(){
	var arr = [];
	$("#saveSubclausesBtn").click(function(){
		$("#subclausestbl tbody").find('input[name="record"]').each(function(){
	    	if($(this).is(":checked")){
	    		arr.push($(this).attr("id"));
	        }
	    });
		if(arr.length==0){
			bootbox.alert("No Clauses Selected to attach..");
			return;
		}
		
		var $currForm = $('#subcl-clauses-form');
		var currValidator = $currForm.validate();
		if (!$currForm.valid()) {
			return;
		}
		
		var data = {};
		$currForm.serializeArray().map(function(x) {
			data[x.name] = x.value;
		});
		var url = "createSubclassClauses";
		data.clauses = arr;
		
		
		$.ajax({
			url : url,
			type : "POST",
			data : JSON.stringify(data),
			success : function(s) {
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#subclausesList').DataTable().ajax.reload();
				$('#subclauseModal').modal('hide');
				 arr = [];
			},
			error : function(jqXHR, textStatus, errorThrown) {
				console.log(jqXHR);
				Swal.fire({
					title: 'Error',
					text: jqXHR.responseText,
					icon: 'error'
				})
			},
			dataType : "json",
			contentType : "application/json"
		});
	})
}



function saveSubclassClause(){
	
	 var $classForm = $('#sub-clause-form');
	 var validator = $classForm.validate();
	 $('#saveSubCluseBtn').click(function(){
			if (!$classForm.valid()) {
				return;
			}
			var $btn = $(this).button('Saving');
			var data = {};
			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
			var url = "createSubClauseDef";
       var request = $.post(url, data );
			request.success(function(){
				Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
				$('#subclausesList').DataTable().ajax.reload();		
				validator.resetForm();
				$('#sub-clause-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
				$("#chk-cl-mandatory").prop("checked", false);
				$('#editSubclauseModal').modal('hide');
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