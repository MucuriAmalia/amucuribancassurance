/**
 * Created by peter on 2/14/2017.
 */

$(function () {

    $(document).ready(function () {

        $(".datepicker-input").each(function () {
            $(this).datetimepicker({
                format: 'DD/MM/YYYY'
            });
        });


    });
});


var reportModule = (function () {

    function createAccountsForSel() {
        if ($("#acc-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "acc-frm",
                sort: 'name',
                change: function (e, a, v) {
                    $("#acc-id").val(e.added.acctId);
                },
                formatResult: function (a) {
                    return a.name
                },
                formatSelection: function (a) {
                    return a.name
                },
                initSelection: function (element, callback) {

                },
                id: "acctId",
                placeholder: "All Agents",
            });
        }
        $("#acc-frm").on("select2-removed", function (e) {
            $("#acc-id").val('');
        });
    }

    var populateSubAgentsLov = function () {
        if ($("#sub-agent-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "sub-agent-frm",
                sort: 'name',
                change: function (e, a, v) {
                    $("#sub-agent-id").val(e.added.acctId);

                },
                formatResult: function (a) {
                    return a.name;
                },
                formatSelection: function (a) {
                    return a.name;
                },
                initSelection: function (element, callback) {

                },
                id: "acctId",
                width: "250px",
                placeholder: "Select Sub Agent"

            });

            $("#sub-agent-frm").on("select2-removed", function (e) {
                $("#sub-agent-id").val('');
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

                },
                formatResult: function (a) {
                    return a.name;
                },
                formatSelection: function (a) {
                    return a.name;
                },
                initSelection: function (element, callback) {

                },
                id: "acctId",
                width: "250px",
                placeholder: "Select Introducer"

            });

            $("#introducer-frm").on("select2-removed", function (e) {
                $("#introducer-id").val('');
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

                },
                formatResult: function (a) {
                    return a.name;
                },
                formatSelection: function (a) {
                    return a.name;
                },
                initSelection: function (element, callback) {

                },
                id: "acctId",
                width: "250px",
                placeholder: "Select Marketer"

            });

            $("#marketer-frm").on("select2-removed", function (e) {
                $("#marketer-id").val('');
            })
        }
    };

    var populateRegionLov = function () {
        if ($("#region-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "region-frm",
                sort: 'regDesc',
                change: function (e, a, v) {
                    $("#region-id").val(e.added.regCode);

                },
                formatResult: function (a) {
                    return a.regDesc;
                },
                formatSelection: function (a) {
                    return a.regDesc;
                },
                initSelection: function (element, callback) {

                },
                id: "regCode",
                width: "250px",
                placeholder: "Select Region"

            });

            $("#region-frm").on("select2-removed", function (e) {
                $("#region-id").val('');
            })
        }
    };

    var populateTaskTypeLov = function () {
        if ($("#task-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "task-frm",
                sort: 'taskType',
                change: function (e, a, v) {
                    $("#task-id").val(e.added.taskName);

                },
                formatResult: function (a) {
                    return a.taskType;
                },
                formatSelection: function (a) {
                    return a.taskType;
                },
                initSelection: function (element, callback) {

                },
                id: "taskName",
                width: "250px",
                placeholder: "Select Task Type"

            });

            $("#task-frm").on("select2-removed", function (e) {
                $("#task-id").val('');
            })
        }
    };

    var populateCheckersLov = function () {
        if ($("#checker-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "checker-frm",
                sort: 'checkedBy',
                change: function (e, a, v) {
                    $("#checker-id").val(e.added.checkerId);

                },
                formatResult: function (a) {
                    return a.checkedBy;
                },
                formatSelection: function (a) {
                    return a.checkedBy;
                },
                initSelection: function (element, callback) {

                },
                id: "checkerId",
                width: "250px",
                placeholder: "Select Checker"

            });

            $("#checker-frm").on("select2-removed", function (e) {
                $("#checker-id").val('');
            })
        }
    };

    var populateActivityLov = function () {
        if ($("#activity-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "activity-frm",
                sort: 'activityDesc',
                change: function (e, a, v) {
                    console.log("Selected activity causation:", e.added.activityDesc);
                    $("#activity-type").val(e.added.activityDesc);
                },
                formatResult: function (a) {
                    return a.activityDesc; // Display activityDesc in dropdown
                },
                formatSelection: function (a) {
                    return a.activityDesc; // Show activityDesc in selection
                },
                initSelection: function (element, callback) {},
                id: "caId", // Use caId as the unique identifier
                width: "250px",
                placeholder: "Select Activity"
            });

            $("#activity-frm").on("select2-removed", function (e) {
                $("#activity-type").val(''); // Clear hidden input when selection is removed
            });
        }
    };

