
var ReprintReceipts = (function($) {
    'use strict';


    var createReceipts = function() {
        var rows_selected = [];
        var url = "printedReceipts";
        var table = $('#rcts-tbl')
            .DataTable(
                {
                    "processing" : true,
                    "serverSide" : true,
                    "ajax": {
                        'url': url,
                        'data': {
                            'dateFrom': $("#from-date").val().Date,
                            'dateTo': $("#wet-date").val().Date
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

    var reprintReceipts = function () {
        $('#btn-receipt-print').on('click', function (e) {
            var arr = [];
            var currTable = $('#rcts-tbl').dataTable();
            currTable.$('input[type="checkbox"]').each(function () {
                if (this.checked) {
                    arr.push(this.value);
                }
            });

            if (arr.length === 0) {
                bootbox.alert("No Record Selected to Print..");
                return;
            }

            // Step 1: Delete Receipts
            $.ajax({
                type: 'GET',
                url: 'deleteReceipts',
                dataType: 'json',
                async: true,
                success: function () {
                    console.log("Receipts deleted successfully.");

                    // Step 2: Validate the form and print
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

                    console.log("Receipt data: ", data);

                    var url = "printReceipts";
                    $.ajax({
                        url: url,
                        type: "POST",
                        data: JSON.stringify(data),
                        success: function () {
                            printPdf(SERVLET_CONTEXT + "/protected/uw/receipts/printbatchreceipt");
                            console.log("Receipts printed successfully.");
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            console.error("Error printing receipts: ", errorThrown);
                        },
                        dataType: "json",
                        contentType: "application/json"
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.error("Error deleting receipts: ", errorThrown);
                },
                contentType: "application/json"
            });
        });
    };


    function printPdf(url){
        $("#receipt-div").attr('src', url);
        $('#printReceiptModal').modal({
            backdrop: 'static',
            keyboard: true
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
            reprintReceipts();

        });
    }

    return {
        init:init
    }
})(jQuery);

jQuery(ReprintReceipts.init);