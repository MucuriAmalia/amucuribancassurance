/**
 * Created by HP on 10/5/2017.
 */

var ChecksSetup = (function($){
    'use strict';
    var addChecks = function(){
        $("#btn-add-common-checks").click(function () {
                $('#checks-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                createPermSel();
                $("#check-prod-id").val($("#prod-id").val());
                $('#checksModal').modal({
                    backdrop: 'static',
                    keyboard: true
                })
        });

        var $classForm = $('#checks-form');
        var validator = $classForm.validate();
        $('#saveChecks').click(function(){
            if (!$classForm.valid()) {
                return;
            }

            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
            console.log(data);
            var url = "createChecks";
            var request = $.post(url, data );
            request.success(function(){
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
              });
                $('#common-checks-tbl').DataTable().ajax.reload();
                $('#checks-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#checksModal').modal('hide');
            });

            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            });
            request.always(function(){
                $btn.button('reset');
            });
        });
    };

    var saveProdChecks = function(){
        $("#saveProdChecksBtn").click(function(){
            var arr = [];
            $("#prodChecksTbl tbody").find('input[name="record"]').each(function(){
                if($(this).is(":checked")){
                    arr.push($(this).attr("id"));
                }
            });
            console.log(arr);
            if(arr.length==0){
                bootbox.alert("No Check Selected to attach..");
                return;
            }
            var $currForm = $('#prod-check-form');
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }

            var data = {};
            $currForm.serializeArray().map(function(x) {
                data[x.name] = x.value;
            });
            var url = "createProductChecks";
            data.checks = arr;
            $.ajax({
                url : url,
                type : "POST",
                data : JSON.stringify(data),
                success : function(s) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Record created Successfully',
                        icon: 'success'
                    });
                    $('#checks-tbl').DataTable().ajax.reload();
                    $('#prodChecksModal').modal('hide');
                    arr = [];
                },
                error : function(jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR);
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                },
                dataType : "json",
                contentType : "application/json"
            });
        })
    }

    var checksDialog = function(){
        $("#btn-add-checks").click(function() {
            if ($("#prod-id").val() != '') {
                $.ajax({
                    type: 'GET',
                    url: 'unselectedChecks',
                    dataType: 'json',
                    data: {"prodId": $("#prod-id").val()},
                    async: true,
                    success: function (result) {
                        $("#prodChecksTbl tbody").each(function () {
                            $(this).remove();
                        });
                        for (var res in result) {
                            var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res].checkId + "'></td><td>" + result[res].checkShtDesc + "</td><td>" + result[res].checkName + "</td></tr>";
                            $("#prodChecksTbl").append(markup);
                        }
                         $("#check-pro-code").val($("#prod-id").val());
                        $('#prodChecksModal').modal({
                            backdrop: 'static',
                            keyboard: true
                        })
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
            else {
                bootbox.alert("Select Product to attach Checks")
            }
        });
    }

    var createProductSel = function(){
        if($("#prod-frm").filter("div").html() != undefined)
        {
            Select2Builder.initAjaxSelect2({
                containerId : "prod-frm",
                sort : 'proDesc',
                change: function(e, a, v){
                    $("#prod-id").val(e.added.proCode);
                    createProdChecksList(e.added.proCode);
                },
                formatResult : function(a)
                {
                    return a.proDesc
                },
                formatSelection : function(a)
                {
                    return a.proDesc
                },
                initSelection: function (element, callback) {

                },
                id: "proCode",
                placeholder:"Select Product",
            });
        }
    };

    var createPermSel = function(){
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
    };

    var createChecksList = function(){
        var url = "checksList";
        var table = $('#common-checks-tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl":url,
            "columns": [
                {
                    "data": "checkId",
                    "render": function ( data, type, full, meta ) {
                        return full.checkShtDesc;
                    }
                },
                {
                    "data": "checkId",
                    "render": function ( data, type, full, meta ) {
                        return full.checkName;
                    }
                },
                {
                    "data": "checkId",
                    "render": function ( data, type, full, meta ) {
                        return full.checkType;
                    }
                },
                {
                    "data": "checkId",
                    "render": function ( data, type, full, meta ) {
                        return full.status;
                    }
                },
                { "data": "checkId",
                    "render": function ( data, type, full, meta ) {
                        if(full.checkCommon){
                            return (full.checkCommon==="Y")?"Yes":"No";
                        }
                        else
                            return "No";
                    }
                },
                {
                    "data": "checkId",
                    "render": function ( data, type, full, meta ) {
                        if(full.permissionsDef)
                            return full.permissionsDef.permName;
                        else return "";
                    }

                },
                {
                    "data": "checkId",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-checks='+encodeURI(JSON.stringify(full)) + ' onclick="ChecksSetup.editChecks(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "checkId",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-checks='+encodeURI(JSON.stringify(full)) + ' onclick="ChecksSetup.confirmDelChecks(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        }));
        return table;
    };

    var confirmDelChecks = function(button){
        var checks = JSON.parse(decodeURI($(button).data("checks")));
        bootbox.confirm("Are you sure want to delete "+checks["checkName"]+"?", function(result) {
            if(result){
                $.ajax({
                    type: 'GET',
                    url:  'deleteChecks/' + checks["checkId"],
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
                        $('#common-checks-tbl').DataTable().ajax.reload();
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

    var createProdChecksList = function(prodId){
        var url = "prodChecksList";
        var table = $('#checks-tbl').DataTable( {
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
                'data':{
                    'prodId': prodId,
                },
            },
            lengthMenu: [ [10, 15], [10, 15] ],
            pageLength: 10,
            destroy: true,
            "columns": [
                {
                    "data": "checks",
                    "render": function ( data, type, full, meta ) {
                        return full.checks.checkShtDesc;
                    }
                },
                {
                    "data": "checks",
                    "render": function ( data, type, full, meta ) {
                        return full.checks.checkName;
                    }
                },
                {
                    "data": "checks",
                    "render": function ( data, type, full, meta ) {
                        return full.checks.checkType;
                    }
                },
                {
                    "data": "checks",
                    "render": function ( data, type, full, meta ) {
                        return full.checks.status;
                    }
                },
                {
                    "data": "checks",
                    "render": function ( data, type, full, meta ) {
                        if(full.checks.permissionsDef)
                            return full.checks.permissionsDef.permName;
                        else return "";
                    }

                },
                {
                    "data": "checks",
                    "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-checks='+encodeURI(JSON.stringify(full)) + ' onclick="ChecksSetup.confirmProdDelChecks(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        } );
        return table;
    };

    var confirmProdDelChecks = function(button){
        var checks = JSON.parse(decodeURI($(button).data("checks")));
        bootbox.confirm("Are you sure want to delete "+checks["checks"].checkName+"?", function(result) {
            if(result){
                $.ajax({
                    type: 'GET',
                    url:  'deleteProdChecks/' + checks["checkId"],
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        Swal.fire({
					title: 'Success',
					text: 'Record Deleted Successfully',
					icon: 'success'
				});
                        $('#checks-tbl').DataTable().ajax.reload();
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

    var editChecks = function(button){
        var checks = JSON.parse(decodeURI($(button).data("checks")));
        $("#check-id").val(checks["checkId"]);
        $("#check-sht-desc").val(checks["checkShtDesc"]);
        $("#check-name").val(checks["checkName"]);
        $("#check-type").val(checks["checkType"]);
        if (checks["permissionsDef"]) {
            $("#perm-id").val(checks["permissionsDef"].permId);
            $("#perm-name").val(checks["permissionsDef"].permName);
        }
        if(checks['checkCommon'] && checks['checkCommon']==="Y") {
            $("#chk-appli").prop("checked", true);
        }
        else {
            $("#chk-appli").prop("checked", false);
        }
        createPermSel();
        $("#chk-active").prop("checked", checks["status"]);
        $('#checksModal').modal('show');
    };

    var init = function(){
        createProdChecksList(-2000);
        createChecksList();
        addChecks();
        checksDialog();
        createProductSel();
        saveProdChecks();
    };

    return {
        init: init,
        editChecks:editChecks,
        confirmDelChecks:confirmDelChecks,
        confirmProdDelChecks:confirmProdDelChecks
    }

})(jQuery);
jQuery(ChecksSetup.init);
