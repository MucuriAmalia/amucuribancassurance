var UTILITIES = UTILITIES || {};
$(function(){

	$(document).ready(function() {
		    createReceipts();
	})
});


function createReceipts() {
	var url = "receipts";
	var currTable = $('#rcts-tbl')
			.DataTable(
					{
						"processing" : true,
						"serverSide" : true,
						"ajax" : url,
						lengthMenu : [ [ 20, 30, 40, 50 ], [ 20, 30, 40, 50 ] ],
						pageLength : 20,
						destroy : true,
						"columns" : [
								{
									"data" : "receiptNo"
								},
								{
									"data" : "receiptDate",
									"render" : function(data, type, full, meta) {
										return moment(full.receiptDate).format(
												'DD/MM/YYYY');
									}

								},
								{
									"data" : "receiptUser",
									"render" : function(data, type, full, meta) {
										return full.receiptUser.username;
									}
								},
								{
									"data" : "paymentMode",
									"render" : function(data, type, full, meta) {
										return full.collectionAccount.paymentModes.pmDesc;
									}
								},
								{
									"data" : "receiptAmount",
									"render" : function(data, type, full, meta) {
										return UTILITIES.currencyFormat(full.receiptAmount);
									}
								},
								{
									"data" : "paidBy"
								},
								{
									"data" : "printed"
								},
								]
					});

	return currTable;
}