/**
 * Created by peter on 3/8/2017.
 */

var mckClaimNo;
var UTILITIES = UTILITIES || {};
$(function() {

    $(document).ready(function () {


        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });

        $(document).ajaxStart(function () {
            $("#saveClmActivity,#saveReqClmDocsBtn").text('Saving....');
            $("#saveClmActivity,#saveReqClmDocsBtn").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#saveClmActivity,#saveReqClmDocsBtn").text('Save');
            $("#saveClmActivity,#saveReqClmDocsBtn").attr("disabled", false);
        });
        $("#btn-approve-claim").on('click', function (){
            approveTask();
        });

        $("#btn-reject-claim").on('click', function (){
            rejectTask();
        });

        getClaimDetails();
        getClaimClaimants();
         getClaimPerils();
        getClmRequiredDocs();
       getClaimActivities();
        getClaimAuditLogs();
        getClaimStatuses();
        getClaimUploads();
        uploadDoc();
         uploadClaimDocument();
        selectClmActivity();
        saveClaimActivity();
        selectNextReviewLov();
        selectProviderTypesLov();
        addClaimant();
        addSelectedPeril();
        saveClaimant();
        addClaimants();
        savePerilPayment();
        saveClaimPeril();
        getPerilsPayment(-2000);
        UTILITIES.emailReports();
        saveClaimStatus();
        uploadClaimReqDocument();
        saveRiskDocsList();
        populatePaymentModes();
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

        $("#self-as-clmnt2").val( "");
        $('#self-as-clmnt').change(function(){
            if(this.checked){
                $("#clmant-def").select2("readonly", true);
                $("#self-as-clmnt2").val( "on");
            }
            else{
                $("#clmant-def").select2("readonly", false);
                $("#self-as-clmnt2").val( "off");
            }

        });

        $("#clm_activities_tbl").on('click', '.btn-ready', function(){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var data = $(this).closest('tr').find('#readyId').val();

            makeReadyRevision(data);

        })
        $("#clm_activities_tbl").on('click', '.btn-undo', function(){
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var data = $(this).closest('tr').find('#undoreadyId').val();

            undoMakeReadyTrans(data);
        })
        $("#clm_activities_tbl").on('click', '.btn-authorise', function(){

            var data = $(this).closest('tr').find('#authoriseId').val();

            authoriseRevision(data);
        })


    });
});
var populatePaymentModes = function () {
    if ($("#pm-mode-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "pm-mode-frm",
            sort: 'pmDesc',
            change: function (e, a, v) {
                // Add null check and support for both Select2 3.x and 4.x
                if (e.added && e.added.pmId) {
                    $("#pm-id").val(e.added.pmId);
                } else if (e.params && e.params.data && e.params.data.pmId) {
                    $("#pm-id").val(e.params.data.pmId);
                }
            },
            formatResult: function (a) {
                return a.pmDesc;
            },
            formatSelection: function (a) {
                return a.pmDesc;
            },
            initSelection: function (element, callback) {
                var code = $('#pm-id').val();
                var name = $("#pm-name").val();
                var data = {pmDesc: name, pmId: code};
                callback(data);
            },
            id: "pmId",
            width: "250px",
            placeholder: "Select Payment Mode"
        });
    }
};
function rejectTask(){
    // Display the modal
    $('#rejectionModal').modal('show');

    $('#rejectConfirmButton').off('click').on('click', function() {
        var reason = $('#rejectionReason').val();

        if (reason) {
            $.ajax({
                url: SERVLET_CONTEXT + '/protected/users/rejectTask',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    taskId: mckClaimNo,
                    reason: reason
                }),
                success: function(response) {
                    window.location.href = SERVLET_CONTEXT + "/protected/home";

                },
                error: function (xhr, status, error) {
                    console.error("Error approving task: ", error);
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText || "Something went wrong during rejection.",
                        icon: 'error'
                    });
                }
            });
        } else {
            console.error("No reason provided for rejection");
        }
        // Hide the modal after submission
        $('#rejectionModal').modal('hide');
    });
}
function approveTask() {
    $('#approvalModal').modal('show');
    $('#approveConfirmButton').off('click').on('click', function () {

        $('#btn-approve-claim').prop('disabled', true);
        $('#btn-reject-claim').prop('disabled', true);

         Swal.fire({
                title: 'Processing...',
                text: 'Please wait while the task is being approved.',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });


        // All good → proceed
        $.ajax({
            url: SERVLET_CONTEXT + '/protected/users/approveTask',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(mckClaimNo),
            success: function () {
                window.location.href = SERVLET_CONTEXT + "/protected/home";
            },
            error: function (xhr) {
                console.error("Error approving task:", xhr);
                Swal.fire({
                    title: 'Error',
                    text: xhr.responseText || "Something went wrong during approval.",
                    icon: 'error'
                });
//                $('#btn-approve-claim').prop('disabled', true);
//                $('#btn-reject-claim').prop('disabled', true);
            },
            complete: function () {
                $('#btn-approve-claim').prop('disabled', false);
                $('#btn-reject-claim').prop('disabled', false);
            }
        });
        // close modal
        $('#approvalModal').modal('hide');
    });
}


function makeReadyRevision(id){
    var url='makeRevTransReady/'+id;
    $.ajax({
        type: 'GET',
        url: url,
    }).done(function (s) {
        // $('#myPleaseWait').modal('hide');
        Swal.fire({
            title: 'Success',
            text: 'Transaction Made Ready Successfully',
            icon: 'success'
        });
        $("#clm_activities_tbl").DataTable().ajax.reload();
    }).fail(function (xhr, error) {
        // $('#myPleaseWait').modal('hide');
         Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
    });
}
function undoMakeReadyTrans(id){
    var url='undoRevTransReady/'+id;
    $.ajax({
        type: 'GET',
        url: url,
    }).done(function (s) {
        // $('#myPleaseWait').modal('hide');
        Swal.fire({
            title: 'Success',
            text: 'Transaction Ready Undone Successfully',
            icon: 'success'
        });
        $("#clm_activities_tbl").DataTable().ajax.reload();
    }).fail(function (xhr, error) {
        // $('#myPleaseWait').modal('hide');
         Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
    });
}
function authoriseRevision(id){
    var url='authoriseRevision/'+id;
    bootbox.confirm("Are you sure want to authorise this Transaction?", function (result) {
        if (result) {
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url: url,
            }).done(function (s) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Transaction Authorised Successfully',
                    icon: 'success'
                });
                $("#clm_activities_tbl").DataTable().ajax.reload();
                getClaimDetails();
            }).fail(function (xhr, error) {
                // $('#myPleaseWait').modal('hide');
                 Swal.fire({
                title: 'Error',
                text: xhr.responseText,
                icon: 'error'
            });
            });
        }
    })

}

