/**
 * Created by peter on 3/12/2017.
 */
var UTILITIES = UTILITIES || {};
var ButtonIntelligence = {
    activeButtons: {},

    // Start processing state
    start: function(buttonId, loadingText) {
        if (this.activeButtons[buttonId]) return false; // Prevent double-click

        var $btn = $('#' + buttonId);
        if (!$btn.length) return false;

        // Store original state
        this.activeButtons[buttonId] = {
            originalText: $btn.val() || $btn.text(),
            originalDisabled: $btn.prop('disabled'),
            originalClass: $btn.attr('class')
        };

        // Set processing state
        $btn.addClass('btn-processing')
            .prop('disabled', true)
            .val(loadingText || 'Processing...');

        return true;
    },

    // End processing state
    finish: function(buttonId, success, message, autoRevert) {
        if (!this.activeButtons[buttonId]) return;

        var $btn = $('#' + buttonId);
        var original = this.activeButtons[buttonId];

        // Remove processing state
        $btn.removeClass('btn-processing')
            .prop('disabled', original.originalDisabled);

        if (success === true) {
            $btn.addClass('btn-success-flash').val(message || 'Success!');
        } else if (success === false) {
            $btn.addClass('btn-error-flash').val(message || 'Error!');
        } else {
            $btn.val(original.originalText); // Neutral finish
        }

        // Auto-revert to original state
        if (autoRevert !== false) {
            setTimeout(() => {
                $btn.removeClass('btn-success-flash btn-error-flash')
                    .val(original.originalText);
            }, success === false ? 3000 : 2000);
        }

        delete this.activeButtons[buttonId];
    },

    // Check if button is processing
    isActive: function(buttonId) {
        return !!this.activeButtons[buttonId];
    }
};
$(function () {


    $(document).ready(function () {
        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        $(document).ajaxStart(function () {
            $("#btn-add-quote,#btn-make-ready-policy,#btn-auth-quote,#btn-undo-make-ready-policy,#btn-confirm-quote,#btn-cancel-quote,#btn-save-quot-prod," +
                "#btn-save-risk,#saveRiskSection, #saveComparisonBtn, #btn-convert-quote, #saveSelectedProduct, #covertPrspctClnt").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#btn-add-quote,#btn-make-ready-policy,#btn-auth-quote,#btn-undo-make-ready-policy,#btn-confirm-quote,#btn-cancel-quote,#btn-save-quot-prod," +
                "#btn-save-risk,#saveRiskSection, #saveComparisonBtn, #btn-convert-quote, #saveSelectedProduct, #covertPrspctClnt").attr("disabled", false);
        });

        $('#saveQuoteTax').on('click', function () {
            saveTaxEdits();
            $("#editTaxModal").modal('hide');

            $('#editTaxModal').on('hidden.bs.modal', function () {
                $("#tax-quot-id").val('');
                $("#tax-id").val('');
            })
        })
        $('#overrid-prem').number(true, 2);

        populateClientLov();

        $("#pol-bin-type").val("B");
        console.log($("#pol-bin-type").val());
        $("#pol-bin-type").on('change', function() {
            if ($(this).val() === "M") {
                $(".motor-disp").show();
            } else {
                $(".motor-disp").hide();
            }
        });
        $("#pol-bin-type").prop("disabled", false);
        $("#comm-rate").prop("readonly", true);
        populateBinderLov();
        $("#sms-to").on('change', function () {
            $.ajax({
                type: 'GET',
                url: 'getReceiverSmsNumber',
                dataType: 'json',
                data: {"receiver": $(this).val()},
                async: true,
                success: function (result) {
                    $("#sms-send-to").val(result);
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

        $("#email-to").on('change', function () {
            $.ajax({
                type: 'GET',
                url: 'getReceiverEmail',
                dataType: 'json',
                data: {"receiver": $(this).val()},
                async: true,
                success: function (result) {
                    $("#email-send-to").val(result);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
            if ($(this).val() === "IN") {
                $("#email-cc").attr("readonly", true);
                $("#email-send-to").attr("readonly", false);
                $.ajax({
                    type: 'GET',
                    url: 'getInhouseEmail',
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        $("#email-cc").val(result);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                    }
                });
            } else {
                $("#email-cc").attr("readonly", false);
                $("#email-send-to").attr("readonly", true);
            }
        });
        $("#section_form_tbl").on('click', '.hyperlink-btn', function () {
            var mandatory = $(this).closest('tr').find(".mandatory").val();
            if (mandatory && mandatory === "Y") {
                bootbox.alert("Cannot delete a Mandatory Premium Item");
                return;
            }
            $(this).closest('tr').remove();
        });

        $('#resident-status').on('change', function () {
            if ($(this).val() !== 'resident') {
                $('#cnvt-passport-no').prop('required', true);
                $('#cnvt-id-number').prop('required', false);
                $('#cnvt-pin-number').prop('required', false);
                $('label[for="cnvt-id-number"]').find('.required').hide();
                $('label[for="cnvt-pin-number"]').find('.required').hide();
                $('label[for="cnvt-passport-no"]').append(' <span class="required">*</span>');
            } else {
                $('#cnvt-id-number').prop('required', true);
                $('#cnvt-pin-number').prop('required', true);
                $('label[for="cnvt-id-number"]').find('.required').show();
                $('label[for="cnvt-pin-number"]').find('.required').show();
                $('#cnvt-passport-no').prop('required', false);
                $('label[for="cnvt-passport-no"]').find('.required').remove();
            }
            // Clear previous data when resident status changes
            $('#cnvt-fname, #cnvt-lname, #cnvt-dob, #cnvt-pin-number, #cnvt-passport-no, #cnvt-id-number').val('').prop('readonly', false);
            $('#cnvt-gender').val('').prop('disabled', false);

            console.log('Resident Status changed, refreshing user information...');

            // Only fetch details if ID/Passport is provided
            if ($('#cnvt-id-number').val().trim() || $('#cnvt-passport-no').val().trim()) {
                fetchUserDetails();
            }

        });
        if ($('#clnt-cnvt-type-name').val()) {

            validateKRA();
        }
        // Trigger fetchUserDetails when ID/Passport is provided
        $('#cnvt-id-number, #cnvt-passport-no').on('blur', function () {
            var clientType = $('#clnt-cnvt-type-name').val().trim();
            if (!clientType) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Please select the Client type before validation',
                    showConfirmButton: true
                });
            }
            if (clientType.toUpperCase() === 'CORPORATE') {
                $('#resident-status').val('company')
                validateKRA();
            } else if (!$('#resident-status').val()) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Please select the Resident Status before validation',
                    showConfirmButton: true
                });
            } else {
                fetchUserDetails();
            }
        });


        function fetchUserDetails() {
            var idNo = $('#cnvt-id-number').val().trim();
            var passportNo = $('#cnvt-passport-no').val().trim();
            var residentStatus = $('#resident-status').val();
            var clientType = $('#clnt-cnvt-client-type').val().trim();

            if (!clientType) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Please select the Client type before validation',
                    showConfirmButton: true
                });

            } else if (clientType.toUpperCase() !== 'CORPORATE') {

                // Ensure both Resident Status and ID/Passport are provided
                if (!residentStatus) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Please select a Resident Status before validation',
                        showConfirmButton: true
                    });
                    return;
                }

                if (!idNo && !passportNo) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Please enter either your ID number or Passport number',
                        showConfirmButton: true
                    });
                    return;
                }

                $('#loading-spinner').show();
                var documentType = idNo ? 'ssnit' : 'passport';
                var documentId = idNo || passportNo;

                // Fetch IPRS details
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/clients/setups/validateID',
                    type: 'GET',
                    data: {documentType: documentType, documentId: documentId},
                    success: function (iprsResponse) {
                        $('#loading-spinner').hide();

                        if (iprsResponse.status === 'Success') {
                            $('#cnvt-id-number').val(documentType === 'ssnit' ? documentId : '').prop('readonly', true);
                            $('#cnvt-passport-no').val(iprsResponse.passportNumber).prop('readonly', true);
                            $('#cnvt-fname').val(iprsResponse.firstName).prop('readonly', true);
                            $('#cnvt-lname').val(iprsResponse.otherName).prop('readonly', true);
                            $('#cnvt-other-names').val(iprsResponse.lastName).prop('readonly', true);
                            $('#cnvt-dob').val(moment(iprsResponse.birthDate).format('DD/MM/YYYY')).prop('readonly', true);

                            if (iprsResponse.gender) {
                                $('#gender').val(iprsResponse.gender === 'M' ? 'M' : 'F').prop('disabled', false);
                            }

                            Swal.fire({
                                icon: 'success',
                                text: 'IPRS data retrieved successfully!',
                                title: 'Success'
                            });
                            validateKRA();

                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: iprsResponse.message,
                            });
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $('#loading-spinner').hide();
                        Swal.fire({
                            icon: 'error',
                            text: jqXHR.responseText,
                            title: 'Error'

                        });
                    }
                });
            }
        }

        function validateKRA() {
            var idNumber = $("#cnvt-id-number").val();
            var residentStatus = $("#resident-status").val();
            if (residentStatus === 'resident') {
                residentStatus = 'KE';
            } else if (residentStatus === 'non-resident') {
                residentStatus = 'NKE';
            } else if (residentStatus === 'non-citizen') {
                residentStatus = 'NKENR';
            } else if (residentStatus === 'company') {
                residentStatus = 'COMP';
            }
            var data = {};
            var authentication = {};
            var authenticationTaskWorkProducts = {};
            authenticationTaskWorkProducts.taxationDetails = {taxPayerId: idNumber, taxPayerType: residentStatus};
            authentication.authenticationTaskWorkProducts = authenticationTaskWorkProducts;
            console.log(authentication)
            data.authentication = authentication;
            console.log(data);
            $('#loading-spinner').show();
            $.ajax({
                url: SERVLET_CONTEXT + '/protected/clients/setups/validateKRA',
                contentType: 'application/json',
                type: 'POST',
                data: JSON.stringify(data),
                success: function (kraResponse) {
                    $('#loading-spinner').hide();
                    $('#cnvt-pin-number').val(kraResponse).prop('readonly', true);
                    Swal.fire({
                        title: 'Success',
                        text: 'PIN Validated Successfully',
                        icon: 'success'
                    });

                },
                error: function(jqXHR, textStatus, errorThrown) {
                    $('#loading-spinner').hide();
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        }

        PROSPECTS_UTILITIES.addProspects("Q");
        PROSPECTS_UTILITIES.populateProspectLov();
        populateCurrencyLov();
        createProductForSel();
        populatePaymentModes();
        populateUserBranches();
        populateQuotDetails();
        getNewClausesModal();
        createQuoteClauses();
        populateInsuredLov();
        getPolicyWet();
        changePolicyWetDt();
        createNewQuote();
        getQuotProducts();
        saveRiskSections();
        getNewPremItemsModal();
        newRisk();
        addQuoteProd();
        changeClientType();
        populateClientTypeLov();
        populateClientTypeLov2();
        populateBranchLov1();
        populateCountryLov();
        populateTitlesLov();
        createSectorSelect();
        populateSourceGroupLov();
        saveClientDetails();
        getNewTaxesModal();
        defaultCountry();
        getProductRisks(-2000);
        getRiskSections(-2000);
        createPolicyTaxes(-2000);
        UTILITIES.createAssignee();
        UTILITIES.emailReports();
        UTILITIES.smsReports();
        //UTILITIES.smsClient();
        compareQuotProd();
        convertProspects();
        populateTitlesLov1();
        createClientTownLov1();
        populatePostalCode1(-2000);
        populateCountryLov1();
        populateSmsPrefix1(-2000);
        populateBranchLov2();
        createSectorSelect1();
        populateOccupations1(-2000);
        populateClientTypeLov1();
        populateSubAgentsLov();
        populateMarketerLov();
        populateIntroducerLov();
        populateLeadsManLov();

    });
});

function makeReadyQuote() {
    if (typeof quotId !== 'undefined') {
        if (quotId !== -2000) {
            $("#quote-id").val(quotId);
            $.ajax({
                url: 'selectQuoteForConvert',
                type: 'GET',
                data: {quotId: quotId},
                dataType: 'json',
                async: true,
                success: function (s) {
                    console.log(s);
                    for (let i = 0; i < s.length; i++) {
                        let quote = s[i];
                        let quoteType = quote[3];

                        if (quoteType === "comparison") {
                            $("#compQuotSelectTable tbody").each(function () {
                                $(this).remove();
                            });
                            for (var res in s) {
                                var markup = "<tr><td><input type='checkbox' class='quote-checkbox' name='record' id='" + s[res][0] + "'></td><td>" + s[res][1] + "</td><td>" + UTILITIES.currencyFormat(s[res][2]) + "</td></tr>";
                                $("#compQuotSelectTable").append(markup);
                            }
                            $('#compQuoteSelectModal').modal({
                                backdrop: 'static',
                                keyboard: true
                            });
                            $('.quote-checkbox').change(function () {
                                if ($(this).is(':checked')) {
                                    $('.quote-checkbox').not(this).prop('disabled', true);
                                } else {
                                    $('.quote-checkbox').prop('disabled', false);
                                }
                            });
                            $('#compQuotSelectTable').DataTable({
                                searching: true,
                                paging: false,
                                destroy: true,
                                lengthMenu: [[-1], ["All"]]
                            });
                            $('#saveSelectedContract').click(function () {

                                var selectedQuoteProducts = $('.quote-checkbox:checked');

                                if (selectedQuoteProducts.length === 0) {
                                    Swal.fire({
                                        title: 'Error',
                                        text: 'Please select one quote product to submit.',
                                        icon: 'error'
                                    });
                                } else if (selectedQuoteProducts.length > 1) {
                                    Swal.fire({
                                        title: 'Error',
                                        text: 'Only one quote product can be submitted at a time.',
                                        icon: 'error'
                                    });
                                } else {
                                    var selectedQuoteProductId = selectedQuoteProducts.attr('id');
                                    console.log("selected: " + selectedQuoteProductId);
                                    var stack_bottomleft = {
                                        "dir1": "up",
                                        "dir2": "left",
                                        "firstpos1": 25,
                                        "firstpos2": 25
                                    };

                                    $.ajax({
                                        type: 'GET',
                                        url: 'makeReadyCompQuote',
                                        data: {quoteProductId: selectedQuoteProductId},
                                        dataType: 'json',
                                        async: true,
                                        success: function (result) {
                                            ButtonIntelligence.finish('btn-make-ready-policy', true, 'Submitted!');
                                            Swal.fire({
                                                title: "Confirmed...",
                                                text: "Your quotation has been submitted successfully....",
                                                icon: "success"
                                            });
                                            populateQuotDetails();
                                        },
                                        error: function (jqXHR, textStatus, errorThrown) {
                                            ButtonIntelligence.finish('btn-make-ready-policy', false, 'Submit Failed!');
                                            Swal.fire({
                                                title: 'Error',
                                                text: jqXHR.responseText,
                                                icon: 'error'
                                            });
                                        }
                                    });
                                    $('#compQuoteSelectModal').modal('hide');
                                }
                            });
                            break;
                        } else {
                            makeReady();
                        }

                    }
                }
            });
        }
    }
}


function defaultCountry() {
    $.ajax({
        url: SERVLET_CONTEXT + '/protected/clients/setups/getDefaultCountry',
        type: 'GET',
        processData: false,
        contentType: false,
        success: function (s) {
            if (s) {
                populateSmsPrefix(s.couCode);
            }
        },
        error: function (xhr, error) {

        }
    });

}


