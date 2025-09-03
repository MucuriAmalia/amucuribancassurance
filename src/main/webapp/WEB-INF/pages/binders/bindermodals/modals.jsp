<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="modal fade" id="binderModal" tabindex="-1" role="dialog"
     aria-labelledby="binderModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="binderModalLabel">
                    Edit/Add Contract
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

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
                                   name="binShtDesc" required>
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
                        <label for="bind-pol-no" class="col-md-3 label-align">Contract Policy No</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="bind-pol-no" name="binPolNo"
                                   required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="bind-pol-no" class="col-md-3 label-align">Min Premium</label>

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
                                 select2-url="<c:url value="/protected/setups/binders/selproducts"/>">

                            </div>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="cur-def" class="col-md-3 label-align">Currency</label>

                        <div class="col-md-8">
                            <input type="hidden" id="cur-id" name="currency"/>
                            <input type="hidden" id="cur-name">
                            <div id="cur-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/activeCurrencies"/>">

                            </div>
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
                                    <
                                    <option value="">Select Type</option>
                                    <option value="I">Individual</option>
                                    <option value="G">Group</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderBtn"
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


<div class="modal fade" id="clonebinderModal" tabindex="-1" role="dialog"
     aria-labelledby="clonebinderModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="clonebinderModalLabel">
                    Clone Binder
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="clonefrombinder-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="clonefrombind-name" class="col-md-3 label-align">Copy From</label>

                        <div class="col-md-8">
                            <p class="form-control-static"
                               id="clonefrombind-name"
                               required>
                        </div>
                    </div>
                </form>
                <hr>

                <form id="clonebinder-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="clonebind-code" name="cloneFromBinCode">
                    <div class="item form-group">
                        <label for="cloneacc-frm" class="col-md-3 label-align">Intermediary</label>

                        <div class="col-md-8">
                            <input type="hidden" id="cloneacc-id" rv-value="account.acctId" name="cloneAccId"/>
                            <input type="hidden" id="cloneacc-name">
                            <div id="cloneacc-frm" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/selAccounts"/>">

                            </div>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="clonebin-type" class="col-md-3 label-align">Bind Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="clonebin-type" name="cloneBinType" required>
                                <option value="">Select Bind Type</option>
                                <option value="B">Binder</option>
                                <option value="M">Open Market</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="clonebind-id" class="col-md-3 label-align">Binder ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="clonebind-id"
                                   name="cloneBinShtDesc" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="clonebind-name" class="col-md-3 label-align">Bind Name</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="clonebind-name" name="cloneBinName"
                                   required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="clonebind-pol-no" class="col-md-3 label-align">Bind Policy No</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="clonebind-pol-no" name="cloneBinPolNo"
                                   required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="clonepr-name" class="col-md-3 label-align">Product</label>

                        <div class="col-md-8">
                            <input type="hidden" id="cloneprd-id" rv-value="product.proCode" name="cloneProdId"/>
                            <div class="col-md-8">
                                <p class="form-control-static"
                                   id="clonepr-name"
                                   required>
                            </div>
                        </div>
                    </div>


                    <div class="item form-group">
                        <label for="clonebin-remarks" class="col-md-3 label-align">Bind Remarks</label>

                        <div class="col-md-8">
								<textarea rows="3" cols="20" name="cloneBinRemarks" id="clonebin-remarks"
                                          class="editUserCntrls form-control"></textarea>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCloneBinderBtn"
                        type="button" class="btn btn-success">
                    Clone
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="quizModal" tabindex="-1" role="dialog"
     aria-labelledby="quizModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="quizModalLabel">
                    Edit/Add Questions
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="quiz_model">
                <form id="quiz-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="pk-code" name="bqdid">
                    <input type="hidden" class="form-control" id="quiz-binder-code" name="binder">
                    <div class="item form-group">
                        <label for="question-id" class="col-md-3 label-align">Question Id</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="question-id"
                                   name="questionShtDesc" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="quiz-name" class="col-md-3 label-align">Question Name</label>

                        <div class="col-md-8">
                            <textarea rows="4" class="form-control" id="quiz-name"
                                      name="questionname" required></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="quiz-type" class="col-md-3 label-align">Choice Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="quiz-type" name="questiontype" required>
                                <option value="">Select Choice Type</option>
                                <option value="text">Text</option>
                                <option value="radiogroup">Radio Group</option>
                                <option value="checkbox">Checkbox</option>
                                <option value="dropdown">Drop Down</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="question-mandatory" class="col-md-3 label-align">Mandatory?</label>

                        <div class="col-md-8">
                            <select class="form-control" id="question-mandatory" name="questionMandatory" required>
                                <option value="">Mandatory?</option>
                                <option value="Y">Yes</option>
                                <option value="N">No</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="triggerquiz-frm" class="col-md-3 label-align">Triggered By</label>

                        <div class="col-md-8">
                            <input type="hidden" id="trigger-quiz-id" name="triggerByQuiz"/>
                            <input type="hidden" id="trigger-quiz-name"/>
                            <div id="triggerquiz-frm" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/selQuestion"/>">

                            </div>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="triggeranswer-frm" class="col-md-3 label-align">Trigger Answer</label>

                        <div class="col-md-8">
                            <input type="hidden" id="answer-id" name="triggerByQuizAnswer"/>
                            <input type="hidden" id="answer-name"/>
                            <div id="triggeranswer-frm" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/selQuestionChoice"/>">

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="quiz-order" class="col-md-3 label-align">Question Order</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="quiz-order" name="questionOrder">

                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveQuestion"
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

