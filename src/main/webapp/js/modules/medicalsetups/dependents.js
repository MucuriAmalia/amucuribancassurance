/**
 * Created by peter on 4/21/2017.
 */
$(function() {

    $(document).ready(function () {

        createDependentTypes();
        addDepTypes();

    })
});

function createDependentTypes(){
    var url = "dependtypeslist";
    var table = $('#dep-type-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "depShtDesc" },
            { "data": "depDesc" },
            { "data": "mainMember" },
            {
                "data": "depId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-deptypes='+encodeURI(JSON.stringify(full)) + ' onclick="editDepTypes(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "depId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-deptypes='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelDeptypes(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}

function confirmDelDeptypes(button){
    var deptypes = JSON.parse(decodeURI($(button).data("deptypes")));
    bootbox.confirm("Are you sure want to delete "+deptypes["depDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteDependTypes/' + deptypes["depId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#dep-type-tbl').DataTable().ajax.reload();
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

function editDepTypes(button){
    var deptypes = JSON.parse(decodeURI($(button).data("deptypes")));
    $("#depend-type-pk").val(deptypes["depId"]);
    $("#depend-type-id").val(deptypes["depShtDesc"]);
    $("#depend-type-desc").val(deptypes["depDesc"]);
    $("#med-dep-child").val(deptypes["child"]);
    $("#dep-main-member").prop("checked", deptypes["mainMember"]);
    $("#gender").val(deptypes["gender"]);
    $('#dependTypesModal').modal('show');
}


function addDepTypes(){
    $("#btn-add-depend-types").on("click", function(){
        $('#dep-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#dependTypesModal').modal('show');
        $("#dep-main-member").prop("checked", false);
        return;
    });

    var $form = $('#dep-type-form');
    var validator = $form.validate();
    $('#dependTypesModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#dep-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveDepType').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createDependTypes";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#dep-type-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#dependTypesModal').modal('hide');
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
