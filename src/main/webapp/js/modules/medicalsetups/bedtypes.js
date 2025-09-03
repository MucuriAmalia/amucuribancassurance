/**
 * Created by peter on 4/21/2017.
 */
var UTILITIES = UTILITIES || {};
$(function() {

    $(document).ready(function () {
        createBedTypes();
        createCardTypes();
        addBedTypes();
        createLabTests();
        addLabTest();
        addBenefits();
        addCardTypes();
        $("#lab-upper-limit").number(true, 2);
    });
});

function addBedTypes(){
    $("#btn-add-bed-types").on("click", function(){
        $('#bed-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#bedTypesModal').modal('show');
        return;
    });

    var $form = $('#bed-type-form');
    var validator = $form.validate();
    $('#bedTypesModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#bed-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveBedType').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createBedTypes";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#bed-type-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#bedTypesModal').modal('hide');
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



function addCardTypes(){
    $("#btn-add-card-types").on("click", function(){
        $('#card-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#cardTypesModal').modal('show');
        return;
    });

    var $form = $('#card-type-form');
    var validator = $form.validate();
    $('#cardTypesModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#bed-type-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveCardType').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createCardTypes";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#card-type-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#cardTypesModal').modal('hide');
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

function editBedTypes(button){
    var bedtypes = JSON.parse(decodeURI($(button).data("bedtypes")));
    $("#bed-type-pk").val(bedtypes["bedId"]);
    $("#bed-type-id").val(bedtypes["bedShtDesc"]);
    $("#bed-type-desc").val(bedtypes["bedDesc"]);
    $("#bed-cost").val(bedtypes["bedCost"]);
    $('#bedTypesModal').modal('show');
}

function editCardTypes(button){
    var cardtypes = JSON.parse(decodeURI($(button).data("cardtypes")));
    $("#card-type-pk").val(cardtypes["cardId"]);
    $("#card-type-id").val(cardtypes["cardShtDesc"]);
    $("#card-type-desc").val(cardtypes["cardDesc"]);
    $('#cardTypesModal').modal('show');
}

function confirmDelBedtypes(button){
    var bedtypes = JSON.parse(decodeURI($(button).data("bedtypes")));
    bootbox.confirm("Are you sure want to delete "+bedtypes["bedDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteBedTypes/' + bedtypes["bedId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#bed-type-tbl').DataTable().ajax.reload();
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


function confirmDelCardTypes(button){
    var cardtypes = JSON.parse(decodeURI($(button).data("cardtypes")));
    bootbox.confirm("Are you sure want to delete "+cardtypes["cardDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteCardTypes/' + cardtypes["cardId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#card-type-tbl').DataTable().ajax.reload();
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


function createBedTypes(){
    var url = "bedtypeslist";
    var table = $('#bed-type-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "bedShtDesc" },
            { "data": "bedDesc" },
            { "data": "bedCost",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.bedCost);
                }
            },
            {
                "data": "bedId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-bedtypes='+encodeURI(JSON.stringify(full)) + ' onclick="editBedTypes(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "bedId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-bedtypes='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelBedtypes(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}


function createCardTypes(){
    var url = "cardtypeslist";
    var table = $('#card-type-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "cardShtDesc" },
            { "data": "cardDesc" },
            {
                "data": "cardId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-cardtypes='+encodeURI(JSON.stringify(full)) + ' onclick="editCardTypes(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "cardId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-cardtypes='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelCardTypes(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return table;
}


function createLabTests(){
    var url = "labtestslist";
    var table = $('#lab-tests-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "labShtDesc" },
            { "data": "labDesc" },
            { "data": "upperLimit" },
            { "data": "cptCode" },
            {
                "data": "labId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-labtests='+encodeURI(JSON.stringify(full)) + ' onclick="editLabTest(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "labId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-labtests='+encodeURI(JSON.stringify(full)) + ' onclick="confirmDelLabTest(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );

    $('#lab-tests-tbl tbody').on( 'click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = table.row( this ).data();
        if(d){
            $("#lab-pk").val(d.labId);
            createActivities(d.labId);

        }
    } );
    return table;
}





function createActivities(serviceCode){
    var url = "benefitslist/"+serviceCode;
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
            {
                "data": "section",
                "render": function ( data, type, full, meta ) {
                      if(full.section){
                          return full.section.desc;
                      }
                    else return "";
                 }

            },
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


function addBenefits(){
    $("#btn-add-benefits").on("click", function(){
        if ($("#lab-pk").val() != '') {
            $('#benefits-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
            $("#lab-benefit-pk").val($("#lab-pk").val());
            populateBenefitLov();
            $('#benefitsModal').modal('show');
            return;
        }
        else{
            bootbox.alert('Select Service to Add Activity');
        }
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


function editBenefits(button){
    var benefits = JSON.parse(decodeURI($(button).data("benefits")));
    $("#benefit-pk").val(benefits["benId"]);
    $("#benefit-id").val(benefits["benShtDesc"]);
    $("#benefit-desc").val(benefits["benDesc"]);
    $("#activity-type").val(benefits["activityType"]);
    $("#lab-benefit-pk").val(benefits["services"].labId);
    if(benefits['section']) {
        $("#benefit-sec-id").val(benefits['section'].id);
        $("#benefit-sec-dec").val(benefits['section'].desc);
    }
    populateBenefitLov();
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



function addLabTest(){
    $("#btn-add-lab-tests").on("click", function(){
        $('#lab-test-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
        $('#labtestsModal').modal('show');
        return;
    });

    var $form = $('#lab-test-form');
    var validator = $form.validate();
    $('#labtestsModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#lab-test-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveLabTest').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createLabTest";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#lab-tests-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#labtestsModal').modal('hide');
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



function editLabTest(button){
    var labtests = JSON.parse(decodeURI($(button).data("labtests")));
    $("#lab-test-pk").val(labtests["labId"]);
    $("#lab-test-id").val(labtests["labShtDesc"]);
    $("#lab-test-desc").val(labtests["labDesc"]);
    $("#lab-upper-limit").val(labtests["upperLimit"]);
    $("#lab-cpt-code").val(labtests["cptCode"]);
    $('#labtestsModal').modal('show');
}

function confirmDelLabTest(button){
    var labtests = JSON.parse(decodeURI($(button).data("labtests")));
    bootbox.confirm("Are you sure want to delete "+labtests["labDesc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deletelabTest/' + labtests["labId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#lab-tests-tbl').DataTable().ajax.reload();
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


function populateBenefitLov(){
    if($("#section-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "section-frm",
            sort : 'desc',
            change: function(e, a, v){
                $("#benefit-sec-id").val(e.added.id);

            },
            formatResult : function(a)
            {
                return a.desc;
            },
            formatSelection : function(a)
            {
                return a.desc;
            },
            initSelection: function (element, callback) {
                var code  = $('#benefit-sec-id').val();
                var name = $("#benefit-sec-dec").val();
                var data = {desc:name,id:code};
                callback(data);
            },
            id: "id",
            width:"250px",
            placeholder:"Select Benefit"

        });
    }
}