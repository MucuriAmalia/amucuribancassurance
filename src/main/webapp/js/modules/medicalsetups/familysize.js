/**
 * Created by peter on 4/24/2017.
 */
$(function() {

    $(document).ready(function () {

        createFamilySize();
        addFamilySize();

    });
});

function addFamilySize() {
    $("#btn-add-fam-size").on("click", function () {
        $('#family-size-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#famSizeModal').modal('show');
        return;
    });

    var $form = $('#family-size-form');
    var validator = $form.validate();
    $('#famSizeModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#family-size-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveFamilySize').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createFamilySize";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#family-size-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#famSizeModal').modal('hide');
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


function createFamilySize(){
    var url = "familysizelist";
    var table = $('#family-size-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "famShtDesc" },
            { "data": "famDesc" },
            { "data": "famSize" },
            {
                "data": "famId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-family='+encodeURI(JSON.stringify(full)) + ' onclick="editFamilySize(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "famId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-family='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelFamilySize(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}


function editFamilySize(button){
    var size = JSON.parse(decodeURI($(button).data("family")));
    $("#fam-size-pk").val(size["famId"]);
    $("#fam-size-id").val(size["famShtDesc"]);
    $("#fam-size-desc").val(size["famDesc"]);
    $("#fam-size").val(size["famSize"]);
    $('#famSizeModal').modal('show');
}


function confirmDelFamilySize(button){
    var size = JSON.parse(decodeURI($(button).data("family")));
    bootbox.confirm("Are you sure want to delete "+size["famDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteFamilySize/' + size["famId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#family-size-tbl').DataTable().ajax.reload();
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