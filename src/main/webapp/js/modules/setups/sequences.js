$(function(){

	$(document).ready(function() {
		
		$("#newSequency").on("click", function(){
			$('#seqform').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
		});	
		
		createSequenceTable();
		saveUpdateSequence();

		$(document).ajaxStart(function () {
			$("#saveSequencyBtn").attr("disabled", true);
		});
		$(document).ajaxComplete(function () {
			$("#saveSequencyBtn").attr("disabled", false);
		});
		
	});
});

function saveUpdateSequence(){
	var $currForm = $('#seqform');
	var currValidator = $currForm.validate();
	
	
	$('#saveSequencyBtn').click(function(){
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createSequence";
        var request = $.post(url, data );
        request.success(function(){
        	 Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
             });
			$('#seqList').DataTable().ajax.reload();
			currValidator.resetForm();
			$('#seqform').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        });
        request.error(function(jqXHR, textStatus, errorThrown){
        	 Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
		});
		request.always(function(){
			$btn.button('reset');
        });
		
	});
}

function editSequence(button){
	var sequence = JSON.parse(decodeURI($(button).data("sequences")));
	$("#seq-code").val(sequence["seqId"]);
	$("#prefix-name").val(sequence["seqPrefix"]);
	$("#last-number").val(sequence["lastNumber"]);
	$("#next-number").val(sequence["nextNumber"]);
	$("#sel3").val(sequence["seqType"]);
	$("#sel2").val(sequence["transType"]);
}

function confirmSequenceDelete(button){
	var sequence = JSON.parse(decodeURI($(button).data("sequences")));
	bootbox.confirm("Are you sure want to delete "+sequence["seqPrefix"]+"?", function(result) {
		 if(result){
	    	  $.ajax({
			        type: 'GET',
			        url:  'deleteSequence/' + sequence["seqId"],
			        dataType: 'json',
			        async: true,
			        success: function(result) {
			        	Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
			        	$('#seqList').DataTable().ajax.reload();
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


function createSequenceTable(){
	var url = "sequences";
	  var currTable = $('#seqList').DataTable( {
			"processing": true,
			"serverSide": true,
			"ajax": url,
			lengthMenu: [ [10, 20, 30], [10, 20, 30] ],
			pageLength: 10,
			destroy: true,
			"columns": [
				{ "data": "seqPrefix" },
				{ "data": "lastNumber" },
				{ "data": "nextNumber" },
				{ "data": "seqType" ,
				   "render": function ( data, type, full, meta ) {
						if(full.seqType){
							if(full.seqType==='PAA') return "All Branches";
							else if(full.seqType==='PPA') return "Per Product Per Branch";
							else if(full.seqType==='PAY') return "All Branches Per Year";
							else if(full.seqType==='PBA') return "Per Branch Perpetual";
							else if(full.seqType==='PBY') return "Per Branch Per year";
						}
				    }
				},
				{ "data": "transType",
					"render": function ( data, type, full, meta ) {
						if(full.transType){
							if(full.transType==='C') return "Client Definition Sequence";
							else if(full.transType==='A') return "Agent Definition Sequence";
							else if(full.transType==='P') return "Policies Creation Sequence";
							else if(full.transType==='R') return "Receipts Creation Sequence";
							else if(full.transType==='D') return "Debit Note Creation Sequence";
							else if(full.transType==='E') return "Endorsements Creation Sequence";
							else if(full.transType==='CL') return "Claim Trans Creation Sequence";
							else if(full.transType==='Q') return "Quote Trans Creation Sequence";
							else if(full.transType==='M') return "Medical Membership Sequence";
							else if(full.transType==='AD') return "Admin Fee Trans Sequence";
							else if(full.transType==='PR') return "Proposal Creation Sequence";
							else if(full.transType==='CRCP') return "Creditor Commissions Sequence"
							else if(full.transType==='SACP') return "Sub-Agent Commissions Sequence"
							else if(full.transType==='BPU') return "Bulk Policy Upload Sequence"
							else if(full.transType==='LD') return "Loaded Data Sequence"
							else if(full.transType==='WPU') return "Wesha Policy Upload Sequence"
							else if(full.transType==='EPIU') return "Embedded Packaged Insurance Upload Sequence"
							else if(full.transType==='ERIU') return "Embedded Retrenchment Insurance Upload Sequence"
							else if(full.transType==='CLU') return "Credit Life Upload Sequence"
							else if(full.transType==='GLU') return "Group Life Upload Sequence"
							else if(full.transType==='TPU') return "Timiza Policy Upload Sequence"
							else if(full.transType==='CCU') return "Credit Card Upload Sequence"
							else if(full.transType==='MLU') return "Mortgage Life Upload Sequence"
						}
					}
				},
				{ 
					"data": "seqId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-sequences='+encodeURI(JSON.stringify(full)) + '  onclick="editSequence(this);"><i class="fa fa-pencil-square-o"></button>';
					}

				},
				{ 
					"data": "seqId",
					"render": function ( data, type, full, meta ) {
						return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-sequences='+encodeURI(JSON.stringify(full)) + '  onclick="confirmSequenceDelete(this);"><i class="fa fa-trash-o"></button>';
					}

				},
			]
		} );
	  return currTable;
}