<div class="modal fade" id="quizChoiceModal" tabindex="-1" role="dialog"
     aria-labelledby="quizChoiceModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="quizChoiceModalLabel">
                    Edit/Add Question Choices
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="quiz-choice-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="choice-pk-code" name="bqcId">
                    <input type="hidden" class="form-control" id="choice-quiz-code" name="questions">
                    <div class="item form-group">
                        <label for="choice-name" class="col-md-3 label-align">Choice </label>

                        <div class="col-md-8">
                            <textarea rows="4" class="form-control" id="choice-name"
                                      name="choice" required></textarea>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveQuestionChoice"
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
<div class="modal fade" id="provContractModal" tabindex="-1" role="dialog"
     aria-labelledby="provContractModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="provContractModalLabel">Edit/Add Service Provider Contract</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="prov-contract-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="provtype-code" name="spcId">
                    <input type="hidden" class="form-control" id="provtype-contract-code" name="contractNo">
                    <input type="hidden" class="form-control" id="contrct-bind-code" name="bindCode"/>
                    <div class="item form-group">
                        <label for="contract-no" class="col-md-3 label-align">
                            Contract No</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="contract-no"></p>
                        </div>
                    </div>
                    <div class="item form-group provider-namelov">
                        <label for="provider-id" class="col-md-3 label-align">
                            Service Provider</label>
                        <div class="item form-group">
                            <div class="col-md-8">
                                <input type="hidden" id="provider-id" name="serviceProviders"/>
                                <div id="provider-frm" class="form-control"
                                     select2-url="<c:url value="/protected/medical/setups/providersel"/>">
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group provider-diaplay">
                        <label for="provider-name" class="col-md-3 label-align">
                            Service Provider</label>
                        <div class="item form-group">
                            <div class="col-md-8">
                                <p class="form-control-static" id="provider-name"></p>
                            </div>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="provider-type" class="col-md-3 label-align">
                            Provider Type</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="provider-type"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="contract-type" class="col-md-3 label-align">
                            Contract Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="contract-type" name="contractType"
                                    required>
                                <option value="">Select Contract Type</option>
                                <option value="P">Permanent</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="from-date" class="col-md-3 label-align">
                            Contract From</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="wef-date">
                                <input class="form-control float-right" name="wefDate"
                                       id="from-date" required/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="contract-to-date" class="col-md-3 label-align">
                            Contract To</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="wet-date">
                                <input class="form-control float-right" name="wetDate"
                                       id="contract-to-date" required/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="contract-status" class="col-md-3 label-align">
                            Status</label>

                        <div class="col-md-8">
                            <select class="form-control" id="contract-status" name="status"
                                    required>
                                <option value="">Select Status</option>
                                <option value="A">Active</option>
                                <option value="I">InActive</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="contr-notes" class="col-md-3 label-align">
                            Remarks</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="3" id="contr-notes" name="notes"></textarea>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveProvderContractsBtn"
                        type="button" class="btn btn-success">Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="editExclusionModal" tabindex="-1" role="dialog"
     aria-labelledby="exclusionModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="categoryModalLabel">
                    Edit Exclusion
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="edit-exclusion-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="exclusion-id" name="beId">
                    <input type="hidden" id="exclusion-binder-id" name="binder">
                    <input type="hidden" name="chronic" id="exclusion-chronic-id">
                    <div class="item form-group">
                        <label for="binder-excl-id" class="col-md-3 label-align">ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="binder-excl-id"
                                   name="clauShtDesc" disabled>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="exclusion-desc" class="col-md-3 label-align">Description</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="3" id="exclusion-desc" name="desc" disabled></textarea>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cost-per-claim" class="col-md-3 label-align">Cost Per Claim</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="cost-per-claim" name="costPerClaim">

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="upper-Limit" class="col-md-3 label-align">Upper Limit</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="upper-Limit" name="upperLimit">

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="waiting-days" class="col-md-3 label-align">Waiting Days</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="waiting-days" name="waitingDays">

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="exclusion-chronic" class="col-md-3 label-align">Chronic?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" id="exclusion-chronic">
                            </label>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderExclusion"
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

<div class="modal fade" id="binderDetModal" tabindex="-1" role="dialog"
     aria-labelledby="binderDetModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="binderDetModalLabel">
                    Add Binder Details
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="subclassCoverTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Sub class Id</th>
                            <th width="4%">Sub class Name</th>
                            <th width="12%">Cover Type Id</th>
                            <th width="12%">Cover Type</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="binder-det-form">
                    <input type="hidden" id="bind-prod-bind-code" name="bindCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderDetBtn"
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


