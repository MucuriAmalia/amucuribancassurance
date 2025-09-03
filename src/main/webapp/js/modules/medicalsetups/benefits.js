/**
 * Created by peter on 4/21/2017.
 */
$(function() {

    $(document).ready(function () {
        createBenefits();
        addBenefits();
        createBenefitsSections();
        addBenefitSections();
    });
});

function addBenefitSections(){
    $("#btn-add-benefit-sections").on("click", function(){
        $('#benefits-section-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#sectionModal').modal('show');
        return;
    });

    var $form = $('#benefits-section-form');
    var validator = $form.validate();
    $('#sectionModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#benefits-section-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveBenefitsSection').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createBenSections";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#benefits-sections-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#sectionModal').modal('hide');
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


function addBenefits(){
    $("#btn-add-benefits").on("click", function(){
        $('#benefits-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#benefitsModal').modal('show');
        return;
    });

    var $form = $('#benefits-form');
    var validator = $form.validate();
    $('#benefitsModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#benefits-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveBenefits').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createBenefits";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#benefits-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#benefitsModal').modal('hide');
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


function createBenefits(){
    var url = "benefitslist";
    var table = $('#benefits-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "benShtDesc" },
            { "data": "benDesc" },
            { "data": "preauthored" },
            {
                "data": "benId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="editBenefits(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "benId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="confirmBenefits(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}

function editBenefits(button){
    var benefits = JSON.parse(decodeURI($(button).data("benefits")));
    $("#benefit-pk").val(benefits["benId"]);
    $("#benefit-id").val(benefits["benShtDesc"]);
    $("#benefit-desc").val(benefits["benDesc"]);
    $("#chk-pre-authored").prop("checked", benefits["preauthored"]);
    $('#benefitsModal').modal('show');
}

function confirmBenefits(button){
    var benefits = JSON.parse(decodeURI($(button).data("benefits")));
    bootbox.confirm("Are you sure want to delete "+benefits["benDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteBenefits/' + benefits["benId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#benefits-tbl').DataTable().ajax.reload();
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


function createBenefitsSections(){
    var url = "bensectionslist";
    var table = $('#benefits-sections-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "benShtDesc" },
            { "data": "benDesc" },
            {
                "data": "benId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="editBenefitSections(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "benId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="confirmBenefitSections(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}

function editBenefitSections(button){
    var benefits = JSON.parse(decodeURI($(button).data("benefits")));
    $("#benefit-pk").val(benefits["benId"]);
    $("#benefit-id").val(benefits["benShtDesc"]);
    $("#benefit-desc").val(benefits["benDesc"]);
    $('#sectionModal').modal('show');
}

function confirmBenefitSections(button){
    var benefits = JSON.parse(decodeURI($(button).data("benefits")));
    bootbox.confirm("Are you sure want to delete "+benefits["benDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteBenSections/' + benefits["benId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#benefits-sections-tbl').DataTable().ajax.reload();
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