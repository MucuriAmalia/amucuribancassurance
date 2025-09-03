/**
 * Created by peter on 4/17/2017.
 */

var UTILITIES = UTILITIES || {};
$(function() {

    $(document).ready(function () {
        createUserRoles();
        createModules();
        $("#module-id-pk").val("-2000");
        $("#role-id-pk").val("-2000");
        createPermissions();
        createPermissionsTable();
        $(".permissions").hide();
        newRole();
        saveRoles();
        savePermLimits();
    })
})


function newRole(){
    $("#btn-add-roles").on("click", function(){
        $('#roles-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#userRolesModal').modal('show');
    });
}

function newPermission(){
    $("#btn-add-permissions").on("click", function(){
        $('#permissions-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#permissionsModal').modal('show');
    });
}


function saveRoles(){
    var $form = $('#roles-form');
    var validator = $form.validate();
    $('#userRolesModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#roles-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveRoles').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createRoles";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#users-roles-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#userRolesModal').modal('hide');
            $(".permissions").hide();
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

function savePermissions(moduleId){
    var $form = $('#permissions-form');
    var validator = $form.validate();
    $('#permissionsModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#permissions-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#save-permission').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        data.roleId = $("#role-id-pk").val();
        data.module = moduleId;
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createPermissions";
        var request = $.post(url, data);
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            var url = "permissions/"+$("#role-id-pk").val()+"/"+$("#module-id-pk").val();
            $('#user-permission-tbl').DataTable().ajax.url(url).load();
            validator.resetForm();
            $('#permissionsModal').modal('hide');
            // $(".permissions").hide();
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


function createUserRoles(){
    var url = "rolesList";
    var table = $('#users-roles-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: false,
        "columns": [
            { "data": "roleId" },
            { "data": "roleName" },
            {
                "data": "roleId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-roles='+encodeURI(JSON.stringify(full)) + ' onclick="editRole(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "roleId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-roles='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelRole(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );

    $('#users-roles-tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = table.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {
        }
        else{
             $("#role-id-pk").val(aData[0].roleId);
             $(".permissions").show();
            $('#users-modules-tbl').DataTable().ajax.reload();
            var url = "permissions/"+$("#role-id-pk").val()+"/"+$("#module-id-pk").val();
            $('#user-permission-tbl').DataTable().ajax.url( url ).load();
        }
    } );
    return table;
}

function editRole(button){
    var roles = JSON.parse(decodeURI($(button).data("roles")));
    $("#user-role-id").val(roles["roleId"]);
    $("#user-role-name").val(roles["roleName"]);
    $('#userRolesModal').modal('show');
}


function confirmDelRole(button){
    var roles = JSON.parse(decodeURI($(button).data("roles")));
    bootbox.confirm("Are you sure want to delete "+roles["roleName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteRole/' + roles["roleId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#users-roles-tbl').DataTable().ajax.reload();
                    $(".permissions").hide();
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

function createModules(){
    var url = "modulesList";
    var table = $('#users-modules-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "sDom": 't',
        "ajax": url,
        lengthMenu: [ [200], [200] ],
        pageLength: 200,
        destroy: false,
        "columns": [
            { "data": "moduleName" },
        ]
    } );
    $('#users-modules-tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = table.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {
        }
        else{

            $("#module-id-pk").val(aData[0].moduleId);
            $("#moduleId").val(aData[0].moduleId);
            console.log($("#role-id-pk").val())
            $("#btn-add-permissions").prop("disabled",false);
            var url = "permissions/"+$("#role-id-pk").val()+"/"+$("#module-id-pk").val();
            $('#user-permission-tbl').DataTable().ajax.url( url ).load();
        }
        console.log($("#module-id-pk").val());
        newPermission();
        savePermissions($("#module-id-pk").val());
    } );
    return table;
}


function createPermissions(){
    var url = "permissions/"+$("#role-id-pk").val()+"/"+$("#module-id-pk").val();
    console.log(url);
    var table = $('#user-permission-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [7], [7] ],
        pageLength: 7,
        "language":
            {
                "processing": "<div class='overlay custom-loader-background'><i class='fa fa-cog fa-spin custom-loader-color'></i></div>"
            },
        destroy: false,
        deferRender: true,
        "orderMulti": false,
        "columns": [
            { "data": "permName" },
            { "data": "permDesc" },
            { "data": "accessType" },
            {
                "data": "permId",
                "render": function (data, type, full, meta) {
                    if(full.accessType){
                        if(full.accessType ==="L")
                            return UTILITIES.currencyFormat(full.minAmount);
                        else
                            return "";
                    }
                    else return "";
                }
            },
            {
                "data": "permId",
                "render": function (data, type, full, meta) {
                    if(full.accessType){
                        if(full.accessType ==="L")
                        return UTILITIES.currencyFormat(full.maxAmount);
                        else
                            return "";
                    }
                    else return "";

                }
            },
            {
                "data": "permId",
                "render": function ( data, type, full, meta ) {
                       if(full.count===0)
                       return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-permissions='+encodeURI(JSON.stringify(full)) + ' onclick="grantPermission(this);">Grant</button>';
                       else
                       return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-permissions='+encodeURI(JSON.stringify(full)) + ' onclick="revokePermission(this);">Revoke</button>';

                }

            },
            {
                "data": "permId",
                "render": function ( data, type, full, meta ) {
                    if(full.count===0)
                        return '';
                    else{
                        if(full.accessType){
                            if(full.accessType ==="L")
                                return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-permissions='+encodeURI(JSON.stringify(full)) + ' onclick="editLimits(this);">Edit</button>';
                            else
                                return "";
                        }
                        else return "";

                    }


                }

            }
        ]
    } );
    return table;
}

function revokePermission(button){
    var permissions = JSON.parse(decodeURI($(button).data("permissions")));
    $("#role-perm-id").val(permissions['permId']);
    var $form = $('#permission-form');
    if (!$form.valid()) {
        return;
    }
    var data = {};
    $form.serializeArray().map(function(x){data[x.name] = x.value;});
    console.log(data);
    var url = "revokePermission";
    var request = $.post(url, data );
    request.success(function(){
        Swal.fire({
            title: 'Success',
            text: 'Permission Revoked Successfully',
            icon: 'success'
        });
        $('#user-permission-tbl').DataTable().ajax.reload(null,false);
    });
    request.error(function(jqXHR, textStatus, errorThrown){
        Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
    });
    request.always(function(){

    });
}

function editLimits(button){
    var permissions = JSON.parse(decodeURI($(button).data("permissions")));
    $("#limits-perm-id").val(permissions['permId']);
    $("#limits-role-id").val($("#role-id-pk").val());
    $("#perm-min-amount").val(permissions['minAmount']);
    $("#perm-max-amount").val(permissions['maxAmount']);
    $("#perm-min-amount").number(true, 2);
    $("#perm-max-amount").number(true, 2);
    $('#permLimitsModal').modal('show');
}



function savePermLimits(){
    var $form = $('#perm-limits-form');
    var validator = $form.validate();
    $('#permLimitsModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#perm-limits-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#savePermLimits').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "updatePermLimits";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#user-permission-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#permLimitsModal').modal('hide');
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

function grantPermission(button){
    var permissions = JSON.parse(decodeURI($(button).data("permissions")));
    $("#role-perm-id").val(permissions['permId']);
    var $form = $('#permission-form');
        if (!$form.valid()) {
            return;
        }
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        console.log(data);
        var url = "grantPermission";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Permission Granted Successfully',
                icon: 'success'
            });
            $('#user-permission-tbl').DataTable().ajax.reload(null,false);
        });
        request.error(function(jqXHR, textStatus, errorThrown){
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
        request.always(function(){

        });
}

function createPermissionsTable() {
    var url = "allPermissions";
    var table = $('#permissions-tbl').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax":
            url,
            lengthMenu: [ [10, 15], [10, 15] ],
            pageLength: 10,
            destroy: true,
            "columns": [
                { "data": "moduleName" },
                { "data": "permName" },
                // { "data": "permDesc" },
                { "data": "accessType" },
                {
                    "data": "enableMakerChecker",
                    "render": function (data) {
                        if(data === 'Y'){
                            return "Yes";
                        } else if(data === 'N'){
                            return "No";
                        } else {
                            return "";
                        }
                    }
                }
            ],
            "language": {
                "processing": "<div class='overlay custom-loader-background'><i class='fa fa-cog fa-spin custom-loader-color'></i></div>"
            },
    });

    return table;
}