<div class="modal fade" id="premRatesModal" tabindex="-1" role="dialog"
     aria-labelledby="premRatesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="premRatesModalLabel">
                    Edit/Add Premium Rates
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="branch_model">

                <form id="prem-rates-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="prem-code" name="Id">
                    <input type="hidden" class="form-control" id="prem-binder-det" name="binderDet">
                    <div class="item form-group">
                        <label for="sect-modal" class="col-md-3 label-align">Premium Item</label>

                        <div class="col-md-8">
                            <input type="hidden" id="prem-sect-id" rv-value="product.proCode" name="section"/>
                            <input type="hidden" id="sec-name"/>
                            <p class="form-control-static" id="p-sect-modal"></p>
                            <div id="section-modal">
                                <div id="sect-modal" class="form-control"
                                     select2-url="<c:url value="/protected/setups/binders/selSections"/>">


                                </div>
                            </div>

                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="chk-rates-appli" class="col-md-3 label-align">Range Applicable</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="rangeApplicable" id="chk-rates-appli">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="prem-range-type" class="col-md-3 label-align">Range Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="prem-range-type" name="rangeType" required>
                                <option value="">Range Type</option>
                                <option value="AG">Age</option>
                                <option value="AM">Amount</option>
                                <option value="TN">Tonnage</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="range-from" class="col-md-3 label-align">Range From</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="range-from"
                                   name="rangeFrom" disabled>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="range-to" class="col-md-3 label-align">Range To</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="range-to" name="rangeTo"
                                   disabled>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="prem-div-fact" class="col-md-3 label-align">Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="prem-div-fact" name="divFactor" required>
                                <option value="">Select Rate Type</option>
                                <option value="100">Percentage</option>
                                <option value="1000">Per Mile</option>
                                <option value="1">Amount</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="prem-rate" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="prem-rate" name="rate"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="prem-free-limit" class="col-md-3 label-align">Free Limit</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="prem-free-limit" name="freeLimit"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="prorated-full" class="col-md-3 label-align">Prorated Full</label>

                        <div class="col-md-8">
                            <select class="form-control" id="prorated-full" name="proratedFull" required>
                                <option value="">Select prorated full</option>
                                <option value="P">Prorated</option>
                                <option value="F">Full</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="prem-min-prem" class="col-md-3 label-align">Min Premium</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="prem-min-prem" name="minPremium"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="chk-rates-mandatory" class="col-md-3 label-align">Mandatory</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="mandatory" id="chk-rates-mandatory">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="chk-prem-active" class="col-md-3 label-align">Active Indicator</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="active" id="chk-prem-active">
                            </label>

                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savepremratesBtn"
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


<div class="modal fade" id="subclauseModal" tabindex="-1" role="dialog"
     aria-labelledby="subclauseModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="subclauseModalLabel">Select Clause</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="clause-name-search" class="col-md-3 label-align">Clause</label>

                        <div class="col-md-6">
                            <input type="text" class="form-control" id="clause-name-search"
                            >
                        </div>
                        <div class="col-md-1">
                            <button id="searchClauses"
                                    type="button" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="subclausestbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Clause Id</th>
                            <th width="12%">Clause Heading</th>
                            <th width="12%">Editable?</th>
                            <th width="12%">Mandatory?</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
                <form id="binder-clauses-form">
                    <input type="hidden" id="cla-det-code" name="detCode"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderclausesBtn"
                        type="button" class="btn btn-success">Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="commRatesModal" tabindex="-1" role="dialog"
     aria-labelledby="commRatesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="commRatesModalLabel">
                    Edit/Add Commission Rates
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="comm-rates-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="comm-code" name="commId">
                    <input type="hidden" class="form-control" id="comm-binder-det" name="bindersDef">

                    <div class="item form-group">
                        <label for="comm-range-from" class="col-md-3 label-align">Range From</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="comm-range-from"
                                   name="commRangeFrom" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-range-to" class="col-md-3 label-align">Range To</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="comm-range-to" name="commRangeTo"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-rate-type" class="col-md-3 label-align">Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="comm-rate-type" name="rateType">
                                <option value="">Select Rate Type</option>
                                <option value="P">Percentage</option>
                                <option value="M">Per Mille</option>
                                <option value="A">Amount</option>

                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="comm-rate" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="comm-rate" name="commRate"
                                   required>
                        </div>
                    </div>


                    <div class="item form-group">
                        <label for="comm-div-fact" class="col-md-3 label-align">Div Factor</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="comm-div-fact" name="commDivFactor"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rev-item-def" class="col-md-3 label-align">Revenue Item</label>

                        <div class="col-md-8">
                            <input type="hidden" id="rev-item-id" name="revenueItems"/>
                            <input type="hidden" id="rev-item-name">
                            <div id="rev-item-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/selCommRevenueItems"/>">

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="applicableAt" class="col-md-3 label-align">Applicable At</label>
                        <div class="col-md-8">
                        <select id="applicableAt" class="form-control" name="applicableAt" required>
                            <option value="">Select Applicable At</option>
                            <option value="NB">New Business</option>
                            <option value="RN">Renewal</option>
                        </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-active" class="col-md-3 label-align">Active?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="active" id="comm-active">
                            </label>
                        </div>
                    </div>



                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savecommratesBtn"
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

<div class="modal fade" id="sub-commRatesModal" tabindex="-1" role="dialog"
     aria-labelledby="sub-commRatesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="sub-commRatesModalLabel">
                    Edit/Add Commission Rates
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="sub-comm-rates-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="sub-comm-code" name="commId">
                    <input type="hidden" class="form-control" id="sub-comm-binder-det" name="bindersDef">

                    <div class="item form-group">
                        <label for="rev-acct-def" class="col-md-3 label-align">Account Types</label>

                        <div class="col-md-8">
                            <input type="hidden" id="rev-acct-id" name="accountTypes"/>
                            <input type="hidden" id="rev-acct-name">
                            <div id="rev-acct-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/subComAcctTypes"/>">

                            </div>
                        </div>
                    </div>


                    <div class="item form-group">
                        <label for="comm-range-from" class="col-md-3 label-align">Range From</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sub-comm-range-from"
                                   name="commRangeFrom" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-range-to" class="col-md-3 label-align">Range To</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="sub-comm-range-to" name="commRangeTo"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="sub-comm-rate-type" class="col-md-3 label-align">Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="sub-comm-rate-type" name="rateType">
                                <option value="">Select Rate Type</option>
                                <option value="P">Percentage</option>
                                <option value="M">Per Mille</option>
                                <option value="A">Amount</option>

                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="comm-rate" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="sub-comm-rate" name="commRate"
                                   required>
                        </div>
                    </div>


                    <div class="item form-group">
                        <label for="comm-div-fact" class="col-md-3 label-align">Div Factor</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="sub-comm-div-fact" name="commDivFactor"
                                   required>
                        </div>
                    </div>
                     <div class="item form-group">
                          <label for="applicableAt" class="col-md-3 label-align">Applicable At</label>
                          <div class="col-md-8">
                          <select id="applicableAt" class="form-control" name="applicableAt" required>
                              <option value="">Select Applicable At</option>
                              <option value="NB">New Business</option>
                              <option value="RN">Renewal</option>
                          </select>
                          </div>
                     </div>
                    <div class="item form-group">
                        <label for="sub-comm-active" class="col-md-3 label-align">Active?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="active" id="sub-comm-active">
                            </label>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="sub-savecommratesBtn"
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

