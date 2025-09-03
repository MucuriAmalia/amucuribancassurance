/**
 * Created by peter on 5/12/2017.
 */

var UTILITIES = UTILITIES || {};
$(function() {

    $(document).ready(function () {
        eventTypeLov();
        countryLov();
        providerLov();
        loadClmMembersModal();
        networkLov();
        //activityLov();
        serviceFrmLov();
        diagnosisLov();
        createParTrans();
        benefitsLov(-2000);
        populateClaimDetails();
        addRequestAndServices();
        saveRequests();
        saveRequestServices();
        checkManualCtrl();
        uploadClmDocument();
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
        UTILITIES.emailReports();
        $("#member-dtls-btn").click(function(){
            if($("#member-cat-id-pk").val() != '') {
                getCategoryMemberDetails($("#member-cat-id-pk").val());
                $('#memberDtlsModal').modal("show");
            }
        });
        $("#btn-make-ready").click(function(){
            makeReadyPreauth();
        });

        $("#btn-auth-clm").click(function(){
            authorizePreauth();
        });
        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });

        $('.currency').number( true, 2 );

        $("#gender-search-number").on('change',function(){
            createMemberTrans();
        });

    });
});


function checkManualCtrl(){
    $('#manual-ctrl').click(function() {
        if ($(this).is(':checked')) {
            $("#approved-qty").removeAttr('disabled');
            $("#approved-price").removeAttr('disabled');
            $("#exgratia-ctrl").removeAttr('disabled');
        } else {
            $("#approved-qty").prop("disabled", true);
            $("#approved-price").prop("disabled", true);
            $("#exgratia-ctrl").prop("disabled", true);
        }

    });

    $('#exgratia-ctrl').click(function() {
        if ($(this).is(':checked')) {
            $("#exgratia-amount").removeAttr('disabled');
        } else {
            $("#exgratia-amount").prop("disabled", true);
        }

    });

    $('#chk-manual-ctrl').click(function() {
        if ($(this).is(':checked')) {
            $("#req-approved-qty").removeAttr('disabled');
            $("#req-approved-price").removeAttr('disabled');
            $("#req-exgratia-ctrl").removeAttr('disabled');
        } else {
            $("#req-approved-qty").prop("disabled", true);
            $("#req-approved-price").prop("disabled", true);
            $("#req-exgratia-ctrl").prop("disabled", true);
        }

    });

    $('#req-exgratia-ctrl').click(function() {
        if ($(this).is(':checked')) {
            $("#req-exgratia-amount").removeAttr('disabled');
        } else {
            $("#req-exgratia-amount").prop("disabled", true);
        }

    });
}



function populateClientLov(){
    if($("#client-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "client-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#client-id").val(e.added.tenId);
                createMemberTrans();

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

            },
            id: "tenId",
            placeholder:"Select Client"

        });
    }
}

function populateClientScheme(){
    if($("#med-pol-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "med-pol-frm",
            sort : 'polNo',
            change: function(e, a, v){
                $("#client-pol-id").val(e.added.policyId);
                createMemberTrans();

            },
            formatResult : function(a)
            {
                return a.polNo;
            },
            formatSelection : function(a)
            {
                return a.polNo;
            },
            initSelection: function (element, callback) {

            },
            id: "policyId",
            placeholder:"Select Policy"

        });
    }
}

