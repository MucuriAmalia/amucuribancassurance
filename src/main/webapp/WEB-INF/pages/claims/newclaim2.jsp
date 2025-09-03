<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/claims2.js"/>"></script>

<%-- Check if we're in edit mode --%>
<c:set var="isEditMode" value="${editMode != null && editMode}" />
<c:set var="claimId" value="${claimId}" />
<c:set var="originalClaimNo" value="${originalClaimNo}" />

<style>
    .btn-processing {
        opacity: 0.6;
        cursor: not-allowed !important;
        pointer-events: none;
        position: relative;
    }

    .btn-processing::after {
        content: "🚫";
        position: absolute;
        right: 3px;
        top: 50%;
        transform: translateY(-50%);
        font-size: 10px;
        line-height: 1;
    }
</style>


<div class="x_panel">
    <div class="x_title">

        <h2><i class="fa fa-bars"></i> Enter Claim Details</h2>
        <div class="clearfix"></div>

        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
    </div>

    <div class="x_content">
        <c:if test="${error != null}">
            <div class="alert alert-error alert-dismissible">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                    ${error}
            </div>
        </c:if>
<%--        <form:form id="claimForm" class="form-horizontal form-label-left" method="post" action="newclaim" modelAttribute="claimForm">--%>
        <form:form id="claimForm" class="form-horizontal form-label-left" method="post"
                   action="${isEditMode ? 'updateRejectedClaim' : 'newclaim'}" modelAttribute="claimForm">
            <c:if test="${isEditMode}">
                <input type="hidden" name="claimId" value="${claimId}" />
                <input type="hidden" name="editMode" value="true" />
            </c:if>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="loss-date" class="label-align col-md-5">
                        Loss Date<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="los-date">
                            <form:input path="lossDate" class="form-control float-right"  id="loss-date" required="true"/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="notification-date" class="label-align col-md-5">
                        Notification Date<span class="required">*</span>
                    </label>
                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="not-date">
                            <form:input path="notificationDate" class="form-control float-right" id="notification-date" required="true"/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
            <div class="item form-group form-required">
<%--                <div class="col-md-6 col-xs-12">--%>
<%--                    <label for="risk-frm" class="label-align col-md-5">--%>
<%--                        Select A Risk<span class="required">*</span></label>--%>
<%--                    <div class="col-md-7 col-xs-12">--%>
<%--                        <form:hidden path="riskId"  id="risk-id"/>--%>
<%--                        <form:hidden path="riskDesc"  id="risk-desc"/>--%>
<%--                        <form:hidden path="riskShtDesc"  id="risk-sht-desc"/>--%>
<%--                        <input type="hidden" id="risk-client-name" />--%>
<%--                        <input type="hidden" id="risk-client-code" />--%>
<%--                        <div id="risk-frm" class="form-control"--%>
<%--                             select2-url="<c:url value="/protected/claims/selLossRisks"/>" >--%>

<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
    <div class="col-md-6 col-xs-12">
        <label for="risk-frm" class="label-align col-md-5">
            Select A Risk<span class="required">*</span></label>
        <div class="col-md-7 col-xs-12">
            <c:choose>
                <c:when test="${isEditMode}">
                    <%-- Read-only risk in edit mode --%>
                    <form:hidden path="riskId" id="risk-id"/>
                    <form:hidden path="riskDesc" id="risk-desc"/>
                    <form:hidden path="riskShtDesc" id="risk-sht-desc"/>
                    <div class="form-control-static" id="risk-display">
                            ${claimForm.riskShtDesc} - ${claimForm.riskDesc}
                    </div>
                    <small class="text-muted">Risk cannot be changed when editing</small>
                </c:when>
                <c:otherwise>
                    <%-- Normal risk selection for new claims --%>
                    <form:hidden path="riskId" id="risk-id"/>
                    <form:hidden path="riskDesc" id="risk-desc"/>
                    <form:hidden path="riskShtDesc" id="risk-sht-desc"/>
                    <input type="hidden" id="risk-client-name" />
                    <input type="hidden" id="risk-client-code" />
                    <div id="risk-frm" class="form-control"
                         select2-url="<c:url value="/protected/claims/selLossRisks"/>" >
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
                <div class="col-md-6 col-xs-12">
                    <label for="loss-desc" class="label-align col-md-5">
                        Loss Description<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                    <form:textarea class="form-control" rows="2" id="loss-desc" path="lossDesc" required="true"></form:textarea>
                        </div>
                </div>
                </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="risk-identifier" class="label-align col-md-5">
                        Risk Identifier</label>
                    <div class="col-md-7 col-xs-12">
                        <form:input class="form-control" id="risk-identifier" path="riskIdentifier" readonly="true"/>
                    </div>
                </div>