<div class="modal fade" id="adminFeeModal" tabindex="-1" role="dialog"
     aria-labelledby="adminFeeModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="sub-adminFeeModalLabel">
                    Edit/Add Admin Fee Rates
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="admin-fee-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="admin-fee-code" name="adminId">
                    <input type="hidden" class="form-control" id="admin-fee-binder-det" name="binderId">
                    <div class="item form-group">
                        <label for="comm-range-from" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="admin-rate"
                                   name="adminFeeRate" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="admin-rate-type" class="col-md-3 label-align">Admin Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="admin-rate-type" name="adminFeeRateType">
                                <option value="">Select Rate Type</option>
                                <option value="Percent">Percent</option>
                                <option value="Amount">Amount</option>

                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-range-from" class="col-md-3 label-align">VAT Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="vat-rate"
                                   name="vatRate" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="vat-rate-type" class="col-md-3 label-align">VAT Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="vat-rate-type" name="vateRateType">
                                <option value="">Select Rate Type</option>
                                <option value="Percent">Percent</option>
                                <option value="Amount">Amount</option>

                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-range-from" class="col-md-3 label-align">Excise Duty Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="excise-rate"
                                   name="exciseRate" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="excise-rate-type" class="col-md-3 label-align">Excise Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="excise-rate-type" name="exciseRateType">
                                <option value="">Select Rate Type</option>
                                <option value="Percent">Percent</option>
                                <option value="Amount">Amount</option>

                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="sub-comm-active" class="col-md-3 label-align">Active?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="status" id="admin-fee-active">
                            </label>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveadminFeeBtn"
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

<div class="modal fade" id="commissionRatesModal" tabindex="-1" role="dialog"
     aria-labelledby="commissionRatesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="commissionRatesModalLabel">
                    Edit/Add Commission Rates
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="commission-rates-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="com-code" name="commId">
                    <input type="hidden" class="form-control" id="comm-binder-code" name="binderDef">


                    <div class="item form-group">
                        <label for="comm-term-from" class="col-md-3 label-align">Term From</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="comm-term-from"
                                   name="commTermFrom" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-term-to" class="col-md-3 label-align">Term To</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="comm-term-to" name="commTermTo"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-year-from" class="col-md-3 label-align">Year From</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="comm-year-from"
                                   name="commYearFrom" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-year-to" class="col-md-3 label-align">Year To</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="comm-year-to" name="commYearTo"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-pay-freq" class="col-md-3 label-align">Pay Frequency</label>

                        <div class="col-md-8">
                            <select class="form-control" id="comm-pay-freq" name="frequency">
                                <option value="">Select Pay Frequency</option>
                                <option value="M">Monthly</option>
                                <option value="Q">Quarterly</option>
                                <option value="S">Semi-Annual</option>
                                <option value="A">Annual</option>
                                <option value="SG">Single</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="com-rate" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="com-rate" name="commRate"
                                   required>
                        </div>
                    </div>


                    <div class="item form-group">
                        <label for="com-div-fact" class="col-md-3 label-align">Div Factor</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="com-div-fact" name="commDivFactor"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-from-date" class="col-md-3 label-align">
                            Date From</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="comm-wef-date">
                                <input class="form-control float-right" name="wefDate"
                                       id="comm-from-date" required/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-to-date" class="col-md-3 label-align">
                            Date To</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="comm-wet-date">
                                <input class="form-control float-right" name="wetDate"
                                       id="comm-to-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savelifecommratesBtn"
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

<div class="modal fade" id="sub-commissionRatesModal" tabindex="-1" role="dialog"
     aria-labelledby="sub-commissionRatesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="sub-commissionRatesModalLabel">
                    Edit/Add Commission Rates
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="sub-commission-rates-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="sub-com-code" name="commId">
                    <input type="hidden" class="form-control" id="sub-comm-binder-code" name="binderDef">

                    <div class="item form-group">
                        <label for="rev-acct-def" class="col-md-3 label-align">Account Types</label>

                        <div class="col-md-8">
                            <input type="hidden" id="sub-rev-acct-id" name="accountTypes"/>
                            <input type="hidden" id="sub-rev-acct-name">
                            <div id="sub-rev-acct-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/subComAcctTypes"/>">

                            </div>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="comm-term-from" class="col-md-3 label-align">Term From</label>

                        <div class="col-md-8">
                            <input type="number" class="form-control" id="sub-comm-term-from"
                                   name="commTermFrom" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-term-to" class="col-md-3 label-align">Term To</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="sub-comm-term-to" name="commTermTo"
                                   required>
                        </div>
                    </div>
