$(function(){

	$(document).ready(function() {

		blackAndTerm();

		$('#rpt-group').on('click', function () {

			createSpecificProducts($("#rpt-id").val());
			createLife($("#rpt-id").val());

		})
		$('#find-rpt').click(function () {
			createBudget();
		})


		$("#btn-add-budget").on('click',function(){
			$("#budgetModal").modal('show');
		});
		$(".input-sm").keyup(function (e) {
			if (e.keyCode==13){
				var dvt=$(".input-sm").val();
				console.log(dvt);
				searchBud(dvt);
			}
		})
		$("#btn-import").on('click',function(){
			$("#excelModal").modal('show');
		});

		$("#btn-import-trans").change(function(e){
			var selectedFile= e.target.files[0];
			var reader=new FileReader();
			reader.onload= function(evt){
				//if(typeof require !== 'undefined') XLSX = require('xlsx');
				var data=evt.target.result;
				var budgetsheet= XLSX.read(data,{type : 'binary'});

				budgetsheet.SheetNames.forEach(function(sheetName) {

					var XL_row_object = XLSX.utils.sheet_to_row_object_array(budgetsheet.Sheets[sheetName]);
					var obj = JSON.stringify(XL_row_object);
					obj=JSON.parse(obj);
					//for(var i=0;i<json_object.length;i++){
					//   var obj=json_object[i];
					//  console.log(obj);

					// }

					//	document.getElementById("jsonObject").innerHTML = obj.NAME+" "+obj.PRODUCT;
					insertExcel(obj);
					$("#excelModal").modal('hide');

					$('#excelModal').on('hidden.bs.modal', function () {
						$("#btn-import-trans").val('');
					});
				})
			};
			reader.onerror = function(event) {
				console.error("File could not be read! Code " + event.target.error.code);
			};
			reader.readAsBinaryString(selectedFile);
		});


		$("#budgets-tbl").on('click', '.btn-edit', function(){

			var data = $(this).closest('tr').find('#editId').val();
			var url='getEdits/'+data;

			$.ajax({
				type: 'GET',
				url: url,
			}).done(function (s) {

				$("#myBudgetId").val(s.id);
				$("#budget_name").val(s.name);
				if(s.productReportGroup!==null) {
					$("#rpt-id").val(s.productReportGroup.rptId);
					$("#rpt-name").val(s.productReportGroup.repDesc);
					createRptGrpSelectLov();
				}
				if(s.orgBranch!==null) {
					$("#obId").val(s.orgBranch.obId);
					$("#ob-name").val(s.orgBranch.obName);
					populateBranchA();
				}
				if(s.accountDef!==null) {
					$("#sub-agent-id").val(s.accountDef.acctId);
					$("#sub-agent-name").val(s.accountDef.name);
					populateUserA();
				}
				$("#myValue").val(s.year);
				$("#yearname").val(s.year);
				populateYear();
				$("#janprod").val(s.janProd);
				$("#janpol").val(s.janPol);
				$("#febprod").val(s.febProd);
				$("#febpol").val(s.febPol);
				$("#marprod").val(s.marProd);
				$("#marpol").val(s.marPol);
				$("#aprprod").val(s.aprProd);
				$("#aprpol").val(s.aprPol);
				$("#mayprod").val(s.mayProd);
				$("#maypol").val(s.mayPol);
				$("#junprod").val(s.junProd);
				$("#junpol").val(s.junPol);
				$("#julprod").val(s.julProd);
				$("#julpol").val(s.julPol);
				$("#augprod").val(s.augProd);
				$("#augpol").val(s.augPol);
				$("#seppol").val(s.sepPol);
				$("#sepprod").val(s.sepProd);
				$("#octpol").val(s.octPol);
				$("#octprod").val(s.octProd);
				$("#novprod").val(s.novProd);
				$("#novpol").val(s.novPol);
				$("#decprod").val(s.decProd);
				$("#decpol").val(s.decPol);
				$('input:radio[name="budSet"][value="UB"]').prop('checked', true);
				loadEditModal();
				$("#budgetModal").modal('show');
			});
		});
		$("#budgets-tbl").on('click', '.btn-delete', function() {

			var data = $(this).closest('tr').find('#editId').val();
			var url = 'deleteBudget/' + data;
			bootbox.confirm("Are you sure want to delete this budget?", function (result) {
				if (result) {
					$.ajax({
						type: 'GET',
						url: url,
					}).done(function (s) {
						Swal.fire({
							title: 'Success',
							text: 'Budget Deleted Successfully',
							icon: 'success'
						})
						$("#budgets-tbl").DataTable().ajax.reload();
					}).fail(function (xhr, error) {
						Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
					})
				}
			});

		});

		$("#disbud").on('click',function(){
			$("#myBudgetId").val('');
			$('#budget-form')[0].reset();
			jQuery('.select2-offscreen').select2('val', '');
			$("#budgetModal").modal('hide');
		});
		$("#fdisbud").on('click',function(){
			$("#myBudgetId").val('');
			$('#budget-form')[0].reset();
			jQuery('.select2-offscreen').select2('val', '');
			$("#budgetModal").modal('hide');
		});


		populateUserA();
		saveBudget();
		populateYear();
		createBudget();
		createProdGrpSelect();
		createProdGrpSelect1();
		populateBranchA();
		loadBudgets();
		loadBReports();
		unloaded();
		checkUnloaded();
		viewUnloaded();
		deleteAllUnloaded();
		createRptGrpSelectLov();
		addProductRptGroup();
		editReportGroup();
		makeAndDownload();
		findRptBudgetSelectLov();
		$(".dismiss").on('click',function(){
			$("#rpt-id").val('');
			$('#product-form')[0].reset();
			jQuery('.select2-offscreen').select2('val', '');
			$("#prodModal").modal('hide');
		});
		//if($("#tenId-pk").val()!='') {

	});
});
function makeAndDownload(){
	$('#btn-download-excel').click(function(){

		$.ajax({
			type: 'GET',
			url: "drawUnloads"
		}).done(function (s) {
			s.forEach(function (obj) {
				obj=Object.assign(...['name', 'productReportGroup', 'accountDef', 'branch', 'year', 'error', 'janProd',
					'janPol', 'marProd', 'marPol', 'aprProd', 'aprPol', 'mayProd', 'mayPol',
					'junProd', 'junPol', 'julProd', 'julPol', 'augProd', 'augPol', 'sepProd', 'sepPol',
					'octProd', 'octPol', 'novProd', 'novPol', 'decProd', 'decPol'].map(k => ({[k]: obj[k]})));
			})
			var q=s;

			var wb = XLSX.utils.book_new();
			wb.Props = {
				Title: "Unloaded Budgets",
				Subject: "Unloaded",
				Author: "Gabu Dev",
			};


			//var obj = s[i];
			//console.log(obj);

			var result =q.map(function(e) {

				return Object.keys(e).map(function(k) {

					return e[k]
				})
			})

			console.log(result);
			wb.SheetNames.push("Failed Budgets");

			var ws = XLSX.utils.aoa_to_sheet(result);
			wb.Sheets["Failed Budgets"] = ws;


			var wbout = XLSX.write(wb, {bookType:'xlsx',  type: 'binary'});


			saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), 'budget.xlsx');
		});

	})
}

