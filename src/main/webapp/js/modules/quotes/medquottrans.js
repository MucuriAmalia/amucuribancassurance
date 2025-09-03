/**
 * Created by waititu on 06/08/2017.
 */
var UTILITIES = UTILITIES || {};
$(function() {


    $(document).ready(function () {

        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });

        $('#overrid-prem').number( true, 2 );
        $("#btn-add-client").hide();

        $("#email-to").on('change', function(){
            $.ajax({
                type: 'GET',
                url:  'getReceiverEmail',
                dataType: 'json',
                data: {"receiver": $(this).val()},
                async: true,
                success: function(result) {
                    $("#email-send-to").val(result);
                },
                error: function(jqXHR, textStatus, errorThrown) {

                }
            });
        });


        $("#pol-bin-type").on('change', function(){
            populateBinderLov();

        });
        $("#pol-medicalCoverType").on('change', function(){
            if ($("#pol-medicalCoverType").val()==='I'){
            $("#profile-tab-2").hide();
                $(".individualDetails").show();

        } else {$("#profile-tab-2").show();
            $(".individualDetails").hide();
            hideCol();
            }

        });
        if(!($("#quot-id").val()==="")){
        quotId=$("#quot-id").val();
        }
        populateCurrencyLov();

        populatePaymentModes();
        populateUserBranches();
        saveCategoryRule();
        addNewCategoryRules();
        //getQuotCategories();
        populateQuotDetails();
        getPolicyWet();
        newCategories();
        changePolicyWetDt();
        createNewQuote();
        saveCategories();
        createPolicyTaxes();
        newFamilyDetails();
        updateBenefitLimits();
        populateClientLov();
        populateBinderLov();
        saveFamilyDetails();
        saveCategoryRules();
        saveQuotTaxes();
        addNewPolTaxes();
        saveNewPolTaxes();
        //$("#pol-ins-comp").text("bind-ins-name");
        //$("#pol-prod-name").text("bind-pro-name");
       // getNewPremItemsModal();
        //newRisk();
       // addQuoteProd();
        changeClientType();
        addProspects();
        populateClientTypeLov();
        populateClientTypeLov2();
        populateSourceGroupLov();
        saveProspects();
       // populateBranchLov1();
       // populateCountryLov();
      //  populateTitlesLov();
    //    createSectorSelect();
      //  saveClientDetails();
        UTILITIES.createAssignee();
    });
});


function updateBenefitLimits(){
    var $form = $('#benefit-limit-form');
    var validator = $form.validate();
    $('#benefitLimitModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#benefit-limit-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#updateBenefitLimit').click(function(){
        if (!$form.valid()) {
            return;
        }
        // $('#myPleaseWait').modal({
        //     backdrop: 'static',
        //     keyboard: true
        // });
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        if ($("#fund-Binder").val()==="Y") {
            var url = "updateQuotFundBenefit" ;
        } else {
            var url = "updateQuotCatbenefits";
        }
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            populateQuotDetails();
            $('#med-categories').DataTable().ajax.reload();
            $('#medCoversList').DataTable().ajax.reload();
            $('#familyDetailsList').DataTable().ajax.reload();
            $('#polTaxesList').DataTable().ajax.reload();
            $('#polclausesList').DataTable().ajax.reload();
            validator.resetForm();
            $('#benefitLimitModal').modal('hide');
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

function getQuotCategories(){
    var url = "getQuotCategories";
    var table;
    if ($("#pol-medicalCoverType").val()==='I') {
        table = $('#med-categories').DataTable({

            "processing": true,
            "serverSide": true,
            "ajax": url,
            lengthMenu: [[10, 15], [10, 15]],
            pageLength: 10,
            destroy: true,
            "columns": [

                {
                    "data": "binderDetails",
                    "render": function (data, type, full, meta) {

                        return full.binderDetails.subCoverTypes.subclass.subDesc + "-" + full.binderDetails.subCoverTypes.coverTypes.covName;
                    }
                },
                {"data": "shtDesc"},
                {"data": "desc"},
                {"data": "principalAge"},
                {"data": "principalGender"},
                {"data": "spouseAge"},
                {"data": "childrenCount"},
                {
                    "data": "premium",
                    "render": function (data, type, full, meta) {

                        return UTILITIES.currencyFormat(full.premium);
                    }
                },
                {
                    "data": "id",
                    "render": function (data, type, full, meta) {
                        if (full.quotation.quotStatus) {
                            if (full.quotation.quotStatus === "D")
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-quotcategories=' + encodeURI(JSON.stringify(full)) + ' onclick="editQoutCategories(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-quotcategories=' + encodeURI(JSON.stringify(full)) + ' onclick="editQoutCategories(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                        }


                    }

                },
                {
                    "data": "id",
                    "render": function (data, type, full, meta) {
                        if (full.quotation.quotStatus) {
                            if (full.quotation.quotStatus === "D")
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotcategories=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteQuotCategories(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotcategories=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteQuotCategories(this);" disabled><i class="fa fa-trash-o"></button>';

                        }


                    }

                },
            ]
        });
    } else{
        table = $('#med-categories').DataTable({

            "processing": true,
            "serverSide": true,
            "ajax": url,
            lengthMenu: [[10, 15], [10, 15]],
            pageLength: 10,
            destroy: true,
            "columns": [

                {
                    "data": "binderDetails",
                    "render": function (data, type, full, meta) {

                        return full.binderDetails.subCoverTypes.subclass.subDesc + "-" + full.binderDetails.subCoverTypes.coverTypes.covName;
                    }
                },
                {"data": "shtDesc"},
                {"data": "desc"},
                {
                    "data": "premium",
                    "render": function (data, type, full, meta) {

                        return UTILITIES.currencyFormat(full.premium);
                    }
                },
                {
                    "data": "id",
                    "render": function (data, type, full, meta) {
                        if (full.quotation.quotStatus) {
                            if (full.quotation.quotStatus === "D")
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-quotcategories=' + encodeURI(JSON.stringify(full)) + ' onclick="editQoutCategories(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-quotcategories=' + encodeURI(JSON.stringify(full)) + ' onclick="editQoutCategories(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                        }


                    }

                },
                {
                    "data": "id",
                    "render": function (data, type, full, meta) {
                        if (full.quotation.quotStatus) {
                            if (full.quotation.quotStatus === "D")
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotcategories=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteQuotCategories(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotcategories=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteQuotCategories(this);" disabled><i class="fa fa-trash-o"></button>';

                        }


                    }

                },
            ]
        });

    }

    if ($("#pol-medicalCoverType").val()==='G') {
        hideCol();
    }
    $('#med-categories tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = table.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null){

        }
        else{
            $("#fam-category-pk").val(aData[0].id);
            $("#fam-category-bin-pk").val(aData[0].binderDetails.detId);
            createMedicalCovers(aData[0].id);
            getCategoryRules(aData[0].id);
            getCategoryFamDetails(aData[0].id);

        }
    } );
    return table;
}

function hideCol() {

    $('#med-categories th:nth-child(' + 4 + ')').hide();
    $('#med-categories th:nth-child(' + 5 + ')').hide();
    $('#med-categories th:nth-child(' + 6 + ')').hide();
    $('#med-categories th:nth-child(' + 7 + ')').hide();
}
function deleteQuotCategories(button){
    var categories = JSON.parse(decodeURI($(button).data("quotcategories")));
    bootbox.confirm("Are you sure want to delete "+categories["desc"]+"?  This will delete all items under this category.", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteQuotCategory/' + categories["id"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#med-categories').DataTable().ajax.reload();
                    $('#med-rules-tbl').DataTable().ajax.reload();
                    $('#med-members').DataTable().ajax.reload();
                    $('#medCoversList').DataTable().ajax.reload();
                    $('#exclusionsList').DataTable().ajax.reload();
                    $('#loadingsList').DataTable().ajax.reload();
                    $('#providersList').DataTable().ajax.reload();
                    $('#familyDetailsList').DataTable().ajax.reload();
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
function editCategoryFamDetails(button){
    var familyDtls = JSON.parse(decodeURI($(button).data("quotcategoriesfamdtls")));
    $('#med-category-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
    $("#fam-details-id").val(familyDtls['familyId']);
    $("#fam-category-id").val(familyDtls["category"].id);
    $("#family-size").val(familyDtls['familySize']);
    $("#require-cards").val(familyDtls['requireCards']);
    $("#age-bracket").val(familyDtls['agebracket']);
    $("#family-count").val(familyDtls['families']);
    $('#familyDetailsModal').modal('show');
}

function editQoutCategories(button){
    var categories = JSON.parse(decodeURI($(button).data("quotcategories")));
   // $(".categoryDisplay").show();
    $("#category-quot-id").val(quotId);;
    $("#category-id").val(categories['id']);
    $("#binder-det-pk").val(categories["binderDetails"].detId);
    //+"-"+categories["binderDetails"].subCoverTypes.coverTypes.covName
    $("#binderDetails-Display").text(categories["binderDetails"].subCoverTypes.subclass.subDesc +"-"+  categories["binderDetails"].subCoverTypes.coverTypes.covName);
    $("#principal-age").val(categories['principalAge']);
    $("#principal-Gender").val(categories['principalGender']);
    $("#spouse-age").val(categories['spouseAge']);
    $("#children-count").val(categories['childrenCount']);
    $("#sht-desc").val(categories['shtDesc']);
    $("#cat-desc").val(categories['desc']);
    $(".categoryLov").hide();
    $(".categoryDetails").show();
    $("#saveCategory").show();
    if ($("#pol-medicalCoverType").val()==='I'){
        $(".individualDetails").show();

    } else {
        $(".individualDetails").hide();}
    $('#categoryModal').modal('show');
}



function editPolTaxes(button){
    var tax = JSON.parse(decodeURI($(button).data("taxes")));
    console.log("testing again ="+UTILITIES.getRevDesc(tax["revenueItems"].item));

    // $(".categoryDisplay").show();
    $("#tax-quot-id").val(quotId);
    $("#tax-id").val(tax['quotTaxId']);
    $("#Trans-Code").text(UTILITIES.getRevDesc(tax["revenueItems"].item));
    $("#tax-rate").val(tax['taxRate']);
    $("#tax-rate-divfact").val(tax['divFactor']);
    $('#editTaxModal').modal('show');
}

function saveNewPolTaxes(){
    var arr = [];
    $("#saveNewTaxBtn").click(function(){
        $("#taxesTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Tax Selected to attach..");
            return;
        }

        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });

        var $currForm = $('#tax-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createNewQuotTax";
        data.taxes = arr;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#polTaxesList').DataTable().ajax.reload();
                $('#taxesModal').modal('hide');
                populateQuotDetails();
                arr=[];
            },
            error : function(jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            },
            dataType : "json",
            contentType : "application/json"
        });

    })
}


function addNewPolTaxes() {

    $("#btn-add-new-tax").click(function () {
        $("#tax-policy-code").val(quotId);
        $.ajax({
            type: 'GET',
            url: 'unassignQuotTaxes',
            dataType: 'json',
            data: {"quotId":quotId},
            async: true,
            success: function (result) {
                $("#taxesTbl tbody").each(function () {
                    $(this).remove();
                });
                for (var res in result) {
                    var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res][0] + "'></td><td>" + UTILITIES.getRevDesc(result[res][1]) + "</td><td>" + result[res][2] + "</td><td>" + result[res][3] + "</td><td>" + result[res][4] + "</td><td>" + result[res][5] + "</td></tr>";
                    $("#taxesTbl").append(markup);
                }
                $('#taxesModal').modal({
                    backdrop: 'static',
                    keyboard: true
                })
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            }
        });



    });
}

function saveQuotTaxes(){

    var $form = $('#med-tax-form');
    var validator = $form.validate();
    $('#editTaxModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#med-tax-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveQuoteTax').click(function(){
        if (!$form.valid()) {
            return;
        }
        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "saveQuotTaxes";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#polTaxesList').DataTable().ajax.reload();

            validator.resetForm();
            $('#editTaxModal').modal('hide');
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
function saveCategoryRules(){
    var arr = [];
    $("#saveNewRulesBtn").click(function(){
        $("#binderRulesTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Rules Selected to attach..");
            return;
        }

        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });

        var $currForm = $('#category-rule-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createCategoryRules";
        data.rules = arr;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                $('#med-rules-tbl').DataTable().ajax.reload();
                $('#binderRulesModal').modal('hide');
                arr=[];
            },
            error : function(jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            },
            dataType : "json",
            contentType : "application/json"
        });
    })
}