function selectRiskPerils(riskId){
    if($("#peril-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "peril-def",
            sort : 'perilDesc',
            change: function(e, a, v){
                $("#peril-code").val(e.added.bindPerilCode);
                $("#peril-name").val(e.added.perilDesc);
            },
            formatResult : function(a)
            {
                console.log(a);
                return a.perilDesc;
            },
            formatSelection : function(a)
            {
                return a.perilDesc;
            },
            initSelection: function (element, callback) {

            },
            id: "bindPerilCode",
            params: {riskId: riskId},
            placeholder:"Select Peril"
        });
    }
}
function saveClaimant(){
    $('#saveClaimantDef').click(function(){
        var $classForm = $('#claimants-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createClaimant";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Success',
                text: 'Record created Successfully',
                icon: 'success'
            });
            $('#claimant-tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#claimants-form').find("input[type=text],input[type=number],input[type=hidden],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#claimants-form').select2('val', null);
            $('#claimantsModal').modal('hide');
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
function addSelectedPeril(){
    $("#btn-add-peril-selected").on('click', function(){
        var $classForm = $('#new-peril-form');
        var validator = $classForm.validate();
        if($("#peril-code").val()==''){
            bootbox.alert("Select Peril");
            return;
        }
        if (!$classForm.valid()) {
            return;
        }
        var bindPeril = $("#peril-code").val();
        var riskId = $("#curr-risk-id").val();
        getExpiringSection(bindPeril, riskId);

        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createClaimantPeril";
        var request = $.post(url, data );
        request.success(function(){
            // $('#myPleaseWait').modal('hide');
             Swal.fire({
                title: 'Success',
                text: 'Record created Successfully',
                icon: 'success'
            });
            $('#clm_claimant_tbl').DataTable().ajax.reload();
            validator.resetForm();
            $('#new-peril-form').find("input[type=text],input[type=number],input[type=hidden],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#new-peril-form').select2('val', null);
            $("#self-as-clmnt").prop('checked', false);
            $("#peril-def").select2('val', null);
            $("#clm-estimate").val('');
            $("#peril-code").val('');
            $("#peril-name").val('');
            $('#perilTransModal').modal('hide');
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
function selectProvidersByTypeLov(){
    if($("#binder-provider-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "binder-provider-frm",
            sort : 'serviceProviders.name',
            change: function(e,a,v){
                $("#provider-code").val(e.added.spcId);
                $("#provider-desc").val(e.added.providerName);
            },
            formatResult : function(a)
            {
                return a.providerName
            },
            formatSelection : function(a)
            {
                return a.providerName
            },
            initSelection: function (element, callback) {
                var code = $("#provider-code").val();
                var name = $("#provider-desc").val();
                var data = {providerName:name,spcId:code};
                callback(data);
            },
            id: "spcId",
            params: {bindCode: $("#binder-code").val(),typeCode: $("#provider-type").val()},
            placeholder:"Select Provider Type"
        });
    }
}
function selectProviderTypesLov(){
    console.log("testing="+$("#binder-code").val());
    if($("#providerType-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "providerType-frm",
            sort : 'shtDesc',
            change: function(e,a,v){
                $("#provider-type").val(e.added.id);
                $("#provider-type-desc").val(e.added.desc);
                selectProvidersByTypeLov();
            },
            formatResult : function(a)
            {
                return a.desc
            },
            formatSelection : function(a)
            {
                return a.desc
            },
            initSelection: function (element, callback) {
                var code = $("#provider-type").val();
                var name = $("#provider-type-desc").val();
                var data = {desc:name,id:code};
                callback(data);
            },
            id: "id",
            params: {bindCode: $("#binder-code").val()},
            placeholder:"Select Provider Type"
        });
    }
}

function selectNextReviewLov(){
    if($("#review-user").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "review-user",
            sort : 'username',
            change: function(e,a,v){
                $("#next-rev-user").val(e.added.id);
                $("#next-rev-user-desc").val(e.added.username);
            },
            formatResult : function(a)
            {
                return a.username
            },
            formatSelection : function(a)
            {
                return a.username
            },
            initSelection: function (element, callback) {
                var code = $("#next-rev-user").val();
                var name = $("#next-rev-user-desc").val();
                var data = {username:name,id:code};
                callback(data);
            },
            id: "id",
            placeholder:"Select Next Review User"
        });
    }
}
function selectClaimantLov(){
    if($("#clmant-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clmant-def",
            sort : 'surname',
            change: function(e,a,v){
                $("#clmt-code").val(e.added.claimantId);
                $("#clmt-name").val(e.added.surname+" "+ e.added.otherNames);

            },
            formatResult : function(a)
            {
                return a.surname+" "+ a.otherNames;
            },
            formatSelection : function(a)
            {
                return a.surname+" "+ a.otherNames;
            },
            initSelection: function (element, callback) {

            },
            id: "claimantId",
            placeholder:"Select Claimant"
        });
    }
}

function addClaimant(){
    $("#add-peril-btn").on('click',function(){
        $('#new-peril-form').find("input[type=text],input[type=mobileNumber],input[type=hidden],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $('#new-peril-form').find("input[type=checkbox]").attr("checked", false);
        selectClaimantLov();
        $("#clm-estimate").number( true, 2 );
        $('#perilTransModal').modal({
            backdrop : 'static',
            keyboard : true
        })
    });
}

function saveClaimActivity(){
    $("#btn-add-activity").click(function(){
        $('#clm-act-form').find("input[type=text] ,input[type=hidden],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");

        $("#clm-activity").val('');

        $("#clm-activity").val('');
        $(".activitydropdown").show();
        $(".activitydescrption").hide();

        $("#claim-ref-section").hide();
        $("#insurer-ref").removeAttr('required');
        $(".claim-ref-required").hide();

        $('#activityModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    });

    $('#clm-activity').on('change', function() {
        var selectedActivityCode = $("#activity-code").val();

        // Show claim reference field ONLY for "Assign Claim Reference" activity
        if (selectedActivityCode === '122617') {
            // Show and make required for "Assign Claim Reference"
            $("#claim-ref-section").show();
            $("#insurer-ref").attr('required', 'required');
            $(".claim-ref-required").show();
            console.log("Showing claim ref field for Assign Claim Reference activity");
        } else {
            // Hide for all other activities
            $("#claim-ref-section").hide();
            $("#insurer-ref").removeAttr('required');
            $(".claim-ref-required").hide();
            $("#insurer-ref").val(''); // Clear the value
            console.log("Hiding claim ref field for activity: " + selectedActivityCode);
        }
    });

    $("#saveClmActivity").click(function(){
        var $currForm = $('#clm-act-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }
        var data = {};
        $currForm.serializeArray().map(function(x){data[x.name] = x.value;});

        var selectedActivityCode = $("#activity-code").val();
        if (selectedActivityCode !== '122617') {
            delete data.insurerRef; // Remove insurerRef for all other activities
            console.log("Removed insurerRef from data for activity: " + selectedActivityCode);
        } else {
            // For "Assign Claim Reference", ensure it's not empty
            if (!data.insurerRef || data.insurerRef.trim() === '') {
                Swal.fire({
                    title: 'Error',
                    text: 'Insurer Reference is required when assigning claim reference',
                    icon: 'error'
                });
                return;
            }
            console.log("Including insurerRef: " + data.insurerRef);
        }

        var url = "createClmActivity";
        var request = $.post(url, data );
        request.success(function(){
            if (selectedActivityCode === '122617' && $("#insurer-ref").val()) {
                $("#insurer-ref-no").text($("#insurer-ref").val());
            }
            // Clear the form fields
            $('#clm-act-form').find("input[type=text], input[type=hidden], input[type=number], input[type=mobileNumber], input[type=emailFull], input[type=password], textarea, select").val("");
            $("#clm-activity").select2("val", ""); // Clear select2 dropdown

            Swal.fire({
                title: 'Success',
                text: 'Activity Created Successfully',
                icon: 'success'
            });
            $('#clm_activities_tbl').DataTable().ajax.reload();
            $('#activityModal').modal('hide');
        });
        request.error(function(jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
    })
}

function savePerilPayment(){
    $("#saveClmPayment").click(function(){
        var $currForm = $('#clm-pay-form');
        var currValidator = $currForm.validate();
        if($("#peril-code1").val()==''){
            bootbox.alert("Select Peril");
            return;
        }
        if (!$currForm.valid()) {
            return;
        }
        var data = {};
        $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createPerilPayment";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Payment Created Successfully',
                icon: 'success'
            });
            $('#clm_claimant_tbl').DataTable().ajax.reload();
            $('#clm_perils_tbl').DataTable().ajax.reload();
            $('#clm_perils_pymnt_tbl').DataTable().ajax.reload();

            $('#paymentModal').modal('hide');
        });
        request.error(function(jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
    })
}



function saveClaimPeril(){
    $("#btn-add-peril").click(function(){
        $('#clm-peril-form').find("input[type=text] ,input[type=hidden],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");

        $("#reserve-amount").number( true, 2 );
        $('#claimPerilModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });
    $("#saveClmPeril").click(function(){
        var $currForm = $('#clm-peril-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }
        var data = {};
        $currForm.serializeArray().map(function(x){data[x.name] = x.value;});
        var url = "createClaimPeril";
        var request = $.post(url, data );
        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Revision Created/Updated Successfully',
                icon: 'success'
            });
            $('#clm_claimant_tbl').DataTable().ajax.reload();
            $('#clm_perils_tbl').DataTable().ajax.reload();
            $('#clm_perils_pymnt_tbl').DataTable().ajax.reload();
            $('#clm_activities_tbl').DataTable().ajax.reload();

            $('#claimPerilModal').modal('hide');
        });
        request.error(function(jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
    })
}


function saveClaimStatus(){
    $("#btn-clm-status").click(function(){
        $('#clm-status-form').find("input[type=text] ,input[type=hidden],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $('#pm-mode-frm').val(null).trigger('change');
        if ($("#clm-status1").val()==="O" || $("#clm-status1").val()==="B"){
            $("#closedStatus").show();
            // $("#edit-payment-mode").closest('.form-group').show();
            $("#clm-settlement-frm").show();
            $("#saveClmStatus").html('Close Claim') ;
            $("#new-status").val('C');

        }
        if ($("#clm-status1").val()==="R") {
            $("#closedStatus").show();
            $("#edit-payment-mode").closest('.form-group').show();
            $("#clm-settlement-frm").show();
            $("#saveClmStatus").html('Close Claim') ;
            $("#new-status").val('C');
        }
        if ($("#clm-status1").val()==="C") {
            $("#closedStatus").hide();
            $("#edit-payment-mode").closest('.form-group').hide();
            $("#clm-settlement-frm").hide();
            $("#dv-fields-section").hide();
            $("#saveDvPartial").hide();
            // $("#activity-date-picker").closest('.form-group').hide();
            $("#saveClmStatus").html('Re-open Claim') ;
            $("#new-status").val('R');
        }
        $('#claimStatusModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });
    $('#claimStatusModal').on('shown.bs.modal', function () {
        populatePaymentModes(); // Initialize the Select2

        $('#activity-date-picker').datetimepicker();
        $('#dv-issuance-date-picker').datetimepicker();
        $('#dv-execution-date-picker').datetimepicker();
        $('#dv-acceptance-date-picker').datetimepicker();
        $('#final-settlement-date-picker').datetimepicker();

        // Initialize currency formatting
        $('.currency-input').on('input', function() {
            var value = $(this).val().replace(/[^\d.]/g, '');
            if (value) {
                var formatted = parseFloat(value).toLocaleString('en-US', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                });
                $(this).val(formatted);
            }
        });
        // loadExistingDvData();
    });

    function handleCloseReasonChange() {
        var closeReason = $('#clsStatus').val();

        // Hide all conditional sections first
        $('#edit-payment-mode').hide();
        $('#clm-settlement-frm').hide();
        $('#dv-fields-section').hide();
        $('#saveDvPartial').hide();

        if (closeReason === 'ST') {
            $('#edit-payment-mode').hide();
            $('#activity-date-picker').closest('.form-group').show();
            $('#clm-settlement-frm').hide();
            $('#clm-remarks').val('');
        } else if (closeReason === 'DV') {
            $('#dv-fields-section').show();
            $('#activity-date-picker').closest('.form-group').hide();
            $("#edit-payment-mode").closest('.form-group').show();
            $('#saveDvPartial').show();
            loadExistingDvData();
        } else if (closeReason === 'NC' || closeReason === 'RC' || closeReason === 'CD') {
            $('#activity-date-picker').closest('.form-group').show();
            $('#clm-remarks').val('');
        }else{
            $('#clm-remarks').val('');
        }

    }

// Call on modal show
    $('#claimStatusModal').on('shown.bs.modal', function () {
        handleCloseReasonChange();

    });

// Call on dropdown change
    $('#clsStatus').on('change', handleCloseReasonChange);



    function handleClientDecisionChange() {
        if ($('#client-decision').val() === 'DECLINED') {
            $('#decline-reason-section').show();
            $('#dv-execution-date').show();
            $('#dv-acceptance-date').hide();
            $("#edit-payment-mode").closest('.form-group').hide();
        }else if ($('#client-decision').val() === 'ACCEPTED') {
            $("#edit-payment-mode").closest('.form-group').show();
            $('#decline-reason-section').hide();
            $('#dv-execution-date').hide();
            $('#dv-acceptance-date').show();
        }
    }

    $('#client-decision').on('change', handleClientDecisionChange);

// Also call this when the modal is shown
    $('#claimStatusModal').on('shown.bs.modal', function () {
        handleClientDecisionChange();
    });

    // Save DV Progress handler - matching the format
    $("#saveDvPartial").click(function(){
        var $currForm = $('#clm-status-form');
//        var currValidator = $currForm.validate();
//        if (!$currForm.valid()) {
//            return;
//        }

        var data = {};
        $currForm.serializeArray().map(function(x){
            if ((x.name === 'dvExecutionDate' || x.name === 'dvIssuanceDate' || x.name === 'finalSettlementDate' || x.name === 'activityDate' || x.name === 'dvAcceptanceDate')
                && (!x.value || x.value.trim() === '')) {
                return; // Skip empty date fields
            }
            if (x.name === 'dvOfferAmount' && x.value) {
                data[x.name] = x.value.replace(/[^\d.]/g, '');
            } else {
                data[x.name] = x.value || ''; // Include empty fields with default empty string
            }
        });
        data.claimId = $("#claim-id").val();


        var url = "saveDvProgress";
        var request = $.post(url, data);

        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'DV progress saved successfully',
                icon: 'success'
            });
            $('#claimStatusModal').modal('hide');
            // Don't close modal or reload everything - just refresh claim details
            getClaimDetails();
        });

        request.error(function(jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
    });

    $("#saveClmStatus").click(function(){
        var $currForm = $('#clm-status-form');
        var closeReason = $('#clsStatus').val();


        // if (closeReason === 'ST') { // Closed As Settled
        //     var isValid = true;
        //     var errorMessage = '';
        //
        //     // Check if payment mode is selected
        //     var paymentModeId = $("#pm-id").val();
        //     console.log('Payment mode ID from hidden field:', paymentModeId);
        //
        //     if (!paymentModeId || paymentModeId.trim() === '') {
        //         isValid = false;
        //         errorMessage += 'Payment Mode is required for settled claims.\n';
        //     }
        //
        //     // Check if settlement type is filled
        //     var settlementType = $("#settlementType").val().trim();
        //     if (!settlementType) {
        //         isValid = false;
        //         errorMessage += 'Type of Settlement is required for settled claims.\n';
        //     }

            // Show error if validation fails
        //     if (!isValid) {
        //         Swal.fire({
        //             title: 'Validation Error',
        //             text: errorMessage,
        //             icon: 'warning'
        //         });
        //         return;
        //
        // }

        // Form validation
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x){

            if ((x.name === 'dvExecutionDate' || x.name === 'dvIssuanceDate' || x.name === 'finalSettlementDate' || x.name === 'activityDate' || x.name === 'dvAcceptanceDate')
                && (!x.value || x.value.trim() === '')) {
                return;
            }


            if (x.name === 'dvOfferAmount' && x.value) {
                data[x.name] = x.value.replace(/[^\d.]/g, '');
            } else {
                data[x.name] = x.value;
            }
        });

        // Add claim ID
        data.claimId = $("#claim-id").val();

        console.log('Form data:', data);

        var url = "createClmStatus";
        var request = $.post(url, data);

        request.success(function(){
            Swal.fire({
                title: 'Success',
                text: 'Status Changed Successfully',
                icon: 'success'
            });
            getClaimDetails();
            getClaimClaimants();
            getClmRequiredDocs();
            getClaimActivities();
            getClaimUploads();
            getClaimAuditLogs();

            $('#claimStatusModal').modal('hide');
        });

        request.error(function(jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
    })
}
function loadExistingDvData() {


    // Use the global clmId variable like getClaimDetails() does
    if (typeof clmId !== 'undefined' && clmId !== -2000) {
        $.ajax({
            url: 'getClaimDvData',  // Same pattern as getClaimDetails
            type: 'GET',
            processData: false,
            contentType: false,
            async: true,
            success: function(data) {
                console.log("Full response:", data);
                console.log("DV Data:", data.dvData);

                if (data && data.dvData) {
                    // Format dates for input fields (dates come as timestamps)
                    var dvIssuanceDate = data.dvData.dvIssuanceDate ? formatDateForInput(data.dvData.dvIssuanceDate) : '';
                    var dvExecutionDate = data.dvData.dvExecutionDate ? formatDateForInput(data.dvData.dvExecutionDate) : '';
                    var finalSettlementDate = data.dvData.finalSettlementDate ? formatDateForInput(data.dvData.finalSettlementDate) : '';

                    // Format currency amount
                    var offerAmount = data.dvData.dvOfferAmount ? parseFloat(data.dvData.dvOfferAmount).toLocaleString('en-US', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    }) : '';

                    // Prefill DV fields with existing data
                    $("#dv-issuance-date").val(dvIssuanceDate);
                    $("#dv-offer-amount").val(offerAmount);
                    $("#client-decision").val(data.dvData.clientDecision || '');
                    $("#decline-reason").val(data.dvData.declineReason || '');
                    $("#dv-execution-date").val(dvExecutionDate);
                    // $("#dv-acceptance-date").val(dvExecutionDate);
                    $("#final-settlement-date").val(finalSettlementDate);
                    $("#clm-remarks").val(data.dvData.remarks);
                    if (data.dvData.paymentMode) {

                        $("#pm-id").val(data.dvData.paymentMode.pmId);

                        // Initialize Select2 with existing data
                        var paymentModeData = {
                            pmId: data.dvData.paymentMode.pmId,
                            pmDesc: data.dvData.paymentMode.pmDesc
                        };

                        // Force Select2 to show the selected value
                        var $select = $("#pm-mode-frm");
                        $select.select2("data", paymentModeData);
                    }

                    // Show/hide decline reason based on client decision
                    if (data.dvData.clientDecision === 'DECLINED') {
                        $('#decline-reason-section').show();
                    } else {
                        $('#decline-reason-section').hide();
                    }


                }
            },
            error: function(xhr, status, error) {
                console.log("Error loading DV data:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }
}

// // Function to load existing DV data
// function loadExistingDvData() {
//     console.log("loadExistingDvData called");
//     var claimId = $("#claim-id").val();
//     if (!claimId) return;
//
//     $.get("getClaimDvData", { claimId: claimId })
//         .done(function(data) {
//             console.log("Full response:", data);
//             console.log("DV Data:", data.dvData);
//
//             if (data && data.dvData) {
//                 // Format dates for input fields (dates come as timestamps)
//                 var dvIssuanceDate = data.dvData.dvIssuanceDate ? formatDateForInput(data.dvData.dvIssuanceDate) : '';
//                 var dvExecutionDate = data.dvData.dvExecutionDate ? formatDateForInput(data.dvData.dvExecutionDate) : '';
//                 var finalSettlementDate = data.dvData.finalSettlementDate ? formatDateForInput(data.dvData.finalSettlementDate) : '';
//
//                 // Format currency amount
//                 var offerAmount = data.dvData.dvOfferAmount ? parseFloat(data.dvData.dvOfferAmount).toLocaleString('en-US', {
//                     minimumFractionDigits: 2,
//                     maximumFractionDigits: 2
//                 }) : '';
//
//                 // Prefill DV fields with existing data
//                 $("#dv-issuance-date").val(dvIssuanceDate);
//                 $("#dv-offer-amount").val(offerAmount);
//                 $("#client-decision").val(data.dvData.clientDecision || '');
//                 $("#decline-reason").val(data.dvData.declineReason || '');
//                 $("#dv-execution-date").val(dvExecutionDate);
//                 $("#final-settlement-date").val(finalSettlementDate);
//
//                 // Show/hide decline reason based on client decision
//                 if (data.dvData.clientDecision === 'DECLINED') {
//                     $('#decline-reason-section').show();
//                 } else {
//                     $('#decline-reason-section').hide();
//                 }
//
//                 console.log("Pre-filled values:", {
//                     dvIssuanceDate: dvIssuanceDate,
//                     offerAmount: offerAmount,
//                     clientDecision: data.dvData.clientDecision
//                 });
//             }
//         })
//         .fail(function(xhr, status, error) {
//             console.log("Error loading DV data:", error);
//             console.log("Response:", xhr.responseText);
//         });
// }

// Helper function to format dates for input fields
function formatDateForInput(dateValue) {
    if (!dateValue) return '';

    var date = new Date(dateValue);
    var day = String(date.getDate()).padStart(2, '0');
    var month = String(date.getMonth() + 1).padStart(2, '0');
    var year = date.getFullYear();

    return day + '/' + month + '/' + year; // Adjust format based on your datepicker
}
$('#claimStatusModal').on('shown.bs.modal', function () {
    populatePaymentModes(); // Initialize the Select2

    // Initialize new datepickers
    $('#activity-date-picker').datetimepicker();
    $('#dv-issuance-date-picker').datetimepicker();
    $('#dv-execution-date-picker').datetimepicker();
    $('#final-settlement-date-picker').datetimepicker();

    // Initialize currency formatting
    $('.currency-input').on('input', function() {
        var value = $(this).val().replace(/[^\d.]/g, '');
        if (value) {
            var formatted = parseFloat(value).toLocaleString('en-US', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            });
            $(this).val(formatted);
        }
    });

    // Load existing DV data if any
    if ($('#clsStatus').val() === 'DV') {
        loadExistingDvData();
    }else {
        // Optionally clear remarks for other close reasons
        $('#clm-remarks').val('');
    }
});










function selectClmActivity(){
    if($("#clm-activity").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clm-activity",
            sort : 'activityDesc',
            change: function(e,a,v){
                $("#activity-code").val(e.added.caId);
                $("#activity-desc").val(e.added.activityDesc);
            },
            formatResult : function(a)
            {
                return a.activityDesc
            },
            formatSelection : function(a)
            {
                return a.activityDesc
            },
            initSelection: function (element, callback) {
                var code = $("#activity-code").val();
                var name = $("#activity-desc").val();
                var data = {activityDesc:name,caId:code};
                callback(data);
            },
            id: "caId",
            placeholder:"Select Activity"
        });
    }
}


function uploadClaimDocument(){
    var $form = $("#clm-doc-form");
    var validator = $form.validate();
    $('form#clm-doc-form')
        .submit( function( e ) {
            e.preventDefault();
            if (!$form.valid()) {
                return;
            }
            var data = new FormData( this );
            data.append( 'file', $( '#file' )[0].files[0] );
            $.ajax( {
                url: 'uploadClaimDocument',
                type: 'POST',
                data: data,
                processData: false,
                contentType: false,
                success: function (s ) {
                     Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            });
                    $('#clm_req_docs_tbl').DataTable().ajax.reload();
                    $('#clm-doc-form').find("input[type=text],input[type=hidden],input[type=mobileNumber],input[file],input[type=email],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
                    //$('#file').fileinput('reset');
                    $("#file").val('');
                    $('#uploadClmModal').modal('hide');

                },
                error: function(jqXHR, textStatus, errorThrown){
                    Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
                }
            });
        });
}

function uploadClaimReqDocument(){
    $('form#clm-req-form').submit( function( e ) {
        var $form = $("#clm-req-form");
        var validator = $form.validate();
        var $button = $("#upload-claimDoc-btn");
        var $spinner = $('#upload-spinner');

        e.preventDefault();
        if (!$form.valid()) {
            return;
        }
         // Disable the button & show spinner
        $button.prop('disabled', true);
        $spinner.show();

        var data = new FormData( this );
        data.append( 'file', $( '#file' )[0].files[0] );

        $.ajax( {
            url: 'uploadClaimReqDocument',
            type: 'POST',
            data: data,
            processData: false,
            contentType: false,
            success: function (s ) {
                 Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });

                $('#clm_required_docs_tbl').DataTable().ajax.reload();

                $('#clm-req-form').find("input[type=text],image,input[type=hidden],input[type=mobileNumber],input[file],input[type=email],input[type=password],input[type=hidden],input[type=number], textarea,select").val("");
                //$('#file').fileinput('reset');
                $("#file").val('');
                $('#uploadClmReqModal').modal('hide');



            },
            error: function(jqXHR, textStatus, errorThrown){
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            },
            complete: function () {
                // Always re-enable and hide spinner
                $spinner.hide();
                $button.prop('disabled', false);
            }
        });
    });
}

function uploadDoc(){
    $("#btn-add-clmdocs").on('click', function(){
        searchReqDocs("");
    });

    $("#searchDocuments").on('click', function (){
        console.log('here...');
        $.ajax({
            type: 'GET',
            url:  'getClmReqDocs',
            dataType: 'json',
            data: {"docName":$("#doc-name-search").val()},
            async: true,
            success: function(result) {
                console.log(result);
                $("#clmReqDocsModal tbody").each(function(){$(this).remove();});
                for(var res in result){
                    var markup = "<tr><td><input type='checkbox' name='record' " +
                        "id='"+result[res].sclReqrdId+"'></td><td>" + result[res].reqShtDesc + "</td><td>"
                        + result[res].reqDesc + "</td></tr>";
                    $("#risksReqDocsTbl").append(markup);
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
            }
        });
    })


    $("#btn-add-upload").on('click', function(){
        if(typeof clmId!== 'undefined') {
            if (clmId !== -2000) {
                $("#upload-clm-id").val(clmId);
                $('#uploadClmModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            }
        }

    });

}


function uploadReqDoc(button) {

    var requiredDocs = JSON.parse(decodeURI($(button).data("reqdocs")));
    $("#uploadreq-code").val(requiredDocs["clmRequiredId"]);
    $("#trans-type").val("N");
    $(".uploadfile").show();
    $('#uploadClmReqModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}

function editReqDoc(button) {

    var requiredDocs = JSON.parse(decodeURI($(button).data("reqdocs")));
    $("#uploadreq-code").val(requiredDocs["clmRequiredId"]);
    $("#trans-type").val("E");
    $(".uploadfile").hide();
    $('#uploadClmReqModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}



function editClaimActivity(button) {
    var act = JSON.parse(decodeURI($(button).data("activity")));
    $("#activity-pk").val(act["activityId"]);
    $("#activity-code").val(act["activity"].caId);
    $("#activity-desc").val(act["activity"].activityDesc);
    $("#activity-descrption").val(act["activity"].activityDesc);


    $("#net-review-date").val(moment(act["remDate"]).format('DD/MM/YYYY'))
    $("#next-rev-user").val(act["reviewUser"].id);
    $("#next-rev-user-desc").val(act["reviewUser"].username);
    if (act["serviceProvider"]) {
        $("#provider-type").val(act["serviceProvider"].serviceProviders.serviceProviderTypes.id);
        $("#provider-type-desc").val(act["serviceProvider"].serviceProviders.serviceProviderTypes.desc);
        $("#provider-code").val(act["serviceProvider"].spcId);
        $("#provider-desc").val(act["serviceProvider"].serviceProviders.name);
    }
    selectNextReviewLov();
    selectClmActivity();
    selectProviderTypesLov();
    selectProvidersByTypeLov();
    $(".activitydropdown").hide();
    $(".activitydescrption").show();

    $("#clm-activity").attr('disabled',true);
    $('#activityModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}



function getClaimUploads(){
    var url = "getClaimUploads";
    var currTable = $('#clm_req_docs_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "fileId"
            },
            { "data": "fileName"
            },

            { "data": "dateUploaded",
                "render": function ( data, type, full, meta ) {
                    return moment(full.dateUploaded).format('DD/MM/YYYY');
                }
            },
            { "data": "uploadedBy",
                "render": function ( data, type, full, meta ) {

                    return full.uploadedBy;
                }
            },

            { "data": "uploadedComment"
            },
            {
                "data": "uploadId",
                "render": function ( data, type, full, meta ) {

                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="downloadRiskDoc(this);"><i class="fa fa-file-archive-o"></button>';

                }

            },
            {
                "data": "uploadId",
                "render": function ( data, type, full, meta ) {
                    if(full.claimStatus==='C')
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClmUploadDoc(this);" disabled><i class="fa fa-trash-o"></button>';
                    else
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-docs='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClmUploadDoc(this);"><i class="fa fa-trash-o"></button>';

                }

            },
        ]
    } );
    return currTable;
}

function downloadRiskDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));
    window.open(SERVLET_CONTEXT+"/protected/claims/clmdocument/"+docs['uploadId'],
        '_blank'
    );
}


function downloadClmReqDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("reqdocs")));
    window.open(SERVLET_CONTEXT+"/protected/claims/clmreqdocument/"+docs['clmRequiredId'],
        '_blank'
    );
}



function getClaimActivities(){
    var url = "getClaimActivities";
    var currTable = $('#clm_activities_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10], [10] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "activityId",
                "render": function ( data, type, full, meta ) {
                    return full.activityDesc
                }
            },
            { "data": "userCreated",
                "render": function ( data, type, full, meta ) {

                    return full.username;
                }
            },
            { "data": "activityDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.activityDate).format('DD/MM/YYYY');
                }
            },
            { "data": "currentActivity",
                "render": function ( data, type, full, meta ) {
                    if(full.currentActivity){
                        if(full.currentActivity === "Y"){
                            return "Yes"
                        }
                        else return "No";
                    }
                    else
                        return "No";
                }
            },
            {
                "data": "activityNotes",
                "render": function (data, type, full, meta) {
                    if (!data || data.trim() === '') {
                        return '<span class="text-muted">No notes</span>';
                    }

                    const lines = data.split('\n').filter(line => line.trim() !== '');
                    const shortened = lines.map(line => {
                        // From: [2025-08-15 16:43:22 by Admin] notes
                        // To: 16:43 - notes
                        return '• ' + line.replace(/\[2025-\d{2}-\d{2} (\d{2}:\d{2}):\d{2} by [^\]]+\]\s*/g, '$1 - ');
                    }).join('<br>');

                    return '<div>' + shortened + '</div>';
                }
            },

            { "data": "remDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.remDate).format('DD/MM/YYYY');
                }
            }
        ]
    } );
    return currTable;
}
function getClaimAuditLogs(){
    var url = "getClaimAuditLogs";
    var currTable = $('#clm_logs_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: false,
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [10], [10] ],
        pageLength: 10,
        destroy: true,
        "columns": [
            { "data": "eventType",
                "render": function ( data, type, full, meta ) {
                    return full.eventDescription;
                }
            },
            { "data": "eventUserId",
                "render": function ( data, type, full, meta ) {
                    return full.username;
                }
            },
            { "data": "eventDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.eventDate).format('DD/MM/YYYY HH:mm');
                }
            },
            { "data": "action",
                "render": function ( data, type, full, meta ) {
                    if(full.action === "A") return "Approved";
                    if(full.action === "R") return "Rejected";
                    if(full.action === "SUBMITTED") return "Submitted";
                    return full.eventType.replace(/_/g, ' ');
                }
            },
            { "data": "RejectedReason",
                "render": function ( data, type, full, meta ) {
                return full.rejectedReason;


                }
            },
            { "data": "resubmissionReason",
                "render": function ( data, type, full, meta ) {
                    return full.resubmissionComment;


                }
            },

        ]
    } );
    return currTable;
}



function getClaimStatuses(){
    var url = "getClaimStatuses";
    var currTable = $('#clm_status_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "order": [[ 3, "desc" ],[ 2, "desc" ]],
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "clmStatusId",
                "render": function ( data, type, full, meta ) {
                    console.log(full);
                    return full.currentStatus ==='O'?'Open':full.currentStatus ==='C'?'Closed':full.currentStatus ==='R'?'Re-Opened':'';
                }
            },
            { "data": "capturedBy",
                "render": function ( data, type, full, meta ) {

                    return full.capturedBy.username;
                }
            },
            { "data": "dateCaptured",
                "render": function ( data, type, full, meta ) {
                    return moment(full.dateCaptured).format('DD/MM/YYYY');
                }
            },
            { "data": "currentActivity",
                "render": function ( data, type, full, meta ) {
                    if(full.currentActivity){
                        if(full.currentActivity === "Y"){
                            return "Yes"
                        }
                        else return "No";
                    }
                    else
                        return "No";
                }
            },
            { "data": "remarks"
            },

        ]
    } );
    return currTable;
}