function s2ab(s) {
	var buf = new ArrayBuffer(s.length);
	var view = new Uint8Array(buf);
	for (var i=0; i<s.length; i++) view[i] = s.charCodeAt(i) & 0xFF;
	return buf;
}
function unloaded() {

	$('#unloaded').hide();
}
function viewUnloaded() {
	$('#btn-view-unloaded').click(function () {
		$('#loaded').hide();
		createUnloadedBudget();

	})

}
function deleteAllUnloaded(){
	$('#btn-delete-unloaded').click(function () {

		$.ajax({
			type: 'GET',
			url: "deleteAll",

		}).done(function (s) {
			Swal.fire({
				title: 'Success',
				text: 'Record deleted Successfully',
				icon: 'success'
			});
			$('#btn-download-excel').hide();
			$('#btn-delete-unloaded').hide();
			$('#btn-view-unloaded').hide();

			$('#unloaded-budgets-tbl').DataTable().ajax.reload();

		}).fail(function (xhr, error) {
			Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
		});
	})
}
function checkUnloaded(){
	$.ajax({
		type: 'GET',
		url: "checkUnloaded",

	}).done(function (s) {
		if(!$.trim(s)){
			$('#btn-view-unloaded').hide();
			$('#btn-delete-unloaded').hide();
			$('#btn-download-excel').hide();
		}
		else{
			$('#btn-view-unloaded').show();
			$('#btn-delete-unloaded').show();
			$('#btn-download-excel').show();

		}
	}).fail(function (xhr, error) {

	});
}
function createRptGrpSelectLov() {

	if($("#rpt-group").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "rpt-group",
			sort : 'repDesc',
			change: function(e, a, v){
				$("#rpt-id").val(e.added.rptId);
				$("#rpt-name").val(e.added.repDesc);
				createLife($("#rpt-id").val());

			},
			formatResult : function(a)
			{
				return a.repDesc
			},
			formatSelection : function(a)
			{
				return a.repDesc
			},
			initSelection: function (element, callback) {
				var code = $("#rpt-id").val();
				var name = $("#rpt-name").val();
				var data = {repDesc:name,rptId:code};
				callback(data);
			},
			id: "rptId",
			placeholder:"Select Report Group",
		});
	}
}
function findRptBudgetSelectLov() {

	if($("#find-rpt").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "find-rpt",
			sort : 'repDesc',
			change: function(e, a, v){
				$("#find-id").val(e.added.rptId);
				$("#find-name").val(e.added.repDesc);

				createLife();
			},
			formatResult : function(a)
			{
				return a.repDesc
			},
			formatSelection : function(a)
			{
				return a.repDesc
			},
			initSelection: function (element, callback) {
				var code = $("#rpt-id").val();
				var name = $("#rpt-name").val();
				var data = {repDesc:name,rptId:code};
				callback(data);
			},
			id: "rptId",
			placeholder:"Select Report Group",
		});
	}
}
function addProductRptGroup(){
	$("#btn-add-rptgroup").click(function () {

		$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$('#prodRptGrpModal').modal({
			backdrop: 'static',
			keyboard: true
		})
		$('#prodModal').modal('hide');
	});

}