function populateSourcesLov(srcGroupId) {


    if ($("#source-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "source-frm",
            sort: 'desc',
            change: function (e, a, v) {
                $("#source-id").val(e.added.srcId);
            },
            formatResult: function (a) {
                return a.desc;
            },
            formatSelection: function (a) {
                return a.desc;
            },
            initSelection: function (element, callback) {
                var code = $('#source-id').val();
                var name = $("#source-name").val();
                var data = {desc: name, srcId: code};
                callback(data);
            },
            id: "srcId",
            width: "250px",
            params: {srcGroupId: srcGroupId},
            placeholder: "Select Source"

        });
    }


}


function populateSourceGroupLov() {
    if ($("#sourcegroup-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "sourcegroup-frm",
            sort: 'desc',
            change: function (e, a, v) {
                $("#sourcegroup-id").val(e.added ? e.added.srcGroupId : "");
                if (e.added) {
                    populateSourcesLov(e.added.srcGroupId);
                } else {
                    $("#activation-code-display").hide();
                }

            },
            formatResult: function (a) {
                return a.desc;
            },
            formatSelection: function (a) {
                    if (a.desc && a.desc.toLowerCase() === "activation campaign"){
                        $("#activation-code-display").show();
                    }else {
                        $("#activation-code-display").hide();
                    }
                return a.desc;
            },
            initSelection: function (element, callback) {
                var code = $('#sourcegroup-id').val();
                var name = $("#sourcegroup-name").val();
                var data = {desc: name, srcGroupId: code};
                callback(data);
                populateSourcesLov($('#sourcegroup-id').val());
            },
            id: "srcGroupId",
            width: "250px",
            placeholder: "Select Source Group"

        });

    }

}


function createSectorSelect() {
    if ($("#sect-def").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "sect-def",
            sort: 'name',
            change: function (e, a, v) {
                $("#sect-id").val(e.added.code);
                populateOccupations(e.added.code);
            },
            formatResult: function (a) {
                return a.name
            },
            formatSelection: function (a) {
                return a.name
            },
            initSelection: function (element, callback) {
            },
            id: "code",
            placeholder: "Select Sector",
        });
    }
}

function populateOccupations(sectCode) {
    if ($("#occ-def").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "occ-def",
            sort: 'name',
            change: function (e, a, v) {
                $("#occ-id").val(e.added.code);
            },
            formatResult: function (a) {
                return a.name
            },
            formatSelection: function (a) {
                return a.name
            },
            initSelection: function (element, callback) {
            },
            id: "code",
            placeholder: "Select Occupation",
            params: {sectCode: sectCode}

        });
    }
}

function populateBranchLov1() {
    if ($("#ten-branch").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "ten-branch",
            sort: 'obName',
            change: function (e, a, v) {
                $("#obId").val(e.added.obId);
            },
            formatResult: function (a) {
                return a.obName
            },
            formatSelection: function (a) {
                return a.obName
            },
            initSelection: function (element, callback) {
            },
            id: "obId",
            placeholder: "Select Branch",

        });
    }
}

var populateSubAgentsLov = function(){
    if($("#sub-agent-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "sub-agent-frm",
            sort : 'name',
            change: function(e, a, v){
                $("#sub-agent-id").val(e.added.acctId);

                if(e.added.absaNo){
                    $("#sub-agent-absaNo-id").val(e.added.absaNo);
                    $("#sub-agent-absaNo-name").val(e.added.absaNo);
                } else {
                    $("#sub-agent-absaNo-id").val('');
                    $("#sub-agent-absaNo-name").val('');
                }

            },
            formatResult : function(a)
            {
                return a.name;
            },
            formatSelection : function(a)
            {
                return a.name;
            },
            initSelection: function (element, callback) {
                var code = $('#sub-agent-id').val();
                var name = $("#sub-agent-name").val();
                var abNo = $("#sub-agent-absaNo-id").val();
                var data = {name:name,acctId:code, absaNo:abNo};
                callback(data);
            },
            id: "acctId",
            width:"250px",
            placeholder:"Select Sub Agent"

        });

        $("#sub-agent-frm").on("select2-removed", function(e) {
            $("#sub-agent-id").val('');
            $("#sub-agent-absaNo-id").val('');
            $("#sub-agent-absaNo-name").val('');
        })
    }
};

var populateLeadsManLov = function(){
    if($("#leads-man-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "leads-man-frm",
            sort : 'name',
            change: function(e, a, v){
                $("#leads-man-id").val(e.added.id);
                $("#leads-man-name").val(e.added.name);

                if (e.added.absaNo){
                    $("#leads-absaNo-id").val(e.added.absaNo);
                    $("#leads-absaNo-name").val(e.added.absaNo);
                } else {
                    $("#leads-absaNo-id").val('');
                    $("#leads-absaNo-name").val('');
                }
            },
            formatResult : function(a)
            {
                return a.name;
            },
            formatSelection : function(a)
            {
                return a.name;
            },
            initSelection: function (element, callback) {
                var code  = $('#leads-man-id').val();
                var name = $("#leads-man-name").val();
                var abNo = $("#leads-absaNo-id").val();
                var data = {name:name,id:code, absaNo:abNo};
                callback(data);
            },
            id: "id",
            width:"250px",
            placeholder:"Select Leads Man"

        });

        $("#leads-man-frm").on("select2-removed", function(e) {
            $("#leads-man-id").val('');
            $("#leads-man-name").val('');
            $("#leads-absaNo-id").val('');
            $("#leads-absaNo-name").val('');
        })
    }
};
var populateIntroducerLov = function() {
    if ($("#introducer-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "introducer-frm",
            sort: 'name',
            change: function(e, a, v) {
                $("#introducer-id").val(e.added.acctId);
                $("#introducer-name").val(e.added.name);

                if (e.added.absaNo) {
                    $("#introducer-absa-No-id").val(e.added.absaNo);
                    $("#introducer-absaNo-name").val(e.added.absaNo);
                } else {
                    $("#introducer-absa-No-id").val('');
                    $("#introducer-absaNo-name").val('');
                }
            },
            formatResult: function(a) {
                return a.name;
            },
            formatSelection: function(a) {
                return a.name;
            },
            initSelection: function(element, callback) {
                var code = $('#introducer-id').val();
                var name = $("#introducer-name").val();
                var abNo = $("#introducer-absa-No-id").val();
                var data = { name:name, acctId:code, absaNo:abNo };
                callback(data);
            },
            id: "acctId",
            width: "250px",
            placeholder: "Select Introducer"
        });

        $("#introducer-frm").on("select2-removed", function(e) {
            $("#introducer-id").val('');
            $("#introducer-name").val('');
            $("#introducer-absa-No-id").val('');
            $("#introducer-absaNo-name").val('');
        });
    }
};

var populateMarketerLov = function(){
    if($("#marketer-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "marketer-frm",
            sort : 'name',
            change: function(e, a, v){
                $("#marketer-id").val(e.added.acctId);

                if(e.added.absaNo){
                    $("#marketer-absaNo-id").val(e.added.absaNo);
                    $("#marketer-absaNo-name").val(e.added.absaNo);
                } else {
                    $("#marketer-absaNo-id").val('');
                    $("#marketer-absaNo-name").val('');
                }
            },
            formatResult : function(a)
            {
                return a.name;
            },
            formatSelection : function(a)
            {
                return a.name;
            },
            initSelection: function (element, callback) {
                var code = $('#marketer-id').val();
                var name = $("#marketer-name").val();
                var abNo = $("#marketer-absaNo-id").val();
                var data = {name:name,acctId:code, absaNo:abNo};
                callback(data);
            },
            id: "acctId",
            width:"250px",
            placeholder:"Select Marketer"

        });

        $("#marketer-frm").on("select2-removed", function(e) {
            $("#marketer-id").val('');
            $("#marketer-absaNo-id").val('');
            $("#marketer-absaNo-name").val('');
        })
    }
};

$(document).ready(function() {
    $("#agent-type").change(function () {
        var selectedValue = $(this).val();

        if (selectedValue === 'SUB') {
            $(".sub-agent-type").css('display', 'block');
            $(".sub-agent-absNo").css('display', 'block');
            $(".marketer-agent-type").css('display', 'none');
            $(".marketer-agent-absNo").css('display', 'none');
            populateSubAgentsLov();
        } else if (selectedValue === 'MRK') {
            $(".marketer-agent-type").css('display', 'block');
            $(".marketer-agent-absNo").css('display', 'block');
            $(".sub-agent-type").css('display', 'none');
            $(".sub-agent-absNo").css('display', 'none');
            populateMarketerLov();
        } else {
            $(".sub-agent-type, .marketer-agent-type").css('display', 'none');
            $(".sub-agent-absNo, .marketer-agent-absNo").css('display', 'none');
        }
    });
});


function populateClientTypeLov2() {
    if ($("#clnt-client-type-1").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "clnt-client-type-1",
            sort: 'clientType',
            change: function (e, a, v) {
                $("#clnt-type-id-1").val(e.added.typeId);
                $("#clnt-type-name-1").val(e.added.typeDesc);
            },
            formatResult: function (a) {
                return a.typeDesc
            },
            formatSelection: function (a) {
                return a.typeDesc
            },
            initSelection: function (element, callback) {
            },
            id: "typeId",
            placeholder: "Select Client Type",

        });
    }
    if ($("#clnt-client-type").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "clnt-client-type",
            sort: 'clientType',
            change: function (e, a, v) {
                $("#clnt-type-id").val(e.added.typeId);
                $("#clnt-type-name").val(e.added.typeDesc);
            },
            formatResult: function (a) {
                return a.typeDesc
            },
            formatSelection: function (a) {
                return a.typeDesc
            },
            initSelection: function (element, callback) {
            },
            id: "typeId",
            placeholder: "Select Client Type",

        });
    }
}

function populateTitlesLov() {
    if ($("#clnt-title").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "clnt-title",
            sort: 'titleName',
            change: function (e, a, v) {
                $("#clnt-title-id").val(e.added.titleId);
                $("#clnt-title-name").val(e.added.titleName);
            },
            formatResult: function (a) {
                return a.titleName
            },
            formatSelection: function (a) {
                return a.titleName
            },
            initSelection: function (element, callback) {
            },
            id: "titleId",
            placeholder: "Select Title",

        });
    }
}


function populateSmsPrefix(couCode) {
    if ($("#sms-pref").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "sms-pref",
            sort: 'prefixName',
            change: function (e, a, v) {
                console.log(e.added.prefixId);
                $("#pref-sms-id").val(e.added.prefixId);
            },
            formatResult: function (a) {
                return a.prefixName
            },
            formatSelection: function (a) {
                return a.prefixName
            },
            initSelection: function (element, callback) {
            },
            id: "prefixId",
            placeholder: "Prefix",
            params: {couCode: couCode}

        });
    }

    if ($("#phone-pref").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "phone-pref",
            sort: 'prefixName',
            change: function (e, a, v) {
                console.log(e.added.prefixId);
                $("#pref-phone-id").val(e.added.prefixId);
            },
            formatResult: function (a) {
                return a.prefixName
            },
            formatSelection: function (a) {
                return a.prefixName
            },
            initSelection: function (element, callback) {
            },
            id: "prefixId",
            placeholder: "Prefix",
            params: {couCode: couCode}

        });
    }


    if ($("#prs-sms-pref").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "prs-sms-pref",
            sort: 'prefixName',
            change: function (e, a, v) {
                console.log(e.added.prefixId);
                $("#prs-pref-sms-id").val(e.added.prefixId);
            },
            formatResult: function (a) {
                return a.prefixName
            },
            formatSelection: function (a) {
                return a.prefixName
            },
            initSelection: function (element, callback) {
            },
            id: "prefixId",
            placeholder: "Prefix",
            params: {couCode: couCode}

        });
    }
}

function populateCountryLov() {
    if ($("#clnt-country").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "clnt-country",
            sort: 'couName',
            change: function (e, a, v) {
                $("#cou-id").val(e.added.couCode);
                $("#cou-prefix").val(e.added.prefix);
                populateSmsPrefix(e.added.couCode);
            },
            formatResult: function (a) {
                return a.couName
            },
            formatSelection: function (a) {
                return a.couName
            },
            initSelection: function (element, callback) {
            },
            id: "couCode",
            placeholder: "Select Country",

        });
    }
}


function populateClientTypeLov() {
    if ($("#clnt-client-type").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "clnt-client-type",
            sort: 'clientType',
            change: function (e, a, v) {
                $("#clnt-type-id").val(e.added.typeId);
                $("#clnt-type-name").val(e.added.typeDesc);
            },
            formatResult: function (a) {
                return a.typeDesc
            },
            formatSelection: function (a) {
                return a.typeDesc
            },
            initSelection: function (element, callback) {
                var code = $("#clnt-type-id").val();
                var name = $("#clnt-type-name").val();
                var data = {typeDesc: name, typeId: code};
                callback(data);
            },
            id: "typeId",
            placeholder: "Select Client Type",

        });
    }
}

function changeClientType() {
    $("#clnt-type").on('change', function () {
        $("#client-id").val("");
        $("#client-f-name").val("");
        $("#client-other-name").val("");
        $("#client-national-id").val("");
        populateClientLov();

        if (this.value === '' || this.value === 'C') {
            $(".quot-client").text("Client *");
            $("#client-div").show();
            $("#insured-clnt-div").show();
            $("#prs-div").hide();
            $("#insured-prs-div").hide();
            $("#client-id").val("");
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-other-name").val("");
            $("#insured-id").val("");
            $("#insured-type").val("");
            $("#btn-add-client").show();
            $("#btn-add-prs").hide();
            $("#btn-add-client").val("New");
            populateInsuredLov();
        } else if (this.value === 'P') {
            $(".quot-client").text("Prospect *");
            $("#client-div").hide();
            $("#insured-clnt-div").hide();
            $("#insured-prs-div").show();
            $("#prs-div").show();
            $('#insured-frm').select2('val', null);
            $("#client-id").val("");
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-id").val("");
            $("#insured-type").val("");
            $("#insured-other-name").val("");
            $("#btn-add-prs").show();
            $("#btn-add-prs").val("New");
            $("#btn-add-client").hide();
            PROSPECTS_UTILITIES.populateProspectLov();
        }
    });


    $("#insured-type").on('change', function () {
        console.log(this.value);
        if (this.value === '' || this.value === 'C') {
            $("#insured-clnt-div").show();
            $("#insured-prs-div").hide();
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-other-name").val("");
            $("#insured-id").val("");
            $('#insured-frm').select2('val', null);
            populateInsuredLov();
        } else if (this.value === 'P') {
            $("#insured-clnt-div").hide();
            $("#insured-prs-div").show();
            $("#insured-name").val("");
            $("#insured-code").val("");
            $("#insured-other-name").val("");
            $('#insured-prs-frm').select2('val', null);
            PROSPECTS_UTILITIES.populateProspectInsured();
        }
    });
}


function changePolicyWetDt() {

    $('#cover-to-date').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        $("#risk-wet-date").val(dt);
        $("#product-wet-date").val(dt);
    });

    $('#risk-cover-from').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate);
        var polwef = $("#from-date").val();
        var wet = $("#wet-date").val();

    });

    $('#risk-cover-to').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        ;
        var polwef = $("#from-date").val();
        var polwet = $("#wet-date").val();

    });


}


