/**
 * Created by HP on 8/21/2017.
 */
var ACCOUNTS = ACCOUNTS || {};
$(function() {

    $(document).ready(function () {

        ACCOUNTS.createAccounts(-2000);
        ACCOUNTS.createPaymentModesSel();
        ACCOUNTS.addCollectAcctsModel();
        ACCOUNTS.saveCollectAccount();

    });
});


ACCOUNTS.saveCollectAccount = function(){
    var $classForm = $('#collect-acct-form');
    var validator = $classForm.validate();
    $('#saveCollectAcct').click(function(){
        if (!$classForm.valid()) {
            return;
        }

        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createCollectAcct";
        console.log(data);
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#collAcctsTbl').DataTable().ajax.reload();
            $('#acct-form').select2('val', null);
            $('#bank-branch-form').select2('val', null);
            $('#currency-form').select2('val', null);
            ACCOUNTS.createAcctsLov();
            ACCOUNTS.createBankBranchLov();
            ACCOUNTS.createCurrencyLov();
            validator.resetForm();
            $('#collect-acct-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#collect-acct-form input:checkbox").prop("checked", false);
            $('#collectAcctsModal').modal('hide');
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

ACCOUNTS.addCollectAcctsModel = function(){
    $("#btn-add-coll-acct").click(function(){
        if($("#pmId").val() != ''){
            $('#collect-acct-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#collect-acct-form input:checkbox").prop("checked", false);
             ACCOUNTS.createAcctsLov();
             ACCOUNTS.createBankBranchLov();
             ACCOUNTS.createCurrencyLov();
            $("#ca-payment-modes").val($("#pmId").val());
            $('#collectAcctsModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        }
        else{
            bootbox.alert("Select Payment Mode to continue");
        }

    });
}


ACCOUNTS.editAccounts = function(button) {
    var accts = JSON.parse(decodeURI($(button).data("accts")));
    $("#ca-id").val(accts["caId"]);
    $("#ca-payment-modes").val(accts["paymentModes"].pmId);
    $("#curr-id").val(accts["currencies"].curCode);
    $("#curr-desc").val(accts["currencies"].curName);
    ACCOUNTS.createCurrencyLov();
    if (accts["bankBranches"]) {
        $("#bbr-desc").val(accts["bankBranches"]).branchName;
        $("#bank-br-id").val(accts["bankBranches"].bbId);
    }
    ACCOUNTS.createBankBranchLov();
    $("#collect-name").val(accts["name"]);
    $("#acct-code").val(accts["accounts"].code);
    $("#acct-name").val(accts["accounts"].name);
    $("#acct-id").val(accts["accounts"].coId);
    ACCOUNTS.createAcctsLov();
    $("#acct-status").prop("checked", accts["status"]);
    $('#collectAcctsModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

ACCOUNTS.createCurrencyLov = function(){
    if($("#currency-form").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "currency-form",
            sort : 'curName',
            change: function(e, a, v){
                $("#curr-id").val(e.added.curCode);
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
                var code = $("#curr-id").val();
                var name = $("#curr-desc").val();
                var data = {curName:name,curCode:code};
                callback(data);
            },
            id: "curCode",
            placeholder:"Select Currency",
        });
    }
}


ACCOUNTS.createBankBranchLov = function(){
    if($("#bank-branch-form").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "bank-branch-form",
            sort : 'branchName',
            change: function(e, a, v){
                $("#bank-br-id").val(e.added.bbId);
            },
            formatResult : function(a)
            {
                return a.branchName;
            },
            formatSelection : function(a)
            {
                return a.branchName;
            },
            initSelection: function (element, callback) {
                var code = $("#bank-br-id").val();
                var name = $("#bbr-desc").val();
                var data = {branchName:name,bbId:code};
                callback(data);
            },
            id: "bbId",
            placeholder:"Select Bank Branch",
        });
    }
}

ACCOUNTS.createAcctsLov = function(){
    if($("#acct-form").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "acct-form",
            sort : 'name',
            change: function(e, a, v){
                $("#acct-id").val(e.added.coId);
            },
            formatResult : function(a)
            {
                return a.code+"-"+ a.name;
            },
            formatSelection : function(a)
            {
                return a.code+"-"+ a.name;
            },
            initSelection: function (element, callback) {
                var code = $("#acct-code").val();
                var name = $("#acct-name").val();
                var coid = $("#acct-id").val();
                var data = {code:code,name:name,coId:coid};
                callback(data);
            },
            id: "coId",
            placeholder:"Select GL Account",
        });
    }
}

ACCOUNTS.createPaymentModesSel = function(){
    if($("#payment-mode").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "payment-mode",
            sort : 'pmDesc',
            change: function(e, a, v){
                $("#pmId").val(e.added.pmId);
                ACCOUNTS.createAccounts(e.added.pmId);
            },
            formatResult : function(a)
            {
                return a.pmDesc
            },
            formatSelection : function(a)
            {
                return a.pmDesc
            },
            initSelection: function (element, callback) {

            },
            id: "pmId",
            placeholder:"Select Payment Mode",
        });
    }
}

 ACCOUNTS.createAccounts= function(pmId){
    var url = "collectionAccts";
    var currTable = $('#collAcctsTbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
            'data':{
                'pmId': pmId
            },
        },
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "name" },
            { "data": "caId",
                "render": function ( data, type, full, meta ) {
                    return full.accounts.code+" - "+full.accounts.name;
                }
            },
            { "data": "caId",
                "render": function ( data, type, full, meta ) {
                    if( full.bankBranches)
                    return full.bankBranches.bank.bankName;
                    else return "";
                }
            },
            { "data": "caId",
                "render": function ( data, type, full, meta ) {
                    if( full.bankBranches)
                    return full.bankBranches.branchName;
                    else return "";
                }
            },
            { "data": "caId",
                "render": function ( data, type, full, meta ) {
                    return full.currencies.curName;
                }
            },
            { "data": "status" },
            {
                "data": "caId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-accts='+encodeURI(JSON.stringify(full)) + ' onclick="ACCOUNTS.editAccounts(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "caId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-accts='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelActivity(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}