<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript">
    var accIdpk = ${accId};

    function unlockForm() {
        if (accIdpk !== -2000 && accIdpk !== 'undefined') {
            navigator.sendBeacon(SERVLET_CONTEXT + '/protected/home/unlockForm',
                new URLSearchParams({"formName": "INSURER/AGENT", "formId": accIdpk})
            );
            console.log("Form unlock request sent.");
        }
    }

    window.addEventListener("beforeunload", unlockForm);
</script>
<div class="x_title">
    <h2><i class="fa fa-bars"></i> Create Insurance Company/Sub-Agent</h2>
    <div class="clearfix"></div>
</div>
<div class="x_panel" id="acct_model">
    <div class="col-md-9 col-sm-9  offset-md-3">
        <h4 class="float-left blue" style="font-weight: bolder" id="h4pol">Name: </h4>
        <input type="button" class="btn btn-danger float-right"
               value="Deactivate" id="btn-deactivate">

    </div>
    <div class="" role="tabpanel" data-example-id="togglable-tabs">
        <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
            <li role="presentation" class="active"><a href="#tab_content1"
                                                      id="home-tab" role="tab" data-toggle="tab"
                                                      aria-expanded="true">Details</a>
            </li>
            <li role="presentation" id="show-docs" style="display:none"><a href="#tab_content2"
                                                                           role="tab" id="profile-tab" data-toggle="tab"
                                                                           aria-expanded="false">Documents</a>
            </li>
        </ul>
        <div id="myTabContent" class="tab-content">
            <div role="tabpanel" class="tab-pane active"
                 id="tab_content1" aria-labelledby="home-tab">
                <form id="account-form" class="form-horizontal" enctype="multipart/form-data">
                    <div class="x_panel">
                        <div class="item form-group">
                            <a href="<c:url value='/protected/setups/accts'/> "
                               class="btn btn-primary float-right">Back</a>
                        </div>
                    </div>
                    <input type="hidden" name="acctId" id="acctId-pk">
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="brn-id" class="col-md-5 label-align">Select
                                Intermediary Type<span class="required">*</span></label>

                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="acc-id" name="acctTypeId"/>
                                <input type="hidden" id="acc-name">
                                <input type="hidden" id="acc-type-desc">
                                <div id="accounttypes" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selAcctTypes"/>">

                                </div>
                            </div>

                        </div>
                        <div class="col-md-6 col-xs-12">

                        </div>


                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="other-names" class="label-align col-md-5">
                                Sht Desc</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="shtDesc" id="other-names" class="form-control"
                                       placeholder="Sht Desc">
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="fname" class="label-align col-md-5">
                                Name<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="name" id="fname" class="form-control"
                                       placeholder="Account Name" required>
                            </div>


                        </div>


                    </div>
                    <div class="item form-group form-required sub-agents">
                        <div class="col-md-6 col-xs-12">
                            <label for="id_No" class="label-align col-md-5">
                                ID No<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="idNumber" id="id_No" class="form-control"
                                       placeholder="ID No" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="absa_No" class="label-align col-md-5">
                                AB No<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="absaNo" id="absa_No" class="form-control"
                                       placeholder="Absa No" required>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required inhouse-agents">
                        <div class="col-md-6 col-xs-12">
                            <label for="noOfUnits" class="label-align col-md-5">Bank</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="acct-bank-id" name="bankId"/>
                                <input type="hidden" id="acct-bank-name">
                                <div id="bank-lov" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selBanks"/>">

                                </div>
                            </div>

                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="bank-branch-lov" class="label-align col-md-5">Bank Branch</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="bank-branch-id" name="bankBranchId"/>
                                <input type="hidden" id="bank-branch-name">
                                <div id="bank-branch-lov" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selBankBranches"/>">

                                </div>
                            </div>

                        </div>


                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="acct-acct-no" class="label-align col-md-5">Account Number</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="bankAccount" id="acct-acct-no" class="form-control"
                                       placeholder="Bank Account No.">
                            </div>
                        </div>

                        <div class="col-md-6 col-xs-12">
                            <label for="noOfUnits" class="label-align col-md-5">Pin No<span
                                    class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="pinNo" id="pinNo" class="form-control"
                                       placeholder="Pin No" required>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="email" class="label-align col-md-5">Email</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="email" name="email" id="email" class="form-control"
                                       placeholder="Email">
                            </div>
                        </div>

                        <div class="col-md-6 col-xs-12">
                            <label for="noOfUnits" class="label-align col-md-5">Phone No<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="phoneNo" id="phone-no" class="form-control"
                                       placeholder="Phone No" required>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group inhouse-agents">
                        <div class="col-md-6 col-xs-12 form-required">
                            <label for="noOfUnits" class="label-align col-md-5">Postal Address</label>
                            <div class="col-md-7 col-xs-12">
                                <textarea rows="3" cols=30 class="form-control" name="address" id="address"></textarea>
                            </div>

                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="noOfUnits" class="label-align col-md-5">Physical Address</label>
                            <div class="col-md-7 col-xs-12">
                                <textarea rows="3" cols=30 class="form-control" name="physaddress"
                                          id="phy-address"></textarea>
                            </div>

                        </div>

                    </div>

                    <div class="item form-group inhouse-agents">
                        <div class="col-md-6 col-xs-12">
                            <label for="cont-title" class="label-align col-md-5">Contact Title</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="contactTitle" id="cont-title" class="form-control"
                                       placeholder="Contact Title">
                            </div>

                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="contact-person" class="label-align col-md-5">Contact Person</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="contactPerson" id="contact-person" class="form-control"
                                       placeholder="Contact Person">
                            </div>
                        </div>

                    </div>
                    <div class="item form-group form-required">
                        <div class="col-md-6 col-xs-12">
                            <label for="sel2" class="label-align col-md-5">Status</label>
                            <div class="col-md-7 col-xs-12">
                                <p  id="sel2" class="form-control-static"
                                       readonly>
