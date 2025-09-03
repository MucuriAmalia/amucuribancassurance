/**
 * Created by HP on 7/26/2017.
 */
var APP = APP || {};
var WORKFLOWS = APP.WORKFLOWS || {};

WORKFLOWS.getWorkflows = function() {
    $('#doc-name option').remove();
    $('#doc-name').append($('<option>', {
        value: "",
        text : "Select Document Type"
    }));
    $.ajax({
        type: 'GET',
        url: 'doctypes',
        dataType: 'json',
        async: true,
        success: function (result) {
             for(var res in result){
                $('#doc-name').append($('<option>', {
                    value: result[res].docTypeName,
                    text : result[res].docTypeValue
                }));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });
}

WORKFLOWS.updateDiagram = function(docType){
    var rand =Math.floor((Math.random() * 100000000) + 1);
    var url = SERVLET_CONTEXT + '/protected/workflow/'+docType;
    console.log(url);
    url += ('?rand=' + rand);
    $('#proc-main-diagram').attr('src', url);
}

WORKFLOWS.uploadProcessDoc = function(){

    $('#updateWFDoc').click(function(){
        var $accTypesFrm= $('#doc-upload-form');
        var validator = $accTypesFrm.validate();
        if (!$accTypesFrm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $accTypesFrm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "updateWFDocument";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Process Updated Successfully',
                icon: 'success'
            });
            WORKFLOWS.getWorkflows();
        });
        request.error(function(jqXHR, textStatus, errorThrown){
            // $('#myPleaseWait').modal('hide');
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


    $(document).ready(function () {

        WORKFLOWS.getWorkflows();
        WORKFLOWS.uploadProcessDoc();
        console.log('getting workflow...');

        $("#doc-name").on('change', function(){
            WORKFLOWS.updateDiagram($(this).val());
        });

    });