function addNewCategoryRules() {

    $("#btn-add-new-rule").click(function () {
        if ($("#fam-category-pk").val() != '') {
            $("#rule-category-code").val($("#fam-category-pk").val());
            $.ajax({
                type: 'GET',
                url: 'unassignBinderRules',
                dataType: 'json',
                data: {"catId": $("#fam-category-pk").val()},
                async: true,
                success: function (result) {
                    $("#binderRulesTbl tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res][0] + "'></td><td>" + result[res][1] + "</td><td>" + result[res][2] + "</td></tr>";
                        $("#binderRulesTbl").append(markup);
                    }
                    $('#binderRulesModal').modal({
                        backdrop: 'static',
                        keyboard: true
                    })
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                }
            });

        }
        else {
            bootbox.alert("Select Category to continue");
        }

    });
}

function editCategoryRule(button){
    var rules = JSON.parse(decodeURI($(button).data("rules")));
    $("#cat-rule-pk").val(rules['sectId']);
    $("#rule-desc-disp").text(rules['desc']);
    $("#rule-value").val(rules['value']);
    $('#catRulesModal').modal('show');
}


function deleteCategoryRule(button){
    var rules = JSON.parse(decodeURI($(button).data("rules")));
    bootbox.confirm("Are you sure want to delete "+rules["desc"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteCategoryRule/' + rules["sectId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#med-rules-tbl').DataTable().ajax.reload();
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

function deletePolTaxes(button){
    var tax = JSON.parse(decodeURI($(button).data("taxes")));
    bootbox.confirm("Are you sure want to delete?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteQuotTax/' + tax["quotTaxId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#polTaxesList').DataTable().ajax.reload();
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

function makeReady(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'makeMedQuoteReady',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                        title: 'Success',
                        text: 'Submitted Successfully',
                        icon: 'success'
                    });
            populateQuotDetails();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}
function createNewQuote(){
    $("#btn-add-quote").click(function(){
        if(typeof quotId!== 'undefined'){
            if(quotId!==-2000){
                updateQuote();
            }
            else{
                createQuote();
            }
        }
        else{
            createQuote();
        }

    });

    $("#btn-save-risk").click(function(){
        if($("#risk-code-pk").val() != ''){
            updateRisk();
        }
        else
            createRisk();
    });

    $("#btn-save-quot-prod").click(function(){
        createQuoteProduct();
    });


    $("#btn-make-ready-policy").click(function(){
        makeReady();
    });
    $("#btn-undo-make-ready-policy").click(function(){
        undoMakeReady();
    });
    $("#btn-auth-quote").click(function(){
        authQuote();
    });

    $("#btn-undo-make-ready").click(function(){
        undoMakeReady();
    });

    $("#btn-confirm-quote").click(function(){
        confirmQuote();
    });

    $("#btn-convert-quote").click(function(){
        convertQuote();
    });

    $("#btn-cancel-quote").click(function(){
        $('#closeModal').modal({
            backdrop: 'static',
            keyboard: true
        })

    });

    $("#closeQuote").click(function(){
        cancelQuote();
    });


}



function undoMakeReady(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'undoMakeReadyQuote',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                        title: 'Success',
                        text: 'Submitted Successfully',
                        icon: 'success'
                    });
            populateQuotDetails();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}

function cancelQuote(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    $.ajax({
        type: 'GET',
        url:  'cancelQuote',
        dataType: 'json',
        data: {"reason": $("#quot-reasons").val()},
        async: true,
        success: function(result) {
            $('#closeModal').modal('hide');
            Swal.fire({
                        title: 'Success',
                        text: 'Quote Closed Successfully',
                        icon: 'success'
                    });
            populateQuotDetails();
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


function confirmQuote(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'confirmQuote',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                        title: 'Success',
                        text: 'Quote Confirmation Process Successfully',
                        icon: 'success'
                    });
            populateQuotDetails();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}

function authQuote(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'authorizeQuote',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                        title: 'Success',
                        text: 'Authorization Process Successfully',
                        icon: 'success'
                    });
            populateQuotDetails();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}