function createNewQuote() {
    $("#btn-add-quote").click(function () {
        if (ButtonIntelligence.isActive('btn-add-quote')) return;

        var isUpdate = (typeof quotId !== 'undefined' && quotId !== -2000);
        var loadingText = isUpdate ? 'Updating Quote...' : 'Creating Quote...';

        if (!ButtonIntelligence.start('btn-add-quote', loadingText)) return;
        if (typeof quotId !== 'undefined') {
            if (quotId !== -2000) {
                updateQuote();
            } else {
                createQuote();
            }
        } else {
            createQuote();
        }
        $("#uw_tabs_title").show();
        $("#uw_tabs").show();
    });

    $("#btn-save-risk").click(function () {
        if (ButtonIntelligence.isActive('btn-save-risk')) return;

        var isUpdate = $("#risk-code-pk").val() != '';
        var loadingText = isUpdate ? 'Updating Risk...' : 'Creating Risk...';

        if (!ButtonIntelligence.start('btn-save-risk', loadingText)) return;

        if (isUpdate) {
            updateRisk();
        } else {
            createRisk();
        }
    });

    $("#btn-save-quot-prod").click(function () {
        if (ButtonIntelligence.isActive('btn-save-quot-prod')) return;

        var isUpdate = $("#quot-prod-pk").val() != '';
        var loadingText = isUpdate ? 'Updating Product...' : 'Creating Product...';

        if (!ButtonIntelligence.start('btn-save-quot-prod', loadingText)) return;

        if (isUpdate) {
            updateQuoteProduct();
        } else {
            createQuoteProduct();
        }
    });

    $("#btn-make-ready-policy").click(function () {
        if (ButtonIntelligence.isActive('btn-make-ready-policy')) return;

        Swal.fire({
            title: "Confirm",
            text: "Are you sure you want to submit the quote to the client?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes"
        }).then((result) => {
            if (result.isConfirmed) {
                if (!ButtonIntelligence.start('btn-make-ready-policy', 'Submitting...')) return;
                makeReadyQuote();
            }
        });
    });

    $("#btn-undo-make-ready-policy").click(function () {
        if (ButtonIntelligence.isActive('btn-undo-make-ready-policy')) return;

        if (!ButtonIntelligence.start('btn-undo-make-ready-policy', 'Processing...')) return;
        undoMakeReady();
    });

    $("#btn-auth-quote").click(function () {
        if (ButtonIntelligence.isActive('btn-auth-quote')) return;

        if (!ButtonIntelligence.start('btn-auth-quote', 'Authorizing...')) return;
        authQuote();
    });

    $("#btn-confirm-quote").click(function () {
        if (ButtonIntelligence.isActive('btn-confirm-quote')) return;

        if (!ButtonIntelligence.start('btn-confirm-quote', 'Confirming...')) return;
        confirmQuote();
    });

    $("#btn-cancel-quote").click(function () {
        $('#closeModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    });

    $("#closeQuote").click(function () {
        if (ButtonIntelligence.isActive('closeQuote')) return;

        if (!ButtonIntelligence.start('closeQuote', 'Closing...')) return;
        cancelQuote();
    });

    $("#btn-convert-quote").click(function () {
        if (ButtonIntelligence.isActive('btn-convert-quote')) return;

        if (!ButtonIntelligence.start('btn-convert-quote', 'Converting...')) return;
        convertQuotProduct();
    });

    $("#convertQuotBtn").click(function () {
    });

    $("#saveSelectedProduct").click(function () {
    });
}

function undoMakeReady() {
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    $.ajax({
        type: 'GET',
        url: 'undoMakeReadyQuote',
        dataType: 'json',
        async: true,
        success: function (result) {
            ButtonIntelligence.finish('btn-undo-make-ready-policy', true, 'Unsubmitted!');
            Swal.fire({
                title: 'Success',
                text: 'Unsubmitted Successfully',
                icon: 'success'
            });
            populateQuotDetails();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ButtonIntelligence.finish('btn-undo-make-ready-policy', false, 'Failed!');
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}


function saveClientDetails() {
    $('#saveClientBtn').click(function () {
        var $classForm = $('#tenant-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createClient";
        var request = $.post(url, data);
        request.success(function (result) {
            $("#client-id").val(result.tenId);
            $("#client-f-name").val(result.fname);
            $("#client-other-name").val(result.otherNames);
            $("#client-national-id").val(result.idNo);
            populateClientLov();
            $("#insured-name").val(result.fname);
            $("#insured-code").val(result.tenId);
            $("#insured-other-name").val(result.otherNames);
            $("#insured-id").val(result.idNo);
            populateInsuredLov();
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            validator.resetForm();
            $('#tenant-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#createClientModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}


var populateUserBranches2 = function () {
    if ($("#branch-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "branch-frm",
            sort: 'obName',
            change: function (e, a, v) {
                $("#prsbrn-id").val(e.added.obId);
            },
            formatResult: function (a) {
                return a.obName;
            },
            formatSelection: function (a) {
                return a.obName;
            },
            initSelection: function (element, callback) {
                var code = $('#prsbrn-id').val();
                var name = $("#prsbrn-name").val();
                var data = {obName: name, obId: code};
                callback(data);
            },
            id: "obId",
            width: "250px",
            placeholder: "Select Branch"

        });
    }
};

function cancelQuote() {
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    $.ajax({
        type: 'GET',
        url: 'cancelQuote',
        dataType: 'json',
        data: {"reason": $("#quot-reasons").val()},
        async: true,
        success: function (result) {
            $('#closeModal').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Quote Closed Successfully',
                icon: 'success'
            });
            populateQuotDetails();
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


function confirmQuote() {
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    $.ajax({
        type: 'GET',
        url: 'confirmQuote',
        dataType: 'json',
        async: true,
        success: function (result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Quote Confirmation Process Successfully',
                icon: 'success'
            });
            populateQuotDetails();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}

function makeReady() {
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    $.ajax({
        type: 'GET',
        url: 'makeReadyQuote',
        dataType: 'json',
        async: true,
        success: function (result) {
            ButtonIntelligence.finish('btn-make-ready-policy', true, 'Submitted!');
            Swal.fire({
                title: 'Success',
                text: 'Submitted Successfully',
                icon: 'success'
            });
            populateQuotDetails();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ButtonIntelligence.finish('btn-make-ready-policy', false, 'Submit Failed!');
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}


function authQuote() {
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    $.ajax({
        type: 'GET',
        url: 'authorizeQuote',
        dataType: 'json',
        async: true,
        success: function (result) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Authorization Process Successfully',
                icon: 'success'
            });
            populateQuotDetails();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        }
    });
}

function getRskSections() {
    var arr = [];
    $("#section_form_tbl tr").each(function (row, tr) {
        var isChecked = $(this).find('.premium-checkbox').is(':checked');
        if (isChecked) {
            var section = $(this).find('.section').eq(0).val();
            var premId = $(this).find('.premId').eq(0).val();
            var rate = $(this).find('.rate').eq(0).val();
            var divFactor = $(this).find('.divFactor').eq(0).val();
            var freeLimit = $(this).find('.freeLimit').eq(0).val();
            var amount = $(this).find('.amount').eq(0).val();
            var multiplierRate = $(this).find('.multiplierRate').eq(0).val();
            var ratesApplicable = $(this).find('.ratesApplicable').eq(0).val();
            arr.push({
                section: section,
                premId: premId,
                rate: rate,
                divFactor: divFactor,
                ratesApplicable: ratesApplicable,
                freeLimit: freeLimit,
                amount: amount,
                multiplierRate: multiplierRate
            });
            console.log(arr);
        }
    });

    return arr;
}

function getQuotProductDetails() {
    var $currForm = $('#quot-pro-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        return;
    }
    $('#quot-pro-form input[type=checkbox]').each(function (e) {
        $(this).val($(this).is(':checked'));
    });
    var data = {};
    $currForm.serializeArray().map(function (x) {
        data[x.name] = x.value;
    });
    return data;
}

function getRiskDetails() {
    var $currForm = $('#risk-form');
    $('#risk-form input[type=checkbox]').each(function (e) {
        $(this).val($(this).is(':checked'));
    });
    var data = {};
    $currForm.serializeArray().map(function (x) {
        data[x.name] = x.value;
    });
    console.log(data);
    return data;
}


function createQuote() {
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    var arr = getRskSections();
    if (arr.length == 0) {
        ButtonIntelligence.finish('btn-add-quote', false, 'No Sections!');
        bootbox.alert("Cannot create Quote without sections")
        return false;
    }
    //arr.shift();
    var $currForm = $('#quot-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        ButtonIntelligence.finish('btn-add-quote', false, 'Invalid Form!');
        return;
    }
    $('#quot-form input[type=checkbox]').each(function (e) {
        $(this).val($(this).is(':checked'));
    });

    var data = {};
    $currForm.serializeArray().map(function (x) {
        console.log("Form data: "+x);
        data[x.name] = x.value;
    });
    var url = "createQuotation";
    console.log('passed here...');
    data.sections = arr;
    data.riskBean = getRiskDetails();
    data.quoteProductBean = getQuotProductDetails();
    console.log('data...',data);
    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(data),
        success: function (s) {
            ButtonIntelligence.finish('btn-add-quote', true, 'Quote Created!');
            Swal.fire({
                title: 'Success',
                text: 'Quote Transaction created Successfully',
                icon: 'success'
            });
            $('#prd-code').select2('val', null);
            createProductForSel();
            console.log($("#prd-id").val());
            $('#insured-frm').select2('val', null);
            populateInsuredLov();
            $('#subclass-frm').select2('val', null);
            populateSubclassLov();
            $('#covertypes-frm').select2('val', null);
            populateCoverTypesLov();
            quotId = s.quoteId;
            console.log(quotId);
            window.location.href = SERVLET_CONTEXT + "/protected/quotes/editquote/" + quotId;
            populateQuotDetails();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ButtonIntelligence.finish('btn-add-quote', false, 'Create Failed!');
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        },
        dataType: "json",
        contentType: "application/json"
    });
}

function updateQuote() {
    console.log("using this");
    var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
    var $currForm = $('#quot-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        ButtonIntelligence.finish('btn-add-quote', false, 'Invalid Form!');
        return;
    }
    $('#quot-form input[type=checkbox]').each(function (e) {
        $(this).val($(this).is(':checked'));
    });
    var data = {};
    $currForm.serializeArray().map(function (x) {
        data[x.name] = x.value;
    });
    var url = "createQuotation";
    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(data),
        success: function (s) {
            ButtonIntelligence.finish('btn-add-quote', true, 'Quote Updated!');

            Swal.fire({
                title: 'Success',
                text: 'Quote Transaction created Successfully',
                icon: 'success'
            });
            $('#prd-code').select2('val', null);
            createProductForSel();
            $('#insured-frm').select2('val', null);
            populateInsuredLov();
            $('#subclass-frm').select2('val', null);
            populateSubclassLov();
            $('#covertypes-frm').select2('val', null);
            populateCoverTypesLov();
            quotId = s.quoteId;
            console.log(quotId);
            populateQuotDetails();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ButtonIntelligence.finish('btn-add-quote', false, 'Update Failed!');
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        },
        dataType: "json",
        contentType: "application/json"
    });
}

function getPolicyWet() {
    $('#wef-date').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        $("#risk-wef-date").val(dt);
        $("#product-wef-date").val(dt);
        $.ajax({
            type: 'GET',
            url: 'getWetDate',
            dataType: 'json',
            data: {"wefDate": dt},
            async: true,
            success: function (result) {
                $("#wet-date").val(moment(result).format('DD/MM/YYYY'));
                $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));
                $("#product-wet-date").val(moment(result).format('DD/MM/YYYY'));

            },
            error: function (jqXHR, textStatus, errorThrown) {

            }
        });
    });

    $('#product-cover-from').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        $("#risk-wef-date").val(dt);
        $.ajax({
            type: 'GET',
            url: 'getWetDate',
            dataType: 'json',
            data: {"wefDate": dt},
            async: true,
            success: function (result) {
                $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));
                $("#product-wet-date").val(moment(result).format('DD/MM/YYYY'));

            },
            error: function (jqXHR, textStatus, errorThrown) {

            }
        });
    });

    $('#risk-cover-from').on('dp.change', function (ev) {
        var curDate = ev.date;
        var dt = moment(curDate).format('DD/MM/YYYY');
        $.ajax({
            type: 'GET',
            url: 'getWetDate',
            dataType: 'json',
            data: {"wefDate": dt},
            async: true,
            success: function (result) {
                $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));

            },
            error: function (jqXHR, textStatus, errorThrown) {

            }
        });
    });
}

function populateInsuredLov() {
    if ($("#insured-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "insured-frm",
            sort: 'fname',
            change: function (e, a, v) {
                $("#insured-code").val(e.added.tenId);
                populateClientLov();
            },
            formatResult: function (a) {
                if (a.idNo) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                } else {
                    return a.fname + " " + a.otherNames;
                }
            },
            formatSelection: function (a) {
                if (a.idNo) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                } else {
                    return a.fname + " " + a.otherNames;
                }
            },
            initSelection: function (element, callback) {
                var code = $("#insured-code").val();
                var name = $("#insured-name").val();
                var othernames = $("#insured-other-name").val();
                var idNo = $("#insured-id").val();
                var data = {fname: name, otherNames: othernames, tenId: code, idNo: idNo};
                callback(data);
            },
            id: "tenId",
            width: "250px",
            placeholder: "Select Insured"

        });
    }
}

function populateQuotDetails() {
    if (typeof quotId !== 'undefined') {
        if (quotId !== -2000) {
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
            $("#myTab #show-taxes,#show-clauses").show();
            $("#activation-code-display").hide();
            $("#act-display").hide();
            getQuotationDetails();
        } else {
            $("#btn-auth-quote").css("display", "none");
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
            $("#quote-info").hide();
            $("#btn-assign-trans").hide();
        }
    } else {
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
        $("#myTab #show-taxes,#show-clauses").show();

    }
}


