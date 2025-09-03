/**
 * Created by peter on 5/3/2017.
 */
$(function() {

    $(document).ready(function () {
        createAilments();
        $('#cost-per-limit, #ailment-upper-limit').number( true, 2 );
        addAilments();
    });
});



function addAilments(){
    $("#btn-add-ailments").on("click", function(){
        $('#ailment-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $("#chronic").prop("checked", false);
        $('#ailmentModal').modal('show');
        return;
    });

    $("#btn-add-diagnosis").on("click", function(){
        if ($("#diag-ailment-pk").val() !== '') {
            $('#ailment-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
            $("#chronic").prop("checked", false);
            $("#diag-ail-pk").val($("#diag-ailment-pk").val());
            $('#ailmentModal').modal('show');
            return;
        }
        else {
            bootbox.alert("Select Ailment to Add Diagnosis");
            return;
        }
    });

    var $form = $('#ailment-form');
    var validator = $form.validate();
    $('#ailmentModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#ailment-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveAilments').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createAilment";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#ailments-tbl').DataTable().ajax.reload();
            $('#diagnosis-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#ailmentModal').modal('hide');
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


function createDiagnosis(baId){
    var url = "diagnosis/"+baId;
    var table = $('#diagnosis-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "baShtDesc" },
            { "data": "baDesc" },
            { "data": "waitingDays" },
            { "data": "chronic" },
            { "data": "genderAffected",
                "render": function ( data, type, full, meta ) {
                    if(full.genderAffected){
                        if(full.genderAffected ==="M") return "Male";
                        else if(full.genderAffected ==="F") return "Female";
                        else if(full.genderAffected ==="B") return "Both";
                        else return "";

                    }else return "";
                }
            },
            {
                "data": "baId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-ailments='+encodeURI(JSON.stringify(full)) + ' onclick="editAilments(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "baId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-ailments='+encodeURI(JSON.stringify(full)) + ' onclick="deleteAilments(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}

function createAilments(){
    var url = "ailmentslist";
    var table = $('#ailments-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "baShtDesc" },
            { "data": "baDesc" },
            { "data": "waitingDays" },
            { "data": "chronic" },
            { "data": "genderAffected",
                "render": function ( data, type, full, meta ) {
                    if(full.genderAffected){
                        if(full.genderAffected ==="M") return "Male";
                        else if(full.genderAffected ==="F") return "Female";
                        else if(full.genderAffected ==="B") return "Both";
                        else return "";

                    }else return "";
                }
            },
            {
                "data": "baId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-ailments='+encodeURI(JSON.stringify(full)) + ' onclick="editAilments(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "baId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-ailments='+encodeURI(JSON.stringify(full)) + ' onclick="deleteAilments(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    $('#ailments-tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = table.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null){

        }
        else{
            createDiagnosis(aData[0].baId);
            $("#diag-ailment-pk").val(aData[0].baId);
        }
    } );
    return table;
}

function editAilments(button){
    var ailments = JSON.parse(decodeURI($(button).data("ailments")));
    $("#ailment-pk").val(ailments["baId"]);
    $("#ailment-id").val(ailments["baShtDesc"]);
    $("#ailment-desc").val(ailments["baDesc"]);
    $("#cost-per-limit").val(ailments["costPerClaim"]);
    $("#ailment-upper-limit").val(ailments["upperLimit"]);
    $("#ailment-wait-days").val(ailments["waitingDays"]);
    $("#ail-gender").val(ailments["genderAffected"]);
    $("#chronic").prop("checked", ailments["chronic"]);
    if(ailments["parentAilment"]){
        $("#diag-ail-pk").val(ailments["parentAilment"].baId);
    }
    $('#ailmentModal').modal('show');
}

function deleteAilments(button){
    var ailments = JSON.parse(decodeURI($(button).data("ailments")));
    bootbox.confirm("Are you sure want to delete "+ailments["baDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteAilment/' + ailments["baId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#ailments-tbl').DataTable().ajax.reload();
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