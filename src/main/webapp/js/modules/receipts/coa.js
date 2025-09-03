/**
 * Created by peter on 4/7/2017.
 */

$(function() {

    $(document).ready(function () {
      chartofAccounts();
        subAccounts(-2000);
        addCoa();
        addsubcoa();

        $('#control-acc').on('change', function() {
            if(this.value && this.value==='Y'){
                $(".control-acc").css('display','block');
            }
            else{
                $(".control-acc").css('display','none');
            }
        });

        $('#business-class-acc').on('change', function() {
            if(this.value && this.value==='Y'){
                $(".business-class-acc").css('display','block');
            }
            else{
                $(".business-class-acc").css('display','none');
            }
        });
    })
});
$(function(){
    // whenever someone clicks one of the export options
    $('.export-dropdown .dropdown-item').on('click', function(e){
        e.preventDefault();
        // read the format from data-format
        var fmt = $(this).data('format');
        // build the URL (use JSP url builder to get the context path)
        var baseUrl = '<c:url value="/protected/accounts/printcoa"/>';
        var fullUrl = baseUrl + '?format=' + fmt;
        // open in new tab
        window.open(fullUrl, '_blank');
    });
});

function addsubcoa(){
    $("#btn-add-subcoa").click(function () {
        if($("#coa-trans-pk").val()===''){
            bootbox.alert("Select Main Account to add Sub Account");
            return;
        }
        createAccountTypeSelect();
        $('#sub-coa-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#coa-sub-code").val($("#coa-sub-trans-pk").val());
        $("#sub-acc-main-pk").val($("#coa-trans-pk").val());
        $('#subcoaModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    $('#saveSubAccounts').click(function(){
        var $classForm = $('#sub-coa-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createSubAccount";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#coa-sub-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#sub-coa-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#subcoaModal').modal('hide');
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

function addCoa() {
    $("#btn-add-coa").click(function () {
        $('#coa-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $('#coaModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });
    $('#saveCoaAccounts').click(function(){
        var $classForm = $('#coa-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createMainAccount";
        var request = $.post(url, data );
        request.success(function(){

            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#coa-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#coa-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#coaModal').modal('hide');
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

function createAccountTypeSelect (){
    if($("#accounttypes").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "accounttypes",
            sort : 'accName',
            change:  function(e, a, v){
                $("#acc-id").val(e.added.accId);
            },
            formatResult : function(a)
            {
                return a.accName
            },
            formatSelection : function(a)
            {
                return a.accName
            },
            initSelection: function (element, callback) {
                var code = $("#acc-id").val();
                var name = $("#acc-name").val();
                var data = {accName:name,accId:code};
                callback(data);
            },
            id: "accId",
            placeholder:"Select Account Type",
        });
    }

    if($("#sub-class-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sub-class-def",
            sort : 'subId',
            change: function(e, a, v){

                $("#sub-code").val(e.added.subId);
            },
            formatResult : function(a)
            {
                return a.subDesc
            },
            formatSelection : function(a)
            {
                return a.subDesc
            },
            initSelection: function (element, callback) {

            },
            id: "subId",
            placeholder:"Select Sub Class"
        });

    }
};


function subAccounts(mainaccId) {
    var url = "coasubtrans/"+mainaccId;
    var currTable = $('#coa-sub-tbl')
        .DataTable(
            {
                "processing" : true,
                "serverSide" : true,
                "ajax": {
                    'url': url,
                },
                lengthMenu : [ [ 10 ], [ 10] ],
                pageLength : 10,
                autoWidth: true,
                destroy : true,
                "columns" : [
                    {
                        "data" : "code",
                        "width": "15%"
                    },
                    {
                        "data" : "name",
                        "width": "25%"
                    },
                    {
                        "data" : "integration",
                        "render" : function(data, type, full, meta) {
                            if(full.integration){
                                if(full.integration ==="Y") return "Yes";
                                else if(full.integration ==="N") return "No";
                                else return "";
                            }else
                            return full.integration;
                        }
                    },
                    {
                        "data" : "accountsOrder",
                        "render" : function(data, type, full, meta) {
                            return full.accountsOrder;
                        }
                    },
                    {
                        "data" : "controlAccount",
                        "render" : function(data, type, full, meta) {
                            if(full.controlAccount){
                                if(full.controlAccount ==="Y") return "Yes";
                                else if(full.controlAccount ==="N") return "No";
                                else return "";
                            }else
                                return full.controlAccount;
                        }
                    },
                    {
                        "data" : "accTypeName"
                    },
                    {
                        "data" : "applicableToScl",
                        "render" : function(data, type, full, meta) {
                            if(full.applicableToScl){
                                if(full.applicableToScl ==="Y") return "Yes";
                                else if(full.applicableToScl ==="N") return "No";
                                else return "";
                            }else
                                return full.applicableToScl;
                        }
                    },
                    {
                        "data" : "sublass"
                    },
                    {
                        "data" : "sapGlAccount"
                    },
                    {
                        "data": "sapGlAccountBusiness"
                    },
                    {
                        "data": "sapGlAccountCorporate"
                    },
                    {
                        "data": "coId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-subaccounts='+encodeURI(JSON.stringify(full)) + ' onclick="editSubAccounts(this);"><i class="fa fa-pencil-square-o"></button>';
                        }

                    },
                    {
                        "data": "coId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-subaccounts='+encodeURI(JSON.stringify(full)) + ' onclick="deleteSubAccounts(this);"><i class="fa fa-trash-o"></button>';
                        }

                    },

                ]
            });
    return currTable;
}

function chartofAccounts() {
    var url = "coatrans";
    var currTable = $('#coa-tbl')
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
                        "data" : "code"
                    },
                    {
                        "data" : "name"
                    },
                    {
                        "data" : "header",
                        "render" : function(data, type, full, meta) {
                            return full.header;
                        }
                    },
                    {
                        "data" : "accountType",
                        "render" : function(data, type, full, meta) {
                            if(full.accountType){
                                if(full.accountType ==="I"){
                                    return "Income";
                                }
                                else if(full.accountType ==="E"){
                                    return "Expense";
                                }
                                else if(full.accountType ==="A"){
                                    return "Assets";
                                }
                                else if(full.accountType ==="L"){
                                    return "Liability";
                                }
                                else return "";
                            }else
                            return "";
                        }
                    },
                    {
                        "data" : "plBalSheet",
                        "render" : function(data, type, full, meta) {
                            if(full.plBalSheet){
                                if(full.plBalSheet ==="P"){
                                    return "Profit and Loss";
                                }
                                else if(full.plBalSheet ==="B"){
                                    return "Balance sheet";
                                }
                                else return "";
                            }else
                            return "";
                        }
                    },
                    {
                        "data" : "accountsOrder",
                        "render" : function(data, type, full, meta) {
                            return full.accountsOrder;
                        }
                    },
                    {
                        "data": "coId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-mainaccounts='+encodeURI(JSON.stringify(full)) + ' onclick="editMainAccounts(this);"><i class="fa fa-pencil-square-o"></button>';
                        }

                    },
                    {
                        "data": "coId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-mainaccounts='+encodeURI(JSON.stringify(full)) + ' onclick="deleteMainAccounts(this);"><i class="fa fa-trash-o"></button>';
                        }

                    },
                ]
            });
    $('#coa-tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {
        }
        else{

            subAccounts(aData[0].coId);
            $("#coa-trans-pk").val(aData[0].coId);
            $("#coa-sub-trans-pk").val(aData[0].code);
        }
    } );
    return currTable;
}

function editMainAccounts(button){
    var accounts = JSON.parse(decodeURI($(button).data("mainaccounts")));
    $("#acc-code-pk").val(accounts["coId"]);
    $("#acc-code-sht-desc").val(accounts["code"]);
    $("#acc-code-name").val(accounts["name"]);
    $("#acc-code-header").val(accounts["header"]);
    $("#acc-code-footer").val(accounts["footer"]);
    $("#sel3").val(accounts["accountType"]);
    $("#blpl").val(accounts["plBalSheet"]);
    $("#acc-code-order").val(accounts["accountsOrder"]);
    $('#coaModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function editSubAccounts(button){
    var accounts = JSON.parse(decodeURI($(button).data("subaccounts")));
    $("#sub-acc-pk").val(accounts["coId"]);
    $("#sub-acc-main-pk").val(accounts["mainAcctId"]);
    $("#coa-sub-code").val(accounts["code"]);
    $("#coa-sub-name").val(accounts["name"]);
    $("#integrat-acc").val(accounts["integration"]);
    $("#sub-acc-code-order").val(accounts["accountsOrder"]);
    $("#acc-id").val(accounts["acctTypeId"]);
    $("#acc-name").val(accounts["accTypeName"]);
    $("#sap-gl-acc").val(accounts["sapGlAccount"]);
    $("#sap-gl-acc-business").val(accounts["sapGlAccountBusiness"]);
    $("#sap-gl-acc-corporate").val(accounts["sapGlAccountCorporate"]);
    createAccountTypeSelect();
    $('#subcoaModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function deleteSubAccounts(button){
    var accounts = JSON.parse(decodeURI($(button).data("subaccounts")));
    bootbox.confirm("Are you sure want to delete "+accounts["name"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteSubAccount/' + accounts["coId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#coa-sub-tbl').DataTable().ajax.reload();
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

function deleteMainAccounts(button){
    var accounts = JSON.parse(decodeURI($(button).data("mainaccounts")));
    bootbox.confirm("Are you sure want to delete "+accounts["name"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteMainAccount/' + accounts["coId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#coa-tbl').DataTable().ajax.reload();
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