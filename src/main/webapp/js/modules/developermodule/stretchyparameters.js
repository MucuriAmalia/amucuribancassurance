var STRETCHY_PARAMETERS_UTILITIES = STRETCHY_PARAMETERS_UTILITIES || {};

STRETCHY_PARAMETERS_UTILITIES.createStretchyParameters = function () {
    // Initialize form validation
    $('#parameter-form').validate({
        rules: {
            paramName: {
                required: true
            },
            parameterVariable: {
                required: false
            },
            paramActualName: {
                required: true
            },
            parameterDisplayType: {
                required: true
            },
            paramType: {
                required: true
            },
            parameterDefault: {
                required: true
            },
            parameterSql: {
                required: true
            }
        },
        messages: {
            // Define error messages for each field
            paramName: "Please enter parameter name",
            paramActualName: "Please enter parameter label",
            parameterDisplayType: "Please enter display type",
            paramType: "Please enter format type",
            parameterDefault: "Please enter parameter default",
            parameterSql: "Please enter correct parameter SQL"
        },
        submitHandler: function(form) {
            // Serialize the form data into an array
            var formData = $(form).serializeArray();

            // Define an object to hold the form data
            var data = {};

            // Convert the serialized form data array into key-value pairs
            $.each(formData, function(index, field){
                data[field.name] = field.value;
            });

            // Define the URL for the POST request
            var url = SERVLET_CONTEXT + "/protected/stretchyparameters/createStretchyParam";

            // Send the POST request with the form data
            var request = $.post(url, data);

            // Handle the success response of the request
            request.success(function(){
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success',
                    showCancelButton: false,
                    confirmButtonText: 'OK'
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = redirectUrl;
                    }
                });
                $(form).trigger("reset");
            });

            // Handle the error response of the request
            request.error(function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
        }
    });

    // Prevent default form submission
    $('#parameter-form').submit(function(e) {
        e.preventDefault(); // Prevent the form from submitting normally
    });
}

STRETCHY_PARAMETERS_UTILITIES.createStretchyParametersListing = function () {
    var url = SERVLET_CONTEXT+ "/protected/stretchyparameters/stretchyparams";
    return $('#streparamtbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "searching": true,
        "ajax": {
            'url': url
        },
        lengthMenu: [[10, 15], [10, 15]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {"data": "stpId"},
            {"data": "paramName"},
            {"data": "paramActualName"},
            {"data": "paramType"},
            {
                "data": "stpId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-xs" type="button" data-parameters=' + encodeURI(JSON.stringify(full)) + ' onclick="STRETCHY_PARAMETERS_UTILITIES.viewParameters(this);"><i class="fa fa-eye"></button>';
                }

            },
            {
                "data": "stpId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-xs" type="button" data-parameters=' + encodeURI(JSON.stringify(full)) + ' onclick="STRETCHY_PARAMETERS_UTILITIES.editStretchyParameters(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "stpId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-xs" data-parameters=' + encodeURI(JSON.stringify(full)) + ' onclick="STRETCHY_PARAMETERS_UTILITIES.deleteStretchyParameters(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    });
}

STRETCHY_PARAMETERS_UTILITIES.deleteStretchyParameters = function (button) {
    var parameters = JSON.parse(decodeURI($(button).data("parameters")));
    bootbox.confirm("Are you sure want to delete "+parameters["parameterName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'DELETE',
                url:  'deleteStretchyParameter/' + parameters["stpId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                    new PNotify({
                        title: 'Success',
                        text: 'Record Deleted Successfully',
                        type: 'success',
                        styling: 'bootstrap3'
                    });
                    $('#streparamtbl').DataTable().ajax.reload();
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    new PNotify({
                        title: 'Error',
                        text: jqXHR.responseText,
                        type: 'error',
                        styling: 'bootstrap3'
                    });
                }
            });
        }
    });
}

STRETCHY_PARAMETERS_UTILITIES.editStretchyParameters = function (button) {

}
$(function() {

        $('.btn-can').on('click',function(){
            $("#perm-id").val('');
            $('#rpt-param-form')[0].reset();
            jQuery('.select2-offscreen').select2('val', '');
            $("#rptParamModal").modal('hide');
        })

        $('.btn-xs').on('click',function(){
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