// var populateActivityLov = function () {
//        if ($("#activity-frm").filter("div").html() != undefined) {
//            Select2Builder.initAjaxSelect2({
//                containerId: "activity-frm",
//                sort: 'activityDesc',
//                change: function (e, a, v) {
//                    $("#activity-type").val(e.added.activityDesc);
//
//                },
//                formatResult: function (a) {
//                    return a.activityDesc;
//                },
//                formatSelection: function (a) {
//                    return a.activityDesc;
//                },
//                initSelection: function (element, callback) {
//
//                },
//                id: "activityDesc",
//                width: "250px",
//                placeholder: "Select Activity"
//
//            });
//
//            $("#activity-frm").on("select2-removed", function (e) {
//                $("#activity-type").val('');
//            })
//        }
//    };


    function populateClientLov() {
        if ($("#client-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "client-frm",
                sort: 'fname',
                change: function (e, a, v) {
                    $("#client-id").val(e.added.tenId);
                },
                formatResult: function (a) {
                    return a.fname + " " + a.otherNames + " (CIF: " + (a.cif || '') + ", Phone: " + (a.phone || '') + ", KRA PIN: " + (a.pinNo || '') +")";
                },
                formatSelection: function (a) {
                    return a.fname + " " + a.otherNames + " (CIF: " + (a.cif || '') + ", Phone: " + (a.phone || '') + ", KRA PIN: " + (a.pinNo || '') + ")";
                },
                initSelection: function (element, callback) {
                },
                id: "tenId",
                placeholder: "All Clients"
            });
        }
        $("#client-frm").on("select2-removed", function (e) {
            $("#client-id").val('');
        });
    }

    function populateProspectLov() {
        if ($("#prospect-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "prospect-frm",
                sort: 'fname',
                change: function (e, a, v) {
                    $("#prospect-id").val(e.added.tenId);

                },
                formatResult: function (a) {
                    return a.fname + " " + a.otherNames;
                },
                formatSelection: function (a) {
                    return a.fname + " " + a.otherNames;
                },
                initSelection: function (element, callback) {
                },
                id: "tenId",
                placeholder: "All Prospects"

            });
        }
        $("#prospect-frm").on("select2-removed", function (e) {
            $("#prospect-id").val('');
        });
    }

    function createUserLov() {
        if ($("#report-user").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "report-user",
                sort: 'name',
                change: function (e, a, v) {
                    $("#userCod").val(e.added.id);
                },
                formatResult: function (a) {
                    return a.name
                },
                formatSelection: function (a) {
                    return a.name
                },
                initSelection: function (element, callback) {

                },
                id: "id",
                placeholder: "All Users"
            });
        }
        $("#report-user").on("select2-removed", function (e) {
            $("#userCod").val('');
        });
    }

    function createReportPolicies() {
        if ($("#report-policies").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "report-policies",
                sort: 'polNo',
                change: function (e, a, v) {
                    $("#policy-code").val(e.added.policyId);
                },
                formatResult: function (a) {
                    return "Policy: " + a.polNo + "  Revision No: " + a.polRevNo;
                },
                formatSelection: function (a) {
                    return "Policy: " + a.polNo + "  Revision No: " + a.polRevNo;
                },
                initSelection: function (element, callback) {

                },
                id: "policyId",
                placeholder: "Select Policy"
            });
        }
        $("#report-policies").on("select2-removed", function (e) {
            $("#policy-code").val('');
        });
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

                },
                id: "obId",
                width: "250px",
                placeholder: "All Branches"

            });
        }
        $("#brn-frm").on("select2-removed", function (e) {
            $("#brn-id").val('');
        });
    }

    function populatePaymentModes() {
        if ($("#PM-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "PM-frm",
                sort: 'pmDesc',
                change: function (e, a, v) {
                    $("#PM-id").val(e.added.pmId);
                },
                formatResult: function (a) {
                    return a.pmDesc;
                },
                formatSelection: function (a) {
                    return a.pmDesc;
                },
                initSelection: function (element, callback) {

                },
                id: "pmId",
                width: "250px",
                placeholder: "All Payment Modes"

            });
        }
        $("#PM-frm").on("select2-removed", function (e) {
            $("#PM-id").val('');
        });
    }

    function createCertTypeLov() {
        if ($("#cert-type").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "cert-type",
                sort: 'certDesc',
                change: function (e, a, v) {
                    $("#cert-type-pk").val(e.added.certId);
                },
                formatResult: function (a) {
                    return a.certDesc
                },
                formatSelection: function (a) {
                    return a.certDesc
                },
                initSelection: function (element, callback) {

                },
                id: "certId",
                placeholder: "All Certificate Types"
            });
        }
        $("#cert-type").on("select2-removed", function (e) {
            $("#cert-type-pk").val('');
        });
    }

    function populateBinderLov() {
        if ($("#binder-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "binder-frm",
                sort: 'binName',
                change: function (e, a, v) {
                    $("#risk-binder-code").val(e.added.binId);
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
                placeholder: "All Binders/Masks"

            });
        }
        $("#binder-frm").on("select2-removed", function (e) {
            $("#risk-binder-code").val('');
        });
    }

    function createProductForSel() {
        if ($("#prd-code").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "prd-code",
                sort: 'proDesc',
                change: function (e, a, v) {
                    $("#product-code").val(e.added.proCode);
                },
                formatResult: function (a) {
                    return a.proDesc
                },
                formatSelection: function (a) {
                    return a.proDesc
                },
                initSelection: function (element, callback) {

                },
                id: "proCode",
                placeholder: "All Products",
            });
        }

        $("#prd-code").on("select2-removed", function (e) {
            $("#product-code").val('');
        })
    }

    function createRemmitanceForSel() {
        if ($("#remit-def").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "remit-def",
                sort: 'refNo',
                change: function (e, a, v) {
                    $("#remit-code").val(e.added.transno);
                },
                formatResult: function (a) {
                    return a.refNo
                },
                formatSelection: function (a) {
                    return a.refNo
                },
                initSelection: function (element, callback) {

                },
                id: "transno",
                placeholder: "All Remmitances",
            });
        }

        $("#remit-def").on("select2-removed", function (e) {
            $("#remit-code").val('');
        })
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

                },
                id: "curCode",
                width: "250px",
                placeholder: "Select Currency"

            });
        }
        $("#curr-frm").on("select2-removed", function (e) {
            $("#cur-id").val('');
        })
    }
