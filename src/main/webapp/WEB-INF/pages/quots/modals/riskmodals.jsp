<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="modal fade" id="sectModal" tabindex="-1" role="dialog"
     aria-labelledby="sectModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="sectModalLabel">
                    Edit/Add Risk Section
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="risk-sect-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="sect-code-pk" name="sectId">
                    <input type="hidden" class="form-control" id="risk-sect-code-pk" name="riskId">
                    <input type="hidden" class="form-control" id="sect-prem-id-pk" name="rateId">
                    <div class="item form-group">
                        <label for="risk-sect-id" class="col-md-3 label-align">Section</label>
                        <div class="col-md-8">
                            <input type="hidden" id="risk-sect-id" name="secId"/>
                            <input type="hidden" id="risk-sect-name">
                            <div id="risk-sect-frm" class="form-control"
                                 select2-url="<c:url value="/protected/uw/policies/riskselectsections"/>">

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="sect-limit-amt" class="col-md-3 label-align">Limit Amount</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="sect-limit-amt" name="amount"
                                   required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="sect-rate" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="sect-rate" name="rate"
                                   required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="sect-div-fact" class="col-md-3 label-align">Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="sect-div-fact" name="divFactor" required>
                                <option value="">Select Rate Type</option>
                                <option value="100">Percentage</option>
                                <option value="1000">Per Mille</option>
                                <option value="1">Amount</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Free Limit</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="sect-free-limit" name="freeLimit"
                                   required>
                        </div>
                    </div>
                    <%--<div class="item form-group">--%>
                    <%--<label for="cou-name" class="col-md-3 label-align">Multiplier Rate</label>--%>

                    <%--<div class="col-md-8">--%>
                    <%--<input type="text" class="editUserCntrls form-control"--%>
                    <%--id="sect-multi-rate" name="multiRate"--%>
                    <%--required>--%>
                    <%--</div>--%>
                    <%--</div>--%>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveRiskSection"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="clauseModal" tabindex="-1" role="dialog"
     aria-labelledby="clauseModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="clauseModalLabel">
                    Edit A Clause
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="pol-clause-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="pol-clause-code" name="qpClauId">
                    <input type="hidden" class="form-control" id="clause-pol-code" name="quotProdCode">
                    <input type="hidden" class="form-control" id="sub-clause-code" name="subclauseId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Clause ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sub-clau-id"
                                   name="clauShtDesc" readonly>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Clause Heading</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="sub-clause-name" name="clauHeading"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Editable</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="editable" id="chk-cl-editable" onclick="return false;">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Clause Wording</label>

                        <div class="col-md-8">
                            <textarea rows="7" cols="20" class="form-control" id="sub-cla-wording"
                                      name="clauWording"></textarea>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savepolClauseBtn"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="familyDetailsModal" tabindex="-1" role="dialog"
     aria-labelledby="familyDetailsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="familyDetailsModalLabel">
                    Edit/Add Family Details
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="med-familyDetails-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="fam-details-id" name="familyId">
                    <input type="hidden" id="fam-category-id" name="category">


                    <div class="item form-group">
                        <div class="item form-group">
                            <label for="family-size" class="col-md-3 label-align">Family Size</label>

                            <div class="col-md-8">
                                <select class="form-control" id="family-size" name="familySize">
                                    <option value="">Select Family Size</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="item form-group">
                            <label for="age-bracket" class="col-md-3 label-align">Age Bracket</label>

                            <div class="col-md-8">
                                <select class="form-control" id="age-bracket" name="agebracket">
                                    <option value="">Select Agent Bracket</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Number Of Families</label>

                            <div class="col-md-8">
                                <input type="number" class="form-control" id="family-count"
                                       name="families" required>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Members Without Cards</label>

                            <div class="col-md-8">
                                <input type="number" class="form-control" id="require-cards"
                                       name="requireCards" required>
                            </div>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveFamilyDetails"
                        type="button" class="btn btn-success">
                    Save
                </button>

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="catRulesModal" tabindex="-1" role="dialog"
     aria-labelledby="categoryModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="catRulesModalLabel">
                    Edit Category Rule
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="cat-rule-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="cat-rule-pk" name="sectId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Rule Description</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="rule-desc-disp"></p>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Rule Value</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="rule-value"
                                   name="value" required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCategoryRule"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="binderRulesModal" tabindex="-1" role="dialog"
     aria-labelledby="binderRulesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="binderRulesModalLabel">
                    Add New Rules
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="binderRulesTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="10%">Rule Description</th>
                            <th width="3%">Rule Value</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="category-rule-form">
                    <input type="hidden" id="rule-category-code" name="catCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveNewRulesBtn"
                        type="button" class="btn btn-primary">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="categoryModal" tabindex="-1" role="dialog"
     aria-labelledby="categoryModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="categoryModalLabel">
                    Edit/Add Category
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="med-category-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="category-id" name="id">
                    <input type="hidden" id="category-quot-id" name="quotation">


                    <div class="item form-group categoryLov">
                        <label for="unit-id" class="col-md-3 label-align">Category</label>

                        <div class="col-md-8">
                            <input type="hidden" id="binder-det-pk" name="binderDetails">
                            <div id="binderdet-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/policies/binderDetails"/>">

                            </div>
                        </div>
                    </div>
                    <div class="item form-group categoryDisplay">
                        <label for="unit-id" class="col-md-3 label-align">Category</label>
                        <div class="col-md-8">
                            <p class="form-control-static" id="binderDetails-Display"></p>
                        </div>
                    </div>
                    <div class="item form-group categoryDetails">
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Sht Desc</label>

                            <div class="col-md-8">
                                <input type="text" class="form-control" id="sht-desc"
                                       name="shtDesc" required>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Description</label>

                            <div class="col-md-8">
                                <textarea class="form-control" rows="3" id="cat-desc" name="desc"></textarea>

                            </div>
                        </div>
                        <div class="item form-group individualDetails">
                            <div class="item form-group">
                                <label for="unit-id" class="col-md-3 label-align">Principal Member Age</label>

                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="principal-age" name="principalAge">

                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="unit-id" class="col-md-3 label-align">Principal Member Gender</label>

                                <div class="col-md-8">
                                    <select class="form-control" id="principal-Gender" name="principalGender"
                                    >
                                        <option value="">Principal Gender</option>
                                        <option value="M">Male</option>
                                        <option value="F">Female</option>
                                    </select>
                                </div>
                            </div>

                            <div class="item form-group">
                                <label for="unit-id" class="col-md-3 label-align">Spouse Age</label>

                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="spouse-age" name="spouseAge">

                                </div>
                            </div>
                            <div class="item form-group">
                                <label for="unit-id" class="col-md-3 label-align">Number of Children</label>

                                <div class="col-md-8">
                                    <input type="text" class="form-control" id="children-count" name="childrenCount">

                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCategory"
                        type="button" class="btn btn-success">
                    Save
                </button>

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="editTaxModal" tabindex="-1" role="dialog"
     aria-labelledby="taxModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="taxModalLabel">
                    Edit Tax
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="med-tax-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="tax-id" name="quotTaxId">
                    <input type="hidden" id="tax-quot-id" name="quotation">

                    <div class="item form-group">
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Trans Code</label>
                            <div class="col-md-8">
                                <p class="form-control-static" id="Trans-Code"></p>
                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Rate</label>

                            <div class="col-md-8">
                                <input type="number" class="form-control" id="tax-rate" name="taxRate" required>

                            </div>
                        </div>
                        <div class="item form-group">
                            <label for="unit-id" class="col-md-3 label-align">Div factor</label>

                            <div class="col-md-8">
                                <input type="number" class="form-control" id="tax-rate-divfact" name="divFactor"
                                       required>

                            </div>
                        </div>

                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveQuoteTax"
                        type="button" class="btn btn-success">
                    Save
                </button>

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="premItemsModal" tabindex="-1" role="dialog"
     aria-labelledby="premItemsModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="premItemsModalLabel">Add A Premium Item</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body table-responsive">
                <table id="new_prem_items_form" class="table table-striped" style="width: 100%">
                    <thead>
                    <tr class="headings">
                        <th width="1%"></th>
                        <th>Premium Item</th>
                        <th>Limit Amount</th>
                        <th>Rate</th>
                        <th>Div Factor</th>
                        <th>Free Limit</th>
                        <th>Multiplier Rate</th>
                    </tr>
                    </thead>
                </table>
                <form id="new-prem-items-form">
                    <input type="hidden" id="prem-item-risk-id" name="riskId"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savenewPremItem"
                        type="button" class="btn btn-success">Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="taxesModal" tabindex="-1" role="dialog"
     aria-labelledby="taxesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="taxesModalLabel">
                    Add New Taxes
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="taxesTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="10%">Trans Code</th>
                            <th width="3%">Rate Type</th>
                            <th width="5%">Tax Level</th>
                            <th width="3%">Rate</th>
                            <th width="3%">Div Factor</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="tax-form">
                    <input type="hidden" id="tax-policy-code" name="taxPolicyCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveNewTaxBtn"
                        type="button" class="btn btn-primary">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="medQuotereportsModal" tabindex="-1" role="dialog"
     aria-labelledby="medQuotereportsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="medQuotereportsModalLabel">Reports</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-6">

                        <ul style="list-style-type: none;" class="reports-links">
                            <li><a
                                    href="<c:url value='/protected/quotes/rpt_med_client_quote'/> "
                                    target="_blank">Client Quote</a></li>
                            <li><a
                                    href="<c:url value='/protected/quotes/rpt_med_quote_summ'/> "
                                    target="_blank">Quote Summary</a></li>
                            <li><a href="javascript:void(0);" onclick="UTILITIES.sendEmail('quote_template');">Send Email</a></li>
                            <li><a
                                    href="javascript:void(0);" onclick="UTILITIES.sendSms();">Send SMS</a></li>

                        </ul>
                    </div>
                    <div class="col-md-6">

                        <ul style="list-style-type: none;">
                            <!--<li><a href="#">Invoice Note</a></li> -->
                        </ul>

                    </div>
                </div>


            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="reportsModal" tabindex="-1" role="dialog"
     aria-labelledby="reportsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <h4 class="modal-title" id="reportsModalLabel">Reports</h4>
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-6">

                        <ul style="list-style-type: none;" class="reports-links">
                            <%--                            <li><a--%>
                            <%--                                    href="<c:url value='/protected/quotes/rpt_quote_info'/> "--%>
                            <%--                                    target="_blank">Client Quote</a></li>--%>
                            <%--                            <li><a--%>
                            <%--                                    href="<c:url value='/protected/quotes/rpt_client_quote'/> "--%>
                            <%--                                    target="_blank">Quote</a></li>--%>
                            <li>
                                <a id="quot-summary-link" href="javascript:void(0);" target="_blank">Quote Summary</a>
                            </li>
                            <li>
                                <a id="quotation-link" href="javascript:void(0);" target="_blank">Quotation</a>
                            </li>
                            <li><a href="javascript:void(0);" onclick="UTILITIES.sendEmail();">Send Email</a></li>
                            <li><a
                                    href="javascript:void(0);" onclick="UTILITIES.sendSms();">Send SMS</a></li>

                        </ul>
                    </div>
                    <div class="col-md-6">

                        <ul style="list-style-type: none;">
                            <!--<li><a href="#">Invoice Note</a></li> -->
                        </ul>

                    </div>
                </div>


            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="benefitLimitModal" tabindex="-1" role="dialog"
     aria-labelledby="benefitLimitModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="benefitLimitModalLabel">
                    Edit Benefit Limit
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="benefit-limit-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="limit-benefit-id" name="sectId">

                    <div class="item form-group cover-limit-det">
                        <label for="unit-id" class="col-md-3 label-align">Limit</label>

                        <div class="col-md-8">
                            <input type="hidden" id="cover-limit-det-pk" name="limit">
                            <input type="hidden" id="cover-limit-value">
                            <div id="cover-limit-frm" class="form-control"
                                 select2-url="<c:url value="/protected/medical/policies/selCoverLimits"/>">

                            </div>
                        </div>
                    </div>
                    <div class="item form-group fund-limit-value">
                        <label for="unit-id" class="col-md-3 label-align">Limit</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control formatcurrency" id="fund-limit-value"
                                   name="fundLimit" required>
                        </div>
                    </div>
                    <div class="item form-group applicablelevel">
                        <label for="unit-id" class="col-md-3 label-align">Applicable At</label>
                        <div class="col-md-8">
                            <select class="form-control" id="benfit-applicable-at" name="applicableAt" required>
                                <option value="">Select Applicable Level</option>
                                <option value="F">Per Family</option>
                                <option value="M">Per Member</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group wait-period">
                        <label for="unit-id" class="col-md-3 label-align">Waiting Period</label>
                        <div class="col-md-8">
                            <input type="text" class="form-control" id="waiting-period"
                                   name="waitPeriod" required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="updateBenefitLimit"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="closeModal" tabindex="-1" role="dialog"
     aria-labelledby="closeModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="closeModalLabel">
                    Reason to Close the Quote
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="close-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Reason</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="5" id="quot-reasons"></textarea>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="closeQuote"
                        type="button" class="btn btn-success">
                    Close
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="assignModal" tabindex="-1" role="dialog"
     aria-labelledby="assignModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="assignModalLabel">Assign Ticket to User</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="assign-user-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Select User</label>

                        <div class="col-md-8">
                            <input type="hidden" id="assign-user-name" name="username"/>
                            <div id="user-assignee-div" class="form-control"
                                 select2-url="<c:url value="/protected/organization/managers"/>">

                            </div>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveTicket"
                        type="button" class="btn btn-success">
                    OK
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="emailModal" tabindex="-1" role="dialog"
     aria-labelledby="emailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="emailModalLabel">Send Email</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="email-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">Email To</label>

                        <div class="col-md-10">
                            <select class="form-control" id="email-to" name="receiverType">
                                <option value="">Select Email To</option>
                                <option value="C">Client</option>
                                <option value="IN">In House</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">Send To</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-send-to" name="sendTo"
                                   readonly>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">CC</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-cc" name="sendCC"
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">BCC</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-bcc" name="sendBcc"
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">Reports</label>

                        <div class="col-md-10">
                            <label class="checkbox-inline"><input type="checkbox" value="CQ">Client Quote</label>
                            <%--                            <label class="checkbox-inline"><input type="checkbox" value="QI">Quote Information</label>--%>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">Subject</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="email-subject" name="subject"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-2 label-align">Email Template</label>

                        <div class="col-md-10">
                            <textarea class="form-control" rows="10" cols="20" id="email-template"
                                      name="message"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="sendEmailForm"
                        type="button" class="btn btn-success">Send
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="smsModal" tabindex="-1" role="dialog"
     aria-labelledby="emailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="smsModalLabel">Send SMS</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="sms-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">SMS To</label>

                        <div class="col-md-10">
                            <select class="form-control" id="sms-to" name="receiverType">
                                <option value="">Select SMS To</option>
                                <option value="C">Client</option>