function saveRequests(){
    var $classForm = $('#medical-request-form');
    var validator = $classForm.validate();
    $('#saveRequestsBtn').click(function(){
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
        var url = "createRequests";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#med_request_tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#medical-request-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
           $('#requestModal').modal('hide');
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



function saveRequestServices(){
    var $classForm = $('#medical-service-form');
    var validator = $classForm.validate();
    $('#saveRequestServiceBtn').click(function(){
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
        var url = "createRequestServices";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            populateClaimDetails();
            $('#med_service_tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#medical-service-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#serviceModal').modal('hide');
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


function  addRequestAndServices(){
    $("#btn-add-new-service").on('click', function(){
        if($("#par-request-code-pk").val() != '') {
            $('#medical-service-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#req-service-reg-code").val($("#par-request-code-pk").val());
            $("#req-approved-qty").prop("disabled", true);
            $("#req-approved-price").prop("disabled", true);
            $("#chk-manual-ctrl").prop("checked", false);
            $("#req-exgratia-amount").prop("disabled", true);
            $("#req-exgratia-ctrl").prop("checked", false);
            serviceFrmLov();

            $('#serviceModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        }
        else{
            bootbox.alert("Select Request to Add Service");
        }
    });

    $("#btn-add-new-request").on('click', function(){
        $('#medical-request-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        diagnosisFrmLov();
        benefitsFrmLov($("#med-par-category-pk").val());
        $(".benefits-display").hide();
        $(".benefits").show();
        $('#requestModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    });
}



function makeReadyPreauth(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'makereadypreauth',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Transaction Made Ready Successfully',
                icon: 'success'
            });
            populateClaimDetails();
            createParServices(-2000);
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



function authorizePreauth(){
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    // $('#myPleaseWait').modal({
   //     backdrop: 'static',
     //   keyboard: true
    // });
    $.ajax({
        type: 'GET',
        url:  'authorizepreauth',
        dataType: 'json',
        async: true,
        success: function(result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Transaction Authorized Successfully',
                icon: 'success'
            });
            populateClaimDetails();
            createParServices(-2000);
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

function populateClaimDetails(){
    if(typeof parCode!== 'undefined'){
        if(parCode!==-2000){
             $("#edit_services").hide();
             $(".med_requests").hide();
             $("#med_requests_tbl").show();
            $("#med_services_tbl").show();
            $("#par-id-pk").val(parCode);
            createParRequests(parCode);
            $.ajax( {
                url: 'getClaimDetails',
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s ) {
                    if(s.parStatus ==='Open'){
                        $("#event-frm,#country,#network-frm,#provider-frm").select2("enable", true);
                        $("#pol-appr-type").removeAttr("disabled");
                        $("#btn-show-search-clm").removeAttr("disabled");
                        $("#from-date").removeAttr("disabled");
                        $("#wet-date").removeAttr("disabled");
                        $("#admission-date").removeAttr("disabled");
                        $("#exp-admission-date").removeAttr("disabled");
                        $("#btn-save-clm").show();
                        $("#btn-auth-clm").show();
                        $("#btn-add-new-request").show();
                        $("#btn-add-new-service").show();
                        $("#btn-claim-reports").show();
                        $("#btn-make-ready").show();
                        $("#btn-auth-clm").show();
                        $("#div-convert-clm").hide();
                    }
                    else  if(s.parStatus ==='Ready'){
                        $("#event-frm,#country,#network-frm,#provider-frm").select2("enable", false);
                        $("#pol-appr-type").prop("disabled", true);
                        $("#btn-show-search-clm").prop("disabled", true);
                        $("#from-date").prop("disabled", true);
                        $("#wet-date").prop("disabled", true);
                        $("#admission-date").prop("disabled", true);
                        $("#exp-admission-date").prop("disabled", true);
                        $("#btn-save-clm").hide();
                        $("#btn-auth-clm").hide();
                        $("#btn-add-new-request").hide();
                        $("#btn-add-new-service").hide();
                        $("#btn-make-ready").hide();
                        $("#btn-auth-clm").show();
                    }
                    else  if(s.parStatus ==='Authorised'){
                        $("#event-frm,#country,#network-frm,#provider-frm").select2("enable", false);
                        $("#pol-appr-type").prop("disabled", true);
                        $("#btn-show-search-clm").prop("disabled", true);
                        $("#btn-show-search-clm").hide()
                        $("#from-date").prop("disabled", true);
                        $("#wet-date").prop("disabled", true);
                        $("#admission-date").prop("disabled", true);
                        $("#exp-admission-date").prop("disabled", true);
                        $("#btn-save-clm").hide();
                        $("#btn-auth-clm").hide();
                        $("#btn-add-new-request").hide();
                        $("#btn-add-new-service").hide();
                        $("#btn-claim-reports").show();
                        $("#btn-make-ready").hide();
                        $("#btn-auth-clm").hide();
                        $("#div-convert-clm").show();

                    }

                    getClaimDocs();
                    $("#par-id").val(s.parId);
                    $("#pol-appr-type").val(s.approvalType);
                    $("#member-number").val(s.clientDef.tenantNumber);
                    $("#pol-no").text(s.policyTrans.polNo);
                    $("#member-client-id").val(s.clientDef.tenId);
                    $("#policy-id").val(s.policyTrans.policyId);
                    $("#from-date").val(moment(s.eventDate).format('DD/MM/YYYY'));
                    $("#wet-date").val(moment(s.notDate).format('DD/MM/YYYY'));
                    $("#med-networks-name").val(s.medicalNetworks.benDesc);
                    $("#med-networks").val(s.medicalNetworks.benId);
                    networkLov();
                    $("#events-name").val(s.events.eventDesc);
                    $("#events-id").val(s.events.eventId);
                    eventTypeLov();
                    $("#country-name").val(s.country.couName);
                    $("#country-id").val(s.country.couCode);
                    countryLov();
                    $("#provider-no").text(s.providerContracts.serviceProviders.name);
                    $("#provider-id").val(s.providerContracts.spcId);
                    $("#provider-contract-no").val(s.providerContracts.contractNo);
                    providerLov();
                    if(s.admissionDate)
                    $("#admission-date").val(moment(s.admissionDate).format('DD/MM/YYYY'));
                    if(s.expectedAdmDate)
                    $("#exp-admission-date").val(moment(s.expectedAdmDate).format('DD/MM/YYYY'));
                    $(".other-clm-details").show();
                    $("#par-trans-no").text(s.parNo);
                    $("#par-rev-no").text(s.parRevisionNo);
                    $("#par-trans-status").text(s.parStatus);
                    $("#par-converted").text(s.converted);
                    $("#med-par-category-pk").val(s.category.id);
                    $("#par-request-amount").text(UTILITIES.currencyFormat(s.totalRequestAmount));
                    $("#par-rej-amount").text(UTILITIES.currencyFormat(s.totalRejectedAmount));
                    if(s.totalApprAmount){
                        if(s.totalApprAmount===0){
                            $(".rejection_letter").show();
                        }
                        else{
                            $(".rejection_letter").hide();
                        }
                    }
                    else{
                        $(".rejection_letter").show();
                    }
                    if(s.totalexgratiaAmount)
                    $("#par-appr-amount").text(UTILITIES.currencyFormat(s.totalApprAmount));
                    else
                        $("#par-appr-amount").text(UTILITIES.currencyFormat(s.totalApprAmount));
                    if(s.categoryMember){
                        $("#member-cat-id-pk").val(s.categoryMember.sectId);
                        $("#clm-member-info").text(s.categoryMember.client.fname+" "+s.categoryMember.client.otherNames);
                        $("#clm-member-dob").text(moment(s.categoryMember.client.dob).format('DD/MM/YYYY'));
                        $("#clm-member-age").text(s.categoryMember.age);
                        $("#clm-member-card").text(s.categoryMember.memberShipNo);
                        $("#clm-member-wef").text(moment(s.categoryMember.wefDate).format('DD/MM/YYYY'));
                        $("#clm-member-wet").text(moment(s.categoryMember.wefDate).format('DD/MM/YYYY'));
                        $("#clm-member-category").text(s.categoryMember.category.desc);
                        showMemberFamilyDetails(s.categoryMember.sectId);
                        if(s.categoryMember.client.gender==="M")
                            $("#clm-member-gender").text("Male");
                        else if(s.categoryMember.client.gender==="F")
                            $("#clm-member-gender").text("Female")
                    }
                },
                error: function(xhr, error){
                    bootbox.alert(xhr.responseText);
                }
            });
        }
        else{
            $("#edit_services").show();
            $(".med_requests").show();
            $("#med_requests_tbl").hide();
            $("#med_services_tbl").hide();
            $(".other-clm-details").hide();
            $("#btn-claim-reports").hide();
            $("#btn-make-ready").hide();
            $("#btn-auth-clm").hide();
            $("#div-convert-clm").hide();
        }
    }
}


function getClaimDocs(){
    var url = "pardocuments";
    var currTable = $('#par_docs_tbl').DataTable( {
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
                "data": "prdId",
                "render": function ( data, type, full, meta ) {
                    if(full.parTrans.parStatus) {
                        if (full.parTrans.parStatus === "Open")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editClmDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editClmDocs(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }
                    else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="editClmDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "prdId",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="downloadClmDoc(this);"><i class="fa fa-file-archive-o"></button>';

                }

            },
            {
                "data": "prdId",
                "render": function ( data, type, full, meta ) {
                    if(full.parTrans.parStatus) {
                        if (full.parTrans.parStatus === "Open")
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClmDoc(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClmDoc(this);" disabled><i class="fa fa-trash-o"></button>';

                    }
                    else
                        return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClmDoc(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );
    return currTable;
}


function deleteClmDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    bootbox.confirm("Are you sure want to delete "+docs['uploadedFileName']+"?", function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deleteclmDoc/' + docs['prdId'],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#par_docs_tbl').DataTable().ajax.reload();
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


function editClmDocs(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    $("#risk-doc-name").text(docs["reqdDocs"].requiredDoc.reqDesc);
    $("#risk-sub-doc-id").val(docs["reqdDocs"].sclReqrdId);
    $("#risk-upload-name").text(docs['uploadedFileName']);
    $("#risk-doc-id").val(docs['prdId']);
    $('#clmdocModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}

function downloadClmDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    window.open(SERVLET_CONTEXT+"/protected/medical/claims/claimdocument/"+docs['prdId'],
        '_blank'
    );
}


function uploadClmDocument(){
    var $form = $("#clm-doc-form");
    var validator = $form.validate();
    $('form#clm-doc-form')
        .submit( function( e ) {
            e.preventDefault();

            if (!$form.valid()) {
                return;
            }
            var data = new FormData( this );
            data.append( 'file', $( '#avatar' )[0].files[0] );
            $.ajax( {
                url: 'uploadRequiredDocs',
                type: 'POST',
                data: data,
                processData: false,
                contentType: false,
                success: function (s ) {
                    Swal.fire({
                        title: 'Success',
                        text: 'File Uploaded Successfully',
                        icon: 'success'
                    });
                    $('#clmdocModal').modal('hide');
                    var $el = $('#avatar');
                    $el.wrap('<form>').closest('form').get(0).reset();
                    $el.unwrap();
                    $('#par_docs_tbl').DataTable().ajax.reload();

                },
                error: function(xhr, error){
                   Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
                }
            });
        });
}



function getCategoryMemberDetails(categoryId){
    $.ajax({
        type: 'GET',
        url:  'getCategoryMemberDetails/' + categoryId,
        dataType: 'json',
        async: true,
        success: function(s) {
            if(s){
                $("#clm-member-info").text(s.client.fname+" "+s.client.otherNames);
                $("#clm-member-client-id").text(s.client.tenantNumber);
                $("#clm-member-dob").text(moment(s.client.dob).format('DD/MM/YYYY'));
                $("#clm-member-age").text(s.age);
                $("#clm-member-card").text(s.memberShipNo);
                $("#clm-member-wef").text(moment(s.wefDate).format('DD/MM/YYYY'));
                $("#clm-member-wet").text(moment(s.wefDate).format('DD/MM/YYYY'));
                $("#clm-member-category").text(s.category.desc);
                showMemberFamilyDetails(s.sectId);
                if(s.client.gender==="M")
                    $("#clm-member-gender").text("Male");
                else if(s.client.gender==="F")
                    $("#clm-member-gender").text("Female")
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {

        }
    });
}


function showMemberFamilyDetails(categoryId){
    $.ajax({
        type: 'GET',
        url:  'getCategoryMemberInfo/' + categoryId,
        dataType: 'json',
        async: true,
        success: function(result) {
            $("#clm-member-family").text(result);
        },
        error: function(jqXHR, textStatus, errorThrown) {

        }
    });
}

function loadClmMembersModal(){
    $("#btn-show-search-clm").on('click', function(){
        populateClientLov();
        populateClientScheme();
        createMemberTrans();
        $('#memberModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });

    $("#btn-search-members").on('click', function(){
        createMemberTrans();

    });


}


function createParTrans(){
    var $form = $('#clm-form');
    var validator = $form.validate();
    $('#btn-save-clm').click(function(){
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
        var url = "saveParRequest";
        var request = $.post(url, data );
        request.success(function(s){
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            validator.resetForm();
            parCode = s;
            populateClaimDetails();
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

function providerLov(){
    if($("#provider-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "provider-frm",
            sort : 'contractNo',
            change: function(e, a, v){
                $("#provider-no").text(e.added.serviceProviders.name);
                $("#provider-id").val(e.added.spcId);
            },
            formatResult : function(a)
            {
                return a.contractNo;
            },
            formatSelection : function(a)
            {
                return a.contractNo;
            },
            initSelection: function (element, callback) {
                var code  = $('#provider-id').val();
                var name = $("#provider-contract-no").val();
                var data = {contractNo:name,spcId:code};
                callback(data);
            },
            id: "spcId",
            width:"250px",
            placeholder:"Select Service Provider Contract"

        });
    }
}

function countryLov(){
    if($("#country").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "country",
            sort : 'couName',
            change:  function(e, a, v){
                $("#country-id").val(e.added.couCode);
            },
            formatResult : function(a)
            {
                return a.couName
            },
            formatSelection : function(a)
            {
                return a.couName
            },
            initSelection: function (element, callback) {
                var code  = $('#country-id').val();
                var name = $("#country-name").val();
                var data = {couName:name,couCode:code};
                callback(data);
            },
            id: "couCode",
            width:"250px",
            placeholder:"Select Country"
        });
    }
}

function activityLov(){
    if($("#activity-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "activity-frm",
            sort : 'benDesc',
            change:  function(e, a, v){
                $("#activity-id").val(e.added.benId);
                $("#txt-benefit-affected").text(e.added.section.desc);
            },
            formatResult : function(a)
            {
                return a.benDesc
            },
            formatSelection : function(a)
            {
                return a.benDesc
            },
            initSelection: function (element, callback) {

            },
            id: "benId",
            width:"250px",
            placeholder:"Select Activity"
        });
    }
}


function activityFrmLov(labId){
    if($("#serv-activity-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "serv-activity-frm",
            sort : 'benDesc',
            change:  function(e, a, v){
                $("#serv-activity-id").val(e.added.benId);
                $("#par-affected-benefit").text(e.added.section.desc);
            },
            formatResult : function(a)
            {
                return a.benDesc
            },
            formatSelection : function(a)
            {
                return a.benDesc
            },
            initSelection: function (element, callback) {
                var code = $("#serv-activity-id").val();
                var name = $("#serv-activity-desc").val();
                var data = {benDesc:name,benId:code};
                callback(data);
            },
            id: "benId",
            width:"250px",
            placeholder:"Select Activity",
            params: {labId: labId}
        });
    }
    if($("#activity-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "activity-frm",
            sort : 'benDesc',
            change:  function(e, a, v){
                $("#activity-id").val(e.added.benId);
                $("#txt-benefit-affected").text(e.added.section.desc);
            },
            formatResult : function(a)
            {
                return a.benDesc
            },
            formatSelection : function(a)
            {
                return a.benDesc
            },
            initSelection: function (element, callback) {
                var code = $("#activity-id").val();
                var name = $("#activity-desc").val();
                var data = {benDesc:name,benId:code};
                callback(data);
            },
            id: "benId",
            width:"250px",
            placeholder:"Select Activity",
            params: {labId: labId}
        });
    }
}

function activityFrmLov2(labId){
    if($("#serv-activity-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "serv-activity-frm",
            sort : 'benDesc',
            change:  function(e, a, v){
                $("#serv-activity-id").val(e.added.benId);
                $("#par-affected-benefit").text(e.added.section.desc);
            },
            formatResult : function(a)
            {
                return a.benDesc
            },
            formatSelection : function(a)
            {
                return a.benDesc
            },
            initSelection: function (element, callback) {
            },
            id: "benId",
            width:"250px",
            placeholder:"Select Activity",
            params: {labId: labId}
        });
    }
    if($("#activity-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "activity-frm",
            sort : 'benDesc',
            change:  function(e, a, v){
                $("#activity-id").val(e.added.benId);
                $("#par-affected-benefit").text(e.added.section.desc);
            },
            formatResult : function(a)
            {
                return a.benDesc
            },
            formatSelection : function(a)
            {
                return a.benDesc
            },
            initSelection: function (element, callback) {
            },
            id: "benId",
            width:"250px",
            placeholder:"Select Activity",
            params: {labId: labId}
        });
    }

}


//function serviceFrmLov(){
  //  if($("#serv-frm").filter("div").html() != undefined)
  //  {
   //     Select2Builder.initAjaxSelect2({
      //      containerId : "serv-frm",
      //      sort : 'labDesc',
        //    change:  function(e, a, v){
        //        $("#serv-id").val(e.added.labId);
         //   },
        //    formatResult : function(a)
         //   {
         //       return a.labDesc
         //   },
        //    formatSelection : function(a)
       //     {
      //          return a.labDesc
      //      },
      //      initSelection: function (element, callback) {
      //      },
        //    id: "labId",
       //     width:"250px",
       //     placeholder:"Select Service"
      //  });
   // }
//}

function diagnosisLov(){
    if($("#diagnosis-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "diagnosis-frm",
            sort : 'baDesc',
            change:  function(e, a, v){
                $("#diagnosis-id").val(e.added.baId);
            },
            formatResult : function(a)
            {
                return a.baDesc
            },
            formatSelection : function(a)
            {
                return a.baDesc
            },
            initSelection: function (element, callback) {

            },
            id: "baId",
            width:"250px",
            placeholder:"Select Diagnosis"
        });
    }
}

function diagnosisFrmLov(){
    if($("#txt-diagnosis-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "txt-diagnosis-frm",
            sort : 'baDesc',
            change:  function(e, a, v){
                $("#txt-diagnosis-id").val(e.added.baId);
            },
            formatResult : function(a)
            {
                return a.baDesc
            },
            formatSelection : function(a)
            {
                return a.baDesc
            },
            initSelection: function (element, callback) {
                var code = $("#txt-diagnosis-id").val();
                var name = $("#txt-diagnosis-desc").val();
                var data = {baDesc:name,baId:code};
                callback(data);
            },
            id: "baId",
            width:"250px",
            placeholder:"Select Diagnosis"
        });
    }
}


function networkLov(){
    if($("#network-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "network-frm",
            sort : 'benDesc',
            change:  function(e, a, v){
                  $("#med-networks").val(e.added.benId);
            },
            formatResult : function(a)
            {
                return a.benDesc
            },
            formatSelection : function(a)
            {
                return a.benDesc
            },
            initSelection: function (element, callback) {
                var code  = $('#med-networks').val();
                var name = $("#med-networks-name").val();
                var data = {benDesc:name,benId:code};
                callback(data);
            },
            id: "benId",
            width:"250px",
            placeholder:"Select Network Code"
        });
    }
}

function eventTypeLov(){
    if($("#event-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "event-frm",
            sort : 'eventDesc',
            change: function(e, a, v){
                $("#events-id").val(e.added.eventId);
            },
            formatResult : function(a)
            {
                return a.eventDesc;
            },
            formatSelection : function(a)
            {
                return a.eventDesc;
            },
            initSelection: function (element, callback) {
                var code  = $('#events-id').val();
                var name = $("#events-name").val();
                var data = {eventDesc:name,eventId:code};
                callback(data);
            },
            id: "eventId",
            width:"250px",
            placeholder:"Select Event Type"

        });
    }
}


function createMemberTrans(){
    var url = "selMembers";
    var currTable = $('#searchMembersTbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
            'data':{
                'clientId': $("#client-id").val(),
                'policyId':  $("#client-pol-id").val(),
                'cardNo':  $("#card-search-number").val(),
                'memberName':  $("#member-search-name").val(),
                'gender':  $("#gender-search-number").val()
            },
        },
        autoWidth: true,
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        searching: false,
        "columns": [
            { "data": "memberShipNo",
                "render": function ( data, type, full, meta ) {
                    return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-members='+encodeURI(JSON.stringify(full)) + ' onclick="selectMember(this);">'+full.memberShipNo+'</button>';
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.tenantNumber;
                }
            },
            { "data": "client",
                "render": function ( data, type, full, meta ) {
                    return full.client.fname + " "+full.client.otherNames;
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

        ]
    } );



    return currTable;
}

function selectMember(button){
    var members = JSON.parse(decodeURI($(button).data("members")));
    $("#member-number").val(members['client'].tenantNumber);
    $("#pol-no").text(members['category'].policy.polNo);
    $("#member-client-id").val(members['client'].tenId);
    $("#member-cat-id-pk").val(members['sectId']);
    $("#policy-id").val(members['category'].policy.policyId);
    $("#med-par-category-pk").val(members['category'].id);
    benefitsLov(members['category'].id);
    $('#memberModal').modal('hide');
}



function benefitsLov(catId){
    if($("#benefits-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "benefits-frm",
            sort : 'sectId',
            change: function(e, a, v){
                $("#benefits-id").val(e.added.sectId);
            },
            formatResult : function(a)
            {
                return a.cover.section.desc;
            },
            formatSelection : function(a)
            {
                return a.cover.section.desc;
            },
            initSelection: function (element, callback) {

            },
            id: "sectId",
            width:"250px",
            placeholder:"Select Service Place",
            params: {catId: catId},

        });
    }
}


function benefitsFrmLov(catId){
    if($("#txt-benefits-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "txt-benefits-frm",
            sort : 'sectId',
            change: function(e, a, v){
                $("#txt-benefits-id").val(e.added.sectId);
            },
            formatResult : function(a)
            {
                return a.cover.section.desc;
            },
            formatSelection : function(a)
            {
                return a.cover.section.desc;
            },
            initSelection: function (element, callback) {
               // var code = $("#txt-benefits-id").val();
               // var name = $("#txt-benefits-desc").val();
               //// var data = {baDesc:name,sectId:code};
               // callback(data);
            },
            id: "sectId",
            width:"250px",
            placeholder:"Select Service Place",
            params: {catId: catId},

        });
    }
}



function createParRequests(parId){
    var url = "requests/"+parId;
    var currTable = $('#med_request_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
        },
        autoWidth: true,
        lengthMenu: [ [6], [6] ],
        pageLength: 6,
        destroy: true,
        searching: false,
        "columns": [
            { "data": "requestDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.requestDate).format('DD/MM/YYYY');
                }
            },
            { "data": "requestType",
                "render": function ( data, type, full, meta ) {
                    if(full.requestType){
                        if(full.requestType ==="D") return "Direct Billing";
                        else if(full.requestType ==="R") return "Reimbursement";
                    }
                }
            },
            { "data": "ailments",
                "render": function ( data, type, full, meta ) {
                    return full.ailments.baDesc;
                }
            },
            { "data": "servicePlace",
                "render": function ( data, type, full, meta ) {
                    if(full.servicePlace){
                        if(full.servicePlace==="IN")
                            return "INPATIENT";
                        else if(full.servicePlace==="OT")
                            return "OUTPATIENT";
                        else if(full.servicePlace==="DC")
                            return "DAYCARE";
                    }
                    else return "";
                }
            },

            { "data": "description" },
            { "data": "requestAmount",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.requestAmount);
                }
            },
            { "data": "approvedAmount",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.approvedAmount);
                }
            },
            { "data": "rejectAmount",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.rejectAmount);
                }
            },
            {
                "data": "reqId",
                "render": function ( data, type, full, meta ) {
                    if(full.parTrans.parStatus) {
                        if (full.parTrans.parStatus === "Open")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-requests=' + encodeURI(JSON.stringify(full)) + ' onclick="editRequest(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-requests=' + encodeURI(JSON.stringify(full)) + ' onclick="editRequest(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                    }
                    }

            },
            {
                "data": "reqId",
                "render": function ( data, type, full, meta ) {
                    if(full.parTrans.parStatus) {
                        if (full.parTrans.parStatus === "Open")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-requests=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRequests(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-requests=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRequests(this);" disabled><i class="fa fa-trash-o"></button>';
                    }
                    }

            },

        ]
    } );


    $('#med_request_tbl tbody').on( 'click', 'tr', function () {

        $(this).addClass('table-primary').siblings().removeClass('table-primary');

        var d = currTable.row( this ).data();
        if(d){
            createParServices(d.reqId);
            $("#par-request-code-pk").val(d.reqId);
        }
    } );


    return currTable;
}


function editRequest(button){
    var requests = JSON.parse(decodeURI($(button).data("requests")));
    $("#req-code").val(requests['reqId']);
    $("#txt-request-date").val(moment(requests['requestDate']).format('DD/MM/YYYY'));
    $("#txt-req-type").val(requests['requestType']);
    $("#txt-diagnosis-id").val(requests['ailments'].baId);
    $("#txt-diagnosis-desc").val(requests['ailments'].baDesc);
    diagnosisFrmLov();
    $("#ben-service-place").val(requests['servicePlace']);
    $("#req-description").val(requests['description']);
    $(".benefits-display").show();
    $(".benefits").hide();
    $('#requestModal').modal({
        backdrop: 'static',
        keyboard: true
    })

}


function deleteRequests(button){
    var requests = JSON.parse(decodeURI($(button).data("requests")));
    bootbox.confirm("Are you sure want to delete Selected Request?", function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deleteRequests/' + requests["reqId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    populateClaimDetails();
                    createParServices(-2000);
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



function createParServices(parId){
    var url = "services/"+parId;
    var currTable = $('#med_service_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
        },
        autoWidth: true,
        lengthMenu: [ [6], [6] ],
        pageLength: 6,
        destroy: true,
        searching: false,
        "columns": [
            { "data": "serviceDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.serviceDate).format('DD/MM/YYYY');
                }
            },
            { "data": "medActivities",
                "render": function ( data, type, full, meta ) {
                    return full.medActivities.benDesc;
                }
            },
            { "data": "reqQuantity" },
            { "data": "reqPrice",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.reqPrice);
                }
            },
            { "data": "reqAmount",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.reqAmount);
                }
            },
            { "data": "appQuantity" },
            { "data": "appPrice",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.appPrice);
                }
            },
            { "data": "appAmount",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.appAmount);
                }
            },
            { "data": "rejAmount",
                "render": function ( data, type, full, meta ) {
                    return UTILITIES.currencyFormat(full.rejAmount);
                }
            },
            { "data": "categoryBenefits",
                "render": function ( data, type, full, meta ) {
                    if(full.categoryBenefits)
                        return UTILITIES.currencyFormat(getLimitAmount(full.categoryBenefits.sectId));
                    else return 0;
                }
            },
            {
                "data": "reqServiceId",
                "render": function ( data, type, full, meta ) {
                    if(full.request.parTrans.parStatus) {
                        if (full.request.parTrans.parStatus === "Open")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-services=' + encodeURI(JSON.stringify(full)) + ' onclick="editRequestService(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-services=' + encodeURI(JSON.stringify(full)) + ' onclick="editRequestService(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                      }
                    }

            },
            {
                "data": "reqServiceId",
                "render": function ( data, type, full, meta ) {
                    if(full.request.parTrans.parStatus) {
                        if (full.request.parTrans.parStatus === "Open")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-services=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRequestServices(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-services=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRequestServices(this);" disabled><i class="fa fa-trash-o"></button>';
                    }
                    }

            },
            {
                "data": "reqServiceId",
                "render": function ( data, type, full, meta ) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-services=' + encodeURI(JSON.stringify(full)) + ' onclick="showLogService(this);"><i class="fa fa-file-archive-o"></button>';

                }

            },
        ]
    } );




    return currTable;
}

