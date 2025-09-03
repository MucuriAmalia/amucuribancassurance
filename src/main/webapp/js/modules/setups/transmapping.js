/**
 * Created by peter on 4/24/2017.
 */
$(function(){

    $(document).ready(function() {
        transmappingMappingList();
        newMapping();
        $(document).ajaxStart(function () {
            $("#saveMapping").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#saveMapping").attr("disabled", false);
        });
    });
});

function newMapping(){
    $("#btn-add-mapping").on("click", function(){
        $('#mapping-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $('#transMappingModal').modal('show');
    });

    var $form = $('#mapping-form');
    var validator = $form.validate();
    $('#transMappingModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#mapping-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveMapping').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createTransMapping";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#trans-mapping-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#transMappingModal').modal('hide');
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

function transmappingMappingList(){
    var url = "mappingList";
    var table = $('#trans-mapping-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "transType" },
            { "data": "debitCode" },
            { "data": "creditCode" },
            {
                "data": "tmNo",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-mappings='+encodeURI(JSON.stringify(full)) + ' onclick="editMapping(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "tmNo",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-mappings='+encodeURI(JSON.stringify(full)) + ' onclick="confirmMappingDel(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}

function editMapping(button){
    var mappings = JSON.parse(decodeURI($(button).data("mappings")));
    $("#mapping-id-pk").val(mappings["tmNo"]);
    $("#trans-type").val(mappings["transType"]);
    $("#debit-code").val(mappings["debitCode"]);
    $("#credit-code").val(mappings["creditCode"]);
    $('#transMappingModal').modal('show');
}

function confirmMappingDel(button){
    var mappings = JSON.parse(decodeURI($(button).data("mappings")));
    bootbox.confirm("Are you sure want to delete "+mappings["transType"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteTransMapping/' + mappings["tmNo"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#trans-mapping-tbl').DataTable().ajax.reload();
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