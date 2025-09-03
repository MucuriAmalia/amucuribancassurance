
var UnprintedReceipts = (function($) {
    'use strict';


    var createReceipts = function() {
        var rows_selected = [];
        var url = "unprintedReceipts";
        var table = $('#rcts-tbl')
            .DataTable(
                {
                    "processing" : true,
                    "serverSide" : true,
                    "ajax": {
                        'url': url,
                        'data': {
                            'dateFrom': $("#from-date").val(),
                            'dateTo': $("#wet-date").val()
                        },
                    },
                    lengthMenu : [ [ 20, 30, 40, 50 ], [ 20, 30, 40, 50 ] ],
                    pageLength : 20,
                    destroy : true,
                    'columnDefs': [{
                        'targets': 7,
                        'searchable':false,
                        'orderable':false,
                        'render': function (data, type, full, meta){
                            return '<input type="checkbox" name="id[]" value="'
                                + $('<div/>').text(data).html() + '">';
                        }
                    }],
                    "columns" : [
                        {
                            "data" : "receiptNo"
                        },
                        {
                            "data" : "receiptDate",
                            "render" : function(data, type, full, meta) {
                                return full.receiptDate;
                            }

                        },
                        {
                            "data" : "receiptDesc"
                        },
                        {
                            "data" : "receiptAmount",
                            "render" : function(data, type, full, meta) {
                                return UTILITIES.currencyFormat(full.receiptAmount);
                            }
                        },
                        {
                            "data" : "paymentMode",
                            "render" : function(data, type, full, meta) {
                                if (full.collectionAccount) {
                                    return full.collectionAccount.paymentModes.pmDesc;
                                } else {
                                    return "";
                                }
                            }
                        },
                        {
                            "data" : "paymentRef"
                        },
                        {
                            "data" : "paidBy"
                        },
                        {
                            "data" : "receiptId"
                        },

                    ]
                });
        return table;
    }
    function printPdf(url){
        $("#receipt-div").attr('src', url);
        $('#printReceiptModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    }


    var printUnprintedReceipts = function(){
        $('#btn-receipt-print').on('click', function(e) {
            var arr = [];
            var currTable = $('#rcts-tbl').dataTable();
            currTable.$('input[type="checkbox"]').each(function () {
                if (this.checked) {
                    arr.push(this.value);
                }

            });

            if (arr.length == 0) {
                bootbox.alert("No Record Selected to Print..");
                return;
            }
            $.ajax({
                type: 'GET',
                url: 'deleteReceipts',
                dataType: 'json',
                async: true,
                success: function (s) {

                },
                error: function (jqXHR, textStatus, errorThrown) {

                },
                contentType: "application/json"
            });

            var $currForm = $('#print-form');
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }
            var data = {};
            $currForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            data.receipts = arr;
            console.log(data);
            var url = "printReceipts";
            $.ajax({
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    printPdf(SERVLET_CONTEXT + "/protected/uw/receipts/printbatchreceipt");
                    arr = [];
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    //new PNotify({
                    //    title: 'Error',
                    //    text: jqXHR.responseText,
                    //    type: 'error',
                    //    styling: 'bootstrap3'
                    //});
                },
                dataType: "json",
                contentType: "application/json"
            });
        });

        $('#printReceipt').on('click', function(e){
            var arr = [];
            var currTable = $('#rcts-tbl').dataTable();
            currTable.$('input[type="checkbox"]').each(function () {
                if (this.checked) {
                    arr.push(this.value);
                }

            });

            if (arr.length == 0) {
                bootbox.alert("No Record Selected to Mark Printed..");
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var $currForm = $('#print-form');
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }
            var data = {};
            $currForm.serializeArray().map(function(x) {
                data[x.name] = x.value;
            });
            data.receipts = arr;
            var url = "markReceiptsPrinted";
            $.ajax({
                url : url,
                type : "POST",
                data : JSON.stringify(data),
                success : function(s) {
                    // $('#myPleaseWait').modal('hide');
                    $('#printReceiptModal').modal('hide');
                    $('#rcts-tbl').DataTable().ajax.reload();
                    Swal.fire({
                        title: 'Success',
                        text: 'Process Successfully',
                        icon: 'success'
                    });
                    arr = [];
                },
                error : function(jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                },
                dataType : "json",
                contentType : "application/json"
            });
        });
    }

    var init = function(){
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });


        $("#btn-search-receipts").on('click', function(){
            var wef = $("#from-date").val();
            var wet = $("#wet-date").val();
            if(wef==='' && wet===''){
                bootbox.alert("Provide At least one Search Parameter");
                return;
            }
            createReceipts();
            printUnprintedReceipts();

        });
    }

    return {
        init:init
    }
})(jQuery);

jQuery(UnprintedReceipts.init);