<%--                    <div class="item form-group">--%>
<%--                        <label for="comm-year-from" class="col-md-3 label-align">Year From</label>--%>

<%--                        <div class="col-md-8">--%>
<%--                            <input type="number" class="form-control" id="sub-comm-year-from"--%>
<%--                                   name="commYearFrom" required>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                    <div class="item form-group">--%>
<%--                        <label for="comm-year-to" class="col-md-3 label-align">Year To</label>--%>

<%--                        <div class="col-md-8">--%>
<%--                            <input type="number" class="editUserCntrls form-control"--%>
<%--                                   id="sub-comm-year-to" name="commYearTo"--%>
<%--                                   required>--%>
<%--                        </div>--%>
<%--                    </div>--%>
                    <div class="item form-group">
                        <label for="comm-pay-freq" class="col-md-3 label-align">Pay Frequency</label>

                        <div class="col-md-8">
                            <select class="form-control" id="sub-comm-pay-freq" name="frequency">
                                <option value="">Select Pay Frequency</option>
                                <option value="M">Monthly</option>
                                <option value="Q">Quarterly</option>
                                <option value="S">Semi-Annual</option>
                                <option value="A">Annual</option>
                                <option value="SG">Single</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="com-rate" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="sub-com-rate" name="commRate"
                                   required>
                        </div>
                    </div>


                    <div class="item form-group">
                        <label for="com-div-fact" class="col-md-3 label-align">Div Factor</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="sub-com-div-fact" name="commDivFactor"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-from-date" class="col-md-3 label-align">
                            Date From</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="sub-comm-wef-date">
                                <input class="form-control float-right" name="wefDate"
                                       id="sub-comm-from-date" required/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comm-to-date" class="col-md-3 label-align">
                            Date To</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="sub-comm-wet-date">
                                <input class="form-control float-right" name="wetDate"
                                       id="sub-comm-to-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="sub-savelifecommratesBtn"
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


<div class="modal fade" id="binderPerilModal" tabindex="-1" role="dialog"
     aria-labelledby="binderPerilModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="binderPerilModalLabel">Select Peril</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="peril-name-search" class="col-md-3 label-align">Peril</label>

                        <div class="col-md-6">
                            <input type="text" class="form-control" id="peril-name-search"
                            >
                        </div>
                        <div class="col-md-1">
                            <button id="searchBinderPerils"
                                    type="button" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="binderSectPerilTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Peril Id</th>
                            <th width="12%">Peril</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
                <form id="bind-peril-form">
                    <input type="hidden" id="peril-binder-det" name="binderDetail"/>
                    <input type="hidden" id="peril-sect-id" name="sectId"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderDetPeril"
                        type="button" class="btn btn-success">Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="medCoversModal" tabindex="-1" role="dialog"
     aria-labelledby="medCoversModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="medCoversModalLabel">
                    Edit/Add Medical Covers
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="med-covers-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="med-cover-code" name="id">
                    <input type="hidden" class="form-control" id="med-cover-binder-det" name="binderDet">
                    <div class="item form-group">
                        <label for="cover-modal" class="col-md-4 label-align">Cover</label>

                        <div class="col-md-8">
                            <input type="hidden" id="med-sect-id" name="section"/>
                            <input type="hidden" id="med-sec-name"/>
                            <p class="form-control-static" id="p-cover"></p>
                            <div id="med-section-modal">
                                <div id="cover-modal" class="form-control"
                                     select2-url="<c:url value="/protected/setups/binders/selSections"/>">
                                </div>
                            </div>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="medical-main-cov" class="col-md-4 label-align">Main Cover?</label>

                        <div class="col-md-8">
                            <select class="form-control" id="medical-main-cov" name="mainCover" required>
                                <option value="">Select</option>
                                <option value="Y">Yes</option>
                                <option value="N">No</option>

                            </select>
                        </div>
                    </div>

                    <div class="item form-group parent-cover">
                        <label for="main-cover-modal" class="col-md-4 label-align">Parent Cover</label>

                        <div class="col-md-8">
                            <input type="hidden" id="main-med-sect-id" name="mainSection"/>
                            <input type="hidden" id="main-med-sec-name"/>
                            <p class="form-control-static" id="p-parent-cover"></p>
                            <div id="main-med-section-modal">
                                <div id="main-cover-modal" class="form-control"
                                     select2-url="<c:url value="/protected/setups/binders/selSections"/>">
                                </div>
                            </div>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="med-prorated-full" class="col-md-4 label-align">Prorated Full</label>

                        <div class="col-md-8">
                            <select class="form-control" id="med-prorated-full" name="proratedFull" required>
                                <option value="">Select prorated full</option>
                                <option value="P">Prorated</option>
                                <option value="F">Full</option>
                            </select>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="med-min-prem" class="col-md-4 label-align">Min Premium</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="med-min-prem" name="minPremium"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="med-applicable-at" class="col-md-4 label-align">Applicable At</label>

                        <div class="col-md-8">
                            <select class="form-control" id="med-applicable-at" name="applicableAt" required>
                                <option value="">Select Applicable At</option>
                                <option value="F">Per Family</option>
                                <option value="M">Per Member</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="med-depends-gender" class="col-md-4 label-align">Dep. on Gender?</label>
                        <div class="col-md-8 checkbox">
                            <label>
                                <input type="checkbox" name="dependsOnGender" id="med-depends-gender">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group gender-select">
                        <label for="med-cov-gender" class="col-md-4 label-align">Gender</label>

                        <div class="col-md-8">
                            <select class="form-control" id="med-cov-gender" name="gender" required>
                                <option value="">Select Gender</option>
                                <option value="F">Female</option>
                                <option value="M">Male</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="bin-fund-cover-man" class="col-md-4 label-align">Fund Mand.?</label>
                        <div class="col-md-8 checkbox">
                            <label>
                                <input type="checkbox" name="fundCoverMand" id="bin-fund-cover-man">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="med-wait-prd" class="col-md-4 label-align">Waiting Period(Months)</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="med-wait-prd" name="waitPeriod"
                                   required>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveMedicalCover"
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


