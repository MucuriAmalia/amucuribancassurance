/**
 * Created by peter on 3/5/2017.
 */

$(function() {

    $(document).ready(function () {

        $(".datepicker-input").each(function() {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });

        if($("#loss-date").val() != ''){
            var dt = moment($("#loss-date").val()).format('DD/MM/YYYY');
            selectRiskLov(dt);
        }

        const today = moment(); // Current moment

        // Initialize datetimepicker on the wrapper div
        $('#not-date').datetimepicker({
            format: 'DD/MM/YYYY',
            useCurrent: false // prevents default overwrite
        });

        // Set current date AFTER initialization
        $('#not-date').data('DateTimePicker').date(today);

        // Update hidden field when checkbox changes
        $('#balance-confirm').change(function() {
            $('#balanceApprovedField').val($(this).is(':checked'));
        });

        // Also update when form is submitted (just in case)
        $('form').submit(function() {
            $('#balanceApprovedField').val($('#balance-confirm').is(':checked'));
        });

        //Checkbox validation
        $('form').submit(function(e) {
            if ($('#balance-checkbox-container').is(':visible') && !$('#balance-confirm').is(':checked')) {
                e.preventDefault();

                // Use bootbox for consistent styling
                bootbox.alert('Please acknowledge the existing balance on this policy before proceeding.');

                // Optionally focus on the checkbox
                $('#balance-confirm').focus();

                return false;
            }

            // Update hidden field before submission
            $('#balanceApprovedField').val($('#balance-confirm').is(':checked'));
            return true;
        });

        // Update hidden field when checkbox changes
        $('#balance-confirm').change(function() {
            $('#balanceApprovedField').val($(this).is(':checked'));
        });
    });
    $(document).ajaxStart(function () {
        $("#saveReqClmDocsBtn").text('Saving....');
        $("#saveReqClmDocsBtn").attr("disabled", true);
    });
    $(document).ajaxComplete(function () {
        $("#saveClmActivity,#saveReqClmDocsBtn").text('Save');
        $("#saveClmActivity,#saveReqClmDocsBtn").attr("disabled", false);
    });

        selectClaimantLov();
        changeLossDate();
        selectNextReviewLov();
        selectClmActivity();
        addClmPeril();
        addSelectedPeril();
        uploadClaimDocument();
        uploadClaimReqDocument();
        uploadDoc();
        getClmRequiredDocs();
        getClaimUploads();
        saveRiskDocsList();
    $('#perilTransModal').on('hidden.bs.modal', function () {
        // Reset the form
        $('#new-peril-form')[0].reset();

        // Clear hidden fields
        $('#clmt-code').val('');
        $('#clmt-name').val('');
        $('#peril-code').val('');
        $('#peril-name').val('');

        // Clear Select2 components
        $('#clmant-def').select2('val', '');
        $('#peril-def').select2('val', '');

        // Clear the display text for Select2 components
        $('#clmant-def').empty();
        $('#peril-def').empty();

        // Explicitly clear other inputs
        $('#clm-estimate').val('');
        $('#self-as-clmnt').prop('checked', false);


        $('#self-as-clmnt').trigger('change');
    });

        $('#self-as-clmnt').change(function(){
            if(this.checked){
                $("#clmant-def").select2("readonly", true);
            }
            else{
                $("#clmant-def").select2("readonly", false);
            }

        });


    function cleanupEmptyDateFields() {
        // List of optional date field IDs
        var optionalDateFields = [
            '#insurer-date',
            '#next-rev-date',
        ];

        optionalDateFields.forEach(function(fieldId) {
            var field = $(fieldId);
            if (field.length && field.val().trim() === '') {
                field.removeAttr('name');
            }
        });
    }



        const $form = $('#claimForm');
        const $saveDraftBtn = $('#saveDraftBtn');
        const $submitBtn = $('#submitClaimFormBtn');
        const $updateBtn = $('#updateClaimBtn');

        let isDraftSaved = false;
        const isEditMode = $('input[name="editMode"]').val() === 'true';
        if (isEditMode) {

            initializeEditMode();
            $saveDraftBtn.hide();
            $('.document-section').show();
            $('#btn-add-clmdocs').prop('disabled', false).removeClass('btn-secondary').addClass('btn-info');


            $updateBtn.on('click', function(e) {
                e.preventDefault();

                if (!$form[0].checkValidity()) {
                    $form[0].reportValidity();
                    return;
                }
                $('#adjustmentNotes').val('');
                $('#adjustmentNotesModal').modal('show');

                // $(this).prop('disabled', true);
                //
                // Swal.fire({
                //     title: 'Updating Claim...',
                //     text: 'Updating claim and resubmitting for approval...',
                //     allowOutsideClick: false,
                //     allowEscapeKey: false,
                //     didOpen: () => {
                //         Swal.showLoading();
                //     }
                // });
                //
                // $form.submit();
            });

        } else {

            $saveDraftBtn.on('click', function (e) {
                e.preventDefault();
                cleanupEmptyDateFields();

                if (!$form[0].checkValidity()) {
                    $form[0].reportValidity();
                    return;
                }

                $saveDraftBtn.prop('disabled', true).addClass('btn-processing');

                Swal.fire({
                    title: 'Saving Draft...',
                    text: 'Creating claim draft...',
                    allowOutsideClick: false,
                    allowEscapeKey: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });

                $.ajax({
                    url: 'saveDraftClaim',
                    type: 'POST',
                    data: $form.serialize(),
                    success: function (response) {
                        Swal.fire({
                            title: 'Draft Saved!',
                            text: 'Claim saved as draft. You can now upload documents.',
                            icon: 'success'
                        });

                        isDraftSaved = true;

                        $('#btn-add-clmdocs').prop('disabled', false);
                        $('#btn-add-clmdocs').removeClass('btn-secondary').addClass('btn-info');

                        // Keep the processing state and update button text to show success
                        $saveDraftBtn.val('Draft Saved ✓').removeClass('btn-info').addClass('btn-success');
                        $submitBtn.prop('disabled', false);

                        $('.document-section').show();
                    },
                    error: function (xhr) {
                        // On error, remove processing state and re-enable button
                        $saveDraftBtn.removeClass('btn-processing').prop('disabled', false);

                        let errorMessage = 'Failed to save draft';
                        try {
                            const response = JSON.parse(xhr.responseText);
                            if (response && response.message) {
                                errorMessage = response.message;
                            }
                        } catch (e) {
                        }
                        Swal.fire({
                            title: 'Error',
                            text: errorMessage,
                            icon: 'error'
                        });
                    },
                    complete: function () {
                    }
                });
            });

            $submitBtn.on('click', function (e) {
                e.preventDefault();

                if (!isDraftSaved) {
                    Swal.fire({
                        title: 'Save Draft First',
                        text: 'Please save as draft first before submitting.',
                        icon: 'warning'
                    });
                    return;
                }
                let hasAtLeastOneDocument = false;
                $('#clm_required_docs_tbl tbody tr').each(function () {
                    const fileName = $(this).find('td:eq(1)').text().trim();
                    if (fileName) {
                        hasAtLeastOneDocument = true;
                        return false;
                    }
                });

                if (!hasAtLeastOneDocument) {
                    Swal.fire({
                        title: 'No Documents Uploaded',
                        text: 'Please upload supporting documents before submitting the claim.',
                        icon: 'warning'
                    });
                    return;
                }

                // Add processing state to button
                $submitBtn.prop('disabled', true).addClass('btn-processing');

                Swal.fire({
                    title: 'Submitting...',
                    text: 'Submitting claim for approval...',
                    allowOutsideClick: false,
                    allowEscapeKey: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });

            const form = $('<form>', {
                    method: 'POST',
                    action: 'submitClaimForApproval'
                });

                $('body').append(form);
                form.submit();
            });


            $('#btn-add-clmdocs').prop('disabled', true);
            $('#btn-add-clmdocs').addClass('btn-secondary').removeClass('btn-info');
            $submitBtn.prop('disabled', true);
            $('.document-section').hide();
        }
    });
    function initializeEditMode() {
        console.log('Initializing edit mode');

        $('#saveDraftBtn').hide();
        $('.document-section').show();
        $('#btn-add-clmdocs').prop('disabled', false).removeClass('btn-secondary').addClass('btn-info');

        loadExistingPerils();

        $('#confirmAdjustmentBtn').on('click', function(e) {
            e.preventDefault();
            const adjustmentNotes = $('#adjustmentNotes').val().trim();
            if (!adjustmentNotes) {
                Swal.fire({
                    title: 'Notes Required',
                    text: 'Please enter adjustment notes before submitting.',
                    icon: 'warning'
                });
                return;
            }
            $(this).prop('disabled', true);
            let $adjustmentNotesField = $('input[name="resubmissionComment"]');
            if ($adjustmentNotesField.length === 0) {
                $adjustmentNotesField = $('<input type="hidden" name="resubmissionComment">');
                $('#claimForm').append($adjustmentNotesField);
            }
            $adjustmentNotesField.val(adjustmentNotes);


            $('#adjustmentNotesModal').modal('hide');


            Swal.fire({
                title: 'Updating Claim...',
                text: 'Updating claim and resubmitting for approval...',
                allowOutsideClick: false,
                allowEscapeKey: false,
                didOpen: () => { Swal.showLoading(); }
            });

            $('#claimForm').submit();
        });


        $('#adjustmentNotesModal').on('hidden.bs.modal', function () {
            $('#confirmAdjustmentBtn').prop('disabled', false);
        });
    }

