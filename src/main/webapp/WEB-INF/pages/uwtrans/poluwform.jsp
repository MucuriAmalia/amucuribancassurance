<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/PrintCerts.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/uwtrans.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/endorse.js"/>"></script>
<script type="text/javascript">
    var createdBy = "${createdBy}";
    var currentUser = "${currentUser}";
    function populateSourceGroupLov() {
        if ($("#sourcegroup-frm").filter("div").html() != undefined) {
            Select2Builder.initAjaxSelect2({
                containerId: "sourcegroup-frm",
                sort: 'desc',
                change: function (e, a, v) {
                    $("#sourcegroup-id").val(e.added.srcGroupId);
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

    $(document).ready(function () {
        populateSourceGroupLov();
        populateSourcesLov();
        $("#sourcegroup-frm").select2("enable", false);
        $("#source-frm").select2("enable", false);
        $('#chk-source-fields').change(toggleSourceFields);
        toggleSourceFields()
    });

    function toggleSourceFields() {
        const isEnabled = $('#chk-source-fields').is(':checked');
        if (isEnabled) {
            $('#source-fields-container').show();
            populateSourceGroupLov();
            $("#sourcegroup-frm").select2("enable", true);
            $("#source-frm").select2("enable", true);
        } else {
            $('#source-fields-container').hide();
            $("#sourcegroup-frm").select2("enable", false);
            $("#source-frm").select2("enable", false);
            // Clear selections when disabled
            $('#sourcegroup-id, #sourcegroup-name, #source-id, #source-name').val('');
            $('#sourcegroup-frm').select2('data', null);
            $('#source-frm').select2('data', null);
        }
    }
</script>
<script type="text/javascript">
    var polCode = ${policyId};
    var polStatus = ${policyStatus}+"";
    function unlockForm() {
        if (polCode !== -2000 && polStatus !== 'undefined') {
            navigator.sendBeacon(SERVLET_CONTEXT + '/protected/home/unlockForm',
                new URLSearchParams({ "formName": "GENERAL", "formId": polCode })
            );
            console.log("Form unlock request sent.");
        }
    }

    window.addEventListener("beforeunload", unlockForm);
</script>
<script type="text/javascript">
    var requestContextPath = '${pageContext.request.contextPath}';
</script>
<script type="text/javascript">
    $(document).ready(function() {
        var prevInstallPerc = null;

        // Show/hide Pay 100%? checkbox based on payment type
        function togglePayFullCheckbox() {
            var val = $('#accrual-payment-type').val();
            if (val && val !== '40-30-30') {
                $('.pay-full-checkbox').show();
            } else {
                $('.pay-full-checkbox').hide();
                $('#pay-full').prop('checked', false).trigger('change');
            }
        }

        // Initially hide the checkbox
        $('.pay-full-checkbox').hide();

        // Bind change event for payment type
        $('#accrual-payment-type').on('change', togglePayFullCheckbox);

        // Pay 100%? checkbox logic
        $('#pay-full').on('change', function() {
            if ($(this).is(':checked')) {
                var current = $('#install-perc').val();
                if (current !== '100') {
                    prevInstallPerc = current;
                }
                $('#install-perc').val('100').prop('readonly', true);
            } else {
                if (prevInstallPerc !== null) {
                    $('#install-perc').val(prevInstallPerc);
                }
                $('#install-perc').prop('readonly', false);
            }
        });
    });
</script>

<div class="x_panel">
    <%--    <c:choose>--%>
    <%--    <c:when test="${policyId != -2000}">--%>

    <%--        <a href="<c:url value="/protected/uw/policies/masterenq?polId=${policyId}"/>" id="backMaster" class="btn btn-default float-right">360&#xb0; View</a>--%>

    <%--    </c:when>--%>
    <%--        <c:when test="${policyId == -2000}">--%>
    <%--            <a href="<c:url value="/protected/uw/policies/masterenq?polId=${policyId}"/>" id="backMaster" class="btn btn-default float-right" style="pointer-events: none">360&#xb0; View</a>--%>

    <%--        </c:when>--%>
    <%--    </c:choose>--%>
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Policy Details</h2>
        <div class="clearfix"></div>
    </div>
    <div>
        <h4 class="float-left blue" style="font-weight: bolder" id="h4pol">Policy No: </h4>
        <div class="float-right">
            <sec:authorize access="hasAnyAuthority('ACCESS_POLICY_REPORTS')">
                <button type="button" class="btn btn-primary float-right"
                        id="btn-uw-reports" data-toggle="modal" data-target="#reportsModal"
                        style="display:none">
                    <i class="fa fa-print"></i>
                </button>

            </sec:authorize>
            <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
                <input type="button" class="btn btn-primary float-right"
                       value="Unsubmit" id="btn-undo-make-ready"
                       style="display:none">
            </sec:authorize>

            <%--            <input type="button" class="btn btn-primary float-right"--%>
            <%--                   value="Assign" id="btn-assign-trans">--%>

        </div>
    </div>


    <form id="policy-form" class="form-horizontal form-label-left">
        <input action="action" type="button" onclick="history.go(-1);" class="btn btn-primary float-right" value="Back"
               id="renbtn" style="display:none"/>
        <%--<input type="button" class="btn btn-info float-right  motor-disp"--%>
        <%--value="Print Certificate" id="btn-print-certs">--%>


        <%--        <div class="x_title">--%>
        <%--            <h4 id="wkflow-task-name">Policy Details</h4>--%>

        <%--<ul class="nav navbar-right panel_toolbox">--%>
        <%--<li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>--%>
        <%--</li>--%>
        <%--</ul>--%>
        <%--        </div>--%>
</div>
<input type="hidden" id="policy-id" name="policyId"/>
<input type="hidden" id="pol-trans-type" name="transType"/>
<input type="hidden" id="pol-rev-trans-type" name="polRevStatus"/>
<input type="hidden" id="pol-prev-policy" name="prevPolicy"/>
<input type="hidden" id="pol-reuse-contra-policy" name="reusecontraPolicy"/>
<input type="hidden" id="hasrefund"/>
<input type="hidden" id="refund-amount1"/>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="client-frm" class="label-align col-md-5">
            Client <span class="required">*</span></label>
        <div class="col-md-7 col-xs-9">

            <input type="hidden" id="client-id" name="clientId"/>
            <input type="hidden" id="client-f-name">
            <input type="hidden" id="client-other-name">
            <input type="hidden" id="ntl-id">
            <div id="client-frm" class="form-control"
                 select2-url="<c:url value="/protected/uw/policies/uwClients"/>">
            </div>

        </div>
        <%--                <div id ="new-client" class="col-md-1 col-xs-3">--%>
        <%--                    &lt;%&ndash;<div id="display-client">&ndash;%&gt;--%>
        <%--                    &lt;%&ndash;<p class="form-control-static" id="client-info"></p>&ndash;%&gt;--%>
        <%--                    &lt;%&ndash;</div>&ndash;%&gt;--%>
        <%--                    <div class="edit-client">--%>
        <%--                        <a href="<c:url value="/protected/clients/setups/clientsform?type=pol"/>"--%>
        <%--                           id="btn-add-client"--%>
        <%--                           class="btn btn-sm btn-info">New</a>--%>
        <%--                    </div>--%>
        <%--                </div>--%>
    </div>
</div>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="pol-buss-type" class="label-align col-md-5">
            Business Type<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <select class="form-control" id="pol-buss-type" name="businessType"
                    required>
                <option value="">Select Business Type</option>
                <option value="N">Annual</option>
                <option value="S">Short Period</option>
            </select>
        </div>
    </div>
</div>
<%--        <div class="col-md-6 col-xs-12 multi-product">--%>
<%--            <div class="item form-group form-required">--%>
<%--                <label for="product-frm" class="label-align col-md-5 ">--%>
<%--                    Product</label>--%>
<%--                <div class="col-md-7 col-xs-12">--%>
<%--                    <input type="hidden" id="bind-product-desc">--%>
<%--                    <div id="product-frm" class="form-control"--%>
<%--                         select2-url="<c:url value="/protected/uw/policies/uwMultiProducts"/>">--%>
<%--                    </div>--%>
<%--                </div>--%>

<%--            </div>--%>
<%--            &lt;%&ndash;<div class="item form-group">&ndash;%&gt;--%>
<%--            &lt;%&ndash;<div class="col-md-6 col-xs-12">&ndash;%&gt;--%>
<%--            &lt;%&ndash;</div>&ndash;%&gt;--%>
<%--            &lt;%&ndash;<div class="col-md-6 col-xs-12">&ndash;%&gt;--%>
<%--            &lt;%&ndash;</div>&ndash;%&gt;--%>

<%--        </div>--%>


<%--<div class="col-md-6 col-xs-12">--%>

<%--</div>--%>

<div class="col-md-6 col-xs-12  bind-insurance">
    <div class="item form-group form-required ">
        <label for="prd-code" class="label-align col-md-5">
            Product<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="prd-id" rv-value="product.proCode" name="product"/>
            <input type="hidden" id="pr-name">
            <div id="prd-code" class="form-control"
                 select2-url="<c:url value="/protected/setups/binders/selgenproducts"/>">

            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 bind-info">
    <div class="item form-group form-required ">
        <label for="binder-frm" class="label-align col-md-5">
            Contract<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <div id="edit-binder">
                <input type="hidden" id="binder-id" name="bindCode"/>
                <input type="hidden" id="product-id" name="prodId"/>
                <input type="hidden" id="pol-agent-id" name="agentId"/>
                <input type="hidden" id="pol-bind-age-appli"/>
                <input type="hidden" id="bind-name">
                <div id="binder-frm" class="form-control"
                     select2-url="<c:url value="/protected/uw/policies/uwBinders"/>">

                </div>
            </div>
            <div id="display-binder">
                <p class="form-control-static" id="binder-info"></p>
            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 bind-insurance">
    <div class="item form-group form-required ">
        <label for="pol-ins-comp" class="label-align col-md-5">
            Insurer</label>
        <div class="col-md-7 col-xs-12">
            <p class="form-control-static" id="pol-ins-comp"></p>
        </div>
    </div>

</div>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="pol-interface-type" class="label-align col-md-5">
            Policy Type<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <select class="form-control" id="pol-interface-type" name="interfaceType"
                    required>
                <%--                <option value="">Select Policy Type</option>--%>
                <option value="A">Accrual</option>
                <option value="C" selected>Cash</option>
            </select>
        </div>
    </div>
</div>


<div class="col-md-6 col-xs-12" style="display:none;" id="disp-accrual-type">
    <div class="item form-group">
        <label for="accrual-payment-type" class="label-align col-md-5">
            Premium Payment Type<span class="required">*</span>
        </label>
        <div class="col-md-7 col-xs-12">
            <select class="form-control" id="accrual-payment-type" name="accrualPaymentType">
                <option value="" selected>Select Payment Type</option>
                <option value="IPF">IPF</option>
                <option value="Dispensation">Dispensation</option>
                <option value="PD Cheque">Post Dated Cheque</option>
                <option value="40-30-30">40-30-30</option>
            </select>
        </div>
    </div>
</div>

<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="pol-frequency" class="label-align col-md-5">
            Payment Frequency<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <select class="form-control" id="pol-frequency" name="frequency"
                    required>
                <option value="">Select Payment Frequency</option>
                <%--                <option value="D">Daily</option>--%>
                <%--                <option value="W">Weekly</option>--%>
                <option value="M">Monthly</option>
                <option value="Q">Quartely</option>
                <option value="S">Semi-Annually</option>
                <option value="A" selected>Annually</option>
                <option value="SG">Single</option>
            </select>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12" style="display:none;" id="disp-inst-date">
    <div class="item form-group">
        <label for="brn-id" class="col-md-5 label-align">Next Installment
            Date<span class="required">*</span></label>

        <div class="col-md-7 col-xs-12">
            <div class='input-group date datepicker-input' id="install-date">
                <input type='text' class="form-control float-left" name="accrualInstDate"
                       id="installment-date"/>
                <div class="input-group-addon">
                    <span class="fa fa-calendar"></span>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="col-md-6 col-xs-12  form-required">
    <div class="item form-group form-required">
        <label for="brn-id" class="col-md-5 label-align">Date
            From</label>

        <div class="col-md-7 col-xs-12">
            <div class='input-group date datepicker-input' id="wef-date">
                <input type='text' class="form-control float-right" name="wefDate"
                       id="from-date" required/>
                <div class="input-group-addon">
                    <span class="fa fa-calendar"></span>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="noOfUnits" class="label-align col-md-5">Date
            To</label>
        <div class="col-md-7 col-xs-12">
            <div class='input-group date datepicker-input' id="cover-to-date">
                <input type='text' class="form-control float-right" name="wetDate"
                       id="wet-date" required/>
                <div class="input-group-addon">
                    <span class="fa fa-calendar"></span>
                </div>
            </div>
        </div>

    </div>


</div>

<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="edit-branch" class="label-align col-md-5">
            Branch<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <div id="edit-branch">
                <input type="hidden" id="brn-id" name="branchId"/>
                <input type="hidden" id="brn-name">
                <div id="brn-frm" class="form-control"
                     select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">

                </div>
            </div>
            <div id="display-branch">
                <p class="form-control-static" id="branch-info"></p>
            </div>
        </div>
    </div>
</div>

<div class="col-md-6 col-xs-12">
    <div class="item form-group form-required">
        <label for="curr-frm" class="label-align col-md-5">
            Currency<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <div id="edit-currency">
                <input type="hidden" id="cur-id" name="currencyId"/>
                <input type="hidden" id="cur-name">
                <div id="curr-frm" class="form-control"
                     select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>">

                </div>
            </div>
            <div id="display-currency">
                <p class="form-control-static" id="currency-info"></p>
            </div>
        </div>
    </div>
</div>
<div id="source-fields-container" style="display: none">
    <div class="form-group">
        <div class="col-md-6 col-xs-12">
            <label for="houseName" class="label-align col-md-5">
                Source Classification</label>
            <div class="col-md-7 col-xs-12">
                <div id="edit-sourcegroup">
                    <input type="hidden" id="sourcegroup-id"/>
                    <input type="hidden" id="sourcegroup-name">
                    <div id="sourcegroup-frm" class="form-control"
                         select2-url="<c:url value="/protected/quotes/sourcesgroups"/>">
                    </div>
                </div>
                <div id="display-sourcegroup">
                    <p class="form-control-static" id="sourcegroup-info"></p>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-xs-12">
            <label for="houseName" class="label-align col-md-5">
                Source</label>
            <div class="col-md-7 col-xs-12">
                <div id="edit-source">
                    <input type="hidden" id="source-id" name="sourceId"/>
                    <input type="hidden" id="source-name">
                    <div id="source-frm" class="form-control"
                         select2-url="<c:url value="/protected/quotes/businesssources"/>">
                    </div>
                </div>
                <div id="display-source">
                    <p class="form-control-static" id="source-info"></p>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 introducer-agent-type">
    <div class="item form-group form-required">
        <label for="houseId" class="label-align col-md-5">
            Introducer </label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="introducer-id" name="introducerAgentId"/>
            <input type="hidden" id="introducer-name">
            <div id="introducer-frm" class="form-control"
                 select2-url="<c:url value="/protected/uw/policies/introducergents"/>">

            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 introducer-agent-absaNo">
    <div class="item form-group">
        <label for="introducer-absaNo-name" class="label-align col-md-5">Introducer AB No</label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="introducer-absa-No-id" name="absaNoIntroducer"/>
            <input type="text" id="introducer-absaNo-name" class="form-control" readonly>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 leads-man-type">
    <div class="item form-group form-required">
        <label for="leadsId" class="label-align col-md-5">Leads Man</label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="leads-man-id" name="leadsManId"/>
            <input type="hidden" id="leads-man-name">
            <div id="leads-man-frm" class="form-control"
                 select2-url="<c:url value='/protected/uw/policies/leadsMan'/>">
            </div>
        </div>
    </div>
</div>
<div class="col-md-6 col-xs-12 leads-man-absaNo">
    <div class="item form-group">
        <label for="leads-absaNo-name" class="label-align col-md-5">Leads Man AB No</label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="leads-absaNo-id" name="absaNoLeadsMan"/>
            <input type="text" id="leads-absaNo-name" class="form-control" readonly>
        </div>
    </div>
</div>
<!--
<div class="col-md-6 col-xs-12">
<div class="item form-group">
<label for="chk-admin-fee" class="col-md-5 label-align">Admin Fee Applicable</label>
<div class="col-md-7 checkbox admin-fee">
<label>
<input type="checkbox" id="chk-admin-fee" name="adminFeePolicy">
</label>
</div>
</div>
</div>
-->
<div class="col-md-6 col-xs-12">
    <div class="item form-group">
        <label for="agent-type" class="label-align col-md-5"> Sub Agent/ Marketer</label>
        <div class="col-md-7 col-xs-12">
            <select class="form-control" id="agent-type" name="accountTypes">
                <option value="">Select Type</option>
                <option value="SUB">Sub Agent</option>
                <option value="MRK">Marketer</option>
            </select>
        </div>
        <div class="col-md-3">
        </div>
    </div>
</div>

<div class="col-md-6 col-xs-12 sub-agent-type" style="display: none;">
    <div class="item form-group form-required">
        <label for="houseId" class="label-align col-md-5">Sub-Agent</label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="sub-agent-id" name="subAgentId"/>
            <input type="hidden" id="acc-id"/>
            <input type="hidden" id="sub-agent-name">
            <div id="sub-agent-frm" class="form-control"
                 select2-url="<c:url value='/protected/uw/policies/inhouseagents'/>">
            </div>
        </div>
    </div>
</div>

<div class="col-md-6 col-xs-12 marketer-agent-type" style="display: none;">
    <div class="item form-group form-required">
        <label for="houseId" class="label-align col-md-5">Marketer</label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="marketer-id" name="marketerAgentId"/>
            <input type="hidden" id="acct-id"/>
            <input type="hidden" id="marketer-name">
            <div id="marketer-frm" class="form-control"
                 select2-url="<c:url value='/protected/uw/policies/marketeragents'/>">
            </div>
        </div>
    </div>
</div>


<div class="col-md-6 col-xs-12 sub-agent-absNo" style="display: none;">
    <div class="item form-group">
        <label for="sub-agent-absaNo-name" class="label-align col-md-5">Sub Agent AB No</label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="sub-agent-absaNo-id" name="absaNoSubAgent"/>
            <input type="text" id="sub-agent-absaNo-name" class="form-control" readonly>
        </div>
    </div>
</div>

<div class="col-md-6 col-xs-12 marketer-agent-absNo" style="display: none;">
    <div class="item form-group">
        <label for="marketer-absaNo-name" class="label-align col-md-5">Marketer AB No</label>
        <div class="col-md-7 col-xs-12">
            <input type="hidden" id="marketer-absaNo-id" name="absaNoMarketer"/>
            <input type="text" id="marketer-absaNo-name" class="form-control" readonly>
        </div>
    </div>
</div>


<div id="other-pol-details">
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="div-pol-no" class="label-align col-md-5">
                Policy No</label>
            <div class="col-md-7 col-xs-12">
                <input type="hidden" id="div-pol-no" name="polNo"/>
                <p class="form-control-static" id="pol-no"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="div-endos-no" class="label-align col-md-5">
                Reference Number</label>
            <div class="col-md-7 col-xs-12">
                <input type="hidden" id="div-endos-no" name="polRevNo"/>
                <p class="form-control-static" id="pol-rev-no"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-sum-insured" class="label-align col-md-5">
                Sum Insured</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-sum-insured"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-premium" class="label-align col-md-5">
                Basic Premium</label>
            <div class="col-md-7 col-xs-12">

                <p class="form-control-static" id="pol-premium"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-tl" class="label-align col-md-5">
                Training Levy</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-tl"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-phcf" class="label-align col-md-5">
                PHCF Fund</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-phcf"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-basic-prem" class="label-align col-md-5">
                Gross Prem</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-basic-prem"></p>
            </div>
        </div>
    </div>

    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-net-prem" class="label-align col-md-5">
                Net Prem</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-net-prem"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-sd" class="label-align col-md-5">
                Stamp Duty</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-sd"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-whtx" class="label-align col-md-5">
                WHTX Tax</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-whtx"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-comm-amt" class="label-align col-md-5">
                Commission Amt</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-comm-amt"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-whtx" class="label-align col-md-5">
                Extras</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-extras"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-tran-type-disp" class="label-align col-md-5">
                Transaction Type</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-tran-type-disp"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-introducer-comm" class="label-align col-md-5">
                Introducer Commission</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-introducer-comm"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-sub-agent-comm" class="label-align col-md-5">
                Sub Agent Commission</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-sub-agent-comm"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-marketer-comm" class="label-align col-md-5">
                Marketer Commission</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-marketer-comm"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-ren-date" class="label-align col-md-5">
                Renewal Date</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-ren-date"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-status" class="label-align col-md-5">
                Policy Status</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-status"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-fap" class="label-align col-md-5">
                Future Expected Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-fap"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-endos-gross-premium" class="label-align col-md-5">
                Installment Total Policy Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-endos-gross-premium"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-paid-premium" class="label-align col-md-5">
                Paid Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-paid-premium"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-outstanding-premium" class="label-align col-md-5">
                Outstanding Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-outstanding-premium"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-paid-premium-pct" class="label-align col-md-5">
                Percentage Premium Paid</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-paid-premium-pct"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12">
        <div class="item form-group form-required">
            <label for="pol-refundable-amount" class="label-align col-md-5">
                Refundable Premium</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-refundable-amount"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12 admin-fee-appl">
        <div class="item form-group form-required">
            <label for="pol-paid-premium-pct" class="label-align col-md-5">
                Admin Fee</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-admin-fee"></p>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xs-12 admin-fee-appl">
        <div class="item form-group form-required">
            <label for="pol-refundable-amount" class="label-align col-md-5">
                Admin Fee WHTX Amt</label>
            <div class="col-md-7 col-xs-12">
                <p class="form-control-static" id="pol-admin-fee-vat"></p>
            </div>
        </div>
    </div>

</div>

<div class="col-md-6 col-xs-12 chkimport-risks">
    <div class="item form-group form-required">
        <label for="rate-taxable" class="col-md-5 label-align import-risks">Import Risks</label>
        <div class="col-md-7 checkbox import-risks">
            <label>
                <input type="checkbox" id="chk-import-risks" name="importRisks">
            </label>
        </div>
    </div>
</div>
<%--        <div class="col-md-6 col-xs-12">--%>
<%--            <div class="item form-group form-required">--%>
<%--                <label for="rate-taxable" class="col-md-5 label-align">Co-Insurance Business</label>--%>
<%--                <div class="col-md-7 checkbox">--%>
<%--                    <label>--%>
<%--                        <input type="checkbox" id="chk-coin-risks" name="coinsuranceBusiness">--%>
<%--                    </label>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--        </div>--%>
</form>
</div>
</div>
<div class="x_panel multi-product-uw">
    <div class="x_title">
        <h4>Policy Binders</h4>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="x_content">
        <div class="card-box table-responsive">
            <input type="hidden" id="pol-binder-pk">
            <table id="policy-binders-tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Insurer</th>
                    <th>Binder ID</th>
                    <th>Binder Desc</th>
                    <th>Basic Prem</th>
                    <th width="5%"></th>
                    <th width="5%"></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<div class="x_panel risk-detail-tab">
    <div class="x_title">
        <h4>Coverage Details</h4>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>
    <div class="x_content">

        <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
            <li role="presentation" class="active"><a href="#tab_content1"
                                                      id="home-tab" role="tab" data-toggle="tab"
                                                      aria-expanded="true">Coverage Details</a>
            </li>
            <li role="presentation" class="" id="show-taxes"><a href="#tab_content2"
                                                                role="tab" id="profile-tab" data-toggle="tab"
                                                                aria-expanded="false">Levies</a>
            </li>
            <%--                <li role="presentation" class=""><a href="#tab_content29" role="tab"--%>
            <%--                                                    id="questionnaire-tabb" data-toggle="tab"--%>
            <%--                                                    aria-controls="profile"--%>
            <%--                                                    aria-expanded="false">Questionnaire</a>--%>
            <%--                </li>--%>
            <li role="presentation" class="" id="show-clauses"><a href="#tab_content3"
                                                                  role="tab" id="comm-rates-tab" data-toggle="tab"
                                                                  aria-expanded="false">Clauses</a>
            </li>
            <li role="presentation" class="" id="end-remarks"><a href="#tab_content4"
                                                                 role="tab" id="end-remarks-tab" data-toggle="tab"
                                                                 aria-expanded="false">Remarks</a>
            </li>
            <li role="presentation" class="" id="refund-remarks"><a href="#tab_refund_comments"
                                                                    role="tab" id="refund-remarks-tab" data-toggle="tab"
                                                                    aria-expanded="false">Refund Remarks</a>
            </li>

            <li role="presentation" class="" id="checks-tab"><a href="#tab_pol_checks"
                                                                role="tab" id="pol-checks-tab" data-toggle="tab"
                                                                aria-expanded="false">Checks</a>
            </li>
            <li role="presentation" class="" id="cert-tab"><a href="#tab_pol_certs"
                                                              role="tab" id="pol-cets-tab"
                                                              data-toggle="tab"
                                                              aria-expanded="false">
                Certificates</a>
            </li>
            <li role="presentation" class="" id="receipts-tab"><a href="#tab_pol_receipts"
                                                                  role="tab" id="pol-rec-tab" data-toggle="tab"
                                                                  aria-expanded="false">Receipts</a>
            </li>
            <li role="presentation" class="" id="workflow"><a href="#tab_content5"
                                                              role="tab" id="workflow-tab" data-toggle="tab"
                                                              aria-expanded="false">Process Diagram</a>
            </li>
            <li role="presentation" class="hide-details" id="audit-trails"><a href="#tab_audit_trails" role="tab" data-toggle="tab" aria-expanded="false">Activity Trails</a></li>

        </ul>


        <div id="myTabContent" class="tab-content">
            <div role="tabpanel" class="tab-pane active"
                 id="tab_content1" aria-labelledby="home-tab">
                <input type="hidden" id="risk-bind-code"/>
                <input type="hidden" id="risk-det-id-pk"/>
                <button class="btn btn-success btn btn-info" id="btn-endors-risk" style="display:none">Select Risk To Endorse
                </button>
                <button class="btn btn-primary btn btn-primary" id="btn-add-risk">New</button>
                <button class="btn btn-primary btn btn-primary" id="btn-save-risk">Save</button>
                <button class="btn btn-primary btn btn-primary" id="btn-save-cancel">Cancel</button>
                <div class="d-flex align-items-center">
                    <button class="btn btn-primary mr-3" id="btn-import-risk">Import Risk</button>

                    <div id="risk-alert-template">
                        <div class="alert alert-secondary mb-0 d-flex align-items-center py-2">
                            <i class="fas fa-info-circle mr-2"></i>
                            <span class="mr-2"><strong>Important:</strong> Please use the standardized template for risk import.</span>
                            <a href="<c:url value='/protected/uw/policies/riskImportTemplate'/>" class="btn btn-outline-primary btn-sm">
                                <i class="fas fa-download mr-1"></i> Download Template
                            </a>
                        </div>
                    </div>
                </div>
<%--                <button class="btn btn-primary btn btn-primary" id="btn-import-logs" style="display:none">Logs</button>--%>
                <div id="risk-div">

                    <table id="risk_tbl" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th class="motor-disp">Risk ID</th>
                            <th class="non-motor-disp">Risk ID</th>
                            <th class="motor-disp">Risk Desc</th>
                            <th class="non-motor-disp">Risk Desc</th>
                            <th>WEF</th>
                            <th>WET</th>
                            <%--                                <th>Binder</th>--%>
                            <th>Classification</th>
                            <th>Cover Type</th>
                            <th>Sum Insured</th>
                            <th>Premium</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <form id="risk-form" class="form-horizontal form-label-left">
                    <input type="hidden" id="risk-code-pk" name="riskId"/>
                    <input type="hidden" id="risk-binder-code-pk" name="polBindCode"/>
                    <input type="hidden" id="risk-trans-type" name="transType"/>
                    <input type="hidden" id="risk-ident-code" name="riskIdentifier"/>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="insured-frm" class="label-align col-md-5">
                                Insured<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="insured-code" name="insuredCode"/>
                                <input type="hidden" id="insured-name">
                                <input type="hidden" name="riskIdentifier"/>
                                <input type="hidden" id="risk-binder-code" name="bindCode"/>
                                <input type="hidden" id="insured-other-name">
                                <input type="hidden" id="insured-ntl-id">
                                <div id="insured-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwClients"/>">

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="subclass-frm" class="label-align col-md-5">
                                Classification<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="risk-sub-code" name="sclCode"/>
                                <input type="hidden" id="sub-name">
                                <div id="subclass-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwsubclasses"/>">

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">

                        <div class="col-md-6 col-xs-12">
                            <label for="covertypes-frm" class="label-align col-md-5">
                                Coverage<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="risk-cov-code" name="coverCode"/>
                                <input type="hidden" id="binder-det-id" name="binderDet"/>
                                <input type="hidden" id="cover-name">
                                <div id="covertypes-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/riskCoverTypes"/>">

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="overrid-prem" class="label-align col-md-5">
                                Negotiated Premium</label>
                            <div class="col-md-7 col-xs-12 checkbox">
                                <input type="text" name="butchargePrem" id="overrid-prem" class="form-control"
                                       placeholder="Negotiated Premium">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="risk-id" class="label-align col-md-5" id="risk-label">
                                Risk Identifier<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="riskShtDesc" id="risk-id" class="form-control"
                                       required style="text-transform: uppercase;">
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="risk-desc" class="label-align col-md-5" id="risk-description">
                                Risk Description<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12 checkbox">
                                <input type="text" name="riskDesc" id="risk-desc" class="form-control"
                                       required>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12  form-required">
                            <label for="brn-id" class="col-md-5 label-align">Date
                                From</label>

                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="risk-cover-from">
                                    <input type='text' class="form-control float-right" name="wefDate"
                                           id="risk-wef-date" required/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="noOfUnits" class="label-align col-md-5">Date
                                To</label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="risk-cover-to">
                                    <input type='text' class="form-control float-right" name="wetDate"
                                           id="risk-wet-date" required/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>

                        </div>


                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12" style="display:none;">
                            <label for="comm-rate" class="label-align col-md-5">
                                Commission Rate<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="number" name="commRate" id="comm-rate" class="form-control"
                                       placeholder="Comm Rate" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12 motor-disp installment">
                            <label for="install-perc" class="label-align col-md-5">
                                Installment Percentage</label>
                            <div class="col-md-7 col-xs-12" style="display: flex; align-items: center;">
                                <input type="text" name="installmentPerc" id="install-perc" class="form-control"
                                       placeholder="Installment Percentage" style="margin-right:10px;">
                                <label class="pay-full-checkbox" style="margin-bottom:0; margin-left:10px;">
                                    <input type="checkbox" id="pay-full" name="payFull"> Pay 100%?
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group installment">
                        <div class="col-md-6 col-xs-12 motor-disp">
                            <label for="install-amt" class="col-md-5 label-align">Installment Amount</label>

                            <div class="col-md-7">
                                <input type="text" class="editUserCntrls form-control"
                                       id="install-amt" name="installAmount"
                                       readonly>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="rate-taxable" class="col-md-5 label-align">Installment No</label>
                            <div class="col-md-7 col-xs-12 checkbox">
                                <input type="text" name="installmentNo" id="install-no" class="form-control"
                                       placeholder="Installment No" readonly>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="prorated-full" class="label-align col-md-5">
                                Proration Type<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="prorated-full" name="prorata" required>
                                    <option value="">Select Proration Type</option>
                                    <option value="P">Prorated</option>
                                    <option value="F">Full</option>
                                    <option value="S">Short Period</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12 motor-disp installment">

                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12 sub-comm-type" style="display: none;">
                            <label for="sub-comm-rate" class="label-align col-md-5">
                                Sub Agent Commission Rate<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="number" name="subAgentCommRate" id="sub-comm-rate" class="form-control"
                                       placeholder="Sub Agent Comm Rate" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12 marketer-comm-type" style="display: none;">
                            <label for="marketer-comm-rate" class="label-align col-md-5">
                                Marketer Commission Rate<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="number" name="marketerCommRate" id="marketer-comm-rate"
                                       class="form-control"
                                       placeholder="Marketert Comm Rate" required>
                            </div>
                        </div>
                    </div>
                </form>


                <div class="x_panel prem-cert-sched" id="prem-item">
                    <div class="x_title">
                        <h4>Premium Items, Certificates and Schedules</h4>
                        <ul class="nav navbar-right panel_toolbox">
                            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                            </li>
                        </ul>
                    </div>
                    <div class="x_content">


                        <div id="sect-div">
                            <div class="" role="tabpanel" data-example-id="togglable-tabs">
                                <ul id="myTab1" class="nav nav-tabs bar_tabs" role="tablist">
                                    <li role="presentation" class="active"><a href="#tab_content11" id="home-tabb"
                                                                              role="tab" data-toggle="tab"
                                                                              aria-controls="home"
                                                                              aria-expanded="true">Premium Items</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content22" role="tab"
                                                                        id="profile-tabb"
                                                                        data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Certificates</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content23" role="tab"
                                                                        id="schedules-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Risk Details</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content24" role="tab"
                                                                        id="docs-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Risk/Supporting
                                        Documents</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content31" role="tab"
                                                                        id="refund-docs-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Refund Supporting Documents</a>
                                    </li>

                                    <li role="presentation" class=""><a href="#tab_content26" role="tab"
                                                                        id="insured-docs-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">KYC Documents</a>
                                    </li>
                                    <li role="presentation" class=""><a href="#tab_content25" role="tab"
                                                                        id="int-parties-tabb" data-toggle="tab"
                                                                        aria-controls="profile"
                                                                        aria-expanded="false">Interested Parties</a>
                                    </li>
                                    <li role="presentation" class="wiba-disp"><a href="#tab_content30" role="tab"
                                                                                 id="int-beneficiaries-tabb"
                                                                                 data-toggle="tab"
                                                                                 aria-controls="profile"
                                                                                 aria-expanded="false">Beneficiaries</a>
                                    </li>
                                </ul>
                                <div id="myTabContent2" class="tab-content">
                                    <div role="tabpanel" class="tab-pane active" id="tab_content11"
                                         aria-labelledby="home-tab">
                                        <button class="btn btn-primary btn btn-primary" id="btn-add-new-section">New
                                        </button>
                                        <input type="hidden" id="insured-client-age">
                                        <div class="card-box table-responsive">
                                            <table id="section_tbl" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Premium Item</th>
                                                    <th>Value</th>
                                                    <th>Rate</th>
                                                    <th>Calc Prem</th>
                                                    <th>Premium</th>
                                                    <th>Div Factor</th>
                                                    <th>Free Limit</th>
                                                    <th width="5%"></th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>
                                    <div role="tabpanel" class="tab-pane fade"
                                         id="tab_content26" aria-labelledby="profile-tab">
                                        <div class="card-box table-responsive">
                                            <table id="clientDocsList" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Document ID</th>
                                                    <th>Document Desc</th>
                                                    <th>File Name</th>
                                                    <th>Uploaded By</th>
                                                    <th>Uploaded Date</th>
                                                    <th>Verified By</th>
                                                    <th>Verified Date</th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>

                                    <div role="tabpanel" class="tab-pane fade" id="tab_content23"
                                         aria-labelledby="profile-tab">
                                        <button class="btn-primary btn btn-primary" id="btn-add-new-sched">New
                                        </button>
                                        <div class="container">
                                            <table id="schedule_details_tbl"
                                                   class="table table-striped"
                                                   style="width:100%">
                                                <thead>

                                                </thead>
                                                <tbody>

                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content25"
                                         aria-labelledby="home-tab">
                                        <button class="btn btn-primary btn btn-primary" id="btn-add-new-ips">New
                                        </button>
                                        <div class="card-box table-responsive">
                                            <table id="interested_parties_tbl"
                                                   class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Name</th>
                                                    <th>Type</th>
                                                    <th>Email Address</th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content24"
                                         aria-labelledby="profile-tab">
                                        <button class="btn btn-primary btn btn-primary" id="btn-add-docs">New</button>
                                        <table id="risk_docs_tbl" class="table table-striped" style="width:100%">
                                            <thead>
                                            <tr>
                                                <th>Revision No</th>
                                                <th>Document ID</th>
                                                <th>File Name</th>
                                                <th>Uploaded By</th>
                                                <th>Uploaded Date</th>
                                                <th>Verified By</th>
                                                <th>Verified Date</th>
                                                <th>Verify Document</th>
                                                <th>Comment</th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                            </tr>
                                            </thead>
                                        </table>

                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content31"
                                         aria-labelledby="profile-tab">
                                        <button class="btn btn-primary btn btn-primary" id="btn-add-docs">New</button>
                                        <table id="refund_docs_tbl" class="table table-striped" style="width:100%">
                                            <thead>
                                            <tr>
                                                <th>Revision No</th>
                                                <th>Document ID</th>
                                                <th>File Name</th>
                                                <th>Uploaded By</th>
                                                <th>Uploaded Date</th>
                                                <th>Verified By</th>
                                                <th>Verified Date</th>
                                                <th>Verify Document</th>
                                                <th>Comment</th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                            </tr>
                                            </thead>
                                        </table>

                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content22"
                                         aria-labelledby="profile-tab">
                                        <button class="btn btn-primary btn btn-primary" id="btn-add-new-cert">New
                                        </button>
                                        <div class="card-box table-responsive">
                                            <table id="cert_tbl" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Cert Type</th>
                                                    <th>Status</th>
                                                    <th>Print Status</th>
                                                    <th>WEF</th>
                                                    <th>WET</th>
                                                    <th>Cert No</th>
                                                    <th>Reason cancelled</th>
                                                    <%--<th width="5%"></th>--%>
                                                    <%--<th width="5%"></th>--%>
                                                    <th width="5%"></th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>

                                    </div>
                                    <div role="tabpanel" class="tab-pane fade" id="tab_content30"
                                         aria-labelledby="profile-tab">
                                        <button class="btn btn-success btn btn-info" id="btn-add-beneficiary">New
                                        </button>
                                        <form id="benefits-upload-form" class="form-horizontal"
                                              enctype="multipart/form-data">
                                            <input type="hidden" class="form-control" id="ben-import-risk-id"
                                                   name="riskId">
                                            <div class="col-md-10 col-xs-12 form-required">

                                                <div class="col-md-5 col-xs-12">
                                                    <div class="input-group col-xs-12">
                                                        <input name="file" type="file" id="avatar" required>
                                                    </div>
                                                </div>
                                                <div class="col-md-3 col-xs-12">
                                                    <input type="submit" class="btn btn-primary btn-sm float-left"
                                                           style="margin-right: 10px;" value="Upload Beneficiaries">
                                                </div>
                                            </div>
                                        </form>
                                        <table id="risk_beneficiaries_tbl" class="table table-striped"
                                               style="width:100%">
                                            <thead>
                                            <tr>
                                                <th>Name</th>
                                                <th>Occupation</th>
                                                <th>Salary</th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                            </tr>
                                            </thead>
                                        </table>

                                    </div>
                                </div>

                            </div>
                        </div>

                    </div>
                    <div class="x_content" id="prem-rates-div">
                        <div class="card-box table-responsive">
                            <table id="section_form_tbl" class="table table-striped" style="width:100%">
                                <thead>
                                <tr>
                                    <th>Premium Item</th>
                                    <th>Value</th>
                                    <th>Rate</th>
                                    <th>Div Factor</th>
                                    <th>Free Limit</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content2" aria-labelledby="profile-tab">
                <button class="btn btn-primary btn btn-primary" id="btn-add-new-tax">New</button>
                <div class="card-box table-responsive">
                    <table id="polTaxesList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Trans Code</th>
                            <th>Trans Code</th>
                            <th>Levy Rate</th>
                            <th>Div Factor</th>
                            <th>Rate Type</th>
                            <th>Levy Amount</th>
                            <th>Levy Level</th>
                            <th width="5%"></th>
                            <th width="5%"></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <%--                <div role="tabpanel" class="tab-pane fade" id="tab_content29"--%>
            <%--                     aria-labelledby="home-tab">--%>
            <%--                    <button class="btn btn-success btn btn-info" id="btn-questionnaire">Fill Questionnaire--%>
            <%--                    </button>--%>
            <%--                    <button class="btn btn-danger btn btn-info" id="btn-del-questionnaire">Remove Questionnaire--%>
            <%--                    </button>--%>

            <%--                    <button class="btn btn-success btn btn-info" id="btn-edit-questionnaire">Edit Questionnaire--%>
            <%--                    </button>--%>
            <%--                    <div class="card-box table-responsive">--%>
            <%--                        <table id="questionnaire_tbl" class="table" style="width:100%">--%>
            <%--                            <thead>--%>
            <%--                            <tr>--%>
            <%--                                <th>Question</th>--%>
            <%--                                <th>Answer</th>--%>
            <%--                            </tr>--%>
            <%--                            </thead>--%>
            <%--                        </table>--%>
            <%--                    </div>--%>
            <%--                </div>--%>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_pol_checks" aria-labelledby="profile-tab">

                <table id="polChecksList" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Check Name</th>
                        <th>Approved</th>
                        <th>Approved By</th>
                        <th>Approved On</th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_pol_certs" aria-labelledby="profile-tab">
                <%--                    <div class="col-md-6 col-xs-12">--%>
                <input type="button" class="btn btn-primary float-left"
                       value="Get Digital Certificate" id="btn-reprint-cert-policy"
                >
                <%--                        <input type="button" value="Allocate" class="btn btn-success" id="btn-allocate">--%>
                <%--                        <input type="button" value="Deallocate" class="btn btn-success" id="btn-deallocate">--%>
                <%--                    </div>--%>
                <div class="x_panel">
                    <form id="print-cert-form">
                        <input type="hidden" id="cert-type-id" name="branchCert"/>
                    </form>
                    <div class="cutom-container">
                        <table id="polCertsList" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Risk</th>
                                <th>WEF</th>
                                <th>WET</th>
                                <th>Start Time</th>
                                <th>Status</th>
                                <th>Alloc By</th>
                                <th>Cert No</th>
                                <th>Good For Print</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                    <form class="form-horizontal" id="print-form">


                    </form>
                    <input type="button" value="Print" class="btn btn-primary" id="btn-print">

                </div>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_pol_receipts" aria-labelledby="profile-tab">

                <table id="polReceipts" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Receipt No</th>
                        <th>Receipt Date</th>
                        <th>Amount</th>
                    </tr>
                    </thead>
                </table>
            </div>
             <div role="tabpanel" class="tab-pane fade" id="tab_audit_trails" aria-labelledby="audit-trails">
                                              <table id="audit_trails_tbl" class="table table-striped table-bordered">
                                                  <thead>
                                                      <tr>
                                                          <th>Checker Name</th>
                                                          <th>Maker Name</th>
                                                          <th>Time</th>
                                                          <th>Maker Comments</th>
                                                          <th>Checker Comments</th>
                                                      </tr>
                                                  </thead>
                                                  <tbody></tbody>
                                              </table>
                                          </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content3" aria-labelledby="comm-rates-tab">
                <button class="btn btn-primary btn btn-primary" id="btn-add-new-clause">New</button>
                <table id="polclausesList" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>

                        <th>Clause Heading</th>
                        <th>Clause Type</th>
                        <th>Editable?</th>
                        <th>Clause Wording</th>
                        <th width="5%"></th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content4" aria-labelledby="end-remarks-tab">
                <button class="btn btn-primary btn btn-primary" id="btn-add-new-remark">Select Remark</button>

                <button class="btn btn-primary btn btn-primary" id="btn-save-remark">Save</button>
                <form id="frm-pol-remarks" class="form-horizontal">
                    <input type="hidden" id="pol-remark-pk" name="remarksId">
                    <input type="hidden" id="remark-pk" name="endRemarks.remarkId">
                    <input type="hidden" id="remark-pol-id" name="policy">
                    <div class="item form-group">
                        <textarea class="form-control" rows="5" id="poli-remarks" name="remarks"></textarea>
                    </div>
                </form>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_refund_comments" aria-labelledby="refund-remarks-tab">
                <div class="x_panel">
                    <div class="x_title">
                        <h4>Refund Comments</h4>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <div class="item form-group">
                            <textarea class="form-control" rows="5" id="refund-comments-display" readonly></textarea>
                        </div>
                    </div>
                </div>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content5" aria-labelledby="workflow-tab">
                <button class="btn btn-primary btn btn-primary" id="btn-show-task-history">History</button>
                <img id="proc-main-diagram" class="img-responsive img-rounded proc-diagram" src=""
                     alt="Workflow Process Diagram">
            </div>
        </div>
        <div class="float-right" id="hidden-buttons">

            <%--            <input type="button" class="btn btn-primary float-right"--%>
            <%--                   value="Dispatch" id="btn-dispatch-trans">--%>
            <input type="button" class="btn btn-primary float-right"
                   value="Assign" id="btn-assign-trans">
            <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
                <input type="button" class="btn btn-primary float-right"
                       value="Authorise" id="btn-auth-policy">
            </sec:authorize>
            <sec:authorize access="hasAnyAuthority('SAVE_POLICY')">
                <input type="button" class="btn btn-primary float-right"
                       value="Save" id="btn-add-policy"
                       style="display:none">
            </sec:authorize>
            <sec:authorize access="hasAnyAuthority('PROPOSAL_CONVERSION')">
                <input type="button" class="btn btn-primary float-right"
                       value="Reject" id="btn-unconvert-policy"
                >
            </sec:authorize>
            <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
                <input type="button" class="btn btn-primary float-right"
                       value="Submit" id="btn-make-ready-policy"
                       style="display:none">
            </sec:authorize>
            <%--            <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">--%>
            <%--                    <input type="button" class="btn btn-primary float-right"--%>
            <%--                           value="Assign" id="btn-assign-trans2">--%>
            <%--            </sec:authorize>--%>
            <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
                <input type="button" class="btn btn-primary float-right"
                       value="Submit" id="btn-cancel-general-policy"
                       style="display:none">
            </sec:authorize>
            <sec:authorize access="hasAnyAuthority('MAKE_POLICY_READY')">
                <button id="btn-comment-policy" class="btn btn-primary float-right style" ="display: none;">Resubmission Comments</button>
            </sec:authorize>



        </div>
    </div>
</div>
<!-- Document Comment Modal-->
<div class="modal fade" id="docCommentModal" tabindex="-1" role="dialog" aria-labelledby="docCommentModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="docCommentModalLabel">Document Comments</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="docCommentForm">
                    <input type="hidden" id="commentDocId">
                    <div class="form-group">
                        <label for="docCommentText">Comments</label>
                        <textarea class="form-control" id="docCommentText" rows="4"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" id="saveDocCommentBtn">Save</button>
            </div>
        </div>
    </div>
</div>
<!-- Comment Modal -->
<div id="authcommentModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Add Comment</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="comment-form">
                    <div class="form-group">
                        <label for="policyComment">Comment <span class="text-danger">*</span></label>
                        <textarea id="policyComment" class="form-control" placeholder="Enter your comment"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="button" id="saveCommentButton" class="btn btn-primary">Save Comment</button>
            </div>
        </div>
    </div>
</div>
<div id="addScheduleModal" style="display:none;">
    <form id="addScheduleForm">
        <div id="addFields"></div>
        <button type="submit">Add Schedule</button>
    </form>
</div>

<script>

    $('#saveDocCommentBtn').click(function() {
        const rdId = $('#commentDocId').val();
        const comment = $('#docCommentText').val().trim();

        $.ajax({
            url: 'saveRiskDocComment',
            type: 'GET',
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            data: {
                rdId: rdId,
                comments: comment
            },
            success: function() {
                $('#docCommentModal').modal('hide');
                Swal.fire({
                    title: 'Success',
                    text: 'Comment saved successfully',
                    icon: 'success',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    // Refresh the table
                    if (window.refreshRiskDocsTable) {
                        window.refreshRiskDocsTable();
                    }

                    // Populate policy details
                    saveRiskDocsList();
                });
            },
            error: function(jqXHR) {
                Swal.fire({
                    title: 'Error',
                    text: jqXHR.responseText || 'Failed to save comment',
                    icon: 'error'
                });
            }
        });
    });
    const currentUserId = "<%= request.getAttribute("currentUserId") %>";
    const isPolicyCreator = <%= request.getAttribute("isPolicyCreator") %>;
    const isMaker = full.createdBy === currentUserId || isPolicyCreator;

    // Convert the value of the Risk Identifier/ID to uppercase before form submission
    document.getElementById('risk-id').addEventListener('input', function (e) {
        e.target.value = e.target.value.toUpperCase();
    });
</script>

<jsp:include page="modals/riskmodals.jsp"></jsp:include>
<jsp:include page="modals/endorseriskmodals.jsp"></jsp:include>
<jsp:include page="../modals/prospectmodals.jsp"></jsp:include>