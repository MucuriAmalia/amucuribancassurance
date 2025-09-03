var UTILITIES = UTILITIES || {};
$(function(){

    $(document).ready(function() {
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        $("#total-val").val(0);
        createAccountsForSel();
        populateCurrencyLov();
        // creditorPaymentTrans();
        insuranceTrans();

        $("#btn-search-trans").on('click', function(){
            creditorPaymentTrans();
            insuranceTrans();
            $("#total-val").val(0);
            $(".total").val(0);
            // findCommissionsAudits(-2000);
        });

        // findCommissionsAudits(-2000);
        processTrnsactions();

        // Select/Deselect all checkboxes and trigger their onchange events
        $("#selectAll").change(function() {
            if (this.checked) {
                $("input[type='checkbox'][id^='rct-check-']").each(function() {
                    this.checked = true;
                    // Trigger onchange event to call getPayableCommAmount function
                    $(this).trigger('change');
                });
            } else {
                $("input[type='checkbox'][id^='rct-check-']").each(function() {
                    this.checked = false;
                    // Optionally clear values if required
                    $(this).trigger('change');
                });
            }
        });

        // Function to handle checkbox state and call the required function
        $(document).on('change', "input[type='checkbox'][id^='rct-check-']", function() {
            if (this.checked) {
                // Extracting JSON data stored in the onchange attribute dynamically
                var jsonData = $(this).attr('onchange').match(/\((.*)\)/)[1];
                var dataObj = JSON.parse(jsonData.replace(/\\/g, "").replace(/\'/g, "\""));

                // Call the function with parsed data
                getPayableCommAmount(dataObj);
            }
        });
    });
});



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



function createAccountsForSel(){
    if($("#acc-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "acc-frm",
            sort : 'name',
            change: function(e, a, v){
                $("#agent-search-number").val(e.added.acctId);
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
            id: "acctId",
            placeholder:"Select Insurance Company",
        });
    }

    $("#acc-frm").on("select2-removed", function(e) {
        $("#agent-search-number").val('');
    })
}


function creditorPaymentTrans() {
    if(!$("#from-date").val()){
        bootbox.alert("Select Date from....");
        return;
    }
    if(!$("#wet-date").val()){
        bootbox.alert("Select Date To....");
        return;
    }
    if(!$("#agent-search-number").val()){
        bootbox.alert("Select Insurance Company....");
        return;
    }
    if(!$("#cur-id").val()){
        bootbox.alert("Select Currency....");
        return;
    }
    if(!$("#rec-type").val()){
        bootbox.alert("Select Commission Type to Continue....");
        return;
    }

    var commTypeMapping = {
        'C': 'Commission',
        'A': 'Admin Fee'
    };

    var selectedCommType = $("#rec-type").val();
    var mappedCommType = commTypeMapping[selectedCommType];
    $.ajax({
        type: 'GET',
        url:  'creditortranscomm',
        dataType: 'json',
        data: {'currCode': $("#cur-id").val(),
            'agentCode': $("#agent-search-number").val(),
            'wefDate': $("#from-date").val(),
            'wetDate':  $("#wet-date").val(),
            'commtype': mappedCommType
        },
        async: true,
        success: function(result) {
            $("#credits-tbl tbody").each(function(){
                $(this).remove();
            });
            for(var res in result){
                var isChecked = result[res].commAmt < 0 ? "checked disabled" : "";
                var markup = "<tr>" +
                    "<td><input type='checkbox' id='rct-check-" + result[res].crNo + "-" + result[res].drNo + "' " + isChecked +
                    " onchange='getPayableCommAmount(" + JSON.stringify(result[res]).replace(/\'/g, "") + ")'/></td>" +
                    "<td>" + result[res].polNo + "</td>" +
                    "<td>" + result[res].clientName + "</td>" +
                    "<td class='debit-val'>" + result[res].drNo + "</td>" +
                    "<td class='credit-val'>" + result[res].crNo + "</td>" +
                    // "<td>" + result[res].payStatus + "</td>" +
                    "<td>" + UTILITIES.currencyFormat(result[res].basicPrem) + "</td>" +
                    "<td>" + UTILITIES.currencyFormat(result[res].commAmt) + "</td>" +
                    "<td>" + UTILITIES.currencyFormat(result[res].whtx) + "</td>" +
                    "<td><input type='hidden' id='hidden-rct-amt-" + result[res].crNo + "-" + result[res].drNo + "'>" +
                    "<input type='hidden' class='hidden-trans-type' value='"+ result[res].transType + "'>" +
                    "<input type='hidden' class='hidden-trans-id' value='"+ result[res].transId + "'>" +
                    " <input type='text' disabled size='7' class='form-control cred-alloc-amt'  id='rctamt-" + result[res].crNo +"-" + result[res].drNo + "'/></td>" +
                    "</tr>" ;
                $("#credits-tbl").append(markup);

                if (result[res].commAmt < 0) {
                    getPayableCommAmount(result[res]);
                }
            }
            $('[id^=rctamt-]').number(true, 2);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });
    $('[id^=rctamt-]').number(true, 2);
}


function getTotalAuditAmt(auditId){
    var total = 0;
    $.ajax({
        type: 'GET',
        url: 'getTotalAuditAmt',
        dataType: 'json',
        data: {"transId": auditId},
        async: false,
        success: function (result) {
            total = result;

        },
        error: function (jqXHR, textStatus, errorThrown) {

        }
    });

    return total;
}

function parseNumber(strg) {
    var strg = strg || "";
    var decimal = '.';
    strg = strg.replace(/[^0-9$.,]/g, '');
    if(strg.indexOf(',') > strg.indexOf('.')) decimal = ',';
    if((strg.match(new RegExp("\\" + decimal,"g")) || []).length > 1) decimal="";
    if (decimal != "" && (strg.length - strg.indexOf(decimal) - 1 == 3) && strg.indexOf("0" + decimal)!==0) decimal = "";
    strg = strg.replace(new RegExp("[^0-9$" + decimal + "]","g"), "");
    strg = strg.replace(',', '.');
    return parseFloat(strg);
}

function parseNumberNormalized(str) {
    if (typeof str !== 'string') str = String(str);
    const s = str.trim();
    let negative = false;
    if (s.startsWith('(') && s.endsWith(')')) {
        negative = true;
    }
    // remove parentheses, commas, spaces, currency symbols if any
    const cleaned = s.replace(/[(),\s$£€]/g, '');
    const num = parseFloat(cleaned);
    if (isNaN(num)) {
        console.warn('could not parse number from', str);
        return 0; // or handle error
    }
    return negative ? -num : num;
}

function getPayableCommAmount(button){

    console.log(button);
    var receiptBox = $('#rct-check-' + button["crNo"] + "-" + button["drNo"] +  '');
    var checkField = $('#rctamt-' + button["crNo"] + "-" + button["drNo"] +  '');

    // if(receiptBox.is(':checked')){
    //     checkField.attr('disabled',false);
    // }
    //  else{
    //     checkField.attr('disabled',true);
    // }

    checkField.on('change', function () {
        var previousValue = $(button["locAmt"] + button["crNo"] + "-" + button["drNo"] + '').val();
        console.log("Prev "+previousValue+" curr val "+$(this).val());
        var currVal = parseFloat($(this).val());
        if(currVal >  previousValue) {
            checkField.val(previousValue);
        }
        else {
            checkField.val(currVal);
        }

        var total = 0;
        $("#credits-tbl tr input[type='text']").each(function() {
            if(this.value) {
                total = total + parseNumberNormalized(this.value);
            }
        });
        $("#total-val").val(total);
        $(".total").val(UTILITIES.currencyFormat(total));
    });



    if(receiptBox.is(':checked')){
        console.log('receipt is checked...');
        if(button['transType']==='CLB'){
            var val = -1*button['debitBal'];
            $(button["locAmt"] + button["crNo"] + "-" + button["drNo"] + '').val(val);
            checkField.val(val);
            var total = 0;
            $("#credits-tbl tr input[type='text']").each(function () {
                if (this.value) {
                        total = total + parseNumberNormalized(this.value);
                }
            });

            $("#total-val").val(total);
            $(".total").val(UTILITIES.currencyFormat(total));
            $('[id^=rctamt-]').number(true, 2);
        }
        else {
            $(button["locAmt"] + button["crNo"] + "-" + button["drNo"] + '').val(button['commNetAmt']);
            checkField.val(button['commNetAmt']);
            var total = 0;
            $("#credits-tbl tr input[type='text']").each(function () {

                if (this.value) {
                        total = total + parseNumberNormalized(this.value);
                }
            });

            $("#total-val").val(total);
            $(".total").val(UTILITIES.currencyFormat(total));
            $('[id^=rctamt-]').number(true, 2);
        }
    }
    else{
        checkField.val(0);
        $('[id^=rctamt-]').number(true, 2);
        var total =0;
        $("#credits-tbl tr input[type='text']").each(function() {
            console.log(this.value);
            if(this.value)
                total = total +parseNumberNormalized(this.value);
        });
        console.log(total);
        $("#total-val").val(total);
        $(".total").val(UTILITIES.currencyFormat(total));

    }
}

function processTrnsactions(){
    $("#process_trans").on('click', function(){
        var oTable = $("#credits-tbl");
        var arr = [];
        var selectedRows = oTable.find( 'tbody' ) // select table body and
            .find( 'tr' ) // select all rows that has
            .has( 'input[type=checkbox]:checked' ) // checked checkbox element
        selectedRows.each(function() {
            var credit = $(this).find(".credit-val").html();
            var debit = $(this).find(".debit-val").html();
            var transId = $(this).find(".hidden-trans-id").val();
            var allocAmt = $(this).find(".cred-alloc-amt").val();
            var transType = $(this).find(".hidden-trans-type").val();
            arr.push({
                amount: allocAmt,
                debiTrans: debit,
                creditTrans:credit,
                transType:transType,
                transId:transId,
            });
        });

        if(arr.length==0){
            bootbox.alert("No Records Selected to Process")
            return;
        }
        var $currForm = $('#credits-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "processCreditorCommissions";
        data.credits = arr;
        // $('#myPleaseWait').modal({
        Swal.fire({
            backdrop: 'static',
            keyboard: true
        });

        console.log(JSON.stringify(data));

        $.ajax(
            {
                url:url,
                type: "POST",
                data: JSON.stringify(data),
                success: function(s){
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Processing Successfully',
                        icon: 'success'
                    });
                    creditorPaymentTrans();
                    insuranceTrans();
                    //$('#credits-tbl').DataTable().ajax.reload();
                    $("#total-val").val(0);
                    $(".total").val(0);
                },
                error: function(jqXHR, textStatus, errorThrown){
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                },
                dataType: "json",
                contentType: "application/json"
            } );



    });
}
function insuranceTrans() {
    $(document).ajaxStart(function () {
        $("#instrans-tbl").attr("disabled", true);
    });
    $(document).ajaxComplete(function () {
        $("#instrans-tbl").attr("disabled", false);
    });
    var url = "creditorCommTrans";
    var currTable = $('#instrans-tbl')
        .DataTable(
            {
                "processing" : true,
                "serverSide" : true,
                "ajax": {
                    'url': url,
                    'data': {
                        'currCode': $("#cur-id").val(),
                        'agentCode': $("#agent-search-number").val(),
                        'transType': $("#rec-type").val()
                    }
                },
                lengthMenu : [ [ 20, 30, 40, 50 ], [ 20, 30, 40, 50 ] ],
                pageLength : 2000,
                "dom": "rt",
                "scrollY": "200px",
                scrollCollapse: true,
                destroy : true,
                "columnDefs": [
                    { "width": 200, "targets":2 }
                ],
                "columns" : [
                    { "data": "transDate" ,
                        "render": function ( data, type, full, meta ) {
                            return moment(full.transDate).format('DD/MM/YYYY');
                        }
                    },
                    {
                        "data" : "agent",
                        "render" : function(data, type, full, meta) {
                            return full.agent.shtDesc;
                        }
                    },
                    {
                        "data" : "agent",
                        "render" : function(data, type, full, meta) {
                            return full.agent.name;
                        }
                    },
                    {
                        "data" : "controlAcc",
                        "render" : function(data, type, full, meta) {
                            return full.controlAcc;
                        }
                    },
                    {
                        "data" : "netAmount",
                        "render" : function(data, type, full, meta) {
                            return UTILITIES.currencyFormat(full.netAmount);
                        }
                    },
                    {
                        "data" : "refNo",
                        "render" : function(data, type, full, meta) {
                            return full.refNo;
                        }
                    },
                    {
                        "data": "transno",
                        "render": function(data, type, full, meta) {
                            const printUrl = `${SERVLET_CONTEXT}/protected/accounts/rpt_creditorCommissions.pdf?transno=${full.transno}`;
                            return `<a href="${printUrl}" class="btn btn-primary btn-sm" target="_blank">Print</a>`;
                        }
                    },
                    {
                        "data" : "transno",
                        "render" : function(data, type, full, meta) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-audits='+encodeURI(JSON.stringify(full)) + '  onclick="approveTrans(this);">Approve</button>';

                        }
                    },
                    {
                        "data" : "transno",
                        "render" : function(data, type, full, meta) {
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" type="button" data-audits='+encodeURI(JSON.stringify(full)) + '  onclick="cancelTrans(this);">Reject</button>';

                        }
                    },
                ]
            });

    $('#instrans-tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {
        }
        else{

            findCommissionsAudits(aData[0].transno);
        }
    } );

    return currTable;
}
function findCommissionsAudits(transNo) {
    console.log("Sending transNo: " + transNo);
    var url = SERVLET_CONTEXT + "/protected/accounts/creditorcommaudits";
    var currTable = $('#payment-trans-tbl').DataTable({
        "processing": true,
        "serverSide": false,
        "ajax": {
            'url': url,
            'data': {
                'transNo': transNo
            },
            'dataSrc': 'data',
            'dataType': 'json',
            'contentType': 'application/json'
        },
        "columns": [
            { "data": "refNo" },
            { "data": "clientPolNo" },
            {
                "data": null,
                "render": function(data, type, full) {
                    return full.fname + " " + full.otherNames;
                }
            },
            { "data": "controlAcc" },
            {
                "data": null,
                "render": function(data, type, full) {
                    return full.proDesc;
                }
            },
            {
                "data": "paymentAmount",
                "render": function(data, type, full) {
                    return UTILITIES.currencyFormat(data);
                }
            },
            {
                "data": "commAmount",
                "render": function(data, type, full) {
                    return UTILITIES.currencyFormat(data);
                }
            },
            {
                "data": "whtxAmount",
                "render": function(data, type, full) {
                    return UTILITIES.currencyFormat(data);
                }
            },
            // {
            //     "data": null,
            //     "render": function(data, type, full) {
            //         return '<button type="button" class="btn btn-success btn btn-info btn-sm" onclick="undoAudits(this);">Undo</button>';
            //     }
            // }
        ]
    });

    return currTable;
}