function addClmPeril(){
    $("#add-peril-btn").on('click',function(){
        $('#new-peril-form').find("input[type=text],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden],input[type=number], textarea").val("");
        $('#new-peril-form').find("input[type=checkbox]").attr("checked", false);
        selectClaimantLov();
        $("#clm-estimate").number( true, 2 );
        $('#perilTransModal').modal({
            backdrop : 'static',
            keyboard : true
        })
    });
}
function loadExistingPerils() {
    var claimId = $('input[name="claimId"]').val();

    if (!claimId) {
        return;
    }

    $.ajax({
        url: 'getClaimPerilsForEdit/' + claimId,
        type: 'GET',
        dataType: 'json',
        success: function(perils) {

            $('#peril-table-id tbody').empty();

            $.each(perils, function(index, peril) {
                var count = index;
                var data = "";

                var checked = peril.selfAsClaimant || "off";
                var claimantName = "";
                var claimantCode = "";

                if (checked === "on") {

                    claimantName = $("#risk-client-name").val() || peril.claimantName || "Self";
                    claimantCode = "";
                } else {
                    claimantName = peril.claimantName || "Third Party";
                    claimantCode = peril.claimantCode || "";
                }


                var rawEstimate = peril.perilEstimate || 0;
                var estimateValue = Number(rawEstimate).toLocaleString('en-US', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                });

                data += "<tr>" +
                    "<td>" +
                    "   <input type='hidden' name='perils[" + count + "].selfAsClaimant' value='" + checked + "'>" +
                    "   <input type='hidden' name='perils[" + count + "].claimantCode' value='" + claimantCode + "'>" +
                    claimantName +
                    "</td>" +
                    "<td>" +
                    "     <input type='hidden' name='perils[" + count + "].perilCode' value='" + peril.perilCode + "'>" +
                    (peril.perilName || "Unknown Peril") +
                    "</td>" +
                    "<td>" +
                    "     <input type='hidden' name='perils[" + count + "].perilEstimate' value='" + rawEstimate + "'>" +
                    estimateValue +
                    "</td> " +
                    "<td> " +
                    "      <input type='button' class='hyperlink-btn btn btn-danger' value='Delete' onclick='delRow(this)'>" +
                    "</td>" +
                    "</tr>";


                $('#peril-table-id').append(data);
            });

        },
        error: function(xhr, status, error) {
            bootbox.alert('Failed to load existing perils: ' + error);
        }
    });
}



