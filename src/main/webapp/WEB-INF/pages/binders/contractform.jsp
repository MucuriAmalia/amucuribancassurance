<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/binder/binder.js"/>"></script>
<script type="text/javascript">
    $(document).ready(function () {
        var urlParams = new URLSearchParams(window.location.search);
        var accountId = urlParams.get('acctId');
        var binId = urlParams.get('binId')

        if (accountId) {
            $("#act-bin-code").val(accountId);
        }
        if(binId && accountId){
            $("#act-bin-code").val(accountId);
            populateAcctsForm(binId);
        }
        $('#bind-calc-type').on('change', function () {
            if ($('#bind-calc-type').val() === 'E') {
                $('#integration').hide();
                $('#integration1').hide();
            } else {
                $('#integration').show();
                $('#integration1').show();
            }
        });
    });
</script>
<div class="x_title">
    <h2><i class="fa fa-bars"></i> Add Contract</h2>
    <div class="clearfix"></div>
</div>

<div class="x_panel">
    <div class="col-md-12 col-sm-12">
        <h4 class="float-left blue" style="font-weight: bolder" id="h4pol"></h4>
        <div>
        <input type="button" class="btn btn-primary float-right"
               value="Save" id="saveBinderBtn"/>
        <input action="action" type="button" onclick="history.go(-1);"
               class="btn btn-primary float-right" value="Back" id="renbtn"/>

        </div>



            <div class="x_content">

                <form id="binder-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="bind-code" name="binId">
                    <input type="hidden" class="form-control" id="act-bin-code" name="account">
                    <div class="item form-group">
                        <label for="bin-type" class="col-md-3 label-align">Contract Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="bin-type" name="binType" required>
                                <option value="">Select Contract Type</option>
                                <option value="B">Contract</option>
                                <option value="M">Negotiable</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="bind-id" class="col-md-3 label-align">Contract ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="bind-id"
                                   name="binShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="bind-name" class="col-md-3 label-align">Contract Name</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="bind-name" name="binName"
                                   required>
                        </div>
                    </div>


                    <div class="item form-group">
                        <label for="bind-min-prem" class="col-md-3 label-align">Min Premium</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="bind-min-prem" name="minPrem"
                                   required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="prd-code" class="col-md-3 label-align">Product</label>

                        <div class="col-md-8">
                            <input type="hidden" id="prd-id" rv-value="product.proCode" name="product"/>
                            <input type="hidden" id="pr-name">
                            <div id="prd-code" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/selproducts"/>" >

                            </div>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="cur-def" class="col-md-3 label-align">Currency</label>

                        <div class="col-md-8">
                            <input type="hidden" id="cur-id"  name="currency"/>
                            <input type="hidden" id="cur-name">
                            <div id="cur-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/activeCurrencies"/>" >

                            </div>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="bind-calc-type" class="col-md-3 label-align">Calculator Type</label>

                        <div class="col-md-8">
                                <select class="form-control" id="bind-calc-type" name="calculatorType" required>
                                    <option value="">Select Calculator Type</option>
                                    <option value="E">Excel</option>
                                    <option value="I">Integration</option>
                                    <option value="N">Non-Motor</option>
                                </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="bind-admin-fee-type" class="col-md-3 label-align">Admin Fee Applicable</label>

                        <div class="col-md-8">
                            <select class="form-control" id="bind-admin-fee-type" name="adminFeeActiveStatus" required>
                                <option value="">Select an Option</option>
                                <option value="Y">Yes</option>
                                <option value="N">No</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group" id="integration">
                        <label for="bind-quote-url" class="col-md-3 label-align">Quote/Proposal Creation URL</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="bind-quote-url" name="premiumUrl">
                        </div>
                    </div>

                    <div class="item form-group" id="integration1">
                        <label for="bind-policy-url" class="col-md-3 label-align">Policy Creation URL</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="bind-policy-url" name="policyUrl">
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="bin-remarks" class="col-md-3 label-align">Contract Remarks</label>

                        <div class="col-md-8">
								<textarea rows="3" cols="20" name="binRemarks" id="bin-remarks"
                                          class="editUserCntrls form-control"></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="chk-active" class="col-md-3 label-align">Active Indicator</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="active" id="chk-active">
                            </label>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="bin-default" class="col-md-3 label-align">Default?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="binDefault" id="bin-default">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group binder-fund">
                        <label for="bin-fund-binder" class="col-md-3 label-align">Fund Contract?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="fundBinder" id="bin-fund-binder">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group binderterms">
                        <div class="item form-group">
                            <label for="bind-min-term" class="col-md-3 label-align">Min Term</label>

                            <div class="col-md-8">
                                <input type="text" class="editUserCntrls form-control"
                                       id="bind-min-term" name="minTerm"
                                       required>
                            </div>
                        </div>

                        <div class="item form-group">
                            <label for="bind-max-term" class="col-md-3 label-align">Max Term</label>

                            <div class="col-md-8">
                                <input type="text" class="editUserCntrls form-control"
                                       id="bind-max-term" name="maxTerm"
                                       required>
                            </div>
                        </div>

                        <div class="item form-group">
                            <label for="premium-age-type" class="col-md-3 label-align">Age Type</label>

                            <div class="col-md-8">
                                <select class="form-control" id="premium-age-type" name="premiumAgeType" required>
                                    <option value="">Select Age Type</option>
                                    <option value="N">ANB</option>
                                    <option value="L">ALB</option>
                                </select>
                            </div>
                        </div>
                        <div class="item form-group med-cover-type">

                            <label for="medical-cover-type" class="col-md-3 label-align">Type</label>

                            <div class="col-md-8">
                                <select class="form-control" id="medical-cover-type" name="medicalCoverType" required>
                                    <<option value="">Select Type</option>
                                    <option value="I">Individual</option>
                                    <option value="G">Group</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </form>
            </div>

<%--            <div class="modal-footer">--%>
<%--                <button data-loading-text="Saving..." id="saveBinderBtn"--%>
<%--                        type="button" class="btn btn-success">--%>
<%--                    Save--%>
<%--                </button>--%>
<%--                <button type="button" class="btn btn-default" data-dismiss="modal">--%>
<%--                    Cancel--%>
<%--                </button>--%>
<%--            </div>--%>
        </div>
    </div>


