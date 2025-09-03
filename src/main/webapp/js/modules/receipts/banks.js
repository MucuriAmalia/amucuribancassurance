/**
 * Created by peter on 4/10/2017.
 */

$(function() {

    $(document).ready(function () {
        findBanks();
        findBankBranches(-2000);
        addBanks();
        addBankBranch();
    });
});




function addBankBranch(){
    $("#btn-add-bankbranch").click(function () {
        if($("#bank-trans-pk").val()===''){
            bootbox.alert("Select Bank to add Branch");
            return;
        }
        $('#bank-branch-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#branch-bn-pk").val($("#bank-trans-pk").val());
        $('#bankBranchModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    $('#saveBankBranch').click(function(){
        var $classForm = $('#bank-branch-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createBankBranch";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#bank-branch-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#bank-branch-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#bankBranchModal').modal('hide');
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

function addBanks() {
    $("#btn-add-banks").click(function () {
        $('#banks-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $('#banksModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    $('#saveBanks').click(function(){
        var $classForm = $('#banks-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createBank";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#banks-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#banks-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#banksModal').modal('hide');
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

function  editBanks(button){
    var banks = JSON.parse(decodeURI($(button).data("banks")));
    $("#bank-code-pk").val(banks["bnId"]);
    $("#bank-code").val(banks["bankShtDesc"]);
    $("#bank-name").val(banks["bankName"]);
    $("#bank-remarks").val(banks["bankRemarks"]);
    $("#chk-eft-supported").prop("checked", banks["eftSupported"]);
    $("#chk-active").prop("checked", banks["active"]);
    $('#banksModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function deleteBanks(button){
    var banks = JSON.parse(decodeURI($(button).data("banks")));
    bootbox.confirm("Are you sure want to delete "+banks["bankName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteBank/' + banks["bnId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#banks-tbl').DataTable().ajax.reload();
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

function findBanks() {
    var url = "banks";
    var currTable = $('#banks-tbl')
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
                        "data" : "bankShtDesc"
                    },
                    {
                        "data" : "bankName"
                    },
                    {
                        "data" : "bankRemarks",
                        "render" : function(data, type, full, meta) {
                            return full.bankRemarks;
                        }
                    },
                    {
                        "data" : "eftSupported",
                        "render" : function(data, type, full, meta) {
                                return full.eftSupported;
                        }
                    },
                    {
                        "data" : "active",
                        "render" : function(data, type, full, meta) {
                            return full.active;
                        }
                    },
                    {
                        "data": "bnId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-banks='+encodeURI(JSON.stringify(full)) + ' onclick="editBanks(this);"><i class="fa fa-pencil-square-o"></button>';
                        }

                    },
                    {
                        "data": "bnId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-banks='+encodeURI(JSON.stringify(full)) + ' onclick="deleteBanks(this);"><i class="fa fa-trash-o"></button>';
                        }

                    },
                ]
            });
    $('#banks-tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {
        }
        else{

            findBankBranches(aData[0].bnId);
            $("#bank-trans-pk").val(aData[0].bnId);
        }
    } );
    return currTable;
}


function findBankBranches(bnId) {
    var url = "bankbranches/"+bnId;
    var currTable = $('#bank-branch-tbl')
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
                        "data" : "branchShtDesc"
                    },
                    {
                        "data" : "branchName"
                    },
                    {
                        "data" : "branchRefCode"
                    },
                    {
                        "data" : "branchRemarks"
                    },
                    {
                        "data" : "eftSupported",
                        "render" : function(data, type, full, meta) {
                            return full.eftSupported;
                        }
                    },
                    {
                        "data" : "postalAddress"
                    },
                    {
                        "data" : "physicalAddress"
                    },
                    {
                        "data": "bbId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-branches='+encodeURI(JSON.stringify(full)) + ' onclick="editBankBranch(this);"><i class="fa fa-pencil-square-o"></button>';
                        }

                    },
                    {
                        "data": "bbId",
                        "render": function ( data, type, full, meta ) {
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-branches='+encodeURI(JSON.stringify(full)) + ' onclick="deleteBankBranch(this);"><i class="fa fa-trash-o"></button>';
                        }

                    },
                ]
            });
    return currTable;
}

function editBankBranch(button){
    var branches = JSON.parse(decodeURI($(button).data("branches")));
    $("#bank-branch-pk").val(branches["bbId"]);
    $("#branch-bn-pk").val(branches["bank"].bnId);
    $("#branch-sht-desc").val(branches["branchShtDesc"]);
    $("#ref-code").val(branches["branchRefCode"]);
    $("#branch-name").val(branches["branchName"]);
    $("#branch-remarks").val(branches["branchRemarks"]);
    $("#branch-postal-address").val(branches["postalAddress"]);
    $("#branch-phy-address").val(branches["physicalAddress"]);
    $('#bankBranchModal').modal({
        backdrop: 'static',
        keyboard: true
    })

}

function deleteBankBranch(button){
    var branches = JSON.parse(decodeURI($(button).data("branches")));
    bootbox.confirm("Are you sure want to delete "+branches["branchName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteBankBranch/' + branches["bbId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#bank-branch-tbl').DataTable().ajax.reload();
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