function addSelectedPeril(){
    $("#btn-add-peril-selected").on('click', function(){
        if ($("#peril-code").val() === '') {
            bootbox.alert("Select Peril");
            return;
        }
        var count = $("#peril-table-id > tbody > tr").length;
        var data = "";
        var checked = "off";
        if ($("#self-as-clmnt").is(":checked")) {
            checked = "on";
        } else checked = "off";
        var claimantName = "";
        var claimantCode = "";
        if ($("#self-as-clmnt").is(":checked")) {
            checked = "on";
            claimantName = $("#risk-client-name").val();
            claimantCode = "";
        } else {
            checked = "off";
            claimantName = $("#clmt-name").val();
            claimantCode = $("#clmt-code").val();
        }

        // if ($("#self-as-clmnt").is(":checked")) {
        //     checked = "on";
        //     claimantName = $("#risk-client-name").val();
        //     claimantCode = $("#risk-client-code").val() || "SELF";
        // } else {
        //     checked = "off";
        //     claimantName = $("#clmt-name").val();
        //     claimantCode = $("#clmt-code").val();
        // }
         var estimateValue = Number($("#clm-estimate").val().replace(/,/g, '')).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        data += "<tr>" +
            "<td>" +
            "   <input type='hidden' name='perils[" + count + "].selfAsClaimant' value='" + checked + "'>" +
            "   <input type='hidden' name='perils[" + count + "].claimantCode' value='" + claimantCode + "'>" +
            claimantName +
            "</td>" +
            "<td>" +
            "     <input type='hidden' name='perils[" + count + "].perilCode' value='" + $("#peril-code").val() + "'>" + $("#peril-name").val() +
            "</td>" +
            "<td>" +
            "     <input type='hidden' name='perils[" + count + "].perilEstimate' value='" + $("#clm-estimate").val() + "'>" + estimateValue +
            "</td> " +
            "<td> " +
            "      <input type='button' class='hyperlink-btn btn btn-danger' value='Delete' onclick='delRow(this)'>" +
            "</td>" +
            "</tr>";
        $('#peril-table-id').append(data);
        $('#perilTransModal').modal('hide');
    });
}