function getClmRequiredDocs() {
    var url = "getClmRequiredDocs";
    var currTable = $('#clm_required_docs_tbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [[5], [5]],
        pageLength: 5,
        destroy: true,
        "columns": [
            {
                "data": "clmRequiredId",
                "render": function (data, type, full, meta) {
                    return full.docName
                }
            },
            // {
            //     "data": "docRefNo"
            // },
            {
                "data": "fileName"
            },
            {
                "data": "userReceived",
                "render": function (data, type, full, meta) {
                    if (full.username)
                        return full.username;
                    else return "";
                }
            },
            {
                "data": "dateReceived",
                "render": function (data, type, full, meta) {
                    if (full.dateReceived) {
                        return moment(full.dateReceived).format('YYYY-MM-DD HH:mm:ss');
                    } else return "";

                }
            },
            {
                "data": "remarks"
            },
            {
                "data": "clmRequiredId",
                "render": function (data, type, full, meta) {
                    if (full.claimStatus === 'C' || full.dateReceived)
                        return '<button type="button" class="btn btn-success  btn-sm" disabled>Upload</button>';
                    else
                        return '<button type="button" class="btn btn-success btn-sm " data-reqdocs=' + encodeURI(JSON.stringify(full)) + ' onclick="uploadReqDoc(this);" data-toggle="tooltip" data-placement="top" title="Upload Document">Upload</button>';
                }

            },
            {
                "data": "clmRequiredId",
                "render": function (data, type, full, meta) {

                    return '<button type="button" class="btn btn-info btn-sm" data-reqdocs=' + encodeURI(JSON.stringify(full)) + ' onclick="downloadClmReqDoc(this);" data-toggle="tooltip" data-placement="top" title="View Upload Document">View</button>';
                }

            },
            // {
            //     "data": "clmRequiredId",
            //     "render": function (data, type, full, meta) {
            //         if (full.claimStatus === 'C')
            //             return '<button type="button" class="btn btn-success btn-sm"  disabled><i class="fa fa-pencil-square-o"></button>';
            //         else
            //             return '<button type="button" class="btn btn-success btn-sm"  data-reqdocs=' + encodeURI(JSON.stringify(full)) + ' onclick="editReqDoc(this);" data-toggle="tooltip" data-placement="top" title="Edit Document Details"><i class="fa fa-pencil-square-o"></button>';
            //     }
            //
            // },
            {
                "data": "clmRequiredId",
                "render": function (data, type, full, meta) {
                    if (full.claimStatus === 'C')
                        return '<button type="button" class="btn btn-danger btn-sm" disabled><i class="fa fa-trash-o"></button>';
                    else
                        return '<button type="button" class="btn btn-danger btn-sm" data-reqdocs=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteClmReqDoc(this);" data-toggle="tooltip" data-placement="top" title="Delete Document"><i class="fa fa-trash-o"></button>';
                }

            }
        ]
    });
    return currTable;
}