function getQuotationDetails() {
    if (typeof quotId !== 'undefined') {
        if (quotId !== -2000) {
            $.ajax({
                url: 'getQuotationDetails/' + quotId,
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s) {
                    console.log(s);
                    $("#quot-id").val(quotId);
                    console.log(s);
                    console.log(s.quotStatus);
                    if (s.quotStatus !== "D" && s.quoteType === "comparison") {
                        var quotationUrl = SERVLET_CONTEXT + '/protected/quotes/rpt_comp_client_quote';
                    } else if (s.quoteType === "combined") {
                        var quotationUrl = SERVLET_CONTEXT + '/protected/quotes/rpt_comb_client_quote';
                    } else {
                        var quotationUrl = SERVLET_CONTEXT + '/protected/quotes/rpt_client_quote';
                    }
                    var quotationSumUrl = SERVLET_CONTEXT + '/protected/quotes/rpt_client_quote_summary';

                    $('#quotation-link').attr('href', quotationUrl);
                    $('#quot-summary-link').attr('href', quotationSumUrl);
                    console.log("Updated Proposal URL: " + quotationUrl);
                    console.log("Updated Proposal URL: " + quotationSumUrl);
                    if (s.quotStatus) {
                        if (s.quotStatus === "D") {
                            $("#quot-summary-link").show();
                            $("#btn-assign-trans").show();
                            $("#btn-add-quote").css("display", "block");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "none");
                            //  $("#btn-cancel-quote").css("display", "none");
                            $("#btn-make-ready-policy").css("display", "block");
                            $("#btn-convert-quote").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display", "none");
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
                            $("#from-date").removeAttr('disabled');
                            $("#wet-date").removeAttr('disabled');
                            $('#clnt-type').removeAttr("disabled");
                            $("#sub-agent-frm").select2("enable", true);
                            $("#introducer-frm").select2("enable", true);
                            $("#marketer-frm").select2("enable", true);
                            $("#leads-man-frm").select2("enable", true);
                            $("#agent-type").prop("disabled", false);
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").show();
                            $("#btn-add-risk").show();
                            $("#sel2").prop("disabled", false);
                            //$("#btn-add-new-clause").show();
                            //$("#btn-add-new-tax").show();
                            $("#btn-add-new-section").show();
                            //$("#btn-add-new-remark").show();
                            //$("#btn-save-remark").show();
                            $("#display-source").hide();
                            $("#display-sourcegroup").hide();
                            $("#edit-sourcegroup").show();
                            $("#edit-source").show();
                            $("#combined-quote").hide();
                            $("#comparison-quote").hide();
                            $("#comparison-type").hide();
                            if (s.quoteType === "combined") {
                                $("#quote-info").text("Combined Quote");
                            } else if (s.quoteType === "comparison") {
                                $("#quote-info").text("Comparison Quote");
                                $("#btn-add-risk").hide();
                                $("#btn-add-new-section").hide();
                                $("#btn-add-quote").hide();
                            } else {
                                $("#quote-info").text("Not Defined");
                            }
                            if (s.quoteType === "combined") {
                                $("#btn-compare-quot").hide();
                            }
                            if (s.quoteType === "comparison") {
                                $("#btn-add-quot-prod").hide();
                            }
                            $("#activation-code-display").hide();
                        } else if (s.quotStatus === "R") {
                            $("#quot-summary-link").hide();
                            $("#btn-assign-trans").hide();
                            $("#sel2").prop("disabled", true);
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "block");
                            $("#btn-confirm-quote").css("display", "none");
                            //$("#btn-cancel-quote").css("display", "none");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-convert-quote").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display", "block");
                            $("#pol-status").text("Ready");
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").attr("disabled", "disabled");
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#sub-agent-frm").select2("enable", false);
                            $("#introducer-frm").select2("enable", false);
                            $("#marketer-frm").select2("enable", false);
                            $("#leads-man-frm").select2("enable", false);
                            $("#agent-type").prop("disabled", true);
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#btn-add-risk").hide();
                            //$("#btn-add-new-clause").show();
                            //$("#btn-add-new-tax").show();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#combined-quote").hide();
                            $("#comparison-quote").hide();
                            $("#comparison-type").hide();
                            $("#btn-compare-quot").hide();
                            if (s.quoteType === "combined") {
                                $("#quote-info").text("Combined Quote");
                            } else if (s.quoteType === "comparison") {
                                $("#quote-info").text("Comparison Quote");
                            } else {
                                $("#quote-info").text("Not Defined");
                            }
                            if (s.quoteType === "combined") {
                                $("#btn-compare-quot").hide();
                            }
                            if (s.quoteType === "comparison") {
                                $("#btn-add-quot-prod").hide();
                            }
                        } else if (s.quotStatus === "A") {
                            $("#quot-summary-link").hide();
                            $("#btn-assign-trans").hide();
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "block");
                            //$("#btn-cancel-quote").css("display", "block");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-convert-quote").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display", "none");
                            $("#pol-status").text("Authorised");
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").prop("disabled", true);
                            $("#policy-remarks").prop("disabled", true);
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $("#sel2").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#sub-agent-frm").select2("enable", false);
                            $("#introducer-frm").select2("enable", false);
                            $("#marketer-frm").select2("enable", false);
                            $("#leads-man-frm").select2("enable", false);
                            $("#agent-type").prop("disabled", true);
                            $("#btn-add-risk").hide();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#combined-quote").hide();
                            $("#comparison-quote").hide();
                            $("#comparison-type").hide();
                            if (s.quoteType === "combined") {
                                $("#quote-info").text("Combined Quote");
                            } else if (s.quoteType === "comparison") {
                                $("#quote-info").text("Comparison Quote");
                            } else {
                                $("#quote-info").text("Not Defined");
                            }
                            if (s.quoteType === "combined") {
                                $("#btn-compare-quot").hide();
                            }
                            if (s.quoteType === "comparison") {
                                $("#btn-add-quot-prod").hide();
                            }
                        } else if (s.quotStatus === "C") {
                            $("#quot-summary-link").hide();
                            $("#btn-assign-trans").hide();
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "none");
                            $("#btn-convert-quote").css("display", "block");
                            // $("#btn-cancel-quote").css("display", "block");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display", "block");
                            $("#pol-status").text("Confirmed");
                            $("#sel2").prop("disabled", true);
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").prop("disabled", true);
                            $("#policy-remarks").prop("disabled", true);
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#sub-agent-frm").select2("enable", false);
                            $("#introducer-frm").select2("enable", false);
                            $("#marketer-frm").select2("enable", false);
                            $("#leads-man-frm").select2("enable", false);
                            $("#agent-type").prop("disabled", true);
                            $("#btn-add-risk").hide();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#btn-compare-quot").hide();
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#combined-quote").hide();
                            $("#comparison-quote").hide();
                            $("#comparison-type").hide();
                            if (s.quoteType === "combined") {
                                $("#quote-info").text("Combined Quote");
                            } else if (s.quoteType === "comparison") {
                                $("#quote-info").text("Comparison Quote");
                            } else {
                                $("#quote-info").text("Not Defined");
                            }
                            if (s.quoteType === "combined") {
                                $("#btn-compare-quot").hide();
                            }
                            if (s.quoteType === "comparison") {
                                $("#btn-add-quot-prod").hide();
                            }
                        } else if (s.quotStatus === "CL") {
                            $("#quot-summary-link").hide();
                            $("#btn-assign-trans").hide();
                            $("#sel2").prop("disabled", true);
                            $("#btn-add-quote").css("display", "none");
                            $("#btn-auth-quote").css("display", "none");
                            $("#btn-confirm-quote").css("display", "none");
                            //  $("#btn-cancel-quote").css("display", "none");
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-undo-make-ready-policy").css("display", "none");
                            $("#pol-status").text("Cancelled");
                            $("#edit-currency").hide();
                            $("#display-currency").show();
                            $("#edit-branch").hide();
                            $("#display-branch").show();
                            $("#edit-payment-mode").hide();
                            $("#display-payment-mode").show();
                            $("#edit-binder").hide();
                            $("#display-binder").show();
                            $("#edit-client").hide();
                            $("#display-client").show();
                            $("#poli-remarks").prop("disabled", true);
                            $("#policy-remarks").prop("disabled", true);
                            $("#pol-buss-type").prop("disabled", true);
                            $("#pol-bin-type").prop("disabled", true);
                            $("#from-date").prop("disabled", true);
                            $("#wet-date").prop("disabled", true);
                            $('#clnt-type').prop("disabled", true);
                            $("#sub-agent-frm").select2("enable", false);
                            $("#introducer-frm").select2("enable", false);
                            $("#marketer-frm").select2("enable", false);
                            $("#leads-man-frm").select2("enable", false);
                            $("#agent-type").prop("disabled", true);
                            $("#btn-add-risk").hide();
                            $("#btn-add-new-clause").hide();
                            $("#btn-add-new-tax").hide();
                            $("#btn-add-new-section").hide();
                            $("#btn-add-new-remark").hide();
                            $("#btn-save-remark").hide();
                            $("#btn-save-quot-prod").hide();
                            $("#btn-cancel-quot-prod").hide();
                            $("#btn-add-quot-prod").hide();
                            $("#display-source").show();
                            $("#display-sourcegroup").show();
                            $("#edit-sourcegroup").hide();
                            $("#edit-source").hide();
                            $("#combined-quote").hide();
                            $("#comparison-quote").hide();
                            $("#comparison-type").hide();
                            if (s.quoteType === "combined") {
                                $("#quote-info").text("Combined Quote");
                            } else if (s.quoteType === "comparison") {
                                $("#quote-info").text("Comparison Quote");
                            } else {
                                $("#quote-info").text("Not Defined");
                            }
                            if (s.quoteType === "combined") {
                                $("#btn-compare-quot").hide();
                            }
                            if (s.quoteType === "comparison") {
                                $("#btn-add-quot-prod").hide();
                            }
                        }
                        $("#display-tl").hide();
                        $("#display-whtx").hide();
                        $("#display-comm").hide();
                        $("#display-suminsured").hide();
                        if (s.quoteType === "comparison") {
                            $("#display-suminsured").hide();
                        }
                    }
                    $("#client-info").text(s.fname + " " + s.otherNames);
                    $("#pay-mode-info").text(s.paymentMode);
                    $("#source-info").text(s.sourceName);
                    $("#source-name").val(s.sourceName);
                    $("#source-id").val(s.sourceId);
                    $("#sourcegroup-id").val(s.sourceGroupId);
                    $("#sourcegroup-name").val(s.sourceGroupName);
                    $("#sourcegroup-info").text(s.sourceGroupName);
                    populateSourceGroupLov();

                    $("#branch-info").text(s.branch);
                    $("#currency-info").text(s.currency);
                    $("#pol-no").text(s.quotNo);
                    $("#h4pol").text("Quote No: " + s.quotNo);
                    $("#pol-rev-no").text(s.quotRevNo);
                    $("#pol-sum-insured").text(UTILITIES.currencyFormat(s.sumInsured));
                    $("#pol-premium").text(UTILITIES.currencyFormat(s.premium));
                    $("#pol-basic-prem").text(UTILITIES.currencyFormat(s.basicPrem));
                    $("#pol-net-prem").text(UTILITIES.currencyFormat(s.netPrem));
                    $("#from-date").val(moment(s.quoteWef).format('DD/MM/YYYY'));
                    $("#wet-date").val(moment(s.quoteWet).format('DD/MM/YYYY'));
                    $('#clnt-type').val(s.clientType);
                    if (s.clientType === 'C') {
                        $(".quot-client").text("Client *");
                        $("#client-div").show();
                        $("#insured-clnt-div").show();
                        $("#prs-div").hide();
                        $("#insured-prs-div").hide();
                        $("#client-id").val(s.tenId);
                        $("#insured-name").val("");
                        $("#insured-code").val("");
                        $("#insured-other-name").val("");
                        $("#insured-id").val("");
                        populateInsuredLov();
                    } else if (s.clientType === 'P') {
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
                        populateInsuredLov();
                    }
                    $("#cur-id").val(s.curCode);
                    $("#cur-name").val(s.currency);
                    populateCurrencyLov();
                    $("#client-id").val(s.tenId);
                    $("#client-f-name").val(s.fname);
                    $("#client-other-name").val(s.otherNames);
                    $("#client-national-id").val(s.idNo);
                    populateClientLov();
                    $("#pm-id").val(s.pmId);
                    $("#pm-name").val(s.paymentMode);
                    populatePaymentModes();
                    $("#brn-id").val(s.obId);
                    $("#brn-name").val(s.branch);
                    $('#pol-comm-amt').text(UTILITIES.currencyFormat(s.commAmt));
                    $('#pol-tl').text(UTILITIES.currencyFormat(s.trainingLevy));
                    $('#pol-phcf').text(UTILITIES.currencyFormat(s.phcf));
                    $('#pol-sd').text(UTILITIES.currencyFormat(s.stampDuty));
                    $('#pol-whtx').text(UTILITIES.currencyFormat(s.whtx));
                    $('#pol-extras').text(UTILITIES.currencyFormat(s.extras));
                    if (s.expiryDate)
                        $("#pol-exp-date").text(moment(s.expiryDate).format('DD/MM/YYYY'));
                    UTILITIES.getProcessActiveDiagram("Q" + quotId);
                    UTILITIES.getTaskActive("Q" + quotId);
                    UTILITIES.getProcessHistory("Q" + quotId);
                    populateUserBranches();
                    $('#section_tbl').DataTable().ajax.url("quotRiskLimits/" + -2000).load();
                    $('#risk_tbl').DataTable().ajax.url("quotProductRisks/" + -2000).load();
                    $('#polTaxesList').DataTable().ajax.url("quotProTaxes/" + -2000).load();
                    $('#prod_tbl').DataTable().ajax.reload();
                    if(s.subAgent){
                        $("#agent-type").val('SUB').trigger('change');
                        $("#sub-agent-id").val(s.subAgentId);
                        $("#sub-agent-name").val(s.subAgent);
                        $("#sub-agent-absaNo-id").val(s.absaNoSubAgent);
                        $("#sub-agent-absaNo-name").val(s.absaNoSubAgent);
                        populateSubAgentsLov();
                    }
                    if(s.marketerAgent){
                        $("#agent-type").val('MRK').trigger('change');
                        $("#marketer-id").val(s.marketerAgentId);
                        $("#marketer-name").val(s.marketerAgent);
                        $("#marketer-absaNo-id").val(s.absaNoMarketer);
                        $("#marketer-absaNo-name").val(s.absaNoMarketer);
                        populateMarketerLov();
                    }
                    if(s.introducerAgent){
                        $("#introducer-id").val(s.introducerAgentId);
                        $("#introducer-name").val(s.introducerAgent);
                        $("#introducer-absa-No-id").val(s.absaNoIntroducer);
                        $("#introducer-absaNo-name").val(s.absaNoIntroducer);
                        populateIntroducerLov();
                    }
                    if(s.leadsMan){
                        $("#leads-man-id").val(s.leadsManId);
                        $("#leads-man-name").val(s.leadsMan);
                        $("#leads-absaNo-id").val(s.absaNoLeadsMan);
                        $("#leads-absaNo-name").val(s.absaNoLeadsMan);
                        populateLeadsManLov();
                    }
                },
                error: function (xhr, error) {
                    bootbox.alert(xhr.responseText);
                }
            });
        }
    }
}


function stringToDate(_date, _format, _delimiter) {
    var formatLowerCase = _format.toLowerCase();
    var formatItems = formatLowerCase.split(_delimiter);
    var dateItems = _date.split(_delimiter);
    var monthIndex = formatItems.indexOf("mm");
    var dayIndex = formatItems.indexOf("dd");
    var yearIndex = formatItems.indexOf("yyyy");
    var month = parseInt(dateItems[monthIndex]);
    month -= 1;
    var formatedDate = new Date(dateItems[yearIndex], month, dateItems[dayIndex]);
    return formatedDate;
}

function populateSourceGroupLov2() {
    if ($("#sourcegroup-frm").filter("div").html() != undefined) {

        Select2Builder.initAjaxSelect2({
            containerId: "sourcegroup-frm",
            sort: 'desc',
            change: function (e, a, v) {
                $("#sourcegroup-id").val(e.added.srcGroupId);
                $('#source-id').val("");
                $("#source-name").val("");
                populateSourcesLov(e.added.srcGroupId);


            },
            formatResult: function (a) {
                return a.desc;
            },
            formatSelection: function (a) {
                return a.desc;
            },
            initSelection: function (element, callback) {
                var code = $('#sourcegroup-id').val();
                var name = $("#sourcegroup-name").val();
                var data = {desc: name, srcGroupId: code};
                callback(data);
                populateSourcesLov($('#sourcegroup-id').val());
            },
            id: "srcGroupId",
            width: "250px",
            placeholder: "Select Source Group"

        });

    }

}

function populateUserBranches() {
    if ($("#brn-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "brn-frm",
            sort: 'obName',
            change: function (e, a, v) {
                $("#brn-id").val(e.added.obId);
            },
            formatResult: function (a) {
                return a.obName;
            },
            formatSelection: function (a) {
                return a.obName;
            },
            initSelection: function (element, callback) {
                var code = $('#brn-id').val();
                var name = $("#brn-name").val();
                var data = {obName: name, obId: code};
                callback(data);
            },
            id: "obId",
            width: "250px",
            placeholder: "Select Branch"

        });
    }
}


