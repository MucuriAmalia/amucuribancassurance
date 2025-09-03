<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/banks.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Banks and Branches</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="x_panel">
            <div class="x_title">
                <h2>Banks</h2>
                <ul class="nav navbar-right panel_toolbox">
                    <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                    </li>
                </ul>
                <div class="clearfix"></div>
            </div>
            <div class="x_content">
                <div class="card-box table-responsive">
                <input type="button" class="btn btn-info float-right"
                       style="margin-right: 10px;" value="New"
                       id="btn-add-banks">

                <table id="banks-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Remarks</th>
                        <th>EFT Supported</th>
                        <th>Active</th>
                        <th width="5%"></th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
                    </div>
                <input type="hidden" id="bank-trans-pk">
            </div>
            <div class="x_title">
                <h2>Bank Branches</h2>
                <ul class="nav navbar-right panel_toolbox">
                    <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                    </li>
                </ul>
                <div class="clearfix"></div>
            </div>
            <div class="x_content">
                <div class="card-box table-responsive">
                <input type="button" class="btn btn-info float-right"
                       style="margin-right: 10px;" value="New"
                       id="btn-add-bankbranch">

                <table id="bank-branch-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Ref Code</th>
                        <th>Remarks</th>
                        <th>EFT Supported</th>
                        <th>Postal Address</th>
                        <th>Physical Address</th>
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


<div class="modal fade" id="banksModal" tabindex="-1" role="dialog"
     aria-labelledby="banksModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="banksModalLabel">
                    Edit/Add Banks
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="banks-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="bank-code-pk" name="bnId">

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Bank Sht Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="bank-code"
                                   name="bankShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Bank Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="bank-name"
                                   name="bankName"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Bank Remarks</label>

                        <div class="col-md-8">
                           <textarea rows="3" cols="20" name="bankRemarks" id="bank-remarks"
                                     class="editUserCntrls form-control"></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">EFT Supported</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="eftSupported" id="chk-eft-supported">
                            </label>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Active Indicator</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="active" id="chk-active">
                            </label>

                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBanks"
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


<div class="modal fade" id="bankBranchModal" tabindex="-1" role="dialog"
     aria-labelledby="bankBranchModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="bankBranchModalLabel">
                    Edit/Add Bank Branch
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="branch_model">
                <form id="bank-branch-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="bank-branch-pk" name="bbId">
                    <input type="hidden" class="form-control" id="branch-bn-pk" name="bank">

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Branch Sht Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="branch-sht-desc"
                                   name="branchShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Branch Ref Code</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="ref-code"
                                   name="branchRefCode"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Branch Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="branch-name"
                                   name="branchName"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Branch Remarks</label>

                        <div class="col-md-8">
                           <textarea rows="3" cols="20" name="branchRemarks" id="branch-remarks"
                                     class="editUserCntrls form-control"></textarea>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">EFT Supported</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="eftSupported" id="branch-eft-supported">
                            </label>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Postal Address</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="branch-postal-address"
                                   name="postalAddress"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Physical Address</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="branch-phy-address"
                                   name="physicalAddress"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBankBranch"
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