function editReportGroup() {
	$('#btn-edit-rptgroup').click(function (){
		$('#prgrptId').val($('#rpt-id').val())
		$('#rpt-desc').val($('#rpt-name').val())
		$('#rpt-shtdesc').val($('#rpt-name').val())
		$('#prodModal').modal('hide');
		$('#prodRptGrpModal').modal({
			backdrop: 'static',
			keyboard: true
		})	})
}
function loadBReports() {
	$('#all-prod').hide();
	$('#spec-prod').hide();
}
function addProductRptGroup() {
	$("#btn-add-rptgroup").click(function () {
		$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$('#prodRptGrpModal').modal({
			backdrop: 'static',
			keyboard: true
		})
	});
}
function addProductGroup(){
	$("#btn-add-classes").click(function(){
		$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
		$('#delproductGrpBtn').hide();
		$('#prodGrpModal').modal({
			backdrop: 'static',
			keyboard: true
		})
	});

	$("#btn-edit-classes").click(function(){
		if($("#prg-id").val() != ''){
			$('#delproductGrpBtn').show();
			$('#prodGrpModal').modal({
				backdrop: 'static',
				keyboard: true
			})
		}
		else{
			bootbox.alert("Select Product Group to Edit");
		}
	});

}
function addEditProductGroup(){
	var $currForm = $('#prg-group-form');
	var currValidator = $currForm.validate();


	$('#saveproductGrpBtn').click(function(){
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createGroup";
		var request = $.post(url, data );
		request.success(function(){
			Swal.fire({
					title: 'Success',
					text: 'Record created/updated Successfully',
					icon: 'success'
				});
			currValidator.resetForm();

			$("#prg-pk").val("");
			createProducts();
			$('#prd-group').select2('val', null);
			createProdGrpSelect();
			$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
			$('#prodGrpModal').modal('hide');
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
function deleteProductGroup(){
	$("#delproductGrpBtn").click(function(){
		if($("#prg-id").val() != ''){
			bootbox.confirm("Are you sure want to delete the group?", function(result) {
				if(result){
					$.ajax({
						type: 'GET',
						url:  'deleteProductGroup/' + $("#prg-id").val(),
						dataType: 'json',
						async: true,
						success: function(result) {
							Swal.fire({
                    title: 'Success',
                    text: 'Record Deleted Successfully',
                    icon: 'success'
                });
							$("#prg-pk").val("");
							createProducts();
							$('#prd-group').select2('val', null);
							createProdGrpSelect();
							$('#prg-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
							$('#prodGrpModal').modal('hide');
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
function createSpecificProducts(prg){
	var url = "products/"+prg
	var currTable = $('#prodList').DataTable( {
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
		},
		lengthMenu: [ [10], [15] ],
		pageLength: 15,
		destroy: true,

		"columns": [

			{ "data": "proDesc" },
			{ "data": "proPolPrefix" },
			{ "data": "active" },
		]
	});
	$('#spec-prod').show();

	return currTable;

}
function saveProductGPR(prCode,prgCode) {
	bootbox.confirm("Are you sure want to add this product?", function (result) {
		if (result) {
			$.ajax({
				type: 'POST',
				url: 'addProducts',
				data: {
					'product': prCode,
					'group': prgCode
				}


			}).done(function (result) {
				Swal.fire({
					title: 'Success',
					text: 'Product Added Successfully',
					icon: 'success'
				})
				$('#all-prod').hide();
				$('#spec-prod').show();
				$('#prodList').DataTable().ajax.reload();

			}).fail(function (jqXHR, textStatus, errorThrown) {
				Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });

			})
		}
	})

}
function remProductGPR(prCode,prgCode) {
	bootbox.confirm("Are you sure want to remove this product?", function (result) {
		if (result) {
			$.ajax({
				type: 'POST',
				url: 'remProducts',
				data: {
					'product': prCode,
					'group': prgCode
				}


			}).done(function (result) {
				Swal.fire({
					title: 'Success',
					text: 'Product Removed Successfully',
					icon: 'success'
				})
				$('#prodList').DataTable().ajax.reload();
			}).fail(function (jqXHR, textStatus, errorThrown) {
				Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });

			})
		}
	})
}
function createAllProducts(){
	var url = "products"
	var currTable = $('#allprodList').DataTable( {
		"processing": true,
		"serverSide": true,
		autoWidth: true,
		"ajax": {
			'url': url,
		},
		lengthMenu: [ [10], [15] ],
		pageLength: 15,
		destroy: true,

		"columns": [

			{ "data": "proDesc" },
			{ "data": "proPolPrefix" },
			{ "data": "active" },
		]
	});
	$('#all-prod').show();
	$('#allprodList tbody').on( 'click', 'tr', function () {
		$(this).addClass('table-primary').siblings().removeClass('table-primary');
		var aData = currTable.rows('.table-primary').data();
		if (aData[0] === undefined || aData[0] === null) {

		} else {
			var prCode=aData[0].proCode;
			var prgCode=$('#prg-id').val();
			saveProductGPR(prCode,prgCode);
		}
	})
	return currTable;

}
function blackAndTerm() {
	$('#sel3').on('change',function(){
		if($('#sel3').val()==='B' || $('#sel3').val()==='T'){
			$('#myComments').show();
		}
		else if($('#sel3').val()==='A'){
			$('#myComments').hide();
		}
	})
}
function loadEditModal(){
	$('#user-per').show();
	$('#branch-per').show();
}
function  loadBudgets(){

	$('#user-per').hide();
	$('#branch-per').hide();
	$('input:radio[name="budSet"]').change(
		function(){
			if (this.checked && this.value=='U') {
				$('#user-per').show();
				$('#branch-per').hide();

			}else if (this.checked && this.value=='B') {
				$('#branch-per').show();
				$('#user-per').hide();

			}else if (this.checked && this.value=='UB') {
				$('#user-per').show();
				$('#branch-per').show();

			}else{
				$('#user-per').hide();
				$('#branch-per').hide();
			}
		})
}


function insertExcel(obj) {
	obj.forEach(function (m) {
		//console.log(m.PRODUCT);
		/*
		var objpr = m.PRODUCT || m.product || m.Product;
		var objus = m.USER || m.user || m.User;
		var objbr = m.BRANCH || m.branch || m.Branch;
		*/

		$.ajax({
			type: 'POST',
			url: 'saveExcel',
			data: {
				'agent':m.USER||m.user||m.User,
				'branch':m.BRANCH||m.Branch||m.branch,
				'product':m.PRODUCT||m.product||m.Product,
				'name':m.NAME || m.Name || m.name,
				'year':m.YEAR || m.year || m.Year,
				'janProd':m.JANUARYPREMIUM || m.JanuaryPremium || m.januarypremium || m.JANPREM || m.JanPrem || m.janprem,
				'febProd':m.FEBRUARYPREMIUM || m.FebruaryPremium || m.februarypremium || m.FEBPREM || m.FebPrem || m.febprem,
				'marProd':m.MARCHPREMIUM || m.MarchPremium || m.marchpremium || m.MARPREM || m.MarPrem || m.marprem,
				'aprProd':m.APRILPREMIUM || m.AprilPremium || m.aprilpremium || m.APRPREM || m.AprPrem || m.aprprem,
				'mayProd':m.MAYPREMIUM || m.MayPremium || m.maypremium||m.mayprem||m.MayPrem||m.MAYPREM,
				'junProd':m.JUNEPREMIUM || m.JunePremium || m.junepremium || m.JUNPREM || m.junprem || m.JunPrem,
				'julProd':m.JULYPREMIUM || m.JulyPremium || m.julypremium || m.JulPrem || m.JULPREM || m.julprem,
				'augProd':m.AUGUSTPREMIUM || m.AugustPremium || m.augustpremium || m.AUGPREM || m.AugPrem || m.augprem,
				'sepProd':m.SEPTEMBERPREMIUM || m.SeptemberPremium || m.septemberpremium || m.SepPrem || m.SEPPREM || m.sepprem,
				'octProd':m.OCTOBERPREMIUM || m.OctoberPremium || m.octoberpremium || m.OCTPREM || m.OctPrem || m.octprem,
				'novProd':m.NOVEMBERPREMIUM || m.NovemberPremium || m.novemberpremium || m.NOVPREM || m.NovPrem || m.novprem,
				'decProd':m.DECEMBERPREMIUM || m.DecemberPremium || m.decemberpremium || m.DECPREM || m.DecPrem || m.decprem,
				'janPol':m.JANUARYPOLICIES || m.JanuaryPolicies || m.januarypolicies || m.JANPOL || m.JanPol || m.janpol,
				'febPol':m.FEBRUARYPOLICIES || m.FebruaryPolicies || m.februarypolicies || m.FEBPOL || m.FebPol || m.febpol,
				'marPol':m.MARCHPOLICIES || m.MarchPolicies || m.marchpolicies || m.MARPOL || m.MarPol || m.marpol,
				'aprPol':m.APRILPOLICIES || m.AprilPolicies || m.aprilpolicies || m.APRPOL || m.AprPol || m.aprpol,
				'mayPol':m.MAYPOLICIES || m.MayPolicies || m.maypolicies||m.MAYPOL||m.MayPol||m.maypol,
				'junPol':m.JUNEPOLICIES || m.JunePolicies || m.junepolicies || m.JUNPOL || m.junpol || m.JunPol,
				'julPol':m.JULYPOLICIES || m.JulyPolicies || m.julypolicies || m.JulPol || m.JULPOL || m.julpol,
				'augPol':m.AUGUSTPOLICIES || m.AugustPolicies || m.augustpolicies || m.AUGPOL || m.AugPol || m.augpol,
				'sepPol':m.SEPTEMBERPOLICIES || m.SeptemberPolicies || m.septemberpolicies || m.SepPol || m.SEPPOL || m.seppol,
				'octPol':m.OCTOBERPOLICIES || m.OctoberPolicies || m.octoberpolicies || m.OCTPOL || m.OctPol || m.octpol,
				'novPol':m.NOVEMBERPOLICIES || m.NovemberPolicies || m.novemberpolicies || m.NOVPOL|| m.NovPol || m.novpol,
				'decPol':m.DECEMBERPOLICIES || m.DecemberPolicies || m.decemberpolicies || m.DECPOL || m.DecPol || m.decpol
			}
		}).done(function (s) {
			Swal.fire({
				title: 'Success',
				text: 'Budget Added Successfully',
				icon: 'success'
			})
			$("#budgets-tbl").DataTable().ajax.reload();

		}).fail(function (xhr, error) {
			Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            })
			$('#btn-view-unloaded').show();
			$('#btn-delete-unloaded').show();
			$('#btn-download-excel').show();
		})


	})



}
function saveExcelBudget() {
	var myForm = $("#budget-form")[0];
	var data = new FormData(myForm);
	$.ajax({
		type: 'POST',
		url: 'createBudget',
		data: data,
		processData: false,
		contentType: false,
	}).done(function (s) {
		Swal.fire({
			title: 'Success',
			text: 'Budget Added Successfully',
			icon: 'success'
		})
		$("#budgets-tbl").DataTable().ajax.reload();
		$("#budgetModal").modal('hide');
		$('#budgetModal').on('hidden.bs.modal', function () {
			$('#budget-form')[0].reset();
			jQuery('.select2-offscreen').select2('val', '');
		});

	}).fail(function (xhr, error) {
		Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
		$("#budgetModal").modal('hide');

		$('#budgetModal').on('hidden.bs.modal', function () {
			$('#budget-form')[0].reset();
			jQuery('.select2-offscreen').select2('val', '');
		});

	});

}


function createRptGrpSelectLov(){
	if($("#rpt-group").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "rpt-group",
			sort : 'repDesc',
			change: function(e, a, v){
				$("#rpt-id").val(e.added.rptId);
				createLife($("#rpt-id").val());
			},
			formatResult : function(a)
			{
				return a.repDesc
			},
			formatSelection : function(a)
			{
				return a.repDesc
			},
			initSelection: function (element, callback) {
				var code = $("#rpt-id").val();
				var name = $("#rpt-name").val();
				var data = {repDesc:name,rptId:code};
				callback(data);
			},
			id: "rptId",
			placeholder:"Select Report Group",
		});
	}
}
function createProdGrpSelect(){
	if($("#prd-group").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "prd-group",
			sort : 'prgDesc',
			change: function(e, a, v){
				$("#prg-id").val(e.added.prgCode);
				$("#prg-code").val(e.added.prgCode);
				$("#prg-name").val(e.added.prgDesc);
				$("#prg-type").val(e.added.prgType);
				var prg=$("#prg-id").val();
				console.log('added');

			},
			formatResult : function(a)
			{
				return a.prgDesc
			},
			formatSelection : function(a)
			{
				return a.prgDesc
			},
			initSelection: function (element, callback) {
//	            	var code = $("#obId").val();
//	                var name = $("#ob-name").val();
//	                model.accounts.branch.brnCode = code;
//	                var data = {obName:name,obId:code};
//	                callback(data);
			},
			id: "prgCode",
			placeholder:"Select Product Group",
		});
	}
}

function populateYear(){
	if($("#myYear").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "myYear",
			sort : 'year',
			change: function(e, a, v){
				$("#myValue").val(e.added.date);
				$("#yearname").val(e.added.year)
			},
			formatResult : function(a)
			{
				return a.year
			},
			formatSelection : function(a)
			{
				return a.year
			},
			initSelection: function (element, callback) {
				var code = $("#myValue").val();
				var name = $("#yearname").val();
				var data = {year:name,date:code};
				callback(data);
			},
			id: "date",
			placeholder:"Select Year",
		});
	}
}
function createProdGrpSelect1(){
	if($("#prd-group1").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "prd-group1",
			sort : 'prgDesc',
			change: function(e, a, v){
				$("#prg-id1").val(e.added.prgCode);
				$("#prg-name1").val(e.added.prgDesc);
				$("#grp-desc").val(e.added.prgDesc);
				$("#prg-type").val(e.added.prgType);
			},
			formatResult : function(a)
			{
				return a.prgDesc
			},
			formatSelection : function(a)
			{
				return a.prgDesc
			},
			initSelection: function (element, callback) {
				var code = $("#prg-id1").val();
				var name = $("#prg-name1").val();
				//         model.accounts.branch.brnCode = code;
				var data = {prgDesc:name,prgCode:code};
				callback(data);
			},
			id: "prgCode",
			placeholder:"Select Product Group",
		});
	}
}
function createProdGrpSelect2(){
	if($("#prd-group11").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "prd-group11",
			sort : 'prgDesc',
			change: function(e, a, v){
				$("#prg-id11").val(e.added.prgCode);
				$("#grp-desc").val(e.added.prgDesc);
				$("#prg-type").val(e.added.prgType);
			},
			formatResult : function(a)
			{
				return a.prgDesc
			},
			formatSelection : function(a)
			{
				return a.prgDesc
			},
			initSelection: function (element, callback) {
//	            	var code = $("#obId").val();
//	                var name = $("#ob-name").val();
//	                model.accounts.branch.brnCode = code;
//	                var data = {obName:name,obId:code};
//	                callback(data);
			},
			id: "prgCode",
			placeholder:"Select Product Group",
		});
	}
}