function populatePaymentModes() {
    if ($("#pm-mode-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "pm-mode-frm",
            sort: 'pmDesc',
            change: function (e, a, v) {
                $("#pm-id").val(e.added.pmId);
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
}

function populateBinderLov() {
    var selectedProductId = $("#prd-id").val();
    if ($("#binder-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "binder-frm",
            sort: 'binName',
            change: function (e, a, v) {
                console.log(e)
                $("#risk-binder-code").val(e.added.binId);
                $("#risk-bind-code").val(e.added.binId);
                $("#pol-ins-comp").text(e.added.name);
                $("#pol-prod-name").text(e.added.proDesc);
                $("#product-id").val(e.added.proCode);
                $("#pol-agent-id").val(e.added.acctId);
                $("#bind-name").val(e.added.binName);
                $("#binder-id").val(e.added.binId);
                $("#risk-binder").text(e.added.binName);
                $("#comm-rate").val();
                populateSubclassLov(e.added.binId);
            },
            formatResult: function (a) {
                return a.binName;
            },
            formatSelection: function (a) {
                return a.binName;
            },
            initSelection: function (element, callback) {
                var code = $('#binder-id').val();
                var name = $("#bind-name").val();
                var data = {binName: name, binId: code};
                callback(data);
            },
            id: "binId",
            width: "250px",
            params: {
                bindType: $("#pol-bin-type").val(),
                productId: selectedProductId
            },
            placeholder: "Select Contract"
        });
    }
}


function populateSubclassLov(bindId) {
    if ($("#subclass-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "subclass-frm",
            sort: 'detId',
            change: function (e, a, v) {
                $("#risk-sub-code").val(e.added.subId);
                populateCoverTypesLov();

            },
            formatResult: function (a) {
                return a.subDesc;
            },
            formatSelection: function (a) {
                return a.subDesc;
            },
            initSelection: function (element, callback) {
                var code = $('#risk-sub-code').val();
                var name = $("#sub-name").val();
                var data = {subDesc: name, subId: code};
                callback(data);
            },
            id: "subDesc",
            width: "250px",
            params: {bindCode: bindId},
            placeholder: "Select Classification"

        });
    }
}


function populateCoverTypesLov() {
    if ($("#covertypes-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "covertypes-frm",
            sort: 'detId',
            change: function (e, a, v) {
                console.log(e.added);
                if (e.added.commRate) {
                    $("#comm-rate").val(e.added.commRate).prop('readonly', true);
                } else {
                    $("#comm-rate").prop('readonly', true);
                }
                $("#risk-cov-code").val(e.added.covId);
                $("#binder-det-id").val(e.added.detId);
                getPremiumRates(e.added.detId);
            },
            formatResult: function (a) {
                return a.covName;
            },
            formatSelection: function (a) {
                return a.covName;
            },
            initSelection: function (element, callback) {
                var code = $('#risk-cov-code').val();
                var name = $("#cover-name").val();
                var data = {covName: name, covId: code};
                callback(data);
            },
            id: "covName",
            width: "250px",
            params: {bindCode: $("#risk-bind-code").val(), subCode: $("#risk-sub-code").val()},
            placeholder: "Select Cover Type"

        });
    }
}


function populateCurrencyLov() {
    if ($("#curr-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "curr-frm",
            sort: 'curName',
            change: function (e, a, v) {
                $("#cur-id").val(e.added.curCode);

            },
            formatResult: function (a) {
                return a.curName;
            },
            formatSelection: function (a) {
                return a.curName;
            },
            initSelection: function (element, callback) {
                var code = $('#cur-id').val();
                var name = $("#cur-name").val();
                var data = {curName: name, curCode: code};
                callback(data);
            },
            id: "curCode",
            width: "250px",
            placeholder: "Select Currency"

        });
    }
}


function populateClientLov() {
    if ($("#client-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "client-frm",
            sort: 'fname',
            change: function (e, a, v) {
                $("#client-id").val(e.added.tenId);
                $("#insured-name").val(e.added.fname);
                $("#insured-code").val(e.added.tenId);
                $("#insured-other-name").val(e.added.otherNames);
                $("#insured-type").val("C");
                $("#insured-id").val(e.added.idNo);
                populateInsuredLov();

            },
            formatResult: function (a) {
                if (a.idNo) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                } else {
                    return a.fname + " " + a.otherNames;
                }
            },
            formatSelection: function (a) {
                if (a.idNo) {
                    return a.fname + " " + a.otherNames + " - " + a.idNo;
                } else {
                    return a.fname + " " + a.otherNames;
                }
            },
            initSelection: function (element, callback) {
                var code = $("#client-id").val();
                var name = $("#client-f-name").val();
                var othernames = $("#client-other-name").val();
                var idNo = $("#client-national-id").val();
                var data = {fname: name, otherNames: othernames, tenId: code, idNo: idNo};
                callback(data);
            },
            id: "tenId",
            placeholder: "Select Client"

        });
    }

    if($("#prospects-frm").filter("div").html() != undefined)
    {
        Select2Builder.initAjaxSelect2({
            containerId : "prospects-frm",
            sort : 'fname',
            change: function(e, a, v){
                $("#prs-search-name").val(e.added.tenId);

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
                    var idNo = $("#client-national-id").val();
                    var data = {fname: name, otherNames: othernames, tenId: code, idNo: idNo};
                    callback(data);
            },
            id: "tenId",
            placeholder:"Select Prospect"

        });
    }

    $("#prospects-frm").on("select2-removed", function(e) {
        $("#prs-search-name").val('');
    })
}

function SegmentCode() {
    if ($("#segment-code").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "segment-code",
            sort: 'segName',
            change: function (e, a, v) {
                $("#segmentName").val(e.added.segName);
                $("#segmentId").val(e.added.segId);
                // var segmentId = e.added.segId;
                // fetchSegmentDetails(segmentId);
            },
            formatResult: function (a) {
                return a.segName;
            },
            formatSelection: function (a) {
                return a.segName;
            },
            initSelection: function (element, callback) {
                var name = $("#segmentName").val();
                var id = $("#segmentId").val();
                var data = {segName: name, segId: id};
                callback(data);
            },
            id: "segId",
            placeholder: "Select Segment",
        });
    }
}


function getPremiumRates(detId) {
    $.ajax({
        type: 'GET',
        url: 'getBinderPremRates',
        dataType: 'json',
        data: {"detId": detId},
        async: true,
        success: function (result) {
            $("#section_form_tbl tbody").each(function () {
                $(this).remove();
            });
            for (var res in result) {
                console.log(result[res]);
                var markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                    "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "' readonly></td></td><td>" +
                    "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" +
                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                if ($("#pol-bin-type").val() === "B") {
                    if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                        markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control">' +
                            "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "' readonly></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                    } else if (result[res].rider && result[res].rider === "Y") {
                        markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "' readonly></td><td><input type='text' class='divFactor form-control' value='" + result[res].divFactor + "' readonly></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "' readonly></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                    } else {
                        markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control">' +
                            "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "' readonly></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                    }
                } else {
                    if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                        markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control">' +
                            "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                    } else if (result[res].rider && result[res].rider === "Y") {
                        markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                    }
                }
                $("#section_form_tbl").append(markup);
            }

            $("#section_form_tbl tr").find("input[type=text]").number(true, 2);
        },
        error: function (jqXHR, textStatus, errorThrown) {

        }
    });

}


function getQuotProducts(s) {
    var url = "quotProducts";
    var currTable = $('#prod_tbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [[10, 15, 20], [10, 15, 20]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "binder",
                "render": function (data, type, full, meta) {

                    return full.binder;
                }
            },
            {
                "data": "account",
                "render": function (data, type, full, meta) {

                    return full.account;
                }
            },
            {
                "data": "product",
                "render": function (data, type, full, meta) {

                    return full.product;
                }
            },
            // { "data": "wef",
            //     "render": function ( data, type, full, meta ) {
            //         return moment(full.wef).format('DD/MM/YYYY');
            //     }
            // },
            // { "data": "wet" ,
            //     "render": function ( data, type, full, meta ) {
            //         return moment(full.wet).format('DD/MM/YYYY');
            //     }
            // },
            {
                "data": "sumInsured",
                "render": function (data, type, full, meta) {

                    return UTILITIES.currencyFormat(full.sumInsured);
                }
            },
            {
                "data": "premium",
                "render": function (data, type, full, meta) {

                    return UTILITIES.currencyFormat(full.premium);
                }
            },
            {
                "data": "commAmt",
                "render": function (data, type, full, meta) {

                    return UTILITIES.currencyFormat(full.commAmt);
                }
            },
            {
                "data": "quotConverted",
                "render": function (data, type, full, meta) {
                    return full.quotConverted === "Y" ? "Converted" : "Not Converted";
                }
            },
            {
                "data": "quoteProductId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="editQuoteProd(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="editQuoteProd(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="editQuoteProd(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "quoteProductId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteQuotProd(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteQuotProd(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-quotprod=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteQuotProd(this);"><i class="fa fa-pencil-square-o"></button>';

                }

            },
        ]
    });

    $('#prod_tbl tbody').on('click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        console.log(aData);
        if (aData[0] === undefined || aData[0] === null) {

        } else {
            $("#risk-binder").text(aData[0].binder);
            $("#risk-binder-id").val(aData[0].binderId);
            $("#risk-binder-code").val(aData[0].binderId);
            $("#risk-bind-code").val(aData[0].binderId);
            $("#binder-id").val(aData[0].binderId);
            $("#product-id").val(aData[0].productId);
            $("#pol-agent-id").val(aData[0].acctId);
            $("#bind-name").val(aData[0].binder);
            $("#prod-binder").val(aData[0].bindType);
            $("#quot-prod-id-pk").val(aData[0].quoteProductId);
            $("#risk-det-id-pk").val(aData[0].binderId);
            $('#risk_tbl').DataTable().ajax.url("quotProductRisks/" + aData[0].quoteProductId).load();
            $('#polTaxesList').DataTable().ajax.url("quotProTaxes/" + aData[0].quoteProductId).load();
            createPolicyClauses(aData[0].quoteProductId);

        }
    });

    return currTable;
}

function editQuoteProd(button) {
    var product = JSON.parse(decodeURI($(button).data("quotprod")));
    $("#pol-ins-comp").text(product["account"]);
    $("#pol-prod-name").text(product["product"]);
    $("#quot-prod-pk").val(product["quoteProductId"]);
    $("#product-id").val(product["productId"]);
    $("#pol-agent-id").val(product["acctId"]);
    $("#bind-name").val(product["binder"]);
    $("#binder-id").val(product["binderId"]);
    $("#pol-bin-type").val(product["bindType"]);
    $("#product-wef-date").val(moment(product["wef"]).format('DD/MM/YYYY'));
    $("#product-wet-date").val(moment(product["wet"]).format('DD/MM/YYYY'));
    populateBinderLov();
    $("#quot-prod-tbl").hide();
    $("#quot-prod-div").show();
    $("#btn-save-quot-prod").show();
    $("#btn-cancel-quot-prod").show();
    $("#btn-add-quot-prod").hide();
}

