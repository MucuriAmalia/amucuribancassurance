/**
 * Created by peter on 5/12/2017.
 */
$(function() {

    $(document).ready(function () {
        createEventTypes();
        addEventTypes();
    });
});


function addEventTypes(){
    $("#btn-add-event-types").on("click", function(){
        $('#event-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#eventTypesModal').modal('show');
        return;
    });

    var $form = $('#event-type-form');
    var validator = $form.validate();
    $('#eventTypesModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#event-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveEventType').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createEvents";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#event-type-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#eventTypesModal').modal('hide');
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



function createEventTypes(){
    var url = "eventslist";
    var table = $('#event-type-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "eventShtDesc" },
            { "data": "eventDesc" },
            {
                "data": "type",
                "render": function ( data, type, full, meta ) {
                  if(full.type){
                     if(full.type==="A") return "Accident";
                     else if(full.type==="I") return "Illness";
                     else if(full.type==="D") return "Death";
                     else return "";
                  }
                    else return "";

                }

            },
            {
                "data": "eventId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-eventtypes='+encodeURI(JSON.stringify(full)) + ' onclick="editEventTypes(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "eventId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-eventtypes='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelEventtypes(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}

function editEventTypes(button){
    var eventtypes = JSON.parse(decodeURI($(button).data("eventtypes")));
    $("#event-type-pk").val(eventtypes["eventId"]);
    $("#event-type-id").val(eventtypes["eventShtDesc"]);
    $("#event-type-desc").val(eventtypes["eventDesc"]);
    $("#event-type").val(eventtypes["type"]);
    $('#eventTypesModal').modal('show');
}


function confirmDelEventtypes(button){
    var eventtypes = JSON.parse(decodeURI($(button).data("eventtypes")));
    bootbox.confirm("Are you sure want to delete "+eventtypes["eventDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteEvent/' + eventtypes["eventId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#event-type-tbl').DataTable().ajax.reload();
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