function convertQuote(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'convertMedQuote',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Quote Converted Successfully',
                icon: 'success'
            });
            populateQuotDetails();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}
function updateQuote(){

    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    var $currForm = $('#quot-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        return;
    }
    $('#quot-form input[type=checkbox]').each(function(e){
        $(this).val($(this).is(':checked'));
    });
    var data = {};
    $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
    var url = "createMedQuotation";
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax(
        {
            url:url,
            type: "POST",
            data: JSON.stringify(data),
            success: function(s){
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                        title: 'Success',
                        text: 'Quote Transaction created Successfully',
                        icon: 'success'
                    });
               /* $('#insured-frm').select2('val', null);
                populateInsuredLov();
                $('#subclass-frm').select2('val', null);
                populateSubclassLov();
                $('#covertypes-frm').select2('val', null);
                populateCoverTypesLov();*/
                quotId = s.quoteId;
                console.log(quotId);
                populateQuotDetails();
            },
            error: function(jqXHR, textStatus, errorThrown){
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            },
            dataType: "json",
            contentType: "application/json"
        } );
}


function populatePaymentModes(){
    if($("#pm-mode-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "pm-mode-frm",
            sort : 'pmDesc',
            change: function(e, a, v){
                $("#pm-id").val(e.added.pmId);
            },
            formatResult : function(a)
            {
                return a.pmDesc;
            },
            formatSelection : function(a)
            {
                return a.pmDesc;
            },
            initSelection: function (element, callback) {
                var code  = $('#pm-id').val();
                var name = $("#pm-name").val();
                var data = {pmDesc:name,pmId:code};
                callback(data);
            },
            id: "pmId",
            width:"250px",
            placeholder:"Select Payment Mode"

        });
    }
}

function createQuote(){

    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    /*var arr = getRskSections();
    if(arr.length==0){
        bootbox.alert("Cannot create Quote without sections")
        return false;

    arr.shift();}*/
    var $currForm = $('#quot-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        return;
    }

    $('#quot-form input[type=checkbox]').each(function(e){
        $(this).val($(this).is(':checked'));
    });

    var data = {};
    $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
    var url = "createMedQuotation";

    //data.sections = arr;
    //data.riskBean = getRiskDetails();
    //data.quoteProductBean = getQuotProductDetails();
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax(
        {
            url:url,
            type: "POST",
            data: JSON.stringify(data),
            success: function(s){
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                        title: 'Success',
                        text: 'Quote Transaction created Successfully',
                        icon: 'success'
                    });
              /*  $('#insured-frm').select2('val', null);
                populateInsuredLov();
                $('#subclass-frm').select2('val', null);
                //populateSubclassLov();
                $('#covertypes-frm').select2('val', null);
                populateCoverTypesLov();*/
                quotId = s.quoteId;
                console.log(quotId);
                populateQuotDetails();
            },
            error: function(jqXHR, textStatus, errorThrown){
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            },
            dataType: "json",
            contentType: "application/json"
        } );
}