function deleteQuotProd(button) {
    var product = JSON.parse(decodeURI($(button).data("quotprod")));
    bootbox.confirm("Are you sure want to delete " + product['product'].proDesc + "?", function (result) {
        if (result) {
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url: 'deleteQuoteProduct/' + product['quoteProductId'],
                dataType: 'json',
                async: true,
                success: function (result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Record created/updated Successfully',
                        icon: 'success'
                    })
                    $('#prod_tbl').DataTable().ajax.reload();
                    $('#risk_tbl').DataTable().ajax.url("quotProductRisks/" + -2000).load();
                    populateQuotDetails();
                },
                error: function (jqXHR, textStatus, errorThrown) {
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


function getProductRisks(qpCode) {

    var url = "quotProductRisks/" + qpCode;
    var currTable = $('#risk_tbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [[10, 15, 20], [10, 15, 20]],
        pageLength: 10,
        destroy: false,
        "columns": [
            {"data": "riskShtDesc"},
            {"data": "riskDesc"},
            // { "data": "wefDate",
            //     "render": function ( data, type, full, meta ) {
            //         return moment(full.wefDate).format('DD/MM/YYYY');
            //     }
            // },
            // { "data": "wetDate" ,
            //     "render": function ( data, type, full, meta ) {
            //         return moment(full.wetDate).format('DD/MM/YYYY');
            //     }
            // },
            {
                "data": "subDesc",
                "render": function (data, type, full, meta) {

                    return full.subDesc;
                }
            },
            {
                "data": "covName",
                "render": function (data, type, full, meta) {

                    return full.covName;
                }
            },
            {
                "data": "sumInsured",
                "render": function (data, type, full, meta) {

                    return UTILITIES.currencyFormat(full.sumInsured);
                }
            },
            {
                "data": "premium",
                "render": function (data, type, full, meta) {

                    return UTILITIES.currencyFormat(full.premium);
                }
            },
            {
                "data": "riskId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="editQuoteRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="editQuoteRisk(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="editQuoteRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "riskId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRisk(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRisk(this);"><i class="fa fa-pencil-square-o"></button>';

                }

            },
        ]
    });

    $('#risk_tbl tbody').on('click', 'tr', function () {
        $(this).addClass('table-primary').siblings().removeClass('table-primary');
        var aData = currTable.rows('.table-primary').data();
        if (aData[0] === undefined || aData[0] === null) {

        } else {
            $("#risk-code-pk").val(aData[0].riskId);
            $('#section_tbl').DataTable().ajax.url("quotRiskLimits/" + aData[0].riskId).load();
        }
    });

    return currTable;
}


function deleteRisk(button) {
    var risks = JSON.parse(decodeURI($(button).data("policyrisks")));
    bootbox.confirm("Are you sure want to delete " + risks['riskShtDesc'] + "?", function (result) {
        if (result) {
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url: 'deleteProductRisk/' + risks['riskId'],
                dataType: 'json',
                async: true,
                success: function (result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Record created/updated Successfully',
                        icon: 'success'
                    })
                    $('#risk_tbl').DataTable().ajax.reload();
                    $('#section_tbl').DataTable().ajax.url("quotRiskLimits/" + -2000).load();
                    populateQuotDetails();
                },
                error: function (jqXHR, textStatus, errorThrown) {
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


function getRiskSections(riskCode) {
    var url = "quotRiskLimits/" + riskCode;
    return $('#section_tbl').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [[10, 15, 20], [10, 15, 20]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "secName",
                "render": function (data, type, full, meta) {

                    return full.secName;
                }
            },
            {
                "data": "amount",
                "render": function (data, type, full, meta) {

                    return UTILITIES.currencyFormat(full.amount);
                }
            },
            {"data": "rate"},
            {
                "data": "prem",
                "render": function (data, type, full, meta) {

                    return UTILITIES.currencyFormat(full.prem);
                }
            },
            {"data": "divFactor"},
            {"data": "freeLimit"},
            {
                "data": "sectId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="editRiskSection(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="editRiskSection(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="editRiskSection(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "sectId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D")
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskSection(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskSection(this);" disabled><i class="fa fa-trash-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteRiskSection(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    });
}


function deleteRiskSection(button) {
    var section = JSON.parse(decodeURI($(button).data("risksections")));
    bootbox.confirm("Are you sure want to delete the selected section?", function (result) {
        if (result) {
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url: 'deleteRiskSection/' + section['sectId'],
                dataType: 'json',
                async: true,
                success: function (result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Record created/updated Successfully',
                        icon: 'success'
                    })
                    $('#section_tbl').DataTable().ajax.reload();
                    populateQuotDetails();
                },
                error: function (jqXHR, textStatus, errorThrown) {
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


function editRiskSection(button) {
    var section = JSON.parse(decodeURI($(button).data("risksections")));
    $("#sect-code-pk").val(section['sectId']);
    $("#sect-limit-amt").val(section['amount']);
    $("#sect-rate").val(section['rate']);
    $("#sect-free-limit").val(section['freeLimit']);
    $("#sect-annual-earning").val(section['annualEarnings']);
    $("#sect-div-fact").val(section['divFactor']);
    $("#sect-multi-rate").val(section['multiRate']);
    //$("#chk-compute").prop("checked", section["compute"]);
    $("#risk-sect-id").val(section['secId']);
    $("#risk-sect-name").val(section['secName']);
    $("#sect-prem-id-pk").val(section['rateId']);
    $("#risk-sect-code-pk").val(section['riskId']);
    if ($("#prod-binder").val() === "B") {
        $("#sect-rate").prop("readonly", true);
        $("#sect-free-limit").prop("readonly", true);
        $('#sect-div-fact option:not(:selected)').prop('readonly', true);
        $("#sect-multi-rate").prop("readonly", true);
    } else {
        $("#sect-rate").prop("readonly", false);
        $("#sect-free-limit").prop("readonly", false);
        $('#sect-div-fact option:not(:selected)').prop('readonly', false);
        $("#sect-multi-rate").prop("readonly", false);
    }
    populateRiskSections($("#risk-det-id-pk").val());
    $('#sect-limit-amt,#sect-free-limit').number(true, 2);
    $('#sectModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}


function saveRiskSections() {
    var $classForm = $('#risk-sect-form');
    var validator = $classForm.validate();
    $('#saveRiskSection').click(function () {
        if (!$classForm.valid()) {
            return;
        }

        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "saveRiskSections";
        var request = $.post(url, data);
        request.success(function () {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Success',
                text: 'Record created/updated Successfully',
                icon: 'success'
            })
            $('#prod_tbl').DataTable().ajax.reload();
            $('#risk_tbl').DataTable().ajax.reload();
            $('#section_tbl').DataTable().ajax.reload();
            populateQuotDetails();
            validator.resetForm();
            $('#risk-sect-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#sectModal').modal('hide');
        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
            bootbox.alert(jqXHR.responseText);
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}


function populateRiskSections(detCode) {
    if ($("#risk-sect-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "risk-sect-frm",
            sort: 'desc',
            change: function (e, a, v) {
                //$("#pm-id").val(e.added.pmId);
            },
            formatResult: function (a) {
                return a.desc;
            },
            formatSelection: function (a) {
                return a.desc;
            },
            initSelection: function (element, callback) {
                var code = $('#risk-sect-id').val();
                var name = $("#risk-sect-name").val();
                var data = {desc: name, id: code};
                callback(data);
            },
            id: "id",
            width: "250px",
            params: {detId: detCode},
            placeholder: "Select Section"

        });
    }
}


function createPolicyClauses(qrCode) {
    var url = "quotProClauses/" + qrCode;
    var currTable = $('#polclausesList').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [[5], [5]],
        pageLength: 5,
        destroy: true,
        "columns": [
            {"data": "clauHeading"},
            {
                "data": "clause",
                "render": function (data, type, full, meta) {
                    if (full.clauseType) {
                        if (full.clauseType === 'E') return "Excess";
                        else if (full.clauseType === 'L') return "Limits";
                        else if (full.clauseType === 'C') return "Clause";
                        else return "";
                    } else {
                        return "";
                    }
                }
            },
            {"data": "editable"},
            {"data": "clauWording"},
            {
                "data": "qpClauId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D") {
                            if (full.editable && full.editable === 'Yes') {
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);"><i class="fa fa-pencil-square-o"></button>';
                            } else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                        } else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                }

            },
            {
                "data": "qpClauId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D") {
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="deletePolicyClause(this);"><i class="fa fa-trash-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="deletePolicyClause(this);" disabled><i class="fa fa-trash-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="deletePolicyClause(this);" disabled><i class="fa fa-trash-o"></button>';

                }

            },
        ]
    });
    return currTable;
}

function createQuoteClauses() {
    var $classForm = $('#pol-clause-form');
    var validator = $classForm.validate();
    $('#savepolClauseBtn').click(function () {
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createQuoteClause";
        var request = $.post(url, data);
        request.success(function () {
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

        request.error(function (jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}


function editPolicyClause(button) {
    var clause = JSON.parse(decodeURI($(button).data("clauses")));
    console.log(clause);
    if (!clause['editable']) {
        bootbox.alert("The Selected Clause is not Editable");
        return;
    }
    $("#pol-clause-code").val(clause['qpClauId']);
    $("#sub-clause-code").val(clause['clauseId']);
    $("#sub-clau-id").val(clause['clauShtDesc']);
    $("#sub-clause-name").val(clause['clauHeading']);
    $("#chk-cl-editable").prop("checked", clause['editable']);
    $("#sub-cla-wording").val(clause['clauWording']);
    $("#clause-pol-code").val(clause['quoteProductId'])
    $('#clauseModal').modal({
        backdrop: 'static',
        keyboard: true
    })
}


function createPolicyTaxes(qrCode) {
    var url = "quotProTaxes/" + qrCode;
    var currTable = $('#polTaxesList').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [[5], [5]],
        pageLength: 5,
        destroy: true,
        "columns": [
            {
                "data": "revItemCode",
                "render": function (data, type, full, meta) {
                    return UTILITIES.getRevDesc(full.revItemCode);
                }
            },
            {"data": "taxRate"},
            {"data": "divFactor"},
            {"data": "rateType"},
            {
                "data": "taxAmount",
                "render": function (data, type, full, meta) {

                    return UTILITIES.currencyFormat(full.taxAmount);
                }
            },
            {
                "data": "taxLevel",
                "render": function (data, type, full, meta) {
                    if (full.taxLevel) {
                        if (full.taxLevel === "R")
                            return "Risk";
                        else if (full.taxLevel === "P")
                            return "Policy";
                    } else
                        return "Policy";

                }
            },
            {
                "data": "polTaxId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D")
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);"><i class="fa fa-pencil-square-o"></button>';
                        else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                }

            },
            {
                "data": "polTaxId",
                "render": function (data, type, full, meta) {
                    if (full.quotStatus) {
                        if (full.quotStatus === "D")
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);" disabled><i class="fa fa-trash-o"></button>';

                    } else
                        return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="deletePolTaxes(this);" disabled><i class="fa fa-trash-o"></button>';


                }

            },
        ]
    });
    return currTable;
}

function editPolTaxes(button) {
    var tax = JSON.parse(decodeURI($(button).data("poltaxes")));
    console.log("testing again =" + UTILITIES.getRevDesc(tax["revItemCode"]));

    // $(".categoryDisplay").show();
    var quotId = $("#tax-quot-id").val(quotId);
    var taxId = $("#tax-id").val(tax['polTaxId']);
    $("#Trans-Code").text(UTILITIES.getRevDesc(tax["revItemCode"]));
    var rate = $("#tax-rate").val(tax['taxRate']);
    var divFactor = $("#tax-rate-divfact").val(tax['divFactor']);
    $('#editTaxModal').modal('show');
}

function saveTaxEdits() {

    var taxId = $("#tax-id").val();
    var rate = $("#tax-rate").val();
    var divFactor = $("#tax-rate-divfact").val();
    $.ajax({
        type: 'POST',
        url: 'editQuotGenTax',
        data: {
            'taxRate': rate,
            'polTaxId': taxId,
            'divFactor': divFactor
        },
    }).done(function (s) {
        Swal.fire({
            title: 'Success',
            text: 'Record Edited Successfully',
            icon: 'success'
        });

        $('#polTaxesList').DataTable().ajax.reload();
        getQuotationDetails();

    }).fail(function (jqXHR, textStatus, errorThrown) {
        Swal.fire({
            title: 'Error',
            text: jqXHR.responseText,
            icon: 'error'
        });
    })
}

function deletePolTaxes(button) {
    var tax = JSON.parse(decodeURI($(button).data("poltaxes")));
    bootbox.confirm("Are you sure want to delete?", function (result) {
        if (result) {
            $.ajax({
                type: 'GET',
                url: 'deleteQuotGenTax/' + tax["polTaxId"],
                dataType: 'json',
                async: true,
                success: function (result) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Record created/updated Successfully',
                        icon: 'success'
                    })
                    $('#polTaxesList').DataTable().ajax.reload();
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

    });
}

function getNewPremItemsModal() {

    $("#btn-add-new-section").click(function () {
        if ($("#risk-code-pk").val() != '') {
            $("#prem-item-risk-id").val($("#risk-code-pk").val());
            getNewPremItems("");
            $('#premItemsModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        } else {
            bootbox.alert("Select Risk to add new Premium Item")
        }
    });

    $("#savenewPremItem").on('click', function () {
        var items = getNewCreatePremItems();
        if (items.length == 0) {
            bootbox.alert("No Section Selected to add..");
            return;
        }

        var $currForm = $('#new-prem-items-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }
        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createPremiumItems";

        data.sections = items;
        $.ajax(
            {
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Selected sections added Successfully',
                        icon: 'success'
                    });
                    $('#section_tbl').DataTable().ajax.reload();
                    $('#prod_tbl').DataTable().ajax.reload();
                    $('#risk_tbl').DataTable().ajax.reload();
                    populateQuotDetails();
                    $('#premItemsModal').modal('hide');
                    items = [];
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                },
                dataType: "json",
                contentType: "application/json"
            });
    });
}


function getNewCreatePremItems() {
    var arr = [];
    $("#new_prem_items_form tr").each(function (row, tr) {
        var checked = $(this).find('.section-check').eq(0).is(":checked");
        var section = $(this).find('.section').eq(0).val();
        var premId = $(this).find('.premId').eq(0).val();
        var rate = $(this).find('.rate').eq(0).val();
        var divFactor = $(this).find('.divFactor').eq(0).val();
        var freeLimit = $(this).find('.freeLimit').eq(0).val();
        var amount = $(this).find('.amount').eq(0).val();
        var multiplierRate = $(this).find('.multiplierRate').eq(0).val();
        if (checked) {
            arr.push({
                section: section,
                premId: premId,
                rate: rate,
                divFactor: divFactor,
                freeLimit: freeLimit,
                amount: amount,
                multiplierRate: multiplierRate
            });
        }

    });

    return arr;
}


function getNewPremItems(sectdesc) {
    $.ajax({
        type: 'GET',
        url: 'getNewPremiumItems',
        dataType: 'json',
        data: {"detId": $("#risk-det-id-pk").val(), "riskId": $("#risk-code-pk").val(), "secName": sectdesc},
        async: true,
        success: function (result) {
            // $('#myPleaseWait').modal('hide');
            $("#new_prem_items_form tbody").each(function () {
                $(this).remove();
            });
            for (var res in result) {

                var markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                    "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                    "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
                    "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td><td><input type='text' class='multiplierRate form-control'  value='" + result[res].multiRate + "'></td></tr>";
                if ($("#prod-binder").val() === "B") {
                    if (result[res].section && result[res].section.type === "RD") {
                        markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                            "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required readonly>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "' readonly></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "' readonly></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "' readonly></td><td><input type='text' class='multiplierRate form-control'  value='" + result[res].multiRate + "'></td></tr>";

                    } else {
                        markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                            "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required >' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "' readonly></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "' readonly></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "' readonly></td><td><input type='text' class='multiplierRate form-control'  value='" + result[res].multiRate + "'></td></tr>";

                    }
                }
                $("#new_prem_items_form").append(markup);
            }

            $("#new_prem_items_form tr").find("input[type=text]").number(true, 2);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
        }
    });

}


function addQuoteProd() {
    $("#btn-add-quot-prod").on('click', function () {
        $("#quot-prod-tbl").hide();
        $("#quot-prod-div").show();
        $("#btn-save-quot-prod").show();
        $("#btn-cancel-quot-prod").show();
        $("#btn-add-quot-prod").hide();
        $("#btn-add-risk").hide();
        $("#btn-save-risk").hide();
        $("#btn-save-cancel").hide();
        $('#risk_tbl').DataTable().ajax.url("quotProductRisks/" + -2000).load();
        $('#section_tbl').DataTable().ajax.url("quotRiskLimits/" + -2000).load();
        $("#risk-div").hide();
        $("#risk-form").show();
        $("#sect-div").hide();
        $("#prem-rates-div").show();
        $('#quot-pro-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        $("#pol-ins-comp").text("");
        $("#pol-prod-name").text("");
        $("#insured-code").val("");
        $("#insured-name").val("");
        $("#insured-id").val("");
        $("#insured-other-name").val("");
        $("#pol-bin-type").val("M");
        $("#pol-bin-type").prop("disabled", true);
        $('#insured-frm').select2('val', null);
        $("#binder-frm").select2('val', null);
        populateBinderLov();
        populateInsuredLov();
        $('#subclass-frm').select2('val', null);
        populateSubclassLov();
        $('#covertypes-frm').select2('val', null);
        populateCoverTypesLov();
        getPremiumRates(-2000);
        $('#risk-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");

        $("#prorated-full").val("P");
        $("#risk-form").show();
        $("#risk-div").hide();
        $("#sect-div").hide();
        $("#prem-rates-div").show();
        $("#myTab #show-taxes,#show-clauses").hide();
        $('.nav-tabs a[href="#tab_content1"]').tab('show');
        $("#risk-pro-code-pk").val($("#quot-prod-id-pk").val());
        $("#risk-binder-code").val($("#risk-bind-code").val());
        $("#binder-id").val($("#risk-bind-code").val());
        populateSubclassLov($("#risk-bind-code").val());
        var fromDate = $("#from-date").val();
        var toDate = $("#wet-date").val();
        $("#risk-wef-date").val(fromDate);
        $("#risk-wet-date").val(toDate);
        $("#section_form_tbl tbody").each(function () {
            $(this).remove();
        });
        $("#btn-add-new-section").hide();
        $(".quot-panel").hide();
    });

    $("#btn-cancel-quot-prod").on('click', function () {
        $("#quot-prod-tbl").show();
        $("#quot-prod-div").hide();
        $("#btn-add-quot-prod").show();
        $("#btn-add-risk").show();
        $("#btn-save-risk").show();
        $("#btn-save-cancel").show();
        $(".quot-panel").show();
        $("#risk-div").show();
        $("#sect-div").show();
        $("#risk-form").hide();
        $("#prem-rates-div").hide();
        $("#myTab #show-taxes,#show-clauses").show();
        $('#prod_tbl').DataTable().ajax.reload();
        $('#risk_tbl').DataTable().ajax.url("quotProductRisks/" + -2000).load();
    });
}

function newRisk() {
    $("#btn-add-risk").on('click', function () {
        if ($("#quot-prod-id-pk").val() === '') {
            bootbox.alert("Select Product to Add A Risk");
            return;
        }
        $("#insured-code").val("");
        $("#insured-name").val("");
        $("#insured-other-name").val("");
        $("#insured-id").val("");
        $('#subclass-frm').select2('val', null);
        populateSubclassLov();
        $('#covertypes-frm').select2('val', null);
        populateCoverTypesLov();
        getPremiumRates(-2000);
        $('#risk-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");

        $("#prorated-full").val("P");
        $("#insured-type").val("C");
        $("#insured-clnt-div").show();
        $("#insured-prs-div").hide();
        $("#insured-name").val("");
        $("#insured-code").val("");
        $("#insured-other-name").val("");
        $("#insured-id").val("");
        $('#insured-frm').select2('val', null);
        populateInsuredLov();
        $("#risk-form").show();
        $("#risk-div").hide();
        $("#sect-div").hide();
        $("#prem-rates-div").show();
        $("#btn-save-risk").show();
        $("#btn-save-cancel").show();

        $("#myTab #show-taxes,#show-clauses").hide();
        $("#risk-pro-code-pk").val($("#quot-prod-id-pk").val());
        $("#risk-binder-code").val($("#risk-bind-code").val());
        $("#binder-id").val($("#risk-bind-code").val());
        populateSubclassLov($("#risk-bind-code").val());
        if ($("#comm-rate1").val()) {
            $("#comm-rate").val($("#comm-rate1").val());
        }
        if ($("#prod-binder").val() === "B") {
            $("#comm-rate").prop("readonly", true);
        } else {
            $("#comm-rate").prop("readonly", false);
        }


        var fromDate = $("#from-date").val();
        var toDate = $("#wet-date").val();
        $("#risk-wef-date").val(fromDate);
        $("#risk-wet-date").val(toDate);
        $("#section_form_tbl tbody").each(function () {
            $(this).remove();
        });
        $("#btn-add-new-section").hide();
    });

    $("#btn-save-cancel").on('click', function () {
        populateQuotDetails();
    })
}

function createRisk() {
    var arr = getRskSections();
    if (arr.length == 0) {
        ButtonIntelligence.finish('btn-save-risk', false, 'No Sections!');
        bootbox.alert("Cannot create Risk without sections")
        return false;
    }

    var $currForm = $('#risk-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        ButtonIntelligence.finish('btn-save-risk', false, 'Invalid Form!');
        return;
    }

    var data = {};
    $currForm.serializeArray().map(function (x) {
        data[x.name] = x.value;
    });
    var url = "createRisk";
    data.sections = arr;

    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(data),
        success: function (s) {
            ButtonIntelligence.finish('btn-save-risk', true, 'Risk Created!');

            Swal.fire({
                title: 'Success',
                text: 'Risk Transaction created Successfully',
                icon: 'success'
            });
            arr = [];
            $('#insured-frm').select2('val', null);
            populateInsuredLov();
            $('#subclass-frm').select2('val', null);
            populateSubclassLov($("#risk-bind-code").val());
            $('#covertypes-frm').select2('val', null);
            populateCoverTypesLov();
            $('#section_tbl').DataTable().ajax.reload();
            $('#prod_tbl').DataTable().ajax.reload();
            $('#risk_tbl').DataTable().ajax.reload();
            populateQuotDetails();
            $("#sect-div").show();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ButtonIntelligence.finish('btn-save-risk', false, 'Create Failed!');

            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        },
        dataType: "json",
        contentType: "application/json"
    });
}

function updateRisk() {
    var $currForm = $('#risk-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        ButtonIntelligence.finish('btn-save-risk', false, 'Invalid Form!');
        return;
    }

    var data = {};
    $currForm.serializeArray().map(function (x) {
        data[x.name] = x.value;
    });
    var url = "createRisk";

    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(data),
        success: function (s) {
            ButtonIntelligence.finish('btn-save-risk', true, 'Risk Updated!');

            Swal.fire({
                title: 'Success',
                text: 'Risk Transaction Updated Successfully',
                icon: 'success'
            });

            $('#insured-frm').select2('val', null);
            populateInsuredLov();
            $('#subclass-frm').select2('val', null);
            populateSubclassLov($("#risk-bind-code").val());
            $('#covertypes-frm').select2('val', null);
            populateCoverTypesLov();
            $('#section_tbl').DataTable().ajax.reload();
            $('#prod_tbl').DataTable().ajax.reload();
            $('#risk_tbl').DataTable().ajax.reload();
            populateQuotDetails();
            $("#sect-div").show();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ButtonIntelligence.finish('btn-save-risk', false, 'Update Failed!');

            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        },
        dataType: "json",
        contentType: "application/json"
    });
}

function editQuoteRisk(button) {
    var risks = JSON.parse(decodeURI($(button).data("policyrisks")));
    $("#risk-binder-code").val($("#risk-bind-code").val());
    $("#risk-id").val(risks["riskShtDesc"]);
    $("#risk-desc").val(risks["riskDesc"]);
    $("#risk-wef-date").val(moment(risks["wefDate"]).format('DD/MM/YYYY'));
    $("#risk-wet-date").val(moment(risks["wetDate"]).format('DD/MM/YYYY'));
    $("#prorated-full").val(risks["prorata"]);
    $("#comm-rate").val(risks["commRate"]);
    $("#overrid-prem").val(risks["butchargePrem"]);
    $("#insured-name").val(risks["fname"]);
    $("#insured-code").val(risks["tenId"]);
    $("#insured-other-name").val(risks["otherNames"]);
    $("#insured-id").val(risks["idNo"]);
    $("#insured-type").val(risks["clientType"]);
    if (risks["clientType"] === 'C') {
        $('#insured-frm').select2('val', null);
    } else if (risks["clientType"] === 'P') {
        $('#insured-prs-frm').select2('val', null);
    }
    populateInsuredLov();
    $("#risk-sub-code").val(risks["subId"]);
    $("#sub-name").val(risks["subDesc"]);
    populateSubclassLov($("#risk-bind-code").val());
    $("#risk-cov-code").val(risks["covId"]);
    $("#cover-name").val(risks["covName"]);
    populateCoverTypesLov();
    $("#binder-det-id").val(risks["binderId"]);
    $("#risk-pro-code-pk").val($("#quot-prod-id-pk").val());
    $("#risk-code-pk").val(risks["riskId"]);
    $("#risk-form").show();
    $("#risk-div").hide();
    $("#btn-save-risk").show();
    $("#btn-save-cancel").show();
    $("#myTab #show-taxes,#show-clauses").hide();
    $("#comm-rate").prop("readonly", true);
}
function createQuoteProduct() {
    var $currForm = $('#quot-pro-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        ButtonIntelligence.finish('btn-save-quot-prod', false, 'Invalid Form!');
        return;
    }

    var data = {};
    $currForm.serializeArray().map(function (x) {
        data[x.name] = x.value;
    });
    var arr = getRskSections();
    if (arr.length == 0) {
        ButtonIntelligence.finish('btn-save-quot-prod', false, 'No Sections!');
        bootbox.alert("Cannot create Quote without sections")
        return false;
    }

    data.sections = arr;
    data.riskBean = getRiskDetails();
    var url = "createQuoteProduct";
    console.log('quote object ', data);

    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(data),
        success: function (s) {
            ButtonIntelligence.finish('btn-save-quot-prod', true, 'Product Created!');

            Swal.fire({
                title: 'Success',
                text: 'Quote Product created Successfully',
                icon: 'success'
            });
            $('#binder-frm').select2('val', null);
            populateBinderLov();
            $('#section_tbl').DataTable().ajax.url("quotRiskLimits/" + -2000).load();
            $('#risk_tbl').DataTable().ajax.url("quotProductRisks/" + -2000).load();
            $('#prod_tbl').DataTable().ajax.reload();
            populateQuotDetails();
            $("#quot-prod-div").hide();
            $("#quot-prod-tbl").show();
            $(".quot-panel").show();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ButtonIntelligence.finish('btn-save-quot-prod', false, 'Create Failed!');

            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        },
        dataType: "json",
        contentType: "application/json"
    });
}


function updateQuoteProduct() {
    var $currForm = $('#quot-pro-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        ButtonIntelligence.finish('btn-save-quot-prod', false, 'Invalid Form!');
        return;
    }

    var data = {};
    $currForm.serializeArray().map(function (x) {
        data[x.name] = x.value;
    });

    var url = "createQuoteProduct";
    $.ajax({
        url: url,
        type: "POST",
        data: JSON.stringify(data),
        success: function (s) {
            ButtonIntelligence.finish('btn-save-quot-prod', true, 'Product Updated!');

            Swal.fire({
                title: 'Success',
                text: 'Quote Product updated Successfully',
                icon: 'success'
            });
            $('#binder-frm').select2('val', null);
            populateBinderLov();
            $('#section_tbl').DataTable().ajax.url("quotRiskLimits/" + -2000).load();
            $('#risk_tbl').DataTable().ajax.url("quotProductRisks/" + -2000).load();
            $('#prod_tbl').DataTable().ajax.reload();
            populateQuotDetails();
            $("#quot-prod-div").hide();
            $("#quot-prod-tbl").show();
            $(".quot-panel").show();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            ButtonIntelligence.finish('btn-save-quot-prod', false, 'Update Failed!');

            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            });
        },
        dataType: "json",
        contentType: "application/json"
    });
}

function getNewTaxes(prodId) {

    $.ajax({
        type: 'GET',
        url: 'getNewTaxes',
        dataType: 'json',
        data: {"quoteProdId": prodId},
        async: true,
        success: function (result) {
            // $('#myPleaseWait').modal('hide');
            $("#new_taxes_tbl tbody").each(function () {
                $(this).remove();
            });
            for (var res in result) {
                var markup = "<tr><td><input type='checkbox' class='tax-check'><input type='hidden' class='tax-id form-control' value='" + result[res].polTaxId +
                    "'></td><td>"
                    + UTILITIES.getRevDesc(result[res].revenueItems.item) + "</td><td>"
                    + result[res].taxRate + "</td><td>"
                    + getRateType(result[res].rateType) + "</td></tr>";
                $("#new_taxes_tbl").append(markup);
            }

        },
        error: function (jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
        }
    });

}

function getNewClauses(prodId) {
    $.ajax({
        type: 'GET',
        url: 'getNewClauses/' + prodId,
        dataType: 'json',
        async: true,
        success: function (result) {
            console.log(result);
            // $('#myPleaseWait').modal('hide');
            $("#new_clause_tbl tbody").each(function () {
                $(this).remove();
            });
            for (var res in result) {
                var markup = "<tr><td><input type='checkbox' class='clause-check'><input type='hidden' class='clause-id form-control' value='" + result[res].subClauseId +
                    "'></td><td>"
                    + result[res].header + "</td></tr>";
                $("#new_clause_tbl").append(markup);
            }

        },
        error: function (jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
        }
    });

}

function getCreateNewClauses() {
    var arr = [];
    $("#new_clause_tbl tr").each(function (row, tr) {
        var checked = $(this).find('.clause-check').eq(0).is(":checked");
        var clauseId = $(this).find('.clause-id').eq(0).val();
        if (checked) {
            arr.push(clauseId);
        }

    });

    return arr;
}


function getNewClausesModal() {
    $("#btn-add-new-clause").click(function () {
        if ($("#quot-prod-id-pk").val() !== '') {
            $("#clause-pol-id").val($("#quot-prod-id-pk").val());
            getNewClauses($("#quot-prod-id-pk").val());
            $('#newclausesModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        } else {
            bootbox.alert("Quotation Product Details are not available to add A clause")
        }
    });

    $("#savenewClause").on('click', function () {
        var items = getCreateNewClauses();
        if (items.length == 0) {
            bootbox.alert("No Clauses Selected to add..");
            return;
        }


        var $currForm = $('#new-clause-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }
        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createNewClause";

        data.clauses = items;
        $.ajax(
            {
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Selected Clauses added Successfully',
                        icon: 'success'
                    });
                    $('#polclausesList').DataTable().ajax.reload();
                    $('#newclausesModal').modal('hide');
                    items = [];
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                },
                dataType: "json",
                contentType: "application/json"
            });
    });
}