function delRow(currElement) {
    var parentRowIndex = currElement.parentNode.parentNode.rowIndex;
    console.log("parentRowIndex="+parentRowIndex);
    $(currElement).closest('tr').remove();
    //document.getElementById($('#peril-table-id')).deleteRow(parentRowIndex);
}

// function changeLossDate(){
//     $('#los-date').on('dp.change', function (ev) {
//         var curDate = ev.date;
//         var dt = moment(curDate).format('DD/MM/YYYY');
//         selectRiskLov(dt);
//     });
// }

var previousLossDate = null;
function changeLossDate() {
    $('#los-date').on('dp.change', function (ev) {
        var curDate = ev.date;
        // Check if the date is valid
        if (!curDate || !moment(curDate).isValid()) {
            return;
        }
        var dt = moment(curDate).format('DD/MM/YYYY');
        if (dt === previousLossDate) {
            return;
        }
        // Update the previous date
        previousLossDate = dt;
        // Check if risk is already selected
        var selectedRiskId = $("#risk-id").val();
        var storedPolicyId = $("#risk-frm").data('selected-policy-id');

        if (selectedRiskId && storedPolicyId) {
            // Risk already selected - just validate, don't reinitialize
            console.log("Risk already selected - validating only");
         //   validateCoverPeriod(storedPolicyId, dt);
        } else {
            // No risk selected - reinitialize dropdown with new loss date
            selectRiskLov(dt);
        }
    });
}