var model = {
	tenant: {
		branch:{
			brnCode:"",
			struct:{
				rentalId: "",
				units:{
					renId: "",
				},
			},
		},

	}
};
function addEditProductRptGroup(){
	var $currForm = $('#prgrpt-group-form');
	var currValidator = $currForm.validate();


	$('#saveproductRptGrpBtn').click(function(){
		if (!$currForm.valid()) {
			return;
		}
		var $btn = $(this).button('Saving');
		var data = {};
		$currForm.serializeArray().map(function(x){data[x.name] = x.value;});
		var url = "createRptGroup";
		var request = $.post(url, data );
		request.success(function(){
			Swal.fire({
				title: 'Success',
				text: 'Record Created/Updated Successfully',
				icon: 'success'
			})
			currValidator.resetForm();


			$('#prgrpt-group-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
			$('#prodRptGrpModal').modal('hide');
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
function searchBud(dvt){
	var url="searchBud/"+dvt;
	var table = $('#budgets-tbl').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax": url,
		lengthMenu: [ [10, 15], [10, 15] ],
		pageLength: 10,
		destroy: true,
		"columns": [

			{ "data": "name" },
			{ "data": "productGroupDef",
				"render": function ( data, type, full, meta ) {
					return full.productGroupDef.prgDesc
				}
			},
			{ "data": "accountDef" ,
				"render": function ( data, type, full, meta ) {
					return full.accountDef.name
				}
			},
			{ "data": "orgBranch",
				"render": function ( data, type, full, meta ) {
					return full.orgBranch.obName
				}
			},
			{ "data": "year" },
			{ "data": "jan" },
			{ "data": "feb" },
			{ "data": "march" },
			{ "data": "april" },
			{ "data": "may" },
			{ "data": "june" },
			{ "data": "july" },
			{ "data": "aug" },
			{ "data": "sep" },
			{ "data": "oct" },
			{ "data": "nov" },
			{ "data": "dec" },
			{
				"data": "id",
				"render": function ( data, type, full, meta ) {
					return '<form id="editForm" method="post" enctype="multipart/form-data"><input type="hidden" id="editId" name="id" value='+full.id+'></form><button class="btn btn-success btn-sm btn-edit" >Edit</button>';

				}

			},
			{
				"data": "id",
				"render": function ( data, type, full, meta ) {
					return '<form method="post" enctype="multipart/form-data"><input type="hidden" name="id" value='+full.id+'></form><button class="btn btn-danger btn-sm btn-delete" >Delete</button>';
				}

			}
		]
	} );

	return table;
}
function createBudget(){
	console.log($('#find-id').val())
	var url = "getBudgets";
	var table = $('#budgets-tbl').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax":{
			"url":url,
			'data':{
				'product':$('#find-id').val()
			},
		},

		lengthMenu: [ [10, 15], [10, 15] ],
		pageLength: 10,
		destroy: true,
		"columns": [

			{ "data": "name" },

			{ "data": "productReportGroup",
				"render": function ( data, type, full, meta ) {
					if(full.productReportGroup!==null){
						return full.productReportGroup.repDesc
					}else{
						return 'Not Set'
					}
				}
			},
			{ "data": "accountDef" ,
				"render": function ( data, type, full, meta ) {
					if(full.accountDef!==null){
						return full.accountDef.name
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "orgBranch",
				"render": function ( data, type, full, meta ) {
					if(full.orgBranch!==null){
						return full.orgBranch.obName
					}
					else{
						return "Not Set";
					}				}
			},


			{ "data": "year"},
			{ "data": "janProd",
				"render": function ( data, type, full, meta ) {
					if(full.janProd!==null){
						return full.janProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "janPol",
				"render": function ( data, type, full, meta ) {
					if(full.janPol!==null){
						return full.janPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "febProd",
				"render": function ( data, type, full, meta ) {
					if(full.febProd!==null){
						return full.febProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "febPol",
				"render": function ( data, type, full, meta ) {
					if(full.febPol!==null){
						return full.febPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "marProd",
				"render": function ( data, type, full, meta ) {
					if(full.marProd!==null){
						return full.marProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "marPol",
				"render": function ( data, type, full, meta ) {
					if(full.marPol!==null){
						return full.marPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "aprProd",
				"render": function ( data, type, full, meta ) {
					if(full.aprProd!==null){
						return full.aprProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "aprPol",
				"render": function ( data, type, full, meta ) {
					if(full.aprPol!==null){
						return full.aprPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "mayProd",
				"render": function ( data, type, full, meta ) {
					if(full.mayProd!==null){
						return full.mayProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "mayPol",
				"render": function ( data, type, full, meta ) {
					if(full.mayPol!==null){
						return full.mayPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "junProd",
				"render": function ( data, type, full, meta ) {
					if(full.junProd!==null){
						return full.junProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "junPol",
				"render": function ( data, type, full, meta ) {
					if(full.junPol!==null){
						return full.junPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "julProd",
				"render": function ( data, type, full, meta ) {
					if(full.julProd!==null){
						return full.julProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "julPol",
				"render": function ( data, type, full, meta ) {
					if(full.julPol!==null){
						return full.julPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "augProd",
				"render": function ( data, type, full, meta ) {
					if(full.augProd!==null){
						return full.augProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "augPol",
				"render": function ( data, type, full, meta ) {
					if(full.augPol!==null){
						return full.augPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "sepProd",
				"render": function ( data, type, full, meta ) {
					if(full.sepProd!==null){
						return full.sepProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "sepPol" ,
				"render": function ( data, type, full, meta ) {
					if(full.sepPol!==null){
						return full.sepPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "octProd",
				"render": function ( data, type, full, meta ) {
					if(full.octProd!==null){
						return full.octProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "octPol",
				"render": function ( data, type, full, meta ) {
					if(full.octPol!==null){
						return full.octPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "novProd",
				"render": function ( data, type, full, meta ) {
					if(full.novProd!==null){
						return full.novProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "novPol",
				"render": function ( data, type, full, meta ) {
					if(full.novPol!==null){
						return full.novPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "decProd",
				"render": function ( data, type, full, meta ) {
					if(full.decProd!==null){
						return full.decProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "decPol",
				"render": function ( data, type, full, meta ) {
					if(full.decPol!==null){
						return full.decPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{
				"data": "id",
				"render": function ( data, type, full, meta ) {
					return '<form id="editForm" method="post" enctype="multipart/form-data"><input type="hidden" id="editId" name="id" value='+full.id+'></form><button class="btn btn-success btn-sm btn-edit" ><i class="fa fa-pencil-square-o"></button>';

				}

			},

			{
				"data": "id",
				"render": function ( data, type, full, meta ) {
					return '<form method="post" enctype="multipart/form-data"><input type="hidden" name="id" value='+full.id+'></form><button class="btn btn-danger btn-sm btn-delete" ><i class="fa fa-trash-o"></button>';
				}
			}
		]
	} );

	return table;

}
function createUnloadedBudget(){
	var url = "getUnloadedBudgets";
	var table = $('#unloaded-budgets-tbl').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax":{
			"url":url,
		},

		lengthMenu: [ [10, 15], [10, 15] ],
		pageLength: 10,
		destroy: true,
		"columns": [

			{ "data": "name",
				"render": function ( data, type, full, meta ) {
					if(full.name!==null){
						return full.name
					}else{
						return 'Not Set'
					}
				}
			},

			{ "data": "productReportGroup",
				"render": function ( data, type, full, meta ) {
					if(full.productReportGroup!==null){
						return full.productReportGroup
					}else{
						return 'Not Set'
					}
				}
			},
			{ "data": "accountDef" ,
				"render": function ( data, type, full, meta ) {
					if(full.accountDef!==null){
						return full.accountDef
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "branch",
				"render": function ( data, type, full, meta ) {
					if(full.orgBranch!==null){
						return full.branch
					}
					else{
						return "Not Set";
					}				}
			},


			{ "data": "year",
				"render": function ( data, type, full, meta ) {
					if(full.year!==null){
						return full.year
					}else{
						return 'Not Set'
					}
				}
			},
			{"data":'error'},
			{ "data": "janProd",
				"render": function ( data, type, full, meta ) {
					if(full.janProd!==null){
						return full.janProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "janPol",
				"render": function ( data, type, full, meta ) {
					if(full.janPol!==null){
						return full.janPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "febProd",
				"render": function ( data, type, full, meta ) {
					if(full.febProd!==null){
						return full.febProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "febPol",
				"render": function ( data, type, full, meta ) {
					if(full.febPol!==null){
						return full.febPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "marProd",
				"render": function ( data, type, full, meta ) {
					if(full.marProd!==null){
						return full.marProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "marPol",
				"render": function ( data, type, full, meta ) {
					if(full.marPol!==null){
						return full.marPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "aprProd",
				"render": function ( data, type, full, meta ) {
					if(full.aprProd!==null){
						return full.aprProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "aprPol",
				"render": function ( data, type, full, meta ) {
					if(full.aprPol!==null){
						return full.aprPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "mayProd",
				"render": function ( data, type, full, meta ) {
					if(full.mayProd!==null){
						return full.mayProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "mayPol",
				"render": function ( data, type, full, meta ) {
					if(full.mayPol!==null){
						return full.mayPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "junProd",
				"render": function ( data, type, full, meta ) {
					if(full.junProd!==null){
						return full.junProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "junPol",
				"render": function ( data, type, full, meta ) {
					if(full.junPol!==null){
						return full.junPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "julProd",
				"render": function ( data, type, full, meta ) {
					if(full.julProd!==null){
						return full.julProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "julPol",
				"render": function ( data, type, full, meta ) {
					if(full.julPol!==null){
						return full.julPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "augProd",
				"render": function ( data, type, full, meta ) {
					if(full.augProd!==null){
						return full.augProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "augPol",
				"render": function ( data, type, full, meta ) {
					if(full.augPol!==null){
						return full.augPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "sepProd",
				"render": function ( data, type, full, meta ) {
					if(full.sepProd!==null){
						return full.sepProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "sepPol" ,
				"render": function ( data, type, full, meta ) {
					if(full.sepPol!==null){
						return full.sepPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "octProd",
				"render": function ( data, type, full, meta ) {
					if(full.octProd!==null){
						return full.octProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "octPol",
				"render": function ( data, type, full, meta ) {
					if(full.octPol!==null){
						return full.octPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "novProd",
				"render": function ( data, type, full, meta ) {
					if(full.novProd!==null){
						return full.novProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "novPol",
				"render": function ( data, type, full, meta ) {
					if(full.novPol!==null){
						return full.novPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "decProd",
				"render": function ( data, type, full, meta ) {
					if(full.decProd!==null){
						return full.decProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "decPol",
				"render": function ( data, type, full, meta ) {
					if(full.decPol!==null){
						return full.decPol;
					}
					else{
						return "Not Set";
					}
				}

			},

		]
	} );
	$('#unloaded').show();
	return table;

}

function setProductDef() {
	var data=$("#prg-id").val();
	$("#productid").val(data);
}

function createLife(id){

	var url = "getBudgetsLife/"+id;

	var table = $('#budgets-tbl').DataTable( {
		"processing": true,
		"serverSide": true,
		"ajax":{
			"url":url
		},
		lengthMenu: [ [10, 15], [10, 15] ],
		pageLength: 10,
		"columns": [
			{ "data": "name" },
			{ "data": "productReportGroup",
				"render": function ( data, type, full, meta ) {
					if(full.productReportGroup!==null){
						return full.productReportGroup.repDesc
					}else{
						return 'Not Set'
					}
				}
			},
			{ "data": "accountDef" ,
				"render": function ( data, type, full, meta ) {
					if(full.accountDef!==null){
						return full.accountDef.name
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "orgBranch",
				"render": function ( data, type, full, meta ) {
					if(full.orgBranch!==null){
						return full.orgBranch.obName
					}
					else{
						return "Not Set";
					}				}
			},

			{ "data": "janProd",
				"render": function ( data, type, full, meta ) {
					if(full.janProd!==null){
						return full.janProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "janPol",
				"render": function ( data, type, full, meta ) {
					if(full.janPol!==null){
						return full.janPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "febProd",
				"render": function ( data, type, full, meta ) {
					if(full.febProd!==null){
						return full.febProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "febPol",
				"render": function ( data, type, full, meta ) {
					if(full.febPol!==null){
						return full.febPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "marProd",
				"render": function ( data, type, full, meta ) {
					if(full.marProd!==null){
						return full.marProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "marPol",
				"render": function ( data, type, full, meta ) {
					if(full.marPol!==null){
						return full.marPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "aprProd",
				"render": function ( data, type, full, meta ) {
					if(full.aprProd!==null){
						return full.aprProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "aprPol",
				"render": function ( data, type, full, meta ) {
					if(full.aprPol!==null){
						return full.aprPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "mayProd",
				"render": function ( data, type, full, meta ) {
					if(full.mayProd!==null){
						return full.mayProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "mayPol",
				"render": function ( data, type, full, meta ) {
					if(full.mayPol!==null){
						return full.mayPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "junProd",
				"render": function ( data, type, full, meta ) {
					if(full.junProd!==null){
						return full.junProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "junPol",
				"render": function ( data, type, full, meta ) {
					if(full.junPol!==null){
						return full.junPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "julProd",
				"render": function ( data, type, full, meta ) {
					if(full.julProd!==null){
						return full.julProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "julPol",
				"render": function ( data, type, full, meta ) {
					if(full.julPol!==null){
						return full.julPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "augProd",
				"render": function ( data, type, full, meta ) {
					if(full.augProd!==null){
						return full.augProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "augPol",
				"render": function ( data, type, full, meta ) {
					if(full.augPol!==null){
						return full.augPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "sepProd",
				"render": function ( data, type, full, meta ) {
					if(full.sepProd!==null){
						return full.sepProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "sepPol" ,
				"render": function ( data, type, full, meta ) {
					if(full.sepPol!==null){
						return full.sepPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "octProd",
				"render": function ( data, type, full, meta ) {
					if(full.octProd!==null){
						return full.octProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "octPol",
				"render": function ( data, type, full, meta ) {
					if(full.octPol!==null){
						return full.octPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "novProd",
				"render": function ( data, type, full, meta ) {
					if(full.novProd!==null){
						return full.novProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "novPol",
				"render": function ( data, type, full, meta ) {
					if(full.novPol!==null){
						return full.novPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "decProd",
				"render": function ( data, type, full, meta ) {
					if(full.decProd!==null){
						return full.decProd;
					}
					else{
						return "Not Set";
					}
				}

			},
			{ "data": "decPol",
				"render": function ( data, type, full, meta ) {
					if(full.decPol!==null){
						return full.decPol;
					}
					else{
						return "Not Set";
					}
				}

			},
			{
				"data": "id",
				"render": function ( data, type, full, meta ) {
					return '<form id="editForm" method="post" enctype="multipart/form-data"><input type="hidden" id="editId" name="id" value='+full.id+'></form><button class="btn btn-success btn-sm btn-edit" ><i class="fa fa-pencil-square-o"></button>';

				}

			},
			{
				"data": "id",
				"render": function ( data, type, full, meta ) {
					return '<form method="post" enctype="multipart/form-data"><input type="hidden" name="id" value='+full.id+'></form><button class="btn btn-danger btn-sm btn-delete" ><i class="fa fa-trash-o"></button>';
				}

			}
		]
	} );

	return table;

}
function saveBudget(){
	var $form= $("#budget-form");
	var validator = $form.validate();

	$('form#budget-form')
		.submit( function( e ) {
			e.preventDefault();

			if (!$form.valid()) {
				return;
			}
			var buds=$("#myBudgetId").val();

			if(buds!=="") {
				var data = new FormData( this );
				console.log(data);
				$.ajax({
					type: 'POST',
					url: 'editBudget',
					data: data,
					processData: false,
					contentType: false,
				}).done(function (s) {
					Swal.fire({
						title: 'Success',
						text: 'Budget Edited Successfully',
						icon: 'success'
					})
					$("#budgets-tbl").DataTable().ajax.reload();
				}).fail(function (xhr, error) {
					Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
				});
				$("#budgetModal").modal('hide');

				$('#budgetModal').on('hidden.bs.modal', function () {
					$("#myBudgetId").val('');
					$('#budget-form')[0].reset();
					jQuery('.select2-offscreen').select2('val', '');

				});
			}else{
				var data = new FormData(this);
				console.log(data);
				$.ajax({
					type: 'POST',
					url: 'createBudget',
					data: data,
					processData: false,
					contentType: false,
				}).done(function (s) {
					Swal.fire({
						title: 'Success',
						text: 'Budget Added Successfully',
						icon: 'success'
					})
					$("#budgets-tbl").DataTable().ajax.reload();

				}).fail(function (xhr, error) {
					Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });

				});
				$("#budgetModal").modal('hide');

				$('#budgetModal').on('hidden.bs.modal', function () {
					$('#budget-form')[0].reset();
					jQuery('.select2-offscreen').select2('val', '');
				});
				checkUnloaded();
			}
		});
}

function populateUserA(){
	if($("#sub-agent-frm").filter("div").html() != undefined)
	{
		Select2Builder.initAjaxSelect2({
			containerId : "sub-agent-frm",
			sort : 'name',
			change: function(e, a, v){
				$("#sub-agent-id").val(e.added.acctId);

			},
			formatResult : function(a)
			{
				return a.name;
			},
			formatSelection : function(a)
			{
				return a.name;
			},
			initSelection: function (element, callback) {
				var code  = $('#sub-agent-id').val();
				var name = $("#sub-agent-name").val();
				var data = {name:name,acctId:code};
				callback(data);
			},
			id: "acctId",
			placeholder:"Select User",

		});
	}
}