function undoAudits(button){
    var audits = JSON.parse(decodeURI($(button).data("audits")));
    bootbox.confirm("Undo Selected Transaction?", function(result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url:  'deletePaymentAudit/' + audits['paId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Operation Successfully',
                        icon: 'success'
                    });
                    creditorPaymentTrans();
                    insuranceTrans();
                    // $('#credits-tbl').DataTable().ajax.reload();
                    $("#total-val").val(0);
                    $(".total").val(0);
                    $('#payment-trans-tbl').DataTable().ajax.reload();
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


function cancelTrans(button){
    var audits = JSON.parse(decodeURI($(button).data("audits")));
    bootbox.confirm("Reject Selected Transaction?", function(result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url:  'deleteInsuranceTrans/' + audits['transno'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transaction Rejected Successfully',
                        icon: 'success'
                    });
                    creditorPaymentTrans();
                    insuranceTrans();
                    // $('#credits-tbl').DataTable().ajax.reload();
                    $("#total-val").val(0);
                    $(".total").val(0);
                    $('#payment-trans-tbl').DataTable().ajax.reload();
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

function approveTrans(button) {
    var audits = JSON.parse(decodeURI($(button).data("audits")));
    bootbox.confirm("Approve Selected Transaction?", function(result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'approveCreditorComm/' + audits['transno'], // Assuming 'transno' is the transaction ID
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transaction Approved Successfully',
                        icon: 'success'
                    });

                    $('#instrans-tbl').DataTable().ajax.reload(); // Reload the table to reflect the changes
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
