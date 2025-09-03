var UWScreen = (function ($) {
    'use strict';
    var activeProcessingButton = null;
    var activeProcessingButtonText = null;
    var selRiskCode = -2000;
    $("#sumassured-amt").number(true, 2);
    $("#premium-amt").number(true, 2);
    var createSectorSelect = function () {
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
    };

    // function formatWithCommas(input) {
    //     let value = input.value.replace(/,/g, '').trim();
    //
    //     if (!value) {
    //         input.value = '';
    //         return;
    //     }
    //
    //     if (isNaN(value)) return;
    //
    //     let parts = value.split('.');
    //     parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    //
    //     if (parts[1]) {
    //         parts[1] = parts[1].substring(0, 2); // Keep max 2 decimal digits
    //     }
    //
    //     input.value = parts.join('.');
    // }
    //
    //
    var getRefundDocs = function () {
        var url = "riskRefundDocs/" + selRiskCode;

        var currTable = $('#refund_docs_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {
                        return full.polRevNo;
                    }
                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {
                        return full.docShtDesc;
                    }
                },
                {"data": "uploadedFileName"},
                {"data": "uploadedBy"},
                {
                    "data": "uploadedDate",
                    "render": function(data, type, full, meta) {
                        return full.uploadedDate ? moment(full.uploadedDate).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {"data": "verifiedBy"},
                {
                    "data": "verifiedDate",
                    "render": function(data, type, full, meta) {
                        return full.verifiedDate ? moment(full.verifiedDate).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {
                    "render": function(data, type, full, meta) {
                        const isMaker = full.createdBy === currentUserId || isPolicyCreator;
                        const isVerified = !!full.verifiedDate;

                        if (isMaker && !isVerified) return ''; // Maker can't verify and it's not verified

                        if (isVerified) {
                            // If verified, show a green tick icon
                            return `
                                <div class="d-flex justify-content-center">
                                    <i class="fa fa-check-circle text-success" title="Verified"></i>
                                </div>
                            `;
                        }
                        // Otherwise, show the checkbox (if not verified)
                        return `
                            <div class="form-check d-flex justify-content-center">
                                <input type="checkbox" class="form-check-input verify-checkbox"
                                       data-id="${full.rdId}">
                            </div>
                        `;
                    }
                },

                {
                    "data": "comments",
                    "render": function(data, type, full, meta) {
                        const isMaker = full.createdBy === currentUserId || isPolicyCreator;
                        let hasComment = full.comments && full.comments.trim() !== '';

                        return `
                            <div class="d-flex flex-column">
                                ${hasComment ? `<span class="mb-1 text-muted small">${full.comments}</span>` : ''}
                                ${!isMaker ? `
                                    <button type="button" class="btn btn-success btn-info btn-sm"
                                        onclick="openCommentModalFromText('${full.rdId}', '${hasComment ? full.comments.replace(/'/g, "\\'") : ''}')">
                                        Comment
                                    </button>
                                ` : ''}
                            </div>
                        `;
                    }
                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {
                        if (full.authStatus !== 'A' && full.policyId === polCode && !full.uploadedDate)
                            return '<button type="button" class="btn btn-success btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskDocs(this);">Upload</button>';
                        else
                            return '<button type="button" class="btn btn-success btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskDocs(this);" disabled>Upload</button>';
                    }
                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.downloadRiskDoc(this);">View</button>';
                    }
                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {
                        if (full.authStatus !== 'A' && full.policyId === polCode)
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskDoc(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskDoc(this);" disabled><i class="fa fa-trash-o"></button>';
                    }
                }
            ]
        }));

        // Add function to refresh table
        window.refreshRiskDocsTable = function() {
            currTable.ajax.reload(null, false);
        };
        return currTable;
    };
    var getRefundComments = function () {
        if (typeof polCode !== 'undefined') {
            if (polCode !== -2000) {
                $.ajax({
                    url: 'getRefundComments',
                    type: 'GET',
                    processData: false,
                    contentType: false,
                    success: function (s) {
                        $("#refund-comments-display").val(s.refundComments || '');
                    },
                    error: function (xhr, error) {
                        bootbox.alert(xhr.responseText);
                    }
                });
            }
        }
    };
   var displayAuditTrails = function() {
           console.log("displayAuditTrails called with polCode:", polCode); // Debugging log
           if (polCode !== -2000) {
               $.ajax({
                   type: 'GET',
                   url: SERVLET_CONTEXT + '/protected/life/policies/audit-trails/' + polCode,
                   dataType: 'json',
                   success: function(response) {
                       console.log("Audit trails data:", response); // Debugging log
                       var table = $('#audit_trails_tbl').DataTable({
                           destroy: true,
                           responsive: true, // Enable responsive behavior
                           autoWidth: false, // Disable auto-width to allow full-width styling
                           data: response,
                           columns: [
                               { data: 'checkerName' },
                               { data: 'makerName' },
                               { data: 'auditTime', render: function(data) { return moment(data).format('DD/MM/YYYY HH:mm:ss'); } },
                               { data: 'resubmissionComment' },
                               { data: 'rejectionReasonDesc' }
                           ]
                       });
                   },
                   error: function(jqXHR) {
                       console.error("Audit trails error:", jqXHR.responseText); // Debugging log
                       Swal.fire({
                           title: 'Error',
                           text: jqXHR.responseText,
                           icon: 'error'
                       });
                   }
               });
           }
       };

    var enablepremsa = function (proDesc) {
        console.log(proDesc);
        if (proDesc.toUpperCase().includes("ALAK ENDOWMENT")) {
            $("#premium-amt").val("");
            $(".premium-disp").hide();
            $(".sumassured-disp").show();
            $("#scompute").prop("checked", true);
        }
        if ($("#pcompute").is(":checked")) {
            console.log("radio button  test 1111")
            $("#sumassured-amt").val("");
            $(".sumassured-disp").hide();
            $(".premium-disp").show();

        }
        if ($("#scompute").is(":checked")) {
            console.log("radio button  test 2222")
            $("#premium-amt").val("");
            $(".premium-disp").hide();
            $(".sumassured-disp").show();
        }
        $('input[type=radio][name=computeType]').change(function () {
            if (this.value == 'P') {
                $("#sumassured-amt").val("");
                $(".sumassured-disp").hide();
                $(".premium-disp").show();
            } else if (this.value == 'S') {
                $("#premium-amt").val("");
                $(".premium-disp").hide();
                $(".sumassured-disp").show();
            }
        });
    };
var updatePaymentFrequencyForContracts = function() {
    const paymentFrequencyDropdown = $('#pol-frequency');
    const productNameDisplay = $('#pol-prod-name'); // This ID holds the product description after binder selection

    const selectedProductName = productNameDisplay.text().toUpperCase(); // Get the text of the displayed product name

    // Clear existing options
    paymentFrequencyDropdown.empty();

    if (selectedProductName.includes("NON PORTFOLIO CREDIT LIFE")) {
        // Add only the "Single" option
        paymentFrequencyDropdown.append($('<option>', { value: 'SG', text: 'Single' }));
        // Set "Single" as the default selected value
        paymentFrequencyDropdown.val('SG');
    }
    // Condition for "ULTIMATE PROTECTOR" with all default options (except Single)
    else if (selectedProductName.includes("ULTIMATE PROTECTOR")) {
        // Re-populate with all default options for ULTIMATE PROTECTOR (excluding Single)
        paymentFrequencyDropdown.append($('<option>', { value: '', text: 'Select Payment Frequency' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'M', text: 'Monthly' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'Q', text: 'Quarterly' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'S', text: 'Semi-Annually' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'A', text: 'Annually' }));
        // Optionally set a default 'Select Payment Frequency' or the first valid option
        paymentFrequencyDropdown.val('');
    }
    // Existing condition for specific products with only Monthly and Annually
    else if (selectedProductName.includes("FAMILY PROTECTION PLAN") ||
             selectedProductName.includes("PERSONAL ACCIDENT-LIFE")) {
        // Add only "Monthly" and "Annually" options
        paymentFrequencyDropdown.append($('<option>', { value: 'M', text: 'Monthly' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'A', text: 'Annually' }));
        // Set "Monthly" as selected by default if available
        paymentFrequencyDropdown.val('M'); // Corrected from 'MONTHLY' to 'M' to match the value
    }
    else {
        // Re-populate with all default options for other contracts/products (including Single)
        paymentFrequencyDropdown.append($('<option>', { value: '', text: 'Select Payment Frequency' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'M', text: 'Monthly' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'Q', text: 'Quarterly' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'S', text: 'Semi-Annually' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'A', text: 'Annually' }));
        paymentFrequencyDropdown.append($('<option>', { value: 'SG', text: 'Single' }));
        // Optionally set a default 'Select Payment Frequency' or the first valid option
        paymentFrequencyDropdown.val('');
    }

    // Trigger Select2 update if #pol-frequency is a Select2 element
    if (paymentFrequencyDropdown.data('select2')) {
        paymentFrequencyDropdown.trigger('change.select2');
    }
};    // $(document).ready(function () {
    //     function formatWithCommas(el) {
    //         let val = el.value.replace(/,/g, '');
    //         if (val === '') return;
    //         let parts = val.split('.');
    //         parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    //         el.value = parts.join('.');
    //     }
    //
    //     $("#premium-amt, #sumassured-amt").on("input", function () {
    //         formatWithCommas(this);
    //     });
    //
    //     // Override form submission to clean first
    //     $("form").on("submit", function (e) {
    //         // Clean comma-separated numbers
    //         const premium = $("#premium-amt").val().replace(/,/g, '');
    //         const sumAssured = $("#sumassured-amt").val().replace(/,/g, '');
    //
    //         $("#premium-amt").val(premium);
    //         $("#sumassured-amt").val(sumAssured);
    //
    //         // Optional: Log to confirm
    //         console.log("Cleaned Premium:", premium);
    //         console.log("Cleaned SumAssured:", sumAssured);
    //
    //         // Now allow the form to submit
    //     });
    // });
    //
    //

    function getPolicyTerms(binCode, proDesc) {
        $('#pol-term option').remove();
        $('#pol-term').append($('<option>', {
            value: "",
            text: "Select Term"
        }));
        $.ajax({
            type: 'GET',
            url: 'binderPolTerms/' + binCode,
            dataType: 'json',
            async: true,
            success: function (result) {
                console.log(result);


                for (let res of result) {
                    if ($("#pol-term option[value='" + res.term + "']").length === 0) {
                        $('#pol-term').append($('<option>', {
                            value: res.term,
                            text: res.termDisplay
                        }));
                    }
                }

                enablepremsa(proDesc);
            },
            error: function (jqXHR) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            }
        });
    }
