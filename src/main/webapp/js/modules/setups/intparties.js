/**
 * Created by HP on 9/13/2017.
 */
var OTHERSETUPS= OTHERSETUPS || {};

$(function() {

    $(document).ready(function () {

        OTHERSETUPS.createIntParties();
        OTHERSETUPS.newInterestedParties();
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                "allowInputToggle": true,
                "showClose": true,
                "showClear": true,
                "showTodayButton": true,
                "format": "DD/MM/YYYY",
            });
        });
        $(document).ajaxStart(function () {
            $("#saveIntPartiesBtn").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#saveIntPartiesBtn").attr("disabled", false);
        });

    });
});

OTHERSETUPS.newInterestedParties = function(){
    $("#btn-add-int-parties").on("click", function(){
        $('#parties-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $('#intPartiesModal').modal('show');
    });

    $("#part-type").on('change', function(){
        if($(this).val()==="B"){
            $("label.dob").html("Date of Birth");
            $("label.idno").html("ID No");
        }
        else{
            $("label.dob").html("Date of Registration");
            $("label.idno").html("Registration No.");
        }
    });

    var $form = $('#parties-form');
    var validator = $form.validate();
    $('#intPartiesModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#parties-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveIntPartiesBtn').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createIntParties";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#intPartiesTbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#intPartiesModal').modal('hide');
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

OTHERSETUPS.confirmDelParties= function(button){
    var parties = JSON.parse(decodeURI($(button).data("parties")));
    bootbox.confirm("Are you sure want to delete "+parties["partName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteIntParties/' + parties["partCode"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#intPartiesTbl').DataTable().ajax.reload();
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

OTHERSETUPS.editParties = function(button){
    var parties = JSON.parse(decodeURI($(button).data("parties")));
    $("#part-code").val(parties["partCode"]);
    $("#part-name").val(parties["partName"]);
    $("#part-post-address").val(parties["postalAddress"]);
    $("#part-email").val(parties["emailAddress"]);
    $("#part-pin").val(parties["pinNumber"]);
    $("#part-tel").val(parties["telNo"]);
    $("#date-reg").val(moment(parties["dateRegistered"]).format('DD/MM/YYYY'));
    $("#part-id-no").val(parties["regNo"]);
    $("#part-type").val(parties["partType"]);
    $('#intPartiesModal').modal('show');
}

OTHERSETUPS.createIntParties= function(){
    var url = "interestedparties";
    var currTable = $('#intPartiesTbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "partName" },
            { "data": "partType",
                "render": function ( data, type, full, meta ) {
                    if(full.partType){
                        if(full.partType==="P"){
                            return "Premium Financier";
                        }
                        else if(full.partType==="B"){
                            return "Beneficiary";
                        }
                        else if(full.partType==="I"){
                            return "Interested Party";
                        }
                    }
                    else
                    return "";
                }
            },
            { "data": "pinNumber" },
            { "data": "postalAddress" },
            { "data": "telNo" },
            { "data": "emailAddress" },
            { "data": "dateRegistered" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.dateRegistered).format('DD/MM/YYYY');
                }
            },
            { "data": "user",
                "render": function ( data, type, full, meta ) {

                    return full.user.username;
                }
            },
            { "data": "dateCreated" ,
                "render": function ( data, type, full, meta ) {
                    return moment(full.dateCreated).format('DD/MM/YYYY');
                }
            },
            {
                "data": "partCode",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-parties='+encodeURI(JSON.stringify(full)) + ' onclick="OTHERSETUPS.editParties(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "partCode",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-parties='+encodeURI(JSON.stringify(full)) + ' onclick="OTHERSETUPS.confirmDelParties(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}