<%--                                <select class="form-control" id="sel2" name="status" disabled readonly>--%>
<%--                                    <option value="">Select Status</option>--%>
<%--                                    <option value="D">Draft</option>--%>
<%--                                    <option value="A">Active</option>--%>
<%--                                    <option value="I">Inactive</option>--%>
<%--                                    <option value="DA">Deactivated</option>--%>
<%--                                    <option value="NA">Not Approved</option>--%>
<%--                                </select>--%>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="acct-branch" class="label-align col-md-5">Branch</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="obId" name="branchId"/>
                                <input type="hidden" id="ob-name">
                                <div id="acct-branch" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/allbranches"/>">

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required inhouse-agents">
                        <div class="col-md-6 col-xs-12">
                            <label for="acct-paybill" class="label-align col-md-5">Pay Bill Number</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="paybillNumber" id="acct-paybill" class="form-control"
                                       placeholder="MPESA Paybill No.">
                            </div>

                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="acct-mob-tel" class="label-align col-md-5">Payment Tel Number</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="payTelNo" id="acct-mob-tel" class="form-control"
                                       placeholder="Mobile Tel Number">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="reg-wet" class="col-md-5 label-align" id="acct-date-reg">Date of Reg.</label>

                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input'>
                                    <input type='text' class="form-control float-right" name="dob" id="dob"/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12 inhouse-agents">
                            <label for="pm-mode-frm" class="label-align col-md-5">
                                Payment Mode</label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-payment-mode">
                                    <input type="hidden" id="pm-id" name="paymentModeId"/>
                                    <input type="hidden" id="pm-name">
                                    <div id="pm-mode-frm" class="form-control"
                                         select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>">

                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                    <div class="item form-group form-required inhouse-agents">
                        <div class="col-md-6 col-xs-12">
                            <label for="integration-type" class="label-align col-md-5">Integration Type</label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="integration-type" name="integrationType">
                                    <option value="">Select Integration Type</option>
                                    <option value="N">None</option>
                                    <option value="D">JWT</option>
                                    <option value="A">OAUTH2</option>
                                    <option value="I">File Upload</option>
                                </select>
                            </div>

                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="integration-url" class="label-align col-md-5">Integration URL</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="underwriterApiUrl" id="integration-url" class="form-control"
                                       placeholder="Integration URL">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group form-required inhouse-agents">
                        <div class="col-md-6 col-xs-12">
                            <label for="integration-username" class="label-align col-md-5">Integration Username</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="underwriterApiUsername" id="integration-username"
                                       class="form-control"
                                       placeholder="Integration Username">
                            </div>

                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="integration-password" class="label-align col-md-5">Integration Password</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="password" name="underwriterApiPassword" id="integration-password"
                                       class="form-control"
                                       placeholder="Integration Password">
                            </div>
                        </div>
                    </div>
                    <%--Insurance Type--%>
                    <div class="item form-group form-required inhouse-agents">
                        <div class="col-md-6 col-xs-12">
                            <label for="insurance-type" class="label-align col-md-5">Insurance Type</label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="insurance-type" name="insuranceType">
                                    <option value="">Select Insurance Type</option>
                                    <option value="LIFE">Life Insurance</option>
                                    <option value="GENERAL">General Insurance</option>
                                    <option value="MEDICAL">Medical Insurance</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12"></div>
                    </div>

                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12  form-required">
                            <label for="brn-id" class="col-md-5 label-align">Date
                                Activated</label>

                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="wef-date">
                                    <input type='text' class="form-control float-right" name="wef"
                                           id="from-date" disabled/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="noOfUnits" class="label-align col-md-5">Date
                                Deactivated</label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input' id="cover-to-date">
                                    <input type='text' class="form-control float-right" name="wet"
                                           id="wet-date" disabled/>
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>

                        </div>


                    </div>
                    <div class="item form-group inhouse-agents">
                        <div class="col-md-6 col-xs-12  form-required">
                            <label for="cou-name" class="col-md-5 label-align">Premium Receivable
                                Account</label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-receivable-premium">
                                    <input type="hidden" name="receivableAccountId" id="receivable-premium">
                                    <input type="hidden" name="receivableAccountName" id="receivable-premium-name">
                                    <input type="hidden" id="receivable-premium-code">
                                    <div id="cr-account-frm" class="form-control"
                                         select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="cou-name" class="label-align col-md-5">Premium Payable
                                Account</label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-payable-premium">
                                    <input type="hidden" name="payableAccountId" id="payable-premium">
                                    <input type="hidden" name="payableAccountName" id="payable-premium-name">
                                    <input type="hidden" id="payable-premium-code">
                                    <div id="dr-account-frm" class="form-control"
                                         select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group inhouse-agents">
                        <div class="col-md-6 col-xs-12  form-required">
                            <label for="cou-name" class="col-md-5 label-align">Whtx
                                Account</label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-whtx">
                                    <input type="hidden" name="whtxAccountId" id="whtx">
                                    <input type="hidden" name="whtxAccountName" id="whtx-name">
                                    <input type="hidden" id="whtx-code">
                                    <div id="whtx-account-frm" class="form-control"
                                         select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="cou-name" class="label-align col-md-5">Commission Receivable
                                Account</label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-comm">
                                    <input type="hidden" name="commAccountId" id="comm">
                                    <input type="hidden" name="commAccountName" id="comm-name">
                                    <input type="hidden" id="comm-code">
                                    <div id="comm-account-frm" class="form-control"
                                         select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group ">
                        <div class="col-md-6 col-xs-12  form-required inhouse-agents">
                            <label for="cou-name" class="col-md-5 label-align">Admin Fees
                                Account</label>
                            <div class="col-md-7 col-xs-12">
                                <div id="edit-admin">
                                    <input type="hidden" name="adminAccountId" id="admin">
                                    <input type="hidden" name="adminAccountName" id="admin-name">
                                    <input type="hidden" id="admin-code">
                                    <div id="admin-account-frm" class="form-control"
                                         select2-url="<c:url value="/protected/setups/revitems/selGlAccount"/>">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12 sub-agents">
                            <div class="item form-group form-required">
                                <label for="commCheckbox" class="label-align col-md-5">
                                    Commission Earning<span class="required">*</span>
                                </label>
                                <div class="col-md-7 col-xs-12">
                                    <select class="form-control" id="commCheckbox" name="commissionEarning" required>
                                        <option value="">Select commission status</option>
                                        <option value="Yes">Yes</option>
                                        <option value="No">No</option>
                                    </select>
                                </div>
                                <div class="col-md-3"></div>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="file" class="label-align col-md-5" id="acct-photo">
                                Logo</label>
                            <div class="col-md-7 col-xs-12">
                                <div class="kv-avatar center-block" style="width: 200px">
                                    <input name="file" type="file" id="avatar" class="file-loading">

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">

                        </div>
                    </div>
                    <div class="x_panel">
                        <div class="item form-group float-right">
                            <input type="submit" id="btn-submit" class="btn btn-primary float-right"
                                   style="margin-right: 10px;" value="Save">
                            <input type="button" class="btn btn-primary float-right"
                                   value="Assign" id="btn-assign-trans">
                            <input type="button" class="btn btn-danger float-right"
                                   value="Reject" id="btn-reject">
                            <input type="button" class="btn btn-primary float-right"
                                   value="Approve" id="btn-approve">
                        </div>
                    </div>

                </form>
            </div>
            <div role="tabpanel" class="tab-pane fade"
                 id="tab_content2" aria-labelledby="profile-tab" style="display:none">
                <button class="btn btn-success btn btn-info" id="btn-add-docs">New</button>
                <div class="card-box table-responsive">
                    <table id="accDocsList" class="table table-striped" style="width:100%">
                        <thead>
                        <tr>
                            <th>Document ID</th>
                            <th>Document Desc</th>
                            <th>File Name</th>
                            <th>Uploaded By</th>
                            <th>Verified By</th>
                            <th>Verified Date</th>
                            <th width="5%"></th>
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