function callReportModal(button) {
    var reportCode = decodeURI($(button).data("report"));
    var template = decodeURI($(button).data("template"));
    $.ajax({
        type: 'GET',
        url: 'getReportParams',
        dataType: 'json',
        data: {"rptId": reportCode},
        async: true,
        success: function (result) {
            $("div.data-inf > div").each(function () {
                $(this).remove();
            });
            var counter = 1;
            result.forEach(function (data) {
                var markup = '<div class="col-md-12">';
                markup += '<label for="rp-param-id" class="col-md-4 label-align">' + data.paramName + '</label>';
                markup += '<div class="item form-group report-data">';
                markup += '<input type="hidden" value="' + data.paramActualName + '" name="paramName' + counter + '" class="paramName">';
                if (data.paramType === 'T') {
                    markup += '<input type="text" class="editUserCntrls form-control paramValue" name="paramValue' + counter + '" id="' + data.paramActualName + '" required>';
                } else if (data.paramType === "O") {
                    var arr = data.options.split(",");
                    var opts = "";
                    for (var x = 0; x < arr.length; x++) {
                        opts += ' <option value="' + arr[x] + '">' + arr[x] + '</option> ';
                    }
                    markup += ' <select class="editUserCntrls form-control paramValue" name="paramValue' + counter + '" id="' + data.paramActualName + '" required> ' +
                        ' <option value="">Select Option Value</option> ' + opts +
                        ' </select> ';
                } else if (data.paramType === 'N') {
                    markup += '<input type="number" class="editUserCntrls form-control paramValue" name="paramValue' + counter + '" id="' + data.paramActualName + '" required>';
                } else if (data.paramType === 'D') {
                    markup += "<div class='input-group date datepicker-input'> " +
                        " <input type='text' name='paramValue" + counter + "' class='form-control float-right paramValue' " +
                        "id='" + data.paramActualName + "' required /> " +
                        "  <div class='input-group-addon'> " +
                        "      <span class='fa fa-calendar'></span> " +
                        "     </div> " +
                        "     </div>";
                } else if (data.paramType === 'L') {
                    if (data.lovName === 'A') {
                        markup += '<input type="hidden" id="acc-id" name="paramValue' + counter + '"> <input type="hidden" value="A" name="paramType' + counter + '">' +
                            '  <div id="acc-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/accounts" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'C') {
                        markup += '<input type="hidden" id="client-id" name="paramValue' + counter + '"><input type="hidden" value="C" name="paramType' + counter + '"> ' +
                            '  <div id="client-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/clients" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'CR') {
                        markup += '<input type="hidden" id="cur-id" name="paramValue' + counter + '"> ' +
                            '  <div id="curr-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/uwcurrencies" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'U') {
                        markup += '<input type="hidden" id="userCod" name="paramValue' + counter + '"> ' +
                            '  <div id="report-user" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/users" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'P') {
                        markup += '<input type="hidden" id="policy-code" name="paramValue' + counter + '"> ' +
                            '  <div id="report-policies" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/policies" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'R') {
                        markup += '<input type="hidden" id="remit-code" name="paramValue' + counter + '"> ' +
                            '  <div id="remit-def" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/remmittances" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'PR') {
                        markup += '<input type="hidden" id="product-code" name="paramValue' + counter + '"> ' +
                            '  <div id="prd-code" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/setups/binders/selproducts" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'B') {
                        markup += '<input type="hidden" id="brn-id" name="paramValue' + counter + '"> ' +
                            '  <div id="brn-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/uwbranches" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'PM') {
                        markup += '<input type="hidden" id="PM-id" name="paramValue' + counter + '"> ' +
                            '  <div id="PM-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/uwpaymentmodes" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'BI') {
                        markup += '<input type="hidden" id="risk-binder-code" name="paramValue' + counter + '"> ' +
                            '  <div id="binder-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/uwBinders" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'CT') {
                        markup += '<input type="hidden" id="cert-type-pk" name="paramValue' + counter + '"> ' +
                            '  <div id="cert-type" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/certs/selCertTypes" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'SA') {
                        markup += '<input type="hidden" id="sub-agent-id" name="paramValue' + counter + '"> ' +
                            '  <div id="sub-agent-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/inhouseagents" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'PRO') {
                        markup += '<input type="hidden" id="prospect-id" name="paramValue' + counter + '"> ' +
                            '  <div id="prospect-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/prospect" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'INT') {
                        markup += '<input type="hidden" id="introducer-id" name="paramValue' + counter + '"> ' +
                            '  <div id="introducer-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/introducergents" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'MRK') {
                        markup += '<input type="hidden" id="marketer-id" name="paramValue' + counter + '"> ' +
                            '  <div id="marketer-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/marketeragents" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'REG') {
                        markup += '<input type="hidden" id="region-id" name="paramValue' + counter + '"> ' +
                            '  <div id="region-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/branchregions" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'TASK') {
                        markup += '<input type="hidden" id="task-id" name="paramValue' + counter + '"> ' +
                            '  <div id="task-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/tasktype" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'CHECKER') {
                        markup += '<input type="hidden" id="checker-id" name="paramValue' + counter + '"> ' +
                            '  <div id="checker-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/checkedby" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'ACTIVITY') {
                        markup += '<input type="hidden" id="activity-type" name="paramValue' + counter + '"> ' +
                            '  <div id="activity-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/claims/selclmActivity" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    }
                }
                markup += '</div></div>';

                $(".data-inf").append(markup);
                counter += 1;
            });
            $(".data-inf").append('<div><input type="hidden" value="' + template + '" name="reportCode"></div>');

            // Initialize datepickers
            $(".datepicker-input").each(function () {
                $(this).datetimepicker({
                    format: 'DD/MM/YYYY',
                    widgetParent: '#printReportModal .modal-body'
                }).on('dp.show', function() {
                    $('.bootstrap-datetimepicker-widget').css('z-index', 1050);
                });
            });

            $('#printReportModal .modal-body').on('click', function(e) {
                var container = $(this);
                var scrollBarWidth = 20; // Approximate scroll bar width in pixels
                var clickX = e.pageX - container.offset().left;
                var containerWidth = container.width();
                if (clickX > containerWidth - scrollBarWidth) {
                    e.stopPropagation();
                    e.preventDefault();
                }
            });

            var style = document.createElement('style');
            style.innerHTML = `
                .data-inf::-webkit-scrollbar {
                    width: 12px;
                }
                .data-inf::-webkit-scrollbar-track {
                    background: #f1f1f1;
                }
                .data-inf::-webkit-scrollbar-thumb {
                    background: #888;
                    border-radius: 6px;
                }
                .data-inf::-webkit-scrollbar-thumb:hover {
                    background: #555;
                }
            `;
            document.head.appendChild(style);



            // Adjust modal width based on number of fields
            var fieldCount = result.length;
            var modalWidth = Math.min(800, 300 + (fieldCount * 250));
            $('#printReportModal .modal-dialog').css('width', modalWidth + 'px');

            // Initialize LOVs
            createAccountsForSel();
            populateClientLov();
            populateProspectLov();
            createUserLov();
            createProductForSel();
            populateBinderLov();
            createCertTypeLov();
            populateUserBranches();
            populatePaymentModes();
            createReportPolicies();
            populateCurrencyLov();
            createRemmitanceForSel();
            populateSubAgentsLov();
            populateIntroducerLov();
            populateMarketerLov();
            populateRegionLov();
            populateTaskTypeLov();
            populateCheckersLov();
            populateActivityLov();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("Error fetching report parameters:", errorThrown);
        }
    });
    $('#printReportModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}

function callStretchyReportModal(button) {
    var reportCode = decodeURI($(button).data("report"));
    var template = decodeURI($(button).data("template"));
    $.ajax({
        type: 'GET',
        url: 'getStretchyReportParams',
        dataType: 'json',
        data: {"strId": reportCode},
        async: true,
        success: function (result) {
            $("div.data-inf > div").each(function () {
                $(this).remove();
            });
            var counter = 1;
            result.forEach(function (data) {
                var markup = '<div class="col-md-12">';
                markup += '<label for="rp-param-id" class="col-md-4 label-align">' + data.paramName + '</label>';
                markup += '<div class="item form-group report-data">';
                markup += '<input type="hidden" value="' + data.paramActualName + '" name="paramName' + counter + '" class="paramName">';
                if (data.paramType === 'T') {
                    markup += '<input type="text" class="editUserCntrls form-control paramValue" name="paramValue' + counter + '" id="' + data.paramActualName + '" required>';
                } else if (data.paramType === "O") {
                    var arr = data.options.split(",");
                    var opts = "";
                    for (var x = 0; x < arr.length; x++) {
                        opts += ' <option value="' + arr[x] + '">' + arr[x] + '</option> ';
                    }
                    markup += ' <select class="editUserCntrls form-control paramValue" name="paramValue' + counter + '" id="' + data.paramActualName + '" required> ' +
                        ' <option value="">Select Option Value</option> ' + opts +
                        ' </select> ';
                } else if (data.paramType === 'N') {
                    markup += '<input type="number" class="editUserCntrls form-control paramValue" name="paramValue' + counter + '" id="' + data.paramActualName + '" required>';
                } else if (data.paramType === 'D') {
                    markup += "<div class='input-group date datepicker-input'> " +
                        " <input type='text' name='paramValue" + counter + "' class='form-control float-right paramValue' " +
                        "id='" + data.paramActualName + "' required /> " +
                        "  <div class='input-group-addon'> " +
                        "      <span class='fa fa-calendar'></span> " +
                        "     </div> " +
                        "     </div>";
                } else if (data.paramType === 'L') {
                    if (data.lovName === 'A') {
                        markup += '<input type="hidden" id="acc-id" name="paramValue' + counter + '"> <input type="hidden" value="A" name="paramType' + counter + '">' +
                            '  <div id="acc-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/accounts" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'C') {
                        markup += '<input type="hidden" id="client-id" name="paramValue' + counter + '"><input type="hidden" value="C" name="paramType' + counter + '"> ' +
                            '  <div id="client-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/clients" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'CR') {
                        markup += '<input type="hidden" id="cur-id" name="paramValue' + counter + '"> ' +
                            '  <div id="curr-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/uwcurrencies" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'U') {
                        markup += '<input type="hidden" id="userCod" name="paramValue' + counter + '"> ' +
                            '  <div id="report-user" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/users" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'P') {
                        markup += '<input type="hidden" id="policy-code" name="paramValue' + counter + '"> ' +
                            '  <div id="report-policies" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/policies" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'R') {
                        markup += '<input type="hidden" id="remit-code" name="paramValue' + counter + '"> ' +
                            '  <div id="remit-def" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/remmittances" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'PR') {
                        markup += '<input type="hidden" id="product-code" name="paramValue' + counter + '"> ' +
                            '  <div id="prd-code" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/setups/binders/selproducts" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'B') {
                        markup += '<input type="hidden" id="brn-id" name="paramValue' + counter + '"> ' +
                            '  <div id="brn-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/uwbranches" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'PM') {
                        markup += '<input type="hidden" id="PM-id" name="paramValue' + counter + '"> ' +
                            '  <div id="PM-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/uwpaymentmodes" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'BI') {
                        markup += '<input type="hidden" id="risk-binder-code" name="paramValue' + counter + '"> ' +
                            '  <div id="binder-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/uwBinders" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'CT') {
                        markup += '<input type="hidden" id="cert-type-pk" name="paramValue' + counter + '"> ' +
                            '  <div id="cert-type" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/certs/selCertTypes" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'SA') {
                        markup += '<input type="hidden" id="sub-agent-id" name="paramValue' + counter + '"> ' +
                            '  <div id="sub-agent-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/inhouseagents" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'PRO') {
                        markup += '<input type="hidden" id="prospect-id" name="paramValue' + counter + '"> ' +
                            '  <div id="prospect-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/reports/prospect" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'INT') {
                        markup += '<input type="hidden" id="introducer-id" name="paramValue' + counter + '"> ' +
                            '  <div id="introducer-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/introducergents" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'MRK') {
                        markup += '<input type="hidden" id="marketer-id" name="paramValue' + counter + '"> ' +
                            '  <div id="marketer-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/marketeragents" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'REG') {
                        markup += '<input type="hidden" id="region-id" name="paramValue' + counter + '"> ' +
                            '  <div id="region-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/branchregions" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'TASK') {
                        markup += '<input type="hidden" id="task-id" name="paramValue' + counter + '"> ' +
                            '  <div id="task-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/tasktype" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'CHECKER') {
                        markup += '<input type="hidden" id="checker-id" name="paramValue' + counter + '"> ' +
                            '  <div id="checker-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/uw/policies/checkedby" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    } else if (data.lovName === 'ACTIVITY') {
                        markup += '<input type="hidden" id="activity-type" name="paramValue' + counter + '"> ' +
                            '  <div id="activity-frm" class="form-control" ' +
                            ' select2-url="' + requestContextPath + '/protected/claims/selclmActivity" ' +
                            ' style="width: 200px;"' +
                            ' </div>';
                    }
                }
                markup += '</div></div>';

                $(".data-inf").append(markup);
                counter += 1;
            });
            $(".data-inf").append('<div><input type="hidden" value="' + template + '" name="reportCode"></div>');

            $(".datepicker-input").each(function () {
                $(this).datetimepicker({
                    format: 'DD/MM/YYYY',
                    widgetParent: '#printReportModal .modal-body'
                }).on('dp.show', function() {
                    $('.bootstrap-datetimepicker-widget').css('z-index', 1050);
                });
            });

            $('#printReportModal .modal-body').on('click', function(e) {
                var container = $(this);
                var scrollBarWidth = 20; // Approximate scroll bar width in pixels
                var clickX = e.pageX - container.offset().left;
                var containerWidth = container.width();
                if (clickX > containerWidth - scrollBarWidth) {
                    e.stopPropagation();
                    e.preventDefault();
                }
            });

            var style = document.createElement('style');
            style.innerHTML = `
                .data-inf::-webkit-scrollbar {
                    width: 12px;
                }
                .data-inf::-webkit-scrollbar-track {
                    background: #f1f1f1;
                }
                .data-inf::-webkit-scrollbar-thumb {
                    background: #888;
                    border-radius: 6px;
                }
                .data-inf::-webkit-scrollbar-thumb:hover {
                    background: #555;
                }
            `;
            document.head.appendChild(style);


            // Adjust modal width based on number of fields
            var fieldCount = result.length;
            var modalWidth = Math.min(800, 300 + (fieldCount * 250));
            $('#printReportModal .modal-dialog').css('width', modalWidth + 'px');

            // Initialize LOVs
            createAccountsForSel();
            populateClientLov();
            populateProspectLov();
            createUserLov();
            createProductForSel();
            populateBinderLov();
            createCertTypeLov();
            populateUserBranches();
            populatePaymentModes();
            createReportPolicies();
            populateCurrencyLov();
            createRemmitanceForSel();
            populateSubAgentsLov();
            populateIntroducerLov();
            populateMarketerLov();
            populateRegionLov();
            populateTaskTypeLov();
            populateCheckersLov();
            populateActivityLov();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("Error fetching stretchy report parameters:", errorThrown);
        }
    });
    $('#printReportModal').modal({
        backdrop: 'static',
        keyboard: true
    });
}

return {
    callReportModal: callReportModal,
    callStretchyReportModal: callStretchyReportModal
};
})();