function getRateType(type) {
    if (type) {
        if (type === "A") return "Amount";
        else if (type === "P") return "Percentage";
        else if (type === "M") return "Per Milli";
    } else {
        return "Amount";
    }
}

function getCreateNewTaxes() {
    var arr = [];
    $("#new_taxes_tbl tr").each(function (row, tr) {
        var checked = $(this).find('.tax-check').eq(0).is(":checked");
        var taxid = $(this).find('.tax-id').eq(0).val();
        if (checked) {
            arr.push(taxid);
        }

    });

    return arr;
}


function getNewTaxesModal() {
    $("#btn-add-new-tax").click(function () {
        if ($("#quot-prod-id-pk").val() != '') {
            $("#new-tax-quot-pr-id").val($("#quot-prod-id-pk").val());
            getNewTaxes($("#quot-prod-id-pk").val());
            $('#newtaxesModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        } else {
            bootbox.alert("Quote Product Details are not available to add A Tax")
        }
    });


    $("#savenewTaxes").click(function () {
        var items = getCreateNewTaxes();
        if (items.length == 0) {
            bootbox.alert("No Tax Selected")
            return;
        }
        var $currForm = $('#new-taxes-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }
        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createNewTax";

        data.taxes = items;
        $.ajax(
            {
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Selected Clauses added Successfully',
                        icon: 'success'
                    });

                    $('#polTaxesList').DataTable().ajax.reload();
                    populateQuotDetails();
                    $('#newtaxesModal').modal('hide');
                    items = [];
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                },
                dataType: "json",
                contentType: "application/json"
            });
    });
}

function compareQuotProd() {
    $("#btn-compare-quot").on('click', function () {
        $("#compare-quot-form").show();
        $('#compareQuotModal').modal({
            backdrop: 'static',
            keyboard: true
        });
        populateComparisonBinderLov();
        saveCompareQuot();

    });
}

function populateComparisonBinderLov() {
    if ($("#comp-binder-frm").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "comp-binder-frm",
            sort: 'binName',
            multiple: true,
            change: function (e, a, v) {

            },
            formatResult: function (a) {
                return a.binName;
            },
            formatSelection: function (a) {
                return a.binName;
            },
            initSelection: function (element, callback) {

            },
            id: "binId",
            width: "250px",
            params: {quotId: quotId},
            placeholder: "Select Contract(s)"

        });
    }
}

function saveCompareQuot() {
    $("#saveComparisonBtn").off('click').on('click', function () {
        var $classForm = $('#compare-quot-form');
        var validator = $classForm.validate();

        if (!$classForm.valid()) {
            return;
        }

        var $btn = $(this).button('loading');
        var data = {
            quotId: window.quotId,
            contractId: []
        };

        console.log(data);

        var selectedValues = $("#comp-binder-frm").select2("val");
        if (Array.isArray(selectedValues)) {
            data.contractId = selectedValues.map(Number);
        } else {
            data.contractId = [Number(selectedValues)];
        }
        var url = SERVLET_CONTEXT + "/protected/quotes/compareQuote";

        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            contentType: "application/json",
            dataType: "json",
            success: function (response) {
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#prod_tbl').DataTable().ajax.reload();
                validator.resetForm();
                $('#compare-quot-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                populateQuotDetails();
                $('#compareQuotModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                $btn.button('reset');
            }
        });
    })
}

function createProductForSel() {
    if ($("#prd-code").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "prd-code",
            sort: 'proDesc',
            change: function (e, a, v) {
                $("#binder-id").val('');
                $("#bind-name").val('');
                $("#binder-frm").select2("val", "");

                // Set selected product ID
                $("#prd-id").val(e.added.proCode);
                populateBinderLov();
            },
            formatResult: function (a) {
                return a.proDesc;
            },
            formatSelection: function (a) {
                return a.proDesc;
            },
            initSelection: function (element, callback) {
                var code = $("#prd-id").val();
                var name = $("#pr-name").val();
                var data = {proDesc: name, proCode: code};
                callback(data);
            },
            id: "proCode",
            placeholder: "Select Product",
        });
    }
}

