<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="modal fade" id="provTypesModal" tabindex="-1" role="dialog"
     aria-labelledby="provTypesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="provTypesModalLabel">Edit/Add Provider Types</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="prov-types-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="provtype-code" name="id">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">
                            ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="provtype-id"
                                   name="shtDesc" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="provtype-name" class="col-md-3 label-align">
                            Provider Type</label>

                        <div class="col-md-8">
                            <input type="text" class="editUserCntrls form-control"
                                   id="provtype-name" name="desc" required>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button id="delPrvdTypeBtn" type="button" class="btn btn-danger">
                    Delete</button>
                <button data-loading-text="Saving..." id="saveProvderTypeBtn"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel</button>
            </div>
        </div>
    </div>
</div>



<div class="modal fade" id="providerServiceModal" tabindex="-1" role="dialog"
     aria-labelledby="providerServiceModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="providerServiceModalLabel">Select Service</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Service</label>

                        <div class="col-md-6">
                            <input type="text" class="form-control" id="service-name-search"
                            >
                        </div>
                        <div class="col-md-1">
                            <button  id="searchService"
                                     type="button" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="servicestbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">service Id</th>
                            <th width="12%">Description</th>
                            <th width="12%">CPT Code</th>
                            <th width="12%">Upper Limit</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
                <form id="provider-service-form">
                    <input type="hidden" id="serv-provider-code" name="providerCode"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveProviderServiceBtn"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="serviceActityModal" tabindex="-1" role="dialog"
     aria-labelledby="serviceActityModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="serviceActityModalLabel">Select Activity</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Activity</label>

                        <div class="col-md-6">
                            <input type="text" class="form-control" id="activity-name-search"
                            >
                        </div>
                        <div class="col-md-1">
                            <button  id="searchActivity"
                                     type="button" class="btn btn-primary">
                                Search
                            </button>
                        </div>
                    </div>
                </form>
                <div style="height: 300px !important; overflow: scroll;">
                    <table class="table table-striped table-hover table-bordered table-fixed" id="activitytbl">
                        <thead>
                        <tr>
                            <th width="1%"></th>
                            <th width="4%">Activity Id</th>
                            <th width="12%">Description</th>
                            <th width="12%">Section</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
                <form id="service-activity-form">
                    <input type="hidden" id="serv-code" name="serviceCode"/>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveServiceActivityBtn"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel</button>
            </div>
        </div>
    </div>
</div>


<div class="modal fade" id="provsModal" tabindex="-1" role="dialog"
     aria-labelledby="provsModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="provsModalLabel">Edit/Add Providers</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="providers-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="provider-code" name="mspId">
                    <input type="hidden" class="form-control" id="prov-type-code" name="serviceProviderTypes">
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-name" class="label-align col-md-5">
                                Name<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="name" id="provd-name" class="form-control"
                                       placeholder="Name" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-phy-address" class="label-align col-md-5">
                                Physical Address<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="physicalAddress" id="provd-phy-address" class="form-control"
                                       placeholder="Physical Address" required>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-post-address" class="label-align col-md-5">
                                Postal Address</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="postalAddress" id="provd-post-address" class="form-control"
                                       placeholder="Postal Address" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-post-code" class="label-align col-md-5">
                                Postal Code</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="postalCode" id="provd-post-code" class="form-control"
                                       placeholder="Postal Code" required>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-tel-no" class="label-align col-md-5">
                                Tel Number</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="telNumber" id="provd-tel-no" class="form-control"
                                       placeholder="Tel No" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-email" class="label-align col-md-5">
                                Email Address</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="email" name="email" id="provd-email" class="form-control"
                                       placeholder="Email Address" required>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-cont-person" class="label-align col-md-5">
                                Contact Person</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="contactPerson" id="provd-cont-person" class="form-control"
                                       placeholder="Contact Person" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-cont-tel" class="label-align col-md-5">
                                Contact Tel No</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="contactTelNo" id="provd-cont-tel" class="form-control"
                                       placeholder="Contact Tel No" required>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="pm-mode-frm" class="label-align col-md-5">
                                Payment Mode</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="pm-id" name="paymentModes"/>
                                <input type="hidden" id="pm-name">
                                <div id="pm-mode-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/uwpaymentmodes"/>" >

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-pin" class="label-align col-md-5">
                                Pin No</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="pinNo" id="provd-pin" class="form-control"
                                       placeholder="Pin Number" required>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="chk-active" class="label-align col-md-5">
                                Active</label>
                            <div class="col-md-7 checkbox">
                                <label>
                                    <input type="checkbox" name="status" id="chk-active">
                                </label>

                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="chk-smart" class="label-align col-md-5">
                               Smart Enabled</label>
                            <div class="col-md-7 checkbox">
                                <label>
                                    <input type="checkbox" name="smartEnabled" id="chk-smart">
                                </label>

                            </div>
                        </div>

                    </div>

                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="chk-black-listed" class="label-align col-md-5">
                                Black Listed</label>
                            <div class="col-md-7 checkbox">
                                <label>
                                    <input type="checkbox" name="blacklisted" id="chk-black-listed">
                                </label>

                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="chk-accredited" class="label-align col-md-5">
                                Accredited</label>
                            <div class="col-md-7 checkbox">
                                <label>
                                    <input type="checkbox" name="accredited" id="chk-accredited">
                                </label>

                            </div>
                        </div>

                    </div>

                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="bank-branch-frm" class="label-align col-md-5">
                               Bank Branch</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="hidden" id="brn-id" name="bankBranches"/>
                                <input type="hidden" id="brn-name">
                                <div id="bank-branch-frm" class="form-control"
                                     select2-url="<c:url value="/protected/medical/setups/bankbranchsel"/>" >

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-account" class="label-align col-md-5">
                                Account Number</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="accountNumber" id="provd-account" class="form-control"
                                       placeholder="Account Number" required>
                            </div>
                        </div>

                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="provd-payee" class="label-align col-md-5">
                                Payee Name</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="payeeName" id="provd-payee" class="form-control"
                                       placeholder="Payee" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">

                        </div>

                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveProvdersBtn"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel</button>
            </div>
        </div>
    </div>
</div>