function showLogService(button){
    var services = JSON.parse(decodeURI($(button).data("services")));
    createServicesLog(services['reqServiceId']);
    $('#serviceLogModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}

function editRequestService(button){
    var services = JSON.parse(decodeURI($(button).data("services")));
    $("#req-service-code").val(services['reqServiceId']);
    $("#req-service-reg-code").val(services['request'].reqId);
    $("#txt-request-serv-date").val(moment(services['serviceDate']).format('DD/MM/YYYY'));
    $("#serv-activity-id").val(services['medActivities'].benId);
    $("#serv-activity-desc").val(services['medActivities'].benDesc);
    $("#activity-id").val(services['medActivities'].benId);
    $("#activity-desc").val(services['medActivities'].benDesc);
    $("#serv-id").val(services['medActivities'].services.labId);
    $("#serv-desc").val(services['medActivities'].services.labDesc);
    $("#serv-id2").val(services['medActivities'].services.labId);
    $("#serv-desc2").val(services['medActivities'].services.labDesc);
    serviceFrmLov();
    $("#req-service-qty").val(services['reqQuantity']);
    $("#req-service-price").val(services['reqPrice']);
    $("#req-approved-qty").val(services['appQuantity']);
    $("#req-exgratia-amount").val(services['exgratiaAmount']);
    $("#req-approved-price").val(services['appPrice']);
    $("#par-affected-benefit").text(services['medActivities'].section.desc);
    if(services['manualControl']){
        if(services['manualControl']==="Y"){
            $("#req-approved-qty").prop("disabled", false);
            $("#req-approved-price").prop("disabled", false);
            $("#chk-manual-ctrl").prop("checked", true);
            if(services['exgratiaService']){
                if(services['exgratiaService']==="Y"){
                    $("#req-exgratia-amount").prop("disabled", false);
                    $("#req-exgratia-ctrl").prop("checked", true);
                    $("#req-exgratia-ctrl").prop("disabled", false);
                }
                else{
                    $("#req-exgratia-amount").prop("disabled", true);
                    $("#req-exgratia-ctrl").prop("checked", false);
                    $("#req-exgratia-ctrl").prop("disabled", true);
                }
            }
        }
        else {
            $("#req-approved-qty").prop("disabled", true);
            $("#req-exgratia-ctrl").prop("disabled", true);
            $("#req-approved-price").prop("disabled", true);
            $("#chk-manual-ctrl").prop("checked", false);
            $("#req-exgratia-amount").prop("disabled", true);
            $("#req-exgratia-ctrl").prop("checked", false);
        }
    }
    else {
        $("#req-approved-qty").prop("disabled", true);
        $("#req-approved-price").prop("disabled", true);
        $("#chk-manual-ctrl").prop("checked", false);
        $("#req-exgratia-amount").prop("disabled", true);
        $("#req-exgratia-ctrl").prop("checked", false);
    }
    $('#serviceModal').modal({
        backdrop: 'static',
        keyboard: true
    })

}

function deleteRequestServices(button){
    var services = JSON.parse(decodeURI($(button).data("services")));
    bootbox.confirm("Are you sure want to delete Selected Service?", function(result) {
        if(result){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url:  'deleteServices/' + services["reqServiceId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                    // $('#myPleaseWait').modal('hide');
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    populateClaimDetails();
                    $('#med_service_tbl').DataTable().ajax.reload();
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

function getLimitAmount(benId){
    var data = 0;
    $.ajax({
        type: 'GET',
        url:  'remainingLimit/'+benId,
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

//function serviceFrmLov() {
//    if ($("#medicalservice").filter("div").html() != undefined) {
//        Select2Builder.initAjaxSelect2({
  //          containerId: "medicalservice",
   //         sort: 'labDesc',
    //        change: function (e, a, v) {
    //            $("#serv-id").val(e.added.labId);
    //            activityFrmLov2(e.added.labId);
    //        },
     //       formatResult: function (a) {
     //           return a.labDesc
      //      },
     //       formatSelection: function (a) {
     //           return a.labDesc
      //      },
      //      initSelection: function (element, callback) {
      //          var code = $("#serv-id").val();
      //          var name = $("#serv-desc").val();
       //         var data = {labDesc: name, labId: code};
      //          callback(data);
        //        activityFrmLov($("#serv-id").val());
      //      },
        //    id: "labId",
     //       width: "250px",
      //      placeholder: "Select Service"
       // });

   // }
//}

function serviceFrmLov() {
    if ($("#medicalservice").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "medicalservice",
            sort: 'labDesc',
            change: function (e, a, v) {
                $("#serv-id").val(e.added.labId);
                activityFrmLov2(e.added.labId);
            },
            formatResult: function (a) {
                return a.labDesc
            },
            formatSelection: function (a) {
                return a.labDesc
            },
            initSelection: function (element, callback) {
                var code = $("#serv-id").val();
                var name = $("#serv-desc").val();
                var data = {labDesc: name, labId: code};
                callback(data);
                activityFrmLov($("#serv-id").val());
            },
            id: "labId",
            width: "250px",
            placeholder: "Select Service"
        });

    }
    if ($("#medicalservice2").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "medicalservice2",
            sort: 'labDesc',
            change: function (e, a, v) {
                $("#serv-id2").val(e.added.labId);
                activityFrmLov2(e.added.labId);
            },
            formatResult: function (a) {
                return a.labDesc
            },
            formatSelection: function (a) {
                return a.labDesc
            },
            initSelection: function (element, callback) {
                var code = $("#serv-id2").val();
                var name = $("#serv-desc2").val();
                var data = {labDesc: name, labId: code};
                callback(data);
                activityFrmLov($("#serv-id2").val());
            },
            id: "labId",
            width: "250px",
            placeholder: "Select Service"
        });

    }
}


function createServicesLog(serviceId){
    var url = "servicesLog/"+serviceId;
    var currTable = $('#med_service_log_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        "ajax": {
            'url': url,
        },
        autoWidth: true,
        lengthMenu: [ [6], [6] ],
        pageLength: 6,
        destroy: true,
        searching: false,
        "columns": [
            { "data": "logShtDesc" },
            { "data": "logDesc" },
        ]
    } );




    return currTable;
}