function getPerilsPayment(claimantId){
    var url = "getClaimPayments/"+claimantId;
    var currTable = $('#clm_perils_pymnt_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        searching: false,
        "autoWidth": false,
        "deferRender": true,
        "ajax": {
            'url': url
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: false,
        "columns": [
            {  "data": "payee",
                "width": "20%"
            },
            { "data": "reference",
                "render": function ( data, type, full, meta ) {
                    return full.reference;
                }
            },
            { "data": "paymentMode",
                "render": function ( data, type, full, meta ) {
                    return full.paymentMode;
                }
            },
            {   "data": "transType",
                "width": "22%",
                "render": function ( data, type, full, meta ) {
                   if(full.transType && full.transType==='CP'){
                       return "Claim Payment";
                   }
                   else  if(full.transType && full.transType==='SP'){
                       return "Service Provider Payment";
                   }
                }
            },
            { "data": "currency",
                "width": "15%",
                "render": function ( data, type, full, meta ) {
                    return full.currency;
                }
            },
            { "data": "amount",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.amount);
                }
            },
            {   "data": "status",
                "width": "20%",
                "render": function ( data, type, full, meta ) {
                    if(full.status && full.status==='N'){
                        return "Not Authorised";
                    }
                    else  if(full.status && full.status==='R'){
                        return "Ready";
                    }
                    else  if(full.status && full.status==='Y'){
                        return "Authorised";
                    }
                }
            },
            { "data": "raisedBy",
                "render": function ( data, type, full, meta ) {
                    return full.raisedBy;
                }
            },
            { "data": "raisedDate",
                "render": function ( data, type, full, meta ) {
                    return moment(full.raisedDate).format('DD/MM/YYYY');
                }
            },
            { "data": "authDate",
                "render": function ( data, type, full, meta ) {
                if(full.authDate)
                    return moment(full.authDate).format('DD/MM/YYYY');
                else return "";
                }
            },
            { "data": "authBy",
                "render": function ( data, type, full, meta ) {
                    return full.authBy;
                }
            },
        ]
    } );
    return currTable;
}

