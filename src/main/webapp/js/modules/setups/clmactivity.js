/**
 * Created by peter on 3/5/2017.
 */

$(function() {

    $(document).ready(function () {
        createClmActivities();
        newClaimActivity();
        saveClaimActivity();
    });
});

function newClaimActivity(){
    $("#btn-add-clm-activity").on("click", function(){
        $('#claim-activity-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#clmActivityModal').modal('show');
    });
}

function saveClaimActivity(){
    var $form = $('#claim-activity-form');
    var validator = $form.validate();
    $('#clmActivityModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#claim-activity-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveClmActivity').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createClmActivity";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#clm-activities-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#clmActivityModal').modal('hide');
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

function editClmActivity(button){
    var activity = JSON.parse(decodeURI($(button).data("climactivity")));
    $("#act-id").val(activity["caId"]);
    $("#act-desc").val(activity["activityDesc"]);
    $("#prd-type").val(activity["periodType"]);
    $("#act-period").val(activity["period"]);
    $("#chk-mandatory").prop("checked", activity["mandatory"]);
    $('#clmActivityModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function confirmDelActivity(button){
    var activity = JSON.parse(decodeURI($(button).data("climactivity")));
    bootbox.confirm("Are you sure want to delete "+activity["activityDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteClmActivity/' + activity["caId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#clm-activities-tbl').DataTable().ajax.reload();
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

function createClmActivities(){
    var url = "clmActivities";
    var table = $('#clm-activities-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "activityDesc" },
            {
                "data": "caId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-climactivity='+encodeURI(JSON.stringify(full)) + ' onclick="editClmActivity(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "caId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-climactivity='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelActivity(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}