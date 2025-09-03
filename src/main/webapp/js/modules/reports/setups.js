/**
 * Created by peter on 2/12/2017.
 */

$(function() {

    $(document).ready(function () {

        createReportHeaders();
        addReportHeaders();
        saveReportHeaders();
        addReportDefs();
        saveReportDefs();
        addReportDefParams();
        saveReportParams();
        selectPermLov();
        saveReportPerm();

        $('.btn-can').on('click',function(){
            $("#perm-id").val('');
            $('#rpt-param-form')[0].reset();
            jQuery('.select2-offscreen').select2('val', '');
            $("#rptParamModal").modal('hide');
        })

        $('.btn-sm').on('click',function(){
            $('#permissionsModal').modal('show');
        })

        $("#param-type").on('change', function(){
            if($("#param-type").val()==="L"){
                $("#lov-name").attr("disabled",false);
                $("#pass-name").attr("disabled",false);
                $("#option-name").attr("disabled",true);
            }
            else  if($("#param-type").val()==="O"){
                $("#lov-name").attr("disabled",true);
                $("#pass-name").attr("disabled",true);
                $("#option-name").attr("disabled",false);
            }
            else{
                $("#lov-name").val('');
                $("#lov-name").attr("disabled",true);
                $("#pass-name").attr("disabled",true);
                $("#option-name").attr("disabled",true);
            }
        })

    });
});
function saveReportPerm(){
    var $form= $("#permissions-form");
    var validator = $form.validate();

    $('#permissionsModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#permissions-form').find("input[type=text]").val("");
    });

    $('#save-permission').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "addPermission";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Permission Created Successfully',
                icon: 'success'
            });
            $('#rpt-param-form')[0].reset();
            validator.resetForm();
            $('#permissionsModal').modal('hide');
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

function saveReportHeaders(){
    var $classForm = $('#rpt-header-form');
    var validator = $classForm.validate();
    $('#saveReportHeader').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createRptHeader";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#rptHeadersTbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#rpt-header-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#rptHeaderModal').modal('hide');
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

function selectPermLov(){
    if($("#perm-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "perm-def",
            sort : 'permName',
            change: function(e, a, v){
                $("#perm-id").val(e.added.permId);
            },
            formatResult : function(a)
            {
                return a.permName
            },
            formatSelection : function(a)
            {
                return a.permName
            },
            initSelection: function (element, callback) {
                var code = $("#perm-id").val();
                var name = $("#perm-name").val();
                var data = {permName:name,permId:code};
                callback(data);
            },
            id: "permId",
            placeholder:"Select Permission",
        });
    }
}