function getQuotationDetails(){
    if(typeof quotId!== 'undefined'){

        if(quotId!==-2000){
            $.ajax( {
                url: 'getMedQuotationDetails/'+quotId,
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s ) {
                    $("#quot-id").val(quotId);
                    console.log(s.quotStatus);

                    if (s.quotStatus) {

                        if (s.quotStatus === "D") {
                            $("#btn-assign-trans").show();
                            $("#btn-add-quote").css("display", "block");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "none");
                            $("#btn-convert-quote").css("display", "none");
                            $("#btn-cancel-quote").css("display", "none");
                            $("#btn-make-ready-policy").css("display", "block");
                            $("#btn-undo-make-ready-policy").css("display","none");
                            $("#pol-status").text("Draft");
                            $("#edit-currency").show();
                            $("#display-currency").hide();
                            $("#edit-branch").show();
                            $("#display-branch").hide();
                            $("#edit-payment-mode").show();
                            $("#display-payment-mode").hide();
                            $("#edit-binder").show();
                            $("#display-binder").hide();
                            $("#edit-client").show();
                            $("#display-client").hide();
                            $("#display-source").hide();
                            $("#display-sourcegroup").hide();
                            $("#from-date").removeAttr('disabled');
                            $("#wet-date").removeAttr('disabled');
                            $('#clnt-type').removeAttr("disabled");
                            $('#pol-buss-type').removeAttr("disabled");
                            $('#pol-bin-type').removeAttr("disabled");
                            $('#pol-medicalCoverType').removeAttr("disabled");
                            $('#card-issue').removeAttr("disabled");
                            $('#card-type').removeAttr("disabled");
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").show();
                            $("#btn-add-risk").show();
                            $("#edit-sourcegroup").show();
                            $("#edit-source").show();
                            $("#sel2").prop("disabled", false);
                            $("#btn-add-new-clause").show();
                            $("#btn-add-new-tax").show();
                            $("#btn-add-new-section").show();
                            //$("#btn-add-new-remark").show();
                            //$("#btn-save-remark").show();
                            $("#btn-add-new-fam").show();
                            $("#btn-add-new-benefit").show();
                            $("#btn-add-new-rule").show();
                            $("#btn-add-category").show();
                            $("#card-frm").select2("enable", true);

                        }
                        else if (s.quotStatus === "R") {
                            $("#btn-assign-trans").hide();
                            $("#sel2").prop("disabled", true);
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "block");
                            $("#btn-confirm-quote").css("display", "none");
                            $("#btn-convert-quote").css("display", "none");
                            $("#btn-cancel-quote").css("display", "none");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display","block");
                            $("#pol-status").text("Ready");
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#comm-rate").prop("disabled", true);
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").attr("disabled", "disabled");
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#card-type").prop("disabled", true);
                            $("#card-issue").prop("disabled", true);
                            $("#pol-medicalCoverType").prop("disabled", true);
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#btn-add-risk").hide();
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#btn-add-risk").hide();
                            //$("#btn-add-new-clause").show();
                            //$("#btn-add-new-tax").show();
                            $("#btn-add-new-section").show();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-fam").hide();
                            $("#btn-add-new-benefit").hide();
                            $("#btn-add-new-rule").hide();
                            $("#btn-add-category").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#card-frm").select2("enable", false);
                        }
                        else if (s.quotStatus === "A") {
                            $("#btn-assign-trans").hide();
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "block");
                            $("#btn-convert-quote").css("display", "none");
                            $("#btn-cancel-quote").css("display", "block");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display","none");
                            $("#pol-status").text("Authorised");
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#comm-rate").prop("disabled", true);
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").prop("disabled", true);
                            $("#policy-remarks").prop("disabled", true);
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#card-type").prop("disabled", true);
                            $("#card-issue").prop("disabled", true);
                            $("#pol-medicalCoverType").prop("disabled", true);
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $("#sel2").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#btn-add-risk").hide();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#btn-add-risk").hide();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#btn-add-new-fam").hide();
                            $("#btn-add-new-benefit").hide();
                            $("#btn-add-new-rule").hide();
                            $("#btn-add-category").hide();
                            $("#card-frm").select2("enable", false);
                        }
                        else if (s.quotStatus === "C") {
                            $("#btn-assign-trans").hide();
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "none");
                            $("#btn-convert-quote").css("display", "block");
                            $("#btn-cancel-quote").css("display", "block");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display","none");
                            $("#pol-status").text("Confirmed");
                            $("#sel2").prop("disabled", true);
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#comm-rate").prop("disabled", true);
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").prop("disabled", true);
                            $("#policy-remarks").prop("disabled", true);
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#card-type").prop("disabled", true);
                            $("#card-issue").prop("disabled", true);
                            $("#pol-medicalCoverType").prop("disabled", true);
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#btn-add-risk").hide();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#btn-add-risk").hide();
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#btn-add-new-fam").hide();
                            $("#btn-add-new-benefit").hide();
                            $("#btn-add-new-rule").hide();
                            $("#btn-add-category").hide();
                            $("#card-frm").select2("enable", false);
                        }
                        else if (s.quotStatus === "CL") {
                            $("#btn-assign-trans").hide();
                            $("#sel2").prop("disabled", true);
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "none");
                            $("#btn-convert-quote").css("display", "none");
                            $("#btn-cancel-quote").css("display", "none");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display","none");
                            $("#pol-status").text("Cancelled");
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#comm-rate").prop("disabled", true);
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").prop("disabled", true);
                            $("#policy-remarks").prop("disabled", true);
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#pol-medicalCoverType").prop("disabled", true);
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#btn-add-risk").hide();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#btn-add-risk").hide();
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#btn-add-new-fam").hide();
                            $("#btn-add-new-benefit").hide();
                            $("#btn-add-new-rule").hide();
                            $("#btn-add-category").hide();
                            $("#card-frm").select2("enable", false);
                        }
                        else if (s.quotStatus === "CV") {
                            $("#btn-assign-trans").hide();
                            $("#sel2").prop("disabled", true);
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "none");
                            $("#btn-convert-quote").css("display", "none");
                            $("#btn-cancel-quote").css("display", "none");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-undo-make-ready").css("display","none");
                            $("#pol-status").text("Cancelled");
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#comm-rate").prop("disabled", true);
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").prop("disabled", true);
                            $("#policy-remarks").prop("disabled", true);
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#pol-medicalCoverType").prop("disabled", true);
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#btn-add-risk").hide();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#btn-add-risk").hide();
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#btn-add-new-fam").hide();
                            $("#btn-add-new-benefit").hide();
                            $("#btn-add-new-rule").hide();
                            $("#btn-add-category").hide();
                            $("#card-frm").select2("enable", false);
                        }

                    }

                    if (s.client)
                        $("#client-info").text(s.client.fname + " " + s.client.otherNames);
                    else if (s.prospect)
                        $("#client-info").text(s.prospect.fname + " " + s.prospect.otherNames);

                    if (s.paymentMode) {

                    $("#pay-mode-info").text(s.paymentMode.pmDesc);
                     }

                    if (s.source){
                        $("#source-info").text(s.source.desc);
                        $("#source-name").val(s.source.desc);
                        $("#source-id").val(s.source.srcId);
                        $("#sourcegroup-id").val(s.source.businessSourceGroup.srcGroupId);
                        $("#sourcegroup-name").val(s.source.businessSourceGroup.desc);
                        $("#sourcegroup-info").text(s.source.businessSourceGroup.desc);
                    }
                    populateSourceGroupLov();
                    $("#branch-info").text(s.branch.obName);
                    $('#pol-comm-amt').text(UTILITIES.currencyFormat(s.commAmt));
                    $('#pol-vat-amt').text(UTILITIES.currencyFormat(s.vatAmount));
                    $("#currency-info").text(s.transCurrency.curName);
                    $("#pol-no").text(s.quotNo);
                    $("#pol-rev-no").text(s.quotRevNo);
                    $("#pol-premium").text(UTILITIES.currencyFormat(s.premium));
                    $("#pol-basic-prem").text(UTILITIES.currencyFormat(s.basicPrem));
                    $("#pol-net-prem").text(UTILITIES.currencyFormat(s.netPrem));
                   console.log(s.wefDate instanceof Date);
                    if(s.wefDate instanceof Date){
                        $("#from-date").val(moment(s.wefDate).format('DD/MM/YYYY'));
                        $("#wet-date").val(moment(s.wetDate).format('DD/MM/YYYY'));
                    }
                    else {
                        try {
                            console.log("here....");
                            var fromDate = new Date(s.wefDate);
                            var toDate = new Date(s.wetDate);
                            $("#from-date").val(moment(fromDate).format('DD/MM/YYYY'));
                            $("#wet-date").val(moment(toDate).format('DD/MM/YYYY'));
                        }
                        catch (Exception){
                            console.log("here....");
                        }
                    }

                    if(s.clientType)
                        $('#clnt-type').val(s.clientType);
                    else $('#clnt-type').val("C");
                    if(!(s.clientType) || s.clientType==='' || s.clientType==='C'){
                        $(".quot-client").text("Client *");
                        $("#client-div").show();
                        $("#insured-clnt-div").show();
                        $("#prs-div").hide();
                        $("#insured-prs-div").hide();
                        $("#client-id").val("");
                        $("#insured-name").val("");
                        $("#insured-code").val("");
                        $("#insured-other-name").val("");
                    }
                    else if(s.clientType==='P'){
                        $(".quot-client").text("Prospect *");
                        $("#client-div").hide();
                        $("#insured-clnt-div").hide();
                        $("#insured-prs-div").show();
                        $("#prs-div").show();
                        $('#insured-frm').select2('val', null);
                        $("#client-id").val("");
                        $("#insured-name").val("");
                        $("#insured-code").val("");
                        $("#insured-other-name").val("");
                        $("#btn-add-prs").val("Edit");
                        $("#btn-add-prs").show();
                    }

                    //$("#pol-interface-type").val(s.interfaceType);
                    //$("#pol-frequency").val(s.frequency);
                    //$("#client-pol-no").val(s.clientPolNo);
                    //$("#pol-buss-type").val(s.businessType);
                    $("#cur-id").val(s.transCurrency.curCode);
                    $("#cur-name").val(s.transCurrency.curName);
                    populateCurrencyLov();

                    if(s.client){
                        $("#client-id").val(s.client.tenId);
                        $("#client-f-name").val(s.client.fname);
                        $("#client-other-name").val(s.client.otherNames);
                    }
                    else if(s.prospect){
                        $("#client-id").val(s.prospect.tenId);
                        $("#client-f-name").val(s.prospect.fname);
                        $("#client-other-name").val(s.prospect.otherNames);
                    }
                    if (s.paymentMode) {
                        $("#pm-id").val(s.paymentMode.pmId);
                        $("#pm-name").val(s.paymentMode.pmDesc);
                    }
                    console.log("222222222222");
                    populatePaymentModes();
                    populateClientLov();
                    if (s.binder){
                    $("#binder-id").val(s.binder.binId);
                    $("#bind-name").val(s.binder.binName);
                    $("#binder-info").text(s.binder.binName);
                    }
                    //$("#pol-ins-comp").val("test");

                    populateBinderLov();
                    populateBinderDetails(s.binder.binId);





                    $("#pol-prod-name").text(s.binder.product.proDesc);
                    $("#product-id").val(s.binder.product.proCode);
                    $("#pol-ins-comp").text(s.binder.account.name);
                    $("#brn-id").val(s.branch.obId);

                    $("#pol-medicalCoverType").val(s.binder.medicalCoverType);
                    if (s.binder.medicalCoverType==='G')
                        $("#medicalCoverTyp-info").text('Group');
                    if (s.binder.medicalCoverType==='I')
                        $("#medicalCoverTyp-info").text('Individual');
                    if (s.binCardType){
                    $("#card-id").val(s.binCardType.id);
                    $("#card-name").val(s.binCardType.cardTypes.cardDesc);
                }
                    $("#pol-buss-type").val(s.businessType);
                    $("#pol-medicalCoverType").val(s.medicalCoverType);
                    getQuotCategories();
                    createQuotClauses();
                    createPolicyTaxes();

                    populateBinderCardTypes(s.binder.binId,moment(fromDate).format('DD/MM/YYYY'));


                   // $("#card-type").val(16);
                    if (s.binCardType)
                    $("#card-type > [value=" + s.binCardType.id + "]").attr("selected", "true");
                    getFamilySize(quotId);
                    getAgeBracket(quotId);

                    if(s.medicalCoverType)
                    { if (s.medicalCoverType==='I'){
                        $("#profile-tab-2").hide();
                        $(".individualDetails").show();
                        } else {
                        $("#profile-tab-2").show();
                        $(".individualDetails").hide();
                        hideCol();
                    }
                    }
                    console.log("s.medicalCoverType="+s.medicalCoverType)
                    $("#pol-bin-type").val(s.binder.binType);

                    $("#brn-name").val(s.branch.obName);
                    $('#pol-tl').text(UTILITIES.currencyFormat(s.trainingLevy));
                    $('#pol-phcf').text(UTILITIES.currencyFormat(s.phcf));
                    $('#pol-sd').text(UTILITIES.currencyFormat(s.stampDuty));
                    $('#pol-whtx').text(UTILITIES.currencyFormat(s.whtx));
                    $('#pol-extras').text(UTILITIES.currencyFormat(s.extras));
                    $("#issue-card-fee").text(UTILITIES.currencyFormat(s.issueCardFee));
                    $("#service-Charge").text(UTILITIES.currencyFormat(s.serviceCharge));
                    //$("#card-issue").val(s.issueCard);



                    UTILITIES.getProcessActiveDiagram(quotId);
                    UTILITIES.getTaskActive(quotId);
                    UTILITIES.getProcessHistory(quotId);
                    populateUserBranches();

                    //if(s.renewalDate)
                    //    $("#pol-ren-date").text(moment(s.renewalDate).format('DD/MM/YYYY'));
                },
                error: function(xhr, error){
                    bootbox.alert(xhr.responseText);
                }
            });
        }
    }
}


