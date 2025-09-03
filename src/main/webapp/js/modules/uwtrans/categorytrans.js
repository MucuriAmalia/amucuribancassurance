/**
 * Created by peter on 4/26/2017.
 */
var UTILITIES = UTILITIES || {};
$(function() {
    $(document).ready(function () {
        populatePolicyDetails();
        createPolicyChecks();
        getPolicyCategories();
        newCategories();
        addFamilySize();
        addFamilyMember();
        saveCategoryRule();
        saveCategories();
        saveCategoryBedType();
        createCategoryLoading();
        changePolicyStatus();
        updateBenefitLimits();
        createSelfFundDetails();
        createPolicyTaxes();
        newSelfFund();
        saveSelfFund();
        addNewCategoryRules();
        saveCategoryRules();
        uploadMembersFile();
        addNewCategoryBenefits();
        saveCategoryBenefits();
        addNewPolicyProviders();
        saveCategoryProviders();
        getNewClausesModal();
        createPolClauses();
        editPolicyDetails();
        savePolicy();
        replaceCards();
        uploadRiskDocument();
        savePolTaxes();
        addNewPolTaxes();
        saveNewPolTaxes();
        saveRiskDocsList();
        allowEditMedicalCovers("");
        readOnlyCategories("");
        readOnlyClauses("");
        readOnlySelfund("");
        readOnlyTaxes("");
        readOnlyMembers("");
        $("#memberfile").hide();

        UTILITIES.createAssignee();
        UTILITIES.emailReports();

        $(".formatcurrency").number( true, 2 );
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        $("#btn-add-docs").click(function(){
            console.log('testing');
            searchReqDocs("");
        });

        $("#closeRiskReqDocsModalBtn").click(function(){
            closeRiskReqDocsModal();
        });
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

            if($(this).val()==="IN"){
                $("#email-cc").attr("readonly",true);
                $("#email-send-to").attr("readonly",false);
                $("#email-send-to").val("");
                $.ajax({
                    type: 'GET',
                    url:  'getInhouseEmail',
                    dataType: 'json',
                    async: true,
                    success: function(result) {
                        $("#email-cc").val(result);
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
            else{
                $("#email-cc").attr("readonly",false);
                $("#email-send-to").attr("readonly",true);
                $("#email-cc").val("");
            }
        });



        $("#dependent-type").on('change', function(){
            if($(this).val()==="C"){
                $(".child-type").show();
                $(".member-principal").show();
            }
            else if ($(this).val()==="P"){
                $(".member-principal").hide();
                $(".child-type").hide();
            }
            else{
                $(".child-type").hide();
                $(".member-principal").show();
            }
        });


        $("#memberhas-card").on('change', function(){
            if($(this).val()==="Y"){
                $("#generate-card").val("");
                $("#card-no").val("");
                $(".cardNoDetails").show();
                $(".generateCardNoDetails").hide();
            }else if ($(this).val()==="N"){
                $("#generate-card").val("");
                $("#card-no").val("");
                $(".cardNoDetails").hide();
                if ($("#fund-Binder").val()==="Y")
                $(".generateCardNoDetails").show();
                else $(".generateCardNoDetails").hide();


            }else {
                $("#generate-card").val("");
                $("#card-no").val("");
                $(".cardNoDetails").hide();
                $(".generateCardNoDetails").hide();
            }
        });

        //$("#pol-cardissue").on('change',function(){
        //    if($(this).val()==="Y"){
        //        $(".policy-card-type").show();
        //    } else{
        //        $("pol-cardtype").val("");
        //        $(".policy-card-type").hide();
        //
        //    }
        //});

    });
});

function searchReqDocs(search){
    if($("#risk-code-pk").val() != ''){

        $.ajax({
            type: 'GET',
            url:  'getmemberreqdocs',
            dataType: 'json',
            data: {"riskId": $("#risk-code-pk").val(),"docName":search},
            async: true,
            success: function(result) {
                $("#risksReqDocsTbl tbody").each(function(){
                    $(this).remove();
                });
                for(var res in result){
                    var markup = "<tr><td><input type='checkbox' name='record' id='"+result[res].sclReqrdId+"'></td><td>" + result[res].requiredDoc.reqShtDesc + "</td><td>" + result[res].requiredDoc.reqDesc + "</td></tr>";
                    $("#risksReqDocsTbl").append(markup);
                }
                $("#req-risk-code").val($("#risk-code-pk").val());
              //  $('#viewDocModal').hide();
                $('#riskReqDocsModal').modal({
                    backdrop: 'false',
                    keyboard: true
                })
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
    else{
        bootbox.alert("Select member to attach Documents")
    }
}


function saveRiskDocsList(){
    var arr = [];
    $("#saveRiskDocsBtn").click(function(){
        $("#risksReqDocsTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Documents Selected to attach..");
            return;
        }

        var $currForm = $('#risk-docs-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createMemberDocs";
        data.requiredDocs = arr;


        $.ajax({
            url : url,
            type : "POST",
            data : JSON.stringify(data),
            success : function(s) {
                Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });

                $('#risk_docs_tbl').DataTable().ajax.reload();
                $('#riskReqDocsModal').modal('hide');
                arr = [];

                $('#viewDocModal').modal({
                    backdrop: 'static',
                    keyboard: true
                })
            },
            error : function(jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
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

function populateBinderCardTypes(binCode,wetdate){
    if($("#card-frm").filter("div").html() != undefined)    {
console.log("wetdate="+wetdate)
        Select2Builder.initAjaxSelect2({
            containerId : "card-frm",
            sort : 'binCardDesc',
            change: function(e, a, v){
                $("#pol-cardid").val(e.added.binCardId);
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

                var code  = $("#pol-cardid").val();
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
function changePolicyStatus(){
    $("#btn-make-ready-policy").click(function(){
        makeReady();
    });

    $("#btn-undo-make-ready").click(function(){
        undoMakeReady();
    });

    $("#btn-auth-policy").click(function(){
        authorizePolicy();
    });

    $("#btn-dispatch-trans").click(function(){
        dispatchDocuments();
    });
}



function dispatchDocuments(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'dispatchDocs',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Document Dispatched Successfully',
                icon: 'success'
            });
            populatePolicyDetails();
            $("#btn-dispatch-trans").hide();
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

function authorizePolicy(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'authorizePolicy',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Policy Authorized Successfully',
                icon: 'success'
            });
            populatePolicyDetails();
            createMedicalCovers(-2000);
            getCategoryMembers(-2000);
            getCategoryRules(-2000);
            getExclusions(-2000);
            getCategoryProviders(-2000);
            getLoadings(-2000);
            getCategoryBedType(-2000);
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


function makeReady(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'makePolicyReady',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                        title: 'Success',
                        text: 'Submited Successfully',
                        icon: 'success'
                    });
            populatePolicyDetails();
            createMedicalCovers(-2000);
            getCategoryMembers(-2000);
            getCategoryRules(-2000);
            getExclusions(-2000);
            getCategoryProviders(-2000);
            getLoadings(-2000);
            getCategoryBedType(-2000);
            createPolicyTaxes();
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


function undoMakeReady(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'undoMakeReady',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                        title: 'Success',
                        text: 'Submit Process Successfully',
                        icon: 'success'
                    });
            populatePolicyDetails();
            createMedicalCovers(-2000);
            getCategoryMembers(-2000);
            getCategoryRules(-2000);
            getExclusions(-2000);
            getCategoryProviders(-2000);
            getLoadings(-2000);
            getCategoryBedType(-2000);
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

function saveCategories(){

    var $form = $('#med-category-form');
    var validator = $form.validate();
    $("#saveCategory").show();
    $("#saveCategoryBedType").hide();
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
     //   keyboard: true
    // });
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createPolCategories";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#med-categories').DataTable().ajax.reload();
            createMedicalCovers(-2000);
            getCategoryMembers(-2000);
            getCategoryRules(-2000);
            getExclusions(-2000);
            getCategoryProviders(-2000);
            getLoadings(-2000);
            getCategoryBedType(-2000);
            createPolicyTaxes();
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

function replaceCards(){
    $("#btn-replace-cards").on('click', function(){
        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
        $.ajax({
            type: 'GET',
            url:  'cancelCard/' + $("#replaceCardId").val(),
            dataType: 'json',
            async: true,
            success: function(result) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Card Replaced Successfully',
                    icon: 'success'
                });
                $('#searchMembersTbl').DataTable().ajax.reload();
                populatePolicyDetails();
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
    })
}


function createCards() {
    var url = "medicalCards";
    var currTable = $('#searchMembersTbl').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
        },
        autoWidth: true,
        lengthMenu: [[10], [10]],
        pageLength: 10,
        destroy: true,
        searching: false,
        "columns": [
            {"data": "cardId"},
            {"data": "cardNo"},
            {
                "data": "member",
                "render": function (data, type, full, meta) {
                    return full.member.client.tenantNumber;
                }
            },
            {
                "data": "member",
                "render": function (data, type, full, meta) {
                    return full.member.client.fname + " " + full.member.client.otherNames;
                }
            },
            {
                "data": "wefDate",
                "render": function (data, type, full, meta) {
                    return moment(full.wefDate).format('DD/MM/YYYY');
                }
            },
            {
                "data": "wetDate",
                "render": function (data, type, full, meta) {
                    return moment(full.wetDate).format('DD/MM/YYYY');
                }
            },
            {
                "data": "member",
                "render": function (data, type, full, meta) {
                    return moment(full.member.client.dob).format('DD/MM/YYYY');
                }
            },
            {
                "data": "status",
                "render": function (data, type, full, meta) {
                    return full.status;
                }
            },

        ]
    });

    $('#searchMembersTbl tbody').on( 'click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = currTable.row(this).data();
        if (d) {
            $("#replaceCardId").val(d.cardId);
        }
    });


}


function saveCategoryBedType(){

    var $form = $('#med-category-form');
    var validator = $form.validate();
    $("#saveCategory").hide();
    $("#saveCategoryBedType").show();
    $('#categoryModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#med-category-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveCategoryBedType').click(function(){
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
        var url = "createCategoriesBedType";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#med-bedtypes-tbl').DataTable().ajax.reload();
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

function newCategories(){
    $("#btn-add-category").on("click", function(){
        $(".categoryLov").show();
        $(".categoryDetails").show();
        $(".bedtypedetails").hide();
        $("#saveCategory").show();
        $("#saveCategoryBedType").hide();
        $('#med-category-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $("#category-pol-id").val(polCode);
        //$("#binder-det-pk").val($("#"));
        populateBinderDetails($("#bin-code").val());
        populateBedTyoesLov();
        $(".categoryDisplay").hide();
        $(".bedtypedetails").hide();
        $('#categoryModal').modal('show');
    });


}

function populatePolicyDetails() {
    if(typeof polCode!== 'undefined') {
        if (polCode !== -2000) {
            $.ajax({
                url: 'getPolicyDetails',
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s) {
                    $("#show-self-fund").hide();
                    $("#tab_content-selffund").hide();
                    if(s.binder){
                        if(s.binder.fundBinder){
                            if(s.binder.fundBinder ==="Y"){
                                $("#show-self-fund").show();
                                $("#tab_content-selffund").show();
                            }
                        }
                        $("#fund-Binder").val(s.binder.fundBinder);
                    }
                    else {
                        $("#fund-Binder").text("N")}

                    $("#client-info").text(s.client.fname+" "+s.client.otherNames);
                    $("#binder-info").text(s.binder.binName);
                    $("#med-pol-bin-code").val(s.binder.binId);
                    $("#bin-code").val(s.binder.binId);
                    $("#pol-ins-comp").val(s.binder.account.name);
                    $("#pol-no").text(s.polNo);
                    $("#pol-rev-no").text(s.polRevNo);
                    $("#from-date").text(moment(s.wefDate).format('DD/MM/YYYY'));
                    $("#wet-date").text(moment(s.wetDate).format('DD/MM/YYYY'));
                    $("#member-date-from").val(moment(s.wefDate).format('DD/MM/YYYY'));
                    $("#member-date-to").val(moment(s.wetDate).format('DD/MM/YYYY'));
                    $("#pol-sum-insured").text(UTILITIES.currencyFormat(s.sumInsured));
                    $("#pol-premium").text(UTILITIES.currencyFormat(s.premium));
                    $("#pol-basic-prem").text(UTILITIES.currencyFormat(s.basicPrem));
                    $("#pol-net-prem").text(UTILITIES.currencyFormat(s.netPrem));
                    $("#pol-taxes-amt").text(s.taxes);
                    $('#pol-comm-amt').text(UTILITIES.currencyFormat(s.commAmt));
                    $('#client-policyno').text(s.clientPolNo);
                    $('#pol-tl').text(UTILITIES.currencyFormat(s.trainingLevy));
                    $('#pol-phcf').text(UTILITIES.currencyFormat(s.phcf));
                    $('#pol-sd').text(UTILITIES.currencyFormat(s.stampDuty));
                    $('#pol-whtx').text(UTILITIES.currencyFormat(s.whtx));
                    $('#pol-extras').text(UTILITIES.currencyFormat(s.extras));
                    $("#pol-fap").text(UTILITIES.currencyFormat(s.futurePrem));
                    $("#pol-issue-card-fee").text(UTILITIES.currencyFormat(s.issueCardFee));
                    $("#pol-service-charge").text(UTILITIES.currencyFormat(s.serviceCharge));
                    $("#pol-reissue-card-fee").text(UTILITIES.currencyFormat(s.reissueCardFee));
                    $("#pol-vat-amount").text(UTILITIES.currencyFormat(s.vatAmount));
                    $("#pol-buss-type").val(s.businessType);
                    $("#pol-bin-type").val(s.binder.binType);
                    if(s.binCardType) {
                        $("#policy-card-id").val(s.binCardType.id);
                        $("#pol-card-type").text(s.binCardType.cardTypes.cardDesc);
                    }


                    $("#pol-freqpay").val(s.frequency);
                    $("#client-id").val(s.client.tenId);
                    $("#client-f-name").val(s.client.fname);
                    $("#client-other-name").val(s.client.otherNames);
                    $("#product-id").val(s.binder.product.proCode);
                    $("#pol-prod-name").val(s.binder.product.proDesc);

                    $("#pol-interface-type").val(s.interfaceType);
                    $("#pol-medicalCover-Type").val(s.medicalCoverType);
                    if ($("#pol-medicalCover-Type").val() === 'I')
                    $("#medicalCoverTyp-info").text('Individual');
                    if ($("#pol-medicalCover-Type").val() === 'G')
                        $("#medicalCoverTyp-info").text('Group');

                    $("#pm-id").val(s.paymentMode.pmId);
                    $("#pm-name").val(s.paymentMode.pmDesc);
                    $("#cur-id").val(s.transCurrency.curCode);
                    $("#cur-name").val(s.transCurrency.curName);

                    $("#brn-id").val(s.branch.obId);
                    $("#brn-name").val(s.branch.obName);

                    if (s.agent)
                    $("#pol-agent-id").val(s.agent.acctId);

                    $("#pol-create-date").val(moment(s.polCreateddt).format('DD/MM/YYYY'));



                    if(s.frequency==="M"){
                        $("#pol-freq-pay").text("Monthly");
                    }
                    if(s.frequency==="Q"){
                        $("#pol-freq-pay").text("Quarterly");
                    }
                    if(s.frequency==="S"){
                        $("#pol-freq-pay").text("Semi-Annually");
                    }
                    if(s.frequency==="A"){
                        $("#pol-freq-pay").text("Annually");
                    }
                    $("#pol-admin-fee").text(UTILITIES.currencyFormat(s.adminFee));
                    $('#pol-tran-type-disp').text(s.transType);
                    if(s.transType){
                        if(s.transType ==='CR'){
                            $("#uw_tabs").hide();
                            $("#med-cards-tabs").show();
                        }
                        else{
                            $("#uw_tabs").show();
                            $("#med-cards-tabs").hide();
                        }
                    }
                    if(s.renewalDate)
                        $("#pol-ren-date").text(moment(s.renewalDate).format('DD/MM/YYYY'));
                    if(s.authStatus==="D"){
                        $("#pol-status").text("Draft");
                        $("#btn-auth-policy").css("display","none");
                        $("#btn-make-ready-policy").css("display","block");
                        $("#btn-undo-make-ready").css("display","none");
                        $("#btn-add-category").show();
                        $("#btn-add-member").show();
                        $("#btn-add-new-tax").show();
                        $("#btn-add-new-clause").show();
                        $("#btn-upload-members").show();
                        $("#btn-add-self-fund").show();
                        $("#btn-add-new-rule").show();
                        $("#btn-add-new-benefit").show();
                        $("#btn-add-new-panel").show();
                        $("#btn-edit-policy").show();
                        $("#btn-dispatch-trans").hide();
                        $("#btn-replace-cards").show();
                        $("#btn-add-docs").show();
                        if (readOnlyCategories(s.transType)){
                            $("#btn-add-category").hide();
                            $("#btn-add-new-panel").hide();
                            $("#btn-add-new-rule").hide();
                        }
                        if (!(allowEditMedicalCovers(s.transType))){
                            $("#btn-add-new-benefit").hide();
                        }
                        if (readOnlyMembers(s.transType)){
                            $("#btn-add-member").hide();
                        }
                        if(readOnlyTaxes(s.transType)){
                            $("#btn-add-new-tax").hide();
                        }
                        if (readOnlyClauses(s.transType)){
                            $("#btn-add-new-clause").hide();
                        }
                        if (readOnlySelfund(s.transType)){
                            $("#btn-add-self-fund").hide();
                        }
                        if ((s.transType==='NB') || (s.transType==='RN') ) {
                            $("#btn-edit-policy").show();
                        }else $("#btn-edit-policy").hide();
                        $("#card-frm").select2("enable", true);
                    }
                    else if(s.authStatus==="R"){
                        $("#pol-status").text("Ready");
                        $("#btn-auth-policy").css("display","block");
                        $("#btn-make-ready-policy").css("display","none");
                        $("#btn-undo-make-ready").css("display","block");
                        $("#btn-add-category").hide();
                        $("#btn-add-member").hide();
                        $("#btn-add-new-tax").hide();
                        $("#btn-add-new-clause").hide();
                        $("#btn-upload-members").hide();
                        $("#btn-add-self-fund").hide();
                        $("#btn-add-new-rule").hide();
                        $("#btn-add-new-benefit").hide();
                        $("#btn-add-new-panel").hide();
                        $("#btn-edit-policy").hide();
                        $("#btn-dispatch-trans").hide();
                        $("#btn-replace-cards").hide();
                        $("#btn-add-docs").hide();
                        $("#card-frm").select2("enable", false);
                    }
                    else if(s.authStatus==="A"){
                        $("#pol-status").text("Authorised");
                        $("#btn-auth-policy").css("display","none");
                        $("#btn-make-ready-policy").css("display","none");
                        $("#btn-undo-make-ready").css("display","none");
                        $("#btn-add-category").hide();
                        $("#btn-add-member").hide();
                        $("#btn-add-new-tax").hide();
                        $("#btn-add-new-clause").hide();
                        $("#btn-upload-members").hide();
                        $("#btn-add-self-fund").hide();
                        $("#btn-add-new-rule").hide();
                        $("#btn-add-new-benefit").hide();
                        $("#btn-add-new-panel").hide();
                        $("#btn-edit-policy").hide();
                        $("#btn-dispatch-trans").show();
                        $("#btn-replace-cards").hide();
                        $("#btn-add-docs").hide();
                        $("#card-frm").select2("enable", false);
                    }
                    if(s.transType==="CO" || s.transType==="CN"){
                        $("#btn-undo-make-ready").css("display","none");
                        $("#btn-make-ready-policy").css("display","none");

                    }
                    populateBinderDetails(s.binder.binId);
                    getPolicyCategories();
                    getSchemeMembers();
                    createPolicyClauses();
                    createSelfFundDetails();
                    getSelfFundReceipts();
                    createCards();
                    UTILITIES.getProcessActiveDiagram(polCode);
                    UTILITIES.getTaskActive(polCode);
                    UTILITIES.getProcessHistory(polCode);
                   // $('#med-members').DataTable().ajax.reload();
                   // $('#polclausesList').DataTable().ajax.reload();

                }
            });
            $("#btn-uw-reports").show();
        }
        else{
            $("#btn-auth-policy").css("display","none");
            $("#btn-uw-reports").hide();
            $("#btn-dispatch-trans").hide();
        }
    }
}


function createPolicyTaxes(){
    var url = "policyTaxes";
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
                "data": "polTaxId",
                "render": function ( data, type, full, meta ) {

                    if(full.policy.authStatus){
                        if(full.policy.authStatus==="D" && !(readOnlyTaxes(full.policy.transType)))
                            if (full.revenueItems.item==='CF' || full.revenueItems.item==='SC' ){
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                            }else {
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);"><i class="fa fa-pencil-square-o"></button>';
                            }
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                        }

                }

            },
            {
                "data": "polTaxId",
                "render": function ( data, type, full, meta ) {
                    if(full.policy.authStatus){
                        if(full.policy.authStatus==="D" && !(readOnlyTaxes(full.policy.transType)))
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);" ><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);" disabled><i class="fa fa-trash-o"></button>';

                    }


                }

            },
        ]
    } );
    return currTable;
}

function getPolicyCategories(){
    var url = "getPolicyCategories";
    var table = $('#med-categories').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "binderDetails",
                "render": function ( data, type, full, meta ) {

                    return full.binderDetails.subCoverTypes.subclass.subDesc+"-"+full.binderDetails.subCoverTypes.coverTypes.covName;
                }
            },
            { "data": "shtDesc" },
            { "data": "desc" },
            { "data": "premium",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.premium);
                }
            },
            { "data": "categoryStatus",
                "render": function ( data, type, full, meta ) {
                    if(full.categoryStatus ==="E") return "Active";
                    else if(full.categoryStatus ==="N") return "New";
                    else if(full.categoryStatus ==="D") return "Removed";
                    else return full.categoryStatus;
                }
            },
            {
                "data": "id",
                "render": function ( data, type, full, meta ) {
                    if(full.policy.authStatus){
                        if(full.policy.authStatus==="D" && !(readOnlyCategories(full.policy.transType)))
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-polcategories='+encodeURI(JSON.stringify(full)) + ' onclick="editPolCategories(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-polcategories='+encodeURI(JSON.stringify(full)) + ' onclick="editPolCategories(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }


                }

            },
            {
                "data": "id",
                "render": function ( data, type, full, meta ) {
                    if(full.policy.authStatus){
                        if(full.policy.authStatus==="D" && !(readOnlyCategories(full.policy.transType)))
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-polcategories='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolCategories(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-polcategories='+encodeURI(JSON.stringify(full)) + ' onclick="deletePolCategories(this);" disabled><i class="fa fa-trash-o"></button>';

                    }


                }

            },
        ]
    } );
    $('#med-categories tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = table.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null){

        }
        else{
            if ( aData[0].policy.authStatus==="D" &&( aData[0].categoryStatus==="N" || allowEditMedicalCovers(aData[0].policy.transType)) ){
                $("#btn-add-new-benefit").show();
            }else $("#btn-add-new-benefit").hide();
            $("#fam-category-pk").val(aData[0].id);
            $("#fam-category-bin-pk").val(aData[0].binderDetails.detId);
            createMedicalCovers(aData[0].id);
            getCategoryMembers(aData[0].id);
            getCategoryRules(aData[0].id);
            getExclusions(aData[0].id);
            getCategoryProviders(aData[0].id);
            getLoadings(aData[0].id);
            getCategoryBedType(aData[0].id);

        }
    } );
    return table;
}
function editPolTaxes(button){
    var tax = JSON.parse(decodeURI($(button).data("poltaxes")));

    // $(".categoryDisplay").show();
    $("#tax-policy-id").val(polCode);
    $("#tax-id").val(tax['polTaxId']);
    $("#Trans-Code").text(UTILITIES.getRevDesc(tax["revenueItems"].item));
    $("#tax-rate").val(tax['taxRate']);
    $("#tax-rate-divfact").val(tax['divFactor']);
    $('#editTaxModal').modal('show');
}