function convertQuotProduct() {
    if (typeof quotId !== 'undefined') {
        if (quotId !== -2000) {
            $.ajax({
                url: 'getProductDetails/' + quotId,
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s) {
                    console.log(quotId);
                    console.log(s);
                    console.log(s.quoteProductId);
                    if (s.quotType === "combined") {
                        $.ajax({
                            url: 'selectQuoteProductForConvert',
                            type: 'GET',
                            data: {quotId: quotId},
                            dataType: 'json',
                            async: true,
                            success: function (s) {
                                console.log(s);
                                $("#convertCombQuotTable tbody").each(function () {
                                    $(this).remove();
                                });
                                for (var res in s) {
                                    var markup = "<tr><td><input type='checkbox' class='quote-product-checkbox' name='record' id='" + s[res][0] + "'></td><td>" + s[res][1] + "</td><td>" + s[res][2] + "</td><td>" + s[res][3] + "</td><td>" + UTILITIES.currencyFormat(s[res][4]) + "</td></tr>";
                                    $("#convertCombQuotTable").append(markup);
                                }
                            }
                        });
                        $("#convertCombQuot").modal({
                            backdrop: 'static',
                            keyboard: true
                        });
                        $('.quote-product-checkbox').change(function () {
                            if ($(this).is(':checked')) {
                                $('.quote-product-checkbox').not(this).prop('disabled', true);
                            } else {
                                $('.quote-product-checkbox').prop('disabled', false);
                            }
                        });
                        $('#convertCombQuotTable').DataTable({
                            searching: true,
                            paging: false,
                            destroy: true,
                            lengthMenu: [[-1], ["All"]]
                        });
                        $('#saveSelectedProduct').click(function () {
                            if (ButtonIntelligence.isActive('saveSelectedProduct')) return;

                            var selectedQuoteProduct = $('.quote-product-checkbox:checked');
                            if (selectedQuoteProduct.length === 0) {
                                Swal.fire({
                                    title: 'Error',
                                    text: 'Please select one quote product to submit.',
                                    icon: 'error'
                                });
                            } else if (selectedQuoteProduct.length > 1) {
                                Swal.fire({
                                    title: 'Error',
                                    text: 'Only one quote product can be submitted at a time.',
                                    icon: 'error'
                                });
                            } else {
                                if (!ButtonIntelligence.start('saveSelectedProduct', 'Processing...')) return;

                                var selectedQuoteProductId = selectedQuoteProduct.attr('id');
                                console.log("selected: " + selectedQuoteProductId);

                                $("#convertCombQuot").modal('hide');
                                $("#conver-quot-pro-id").val(selectedQuoteProductId);

                                ButtonIntelligence.finish('saveSelectedProduct', true, 'Selected!');

                                $('#converQuotModal').modal({
                                    backdrop: 'static',
                                    keyboard: true
                                });
                                convertQuoteProcess();
                            }
                        });
                    } else if (s.quotType === "comparison") {
                        console.log("here");
                        console.log(s.quoteProductId);
                        if (s.converted === "Y"){
                            Swal.fire({
                                title: 'Error',
                                text: 'The Quote Product is Already Converted.',
                                icon: 'error'
                            });
                            return;
                        }
                        $("#conver-quot-pro-id").val(s.quoteProductId);
                        $('#converQuotModal').modal({
                            backdrop: 'static',
                            keyboard: true
                        });
                        convertQuoteProcess();
                    }
                }
            });
        }
    }
}

function convertQuoteProcess() {
    $("#convertQuotBtn").click(function () {
        if (ButtonIntelligence.isActive('convertQuotBtn')) return;

        var $currForm = $('#convert-quot-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        if (!ButtonIntelligence.start('convertQuotBtn', 'Converting...')) return;

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "convertQuote";

        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            success: function (result) {
                console.log(result);
                if (result.prospectId) {
                    ButtonIntelligence.finish('convertQuotBtn', null, 'Convert');
                    populateQuotProspects(result.prospectId, $("#conver-quot-pro-id").val());
                    return;
                }
                if (result.polId) {
                    console.log(result.polId);
                    if (!$("#polIdField").length) {
                        $('<input>')
                            .attr({
                                type: 'hidden',
                                id: 'polIdField',
                                name: 'policyCode',
                                value: result.polId
                            })
                            .appendTo($currForm.find('.modal-footer'));
                    } else {
                        $("#polIdField").val(result.polId);
                    }
                }

                ButtonIntelligence.finish('convertQuotBtn', true, 'Converted!');

                $('#converQuotModal').modal('hide');
                $('#prod_tbl').DataTable().ajax.reload();

                setTimeout(function() {
                    window.location.href = SERVLET_CONTEXT + "/protected/uw/policies/edituwpolicy";
                }, 1000);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                ButtonIntelligence.finish('convertQuotBtn', false, 'Convert Failed!');

                console.log(jqXHR);
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            },
            dataType: "json",
            contentType: "application/json"
        });
    });
}

function populateQuotProspects(tenId, quoteProductId) {
    $(".prop-status").hide();
    $.ajax({
        type: 'GET',
        url: 'getProspectDetails/' + tenId,
        dataType: 'json',
        async: true,
        success: function (result) {
            console.log(result);
            $("#prospect-cnvt-id-pk").val(tenId);
            $("#prs-cnvt-sht-desc").val(result.prospShtDesc);
            $("#cnvt-fname").val(result.fname);
            $("#cnvt-dob").val(result.dob);
            $("#cnvt-gender").val(result.gender);
            $("#cnvt-prosp-email").val(result.emailAddress);
            $("#cnvt-other-names").val(result.otherNames);
            $("#cnvt-phone-no").val(result.phoneNo);
            var phoneNo = result.phoneNo;
            $("#cnvt-pref-sms-id").val(result.prefixId);
            $("#cnvt-pref-sms-name").val(result.prefix);
            $("#cnvt-pref-phone-id").val(result.prefixId);
            $("#cnvt-pref-phone-name").val(result.prefix);
            $("#cnvt-pol_start-date").val($("#date-from").val());
            defaultCountry1();
            if (phoneNo) {
                $("#cnvt-sms-no").val(phoneNo.substring(phoneNo.length - 9, phoneNo.length));
                $("#cnvt-clnt-phone-no").val(phoneNo.substring(phoneNo.length - 9, phoneNo.length));
            }

            $("#clnt-cnvt-type-id").val(result.clientTypeId);
            $("#clnt-cnvt-type-name").val(result.clientType);
            populateClientTypeLov1();
            $("#cnvt-sel3").val(result.status);
            $("#prosp-cnvt-quot-prd-pk").val(quoteProductId);
            $("#prosp-cnvt-quot-pk").val("");
            getClientDocs(tenId);
            $("#cnvt-address").val("P.O. Box");
            $("#cnvt-sel3").attr("readonly", false);
            SegmentCode();
            $('#convertProspectModal').modal({
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

function getClientDocs(clientId) {
    var url = "prospectDocs/" + clientId;
    var currTable = $('#clientDocsList').DataTable({
        "processing": true,
        "serverSide": true,
        autoWidth: true,
        "ajax": {
            'url': url,
        },
        lengthMenu: [[10, 15, 20], [10, 15, 20]],
        pageLength: 10,
        destroy: true,
        "columns": [
            {
                "data": "requiredDoc",
                "render": function (data, type, full, meta) {

                    return full.requiredDoc.reqShtDesc;
                }
            },
            {
                "data": "requiredDoc",
                "render": function (data, type, full, meta) {

                    return full.requiredDoc.reqDesc;
                }
            },
            {"data": "uploadedFileName"},
            {"data": "checkSum"},
            {
                "data": "cdId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="editClientDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                }

            },
            {
                "data": "cdId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="downloadClientDoc(this);"><i class="fa fa-file-archive-o"></button>';

                }

            },
            {
                "data": "cdId",
                "render": function (data, type, full, meta) {
                    return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="deleteClientDoc(this);"><i class="fa fa-trash-o"></button>';
                }

            },
        ]
    });
    return currTable;
}

function populateClientTypeLov1() {
    if ($("#clnt-cnvt-client-type").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "clnt-cnvt-client-type",
            sort: 'clientType',
            change: function (e, a, v) {
                $("#clnt-cnvt-type-id").val(e.added.typeId);
                $("#clnt-cnvt-type-name").val(e.added.typeDesc);
                if (e.added.clientType === 'C' || e.added.clientType === '') {
                    $("#gender,.gender,#cnvt-lblGender").hide();
                    $("#cnvt-lbl-dob").html("Date of Incorporation");
                    $("#cnvt-lbl-id-no").html("Registration No.");
                    $("#cnvt-clnt-title").select2("enable", false);
                    $('#cnvt-clnt-title').select2('val', null);
                    $("#cnvt-clnt-title-id").val("");
                    $("span.othernames").hide();
                    $("#client-photo").html("Logo");
                    $(".employee-info").hide();
                } else if (e.added.clientType === 'I') {
                    $("#gender,.gender,#cnvt-lblGender").show();
                    $("#cnvt-lbl-dob").html("Date of Birth");
                    $("#lbl-id-no").html("ID No.");
                    $("#cnvt-clnt-title").select2("enable", true);
                    $('#cnvt-clnt-title').select2('val', null);
                    $("#cnvt-clnt-title-id").val("");
                    $("span.othernames").show();
                    $("#client-photo").html("Photo");
                    $(".employee-info").show();
                }
            },
            formatResult: function (a) {
                return a.typeDesc
            },
            formatSelection: function (a) {
                return a.typeDesc
            },
            initSelection: function (element, callback) {
                var code = $("#clnt-cnvt-type-id").val();
                var name = $("#clnt-cnvt-type-name").val();
                var data = {typeDesc: name, typeId: code};
                callback(data);
            },
            id: "typeId",
            placeholder: "Select Client Type",

        });
    }
}

function convertProspects() {
    $('#covertPrspctClnt').click(function () {
        var $classForm = $('#cnvt-prospect-form');
        var validator = $classForm.validate();
        if (!$classForm.valid()) {
            return;
        }
        var $btn = $(this).button('Saving');
        var data = {};
        $classForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createProspectConvert";
        var request = $.post(url, data);
        request.success(function (result) {
            console.log(result);
            Swal.fire({
                title: 'Success',
                text: 'Quote Product converted Successfully',
                icon: 'success'
            });
            $('#convertProspectModal').modal('hide');
            if (result) {
                console.log(result);
                if (!$("#polId_field").length) {
                    $('<input>')
                        .attr({
                            type: 'hidden',
                            id: 'polIdField',
                            name: 'policyCode',
                            value: result
                        })
                        .appendTo($currForm.find('.modal-footer'));
                } else {
                    $("#polId_field").val(result);
                }
            }
            $('#converQuotModal').modal('hide');
            $('#prod_tbl').DataTable().ajax.reload();
            window.location.href = SERVLET_CONTEXT + "/protected/uw/policies/edituwpolicy";

        });

        request.error(function (jqXHR, textStatus, errorThrown) {
            // $('#myPleaseWait').modal('hide');
            Swal.fire({
                title: 'Error',
                text: jqXHR.responseText,
                icon: 'error'
            })
        });
        request.always(function () {
            $btn.button('reset');
        });
    });
}

function defaultCountry1() {
    $.ajax({
        url: SERVLET_CONTEXT + '/protected/clients/setups/getDefaultCountry',
        type: 'GET',
        processData: false,
        contentType: false,
        success: function (s) {
            if (s) {
                $("#cou-id").val(s.couCode);
                $("#cou-prefix").val(s.prefix);
                $("#cou-name").val(s.couName);
                populateCountryLov1();
                populateSmsPrefix1(s.couCode);
            }
        },
        error: function (xhr, error) {

        }
    });

}

function populateTitlesLov1() {
    if ($("#cnvt-clnt-title").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-clnt-title",
            sort: 'titleName',
            change: function (e, a, v) {
                $("#cnvt-clnt-title-id").val(e.added.titleId);
                $("#cnvt-clnt-title-name").val(e.added.titleName);
            },
            formatResult: function (a) {
                return a.titleName
            },
            formatSelection: function (a) {
                return a.titleName
            },
            initSelection: function (element, callback) {
            },
            id: "titleId",
            placeholder: "Select Title",

        });
    }
}

function createClientTownLov1() {
    if ($("#cnvt-town-code-lov").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-town-code-lov",
            sort: 'ctName',
            change: function (e, a, v) {
                $("#cnvt-clnt-town-id").val(e.added.ctCode);
                populatePostalCode1(e.added.ctCode);
            },
            formatResult: function (a) {
                return a.ctName
            },
            formatSelection: function (a) {
                return a.ctName
            },
            initSelection: function (element, callback) {
                var code = $("#cnvt-clnt-town-id").val();
                var name = $("#cnvt-clnt-town-name").val();
                var data = {ctName: name, ctCode: code};
                callback(data);
            },
            id: "ctCode",
            placeholder: "Select Town",
        });
    }
}

function populatePostalCode1(townCode) {
    if ($("#cnvt-postal-code").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-postal-code",
            sort: 'zipCode',
            change: function (e, a, v) {
                $("#cnvt-postal-code-id").val(e.added.pcode);
            },
            formatResult: function (a) {
                return a.zipCode
            },
            formatSelection: function (a) {
                return a.zipCode
            },
            initSelection: function (element, callback) {
                var code = $("#cnvt-postal-code-id").val();
                var name = $("#cnvt-postal-name").val();
                var data = {zipCode: name, pcode: code};
                callback(data);
            },
            id: "pcode",
            placeholder: "Select Postal Code",
            params: {townCode: townCode}

        });
    }
}

function populateCountryLov1() {
    if ($("#cnvt-clnt-country").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-clnt-country",
            sort: 'couName',
            change: function (e, a, v) {
                $("#cnvt-cou-id").val(e.added.couCode);
                $("#cnvt-cou-prefix").val(e.added.prefix);
                populateSmsPrefix(e.added.couCode);
            },
            formatResult: function (a) {
                return a.couName
            },
            formatSelection: function (a) {
                return a.couName
            },
            initSelection: function (element, callback) {
                var code = $("#cnvt-cou-id").val();
                var name = $("#cnvt-cou-name").val();
                var data = {couName: name, couCode: code};
                callback(data);
            },
            id: "couCode",
            placeholder: "Select Country",

        });
    }
}


function populateSmsPrefix1(couCode) {
    if ($("#cnvt-sms-pref").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-sms-pref",
            sort: 'prefixName',
            change: function (e, a, v) {
                $("#cnvt-pref-sms-id").val(e.added.prefixId);
            },
            formatResult: function (a) {
                return a.prefixName
            },
            formatSelection: function (a) {
                return a.prefixName
            },
            initSelection: function (element, callback) {
                var code = $("#cnvt-pref-sms-id").val();
                var name = $("#cnvt-pref-sms-name").val();
                var data = {prefixName: name, prefixId: code};
                callback(data);
            },
            id: "prefixId",
            placeholder: "Prefix",
            params: {couCode: couCode}

        });
    }

    if ($("#cnvt-phone-pref").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-phone-pref",
            sort: 'prefixName',
            change: function (e, a, v) {
                $("#cnvt-pref-phone-id").val(e.added.prefixId);
            },
            formatResult: function (a) {
                return a.prefixName
            },
            formatSelection: function (a) {
                return a.prefixName
            },
            initSelection: function (element, callback) {
                var code = $("#cnvt-pref-phone-id").val();
                var name = $("#cnvt-pref-phone-name").val();
                var data = {prefixName: name, prefixId: code};
                callback(data);
            },
            id: "prefixId",
            placeholder: "Prefix",
            params: {couCode: couCode}

        });
    }
}

function createSectorSelect1() {
    if ($("#cnvt-sect-def").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-sect-def",
            sort: 'name',
            change: function (e, a, v) {
                $("#cnvt-sect-id").val(e.added.code);
                populateOccupations1(e.added.code);
            },
            formatResult: function (a) {
                return a.name
            },
            formatSelection: function (a) {
                return a.name
            },
            initSelection: function (element, callback) {
            },
            id: "code",
            placeholder: "Select Sector",
        });
    }
}

function populateOccupations1(sectCode) {
    if ($("#cnvt-occ-def").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-occ-def",
            sort: 'name',
            change: function (e, a, v) {
                $("#cnvt-occ-id").val(e.added.code);
            },
            formatResult: function (a) {
                return a.name
            },
            formatSelection: function (a) {
                return a.name
            },
            initSelection: function (element, callback) {
            },
            id: "code",
            placeholder: "Select Occupation",
            params: {sectCode: sectCode}

        });
    }
}

function populateBranchLov2() {
    if ($("#cnvt-ten-branch").filter("div").html() != undefined) {
        Select2Builder.initAjaxSelect2({
            containerId: "cnvt-ten-branch",
            sort: 'obName',
            change: function (e, a, v) {
                $("#cnvt-obId").val(e.added.obId);
            },
            formatResult: function (a) {
                return a.obName
            },
            formatSelection: function (a) {
                return a.obName
            },
            initSelection: function (element, callback) {
            },
            id: "obId",
            placeholder: "Select Branch",

        });
    }
}