<div class="modal fade" id="medCardsModal" tabindex="-1" role="dialog"
     aria-labelledby="medCoversModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="medCardsModalLabel">
                    Edit/Add Medical Card Types
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="med-cards-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="med-card-code" name="id">
                    <input type="hidden" class="form-control" id="med-card-binder" name="binder">
                    <div class="item form-group">
                        <label for="cardtype-modal" class="col-md-3 label-align">Card Type</label>

                        <div class="col-md-8">
                            <input type="hidden" id="med-card-id" name="cardTypes"/>
                            <input type="hidden" id="med-cardtype-name"/>
                            <p class="form-control-static" id="p-cardtype"></p>
                            <div id="med-cardtype-modal">
                                <div id="cardtype-modal" class="form-control"
                                     select2-url="<c:url value="/protected/setups/binders/selCardTypes"/>">
                                </div>
                            </div>

                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="med-card-fee" class="col-md-3 label-align">New Card Fee</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="med-card-fee" name="cardFee"
                                   required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="med-reissue-fee" class="col-md-3 label-align">Card Re-Issue Fee</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="med-reissue-fee" name="cardReissueFee"
                                   required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="med-service-charge" class="col-md-3 label-align">Service Charge</label>

                        <div class="col-md-8">
                            <input type="number" class="editUserCntrls form-control"
                                   id="med-service-charge" name="serviceCharge"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="card-prorated-full" class="col-md-3 label-align">Service Charged Prorated?</label>

                        <div class="col-md-8">
                            <select class="form-control" id="card-prorated-full" name="serviceProrated" required>
                                <option value="">Select prorated</option>
                                <option value="Y">Yes</option>
                                <option value="N">No</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="card-vat_applicable" class="col-md-3 label-align">VAT Applicable?</label>

                        <div class="col-md-8">
                            <select class="form-control" id="card-vat_applicable" name="vatApplicable" required>
                                <option value="">Select VAT Applicable</option>
                                <option value="Y">Yes</option>
                                <option value="N">No</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="card-from-date" class="col-md-3 label-align">
                            Date From</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="card-wef-date">
                                <input class="form-control float-right" name="wefDate"
                                       id="card-from-date" required/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="card-to-date" class="col-md-3 label-align">
                            Date To</label>

                        <div class="col-md-8">
                            <div class='input-group date datepicker-input' id="card-wet-date">
                                <input class="form-control float-right" name="wetDate"
                                       id="card-to-date"/>
                                <div class="input-group-addon">
                                    <span class="fa fa-calendar"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveMedCardTypes"
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
<div class="modal fade" id="bindRulesModal" tabindex="-1" role="dialog"
     aria-labelledby="bindRulesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="bindRulesModalLabel">
                    Edit/Add Binder Rules
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="bind-rules-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="bind-rules-code" name="ruleId">
                    <input type="hidden" class="form-control" id="bind-rules-binder" name="binder">
                    <div class="item form-group">
                        <label for="check-code" class="col-md-3 label-align">Rule ID</label>

                        <div class="col-md-8">
                            <input type="hidden" id="check-id" name="checks"/>
                            <input type="hidden" id="check-name">
                            <div id="check-code" class="form-control"
                                 select2-url="<c:url value="/protected/setups/binders/selChecks"/>">

                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rule-value" class="col-md-3 label-align">Rule Value</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="rule-value" name="value"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rule-desc" class="col-md-3 label-align">Rule Description</label>

                        <div class="col-md-8">
                            <textarea class="form-control" rows="3" id="rule-desc" name="desc" required></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rule-mandatory" class="col-md-3 label-align">Mandatory?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="mandatory" id="rule-mandatory">
                            </label>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderRules"
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

<div class="modal fade" id="exclusionsModal" tabindex="-1" role="dialog"
     aria-labelledby="exclusionsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="exclusionsModalLabel">
                    Add Exclusions
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

                <div>

                </div>
            </div>

            <div class="modal-body">
                <div style="height: 300px !important; overflow: scroll;">
                    <div class="item form-group">
                        <label for="exclusion-type" class="label-align col-md-3">
                            Exclusion Type</label>
                        <div class="col-md-6">
                            <select class="form-control" id="exclusion-type" name="exclusiontype" required>
                                <option value="A">Ailment</option>
                                <option value="N">Claim Network</option>
                                <option value="X">Clause Exclusion</option>
                            </select>
                        </div>
                    </div>
                    <div>

                    </div>
                    <br>
                    </br>
                    <div class="item form-group">
                        <label for="exclusion-name-search" class="col-md-3 label-align">Clause</label>

                        <div class="col-md-6">
                            <input type="text" class="form-control" id="exclusion-name-search"
                            >
                        </div>
                        <div class="col-md-1">
                            <button id="searchexclusions"
                                    type="button" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                    <br>
                    </br>
                    <table class="table table-striped table-hover table-bordered table-fixed" id="exclusionsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Id</th>
                            <th width="4%">Desc</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="exclusions-det-form">
                    <input type="hidden" id="exclusion-bind-code" name="bindCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveExclusionsBtn"
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