<%--                <div class="col-md-6 col-xs-12">--%>
<%--                    <label for="pol-ins-balance" class="label-align col-md-5">--%>
<%--                        Insurance Balance</label>--%>
<%--                    <div class="col-md-7 col-xs-12">--%>
<%--                        <p class="form-control-static" id="pol-client-balance"></p>--%>
<%--                    </div>--%>
<%--                </div>--%>
            </div>

            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="pol-client-balance" class="label-align col-md-5">
                        Client Balance</label>
                    <div class="col-md-7 col-xs-12">
                        <p class="form-control-static" id="pol-ins-balance"></p>
                    </div>
                </div>
                <form:hidden path="balanceApproved" id="balanceApprovedField" value="false"/>
                <div class="col-md-6 col-xs-12" id="balance-checkbox-container" style="display: none;">
                    <label class="label-align col-md-5"></label>
                    <div class="col-md-7 col-xs-12">
                        <div class="checkbox" style="padding-top: 7px;">
                            <label>
                                <input type="checkbox" id="balance-confirm" name="balanceConfirm">
                                I acknowledge there is an existing balance on this policy
                            </label>
                        </div>
                    </div>
                </div>
            </div>

            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="insurer-date" class="label-align col-md-5">
                        Submit Insurer Date</label>
                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input'>
                            <form:input  class="form-control float-right" path="insurerDate"
                                   id="insurer-date" />
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="clm-activity" class="label-align col-md-5">
                        Select Activity<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <form:hidden  id="activity-code" path="activityId"/>
                        <form:hidden id="activity-desc" path="activityDesc"/>
                        <div id="clm-activity" class="form-control"
                             select2-url="<c:url value="/protected/claims/selclmActivity"/>" >

                        </div>
                    </div>
                </div>

            </div>

            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="next-rev-date" class="label-align col-md-5">
                        Next Review Date<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="next-rev-date">
                            <form:input  class="form-control float-right" path="nextReviewDate"
                                   id="net-review-date" required="true" />
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="review-user" class="label-align col-md-5">
                        Next Review User<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <form:hidden  id="next-rev-user" path="nextReviewUser" required="true"/>
                        <form:hidden  id="next-rev-user-desc" path="reviewUser"/>
                        <div id="review-user" class="form-control"
                             select2-url="<c:url value="/protected/organization/managers"/>" >

                        </div>
                    </div>
                </div>

            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="loss-desc" class="label-align col-md-5">
                        Activity Notes<span class="required">*</span></label>
                    <div class="col-md-7 col-xs-12">
                        <form:textarea class="form-control" rows="2" id="claim-desc" path="activityNotes" required="true"></form:textarea>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="bin-type" class="col-md-5 label-align">Party to Blame</label>

                    <div class="col-md-7">
                        <form:select class="form-control" id="bin-type" path="partyToBlame">
                            <form:option value="">Select Party to Blame</form:option>
                            <form:option value="N">Non</form:option>
                            <form:option value="I">Insured</form:option>
                            <form:option value="T">Third party</form:option>
                        </form:select>
                    </div>

                </div>
            </div>
            <div class="item form-group form-required">
                <div class="col-md-6 col-xs-12">
                    <label for="rate-taxable" class="col-md-5 label-align">Liability Admission</label>
                    <div class="col-md-7 checkbox">
                        <label>
                            <form:checkbox path="liabilityAdmission" id="chk-active"/>
                        </label>

                    </div>
                </div>
            </div>


                <div class="x_title">
                    <h4>Perils</h4>

                </div>

            <input type="button" id="add-peril-btn"
                   class="btn btn-info float-left" style="margin-right: 10px;"
                   value="Add"
                   <c:if test="${isEditMode}">disabled="disabled"</c:if>>

            <table id="peril-table-id" class="table table-striped" style="width: 100%">
                <thead>
                <tr class="headings">
                    <th>Claimant</th>
                    <th>Peril</th>
                    <th>Estimated Amount</th>
                    <th></th>
                </tr>
                </thead>

            </table>
            <input type="button" id="saveDraftBtn" value="Save" class="btn btn-info" />

            <div class="x_panel">
                <div class="x_title">
                    <h4>Upload Required Documents</h4>
                </div>
                <div class="table-responsive"  aria-labelledby="profile-tab">
                    <button type="button" class="btn btn-info" id="btn-add-clmdocs">New</button>
                    <table id="clm_required_docs_tbl" class="table table-striped" style="width: 100%">
                        <thead>
                        <tr class="headings">
                            <th>Document Desc</th>
                                <%--                            <th>Ref</th>--%>
                            <th>File Name</th>
                            <th>Uploaded By</th>
                            <th>Uploaded Date</th>
                            <th>Remarks</th>
                            <th ></th>
                            <th></th>
                            <th></th>
                                <%--                            <th></th>--%>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <div class="modal fade" id="clmReqDocsModal" tabindex="-1" role="dialog"
                 aria-labelledby="clmReqDocsModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h4 class="modal-title" id="clmReqDocsModalLabel">Select Required Docs</h4>
                            <button type="button" class="close" data-dismiss="modal"  aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>

                        </div>
                        <div class="modal-body">
                            <form class="form-horizontal">
                                <div class="item form-group">
                                    <label for="doc-name-search" class="col-md-3 label-align">Document Name</label>
                                    <div class="col-md-6">
                                        <input type="text" class="form-control" id="doc-name-search" name="req-doc">
                                    </div>
                                    <div class="col-md-1">
                                        <button  id="searchDocuments" type="button" class="btn btn-primary"> Search </button>
                                    </div>
                                </div>
                            </form>
                            <div style="height: 300px !important; overflow: scroll;">
                                <table class="table table-striped table-hover table-bordered table-fixed" id="risksReqDocsTbl">
                                    <thead>
                                    <tr>
                                        <th width="1%"></th>
                                        <th width="4%">Document Id</th>
                                        <th width="12%">Document Name</th>
                                    </tr>
                                    </thead>
                                    <tbody> </tbody>
                                </table>
                            </div>
                            <form id="req-clm-docs-form">
                                <input type="hidden" id="req-risk-code" name="subCode"/>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button data-loading-text="Saving..." id="saveReqClmDocsBtn"
                                    type="button" class="btn btn-success">Save</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="uploadClmReqModal" tabindex="-1" role="dialog"
                 aria-labelledby="uploadClmReqModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <form id="clm-req-form" class="form-horizontal">
                            <div class="modal-header">
                                <h4 class="modal-title" id="uploadClmReqModalLabel">
                                    Upload Claim Document
                                </h4>
                                <button type="button" class="close" data-dismiss="modal"
                                        aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>

                            </div>
                            <div class="modal-body">


                                <input type="hidden" class="form-control" id="uploadreq-code" name="clmRequiredId">
                                <input type="hidden" class="form-control" id="trans-type" name="transType">
                                <div class="item form-group">
                                    <label for="brn-id" class="col-md-3 label-align">Doc Ref No</label>

                                    <div class="col-md-8">
                                        <input type="text" class="form-control" id="doc-ref-no"
                                               name="docRefNo">
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <label for="rate-taxable" class="col-md-3 label-align">Remarks</label>
                                    <div class="col-md-8">
                                        <label>
                                            <textarea class="form-control" rows="3" id="req-remarks" name="remarks"></textarea>
                                        </label>
                                    </div>
                                </div>
                                <div class="item form-group uploadfile">
                                    <label for="req-doc-file" class="col-md-3 label-align">File</label>

                                    <div class="col-md-8">
                                        <label class="input-group col-xs-12">
                                            <input type="file" id="req-doc-file" name="file" accept="*/*">
                                            <span class="custom-file-control"></span>
                                        </label>
                                    </div>
                                </div>








                            </div>
                            <div class="modal-footer">
                                    <%-- <input type="submit" class="btn btn-success" value="Save"> --%>
                                <input type="submit" class="btn btn-success" value="Upload" id="upload-claimDoc-btn">
                                <button type="button" class="btn btn-default" data-dismiss="modal">
                                    Cancel
                                </button>
                            </div>
                            <div id="upload-spinner" class="text-center mt-3" style="display: none;">
                                <!-- Spinner only (no text inside) -->
                                <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">

                                </div>
                                <!-- Status text placed separately to avoid rotation -->
                                <div class="mt-2" style="margin-bottom: 30px;">Uploading document, please wait...
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        <c:choose>
            <c:when test="${isEditMode}">
                <input type="submit" id="updateClaimBtn" value="Update & Resubmit for Approval" class="btn btn-warning" />
            </c:when>
            <c:otherwise>
                <input type="submit" id="submitClaimFormBtn" value="Submit" class="btn btn-info" />
            </c:otherwise>
        </c:choose>

        </form:form>

    </div>

