/**
 * Created by peter on 4/21/2017.
 */
$(function() {

    $(document).ready(function () {
        createSpecialistFees();
        addSpecialist();
        $("#specialist-upper-limit").number(true, 2);


    });
});

function addSpecialist(){
    $("#btn-add-specialist").on("click", function(){
        $('#specialist-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#specialistModal').modal('show');
        return;
    });

    var $form = $('#specialist-form');
    var validator = $form.validate();
    $('#specialistModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#specialist-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveSpecialists').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createSpecialFees";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#specialist-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#specialistModal').modal('hide');
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


function createSpecialistFees(){
    var url = "specfeeslist";
    var table = $('#specialist-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "specShtDesc" },
            { "data": "specDesc" },
            { "data": "upperLimit" },
            {
                "data": "specId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-specialist='+encodeURI(JSON.stringify(full)) + ' onclick="editSpecialist(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "specId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-specialist='+encodeURI(JSON.stringify(full)) + ' onclick="confirmSpecialist(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}

function editSpecialist(button){
    var specialist = JSON.parse(decodeURI($(button).data("specialist")));
    $("#specialist-pk").val(specialist["specId"]);
    $("#specialist-id").val(specialist["specShtDesc"]);
    $("#specialist-desc").val(specialist["specDesc"]);
    $("#specialist-upper-limit").val(specialist["upperLimit"]);
    $('#specialistModal').modal('show');
}


function confirmSpecialist(button){
    var specialist = JSON.parse(decodeURI($(button).data("specialist")));
    bootbox.confirm("Are you sure want to delete "+specialist["specDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteSpecialistFees/' + specialist["specId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#specialist-tbl').DataTable().ajax.reload();
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