<div class="modal fade" id="acctReqDocsModal" tabindex="-1" role="dialog"
     aria-labelledby="acctReqDocsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="acctReqDocsModalLabel">Select Required Docs</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Document Name</label>

                        <div class="col-md-6">
                            <input type="text" class="form-control" id="doc-name-search"
                            >
                        </div>
                        <div class="col-md-1">
                            <button id="searchDocuments"
                                    type="button" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="acctDocsTbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Document Id</th>
                            <th width="12%">Document Name</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
                <form id="acct-doc-form">
                    <input type="hidden" id="req-acct-code" name="subCode"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveAcctDocsBtn"
                        type="button" class="btn btn-success">Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="acctdocModal" tabindex="-1" role="dialog"
     aria-labelledby="acctdocModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <form id="accts-doc-form" class="form-horizontal" enctype="multipart/form-data">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="acctdocModalLabel">Upload Account Document</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <input type="hidden" id="acct-doc-id" name="docId"/>
                    <input type="hidden" id="reqd-doc-id" name="requiredDoc"/>
                    <div class="item form-group">
                        <label for="acct-doc-name" class="col-md-3 label-align">Document Type</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="acct-doc-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="acct-upload-name" class="col-md-3 label-align">Uploaded File Name</label>

                        <div class="col-md-8">
                            <p class="form-control-static" id="acct-upload-name"></p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-4 label-align">Document</label>

                        <div class="col-md-8">
                            <div class="input-group col-xs-12">
                                <input name="file" type="file" id="acct-avatar" required>
                            </div>
                        </div>
                    </div>


                </div>
                <div class="modal-footer">
                    <input value="Upload"
                           type="submit" class="btn btn-success">

                    </input>
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        Close
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<div id="rejectionModal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Reject Task</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <textarea id="rejectionReason" class="form-control"
                          placeholder="Enter the reason for rejection"></textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="button" id="rejectConfirmButton" class="btn btn-danger">Reject</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="assignCheckerModal" tabindex="-1" role="dialog"
     aria-labelledby="assignCheckerModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="assignCheckerModalLabel">Assign Ticket To User</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <input type="text" id="searchChecker" class="form-control" placeholder="Search Checkers...">
                <table class="table table-striped table-hover table-bordered" id="checkersList">
                    <thead>
                    <tr>
                        <th width="5%"><input type="checkbox" id="selectAll"></th>
                        <%--                        <th width="4%">User Name</th>--%>
                        <th width="12%">Checker Name</th>
                        <th width="12%">AB No.</th>
                    </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>
            </div>
            <div class="modal-footer" style="position: sticky; bottom: 0; background-color: #FFFFFF; z-index: 1000;">
                <button data-loading-text="Saving..." id="assign-checker-submit"
                        type="button" class="btn btn-success">Submit
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<script>

    $(function () {

        $(document).ready(function () {
            AccountDef.init();
        });

    });

    var AccountDef = (function ($) {
        'use strict';

        var pinNoElement = $('#pinNo');
        var model = {
            accounts: {
                accType: {
                    accId: "",
                },
                branch: {
                    brnCode: "",
                },
            }
        };

        var accountImage = function (id) {
            $("#avatar").fileinput('refresh', {
                overwriteInitial: true,
                maxFileSize: 1500,
                showClose: false,
                showCaption: false,
                browseLabel: '',
                removeLabel: '',
                browseIcon: '<i class="fa fa-folder-open"></i>',
                removeIcon: '<i class="fa fa-times"></i>',
                removeTitle: 'Cancel or reset changes',
                elErrorContainer: '#kv-avatar-errors',
                msgErrorClass: 'alert alert-block alert-danger',
                defaultPreviewContent: '<img src="' + SERVLET_CONTEXT + '/protected/setups/accountImage/' + id + '"  style="width:180px">',
                layoutTemplates: {main2: '{preview} ' + ' {remove} {browse}'},
                allowedFileExtensions: ["jpg", "png", "gif"]
            });
        };

        var createAccount = function (status) {
            var $form = $("#account-form");
            var validator = $form.validate();

            if (!$form.valid()) {
                return;
            }

            var formDataArray = $form.serializeArray();
            var formData = new FormData();

            // Append serialized data to FormData
            $.each(formDataArray, function (i, field) {
                formData.append(field.name, field.value);
            });

            // Collect selected checkers
            var selectedCheckers = [];
            if (status === "NA") {
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

                formData.append('checkerIds', selectedCheckers);
            }

            // Append file if selected
            var fileInput = $('#avatar')[0];
            if (fileInput.files.length > 0) {
                formData.append('file', fileInput.files[0]);
            }

            // AJAX request
            $.ajax({
                url: SERVLET_CONTEXT + '/protected/setups/createAccount',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    $("#acctId-pk").val(response);
                    getAccountDetails();
                    Swal.fire({
                        title: 'Success',
                        text: 'Record created/updated successfully',
                        icon: 'success'
                    });
                    window.location.href = SERVLET_CONTEXT + '/protected/home';
                },
                error: function (jqXHR) {
                    Swal.fire({
                        title: 'Error',
                        text: jqXHR.responseText,
                        icon: 'error'
                    });
                }
            });
        };

        var createBranchSelect = function () {
            if ($("#acct-branch").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "acct-branch",
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
                        var code = $("#obId").val();
                        var name = $("#ob-name").val();
                        model.accounts.branch.brnCode = code;
                        var data = {obName: name, obId: code};
                        callback(data);
                    },
                    id: "obId",
                    placeholder: "Select Branch",
                });
            }
        };

        var populateParentControlAcc = function () {
            if ($("#parent-account-lov").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "parent-account-lov",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#parent-account-id").val(e.added.acctId);
                    },
                    formatResult: function (a) {
                        return a.name;
                    },
                    formatSelection: function (a) {
                        return a.name;
                    },
                    initSelection: function (element, callback) {
                        var code = $('#parent-account-id').val();
                        var name = $("#parent-account-name").val();
                        var data = {name: name, acctId: code};
                        callback(data);
                    },
                    id: "acctId",
                    width: "250px",
                    placeholder: "Select Parent Account"

                });
            }
        };

        var createAccountTypeSelect = function () {
            if ($("#accounttypes").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "accounttypes",
                    sort: 'accName',
                    change: function (e, a, v) {
                        $("#acc-id").val(e.added.accId);
                        if (e.added.accountType) {
                            $("#acc-type-desc").val(e.added.accountType);
                            if (e.added.accountType === "IA" || e.added.accountType === 'SUB' || e.added.accountType === 'MRK' || e.added.accountType === 'INT') {
                                $(".inhouse-agents").hide();
                                $(".sub-agents").show();
                                $(".insurance-co").show();
                                $("#acct-photo").text("Photo");
                                populatePremiumAccounts();
                            } else {
                                $(".inhouse-agents").show();
                                $(".sub-agents").hide();
                                $("#acct-photo").text("Photo");
                                $(".insurance-co").hide();
                                populatePremiumAccounts();
                            }


                            if (e.added.accountType === 'SUB' || e.added.accountType === 'MRK' || e.added.accountType === 'INT') {
                                $("#parent-account-lov").select2("readonly", false).select2("val", "");
                            } else {
                                $("#parent-account-lov").select2("readonly", true).select2("val", "");
                            }
                        }
                    },
                    formatResult: function (a) {
                        return a.accName
                    },
                    formatSelection: function (a) {
                        return a.accName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#acc-id").val();
                        var name = $("#acc-name").val();
                        model.accounts.accType.accId = code;
                        var data = {accName: name, accId: code};
                        callback(data);
                    },
                    id: "accId",
                    placeholder: "Select Account Type",
                });
            }
        };

        var confirmAccountDel = function (button) {
            var account = JSON.parse(decodeURI($(button).data("account")));
            bootbox.confirm("Are you sure want to delete " + account["fname"] + " " + account["otherNames"] + "?", function (result) {
                if (result) {
                    $.ajax({
                        type: 'GET',
                        url: 'deleteAccount/' + account["acctId"],
                        dataType: 'json',
                        async: true,
                        success: function (result) {
                            bootbox.alert("Record deleted Successfully");
                            $('#acctbl').DataTable().ajax.reload();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            bootbox.alert(jqXHR.responseText);
                        }
                    });
                }

            });
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

        var populatePremiumAccounts = function () {
            if ($("#dr-account-frm").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "dr-account-frm",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#payable-premium").val(e.added.coId);

                    },
                    formatResult: function (a) {
                        console.log("a.code" + a.code);
                        return a.code + " - " + a.name;

                    },
                    formatSelection: function (a) {
                        return a.code + " - " + a.name;
                    },
                    initSelection: function (element, callback) {
                        var code = $('#payable-premium-code').val();
                        var name = $("#payable-premium-name").val();
                        var coId = $("#payable-premium").val();

                        var data = {name: name, code: code, coId: coId};
                        console.log("pay name" + name);
                        console.log("pay code" + code);
                        callback(data);
                    },
                    id: "coId",
                    width: "250px",
                    placeholder: "Select Account"

                });
            }
            if ($("#cr-account-frm").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "cr-account-frm",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#receivable-premium").val(e.added.coId);

                    },
                    formatResult: function (a) {
                        return a.code + " - " + a.name;
                    },
                    formatSelection: function (a) {
                        return a.code + " - " + a.name;
                    },
                    initSelection: function (element, callback) {
                        var code = $('#receivable-premium-code').val();
                        var name = $("#receivable-premium-name").val();
                        var coId = $("#receivable-premium").val();

                        var data = {name: name, code: code, coId: coId};
                        console.log("Receive name" + name);
                        console.log("Receive code" + code);
                        callback(data);
                    },
                    id: "coId",
                    width: "250px",
                    placeholder: "Select Account"

                });
            }
            if ($("#whtx-account-frm").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "whtx-account-frm",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#whtx").val(e.added.coId);

                    },
                    formatResult: function (a) {
                        return a.code + " - " + a.name;
                    },
                    formatSelection: function (a) {
                        return a.code + " - " + a.name;
                    },
                    initSelection: function (element, callback) {
                        var code = $('#whtx-code').val();
                        var name = $("#whtx-name").val();
                        var coId = $("#whtx").val();

                        var data = {name: name, code: code, coId: coId};
                        console.log("Receive name" + name);
                        console.log("Receive code" + code);
                        callback(data);
                    },
                    id: "coId",
                    width: "250px",
                    placeholder: "Select Account"

                });
            }
            if ($("#comm-account-frm").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "comm-account-frm",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#comm").val(e.added.coId);

                    },
                    formatResult: function (a) {
                        return a.code + " - " + a.name;
                    },
                    formatSelection: function (a) {
                        return a.code + " - " + a.name;
                    },
                    initSelection: function (element, callback) {
                        var code = $('#comm-code').val();
                        var name = $("#comm-name").val();
                        var coId = $("#comm").val();

                        var data = {name: name, code: code, coId: coId};
                        console.log("Receive name" + name);
                        console.log("Receive code" + code);
                        callback(data);
                    },
                    id: "coId",
                    width: "250px",
                    placeholder: "Select Account"

                });
            }
            if ($("#admin-account-frm").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "admin-account-frm",
                    sort: 'name',
                    change: function (e, a, v) {
                        $("#admin").val(e.added.coId);

                    },
                    formatResult: function (a) {
                        return a.code + " - " + a.name;
                    },
                    formatSelection: function (a) {
                        return a.code + " - " + a.name;
                    },
                    initSelection: function (element, callback) {
                        var code = $('#admin-code').val();
                        var name = $("#admin-name").val();
                        var coId = $("#admin").val();

                        var data = {name: name, code: code, coId: coId};
                        console.log("Receive name" + name);
                        console.log("Receive code" + code);
                        callback(data);
                    },
                    id: "coId",
                    width: "250px",
                    placeholder: "Select Account"

                });
            }
        };
        var populateAccountDetails = function (data) {
            $("#acctId-pk").val(data.acctId);
            $("#fname").val(data.name);
            $("#other-names").val(data.shtDesc);
            $("#idno").val(data.idPassportNo);
            $("#pinNo").val(data.pinNo);
            $("#email").val(data.email);
            $("#phone-no").val(data.phoneNo);
            $("#address").val(data.address);
            $("#insurance-type").val(data.insuranceType);
            if (data.status && data.status === 'A') {
                $("#btn-deactivate").css('display', 'block').val('Deactivate');
                $("#btn-approve").css('display', 'none');
                $("#btn-reject").css('display', 'none');
                $('form *').prop('disabled', true);
                $('#btn-add-docs').prop('disabled', true);
                $('#btn-assign-trans').css('display', 'none');
                $('#btn-submit').css('display','none')
                $('#sel2').text('Active')
            } else if (data.status && data.status === 'D') {
                $("#btn-deactivate").css('display', 'none');
                //$("#btn-approve").css('display','display').val('Approve');
                $("#btn-approve").css('display', 'block');
                $("#btn-reject").css('display', 'block');
                //$("#btn-reject").css('display','nine');
                $('form *').prop('disabled', false);
                $('#btn-add-docs').prop('disabled', false);
                $('#btn-assign-trans').css('display', 'none');
                $('#btn-submit').css('display','none')
                $('#sel2').text('Draft')

            } else if (data.status && data.status === 'DA') {
                $("#btn-deactivate").css('display', 'block').val('Activate');
                $("#btn-approve").css('display', 'none');
                $("#btn-reject").css('display', 'none');
                $('form *').prop('disabled', false);
                $('#btn-add-docs').prop('disabled', false);
                $('#btn-assign-trans').css('display', 'none');
                $('#btn-submit').css('display','block')
                $('#sel2').text('Deactivated')
            } else if (data.status && data.status === 'NA') {
                $("#btn-deactivate").css('display', 'none');
                $("#btn-approve").css('display', 'none');
                $("#btn-reject").css('display', 'none');
                $('form *').prop('disabled', false);
                $('#btn-add-docs').prop('disabled', false);
                $('#btn-assign-trans').css('display', 'block');
                $('#btn-submit').css('display','none')
                $('#sel2').text('Not Approved')

            }
            $("#h4pol").text("Name: " + data.name);
            if (data.dob)
                $("#dob").val(moment(data.dob).format('DD/MM/YYYY'));
            if (data.wef)
                $("#from-date").val(moment(data.wef).format('DD/MM/YYYY'));
            if (data.wet)
                $("#wet-date").val(moment(data.wet).format('DD/MM/YYYY'));
            $("#acc-id").val(data.acctTypeId);
            $("#acc-name").val(data.accountTypeName);
            createAccountTypeSelect();
            if (data.branchId) {
                $("#obId").val(data.branchId);
                $("#ob-name").val(data.branchName);
            }
            if (data.parentAcctId) {
                $("#parent-account-id").val(data.parentAcctId);
                $("#parent-account-name").val(data.parentAcctName);
                populateParentControlAcc();
            }
            $("#phy-address").val(data.physaddress);
            $("#cont-title").val(data.contactTitle);
            $("#acct-acct-no").val(data.bankAccount);
            $("#acct-paybill").val(data.paybillNumber);
            $("#contact-person").val(data.contactPerson);
            $("#acct-mob-tel").val(data.payTelNo);
            if (data.paymentMode) {
                $("#pm-id").val(data.paymentModeId);
                $("#pm-name").val(data.paymentMode);
            }
            populatePaymentModes();
            $("#absa_No").val(data.absaNo);
            $("#commCheckbox").val(data.commissionEarning);
            $("#receivable-premium-code").val(data.receivableAccount);
            $("#receivable-premium").val(data.receivableAccountId);
            $("#receivable-premium-name").val(data.receivableAccountName);
            $("#payable-premium-code").val(data.payableAccount);
            $("#payable-premium").val(data.payableAccountId);
            $("#payable-premium-name").val(data.payableAccountName);
            $("#whtx-code").val(data.whtxAccount);
            $("#whtx").val(data.whtxAccountId);
            $("#whtx-name").val(data.whtxAccountName);
            $("#comm-code").val(data.commAccount);
            $("#comm").val(data.commAccountId);
            $("#comm-name").val(data.commAccountName);
            $("#admin-code").val(data.adminAccount);
            $("#admin").val(data.adminAccountId);
            $("#admin-name").val(data.adminAccountName);
            populatePremiumAccounts();
            if (data.accountTypeId) {
                if (data.accountTypeId === "IA" || data.accountTypeId === "SUB" || data.accountTypeId === "MRK" || data.accountTypeId === "INT") {
                    $(".inhouse-agents").hide();
                    $(".sub-agents").show();
                    $("#acct-photo").text("Photo");
                    // $("#acct-date-reg").text("Date of Birth");
                    $(".insurance-co").show();
                } else {
                    $(".inhouse-agents").show();
                    $(".sub-agents").hide();
                    $("#acct-photo").text("Logo");
                    $("#acct-date-reg").text("Date of Reg");
                    $(".insurance-co").hide();
                }
            }
            if (data.bankName) {
                $("#acct-bank-id").val(data.bankId);
                $("#acct-bank-name").val(data.bankName);
                createBanksForSel();
                if (data.bankBranchName) {
                    $("#bank-branch-id").val(data.bankBranchId);
                    $("#bank-branch-name").val(data.bankBranchName);
                    populateBankBranches(data.bankId);
                } else {
                    populateBankBranches(data.bankId);
                }
            }

            createBranchSelect();
            accountImage(data.acctId);
            getAccountDocs(data.acctId);
        };

        var getAccountDetails = function () {
            if (typeof accIdpk !== 'undefined') {
                if (accIdpk !== -2000) {
                    $.ajax({
                        url: SERVLET_CONTEXT + '/protected/setups/accounts/' + accIdpk,
                        type: 'GET',
                        processData: false,
                        contentType: false,
                        success: function (s) {
                            $("#h4pol").show();
                            populateAccountDetails(s);
                            $("#show-docs").css("display", "block");
                            $("#tab_content2").css("display", "block");
                        },
                        error: function (xhr, error) {
                            bootbox.alert(xhr.responseText);
                        }
                    });
                } else {
                    $("#h4pol").hide();
                    accountImage(-2000);
                    $("#show-docs").css("display", "none");
                    $("#tab_content2").css("display", "none");
                    $("#btn-deactivate").css('display', 'none');
                    $("#btn-reject").css('display', 'none');
                    $("#btn-approve").css('display', 'none');
                    $('#btn-assign-trans').css('display', 'none');
                }

            }
        };

        var validateDetails = function () {
            pinNoElement.on('change', function () {
                var pinNo = pinNoElement.val();
                if (pinNo !== '') {
                    $.ajax({
                        type: 'GET',
                        url: 'validatePin',
                        dataType: 'json',
                        data: {"pinNo": pinNo},
                        async: true,
                        success: function (result) {

                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            Swal.fire({
                                title: 'Error',
                                text: jqXHR.responseText,
                                icon: 'error'
                            });
                            pinNoElement.val("");
                        }
                    });
                }
            });
        };

        var createAccounts = function () {
            var url = "allaccounts/0";
            if ($("#accId").val() != '') {
                url = "allaccounts/" + $("#accId").val();
            }
            return $('#acctbl').DataTable(UTILITIES.extendsOpts({
                "ajaxUrl": url,
                "columns": [
                    {"data": "shtDesc"},
                    {"data": "name"},
                    {"data": "email"},
                    {"data": "phoneNo"},
                    {"data": "pinNo"},
                    {
                        "data": "status",
                        "render": function (data, type, full, meta) {
                            if (!full.status || full.status === "I") {
                                return "Inactive";
                            } else if (full.status === "A")
                                return "Active";
                            else if (full.status === "DA")
                                return "Deactivated";
                            else if (full.status === "DR")
                                return "Draft";
                        }
                    },
                    {"data": "createdBy"},
                    {"data": "updatedBy"},
                    {
                        "data": "acctId",
                        "render": function (data, type, full, meta) {
                            return '<form action="editAcctForm" method="post"><input type="hidden" name="id" value=' + full.acctId + '><input type="submit"  class="btn btn-success btn btn-info btn-sm" value="Edit" ></form>';
                        }

                    },
                    {
                        "data": "acctId",
                        "render": function (data, type, full, meta) {
                            return '<input type="button" class="btn btn-danger btn btn-info btn-sm" data-account=' + encodeURI(JSON.stringify(full)) + ' value="Delete" onclick="confirmAccountDel(this);"/>';
                        }

                    },
                ]
            }));
        };


        var createBanksForSel = function () {
            if ($("#bank-lov").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "bank-lov",
                    sort: 'bankName',
                    change: function (e, a, v) {
                        $("#acct-bank-id").val(e.added.bnId);
                        populateBankBranches(e.added.bnId);
                    },
                    formatResult: function (a) {
                        return a.bankName
                    },
                    formatSelection: function (a) {
                        return a.bankName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#acct-bank-id").val();
                        var name = $("#acct-bank-name").val();
                        var data = {bankName: name, bnId: code};
                        callback(data);
                    },
                    id: "bnId",
                    placeholder: "Select Bank",
                });
            }
        };

        var populateBankBranches = function (bankCode) {
            if ($("#bank-branch-lov").filter("div").html() != undefined) {
                Select2Builder.initAjaxSelect2({
                    containerId: "bank-branch-lov",
                    sort: 'branchName',
                    change: function (e, a, v) {
                        $("#bank-branch-id").val(e.added.bbId);
                    },
                    formatResult: function (a) {
                        return a.branchName
                    },
                    formatSelection: function (a) {
                        return a.branchName
                    },
                    initSelection: function (element, callback) {
                        var code = $("#bank-branch-id").val();
                        var name = $("#bank-branch-name").val();
                        var data = {branchName: name, bbId: code};
                        callback(data);
                    },
                    id: "bbId",
                    placeholder: "Select Bank Branch",
                    params: {bankCode: bankCode}
                });
            }
        };


        var getAccountDocs = function (acctId) {
            var ajaxUrl = SERVLET_CONTEXT + "/protected/setups/accountDocs/" + acctId;
            var currTable = $('#accDocsList').DataTable(UTILITIES.extendsOpts({
                "ajaxUrl": ajaxUrl,
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
                    {"data": "initiator"},
                    {"data": "approver"},
                    {
                        "data": "approvedDate",
                        "render": function (data, type, full, meta) {
                            return full.approvedDate ? full.approvedDate : "";
                        }
                    }, {
                        "data": "adId",
                        "render": function (data, type, full, meta) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="AccountDef.editAcctDocs(this);"><i class="fa fa-pencil-square-o"></button>';
                        }

                    },
                    {
                        "data": "adId",
                        "render": function (data, type, full, meta) {
                            return '<button type="button" class="btn btn-success btn btn-info btn-sm" type="button" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="AccountDef.downloadAcctDoc(this);"><i class="fa fa-file-archive-o"></button>';

                        }

                    },
                    {
                        "data": "adId",
                        "render": function (data, type, full, meta) {
                            return '<button type="button" class="btn btn-danger btn btn-info btn-sm" data-docs=' + encodeURI(JSON.stringify(full)) + ' onclick="AccountDef.deleteAcctDoc(this);"><i class="fa fa-trash-o"></button>';
                        }

                    },
                ]
            }));
            return currTable;
        };

        var deleteAcctDoc = function (button) {
            var docs = JSON.parse(decodeURI($(button).data("docs")));
            bootbox.confirm("Are you sure want to delete " + docs['requiredDoc'].reqDesc + "?", function (result) {
                if (result) {
                    $.ajax({
                        type: 'GET',
                        url: 'deleteAcctDoc/' + docs['adId'],
                        dataType: 'json',
                        async: true,
                        success: function (result) {
                            // $('#myPleaseWait').modal('hide');
                            Swal.fire({
                                title: 'Success',
                                text: 'Record Deleted Successfully',
                                icon: 'success'
                            });
                            $('#accDocsList').DataTable().ajax.reload();
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
        var downloadAcctDoc = function (button) {
            var docs = JSON.parse(decodeURI($(button).data("docs")));
            window.open(SERVLET_CONTEXT + "/protected/setups/acctDocument/" + docs['adId'],
                '_blank' // <- This is what makes it open in a new window.
            );
        };

        var editAcctDocs = function (button) {
            var docs = JSON.parse(decodeURI($(button).data("docs")));
            $("#acct-doc-name").text(docs["requiredDoc"].reqDesc);
            $("#acct-upload-name").text(docs['uploadedFileName']);
            $("#acct-doc-id").val(docs['adId']);
            $('#acctdocModal').modal({
                backdrop: 'static',
                keyboard: true
            });
        };

        var uploadAcctDocument = function () {
            var $form = $("#accts-doc-form");
            var validator = $form.validate();
            $('form#accts-doc-form')
                .submit(function (e) {
                    e.preventDefault();

                    if (!$form.valid()) {
                        return;
                    }

                    var data = new FormData(this);
                    data.append('file', $('#acct-avatar')[0].files[0]);
                    $.ajax({
                        url: SERVLET_CONTEXT + '/protected/setups/uploadAccountDocs',
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
                            $('#acctdocModal').modal('hide');
                            var $el = $('#acct-avatar');
                            $el.wrap('<form>').closest('form').get(0).reset();
                            $el.unwrap();
                            $('#accDocsList').DataTable().ajax.reload();

                        },
                        error: function (xhr, error) {
                            // $('#myPleaseWait').modal('hide');
                            Swal.fire({
                                title: 'Error',
                                text: xhr.responseText,
                                icon: 'error'
                            });
                        }
                    });
                });
        };

        var searchReqDocs = function (search) {
            if ($("#acctId-pk").val() != '') {
                $.ajax({
                    type: 'GET',
                    url: 'acctreqdocs',
                    dataType: 'json',
                    data: {"acctCode": $("#acctId-pk").val(), "docName": search},
                    async: true,
                    success: function (result) {
                        $("#acctDocsTbl tbody").each(function () {
                            $(this).remove();
                        });
                        for (var res in result) {
                            var markup = "<tr><td><input type='checkbox' name='record' id='" + result[res].reqId + "'></td><td>" + result[res].reqShtDesc + "</td><td>" + result[res].reqDesc + "</td></tr>";
                            $("#acctDocsTbl").append(markup);
                        }
                        $("#req-acct-code").val($("#acctId-pk").val());
                        $('#acctReqDocsModal').modal({
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
                bootbox.alert("No Account to attach Documents")
            }
        };


        var approveAccount = function () {
            var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url: SERVLET_CONTEXT + '/protected/setups/approveAccount/' + accIdpk,
                dataType: 'json',
                async: true,
                success: function (result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Intermediary Approved Successfully',
                        icon: 'success'
                    });
                    AccountDef.getAccountDetails();
                    window.location.href = SERVLET_CONTEXT + "/protected/home";
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
        var rejectAccount = function () {
            var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $('#rejectionModal').modal('show');
            $('#rejectConfirmButton').off('click').on('click', function () {
                var reason = $('#rejectionReason').val();
                if (reason) {
                    $.ajax({
                        type: 'GET',
                        url: SERVLET_CONTEXT + '/protected/setups/rejectAccount/' + accIdpk,
                        dataType: 'json',
                        data: {reason: reason},
                        async: true,
                        success: function (result) {
                            // $('#myPleaseWait').modal('hide');
                            Swal.fire({
                                title: 'Success',
                                text: 'Intermediary Rejected Successfully',
                                icon: 'success'
                            });
                            AccountDef.getAccountDetails();
                            $('#rejectionModal').modal('hide');
                            window.location.href = SERVLET_CONTEXT + "/protected/home";
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
                } else {
                    Swal.fire({
                        title: 'Error',
                        text: 'No reason provided for rejection',
                        icon: 'error'
                    });
                }
                $('#rejectionModal').modal('hide');
            });

        }

        var loadEligibleCheckers = function (searchParam) {
            $.ajax({
                url: SERVLET_CONTEXT + '/protected/home/getEligibleCheckers', // Endpoint for getting eligible checkers
                type: 'GET',
                data: {permissionName: 'CREATE_INSURANCE_COMPANIES', searchParam: searchParam},
                success: function (response) {
                    // Populate checkboxes with eligible checkers
                    $("#checkersList tbody").each(function () {
                        $(this).remove();
                    });
                    for (var res in response) {
                        var absaNo = response[res].absaNo !== null ? response[res].absaNo : '';
                        var markup = "<tr>" +
                            // "<td><input type='hidden' id='checker-id'></td>" +
                            "<td><input type='checkbox'  class='checker-checkbox' value='" + response[res].id + "' id='" + response[res].id + "'></td><td>" + response[res].username + "</td><td>" + absaNo + "</td>" +
                            "</tr>";
                        $('#checkersList').append(markup)

                    }
                    $('#assignCheckerModal').modal({
                        backdrop: 'static',
                        keyboard: true
                    });
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

        var deactivateAccount = function () {
            var stack_bottomleft = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
            // // $('#myPleaseWait').modal({
//			backdrop: 'static',
//			keyboard: true
//		})
            $.ajax({
                type: 'GET',
                url: SERVLET_CONTEXT + '/protected/setups/deactivateAccount/' + accIdpk,
                dataType: 'json',
                async: true,
                success: function (result) {
                    // $('#myPleaseWait').modal('hide');
                    Swal.fire({
                        title: 'Success',
                        text: 'Intermediary Activated/Deactivated Successfully',
                        icon: 'success'
                    });
                    AccountDef.getAccountDetails();
                    window.location.reload();
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


        var saveAcctDocsList = function () {
            var arr = [];
            $("#saveAcctDocsBtn").click(function () {
                $("#acctDocsTbl tbody").find('input[name="record"]').each(function () {
                    if ($(this).is(":checked")) {
                        arr.push($(this).attr("id"));
                    }
                });
                if (arr.length == 0) {
                    bootbox.alert("No Documents Selected to attach..");
                    return;
                }

                var $currForm = $('#acct-doc-form');
                var currValidator = $currForm.validate();
                if (!$currForm.valid()) {
                    return;
                }

                var data = {};
                $currForm.serializeArray().map(function (x) {
                    data[x.name] = x.value;
                });
                var url = "createAcctDocs";
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
                        $('#accDocsList').DataTable().ajax.reload();
                        $('#acctReqDocsModal').modal('hide');
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
        };


        var init = function () {
            $(".datepicker-input").each(function () {
                $(this).datetimepicker({
                    format: 'DD/MM/YYYY'
                });

            });

            $(document).ajaxStart(function () {
                $("#btn-deactivate,#btn-approve,#btn-reject,#btn-submit").attr("disabled", true);
            });
            $(document).ajaxComplete(function () {
                $("#btn-deactivate,#btn-approve,#btn-reject,#btn-submit").attr("disabled", false);
            });

            $("#h4pol").hide();

            $("#btn-deactivate").click(function () {
                deactivateAccount();
            });
            $("#btn-approve").click(function () {
                approveAccount();
            });
            $("#btn-reject").click(function () {
                rejectAccount();
            });

            $("#btn-assign-trans").click(function () {
                loadEligibleCheckers('');
            });
            $('#searchChecker').on('keyup', function () {
                loadEligibleCheckers($(this).val());
            });
            $('#assign-checker-submit').click(function () {
                createAccount("NA");
            });

            $('#btn-submit').click(function () {
                createAccount("");
            });

            $('#integration-type').on('change', function () {
                if (this.value && this.value === 'N') {
                    $("#integration-url").attr('disabled', 'disabled').val('');
                    $("#integration-username").attr('disabled', 'disabled').val('');
                    $("#integration-password").attr('disabled', 'disabled').val('');
                } else if (this.value && this.value === 'I') {
                    $("#integration-url").removeAttr('disabled').val('');
                    $("#integration-username").attr('disabled', 'disabled').val('');
                    $("#integration-password").attr('disabled', 'disabled').val('');
                } else {
                    $("#integration-url").removeAttr('disabled').val('');
                    $("#integration-username").removeAttr('disabled').val('');
                    $("#integration-password").removeAttr('disabled').val('');
                }

            });

            createBranchSelect();
            createAccountTypeSelect();
            populatePaymentModes();
            getAccountDetails();
            createBanksForSel();
            validateDetails();
            saveAcctDocsList();
            createAccounts();
            uploadAcctDocument();
            populateParentControlAcc();
            $("#btn-add-docs").click(function () {
                searchReqDocs("");
            });
            $("#searchDocuments").click(function () {
                searchReqDocs($("#doc-name-search").val());
            });
            var genShtDesc = UTILITIES.getParamValue("AUTO_ACCOUNT_ID_GEN");
            if (genShtDesc && genShtDesc === "Y") {
                $("#other-names").prop("readonly", true);
            } else {
                $("#other-names").removeAttr('readonly');
            }

            if ($("#acc-type-desc").val() === 'SUB' || $("#acc-type-desc").val() === 'MRK' || $("#acc-type-desc").val() === 'INT') {
                $("#parent-account-lov").select2("readonly", false);
            } else {
                $("#parent-account-lov").select2("val", "").select2("readonly", true);
            }
        };

        return {
            init: init,
            editAcctDocs: editAcctDocs,
            downloadAcctDoc: downloadAcctDoc,
            deleteAcctDoc: deleteAcctDoc,
            getAccountDetails: getAccountDetails
        };

    })(jQuery);
</script>