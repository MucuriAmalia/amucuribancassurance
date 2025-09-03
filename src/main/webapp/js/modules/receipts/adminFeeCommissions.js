var UTILITIES = UTILITIES || {};
$(function(){

    $(document).ready(function() {
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        $("#admin-total-val").val(0);
        createInsuranceCompaniesForSel();
        populateAdminCurrencyLov();
        adminFeeTrans();

        $("#btn-search-admin-trans").on('click', function(){
            adminFeePaymentTrans();
            adminFeeTrans();
            $("#admin-total-val").val(0);
            $(".admin-total").val(0);
        });

        processAdminTransactions();

        // Select/Deselect all checkboxes and trigger their onchange events
        $("#selectAllAdmin").change(function() {
            if (this.checked) {
                $("input[type='checkbox'][id^='admin-rct-check-']").each(function() {
                    this.checked = true;
                    // Trigger onchange event to call getPayableCommAmount function
                    $(this).trigger('change');
                });
            } else {
                $("input[type='checkbox'][id^='admin-rct-check-']").each(function() {
                    this.checked = false;
                    // Optionally clear values if required
                    $(this).trigger('change');
                });
            }
        });

        // Function to handle checkbox state and call the required function
        $(document).on('change', "input[type='checkbox'][id^='admin-rct-check-']", function() {
            if (this.checked) {
                // Extracting JSON data stored in the onchange attribute dynamically
                var jsonData = $(this).attr('onchange').match(/\((.*)\)/)[1];
                var dataObj = JSON.parse(jsonData.replace(/\\/g, "").replace(/\'/g, "\""));

                // Call the function with parsed data
                getPayableAdminFeeComm(dataObj);
            }
        });

    });
});



function populateAdminCurrencyLov(){
    if($("#admin-curr-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "admin-curr-frm",
            sort : 'curName',
            change: function(e, a, v){
                $("#admin-cur-id").val(e.added.curCode);

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



function createInsuranceCompaniesForSel(){
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


function adminFeePaymentTrans() {
    if(!$("#admin-from-date").val()){
        bootbox.alert("Select Date from....");
        return;
    }
    if(!$("#admin-wet-date").val()){
        bootbox.alert("Select Date To....");
        return;
    }
    if(!$("#agent-search-number").val()){
        bootbox.alert("Select Insurance Company....");
        return;
    }
    if(!$("#admin-cur-id").val()){
        bootbox.alert("Select Currency....");
        return;
    }
    $.ajax({
        type: 'GET',
        url:  'adminFeeTranscomm',
        dataType: 'json',
        data: {'currCode': $("#admin-cur-id").val(),
            'agentCode': $("#agent-search-number").val(),
            'wefDate': $("#admin-from-date").val(),
            'wetDate':  $("#admin-wet-date").val(),
        },
        async: true,
        success: function(result) {
            console.log("Result" +result);
            $("#admin-fee-tbl tbody").each(function(){
                $(this).remove();
            });
            for(var res in result){
                var markup = "<tr>" +
                    "<td><input type='checkbox' id='admin-rct-check-" + result[res].crNo +"-" + result[res].drNo + "'  onchange='getPayableAdminFeeComm("+JSON.stringify(result[res]).replace(/\'/g, "")+")'/></td>" +
                    "<td>" + result[res].clientPolNo + "</td>" +
                    "<td>" + result[res].clientName + "</td>" +
                    "<td class='admin-debit-val'>" + result[res].drNo + "</td>" +
                    "<td class='admin-credit-val'>" + result[res].crNo + "</td>" +
                    "<td>" + result[res].payStatus + "</td>" +
                    "<td>" + UTILITIES.currencyFormat(result[res].basicPrem) + "</td>" +
                    "<td>" + UTILITIES.currencyFormat(result[res].adminFeeAmt) + "</td>" +
                    "<td><input type='hidden' id='admin-hidden-rct-amt-" + result[res].crNo + "-" + result[res].drNo + "'>" +
                    "<input type='hidden' class='admin-hidden-trans-type' value='"+ result[res].transType + "'>" +
                    " <input type='text' disabled size='7' class='form-control admin-cred-alloc-amt'  id='admin-rctamt-" + result[res].crNo +"-" + result[res].drNo + "'/></td>" +
                    "</tr>" ;
                $("#admin-fee-tbl").append(markup);
            }
            $('[id^=admin-rctamt-]').number(true, 2);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });
    $('[id^=admin-rctamt-]').number(true, 2);
}


function getTotalAdminAuditAmt(auditId){
    var total = 0;
    $.ajax({
        type: 'GET',
        url: 'getTotalAdminAuditAmt',
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

function getPayableAdminFeeComm(button){

    var receiptBox = $('#admin-rct-check-' + button["crNo"] + "-" + button["drNo"] +  '');
    var checkField = $('#admin-rctamt-' + button["crNo"] + "-" + button["drNo"] +  '');

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
        $("#admin-fee-tbl tr input[type='text']").each(function() {
            if(this.value) {
                total = total + parseFloat(parseNumber(this.value));
            }
        });
        $("#admin-total-val").val(total);
        $(".admin-total").val(UTILITIES.currencyFormat(total));
    });



    if(receiptBox.is(':checked')){
        if(button['transType']==='CLB'){
            var val = -1*button['debitBal'];
            $(button["locAmt"] + button["crNo"] + "-" + button["drNo"] + '').val(val);
            checkField.val(val);
            var total = 0;
            $("#admin-fee-tbl tr input[type='text']").each(function () {
                if (this.value) {
                    if(this.value.indexOf("-")!==-1)
                        total = total - parseFloat(parseNumber(this.value));
                    else
                        total = total + parseFloat(parseNumber(this.value));
                }
            });

            $("#admin-total-val").val(total);
            $(".admin-total").val(UTILITIES.currencyFormat(total));
            $('[id^=admin-rctamt-]').number(true, 2);
        }
        else {
            $.ajax({
                type: 'GET',
                url: 'getpayableAdminFeeamt',
                dataType: 'json',
                data: {"receiptNo": button["crNo"], "debitRef": button["drNo"], "locAmt": button["locAmt"], "allocAmt": button["allocAmt"] },
                async: true,
                success: function (result) {
                    if (result) {
                        $(button["locAmt"] + button["crNo"] + "-" + button["drNo"] + '').val(result);
                        checkField.val(result);
                        var total = 0;
                        $("#admin-fee-tbl tr input[type='text']").each(function () {
                            if (this.value) {
                                if(this.value.indexOf("-")!==-1)
                                    total = total - parseFloat(parseNumber(this.value));
                                else
                                    total = total + parseFloat(parseNumber(this.value));
                            }
                        });

                        $("#admin-total-val").val(total);
                        $(".admin-total").val(UTILITIES.currencyFormat(total));
                        $('[id^=admin-rctamt-]').number(true, 2);
                    }


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
    }
    else{
        checkField.val(0);
        $('[id^=admin-rctamt-]').number(true, 2);
        var total =0;
        $("#admin-fee-tbl tr input[type='text']").each(function() {
            if(this.value)
                total = total + parseFloat(parseNumber(this.value));
        });
        $("#admin-total-val").val(total);
        $(".admin-total").val(UTILITIES.currencyFormat(total));

    }
}

function processAdminTransactions(){
    $("#process_admin_trans").on('click', function(){
        var oTable = $("#admin-fee-tbl");
        var arr = [];
        var selectedRows = oTable.find( 'tbody' ) // select table body and
            .find( 'tr' ) // select all rows that has
            .has( 'input[type=checkbox]:checked' ) // checked checkbox element
        selectedRows.each(function() {
            var credit = $(this).find(".admin-credit-val").html();
            var debit = $(this).find(".admin-debit-val").html();
            var allocAmt = $(this).find(".admin-cred-alloc-amt").val();
            var transType = $(this).find(".admin-hidden-trans-type").val();
            arr.push({
                amount: allocAmt,
                debiTrans: debit,
                creditTrans:credit,
                transType:transType
            });
        });

        if(arr.length==0){
            bootbox.alert("No Records Selected to Process")
            return;
        }
        var $currForm = $('#admin-fee-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "processAdminFeeCommTrans";
        data.credits = arr;

        Swal.fire({
            backdrop: 'static',
            keyboard: true
        });

        $.ajax(
            {
                url:url,
                type: "POST",
                data: JSON.stringify(data),
                success: function(s){
                    Swal.fire({
                        title: 'Success',
                        text: 'Processing Successfully',
                        icon: 'success'
                    });
                    adminFeePaymentTrans();
                    adminFeeTrans();
                    $("#admin-total-val").val(0);
                    $(".admin-total").val(0);
                },
                error: function(jqXHR, textStatus, errorThrown){
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

function adminFeeTrans() {
    $(document).ajaxStart(function () {
        $("#admin-instrans-tbl").attr("disabled", true);
    });
    $(document).ajaxComplete(function () {
        $("#admin-instrans-tbl").attr("disabled", false);
    });
    var url = "adminFeeCommTrans";
    var currTable = $('#admin-instrans-tbl')
        .DataTable(
            {
                "processing" : true,
                "serverSide" : true,
                "ajax": {
                    'url': url,
                    'data': {
                        'currCode': $("#admin-cur-id").val(),
                        'agentCode': $("#agent-search-number").val(),
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
                            const printUrl = `${SERVLET_CONTEXT}/protected/accounts/rpt_adminFeeCommissions.pdf?transno=${full.transno}`;
                            return `<a href="${printUrl}" class="btn btn-primary btn-sm" target="_blank">Print</a>`;
                        }
                    },
                    {
                        "data" : "transno",
                        "render" : function(data, type, full, meta) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-audits='+encodeURI(JSON.stringify(full)) + '  onclick="approveAdminTrans(this);">Approve</button>';

                        }
                    },
                    {
                        "data" : "transno",
                        "render" : function(data, type, full, meta) {
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" type="button" data-audits='+encodeURI(JSON.stringify(full)) + '  onclick="cancelAdminTrans(this);">Reject</button>';

                        }
                    },
                ]
            });

    $('#admin-instrans-tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {
        }
        else{
            findAdminFeeCommAudits(aData[0].transno);
        }
    } );

    return currTable;
}

function findAdminFeeCommAudits(transNo) {
    console.log("Sending transNo: " + transNo);
    var url = SERVLET_CONTEXT + "/protected/accounts/adminFeecommaudits";
    var currTable = $('#admin-payment-trans-tbl').DataTable({
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
                "data": "adminFeeAmount",
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
        ]
    });

    return currTable;
}

function undoAdminAudits(button){
    var audits = JSON.parse(decodeURI($(button).data("audits")));
    bootbox.confirm("Undo Selected Transaction?", function(result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url:  'deleteAdminPaymentAudit/' + audits['paId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Operation Successfully',
                        icon: 'success'
                    });
                    adminFeePaymentTrans();
                    adminFeeTrans();
                    $("#admin-total-val").val(0);
                    $(".admin-total").val(0);
                    $('#admin-payment-trans-tbl').DataTable().ajax.reload();
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


function cancelAdminTrans(button){
    var audits = JSON.parse(decodeURI($(button).data("audits")));
    bootbox.confirm("Reject Selected Transaction?", function(result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url:  'deleteAdminInsuranceTrans/' + audits['transno'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transaction Rejected Successfully',
                        icon: 'success'
                    });
                    adminFeePaymentTrans();
                    adminFeeTrans();
                    $("#admin-total-val").val(0);
                    $(".admin-total").val(0);
                    $('#admin-payment-trans-tbl').DataTable().ajax.reload();
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

function approveAdminTrans(button) {
    var audits = JSON.parse(decodeURI($(button).data("audits")));
    bootbox.confirm("Approve Selected Transaction?", function(result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'approveAdminFeeComm/' + audits['transno'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Transaction Approved Successfully',
                        icon: 'success'
                    });

                    $('#admin-instrans-tbl').DataTable().ajax.reload();
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