<%--                                <option value="IN">In House</option>--%>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-2 label-align">Send To</label>

                        <div class="col-md-10">
                            <input type="text" class="editUserCntrls form-control"
                                   id="sms-send-to" name="sendTo"
                                   readonly>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-2 label-align">SMS Template</label>

                        <div class="col-md-10">
                            <textarea class="form-control" rows="10" cols="20" id="sms-template"
                                      name="message"></textarea>
                        </div>
                    </div>

                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="sendSMSForm"
                        type="button" class="btn btn-success">Send
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="newclausesModal" tabindex="-1" role="dialog"
     aria-labelledby="newclausesModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="newclausesModalLabel">Add A Clause</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div class="table-responsive" style="height: 300px !important; overflow: scroll;">
                    <table id="new_clause_tbl" class="table table-striped" style="width: 100%">
                        <thead>
                        <tr class="headings">
                            <th width="1%"></th>
                            <th>Clause Header</th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <form id="new-clause-form">
                    <input type="hidden" id="clause-pol-id" name="quoteProdId"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savenewClause"
                        type="button" class="btn btn-success">Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="newtaxesModal" tabindex="-1" role="dialog"
     aria-labelledby="newtaxesModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="newtaxesModalLabel">Add A Tax</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body table-responsive">
                <table id="new_taxes_tbl" class="table table-striped" style="width: 100%">
                    <thead>
                    <tr class="headings">
                        <th width="1%"></th>
                        <th>Tax Name</th>
                        <th>Tax Rate</th>
                        <th>Rate Type</th>
                    </tr>
                    </thead>
                </table>
                <form id="new-taxes-form">
                    <input type="hidden" id="new-tax-quot-pr-id" name="polId"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savenewTaxes"
                        type="button" class="btn btn-success">Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="converQuotModal" tabindex="-1" role="dialog"
     aria-labelledby="converQuotModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form id="convert-quot-form" class="form-horizontal">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="converQuotModalLabel">Convert Quote to Policy</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <input type="hidden" id="conver-quot-pro-id" name="quotProdId"/>
                    <div class="item form-group">
                        <label for="wef-date" class="col-md-3 label-align">Policy Start Date</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="wef-date">
                                <input type='text' class="form-control float-right" name="startDate"
                                       id="date-from" required/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <input type="hidden" id="polIdField" name="polId">
                    <button type="button" class="btn btn-success" id="convertQuotBtn">
                        Convert
                    </button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Close
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>