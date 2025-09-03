/**
 * Created by HP on 3/30/2018.
 */

var CancelReceipts = (function($) {
    'use strict';

    var populateClientLov = function(){
        if($("#client-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "client-frm",
                sort : 'fname',
                change: function(e, a, v){
                    $("#rev-search-name").val(e.added.tenId);

                },
                formatResult : function(a)
                {
                    return a.fname+" "+a.otherNames;
                },
                formatSelection : function(a)
                {
                    return a.fname+" "+a.otherNames;
                },
                initSelection: function (element, callback) {

                },
                id: "tenId",
                placeholder:"Select Client"

            });
        }

        $("#client-frm").on("select2-removed", function(e) {
            $("#rev-search-name").val('');
        })
    }


    var createReceipts = function(wef,wet) {
        var rows_selected = [];
        var url = "cancelReceipts";
        var table = $('#rcts-tbl')
            .DataTable(
                {
                    "processing" : true,
                    "serverSide" : true,
                    "ajax": {
                        'url': url,
                        'data': {
                            'dateFrom': wef,
                            'dateTo':wet,
                            'refno':$("#rev-search-number").val(),
                            'receiptNo':$("#dr-search-number").val(),
                            'clientId':$("#rev-search-name").val(),
                            'policyNo':$("#pol-search-number").val(),
                        },
                    },
                    lengthMenu : [ [ 10,20, 30, 40, 50 ], [ 10,20, 30, 40, 50 ] ],
                    pageLength : 20,
                    destroy : true,
                    'columnDefs': [{
                        'targets': 8,
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
                            "data" : "receiptId",
                            "render" : function(data, type, full, meta) {
                                return " <textarea rows='2' cols='5' class='form-control'  id='cancel-comments-" + full.receiptId + "'/>";
                            }
                        },
                        {
                            "data" : "receiptId"
                        },
                    ]
                });

        return table;
    }


    var cancelReceipts = function(){
        $('#btn-receipt-cancel').one('click', function(e) {
            var arr = [];
            var currTable = $('#rcts-tbl').dataTable();
            currTable.$('input[type="checkbox"]').each(function () {
                if (this.checked) {
                    arr.push({
                        receiptId: this.value,
                        commentl: $('#cancel-comments-' + this.value +  '').val()
                    });
                }

            });

            if (arr.length == 0) {
                bootbox.alert("No Record Selected to Cancel..");
                return;
            }
            var $currForm = $('#print-form');
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }
            var data = {};
            $currForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            data.receipts = arr;
            var url = "cancelReceipts";
            $.ajax({
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    // $('#myPleaseWait').modal('hide');
                    $('#rcts-tbl').DataTable().ajax.reload();
                    Swal.fire({
                        title: 'Success',
                        text: 'Receipt Cancelled Successfully',
                        icon: 'success'
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                },
                dataType: "json",
                contentType: "application/json"
            });
        });
    }

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

        populateClientLov();


        $("#btn-search-receipts").on('click', function(){
            var wef = $("#from-date").val();
            var wet = $("#wet-date").val();
            if(wef==='' ){
                wef="01/01/2011"
            }
            if(wet==='' ){
                wet=moment(new Date()).format('DD/MM/YYYY');
            }
            createReceipts(wef,wet);
            cancelReceipts();

        });
    }

    return {
        init:init
    }
})(jQuery);

jQuery(CancelReceipts.init);