</div>

<div class="modal fade" id="perilTransModal" tabindex="-1" role="dialog"
     aria-labelledby="perilTransModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="perilTransModalLabel">
                    Add A new Peril
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="new-peril-form" class="form-horizontal">
                    <div class="item form-group">

                            <label for="brn-id" class="col-md-4 label-align">Self as
                                Claimant</label>

                            <div class="col-md-8 checkbox">
                                <label>
                                    <input type="checkbox" name="selfAsClaimant" id="self-as-clmnt">
                                </label>
                            </div>
                    </div>
                    <div class="item form-group">
                            <label for="brn-id" class="col-md-4 label-align">Claimant</label>

                            <div class="col-md-8">
                                <input type="hidden" id="clmt-code"/>
                                <input type="hidden" id="clmt-name">
                                <div id="clmant-def" class="form-control"
                                     select2-url="<c:url value="/protected/claims/selClaimants"/>" >

                                </div>
                            </div>
                    </div>
                    <div class="item form-group">
                            <label for="brn-id" class="col-md-4 label-align">Peril</label>

                            <div class="col-md-8">
                                <input type="hidden" id="peril-code"/>
                                <input type="hidden" id="peril-name"/>
                                <div id="peril-def" class="form-control"
                                     select2-url="<c:url value="/protected/claims/selSubclassPerils"/>" >

                                </div>
                            </div>
                    </div>
                    <div class="item form-group">
                            <label for="brn-id" class="col-md-4 label-align">Claim Estimate</label>

                            <div class="col-md-8">
                                <input type='text' class="form-control float-right"
                                       id="clm-estimate" />
                            </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="btn-add-peril-selected"
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