<div class="modal fade" id="loadingsModal" tabindex="-1" role="dialog"
     aria-labelledby="loadingsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="loadingsModalLabel">
                    Add Loadings
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="loadingsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Id</th>
                            <th width="4%">Desc</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="loadings-det-form">
                    <input type="hidden" id="loadings-bind-code" name="bindCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveLoadingsBtn"
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


<div class="modal fade" id="providersModal" tabindex="-1" role="dialog"
     aria-labelledby="providersModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="providersModalLabel">
                    Add Provider Panels
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="providerTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Name</th>
                            <th width="4%">Contact Person</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="provider-det-form">
                    <input type="hidden" id="provider-bind-code" name="bindCode"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveProvidersBtn"
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


<div class="modal fade" id="coverLimitsModal" tabindex="-1" role="dialog"
     aria-labelledby="coverLimitsModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">
                    Cover Limits
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <div class="x_panel">
                    <button type="button" class="btn btn-info"
                            id="btn-add-cover-limit">New
                    </button>
                    <table class="table table-striped table-hover table-bordered table-fixed" id="coverLimitsTbl">
                        <thead>
                        <tr>
                            <th>Limit</th>
                            <th width="1%"></th>
                        </tr>
                        </thead>
                    </table>

                    <div class="x_title">
                        <h2>Sub Limits</h2>
                        <ul class="nav navbar-right panel_toolbox">
                        </ul>
                        <div class="clearfix"></div>
                    </div>
                    <form id="limits-upload-form" class="form-horizontal" enctype="multipart/form-data">
                        <input type="hidden" class="form-control" id="sub-limit-cov-id" name="coverLimit">
                        <div class="col-md-10 col-xs-12 form-required">

                            <div class="col-md-5 col-xs-12">
                                <div class="input-group col-xs-12">
                                    <input name="file" type="file" id="avatar">
                                </div>
                            </div>
                            <div class="col-md-3 col-xs-12">
                                <input type="submit" class="btn btn-success btn-sm float-left"
                                       style="margin-right: 10px;" value="Upload">

                            </div>
                        </div>
                    </form>
                    <table class="table table-striped table-hover table-bordered table-fixed" id="coverSubLimitsTbl">
                        <thead>
                        <tr>
                            <th>Sht Desc</th>
                            <th>Desc</th>
                            <th>Limit</th>
                            <th>Waiting Period</th>
                            <th width="1%"></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    OK
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="limitsModal" tabindex="-1" role="dialog"
     aria-labelledby="limitsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">
                    Add/Edit Limits
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="cover-limits-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="cov-limits-code" name="id">
                    <input type="hidden" class="form-control" id="limit-cov-code" name="covers">
                    <div class="item form-group">
                        <label for="cov-limit-amt" class="col-md-3 label-align">Limit Amount</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="cov-limit-amt" name="limitAmount"
                                   required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCoverLimits"
                        type="button" class="btn btn-primary">
                    OK
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="binderCoverModal" tabindex="-1" role="dialog"
     aria-labelledby="binderDetModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">
                    Edit Cover Summary
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="cover-summary-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="cov-binder-det-code" name="detId">
                    <div class="item form-group">
                        <label for="cov-summary" class="col-md-3 label-align">Cover Summary</label>

                        <div class="col-md-8">
							<textarea rows="3" cols="20" name="remarks" id="cov-summary"
                                      class="editUserCntrls form-control"></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cov-min-prem" class="col-md-3 label-align">Min Prem</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls formatcurrency form-control"
                                   id="cov-min-prem" name="minPrem"
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="chk-limits-allowed" class="col-md-3 label-align">Limits Allowed</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="limitsAllowed" id="chk-limits-allowed">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="chk-single-risk" class="col-md-3 label-align">Single Section Cover</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="singleSectionCover" id="chk-single-risk">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="installs-no" class="col-md-3 label-align">Number of Installments</label>

                        <div class="col-md-5">
                            <input type="number" class="form-control" id="installs-no" min="1" max="10"
                                   name="installmentsNo">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="install-perce" class="col-md-3 label-align">Installment Percentages</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="install-perce" name="distribution"
                            >
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCoverSummary"
                        type="button" class="btn btn-primary">
                    OK
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="editBinderclauseModal" tabindex="-1" role="dialog"
     aria-labelledby="editBinderclauseModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editBinderclauseModalLabel">
                    Edit Binder Clause
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="binder-clause-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="binder-clause-code" name="clauId">
                    <input type="hidden" class="form-control" id="binder-clause-pk" name="clause">
                    <div class="item form-group">
                        <label for="binder-clau-id" class="col-md-3 label-align">Clause ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="binder-clau-id"
                                   name="clauShtDesc" disabled>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="binder-clause-name" class="col-md-3 label-align">Clause Heading</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="binder-clause-name" name="clauHeading"
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="binder-cla-wording" class="col-md-3 label-align">Clause Wording</label>

                        <div class="col-md-8">
                            <textarea rows="7" cols="20" class="form-control" id="binder-cla-wording"
                                      name="clauWording"></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="chk-cl-mandatory" class="col-md-3 label-align">Mandatory</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="mandatory" id="chk-cl-mandatory">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="chk-cl-editable" class="col-md-3 label-align">Editable</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="editable" id="chk-cl-editable">
                            </label>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderClauseBtn"
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