function addReportHeaders(){
    $("#addRptHeader").click(function(){
        $('#rpt-header-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $('#rptHeaderModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

}

function addReportDefs(){
    $("#btn-add-rpt-def").click(function(){
        if($("#rpt-header-pk").val() != '') {
            $('#rpt-def-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#rp-header-id').val($("#rpt-header-pk").val());
            $('#rptDefModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        }
        else{
            bootbox.alert("Select Report Header to Define the Report");
        }
    });

}

function addReportDefParams(){
    $("#btn-add-rpt-param").click(function(){
        if($("#rpt-definition-pk").val() != '') {
            $('#rpt-param-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#rp-reportdef-id').val($("#rpt-definition-pk").val());
            $("#lov-name").attr("disabled",true);
            $('#rptParamModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        }
        else{
            bootbox.alert("Select Report to Define the Parameter");
        }
    });

    //$("#param-type").on('change', function(){
    //    if($("#param-type").val()==="L"){
    //        $("#lov-name").attr("disabled",false);
    //    }
    //    else{
    //        $("#lov-name").val('');
    //        $("#lov-name").attr("disabled",true);
    //    }
    //})



}

function createReportHeaders(){
    var url = "rptHeaders";
    var currTable = $('#rptHeadersTbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "moduleCode",
                "render":function(data,type,full,meta){
                    if(full.moduleCode){
                        if(full.moduleCode==="U"){
                            return "Underwriting";
                        }
                        else if(full.moduleCode==="A"){
                            return "Accounts";
                        }
                        else if(full.moduleCode==="C"){
                            return "Claims";
                        }
                    }
                }
            },
            { "data": "moduleName" },
            {
                "data": "rhId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-reportheaders='+encodeURI(JSON.stringify(full)) + ' onclick="editReportHeader(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "rhId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-reportheaders='+encodeURI(JSON.stringify(full)) + ' onclick="delReportHeader(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );

    $('#rptHeadersTbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        placeRepHead();
        if (aData[0] === undefined || aData[0] === null) {
        }
        else{
            $("#rpt-header-pk").val(aData[0].rhId);
            createReportDefs(aData[0].rhId);
        }
    } );
    return currTable;
}

function placeRepHead() {
    let moduleName='Reports Module';
    $.ajax({
        type: 'GET',
        url: 'moduleFetch',
        data: {
            'moduleName':moduleName
        },

    }).done(function (s) {
        console.log(s);
        $('#moduleId').val(s.moduleId);

    }).fail(function (xhr, error) {
         Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
    });

}

function editReportHeader(button){
    var headers = JSON.parse(decodeURI($(button).data("reportheaders")));
    $("#rh-id").val(headers['rhId']);
    $("#module-name").val(headers['moduleCode']);
    $("#rpt-header").val(headers['moduleName']);
    $('#rptHeaderModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}


function delReportHeader(button){
    var headers = JSON.parse(decodeURI($(button).data("reportheaders")));
    bootbox.confirm("Are you sure want to delete "+headers["moduleName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteRptHeader/' + headers['rhId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#rptHeadersTbl').DataTable().ajax.reload();
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

function createReportDefs(rhId){
    var url = "reportDefs/"+rhId;
    var currTable = $('#rptDefTbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        destroy: true,
        lengthMenu: [ [5, 10], [5, 10] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "rptName" },
            { "data": "rptDescription" },
            { "data": "rptTemplateName" },
            { "data": "active" },
            { "data": "passwordProtect" },
            {
                "data": "rptId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-reportdefs='+encodeURI(JSON.stringify(full)) + '  onclick="editReportDefs(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "rptId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-reportdefs='+encodeURI(JSON.stringify(full)) + '  onclick="deleteReportDefs(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );

    $('#rptDefTbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();

        if (aData[0] === undefined || aData[0] === null) {
        }
        else{
            $("#rpt-definition-pk").val(aData[0].rptId);
            createReportParams(aData[0].rptId);
        }
    } );
    return currTable;
}

function createReportParams(rhId){
    var url = "reportParams/"+rhId;
    var currTable = $('#rptParamsTbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        destroy: true,
        lengthMenu: [ [5, 10], [5, 10] ],
        pageLength: 5,
        destroy: true,
        "order": [[ 5, "asc" ]],
        "columns": [
            { "data": "paramName" },
            { "data": "paramActualName" },
            { "data": "paramType" },
            { "data": "lovName" },
            {
                "data": "rpId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-reportparams='+encodeURI(JSON.stringify(full)) + '  onclick="editReportParams(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "rpId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" type="button" data-reportparams='+encodeURI(JSON.stringify(full)) + '  onclick="deleteReportParams(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}


function deleteReportParams(button){
    var reportparams = JSON.parse(decodeURI($(button).data("reportparams")));
    bootbox.confirm("Are you sure want to delete "+reportparams["paramName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteReportParam/' + reportparams['rpId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#rptParamsTbl').DataTable().ajax.reload();
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

function deleteReportDefs(button){
    var reportdefs = JSON.parse(decodeURI($(button).data("reportdefs")));
    bootbox.confirm("Are you sure want to delete "+reportdefs["rptName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteRptDef/' + reportdefs['rptId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#rptDefTbl').DataTable().ajax.reload();
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

function editReportDefs(button){
    var reportdefs = JSON.parse(decodeURI($(button).data("reportdefs")));
    $("#rp-id").val(reportdefs['rptId']);
    $("#rp-header-id").val(reportdefs['reportHeader'].rhId);
    $("#rpt-name").val(reportdefs['rptName']);
    $("#rpt-template").val(reportdefs['rptTemplateName']);
    $("#rpt-desc").val(reportdefs['rptDescription']);
    if(reportdefs['permissionsDef']){
        $('#perm-id').val(reportdefs['permissionsDef']);
        $('#perm-name').val(reportdefs['permissionsDef'].permName);
        selectPermLov();
    }
    $("#rpt-active").prop("checked", reportdefs["active"]);
    if(reportdefs["passwordProtect"]){
        if(reportdefs["passwordProtect"] ==="Y") {
            $("#rpt-password-protect").prop("checked", true);
        }
        else $("#rpt-password-protect").prop("checked", false);
    }
    $('#rptDefModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function editReportParams(button){
    var reportparams = JSON.parse(decodeURI($(button).data("reportparams")));
    $("#rp-reportdef-id").val(reportparams['reportDef'].rptId);
    $("#rp-param-id").val(reportparams['rpId']);
    $("#rpt-param-name").val(reportparams['paramName']);
    $("#rpt-param-act-name").val(reportparams['paramActualName']);
    $("#param-type").val(reportparams['paramType']);
    $("#lov-name").val(reportparams['lovName']);
    $("#option-name").val(reportparams['options']);
    $("#pass-name").val(reportparams['passwordField']);
    if(reportparams['paramType']==="L"){
        $("#lov-name").attr("disabled",false);
        $("#pass-name").attr("disabled",false);
        $("#option-name").attr("disabled",true);
    }
    else  if(reportparams['paramType']==="O"){
        $("#lov-name").attr("disabled",true);
        $("#pass-name").attr("disabled",true);
        $("#option-name").attr("disabled",false);
    }
    else{
        $("#lov-name").val('');
        $("#pass-name").attr("disabled",true);
        $("#lov-name").attr("disabled",true);
        $("#option-name").attr("disabled",true);
    }
    $('#rptParamModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}


function saveReportDefs(){
    var $classForm = $('#rpt-def-form');
    var validator = $classForm.validate();
    $('#saveReportDef').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createReportDefs";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#rptDefTbl').DataTable().ajax.reload();
            $("#perm-id").val('');
            jQuery('.select2-offscreen').select2('val', '');
            validator.resetForm();
            $('#rpt-def-form').find("input[type=hidden],input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#rptDefModal').modal('hide');
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



function saveReportParams(){
    var $classForm = $('#rpt-param-form');
    var validator = $classForm.validate();
    $('#saveReportParams').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createReportParams";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#rptParamsTbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#rpt-param-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#rptParamModal').modal('hide');
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