//    function getPolicyTerms(binCode, proDesc) {
//        $('#pol-term option').remove();
//        $('#pol-term').append($('<option>', {
//            value: "",
//            text: "Select Term"
//        }));
//        $.ajax({
//            type: 'GET',
//            url: 'binderPolTerms/' + binCode,
//            dataType: 'json',
//            async: true,
//            success: function (result) {
//                console.log(result);
//                if (proDesc.toUpperCase().includes("AKIBA")) {
//                    const akibaTerms = [6, 9, 12, 15, 18];
//                    for (var res in result) {
//                        if (akibaTerms.includes(result[res].term)) {
//                            $('#pol-term').append($('<option>', {
//                                value: result[res].term,
//                                text: result[res].termDisplay
//                            }));
//                        }
//                    }
//                } else {
//                    for (var res in result) {
//                        $('#pol-term').append($('<option>', {
//                            value: result[res].term,
//                            text: result[res].termDisplay
//                        }));
//                    }
//                }
//                enablepremsa(proDesc);
//            },
//            error: function (jqXHR, textStatus, errorThrown) {
//                Swal.fire({
//                    title: 'Error',
//                    text: jqXHR.responseText,
//                    icon: 'error'
//                });
//            }
//        });
//    };
    var populateOccupations = function () {
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
    };

    var populateBranchLov1 = function () {
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
    };

    var populateClientTypeLov2 = function () {
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
    };

    var populateCountryLov = function () {
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
    };

    var populateTitlesLov = function () {
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
    };

    var populateSmsPrefix = function (couCode) {
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
    };

    var populateClientTypeLov2 = function () {
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
    };

    var saveClientDetails = function () {
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
                $("#ntln-id").val(result.idNo);
                populateClientLov();
                $("#lifeassured-name").val(result.fname);
                $("#lifeassured-code").val(result.tenId);
                $("#lifeassured-other-name").val(result.otherNames);
                $("#life-ntl-id").val(result.idNo);
                populateInsuredLov();
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                validator.resetForm();
                $('#tenant-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#createClientModal').modal('hide');
            });

            request.error(function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    };

    var addNewClient = function () {
        $("#btn-add-client").click(function () {
            $(".clnt-status").hide();
            $('#tenant-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $('#createClientModal').modal({
                backdrop: 'static',
                keyboard: true
            })
        })
    }

    var populatePolicyDetails = function () {

        if (typeof polCode !== 'undefined') {
            if (polCode !== -2000) {
                console.log('polCode ' + polCode);
                $("#btn-uw-reports").show();
                $("#risk-form").hide();
                $("#btn-save-risk").hide();
                $("#btn-save-cancel").hide();
                $("#btn-add-risk").show();
                $("#risk-div").show();
                $("#sect-div").show();
                $("#prem-rates-div").hide();
                $("#btn-import-risk").show();
                getPolicyDetails();
                getRefundComments();
                getPolicyRemakrs();
                $(".show-output").show();
                $("#myTab, #show-taxes, #show-clauses, #end-remarks, #checks-tab, #interested-parties, #est-ben, #instal-schedule, #show_recpts").show();
                $("#display-client").css('display', 'block');
                $("#pol-term").css('display', 'none');
                $("#pol-term-display").css('display', 'block');
                createPolicyClauses();
                createPolicyTaxes();
                $(".import-risks").hide();
                $(".computetype").hide();
                $(".premium-disp").show();
                $(".sumassured-disp").show();
                createPolicyChecks();
                getPolicyInstallments();
                $("#other-pol-details").show();
                $("btn-assign-trans2").show();
                displayAuditTrails();
            } else {
                $(".show-output").css("display", "none");
                $("#btn-auth-policy").css("display", "none");
                $("#btn-dispatch-trans").hide();
                $("#btn-import-risk").hide();
                $("#btn-uw-reports").hide();
                $("#risk-form").show();
                $("#risk-div").hide();
                $("#other-pol-details").hide();
                $("#prem-rates-div").show();
                $("#btn-add-policy").show();
                $("#pol-term").css('display', 'block');
                $("#pol-term-display").css('display', 'none');
                $("#sect-div").hide();
                $("#btn-save-risk").hide();
                $("#btn-save-cancel").hide();
                $("#btn-add-risk").hide();
                $("#myTab #show-taxes,#show-clauses").hide();
                $(".hide-details").hide();
                $("#btn-add-new-section").hide();
                $("#display-client").css('display', 'none');
                $("#display-binder").css('display', 'none');
                $("#display-payment-mode").hide();
                $("#display-branch").hide();
                $("#display-currency").hide();
                $(".import-risks").show();
                $(".computetype").show();
                $(".premium-disp").hide();
                $(".sumassured-disp").hide();
                $("#btn-convert-policy").hide();
                $("#btn-unconvert-policy").hide();
                $("#btn-assign-trans").hide();
                $("#btn-assign-trans2").hide();
                $("#btn-comment-policy").hide();
            }
        }
    };

    var getPolicyRemakrs = function () {
        if (typeof polCode !== 'undefined') {
            if (polCode !== -2000) {
                $.ajax({
                    url: 'getPolicyRemarks',
                    type: 'GET',
                    processData: false,
                    contentType: false,
                    success: function (s) {

                        $("#poli-remarks").val(s.polRemarks);
                        $("#pol-remark-pk").val(s.remarksId);
                        if (s.endRemarks)
                            $("#remark-pk").val(s.endRemarks.remarkId);
                        $("#remark-pol-id").val(polCode);
                    },
                    error: function (xhr, error) {
                        bootbox.alert(xhr.responseText);
                    }
                });
            }
        }
    };

    var getPolicyDetails = function () {
        if (typeof polCode !== 'undefined') {
            if (polCode !== -2000) {
                $.ajax({
                    url: 'getPolicyDetails',
                    type: 'GET',
                    processData: false,
                    contentType: false,
                    success: function (s) {
                    console.log("dets", s)
                        if (s.product.riskNote != null && s.product.riskNote != "") {
                            var reportTemplate = s.product.riskNote;
                            var proposalUrl = SERVLET_CONTEXT + '/protected/uw/policies/' + reportTemplate;
                        } else {
                            var productName = "default";

                            // Replace spaces with underscores and convert to lowercase
                            productName = productName.trim().toLowerCase().replace(/\s+/g, '_');

                            // Build the URL based on the modified product name
                            var proposalUrl = SERVLET_CONTEXT + '/protected/uw/policies/rpt_' + productName + '_client_quote';

                        }
                          // Set the href attribute of the proposal link
                        $('#proposal-link').attr('href', proposalUrl);
                        console.log("Updated Proposal URL: " + proposalUrl);

                        console.log("to check status being added: " + s.authStatus);

                        if (s.authStatus) {
                            if (s.authStatus === "D") {
                                $("#btn-assign-trans").show();
                                $("#btn-assign-trans2").hide();
                                $("#btn-dispatch-trans").hide();
                                $("#btn-import-risk").show();
                                $("#btn-add-docs").show();
                                $("#btn-add-new-ips").show();
                                $("#btn-add-policy").css("display", "block");
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-undo-make-ready").css("display", "none");
                                $("#btn-make-ready-policy").css("display", "block");
                                $("#btn-negotiate-premium").css("display", "block");
                                $("#pol-status").text("Draft");
                                $("#edit-currency").show();
                                $("#display-currency").hide();
                                $("#edit-branch").show();
                                $("#display-branch").hide();
                                $("#edit-payment-mode").show();
                                $("#display-payment-mode").hide();
                                $("#edit-binder").show();
                                $("#display-binder").hide();
                                $(".edit-client").show();
                                $("#display-client").hide();
                                $("#policy-remarks").removeAttr('disabled');
                                $("#pol-bin-type").removeAttr('disabled');
                                $("#pol-buss-type").removeAttr('disabled');
                                $("#from-date").removeAttr('disabled');
                                $("#wet-date").removeAttr('disabled');
                                $("#pol-interface-type").removeAttr('disabled');
                                $("#pol-frequency").removeAttr('disabled');
                                $("#client-pol-no").removeAttr('disabled');
                                $("#poli-remarks").removeAttr('disabled');
                                $("#btn-add-risk").show();
                                $("#btn-add-new-clause").show();
                                $("#btn-add-new-tax").show();
                                $("#btn-add-new-section").show();
                                $("#btn-add-new-remark").show();
                                $("#btn-cancel-policy").hide();
                                $("#btn-save-remark").show();
                                $("#btn-add-new-sched").show();
                                $("#btn-add-edit-sched").show();
                                $("#btn-add-del-sched").show();
                                $("#btn-convert-policy").hide();
                                $("#btn-unconvert-policy").hide();
                                $("#sub-agent-frm").select2("enable", true);
                                $("#introducer-frm").select2("enable", true);
                                $("#marketer-frm").select2("enable", true);
                                $("#leads-man-frm").select2("enable", true);
                                $("#agent-type").prop("disabled", false);
                                $("#btn-comment-policy").css("display", "none");
                            } else if (s.authStatus === "R" || s.authStatus === "CV" || s.authStatus === "RCV") {
                                // $("#btn-assign-trans2").hide();
                                $("#btn-assign-trans").hide();
                                $("#btn-assign-trans2").show();
                                $("#btn-import-risk").hide();
                                $("#btn-add-docs").show();
                                $("#btn-add-new-ips").hide();
                                $("#btn-add-policy").css("display", "none");
                                $("#btn-dispatch-trans").hide();
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-make-ready-policy").css("display", "none");
                                $("#btn-negotiate-premium").css("display", "none");
                                if (s.authStatus === "R") {
                                    $("#btn-add-new-remark").css("display", "block");
                                    $("#btn-save-remark").css("display", "block");
                                    $("#btn-cancel-policy").css("display", "none");
                                    $("#btn-undo-make-ready").css("display", "block");
                                    $("#btn-convert-policy").css("display", "block");
                                    $("#btn-unconvert-policy").css("display", "none");
                                    $("#btn-auth-policy").css("display", "none");
                                    $("#pol-status").text("Ready");
                                    $("#btn-comment-policy").css("display", "none");
                                } else if (s.authStatus === "CV") {
                                    $("#btn-undo-make-ready").css("display", "none");
                                    $("#btn-convert-policy").css("display", "none");
                                    $("#btn-unconvert-policy").css("display", "block");
                                    $("#btn-auth-policy").css("display", "block");
                                    $("#pol-status").text("Converted");
                                    $("#edit-payment-mode").hide();
                                    $("#edit-currency").hide();
                                    $("#edit-branch").hide();
                                    $("#edit-binder").hide();
                                    $(".edit-client").hide();
                                    $("#from-date").prop("disabled", true);
                                    $("#wet-date").prop("disabled", true);
                                    $("#agent-type").prop("disabled", true);
                                    $("#pol-frequency").prop("disabled", true);
                                    $("#sub-agent-frm").select2("enable", false);
                                    $("#introducer-frm").select2("enable", false);
                                    $("#marketer-frm").select2("enable", false);
                                    $("#leads-man-frm").select2("enable", false);
                                    $("#pol-bin-type").prop("disabled", true);
                                    $("#btn-comment-policy").css("display", "none");
                                } else if (s.authStatus === "RCV") {
                                    $("#btn-assign-trans").show();
                                    $("#btn-assign-trans2").hide();
                                    $("#btn-undo-make-ready").css("display", "none");
                                    $("#btn-convert-policy").css("display", "none");
                                    $("#btn-unconvert-policy").css("display", "none");
                                    $("#btn-auth-policy").css("display", "none");
                                    $("#btn-make-ready-policy").css("display", "block");
                                    $("#btn-add-policy").css("display", "block");
                                    $("#btn-comment-policy").css("display", "block");
                                    $("#pol-status").text("Draft");



                                    // Enable editing for Term
                                    $("#pol-term-display").hide(); // Hide read-only display
                                    $("#pol-term").show(); // Show editable input
                                    $("#pol-term").removeAttr('disabled'); // Ensure the input is editable
                                    $("#edit-currency").show();
                                    $("#display-currency").show();
                                    $("#edit-branch").show();
                                    $("#display-branch").show();
                                    $("#edit-payment-mode").show();
                                    $("#display-payment-mode").show();
                                    $("#edit-binder").show();
                                    $("#display-binder").css('display', 'block');
                                    $(".edit-client").show();
                                    $("#display-client").show();
                                    $("#sub-agent-frm").select2("enable", true);
                                    $("#introducer-frm").select2("enable", true);
                                    $("#marketer-frm").select2("enable", true);
                                    $("#leads-man-frm").select2("enable", true);
                                    $("#agent-type").prop("disabled", false);
                                    $("#poli-remarks").attr("disabled", "disabled");
                                    $("#pol-buss-type").prop("disabled", false);
                                    $("#pol-bin-type").prop("disabled", false);
                                    $("#from-date").prop("disabled", false);
                                    $("#wet-date").prop("disabled", false);
                                    $("#pol-interface-type").prop("disabled", true);
                                    $("#pol-frequency").prop("disabled", false);
                                    $("#client-pol-no").prop("disabled", false);
                                    $("#btn-add-risk").show();
                                    $("#btn-add-new-clause").show();
                                    $("#btn-add-new-tax").show();
                                    $("#btn-add-new-section").show();
                                    // $("#btn-add-new-remark").hide();
                                    // $("#btn-save-remark").hide();
                                    $("#btn-add-new-sched").show();
                                    $("#btn-add-edit-sched").show();
                                    $("#btn-add-del-sched").show();
                                    }
                            } else if (s.authStatus === "A") {
                                $("#btn-assign-trans").hide();
                                $("#btn-assign-trans2").show();
                                $("#btn-import-risk").hide();
                                $("#btn-add-docs").hide();
                                $("#btn-add-new-ips").hide();
                                $("#btn-dispatch-trans").show();
                                $("#btn-add-policy").css("display", "none");
                                $("#btn-convert-policy").css("display", "none");
                                $("#btn-unconvert-policy").css("display", "none");
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-undo-make-ready").css("display", "none");
                                $("#btn-make-ready-policy").css("display", "none");
                                $("#btn-negotiate-premium").css("display", "none");
                                $("#pol-status").text("Authorised");
                                $("#sub-agent-frm").select2("enable", false);
                                $("#introducer-frm").select2("enable", false);
                                $("#marketer-frm").select2("enable", false);
                                $("#leads-man-frm").select2("enable", false);
                                $("#agent-type").prop("disabled", true);
                                $("#edit-currency").hide();
                                $("#display-currency").show();
                                $("#edit-branch").hide();
                                $("#display-branch").show();
                                $("#edit-payment-mode").hide();
                                $("#display-payment-mode").show();
                                $("#edit-binder").hide();
                                $("#display-binder").css('display', 'block');
                                $(".edit-client").hide();
                                $("#display-client").show();
                                $("#poli-remarks").prop("disabled", true);
                                $("#policy-remarks").prop("disabled", true);
                                $("#pol-buss-type").prop("disabled", true);
                                $("#pol-bin-type").prop("disabled", true);
                                $("#from-date").prop("disabled", true);
                                $("#wet-date").prop("disabled", true);
                                $("#pol-interface-type").prop("disabled", true);
                                $("#pol-frequency").prop("disabled", true);
                                $("#client-pol-no").prop("disabled", true);
                                $("#btn-add-risk").hide();
                                $("#btn-add-new-clause").hide();
                                $("#btn-add-new-tax").hide();
                                $("#btn-add-new-section").hide();
                                $("#btn-add-new-remark").hide();
                                $("#btn-cancel-policy").hide();
                                $("#btn-save-remark").hide();
                                $("#btn-add-new-sched").hide();
                                $("#btn-add-edit-sched").hide();
                                $("#btn-add-del-sched").hide();
                                $("#btn-comment-policy").css("display", "none");
                            } else if (s.authStatus === "LD") {
                                $("#btn-assign-trans").hide();
                                $("#btn-assign-trans2").show();
                                $("#btn-import-risk").hide();
                                $("#btn-add-docs").hide();
                                $("#btn-add-new-ips").hide();
                                $("#btn-dispatch-trans").hide();
                                $("#btn-add-policy").css("display", "none");
                                $("#btn-convert-policy").css("display", "none");
                                $("#btn-unconvert-policy").css("display", "none");
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-undo-make-ready").css("display", "none");
                                $("#btn-make-ready-policy").css("display", "none");
                                $("#btn-negotiate-premium").css("display", "none");
                                $("#pol-status").text("Authorised");
                                $("#sub-agent-frm").select2("enable", false);
                                $("#introducer-frm").select2("enable", false);
                                $("#marketer-frm").select2("enable", false);
                                $("#leads-man-frm").select2("enable", false);
                                $("#agent-type").prop("disabled", true);
                                $("#edit-currency").hide();
                                $("#display-currency").show();
                                $("#edit-branch").hide();
                                $("#display-branch").show();
                                $("#edit-payment-mode").hide();
                                $("#display-payment-mode").show();
                                $("#edit-binder").hide();
                                $("#display-binder").css('display', 'block');
                                $(".edit-client").hide();
                                $("#display-client").show();
                                $("#poli-remarks").prop("disabled", true);
                                $("#policy-remarks").prop("disabled", true);
                                $("#pol-buss-type").prop("disabled", true);
                                $("#pol-bin-type").prop("disabled", true);
                                $("#from-date").prop("disabled", true);
                                $("#wet-date").prop("disabled", true);
                                $("#pol-interface-type").prop("disabled", true);
                                $("#pol-frequency").prop("disabled", true);
                                $("#client-pol-no").prop("disabled", true);
                                $("#btn-add-risk").hide();
                                $("#btn-add-new-clause").hide();
                                $("#btn-add-new-tax").hide();
                                $("#btn-add-new-section").hide();
                                $("#btn-add-new-remark").hide();
                                $("#btn-cancel-policy").hide();
                                $("#btn-save-remark").hide();
                                $("#btn-add-new-sched").hide();
                                $("#btn-add-edit-sched").hide();
                                $("#btn-add-del-sched").hide();
                                $("#btn-uw-reports").hide();
                                $("#prem-item").hide();
                                $("#hidden-buttons").hide();
                                $("#btn-comment-policy").css("display", "none");
                            }

                        }
                        if(s.adminFeeApplicable && s.adminFeeApplicable==='Y'){
                            $('#chk-admin-fee').prop('checked', true);
                        }
                        else{
                            $('#chk-admin-fee').prop('checked', false);
                        }
                        polCode = s.policyId;
                        getUWPolicyRisks(s.policyId);
                        getUWPolicyReceipts(s.policyId);
                        getPolicybeneficiaries();
                        getPolicybenefits();
                        getPolicyInstallments();
                        UTILITIES.getProcessActiveDiagram(s.policyId);
                        UTILITIES.getTaskActive(s.policyId);
                        UTILITIES.getProcessHistory(s.policyId);
                        $("#client-info").text(s.client.fname + " " + s.client.otherNames);
                        $("#binder-info").text(s.binder.binName);
                        if (s.paymentMode)
                            console.log(s)
                        $("#pay-mode-info").text(s.paymentMode.pmDesc);
                        $("#branch-info").text(s.branch.obName);
                        $("#currency-info").text(s.transCurrency.curName);
                        console.log("s.totalInstalments=" + s.totalInstalments)
                        $("#pol-tot-inst").text(s.totalInstalments);
                        $("#pol-no").text(s.polNo);
                        $("#pol-term-display").css('display', 'block').text(s.polTerm);
                        $("#prop-no").text(s.proposalNo);
                        $("#div-pol-no").val(s.polNo);
                        $("#div-endos-no").val(s.polRevNo);
                        $("#policy-id").val(s.policyId);
                        $("#pol-rev-no").text(s.polRevNo);
                        $("#pol-bind-age-appli").val(s.product.ageApplicable);
                        $("#pol-sub-agent-comm").text(UTILITIES.currencyFormat(s.subAgentComm));
                        $("#pol-introducer-comm").text(UTILITIES.currencyFormat(s.introducerAgentComm));
                        $("#pol-marketer-comm").text(UTILITIES.currencyFormat(s.marketerAgentComm));
                        $("#pol-sum-insured").text(UTILITIES.currencyFormat(s.sumInsured));
                        $("#pol-paid-insts").text(s.paidInsts)
                        var numberFormatter = new Intl.NumberFormat('en-US', {
                            style: 'decimal',
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2
                        });
                        if (s.negotiatedPremium != null) {
                            $("#negotiated").text(numberFormatter.format(s.negotiatedPremium));
                            $("#pol-premium").text(numberFormatter.format(s.negotiatedPremium));
                        } else {
                            $("#negotiated").text("N/A"); // display a placeholder if no value exists
                            $("#pol-premium").text(UTILITIES.currencyFormat(s.premium));
                        }

                        $("#pol-basic-prem").text(UTILITIES.currencyFormat(s.basicPrem));
                        $("#pol-rev-bonus").text(UTILITIES.currencyFormat(s.revBonus));
                        $("#pol-term-bonus").text(UTILITIES.currencyFormat(s.terminalBonus));
                        $("#pol-tax-relief").text(UTILITIES.currencyFormat(s.taxRelief));
                        $("#pol-net-prem").text(UTILITIES.currencyFormat(s.taxRelief));
                        $("#pol-taxes-amt").text(s.taxes);

                        if (s.product.motorProduct) {
                            $(".motor-disp").show();
                            $(".non-motor-disp").hide();
                        } else {
                            $(".motor-disp").hide();
                            $(".non-motor-disp").show();
                        }
                        $("#pol-prod-name").text(s.product.proDesc);
                        $("#from-date").val(moment(s.wefDate).format('DD/MM/YYYY'));
                        $("#wet-date").val(moment(s.wetDate).format('DD/MM/YYYY'));
                        if (s.polPaidToDate) {
                            $("#pol-paid-to-date").text(moment(s.polPaidToDate).format('DD/MM/YYYY'));
                        } else {
                            $("#pol-paid-to-date").text("");
                        }
                        $('#pol-ins-comp').text(s.agent.name)
                        $("#pol-interface-type").val(s.interfaceType);
                        $("#negotiated-prem").val(s.negotiatedPremium);
                        $("#pol-frequency").val(s.frequency);
                        $("#client-pol-no").val(s.clientPolNo);
                        $("#pol-buss-type").val(s.businessType);
                        if (s.subAgent) {
                            $("#agent-type").val('SUB').trigger('change');
                            $("#sub-agent-id").val(s.subAgent.acctId);
                            $("#sub-agent-name").val(s.subAgent.name);
                            $("#sub-agent-absaNo-id").val(s.absaNoSubAgent);
                            $("#sub-agent-absaNo-name").val(s.absaNoSubAgent);
                            populateSubAgentsLov();
                        }
                        if (s.marketerAgent) {
                            $("#agent-type").val('MRK').trigger('change');
                            $("#marketer-id").val(s.marketerAgent.acctId);
                            $("#marketer-name").val(s.marketerAgent.name);
                            $("#marketer-absaNo-id").val(s.absaNoMarketer);
                            $("#marketer-absaNo-name").val(s.absaNoMarketer);
                            populateMarketerLov();
                        }
                        if (s.introducerAgent) {
                            $("#introducer-id").val(s.introducerAgent.acctId);
                            $("#introducer-name").val(s.introducerAgent.name);
                            $("#introducer-absa-No-id").val(s.absaNoIntroducer);
                            $("#introducer-absaNo-name").val(s.absaNoIntroducer);
                            populateIntroducerLov();
                        }
                        if (s.leadsMan) {
                            $("#leads-man-id").val(s.leadsMan.id);
                            $("#leads-man-name").val(s.leadsMan.name);
                            $("#leads-absaNo-id").val(s.absaNoLeadsMan);
                            $("#leads-absaNo-name").val(s.absaNoLeadsMan);
                            populateLeadsManLov();
                        }
                        $("#cur-id").val(s.transCurrency.curCode);
                        $("#cur-name").val(s.transCurrency.curName);
                        populateCurrencyLov();
                        $("#client-id").val(s.client.tenId);
                        $("#client-f-name").val(s.client.fname);
                        $("#client-other-name").val(s.client.otherNames);
                        $("#ntln-id").val(s.client.idNo);
                        populateClientLov();
                        getPolicyTerms(s.binder.binId, s.product.proDesc);
                        console.log(s.polTerm);
                        $("#pol-term").val(s.polTerm);
                        $("#risk-binder").text(s.binder.binName);
                        $("#risk-binder-id").val(s.binder.binId);
                        $("#risk-binder-code").val(s.binder.binId);
                        $("#risk-bind-code").val(s.binder.binId);
                        $("#binder-id").val(s.binder.binId);
                        $("#product-id").val(s.product.proCode);
                        $("#pol-agent-id").val(s.agent.acctId);
                        $("#bind-name").val(s.binder.binName);
                        $("#pol-binder-policy").val(s.binder.binType);
                        populateBinderLov();
                        $("#pm-id").val(s.paymentMode.pmId);
                        $("#pm-name").val(s.paymentMode.pmDesc);
                        populatePaymentModes();
                        $("#brn-id").val(s.branch.obId);
                        $("#brn-name").val(s.branch.obName);
                        $('#pol-comm-amt').text(UTILITIES.currencyFormat(s.commAmt));
                        $('#pol-tl').text(UTILITIES.currencyFormat(s.trainingLevy));
                        $('#pol-phcf').text(UTILITIES.currencyFormat(s.phcf));
                        $('#pol-sd').text(UTILITIES.currencyFormat(s.stampDuty));
                        $('#pol-whtx').text(UTILITIES.currencyFormat(s.whtx));
                        $('#pol-extras').text(UTILITIES.currencyFormat(s.extras));
                        $("#pol-fap").text(UTILITIES.currencyFormat(s.futurePrem));
                        $("#pol-bin-type").val(s.binder.binType);
                        $('#pol-tran-type-disp').text(s.transType);
                        $('#pol-trans-type').val(s.transType);
                        $('#pol-prev-policy').val(s.prevPolicy);
                        $('#pol-prev-policy').val(s.prevPolicy);
                        $('#pol-reuse-contra-policy').val(s.reusecontraPolicy);

                        if (s.transType === "EN") {
                            if (s.authStatus === "D") {
                                $("#btn-endors-risk").show();
                            } else {
                                $("#btn-endors-risk").hide();
                            }
                            $("ul.reports-links li.endorse-disp").show();

                        } else {
                            $("#btn-endors-risk").hide();
                            $("ul.reports-links li.endorse-disp").hide();
                        }

                        if (s.transType === "CO" || s.transType === "CN") {
                            $("#btn-undo-make-ready").css("display", "none");
                            // $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-negotiate-premium").css("display", "none");
                        }
                        if (s.transType === "CO"){
                            $("#btn-make-ready-policy").css("display", "block");
                            $("#btn-auth-policy").css("display", "block");
                            $("#btn-convert-policy").css("display", "none");

                        }else if (s.transType === "CN"){
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-negotiate-premium").css("display", "block");
                        }

                        if (s.transType === "RN") {
                            $("#renbtn").show();
                        } else {
                            $("#renbtn").hide();
                        }
                        if (s.transType === "RF") {
                            $("#refund-docs-tabb").show();
                            $("#refund-remarks-tab").show();
                        } else {
                            $("#refund-docs-tabb").hide();
                            $("#refund-remarks-tab").hide();
                        }
                        // Inside your success function, after checking authStatus
                        if (s.transType === "CN") {
                            $("#btn-cancel-policy").css("display", "block");
                        } else
                            $("#btn-cancel-policy").css("display", "none");

                        $("#binder-frm").select2("enable", false);
                        $("#pol-bintype").prop("disabled", true);
                        populateUserBranches();
                        if (s.renewalDate)
                            $("#pol-ren-date").text(moment(s.renewalDate).format('DD/MM/YYYY'));

                        if (s.authStatus === "A") {
                            $("#btn-make-ready-policy").css("display", "none");
                            $("#btn-cancel-policy").css("display", "none");
                        }

                    },
                    error: function (xhr, error) {
                        bootbox.alert(xhr.responseText);
                    }
                });
            } else {
                $("#display-client").hide();
                $("#display-binder").css('display', 'none');
                $("#display-payment-mode").hide();
                $("#display-branch").hide();
                $("#pol-term-display").css('display', 'none')
            }
        }
    };

    var changePolicyWetDt = function () {
        $('#cover-to-date').on('dp.change', function (ev) {
            var curDate = ev.date;
            var dt = moment(curDate).format('DD/MM/YYYY');
            $("#risk-wet-date").val(dt);
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
    };

    var validateRisk = function () {
        $('#risk-id').on('change', function () {
            var riskId = $("#risk-id").val();
            var subCode = $("#risk-sub-code").val();
            $.ajax({
                type: 'GET',
                url: 'validateRisk',
                dataType: 'json',
                data: {"riskId": riskId, "sclCode": subCode},
                async: true,
                success: function (result) {

                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                    $("#risk-id").val("");
                }
            });
        });
    };

    var getPolicyWet = function () {
        $('#wef-date').on('dp.change', function (ev) {
            var curDate = ev.date;
            console.log(curDate);
            var dt = moment(curDate).format('DD/MM/YYYY');
            $("#risk-wef-date").val(dt);
            $.ajax({
                type: 'GET',
                url: SERVLET_CONTEXT + '/protected/uw/policies/getMaturityDate',
                dataType: 'json',
                data: {"wefDate": dt, "polTerm": $('#pol-term').val()},
                async: true,
                success: function (result) {
                    $("#wet-date").val(moment(result).format('DD/MM/YYYY'));
                    $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));


                },
                error: function (jqXHR, textStatus, errorThrown) {

                }
            });
        });
        $("#pol-term").on('change', function () {

            if (!($("#from-date").val() === null)) {
                $.ajax({
                    type: 'GET',
                    url: SERVLET_CONTEXT + '/protected/uw/policies/getMaturityDate',
                    dataType: 'json',
                    data: {"wefDate": $("#from-date").val(), "polTerm": $('#pol-term').val()},
                    async: true,
                    success: function (result) {
                        $("#wet-date").val(moment(result).format('DD/MM/YYYY'));
                        $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));


                    },
                    error: function (jqXHR, textStatus, errorThrown) {

                    }
                });
            }
        });
    };


    var populateInsuredLov = function () {

        if ($("#lifeassured-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "lifeassured-frm",
                sort: 'fname',
                change: function (e, a, v) {

                    if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {

                        $.ajax({
                            type: 'GET',
                            url: 'getLifeClientAge',
                            dataType: 'json',
                            data: {"clientId": e.added.tenId, "binCode": $("#binder-id").val()},
                            async: true,
                            success: function (result) {

                                $("#lifeassured-age").val(result);
                                $("#lifeassured-code").val(e.added.tenId);
                                $("#life-ntl-id").val(e.added.idNo);
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                Swal.fire({
                                    title: 'Error',
                                    text: jqXHR.responseText,
                                    icon: 'error'
                                });
                            }
                        });
                    } else
                        $("#lifeassured-code").val(e.added.tenId);
                    $("#life-ntl-id").val(e.added.idNo);
                    //$("#insured-id").val(e.added.tenId);
                },
                formatResult: function (a) {
                    if (a.idNo !== null) {
                        return a.fname + " " + a.otherNames + " - " + a.idNo;
                    } else {
                        return a.fname + " " + a.otherNames
                    }
                },
                formatSelection: function (a) {
                    if (a.idNo !== null) {
                        return a.fname + " " + a.otherNames + " - " + a.idNo;
                    } else {
                        return a.fname + " " + a.otherNames
                    }
                },
                initSelection: function (element, callback) {
                    var code = $("#lifeassured-code").val();
                    var name = $("#lifeassured-name").val();
                    var othernames = $("#lifeassured-other-name").val();
                    var idNo = $("#life-ntl-id").val();
                    var data = {fname: name, otherNames: othernames, tenId: code, idNo: idNo};
                    callback(data);
                },
                id: "tenId",
                width: "250px",
                placeholder: "Select Insured"

            });
        }
    }

    var populateClientLov = function () {
        if ($("#client-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "client-frm",
                sort: 'fname',
                change: function (e, a, v) {
                    $("#client-id").val(e.added.tenId);
                    $("#ntln-id").val(e.added.idNo);
                    $("#lifeassured-code").val(e.added.tenId);
                    $("#life-ntl-id").val(e.added.idNo);
                    $("#lifeassured-name").val(e.added.fname)
                    $("#lifeassured-other-name").val(e.added.otherNames)
                    if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
                        $.ajax({
                            type: 'GET',
                            url: 'getLifeClientAge',
                            dataType: 'json',
                            data: {"clientId": e.added.tenId, "binCode": $("#binder-id").val()},
                            async: true,
                            success: function (result) {
                                $("#lifeassured-age").val(result);
                                populateInsuredLov();
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                            }
                        });
                    } else {
                        populateInsuredLov();
                    }


                },
                formatResult: function (a) {
                    if (a.idNo !== null) {
                        return a.fname + " " + a.otherNames + " - " + a.idNo;
                    } else {
                        return a.fname + " " + a.otherNames
                    }
                },
                formatSelection: function (a) {
                    if (a.idNo !== null) {
                        return a.fname + " " + a.otherNames + " - " + a.idNo;
                    } else {
                        return a.fname + " " + a.otherNames
                    }
                },
                initSelection: function (element, callback) {
                    var code = $("#client-id").val();
                    var name = $("#client-f-name").val();
                    var othernames = $("#client-other-name").val();
                    var idNo = $("#ntln-id").val();
                    var data = {fname: name, otherNames: othernames, tenId: code, idNo: idNo};
                    callback(data);
                },
                id: "tenId",
                placeholder: "Select Assured"

            });
        }
    };

    var populateSubAgentsLov = function () {
        if ($("#sub-agent-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "sub-agent-frm",
                sort: 'name',
                change: function (e, a, v) {
                    $("#sub-agent-id").val(e.added.acctId);

                    if (e.added.absaNo) {
                        $("#sub-agent-absaNo-id").val(e.added.absaNo);
                        $("#sub-agent-absaNo-name").val(e.added.absaNo);
                    } else {
                        $("#sub-agent-absaNo-id").val('');
                        $("#sub-agent-absaNo-name").val('');
                    }

                },
                formatResult: function (a) {
                    return a.name;
                },
                formatSelection: function (a) {
                    return a.name;
                },
                initSelection: function (element, callback) {
                    var code = $('#sub-agent-id').val();
                    var name = $("#sub-agent-name").val();
                    var abNo = $("#sub-agent-absaNo-id").val();
                    var data = {name: name, acctId: code, absaNo: abNo};
                    callback(data);
                },
                id: "acctId",
                width: "250px",
                placeholder: "Select Sub Agent"

            });

            $("#sub-agent-frm").on("select2-removed", function (e) {
                $("#sub-agent-id").val('');
                $("#sub-agent-absaNo-id").val('');
                $("#sub-agent-absaNo-name").val('');
            })
        }
    };
    var populateIntroducerLov = function () {
        if ($("#introducer-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "introducer-frm",
                sort: 'name',
                change: function (e, a, v) {
                    $("#introducer-id").val(e.added.acctId);

                    if (e.added.absaNo) {
                        $("#introducer-absa-No-id").val(e.added.absaNo);
                        $("#introducer-absaNo-name").val(e.added.absaNo);
                    } else {
                        $("#introducer-absa-No-id").val('');
                        $("#introducer-absaNo-name").val('');
                    }

                },
                formatResult: function (a) {
                    return a.name;
                },
                formatSelection: function (a) {
                    return a.name;
                },
                initSelection: function (element, callback) {
                    var code = $('#introducer-id').val();
                    var name = $("#introducer-name").val();
                    var abNo = $("#introducer-absa-No-id").val();
                    var data = {name: name, acctId: code};
                    callback(data);
                },
                id: "acctId",
                width: "250px",
                placeholder: "Select Introducer"

            });

            $("#introducer-frm").on("select2-removed", function (e) {
                $("#introducer-id").val('');
                $("#introducer-absa-No-id").val('');
                $("#introducer-absaNo-name").val('');
            })
        }
    };

    var populateLeadsManLov = function () {
        if ($("#leads-man-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "leads-man-frm",
                sort: 'name',
                change: function (e, a, v) {
                    console.log("LeadGen id: " + e.added.id);
                    console.log("LeadGen name: " + e.added.name);
                    $("#leads-man-id").val(e.added.id);
                    $("#leads-man-name").val(e.added.name);

                    if (e.added.absaNo) {
                        $("#leads-absaNo-id").val(e.added.absaNo);
                        $("#leads-absaNo-name").val(e.added.absaNo);
                    } else {
                        $("#leads-absaNo-id").val('');
                        $("#leads-absaNo-name").val('');
                    }

                },
                formatResult: function (a) {
                    return a.name;
                },
                formatSelection: function (a) {
                    return a.name;
                },
                initSelection: function (element, callback) {
                    var code = $('#leads-man-id').val();
                    var name = $("#leads-man-name").val();
                    var abNo = $("#leads-absaNo-id").val();
                    var data = {name: name, id: code, absaNo: abNo};
                    callback(data);
                },
                id: "id",
                width: "250px",
                placeholder: "Select Leads Man"

            });

            $("#leads-man-frm").on("select2-removed", function (e) {
                $("#leads-man-id").val('');
                $("#leads-absaNo-id").val('');
                $("#leads-absaNo-name").val('');
            })
        }
    };

    var populateMarketerLov = function () {
        if ($("#marketer-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "marketer-frm",
                sort: 'name',
                change: function (e, a, v) {
                    $("#marketer-id").val(e.added.acctId);

                    if (e.added.absaNo) {
                        $("#marketer-absaNo-id").val(e.added.absaNo);
                        $("#marketer-absaNo-name").val(e.added.absaNo);
                    } else {
                        $("#marketer-absaNo-id").val('');
                        $("#marketer-absaNo-name").val('');
                    }

                },
                formatResult: function (a) {
                    return a.name;
                },
                formatSelection: function (a) {
                    return a.name;
                },
                initSelection: function (element, callback) {
                    var code = $('#marketer-id').val();
                    var name = $("#marketer-name").val();
                    var abNo = $("#marketer-absaNo-id").val();
                    var data = {name: name, acctId: code, absaNo: abNo};
                    callback(data);
                },
                id: "acctId",
                width: "250px",
                placeholder: "Select Marketer"

            });

            $("#marketer-frm").on("select2-removed", function (e) {
                $("#marketer-id").val('');
                $("#marketer-absaNo-id").val('');
                $("#marketer-absaNo-name").val('');
            })
        }
    };

    var checkAndDisableRemarkControls = function() {

        if (!document.getElementById('remark-disable-styles')) {
            var style = document.createElement('style');
            style.id = 'remark-disable-styles';
            style.innerHTML = `
            .disabled-remark {
                background-color: #f5f5f5 !important;
                cursor: not-allowed !important;
            }
            .btn-disabled {
                opacity: 0.6 !important;
                cursor: not-allowed !important;
            }
        `;
            document.head.appendChild(style);
        }

       setTimeout(function() {
            var hasRemarkContent = $("#poli-remarks").val() && $("#poli-remarks").val().trim() !== '';
            var hasRemarkId = $("#pol-remark-pk").val() && $("#pol-remark-pk").val() !== '';

            if (hasRemarkContent || hasRemarkId) {

                $("#btn-add-new-remark").prop("disabled", true);
                $("#btn-save-remark").prop("disabled", true);
                $("#poli-remarks").prop("disabled", true);

                // Add visual indication
                $("#poli-remarks").addClass('disabled-remark');
                $("#btn-add-new-remark").addClass('btn-disabled');
                $("#btn-save-remark").addClass('btn-disabled');
            }
        }, 1500);
    };
    $(document).ready(function () {
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
            }
        });
    });

    var populateCurrencyLov = function () {
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
    };

    var populateSubclassLov = function () {
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
                params: {bindCode: $("#risk-bind-code").val()},
                placeholder: "Select Sub Class"

            });
        }
    };

    var populateCoverTypesLov = function () {
        if ($("#covertypes-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "covertypes-frm",
                sort: 'detId',
                change: function (e, a, v) {
                    $("#risk-cov-code").val(e.added.covId);
                    $("#binder-det-id").val(e.added.detId);
                    //getCommissionRate(e.added.detId);
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
    };

    var getPremiumRates = function (detId) {
        console.log('bind age applicable ', $("#pol-bind-age-appli").val());
        if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
            $.ajax({
                type: 'GET',
                url: 'getBinderClientPremRates',
                dataType: 'json',
                data: {"detId": detId, "age": $("#lifeassured-age").val()},
                async: true,
                success: function (result) {
                    $("#section_form_tbl tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                            "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                        if ($("#pol-bin-type").val() === "B") {
                            if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" value="' + $("#lifeassured-age").val() + '" readonly>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                            } else if (result[res].rider && result[res].rider === "Y") {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
                                    "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" +
                                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                            } else {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                            }
                        } else {
                            if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" value="' + $("#lifeassured-age").val() + '" readonly>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                            } else if (result[res].rider && result[res].rider === "Y") {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
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
        } else {
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
                        var markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
                        if ($("#pol-bin-type").val() === "B") {
                            markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                                "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td></tr>";
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
    };

    var populateBinderLov = function (bindType) {
        if ($("#binder-frm").filter("div").html() != undefined) {

            Select2Builder.initAjaxSelect2({
                containerId: "binder-frm",
                sort: 'binName',
                change: function (e, a, v) {
                    showCoverOptions(e.added.binName);
                    populateImportSubCovers(e.added.binId);
                    $("#binder-id").val(e.added.binId);
                    $("#risk-binder-code").val(e.added.binId);
                    $("#risk-bind-code").val(e.added.binId);
                    $("#pol-ins-comp").text(e.added.account.name);
                    $("#pol-prod-name").text(e.added.product.proDesc);
                    $("#product-id").val(e.added.product.proCode);
                    $("#pol-agent-id").val(e.added.account.acctId);
                    // $("#client-pol-no").val(e.added.binPolNo);
                    $("#risk-binder").text(e.added.binName);
                    $("#pol-bind-age-appli").val(e.added.product.ageApplicable);
                    $("#pol-buss-type").val("L");
                    populateSubclassLov();
                    getPolicyTerms(e.added.binId, e.added.product.proDesc);
                    updatePaymentFrequencyForContracts();
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
                params: {bindType: bindType},
                placeholder: "Select Contract"

            });
        }
    };

    var populatePaymentModes = function () {
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
    };

    var populateUserBranches = function () {
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
    };

    var createNewPolicy = function () {
        $("#btn-add-policy").click(function () {
            var $button = $(this);
            var originalText = $button.val();
            $button.addClass('btn-processing').prop("disabled", true).val('Saving...');
            if (typeof polCode !== 'undefined') {
                if (polCode !== -2000) {
                    updatePolicy(true);
                } else {
                    createPolicy(true);
                }
            } else {
                createPolicy(true);
            }
        });

        $("#btn-save-risk").click(function (event) {
            if ($("#risk-code-pk").val() != '') {
                updateRisk();
            } else
                createRisk();
        });

        $("#btn-make-ready-policy").click(function () {
            var $button = $(this);
            var originalText = $button.val();
            $button.addClass('btn-processing').prop("disabled", true).val('Processing...');
            console.log('passed here...');
            makeReady()
                .done(function() {
                })
                .fail(function() {
                    $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
                });
        });

        $("#btn-undo-make-ready").click(function () {
            var $button = $(this);
            var originalText = $button.val();
            activeProcessingButton = $button;
            activeProcessingButtonText = originalText;
            $button.addClass('btn-processing').prop("disabled", true).val('Processing...');

            undoMakeReady();
        });

        $("#btn-auth-policy").click(function () {
            var $button = $(this);
            var originalText = $button.val();
            $button.addClass('btn-processing').prop("disabled", true).val('Processing...');

            authorizePolicy($button, originalText);
        });
        $("#btn-cancel-policy").click(function () {
            console.log('passed here...');
            submitPolicyCancellation();
        });
        $("#btn-comment-policy").click(function () {
            commentPolicy();
        });

        $("#btn-dispatch-trans").click(function () {
            dispatchDocuments();
        });
        $("#btn-convert-policy").click(function () {
            var $button = $(this);
            var originalText = $button.val();
            activeProcessingButton = $button;
            activeProcessingButtonText = originalText;
            $button.addClass('btn-processing').prop("disabled", true).val('Converting...');

            convertToPolicy([], null, null);
        });



        $("#btn-unconvert-policy").click(function () {
            var $button = $(this);
            var originalText = $button.val();
            activeProcessingButton = $button;
            activeProcessingButtonText = originalText;
            $button.addClass('btn-processing').prop("disabled", true).val('Processing...');

            undoConvertToPolicy();
        });

        $('#searchChecker').on('keyup', function () {
            loadEligibleCheckers($(this).val());
        });

        $('#assign-checker-submit').click(function () {
            var $submitButton = $(this);
            var originalSubmitText = $submitButton.text();
            var selectedCheckers = [];

            $('#checkersList tbody input.checker-checkbox:checked').each(function () {
                selectedCheckers.push($(this).val());
            });

            if (selectedCheckers.length === 0) {
                Swal.fire({
                    title: 'Warning',
                    text: 'Please select at least one checker to assign the task.',
                    icon: 'warning'
                });
                return;
            }
            $submitButton.addClass('btn-processing').prop("disabled", true).text('Submitting...');

            console.log(selectedCheckers);
            convertToPolicy(selectedCheckers, $submitButton, originalSubmitText);
        });

        $("#btn-assign-trans").click(function () {
            var $button = $(this);
            var originalText = $button.val();
            activeProcessingButton = $button;
            activeProcessingButtonText = originalText;
            $button.addClass('btn-processing').prop("disabled", true).val('Processing...');

            loadEligibleCheckers('');
        });
    };


var commentPolicy = function() {
    // Set modal title to "Changes made"
    $('#authcommentModal').find('.modal-title').text('Changes made');

    // Show the modal
    $('#authcommentModal').modal({
        backdrop: 'static',
        keyboard: true
    });

    // Clear any previous comments when modal opens
    $('#policyComment').val('');

    // Handle save button click
    $('#saveCommentButton').off('click').on('click', function() {
        ButtonStateManager.greyOutButton('saveCommentButton');
        var comments = $('#policyComment').val().trim();

        if (!comments) {
            Swal.fire({
                title: 'Warning',
                text: 'Please enter a comment before saving',
                icon: 'warning'
            });
            ButtonStateManager.enableButton('saveCommentButton');
            return;
        }

        // Store comment in sessionStorage
        sessionStorage.setItem('policyComment_' + polCode, comments);

        // Close the modal
        $('#authcommentModal').modal('hide');
console.log(comments)
        Swal.fire({
            title: 'Success',
            text: 'Comment saved temporarily. Please submit the policy to include the comment.',
            icon: 'success'
        }).then(() => {
            // Refresh policy details if needed
            getPolicyDetails();
            ButtonStateManager.enableButton('saveCommentButton');
        });
    });

    // Reset modal title when closed
    $('#authcommentModal').on('hidden.bs.modal', function() {
        $(this).find('.modal-title').text('Authorization Comment');
    });
};

var ButtonStateManager = {
    greyOutButton: function(buttonId) {
        $('#' + buttonId).prop('disabled', true).addClass('disabled');
    },
    enableButton: function(buttonId) {
        $('#' + buttonId).prop('disabled', false).removeClass('disabled');
    }
};

var refreshTasksTable = function() {
    if ($('#tasks').DataTable()) {
        $('#tasks').DataTable().ajax.reload(null, false); // false to preserve pagination
    }
};

    var submitPolicyCancellation = function () {
        if(!$("#negotiated-prem").val()){
            Swal.fire({
                title: 'Error',
                text:  'Please enter valid negotiated amount to continue..',
                icon: 'error'
            });
            return;
        }
        bootbox.confirm("Are you sure you want to submit this policy cancellation for approval?", function (result) {
            if (result) {
                // Capture form values from #frm-pol-remarks
                var cancelData = {
                    policyId: $("#remark-pol-id").val(), // Maps to policyId
                    remarks: $("#poli-remarks").val(),  // Maps to remarks
                    cancelReasonId: $("#remark-pk").val(), // Maps to cancelReasonId (adjust if needed)
                     refundAmount: $("#negotiated-prem").val() // Uncomment and add field if needed
                };
                console.log(cancelData);

                $.ajax({
                    type: 'POST',
                    // url: 'submitPolicyCancellation',
                    url: SERVLET_CONTEXT + '/protected/uw/policies/submitPolicyCancellation',

                    contentType: 'application/json',
                    data: JSON.stringify(cancelData),
                    dataType: 'json',
                  //  async: true,
                    success: function (result, textStatus, jqXHR) {
                        if (jqXHR.status === 204) {
                            Swal.fire({
                                title: 'Submitted',
                                text: 'Policy cancellation submitted successfully for approval.',
                                icon: 'success'
                            }).then(() => {
                                window.location.href = SERVLET_CONTEXT + "/protected/home";
                            });
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText || 'Failed to submit policy cancellation.',
                            icon: 'error'
                        });
                    }
                });
            }
        });
    };



    var authorizePolicy = function ($button, originalText) {
        bootbox.confirm("Are you sure you want to authorize this transaction?", function (result) {
            if (result) {
                // User confirmed - proceed with authorization
                $.ajax({
                    type: 'GET',
                    url: 'authorizeLifePolicy',
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Transaction Authorized Successfully',
                            icon: 'success'
                        });
                        window.location.href = SERVLET_CONTEXT + "/protected/home";
                        populatePolicyDetails();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                    }
                });
            } else {
                $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
            }
        });
    };

    var dispatchDocuments = function () {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        $.ajax({
            type: 'GET',
            url: 'dispatchDocs',
            dataType: 'json',
            async: true,
            success: function (result) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Document Dispatched Successfully',
                    icon: 'success'
                });
                populatePolicyDetails();
                $("#btn-dispatch-trans").hide();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                ;
            }
        });
    };

    var convertToPolicy = function (selectedCheckers, $submitButton, originalSubmitText) {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};

        // Get comment from sessionStorage
        var comment = sessionStorage.getItem('policyComment_' + polCode);

        var ajaxData = {};
        if (selectedCheckers.length !== 0) {
            ajaxData.checkers = selectedCheckers;
        }
        if (comment) {
            ajaxData.resubmissionComment = comment;
        }

        if (selectedCheckers.length !== 0) {
            $.ajax({
                type: 'POST',
                url: SERVLET_CONTEXT + '/protected/life/policies/proposalConversion',
                dataType: 'json',
                traditional: true,
                async: true,
                data: ajaxData,
                success: function (result) {
                    activeProcessingButton = null;
                    activeProcessingButtonText = null;

                    // Clear comment from sessionStorage on success
                    if (comment) {
                        sessionStorage.removeItem('policyComment_' + polCode);
                    }

                    Swal.fire({
                        title: 'Success',
                        text: 'Submitted Successfully',
                        icon: 'success'
                    });
                    $('#assignCheckerModal').modal('hide');
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                    populatePolicyDetails();
                    $('#polChecksList').DataTable().ajax.reload();
                    refreshTasksTable(); // Refresh tasks table to show updated comment
                    displayAuditTrails();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (activeProcessingButton) {
                        activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                        activeProcessingButton = null;
                        activeProcessingButtonText = null;
                    }
                    if ($submitButton) {
                        $submitButton.removeClass('btn-processing').prop("disabled", false).text(originalSubmitText);
                    }

                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        } else {
            bootbox.confirm("Are you sure you want to convert this proposal to a policy", function (result) {
                if (result) {
                    Swal.fire({
                        title: 'Processing...',
                        text: 'Please wait while the transaction is being converted.',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        didOpen: () => {
                            Swal.showLoading();
                        }
                    });

                    $.ajax({
                        type: 'POST',
                        url: SERVLET_CONTEXT + '/protected/life/policies/proposalConversion',
                        dataType: 'json',
                        async: true,
                        data: comment ? { resubmissionComment: comment } : {}, // Include comment if it exists
                        success: function (result) {
                            activeProcessingButton = null;
                            activeProcessingButtonText = null;

                            // Clear comment from sessionStorage on success
                            if (comment) {
                                sessionStorage.removeItem('policyComment_' + polCode);
                            }

                            Swal.fire({
                                title: 'Success',
                                text: 'Submitted Successfully',
                                icon: 'success'
                            });
                            window.location.href = SERVLET_CONTEXT + "/protected/home";
                            populatePolicyDetails();
                            $('#polChecksList').DataTable().ajax.reload();
                            refreshTasksTable(); // Refresh tasks table to show updated comment
                            displayAuditTrails();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            if (activeProcessingButton) {
                                activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                                activeProcessingButton = null;
                                activeProcessingButtonText = null;
                            }

                            Swal.fire({
                                title: 'Error',
                                text: jqXHR.responseText,
                                icon: 'error'
                            });
                        }
                    });
                } else {
                    if (activeProcessingButton) {
                        activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                        activeProcessingButton = null;
                        activeProcessingButtonText = null;
                    }
                }
            });
        }
    };

    var undoConvertToPolicy = function() {
        // Fetch rejection reasons when modal is opened
        $.ajax({
            type: 'GET',
            url: 'rejection-reasons',
            dataType: 'json',
            success: function(response) {
                var reasonsHtml = '';
                response.data.forEach(function(reason) {
                    reasonsHtml += `
                <div class="custom-control custom-radio mb-2">
                    <input type="radio" class="custom-control-input rejection-reason"
                           id="reason-${reason.reasonId}" value="${reason.reasonId}" name="rejectionReason">
                    <label class="custom-control-label" for="reason-${reason.reasonId}">
                        ${reason.reasonDesc}
                    </label>
                </div>`;
                });
                $('#rejection-reasons-list').html(reasonsHtml);
                $('#rejectionModal').modal('show');
            },
            error: function(jqXHR) {
                if (activeProcessingButton) {
                    activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                    activeProcessingButton = null;
                    activeProcessingButtonText = null;
                }
                Swal.fire({
                    title: 'Error',
                    text: 'Failed to load rejection reasons',
                    icon: 'error'
                });
            }
        });

        $('#rejectConfirmButton').off('click').on('click', function() {
            var selectedReason = $('input[name="rejectionReason"]:checked').val();
            var comments = $('#rejectionReason').val().trim();

            if (!selectedReason) {
                Swal.fire({
                    title: 'Error',
                    text: 'Please select a rejection reason',
                    icon: 'error'
                });
                return;
            }

            if (!comments) {
                Swal.fire({
                    title: 'Error',
                    text: 'Please provide additional comments',
                    icon: 'error'
                });
                return;
            }

            $.ajax({
                type: 'GET',
                url: SERVLET_CONTEXT + '/protected/life/policies/undoProposalConversion',
                dataType: 'json',
                data: {
                    reasonId: selectedReason,
                    reason: comments
                },
                async: true,
                success: function(result) {
                    activeProcessingButton = null;
                    activeProcessingButtonText = null;
                    Swal.fire({
                        title: 'Success',
                        text: 'Submitted Successfully',
                        icon: 'success'
                    }).then(() => {
                        $('#rejectionModal').modal('hide');
                        populatePolicyDetails();
                        $('#polChecksList').DataTable().ajax.reload();
                        window.location.href = SERVLET_CONTEXT + "/protected/home";
                        displayAuditTrails();
                    });
                },
                error: function(jqXHR) {
                    if (activeProcessingButton) {
                        activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                        activeProcessingButton = null;
                        activeProcessingButtonText = null;
                    }
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        });
    };

    var loadEligibleCheckers = function (searchParam) {
        $.ajax({
            url: SERVLET_CONTEXT + '/protected/home/getEligibleCheckers',
            type: 'GET',
            data: {permissionName: 'AUTHORIZE_POLICY', searchParam: searchParam},
            success: function (response) {
                // Populate checkboxes with eligible checkers
                $("#checkersList tbody").each(function () {
                    $(this).remove();
                });
                for (var res in response) {
                    var absaNo = response[res].absaNo !== null ? response[res].absaNo : '';
                    var markup = "<tr>" +
                        "<td><input type='checkbox' class='checker-checkbox' value='" + response[res].id + "' id='" + response[res].id + "'></td><td>" + response[res].username + "</td><td>" + absaNo + "</td>" +
                        "</tr>";
                    $('#checkersList').append(markup)
                }
                $('#assignCheckerModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                if (activeProcessingButton) {
                    activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                    activeProcessingButton = null;
                    activeProcessingButtonText = null;
                }
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            }
        });
    }

   var makeReady = function () {
       var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
       var $button = $("#btn-make-ready-policy");
       var originalText = $button.val();

       Swal.fire({
           title: 'Processing...',
           text: 'Please wait while the transaction is being submitted.',
           allowOutsideClick: false,
           allowEscapeKey: false,
           didOpen: () => {
               Swal.showLoading();
           }
       });

       // Get comment from sessionStorage
       var comment = sessionStorage.getItem('policyComment_' + polCode);

       return $.ajax({
           type: 'POST',
           url: SERVLET_CONTEXT + '/protected/life/policies/createLifePolMakeReady',
           dataType: 'json',
           async: true,
           data: comment ? { resubmissionComment: comment } : {}, // Include comment if it exists
           success: function (result) {
               // Clear comment from sessionStorage on success
               if (comment) {
                 //  sessionStorage.removeItem('policyComment_' + polCode);
               }

               Swal.fire({
                   title: 'Success',
                   text: 'Submitted Successfully',
                   icon: 'success'
               }).then(() => {
                   if (result.transType === 'CO') {
                       // Redirect to homepage for CO policies
                       window.location.href = SERVLET_CONTEXT + "/protected/home";
                   } else {
                       populatePolicyDetails();
                       $('#polChecksList').DataTable().ajax.reload();
                       refreshTasksTable(); // Refresh tasks table to show updated comment
                   }
               });
           },
           error: function (jqXHR, textStatus, errorThrown) {
               Swal.fire({
                   title: 'Error',
                   text: jqXHR.responseText,
                   icon: 'error'
               });
               $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
           }
       });
   };

    var undoMakeReady = function() {
        // Fetch rejection reasons when modal is opened
        $.ajax({
            type: 'GET',
            url: 'rejection-reasons',
            dataType: 'json',
            success: function(response) {
                var reasonsHtml = '';
                response.data.forEach(function(reason) {
                    reasonsHtml += `
            <div class="custom-control custom-radio mb-2">
                <input type="radio" class="custom-control-input rejection-reason"
                       id="reason-${reason.reasonId}" value="${reason.reasonId}" name="rejectionReason">
                <label class="custom-control-label" for="reason-${reason.reasonId}">
                    ${reason.reasonDesc}
                </label>
            </div>`;
                });
                $('#rejection-reasons-list').html(reasonsHtml);
                $('#rejectionModal').modal('show');
            },
            error: function(jqXHR) {
                if (activeProcessingButton) {
                    activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                    activeProcessingButton = null;
                    activeProcessingButtonText = null;
                }
                Swal.fire({
                    title: 'Error',
                    text: 'Failed to load rejection reasons',
                    icon: 'error'
                });
            }
        });

        $('#rejectConfirmButton').off('click').on('click', function() {
            var selectedReason = $('input[name="rejectionReason"]:checked').val();
            var comments = $('#rejectionReason').val().trim();

            if (!selectedReason) {
                Swal.fire({
                    title: 'Error',
                    text: 'Please select a rejection reason',
                    icon: 'error'
                });
                return;
            }

            if (!comments) {
                Swal.fire({
                    title: 'Error',
                    text: 'Please provide additional comments',
                    icon: 'error'
                });
                return;
            }

            $.ajax({
                type: 'GET',
                url: 'undoMakeReady',
                data: {
                    reasonId: selectedReason,
                    reason: comments
                },
                success: function() {
                    activeProcessingButton = null;
                    activeProcessingButtonText = null;

                    Swal.fire({
                        title: 'Success',
                        text: 'Submitted Successfully',
                        icon: 'success'
                    }).then(() => {
                        $('#rejectionModal').modal('hide');
                        window.location.href = SERVLET_CONTEXT + "/protected/home";
                    });
                },
                error: function(jqXHR) {
                    if (activeProcessingButton) {
                        activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                        activeProcessingButton = null;
                        activeProcessingButtonText = null;
                    }
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        });
    };


    var createRisk = function () {
        var arr = getRskSections();
        if (arr.length == 0) {
            bootbox.alert("Cannot create Risk without sections")
            return false;
        }
        // arr.shift();
        var $currForm = $('#risk-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createRisk";
        data.sections = arr;
        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});

        $.ajax(
            {
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Risk Transaction created Successfully',
                        icon: 'success'
                    });
                    $('#insured-frm').select2('val', null);
                    populateInsuredLov();
                    $('#subclass-frm').select2('val', null);
                    populateSubclassLov();
                    $('#covertypes-frm').select2('val', null);
                    populateCoverTypesLov();
                    populatePolicyDetails();
                    $("#sect-div").show();
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
    };

    var updateRisk = function () {
        var $currForm = $('#risk-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createRisk";
        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});

        $.ajax(
            {
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Policy Transaction created Successfully',
                        icon: 'success'
                    });
                    $('#insured-frm').select2('val', null);
                    populateInsuredLov();
                    $('#subclass-frm').select2('val', null);
                    populateSubclassLov();
                    $('#covertypes-frm').select2('val', null);
                    populateCoverTypesLov();
                    populatePolicyDetails();
                    $("#sect-div").show();
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
    };

    var updateMakeReadyPolicy = function () {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
        var $currForm = $('#policy-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }

        $('#policy-form input[type=checkbox]').each(function (e) {
            $(this).val($(this).is(':checked'));
        });

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createPolicyMakeReady";

        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            success: function (s) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Policy Transaction created Successfully',
                    icon: 'success'
                })
                $('#insured-frm').select2('val', null);
                populateInsuredLov();
                $('#subclass-frm').select2('val', null);
                populateSubclassLov();
                $('#covertypes-frm').select2('val', null);
                populateCoverTypesLov();
                polCode = s.policyId;
                populatePolicyDetails();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                ;
            },
            dataType: "json",
            contentType: "application/json"
        });
    };

    var updatePolicy = function (isDraftSave) {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
        var $button = $("#btn-add-policy");
        var originalText = 'Save Draft';
        var $currForm = $('#policy-form');
        console.log($currForm)
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            $button.removeClass('btn-processing').prop("disabled", false).val(originalText);

            return;
        }

        $('#policy-form input[type=checkbox]').each(function (e) {
            $(this).val($(this).is(':checked'));
        });

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        console.log(data)
        var url = "createLifePolicy";

        Swal.fire({
            title: 'Processing...',
            text: 'Please wait while the transaction is being created.',
            allowOutsideClick: false,
            allowEscapeKey: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            success: function (s) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: isDraftSave ? 'Policy Draft saved successfully' : 'Policy Transaction created Successfully',
                    icon: 'success'
                })
//					 .then(() => {
//                           // Navigate to the new page only after the success alert
//                           window.location.href = SERVLET_CONTEXT + "/protected/uw/policies/policyEnquiry";
//                     });
                $('#insured-frm').select2('val', null);
                populateInsuredLov();
                $('#subclass-frm').select2('val', null);
                populateSubclassLov();
                $('#covertypes-frm').select2('val', null);
                populateCoverTypesLov();
                polCode = s.policyId;
                populatePolicyDetails();
                $button.removeClass('btn-processing').prop("disabled", false).val(originalText);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                $("#btn-add-policy").removeClass('btn-processing').prop("disabled", false).val('Save Draft');
            },
            dataType: "json",
            contentType: "application/json"
        });
    };

    var createPolicy = function (isDraftSave) {
        var $button = $("#btn-add-policy");
        var originalText = 'Save Draft';
    var $currForm = $('#policy-form');
    var currValidator = $currForm.validate();
    if (!$currForm.valid()) {
        $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
        return;
    }

    $('#policy-form input[type=checkbox]').each(function (e) {
        $(this).val($(this).is(':checked'));
    });

    var data = {};
    $currForm.serializeArray().map(function (x) {
        data[x.name] = x.value;
    });
    console.log("INVESTMENT: " + data.investment);
    var url = "createLifePolicy";
    if (!$('#chk-import-risks').is(':checked')) {
        var riskForm = $("#risk-form");
        var riskValidator = riskForm.validate();
        if (!riskForm.valid()) {
            $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
            return;
        }
        // Validate sum assured and premium
        var sumAssured = parseFloat($('#sumassured-amt').val());
        var premium = parseFloat($('#premium-amt').val());
        if (sumAssured === 0 || premium === 0) {
            Swal.fire({
                title: 'Error',
                text: 'Sum Assured or Premium must be greater than zero.',
                icon: 'error'
            });

            $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
            return;
        }

        var arr = getRskSections();
        if (arr.length == 0) {
            bootbox.alert("Cannot create policy without sections")
            $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
            return false;
        }
        // arr.shift();
        data.sections = arr;
        data.riskBean = getRiskDetails();
    }

    Swal.fire({
        title: 'Processing...',
        text: 'Please wait while the transaction is being created.',
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });
    // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
    $.ajax(
        {
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            success: function (s) {
                // $('#myPleaseWait').modal('hide');
                $(".risk-detail-tab").show();
                Swal.fire({
                    title: 'Success',
                    text: isDraftSave ? 'Policy Draft saved successfully' : 'Policy Transaction created Successfully',
                    icon: 'success'
                })
                window.location.href = SERVLET_CONTEXT + "/protected/uw/policies/editlifepolicy";
                //  .then(() => {
                //       // Navigate to the new page only after the success alert
                //       window.location.href = SERVLET_CONTEXT + "/protected/uw/policies/policyEnquiry";
                // });
                // $('#lifeassured-frm').select2('val', null);
                populateInsuredLov();
                $('#subclass-frm').select2('val', null);
                populateSubclassLov();
                $('#covertypes-frm').select2('val', null);
                populateCoverTypesLov();
                polCode = s.policyId;
                populatePolicyDetails();
                $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                $button.removeClass('btn-processing').prop("disabled", false).val(originalText);
            },
            dataType: "json",
            contentType: "application/json"
        });
};

    var getRiskDetails = function () {
        var $currForm = $('#risk-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return;
        }
        $('#risk-form input[type=checkbox]').each(function (e) {
            $(this).val($(this).is(':checked'));
        });
        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        return data;
    };

    var getRskSections = function () {
        var arr = [];
        $("#section_form_tbl tr").each(function (row, tr) {
            var isChecked = $(this).find('.premium-checkbox').is(':checked');
            if (isChecked) {
                var section = $(this).find('.section').eq(0).val();
                var ratesApplicable = $(this).find('.ratesApplicable').eq(0).val();
                var rate = $(this).find('.rate').eq(0).val();
                var divFactor = $(this).find('.divFactor').eq(0).val();
                var freeLimit = $(this).find('.freeLimit').eq(0).val();
                var amount = $(this).find('.amount').eq(0).val();
                var multiplierRate = $(this).find('.multiplierRate').eq(0).val();
                arr.push({
                    section: section,
                    ratesApplicable: ratesApplicable,
                    rate: rate,
                    divFactor: divFactor,
                    freeLimit: freeLimit,
                    amount: amount,
                    multiplierRate: multiplierRate
                });
            }
        });
        return arr;
    };

    var getRiskCertificates = function () {
        var url = "riskCerts/" + selRiskCode;
        var currTable = $('#cert_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "cert",
                    "render": function (data, type, full, meta) {
                        return full.cert.certLots.certTypes.certDesc;
                    }
                },
                {
                    "data": "status",
                    "render": function (data, type, full, meta) {
                        if (full.status) {
                            if (full.status === "A") {
                                return "Active";
                            } else if (full.status === "C") {
                                return "Cancelled";
                            } else if (full.status === "I") {
                                return "Inactive";
                            }
                        } else
                            return full.status;
                    }
                },
                {
                    "data": "printStatus",
                    "render": function (data, type, full, meta) {
                        if (full.printStatus) {
                            if (full.printStatus === "P") {
                                return "Printed";
                            } else if (full.printStatus === "R") {
                                return "Ready";
                            }
                        } else
                            return full.printStatus;
                    }
                },
                {
                    "data": "certWef",
                    "render": function (data, type, full, meta) {
                        return moment(full.certWef).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "certWet",
                    "render": function (data, type, full, meta) {
                        return moment(full.certWet).format('DD/MM/YYYY');
                    }
                },
                {"data": "certNo"},
                {"data": "reasonCancelled"},
                {
                    "data": "pcId",
                    "render": function (data, type, full, meta) {
                        if (full.risk.policy.authStatus) {
                            if (full.risk.policy.authStatus === "D")
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskCerts(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certs=' + encodeURI(JSON.stringify(full)) + ' disabled><i class="fa fa-pencil-square-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskCerts(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "pcId",
                    "render": function (data, type, full, meta) {
                        if (full.risk.policy.authStatus) {
                            if (full.risk.policy.authStatus === "D")
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-certs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskCerts(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-certs=' + encodeURI(JSON.stringify(full)) + '  disabled><i class="fa fa-trash-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-certs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskCerts(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        }));
        return currTable;
    };

    var getRiskSections = function () {
        var url = "risksSections/" + selRiskCode;
        var currTable = $('#section_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "section",
                    "render": function (data, type, full, meta) {

                        return full.section.desc;
                    }
                },
                {"data": "rate"},
                {
                    "data": "calcprem",
                    "render": function (data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.calcprem);
                    }
                },
                {
                    "data": "prem",
                    "render": function (data, type, full, meta) {

                        return UTILITIES.currencyFormat(full.prem);
                    }
                },
                {
                    "data": "sectId",
                    "render": function (data, type, full, meta) {
                        // if(full.risk.policy.authStatus){
                        // 	if(full.risk.policy.authStatus ==="D")
                        // 		return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskSection(this);"><i class="fa fa-pencil-square-o"></button>';
                        // 	else
                        // 		return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskSection(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                        //
                        // }
                        // else
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskSection(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "sectId",
                    "render": function (data, type, full, meta) {
                        // if(full.risk.policy.authStatus){
                        // 	if(full.risk.policy.authStatus ==="D")
                        // 		return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskSection(this);"><i class="fa fa-trash-o"></button>';
                        // 	else
                        // 		return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskSection(this);" disabled><i class="fa fa-trash-o"></button>';
                        //
                        // }
                        // else
                        return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskSection(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        }));
        return currTable;
    };

    var getCommissionAllocs = function (receiptId) {
        var url = SERVLET_CONTEXT + "/protected/life/policies/allocationCommission/" + receiptId;
        var currTable = $('#receiptalloc_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "allocCommId",
                    "render": function (data, type, full, meta) {

                        return full.installNo;
                    }
                },
                {
                    "data": "allocCommId",
                    "render": function (data, type, full, meta) {

                        return full.premiumItem;
                    }
                },
                {
                    "data": "allocCommId",
                    "render": function (data, type, full, meta) {

                        return UTILITIES.currencyFormat(full.instalmentPremium);
                    }
                },
                {
                    "data": "allocCommId",
                    "render": function (data, type, full, meta) {

                        return UTILITIES.currencyFormat(full.commissionAmt);
                    }
                },
                {
                    "data": "allocCommId",
                    "render": function (data, type, full, meta) {
                        if (full.subAgentCommissionAmt) {
                            return UTILITIES.currencyFormat(full.subAgentCommissionAmt);
                        } else {
                            return UTILITIES.currencyFormat(full.marketerCommissionAmt);
                        }
                    }
                },
                {
                    "data": "allocCommId",
                    "render": function (data, type, full, meta) {

                        return full.paidToDate;

                    }
                },
            ]
        }));
        return currTable;
    };

    var deleteBeneficiary = function (button) {
        var beneficiaries = JSON.parse(decodeURI($(button).data("beneficiary")));
        bootbox.confirm("Are you sure want to delete " + beneficiaries['beneficiaryName'] + "?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deleteBeneficiary/' + beneficiaries['beneficiaryCode'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#benefeciary_tbl').DataTable().ajax.reload();

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
    };


    var getPolicybeneficiaries = function () {
        var url = "policyBeneficiary/" + polCode;
        var currTable = $('#benefeciary_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "beneficiaryName",
                    "render": function (data, type, full, meta) {

                        return full.beneficiaryName;
                    }
                },
//                {
//                    "data": "relationshipType",
//                    "render": function (data, type, full, meta) {
//                        return full.relationshipType.relationDesc;
//                    }
//                },
                {
                    "data": "relationshipType",
                    "render": function (data, type, full, meta) {
                        return full.beneficiaryRelationship;
                    }
                },
                {
                    "data": "benAllocation",
                    "render": function (data, type, full, meta) {
                        return full.benAllocation;
                    }
                },
                {
                    "data": "beneficiaryregNo",
                    "render": function (data, type, full, meta) {

                        return full.beneficiaryregNo;
                    }
                },
                {
                    "data": "beneficiaryCode",
                    "render": function (data, type, full, meta) {
                        var buttons = '';
                        // Add Edit button
                        buttons += '<button type="button" class="btn btn-success btn-sm" data-beneficiary=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editBeneficiary(this);"><i class="fa fa-pencil"></i></button>';
                        return buttons;
                    },
                    "orderable": false // Disable ordering on the action column
                },
                {
                    "data": "beneficiaryCode",
                    "render": function (data, type, full, meta) {
                        var buttons = '';

                        // Add Delete button
                        if (full.policy && full.policy.authStatus) {
                            if (full.policy.authStatus === "D") {
                                buttons += '<button type="button" class="btn btn-danger btn-sm" data-beneficiary=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteBeneficiary(this);"><i class="fa fa-trash-o"></i></button>';
                            } else {
                                buttons += '<button type="button" class="btn btn-danger btn-sm" data-beneficiary=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteBeneficiary(this);" disabled><i class="fa fa-trash-o"></i></button>';
                            }
                        } else {
                            buttons += '<button type="button" class="btn btn-danger btn-sm" data-beneficiary=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteBeneficiary(this);"><i class="fa fa-trash-o"></i></button>';
                        }

                        return buttons;
                    },
                    "orderable": false
                }
            ]
        }));
        return currTable;
    };


    var getPolicybenefits = function () {
        var url = "policyBenefits/" + polCode;
        var currTable = $('#benefit_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "maturityYear"
                },
                {
                    "data": "estBenefit",
                    "render": function (data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.estBenefit);
                    }
                }
            ]
        }));
        return currTable;
    };

    var getPolicyInstallments = function () {
        var url = SERVLET_CONTEXT + "/protected/life/policies/policyInstallments";
        var currTable = $('#installments_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "installmentNo",
                    "render": function (data, type, full, meta) {
                        return full.installmentNo;
                    }
                },
                {
                    "data": "installPrem",
                    "render": function (data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.installPrem);
                    }
                },
                {
                    "data": "dueDate",
                    "render": function (data, type, full, meta) {

                        return moment(full.dueDate).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "installPaid",
                    "render": function (data, type, full, meta) {
                        if (full.installPaid && full.installPaid === 'Y')
                            return 'Yes';
                        else
                            return 'No';
                    }
                },
                {
                    "data": "paidDate",
                    "render": function (data, type, full, meta) {
                        if (full.paidDate)
                            return moment(full.paidDate).format('DD/MM/YYYY');
                        else return '';
                    }
                },
            ]
        }));
        return currTable;
    };

    var getRiskDocs = function () {
        var url = "riskDocs/" + selRiskCode;
        var currTable = $('#risk_docs_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {

                        return full.polRevNo;
                    }
                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {

                        return full.docShtDesc;
                    }
                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {

                        return full.docDesc;
                    }
                },
                {"data": "uploadedFileName"},
                {"data": "uploadedBy"},
                {
                    "data": "uploadedDate",
                    "render" : function(data, type, full, meta) {
                        return full.uploadedDate ? moment(full.uploadedDate).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {"data": "verifiedBy"},
                {
                    "data": "verifiedDate",
                    "render": function (data, type, full, meta) {
                        return full.verifiedDate ? moment(full.verifiedDate).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {
                    "render": function(data, type, full, meta) {
                        const isMaker = full.createdBy === currentUserId || isPolicyCreator;
                        const isVerified = !!full.verifiedDate;
                        if (isMaker && !isVerified) return ''; // Maker can't verify and it's not verified
                        if (isVerified) {
                            // If verified, show a green tick icon
                            return `
                                <div class="d-flex justify-content-center">
                                    <i class="fa fa-check-circle text-success" title="Verified"></i>
                                </div>
                            `;
                                        }
                                        // Otherwise, show the checkbox (if not verified)
                                        return `
                            <div class="form-check d-flex justify-content-center">
                                <input type="checkbox" class="form-check-input verify-checkbox"
                                       data-id="${full.rdId}">
                            </div>
                        `;
                    }
                },
                {
                    "data": "comments",
                    "render": function(data, type, full, meta) {
                        const isMaker = full.createdBy === currentUserId || isPolicyCreator;
                        let hasComment = full.comments && full.comments.trim() !== '';

                        return `
                                <div class="d-flex flex-column">
                                    ${hasComment ? `<span class="mb-1 text-muted small">${full.comments}</span>` : ''}
                                    ${!isMaker ? `
                                        <button type="button" class="btn btn-success btn-info btn-sm"
                                            onclick="openCommentModalFromText('${full.rdId}', '${hasComment ? full.comments.replace(/'/g, "\\'") : ''}')">
                                            Comment
                                        </button>
                                    ` : ''}
                                </div>
                            `;
                    }
                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {
                        if (full.authStatus !== 'A' && full.policyId === polCode && !full.uploadedDate)
                            return '<button type="button" class="btn btn-success btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskDocs(this);">Upload</button>';
                        else
                            return '<button type="button" class="btn btn-success btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskDocs(this);" disabled>Upload</button>';
                    }
                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.downloadRiskDoc(this);">View</button>';

                    }

                },
                {
                    "data": "rdId",
                    "render": function (data, type, full, meta) {
                        if (full.authStatus !== 'A' && full.policyId === polCode)
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskDoc(this);"><i class="fa fa-trash-o"></button>';
                        else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskDoc(this);" disabled><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        }));

        window.refreshRiskDocsTable = function() {
            currTable.ajax.reload(null, false);
        };
        return currTable;
    };
    $(document).on('change', '.verify-checkbox', function () {
        const checkbox = this;
        const rdId = $(checkbox).data('id');
        console.log("rdId being passed:", rdId);
        handleVerification(checkbox, rdId);
    });

    window.openCommentModalFromButton = function(rdId) {
        $('#commentDocId').val(rdId);
        $('#docCommentText').val('');
        $('#docCommentModal').modal('show');
    };

    window.openCommentModalFromText = function(rdId, commentText) {
        $('#commentDocId').val(rdId);
        $('#docCommentText').val(commentText);
        $('#docCommentModal').modal('show');
    };
    function handleVerification(checkbox, rdId) {
        console.log("rdId being passed:", rdId);
        if (!checkbox.checked) return; // Prevent unchecking logic

        Swal.fire({
            title: "Are you sure?",
            text: "You are verifying this document.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Yes, verify it!",
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `verifyRiskDoc?rdId=${rdId}`,
                    type: 'GET',
                    success: function (res) {
                        Swal.fire("Verified!", "Document has been verified.", "success");
                        refreshRiskDocsTable();
                    },
                    error: function (err) {
                        Swal.fire("Error", "Something went wrong. Please try again.", "error");
                        checkbox.checked = false; // revert check
                    }
                });
            } else {
                checkbox.checked = false;
            }
        });
    }



    var getUWPolicyReceipts = function (policyCode) {
        var url = SERVLET_CONTEXT + "/protected/life/policies/getpolicyReceipts/" + policyCode;
        var currTable = $('#receipts_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "receiptNo",
                    "render": function (data, type, full, meta) {

                        return (full.receiptNo);
                    }
                },
                {
                    "data": "receiptDate",
                    "render": function (data, type, full, meta) {

                        return moment(full.receiptDate).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "dc",
                    "render": function (data, type, full, meta) {

                        return full.dc;
                    }
                },
                {
                    "data": "receiptAmount",
                    "render": function (data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.receiptAmount);
                    }
                },
                {
                    "data": "allocationAmount",
                    "render": function (data, type, full, meta) {
                        if (full.allocationAmount) {
                            return UTILITIES.currencyFormat(full.allocationAmount);
                        }
                    }

                },
                {
                    "data": "balance",
                    "render": function (data, type, full, meta) {
                        if (full.balance) {
                            return UTILITIES.currencyFormat(full.balance);
                        } else {
                            return UTILITIES.currencyFormat(0);

                        }
                    }

                },
                {
                    "data": "commissionAmount",
                    "render": function (data, type, full, meta) {
                        if (full.commissionAmount) {
                            return UTILITIES.currencyFormat(full.commissionAmount);
                        }
                    }

                },

                {
                    "data": "whtxAmt",
                    "render": function (data, type, full, meta) {
                        if (full.whtxAmt) {
                            return UTILITIES.currencyFormat(full.whtxAmt);
                        }
                    }

                },

                {
                    "data": "subAgentcommissionAmount",
                    "render": function (data, type, full, meta) {
                        console.log(full.subAgentcommissionAmount)
                        if (full.subAgentcommissionAmount) {
                            return UTILITIES.currencyFormat(full.subAgentcommissionAmount);
                        } else {
                            return UTILITIES.currencyFormat(full.marketerCommissionAmount);
                        }

                    }

                },
            ]
        }));

        $('#receipts_tbl tbody').on('click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            if (aData[0] === undefined || aData[0] === null) {

            } else {
                console.log(aData[0]);
                getCommissionAllocs(aData[0].lifeRctId);
            }
        });
    }

    var getUWPolicyRisks = function (policyCode) {
        console.log('policyCode ', policyCode);
        var url = SERVLET_CONTEXT + "/protected/life/policies/policyRisks/" + policyCode;
        var currTable = $('#risk_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "insured",
                    "render": function (data, type, full, meta) {

                        return (full.fname + " " + full.othernames);
                    }
                },
                {
                    "data": "subclass",
                    "render": function (data, type, full, meta) {

                        return full.subDesc;
                    }
                },
                {
                    "data": "covertype",
                    "render": function (data, type, full, meta) {

                        return full.covName;
                    }
                },
                {
                    "data": "workingAge",
                    "render": function (data, type, full, meta) {
                        return full.age;

                    }
                },

            ]
        }));

        $('#receipts_tbl tbody').on('click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            if (aData[0] === undefined || aData[0] === null) {

            } else {
                console.log(aData[0]);
                getCommissionAllocs(aData[0].lifeRctId);
            }
        });
    }

    var getUWPolicyRisks = function (policyCode) {
        console.log('policyCode ', policyCode);
        var url = SERVLET_CONTEXT + "/protected/life/policies/policyRisks/" + policyCode;
        var currTable = $('#risk_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "insured",
                    "render": function (data, type, full, meta) {

                        return (full.fname + " " + full.othernames);
                    }
                },
                {
                    "data": "subclass",
                    "render": function (data, type, full, meta) {

                        return full.subDesc;
                    }
                },
                {
                    "data": "covertype",
                    "render": function (data, type, full, meta) {

                        return full.covName;
                    }
                },
                {
                    "data": "workingAge",
                    "render": function (data, type, full, meta) {

                        return full.age;
                    }
                },
                {
                    "data": "riskId",
                    "render": function (data, type, full, meta) {
                        console.log(full)
                        if (full.authStatus) {
                            if (full.authStatus === "D"  || full.authStatus === "RCV")

                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editLifePolicyRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + '  disabled><i class="fa fa-pencil-square-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editLifePolicyRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "riskId",
                    "render": function (data, type, full, meta) {
                        if (full.authStatus) {
                            if (full.authStatus === "D")
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + '  disabled><i class="fa fa-pencil-square-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRisk(this);"><i class="fa fa-pencil-square-o"></button>';

                    }

                },
            ]
        }));

        $('#risk_tbl tbody').on('click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            if (aData[0] === undefined || aData[0] === null) {

            } else {
                console.log(aData[0]);
                selRiskCode = aData[0].riskId;
                $("#risk-code-pk").val(selRiskCode);
                getRiskSections();
                //getRiskIntParties();
                getRiskDocs();
                getRefundDocs();
                getRiskCertificates();
                //getRiskSchedules();
                getClientDocs(aData[0].tenId);
                $("#risk-det-id-pk").val(aData[0].binderDetId);
                populateRiskSections(aData[0].binderDetId);
                $("#cert-from-date").val(moment(aData[0].wefDate).format('DD/MM/YYYY'));
                $("#cert-wet-date").val(moment(aData[0].wetDate).format('DD/MM/YYYY'));
                console.log("binder=" + $("#binder-id").val())
                if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
                    $.ajax({
                        type: 'GET',
                        url: 'getLifeClientAge',
                        dataType: 'json',
                        data: {"clientId": aData[0].tenId, "binCode": $("#binder-id").val()},
                        async: true,
                        success: function (result) {
                            $("#lifeassured-age").val(result);
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
            }
        });
        return currTable;
    };

    var deleteRiskCerts = function () {
        var certs = JSON.parse(decodeURI($(button).data("certs")));
        bootbox.confirm("Are you sure want to delete " + certs['cert'].certLots.certTypes.certDesc + "?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deleteRiskCert/' + certs['pcId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#cert_tbl').DataTable().ajax.reload();

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
    };
    var editRiskCerts = function (button) {
        var certs = JSON.parse(decodeURI($(button).data("certs")));
        $("#cert-pc-id").val(certs['pcId']);
        $("#risk-certtype-name").text(certs['cert'].certLots.certTypes.certDesc);
        $("#risk-cert-status").val(certs['status']);
        $("#risk-cert-from-date").val(moment(certs['certWef']).format('DD/MM/YYYY'));
        $("#risk-cert-wet-date").val(moment(certs['certWet']).format('DD/MM/YYYY'));
        $("#risk-canc-date").val(moment(certs['certWet']).format('DD/MM/YYYY'));
        if (certs['status']) {
            if (certs['status'] === "A") {
                $(".cert-cancellation").hide();
            } else if (certs['status'] === "C") {
                $(".cert-cancellation").show();
            } else {
                $(".cert-cancellation").hide();
            }

        }
        $('#editRiskCertModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    };

    var downloadRiskDoc = function (button) {
        var docs = JSON.parse(decodeURI($(button).data("docs")));
        window.open(SERVLET_CONTEXT + "/protected/uw/policies/riskdocument/" + docs['rdId'],
            '_blank' // <- This is what makes it open in a new window.
        );
    }


    var deleteRiskDoc = function (button) {
        var docs = JSON.parse(decodeURI($(button).data("docs")));
        bootbox.confirm("Are you sure want to delete " + docs['docDesc'] + "?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deleteRiskDoc/' + docs['rdId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#risk_docs_tbl').DataTable().ajax.reload();
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

    var editRiskDocs = function (button) {
        var docs = JSON.parse(decodeURI($(button).data("docs")));
        $("#risk-doc-name").text(docs["docDesc"]);
        $("#risk-upload-name").text(docs['uploadedFileName']);
        $("#risk-doc-id").val(docs['rdId']);
        $('#riskdocModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    }

    var uploadRiskDocument = function () {
        var $form = $("#risk-doc-form");
        var validator = $form.validate();
        $('form#risk-doc-form')
            .submit(function (e) {
                e.preventDefault();

                if (!$form.valid()) {
                    return;
                }
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                var $submitButton = $('#upload-doc-btn');
                var originalText = $submitButton.text();
                $submitButton.prop('disabled', true).text('Uploading...');

                var data = new FormData(this);
                data.append('file', $('#avatar')[0].files[0]);

                // Show spinner
                $('#upload-spinner').show();

                $.ajax({
                    url: 'uploadRequiredDocs',
                    type: 'POST',
                    data: data,
                    processData: false,
                    contentType: false,
                    success: function (s) {
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
                        refreshRiskDocsTable();
                        $('#risk_docs_tbl').DataTable().ajax.reload();

                    },
                    error: function (xhr, error) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Error',
                            text: xhr.responseText,
                            icon: 'error'
                        });
                        $('#upload-spinner').hide();
                    },
                    complete: function () {
                        // Reset UI
                        $submitButton.prop('disabled', false).text(originalText);
                        $('#upload-spinner').hide();
                    }
                });
            });
    }

    var editRiskSection = function (button) {
        var section = JSON.parse(decodeURI($(button).data("risksections")));
        console.log(section);
        $("#sect-code-pk").val(section['sectId']);
        $("#sect-limit-amt").val(section['amount']);
        $("#sect-rate").val(section['rate']);
        $("#sect-free-limit").val(section['freeLimit']);
        $("#sect-div-fact").val(section['divFactor']);
        $("#sect-multi-rate").val(section['multiRate']);
        //$("#chk-compute").prop("checked", section["compute"]);
        $("#risk-sect-id").val(section['section'].id);
        $("#risk-sect-name").val(section['section'].desc);
        $("#sect-prem-id-pk").val(section['premRates'].id);
        if (section['risk']) {
            $("#risk-sect-code-pk").val(section['risk'].riskId);
        } else {
            $("#risk-sect-code-pk").val(section['riskId']);
        }
        populateRiskSections($("#risk-det-id-pk").val());
        $('#sect-limit-amt,#sect-free-limit').number(true, 2);
        $("#risk-sect-frm").select2("readonly", true);
        if ($("#pol-bin-type").val() === "B") {
            $("#sect-rate").prop("readonly", true);
            $("#sect-free-limit").prop("readonly", true);
            $("#sect-div-fact").attr("style", "pointer-events: none;");
        } else {
            $("#sect-rate").prop("readonly", false);
            $("#sect-free-limit").prop("readonly", false);
            //$("#sect-div-fact").prop('disabled', false);
        }

        $('#sectModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    }

    var deleteRiskSection = function (button) {
        var section = JSON.parse(decodeURI($(button).data("risksections")));
        bootbox.confirm("Are you sure want to delete " + section['section'].desc + "?", function (result) {
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
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#section_tbl').DataTable().ajax.reload();
                        populatePolicyDetails();
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


    var populateRiskSections = function (detCode) {
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

    var saveRiskSections = function () {
        var $classForm = $('#risk-sect-form');
        var validator = $classForm.validate();
        $('#saveRiskSection').click(function () {
            if (!$classForm.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})

            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = SERVLET_CONTEXT+"/protected/uw/policies/saveRiskSections";
            var request = $.post(url, data);
            request.success(function () {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#section_tbl').DataTable().ajax.reload();
                populatePolicyDetails();
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


    var createPolicyClauses = function () {
        var url = "policyClauses";
        var currTable = $('#polclausesList').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {"data": "clauHeading"},
                {
                    "data": "clause",
                    "render": function (data, type, full, meta) {
                        if (full.clause.clause.clauseType) {
                            if (full.clause.clause.clauseType === 'E') return "Excess";
                            else if (full.clause.clause.clauseType === 'L') return "Limits";
                            else if (full.clause.clause.clauseType === 'C') return "Clause";
                            else if (full.clause.clause.clauseType === 'X') return "Exclusion";
                            else return "";
                        } else {
                            return "";
                        }
                    }
                },
                {"data": "editable"},
                {"data": "clauWording"},
                {
                    "data": "polClauseId",
                    "render": function (data, type, full, meta) {
                        if (full.policy.authStatus) {
                            if (full.policy.authStatus === "D") {
                                if (full.editable) {
                                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolicyClause(this);"><i class="fa fa-pencil-square-o"></button>';
                                } else {
                                    return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';
                                }
                            } else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolicyClause(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }

                },
                {
                    "data": "polClauseId",
                    "render": function (data, type, full, meta) {
                        if (full.policy.authStatus) {
                            if (full.policy.authStatus === "D" && $("#pol-bin-type").val() === "M") {

                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deletePolicyClause(this);"><i class="fa fa-trash-o"></button>';


                            } else
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deletePolicyClause(this);" disabled><i class="fa fa-trash-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-clauses=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deletePolicyClause(this);" disabled><i class="fa fa-trash-o"></button>';

                    }

                },
            ]
        }));
        return currTable;
    }

    var editPolicyClause = function (button) {
        var clause = JSON.parse(decodeURI($(button).data("clauses")));
        if (!clause['editable']) {
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


    var createPolClauses = function () {
        var $classForm = $('#pol-clause-form');
        var validator = $classForm.validate();
        $('#savepolClauseBtn').click(function () {
            if (!$classForm.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "createPolicyClause";
            var request = $.post(url, data);
            request.success(function () {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
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
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }

    var deletePolicyClause = function (button) {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
        var clause = JSON.parse(decodeURI($(button).data("clauses")));
        bootbox.confirm("Are you sure want to delete " + clause['clauHeading'] + "?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deletePolClause/' + clause['polClauseId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#polclausesList').DataTable().ajax.reload();
                        populatePolicyDetails();
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


    var createPolicyChecks = function () {
        var url = "policyChecks";
        var currTable = $('#polChecksList').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [

                {
                    "data": "tcNo",
                    "render": function (data, type, full, meta) {
                        return full.checks.checkName;
                    }
                },
                {
                    "data": "authorised",
                    "render": function (data, type, full, meta) {
                        if (full.authorised)
                            return full.authorised;
                        else return "No";
                    }
                },
                {
                    "data": "authBy",
                    "render": function (data, type, full, meta) {
                        if (full.authBy)
                            return full.authBy.username;
                        else return "";
                    }
                },
                {
                    "data": "authDate",
                    "render": function (data, type, full, meta) {
                        if (full.authDate)
                            return moment(full.authDate).format('DD/MM/YYYY');
                        else return "";
                    }
                },
                {
                    "data": "checks",
                    "render": function (data, type, full, meta) {
                        if (full.authorised && full.authorised === "Y") {

                        } else
                            return '<button type="button" class="btn btn-info btn btn-info btn-sm" data-checks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.authChecks(this);">Approve</button>';


                    }

                },
            ]
        }));
        return currTable;
    }

    var authChecks = function (button) {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
        var checks = JSON.parse(decodeURI($(button).data("checks")));
        bootbox.confirm("Are you sure want to Approve?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'authChecks/' + checks["tcNo"],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Check Approved Successfully',
                            icon: 'success'
                        });
                        $('#polChecksList').DataTable().ajax.reload();
                        populatePolicyDetails();
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


    var createPolicyTaxes = function () {
        var url = "policyTaxes";
        var currTable = $('#polTaxesList').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "revenueItems",
                    "render": function (data, type, full, meta) {
                        return UTILITIES.getRevDesc(full.revenueItems.item);
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
                        if (full.policy.authStatus) {
                            if (full.policy.authStatus === "D")
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolTaxes(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolTaxes(this);" disabled><i class="fa fa-pencil-square-o"></button>';

                    }

                },
                {
                    "data": "polTaxId",
                    "render": function (data, type, full, meta) {
                        if (full.policy.authStatus) {
                            if (full.policy.authStatus === "D")
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deletePolTaxes(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deletePolTaxes(this);" disabled><i class="fa fa-trash-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deletePolTaxes(this);" disabled><i class="fa fa-trash-o"></button>';


                    }

                },
            ]
        }));
        return currTable;
    }

    var editPolTaxes = function (button) {
        var tax = JSON.parse(decodeURI($(button).data("poltaxes")));
        $("#tax-code").val(tax["polTaxId"]);
        $("#tax-pol-code").val(tax["policy"].policyId);
        $("#tax-rev-code").val(tax["revenueItems"].revenueId);
        $("#tax-trans-code").text(UTILITIES.getRevDesc(tax["revenueItems"].item));
        $("#pol-tax-sub-code").val(tax["subclass"].subId);
        $("#tax-rate-type").val(tax["rateType"]);
        $("#tax-rate").val(tax["taxRate"]);
        $("#tax-div-fact").val(tax["divFactor"]);
        if (tax["taxLevel"])
            $("#tax-level").val(tax["taxLevel"]);
        else
            $("#tax-level").val("P");
        $('#taxRatesModal').modal({
            backdrop: 'static',
            keyboard: true
        })
    }
    var editBeneficiary = function (button) {
        var beneficiary = JSON.parse(decodeURI($(button).data("beneficiary")));

        // Update modal title to indicate editing
        $("#beneficiaryModalLabel").text('Edit Beneficiary');

        // Reset the form fields before populating with beneficiary data
        $("#beneficiary-form")[0].reset();

        // Populate form fields with beneficiary data
        $("#part-code").val(beneficiary.beneficiaryCode);
        $("#part-pol-code").val(beneficiary.policy?.policyId || "");
        $("#part-name").val(beneficiary.beneficiaryName);
        $("#part-email").val(beneficiary.beneficiaryemailAddress);
        $("#part-id-no").val(beneficiary.beneficiaryregNo);
        $("#ben-allocation").val(beneficiary.benAllocation);
        $("#part-post-address").val(beneficiary.beneficiarypostalAddress);
        $("#part-tel").val(beneficiary.beneficiarytelNo);
        $("#date-reg").val(beneficiary.dateRegistered);
        $("#relationship-type-str").val(beneficiary.beneficiaryRelationship);
        populateRelationTypes();

        // Set the selected relationship type if available
        if (beneficiary.relationshipType) {
            var selectedOption = {
                relationDesc: beneficiary.relationshipType.relationDesc,
                typeId: beneficiary.relationshipType.typeId
            };
            $("#relationship-type").select2("data", selectedOption);
            $("#relation-type-id").val(beneficiary.relationshipType.typeId);
        }

        // Open the modal
        $('#beneficiaryModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    };


    var updatePolicyTaxes = function () {
        var $classForm = $('#pol-taxes-form');
        var validator = $classForm.validate();
        $('#savepoltaxesBtn').click(function () {
            if (!$classForm.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "createPolicyTax";
            var request = $.post(url, data);
            request.success(function () {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#polTaxesList').DataTable().ajax.reload();
                populatePolicyDetails();
                validator.resetForm();
                $('#pol-taxes-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#taxRatesModal').modal('hide');
            });

            request.error(function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }

    var deletePolTaxes = function (button) {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
        var tax = JSON.parse(decodeURI($(button).data("poltaxes")));
        bootbox.confirm("Are you sure want to delete " + UTILITIES.getRevDesc(tax["revenueItems"].item) + "?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deletePolTaxes/' + tax["polTaxId"],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#polTaxesList').DataTable().ajax.reload();
                        populatePolicyDetails();
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


    var newRisk = function () {
        $("#btn-add-risk").on('click', function () {
            $("#lifeassured-code").val("");
            $("#lifeassured-name").val("");
            $("#lifeassured-other-name").val("");
            $('#lifeassured-frm').select2('val', null);

            populateInsuredLov();
            $('#subclass-frm').select2('val', null);
            populateSubclassLov();
            $('#covertypes-frm').select2('val', null);
            populateCoverTypesLov();
            $('#risk-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
            $("#risk-binder-code").val($("#risk-bind-code").val());
            if ($("#pol-buss-type").val() === "S") {
                $("#prorated-full").val("S");
            } else {
                $("#prorated-full").val("P");
            }
            $("#risk-form").show();
            $("#risk-div").hide();
            $("#sect-div").hide();
            $("#prem-rates-div").show();
            $("#btn-save-risk").show();
            $("#btn-save-cancel").show();

            $("#myTab #show-taxes,#show-clauses").hide();
            $("#binder-id").val($("#risk-bind-code").val());
            populateSubclassLov();
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
            populatePolicyDetails();
        })
    }

    var deleteRisk = function (button) {
        var risks = JSON.parse(decodeURI($(button).data("policyrisks")));
        bootbox.confirm("Are you sure want to delete " + risks['riskShtDesc'] + "?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deleteRisk/' + risks['riskId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#risk_tbl').DataTable().ajax.reload();
                        getRiskSections(-2000);
                        //getRiskIntParties(-2000);
                        getRiskDocs(-2000);
                        getRefundDocs(-2000)
                        $('#section_tbl').DataTable().ajax.reload();
                        getRiskCertificates(-2000);
                        //getRiskSchedules(-2000);
                        $("#risk-sched_tbl").DataTable().ajax.reload();
                        $('#cert_tbl').DataTable().ajax.reload();
                        populatePolicyDetails();
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

    var editLifePolicyRisk = function (button) {
        console.log('passed here...');
        var risks = JSON.parse(decodeURI($(button).data("policyrisks")));
        console.log(risks);

        $("#risk-binder-code").val($("#risk-bind-code").val());
        $("#risk-id").val(risks["riskShtDesc"]);
        $("#risk-desc").val(risks["riskDesc"]);
        $("#risk-wef-date").val(moment(risks["wefDate"]).format('DD/MM/YYYY'));
        $("#risk-wet-date").val(moment(risks["wetDate"]).format('DD/MM/YYYY'));
        $("#prorated-full").val(risks["prorata"]);
        $("#comm-rate").val(risks["commRate"]);
        $("#overrid-prem").val(risks["butchargePrem"]);

        // Check if Wealth Builder is selected
        if ($("#pol-prod-name").text().trim() === "Wealth Builder") {
            // Hide Sum Assured and Both, and select Premium by default
            $("#pcompute").prop("checked", true); // Premium selected
            $(".sumassured-disp").hide(); // Hide Sum Assured field
            $("#bcompute").closest("label").hide(); // Hide Both option
            $("#scompute").closest("label").hide(); // Hide Sum Assured option
            $(".computetype-disp").hide(); // Optionally hide the whole compute type section

            // Set Premium value from the risks object
            $("#premium-amt").val(risks["premium"]);

        } else {
            // Normal behavior for non-Wealth Builder products
            $(".computetype-disp").show(); // Show compute type options
            $("#bcompute").closest("label").show(); // Show Both option
            $("#scompute").closest("label").show(); // Show Sum Assured option

            if (risks["computeType"] == "P") {
                $("#pcompute").prop("checked", true);
                $("#premium-amt").val(risks["premium"]);
                $(".sumassured-disp").hide();
            } else if (risks["computeType"] == "S") {
                $("#scompute").prop("checked", true);
                $("#sumassured-amt").val(risks["sumInsured"]);
                $(".premium-disp").hide();
            } else {
                $("#bcompute").prop("checked", true);
                $(".premium-disp").show();
                $(".sumassured-disp").show();
            }
        }

        // Populate the rest of the form fields
        if (risks["autogenCert"]) {
            if (risks["autogenCert"] === "Y") {
                $("#chk-autogen").prop("checked", true);
            } else {
                $("#chk-autogen").prop("checked", false);
            }
        } else {
            $("#chk-autogen").prop("checked", false);
        }

        $("#lifeassured-name").val(risks["fname"]);
        $("#lifeassured-code").val(risks["tenId"]);
        $("#lifeassured-other-name").val(risks["otherNames"]);
        $("#life-ntl-id").val(risks["idNo"]);
        populateInsuredLov();
        $("#risk-sub-code").val(risks["subId"]);
        $("#sub-name").val(risks["subDesc"]);
        populateSubclassLov();

        $("#risk-cov-code").val(risks["covId"]);
        $("#cover-name").val(risks["covName"]);
        populateCoverTypesLov();
        $("#binder-det-id").val(risks["binderDetId"]);
        $("#risk-code-pk").val(risks["riskId"]);
        $("#risk-trans-type").val(risks["transType"]);
        $("#risk-ident-code").val(risks["riskIdentifier"]);
        $("#risk-form").show();
        $("#risk-div").hide();
        $("#btn-save-risk").show();
        $("#btn-save-cancel").show();
        $("#myTab #show-taxes,#show-clauses").hide();
    };


    function getNewPremItems(sectdesc) {
        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
            $.ajax({
                type: 'GET',
                url: 'getNewClientPremiumItems',
                dataType: 'json',
                data: {
                    "detId": $("#risk-det-id-pk").val(),
                    "riskId": $("#risk-code-pk").val(),
                    "secName": sectdesc,
                    "age": $("#lifeassured-age").val()
                },
                async: true,
                success: function (result) {
                    // $('#myPleaseWait').modal('hide');
                    $("#new_prem_items_form tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td></tr>";
                        if ($("#pol-bin-type").val() === "B") {
                            markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control">' +
                                "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
                        } else {
                            if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" value="' + $("#lifeassured-age").val() + '" readonly>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
                            } else if (result[res].section && result[res].section.type === "RD") {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
                                    "</td><td><input type='text' class='rate form-control'  value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'   value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control'   value='" + result[res].freeLimit + "'></td></tr>";
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
        } else {
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
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td></tr>";
                        if ($("#pol-bin-type").val() === "B") {
                            markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                                "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
                        } else {
                            if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
                            } else if (result[res].section && result[res].section.type === "RD") {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
                                    "</td><td><input type='text' class='rate form-control'  value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'   value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control'   value='" + result[res].freeLimit + "'></td></tr>";
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

    }


    var getNewPremItemsModal = function () {

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
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax(
                {
                    url: url,
                    type: "POST",
                    data: JSON.stringify(data),
                    success: function (s) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Selected Sections Added Successfully',
                            icon: 'success'
                        });
                        $('#section_tbl').DataTable().ajax.reload();
                        populatePolicyDetails();
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


    var getNewCreatePremItems = function () {
        var arr = [];
        $("#new_prem_items_form tr").each(function (row, tr) {
            var checked = $(this).find('.section-check').eq(0).is(":checked");
            var section = $(this).find('.section').eq(0).val();
            var premId = $(this).find('.premId').eq(0).val();
            var ratesApplicable = $(this).find('.ratesApplicable').eq(0).val();
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
                    ratesApplicable: ratesApplicable,
                    amount: amount,
                    multiplierRate: multiplierRate
                });
            }

        });

        return arr;
    }

    var getNewClausesModal = function () {
        $("#btn-add-new-clause").click(function () {
            if ($("#policy-id").val() != '') {
                $("#clause-pol-id").val($("#policy-id").val());
                getNewClauses();
                $('#newclausesModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            } else {
                bootbox.alert("Policy Details are not available to add A clause")
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
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax(
                {
                    url: url,
                    type: "POST",
                    data: JSON.stringify(data),
                    success: function (s) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Selected Clauses Added Successfully',
                            icon: 'success'
                        });
                        $('#polclausesList').DataTable().ajax.reload();
                        populatePolicyDetails();
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

    var getNewTaxesModal = function () {
        $("#btn-add-new-tax").click(function () {
            if ($("#policy-id").val() != '') {
                $("#clause-pol-id").val($("#policy-id").val());
                getNewTaxes();
                $('#newtaxesModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            } else {
                bootbox.alert("Policy Details are not available to add A Tax")
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
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax(
                {
                    url: url,
                    type: "POST",
                    data: JSON.stringify(data),
                    success: function (s) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Selected Clauses Added Successfully',
                            icon: 'success'
                        });
                        $('#polTaxesList').DataTable().ajax.reload();
                        populatePolicyDetails();
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

    var getRateType = function (type) {
        if (type) {
            if (type === "A") return "Amount";
            else if (type === "P") return "Percentage";
            else if (type === "M") return "Per Milli";
        } else {
            return "Amount";
        }
    }

    var getCreateNewTaxes = function () {
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

    var getNewTaxes = function () {

        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        $.ajax({
            type: 'GET',
            url: 'getNewTaxes',
            dataType: 'json',
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

    var getNewClauses = function () {
        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        $.ajax({
            type: 'GET',
            url: 'getNewClauses',
            dataType: 'json',
            async: true,
            success: function (result) {
                // $('#myPleaseWait').modal('hide');
                $("#new_clause_tbl tbody").each(function () {
                    $(this).remove();
                });
                for (var res in result) {
                    var markup = "<tr><td><input type='checkbox' class='clause-check'><input type='hidden' class='clause-id form-control' value='" + result[res].clause.clauId +
                        "'></td><td>"
                        + result[res].clauHeading + "</td></tr>";
                    $("#new_clause_tbl").append(markup);
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
            }
        });

    }


    var getCreateNewClauses = function () {
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

    var endorseriskModal = function () {
        $("#btn-endors-risk").on('click', function () {
            populateEndorsRisks(polCode);
            createActiveRisksTbl(polCode);
            $('#endorseRiskModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        });

        $("#btn-search-endos-risks").on('click', function () {
            createActiveRisksTbl(polCode);
        })
    }


    var createCertypeLov = function (riskCode) {
        if ($("#risk-cert-div").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "risk-cert-div",
                sort: 'brnCertId',
                change: function (e, a, v) {
                    $("#risk-cert-id").val(e.added.brnCertId);
                },
                formatResult: function (a) {
                    return a.certLots.certTypes.certDesc;
                },
                formatSelection: function (a) {
                    return a.certLots.certTypes.certDesc;
                },
                initSelection: function (element, callback) {

                },
                id: "brnCertId",
                width: "250px",
                params: {riskId: riskCode},
                placeholder: "Select Cert Type"

            });
        }
    }

    var updateRiskCertTypes = function () {
        var $classForm = $('#edit-cert-form');
        var validator = $classForm.validate();
        $('#updateRiskCert').click(function () {
            if (!$classForm.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "updateRiskCertificate";
            var request = $.post(url, data);
            request.success(function () {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#cert_tbl').DataTable().ajax.reload();
                validator.resetForm();
                $('#editRiskCertModal').modal('hide');
            });

            request.error(function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }

    var saveRiskCertTypes = function () {
        var $classForm = $('#new-cert-form');
        var validator = $classForm.validate();
        $('#saveRiskCert').click(function () {
            if (!$classForm.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "saveRiskCertificate";
            var request = $.post(url, data);
            request.success(function () {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#cert_tbl').DataTable().ajax.reload();
                validator.resetForm();
                $('#riskCertModal').modal('hide');
            });

            request.error(function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }

    var newCertTypeModal = function () {
        $("#risk-cert-status").on('change', function () {
            if ($(this).val() === 'C') {
                $(".cert-cancellation").show();
            } else {
                $(".cert-cancellation").hide();
            }
        });


        $("#btn-add-new-cert").on("click", function () {
            if ($("#risk-code-pk").val() !== '') {
                $("#cert-risk-id").val($("#risk-code-pk").val());
                createCertypeLov($("#risk-code-pk").val());
                $('#riskCertModal').modal('show');

            } else {
                bootbox.alert("Select Risk to add Certificate");
                return;
            }
        });

    }


    var populateEndorsRisks = function (policyCode) {
        if ($("#endos-insured-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "endos-insured-frm",
                sort: 'arId',
                change: function (e, a, v) {
                    $("#endorse-insured-id").val(e.added.risk.insured.tenId);
                },
                formatResult: function (a) {
                    return a.risk.insured.fname + " " + a.risk.insured.otherNames;
                },
                formatSelection: function (a) {
                    return a.risk.insured.fname + " " + a.risk.insured.otherNames;
                },
                initSelection: function (element, callback) {

                },
                id: "arId",
                width: "250px",
                params: {polCode: policyCode},
                placeholder: "Select Insured"

            });
        }
    }


    var createActiveRisksTbl = function (policyCode) {
        var url = "polactiverisks";
        var currTable = $('#endorserisktbl').DataTable({
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
                'data': {
                    'riskId': $("#endorse-risk-search").val(),
                    'insuredId': $("#endorse-insured-id").val(),
                    'policyCode': policyCode
                },
            },
            autoWidth: true,
            lengthMenu: [[10], [10]],
            pageLength: 10,
            destroy: true,
            searching: false,
            "columns": [
                {
                    "data": "arId",
                    "render": function (data, type, full, meta) {
                        return full.risk.riskShtDesc;
                    }
                },
                {
                    "data": "arId",
                    "render": function (data, type, full, meta) {
                        return full.risk.riskDesc;
                    }
                },
                {
                    "data": "arId",
                    "render": function (data, type, full, meta) {
                        return moment(full.risk.wefDate).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "arId",
                    "render": function (data, type, full, meta) {
                        return moment(full.risk.wetDate).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "arId",
                    "render": function (data, type, full, meta) {
                        return '<select class="form-control" id="bin-type" name="binType" required> ' +
                            ' <option value="R">Revise</option> ' +
                            ' <option value="C">Cancel</option>' +
                            ' <option value="E">Extend</option>' +
                            ' </select>';
                    }
                },
                {
                    "data": "arId",
                    "render": function (data, type, full, meta) {
                        return '<button  class="editor_edit btn btn-success">Endorse</button>';
                    }
                },

            ]
        });


        $('#endorserisktbl').on('click', '.editor_edit', function (e) {
            var combo = $(this).closest('tr').find("select");
            var data = currTable.row($(this).closest('tr')).data();
            if (combo.val() === "R") {
                if (data === undefined || data === null) {

                } else {
                    // $('#myPleaseWait').modal({

                    $.ajax({
                        type: 'GET',
                        url: 'endorseRisk',
                        data: {"activeRiskCode": data.arId},
                        dataType: 'json',
                        async: true,
                        success: function (result) {
                            // $('#myPleaseWait').modal('hide');
                            $('#endorserisktbl').DataTable().ajax.reload();
                            $('#risk_tbl').DataTable().ajax.reload();
                            populatePolicyDetails();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            // $('#myPleaseWait').modal('hide');

                            bootbox.alert(jqXHR.responseText);
                        }
                    });
                }
            }
        });


        return currTable;
    }

    var newPolicyRemarksModal = function () {
        $("#btn-add-new-remark").on('click', function () {
            createPolicyRemarksTbl(polCode);
            $('#endorseRemarksModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        });

    }

    var createPolicyRemarksTbl = function (policyCode) {
        var url = SERVLET_CONTEXT + "/protected/uw/policies/getNewPolicyRemarks";
        var currTable = $('#remarks_tbl').DataTable({
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
                'data': {
                    'policyCode': policyCode
                },
            },
            autoWidth: true,
            lengthMenu: [[10], [10]],
            pageLength: 10,
            destroy: true,
            searching: true,
            "columns": [
                {
                    "data": "remarkShtDesc",
                    "render": function (data, type, full, meta) {
                        return full.remarkShtDesc;
                    }
                },
                {
                    "data": "remarks",
                    "render": function (data, type, full, meta) {
                        return full.remarks;
                    }
                },


            ]
        });

        $('#remarks_tbl tbody').on('click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');

            var d = currTable.row(this).data();
            if (d) {
                $("#poli-remarks").val(d.remarks);
                $("#remark-pk").val(d.remarkId);
                $('#endorseRemarksModal').modal('hide');
                $("#remark-pol-id").val(policyCode);

                var $paymodesForm = $('#frm-pol-remarks');
                var data = {};
                $paymodesForm.serializeArray().map(function (x) {
                    data[x.name] = x.value;
                });
                data.remarks = $("#poli-remarks").val();

                $.ajax({
                    url: "createPolicyRemarks",
                    type: 'POST',
                    data: data,
                    success: function() {
                        // Disable all remark controls permanently
                        $("#btn-add-new-remark").prop("disabled", true);
                        $("#btn-save-remark").prop("disabled", true);
                        $("#poli-remarks").prop("disabled", true);

                        // Add visual indication
                        $("#poli-remarks").addClass('disabled-remark');
                        $("#btn-add-new-remark").addClass('btn-disabled');
                        $("#btn-save-remark").addClass('btn-disabled');

                        Swal.fire({
                            title: 'Success',
                            text: 'Remark saved successfully',
                            icon: 'success',
                            timer: 2000
                        });
                    },
                    error: function(jqXHR) {
                        console.log('Error auto-saving remark');
                    }
                });
            }
        });

        return currTable;
    }


    var saveEndorsementRemakrs = function () {
        var $paymodesForm = $('#frm-pol-remarks');
        var validator = $paymodesForm.validate();

        $('#btn-save-remark').click(function () {
            if (!$paymodesForm.valid()) {
                return;
            }
            var $btn = $(this).button('Saving');
            var data = {};
            $paymodesForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "createPolicyRemarks";
            data.remarks = $("#poli-remarks").val();
            var request = $.post(url, data);

            request.success(function () {
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                getPolicyRemakrs();

                 $("#btn-add-new-remark").prop("disabled", true);
                $("#btn-save-remark").prop("disabled", true);
                $("#poli-remarks").prop("disabled", true);

                $("#poli-remarks").addClass('disabled-remark');
                $("#btn-add-new-remark").addClass('btn-disabled');
                $("#btn-save-remark").addClass('btn-disabled');
            });
            request.error(function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }

    var getRiskSchedules = function () {
        $.ajax({
            url: 'getRiskSchedules/' + selRiskCode,
            type: 'GET',
            processData: false,
            contentType: false,
            success: function (s) {
                getRiskScheduleDetails(s.mappings);
            },
            error: function (xhr, error) {
                bootbox.alert(xhr.responseText);
            }
        });
    }

    var addNewRiskSchedule = function () {
        $("#btn-add-new-sched").on('click', function () {
            $("#schedule-risk-id").val($("#risk-code-pk").val());
            $("#schedule-pk-id").val("");
            $.ajax({
                url: 'getRiskSchedules/' + $("#risk-code-pk").val(),
                type: 'GET',
                processData: false,
                contentType: false,
                success: function (s) {
                    $("form#new-risk-schedule-form > div ").each(function () {
                        $(this).remove();
                    });
                    for (var i = 0; i < s.mappings.length; i++) {
                        if (s.mappings[i].colType === "T") {
                            var data = '<div class="form-group"> ' +
                                ' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
                                '	<div class="col-md-8"> ' +
                                '	<input type="text" class="editUserCntrls form-control" name="column' + s.mappings[i].key + '" ' +
                                ' required> ' +
                                ' </div> ' +
                                ' </div>	';
                            $("form#new-risk-schedule-form").append(data);
                        } else if (s.mappings[i].colType === "N") {
                            var data = '<div class="form-group"> ' +
                                ' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
                                '	<div class="col-md-8"> ' +
                                '	<input type="number" class="editUserCntrls form-control" name="column' + s.mappings[i].key + '" ' +
                                ' required> ' +
                                ' </div> ' +
                                ' </div>	';
                            $("form#new-risk-schedule-form").append(data);
                        } else if (s.mappings[i].colType === "D") {
                            var data = '<div class="form-group"> ' +
                                ' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
                                '	<div class="col-md-8"> ' +
                                '<div class="input-group date datepicker-input"> ' +
                                ' <input type="text" name="column' + s.mappings[i].key + '" class="form-control float-right"' +
                                ' required /> ' +
                                '  <div class="input-group-addon"> ' +
                                '      <span class="fa fa-calendar"></span> ' +
                                '     </div> ' +
                                '    </div>' +
                                ' </div> ' +
                                ' </div>	';
                            $("form#new-risk-schedule-form").append(data);
                        } else if (s.mappings[i].colType === "O") {
                            var arr = s.mappings[i].options.split(",");
                            var opts = "";
                            for (var x = 0; x < arr.length; x++) {
                                opts += ' <option value="' + arr[x] + '">' + arr[x] + '</option> ';
                            }
                            var data = '<div class="form-group"> ' +
                                ' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
                                '	<div class="col-md-8"> ' +
                                ' <select class="form-control" name="column' + s.mappings[i].key + '" ' +
                                ' required> ' +
                                ' <option value="">Select Option Value</option> ' + opts +
                                ' </select> ' +
                                ' </div> ' +
                                ' </div>	';
                            $("form#new-risk-schedule-form").append(data);
                        }

                    }
                    $(".datepicker-input").each(function () {
                        $(this).datetimepicker({
                            format: 'DD/MM/YYYY'
                        });

                    });
                },
                error: function (xhr, error) {
                    bootbox.alert(xhr.responseText);
                }
            });
            $('#riskscheduleModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        })
    }


    var getRiskScheduleDetails = function (cols) {
        var arr = [];
        for (var i = 0; i < cols.length; i++) {
            arr.push({
                title: cols[i].column,
                data: "column" + (cols[i].key)
            });
        }
        var url = "riskSchedules/" + selRiskCode;
        var currTable = $('#risk-sched_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": arr
        }));

        $('#risk-sched_tbl tbody').on('click', 'tr', function () {
            currTable.$('tr.active').removeClass('active');
            $(this).addClass('active');
        });
        $("#btn-add-del-sched").on('click', function () {
            var d = currTable.row('.active').data();
            if (d) {
                bootbox.confirm("Are you sure want to delete selected Schedule Record?", function (result) {
                    if (result) {
                        $.ajax({
                            type: 'GET',
                            url: 'deleteRiskSchedule/' + d.scheduleId,
                            dataType: 'json',
                            async: true,
                            success: function (result) {
                                Swal.fire({
                                    title: 'Success',
                                    text: 'Record Deleted Successfully',
                                    icon: 'success'
                                });
                                $('#risk-sched_tbl').DataTable().ajax.reload();

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
        });

        $("#btn-add-edit-sched").on('click', function () {
            var d = currTable.row('.active').data();
            if (d) {
                $("#schedule-risk-id").val($("#risk-code-pk").val());
                $("#schedule-pk-id").val(d.scheduleId);
                $.ajax({
                    url: 'getRiskSchedules/' + $("#risk-code-pk").val(),
                    type: 'GET',
                    processData: false,
                    contentType: false,
                    success: function (s) {
                        $("form#new-risk-schedule-form > div ").each(function () {
                            $(this).remove();
                        });
                        for (var i = 0; i < s.mappings.length; i++) {
                            var value;
                            if (s.mappings[i].key === '1') {
                                value = d.column1;
                            } else if (s.mappings[i].key === '2') {
                                value = d.column2;
                            } else if (s.mappings[i].key === '3') {
                                value = d.column3;
                            } else if (s.mappings[i].key === '4') {
                                value = d.column4;
                            } else if (s.mappings[i].key === '5') {
                                value = d.column5;
                            } else if (s.mappings[i].key === '6') {
                                value = d.column6;
                            } else if (s.mappings[i].key === '7') {
                                value = d.column7;
                            } else if (s.mappings[i].key === '8') {
                                value = d.column8;
                            } else if (s.mappings[i].key === '9') {
                                value = d.column9;
                            } else if (s.mappings[i].key === '10') {
                                value = d.column10;
                            } else if (s.mappings[i].key === '11') {
                                value = d.column11;
                            } else if (s.mappings[i].key === '12') {
                                value = d.column12;
                            } else if (s.mappings[i].key === '13') {
                                value = d.column13;
                            } else if (s.mappings[i].key === '14') {
                                value = d.column14;
                            } else if (s.mappings[i].key === '15') {
                                value = d.column15;
                            } else if (s.mappings[i].key === '16') {
                                value = d.column16;
                            } else if (s.mappings[i].key === '17') {
                                value = d.column17;
                            } else if (s.mappings[i].key === '18') {
                                value = d.column18;
                            } else if (s.mappings[i].key === '19') {
                                value = d.column19;
                            } else if (s.mappings[i].key === '20') {
                                value = d.column20;
                            } else if (s.mappings[i].key === '21') {
                                value = d.column21;
                            } else if (s.mappings[i].key === '22') {
                                value = d.column22;
                            } else if (s.mappings[i].key === '23') {
                                value = d.column23;
                            } else if (s.mappings[i].key === '24') {
                                value = d.column24;
                            } else if (s.mappings[i].key === '25') {
                                value = d.column25;
                            } else if (s.mappings[i].key === '26') {
                                value = d.column26;
                            } else if (s.mappings[i].key === '27') {
                                value = d.column27;
                            } else if (s.mappings[i].key === '28') {
                                value = d.column28;
                            } else if (s.mappings[i].key === '29') {
                                value = d.column29;
                            } else if (s.mappings[i].key === '30') {
                                value = d.column30;
                            }
                            if (s.mappings[i].colType === "T") {
                                var data = '<div class="form-group"> ' +
                                    ' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
                                    '	<div class="col-md-8"> ' +
                                    '	<input type="text" class="editUserCntrls form-control" value="' + value + '" name="column' + s.mappings[i].key + '" ' +
                                    ' required> ' +
                                    ' </div> ' +
                                    ' </div>	';
                                $("form#new-risk-schedule-form").append(data);
                            } else if (s.mappings[i].colType === "N") {
                                var data = '<div class="form-group"> ' +
                                    ' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
                                    '	<div class="col-md-8"> ' +
                                    '	<input type="number" class="editUserCntrls form-control" value="' + value + '" name="column' + s.mappings[i].key + '" ' +
                                    ' required> ' +
                                    ' </div> ' +
                                    ' </div>	';
                                $("form#new-risk-schedule-form").append(data);
                            } else if (s.mappings[i].colType === "D") {
                                var data = '<div class="form-group"> ' +
                                    ' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
                                    '	<div class="col-md-8"> ' +
                                    '<div class="input-group date datepicker-input"> ' +
                                    ' <input type="text" value="' + value + '" name="column' + s.mappings[i].key + '" class="form-control float-right"' +
                                    ' required /> ' +
                                    '  <div class="input-group-addon"> ' +
                                    '      <span class="fa fa-calendar"></span> ' +
                                    '     </div> ' +
                                    '    </div>' +
                                    ' </div> ' +
                                    ' </div>	';
                                $("form#new-risk-schedule-form").append(data);
                            } else if (s.mappings[i].colType === "O") {
                                console.log(s.mappings[i].options);
                                var arr = s.mappings[i].options.split(",");
                                var opts = "";
                                for (var x = 0; x < arr.length; x++) {
                                    if (arr[x] === value)
                                        opts += ' <option value="' + arr[x] + '" selected>' + arr[x] + '</option> ';
                                    else opts += ' <option value="' + arr[x] + '">' + arr[x] + '</option> ';
                                }
                                var data = '<div class="form-group"> ' +
                                    ' <label for="cou-name" class="col-md-3 control-label">' + s.mappings[i].column + '</label> ' +
                                    '	<div class="col-md-8"> ' +
                                    ' <select class="form-control" id="opts-column' + s.mappings[i].key + '"  name="column' + s.mappings[i].key + '" ' +
                                    ' required> ' +
                                    ' <option value="">Select Option Value</option> ' + opts +
                                    ' </select> ' +
                                    ' </div> ' +
                                    ' </div>	';
                                $("form#new-risk-schedule-form").append(data);
                            }

                        }
                        $(".datepicker-input").each(function () {
                            $(this).datetimepicker({
                                format: 'DD/MM/YYYY'
                            });

                        });
                    },
                    error: function (xhr, error) {
                        bootbox.alert(xhr.responseText);
                    }
                });
                $('#riskscheduleModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            }
        })


        return currTable;
    }


    var saveRiskSchedules = function () {
        var $classForm = $('#new-risk-schedule-form');
        var validator = $classForm.validate();
        $('#saveRiskSchedules').click(function () {
            if (!$classForm.valid()) {
                return;
            }
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})

            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "saveRiskSchedule";
            var request = $.post(url, data);
            request.success(function () {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#risk-sched_tbl').DataTable().ajax.reload();
                $('#section_tbl').DataTable().ajax.reload();
                populatePolicyDetails();
                validator.resetForm();
                $('#riskscheduleModal').modal('hide');
            });

            request.error(function (jqXHR, textStatus, errorThrown) {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }


    var getCommissionRate = function (bindDetCode) {
        $.ajax({
            type: 'GET',
            url: 'getCommissionRate',
            dataType: 'json',
            data: {"detId": bindDetCode},
            async: true,
            success: function (result) {
                $("#comm-rate").val(result);
            },
            error: function (jqXHR, textStatus, errorThrown) {

            }
        });
    }

    var getCreateNewIntParties = function () {
        var arr = [];
        $("#interested_party_tbl tr").each(function (row, tr) {
            var checked = $(this).find('.int-check').eq(0).is(":checked");
            var partId = $(this).find('.int-part-id').eq(0).val();
            if (checked) {
                arr.push(partId);
            }

        });

        return arr;
    }

    var populateRelationTypes = function () {
        if ($("#relationship-type").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "relationship-type",
                sort: 'typeId',
                change: function (e, a, v) {
                    $("#relation-type-id").val(e.added.typeId);
                },
                formatResult: function (a) {
                    return a.relationDesc;
                },
                formatSelection: function (a) {
                    return a.relationDesc;
                },
                initSelection: function (element, callback) {
                    //var code  = $('#risk-cov-code').val();
                    //var name = $("#cover-name").val();
                    //var data = {covName:name,covId:code};
                    //callback(data);
                },
                id: "relationDesc",
                width: "250px",
                placeholder: "Select Relation Type"

            });
        }

    }
    // Beneficiary Form Validation
    var getNewIntParties = function () {
        $("#btn-add-new-ips").click(function () {
            if ($("#policy-id").val() != '') {
                $("#beneficiary-form")[0].reset();
                $("#beneficiaryModalLabel").text('Add Beneficiary');
                $("#part-code").val("");
                $("#part-pol-code").val($("#policy-id").val());
                populateRelationTypes();
                $('#beneficiaryModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            } else {
                Swal.fire({
                    title: 'Policy Required',
                    text: 'Please select a policy before adding beneficiaries.',
                    icon: 'warning',
                    confirmButtonText: 'OK'
                });
            }
        });

        function validateBeneficiaryForm() {
            const form = document.getElementById('beneficiary-form');
            const formData = new FormData(form);
            const errors = [];
            const beneficiaryName = $("#part-name").val().trim();
            const beneficiaryEmail = $("#part-email").val().trim();
            const beneficiaryRegNo = $("#part-id-no").val().trim();
            const benAllocation = $("#ben-allocation").val().trim();
            const beneficiaryTelNo = $("#part-tel").val().trim();
            const dateRegistered = $("#date-reg").val().trim();
            const beneficiaryRelationship = $("#relationship-type-str").val().trim();

            // 1. Beneficiary Name Validation
            if (!beneficiaryName) {
                errors.push("Beneficiary name is required.");
            } else if (beneficiaryName.length < 2) {
                errors.push("Beneficiary name must be at least 2 characters long.");
            } else if (beneficiaryName.split(" ").filter(name => name.length > 0).length < 2) {
                errors.push("Please enter at least two names (First name and Last name).");
            } else if (!/^[a-zA-Z\s]+$/.test(beneficiaryName)) {
                errors.push("Beneficiary name must contain only letters and spaces.");
            }

            // 2. Email Validation
            if (beneficiaryEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(beneficiaryEmail)) {
                errors.push("Please enter a valid email address.");
            }

            // 3. Identification Number Validation
            if (!beneficiaryRegNo) {
                errors.push("Identification number is required.");
            } else if (beneficiaryRegNo.length < 6) {
                errors.push("Identification number must be at least 6 characters long.");
            } else if (!/^[A-Za-z0-9]+$/.test(beneficiaryRegNo)) {
                errors.push("Identification number must contain only letters and numbers.");
            }

            // 4. Allocation Percentage Validation
            if (!benAllocation) {
                errors.push("Allocation percentage is required.");
            } else {
                const allocation = parseFloat(benAllocation);
                if (isNaN(allocation)) {
                    errors.push("Allocation must be a valid number.");
                } else if (allocation <= 0) {
                    errors.push("Allocation percentage must be greater than 0.");
                } else if (allocation > 100) {
                    errors.push("Allocation percentage cannot exceed 100%.");
                }
            }

            // 5. Telephone Number Validation
            if (beneficiaryTelNo) {
                if (!/^\d+$/.test(beneficiaryTelNo)) {
                    errors.push("Telephone number must contain only digits.");
                } else if (beneficiaryTelNo.length < 10) {
                    errors.push("Telephone number must be at least 10 digits long.");
                }
            }

            // 6. Date of Birth Validation
            if (!dateRegistered) {
                errors.push("Date of birth is required.");
            } else {
                // Parse date
                const dateParts = dateRegistered.split('/');
                if (dateParts.length !== 3) {
                    errors.push("Date of birth must be in DD/MM/YYYY format.");
                } else {
                    const day = parseInt(dateParts[0], 10);
                    const month = parseInt(dateParts[1], 10);
                    const year = parseInt(dateParts[2], 10);

                    // Create date object
                    const inputDate = new Date(year, month - 1, day);
                    const today = new Date();

                    // Check if date is valid
                    if (inputDate.getDate() !== day ||
                        inputDate.getMonth() !== month - 1 ||
                        inputDate.getFullYear() !== year) {
                        errors.push("Please enter a valid date of birth.");
                    } else if (inputDate > today) {
                        errors.push("Date of birth cannot be in the future.");
                    } else {
                        // Check reasonable age
                        const age = today.getFullYear() - inputDate.getFullYear();
                        if (age > 150) {
                            errors.push("Date of birth indicates an unrealistic age (over 150 years).");
                        } else if (age < 0) {
                            errors.push("Date of birth cannot be in the future.");
                        }
                    }
                }
            }

            // 7. Relationship Validation
            if (!beneficiaryRelationship) {
                errors.push("Relationship to insured is required.");
            } else if (beneficiaryRelationship.length < 2) {
                errors.push("Relationship must be at least 2 characters long.");
            } else if (!/^[a-zA-Z\s]+$/.test(beneficiaryRelationship)) {
                errors.push("Relationship must contain only letters and spaces.");
            }

            return errors;
        }

               $('#saveBeneficiaryBtn').click(function () {
            const $btn = $(this);

            if ($btn.prop('disabled')) return;

            // Show loading state
            $btn.prop('disabled', true);
            const originalText = $btn.text();
            $btn.text('Validating...');

            // Perform validation
            const validationErrors = validateBeneficiaryForm();

            if (validationErrors.length > 0) {
                // Reset button state
                $btn.prop('disabled', false);
                $btn.text(originalText);

                // Display validation errors with
                Swal.fire({
                    title: 'Validation Errors',
                    html: `
                    <div style="text-align: left;">
                        <p style="margin-bottom: 15px; color: #666;">Please correct the following errors:</p>
                        <ul style="margin: 0; padding-left: 20px;">
                            ${validationErrors.map(error => `<li style="margin-bottom: 8px; color: #e74c3c;">${error}</li>`).join('')}
                        </ul>
                    </div>
                `,
                    icon: 'error',
                    confirmButtonText: 'OK',
                    confirmButtonColor: '#3085d6',
                    width: '500px',
                    customClass: {
                        popup: 'swal-validation-popup',
                        title: 'swal-validation-title',
                        content: 'swal-validation-content'
                    }
                });
                return;
            }


            $btn.text('Saving...');

            const $form = $('#beneficiary-form');
            const data = {};
            $form.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });

            const url = "createBeneficiary";
            const request = $.post(url, data);

            request.success(function () {
                // Reset button state
                $btn.prop('disabled', false);
                $btn.text(originalText);

                Swal.fire({
                    title: 'Success!',
                    text: 'Beneficiary has been saved successfully.',
                    icon: 'success',
                    confirmButtonText: 'OK',
                    confirmButtonColor: '#28a745'
                }).then(() => {
                    // Refresh the beneficiaries table and close modal
                    $('#benefeciary_tbl').DataTable().ajax.reload();
                    $('#beneficiaryModal').modal('hide');

                    // Reset form
                    $form[0].reset();
                });
            });

            request.error(function (jqXHR, textStatus, errorThrown) {
                // Reset button state
                $btn.prop('disabled', false);
                $btn.text(originalText);

                let errorMessage = 'An error occurred while saving the beneficiary.';


                if (jqXHR.responseText) {
                    try {
                        const errorResponse = JSON.parse(jqXHR.responseText);
                        errorMessage = errorResponse.message || errorResponse.error || jqXHR.responseText;
                    } catch (e) {
                        errorMessage = jqXHR.responseText;
                    }
                }

                Swal.fire({
                    title: 'Save Failed',
                    text: errorMessage,
                    icon: 'error',
                    confirmButtonText: 'OK',
                    confirmButtonColor: '#dc3545'
                });
            });
        });


        if (!document.getElementById('beneficiary-validation-styles')) {
            const style = document.createElement('style');
            style.id = 'beneficiary-validation-styles';
            style.innerHTML = `
            .swal-validation-popup {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif !important;
            }
            .swal-validation-title {
                font-weight: 600 !important;
                color: #2c3e50 !important;
            }
            .swal-validation-content {
                font-size: 14px !important;
            }
            .swal2-html-container ul li {
                line-height: 1.5 !important;
            }
        `;
            document.head.appendChild(style);
        }
    };

// Additional utility function for real-time validation feedback (optional)
    function addRealTimeValidation() {
        // Add real-time validation feedback for key fields
        $("#part-name").on('blur', function() {
            const name = $(this).val().trim();
            const $group = $(this).closest('.form-group');

            if (name && name.split(" ").filter(n => n.length > 0).length < 2) {
                $group.addClass('has-error');
                if (!$group.find('.validation-hint').length) {
                    $group.append('<span class="validation-hint text-danger" style="font-size: 12px;">Please enter at least two names</span>');
                }
            } else {
                $group.removeClass('has-error');
                $group.find('.validation-hint').remove();
            }
        });

        $("#ben-allocation").on('input', function() {
            const value = parseFloat($(this).val());
            const $group = $(this).closest('.form-group');

            if (value > 100) {
                $group.addClass('has-error');
                if (!$group.find('.validation-hint').length) {
                    $group.append('<span class="validation-hint text-danger" style="font-size: 12px;">Cannot exceed 100%</span>');
                }
            } else {
                $group.removeClass('has-error');
                $group.find('.validation-hint').remove();
            }
        });
    }

// Initialize real-time validation when modal opens
    $(document).on('shown.bs.modal', '#beneficiaryModal', function () {
        // addRealTimeValidation();
    });
    var populateImportCoverTypesLov = function () {
        if ($("#import-covertypes-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "import-covertypes-frm",
                sort: 'detId',
                change: function (e, a, v) {
                    $("#excel-binder-det-id").val(e.added.detId);
                },
                formatResult: function (a) {
                    return a.covName + " - " + a.subclassName;
                },
                formatSelection: function (a) {
                    return a.covName + " - " + a.subclassName;
                },
                initSelection: function (element, callback) {
                    //var code  = $('#risk-cov-code').val();
                    //var name = $("#cover-name").val();
                    //var data = {covName:name,covId:code};
                    //callback(data);
                },
                id: "covName",
                width: "250px",
                params: {bindCode: $("#binder-id").val()},
                placeholder: "Select Cover Type"

            });
        }

    }
    var showCoverOptions = function(binName) {
        // First hide all cover options by default
        $("#fpp_cover").hide();
        $("#pol_cover_option_fpp").hide();
        $("#pa_cover").hide();
        $("#pol_cover_option_pa").hide();
        $("#up_cover").hide();
        $("#pol_cover_option_up").hide();
        $("#en_cover").hide();



        if (binName.toUpperCase().includes("ALAK FAMILY PROTECTION PLAN")) {
            $("#fpp_cover").show();
            $("#pol_cover_option_fpp").show();


        }

        if (binName.toUpperCase().includes("ALAK PERSONAL ACCIDENT")) {
            $("#pa_cover").show();
            $("#pol_cover_option_pa").show();

        }
        if (binName.toUpperCase().includes("ALAK ULTIMATE PROTECTOR")) {
            $("#up_cover").show();
            $("#pol_cover_option_up").show();

        }

    };


    var populateImportSubCovers = function (binId) {
        if ($("#import-pol-covertypes-frm").filter("div").html() != undefined) {
            console.log($("#risk-bind-code").val());
            Select2Builder.initAjaxSelect2({
                containerId: "import-pol-covertypes-frm",
                sort: 'detId',
                change: function (e, a, v) {
                    $("#pol-excel-det-id").val(e.added.detId);
                },
                formatResult: function (a) {
                    return a.covName + " - " + a.subclassName;
                },
                formatSelection: function (a) {
                    return a.covName + " - " + a.subclassName;
                },
                initSelection: function (element, callback) {
                    //var code  = $('#risk-cov-code').val();
                    //var name = $("#cover-name").val();
                    //var data = {covName:name,covId:code};
                    //callback(data);
                },
                id: "covName",
                width: "250px",
                params: {bindCode: binId},
                placeholder: "Select Cover Type"

            });
        }
    }


    var importuWRisks = function () {
        $("#btn-import-risk").on('click', function () {
            populateImportCoverTypesLov();
            $('#importRisksModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        });

        var $form = $("#risks-upload-form");
        var validator = $form.validate();
        $('form#risks-upload-form')
            .submit(function (e) {
                e.preventDefault();
                if (!$form.valid()) {
                    return;
                }
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                var data = new FormData(this);
                data.append('file', $('#file-avatar')[0].files[0]);
                $.ajax({
                    url: 'importRisks',
                    type: 'POST',
                    data: data,
                    processData: false,
                    contentType: false,
                    success: function (s) {
                        $('#importRisksModal').modal('hide');
                        // $('#myPleaseWait').modal('hide');
                        $("#btn-import-logs").show();
                        Swal.fire({
                            title: 'Success',
                            text: 'Risks Uploaded Successfully',
                            icon: 'success'
                        });
                        populatePolicyDetails();
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
            });
    }


    var getClientDocs = function (clientId) {
        var url = SERVLET_CONTEXT + "/protected/clients/setups/clientDocs/" + clientId;
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
                    "data": "cdId",
                    "render": function (data, type, full, meta) {

                        return full.reqShtDesc;
                    }
                },
                {
                    "data": "cdId",
                    "render": function (data, type, full, meta) {

                        return full.reqDesc;
                    }
                },
                {"data": "uploadedFileName"},
                // { "data": "checkSum" },
                {"data": "uploadedBy"},
                {
                    "data": "uploadedDate",
                    "render" : function(data, type, full, meta) {
                        return full.uploadedDate ? moment(full.uploadedDate).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {"data": "verifiedBy"},
                {
                    "data": "verifiedDate",
                    "render" : function(data, type, full, meta) {
                        return full.verifiedDate ? moment(full.verifiedDate).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                },
                {
                    "data": "cdId",
                    "render": function (data, type, full, meta) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.downloadClientDoc(this);">View</button>';

                    }

                },
            ]
        });
        return currTable;
    }

    var downloadClientDoc = function (button) {
        var docs = JSON.parse(decodeURI($(button).data("docs")));
        window.open(SERVLET_CONTEXT + "/protected/clients/setups/clientDocument/" + docs['cdId'],
            '_blank' // <- This is what makes it open in a new window.
        );
    }

    var searchReqDocs = function (search) {
        if ($("#risk-code-pk").val() != '') {
            $.ajax({
                type: 'GET',
                url: 'getriskreqdocs',
                dataType: 'json',
                data: {"riskId": $("#risk-code-pk").val(), "docName": search},
                async: true,
                success: function (result) {
                    $("#risksReqDocsTbl tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res].sclReqrdId + "'></td><td>" + result[res].reqShtDesc + "</td><td>" + result[res].reqDesc + "</td></tr>";
                        $("#risksReqDocsTbl").append(markup);
                    }
                    $("#req-risk-code").val($("#risk-code-pk").val());
                    $('#riskReqDocsModal').modal({
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
        } else {
            bootbox.alert("Select Risk to attach Documents")
        }
    }

    var saveRiskDocsList = function () {
        var arr = [];
        $("#saveRiskDocsBtn").click(function () {
            $("#risksReqDocsTbl tbody").find('input[name="record"]').each(function () {
                if ($(this).is(":checked")) {
                    arr.push($(this).attr("id"));
                }
            });
            if (arr.length == 0) {
                bootbox.alert("No Documents Selected to attach..");
                return;
            }

            var $currForm = $('#risk-docs-form');
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }

            var data = {};
            $currForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "createRiskDocs";
            data.requiredDocs = arr;


            $.ajax({
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Records Created Successfully',
                        icon: 'success'
                    });
                    $('#risk_docs_tbl').DataTable().ajax.reload();
                    $('#riskReqDocsModal').modal('hide');
                    arr = [];
                },
                error: function (jqXHR, textStatus, errorThrown) {
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
        })
    }

    var getRiskImportLogs = function () {
        var url = "riskImportLogs";
        var currTable = $('#riskImportLogTbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {"data": "errorMessage"},
            ]
        }));
        return currTable;
    };

    var rejectTask = function () {
        $('#rejectionModal').modal('show');

        $('#rejectConfirmButton').off('click').on('click', function () {
            var reason = $('#rejectionReason').val();

            if (reason) {
                $.ajax({
                    url: SERVLET_CONTEXT + '/protected/users/rejectTask',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        taskId: mckPolId,
                        reason: reason
                    }),
                    success: function (response) {
                        window.location.href = SERVLET_CONTEXT + "/protected/home";

                    },
                    error: function (xhr, status, error) {
                        Swal.fire({
                            title: 'Error',
                            text: xhr.responseText,
                            icon: 'error'
                        });
                    }
                });
            } else {
                Swal.fire({
                    title: 'Error',
                    text: 'No reason provided for rejection',
                    icon: 'error'
                });
            }
            // Hide the modal after submission
            $('#rejectionModal').modal('hide');
        });
    }

    var approveTask = function () {
        $('#approvalModal').modal('show');

        $('#approveConfirmButton').off('click').on('click', function () {
            var $confirmButton = $(this);
            var originalConfirmText = $confirmButton.text();

            $confirmButton.addClass('btn-processing').prop("disabled", true).text('Processing...');

            $.ajax({
                url: SERVLET_CONTEXT + '/protected/users/approveTask',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(mckPolId),
                success: function (response) {
                    activeProcessingButton = null;
                    activeProcessingButtonText = null;
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                },
                error: function (xhr, status, error) {
                    if (activeProcessingButton) {
                        activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                    }
                    $confirmButton.removeClass('btn-processing').prop("disabled", false).text(originalConfirmText);

                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                }
            });

            // Hide the modal after submission
            $('#approvalModal').modal('hide');
        });
    }

    var init = function () {
        // Handle assign checker modal close events
        $('#assignCheckerModal').on('hidden.bs.modal', function () {
            // Re-enable the assign button if modal is closed without successful submission
            if (activeProcessingButton && activeProcessingButton.hasClass('btn-processing')) {
                activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                activeProcessingButton = null;
                activeProcessingButtonText = null;
            }

            // Also re-enable submit button if it was disabled
            var $submitButton = $('#assign-checker-submit');
            if ($submitButton.hasClass('btn-processing')) {
                $submitButton.removeClass('btn-processing').prop("disabled", false).text('Submit');
            }
        });

// Handle direct clicks on assign checker modal close buttons
        $('#assignCheckerModal .close, #assignCheckerModal [data-dismiss="modal"]').on('click', function() {
            // Re-enable buttons immediately when close buttons are clicked
            if (activeProcessingButton && activeProcessingButton.hasClass('btn-processing')) {
                activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                activeProcessingButton = null;
                activeProcessingButtonText = null;
            }

            // Also re-enable submit button
            var $submitButton = $('#assign-checker-submit');
            if ($submitButton.hasClass('btn-processing')) {
                $submitButton.removeClass('btn-processing').prop("disabled", false).text('Submit');
            }
        });
        $('#rejectionModal').on('hidden.bs.modal', function () {
            // Re-enable the button if modal is closed without successful submission
            if (activeProcessingButton && activeProcessingButton.hasClass('btn-processing')) {
                activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                activeProcessingButton = null;
                activeProcessingButtonText = null;
            }
        });

// Also handle direct clicks on close buttons for immediate feedback
        $('#rejectionModal .close, #rejectionModal [data-dismiss="modal"]').on('click', function() {
            // Re-enable the button immediately when close buttons are clicked
            if (activeProcessingButton && activeProcessingButton.hasClass('btn-processing')) {
                activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                activeProcessingButton = null;
                activeProcessingButtonText = null;
            }
        });
        // Handle approval modal close events
        $('#approvalModal').on('hidden.bs.modal', function () {
            // Re-enable the approve button if modal is closed without successful submission
            if (activeProcessingButton && activeProcessingButton.hasClass('btn-processing')) {
                activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                activeProcessingButton = null;
                activeProcessingButtonText = null;
            }
        });

// Handle direct clicks on approval modal close buttons
        $('#approvalModal .close, #approvalModal [data-dismiss="modal"]').on('click', function() {
            // Re-enable the button immediately when close buttons are clicked
            if (activeProcessingButton && activeProcessingButton.hasClass('btn-processing')) {
                activeProcessingButton.removeClass('btn-processing').prop("disabled", false).val(activeProcessingButtonText);
                activeProcessingButton = null;
                activeProcessingButtonText = null;
            }
        });
        var numberFormatter = new Intl.NumberFormat('en-US', {
            style: 'decimal',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });

        $("#btn-approve-quote").on('click', function () {
            var $button = $(this);
            var originalText = $button.val();

            // Store button reference globally for modal handling
            activeProcessingButton = $button;
            activeProcessingButtonText = originalText;
            $button.addClass('btn-processing').prop("disabled", true).val('Processing...');

            approveTask();
        });

        $("#btn-reject-quot").on('click', function () {
            var $button = $(this);
            var originalText = $button.val();

            // Store button reference globally for modal handling
            activeProcessingButton = $button;
            activeProcessingButtonText = originalText;

            // Disable and grey out immediately
            $button.addClass('btn-processing').prop("disabled", true).val('Processing...');

            rejectTask();
        });

        // Show modal on button click
        $("#btn-negotiate-premium").click(function () {
            $("#negotiatePremiumModal").modal('show');
        });

        // Save negotiated premium and update Basic Premium field
        $("#save-negotiated-premium").click(function () {
            // Get the negotiated premium from the modal input
            var negotiatedPremium = parseFloat($("#negotiated-premium").val());

            if (negotiatedPremium && !isNaN(negotiatedPremium) && negotiatedPremium > 0) {
                $("#negotiated-prem").val(negotiatedPremium);
                // Format the negotiated premium without currency symbol
                $("#negotiated").text(numberFormatter.format(negotiatedPremium));

                // Close the modal
                $("#negotiatePremiumModal").modal('hide');
                $("#negotiated-premium").val('');
            } else {
                // If invalid input, show an alert
                alert("Please enter a valid premium amount.");
            }
        });
        populatePolicyDetails();
        checkAndDisableRemarkControls();
        $('#overrid-prem').number(true, 2);
        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        $(document).ajaxStart(function () {
            $("#btn-dispatch-trans,#btn-auth-policy,#btn-add-policy,#btn-make-ready-policy,#btn-undo-make-ready,btn-negotiate-premium," +
                "#btn-convert-policy,#btn-unconvert-policy,#btn-save-risk,#upload-doc-btn,#saveBeneficiaryBtn").attr("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#btn-dispatch-trans,#btn-auth-policy,#btn-add-policy,#btn-make-ready-policy,#btn-undo-make-ready,#btn-save-remark,btn-negotiate-premium," +
                "#btn-convert-policy,#btn-unconvert-policy,#btn-save-risk,#upload-doc-btn,#saveBeneficiaryBtn").attr("disabled", false);
        });

        populateClientLov();
        $(document).ready(function () {
            $("#pol-bin-type").val("B").trigger('change');
            $("#btn-negotiate-premium").css('display','none')
        });
        $("#pol-bin-type").on('change', function () {
            populateBinderLov($("#pol-bin-type").val());
            if ($(this).val() === "B") {
                $("#comm-rate").attr("readonly", "true");
                $("#btn-negotiate-premium").css('display','none')
            } else {
                $("#comm-rate").attr("readonly", "false");
                $("#btn-negotiate-premium").css('display','block')

            }

        });

        $("#pol-buss-type").on('change', function () {
            if ($(this).val() === "S") {
                $("#prorated-full").val("S");
            } else {
                $("#prorated-full").val("P");
            }
        });

        $("#chk-import-risks").change(function () {
            if (this.checked) {
                $(".risk-detail-tab").hide();
            } else {
                $(".risk-detail-tab").show();
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

                }
            });
        });

        $('#email-template-type').on('change', function () {
            if ($(this).val()) {
                UTILITIES.sendEmail($(this).val());
            }

        });

        populateCurrencyLov();
        populateSubAgentsLov();
        populateIntroducerLov();
        populateMarketerLov();
        populateLeadsManLov();
        populatePaymentModes();
        populateUserBranches();
        populateInsuredLov();
        populateSubclassLov();
        populateCoverTypesLov();
        //$("#other-pol-details").hide();
        //getPolicyWet();
        changePolicyWetDt();
        createNewPolicy();
        saveRiskSections();
        newRisk();
        getNewPremItemsModal();
        getNewClausesModal();
        createPolClauses();
        createPolicyChecks();
        updatePolicyTaxes();
        endorseriskModal();
        newPolicyRemarksModal();
        saveEndorsementRemakrs();
        newCertTypeModal();
        saveRiskCertTypes();
        updateRiskCertTypes();
        getNewTaxesModal();
        addNewRiskSchedule();
        saveRiskSchedules();
        addNewClient();
        createSectorSelect();
        populateBranchLov1();
        populateClientTypeLov2();
        populateTitlesLov();
        populateCountryLov();
        saveClientDetails();
        validateRisk();
        getPolicyWet();
        UTILITIES.createAssignee();
        UTILITIES.emailReports();
        UTILITIES.smsReports();
        uploadRiskDocument();
        getNewIntParties();
        importuWRisks();
        enablepremsa('');
        saveRiskDocsList();
        $("#btn-add-docs").click(function () {
            searchReqDocs("");
        });
        $("#btn-import-logs").hide();
        $("#btn-import-logs").click(function () {
            getRiskImportLogs();
            $('#riskimportLogsModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        });
    }

    return {
        init: init,
        editRiskCerts: editRiskCerts,
        //deleteRiskCerts:deleteRiskCerts,
        editRiskSection: editRiskSection,
        deleteRiskSection: deleteRiskSection,
        deleteBeneficiary: deleteBeneficiary,
        editBeneficiary: editBeneficiary,
        editRiskDocs: editRiskDocs,
        downloadRiskDoc: downloadRiskDoc,
        deleteRiskDoc: deleteRiskDoc,
        editLifePolicyRisk: editLifePolicyRisk,
        deleteRisk: deleteRisk,
        editPolicyClause: editPolicyClause,
        deletePolicyClause: deletePolicyClause,
        authChecks: authChecks,
        editPolTaxes: editPolTaxes,
        deletePolTaxes: deletePolTaxes,
        getPolicyRemakrs: getPolicyRemakrs,
        downloadClientDoc: downloadClientDoc
    }


})(jQuery);

jQuery(UWScreen.init);