function selectClaimantLov(){
    if($("#clmant-def").filter("div").length > 0)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "clmant-def",
            url: $("#clmant-def").attr("select2-url"),
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


function selectRiskPerils(riskId){
    if($("#peril-def").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "peril-def",
            sort : 'bindPerilCode',
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

function selectNextReviewLov(){
    if($("#review-user").filter("div").html() != undefined)
    {
        console.log("review-user div found, initializing Select2");
        Select2Builder.initAjaxSelect2({
            containerId : "review-user",
            sort : 'username',
            change: function(e,a,v){
                $("#next-rev-user").val(e.added.acctId);
                $("#next-rev-user-desc").val(e.added.name);
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
                var code = $("#next-rev-user").val();
                var name = $("#next-rev-user-desc").val();
                var data = {name:name,acctId:code};
                callback(data);
            },
            id: "acctId",
            placeholder:"Select Next Review User"
        });
    }
}



function getPolicyBalance(polNo){

        $.ajax( {
            url: 'claimBalance?polNo=' + encodeURIComponent(polNo),
            type: 'GET',
            processData: false,
            contentType: false,
            success: function (s ) {
                const clientBalance = s.clientBalance != null
                    ? parseFloat(s.clientBalance).toLocaleString('en-US', {
                        minimumFractionDigits: 2,
                        maximumFractionDigits: 2
                    })
                    : '0.00';
                // const insBalance = s.insBalance != null
                //     ? parseFloat(s.insBalance).toLocaleString('en-US', {
                //         minimumFractionDigits: 2,
                //         maximumFractionDigits: 2
                //     })
                //     : '0.00';
                $("#pol-ins-balance").text(clientBalance);

                // $("#pol-ins-balance").text(insBalance);

                // Show checkbox if either balance exists
                if (s.clientBalance > 0) {
                    $("#balance-checkbox-container").show();
                } else {
                    $("#balance-checkbox-container").hide();
                    $("#balance-confirm").prop('checked', false);
                }
            },
            error: function(xhr, error){
                //bootbox.alert(xhr.responseText);
            }
        });

}
var isValidating = false;
function validateCoverPeriod(policyId, lossDate) {
    // Prevent multiple simultaneous calls
    if (isValidating) {
        return;
    }
    // Validate inputs before proceeding
    if (!policyId || !lossDate) {
        return;
    }
    // Check if date is valid
    if (!moment(lossDate, 'DD/MM/YYYY', true).isValid()) {
        return;
    }

    isValidating = true;
    console.log("validateCoverPeriod called with:", policyId, lossDate);

    var formattedDate = moment(lossDate, 'DD/MM/YYYY').format('DD/MM/YYYY');

    $.ajax({
        url: 'validateCoverPeriod/' + policyId + '?lossDate=' + formattedDate,
        type: 'GET',
        processData: false,
        contentType: false,
        async: true,
        success: function(response) {
            console.log("Cover period validation passed for policy:", policyId);
            $("#submitClaimFormBtn").prop('disabled', false);
            isValidating = false;
        },
        error: function(xhr, error) {
            bootbox.alert(xhr.responseText);
            $("#submitClaimFormBtn").prop('disabled', true);
            isValidating = false;
        }
    });
}

function selectRiskLov(lossdate) {
    if ($("#risk-frm").filter("div").html() !== undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "risk-frm",
            sort: 'riskShtDesc',
            change: function(e, a, v) {
                console.log("e", e);
                $("#risk-id").val(e.added.riskId);
                $("#risk-desc").val(e.added.riskDesc || '');
                $("#risk-sht-desc").val(e.added.riskShtDesc || '');
                var clientName = e.added.names || e.added.clientname || '';
                $("#risk-client-name").val(clientName);
                $("#risk-client-code").val(e.added.riskId);
                var riskIdentifier = e.added.riskShtDesc || e.added.clientname || e.added.names || '';
                $("#risk-identifier").val(riskIdentifier);
                selectRiskPerils(e.added.riskId);
                getPolicyBalance(e.added.polno);
                $("#risk-frm").data('selected-policy-id', e.added.polId);
                var lossDate = $("#loss-date").val();
                if (lossDate && e.added.polId) {
                   // validateCoverPeriod(e.added.polId, lossDate);
                }

            },
            formatResult: function(a) {
                var displayText = a.riskShtDesc || a.polno || a.clientname || 'Unknown';
                return "Risk: " + displayText + " Policy: " + (a.polno || 'N/A') + " Insured: " + (a.clientname || 'Unknown');
            },
            formatSelection: function(a) {
                var displayText = a.riskShtDesc || a.polno || a.clientname || 'Unknown';
                return displayText + (a.riskDesc ? ' ' + a.riskDesc : '');
            },
            initSelection: function(element, callback) {
                var code = $("#risk-id").val();
                var name = $("#risk-desc").val();
                var shtdesc = $("#risk-sht-desc").val();
                var data = { riskDesc: name || '', riskShtDesc: shtdesc || '', riskId: code, polno: '', clientname: '' };
                callback(data);
            },
            id: "riskId",
            params: { lossDate: lossdate },
            placeholder: "Select A Risk"
        });
    }
}


// function selectRiskLov(lossdate){
//     if($("#risk-frm").filter("div").html() != undefined)
//     {
//         Select2Builder.initAjaxSelect2({
//             containerId : "risk-frm",
//             sort : 'riskShtDesc',
//             change: function(e, a, v){
//                 $("#risk-id").val(e.added.riskId);
//                 $("#risk-desc").val(e.added.riskDesc);
//                 $("#risk-sht-desc").val(e.added.riskShtDesc)
//                 $("#risk-identifier").val(e.added.riskShtDesc);
//                 selectRiskPerils(e.added.riskId);
//                 getPolicyBalance(e.added.polId);
//             },
//             formatResult : function(a)
//             {
//                 return "Risk: "+a.riskShtDesc +" Policy: "+ a.polno+" Insured: "+ a.clientname
//             },
//             formatSelection : function(a)
//             {
//                 return a.riskShtDesc+" "+a.riskDesc
//             },
//             initSelection: function (element, callback) {
//                 var code = $("#risk-id").val();
//                 var name = $("#risk-desc").val();
//                 var shtdesc = $("#risk-sht-desc").val();
//                 var data = {riskDesc:name,riskShtDesc:shtdesc,riskId:code};
//                 callback(data);
//             },
//             id: "riskId",
//             params: {lossDate: lossdate},
//             placeholder:"Select A Risk",
//         });
//     }
// }

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
        // Disable the button & show spinner with processing state
        //$button.prop('disabled', true).addClass('btn-processing');
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
                // Mark the trigger button as successful
                var triggerButton = $('#uploadClmReqModal').data('triggerButton');
                if (triggerButton) {
                    $(triggerButton).data('upload-success', true);
                }

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
                // On error, remove processing state immediately
                $button.removeClass('btn-processing');

                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            },
            complete: function () {
                // Always hide spinner and re-enable button, but keep processing class on success
                $spinner.hide();
                $button.prop('disabled', false);

                // Only remove processing class on error (success keeps it until page refresh)
                // The processing class will be removed on page refresh naturally
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

    $(button).prop('disabled', true).addClass('btn-processing').text('Processing...');

    var requiredDocs = JSON.parse(decodeURI($(button).data("reqdocs")));
    $("#uploadreq-code").val(requiredDocs["clmRequiredId"]);
    $("#trans-type").val("N");
    $(".uploadfile").show();

    $('#uploadClmReqModal').modal({
        backdrop: 'static',
        keyboard: true
    });

    $('#uploadClmReqModal').data('triggerButton', button);
}

$(document).ready(function() {
    $('#uploadClmReqModal').on('hidden.bs.modal', function () {

        var triggerButton = $(this).data('triggerButton');
        if (triggerButton) {
            var $btn = $(triggerButton);
            if ($btn.hasClass('btn-processing') && !$btn.data('upload-success')) {
                $btn.prop('disabled', false).removeClass('btn-processing').text('Upload');
            }
        }
    });
});

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
                        return '<button type="button" class="btn btn-danger btn-sm upload-req-btn" data-reqdocs=' + encodeURI(JSON.stringify(full)) + ' onclick="uploadReqDoc(this);" data-toggle="tooltip" data-placement="top" title="Upload Document">Upload</button>';
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
        console.log("saveReqClmDocsBtn clicked");
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
