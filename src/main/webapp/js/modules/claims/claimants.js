/**
 * Created by peter on 3/5/2017.
 */
$(function() {

    $(document).ready(function () {
        createClaimants();
        addClaimants();
        saveClaimant();
        selectOccupationLov();
        editClaimants();
        deleteClaimants();

    });
});


function selectOccupationLov(){
    if($("#occup-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "occup-def",
            sort : 'name',
            change: function(e, a, v){
                $("#clmt-occupt").val(e.added.code);
            },
            formatResult : function(a)
            {
                return a.name
            },
            formatSelection : function(a)
            {
                return a.name
            },
            initSelection: function (element, callback) {
                var code = $("#clmt-occupt").val();
                var name = $("#clmt-occupt-names").val();
//	                model.accounts.branch.brnCode = code;
                var data = {name:name,code:code};
                callback(data);
            },
            id: "code",
            placeholder:"Select Occupation"
        });
    }
}

function addClaimants(){
    $("#btn-add-claimant").click(function(){
        $('#claimants-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=email],input[type=password],input[type=hidden], textarea,select").val("");
        $('#claimantsModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });
}

function saveClaimant(){
    var $classForm = $('#claimants-form');
    var validator = $classForm.validate();
    $('#saveClaimantDef').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createClaimant";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#claimant-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#claimants-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=email],input[type=password],input[type=hidden], textarea,select").val("");
            $('#claimantsModal').modal('hide');
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

function editClaimants(button){
    var claimants = JSON.parse(decodeURI($(button).data("claimants")));
    $("#clmnt-id").val(claimants["claimantId"]);
    $("#clmnt-surname").val(claimants["surname"]);
    $("#clmnt-othernames").val(claimants["otherNames"]);
    $("#clmt-occupt").val(claimants["occupId"]);
    $("#clmt-occupt-names").val(claimants["occupation"]);
    selectOccupationLov();
    $("#clmnt-idnumber").val(claimants["idNumber"]);
    $("#clmnt-email").val(claimants["email"]);
    $("#clmnt-address").val(claimants["address"]);
    $("#clmnt-mobnumber").val(claimants["mobileNo"]);
    $('#claimantsModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function deleteClaimants(button){
    var claimants = JSON.parse(decodeURI($(button).data("claimants")));
    bootbox.confirm("Are you sure want to delete "+claimants["surname"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteClaimant/' + claimants["claimantId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#claimant-tbl').DataTable().ajax.reload();
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

function createClaimants(){
    var url = "claimantdefs";
    var currTable = $('#claimant-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,20,50], [10,20,50] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "surname" },
            { "data": "otherNames" },
            {
                "data": "occupation",
                "render": function ( data, type, full, meta ) {
                    return full.occupation;
                }

            },
            { "data": "idNumber" },
            { "data": "email" },

            { "data": "address" },
            { "data": "mobileNo" },
            { "data": "createdBy" },
            {
                "data" : "createdDate",
                "render" : function(data, type, full, meta) {
                    if(full.createdDate){
                        return moment(full.createdDate).format('DD/MM/YYYY');
                    }
                    else {
                        return "";
                    }
                }

            },
            {
                "data": "claimantId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-claimants='+encodeURI(JSON.stringify(full)) + ' onclick="editClaimants(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "claimantId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-claimants='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClaimants(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}