function savePolTaxes(){

    var $form = $('#med-tax-form');
    var validator = $form.validate();
    $('#editTaxModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#med-tax-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#savePolTax').click(function(){
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
        var url = "savePolTaxes";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#polTaxesList').DataTable().ajax.reload();
            $('#med-members').DataTable().ajax.reload();
            $('#pol-med-members').DataTable().ajax.reload();
            $('#medCoversList').DataTable().ajax.reload();
            $('#polclausesList').DataTable().ajax.reload();
            $('#exclusionsList').DataTable().ajax.reload();
            $('#loadingsList').DataTable().ajax.reload();
            $('#providersList').DataTable().ajax.reload();
            $('#med-bedtypes-tbl').DataTable().ajax.reload();
            populatePolicyDetails();
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
function deletePolTaxes(button){
    var taxes = JSON.parse(decodeURI($(button).data("poltaxes")));
    bootbox.confirm("Are you sure want to delete "+UTILITIES.getRevDesc(taxes['revenueItems'].item)+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deletePolTaxes/' + taxes["polTaxId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#polTaxesList').DataTable().ajax.reload();
                    populatePolicyDetails();
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
function deletePolCategories(button){
    var categories = JSON.parse(decodeURI($(button).data("polcategories")));
    bootbox.confirm("Are you sure want to delete "+categories["desc"]+"?  This will delete all items under this category.", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deletePolCategory/' + categories["id"],
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
                    $('#med-bedtypes-tbl').DataTable().ajax.reload();
                    populatePolicyDetails();
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
function addFamilySize(){
    $("#btn-add-fam-size").on("click", function () {
        if($("#fam-category-pk").val() !== '') {
            $('#med-deptypes-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
            $("#dep-category-id").val($("#fam-category-pk").val());
            populateDependentTypes();
            $('#depTypesModal').modal('show');

        }else{
            bootbox.alert("Select Category to Add Family Size");
            return;
        }
    });

    var $form = $('#med-deptypes-form');
    var validator = $form.validate();
    $('#depTypesModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#med-deptypes-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveFamilySize').click(function(){
        if (!$form.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createFamilySize";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#med-family-size').DataTable().ajax.reload();
            validator.resetForm();
            $('#depTypesModal').modal('hide');
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

function populateDependentTypes(){
    if($("#deptypes-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "deptypes-frm",
            sort : 'depDesc',
            change: function(e, a, v){
                $("#deptype-pk").val(e.added.depId);
            },
            formatResult : function(a)
            {
                return a.depDesc;
            },
            formatSelection : function(a)
            {
                return a.depDesc;
            },
            initSelection: function (element, callback) {
                //var code  = $('#cur-id').val();
                //var name = $("#cur-name").val();
                //var data = {curName:name,curCode:code};
                //callback(data);
            },
            id: "depId",
            width:"250px",
            placeholder:"Select Dependent Type"

        });

    }

    if($("#member-deptypes-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "member-deptypes-frm",
            sort : 'depDesc',
            change: function(e, a, v){
                $("#client-dept-pk").val(e.added.depId);
            },
            formatResult : function(a)
            {
                return a.depDesc;
            },
            formatSelection : function(a)
            {
                return a.depDesc;
            },
            initSelection: function (element, callback) {
                //var code  = $('#cur-id').val();
                //var name = $("#cur-name").val();
                //var data = {curName:name,curCode:code};
                //callback(data);
            },
            id: "depId",
            width:"250px",
            placeholder:"Select Dependent Type"

        });

    }
}


function populateBinderDetails(bindCode){
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

function allowEditMedicalCovers(transType){
    console.log("transType="+transType)
    if(transType==='AD'  )
            return true;
    else return false;
}

function readOnlyCategories(transType){
    console.log("transType="+transType)
    if( transType==='AD' )
        return true;
    else
        return false;
}

function readOnlyClauses(transType){
    console.log("transType="+transType)
    if(transType==='AD'  )
        return true;
    else return false;
}

function readOnlySelfund(transType){
    console.log("transType="+transType)
    if(transType==='AD')
        return true;
    else return false;
}


function readOnlyTaxes(transType){
    console.log("transType="+transType)
    if(transType==='AD'  )
        return true;
    else return false;
}

function readOnlyMembers(transType){
    console.log("transType="+transType)
    if(transType==='AB' )
        return true;
    else return false;
}


function createMedicalCovers(catId){
    var url = "categoryBenefits/"+catId;

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
            { "data": "status",
                "render": function ( data, type, full, meta ) {
                    if(full.status ==="E") return "Active";
                    else if(full.status ==="N") return "New";
                    else if(full.status ==="D") return "Removed";
                    else if(full.status ==="C") return "Changed";
                    else return full.status;
                }
            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.policy.authStatus){

                        if(full.category.policy.authStatus ==="D" && (allowEditMedicalCovers(full.category.policy.transType) || full.category.categoryStatus==='N'))
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-medcovers=' + encodeURI(JSON.stringify(full)) + ' onclick="editMedCovers(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                               return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-medcovers=' + encodeURI(JSON.stringify(full)) + ' onclick="editMedCovers(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }
                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.policy.authStatus){
                        if(full.category.policy.authStatus ==="D" && (allowEditMedicalCovers(full.category.policy.transType) || full.category.categoryStatus==='N' ))
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

function editPolicyDetails(){
    $("#btn-edit-policy").click(function(){

        $("#edit-pol-pk").val(polCode);
        $("#pol-busstype").val($("#pol-buss-type").val());
        $("#pol-bintype").val($("#pol-bin-type").val());
        $("#pol-bin-code").val($("#med-pol-bin-code").val());
        $("#clientid").val($("#client-id").val());
        $("#pol-createdate").val( $("#pol-create-date").val())
        $("#client-fname").val($("#client-f-name").val());
        $("#client-othername").val($("#client-other-name").val());
        populateClientLov();
        $("#clientinfo").val($("#client-info").text());
        $("#binderid").val($("#med-pol-bin-code").val());
        $("#productid").val($("#product-id").val());
        $("#pol-agentid").val($("#pol-agent-id").val());
        $("#bindname").val($("#bind-name").text());
        $("#bind-proname").val($("#bind-pro-name").val());
        $("#bind-insname").val($("#bind-ins-name").val());
        $("#pol-inscomp").text($("#pol-ins-comp").val());
        $("#pol-prodname").text($("#pol-prod-name").val());
        $("#fromdate").val($("#from-date").text());
        $( "#wetdate").val($( "#wet-date").text());
        $("#pol-interfacetype").val($("#pol-interface-type").val());
        $("#pmid").val($("#pm-id").val());
        $("#pmname").val($("#pm-name").val());
        populatePaymentModes();
        $("#polfrequency").val($("#pol-freqpay").val());
        $("#brnid").val($("#brn-id").val());
        $("#brnname").val($("#brn-name").val());
        populateUserBranches();
        $("#client-polno").val($("#client-policyno").text());
        $("#polmedicalCoverType").val($("#pol-medicalCover-Type").val());
        if ($("#polmedicalCoverType").val()==='G')
            $("#medicalCoverTypinfo").text('Group');
        if ($("#polmedicalCoverType").val()==='I')
            $("#medicalCoverTypinfo").text('Individual');
        $("#curid").val($("#cur-id").val());
        $("#curname").val($("#cur-name").val());
        populateCurrencyLov();


        $("#pol-cardid").val($("#policy-card-id").val());
        $("#card-name").val($("#pol-card-type").text());
        populateBinderCardTypes($("#binderid").val(),$("#from-date").text());
        populateBinderLov();
        $("#binder-frm").select2("enable", false);
        $("#pol-bintype").prop("disabled", true);
        $('#editPolicyModal').modal('show');
    });

}
function populateUserBranches(){
    if($("#brn-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "brn-frm",
            sort : 'obName',
            change: function(e, a, v){
                $("#brnid").val(e.added.obId);
                $("#brnname").val(e.added.obName);
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
function populateCurrencyLov(){
    if($("#curr-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "curr-frm",
            sort : 'curName',
            change: function(e, a, v){
                $("#curid").val(e.added.curCode);
                $("#curname").val(e.added.curName);
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
function populatePaymentModes(){
    if($("#pm-mode-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "pm-mode-frm",
            sort : 'pmDesc',
            change: function(e, a, v){
                $("#pmid").val(e.added.pmId);
                $("#pmname").val(e.added.pmDesc);
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
function populateBinderLov(){
    if($("#binder-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "binder-frm",
            sort : 'binName',
            change: function(e, a, v){
                $("#binderid").val(e.added.binId);
                $("#insuranceCompany").val(e.added.account.name);
                $("#pol-inscomp").val(e.added.account.name);
                $("#productName").val(e.added.product.proDesc);
                $("#productid").val(e.added.product.proCode);
                $("#pol-agentid").val(e.added.account.acctId);
                $("#bindname").val(e.added.binName);
                $("#pol-prodname").val(e.added.product.proDesc);
                $("#polmedicalCoverType").val(e.added.medicalCoverType);
                if (e.added.medicalCoverType==='G')
                    $("#medicalCoverTypinfo").text('Group');
                if (e.added.medicalCoverType==='I')
                    $("#medicalCoverTypinfo").text('Individual');
                populateSubclassLov();
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
                var code  = $('#med-pol-bin-code').val();
                var name = $("#binder-info").text();
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

function populateClientLov(){
    if($("#client-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "client-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#clientid").val(e.added.tenId);
                $("#client-fname").val(e.added.fname);
                $("#client-othername").val(e.added.otherNames);

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

function editMedCovers(button){
    //console.log("waititu testing=="+$("#fund-Binder").val());

    var covers = JSON.parse(decodeURI($(button).data("medcovers")));

    if (covers['sectId']){
    $("#limit-benefit-id").val(covers['sectId']);
    }

    if ($("#fund-Binder").val()==="Y"){
        $(".cover-limit-det").hide();
        // $("#rule-value").val(rules['value']);
        if(covers['fundLimit']){
            $("#fund-limit-value").val(UTILITIES.currencyFormat(covers['fundLimit']));
        }
    }else {
        $(".fund-limit-value").hide();
        console.log("testing here tttt");
        if (covers['limit']) {
            $("#cover-limit-det-pk").val(covers['limit'].id);
            $("#cover-limit-value").val(covers['limit'].limitAmount);
        }
        if (covers['cover']) {

        console.log("testingbb =" + covers['cover'].id);
        populateBenefitsLimitsLov(covers['cover'].id);
    }

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
     //   keyboard: true
    // });
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        if ($("#fund-Binder").val()==="Y") {
            var url = "updateFundBenefit" ;
        } else {
            var url = "updateCatbenfits";
        }
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#medCoversList').DataTable().ajax.reload();
            $('#polTaxesList').DataTable().ajax.reload();
            populatePolicyDetails();
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


function getCategoryMembers(catId){
    var url = "categoryMembers/"+catId;

    var currTable = $('#med-members').DataTable( {
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
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.fname+" "+full.client.otherNames;
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    if(full.memberShipNo)
                        return full.memberShipNo;
                    else return "";
                }
            },
            { "data": "dependentTypes",
                "render": function ( data, type, full, meta ) {
                    if(full.dependentTypes ==="P") return "Principal";
                    else if(full.dependentTypes ==="S") return "Spouse";
                    else if(full.dependentTypes ==="C") {
                        var childType="";
                        if(full.childType){
                            if(full.childType==="D") childType = "Daughter";
                            else if(full.childType==="S") childType = "Son";
                            else if(full.childType==="B") childType = "Brother";
                            else if(full.childType==="SS") childType = "Sister";
                            else if(full.childType==="N") childType = "Nephew";
                            else if(full.childType==="C") childType = "Cousin";
                            else childType="";
                        }
                        return childType;
                    }
                    else return "";
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.gender;
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return moment(full.client.dob).format('DD/MM/YYYY');
                }
            },
            { "data": "mainClient",
                "render": function ( data, type, full, meta ) {
                    if(!full.mainMember)
                        return full.mainClient.fname+" "+full.mainClient.otherNames;
                    else return "";
                }
            },
            { "data": "age" },
            { "data": "cardNo" },
            { "data": "memberStatus",
                "render": function ( data, type, full, meta ) {
                    if(full.memberStatus ==="E") return "Active";
                    else if(full.memberStatus ==="A") return "Active";
                    else if(full.memberStatus ==="N") return "New";
                    else if(full.memberStatus ==="D") return "Removed";
                    else return full.memberStatus;
                }
            },
            { "data": "premium",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.premium);
                }
            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="viewMemberDocs(this);">Documents</button>';

                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="viewFamilyTree(this);">Family Tree</button>';

                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    if (full.category.policy.authStatus ==="D" && full.memberStatus==="D")
                        return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="undoDeleteCatMember(this);">Undo Remove</button>';
                    else
                        return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="undoDeleteCatMember(this);" disabled>Undo Remove</button>';

                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {


                    if(full.category.policy.authStatus){
                        if(full.category.policy.authStatus ==="D" && !(full.memberStatus==="D") && (full.memberStatus==="N"))
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="editFamilyMember(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="editFamilyMember(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }

                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {


                    if(full.category.policy.authStatus){
                        if(full.category.policy.authStatus ==="D" && !(readOnlyMembers(full.category.policy.transType)))
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCatMember(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCatMember(this);" disabled><i class="fa fa-trash-o"></button>';

                    }

                }

            },
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
                    populatePolicyDetails();
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



function deleteCatMember(button){
    var members = JSON.parse(decodeURI($(button).data("members")));
    var message = "Are you sure want to delete "+members['client'].tenantNumber+"?";
    if(members['dependentTypes']==="P"){
        message = "This will delete the Principle "+members['client'].tenantNumber+" and all his family. Continue?";
    }
    bootbox.confirm(message, function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deleteCatMember/' + members['sectId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#med-members').DataTable().ajax.reload();
                    $('#medCoversList').DataTable().ajax.reload();
                    $('#polclausesList').DataTable().ajax.reload();
                    $('#exclusionsList').DataTable().ajax.reload();
                    $('#loadingsList').DataTable().ajax.reload();
                    $('#providersList').DataTable().ajax.reload();
                    $('#med-bedtypes-tbl').DataTable().ajax.reload();
                    populatePolicyDetails();
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



function undoDeleteCatMember(button){
    var members = JSON.parse(decodeURI($(button).data("members")));
    var message = "Are you sure want to undo delete "+members['client'].tenantNumber+"?";
    if(members['dependentTypes']==="P"){
        message = "This will undo delete the Principle "+members['client'].tenantNumber+" and all his family. Continue?";
    }
    bootbox.confirm(message, function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'undoDeleteCatMember/' + members['sectId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Undo Remove Completed Successfully',
                        icon: 'success'
                    });
                    $('#med-members').DataTable().ajax.reload();
                    $('#medCoversList').DataTable().ajax.reload();
                    $('#polclausesList').DataTable().ajax.reload();
                    $('#exclusionsList').DataTable().ajax.reload();
                    $('#loadingsList').DataTable().ajax.reload();
                    $('#providersList').DataTable().ajax.reload();
                    $('#med-bedtypes-tbl').DataTable().ajax.reload();
                    populatePolicyDetails();
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

function populateClientLovs(catId){
    if($("#member-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "member-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#member-client-pk").val(e.added.tenId);
            },
            formatResult : function(a)
            {
                return a.fname+" "+ a.otherNames;
            },
            formatSelection : function(a)
            {
                return a.fname+" "+ a.otherNames;
            },
            initSelection: function (element, callback) {
                var code  = $('#member-client-pk').val();
                var name = $("#member-fname").val();
                var onames = $("#member-oname").val();
                var data = {fname:name,otherNames:onames,tenId:code};
                callback(data);

                callback(data);
            },
            id: "tenId",
            width:"250px",
            placeholder:"Select Member"

        });

    }
    if($("#princi-member-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "princi-member-frm",
            sort : 'sectId',
            change: function(e, a, v){
                $("#principal-client-pk").val(e.added.tenId);
            },
            formatResult : function(a)
            {
                return a.fname+" "+ a.otherNames;
            },
            formatSelection : function(a)
            {
                return a.fname+" "+ a.otherNames;
            },
            initSelection: function (element, callback) {
                var code  = $('#principal-client-pk').val();
                var name = $("#principal-fname").val();
                var onames = $("#principal-oname").val();
                var data = {fname:name,otherNames:onames,tenId:code};
                callback(data);
            },
            id: "tenId",
            width:"250px",
            params: {catId: catId},
            placeholder:"Select Member"

        });

    }
}


function addFamilyMember() {


    $("#member-pk").val("");
    $("#member-client-pk").val("");
    $("#member-fname").val("");
    $("#member-oname").val("");
    $("#dependent-type").val("");
    $("#principal-client-pk").val("");
    $("#principal-fname").val("");
    $("#principal-oname").val("");

    $("#btn-add-member").on("click", function () {




        if ($("#fam-category-pk").val() !== '') {

            $('#member-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#member-cat-id").val($("#fam-category-pk").val());
            populateClientLovs($("#fam-category-pk").val());
            $(".child-type").hide();

            $(".member-principal").hide();
            if ($("#policy-card-id").val()==="")
                $(".membercardDetails").hide();
            else
                $(".membercardDetails").show();
            $(".cardNoDetails").hide();
            $(".generateCardNoDetails").hide();

            $("#risk-wef-date").val($("#member-date-from").val());
            $("#risk-wet-date").val($("#member-date-to").val());
            //if($('#pol-tran-type-disp').text()==="AD"){
                $("#risk-wef-date").attr("readonly",true);
                $("#risk-wet-date").attr("readonly",true);
           //}

            $('#memberModal').modal('show');

        } else {
            bootbox.alert("Select Category to Add Family Member");
            return;
        }
    });

    var $form = $('#member-form');
    var validator = $form.validate();
    $('#memberModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#member-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveCategoryMember').click(function(){
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
        var url = "createCategoryMember";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#med-members').DataTable().ajax.reload();
            $('#pol-med-members').DataTable().ajax.reload();
            $('#medCoversList').DataTable().ajax.reload();
            $('#polclausesList').DataTable().ajax.reload();
            $('#exclusionsList').DataTable().ajax.reload();
            $('#loadingsList').DataTable().ajax.reload();
            $('#providersList').DataTable().ajax.reload();
            $('#med-bedtypes-tbl').DataTable().ajax.reload();
            populatePolicyDetails();
            validator.resetForm();
            $('#memberModal').modal('hide');
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

function getCategoryRules(catId){
    var url = "categoryRules/"+catId;
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
                    if (full.category.policy.authStatus) {
                        if (full.category.policy.authStatus === "D" && !(readOnlyCategories(full.category.policy.transType)))
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
                    if(full.category.policy.authStatus){
                        if(full.category.policy.authStatus ==="D" && !(readOnlyCategories(full.category.policy.transType)))
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

function getCategoryBedType(catId){
    var url = "getCategoryBedTypes/"+catId;
    var currTable = $('#med-bedtypes-tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "deferRender": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 15,
        destroy: true,
        "columns": [
            { "data": "desc" },
            { "data": "id",
                "render": function ( data, type, full, meta ) {
                    if(full.bedTypes){
                    return full.bedTypes.bedDesc;
                    }  else return "";
                }
            },
            { "data": "bedCost" ,
                "render": function ( data, type, full, meta ) {
                  if (full.bedCost){
             return  UTILITIES.currencyFormat(full.bedCost);
                }
                else return "";
            }
            }
            ,
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    if (full.policy.authStatus) {
                        if (full.policy.authStatus === "D" && !(readOnlyCategories(full.policy.transType)))
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-bedtype=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolCategoriesBedType(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-bedtype=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolCategoriesBedType(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }
                }

            }


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



function getExclusions(catId){
    var url = "categoryExclusions/"+catId;;
    var table = $('#exclusionsList').DataTable( {
        "processing": true,
        "serverSide": true,
        "deferRender": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.ailment.baShtDesc;
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.ailment.baDesc;
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.ailment.costPerClaim);
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.ailment.upperLimit);
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.ailment.waitingDays;
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.ailment.chronic;
                }
            },
            {
                "data": "clId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-ailments='+encodeURI(JSON.stringify(full)) + ' onclick="editAilments(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "clId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.policy.authStatus){
                        if(full.category.policy.authStatus ==="D" && !(readOnlyCategories(full.category.policy.transType)))
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-certs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskCerts(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-certs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskCerts(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                }

            },
        ]
    } );
    return table;
}




function getCategoryProviders(catId){
    var url = "categoryProviders/"+catId;
    var currTable = $('#providersList').DataTable( {
        "processing": true,
        "serverSide": true,
        "deferRender": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10], [10] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "providers",
                "render": function ( data, type, full, meta ) {
                    return full.providers.name;
                }

            },
            {
                "data": "providers",
                "render": function ( data, type, full, meta ) {
                    return full.providers.contactPerson;
                }

            },
            {
                "data": "providers",
                "render": function ( data, type, full, meta ) {
                    return full.providers.pinNo;
                }

            },
            {
                "data": "providers",
                "render": function ( data, type, full, meta ) {
                    return full.providers.telNumber;
                }

            },
            {
                "data": "providers",
                "render": function ( data, type, full, meta ) {
                    if(full.providers.bankBranches)
                        return full.providers.bankBranches.branchName;
                    else return "";
                }

            },
            {
                "data": "providers",
                "render": function ( data, type, full, meta ) {
                    return full.providers.accountNumber;
                }

            },
            {
                "data": "providers",
                "render": function ( data, type, full, meta ) {
                    if(full.providers.paymentModes)
                        return full.providers.paymentModes.pmDesc;
                    else return "";
                }

            },
            {
                "data": "cpId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.policy.authStatus){
                        if(full.category.policy.authStatus ==="D" && !(readOnlyCategories(full.category.policy.transType)))
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-providers='+encodeURI(JSON.stringify(full)) + ' onclick="deleteProviders(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-providers='+encodeURI(JSON.stringify(full)) + ' onclick="deleteProviders(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                }

            },
        ]
    } );
    return currTable;
}


function deleteProviders(button){
    var providers = JSON.parse(decodeURI($(button).data("providers")));
    bootbox.confirm("Are you sure want to delete the Panel Provider? ", function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'deleteCategoryProvider/' + providers["cpId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#providersList').DataTable().ajax.reload();
                },
                error: function (jqXHR, textStatus, errorThrown) {

                },
            });
        }
    });
}

function getLoadings(catId){
    var url = "categoryLoadings/"+catId;;
    var table = $('#loadingsList').DataTable( {
        "processing": true,
        "serverSide": true,
        "deferRender": true,
        "ajax": url,
        lengthMenu: [ [10, 15], [10, 15] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.ailment.baShtDesc;
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.ailment.baDesc;
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.ailment.costPerClaim);
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.ailment.upperLimit);
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.ailment.waitingDays;
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.ailment.chronic;
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return full.rateType;
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.rate);
                }
            },
            { "data": "clId",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.loadingAmt);
                }
            },
            {
                "data": "clId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.policy.authStatus) {
                        if (full.category.policy.authStatus === "D" && !(readOnlyCategories(full.category.policy.transType)) )
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-loadings=' + encodeURI(JSON.stringify(full)) + ' onclick="editLoadings(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-loadings=' + encodeURI(JSON.stringify(full)) + ' onclick="editLoadings(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                    }
                    else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-loadings=' + encodeURI(JSON.stringify(full)) + ' onclick="editLoadings(this);"><i class="fa fa-pencil-square-o"></button>';


                }

            },
            {
                "data": "clId",
                "render": function ( data, type, full, meta ) {
                    if(full.category.policy.authStatus){
                        if(full.category.policy.authStatus ==="D" && !(readOnlyCategories(full.category.policy.transType)))
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-certs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskCerts(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-certs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskCerts(this);" disabled><i class="fa fa-trash-o"></button>';

                    }

                }

            },
        ]
    } );
    return table;
}

function editLoadings(button){
    var loadings = JSON.parse(decodeURI($(button).data("loadings")));
    $("#loading-rate").number( true, 2 );
    $("#cat-loading-pk").val(loadings['clId']);
    $("#loading-desc-disp").text(loadings['ailment'].baDesc);
    $("#load-rate-type").val(loadings['rateType']);
    $("#loading-rate").val(loadings['rate']);
    $('#editLoadingsModal').modal('show');
}

function createPolicyClauses(){
    var url = "policyClauses";
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
                        else if(full.clause.clause.clauseType ==='X') return "Exclusion";
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
                    if(full.policy.authStatus){
                        if(full.policy.authStatus==="D" && !(readOnlyClauses(full.policy.transType))){
                            if(full.editable){
                                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);"><i class="fa fa-pencil-square-o"></button>';

                            }else {

                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                        }
                    }else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses='+encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }

                }

            },
            {
                "data": "ccId",
                "render": function ( data, type, full, meta ) {
                    if(full.policy.authStatus){
                        if(full.policy.authStatus ==="D" && !(readOnlyClauses(full.policy.transType)) ){
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



function deletePolicyClause(button){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    var clause = JSON.parse(decodeURI($(button).data("clauses")));
    bootbox.confirm("Are you sure want to delete "+clause['clauHeading']+"?", function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deletePolClause/' + clause['polClauseId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Success',
                text: 'Record Deleted Successfully',
                icon: 'success'
               });
                    $('#polclausesList').DataTable().ajax.reload();
                    populatePolicyDetails();
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

function createPolClauses(){
    var $classForm = $('#pol-clause-form');
    var validator = $classForm.validate();
    $('#savepolClauseBtn').click(function(){
        if (!$classForm.valid()) {
            return;
        }
        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createPolicyClause";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#polclausesList').DataTable().ajax.reload();
            validator.resetForm();
            $('#pol-clause-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#chk-cl-editable").prop("checked", false);
            $('#clauseModal').modal('hide');
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

function editPolicyClause(button){
    var clause = JSON.parse(decodeURI($(button).data("clauses")));
    if(!clause['editable']){
        bootbox.alert("The Selected Clause is not Editable");
        return;
    }
    $("#pol-clause-code").val(clause['polClauseId']);
    $("#sub-clause-code").val(clause['clause'].clauId);
    $("#pol-new-clause").val(clause['newClause']);
    $("#sub-clau-id").val(clause['clause'].clause.clauShtDesc);
    $("#sub-clause-name").val(clause['clauHeading']);
    $("#chk-cl-editable").prop("checked", clause['editable']);
    $("#sub-cla-wording").val(clause['clauWording']);
    $("#clause-pol-code").val(clause['policy'].policyId)
    $('#clauseModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}


function createCategoryLoading(){
    var $form = $('#cat-loading-form');
    var validator = $form.validate();
    $('#editLoadingsModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#cat-loading-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#createCatLoadings').click(function(){
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
        var url = "saveCatLoadings";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#loadingsList').DataTable().ajax.reload();
            $('#med-members').DataTable().ajax.reload();
            $('#medCoversList').DataTable().ajax.reload();
            $('#exclusionsList').DataTable().ajax.reload();
            populatePolicyDetails();
            validator.resetForm();
            $('#editLoadingsModal').modal('hide');
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




function createSelfFundDetails(){
    var url = "selffundparams";
    var currTable = $('#pol_self_fund_tbl').DataTable( {
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
            { "data": "applicableLevel",
                "render":function(data,type,full,meta){
                    if (full.applicableLevel =='FFS' )
                        return "Fixed Fee Per Scheme";
                    else
                    if  (full.applicableLevel =='FFP' )
                        return "Fixed Fee Per Person";
                    else
                    if  (full.applicableLevel =='FFF' )
                        return "Fixed Fee Per Family";
                    else
                    if  (full.applicableLevel =='FFC' )
                        return "Fixed Fee Per Claim";
                    else
                    if  (full.applicableLevel =='PPC' )
                        return "Percentage Per Claim";
                } },
            { "data": "applicableValue" },
            { "data": "payWhenFundExhausted" },
            { "data": "payWhenBenefitExhausted" },
            { "data": "minDeposit",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.minDeposit);
                }
            },
            { "data": "fundResetAmount",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.fundResetAmount);
                }
            },
            { "data": "fundDepositAmount",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.fundDepositAmount);
                }
            },
            { "data": "fundDepositAmount",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(getFundReceiptedAmt());
                }
            },

            { "data": "billingFrequency"
            },
            {
                "data": "sfpId",
                "render": function ( data, type, full, meta ) {
                    if (full.policyTrans.authStatus) {
                        if (full.policyTrans.authStatus === "D" && !(readOnlySelfund(full.policyTrans.transType))) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-selffundparams=' + encodeURI(JSON.stringify(full)) + ' onclick="editSelfFund(this);"><i class="fa fa-pencil-square-o"></button>';
                        }
                        else {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-selffundparams=' + encodeURI(JSON.stringify(full)) + ' onclick="editSelfFund(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                        }
                    }
                    else     return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-selffundparams=' + encodeURI(JSON.stringify(full)) + ' onclick="editSelfFund(this);"><i class="fa fa-pencil-square-o"></button>';

                }

            },
            {
                "data": "sfpId",
                "render": function (data, type, full, meta) {
                    if (full.policyTrans.authStatus) {
                        if (full.policyTrans.authStatus === "D" && !(readOnlySelfund(full.policyTrans.transType))) {
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-selffundparams=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteSelfFund(this);"><i class="fa fa-trash-o"></button>';
                        }
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-selffundparams=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteSelfFund(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                    else     return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-selffundparams=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteSelfFund(this);"><i class="fa fa-trash-o"></button>';
                }
            }
        ]
    } );

    return currTable;
}





function getSelfFundReceipts(){
    var url = "fundreceipts";
    var currTable = $('#self_fund_receipts').DataTable( {
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
            { "data": "refNo" },
            { "data": "transDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.transDate).format('DD/MM/YYYY');
                }
            },
            { "data": "amount",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.amount);
                }
            },
            { "data": "narrations" },
            { "data": "transdc" },


            { "data": "postedUser",
                "render": function ( data, type, full, meta ) {

                    return full.postedUser.username;
                }
            },

            { "data": "transType"
            },

        ]
    } );

    return currTable;
}

function getFundReceiptedAmt(){
    var data = 0;
    $.ajax({
        type: 'GET',
        url:  'getReceiptedAmt',
        dataType: 'json',
        async: false,
        success: function(result) {
            data = result;
        },
        error: function(jqXHR, textStatus, errorThrown) {

        }
    });

    return data;
}

function newSelfFund(){
    $("#btn-add-self-fund").on("click", function(){
        $("#self-billing-freq").val($("#pol-freqpay").val());
        $('#self-fund-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $('#selfFundModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });


}



function saveSelfFund(){
    var $form = $('#self-fund-form');
    var validator = $form.validate();
    $('#selfFundModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#self-fund-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#saveSelfFund').click(function(){
        if (!$form.valid()) {
            return;
        }
        // $('#myPleaseWait').modal({
        //     backdrop: 'static',
        //     keyboard: true
        // })
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createSelfFundParams";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#pol_self_fund_tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#selfFundModal').modal('hide');
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



function savePolicy(){
    var $form = $('#med-polcy-form');
    var validator = $form.validate();
    $('#editPolicyModal').on('hidden.bs.modal', function () {
        validator.resetForm();
        $('#med-polcy-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
    });

    $('#savePolicy').click(function(){
        if (!$form.valid()) {
            return;
        }
        // $('#myPleaseWait').modal({
        //     backdrop: 'static',
        //     keyboard: true
        // })
        var $btn = $(this).button('Saving');
        var data = {};
        $form.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "editPolicyDetails";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            populatePolicyDetails();

            validator.resetForm();
            $('#editPolicyModal').modal('hide');
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

function editFamilyMember(button) {
    var members = JSON.parse(decodeURI($(button).data("members")));
            $("#member-cat-id").val(members["category"].id);
            $("#member-pk").val(members['sectId']);
            $("#member-client-pk").val(members["client"].tenId);
            $("#member-fname").val(members["client"].fname);
            $("#member-oname").val(members["client"].otherNames);
            $("#dependent-type").val(members['dependentTypes']);
            $("#principal-client-pk").val(members["mainClient"].tenId);
            $("#principal-fname").val(members["mainClient"].fname);
            $("#principal-oname").val(members["mainClient"].otherNames);
            populateClientLovs(members["category"].id);
    if (members['memberHasCard'])
            $("#memberhas-card").val(members['memberHasCard']);
    if (members['autoGenerateCard'])
            $("#generate-card").val(members['autoGenerateCard']);
    if (members['cardNo'])
            $("#card-no").val(members['cardNo']);

    if (members['childType']) {
        $("#child-type").val(members['childType']);
        if (members['childType'] === "C") {
            $(".child-type").show();
            $(".member-principal").show();
        }
        else if (members['childType']=== "P") {
            $(".member-principal").hide();
            $(".child-type").hide();
        }

    }
    else {
        $(".child-type").hide();
        $(".member-principal").show();
    }
        if ($("#policy-card-id").val()==="")
            $(".membercardDetails").hide();
        else
        $(".membercardDetails").show();
            if($("#memberhas-card").val()==="Y"){
                $(".cardNoDetails").show();
                $(".generateCardNoDetails").hide();
            }else if ($("#memberhas-card").val()==="N"){

                $(".cardNoDetails").hide();
                if ($("#fund-Binder").val()==="Y")
                    $(".generateCardNoDetails").show();
                else $(".generateCardNoDetails").hide();


            }else {
                $(".cardNoDetails").hide();
                $(".generateCardNoDetails").hide();
            }


            $("#risk-wef-date").val(moment(members['wefDate']).format('DD/MM/YYYY'));
            $("#risk-wet-date").val(moment(members['wetDate']).format('DD/MM/YYYY'));
            //if($('#pol-tran-type-disp').text()==="AD"){
            $("#risk-wef-date").attr("readonly", true);
            $("#risk-wet-date").attr("readonly", true);
            //}

            $('#memberModal').modal('show');

}
function editPolCategories(button){
    var categories = JSON.parse(decodeURI($(button).data("polcategories")));
    $(".categoryDisplay").show();
    $("#category-pol-id").val(polCode);
    $("#category-id").val(categories['id']);
    $("#binder-det-pk").val(categories["binderDetails"].detId);
    //+"-"+categories["binderDetails"].subCoverTypes.coverTypes.covName
    $("#binderDetails-Display").text(categories["binderDetails"].subCoverTypes.subclass.subDesc +"-"+  categories["binderDetails"].subCoverTypes.coverTypes.covName);

    $("#sht-desc").val(categories['shtDesc']);
    $("#cat-desc").val(categories['desc']);
    $("#bedtype-pk").val(categories['bedTypes'].bedId);
     populateBedTyoesLov();
    $("#bed-cost").val(categories['bedCost']);
    $(".categoryLov").hide();
    $(".categoryDetails").show();
    $(".bedtypedetails").hide();
    $("#saveCategory").show();
    $("#saveCategoryBedType").hide();
    $('#categoryModal').modal('show');
}
function editPolCategoriesBedType(button){
    var categories = JSON.parse(decodeURI($(button).data("bedtype")));
    $(".categoryDisplay").show();
    $(".bedtypedetails").show();
    $("#category-pol-id").val(polCode);
    $("#category-id").val(categories['id']);
    $("#binderDetails-Display").text(categories["desc"]);
    $("#categoryModalLabel").val("Edit Bed Type");
    $("#sht-desc").val(categories['shtDesc']);
    $("#cat-desc").val(categories['desc']);
    if(categories['bedTypes']) {
        $("#bedtype-pk").val(categories['bedTypes'].bedId);
        $("#bedtype-disp").val(categories['bedTypes'].bedDesc);
    }

    $("#binder-det-pk").val(categories["binderDetails"].detId);
    populateBedTyoesLov();
    $("#bed-cost").val(categories['bedCost']);
    $(".categoryLov").hide();
    $(".categoryDetails").hide();
    $("#saveCategory").hide();
    $("#saveCategoryBedType").show();
    $('#categoryModal').modal('show');
}
function editSelfFund(button){
    var selffundparams = JSON.parse(decodeURI($(button).data("selffundparams")));
    $("#self-fund-pk").val( selffundparams['sfpId']);
    $("#self-appli-level").val( selffundparams['applicableLevel']);
    $("#self-appli-value").val(selffundparams['applicableValue']);
    $("#pay-fund-exhausted").prop("checked", selffundparams["payWhenFundExhausted"]);
    $("#pay-benefit-exhausted").prop("checked", selffundparams["payWhenBenefitExhausted"]);
    $("#carry-forward-fund").prop("checked", selffundparams["carryForwardBalances"]);
    $("#self-min-deposit").val(selffundparams['minDeposit'])
    $("#fund-reset-deposit").val(selffundparams['fundResetAmount'])
    $("#fund-deposit-amount").val(selffundparams['fundDepositAmount'])
    $("#self-billing-freq").val(selffundparams['billingFrequency'])
    $("#deduct-admin-from-fund").prop("checked", selffundparams["deductAdminFeeFromFund"]);



    $('#selfFundModal').modal('show');
}

function deleteSelfFund(button) {
    var fund = JSON.parse(decodeURI($(button).data("selffundparams")));
    bootbox.confirm("Are you sure want to delete fund data? ", function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'deleteSelfFundParams/' + fund["sfpId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#pol_self_fund_tbl').DataTable().ajax.reload();
                },
                error: function (jqXHR, textStatus, errorThrown) {

                }
            });
        }
    });
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






function addNewCategoryBenefits() {

    $("#btn-add-new-benefit").click(function () {
        if ($("#fam-category-pk").val() != '') {
            $("#category-ben-code").val($("#fam-category-pk").val());
            $.ajax({
                type: 'GET',
                url: 'unassignMedCovers',
                dataType: 'json',
                data: {"catId": $("#fam-category-pk").val(),"binCode": $("#fam-category-bin-pk").val()},
                async: true,
                success: function (result) {
                    $("#binderCoversTbl tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res][0] + "'></td><td>" + result[res][1] + "</td><td>" + result[res][2] + "</td></tr>";
                        $("#binderCoversTbl").append(markup);
                    }
                    $('#binderCoversModal').modal({
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


function addNewPolTaxes() {

    $("#btn-add-new-tax").click(function () {
        $("#tax-policy-code").val(polCode);
            $.ajax({
                type: 'GET',
                url: 'unassignPolTaxes',
                dataType: 'json',
                data: {"polId":polCode},
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



function getSchemeMembers(){
    var url = "schemeMembers";

    var currTable = $('#pol-med-members').DataTable( {
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
            { "data": "category",
                "render": function ( data, type, full, meta ) {
                    return full.category.desc;
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.fname+" "+full.client.otherNames;
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    if(full.memberShipNo)
                        return full.memberShipNo;
                    else return "";
                }
            },
            { "data": "dependentTypes",
                "render": function ( data, type, full, meta ) {
                    if(full.dependentTypes ==="P") return "Principal";
                    else if(full.dependentTypes ==="S") return "Spouse";
                    else if(full.dependentTypes ==="C") {
                        var childType="";
                        if(full.childType){
                            if(full.childType==="D") childType = "Daughter";
                            else if(full.childType==="S") childType = "Son";
                            else if(full.childType==="B") childType = "Brother";
                            else if(full.childType==="SS") childType = "Sister";
                            else if(full.childType==="N") childType = "Nephew";
                            else if(full.childType==="C") childType = "Cousin";
                            else childType="";
                        }
                        return childType;
                    }
                    else return "";
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.gender;
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return moment(full.client.dob).format('DD/MM/YYYY');
                }
            },
            { "data": "age" },
            { "data": "cardNo" },
            { "data": "memberStatus",
                "render": function ( data, type, full, meta ) {
                    if(full.memberStatus ==="E") return "Active";
                    else if(full.memberStatus ==="A") return "Active";
                    else if(full.memberStatus ==="N") return "New";
                    else if(full.memberStatus ==="D") return "Removed";
                    else return full.memberStatus;
                }
            },
            { "data": "premium",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.premium);
                }
            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="viewMemberDocs(this);">Documents</button>';

                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="viewFamilyTree(this);">Family Tree</button>';

                }

            },
            {
                "data": "sectId",
                "render": function ( data, type, full, meta ) {


                    if(full.category.policy.authStatus){
                        if(full.category.policy.authStatus ==="D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCatMember(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="deleteCatMember(this);" disabled><i class="fa fa-trash-o"></button>';

                    }


                }

            },
        ]
    } );
    return currTable;
}

function viewFamilyTree(button){
    var members = JSON.parse(decodeURI($(button).data("members")));
    getPrincipalDetails(members['sectId']);
    getDependentDetails(members['sectId']);
    $('#memberFamilyModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}


function viewMemberDocs(button){
    var members = JSON.parse(decodeURI($(button).data("members")));
    $("#risk-code-pk").val(members['sectId']);
    console.log("members="+members['sectId'])
    getRiskDocs(members['sectId']);


    //$('#viewDocModal').show();
    $('#viewDocModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}


function closeRiskReqDocsModal(){
    getRiskDocs($("#risk-code-pk").val());


    $('#riskReqDocsModal').modal('hide');
    $('#viewDocModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function getRiskDocs(riskCode){
    var url = "riskDocs/"+riskCode;
    var currTable = $('#risk_docs_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10,15,20], [10,15,20] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "reqdDocs",
                "render": function ( data, type, full, meta ) {

                    return full.reqdDocs.requiredDoc.reqShtDesc;
                }
            },
            { "data": "reqdDocs",
                "render": function ( data, type, full, meta ) {

                    return full.reqdDocs.requiredDoc.reqDesc;
                }
            },
            { "data": "uploadedFileName" },
            { "data": "checkSum" },
            {
                "data": "rdId",
                "render": function ( data, type, full, meta ) {
                    if(full.member.category.policy.authStatus){
                        if(full.member.category.policy.authStatus ==="D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editRiskDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editRiskDocs(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }
                    else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editRiskDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "rdId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="downloadRiskDoc(this);"><i class="fa fa-file-archive-o"></button>';

                }

            },
            {
                "data": "rdId",
                "render": function ( data, type, full, meta ) {
                    if(full.member.category.policy.authStatus){
                        if(full.member.category.policy.authStatus ==="D")
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskDoc(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskDoc(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                    else
                        return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskDoc(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}

function deleteRiskDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    bootbox.confirm("Are you sure want to delete "+docs['uploadedFileName']+"?", function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deleteRiskDoc/' + docs['rdId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#risk_docs_tbl').DataTable().ajax.reload();
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

function downloadRiskDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    window.open(SERVLET_CONTEXT+"/protected/uw/policies/riskdocument/"+docs['rdId'],
        '_blank' // <- This is what makes it open in a new window.
    );
}

function editRiskDocs(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    $("#risk-doc-name").text(docs["reqdDocs"].requiredDoc.reqDesc);
    $("#risk-upload-name").text(docs['uploadedFileName']);
    $("#risk-doc-id").val(docs['rdId']);
    $('#riskdocModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}

function uploadRiskDocument(){
    var $form = $("#risk-doc-form");
    var validator = $form.validate();
    $('form#risk-doc-form')
        .submit( function( e ) {
            e.preventDefault();

            if (!$form.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var data = new FormData( this );
            data.append( 'file', $( '#avatar' )[0].files[0] );
            $.ajax( {
                url: 'uploadRequiredDocs',
                type: 'POST',
                data: data,
                processData: false,
                contentType: false,
                success: function (s ) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Success',
                text: 'File Uploaded Successfully',
                icon: 'success'
            });
                    $('#riskdocModal').modal('hide');
                    var $el = $('#avatar');
                    $el.wrap('<form>').closest('form').get(0).reset();
                    $el.unwrap();
                    $('#risk_docs_tbl').DataTable().ajax.reload();

                },
                error: function(xhr, error){
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
                }
            });
        });
}



function getPrincipalDetails(memberId){
    var url = "principalDetails/"+memberId;

    var currTable = $('#principalList').DataTable( {
        "processing": true,
        "serverSide": true,
        "searching": false,
        autoWidth: true,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.fname+" "+full.client.otherNames;
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    if(full.memberShipNo)
                        return full.memberShipNo;
                    else return "";
                }
            },
            { "data": "dependentTypes",
                "render": function ( data, type, full, meta ) {
                    if(full.dependentTypes ==="P") return "Principal";
                    else if(full.dependentTypes ==="S") return "Spouse";
                    else if(full.dependentTypes ==="C") {
                        var childType="";
                        if(full.childType){
                            if(full.childType==="D") childType = "Daughter";
                            else if(full.childType==="S") childType = "Son";
                            else if(full.childType==="B") childType = "Brother";
                            else if(full.childType==="SS") childType = "Sister";
                            else if(full.childType==="N") childType = "Nephew";
                            else if(full.childType==="C") childType = "Cousin";
                            else childType="";
                        }
                        return childType;
                    }
                    else return "";
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.gender;
                }
            },
            { "data": "age" },
        ]
    } );
    return currTable;
}


function getDependentDetails(memberId){
    var url = "dependentDetails/"+memberId;

    var currTable = $('#dependentList').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "searching": false,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.fname+" "+full.client.otherNames;
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    if(full.memberShipNo)
                        return full.memberShipNo;
                    else return "";
                }
            },
            { "data": "dependentTypes",
                "render": function ( data, type, full, meta ) {
                    if(full.dependentTypes ==="P") return "Principal";
                    else if(full.dependentTypes ==="S") return "Spouse";
                    else if(full.dependentTypes ==="C") {
                        var childType="";
                        if(full.childType){
                            if(full.childType==="D") childType = "Daughter";
                            else if(full.childType==="S") childType = "Son";
                            else if(full.childType==="B") childType = "Brother";
                            else if(full.childType==="SS") childType = "Sister";
                            else if(full.childType==="N") childType = "Nephew";
                            else if(full.childType==="C") childType = "Cousin";
                            else childType="";
                        }
                        return childType;
                    }
                    else return "";
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.gender;
                }
            },
            { "data": "age" },
        ]
    } );
    return currTable;
}


function uploadMembersFile(){
    var $form = $("#member-upload-form");
    var validator = $form.validate();
    $('form#member-upload-form')
        .submit( function( e ) {
            e.preventDefault();
            if (!$form.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var data = new FormData( this );
            data.append( 'file', $( '#avatar' )[0].files[0] );
            $.ajax( {
                url: 'uploadMembers',
                type: 'POST',
                data: data,
                processData: false,
                contentType: false,
                success: function (s ) {
                    $("#memberfile").show();
                     $("#memberfile").text(s);
                     $("#memberfile").attr("href",SERVLET_CONTEXT+"/protected/medical/policies/excelUpload/"+s);
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Success',
                text: 'File Uploaded Successfully',
                icon: 'success'
            });
                    var $el = $('#avatar');
                    $el.wrap('<form>').closest('form').get(0).reset();
                    $el.unwrap();
                    $('#pol-med-members').DataTable().ajax.reload();
                },
                error: function(jqXHR, textStatus, errorThrown){
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                }
            });
        });
}




function saveCategoryBenefits(){
    var arr = [];
    $("#saveNewBenefitBtn").click(function(){
        $("#binderCoversTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Benefits Selected to attach..");
            return;
        }

        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });

        var $currForm = $('#category-benefit-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createCategoryBenefits";
        data.benefits = arr;


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
                $('#medCoversList').DataTable().ajax.reload();
                $('#binderCoversModal').modal('hide');
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
        var url = "createNewPolTax";
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
                $('#med-members').DataTable().ajax.reload();
                $('#pol-med-members').DataTable().ajax.reload();
                $('#medCoversList').DataTable().ajax.reload();
                $('#polclausesList').DataTable().ajax.reload();
                $('#exclusionsList').DataTable().ajax.reload();
                $('#loadingsList').DataTable().ajax.reload();
                $('#providersList').DataTable().ajax.reload();
                $('#med-bedtypes-tbl').DataTable().ajax.reload();
                populatePolicyDetails();
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


function addNewPolicyProviders() {

    $("#btn-add-new-panel").click(function () {
        if ($("#fam-category-pk").val() != '') {
            $("#category-provider-code").val($("#fam-category-pk").val());
            $.ajax({
                type: 'GET',
                url: 'unassignCatProviders',
                dataType: 'json',
                data: {"catId": $("#fam-category-pk").val(),"binCode": $("#med-pol-bin-code").val()},
                async: true,
                success: function (result) {
                    $("#providerPanelTbl tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res][0] + "'></td><td>" + result[res][1] + "</td></tr>";
                        $("#providerPanelTbl").append(markup);
                    }
                    $('#catProvidersModal').modal({
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



function saveCategoryProviders(){
    var arr = [];
    $("#saveNewProvidersBtn").click(function(){
        $("#providerPanelTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Benefits Selected to attach..");
            return;
        }

        // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });

        var $currForm = $('#category-provider-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) {
            data[x.name] = x.value;
        });
        var url = "createCategoryProviders";
        data.providers = arr;


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
                $('#providersList').DataTable().ajax.reload();
                $('#catProvidersModal').modal('hide');
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


function getNewClauses(){

    $.ajax({
        type: 'GET', url:  'getNewClauses',
        dataType: 'json',
        async: true,
        success: function(result) {
            $("#new_clause_tbl tbody").each(function(){
                $(this).remove();
            });
            for(var res in result){
                var markup = "<tr><td><input type='checkbox' class='clause-check'><input type='hidden' class='clause-id form-control' value='"+result[res].clause.clauId+
                    "'></td><td>"
                    + result[res].clauHeading + "</td></tr>";
                $("#new_clause_tbl").append(markup);
            }

        },
        error: function(jqXHR, textStatus, errorThrown) {

        }
    });

}


function getCreateNewClauses(){
    var arr = [];
    $("#new_clause_tbl tr").each(function(row,tr){
        var checked   = $(this).find('.clause-check').eq(0).is(":checked");
        var clauseId   = $(this).find('.clause-id').eq(0).val();
        if(checked){
            arr.push(clauseId);
        }

    });

    return arr;
}



function getNewClausesModal(){
    $("#btn-add-new-clause").click(function(){
        getNewClauses();
        $('#newclausesModal').modal({
            backdrop: 'static',
            keyboard: true
        });

    });

    $("#savenewClause").on('click', function(){
        var items = getCreateNewClauses();
        if(items.length==0){
            bootbox.alert("No Clauses Selected to add..");
            return;
        }



        var $currForm = $('#new-clause-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }
        var data = {};
        $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createNewClause";

        data.clauses = items;
        console.log(data);
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
                        text: 'Selected Claused Added Successfully',
                        icon: 'success'
                    });
                    $('#polclausesList').DataTable().ajax.reload();
                    populatePolicyDetails();
                    $('#newclausesModal').modal('hide');
                    items = [];
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
    });

}

function populateBedTyoesLov() {
    if ($("#bedtypes-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "bedtypes-frm",
            sort: 'bedDesc',
            change: function (e, a, v) {
                $("#bedtype-pk").val(e.added.bedId);
                $("#bed-cost").val(e.added.bedCost);
            },
            formatResult: function (a) {
                return a.bedDesc;
            },
            formatSelection: function (a) {
                return a.bedDesc;
            },
            initSelection: function (element, callback) {
                var code = $("#bedtype-pk").val();
                var name = $("#bedtype-disp").val();
                var data = {bedDesc: name, bedId: code};
                callback(data);
            },
            id: "bedId",
            width: "250px",
            placeholder: "Select Bed Type"

        });

    }
}

function createPolicyChecks(){
    var url = "policyChecks";
    var currTable = $('#polChecksList').DataTable(UTILITIES.extendsOpts({
        "ajaxUrl":url,
        "columns": [

            { "data": "tcNo",
                "render": function ( data, type, full, meta ) {
                    return full.checks.checkName;
                }
            },
            { "data": "authorised",
                "render": function ( data, type, full, meta ) {
                    if(full.authorised)
                        return full.authorised;
                    else return "No";
                }
            },
            { "data": "authBy",
                "render": function ( data, type, full, meta ) {
                    if(full.authBy)
                        return full.authBy.username;
                    else return "";
                }
            },
            { "data": "authDate",
                "render": function ( data, type, full, meta ) {
                    if(full.authDate)
                        return moment(full.authDate).format('DD/MM/YYYY');
                    else return "";
                }
            },
            {
                "data": "checks",
                "render": function ( data, type, full, meta ) {
                    if(full.authorised && full.authorised==="Y"){

                    }else
                        return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-checks='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.authChecks(this);">Approve</button>';


                }

            },
        ]
    }) );
    return currTable;
}