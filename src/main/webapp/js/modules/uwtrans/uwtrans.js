var UWScreen = (function ($, printCerts) {
    'use strict';
    var selRiskCode = -2000;
    var columns = [];
    var scheddata = [];
    var tableName = '';
    var selPriCode = -2000;
    var endorsementType = null;
    var originalSectionAmount = null;
    var trulyOriginalSectionAmounts = {};
    var ButtonStateManager = {
        stylesAdded: false,

        addStyles: function() {
            if (!this.stylesAdded && !document.getElementById('button-disable-styles')) {
                var style = document.createElement('style');
                style.id = 'button-disable-styles';
                style.innerHTML = `
                .btn-disabled {
                    opacity: 0.6 !important;
                }
                
                .btn-disabled,
                .btn-disabled:hover,
                .btn-disabled:focus,
                .btn-disabled:active {
                    cursor: not-allowed !important;
                }
            `;
                document.head.appendChild(style);
                this.stylesAdded = true;
            }
        },

        greyOutButton: function(buttonId) {
            this.addStyles();
            var $button = $("#" + buttonId);
            $button.prop("disabled", true);
            $button.addClass('btn-disabled');
            $button.css('cursor', 'not-allowed');
            $button.off('click').on('click', function(e) {
                e.preventDefault();
                e.stopImmediatePropagation();
                return false;
            });
        }
    };
     var displayAuditTrails = function() {
               console.log("displayAuditTrails called with polCode:", polCode); // Debugging log
               if (polCode !== -2000) {
                   $.ajax({
                       type: 'GET',
                       url: SERVLET_CONTEXT + '/protected/uw/policies/audit-trails/' + polCode,
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
    var ButtonManager = {
        buttonStates: {},

        // Initialize button
        initButton: function(buttonId, options) {
            options = options || {};
            var $button = $("#" + buttonId);
            if ($button.length && !this.buttonStates[buttonId]) {
                this.buttonStates[buttonId] = {
                    originalText: $button.val() || $button.text(),
                    isProcessing: false,
                    isCompleted: false,
                    type: options.type || 'simple',
                    successText: options.successText || 'Completed',
                    processingText: options.processingText || 'Processing...'
                };
            }
        },

        // Initialize button for modal workflow (missing method)
        initModalButton: function(buttonId, loadingText) {
            this.initButton(buttonId, { type: 'modal' });
            this.setLoading(buttonId, loadingText);
        },

        // Enable button for modal (missing method)
        enableForModal: function(buttonId, readyText) {
            this.ensureInitialized(buttonId);
            var $button = $("#" + buttonId);
            var state = this.buttonStates[buttonId];
            if ($button.length && state && !state.isCompleted) {
                $button.prop('disabled', false);
                var text = readyText || state.originalText;
                if ($button.is('input')) {
                    $button.val(text);
                } else {
                    $button.text(text);
                }
            }
        },

        // Set completed state (missing method)
        setCompleted: function(buttonId, completedText) {
            this.ensureInitialized(buttonId);
            var $button = $("#" + buttonId);
            var state = this.buttonStates[buttonId];
            if ($button.length && state) {
                state.isCompleted = true;
                state.isProcessing = false;
                $button.prop('disabled', true);
                $button.addClass('btn-disabled');
                var text = completedText || state.successText;
                if ($button.is('input')) {
                    $button.val(text);
                } else {
                    $button.text(text);
                }
            }
        },

        // Set loading state (backward compatible)
        setLoading: function(buttonId, loadingText) {
            this.ensureInitialized(buttonId);
            var $button = $("#" + buttonId);
            var state = this.buttonStates[buttonId];

            if ($button.length && state) {
                $button.prop('disabled', true);
                var text = loadingText || state.processingText;
                if ($button.is('input')) {
                    $button.val(text);
                } else {
                    $button.text(text);
                }
            }
        },

        // Set processing state
        setProcessing: function(buttonId, processingText) {
            this.ensureInitialized(buttonId);
            var state = this.buttonStates[buttonId];
            if (state) {
                state.isProcessing = true;
                this.setLoading(buttonId, processingText || state.processingText);
            }
        },

        // Set success state
        setSuccess: function(buttonId, successText, permanent) {
            this.ensureInitialized(buttonId);
            var $button = $("#" + buttonId);
            var state = this.buttonStates[buttonId];

            if ($button.length && state) {
                state.isProcessing = false;

                if (permanent || state.type === 'permanent') {
                    state.isCompleted = true;
                    $button.addClass('btn-disabled');
                }

                $button.prop('disabled', permanent || state.type === 'permanent');
                var text = successText || state.successText || state.originalText;
                if ($button.is('input')) {
                    $button.val(text);
                } else {
                    $button.text(text);
                }
            }
        },

        // Reset button (backward compatible)
        resetButton: function(buttonId, force) {
            this.ensureInitialized(buttonId);
            var $button = $("#" + buttonId);
            var state = this.buttonStates[buttonId];

            if ($button.length && state && (force || !state.isCompleted)) {
                state.isProcessing = false;
                if (force) state.isCompleted = false;

                $button.prop('disabled', false);
                $button.removeClass('btn-disabled');
                if ($button.is('input')) {
                    $button.val(state.originalText);
                } else {
                    $button.text(state.originalText);
                }
            }
        },

        // Validation workflow helper
        handleValidationWorkflow: function(buttonId, validationFn, processingText) {
            this.ensureInitialized(buttonId);
            var $button = $("#" + buttonId);
            if (!$button.length) return false;

            // Run validation
            if (validationFn && !validationFn()) {
                return false; // Don't change button state if validation fails
            }

            // Set processing state
            this.setProcessing(buttonId, processingText);
            return true;
        },

        // Handle modal workflows
        handleModalWorkflow: function(buttonId, modalId, options) {
            options = options || {};
            this.ensureInitialized(buttonId);
            var state = this.buttonStates[buttonId];

            // Setup modal event handlers
            var self = this;
            $(modalId).off('hidden.bs.modal.btnmgr').on('hidden.bs.modal.btnmgr', function() {
                if (state && !state.isCompleted && !state.isProcessing) {
                    self.resetButton(buttonId);
                }
            });

            // Enable button when modal is ready
            if (options.enableOnShow) {
                this.resetButton(buttonId);
            }
        },

        // Success helper
        handleSuccess: function(buttonId, successText, permanent) {
            this.setSuccess(buttonId, successText, permanent);
        },

        // Error helper
        handleError: function(buttonId) {
            this.resetButton(buttonId);
        },

        // Check button state
        isInState: function(buttonId, stateName) {
            var state = this.buttonStates[buttonId];
            if (!state) return false;

            switch(stateName) {
                case 'processing': return state.isProcessing;
                case 'completed': return state.isCompleted;
                case 'ready': return !state.isProcessing && !state.isCompleted;
                default: return false;
            }
        },

        // Utility: ensure button is initialized
        ensureInitialized: function(buttonId) {
            if (!this.buttonStates[buttonId]) {
                this.initButton(buttonId);
            }
        },

        // Handle confirmation dialog workflow
        handleConfirmationWorkflow: function(buttonId, confirmationFn, processingText) {
            this.ensureInitialized(buttonId);
            var self = this;

            // Set initial loading state
            this.setLoading(buttonId, 'Processing...');

            // Execute confirmation
            confirmationFn(
                // Success callback - user confirmed
                function() {
                    self.setProcessing(buttonId, processingText || 'Processing...');
                },
                // Cancel callback - user cancelled or closed
                function() {
                    self.resetButton(buttonId);
                }
            );
        },

        // Handle form modal workflow (for modals with forms)
        handleFormModalWorkflow: function(buttonId, modalId, options) {
            options = options || {};
            this.ensureInitialized(buttonId);
            var self = this;

            // Set loading while preparing modal
            this.setLoading(buttonId, options.loadingText || 'Loading...');

            // Setup modal close handler
            $(modalId).off('hidden.bs.modal.formmgr').on('hidden.bs.modal.formmgr', function() {
                var state = self.buttonStates[buttonId];
                if (state && !state.isCompleted && !state.isProcessing) {
                    self.resetButton(buttonId);
                }
            });
        },

        // Enable button when modal is ready (for form modals)
        enableForFormModal: function(buttonId, readyText) {
            this.ensureInitialized(buttonId);
            var $button = $("#" + buttonId);
            var state = this.buttonStates[buttonId];

            if ($button.length && state && !state.isCompleted) {
                $button.prop('disabled', false);
                var text = readyText || state.originalText;
                if ($button.is('input')) {
                    $button.val(text);
                } else {
                    $button.text(text);
                }
            }
        },
        handleTableButton: function(buttonId, processingText, successText) {
            this.initButton(buttonId, {
                type: 'simple',
                processingText: processingText || 'Processing...',
                successText: successText || 'Selected'
            });
            this.setProcessing(buttonId, processingText);
        },

        // Set permanent success (for buttons that redirect)
        setPermanentSuccess: function(buttonId, successText) {
            this.ensureInitialized(buttonId);
            var $button = $("#" + buttonId);
            var state = this.buttonStates[buttonId];

            if ($button.length && state) {
                state.isCompleted = true;
                state.isProcessing = false;
                $button.prop('disabled', true);
                $button.addClass('btn-disabled');
                var text = successText || 'Completed';
                if ($button.is('input')) {
                    $button.val(text);
                } else {
                    $button.text(text);
                }
            }
        }
    };
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

    function populateMultiProduct() {
        if ($("#product-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "product-frm",
                sort: 'proDesc',
                change: function (e, a, v) {
                    // $("#sect-id").val(e.added.code);
                    // populateOccupations(e.added.code);
                    $("#product-id").val(e.added.proCode);
                    populateBinderLov();
                },
                formatResult: function (a) {
                    return a.proDesc
                },
                formatSelection: function (a) {
                    return a.proDesc
                },
                initSelection: function (element, callback) {
                    var code = $("#product-id").val();
                    var name = $("#bind-product-desc").val();
                    var data = {proDesc: name, proCode: code};
                    callback(data);
                },
                id: "proCode",
                placeholder: "Select Product",
            });
        }
    }

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

    var createAccrualPolicy = function () {
        $("#pol-interface-type").on('change', function () {
            if ($(this).val() === 'A') {
                bootbox.confirm("Do You Want To Make This An Accrual Policy?", function (result) {
                    if (result) {
                        bootbox.alert("You'll be prompted to upload required accrual policy documents under risk/supporting documents tab.");
                    } else {
                        $("#pol-interface-type").val('C');
                    }
                });
            }
        });
    }

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
                $("#ntl-id").val(result.idNo);
                populateClientLov();
                $("#insured-name").val(result.fname);
                $("#insured-code").val(result.tenId);
                $("#insured-other-name").val(result.otherNames);
                $("#insured-ntl-id").val(result.idNo);
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
        //$("#btn-add-client").click(function(){
        //	$(".clnt-status").hide();
        //	$('#tenant-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
        //	$('#createClientModal').modal({
        //		backdrop: 'static',
        //		keyboard: true
        //	})
        //})
    }
    var getQuestionnairedtls = function () {
        var url = SERVLET_CONTEXT + "/protected/setups/binders/allBinderQuestions/" + $("#binder-id").val();
        $.ajax({
            type: 'GET',
            url: url,
            dataType: 'json',
            async: true,
            success: function (result) {
                for (var res in result) {
                    console.log("Mandatory=" + result[res].isRequired)
                }
                getObject(result);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR);
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            }
        });

        function getObject(quiz) {
            var pages = [];
            var batches = [];
            var count = 0;
            for (var i = 0; i < quiz.length; i++) {
                batches.push(quiz[i]);
                count++;
                if (count === 5 || (count < 5 && i === quiz.length - 1)) {
                    pages.push({
                        questions: batches
                    })
                    batches = [];
                    count = 0;
                }
            }

            console.log(pages);

            var json = {
                title: "Questionnaire",
                showProgressBar: "top",
                pages: pages
            };
            console.log(json);
            window.survey = new Survey.Model(json);

            survey.onComplete.add(function (result) {
                //document.querySelector("#surveyResult").innerHTML =
                //	"result: " + JSON.stringify(result.data);
                var questions = result.getAllQuestions();
                var arr = [];
                for (var i = 0; i < questions.length; i++) {
                    result.validateQuestion(questions[i].name);
                    console.log(questions[i].name + '=' + questions[i].value);
                    var quizname = questions[i].name;
                    var quizanswer = questions[i].value;
                    arr.push({
                        question: quizname,
                        answer: quizanswer
                    });
                }
                if (arr.length == 0) {
                    bootbox.alert("No Records Selected to Process")
                    return;
                }
                var data = {};
                data.quizPolicyCode = polCode;
                data.quizandAnswers = arr;
                console.log(data);

                var url = SERVLET_CONTEXT + "/protected/uw/policies/SavePolicyQuiz";
                //var request = $.post(url, data );

                $.ajax(
                    {
                        url: url,
                        type: "POST",
                        data: JSON.stringify(data),
                        success: function (s) {
                            // $('#myPleaseWait').modal('hide');
                            Swal.fire({
                                title: 'Success',
                                text: 'Processing Successfully',
                                icon: 'success'
                            });
                            $('#questionnaire_tbl').DataTable().ajax.reload();
                            getPolicyDetails();
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
            var surveyValueChanged = function (sender, options) {
                var el = document.getElementById(options.name);
                if (el) {
                    el.value = options.value;

                }
            };
            survey.onValueChanged.add(function (sender, options) {
                var mySurvey = sender;
                var questionName = options.name;
                var newValue = options.value;
            });

            var quizdata = {};
            $.ajax({
                type: 'GET',
                url: SERVLET_CONTEXT + '/protected/uw/policies/policyQuizList',
                dataType: 'json',
                async: false,
                success: function (result) {
                    for (var i = 0; i < result.length; i++) {
                        var key = result[i].question.questionname;
                        var obj = {};
                        var SplitChar = ',';

                        if (result[i].choice.indexOf(SplitChar) > 0) {
                            var multipleChoices = [];
                            multipleChoices = result[i].choice.split(SplitChar);
                            obj[key] = multipleChoices;
                        } else {
                            obj[key] = result[i].choice
                        }
                        //obj[key] = result[i].choice
                        $.extend(quizdata, obj);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR);
                }
            });

            //console.log(quizdata);
            survey.data = quizdata;
            console.log(survey.data);

            $("#surveyElement").Survey({
                model: survey, onValueChanged: surveyValueChanged
            });
        }
    }
    var getPolicyQuestionnaire = function () {
        var url = SERVLET_CONTEXT + "/protected/uw/policies/policyQuiz/" + polCode;

        var currTable = $('#questionnaire_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "quizId",
                    "render": function (data, type, full, meta) {
                        return full.question.questionname;
                    }
                },
                {
                    "data": "choice"
                },

            ]
        }));
        return currTable;
    };
    var deletePolicyQuiz = function () {
        bootbox.confirm("Are you sure want to delete this questionnaire?", function (result) {
            var url = SERVLET_CONTEXT + "/protected/uw/policies/deletePolicyQuiz/" + polCode;
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: url,
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Questionnaire Deleted Successfully',
                            icon: 'success'
                        });
                        $('#questionnaire_tbl').DataTable().ajax.reload();
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
    var fillQuestionnaire = function () {

        $("#btn-questionnaire").click(function () {
            getQuestionnairedtls();
            $('#questionnaireModal').modal({
                backdrop: 'static',
                keyboard: true
            });

        });

        $("#btn-edit-questionnaire").click(function () {
            getQuestionnairedtls();
            $('#questionnaireModal').modal({
                backdrop: 'static',
                keyboard: true
            });

        });

        $("#btn-del-questionnaire").click(function () {
            deletePolicyQuiz();
        });


    }

    var populatePolicyDetails = function () {
        console.log('polCode ', polCode);
        if (typeof polCode !== 'undefined') {
            if (polCode !== -2000) {
                $("#btn-uw-reports").show();
                $("#risk-form").hide();
                $("#btn-save-risk").hide();
                $("#btn-save-cancel").hide();

                $("#btn-add-risk").show();
                $("#risk-div").show();
                $("#sect-div").show();
                $("#prem-rates-div").hide();
                $("#btn-import-risk").show();
                $("#risk-alert-template").show();
                getPolicyDetails();
                getPolicybeneficiaries();
                getPolicyRemakrs();
                getRefundComments();
                $("#other-pol-details").show();
                $("#questionnaire-tabb").show();
                $("#myTab,#show-taxes,#show-clauses,#end-remarks,#checks-tab,#cert-tab,#receipts-tab,#workflow").show();
                createPolicyClauses();
                createPolicyTaxes();
                $(".import-risks").hide();
                $('.pol-scheme').hide();
                createPolicyChecks();
                displayAuditTrails();
                $("#btn-assign-trans2").show();
            } else {
                $("#btn-auth-policy").css("display", "none");
                $("#btn-assign-trans2").css("display", "none");
                $("#btn-assign-trans").hide();
                $("#btn-assign-trans2").hide();
                $("#btn-unconvert-policy").hide();
                $("#btn-dispatch-trans").hide();
                $("#btn-comment-policy").css("display", "none");
                $("#btn-print-certs").hide();
                $("#btn-import-risk").hide();
                $("#risk-alert-template").hide();
                $("#btn-uw-reports").hide();
                $("#risk-form").show();
                $("#risk-div").hide();
                $("#other-pol-details").hide();
                $("#prem-rates-div").show();
                $("#btn-add-policy").show();
                $("#sect-div").hide();
                $("#btn-save-risk").hide();
                $("#btn-save-cancel").hide();
                $("#btn-add-risk").hide();
                $("#questionnaire-tabb").hide();
                $("#myTab,#show-taxes,#show-clauses,#end-remarks,#checks-tab,#cert-tab,#receipts-tab,#workflow").hide();
                $("#btn-add-new-section").hide();
                $("#display-client").hide();
                $("#display-binder").hide();
                $("#display-payment-mode").hide();
                $("#display-branch").hide();
                $("#display-currency").hide();
                $(".import-risks").show();
                $(".pol-scheme").show();
                populateSubAgentsLov();
                populateIntroducerLov();
                populateMarketerLov();
                populateLeadsManLov();

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
                    console.log(s)
                        if (s.product.riskNote != null && s.product.riskNote != "") {
                            var reportTemplate = s.product.riskNote;
                            var riskNoteUrl = SERVLET_CONTEXT + '/protected/uw/policies/' + reportTemplate;
                        } else {
                            var productName = "rpt_risk_note";

                            // Replace spaces with underscores and convert to lowercase
                            productName = productName.trim().toLowerCase().replace(/\s+/g, '_');

                            // Build the URL based on the modified product name
                            var riskNoteUrl = SERVLET_CONTEXT + '/protected/uw/policies/' + productName;

                        }

                        if (s.product.proShtDesc.trim().toUpperCase().includes("MOTOR") || s.product.proDesc.trim().toUpperCase().includes("MOTOR")) {
                            $(".motor-disp").show();
                            $(".non-motor-disp").hide();
                        } else {
                            $(".motor-disp").hide();
                            $(".non-motor-disp").show();
                        }
                        // Set the href attribute of the risk note link
                        $('#risknote-link').attr('href', riskNoteUrl);
                        if (s.authStatus) {
                            if (s.authStatus === "D") {
                                $("#btn-assign-trans").show();
                                // $("#btn-assign-trans2").show();
                                $("#btn-dispatch-trans").hide();
                                $("#btn-import-risk").show();
                                $("#risk-alert-template").show();
                                $("#btn-add-docs").show();
                                $('#chk-admin-fee').prop('disabled', false);
                                $("#btn-add-new-ips").show();
                                $("#btn-add-policy").css("display", "block");
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-assign-trans2").css("display", "block");
                                $("#btn-undo-make-ready").css("display", "none");
                                $("#btn-unconvert-policy").css("display", "none");
                                $("#btn-make-ready-policy").css("display", "block");
                                $("#btn-print-certs").hide();
                                $("#pol-status").text("Draft");
                                $("#edit-currency").show();
                                $("#display-currency").hide();
                                $("#btn-cancel-general-policy").hide();
                                $("#edit-branch").show();
                                $("#display-branch").hide();
                                $("#edit-payment-mode").show();
                                $("#display-payment-mode").hide();
                                $("#edit-binder").show();
                                $("#display-binder").hide();
                                //$(".edit-client").show();
                                //$("#display-client").hide();
                                $("#client-frm").select2("enable", true);
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
                                $("#btn-comment-policy").css("display", "none");
//                                $("#btn-add-new-remark").show();
                               if (s.transType !== 'NB') {
                                   $("#btn-add-new-remark").show().prop("disabled", false);
                                   $("#btn-save-remark").show().prop("disabled", false);
                                   $("#poli-remarks").prop("disabled", false);
                               } else {
                                   $("#btn-add-new-remark").hide().prop("disabled", true);
                                   $("#btn-save-remark").hide().prop("disabled", true);
                                   $("#poli-remarks").prop("disabled", true);
                               }
//                                $("#btn-save-remark").show();
                                $("#btn-add-new-sched").show();
                                $("#btn-add-edit-sched").show();
                                $("#btn-add-del-sched").show();
                                $("#sub-agent-frm").select2("enable", true);
                                $("#introducer-frm").select2("enable", true);
                                $("#marketer-frm").select2("enable", true);
                                $("#leads-man-frm").select2("enable", true);
                                $("#agent-type").prop("disabled", false);
                                $('#client-input').addClass('col-md-6 col-xs-9').removeClass('col-md-7 col-xs-12');
                                $('#new-client').css('visibility', 'visible');
                                if (s.quizTaken === "Y") {
                                    $("#btn-questionnaire").hide();
                                    $("#btn-del-questionnaire").show();
                                } else {
                                    $("#btn-del-questionnaire").hide();
                                    $("#btn-questionnaire").show();

                                }
                            } else if (s.authStatus === "RD") {
                                $("#btn-assign-trans").show();
                                $("#btn-dispatch-trans").hide();
                                $('#chk-admin-fee').prop('disabled', true);
                                $("#btn-import-risk").show();
                                $("#risk-alert-template").show();
                                $("#btn-add-docs").show();
                                $("#btn-add-new-ips").show();
                                $("#btn-add-policy").css("display", "none");
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-assign-trans2").css("display", "none");
                                $("#btn-undo-make-ready").css("display", "none");
                                $("#btn-unconvert-policy").css("display", "none");
                                $("#btn-make-ready-policy").css("display", "none");
                                $("#btn-print-certs").hide();
                                $("#pol-status").text("Draft");
                                $("#edit-currency").show();
                                $("#display-currency").hide();
                                $("#edit-branch").show();
                                $("#display-branch").hide();
                                $("#edit-payment-mode").show();
                                $("#display-payment-mode").hide();
                                $("#edit-binder").show();
                                $("#display-binder").hide();
                                //$(".edit-client").show();
                                //$("#display-client").hide();
                                $("#client-frm").select2("enable", true);
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
                                $("#btn-comment-policy").css("display", "none");
//                                $("#btn-add-new-remark").show();
                               if (s.transType !== 'NB') {
                                   $("#btn-add-new-remark").show().prop("disabled", false);
                                   $("#btn-save-remark").show().prop("disabled", false);
                                   $("#poli-remarks").prop("disabled", false);
                               } else {
                                   $("#btn-add-new-remark").hide().prop("disabled", true);
                                   $("#btn-save-remark").hide().prop("disabled", true);
                                   $("#poli-remarks").prop("disabled", true);
                               }
//                                $("#btn-save-remark").show();
                                $("#btn-add-new-sched").show();
                                $("#btn-add-edit-sched").show();
                                $("#btn-add-del-sched").show();
                                $("#sub-agent-frm").select2("enable", true);
                                $("#introducer-frm").select2("enable", true);
                                $("#marketer-frm").select2("enable", true);
                                $("#leads-man-frm").select2("enable", true);
                                $("#agent-type").prop("disabled", false);
                                $('#client-input').addClass('col-md-6 col-xs-9').removeClass('col-md-7 col-xs-12');
                                $('#new-client').css('visibility', 'visible');
                                if (s.quizTaken === "Y") {
                                    $("#btn-questionnaire").hide();
                                    $("#btn-del-questionnaire").show();
                                } else {
                                    $("#btn-del-questionnaire").hide();
                                    $("#btn-questionnaire").show();

                                }
                            } else if (s.authStatus === "R") {
                                $('.btn-edit').prop("disabled", true);
                                $('.btn-del').prop("disabled", true)
                                $('#chk-admin-fee').prop('disabled', true);
                                $("#btn-assign-trans").hide();
                                $("#btn-assign-trans2").show();
                                $("#btn-import-risk").hide();
                                $("#risk-alert-template").hide();
                                $("#btn-add-docs").show();
                                $("#btn-add-new-ips").hide();
                                $("#btn-cancel-general-policy").css("display", "none");
                                $("#btn-add-policy").css("display", "none");
                                $("#btn-dispatch-trans").hide();
                                $("#btn-auth-policy").css("display", "block");
                                $("#btn-unconvert-policy").css("display", "block");
                                $("#btn-assign-trans2").css("display", "block");
                                var count = 0;
                                $('#polReceipts tr').each(function () {
                                    count++;
                                });
                                console.log(count);

//                                if (count > 3) {
//                                    $("#btn-make-ready-policy").css("display", "none");
//                                } else {
//                                    if (createdBy === currentUser) {
//                                        $("#btn-undo-make-ready").css("display", "block");
//                                    } else {
//                                        $("#btn-undo-make-ready").css("display", "none");
//                                    }
//                                    $("#btn-make-ready-policy").css("display", "none");
//                                }
//                                if (createdBy === currentUser) {
//                                    $("#btn-unconvert-policy").css("display", "none");
//                                }else{
//                                    $("#btn-unconvert-policy").css("display", "block");
//                                }
                                $("#btn-print-certs").hide();
                                $("#pol-status").text("Ready");
                                $("#edit-currency").hide();
                                $("#display-currency").show();
                                $("#edit-branch").hide();
                                $("#display-branch").show();
                                $("#edit-payment-mode").hide();
                                $("#display-payment-mode").show();
                                $("#edit-binder").hide();
                                $("#display-binder").show();
                                $("#client-frm").select2("enable", false);
                                //$(".edit-client").hide();
                                //$("#display-client").show();
                                $("#sub-agent-frm").select2("enable", false);
                                $("#introducer-frm").select2("enable", false);
                                $("#leads-man-frm").select2("enable", false);
                                $("#marketer-frm").select2("enable", false);
                                $("#agent-type").prop("disabled", true);
                                $('#client-input').addClass('col-md-7 col-xs-12').removeClass('col-md-6 col-xs-9');
                                $('#new-client').css('visibility', 'hidden');
                                $("#poli-remarks").attr("disabled", "disabled");
                                $("#pol-buss-type").prop("disabled", true);
                                $("#pol-bin-type").prop("disabled", true);
                                $("#from-date").prop("disabled", true);
                                $("#wet-date").prop("disabled", true);
                                $("#pol-interface-type").prop("disabled", true);
                                $("#accrual-payment-type").prop("disabled", true);
                                $("#pol-frequency").prop("disabled", true);
                                $("#client-pol-no").prop("disabled", true);
                                $("#btn-add-risk").hide();
                                $("#btn-add-new-clause").hide();
                                $("#btn-add-new-tax").hide();
                                $("#btn-add-new-section").hide();
                                $("#btn-comment-policy").css("display", "none");
                                if (s.transType === 'CO' || s.transType === 'CN' || s.transType === 'RS') {
                                    $("#btn-add-new-remark").show();
                                    $("#btn-save-remark").show();
                                    $("#poli-remarks").attr("disabled", false);
                                } else {
                                    $("#btn-add-new-remark").hide();
                                    $("#btn-save-remark").hide();
                                    $("#poli-remarks").attr("disabled", true);
                                }
                                $("#btn-add-new-sched").hide();
                                $("#btn-add-edit-sched").hide();
                                $("#btn-add-del-sched").hide();
                                $("#btn-questionnaire").hide();
                                $("#btn-del-questionnaire").hide();
                                $(".chk-admin-fee").show();
                            } else if (s.authStatus === "A") {
                                $('.btn-edit').prop("disabled", true);
                                $('#chk-admin-fee').prop('disabled', true);
                                $('.btn-del').prop("disabled", true)
                                $("#btn-import-risk").hide();
                                $("#risk-alert-template").hide();
                                $("#btn-add-docs").hide();
                                $("#btn-add-new-ips").hide();
                                $("#btn-dispatch-trans").show();
                                $("#btn-print-certs").show();
                                $("#btn-add-policy").css("display", "none");
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-assign-trans").css("display", "none");
                                $("#btn-assign-trans2").css("display", "none");
                                $("#btn-undo-make-ready").css("display", "none");
                                $("#btn-unconvert-policy").css("display", "none");
                                $("#btn-make-ready-policy").css("display", "none");

                                $("#pol-status").text("Authorised");
                                $("#sub-agent-frm").select2("enable", false);
                                $("#introducer-frm").select2("enable", false);
                                $("#leads-man-frm").select2("enable", false);
                                $("#marketer-frm").select2("enable", false);
                                $("#agent-type").prop("disabled", true);
                                $('#client-input').addClass('col-md-7 col-xs-12').removeClass('col-md-6 col-xs-9');
                                $('#new-client').css('visibility', 'hidden');
                                $("#edit-currency").hide();
                                $("#display-currency").show();
                                $("#edit-branch").hide();
                                $("#display-branch").show();
                                $("#edit-payment-mode").hide();
                                $("#display-payment-mode").show();
                                $("#edit-binder").hide();
                                $("#display-binder").show();
                                $("#client-frm").select2("enable", false);
                                //$(".edit-client").hide();
                                //$("#display-client").show();
                                $("#poli-remarks").prop("disabled", true);
                                $("#policy-remarks").prop("disabled", true);
                                $("#pol-buss-type").prop("disabled", true);
                                $("#pol-bin-type").prop("disabled", true);
                                $("#from-date").prop("disabled", true);
                                $("#wet-date").prop("disabled", true);
                                $("#pol-interface-type").prop("disabled", true);
                                $("#accrual-payment-type").prop("disabled", true);
                                $("#pol-frequency").prop("disabled", true);
                                $("#client-pol-no").prop("disabled", true);
                                $("#btn-add-risk").hide();
                                $("#btn-add-new-clause").hide();
                                $("#btn-add-new-tax").hide();
                                $("#btn-add-new-section").hide();
                                $("#btn-add-new-remark").hide();
                                $("#btn-save-remark").hide();
                                $("#btn-add-new-sched").hide();
                                $("#btn-add-edit-sched").hide();
                                $("#btn-add-del-sched").hide();
                                $("#btn-questionnaire").hide();
                                $("#btn-del-questionnaire").hide();
                                $("#btn-comment-policy").css("display", "none");
                                $("#btn-cancel-general-policy").hide();
                            }else if (s.authStatus === "LD") {
                                $('.btn-edit').prop("disabled", true);
                                $('.btn-del').prop("disabled", true)
                                $('#chk-admin-fee').prop('disabled', true);
                                $("#btn-assign-trans").hide();
                                $("#btn-assign-trans2").show();
                                $("#btn-import-risk").hide();
                                $("#risk-alert-template").hide();
                                $("#btn-cancel-general-policy").hide();
                                $("#btn-add-docs").hide();
                                $("#btn-add-new-ips").hide();
                                $("#btn-dispatch-trans").show();
                                $("#btn-print-certs").show();
                                $("#btn-add-policy").css("display", "none");
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-assign-trans2").css("display", "none");
                                $("#btn-undo-make-ready").css("display", "none");
                                $("#btn-unconvert-policy").css("display", "none");
                                $("#btn-unconvert-policy").css("display", "none");
                                $("#btn-make-ready-policy").css("display", "none");
                                $("#pol-status").text("Authorised");
                                $("#sub-agent-frm").select2("enable", false);
                                $("#introducer-frm").select2("enable", false);
                                $("#leads-man-frm").select2("enable", false);
                                $("#marketer-frm").select2("enable", false);
                                $("#agent-type").prop("disabled", true);
                                $('#client-input').addClass('col-md-7 col-xs-12').removeClass('col-md-6 col-xs-9');
                                $('#new-client').css('visibility', 'hidden');
                                $("#edit-currency").hide();
                                $("#display-currency").show();
                                $("#edit-branch").hide();
                                $("#display-branch").show();
                                $("#edit-payment-mode").hide();
                                $("#display-payment-mode").show();
                                $("#edit-binder").hide();
                                $("#display-binder").show();
                                $("#client-frm").select2("enable", false);
                                //$(".edit-client").hide();
                                //$("#display-client").show();
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
                                $("#btn-save-remark").hide();
                                $("#btn-add-new-sched").hide();
                                $("#btn-add-edit-sched").hide();
                                $("#btn-add-del-sched").hide();
                                $("#btn-questionnaire").hide();
                                $("#btn-del-questionnaire").hide();
                                $("#btn-uw-reports").hide();
                                $("#prem-item").hide();
                                $("#hidden-buttons").hide();
                                $("#btn-comment-policy").css("display", "none");
                            }
                            else if (s.authStatus === "RCV") {
                                $("#pol-status").text("Draft");
                                $("#btn-make-ready-policy").css("display", "block");
                                $("#btn-assign-trans").css("display", "block");
                                $("#btn-add-policy").css("display", "block");
                                $("#btn-auth-policy").css("display", "none");
                                $("#btn-unconvert-policy").css("display", "none");
                                $("#btn-comment-policy").css("display", "block");
                            }
                            else {
                                $("#btn-comment-policy").css("display", "none"); // Hide for other statuses
                            }

                        }
                        polCode = s.policyId;
                        UTILITIES.getProcessActiveDiagram(s.policyId);
                        UTILITIES.getTaskActive(s.policyId);
                        UTILITIES.getProcessHistory(s.policyId);
                        // $("#client-info").text(s.client.fname+" "+s.client.otherNames);
                        if (s.binder)
                            $("#binder-info").text(s.binder.binName);
                        //	$("#pay-mode-info").text(s.paymentMode.pmDesc);
                        $("#branch-info").text(s.branch.obName);
                        $("#currency-info").text(s.transCurrency.curName);
                        $("#pol-no").text(s.polNo);
                        $("#h4pol").text("Policy No: " + s.polNo);
                        $("#div-pol-no").val(s.polNo);
                        $("#div-endos-no").val(s.polRevNo);
                        $("#policy-id").val(s.policyId);
                        $("#pol-rev-no").text(s.polRevNo);
                        if(s.adminFeeApplicable && s.adminFeeApplicable==='Y'){
                            $('#chk-admin-fee').prop('checked', true);
                            $(".admin-fee-appl").show();
                        }
                        else{
                            $('#chk-admin-fee').prop('checked', false);
                            $(".admin-fee-appl").hide();
                        }
                        if (s.product)
                            $("#pol-bind-age-appli").val(s.product.ageApplicable);
                        $("#pol-sub-agent-comm").text(UTILITIES.currencyFormat(s.subAgentComm));
                        $("#pol-admin-fee").text(UTILITIES.currencyFormat(s.adminFeeAmt));
                        $("#pol-admin-fee-vat").text(UTILITIES.currencyFormat(s.adminFeeVatAmt));
                        $("#pol-introducer-comm").text(UTILITIES.currencyFormat(s.introducerAgentComm));
                        $("#pol-marketer-comm").text(UTILITIES.currencyFormat(s.marketerAgentComm));
                        $("#pol-endos-gross-premium").text(UTILITIES.currencyFormat(s.endosgrossPremium));
                        $("#pol-paid-premium").text(UTILITIES.currencyFormat(s.paidPremium));
                        if (s.refundablePremium) {
                            if (s.refundablePremium != 0) {
                                $("#pol-refundable-amount").text(UTILITIES.currencyFormat(s.refundablePremium));
                                $("#refund-amount1").val(s.refundablePremium);
                                $("#hasrefund").val("Y");
                            } else {
                                $("#hasrefund").val("N");
                            }
                        }

                        $("#pol-sum-insured").text(UTILITIES.currencyFormat(s.sumInsured));
                        $("#pol-premium").text(UTILITIES.currencyFormat(s.premium)); // Basic premium
                        $("#pol-basic-prem").text(UTILITIES.currencyFormat(s.basicPrem)); // gross premium
                        $("#pol-paid-premium").text(UTILITIES.currencyFormat(s.paidPremium));
                        $("#pol-outstanding-premium").text(UTILITIES.currencyFormat(s.outstandingPremium))
                        if (s.paidPremPct != null) {
                            $("#pol-paid-premium-pct").text(s.paidPremPct + "%")
                        } else {
                            $("#pol-paid-premium-pct").text("0%")
                        }
                        $("#pol-net-prem").text(UTILITIES.currencyFormat(s.netPrem));
                        $("#pol-taxes-amt").text(s.taxes);
                        if (s.product) {
                            if (s.product.motorProduct) {
                                $(".motor-disp").show();
                                $(".non-motor-disp").hide();
                            } else {
                                $(".motor-disp").hide();
                                $(".non-motor-disp").show();
                            }
                            $(".wiba-disp").hide();
                            // if (s.product.wibaProduct && s.product.wibaProduct==='Y') {
                            // 	$(".motor-disp").hide();
                            // 	$(".non-motor-disp").show();
                            // 	$(".wiba-disp").show();
                            // } else {
                            // 	$(".motor-disp").hide();
                            // 	$(".non-motor-disp").show();
                            //
                            // }


                        } else {
                            $(".motor-disp").hide();
                            $(".non-motor-disp").show();
                        }
                        if (s.product)
                            $("#pol-prod-name").text(s.product.proDesc);
                        $("#from-date").val(moment(s.wefDate).format('DD/MM/YYYY'));
                        $("#wet-date").val(moment(s.wetDate).format('DD/MM/YYYY'));
                        if (s.agent)
                            $('#pol-ins-comp').text(s.agent.name)
                        $("#pol-interface-type").val(s.interfaceType);
                        if (s.interfaceType === "A") {
                            $("#disp-inst-date").show();
                            $("#disp-accrual-type").show();
                            $("#accrual-payment-type").val(s.accrualPaymentType);
                            $("#installment-date").val(moment(s.accrualInstDate).format('DD/MM/YYYY'))
                        } else {
                            $("#disp-inst-date").hide();
                            $("#disp-accrual-type").hide();

                        }

                        $("#pol-frequency").val(s.frequency);
                        $("#client-pol-no").val(s.clientPolNo);
                        $("#pol-buss-type").val(s.businessType);
                        if (s.subAgent) {
                            $("#agent-type").val('SUB').trigger('change');
                            $("#sub-agent-id").val(s.subAgent.acctId);
                            $("#sub-agent-name").val(s.subAgent.name);
                            $("#acc-id").val(s.subAgent.accountType.accId);
                            $("#sub-agent-absaNo-id").val(s.absaNoSubAgent);
                            $("#sub-agent-absaNo-name").val(s.absaNoSubAgent);
                            populateSubAgentsLov();
                        }
                        if (s.marketerAgent) {
                            $("#agent-type").val('MRK').trigger('change');
                            $("#marketer-id").val(s.marketerAgent.acctId);
                            $("#marketer-name").val(s.marketerAgent.name);
                            $("#acct-id").val(s.marketerAgent.accountType.accId);
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
                        $("#ntl-id").val(s.client.idNo);
                        populateClientLov();
                        $("#product-id").val(s.product.proCode);
                        $("#prd-id").val(s.product.proCode);
                        $('#pr-name').val(s.product.proDesc);
                        createProductForSel();
                        if (s.product.proGroup.prgType === 'MP') {
                            $("#pol-bin-type").val('MP');
                            $(".multi-product").css("display", "block");
                            $(".multi-product-uw").css("display", "block");
                            $(".bind-insurance").css("display", "none");
                            $(".bind-info").css("display", "none");
                            $("#bind-product-desc").val(s.product.proDesc);
                            populateMultiProduct();
                            getPolicyBinders(s.policyId);
                        } else {
                            $(".bind-info").css("display", "block");
                            $(".multi-product").css("display", "none");
                            $(".bind-insurance").css("display", "block");
                            $(".multi-product-uw").css("display", "none");
                            $("#risk-binder").text(s.binder.binName);
                            $("#risk-binder-id").val(s.binder.binId);
                            $("#risk-binder-code").val(s.binder.binId);
                            $("#risk-bind-code").val(s.binder.binId);
                            $("#binder-id").val(s.binder.binId);
                            $("#pol-agent-id").val(s.agent.acctId);
                            $("#bind-name").val(s.binder.binName);
                            $("#pol-binder-policy").val(s.binder.binType);
                            $("#pol-bin-type").val(s.binder.binType);
                            populateBinderLov();
                            getUWPolicyRisks(s.policyId);

                        }

                        //
                        // $("#pm-id").val(s.paymentMode.pmId);
                        // $("#pm-name").val(s.paymentMode.pmDesc);
                        // populatePaymentModes();
                        $("#brn-id").val(s.branch.obId);
                        $("#brn-name").val(s.branch.obName);
                        populateUserBranches();
                        $('#pol-comm-amt').text(UTILITIES.currencyFormat(s.commAmt));
                        $('#pol-tl').text(UTILITIES.currencyFormat(s.trainingLevy));
                        $('#pol-phcf').text(UTILITIES.currencyFormat(s.phcf));
                        $('#pol-sd').text(UTILITIES.currencyFormat(s.stampDuty));
                        $('#pol-whtx').text(UTILITIES.currencyFormat(s.whtx));
                        $('#pol-extras').text(UTILITIES.currencyFormat(s.extras));
                        $("#pol-fap").text(UTILITIES.currencyFormat(s.futurePrem));
                        $('#pol-tran-type-disp').text(s.transType);
                        $('#pol-trans-type').val(s.transType);
                        $('#pol-prev-policy').val(s.prevPolicy);
                        $('#pol-reuse-contra-policy').val(s.reusecontraPolicy);
                        if (s.transType === "EN" || s.transType === "RS") {
                            if (s.authStatus === "D" || s.authStatus === "RD") {
                                $("#btn-endors-risk").show();
                            } else {
                                $("#btn-endors-risk").hide();
                            }
                            $(".endorse-disp").show();
                            $(".risk-note-dip").hide();
                            if (s.transType === "RS") {
                                $("#btn-add-risk").hide();
                                $("#btn-import-risk").hide();
                                $("#risk-alert-template").hide();
                            }

                        } else if (s.transType === "CO" || s.transType === "CN") {
                            $(".endorse-disp").attr("id", "endrsrpt");
                            if (s.transType === "CO") {
                                $("#endrsrpt").html("<a href=" + SERVLET_CONTEXT + "/protected/uw/policies/rpt_endorse ' target='_blank'>Reversal Report</a>");
                            } else if (s.transType === "CN") {
                                $("#btn-cancel-general-policy").css("display", "block");
                                $("#endrsrpt").html("<a href=" + SERVLET_CONTEXT + "/protected/uw/policies/rpt_endorse ' target='_blank'>Cancellation Report</a>");
                            }
                            $("#btn-endors-risk").hide();
                            $(".endorse-disp").show();
                            $(".risk-note-dip").hide();
                            //$(".motor-disp").hide();
                        } else {
                            $("#btn-endors-risk").hide();
                            $(".endorse-disp").hide();
                            $(".risk-note-dip").show();
                        }

                        if (s.transType === "CO" || s.transType === "CN") {
                            $("#btn-undo-make-ready").css("display", "none");
                            $("#btn-unconvert-policy").css("display", "none");


                        }
                        if (s.transType === "CO"){
                            $("#btn-make-ready-policy").css("display", "block");
                        }else if (s.transType === "CN") {
                            $("#btn-make-ready-policy").css("display", "none");
                        }

                        if (s.transType === "RN") {
                            $("#renbtn").show();
                        } else {
                            $("#renbtn").hide();
                        } if (s.transType === "RF") {
                            $("#refund-docs-tabb").show();
                            $("#refund-remarks-tab").show();
                        } else {
                            $("#refund-docs-tabb").hide();
                            $("#refund-remarks-tab").hide();
                        }

                        if (s.currentStatus === "A" || s.currentStatus === "D") {
                            $("#btn-add-new-cert").show();
                        } else {
                            $("#btn-add-new-cert").hide();
                        }

                        $("#binder-frm").select2("enable", false);
                        $("#product-frm").select2("enable", false);
                        $("#prd-code").select2("enable", false)
                        $("#pol-bintype").prop("disabled", true);
                        $("#pol-bin-type").prop("disabled", true);


                        getPolicybeneficiaries();
                        printCerts.createCertTable(s.policyId);
                        if (s.renewalDate)
                            $("#pol-ren-date").text(moment(s.renewalDate).format('DD/MM/YYYY'));
                    },
                    error: function (xhr, error) {
                        bootbox.alert(xhr.responseText);
                    }
                });
            } else {

                $("#display-client").hide();
                $("#display-binder").hide();
                $("#display-payment-mode").hide();
                $("#display-branch").hide();
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
            var dt = moment(curDate).format('DD/MM/YYYY');
            $("#risk-wef-date").val(dt);
            var frequency = $('#pol-frequency').val()
            if (!frequency) {
                bootbox.alert("Please select a payment frequency.");
                return;
            }
            getRiskWet(dt, frequency);
            $.ajax({
                type: 'GET',
                url: 'getWetDate',
                dataType: 'json',
                data: {"wefDate": dt},
                async: true,
                success: function (result) {
                    $("#wet-date").val(moment(result).format('DD/MM/YYYY'));
                    // $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));


                },
                error: function (jqXHR, textStatus, errorThrown) {

                }
            });
        });
    };

    var getRiskWet = function (wefDt, frequency) {
        $.ajax({
            type: 'GET',
            url: 'getRiskWetDate',
            dataType: 'json',
            data: {"wefDate": wefDt, "frequency": frequency},
            async: true,
            success: function (result) {
                console.log("RISKWET: " + result);
                $("#risk-wet-date").val(moment(result).format('DD/MM/YYYY'));

            },
            error: function (jqXHR, textStatus, errorThrown) {

            }
        });
    };

    var populateInsuredLov = function () {
        if ($("#insured-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "insured-frm",
                sort: 'fname',
                change: function (e, a, v) {
                    if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
                        $.ajax({
                            type: 'GET',
                            url: 'getClientAge',
                            dataType: 'json',
                            data: {"clientId": e.added.tenId},
                            async: true,
                            success: function (result) {
                                $("#insured-client-age").val(result);
                                $("#insured-code").val(e.added.tenId);
                                $('#subclass-frm').select2('val', null);
                                $('#covertypes-frm').select2('val', null);
                                $("#insured-ntl-id").val(e.added.idNo);
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
                        $("#insured-code").val(e.added.tenId);
                    //$("#insured-id").val(e.added.tenId);
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
                    var idNo = $("#insured-ntl-id").val();
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
                    $("#insured-name").val(e.added.fname);
                    $("#insured-code").val(e.added.tenId);
                    $("#insured-other-name").val(e.added.otherNames);
                    $("#ntl-id").val(e.added.idNo);
                    $("#insured-ntl-id").val(e.added.idNo);
                    if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
                        $.ajax({
                            type: 'GET',
                            url: 'getClientAge',
                            dataType: 'json',
                            data: {"clientId": e.added.tenId},
                            async: true,
                            success: function (result) {
                                $("#insured-client-age").val(result);
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
                    var idNo = $("#ntl-id").val();
                    var data = {fname: name, otherNames: othernames, tenId: code, idNo: idNo};
                    callback(data);
                },
                id: "tenId",
                placeholder: "Select Client"

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
                    $("#acc-id").val(e.added.accountType.accId);

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
                    $("#acct-id").val(e.added.accountType.accId);

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
        // Add CSS styles if they don't exist
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

        // Check if remarks textarea has content AND if policy has existing remarks
        setTimeout(function() {
            var hasRemarkContent = $("#poli-remarks").val() && $("#poli-remarks").val().trim() !== '';
            var hasRemarkId = $("#pol-remark-pk").val() && $("#pol-remark-pk").val() !== '';

            if (hasRemarkContent || hasRemarkId) {
                // Disable all remark controls permanently
                $("#btn-add-new-remark").prop("disabled", true);
                $("#btn-save-remark").prop("disabled", true);
                $("#poli-remarks").prop("disabled", true);

                // Add visual indication
                $("#poli-remarks").addClass('disabled-remark');
                $("#btn-add-new-remark").addClass('btn-disabled');
                $("#btn-save-remark").addClass('btn-disabled');
            }
        }, 1500); // Give time for getPolicyRemakrs() to load data
    };
    $(document).ready(function () {
        $("#agent-type").change(function () {
            var selectedValue = $(this).val();

            if (selectedValue === 'SUB') {
                $(".sub-agent-type").css('display', 'block');
                $(".sub-agent-absNo").css('display', 'block');
                $(".sub-comm-type").css('display', 'none'); //lastupdate from block to none
                $(".marketer-agent-type").css('display', 'none');
                $(".marketer-comm-type").css('display', 'none');
                $(".marketer-agent-absNo").css('display', 'none');
                populateSubAgentsLov();
            } else if (selectedValue === 'MRK') {
                $(".marketer-agent-type").css('display', 'block');
                $(".marketer-agent-absNo").css('display', 'block');
                $(".marketer-comm-type").css('display', 'none'); //lastupdate from block to none
                $(".sub-agent-type").css('display', 'none');
                $(".sub-comm-type").css('display', 'none');
                $(".sub-agent-absNo").css('display', 'none');
                populateMarketerLov();
            } else {
                $(".sub-agent-type, .marketer-agent-type").css('display', 'none');
                $(".sub-comm-type, .marketer-comm-type").css('display', 'none');
                $(".sub-agent-absNo, .marketer-agent-absNo").css('display', 'none');
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
                    getClientAge();
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
                placeholder: "Select Classification"

            });
        }
    };

    var populateCoverTypesLov = function () {
        if ($("#covertypes-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "covertypes-frm",
                sort: 'detId',
                change: function (e, a, v) {
                    console.log(e.added);
                    $("#risk-cov-code").val(e.added.covId);
                    $("#binder-det-id").val(e.added.detId);
                    $("#install-perc").val(e.added.distribution);
                    $("#install-no").val(1);
                    getCommissionRate(e.added.binId);
                    getPremiumRates(e.added.detId);
                    getSubAgentCommissionRate(e.added.binId, $("#acc-id").val());
                    getMarketerCommissionRate(e.added.binId, $("#acct-id").val());
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
    var shouldMakeValueReadonly = function(sectionDesc) {
        var readonlyItems = [
            'POLITICAL VIOLENCE & TERRORISM',
            'PERSONAL ACCIDENT',
            'EXCESS PROTECTOR',
            'FLEET OF THREE'
        ];

        var sectionUpper = sectionDesc.toUpperCase().trim();
        return readonlyItems.some(function(item) {
            return sectionUpper.indexOf(item) !== -1;
        });
    };

    var getPremiumRates = function (detId) {
        console.log($("#pol-bind-age-appli").val());
        if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
            $.ajax({
                type: 'GET',
                url: 'getBinderClientPremRates',
                dataType: 'json',
                data: {"detId": detId, "age": $("#insured-client-age").val()},
                async: true,
                success: function (result) {
                    $("#section_form_tbl tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var allowLimit = "N";
                        if (result[res].limitsAllowed) {
                            allowLimit = result[res].limitsAllowed;
                        }

                        // Check if this premium item should have readonly value field
                        var isReadonly = shouldMakeValueReadonly(result[res].sectionDesc);

                        var markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required' + (isReadonly ? ' readonly' : '') + '>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td><td></td></tr>";
                        if ($("#pol-bin-type").val() === "B") {
                            var str = '';
                            if (result[res].rangeType && result[res].rangeType === "AG") {
                                str = " <input type='text' class='amount form-control' value='" + $("#insured-client-age").val() + "' readonly>";
                            } else if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                                str = " <input type='text' class='amount form-control' required" + (isReadonly ? ' readonly' : '') + ">";
                            } else {
                                str = " <input type='text' class='amount form-control' disabled required>";
                            }

                            var limitStr = (result[res].limitsAllowed === "Y") ? "<button type='button' class='btn btn-primary btn btn-info btn-sm'  onclick='UWScreen.viewPremLimits(" + result[res].pk + ");'>Limits</button>" : "";
                            console.log(limitStr);
                            markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td>" +
                                "<td>" + str + "</td>" +
                                "<td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td>" +
                                "<td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td>" +
                                "<td><input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td>" +
                                "<td>" + limitStr + "</td></tr>";

                        } else {
                            var valueInput = '';
                            if (result[res].rangeType && result[res].rangeType === "AG") {
                                valueInput = ' <input type="text" class="amount form-control" value="' + $("#insured-client-age").val() + '" readonly>';
                            } else {
                                valueInput = ' <input type="text" class="amount form-control" required' + (isReadonly ? ' readonly' : '') + '>';
                            }

                            markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'<input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + valueInput + "</td>" +
                            "<td>" + (result[res].rider && result[res].rider === "Y") ? "<input type='text' class='rate form-control' value='" + result[res].rate + "'>" : "<input type='text' class='rate form-control' readonly value='" + result[res].rate + "'>" + "</td>" +
                            "<td>" + (result[res].rider && result[res].rider === "Y") ? "<input type='text' class='divFactor form-control' value='" + result[res].divFactor + "'>" : "<input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td>" +
                                "<td><input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td>" +
                                "<td></td></tr>";

                        }
                        $("#section_form_tbl").append(markup);
                    }

                    $("#section_form_tbl tr").each(function () {
                        var $freeLimit = $(this).find('.freeLimit');
                        var $amount = $(this).find('.amount');
                        var freeLimitVal = parseFloat($freeLimit.val() || $freeLimit.text() || 0);
                        if ($amount.length) {
                            if (freeLimitVal > 0 && !$amount.prop('readonly')) {
                                $amount.val(freeLimitVal);
                                $amount.trigger('change');
                            } else if (!$amount.prop('readonly')) {
                                $amount.val('');
                                $amount.trigger('change');
                            }
                        }
                    });
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
                        // Check if this premium item should have readonly value field
                        var isReadonly = shouldMakeValueReadonly(result[res].sectionDesc);

                        var markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required' + (isReadonly ? ' readonly' : '') + '>' +
                            "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "' disabled></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" +
                            "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td><td></td></tr>";

                        if ($("#pol-bin-type").val() === "B") {
                            if (result[res].rider && result[res].rider === "Y") {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required disabled>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td><td></td></td></tr>";

                            } else {
                                if (result[res].limitsAllowed === "Y") {
                                    var input = ' <input type="text" class="amount form-control" required disabled>';
                                    if (result[res].ratesApplicable === "Y") {
                                        input = ' <input type="text" class="amount form-control" required' + (isReadonly ? ' readonly' : '') + '>';
                                    }
                                    markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                        "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + input +
                                        "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                        "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                        "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td><td><input type='button' class='btn btn-primary btn btn-info btn-sm' value='Limits' onclick='UWScreen.viewPremLimits(" + result[res].pk + ");'></td></tr>";
                                } else {
                                    markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                        "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required' + (isReadonly ? ' readonly' : '') + '>' +
                                        "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                        "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                        "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td><td></td></tr>";
                                }
                            }
                        } else {

                            if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control"' + (isReadonly ? ' readonly' : '') + '>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td><td></td></tr>";
                            } else if (result[res].rider && result[res].rider === "Y") {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control"' + (isReadonly ? ' readonly' : ' readonly') + '>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td>" +
                                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td><td></td></tr>";
                            }
                            if (result[res].limitsAllowed === "Y") {
                                markup = "<tr><td><input type='hidden' class='mandatory form-control' value='" + result[res].mandatory + "'><input type='hidden' class='section form-control' value='" + result[res].premId +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'>" + result[res].sectionDesc + "</td><td>" + ' <input type="text" class="amount form-control" required disabled>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control' readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td>" +
                                    "<td><input type='checkbox' class='premium-checkbox' " + (result[res].mandatory === 'Y' ? 'checked disabled' : '') + "></td><td><button type='button' class='btn btn-primary btn btn-info btn-sm'  onclick='UWScreen.viewPremLimits(" + result[res].pk + ");'>Limits</button></td></tr>";
                            }
                        }
                        $("#section_form_tbl").append(markup);
                    }

                    $("#section_form_tbl tr").each(function () {
                        var $freeLimit = $(this).find('.freeLimit');
                        var $amount = $(this).find('.amount');
                        var freeLimitVal = parseFloat($freeLimit.val() || $freeLimit.text() || 0);
                        if ($amount.length) {
                            if (freeLimitVal > 0 && !$amount.prop('readonly')) {
                                $amount.val(freeLimitVal);
                                $amount.trigger('change');
                            } else if (!$amount.prop('readonly')) {
                                $amount.val('');
                                $amount.trigger('change');
                            }
                        }
                    });
                    $("#section_form_tbl tr").find("input[type=text]").number(true, 2);
                },
                error: function (jqXHR, textStatus, errorThrown) {

                }
            });
        }
    };


    var viewPremLimits = function (id) {
        var url = SERVLET_CONTEXT + "/protected/setups/binders/sectLimits/" + id;
        $('#premLimitsTbl').DataTable({
            "processing": true,
            "serverSide": true,
            autoWidth: true,
            searching: false,
            "deferRender": true,
            "ajax": {
                'url': url
            },
            lengthMenu: [[5, 10], [5, 10]],
            pageLength: 10,
            destroy: true,
            "columns": [
                {
                    "data": "clausesDef",
                    "render": function (data, type, full, meta) {
                        return full.clausesDef.clause.clauShtDesc;
                    }
                },
                {
                    "data": "clausesDef",
                    "render": function (data, type, full, meta) {
                        return full.clausesDef.clause.clauHeading;
                    }
                },
                {"data": "value"},
            ]
        });
        $('#premLimitsModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    }

    function populateBinderLov(data) {
        var selectedProductId = $("#prd-id").val();
        if ($("#binder-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "binder-frm",
                sort: 'binName',
                change: function (e, a, v) {
                    populateImportSubCovers(e.added.binId);
                    $("#binder-id").val(e.added.binId);
                    $("#risk-binder-code").val(e.added.binId);
                    $("#risk-bind-code").val(e.added.binId);
                    $("#pol-ins-comp").text(e.added.name);
                    $("#pol-prod-name").text(e.added.proDesc);
                    $("#product-id").val(e.added.proCode);
                    $("#pol-agent-id").val(e.added.acctId);
                    $("#client-pol-no").val(e.added.binPolNo);
                    $("#risk-binder").text(e.added.binName);
                    $("#pol-bind-age-appli").val(e.added.ageApplicable);
                    populateSubclassLov();

                },
                data: (data) ? data : [],
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
                multiple: ($("#pol-bin-type").val() && $("#pol-bin-type").val() === 'MP'),
                params: {
                    bindType: $("#pol-bin-type").val(),
                    productId: selectedProductId
                },
                placeholder: "Select Contract"

            });
        }
    };

    function createProductForSel() {
        if ($("#prd-code").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "prd-code",
                sort: 'proDesc',
                change: function (e, a, v) {
                    $("#prd-id").val(e.added.proCode);
                    populateBinderLov();
                },
                formatResult: function (a) {
                    return a.proDesc
                },
                formatSelection: function (a) {
                    return a.proDesc
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
            if (typeof polCode !== 'undefined') {
                if (polCode !== -2000) {
                    updatePolicy();
                } else {
                    createPolicy();
                }
            } else {
                createPolicy();
            }
        });

//        $("#btn-save-risk").click(function () {
//            if ($("#risk-code-pk").val() != '') {
//                updateRisk();
//            } else
//                createRisk();
//        });

        $("#btn-save-risk").click(function () {
            var $button = $(this); // Store reference to the button
            $button.prop("disabled", true); // Disable the button

            // Get transaction type from the hidden input field
            var transactionType = $("#pol-trans-type").val();

            // Validate negotiated premium for endorsements - Apply to ALL transaction types EXCEPT NB
            var endorsementType = sessionStorage.getItem('endorsementType');
            if (endorsementType && transactionType && transactionType !== 'NB') {
                var newNegotiatedPremium = parseFloat($("#overrid-prem").val()) || 0;
                var installmentTotalText = $("#pol-endos-gross-premium").text().replace(/[^0-9.-]+/g, "");
                var installmentTotal = parseFloat(installmentTotalText) || 0;

                console.log('Negotiated Premium:', newNegotiatedPremium, 'Installment Total:', installmentTotal, 'Endorsement:', endorsementType, 'Transaction Type:', transactionType);

                if (endorsementType === 'Upward revision' && newNegotiatedPremium <= installmentTotal) {
                    Swal.fire({
                        title: 'Invalid Negotiated Premium',
                        text: 'For upward endorsement, negotiated premium must be higher than Installment Total Policy Premium (' + installmentTotal.toFixed(2) + ')',
                        icon: 'error'
                    });
                    $button.prop("disabled", false);
                    return;
                }

                if (endorsementType === 'ENDDOWN' && newNegotiatedPremium >= installmentTotal) {
                    Swal.fire({
                        title: 'Invalid Negotiated Premium',
                        text: 'For downward endorsement, negotiated premium must be lower than Installment Total Policy Premium (' + installmentTotal.toFixed(2) + ')',
                        icon: 'error'
                    });
                   $button.prop("disabled", false);
                    return;
                }
            }

            // If validation passes, proceed with save
            if ($("#risk-code-pk").val() != '') {
                updateRisk().done(function() {
                   $button.prop("disabled", false);
                }).fail(function() {
                    $button.prop("disabled", false);
                });
            } else {
                createRisk().done(function() {
                   $button.prop("disabled", false);
                }).fail(function() {
                   $button.prop("disabled", false);
                });
            }
        });

//        $("#btn-make-ready-policy").click(function () {
//            updateMakeReadyPolicy("D");
//        });

        $("#btn-make-ready-policy").click(function () {
            // Initialize and validate
            var isValid = ButtonManager.handleValidationWorkflow(
                'btn-make-ready-policy',
                function() {
                    var $currForm = $('#policy-form');
                    return $currForm.valid();
                },
                'Submitting...'
            );

            if (!isValid) return;

            updateMakeReadyPolicy("D")
                .done(function() {
                    ButtonManager.handleSuccess('btn-make-ready-policy', 'Submitted', true);
                })
                .fail(function() {
                    ButtonManager.handleError('btn-make-ready-policy');
                });
        });

        $('#searchChecker').on('keyup', function () {
            loadEligibleCheckers($(this).val());
        });

        $('#assign-checker-submit').click(function () {
            // Check if any checkers are selected
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

            // Set processing state
            ButtonManager.setProcessing('btn-assign-trans', 'Assigning...');

            // Call the update function
            updateMakeReadyPolicy("RD");
        });

        $("#btn-assign-trans").click(function () {
            // Initialize button for modal workflow
            ButtonManager.initModalButton('btn-assign-trans', 'Loading...');
            loadEligibleCheckers('');
        });
        $("#btn-assign-trans2").click(function () {
            loadEligibleCheckers('');

        });
//        $("#btn-cancel-general-policy").click(function () {
//            submitPolicyCancellation();
//        });


        $("#btn-cancel-general-policy").click(function () {
            // Initialize button
            ButtonManager.initButton('btn-cancel-general-policy', {
                type: 'permanent',
                processingText: 'Submitting...',
                successText: 'Submitted'
            });

            if ($("#hasrefund").val() === 'Y') {
                $("#refund-amount").val($("#refund-amount1").val());
                $('#refundModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });

                // Setup modal workflow
                ButtonManager.handleModalWorkflow('btn-cancel-general-policy', '#refundModal');

                $("#proceedTorefund").off('click.cancelPolicy').on('click.cancelPolicy', function () {
                    $('#refundModal').modal('hide');
                    submitPolicyCancellationWithButtonHandling($("#refund-amount").val());
                });

                $("#stoprefund").off('click.cancelPolicy').on('click.cancelPolicy', function () {
                    $('#refundModal').modal('hide');
                    ButtonManager.handleError('btn-cancel-general-policy');
                });
            } else {
                submitPolicyCancellationWithButtonHandling();
            }
        });

// Helper function for cancellation with button handling
        function submitPolicyCancellationWithButtonHandling(refundAmount) {
            bootbox.confirm("Are you sure you want to submit this policy cancellation for approval?", function (result) {
                if (result) {
                    ButtonManager.setProcessing('btn-cancel-general-policy', 'Submitting...');

                    var cancelData = {
                        policyId: $("#remark-pol-id").val(),
                        remarks: $("#poli-remarks").val(),
                        cancelReasonId: $("#remark-pk").val(),
                        refundAmount: refundAmount
                    };

                    Swal.fire({
                        title: 'Processing...',
                        text: 'Please wait while we submit the transaction.',
                        allowOutsideClick: false,
                        allowEscapeKey: false,
                        didOpen: () => {
                            Swal.showLoading();
                        }
                    });

                    $.ajax({
                        type: 'POST',
                        url: SERVLET_CONTEXT + '/protected/uw/policies/submitPolicyCancellation',
                        contentType: 'application/json',
                        data: JSON.stringify(cancelData),
                        dataType: 'json',
                        success: function (result, textStatus, jqXHR) {
                            if (jqXHR.status === 204) {
                                Swal.fire({
                                    title: 'Submitted',
                                    text: 'Policy cancellation submitted successfully for approval.',
                                    icon: 'success'
                                }).then(() => {
                                    ButtonManager.handleSuccess('btn-cancel-general-policy', 'Submitted', true);
                                    window.location.href = SERVLET_CONTEXT + "/protected/home";
                                });
                            }
                        },
                        error: function (jqXHR) {
                            if (jqXHR.responseText.includes("Transaction marked as rollbackOnly")) {
                                Swal.fire({
                                    title: 'Error',
                                    text: 'Check the Cancellation Amount....Failed to submit policy cancellation.',
                                    icon: 'error'
                                });
                            } else {
                                Swal.fire({
                                    title: 'Error',
                                    text: jqXHR.responseText || 'Failed to submit policy cancellation.',
                                    icon: 'error'
                                });
                            }
                            ButtonManager.handleError('btn-cancel-general-policy');
                        }
                    });
                } else {
                    ButtonManager.handleError('btn-cancel-general-policy');
                }
            });
        }



        $("#btn-reprint-cert-policy").click(function () {
            generateCert();
        });


//        $("#btn-undo-make-ready").click(function () {
//            undoMakeReady();
//        });

        $("#btn-undo-make-ready").click(function () {
            // Initialize for modal workflow
            ButtonManager.initButton('btn-undo-make-ready', {
                type: 'modal',
                processingText: 'Processing...',
                successText: 'Unsubmitted'
            });

            ButtonManager.setLoading('btn-undo-make-ready', 'Loading...');

            // Fetch rejection reasons and show modal
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

                    // Setup modal workflow
                    ButtonManager.handleModalWorkflow('btn-undo-make-ready', '#rejectionModal', {
                        enableOnShow: true
                    });

                    $('#rejectionModal').modal('show');
                },
                error: function(jqXHR) {
                    Swal.fire({
                        title: 'Error',
                        text: 'Failed to load rejection reasons',
                        icon: 'error'
                    });
                    ButtonManager.handleError('btn-undo-make-ready');
                }
            });

            // Handle form submission
            $('#rejectConfirmButton').off('click.undoReady').on('click.undoReady', function() {
                var selectedReason = $('input[name="rejectionReason"]:checked').val();
                var comments = $('#rejectionReason').val().trim();

                if (!selectedReason || !comments) {
                    Swal.fire({
                        title: 'Error',
                        text: 'Please select a rejection reason and provide comments',
                        icon: 'error'
                    });
                    return;
                }

                ButtonManager.setProcessing('btn-undo-make-ready', 'Processing...');

                $.ajax({
                    type: 'GET',
                    url: 'undoMakeReady',
                    data: {
                        reasonId: selectedReason,
                        reason: comments
                    },
                    success: function() {
                        Swal.fire({
                            title: 'Success',
                            text: 'Submitted Successfully',
                            icon: 'success'
                        }).then(() => {
                            ButtonManager.handleSuccess('btn-undo-make-ready', 'Unsubmitted', true);
                            $('#rejectionModal').modal('hide');
                            window.location.href = SERVLET_CONTEXT + "/protected/home";
                        });
                    },
                    error: function(jqXHR) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                        ButtonManager.handleError('btn-undo-make-ready');
                    }
                });
            });
        });


        $('#btn-comment-policy').click(function () {
            commentPolicy();
        });
//        $("#btn-unconvert-policy").click(function () {
//            undoConvertToPolicy();
//        });

        $("#btn-unconvert-policy").click(function () {
            // Handle form modal workflow
            ButtonManager.handleFormModalWorkflow('btn-unconvert-policy', '#rejectionModal', {
                loadingText: 'Loading...'
            });

            undoConvertToPolicy();
        });




//        $("#btn-auth-policy").click(function () {
//             if ($("#hasrefund").val() === 'Y') {
//
//                 $("#refund-amount").val($("#refund-amount1").val());
//                $('#refundModal').modal({
//                    backdrop: 'static',
//                    keyboard: true,
//
//
//                });
//                $("#proceedTorefund").click(function () {
//                    if ($("#refund-amount").val() > $("#refund-amount1").val()) {
//                        bootbox.alert({
//                            message: "Refund Amount cannot be greater than " + $("#refund-amount1").val(),
//                            backdrop: false,
//                            closeButton: false
//                        });
//                        return;
//                    }
//
//                    $('#refundModal').modal('hide');
//                    authorizePolicy($("#refund-amount").val());
//                });
//                $("#stoprefund").click(function () {
//                    $('#refundModal').modal('hide');
//                    return;
//                });
//
//            } else {
//                authorizePolicy();
//            }
//
//        });


        $("#btn-auth-policy").click(function () {
            ButtonManager.setProcessing('btn-auth-policy', 'Authorizing...');
            authorizePolicy();
        });


        $("#btn-dispatch-trans").click(function () {
            dispatchDocuments();
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
        var $button = $("#btn-cancel-general-policy");

        bootbox.confirm("Are you sure you want to submit this policy cancellation for approval?", function (result) {
            if (result) {
                // Capture form values from #frm-pol-remarks
                var cancelData = {
                    policyId: $("#remark-pol-id").val(), // Maps to policyId
                    remarks: $("#poli-remarks").val(),  // Maps to remarks
                    cancelReasonId: $("#remark-pk").val(), // Maps to cancelReasonId (adjust if needed)
                     refundAmount: $("#refund-amount").val() // Uncomment and add field if needed
                };

                console.log(cancelData);

                Swal.fire({
                    title: 'Processing...',
                    text: 'Please wait while we submit the transaction.',
                    allowOutsideClick: false,
                    allowEscapeKey: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });

                $.ajax({
                    type: 'POST',
                    // url: 'submitPolicyCancellation',
                    url: SERVLET_CONTEXT + '/protected/uw/policies/submitPolicyCancellation',

                    contentType: 'application/json',
                    data: JSON.stringify(cancelData),
                    dataType: 'json',
                   // async: true,
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
                        if (jqXHR.responseText.includes("Transaction marked as rollbackOnly")) {
                            Swal.fire({
                                title: 'Error',
                                text: 'Check the Cancellation Amount....Failed to submit policy cancellation.',
                                icon: 'error'
                            });
                        } else {
                            Swal.fire({
                                title: 'Error',
                                text: jqXHR.responseText || 'Failed to submit policy cancellation.',
                                icon: 'error'
                            });
                        }

                        $button.prop("disabled", false);
                    }
                });
            }else {
                  // User clicked "No" or closed the modal
                  $button.prop("disabled", false);
            }
        });
    };

    var authorizePolicy = function (refundAmt) {
        // Show confirmation dialog
        bootbox.confirm("Are you sure you want to authorize this transaction?", function (result) {
            if (result) {
                Swal.fire({
                    title: 'Processing...',
                    text: 'Please wait while we authorize the transaction.',
                    allowOutsideClick: false,
                    allowEscapeKey: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });

                $.ajax({
                    type: 'GET',
                    url: 'authorizePolicy',
                    dataType: 'json',
                    data: {"refundAmt": refundAmt},
                    async: true,
                    success: function (result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Transaction Authorized Successfully',
                            icon: 'success'
                        });
                        ButtonManager.setPermanentSuccess('btn-auth-policy', 'Authorized');

                        window.location.href = SERVLET_CONTEXT + "/protected/home";
                        populatePolicyDetails();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });

                        // Reset button on error to allow retry
                        ButtonManager.handleError('btn-auth-policy');
                    }
                });
            } else {
                // User cancelled - reset button to original state
                ButtonManager.resetButton('btn-auth-policy');
            }
        });
    };


    var dispatchDocuments = function () {
        $.ajax({
            type: 'GET',
            url: 'dispatchDocs',
            dataType: 'json',
            async: true,
            success: function (result) {
                Swal.fire({
                    title: 'Success',
                    text: 'Document Dispatched Successfully',
                    icon: 'success'
                });
                populatePolicyDetails();
                $("#btn-dispatch-trans").hide();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                ;
            }
        });
    };

    var makeReady = function () {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        $.ajax({
            type: 'POST',
            url: 'makePolicyReady',
            dataType: 'json',
            async: true,
            data: JSON.stringify({}),
            success: function (result) {
                console.log(result);
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Submitted Successfully',
                    icon: 'success'
                });
                populatePolicyDetails();
                $('#polChecksList').DataTable().ajax.reload();
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

                // Enable button when modal is ready
                ButtonManager.enableForFormModal('btn-unconvert-policy', 'Reject');

                $('#rejectionModal').modal('show');
            },
            error: function(jqXHR) {
                Swal.fire({
                    title: 'Error',
                    text: 'Failed to load rejection reasons',
                    icon: 'error'
                });

                // Reset button on error
                ButtonManager.handleError('btn-unconvert-policy');
            }
        });

        $('#rejectConfirmButton').off('click.unconvert').on('click.unconvert', function() {
            var selectedReason = $('input[name="rejectionReason"]:checked').val();
            var comments = $('#rejectionReason').val().trim();

            if (!selectedReason) {
                Swal.fire({
                    title: 'Error',
                    text: 'Please select a rejection reason',
                    icon: 'error'
                });
                return; // Keep button enabled for retry
            }

            if (!comments) {
                Swal.fire({
                    title: 'Error',
                    text: 'Please provide additional comments',
                    icon: 'error'
                });
                return; // Keep button enabled for retry
            }

            // Set processing state when submitting
            ButtonManager.setProcessing('btn-unconvert-policy', 'Rejecting...');

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
                    Swal.fire({
                        title: 'Success',
                        text: 'Submitted Successfully',
                        icon: 'success'
                    }).then(() => {
                        // Set permanent success since we're redirecting
                        ButtonManager.setPermanentSuccess('btn-unconvert-policy', 'Rejected');

                        $('#rejectionModal').modal('hide');
                        populatePolicyDetails();
                        $('#polChecksList').DataTable().ajax.reload();
                        window.location.href = SERVLET_CONTEXT + "/protected/home";
                    });
                },
                error: function(jqXHR) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });

                    // Reset button on error to allow retry
                    ButtonManager.handleError('btn-unconvert-policy');
                }
            });
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
                    Swal.fire({
                        title: 'Success',
                        text: 'Submitted Successfully',
                        icon: 'success'
                    }).then(() => {
                        $('#rejectionModal').modal('hide');
                        window.location.href = SERVLET_CONTEXT + "/protected/home";
                        displayAuditTrails();
                    });
                },
                error: function(jqXHR) {
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
           return bootbox.alert("Cannot create Risk without sections", function () {
               $("#btn-save-risk").prop("disabled", false); // Re-enable button
           });
        }
        var $currForm = $('#risk-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            $("#btn-save-risk").prop("disabled", false); // Re-enable on validation failure
            return $.Deferred().reject().promise();
        }

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createRisk";
        data.sections = arr;

        $.ajax(
            {
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Risk Transaction created Successfully',
                        icon: 'success'
                    });

                    //  Re-enable New button after save
                    $("#btn-add-risk").prop("disabled", false);

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
            $("#btn-save-risk").prop("disabled", false); // Re-enable on validation failure
            return $.Deferred().reject().promise();
        }

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createRisk";
        $.ajax(
            {
                url: url,
                type: "POST",
                data: JSON.stringify(data),
                success: function (s) {
                    Swal.fire({
                        title: 'Success',
                        text: 'Policy Transaction created Successfully',
                        icon: 'success'
                    });

                    //  Re-enable New button after save
                    $("#btn-add-risk").prop("disabled", false);

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
                    $('#checkersList').append(markup);
                }

                // Enable button when modal is ready
                ButtonManager.enableForModal('btn-assign-trans', 'Assign');

                // Show modal with intelligent event handlers
                $('#assignCheckerModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });

                // Handle modal close events
                ButtonManager.handleModalWorkflow('btn-assign-trans', '#assignCheckerModal');

            },
            error: function (jqXHR, textStatus, errorThrown) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
                // Reset button on error
                ButtonManager.resetButton('btn-assign-trans');
            }
        });
    }

  var updateMakeReadyPolicy = function (status) {
      var $currForm = $('#policy-form');
      var currValidator = $currForm.validate();
      if (!$currForm.valid()) {
          return $.Deferred().reject().promise(); // Return rejected promise
      }

      $('#policy-form input[type=checkbox]').each(function (e) {
          $(this).val($(this).is(':checked'));
      });

      var data = {};
      $currForm.serializeArray().map(function (x) {
          data[x.name] = x.value;
      });

      // Include the comment from sessionStorage
      var comment = sessionStorage.getItem('policyComment_' + polCode);
      if (comment) {
          data.resubmissionComment = comment;
      }

      var selectedCheckers = [];
      if (status === "RD") {
          $('#checkersList tbody input.checker-checkbox:checked').each(function () {
              selectedCheckers.push($(this).val());
          });
          data.checkerIds = selectedCheckers;
          if (selectedCheckers.length === 0) {
              Swal.fire({
                  title: 'Warning',
                  text: 'Please select at least one checker to assign the task.',
                  icon: 'warning'
              });
              return $.Deferred().reject().promise(); // Return rejected promise
          }
      }

      var url = "createPolicyMakeReady";

      Swal.fire({
          title: 'Processing...',
          text: 'Please wait while the transaction is being submitted.',
          allowOutsideClick: false,
          allowEscapeKey: false,
          didOpen: () => {
              Swal.showLoading();
          }
      });

      // Return the jQuery promise
      return $.ajax({
          url: url,
          type: "POST",
          data: JSON.stringify(data),
          success: function (s) {
              // Clear comment from sessionStorage on success
              sessionStorage.removeItem('policyComment_' + polCode);

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
              polCode = s.policyId;
              populatePolicyDetails();
              console.log(s.status);
              $('#assignCheckerModal').modal('hide');
              if (s.status && s.status === 'N') {
                  bootbox.alert("Authorize Checks before proceeding....");
              }
              window.location.href = SERVLET_CONTEXT + "/protected/home";
              displayAuditTrails();
          },
          error: function (jqXHR, textStatus, errorThrown) {
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

    var generateCert = function () {
        var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
        var data = {};
        var url = "issueCertificate";
        //var url = "makePolicyReady";

        // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		});
        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            success: function (s) {
                console.log(s);

                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Certificate Issued Successfully',
                    icon: 'success'
                });
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

    var updatePolicy = function () {
        var $currForm = $('#policy-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return; // Don't change button state if form is invalid
        }

        // Only proceed with button state management if form is valid
        ButtonManager.setLoading('btn-add-policy', 'Updating...');

        $('#policy-form input[type=checkbox]').each(function (e) {
            $(this).val($(this).is(':checked'));
        });

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createPolicy";

        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            success: function (s) {
                Swal.fire({
                    title: 'Success',
                    text: 'Policy updated successfully',
                    icon: 'success'
                });
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
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            },
            complete: function() {
                // Always reset button state, whether success or error
                ButtonManager.resetButton('btn-add-policy');
            },
            dataType: "json",
            contentType: "application/json"
        });
    };

    var createPolicy = function () {
        var $currForm = $('#policy-form');
        var currValidator = $currForm.validate();
        if (!$currForm.valid()) {
            return; // Don't change button state if form is invalid
        }

        // Only proceed with button state management if form is valid
        ButtonManager.setLoading('btn-add-policy', 'Saving...');

        $('#policy-form input[type=checkbox]').each(function (e) {
            $(this).val($(this).is(':checked'));
        });

        var data = {};
        $currForm.serializeArray().map(function (x) {
            data[x.name] = x.value;
        });
        var url = "createPolicy";
        if (!$('#chk-import-risks').is(':checked')) {
            var riskForm = $("#risk-form");
            var riskValidator = riskForm.validate();
            if (!riskForm.valid()) {
                ButtonManager.resetButton('btn-add-policy');
                return;
            }
            var arr = getRskSections();
            if (arr.length == 0) {
                bootbox.alert("Cannot create policy without sections");
                ButtonManager.resetButton('btn-add-policy');
                return false;
            }
            if ($('#chk-pol-scheme').is(':checked')) {
                data.schemePolicy = 'Y';
            } else {
                data.schemePolicy = 'N';
            }

            data.sections = arr;
            data.riskBean = getRiskDetails();
            data.importRisks = false;
        } else {
            data.importRisks = true;
        }
        if ($("#pol-bin-type").val() && $("#pol-bin-type").val() === 'MP') {
            var arr = $("#binder-frm").val().split(",");
            var binddata = {
                "bindCodes": arr
            }
            $.extend(data, binddata);
            console.log(data);
        }
        console.log(data);

        $.ajax({
            url: url,
            type: "POST",
            data: JSON.stringify(data),
            success: function (s) {
                $(".risk-detail-tab").show();
                Swal.fire({
                    title: 'Success',
                    text: 'Policy Draft saved successfully', // Changed message
                    icon: 'success',
                    timer: 4000
                });
                window.location.href = SERVLET_CONTEXT + "/protected/uw/policies/edituwpolicy";
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
                console.log('Passed here...');
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText,
                    icon: 'error'
                });
            },
            complete: function() {
                // Always reset button state, whether success or error
                ButtonManager.resetButton('btn-add-policy');
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

    var getRiskBeneficiaries = function () {
        var url = "riskBeneficiaries/" + selRiskCode;
        var currTable = $('#risk_beneficiaries_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {"data": "fullName"},
                {"data": "occupation"},
                {
                    "data": "salary",
                    "render": function (data, type, full, meta) {
                        return UTILITIES.currencyFormat(full.salary);
                    }
                },
                {
                    "data": "bwbId",
                    "render": function (data, type, full, meta) {
                        return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-beneficiaries=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskBeneficiary(this);"><i class="fa fa-pencil-square-o"></button>';

                    }
                },
                {
                    "data": "bwbId",
                    "render": function (data, type, full, meta) {
                        if (polStatus) {
                            if (polStatus === '0')
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-beneficiaries=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskBeneficiary(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-beneficiaries=' + encodeURI(JSON.stringify(full)) + '  disabled><i class="fa fa-trash-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-beneficiaries=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskBeneficiary(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        }));
        return currTable;
    };

    var getRiskCertificates = function () {
        var url = "riskCerts/" + selRiskCode;
        var currTable = $('#cert_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "subclassCertTypes",
                    "render": function (data, type, full, meta) {
                        return full.subclassCertTypes.certType.certDesc;
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
                            } else return full.status;
                        }
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
                        if (polStatus) {
                            if (polStatus === '0')
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-certs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskCerts(this);"><i class="fa fa-pencil-square-o"></button>';
                        }
                    }

                },
                {
                    "data": "pcId",
                    "render": function (data, type, full, meta) {
                        if (polStatus) {
                            if (polStatus === '0')
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm btn-del" data-certs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskCerts(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm btn-del" data-certs=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskCerts(this);"><i class="fa fa-trash-o"></button>';
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

                        return full.desc;
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
                {"data": "divFactor"},
{"data": "freeLimit", "render": function(data, type, full, meta) { return `<input type='text' class='form-control' value='${full.freeLimit}' readonly>`; }},
                {
                    "data": "sectId",
                    "render": function (data, type, full, meta) {
                        if (polStatus) {
                            if (polStatus === '0')
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskSection(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '';

                        } else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskSection(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "sectId",
                    "render": function (data, type, full, meta) {
                        if (polStatus) {
                            if (polStatus === '0')
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm btn-del" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskSection(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm btn-del" data-risksections=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskSection(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        }));
        return currTable;
    };

    var deleteRiskParties = function (button) {
        var parties = JSON.parse(decodeURI($(button).data("parties")));
        bootbox.confirm("Are you sure want to delete " + parties['ipName'] + "?", function (result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url: 'deleteIntParties/' + parties['ipId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#interested_parties_tbl').DataTable().ajax.reload();

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
    };

    // var getScheduleDetails = function(){
    // 	var url = "vehicleDetails/"+selRiskCode;
    // 	var currTable = $('#schedule_details_tbl').DataTable(UTILITIES.extendsOpts({
    // 		"ajaxUrl":url,
    // 		"columns": [
    // 			{ "data": "bodyColor" },
    // 			{ "data": "bodyType" },
    // 			{ "data": "carMake" },
    // 			{ "data": "carModel" },
    // 			{ "data": "carryCapacity" },
    // 			{ "data": "ChassisNo" },
    // 			{ "data": "engineCapacity" },
    // 			{ "data": "engineNumber" },
    // 			{ "data": "logbookNumber" },
    // 			{ "data": "yearOfManufacture" },
    // 			{
    // 				"data": "riskId",
    // 				"render": function ( data, type, full, meta ) {
    // 					if(polStatus){
    // 						if(polStatus ==='0')
    // 							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskSection(this);"><i class="fa fa-pencil-square-o"></button>';
    // 						else
    // 							return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskSection(this);" disabled><i class="fa fa-pencil-square-o"></button>';
    //
    // 					}
    // 					else
    // 						return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editRiskSection(this);"><i class="fa fa-pencil-square-o"></button>';
    // 				}
    //
    // 			},
    // 			{
    // 				"data": "riskId",
    // 				"render": function ( data, type, full, meta ) {
    // 					if(polStatus){
    // 						if(polStatus ==='0')
    // 							return '<button type="button" class="btn btn-danger btn btn-info btn-sm btn-del" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskSection(this);"><i class="fa fa-trash-o"></button>';
    // 						else
    // 							return '<button type="button" class="btn btn-danger btn btn-info btn-sm btn-del" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskSection(this);" disabled><i class="fa fa-trash-o"></button>';
    //
    // 					}
    // 					else
    // 						return '<button type="button" class="btn btn-danger btn btn-info btn-sm btn-del" data-risksections='+encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskSection(this);"><i class="fa fa-trash-o"></button>';
    // 				}
    //
    // 			},
    // 		]
    // 	}) );
    //
    // 	$('#schedule_details_tbl tbody').on( 'click', 'tr', function () {
    // 		$(this).addClass('table-primary').siblings().removeClass('table-primary');
    // 		var d = currTable.row( this ).data();
    // 		if(d){
    // 			$("#schedule-pk-id").val(d.vdId);
    // 			$("#schedule-risk-id").val(d.riskId);
    // 			$("#chasisno").val(d.ChassisNo);
    // 			$("#engineno").val(d.engineNumber);
    // 			$("#body-type").val(d.bodyType);
    // 			$("#make").val(d.carMake);
    // 			$("#model").val(d.carModel);
    // 			$("#yom").val(d.yearOfManufacture);
    // 			$("#color").val(d.bodyColor);
    // 			$("#logbookno").val(d.logbookNumber);
    // 			$("#tonnage").val('');
    // 			$("#cubiccapacity").val(d.engineCapacity);
    // 			$("#cc").val(d.carryCapacity);
    // 		}
    // 	});
    // 	return currTable;
    // };

    var getRiskIntParties = function () {
        var url = "risksIntParties/" + selRiskCode;
        var currTable = $('#interested_parties_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "ipId",
                    "render": function (data, type, full, meta) {
                        return full.ipName;
                    }
                },
                {
                    "data": "ipId",
                    "render": function (data, type, full, meta) {
                        return full.ipType;
                    }
                },
                {
                    "data": "ipId",
                    "render": function (data, type, full, meta) {
                        return full.ipEmailAddress;
                    }
                },

                {
                    "data": "ipId",
                    "render": function (data, type, full, meta) {
                        if (full.polStatus) {
                            if (full.polStatus === "D" || full.polStatus === "RD")
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-parties=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskParties(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-parties=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskParties(this);" disabled><i class="fa fa-trash-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-parties=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskParties(this);"><i class="fa fa-trash-o"></button>';
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


    $(document).on('change', '.verify-checkbox', function () {
        const checkbox = this;
        const rdId = $(checkbox).data('id');
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
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            title: 'Error',
                            text: jqXHR.responseText,
                            icon: 'error'
                        });
                        checkbox.checked = false;

                    }
                });
            } else {
                checkbox.checked = false;
            }
        });
    }

    //var  createCertTable=function(policycode){
    //	var rows_selected = [];
    //	var table = $('#polCertsList').DataTable({
    //		"processing": true,
    //		"serverSide": true,
    //		autoWidth: true,
    //		"searching": false,
    //		"ajax": {
    //			'url': "getpolicyprintcerts",
    //			'data':{
    //				'polId':  policycode
    //			},
    //		},
    //		lengthMenu: [ [20, 40,60], [20, 40,60] ],
    //		pageLength: 20,
    //		destroy: true,
    //		'columnDefs': [{
    //			'targets': 7,
    //			'searchable':false,
    //			'orderable':false,
    //			'className': 'dt-body-center',
    //			'render': function (data, type, full, meta){
    //				//if(full.goodForPrint){
    //				//    if(full.goodForPrint ==='Y'){
    //				//        return '<input type="checkbox" checked>';
    //				//    }
    //				//    else
    //				//        return '<input type="checkbox">';
    //				//}else
    //				return '<input type="checkbox">';
    //			}
    //		}],
    //		'rowCallback': function(row, data, dataIndex){
    //			var rowId = data.cqId;
    //			if($.inArray(rowId, rows_selected) !== -1){
    //				$(row).find('input[type="checkbox"]').prop('checked', true);
    //				$(row).addClass('selected');
    //			}
    //		},
    //		"columns": [
    //
    //			{ "data": "risk",
    //				"render": function ( data, type, full, meta ) {
    //					return full.risk.riskShtDesc;
    //				}
    //			},
    //			{ "data": "policyCerts",
    //				"render": function ( data, type, full, meta ) {
    //					return moment(full.policyCerts.certWef).format('DD/MM/YYYY');
    //				}
    //			},
    //			{ "data": "policyCerts" ,
    //				"render": function ( data, type, full, meta ) {
    //					return moment(full.policyCerts.certWet).format('DD/MM/YYYY');
    //				}
    //			},
    //			{ "data": "policyCerts" ,
    //				"render": function ( data, type, full, meta ) {
    //					return moment(full.certWef).format('DD/MM/YYYY hh:mm:ss');
    //				}
    //			},
    //			{ "data": "status",
    //				"render": function ( data, type, full, meta ) {
    //
    //					return full.status;
    //				}
    //			},
    //			{ "data": "allocBy",
    //				"render": function ( data, type, full, meta ) {
    //					if(full.allocBy)
    //						return full.allocBy.username;
    //					else return "";
    //				}
    //			},
    //			{ "data": "certNo",
    //				"render": function ( data, type, full, meta ) {
    //
    //					return full.certNo;
    //				}
    //			},
    //			{ "data": "cqId" },
    //		]
    //	} );
    //
    //	// Handle click on checkbox
    //	$('#polCertsList tbody').on('click', 'input[type="checkbox"]', function(e){
    //		var $row = $(this).closest('tr');
    //
    //		// Get row data
    //		var data = table.row($row).data();
    //
    //		// Get row ID
    //		var rowId = data.cqId;
    //
    //		// Determine whether row ID is in the list of selected row IDs
    //		var index = $.inArray(rowId, rows_selected);
    //
    //		// If checkbox is checked and row ID is not in list of selected row IDs
    //		if(this.checked && index === -1){
    //			rows_selected.push(rowId);
    //
    //			// Otherwise, if checkbox is not checked and row ID is in list of selected row IDs
    //		} else if (!this.checked && index !== -1){
    //			rows_selected.splice(index, 1);
    //		}
    //
    //		if(this.checked){
    //			$row.addClass('selected');
    //		} else {
    //			$row.removeClass('selected');
    //		}
    //
    //		// Update state of "Select all" control
    //		//updateDataTableSelectAllCtrl(table);
    //
    //		// Prevent click event from propagating to parent
    //		e.stopPropagation();
    //	});
    //	$('#polCertsList').on('click', 'tbody td, thead th:first-child', function(e){
    //		$(this).parent().find('input[type="checkbox"]').trigger('click');
    //	});
    //
    //	//table.on('draw', function(){
    //	//    // Update state of "Select all" control
    //	//    updateDataTableSelectAllCtrl(table);
    //	//});
    //
    //	$('#btn-print').on('click', function(e){
    //		var arr = [];
    //		$.each(rows_selected, function(index, rowId){
    //			arr.push(rowId);
    //		});
    //
    //		if(arr.length==0){
    //			bootbox.alert("No Certificates Selected to Print..");
    //			return;
    //		}
    //
    //		var $currForm = $('#print-form');
    //		var currValidator = $currForm.validate();
    //		if (!$currForm.valid()) {
    //			return;
    //		}
    //		var data = {};
    //		$currForm.serializeArray().map(function(x) {
    //			data[x.name] = x.value;
    //		});
    //		data.certCodes = arr;
    //		var url = "printPolCertificate";
    //		$.ajax({
    //			url : url,
    //			type : "POST",
    //			data : JSON.stringify(data),
    //			success : function(s) {
    //
    //				printPdf(SERVLET_CONTEXT +"/protected/certs/printcert");
    //				bootbox.confirm({
    //					message: "Certificate Printed Successfully?",
    //					buttons: {
    //						confirm: {
    //							label: 'Yes',
    //							className: 'btn-success'
    //						},
    //						cancel: {
    //							label: 'No',
    //							className: 'btn-danger'
    //						}
    //					},
    //					callback: function (result) {
    //						if(result){
    //							$.ajax({
    //								type: 'GET',
    //								url:  'markPrintedPolCerts',
    //								dataType: 'json',
    //								async: true,
    //								success: function(result) {
    //									bootbox.alert("Receipt Printing operation complete");
    //									$('#polCertsList').DataTable().ajax.reload();
    //
    //								},
    //								error: function(jqXHR, textStatus, errorThrown) {
    //									new PNotify({
    //										title: 'Error',
    //										text: jqXHR.responseText,
    //										type: 'error',
    //										styling: 'bootstrap3'
    //									});
    //								}
    //							});
    //
    //						}
    //					}
    //				});
    //
    //				arr = [];
    //			},
    //			error : function(jqXHR, textStatus, errorThrown) {
    //				new PNotify({
    //					title: 'Error',
    //					text: jqXHR.responseText,
    //					type: 'error',
    //					styling: 'bootstrap3'
    //				});
    //			},
    //			dataType : "json",
    //			contentType : "application/json"
    //		});
    //
    //	});
    //
    //	$('#btn-deallocate').on('click', function(e){
    //
    //		var arr = [];
    //		$.each(rows_selected, function(index, rowId){
    //			arr.push(rowId);
    //		});
    //
    //		if(arr.length==0){
    //			bootbox.alert("No Certificates Selected to Deallocate..");
    //			return;
    //		}
    //
    //		//var $currForm = $('#print-cert-form');
    //		//var currValidator = $currForm.validate();
    //		//if (!$currForm.valid()) {
    //		//	return;
    //		//}
    //		var data = {};
    //		//$currForm.serializeArray().map(function(x) {
    //		//	data[x.name] = x.value;
    //		//});
    //		var url = "deallocateCerts";
    //		data.certs = arr;
    //		$.ajax({
    //			url : url,
    //			type : "POST",
    //			data : JSON.stringify(data),
    //			success : function(s) {
    //				new PNotify({
    //					title: 'Success',
    //					text: 'Certificate Allocation Successfully',
    //					type: 'success',
    //					styling: 'bootstrap3'
    //				});
    //				$('#cert_tbl').DataTable().ajax.reload();
    //				arr = [];
    //			},
    //			error : function(jqXHR, textStatus, errorThrown) {
    //				new PNotify({
    //					title: 'Error',
    //					text: jqXHR.responseText,
    //					type: 'error',
    //					styling: 'bootstrap3'
    //				});
    //			},
    //			dataType : "json",
    //			contentType : "application/json"
    //		});
    //
    //	});
    //
    //	$('#btn-allocate').on('click', function(e){
    //
    //		var arr = [];
    //		$.each(rows_selected, function(index, rowId){
    //			arr.push(rowId);
    //		});
    //
    //		if(arr.length==0){
    //			bootbox.alert("No Certificates Selected to Allocate..");
    //			return;
    //		}
    //
    //		//var $currForm = $('#print-cert-form');
    //		//var currValidator = $currForm.validate();
    //		//if (!$currForm.valid()) {
    //		//	return;
    //		//}
    //		var data = {};
    //		//$currForm.serializeArray().map(function(x) {
    //		//	data[x.name] = x.value;
    //		//});
    //		var url = "allocatePolCerts";
    //		data.certs = arr;
    //		$.ajax({
    //			url : url,
    //			type : "POST",
    //			data : JSON.stringify(data),
    //			success : function(s) {
    //				new PNotify({
    //					title: 'Success',
    //					text: 'Certificate Allocation Successfully',
    //					type: 'success',
    //					styling: 'bootstrap3'
    //				});
    //				$('#polCertsList').DataTable().ajax.reload();
    //				arr = [];
    //			},
    //			error : function(jqXHR, textStatus, errorThrown) {
    //				new PNotify({
    //					title: 'Error',
    //					text: jqXHR.responseText,
    //					type: 'error',
    //					styling: 'bootstrap3'
    //				});
    //			},
    //			dataType : "json",
    //			contentType : "application/json"
    //		});
    //
    //	});
    //	rows_selected = table.column(0).data();
    //	table.draw(false);
    //	return table;
    //}

    function getPolicyBinders(policyCode) {
        var url = "policyBinders/" + policyCode;
        var currTable = $('#policy-binders-tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {
                    "data": "binder",
                    "render": function (data, type, full, meta) {

                        return full.binder.account.name;
                    }
                },
                {
                    "data": "binder",
                    "render": function (data, type, full, meta) {

                        return full.binder.binShtDesc;
                    }
                },
                {
                    "data": "binder",
                    "render": function (data, type, full, meta) {

                        return full.binder.binName;
                    }
                },
                {
                    "data": "basicPrem",
                    "render": function (data, type, full, meta) {

                        return UTILITIES.currencyFormat(full.basicPrem);
                    }
                },

                {
                    "data": "policyBindId",
                    "render": function (data, type, full, meta) {
                        if (full.policyTrans.authStatus) {

                            if (full.policyTrans.authStatus === "D" || full.policyTrans.authStatus === "RD")
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolicyRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + '  disabled><i class="fa fa-pencil-square-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolicyRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                    }

                },
                {
                    "data": "policyBindId",
                    "render": function (data, type, full, meta) {
                        if (full.policyTrans.authStatus) {
                            if (full.policyTrans.authStatus === "D" || full.policyTrans.authStatus === "RD")
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + '  disabled><i class="fa fa-pencil-square-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRisk(this);"><i class="fa fa-pencil-square-o"></button>';

                    }

                },
            ]
        }));

        $('#policy-binders-tbl tbody').on('click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            if (aData[0] === undefined || aData[0] === null) {

            } else {
                $("#risk-bind-code").val(aData[0].binder.binId);
                $("#pol-binder-pk").val(aData[0].policyBindId);
                $("#risk-binder").text(aData[0].binder.binName);
                $("#insured-name").val(aData[0].policyTrans.client.fname);
                $("#insured-code").val(aData[0].policyTrans.client.tenId);
                $("#insured-other-name").val(aData[0].policyTrans.client.otherNames);
                $("#insured-ntl-id").val(aData[0].policyTrans.client.idNo);
                populateInsuredLov();
                getUWPolicyRisks(policyCode, aData[0].policyBindId);
                initiateRisk(9000);
            }
        });
    }

    function getUWPolicyRisks(policyCode, polBindCode) {

        polBindCode = typeof polBindCode !== 'undefined' ? polBindCode : -2000;
        var url = "policyRisks/" + policyCode + "/" + polBindCode;
        var currTable = $('#risk_tbl').DataTable(UTILITIES.extendsOpts({
            "ajaxUrl": url,
            "columns": [
                {"data": "riskShtDesc"},
                {"data": "riskDesc"},
                {
                    "data": "wefDate",
                    "render": function (data, type, full, meta) {
                        return full.startDate;
                    }
                },
                {
                    "data": "wetDate",
                    "render": function (data, type, full, meta) {
                        return full.endDate;
                    }
                },
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

                       if (full.transType === "RN") {
                           return '<button type="button" class="btn btn-success btn btn-info btn-sm" disabled><i class="fa fa-pencil-square-o"></i></button>';
                        }

                        if (full.authStatus) {

                            if (full.authStatus === "D" || full.authStatus === "RD" || full.authStatus === "R" || full.authStatus === "RCV" )
                                return '<button type="button" class="btn btn-primary btn btn-primary btn-sm btn-del" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolicyRisk(this);"><i class="fa fa-pencil-square-o"></button>';
                            else
                                return '';

                        } else
                            return '';
                    }

                },
                {
                    "data": "riskId",
                    "render": function (data, type, full, meta) {
                       if (full.transType === "RN"){
                            return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRisk(this);" disabled><i class="fa fa-trash-o"></button>';
                        }

                        if (full.authStatus) {
                            if (full.authStatus === "D" || full.authStatus === "RD" || full.authStatus === "R" || full.authStatus === "RCV")
                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm" data-policyrisks=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRisk(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '';

                        } else
                            return '';

                    }

                },
            ]
        }));

        $('#risk_tbl tbody').on('click', 'tr', function () {
            $(this).addClass('table-primary').siblings().removeClass('table-primary');
            var aData = currTable.rows('.table-primary').data();
            console.log('data', aData[0]);
            if (aData[0] === undefined || aData[0] === null) {

            } else {
                selRiskCode = aData[0].riskId;
                $("#risk-code-pk").val(selRiskCode);
                $("#ben-import-risk-id").val(selRiskCode);
                console.log(selRiskCode);
                getRiskSections();
                getRiskSchedules(selRiskCode);
                getRiskIntParties();
                getRiskDocs();
                getRefundDocs();
                getRiskCertificates();
                getRiskBeneficiaries();
                getClientDocs(aData[0].insuredId);
                $("#risk-det-id-pk").val(aData[0].binderDetId);
                populateRiskSections(aData[0].binderDetId);
                $("#cert-from-date").val(moment(aData[0].wefDate).format('DD/MM/YYYY'));
                $("#cert-wet-date").val(moment(aData[0].wetDate).format('DD/MM/YYYY'));
                if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
                    $.ajax({
                        type: 'GET',
                        url: 'getClientAge',
                        dataType: 'json',
                        data: {"clientId": aData[0].insured.tenId},
                        async: true,
                        success: function (result) {
                            $("#insured-client-age").val(result);
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

    var editRiskBeneficiary = function (button) {
        $('#salary').number(true, 2);
        var beneficiaries = JSON.parse(decodeURI($(button).data("beneficiaries")));
        $("#fullName").val(beneficiaries['fullName']);
        $("#occupation").val(beneficiaries['occupation']);
        $("#salary").val(beneficiaries['salary']);
        $("#risk-ben-code-pk").val(beneficiaries['riskId']);
        $("#risk-bencode-pk").val(beneficiaries['bwbId']);
        $('#beneficiaryModals').modal({
            backdrop: 'static',
            keyboard: true
        })
    }


    var deleteRiskBeneficiary = function (button) {
        var beneficiaries = JSON.parse(decodeURI($(button).data("beneficiaries")));
        bootbox.confirm("Are you sure want to delete " + beneficiaries['fullName'] + "?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deleteRiskBeneficiary/' + beneficiaries['bwbId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#risk_beneficiaries_tbl').DataTable().ajax.reload();

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


    var uploadRiskBeneficiries = function () {
        var $form = $("#benefits-upload-form");
        var validator = $form.validate();
        $('form#benefits-upload-form')
            .submit(function (e) {
                if ($("#ben-import-risk-id").val() === '') {
                    bootbox.alert('Select Risk to upload beneficiaries');
                    return;
                }
                e.preventDefault();
                if (!$form.valid()) {
                    return;
                }
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                var data = new FormData(this);
                data.append('file', $('#avatar')[0].files[0]);
                $.ajax({
                    url: 'importRiskBeneficiaries',
                    type: 'POST',
                    data: data,
                    processData: false,
                    contentType: false,
                    success: function (s) {
                        Swal.fire({
                            title: 'Success',
                            text: 'File Uploaded Successfully',
                            icon: 'success'
                        });
                        // $('#myPleaseWait').modal('hide');
                        $('#benefits-upload-form').find("input[type=file]").val("");
                        $('#risk_beneficiaries_tbl').DataTable().ajax.reload();
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

    var deleteRiskCerts = function (button) {
        var certs = JSON.parse(decodeURI($(button).data("certs")));
        bootbox.confirm("Are you sure want to delete " + certs['subclassCertTypes'].certType.certDesc + "?", function (result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url: 'deleteRiskCert/' + certs['pcId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#cert_tbl').DataTable().ajax.reload();
                        $('#polCertsList').DataTable().ajax.reload();

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
    };

    var allocateRiskCerts = function (button) {
        var certs = JSON.parse(decodeURI($(button).data("certs")));
        bootbox.confirm("Are you sure want to allocate " + certs['cert'].certLots.certTypes.certDesc + "?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'allocateRiskCert/' + certs['pcId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Certificate allocated Successfully',
                            icon: 'success'
                        });
                        $('#cert_tbl').DataTable().ajax.reload();
                        $('#polCertsList').DataTable().ajax.reload();

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
    var deallocateRiskCerts = function (button) {
        var certs = JSON.parse(decodeURI($(button).data("certs")));
        bootbox.confirm("Are you sure want to deallocate " + certs['cert'].certLots.certTypes.certDesc + "(" + +certs['certNo'] + ")?", function (result) {
            if (result) {
                // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
                $.ajax({
                    type: 'GET',
                    url: 'deallocateRiskCert/' + certs['pcId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        // $('#myPleaseWait').modal('hide');
                        Swal.fire({
                            title: 'Success',
                            text: 'Certificate Deallocated Successfully',
                            icon: 'success'
                        });
                        $('#cert_tbl').DataTable().ajax.reload();
                        $('#polCertsList').DataTable().ajax.reload();

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
        $("#risk-certtype-name").text(certs['subclassCertTypes'].certType.certDesc);
        $("#risk-cert-status").val(certs['status']);
        if (certs['certWef'])
            $("#risk-cert-from-date").val(moment(certs['certWef']).format('DD/MM/YYYY'));
        if (certs['certWet'])
            $("#risk-cert-wet-date").val(moment(certs['certWet']).format('DD/MM/YYYY'));
        if (certs['cancelDate'])
            $("#risk-canc-date").val(moment(certs['cancelDate']).format('DD/MM/YYYY'));
        //console.log("bbbbb" +moment(certs['cancelDate']).format('DD/MM/YYYY'))
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
        window.open('riskdocument/' + docs['rdId'], '_blank');
    };


    var deleteRiskDoc = function (button) {
        var docs = JSON.parse(decodeURI($(button).data("docs")));
        console.log(docs);
        bootbox.confirm("Are you sure want to delete " + docs['docShtDesc'] + "?", function (result) {
            if (result) {
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

//    var uploadRiskDocument = function () {
//        var $form = $("#risk-doc-form");
//        var validator = $form.validate();
//        $('form#risk-doc-form')
//            .submit(function (e) {
//                e.preventDefault();
//                if (!$form.valid()) {
//                    return;
//                }
//
//                var $submitButton = $('#upload-doc-btn');
//                var originalText = $submitButton.text();
//                $submitButton.prop('disabled', true).text('Uploading...');
//
//                var data = new FormData(this);
//                data.append('file', $('#avatar')[0].files[0]);
//                $.ajax({
//                    url: 'uploadRequiredDocs',
//                    type: 'POST',
//                    data: data,
//                    processData: false,
//                    contentType: false,
//                    success: function (s) {
//                        // $('#myPleaseWait').modal('hide');
//                        Swal.fire({
//                            title: 'Success',
//                            text: 'File Uploaded Successfully',
//                            icon: 'success'
//                        });
//                        $('#riskdocModal').modal('hide');
//                        var $el = $('#avatar');
//                        $el.wrap('<form>').closest('form').get(0).reset();
//                        $el.unwrap();
//                        refreshRiskDocsTable();
//                        $('#risk_docs_tbl').DataTable().ajax.reload();
//                    },
//                    error: function (xhr, error) {
//                        // $('#myPleaseWait').modal('hide');
//                        Swal.fire({
//                            title: 'Error',
//                            text: xhr.responseText,
//                            icon: 'error'
//                        });
//                    },
//                    complete: function() {
//                        $submitButton.prop('disabled', false).text(originalText);
//                    }
//                });
//            });
//    }

    var uploadRiskDocument = function () {
        var $form = $("#risk-doc-form");
        var validator = $form.validate();

        $('form#risk-doc-form').submit(function (e) {
            e.preventDefault();
            if (!$form.valid()) {
                return;
            }

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
                xhr: function () {
                    return new window.XMLHttpRequest(); // No progress tracking needed
                },
                success: function (s) {
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
                    Swal.fire({
                        title: 'Error',
                        text: xhr.responseText,
                        icon: 'error'
                    });
                },
                complete: function () {
                    // Reset UI
                    $submitButton.prop('disabled', false).text(originalText);
                    $('#upload-spinner').hide();
                }
            });
        });
    };


    var editRiskSection = function (button) {
        var section = JSON.parse(decodeURI($(button).data("risksections")));
        $("#sect-code-pk").val(section['sectId']);
        $("#sect-limit-amt").val(section['amount']);
        var sectionId = section['sectId'];
        if (!trulyOriginalSectionAmounts[sectionId]) {
            trulyOriginalSectionAmounts[sectionId] = section['amount'];
        }
        originalSectionAmount = trulyOriginalSectionAmounts[sectionId];
        endorsementType = sessionStorage.getItem('endorsementType');
        console.log('Editing section - Truly Original amount:', originalSectionAmount, 'Current amount:', section['amount'], 'Endorsement type:', endorsementType);
        $("#sect-rate").val(section['rate']);
        $("#sect-free-limit").val(section['freeLimit']);
        $("#sect-annual-earning").val(section['annualEarnings']);
        $("#sect-div-fact").val(section['divFactor']);
        $("#sect-multi-rate").val(section['multiRate']);
        //$("#chk-compute").prop("checked", section["compute"]);
        $("#risk-sect-id").val(section['section'].id);
        $("#risk-sect-name").val(section['section'].desc);
        $("#sect-prem-id-pk").val(section['premRates'].id);
        $("#risk-sect-code-pk").val(section['riskId']);
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
                $.ajax({
                    type: 'GET',
                    url: 'deleteRiskSection/' + section['sectId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#section_tbl').DataTable().ajax.reload();
                        populatePolicyDetails();
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

    var saveRiskBeneficiaries = function () {
        var $classForm = $('#risk-ben-form');
        var validator = $classForm.validate();
        $('#saveRiskBeneficiary').click(function () {
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
            var url = "saveRiskBeneficiary";
            var request = $.post(url, data);
            request.success(function () {
                // $('#myPleaseWait').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#risk_beneficiaries_tbl').DataTable().ajax.reload();
                validator.resetForm();
                $('#risk-ben-form').find("input[type=text],input[type=number],input[type=mobileNumber],input[type=emailFull],input[type=password],input[type=hidden], textarea,select").val("");
                $('#beneficiaryModals').modal('hide');
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

    var saveRiskSections = function () {
        var $classForm = $('#risk-sect-form');
        var validator = $classForm.validate();
        $('#saveRiskSection').click(function () {
            if (!$classForm.valid()) {
                return;
            }

            // Apply smart button management only after validation
            ButtonManager.setLoading('saveRiskSection', 'Saving...');

            // Get transaction type from the hidden input field
            var transactionType = $("#pol-trans-type").val();

            // Validate endorsement amount changes - Apply to ALL transaction types EXCEPT NB
            if (endorsementType && originalSectionAmount && transactionType && transactionType !== 'NB') {
                var newAmount = parseFloat($("#sect-limit-amt").val()) || 0;
                var originalAmount = parseFloat(originalSectionAmount) || 0; // This is now always the truly original amount

                console.log('Section validation - Transaction Type:', transactionType, 'Endorsement Type:', endorsementType, 'Truly Original Amount:', originalAmount, 'New Amount:', newAmount);

                // Block editing for certain endorsement types
                if (endorsementType === 'Cancellation' || endorsementType === 'NON-FINANCIAL' ||
                    endorsementType === 'EXT' || endorsementType === 'DATE') {
                    Swal.fire({
                        title: 'Editing Not Allowed',
                        text: 'Premium editing is not allowed for ' + endorsementType + ' endorsements.',
                        icon: 'warning'
                    });
                    ButtonManager.resetButton('saveRiskSection');
                    return;
                }

                // Validate upward endorsement
                if (endorsementType === 'Upward revision' && newAmount <= originalAmount) {
                    Swal.fire({
                        title: 'Invalid Amount',
                        text: 'For upward endorsement, amount must be higher than the original amount ('+ new Intl.NumberFormat('en-US').format(originalAmount) + ')',
                        icon: 'error'
                    });
                    ButtonManager.resetButton('saveRiskSection');
                    return;
                }

                // Validate downward endorsement
                if (endorsementType === 'ENDDOWN' && newAmount >= originalAmount) {
                    Swal.fire({
                        title: 'Invalid Amount',
                        text: 'For downward endorsement, amount must be lower than the original amount ('+ new Intl.NumberFormat('en-US').format(originalAmount) + ')',
                        icon: 'error'
                    });
                    ButtonManager.resetButton('saveRiskSection');
                    return;
                }
            }

            // Proceed with saving
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "saveRiskSections";

            $.ajax({
                url: url,
                type: "POST",
                data: data,
                success: function () {
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
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText || 'An error occurred',
                        icon: 'error'
                    });
                },
                complete: function() {
                    ButtonManager.resetButton('saveRiskSection');
                }
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
                            if (full.policy.authStatus === "D" || full.policy.authStatus === "RD") {
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
                            if ((full.policy.authStatus === "D" || full.policy.authStatus === "RD") && $("#pol-bin-type").val() === "M") {

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
                        if (full.binderRules) {
                            return full.binderRules.desc;
                        } else if (full.checks)
                            return full.checks.checkName;
                        else return "";
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
                            if (full.policy.authStatus === "D" || full.policy.authStatus === "RD") {
                                return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.editPolTaxes(this);"><i class="fa fa-pencil-square-o"></button>';
                            } else {
                                return '';
                            }

                        } else
                            return '';

                    }

                },
                {
                    "data": "polTaxId",
                    "render": function (data, type, full, meta) {
                        if (full.policy.authStatus) {
                            if (full.policy.authStatus === "D" || full.policy.authStatus === "RD") {

                                return '<button type="button" class="btn btn-danger btn btn-danger btn-sm btn-del" data-poltaxes=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deletePolTaxes(this);"><i class="fa fa-trash-o"></button>';
                            } else {
                                return '';
                            }

                        } else
                            return '';


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
        var tax = JSON.parse(decodeURI($(button).data("poltaxes")));
        bootbox.confirm("Are you sure want to delete " + UTILITIES.getRevDesc(tax["revenueItems"].item) + "?", function (result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url: 'deletePolTaxes/' + tax["polTaxId"],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#polTaxesList').DataTable().ajax.reload();
                        populatePolicyDetails();
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

    var getPolLevies = function (button) {
        $('#polTaxesList').DataTable({
            processing: true,
            serverSide: true,
            ajax: {
                url: '<c:url value="/protected/setups/taxes/taxRates"/>',
                type: 'GET',
                data: function(d) {
                    d.riskId = $('#risk-code-pk').val();
                    return d;
                }
            },
            columns: [
                {data: 'transCode'},
                {data: 'levyRate'},
                {data: 'divFactor'},
                {data: 'rateType'},
                {data: 'levyAmount'},
                {data: 'levyLevel'},
                {
                    data: null,
                    render: function(data, type, row) {
                        return '<button class="btn btn-info btn-xs edit-tax"><i class="fa fa-pencil"></i></button>';
                    }
                },
                {
                    data: null,
                    render: function(data, type, row) {
                        return '<button class="btn btn-danger btn-xs delete-tax"><i class="fa fa-trash"></i></button>';
                    }
                }
            ]
        });
    }


    function initiateRisk(multibinder) {
        multibinder = typeof multibinder !== 'undefined' ? multibinder : -2000;
        if (multibinder === -2000) {
            $("#insured-code").val("");
            $("#insured-name").val("");
            $("#insured-other-name").val("");
            $("#insured-ntl-id").val("");
            $('#insured-frm').select2('val', null);
        }

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

        $("#binder-id").val($("#risk-bind-code").val());
        $("#risk-binder-code-pk").val($("#pol-binder-pk").val());
        populateSubclassLov();
        var fromDate = $("#from-date").val();
        var toDate = getRiskWet(fromDate, $("#pol-frequency").val());
        $("#risk-wef-date").val(fromDate);
        $("#risk-wet-date").val(toDate);
        $("#section_form_tbl tbody").each(function () {
            $(this).remove();
        });
        $("#btn-add-new-section").hide();
    }

 //   var newRisk = function () {
//        $("#btn-add-risk").on('click', function () {
//            initiateRisk();
//            $("#risk-form").show();
//            $("#risk-div").hide();
//            $("#sect-div").hide();
//            $("#prem-rates-div").show();
//            $("#btn-save-risk").show();
//            $("#btn-save-cancel").show();
//
//            $("#myTab #show-taxes,#show-clauses").hide();
//        });

//        $("#btn-save-cancel").on('click', function () {
//            populatePolicyDetails();
//
//        })

    var newRisk = function () {
        $("#btn-add-risk").on('click', function () {
            // ButtonStateManager.greyOutButton('btn-add-risk');
            initiateRisk();
            $("#risk-form").show();
            $("#risk-div").hide();
            $("#sect-div").hide();
            $("#prem-rates-div").show();
            $("#btn-save-risk").show();
            $("#btn-save-cancel").show();
            $("#myTab #show-taxes,#show-clauses").hide();
        });


        $("#btn-save-risk, #btn-save-cancel").on('click', function () {
            $("#btn-add-risk").prop("disabled", false); // Re-enable after any of these buttons is clicked
        });

        $("#btn-save-cancel").on('click', function () {
            var $button = $(this);
            $button.prop("disabled", true); // Disable the button immediately
            populatePolicyDetails(); // Synchronous call
            $button.prop("disabled", false); // Re-enable after completion
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


// 	var saveRiskSchedules= function(){
// 		var $classForm = $('#new-risk-schedule-form');
// 		var validator = $classForm.validate();
// 		$('#saveRiskSchedules').click(function(){
// 			if (!$classForm.valid()) {
// 				return;
// 			}
// 			// // $('#myPleaseWait').modal({
// //			backdrop: 'static',
// //			keyboard: true
// //		})
//
// 			var $btn = $(this).button('Saving');
// 			var data = {};
// 			$classForm.serializeArray().map(function(x){data[x.name] = x.value;});
// 			var url = "saveRiskSchedule";
// 			var request = $.post(url, data );
// 			request.success(function(){
// 				// $('#myPleaseWait').modal('hide');
// 				Swal.fire({
//                 title: 'Success',
//                 text: 'Record created/updated Successfully',
//                 icon: 'success'
//             });
// 				$('#risk-sched_tbl').DataTable().ajax.reload();
// 				$('#section_tbl').DataTable().ajax.reload();
// 				populatePolicyDetails();
// 				validator.resetForm();
// 				$('#riskscheduleModal').modal('hide');
// 			});
//
// 			request.error(function(jqXHR, textStatus, errorThrown){
// 				// $('#myPleaseWait').modal('hide');
// 				Swal.fire({
//                 title: 'Error',
//                 text: jqXHR.responseText,
//                 icon: 'error'
//             });
// 			});
// 			request.always(function(){
// 				$btn.button('reset');
// 			});
// 		});
// 	}

    var deleteRisk = function (button) {
        var risks = JSON.parse(decodeURI($(button).data("policyrisks")));
        bootbox.confirm("Are you sure want to delete " + risks['riskShtDesc'] + "?", function (result) {
            if (result) {
                $.ajax({
                    type: 'GET',
                    url: 'deleteRisk/' + risks['riskId'],
                    dataType: 'json',
                    async: true,
                    success: function (result) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Record Deleted Successfully',
                            icon: 'success'
                        });
                        $('#risk_tbl').DataTable().ajax.reload();
                        getRiskSections(-2000);
                        getRiskIntParties();
                        getScheduleDetails();
                        getRiskDocs(-2000);
                        getRefundDocs(-2000);
                        $('#section_tbl').DataTable().ajax.reload();
                        getRiskCertificates(-2000);
                        getRiskBeneficiaries(-2000);
                        //getRiskSchedules(-2000);
                        //$("#risk-sched_tbl").DataTable().ajax.reload();
                        $('#cert_tbl').DataTable().ajax.reload();
                        $('#polCertsList').DataTable().ajax.reload();
                        populatePolicyDetails();
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

    var editPolicyRisk = function (button) {
        var risks = JSON.parse(decodeURI($(button).data("policyrisks")));
        console.log('risks ', risks);
        $("#risk-binder-code").val($("#risk-bind-code").val());
        $("#risk-id").val(risks["riskShtDesc"]);
        $("#risk-desc").val(risks["riskDesc"]);
        console.log('wef ', risks["wefDate"]);
        console.log('wet ', risks["wetDate"]);
        console.log('risk id ', risks["riskShtDesc"]);
        $("#risk-wef-date").val(moment(risks["wefDate"]).format('DD/MM/YYYY'));
        $("#risk-wet-date").val(moment(risks["wetDate"]).format('DD/MM/YYYY'));
        $("#prorated-full").val(risks["prorata"]);
        $("#comm-rate").val(risks["commRate"]);
        $("#sub-comm-rate").val(risks["subAgentCommRate"]);
        $("#marketer-comm-rate").val(risks["marketerCommRate"]);
        $("#overrid-prem").val(risks["butchargePrem"]);
       if (risks["autogenCert"]) {
            if (risks["autogenCert"] === "Y") {
                $("#chk-autogen").prop("checked", true);
            } else {
                $("#chk-autogen").prop("checked", false);
            }
        } else
            $("#chk-autogen").prop("checked", false);
        $("#insured-name").val(risks["fname"]);
        $("#insured-code").val(risks["tenId"]);
        $("#insured-other-name").val(risks["othernames"]);
        $("#insured-ntl-id").val();
        populateInsuredLov();
        $("#risk-sub-code").val(risks["subId"]);
        $("#sub-name").val(risks["subDesc"]);
        populateSubclassLov();

        $("#risk-cov-code").val(risks["covId"]);
        $("#cover-name").val(risks["covName"]);
        populateCoverTypesLov();
        $("#binder-det-id").val(risks["binderDetId"]);
        if (risks["polBindId"])
            $("#risk-binder-code-pk").val(risks["polBindId"]);
        $("#risk-code-pk").val(risks["riskId"]);
        $("#risk-trans-type").val(risks["transType"]);
        $("#risk-ident-code").val(risks["riskIdentifier"]);
        $("#install-perc").val(risks["installmentPerc"]);
        $("#install-amt").val(risks["installAmount"]);
        $("#install-no").val(risks["installmentNo"]);
        $("#risk-form").show();
        $("#risk-div").hide();
        $("#btn-save-risk").show();
        $("#btn-save-cancel").show();
        $("#myTab #show-taxes,#show-clauses").hide();
    }

    function getNewPremItems(sectdesc) {
        if ($("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
            $.ajax({
                type: 'GET',
                url: 'getNewClientPremiumItems',
                dataType: 'json',
                data: {
                    "detId": $("#risk-det-id-pk").val(),
                    "riskId": $("#risk-code-pk").val(),
                    "secName": sectdesc,
                    "age": $("#insured-client-age").val()
                },
                async: true,
                success: function (result) {
                    $("#new_prem_items_form tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td></tr>";
                        if ($("#pol-bin-type").val() === "B") {
                            if (result[res].section && result[res].section.type === "RD") {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" readonly>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
                            } else {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control">' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
                            }
                        } else {
                            if (result[res].ratesApplicable && result[res].ratesApplicable === "Y") {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" value="' + $("#insured-client-age").val() + '" readonly>' +
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
                    $("#new_prem_items_form tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in result) {
                        var markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                            "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                            "</td><td><input type='text' class='rate form-control' value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  value='" + result[res].divFactor + "'></td></td><td>" +
                            "<input type='text' class='freeLimit form-control'  value='" + result[res].freeLimit + "'></td></tr>";
                        if ($("#pol-bin-type").val() === "B") {
                            if (result[res].section && result[res].section.type === "RD") {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required readonly>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
                            } else {
                                markup = "<tr><td><input type='checkbox' class='section-check' id='" + result[res].id + "'></td><td><input type='hidden' class='section form-control' value='" + result[res].section.id +
                                    "'><input type='hidden' class='ratesApplicable form-control' value='" + result[res].ratesApplicable + "'><input type='hidden' class='premId form-control' value='" + result[res].id + "'>" + result[res].section.desc + "</td><td>" + ' <input type="text" class="amount form-control" required>' +
                                    "</td><td><input type='text' class='rate form-control' readonly value='" + result[res].rate + "'></td><td><input type='text' class='divFactor form-control'  readonly value='" + result[res].divFactor + "'></td></td><td>" +
                                    "<input type='text' class='freeLimit form-control' readonly  value='" + result[res].freeLimit + "'></td></tr>";
                            }
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
           // ButtonStateManager.greyOutButton('btn-add-new-section');
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
            ButtonStateManager.greyOutButton('btn-add-new-clause');
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
            ButtonStateManager.greyOutButton('btn-add-new-tax');
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
                console.log(result);
                // $('#myPleaseWait').modal('hide');
                $("#new_clause_tbl tbody").each(function () {
                    $(this).remove();
                });
                for (var res in result) {
                    var markup = "<tr><td><input type='checkbox' class='clause-check'><input type='hidden' class='clause-id form-control' value='" + result[res].clauseId +
                        "'></td><td>"
                        + result[res].clauseHeading + "</td></tr>";
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

//    var endorseriskModal = function () {
//        $("#btn-endors-risk").on('click', function () {
//            populateEndorsRisks(polCode);
//            createActiveRisksTbl(polCode);
//            $('#endorseRiskModal').modal({
//                backdrop: 'static',
//                keyboard: true
//            });
//        });
//
//        $("#btn-search-endos-risks").on('click', function () {
//            createActiveRisksTbl(polCode);
//        })
//    }

    var endorseriskModal = function () {
        $("#btn-endors-risk").on('click', function () {
            // Handle modal workflow for main button
            ButtonManager.handleFormModalWorkflow('btn-endors-risk', '#endorseRiskModal', {
                loadingText: 'Loading...'
            });

            populateEndorsRisks(polCode);
            createActiveRisksTblIntelligent(polCode);

            $('#endorseRiskModal').modal({
                backdrop: 'static',
                keyboard: true
            });

            // Enable button when modal is shown
            $('#endorseRiskModal').on('shown.bs.modal.endorse', function() {
                ButtonManager.enableForFormModal('btn-endors-risk', 'Select Risk');
            });
        });

        $("#btn-search-endos-risks").on('click', function () {
            // Handle search button intelligence
            var isValid = ButtonManager.handleValidationWorkflow(
                'btn-search-endos-risks',
                function() {
                    return true;
                },
                'Searching...'
            );

            if (!isValid) return;

            $('#endorserisktbl').off('click', '.editor_edit');
            createActiveRisksTblIntelligent(polCode);

            // Reset search button after completion
            setTimeout(function() {
                ButtonManager.resetButton('btn-search-endos-risks');
            }, 1000);
        });
    }



    var createCertypeLov = function (riskCode) {
        if ($("#risk-cert-div").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "risk-cert-div",
                sort: 'subclasscertId',
                change: function (e, a, v) {
                    $("#subcls-cert-id").val(e.added.subclasscertId);
                },
                formatResult: function (a) {
                    return a.certDesc;
                },
                formatSelection: function (a) {
                    return a.certDesc;
                },
                initSelection: function (element, callback) {

                },
                id: "subclasscertId",
                width: "250px",
                params: {riskId: riskCode},
                placeholder: "Select Cert Type"

            });
        }
    }

    //
    //var createCertypeLov= function(riskCode){
    //	if($("#risk-cert-div").filter("div").html() != undefined)
    //	{
    //		Select2Builder.initAjaxSelect2({
    //			containerId : "risk-cert-div",
    //			sort : 'brnCertId',
    //			change: function(e, a, v){
    //				$("#risk-cert-id").val(e.added.brnCertId);
    //			},
    //			formatResult : function(a)
    //			{
    //				return a.certLots.certTypes.certDesc;
    //			},
    //			formatSelection : function(a)
    //			{
    //				return a.certLots.certTypes.certDesc;
    //			},
    //			initSelection: function (element, callback) {
    //
    //			},
    //			id: "brnCertId",
    //			width:"250px",
    //			params: {riskId: riskCode},
    //			placeholder:"Select Cert Type"
    //
    //		});
    //	}
    //}


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
                $('#polCertsList').DataTable().ajax.reload();
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
            var $btn = $(this).button('Saving');
            var data = {};
            $classForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "saveRiskCertificate";
            var request = $.post(url, data);
            request.success(function () {
                Swal.fire({
                    title: 'Success',
                    text: 'Record created/updated Successfully',
                    icon: 'success'
                });
                $('#cert_tbl').DataTable().ajax.reload();
                $('#polCertsList').DataTable().ajax.reload();
                validator.resetForm();
                $('#riskCertModal').modal('hide');
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

    var newCertTypeModal = function () {
        $("#risk-cert-status").on('change', function () {
            if ($(this).val() === 'C') {
                $(".cert-cancellation").show();
            } else {
                $(".cert-cancellation").hide();
            }
        });


        $("#btn-add-new-cert").on("click", function () {
            ButtonStateManager.greyOutButton('btn-add-new-cert');
            if ($("#risk-code-pk").val() !== '') {
                $("#cert-risk-id").val($("#risk-code-pk").val());
                createCertypeLov($("#risk-code-pk").val());
                $('#riskCertModal').modal('show');

            } else {

                Swal.fire({
                    title: 'Error',
                    text: 'Select Risk to add Certificate',
                    icon: 'error'
                });
                return;
            }
        });

    }


    var populateEndorsRisks = function (policyCode) {
        if ($("#endos-insured-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "endos-insured-frm",
                sort: 'tenId',
                change: function (e, a, v) {
                    $("#endorse-insured-id").val(e.added.tenId);
                },
                formatResult: function (a) {
                    return a.fname;
                },
                formatSelection: function (a) {
                    return a.fname;
                },
                initSelection: function (element, callback) {

                },
                id: "tenId",
                width: "250px",
                params: {polCode: policyCode},
                placeholder: "Select Insured"

            });
        }
    }


    var createActiveRisksTblIntelligent = function (policyCode) {
        console.log('policy code ', policyCode);
        var url = "polactiverisks";
        var currTable = $('#endorserisktbl').DataTable({
            "processing": true,
            "serverSide": true,
            "ajax": {
                'url': url,
                'data': {
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
                        return full.riskShtDesc;
                    }
                },
                {
                    "data": "riskId",
                    "render": function (data, type, full, meta) {
                        return full.riskDesc;
                    }
                },
                {
                    "data": "riskId",
                    "render": function (data, type, full, meta) {
                        return moment(full.wefDate).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "riskId",
                    "render": function (data, type, full, meta) {
                        return moment(full.wetDate).format('DD/MM/YYYY');
                    }
                },
                {
                    "data": "riskId",
                    "render": function (data, type, full, meta) {
                        return '<select class="form-control" id="bin-type" name="binType" required> ' +
                            ' <option value="R">Revise</option> ' +
                            ' <option value="C">Cancel</option>' +
                            ' <option value="E">Extend</option>' +
                            ' <option value="S">Reinstate</option>' +
                            ' </select>';
                    }
                },
                {
                    "data": "riskId",
                    "render": function (data, type, full, meta) {
                        // Generate unique button ID for each row
                        var buttonId = 'endorse-btn-' + full.riskId;
                        return '<button class="editor_edit btn btn-primary" id="' + buttonId + '">Endorse</button>';
                    }
                },
            ]
        });

        // Handle individual endorse button clicks with intelligence
        $('#endorserisktbl').on('click', '.editor_edit', function (e) {
            var $button = $(this);
            var buttonId = $button.attr('id');
            var combo = $button.closest('tr').find("select");
            var data = currTable.row($button.closest('tr')).data();

            if (data === undefined || data === null) {
                return;
            }

            // Set processing state for this specific button
            ButtonManager.setProcessing(buttonId, 'Processing...');

            // Also grey out the main endorse risk button to prevent modal closure
            ButtonManager.setLoading('btn-endors-risk', 'Processing...');

            $("#myPleaseWait").css("z-index", "1500");
            $.ajax({
                type: 'GET',
                url: 'endorseRisk',
                data: {"activeRiskCode": data.riskId, "endorseType": combo.val()},
                dataType: 'json',
                async: true,
                success: function (result) {
                    // Success - permanently disable
                    ButtonManager.setPermanentSuccess(buttonId, 'Endorsed');

                    // Reset main button and close modal
                    ButtonManager.setPermanentSuccess('btn-endors-risk', 'Completed');

                    $('#endorserisktbl').DataTable().ajax.reload();
                    $('#risk_tbl').DataTable().ajax.reload();
                    populatePolicyDetails();
                    $("#endorseRiskModal").modal('hide');

                    // Show success message
                    Swal.fire({
                        title: 'Success',
                        text: 'Risk endorsed successfully',
                        icon: 'success'
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // Error - reset both buttons
                    ButtonManager.handleError(buttonId);
                    ButtonManager.enableForFormModal('btn-endors-risk', 'Select Risk');

                    bootbox.alert(jqXHR.responseText);
                }
            });
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
        var url = "getNewPolicyRemarks";
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

                // ADD THIS: Auto-save the selected remark and disable controls
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

                        $("#btn-add-new-remark").prop("disabled", true);
                        $("#btn-save-remark").prop("disabled", true);
                        $("#poli-remarks").prop("disabled", true);


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
    var getRiskSchedules = function (riskId) {
        $.ajax({
            url: SERVLET_CONTEXT + '/protected/uw/policies/getRiskSchedule',
            type: 'GET',
            data: {riskId: riskId},
            success: function (response) {
                console.log(response);
                columns = response.tableForm.columnFormList;
                scheddata = response.data;
                tableName = response.tableForm.datatableName;
                console.log(columns);
                renderTable();
            },
            error: function (error) {
                console.error(error);
            }
        });
    }

    var renderTable = function () {
        var table = $('#schedule_details_tbl tbody');
        table.empty();
        var headerRow = '<tr>';
        columns.forEach(function (col) {
            headerRow += '<th>' + col.name + '</th>';
        });
        headerRow += '<th width="5%"></th>' + '<th width="5%"></th>' + '</tr>';
        $('#schedule_details_tbl thead').html(headerRow);
        scheddata.forEach(function (item) {
            var row = '<tr>';
            columns.forEach(function (col) {
                row += '<td>' + (item[col.name] || '') + '</td>';
            });
            row += '</tr>';
            table.append(row);
        });

        if ($.fn.DataTable.isDataTable('#schedule_details_tbl')) {
            $('#schedule_details_tbl').DataTable().destroy();
        }
        $('#schedule_details_tbl').empty(); // Clear the table

        $('#schedule_details_tbl').DataTable({
            paging: true,
            searching: true,
            ordering: true,
            info: true,
            data: scheddata,
            columns: columns.map(function (col) {
                return {
                    title: col.name,
                    data: col.name,
                    visible: !(col.name === 's_brk_risks_id' || col.name === 'pri_code') // Hide specific columns
                };
            }).concat([
                {
                    title: '',
                    data: null,
                    render: function (data, type, row) {
                        return `<button type="button" class="btn btn-success btn-info btn-sm btn-edit" data-id="${row.s_brk_risks_id}" data-pri_code="${row.pri_code}">
                                <i class="fa fa-pencil-square-o"></i>
                            	</button>`;
                    }
                },
                {
                    title: '',
                    data: null,
                    render: function (data, type, row) {
                        return `<button class="btn btn-danger btn-sm btn-del" data-pri_code="${row.pri_code}">
                                <i class="fa fa-trash-o"></i>
                            	</button>`;
                    }
                }
            ])

        });

        $('#schedule_details_tbl').on('click', '.btn-edit', function () {
            var id = $(this).data('id');
            selPriCode = $(this).data('pri_code');
            editSchedule(id, selPriCode);
        });

        $('#schedule_details_tbl').on('click', '.btn-del', function () {
            var priCode = $(this).data('pri_code');
            deleteSchedule(tableName, priCode);
        });

    }

    var addNewRiskSchedule = function () {
        $("#btn-add-new-sched").on('click', function () {
            ButtonStateManager.greyOutButton('btn-add-new-sched');
            $("#schedule-risk-id").val($("#risk-code-pk").val());
            $("#schedule-pk-id").val("");
            generateDynamicFormFields();
            $('#riskscheduleModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        })
    }

    var generateDynamicFormFields = function () {
        var formContainer = $('#new-risk-schedule-form');
        formContainer.empty();

        columns.forEach(function (col) {
            if (col.name !== 's_brk_risks_id' && col.name !== 'pri_code') {
                var coltype = '';
                var isRequired = col.mandatory === 'YES' ? 'required' : '';
                var formGroup = '';

                if (col.type === 'bigint' || col.type === 'numeric') {
                    coltype = 'number';
                } else if (col.type === 'date') {
                    coltype = 'date';
                } else if (col.type === 'boolean') {
                    // Create radio buttons for boolean fields
                    formGroup = `
                    <div class="item form-group m-b-10">
                        <label class="col-md-3 label-align">${col.name} <span class="text-danger">${isRequired ? '*' : ''}</span></label>
                        <div class="col-md-9">
                            <input type="radio" name="${col.name}" value="true" ${isRequired}> True
                            <input type="radio" name="${col.name}" value="false" ${isRequired}> False
                        </div>
                    </div>
                `;
                } else {
                    coltype = 'text';
                }

                if (!formGroup) { // If not a boolean (radio), build the normal input
                    formGroup = `
                    <div class="item form-group m-b-10">
                        <label class="col-md-3 label-align">${col.name} <span class="text-danger">${isRequired ? '*' : ''}</span></label>
                        <div class="col-md-9">
                            <input type="${coltype}" class="form-control" id="${col.name.replace(/\s+/g, '-').toLowerCase()}" name="${col.name}" ${isRequired}>
                        </div>
                    </div>
                `;
                }

                formContainer.append(formGroup);
            }
        });
    }

    var editSchedule = function (id, priCode) {
        var scheduleData = scheddata.find(item => item.s_brk_risks_id === id && item.pri_code === priCode);
        $('#schedule-risk-id').val(scheduleData.s_brk_risks_id);
        selPriCode = scheduleData.pri_code;
        console.log(scheduleData.pri_code)
        generateDynamicFormFields();
        columns.forEach(function (col) {
            if (col.name !== 's_brk_risks_id' && col.name !== 'pri_code') {
                var fieldId = $("#" + col.name.replace(/\s+/g, '-').toLowerCase());
                if (fieldId.length) {
                    fieldId.val(scheduleData[col.name] || '');
                }
            }
        });
        $('#riskscheduleModal').modal({
            backdrop: 'static',
            keyboard: true
        });
    }

    var deleteSchedule = function (tableName, priCode) {
        console.log(priCode);
        var scheduleData = scheddata.find(item => item.pri_code === priCode);
        console.log(scheduleData.pri_code);
        bootbox.confirm("Are you sure you want to delete this Schedule?", function (result) {
            if (result) {
                $.ajax({
                    type: 'POST',
                    url: 'deleteScheduleData',
                    data: JSON.stringify({tableName: tableName, priCode: scheduleData.pri_code}),
                    contentType: 'application/json',
                    async: true,
                    success: function (response) {
                        Swal.fire({
                            title: 'Success',
                            text: response,
                            icon: 'success'
                        });
                        getRiskSchedules(selRiskCode);
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


    var saveRiskSchedules = function () {
        console.log('entering the function')
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
                var col = columns.find(col => col.name === x.name);

                if (col && col.type === 'bigint') {
                    data[x.name] = parseInt(x.value);
                } else {
                    data[x.name] = x.value;
                }
            });
            data.pri_code = selPriCode;
            data.s_brk_risks_id = selRiskCode;

            var url = "saveScheduleData";
            //var request = $.post(url, data );
            var request = $.ajax({
                url: url,
                type: 'POST',
                data: JSON.stringify({tableName: tableName, scheduleData: data}),
                contentType: 'application/json',
                dataType: 'json',
                success: function (response) {
                    Swal.fire({
                        title: 'Success',
                        text: response,
                        icon: 'success'
                    });
                    console.log(request);
                    getRiskSchedules(data.s_brk_risks_id);
                    validator.resetForm();
                    $('#riskscheduleModal').modal('hide');
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // $('#myPleaseWait').modal('hide');
                    console.log("Error Details: " + jqXHR)
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
            request.always(function () {
                $btn.button('reset');
            });
        });
    }


    var getCommissionRate = function (binCode) {
        $.ajax({
            type: 'GET',
            url: 'getCommissionRate',
            dataType: 'json',
            data: {"binId": binCode},
            async: true,
            success: function (result) {
                $("#comm-rate").val(result);
                console.log(result);
            },
            error: function (jqXHR, textStatus, errorThrown) {

            }
        });
    }

    var getSubAgentCommissionRate = function (binCode, accId) {
        $.ajax({
            type: 'GET',
            url: 'getSubAgentCommissionRate',
            dataType: 'json',
            data: {"binId": binCode, "accId": accId},
            async: true,
            success: function (result) {
                $("#sub-comm-rate").val(result);
            },
            error: function (jqXHR, textStatus, errorThrown) {

            }
        });
    }

    var getMarketerCommissionRate = function (binCode, accId) {
        $.ajax({
            type: 'GET',
            url: 'getMarketerCommissionRate',
            dataType: 'json',
            data: {"binId": binCode, "accId": accId},
            async: true,
            success: function (result) {
                $("#marketer-comm-rate").val(result);
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

    var getNewIntParties = function () {
        $("#btn-add-new-ips").click(function () {
            ButtonStateManager.greyOutButton('btn-add-new-ips');
            if ($("#risk-code-pk").val() != '') {
                $("#new-part-risk-id").val($("#risk-code-pk").val());
                getNewInterestedParties($("#risk-code-pk").val());
                $('#intPartiesModal').modal({
                    backdrop: 'static',
                    keyboard: true
                });
            } else {
                bootbox.alert("Select Risk to Add Interested Parties")
            }
        });

        $("#saveNewIntParties").click(function () {
            var items = getCreateNewIntParties();
            if (items.length == 0) {
                bootbox.alert("No Interested Party Selected")
                return;
            }
            var $currForm = $('#new-intparties-form');
            var currValidator = $currForm.validate();
            if (!$currForm.valid()) {
                return;
            }
            var data = {};
            $currForm.serializeArray().map(function (x) {
                data[x.name] = x.value;
            });
            var url = "createNewIntParties";

            data.parties = items;
            $.ajax(
                {
                    url: url,
                    type: "POST",
                    data: JSON.stringify(data),
                    success: function (s) {
                        Swal.fire({
                            title: 'Success',
                            text: 'Selected Interested Parties Added Successfully',
                            icon: 'success'
                        });
                        $('#interested_parties_tbl').DataTable().ajax.reload();
                        $('#intPartiesModal').modal('hide');
                        items = [];
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
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


    var getNewInterestedParties = function (riskId) {
        $.ajax({
            type: 'GET',
            url: 'getNewIntParties/' + riskId,
            dataType: 'json',
            async: false,
            success: function (result) {
                $("#interested_party_tbl tbody").each(function () {
                    $(this).remove();
                });
                for (var res in result) {
                    var markup = "<tr><td><input type='checkbox' class='int-check'><input type='hidden' class='int-part-id form-control' value='" + result[res].partCode +
                        "'></td><td>"
                        + result[res].partName + "</td><td>"
                        + result[res].partType + "</td>"
                        + "<td>" + result[res].emailAddress + "</td>"
                    "</tr>";
                    $("#interested_party_tbl").append(markup);
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
            }
        });

    }


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
//        $("#btn-import-risk").on('click', function () {
//            populateImportCoverTypesLov();
//            $('#importRisksModal').modal({
//                backdrop: 'static',
//                keyboard: true
//            });
//        });

        $("#btn-import-risk").on('click', function () {
            //ButtonStateManager.greyOutButton('btn-import-risk');
            populateImportCoverTypesLov();
            $('#importRisksModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        });

        // // Re-enable #btn-import-risk when the modal is hidden
        // $('#importRisksModal').on('hidden.bs.modal', function () {
        //     $("#btn-import-risk").prop("disabled", false); // Re-enable after modal closes
        // });

        var $form = $("#risks-upload-form");
        var validator = $form.validate();
        $('form#risks-upload-form')
            .submit(function (e) {
                e.preventDefault();
                if (!$form.valid()) {
                    return;
                }
                // $("#btn-import-risk").prop('disabled', true);

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
                        $('#risk-form').hide();
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

    var creditTrans = function () {
        var url = "debitreceipts";
        var currTable = $('#polReceipts')
            .DataTable(
                {
                    "processing": true,
                    "serverSide": true,
                    "ajax": {
                        'url': url
                    },
                    lengthMenu: [[10], [10]],
                    pageLength: 10,
                    destroy: true,
                    "columns": [
                        {
                            "data": "receiptNo",
                            "render": function (data, type, full, meta) {
                                return full.receiptNo;
                            }
                        },
                        {
                            "data": "receiptDate",
                            "render": function (data, type, full, meta) {
                                return moment(full.receiptDate).format('DD/MM/YYYY');
                            }
                        },
                        {
                            "data": "receiptAmount",
                            "render": function (data, type, full, meta) {
                                return UTILITIES.currencyFormat(full.receiptAmount);
                            }
                        },

                    ]
                });

        return currTable;

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
                    console.log(result)
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
                Swal.fire({
                    title: 'Error',
                    text: 'No Documents Selected to attach..',
                    icon: 'error'
                });
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
                    refreshRiskDocsTable();
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
    var getClientAge = function () {
        if ($("#insured-code").val() && $("#pol-bind-age-appli").val() && $("#pol-bind-age-appli").val() === "Y") {
            $.ajax({
                type: 'GET',
                url: 'getClientAge',
                dataType: 'json',
                data: {"clientId": $("#insured-code").val()},
                async: true,
                success: function (result) {
                    console.log('Msee ako ' + result + ' Yrs Old');
                    $("#insured-client-age").val(result);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                }
            });
        }
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
                {
                    "data": "relationshipType",
                    "render": function (data, type, full, meta) {
                        return full.relationshipType.relationDesc;
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
                        if (full.policy.authStatus) {
                            if (full.policy.authStatus === "D")
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-parties=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskParties(this);"><i class="fa fa-trash-o"></button>';
                            else
                                return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-parties=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskParties(this);" disabled><i class="fa fa-trash-o"></button>';

                        } else
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-parties=' + encodeURI(JSON.stringify(full)) + ' onclick="UWScreen.deleteRiskParties(this);"><i class="fa fa-trash-o"></button>';
                    }

                },
            ]
        }));
        return currTable;
    };

    var init = function () {
        $(document).ajaxStart(function () {
            $("#btn-dispatch-trans,#btn-auth-policy,#btn-add-policy,#btn-make-ready-policy,#btn-undo-make-ready," +
                "#btn-save-remark,#btn-save-risk,#saveNewIntParties,#btn-dispatch-trans,.btn-del,#saveRiskCert,#saveRiskSection," +
                "#savenewPremItem,#saveRiskDocsBtn,#savepoltaxesBtn,#upload-doc-btn,#savenewClause,#btn-save-remark,#btn-reprint-cert-policy").prop("disabled", true);
        });
        $(document).ajaxComplete(function () {
            $("#btn-dispatch-trans,#btn-auth-policy,#btn-add-policy,#btn-make-ready-policy,#btn-undo-make-ready,#btn-save-remark," +
                "#btn-save-risk,#saveNewIntParties,#btn-dispatch-trans,.btn-del,#saveRiskCert,#saveRiskSection," +
                "#savenewPremItem,#saveRiskDocsBtn,#savepoltaxesBtn,#upload-doc-btn,#savenewClause,#btn-save-remark,#btn-reprint-cert-policy").prop("disabled", false);
        });
        console.log($("#pol-interface-type").val());
        $("#pol-interface-type").on('change', function () {
            if ($(this).val() === "A") {
                $("#disp-inst-date").show();
                $("#disp-accrual-type").show();
            } else {
                $("#disp-inst-date").hide();
                $("#disp-accrual-type").hide();
            }
        });
        $(".motor-disp").show();
        $(".non-motor-disp").hide();
        populatePolicyDetails();
        checkAndDisableRemarkControls();
        createProductForSel();
        $('#overrid-prem').number(true, 2);
        $('#install-amt').number(true, 2);
        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });

        });
        $(".multi-product-uw").css("display", "none");
        populateClientLov();
        populateBinderLov();
        $("#comm-rate").attr("readonly", "false");
        $("#sub-comm-rate").attr("readonly", "false");
        $("#marketer-comm-rate").attr("readonly", "false");
        $(".chkimport-risks").show();
        $(".chk-pol-scheme").show();
        $(".risk-detail-tab").show();
        $(".bind-insurance").show();
        $(".multi-product").css("display", "none");
        $(".multi-product-uw").css("display", "none");
        // $("#pol-bin-type").on('change', function(){
        //
        // 	if($(this).val()==="B"){
        // 		$("#comm-rate").attr("readonly", "true");
        // 		$(".chkimport-risks").show();
        // 		$(".risk-detail-tab").show();
        // 		$(".bind-insurance").show();
        // 		$(".multi-product").css("display","none");
        // 		$(".multi-product-uw").css("display","none");
        // 	}
        // 	else if($(this).val()==="MP"){
        // 		$(".risk-detail-tab").hide();
        // 		$("#comm-rate").attr("readonly", "true");
        // 		$(".chkimport-risks").hide();
        // 		$(".bind-insurance").hide();
        // 		$(".multi-product").css("display","block");
        // 		$(".multi-product-uw").css("display","none");
        // 		populateMultiProduct();
        // 	}
        // 	else{
        // 		// populateBinderLov();
        // 		// $("#comm-rate").attr("readonly", "false");
        // 		// $(".chkimport-risks").show();
        // 		// $(".risk-detail-tab").show();
        // 		// $(".bind-insurance").show();
        // 		// $(".multi-product").css("display","none");
        // 		// $(".multi-product-uw").css("display","none");
        //
        // 	}
        //
        // });

        $("#pol-interface-type").on('change', function () {
            // if ($(this).val() === "A") {
            //     $(".installment").hide();
            // } else {
                $(".installment").show();
            // }
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
                //$(".risk-detail-tab").hide();
                $(".prem-cert-sched").hide();
                $("#risk-form").hide();
                $("#myTab").show();
                $("#btn-import-risk").show();
                $("#risk-alert-template").show();
            } else {
                // $(".risk-detail-tab").show();
                $(".prem-cert-sched").show();
                $("#risk-form").show();
                $("#myTab").hide();
                $("#btn-import-risk").hide();
                $("#risk-alert-template").hide();
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

        $("#risk-cert-status").on('change', function () {
            if ($(this).val() === "C") {
                var d = new Date();
                $("#risk-canc-date").val(moment(d).format('DD/MM/YYYY'));
            } else {
                $("#risk-canc-date").val("");
            }
        })

        $("#email-to").on('change', function () {
            if ($(this).val() == 'C') {
                $(".motor-disp").hide();
                $(".non-motor-disp").hide();
            } else {

            }
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

            if ($(this).val() === "IN") {
                $("#email-cc").attr("readonly", true);
                $("#email-send-to").attr("readonly", false);
                $("#email-send-to").val("");
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
                $("#email-cc").val("");
            }
        });

        $('#email-template-type').on('change', function () {
            if ($(this).val()) {
                UTILITIES.sendEmail($(this).val());
            }

        });

        $('#pol-frequency').on('change', function () {
            $('#wef-date').trigger('dp.change');
        })

        populateCurrencyLov();
        populateSubAgentsLov();
        populateIntroducerLov();
        populateMarketerLov();
        populateLeadsManLov();
        // populatePaymentModes();
        populateUserBranches();
        populateInsuredLov();
        populateSubclassLov();
        populateCoverTypesLov();
        uploadRiskBeneficiries();
        //$("#other-pol-details").hide();
        getPolicyWet();
        changePolicyWetDt();
        createNewPolicy();
        saveRiskSections();
        saveRiskBeneficiaries();
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
        createAccrualPolicy();
        //validateRisk();
        //createCertTable();
        // UTILITIES.createAssignee();
        UTILITIES.emailReports();
        UTILITIES.smsReports();
        uploadRiskDocument();
        getNewIntParties();
        fillQuestionnaire();
        importuWRisks();
        saveRiskDocsList();
        printCerts.printCerts();
        creditTrans();

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

        $("#btn-add-beneficiary").click(function () {
            //ButtonStateManager.greyOutButton('btn-add-beneficiary');
            if ($("#risk-code-pk").val() != '') {
                $('#salary').number(true, 2);
                $("#risk-ben-code-pk").val($("#risk-code-pk").val());
                $('#beneficiaryModals').modal({
                    backdrop: 'static',
                    keyboard: true
                })
            }
        });


        $("#btn-add-docs").click(function () {
            // ButtonStateManager.greyOutButton('btn-add-docs');
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

        $("#btn-add-del-sched").on('click', function () {

        });

        $("#btn-add-edit-sched").on('click', function () {
            if ($("#schedule-pk-id").val() !== '') {
                $('#riskscheduleModal').modal('show');
            } else {
                bootbox.alert("Select Schedule to Edit Details");
                return;
            }
        })


    }

    return {
        init: init,
        editRiskCerts: editRiskCerts,
        deleteRiskCerts: deleteRiskCerts,
        editRiskBeneficiary: editRiskBeneficiary,
        deleteRiskBeneficiary: deleteRiskBeneficiary,
        allocateRiskCerts: allocateRiskCerts,
        deallocateRiskCerts: deallocateRiskCerts,
        editRiskSection: editRiskSection,
        deleteRiskSection: deleteRiskSection,
        deleteRiskParties: deleteRiskParties,
        editRiskDocs: editRiskDocs,
        downloadRiskDoc: downloadRiskDoc,
        deleteRiskDoc: deleteRiskDoc,
        editPolicyRisk: editPolicyRisk,
        deleteRisk: deleteRisk,
        editPolicyClause: editPolicyClause,
        deletePolicyClause: deletePolicyClause,
        authChecks: authChecks,
        editPolTaxes: editPolTaxes,
        deletePolTaxes: deletePolTaxes,
        downloadClientDoc: downloadClientDoc,
        viewPremLimits: viewPremLimits,
        getPolLevies: getPolLevies
    }


})(jQuery, PrintCerts);

jQuery(UWScreen.init);