function populateBinderDetails(bindCode){

    console.log('bindCode='+bindCode)
    if($("#binderdet-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "binderdet-frm",
            sort : 'detId',
            change: function(e, a, v){
                $("#binder-det-pk").val(e.added.detId);

            },
            formatResult : function(a)
            {
                return a.subCoverTypes.subclass.subDesc+"-"+a.subCoverTypes.coverTypes.covName;
            },
            formatSelection : function(a)
            {
                return a.subCoverTypes.subclass.subDesc+"-"+a.subCoverTypes.coverTypes.covName;
            },
            initSelection: function (element, callback) {

            },
            id: "detId",
            width:"250px",
            params: {bindCode: bindCode},

            placeholder:"Select Category"

        });
    }

}
function populateQuotDetails(){
    if(typeof quotId!== 'undefined'){
        console.log('quotId='+quotId)
        if(quotId!==-2000){
            $("#btn-quot-reports").show();
            $("#risk-form").hide();
            $("#btn-save-risk").hide();
            $("#btn-save-cancel").hide();
            $("#btn-add-risk").show();
            $("#quot-prod-tbl").show();
            $("#quot-prod-div").hide();
            $("#risk-div").show();
            $("#sect-div").show();
            $("#prem-rates-div").hide();
            $("#other-pol-details").show();
            $("#btn-add-new-section").show();
            $("#btn-add-quot-prod").show();
            $("#btn-save-quot-prod").show();
            $("#btn-cancel-quot-prod").show();
            $("#btn-auth-quote").css("display","none");
            $("#uw_tabs_title").show();
            $("#uw_tabs").show();

            getQuotationDetails();
        }
        else{
            $("#btn-auth-quote").css("display","none");
            $("#btn-quot-reports").hide();
            $("#quot-prod-tbl").hide();
            $("#quot-prod-div").show();
            $("#risk-form").show();
            $("#risk-div").hide();
            $("#other-pol-details").hide();
            $("#prem-rates-div").show();
            $("#btn-add-quote").show();
            $("#sect-div").hide();
            $("#btn-save-risk").hide();
            $("#btn-save-cancel").hide();
            $("#btn-add-risk").hide();
            $("#myTab #show-taxes,#show-clauses").hide();
            $("#btn-add-new-section").hide();
            $("#client-div").show();
            $("#prs-div").hide();
            $("#insured-clnt-div").show();
            $("#insured-prs-div").hide();
            $("#display-client").hide();
            $("#display-binder").hide();
            $("#display-currency").hide();
            $("#btn-add-quot-prod").hide();
            $("#btn-save-quot-prod").hide();
            $("#btn-cancel-quot-prod").hide();
            $("#display-source").hide();
            $("#display-sourcegroup").hide();
            $("#edit-sourcegroup").show();
            $("#edit-source").show();
            $("#btn-add-prs").show();
            $("#btn-add-prs").val("New");
            $("#uw_tabs_title").hide();
            $("#uw_tabs").hide();

        }
    }
    else{
        $("#other-pol-details").hide();
    }
}

function populateQuotProspects(prospectId){
    $.ajax({
        type: 'GET',
        url: 'getProspectDetails/'+prospectId,
        dataType: 'json',
        async: true,
        success: function (result) {
            $("#prospect-id-pk").val(result.tenId);
            $("#prs-sht-desc").val(result.prospShtDesc);
            $("#fname").val(result.fname);
            $("#other-names").val(result.otherNames);
            $("#phone-no").val(result.phoneNo);
            $("#clnt-type-id").val(result.tenantType.typeId);
            $("#clnt-type-name").val(result.tenantType.typeDesc);
            $("#other-names").val(result.otherNames);
            $("#prosp-email").val(result.emailAddress);
            populateClientTypeLov();
            $("#pin-no").val(result.pinNo);
            $("#id-no").val(result.idNo);
            $("#gender").val(result.gender);
            $("#sel3").val(result.status);
            $("#sel3").attr("readonly",false);
            defaultCountry();
            $('#createProspectModal').modal({
                backdrop: 'static',
                keyboard: true
            })
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

function defaultCountry(){
    $.ajax( {
        url: SERVLET_CONTEXT+'/protected/clients/setups/getDefaultCountry',
        type: 'GET',
        processData: false,
        contentType: false,
        success: function (s ) {
            if(s){
                populateSmsPrefix(s.couCode);
            }
        },
        error: function(xhr, error){

        }
    });

}


function populateSmsPrefix(couCode){
    if($("#prs-sms-pref").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "prs-sms-pref",
            sort : 'prefixName',
            change: function(e, a, v){
                console.log(e.added.prefixId);
                $("#prs-pref-sms-id").val(e.added.prefixId);
            },
            formatResult : function(a)
            {
                return a.prefixName
            },
            formatSelection : function(a)
            {
                return a.prefixName
            },
            initSelection: function (element, callback) {
            },
            id: "prefixId",
            placeholder:"Prefix",
            params: {couCode: couCode}

        });
    }
}
function populateClientTypeLov2(){
    if($("#clnt-client-type-1").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-client-type-1",
            sort : 'clientType',
            change: function(e, a, v){
                $("#clnt-type-id-1").val(e.added.typeId);
                $("#clnt-type-name-1").val(e.added.typeDesc);
            },
            formatResult : function(a)
            {
                return  a.typeDesc
            },
            formatSelection : function(a)
            {
                return  a.typeDesc
            },
            initSelection: function (element, callback) {
            },
            id: "typeId",
            placeholder:"Select Client Type",

        });
    }
    if($("#clnt-client-type").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-client-type",
            sort : 'clientType',
            change: function(e, a, v){
                $("#clnt-type-id").val(e.added.typeId);
                $("#clnt-type-name").val(e.added.typeDesc);
            },
            formatResult : function(a)
            {
                return  a.typeDesc
            },
            formatSelection : function(a)
            {
                return  a.typeDesc
            },
            initSelection: function (element, callback) {
            },
            id: "typeId",
            placeholder:"Select Client Type",

        });
    }
}