function getClaimPerils(){
    var url = "getClaimPerils";
    var currTable = $('#clm_perils_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        "columns": [
            { "data": "clmPerilId",
                "render": function ( data, type, full, meta ) {
                    return full.perilDesc;

                }
            },
            { "data": "type",
                "render": function ( data, type, full, meta ) {
                    if(full.type ==="RS"){
                        return "Risk Sum Insured";
                    }
                    else if(full.type ==="SS"){
                        return "Section SI/Limit";
                    }
                    else if(full.type ==="PL"){
                        return "Peril Limit";
                    }
                    else if(full.type ==="UL"){
                        return "Unlimited";
                    }
                    else if(full.type ==="EX"){
                        return "Extension";
                    }
                    else if(full.type ==="GT"){
                        return "GPA Total/Temp Disability";
                    }
                    else if(full.type ==="PD"){
                        return "Permanent Disability";
                    }
                    else if(full.type ==="WT"){
                        return "Workmen Total/Temp Disability";
                    }
                    else
                        return full.type;
                }
            },
            { "data": "limitAmt",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.limitAmt);
                }
            },
            { "data": "excessAmt",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.excessAmt);
                }
            },{ "data": "reserve",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.reserve);
                }
            },
            { "data": "remarks",
                "render": function ( data, type, full, meta ) {
                    return full.remarks;
                }
            },
            {
                "data": "clmPerilId",
                "render": function ( data, type, full, meta ) {
                    if($("#clm-status").val()==='Closed')
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-perils='+encodeURI(JSON.stringify(full)) + ' onclick="editClaimPeril(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                    else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-perils='+encodeURI(JSON.stringify(full)) + ' onclick="editClaimPeril(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
        ]
    } );

    $('#clm_perils_tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {
        }
        else{
            console.log("aData[0].clmPerilId"+aData[0].clmPerilId);
            $("#peril-code1").val(aData[0].clmPerilId);
            getPerilsPayment(aData[0].clmPerilId);
        }
    } );
    return currTable;
}