<div class="modal fade" id="editBinderLoadingModal" tabindex="-1" role="dialog"
     aria-labelledby="editBinderLoadingModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editBinderLoadingModalLabel">
                    Edit Binder Loading
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="binder-loading-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="binder-loading-code" name="clId">
                    <input type="hidden" class="form-control" id="binder-loading-pk" name="ailment">
                    <div class="item form-group">
                        <label for="binder-loading-id" class="col-md-3 label-align">Loading ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="binder-loading-id"
                                   name="clauShtDesc" disabled>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="binder-loading-desc" class="col-md-3 label-align">Loading Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="binder-loading-desc"
                                   name="ailmentDesc">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="load-rate-type" class="col-md-3 label-align">Rate Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="load-rate-type" name="rateType">
                                <option value="">Select Rate Type</option>
                                <option value="P">Percentage</option>
                                <option value="M">Per Mille</option>
                                <option value="A">Amount</option>

                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="load-rate" class="col-md-3 label-align">Rate</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="load-rate" name="rate"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="loading-chronic" class="col-md-3 label-align">Chronic?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="chronic" id="loading-chronic">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="load-wait-days" class="col-md-3 label-align">Waiting Days</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="load-wait-days" name="waitingDays"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="load-cost-per-claim" class="col-md-3 label-align">Cost Per Claim</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="load-cost-per-claim" name="costPerClaim"
                                   required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="load-upper-limit" class="col-md-3 label-align">Upper Limit</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="load-upper-limit" name="upperLimit"
                                   required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderLoadingBtn"
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

<div class="modal fade" id="reqDocsModal" tabindex="-1" role="dialog"
     aria-labelledby="reqDocsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="reqDocsModalLabel">
                    Add Required Documents
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="reqDocsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Id</th>
                            <th width="4%">Desc</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="reqDocs-det-form">
                    <input type="hidden" id="reqDocs-det-code" name="binderDetail"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savereqDocsBtn"
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


<div class="modal fade" id="editBinderreqDocsModal" tabindex="-1" role="dialog"
     aria-labelledby="editBinderreqDocsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editBinderreqDocsModalLabel">
                    Edit Binder Required Docs
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="binder-reqdocs-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="binder-reqdoc-code" name="brdId">
                    <input type="hidden" class="form-control" id="binder-reqdoc-doc-id" name="requiredDocs">
                    <div class="item form-group">
                        <label for="binder-reqdoc-id" class="col-md-3 label-align">Document ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="binder-reqdoc-id"
                                   disabled>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="binder-reqdoc-name" class="col-md-3 label-align">Document Desc.</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="binder-reqdoc-name" disabled
                            >
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="chk-reqdoc-mandatory" class="col-md-3 label-align">Mandatory</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="mandatory" id="chk-reqdoc-mandatory">
                            </label>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBinderreqDocsBtn"
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


<div class="modal fade" id="premLimitsModal" tabindex="-1" role="dialog"
     aria-labelledby="premLimitsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="premLimitsModalLabel">
                    Premium Item Limits
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <button type="button" class="btn btn-info"
                        id="btn-add-prem-limit">Add
                </button>
                <input type="hidden" id="covt-limit-sub-id">
                <table class="table table-striped table-hover table-bordered table-fixed" id="premLimitsTbl">
                    <thead>
                    <tr>
                        <th>Limit ID</th>
                        <th>Limit Desc</th>
                        <th width="4%">Value</th>
                        <th width="4%"></th>
                        <th width="4%"></th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>


            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    OK
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="addSectLimtsModal" tabindex="-1" role="dialog"
     aria-labelledby="addSectLimtsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="addSectLimtsModalLabel">
                    Add Prem Limits
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="premSectLimitsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Id</th>
                            <th width="4%">Desc</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>

                <form id="premlimits-det-form">
                    <input type="hidden" id="premlimts-det-code" name="premRatesId"/>
                </form>

            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savePremLimitsBtn"
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

<div class="modal fade" id="editpremLimitsModal" tabindex="-1" role="dialog"
     aria-labelledby="editpremLimitsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="editpremLimitsModalLabel">
                    Edit Premium Limit
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="edit-prem-limit-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="prem-limit-code" name="id">
                    <input type="hidden" class="form-control" id="prem-sub-clause-code" name="clausesDef">
                    <input type="hidden" class="form-control" id="prem-premdef-code" name="premRatesDef">
                    <div class="item form-group">
                        <label for="edit-clause-id" class="col-md-3 label-align">Limit</label>
                        <div class="col-md-8">
                            <p class="form-control-static" id="edit-clause-id"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="edit-clause-limit" class="col-md-3 label-align">Value</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control formatcurrency"
                                   id="edit-clause-limit" name="value"
                                   required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveEditPremLimit"
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

<div class="modal fade" id="compSheetName" tabindex="-1" role="dialog"
     aria-labelledby="compSheetModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="compSheetModalLabel">
                    Edit/Add Computation Type
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="comp-sheet-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="comp-sheet-code" name="shtId">
                    <input type="hidden" class="form-control" id="comp-sheet-binder-det" name="binderDetails">


                    <div class="item form-group">
                        <label for="comp-sheet-desc" class="col-md-3 label-align">Sheet Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="comp-sheet-desc"
                                   name="sheetName" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="comp-sheet-type" class="col-md-3 label-align">Compute Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="comp-sheet-type" name="computeType">
                                <option value="">Select Computation Type</option>
                                <option value="S">Sum Assured</option>
                                <option value="P">Premium</option>
                            </select>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savecompSheet"
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