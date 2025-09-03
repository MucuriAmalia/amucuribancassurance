<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/coa.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Chart of Accounts</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="x_panel">
            <div class="x_title">
                <h2>Main Accounts</h2>
                <ul class="nav navbar-right panel_toolbox">
                    <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                    </li>
                </ul>
                <div class="clearfix"></div>
            </div>
            <div class="x_content">
                <div class="card-box table-responsive">
                    <style>
                        .export-dropdown .dropdown-menu { min-width: 180px; border-radius: .25rem; box-shadow: 0 .5rem 1rem rgba(0,0,0,.15); padding: .25rem 0; }
                        .export-dropdown .dropdown-item { padding: .5rem 1rem; display: flex; align-items: center; }
                        .export-dropdown .dropdown-item i { width: 20px; text-align: center; margin-right: .5rem; font-size: 1.1em; }
                        .export-dropdown .dropdown-divider { margin: .5rem 0; }
                    </style>
                    <div class="btn-toolbar float-right mb-3 export-dropdown" role="toolbar" style="margin-right:10px;">
                        <div class="btn-group" role="group">
                            <button type="button" class="btn btn-info dropdown-toggle" id="btn-export-main" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" data-toggle="tooltip" data-placement="bottom" title="Export Chart of Accounts">
                                <i class="fa fa-download"></i> Export
                            </button>
                            <div class="dropdown-menu dropdown-menu-right">
                                <button class="dropdown-item" onclick="window.open('<c:url value='/protected/accounts/printcoa?format=pdf'/>','_blank')">
                                    <i class=""></i> PDF
                                </button>
                                <div class="dropdown-divider"></div>
                                <button class="dropdown-item" onclick="window.open('<c:url value='/protected/accounts/printcoa?format=csv'/>','_blank')">
                                    <i class=""></i> CSV
                                </button>
                                <button class="dropdown-item" onclick="window.open('<c:url value='/protected/accounts/printcoa?format=xlsx'/>','_blank')">
                                    <i class=""></i> Excel
                                </button>
                            </div>
                        </div>
                        <div class="btn-group" role="group" style="margin-left:5px;">
                            <button type="button" class="btn btn-info" id="btn-add-coa" data-toggle="tooltip" data-placement="bottom" title="Add a new main account">
                                <i class="fa fa-plus"></i> New
                            </button>
                        </div>
                    </div>
                    <script>
                        $(function () { $('[data-toggle="tooltip"]').tooltip(); });
                    </script>
                          <table id="coa-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Header</th>
                        <th>Account Type</th>
                        <th>BS/PL</th>
                        <th>Accounts Order</th>
                        <th width="5%"></th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
                    </div>
                <input type="hidden" id="coa-trans-pk">
                <input type="hidden" id="coa-sub-trans-pk">
            </div>
            <div class="x_title">
                <h2>Sub Accounts</h2>
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
                       id="btn-add-subcoa">

                <table id="coa-sub-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Integration Account</th>
                        <th>Accounts Order</th>
                        <th>Control Account</th>
                        <th>Account Type</th>
                        <th>Applicable to Class</th>
                        <th>Class of Business</th>
                        <th>SAP GL (Retail)</th>
                        <th>SAP GL (Business)</th>
                        <th>SAP GL (Corporate)</th>
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


<div class="modal fade" id="coaModal" tabindex="-1" role="dialog"
     aria-labelledby="coaModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="coaModalLabel">
                    Edit/Add Main Accounts
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <form id="coa-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="acc-code-pk" name="coId">

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Code</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="acc-code-sht-desc"
                                   name="code"  required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="acc-code-name"
                                   name="name"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Header</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="acc-code-header"
                                   name="header"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Footer</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="acc-code-footer"
                                   name="footer"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Account Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="sel3" name="accountType" required>
                                <option value="">Select Account Type</option>
                                <option value="I">Income</option>
                                <option value="E">Expense</option>
                                <option value="A">Asset</option>
                                <option value="L">Liability</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Balance Sheet/PL</label>

                        <div class="col-md-8">
                            <select class="form-control" id="blpl" name="plBalSheet" required>
                                <option value="">Select  Type</option>
                                <option value="B">Balance Sheet</option>
                                <option value="P">Profit and Loss</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Accounts Order</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="acc-code-order"
                                   name="accountsOrder"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCoaAccounts"
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

<div class="modal fade" id="subcoaModal" tabindex="-1" role="dialog"
     aria-labelledby="subcoaModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="subcoaModalLabel">
                    Edit/Add Sub Accounts
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="branch_model">
                <form id="sub-coa-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="sub-acc-pk" name="coId">
                    <input type="hidden" class="form-control" id="sub-acc-main-pk" name="mainAccounts">

                    <div class="item form-group">
                        <label for="coa-sub-code" class="col-md-3 label-align">Code</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="coa-sub-code"
                                   name="code"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="coa-sub-name" class="col-md-3 label-align">Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="coa-sub-name"
                                   name="name"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="integrat-acc" class="col-md-3 label-align">Integration Account?</label>

                        <div class="col-md-8">
                            <select class="form-control" id="integrat-acc" name="integration" required>
                                <option value="">Select</option>
                                <option value="Y">Yes</option>
                                <option value="N">No</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="sub-acc-code-order" class="col-md-3 label-align">Accounts Order</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sub-acc-code-order"
                                   name="accountsOrder"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="control-acc" class="col-md-3 label-align">Control Account?</label>

                        <div class="col-md-8">
                            <select class="form-control" id="control-acc" name="controlAccount" required>
                                <option value="">Select</option>
                                <option value="Y">Yes</option>
                                <option value="N">No</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group control-acc" style="display: none">
                            <label for="brn-id" class="col-md-3 label-align">Account Type<span class="required">*</span></label>

                            <div class="col-md-8 col-xs-12">
                                <input type="hidden" id="acc-id" name="accountTypes"/>
                                <input type="hidden" id="acc-name">
                                <div id="accounttypes" class="form-control"
                                     select2-url="<c:url value="/protected/setups/selAcctTypes"/>" >

                                </div>
                            </div>
                    </div>
                    <div class="item form-group">
                        <label for="business-class-acc" class="col-md-3 label-align">Class Applicable?</label>

                        <div class="col-md-8">
                            <select class="form-control" id="business-class-acc" name="mappedToSubclass" required>
                                <option value="">Select</option>
                                <option value="Y">Yes</option>
                                <option value="N">No</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group business-class-acc" style="display: none">
                        <label for="brn-id" class="col-md-3 label-align">Select Class</label>

                        <div class="col-md-8">
                            <input type="hidden" id="sub-code" name="subClassDef"/>
                            <input type="hidden" id="sub-desc"/>
                            <div id="sub-class-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/clauses/subclassSelect"/>">

                            </div>

                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="sap-gl-acc" class="col-md-3 label-align">SAP GL (Retail)</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sap-gl-acc"
                                   name="sapGlAccount">
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="sap-gl-acc-business" class="col-md-3 label-align">SAP GL (Business) <span class="text-muted"><small>Optional</small></span></label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sap-gl-acc-business"
                                   name="sapGlAccountBusiness"
                                   placeholder="Enter SAP GL account for Business Banking segment">
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="sap-gl-acc-corporate" class="col-md-3 label-align">SAP GL (Corporate) <span class="text-muted"><small>Optional</small></span></label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sap-gl-acc-corporate"
                                   name="sapGlAccountCorporate"
                                   placeholder="Enter SAP GL account for Corporate Banking segment">
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveSubAccounts"
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