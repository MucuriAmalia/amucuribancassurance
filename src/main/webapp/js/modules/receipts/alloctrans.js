/**
 * Created by peter on 3/25/2017.
 */

var UTILITIES = UTILITIES || {};
$(function(){

    $(document).ready(function() {
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        $("#total-val").val(0);
        populateClientLov();
        populateCurrencyLov();
        populateUserBranches();
        creditTrans();
        debitTrans();

        $("#btn-search-trans").on('click', function(){
            creditTrans();
            debitTrans();
        });

        $('#chk-balance').change(function() {
           // if ($(this).is(":checked")) {
                creditTrans();
                debitTrans();
           // }
        });

    })
});


function populateUserBranches(){
    if($("#brn-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "brn-frm",
            sort : 'obName',
            change: function(e, a, v){
                $("#brn-id").val(e.added.obId);
            },
            formatResult : function(a)
            {
                return a.obName;
            },
            formatSelection : function(a)
            {
                return a.obName;
            },
            initSelection: function (element, callback) {

            },
            id: "obId",
            width:"250px",
            placeholder:"All Branches"

        });
    }
    $("#brn-frm").on("select2-removed", function(e) {
        $("#brn-id").val('');
    });
}


function populateCurrencyLov(){
    if($("#curr-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "curr-frm",
            sort : 'curName',
            change: function(e, a, v){
                $("#cur-id").val(e.added.curCode);

            },
            formatResult : function(a)
            {
                return a.curName;
            },
            formatSelection : function(a)
            {
                return a.curName;
            },
            initSelection: function (element, callback) {

            },
            id: "curCode",
            width:"250px",
            placeholder:"Select Currency"

        });
    }
}

function populateClientLov(){
    if($("#client-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "client-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#client-id").val(e.added.tenId);

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
            placeholder:"All Clients"

        });
    }
    $("#client-frm").on("select2-removed", function(e) {
        $("#client-id").val('');
    });
}


function creditTrans() {
    var checkVal = "N";
    if ($('#chk-balance').is(":checked"))
    {
        checkVal = "Y";
    }
    var url = "credittrans";
    var currTable = $('#credits-tbl')
        .DataTable(
            {
                "processing" : true,
                "serverSide" : true,
                "ajax": {
                    'url': url,
                    'data': {
                        'currCode': $("#cur-id").val(),
                        'clientCode':$("#client-id").val(),
                        'brnCode': $("#brn-id").val(),
                        'alltrans':  checkVal,
                    }
                },
                lengthMenu : [ [ 10 ], [ 10] ],
                pageLength : 10,
                destroy : true,
                "columns" : [
                    {
                        "data" : "refNo"
                    },
                    {
                        "data" : "transDate",
                        "render" : function(data, type, full, meta) {
                            return moment(full.transDate).format('DD/MM/YYYY');
                        }
                    },
                    {
                        "data" : "narrations",
                        "render" : function(data, type, full, meta) {
                            return full.narrations;
                        }
                    },
                    {
                        "data" : "transType"
                    },
                    {
                        "data" : "amount",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.amount);
                        }
                    },
                    {
                        "data" : "balance",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.balance);
                        }
                    },
                    {
                        "data" : "payeeName",
                        "render" : function(data, type, full, meta) {
                            return full.payeeName;
                        }
                    },
                ]
            });
    $('#credits-tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null){

        }
        else{
            $("#cr-trans-pk").val(aData[0].transno);
            creditSettlements(aData[0].transno);
        }
    } );
    return currTable;
}