function addProspects() {
    $("#btn-add-prs").click(function () {
        if($("#client-id").val()!=='')
            populateQuotProspects($("#client-id").val());
        else {
            $('#prospect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#sel3").val("A");
            $(".prop-status").hide();
            defaultCountry();
            $('#clnt-client-type').select2('val', null);
            $('#clnt-client-type').attr('value', '');
            $('#createProspectModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        }
    });



}

function saveProspects(){
    $('#saveProspectsBtn').click(function(){
        var $classForm = $('#prospect-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createProspect";
        var request = $.post(url, data );
        request.success(function(result){
            $("#client-id").val(result.tenId);
            $("#client-f-name").val(result.fname);
            $("#client-other-name").val(result.otherNames);
            populateClientLov();
            $("#insured-name").val(result.fname);
            $("#insured-code").val(result.tenId);
            $("#insured-other-name").val(result.otherNames);
           /// populateInsuredLov();
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            validator.resetForm();
            $('#prospect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#createProspectModal').modal('hide');
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
function populateClientTypeLov(){
    if($("#clnt-client-type").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clnt-client-type",
            sort : 'clientType',
            change: function(e, a, v){
                $("#clnt-type-id").val(e.added.typeId);
                $("#clnt-type-name").val(e.added.typeDesc);
            },
            formatResult : function(a)
            {
                return  a.typeDesc
            },
            formatSelection : function(a)
            {
                return  a.typeDesc
            },
            initSelection: function (element, callback) {
                var code = $("#clnt-type-id").val();
                var name = $("#clnt-type-name").val();
                var data = {typeDesc:name,typeId:code};
                callback(data);
            },
            id: "typeId",
            placeholder:"Select Client Type",

        });
    }
}

function  changeClientType(){
    $("#clnt-type").on('change', function(){
        $("#client-id").val("");
        $("#client-f-name").val("");
        $("#client-other-name").val("");
        populateClientLov();

        if(this.value==='' || this.value==='C'){
            $(".quot-client").text("Client *");
            $("#client-div").show();
            $("#insured-clnt-div").show();
            $("#prs-div").hide();
            $("#insured-prs-div").hide();
            $("#client-id").val("");
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-other-name").val("");
            $("#btn-add-client").show();
            $("#btn-add-prs").hide();
            $("#btn-add-client").val("New");
        }
        else if(this.value==='P'){
            $(".quot-client").text("Prospect *");
            $("#client-div").hide();
            $("#insured-clnt-div").hide();
            $("#insured-prs-div").show();
            $("#prs-div").show();
            $('#insured-frm').select2('val', null);
            $("#client-id").val("");
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-other-name").val("");
            $("#btn-add-prs").show();
            $("#btn-add-prs").val("New");
            $("#btn-add-client").hide();
        }
    });
}
function changePolicyWetDt(){

    $('#cover-to-date').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        $("#risk-wet-date").val(dt);
        $("#product-wet-date").val(dt);
    });



}

function getPolicyWet(){
    $('#wef-date').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        $("#risk-wef-date").val(dt);
        $("#product-wef-date").val(dt);
        $.ajax({
            type: 'GET',
            url:  'getWetDate',
            dataType: 'json',
            data: {"wefDate":dt},
            async: true,
            success: function(result) {
                $("#wet-date").val(moment(result).format('DD/MM/YYYY'));
                $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));
                $("#product-wet-date").val(moment(result).format('DD/MM/YYYY'));

            },
            error: function(jqXHR, textStatus, errorThrown) {

            }
        });
        populateBinderCardTypes($("#binder-id").val(),dt);
    });
}


function getFamilySize(quotCode) {
    $('#family-size option').remove();
    $('#family-size').append($('<option>', {
        value: "",
        text : "Select Family Size"
    }));
    $.ajax({
        type: 'GET',
        url: 'familySize/'+quotCode,
        dataType: 'json',
        async: true,
        success: function (result) {
            console.log(result);
            for(var res in result){
                $('#family-size').append($('<option>', {
                    value: result[res].famSize,
                    text : result[res].famSizeDisp
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

//function getCardTypes(binCode,wetdate) {
//    console.log("binCode="+binCode +" wefDate="+wetdate);
//    $('#card-type option').remove();
//
//
//    $('#card-type').append($('<option>', {
//        value: "",
//        text : "Select Card Type"
//    }));
//    $.ajax({
//        type: 'GET',
//        url: 'binderCardTypes',
//        dataType: 'json',
//        data: {"binCode":binCode,"wefDate":wetdate},
//        async: true,
//        success: function (result) {
//            console.log(result);
//            for(var res in result){
//                $('#card-type').append($('<option>', {
//                    value: result[res].binCardId,
//                    text : result[res].binCardDesc
//                }));
//            }
//        },
//        error: function (jqXHR, textStatus, errorThrown) {
//            new PNotify({
//                title: 'Error',
//                text: jqXHR.responseText,
//                type: 'error',
//                styling: 'bootstrap3'
//            });
//        }
//    });
//}


function getAgeBracket(quotCode) {
    $('#age-bracket option').remove();
    $('#age-bracket').append($('<option>', {
        value: "",
        text : "Select Age Bracket"
    }));
    $.ajax({
        type: 'GET',
        url: 'ageBracket/'+quotCode,
        dataType: 'json',
        async: true,
        success: function (result) {
            console.log(result);
            for(var res in result){
                $('#age-bracket').append($('<option>', {
                    value: result[res].ageBracket,
                    text : result[res].ageBracketDisp
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


function populateUserBranches(){
    if($("#brn-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "brn-frm",
            sort : 'obName',
            change: function(e, a, v){
                $("#brn-id").val(e.added.obId);
            },
            formatResult : function(a)
            {
                return a.obName;
            },
            formatSelection : function(a)
            {
                return a.obName;
            },
            initSelection: function (element, callback) {
                var code  = $('#brn-id').val();
                var name = $("#brn-name").val();
                var data = {obName:name,obId:code};
                callback(data);
            },
            id: "obId",
            width:"250px",
            placeholder:"Select Branch"

        });
    }
}


function populateBinderCardTypes(binCode,wetdate){
    if($("#card-frm").filter("div").html() != undefined)
    {
        console.log("testing refresh")
        Select2Builder.initAjaxSelect2({
            containerId : "card-frm",
            sort : 'binCardDesc',
            change: function(e, a, v){
                $("#card-id").val(e.added.binCardId);
            },
            formatResult : function(a)
            {
                return a.binCardDesc;
            },
            formatSelection : function(a)
            {
                return a.binCardDesc;
            },
            initSelection: function (element, callback) {

                var code  = $("#card-id").val();
                var name = $("#card-name").val();
                var data = {binCardDesc:name,binCardId:code};
                callback(data);
            },
            id: "binCardId",
            width:"250px",
            params: {bindCode: binCode,wefDate:wetdate},
            placeholder:"Select Card Type"

        });
    }
}

function populateFamDetailsLov(){
    if($("#curr-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "familySize-frm",
            sort : 'type',
            change: function(e, a, v){
                $("#family-size").val(e.added.type);

            },
            formatResult : function(a)
            {
                return a.type;
            },
            formatSelection : function(a)
            {
                return a.type;
            },
            initSelection: function (element, callback) {
                var code  = $('#family-size').val();
                var name = $("#family-size-disp").val();
                var data = {type:name,type:code};
                callback(data);
            },
            id: "type",
            width:"250px",
            placeholder:"Select Family Size"

        });
    }
}



function populateCurrencyLov(){
    if($("#curr-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "curr-frm",
            sort : 'curName',
            change: function(e, a, v){
                $("#cur-id").val(e.added.curCode);

            },
            formatResult : function(a)
            {
                return a.curName;
            },
            formatSelection : function(a)
            {
                return a.curName;
            },
            initSelection: function (element, callback) {
                var code  = $('#cur-id').val();
                var name = $("#cur-name").val();
                var data = {curName:name,curCode:code};
                callback(data);
            },
            id: "curCode",
            width:"250px",
            placeholder:"Select Currency"

        });
    }
}



function populateSourcesLov(srcGroupId){


    if($("#source-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "source-frm",
            sort : 'desc',
            change: function(e, a, v){
                $("#source-id").val(e.added.srcId);
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
                var code  = $('#source-id').val();
                var name = $("#source-name").val();
                var data = {desc:name,srcId:code};
                callback(data);
            },
            id: "srcId",
            width:"250px",
            params: {srcGroupId: srcGroupId},
            placeholder:"Select Source"

        });
    }


}

function populateSourceGroupLov2(){
    if($("#sourcegroup-frm").filter("div").html() != undefined)
    {

        Select2Builder.initAjaxSelect2({
            containerId : "sourcegroup-frm",
            sort : 'desc',
            change: function(e, a, v){
                $("#sourcegroup-id").val(e.added.srcGroupId);
                $('#source-id').val("");
                $("#source-name").val("");
                populateSourcesLov(e.added.srcGroupId);


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
                var code  = $('#sourcegroup-id').val();
                var name = $("#sourcegroup-name").val();
                var data = {desc:name,srcGroupId:code};
                callback(data);
                populateSourcesLov($('#sourcegroup-id').val());
            },
            id: "srcGroupId",
            width:"250px",
            placeholder:"Select Source Group"

        });

    }

}
function populateSourceGroupLov(){
    if($("#sourcegroup-frm").filter("div").html() != undefined)
    {

        Select2Builder.initAjaxSelect2({
            containerId : "sourcegroup-frm",
            sort : 'desc',
            change: function(e, a, v){
                $("#sourcegroup-id").val(e.added.srcGroupId);

                populateSourcesLov(e.added.srcGroupId);


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
                var code  = $('#sourcegroup-id').val();
                var name = $("#sourcegroup-name").val();
                var data = {desc:name,srcGroupId:code};
                callback(data);
                populateSourcesLov($('#sourcegroup-id').val());
            },
            id: "srcGroupId",
            width:"250px",
            placeholder:"Select Source Group"

        });

    }

}



function populateClientLov(){
    if($("#prospects-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "prospects-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#client-id").val(e.added.tenId);
                $("#btn-add-prs").val("Edit");

            },
            formatResult : function(a)
            {
                return a.fname+" "+a.otherNames;
            },
            formatSelection : function(a)
            {
                return a.fname+" "+a.otherNames;
            },
            initSelection: function (element, callback) {
                var code = $("#client-id").val();
                var name = $("#client-f-name").val();
                var othernames = $("#client-other-name").val();
                var data = {fname:name,otherNames:othernames,tenId:code};
                callback(data);
            },
            id: "tenId",
            placeholder:"Select Prospect"

        });

        $("#prospects-frm").on("select2-removed", function(e) {
            $("#client-id").val("");
            $("#btn-add-prs").val("New");
        });
    }
    if($("#client-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "client-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#client-id").val(e.added.tenId);
                //populateInsuredLov();

            },
            formatResult : function(a)
            {
                return a.fname+" "+a.otherNames;
            },
            formatSelection : function(a)
            {
                return a.fname+" "+a.otherNames;
            },
            initSelection: function (element, callback) {
                var code = $("#client-id").val();
                var name = $("#client-f-name").val();
                var othernames = $("#client-other-name").val();
                var data = {fname:name,otherNames:othernames,tenId:code};
                callback(data);
            },
            id: "tenId",
            placeholder:"Select Client"

        });
    }
}

function newCategories(){
    $("#btn-add-category").on("click", function(){
        $(".categoryLov").show();
       // $(".categoryDetails").show();
        if ($("#pol-medicalCoverType").val()==='I'){
            $(".individualDetails").show();

        } else {
            $(".individualDetails").hide();}
        $("#saveCategory").show();
        $('#med-category-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $("#category-quot-id").val(quotId);
        populateBinderDetails($("#binder-id").val());

        //$("#binder-det-pk").val($("#"));
        // populateBedTyoesLov();
       $(".categoryDisplay").hide();
        $('#categoryModal').modal('show');
    });


}


function newFamilyDetails(){
    $("#btn-add-new-fam").on("click", function(){
        $('#med-familyDetails-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $("#fam-category-id").val($("#fam-category-pk").val());
        //$("#binder-det-pk").val($("#"));
        // populateBedTyoesLov();
        populateFamDetailsLov();
        $('#familyDetailsModal').modal('show');
    });


}

function saveFamilyDetails(){

    var $form = $('#med-familyDetails-form');
    var validator = $form.validate();
    $('#familyDetailsModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#med-familyDetails-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveFamilyDetails').click(function(){
        if (!$form.valid()) {
            return;
        }
        // $('#myPleaseWait').modal({
        //     backdrop: 'static',
        //     keyboard: true
        // });
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createQuotFamDetails";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#familyDetailsList').DataTable().ajax.reload();
            validator.resetForm();
            $('#familyDetailsModal').modal('hide');
            $('#med-categories').DataTable().ajax.reload();
            $('#polTaxesList').DataTable().ajax.reload();
            populateQuotDetails();
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


function saveCategories(){

    var $form = $('#med-category-form');
    var validator = $form.validate();
    $("#saveCategory").show();
    $('#categoryModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#med-category-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveCategory').click(function(){
        if (!$form.valid()) {
            return;
        }
        // $('#myPleaseWait').modal({
        //     backdrop: 'static',
        //     keyboard: true
        // });
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createQuotCategories";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            console.log("test 1")
            $('#med-categories').DataTable().ajax.reload();
            $('#polTaxesList').DataTable().ajax.reload();
            $('#polclausesList').DataTable().ajax.reload();
            populateQuotDetails();
            console.log("test 2")
            createMedicalCovers(-2000);
            console.log("test 3")
            getCategoryRules(-2000);
            console.log("test 4")
           // getExclusions(-2000);
           // getCategoryProviders(-2000);
           // getLoadings(-2000);
            //getCategoryBedType(-2000);
            $("#binderdet-frm").select2("val", "");
            validator.resetForm();
            $('#categoryModal').modal('hide');
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

function createQuotClauses(){
    var url = "quotClauses";
    var currTable = $('#polclausesList').DataTable( {
        "processing": true,
        "serverSide": true,
        "deferRender": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "clauHeading" },
            { "data": "clause",
                "render": function ( data, type, full, meta ) {
                    if(full.clause.clause.clauseType){
                        if(full.clause.clause.clauseType ==='E') return "Excess";
                        else if(full.clause.clause.clauseType ==='L') return "Limits";
                        else if(full.clause.clause.clauseType ==='C') return "Clause";
                        else return "";
                    }
                    else{
                        return "";
                    }
                }
            },
            { "data": "editable" },
            { "data": "clauWording" },
            {
                "data": "ccId",
                "render": function ( data, type, full, meta ) {
                    if(full.quotation.quotStatus){
                        if(full.quotation.quotStatus==="D"){
                            if(full.editable)  {
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);"><i class="fa fa-pencil-square-o"></button>';
                            }else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                        }

                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }

                }

            },
            {
                "data": "ccId",
                "render": function ( data, type, full, meta ) {
                    if(full.quotation.quotStatus){
                        if(full.quotation.quotStatus ==="D") {

                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="deletePolicyClause(this);"><i class="fa fa-trash-o"></button>';

                        }
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolicyClause(this);" disabled><i class="fa fa-trash-o"></button>';

                    }

                }

            },
        ]
    } );
    return currTable;
}
function createPolicyTaxes(){
    var url = "quotTaxes";
    var currTable = $('#polTaxesList').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "revenueItems",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.getRevDesc(full.revenueItems.item);
                }
            },
            { "data": "taxRate" },
            { "data": "divFactor" },
            { "data": "rateType" },
            { "data": "taxAmount",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.taxAmount);
                }
            },
            { "data": "taxLevel",
                "render": function ( data, type, full, meta ) {
                    if(full.taxLevel){
                        if(full.taxLevel==="R")
                            return "Risk";
                        else if(full.taxLevel==="P")
                            return "Policy";
                    }
                    else
                        return "Policy";

                }
            },
            {
                "data": "quotTaxId",
                "render": function ( data, type, full, meta ) {

                    if(full.quotation.quotStatus){
                        if(full.quotation.quotStatus==="D"){
                            if (full.revenueItems.item==='CF' || full.revenueItems.item==='SC' ){
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-taxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                            }else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-taxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);"><i class="fa fa-pencil-square-o"></button>';
                    }else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-taxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }

                }

            },
            {
                "data": "quotTaxId",
                "render": function ( data, type, full, meta ) {
                    if(full.quotation.quotStatus){
                        if(full.quotation.quotStatus==="D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-taxes='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);" ><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-taxes='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);" disabled><i class="fa fa-trash-o"></button>';

                    }


                }

            },
        ]
    } );
    return currTable;
}



function getCategoryRules(catId){
    var url = "quotCategoryRules/"+catId;
    var currTable = $('#med-rules-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "deferRender": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "shtDesc" },
            { "data": "value" },
            { "data": "desc" },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    if (full.category.quotation.quotStatus) {
                        if (full.category.quotation.quotStatus === "D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-rules=' + encodeURI(JSON.stringify(full)) + ' onclick="editCategoryRule(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-rules=' + encodeURI(JSON.stringify(full)) + ' onclick="editCategoryRule(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }
                    else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-rules=' + encodeURI(JSON.stringify(full)) + ' onclick="editCategoryRule(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.quotation.quotStatus){
                        if(full.category.quotation.quotStatus ==="D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-rules='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCategoryRule(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-rules='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCategoryRule(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                }

            },


        ]
    } );

    return currTable;
}
function saveCategoryRule(){
    var $form = $('#cat-rule-form');
    var validator = $form.validate();
    $('#catRulesModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#cat-rule-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveCategoryRule').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createCategoryRule";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#med-rules-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#catRulesModal').modal('hide');
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

function getCategoryFamDetails(catId){
    var url = "quotCategoryFamDetails/"+catId;
    var currTable = $('#familyDetailsList').DataTable( {
        "processing": true,
        "serverSide": true,
        "deferRender": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "familySize" },
            { "data": "families" },
            { "data": "agebracket" },
            { "data": "requireCards" },
            { "data": "premium"  ,
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.premium);
                }	},
            {
                "data": "familyId",
                "render": function ( data, type, full, meta ) {
                    if (full.category.quotation.quotStatus) {
                        if (full.category.quotation.quotStatus === "D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-quotcategoriesfamdtls=' + encodeURI(JSON.stringify(full)) + ' onclick="editCategoryFamDetails(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-quotcategoriesfamdtls=' + encodeURI(JSON.stringify(full)) + ' onclick="editCategoryFamDetails(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }
                    else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-quotcategoriesfamdtls=' + encodeURI(JSON.stringify(full)) + ' onclick="editCategoryFamDetails(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "familyId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.quotation.quotStatus){
                        if(full.category.quotation.quotStatus ==="D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotcategoriesfamdtls='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCategoryFamDetails(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotcategoriesfamdtls='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCategoryFamDetails(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                }

            },


        ]
    } );

    return currTable;
}


function deleteCategoryFamDetails(button){
    var families = JSON.parse(decodeURI($(button).data("quotcategoriesfamdtls")));
    bootbox.confirm("Are you sure want to delete "+families["familySize"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteCategoryFamDetails/' + families["familyId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    populateQuotDetails();
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
function createMedicalCovers(catId){
    var url = "categoryQuotBenefits/"+catId;

    var currTable = $('#medCoversList').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "cover",
                "render": function ( data, type, full, meta ) {
                    return full.cover.section.desc;
                }
            },
            { "data": "cover",
                "render": function ( data, type, full, meta ) {
                    return full.cover.mainCover;
                }
            },
            { "data": "cover",
                "render": function ( data, type, full, meta ) {
                    return full.cover.mainSection.desc;
                }
            },
            { "data": "cover",
                "render": function ( data, type, full, meta ) {
                    return full.cover.proratedFull;
                }
            },
            { "data": "cover",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.cover.minPremium);
                }
            },
            { "data": "cover",
                "render": function ( data, type, full, meta ) {
                    if(full.limit){
                        return UTILITIES.currencyFormat(full.limit.limitAmount);

                    }
                    else{
                        if(full.fundLimit){

                            return UTILITIES.currencyFormat(full.fundLimit);

                        }else {
                            return 'No Limit';
                        }
                    }

                }
            },
            { "data": "cover",
                "render": function ( data, type, full, meta ) {
                    if(full.applicableAt){
                        if(full.applicableAt ==="F") return "Per Family";
                        else if(full.applicableAt ==="M") return "Per Member";
                    }else {
                        if(full.cover.applicableAt){
                            if(full.cover.applicableAt ==="F") return "Per Family";
                            else if(full.cover.applicableAt ==="M") return "Per Member";
                        }
                        else return "";
                    }
                }
            },
            { "data": "cover",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.premium);
                }
            },
            { "data": "cover",
                "render": function ( data, type, full, meta ) {
                    return full.waitPeriod;
                }
            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.quotation.quotStatus){

                        if(full.category.quotation.quotStatus ==="D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-medcovers=' + encodeURI(JSON.stringify(full)) + ' onclick="editMedCovers(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-medcovers=' + encodeURI(JSON.stringify(full)) + ' onclick="editMedCovers(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }
                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.quotation.quotStatus){
                        if(full.category.quotation.quotStatus ==="D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="deleteMedCovers(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-benefits='+encodeURI(JSON.stringify(full)) + ' onclick="deleteMedCovers(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                }

            }
        ]
    } );
    return currTable;
}
function deleteMedCovers(button){
    var benefits = JSON.parse(decodeURI($(button).data("benefits")));
    var message = "Are you sure want to delete "+benefits['cover'].section.desc+"?";
    bootbox.confirm(message, function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deleteCategoryBenefit/' + benefits['sectId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#medCoversList').DataTable().ajax.reload();
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
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
function editMedCovers(button){
    var covers = JSON.parse(decodeURI($(button).data("medcovers")));
    $("#limit-benefit-id").val(covers['sectId']);
    if ($("#fund-Binder").val()==="Y"){
        $(".cover-limit-det").hide();
        // $("#rule-value").val(rules['value']);
        if(covers['fundLimit']){
            $("#fund-limit-value").val(UTILITIES.currencyFormat(covers['fundLimit']));
        }
    }else {
        $(".fund-limit-value").hide();
        if (covers['limit']){
        $("#cover-limit-det-pk").val(covers['limit'].id);
        $("#cover-limit-value").val(covers['limit'].limitAmount);
        }
        populateBenefitsLimitsLov(covers['cover'].id);

    }

    if(covers['applicableAt']){
        $("#benfit-applicable-at").val(covers['applicableAt']);
    }else {
        if(covers['cover']){
            $("#benfit-applicable-at").val(covers['cover'].applicableAt);
        }}
    if(covers['waitPeriod']){
        $("#waiting-period").val(covers['waitPeriod']);
    }
    if(covers['category'].binderDetails.binder.binType==="B"){
        if ($("#fund-Binder").val()==="Y") {
            $(".applicablelevel").show();
            $(".wait-period").show();
        } else {
            $(".applicablelevel").hide();
            $(".wait-period").hide();
        }
    }

    $('#benefitLimitModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}

function populateBenefitsLimitsLov(covId) {
    if ($("#cover-limit-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cover-limit-frm",
            sort: 'limitAmount',
            change: function (e, a, v) {
                $("#cover-limit-det-pk").val(e.added.id);
            },
            formatResult: function (a) {
                return a.limitAmount;
            },
            formatSelection: function (a) {
                return a.limitAmount;
            },
            initSelection: function (element, callback) {
                var code  = $('#cover-limit-det-pk').val();
                var value = $("#cover-limit-value").val();
                var data = {id:code,limitAmount:value};
                callback(data);
            },
            id: "id",
            width: "250px",
            placeholder: "Select Limit",
            params: {covId: covId}

        });

    }
}


function populateBinderLov(){
    if($("#binder-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "binder-frm",
            sort : 'binName',
            change: function(e, a, v){

                $("#pol-ins-comp").text(e.added.account.name);
                $("#pol-prod-name").text(e.added.product.proDesc);
                $("#product-id").val(e.added.product.proCode);
                $("#pol-agent-id").val(e.added.account.acctId);
                $("#bind-name").val(e.added.binName);
                $("#binder-id").val(e.added.binId);
                $("#pol-medicalCoverType").val(e.added.medicalCoverType);
                if (e.added.medicalCoverType==='G')
                    $("#medicalCoverTyp-info").text('Group');
                if (e.added.medicalCoverType==='I')
                    $("#medicalCoverTyp-info").text('Individual');
                $("#comm-rate").val(e.added.account.accountType.commRate);
                //populateSubclassLov(e.added.binId);
            },
            formatResult : function(a)
            {
                return a.binName;
            },
            formatSelection : function(a)
            {
                return a.binName;
            },
            initSelection: function (element, callback) {
                var code  = $('#binder-id').val();
                var name = $("#bind-name").val();
                var data = {binName:name,binId:code};
                callback(data);
            },
            id: "binId",
            width:"250px",
            params: {bindType: $("#pol-bin-type").val()},
            placeholder:"Select Binder"

        });
    }


}