function editPerilPayment(button){
    var payment = JSON.parse(decodeURI($(button).data("payment")));
    $("#payment-id").val(payment["clmPymntId"]);
    $("#peril-pk").val(payment["claimPerils"].clmPerilId);
    $("#payee-name").val(payment["payee"]);
    $("#pay-ref").val(payment["pymntRef"]);
    $("#payment-type").val(payment["pymntType"]);
    $("#pay-date").val(moment(payment["pymntDate"]).format('DD/MM/YYYY'));
    $("#paid-amount").number( true, 2 );
    $("#paid-amount").val(payment["clmPymntAmount"]);
    $('#paymentModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function editClaimPeril(button){
    var perils = JSON.parse(decodeURI($(button).data("perils")));
    $("#peril-id").val(perils["clmPerilId"]);
    $("#peril-desc").val(perils["perilDesc"]);
    $("#remarks-desc").val(perils["remarks"]);
    $("#reserve-amount").number( true, 2 ).val(perils["reserve"]);
    $('#claimPerilModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}


function getClaimStatusHistory(){
    getClaimStatuses();
    $('#claimStatusHistModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}

function getClaimClaimants(){
    var url = "getClmClaimants";
    var currTable = $('#clm_claimant_tbl').DataTable( {
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        searching: false,
        "deferRender": true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [ [5], [5] ],
        pageLength: 5,
        destroy: true,
        scrollCollapse: true,
        "columns": [
            { "data": "claimantId",
                "render": function ( data, type, full, meta ) {
                console.log(full);

                    if(full.thirdParty==="T") {
                        return full.tpClaimant;
                    }
                    else  if(full.thirdParty==="S")
                        return full.selfClaimant;
                    else{
                        return full.selfClaimant;
                    }
                }
            },
            { "data": "claimantId",
                "render": function ( data, type, full, meta ) {
                    if(full.thirdParty==="T")
                        return "Third Party";
                    else if(full.thirdParty==="S")
                        return "Insured" || "Service Provider";
                    else
                        return "Self";
                }
            },
            { "data": "claimantId",
                "render": function ( data, type, full, meta ) {
                    return full.peril;

                }
            },
            { "data": "limitAmt",
                "render": function ( data, type, full, meta ) {

                    return UTILITIES.currencyFormat(full.estimatedAmount);
                }
            },
            { "data": "claimantId",
                "render": function ( data, type, full, meta ) {
                if(full.createdDate)
                    return moment(full.createdDate).format('DD/MM/YYYY');
                }
            },
            { "data": "claimantId",
                "render": function ( data, type, full, meta ) {
                        return full.createdBy;
                }
            },
            {
                "data": "claimantId",
                "render": function ( data, type, full, meta ) {
                    if($("#clm-status").val()==='Closed')
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-claimants='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClaimants(this);" disabled><i class="fa fa-trash-o"></button>';
                    else
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-claimants='+encodeURI(JSON.stringify(full)) + ' onclick="deleteClaimants(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    } );

    $('#clm_claimant_tbl tbody').on( 'click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null){

        }
        else{
            $('#clm_perils_pymnt_tbl').DataTable().ajax.url( "getClaimPayments/"+aData[0].claimantId ).load();
        }
    } );
    return currTable;
}

function deleteClaimants(button){
    var claimants = JSON.parse(decodeURI($(button).data("claimants")));
    var claimantName;
    if(claimants["claimant"]){
        claimantName=claimants["claimant"].otherNames+' '+claimants["claimant"].surname;
    }else {
        claimantName=claimants["client"].fname+' '+ claimants["client"].otherNames;
    }
    bootbox.confirm("Are you sure want to delete "+claimantName+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteClaimClaimant/' + claimants["claimantId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#clm_claimant_tbl').DataTable().ajax.reload();
                   // $('#clm_perils_tbl').DataTable().ajax.reload();
                    //$('#clm_perils_pymnt_tbl').DataTable().ajax.reload();
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

function deleteClaimantPeril(button){
    var perils = JSON.parse(decodeURI($(button).data("perils")));

    bootbox.confirm("Are you sure want to delete "+perils["perilsDef"].perilDesc+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteClaimantPeril/' + perils["clmPerilId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#clm_claimant_tbl').DataTable().ajax.reload();
                    $('#clm_perils_tbl').DataTable().ajax.reload();
                    $('#clm_perils_pymnt_tbl').DataTable().ajax.reload();
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

function deletePerilPayment(button){
    var payment = JSON.parse(decodeURI($(button).data("payment")));

    bootbox.confirm("Are you sure want to delete payment for "+payment["claimPerils"].perilsDef.perilDesc+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deletePerilPayment/' + payment["clmPymntId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                   Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
                    $('#clm_claimant_tbl').DataTable().ajax.reload();
                    $('#clm_perils_tbl').DataTable().ajax.reload();
                    $('#clm_perils_pymnt_tbl').DataTable().ajax.reload();
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

function deleteClmReqDoc(button){
    var reqdocs = JSON.parse(decodeURI($(button).data("reqdocs")));

    bootbox.confirm("Are you sure want to delete "+reqdocs["docName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteClmReqDoc/' + reqdocs["clmRequiredId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Document Deleted Successfully',
                        icon: 'success'
                    });
                    $('#clm_required_docs_tbl').DataTable().ajax.reload();
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

function deleteClmUploadDoc(button){
    var docs = JSON.parse(decodeURI($(button).data("docs")));

    bootbox.confirm("Are you sure want to delete "+docs["fileName"]+"?", function(result) {
        if(result){
            $.ajax({
                type: 'GET',
                url:  'deleteClmUploadDoc/' + docs["uploadId"],
                dataType: 'json',
                async: true,
                success: function(result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Document Deleted Successfully',
                        icon: 'success'
                    });

                    $('#clm_req_docs_tbl').DataTable().ajax.reload();
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
        $('#claimants-form').find("input[type=text],input[type=hidden],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $('#claimants-form').select2('val', null);
        selectOccupationLov();
        $('#claimantsModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    });
}

var mckClaimNo = null;

function getClaimDetails() {
    if (typeof clmId !== 'undefined') {
        if (clmId !== -2000) {
            $.ajax( {
                url: 'getClaimDetails/'+clmId,
                type: 'GET',
                processData: false,
                contentType: false,
                async: true,
                success: function (s ) {
                    $("#myRiskId").val(s.clmRiskId || "");
                    $("#claim-no").text(s.claimNo || "N/A");
                    $("#client-name").text(s.client || "N/A");
                    $("#loss-desc").text(s.lossDesc || "N/A");
                    $("#risk-identifier").text(s.riskIdentifier);

                    // Set mckClaimNo with fallback to clmId if taskId is null or undefined
                    mckClaimNo = (s.taskId !== null && s.taskId !== undefined) ? s.taskId : clmId;
                    $("#approvalTaskId").val(mckClaimNo);


                    if (s.createdBy === s.currentUser.username) {
                        $("#btn-approve-claim").hide();
                        $("#btn-reject-claim").hide();
                } else {
                        if (s.approvalStatus === 'A') {
                            $("#btn-approve-claim").hide();
                            $("#btn-reject-claim").hide();
                            $("btn-clm-status").show();
                        } else if (s.approvalStatus === 'R') {
                            $("#btn-approve-claim").hide();
                            $("#btn-reject-claim").hide();
                            $("btn-clm-status").hide();
                        } else if (s.approvalStatus === 'N') {
                            $("#btn-approve-claim").show();
                            $("#btn-reject-claim").show();
                            $("btn-clm-status").hide();
                        }
                    }
                    if (s.claimStatus){
                        $("#clm-status1").val(s.claimStatus);
                        if (s.claimStatus==="O" || s.claimStatus==="B"){
                            $("#clm-status").text("Open");
                            $("#btn-clm-status").prop('value','Close Claim') ;
                            $("#add-peril-btn").show();
                            $("#btn-add-payment").show();
                            $("#btn-add-upload").show();
                            $("#btn-add-clmdocs").show();
                            $("#btn-add-activity").show();
                        }
                        if (s.claimStatus==="R") {
                            $("#clm-status").text("Re-Open");
                            $("#btn-clm-status").prop('value','Close Claim') ;
                            $("#add-peril-btn").show();
                            $("#btn-add-payment").show();
                            $("#btn-add-upload").show();
                            $("#btn-add-clmdocs").show();
                            $("#btn-add-activity").show();
                        }
                        if (s.claimStatus==="C") {
                            $("#clm-status").text("Closed");
                            $("#btn-clm-status").prop('value','Re-open Claim') ;
                            $("#add-peril-btn").hide();
                            $("#btn-add-payment").hide();
                            $("#btn-add-upload").hide();
                            $("#btn-add-clmdocs").hide();
                            $("#btn-add-activity").hide();

                        }
                    }

                    $("#loss-date").text(moment(s.lossDate).format('DD/MM/YYYY'));
                    $("#notific-date").text(moment(s.notificationDate).format('DD/MM/YYYY'));
                    $("#bkd-date").text(moment(s.bookedDate).format('DD/MM/YYYY'));
                    $("#next-rev-date").text(moment(s.nextRvwDate).format('DD/MM/YYYY'));
                   // $("#insurer-date").text(moment(s.insurerDate).format('DD/MM/YYYY'));
                    $("#liab-admission").text((s.liabilityAdmission)?'Yes':'No');
                    $("#causation-desc").text(s.causation);
                    // $("#insurance-co").text(s.risk.policy.agent.name);
                    // $("#insurer-pol-no").text(s.risk.policy.clientPolNo);
                    $("#pol-no").text(s.policyNo);
                    $("#insurer").text(s.insuranceName);
                    $("#insurer-ref-no").text(s.refNo);
                    $("#insured").text(s.insured);
                    $("#product").text(s.product);
                    $("#risk-id").text(s.riskId);
                    $("#curr-risk-id").val(s.clmRiskId);
                    $("#balance-approver").text(s.balanceApprovedBy);



                    $('#client-balance').text(UTILITIES.currencyFormat(s.clientBalance));
                    $('#insurer-balance').text(UTILITIES.currencyFormat(s.insBalance));
                    $('#risk-value').text(UTILITIES.currencyFormat(s.riskValue));
                    $('#reserve-total').text(UTILITIES.currencyFormat(s.totalReserve));
                    $('#payments-total').text(UTILITIES.currencyFormat(s.totalPayments));
                    $('#outstanding-total').text(UTILITIES.currencyFormat(s.ostReserve));
                    $("#risk-wef").text(moment(s.riskWef).format('DD/MM/YYYY'));
                    $("#risk-wet").text(moment(s.riskWet).format('DD/MM/YYYY'));
                    selectRiskPerils(s.clmRiskId);

                    if(s.riskBindId){
                        $("#binder-code").val(s.riskBindId);
                    } else {
                        $("#binder-code").val(s.policyBindId);
                    }
                    if (s.clientBalance > 0 || s.insBalance > 0) {
                        $("#balance-section").show();
                    } else {
                        $("#balance-section").hide();
                    }


                    console.log("taskId:", s.taskId)

                },
                error: function(xhr, error){
                    bootbox.alert(xhr.responseText);
                }
            });

        }
    }
}
var searchReqDocs= function(search){
    $.ajax({
        type: 'GET',
        url:  'getClmReqDocs',
        dataType: 'json',
        data: {"docName":search},
        async: true,
        success: function(result) {
            $("#clmReqDocsModal tbody").each(function(){$(this).remove();});
            for(var res in result){
                var markup = "<tr><td><input type='checkbox' name='record' " +
                    "id='"+result[res].sclReqrdId+"'></td><td>" + result[res].reqShtDesc + "</td><td>"
                    + result[res].reqDesc + "</td></tr>";
                $("#risksReqDocsTbl").append(markup);
            }
            $("#req-risk-code").val($("#risk-code-pk").val());
            $('#clmReqDocsModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        },
        error: function(jqXHR, textStatus, errorThrown) {
             Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        }
    });
}

var saveRiskDocsList= function(){
    var arr = [];
    $("#saveReqClmDocsBtn").click(function(){
        $("#risksReqDocsTbl tbody").find('input[name="record"]').each(function(){
            if($(this).is(":checked")){
                arr.push($(this).attr("id"));
            }
        });
        if(arr.length==0){
            bootbox.alert("No Documents Selected to attach..");
            return;
        }

        var $currForm = $('#req-clm-docs-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function(x) { data[x.name] = x.value;});
        var url = "createClaimReqDocs";
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
                $('#clm_required_docs_tbl').DataTable().ajax.reload();
                $('#clmReqDocsModal').modal('hide');
                arr = [];
            },
            error : function(jqXHR, textStatus, errorThrown) {
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

function getExpiringSection(peril,risk) {
    $.ajax({
        type: 'GET',
        url: 'getExpireRiskSection/' + peril + '/' + risk,
        processData: false,
        contentType: false,
        async: false,
        success: function (result) {
            for (var res in result) {
                $("#expire-section-id").val(result[res].sectId);
                $("#expire-section").val(result[res].section);
            }
        },
        error: function (xhr, error) {
            bootbox.alert(xhr.responseText);
        }
    });
}