function allocateTransaction(button){
    var settlements = JSON.parse(decodeURI($(button).data("settlements")));
    var amt = $('#rctamt-' + settlements["transno"] + '').val();
    console.log("here....");
    $.ajax({
        type: 'GET',
        url: 'allocateTrans',
        dataType: 'json',
        data: {"crTransNo": $("#cr-trans-pk").val(),"drTransNo":settlements["transno"],"allocAmount":amt},
        async: true,
        success: function (result) {
            Swal.fire({
                title: 'Success',
                text: 'Allocation Process Successfully',
                icon: 'success'
            });
            $('#credits-tbl').DataTable().ajax.reload();
            $('#debits-tbl').DataTable().ajax.reload();
        },
        error: function (jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });
}

function getPayableAmount(button){
    var settlements = JSON.parse(decodeURI($(button).data("settlements")));
    if(!$($(button).context).is(':checked')) {
        $('#rctamt-' + settlements["transno"] + '').attr('disabled',true);
    }
    else{
        $('#rctamt-' + settlements["transno"] + '').attr('disabled',false);
        $('#rctamt-' + settlements["transno"] + '').val("");
    }

    if($($(button).context).is(':checked')) {
        $.ajax({
            type: 'GET',
            url: 'getAllocatedAmt',
            dataType: 'json',
            data: {"transId": settlements["transno"]},
            async: true,
            success: function (result) {
                $('#rctamt-' + settlements["transno"] + '').val(result);
                $('[id^=rctamt-]').number(true, 2);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            }
        });
    }
    else{
        $('#rctamt-' + settlements["settlementId"] + '').val(0);
        $('[id^=rctamt-]').number(true, 2);

    }
}


function debitTrans() {
    var checkVal = "N";
    if ($('#chk-balance').is(":checked"))
    {
        checkVal = "Y";
    }
    var url = "debittrans";
    var currTable = $('#debits-tbl')
        .DataTable(
            {
                "processing" : true,
                "serverSide" : true,
                "ajax": {
                    'url': url,
                    'data': {
                        'currCode': $("#cur-id").val(),
                        'clientCode':$("#client-id").val(),
                        'brnCode': $("#brn-id").val(),
                        'alltrans':  checkVal,
                    }
                },
                lengthMenu : [ [ 10 ], [ 10] ],
                pageLength : 10,
                destroy : true,
                "columns" : [
                    {
                        "data" : "refNo"
                    },
                    {
                        "data" : "transDate",
                        "render" : function(data, type, full, meta) {
                            return moment(full.transDate).format('DD/MM/YYYY');
                        }
                    },
                    {
                        "data" : "policy",
                        "render" : function(data, type, full, meta) {
                            return full.policy.polNo;
                        }
                    },
                    {
                        "data" : "transType"
                    },
                    {
                        "data" : "amount",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.amount);
                        }
                    },
                    {
                        "data" : "netAmount",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.netAmount);
                        }
                    },
                    {
                        "data" : "settleAmt",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.settleAmt);
                        }
                    },
                    {
                        "data" : "balance",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.balance);
                        }
                    },
                    {
                        "data" : "balance",
                        "render" : function(data, type, full, meta) {
                            return "<input type='text' disabled size='11' class='form-control'  id='rctamt-" + full.transno + "'/>";
                        }
                    },
                    {
                        "data" : "balance",
                        "render" : function(data, type, full, meta) {
                            return '<input type="checkbox"  data-settlements='+encodeURI(JSON.stringify(full)) + ' onchange="getPayableAmount(this);"/>';

                        }
                    },
                    {
                        "data" : "transno",
                        "render" : function(data, type, full, meta) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-settlements='+encodeURI(JSON.stringify(full)) + '  onclick="allocateTransaction(this);">Allocate</button>';

                        }
                    },
                ]
            });
    $('[id^=rctamt-]').number(true, 2);
    return currTable;
}


function creditSettlements(crNo) {
    var url = "creditSettlements/"+crNo;
    var currTable = $('#debits-settle-tbl')
        .DataTable(
            {
                "processing" : true,
                "serverSide" : true,
                "ajax": {
                    'url': url,
                },
                lengthMenu : [ [ 10 ], [ 10] ],
                pageLength : 10,
                destroy : true,
                "columns" : [
                    {
                        "data" : "debitRefNo"
                    },
                    {
                        "data" : "drCr"
                    },
                    {
                        "data" : "allocatedAmt",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.allocatedAmt);
                        }
                    },
                    {
                        "data" : "allocDate",
                        "render" : function(data, type, full, meta) {
                            return moment(full.allocDate).format('DD/MM/YYYY');
                        }
                    },
                    {
                        "data" : "policy",
                        "render" : function(data, type, full, meta) {
                            return full.debit.policy.polNo;
                        }
                    },
                ]
            });
    return currTable;
}