<div class="modal fade" id="uploadClmModal" tabindex="-1" role="dialog"
     aria-labelledby="uploadClmModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="clm-doc-form" class="form-horizontal">
                <div class="modal-header">
                    <h4 class="modal-title" id="uploadClmModalLabel">
                        Upload Claim Document
                    </h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">


                    <input type="hidden" class="form-control" id="upload-code" name="uploadId">
                    <input type="hidden" class="form-control" id="upload-clm-id" name="claimBookings">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">File ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="upload-sht-id"
                                   name="fileId">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="file" class="col-md-3 label-align">File</label>

                        <div class="col-md-8">
                            <label class="custom-file">
                                <input type="file" id="file" class="custom-file-input" name="file">
                                <span class="custom-file-control"></span>
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Comment</label>
                        <div class="col-md-8">
                            <label>
                                <textarea class="form-control" rows="3" id="comment-desc" name="uploadedComment"></textarea>
                            </label>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <input type="submit" class="btn btn-success" value="Save">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>


<div class="modal fade" id="clmReqDocsModal" tabindex="-1" role="dialog"
     aria-labelledby="clmReqDocsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="clmReqDocsModalLabel">Select Required Docs</h4>
                <button type="button" class="close" data-dismiss="modal"  aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="doc-name-search" class="col-md-3 label-align">Document Name</label>
                        <div class="col-md-6">
                            <input type="text" class="form-control" id="doc-name-search" name="req-doc">
                        </div>
                        <div class="col-md-1">
                            <button  id="searchDocuments" type="button" class="btn btn-primary"> Search </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="risksReqDocsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Document Id</th>
                            <th width="12%">Document Name</th>
                        </tr>
                        </thead>
                        <tbody> </tbody>
                    </table>
                </div>
                <form id="req-clm-docs-form">
                    <input type="hidden" id="req-risk-code" name="subCode"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveReqClmDocsBtn"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="uploadClmReqModal" tabindex="-1" role="dialog"
     aria-labelledby="uploadClmReqModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="clm-req-form" class="form-horizontal">
                <div class="modal-header">
                    <h4 class="modal-title" id="uploadClmReqModalLabel">
                        Upload Claim Document
                    </h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">


                    <input type="hidden" class="form-control" id="uploadreq-code" name="clmRequiredId">
                    <input type="hidden" class="form-control" id="trans-type" name="transType">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Doc Ref No</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="doc-ref-no"
                                   name="docRefNo">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Remarks</label>
                        <div class="col-md-8">
                            <label>
                                <textarea class="form-control" rows="3" id="req-remarks" name="remarks"></textarea>
                            </label>
                        </div>
                    </div>
                    <div class="item form-group uploadfile">
                        <label for="req-doc-file" class="col-md-3 label-align">File</label>

                        <div class="col-md-8">
                            <label class="input-group col-xs-12">
                                <input type="file" id="req-doc-file" name="file" accept="*/*">
                                <span class="custom-file-control"></span>
                            </label>
                        </div>
                    </div>








                </div>
                <div class="modal-footer">
                    <%-- <input type="submit" class="btn btn-success" value="Save"> --%>
                    <input type="submit" class="btn btn-success" value="Upload" id="upload-claimDoc-btn">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Cancel
                    </button>
                </div>
                <div id="upload-spinner" class="text-center mt-3" style="display: none;">
                    <!-- Spinner only (no text inside) -->
                    <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">

                    </div>
                    <!-- Status text placed separately to avoid rotation -->
                    <div class="mt-2" style="margin-bottom: 30px;">Uploading document, please wait...
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>


<div class="modal fade" id="adjustmentNotesModal" tabindex="-1" role="dialog" aria-labelledby="adjustmentNotesModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="adjustmentNotesModalLabel">Adjustment Notes</h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label for="adjustmentNotes">Please highlight the adjustments made:</label>
                    <textarea class="form-control" id="adjustmentNotes" rows="4" required></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-warning" id="confirmAdjustmentBtn">Submit</button>
            </div>
        </div>
    </div>
</div>



