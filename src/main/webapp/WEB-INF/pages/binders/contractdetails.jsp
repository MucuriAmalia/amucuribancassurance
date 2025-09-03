<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="<c:url value="/js/modules/binder/binder.js"/>"></script>
<script type="text/javascript">
    $(document).ready(function () {
        var urlParams = new URLSearchParams(window.location.search);
        var accountId = urlParams.get('acctId');
        var binId = urlParams.get('binId')
        var prodId = urlParams.get('prodId')
        if (binId && accountId && prodId) {
            if ($('#bind-calc-type').val() === 'E') {
                $('#integration').hide();
            }
            $("#act-bin-code").val(accountId);
            $("#binder-sel-pk").val(binId);
            $("#binder-det-code-pk").val(binId);
            model.product.proCode = prodId;
            $("#prd-id").val(prodId);
            createAcctBindDet();
            createCommRatesTbl(binId);
            createLifeCommRatesTbl(binId);
            createSubAgentCommRatesTbl(binId);
            createAdminFeeSetup(binId);
            createSubAgentLifeCommRatesTbl(binId);
            populateAcctsForm(binId);
            $('#binder-form input, #binder-form select, #binder-form textarea').prop('disabled', true);
            makeBinderDetSelection();
            // $("#btn-make-ready-binder").click(function () {
            //     makeBinReady(binId);
            // });
            // $("#btn-undo-make-ready-binder").click(function () {
            //     makeBinUndo(binId);
            // });
            $("#btn-auth-binder").click(function () {
                makeBinReady(binId);
            });
            $("#btn-unauth-binder").click(function () {
                makeBinUndo(binId);
            });
            $("#btn-clone-binder").click(function () {
                cloneBinder();
            })


        }
    });
</script>
<div class="x_panel">
    <div class="x_title">
        <h2>Edit Contract</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <input id="binder-sel-pk" type="hidden">
    <div id="contract-panel">
        <input action="action" type="button" style="background-color: royalblue"
               onclick="window.location.href = SERVLET_CONTEXT + '/protected/setups/binders/bindersHome';"
               class="btn btn-secondary float-left"
               value="Back"
               id="back-contract">
        <input type="button" class="btn btn-primary float-right"
               value="Unauthorize" id="btn-unauth-binder">
        <input type="button" class="btn btn-primary float-right"
               value="Authorize" id="btn-auth-binder">
        <%--        <input type="button" class="btn btn-primary float-right"--%>
        <%--               value="Unsubmit" id="btn-undo-make-ready-binder">--%>
        <%--        <input type="button" class="btn btn-primary float-right"--%>
        <%--               value="Submit" id="btn-make-ready-binder">--%>
        <input type="button" class="btn btn-primary float-right"
               value="Clone" id="btn-clone-binder">
    </div>
</div>
<div class="x_content">
    <form id="binder-form" class="form-horizontal">
        <input type="hidden" class="form-control" id="bind-code" name="binId">
        <input type="hidden" class="form-control" id="act-bin-code" name="account">
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="bin-type" class="col-md-3 label-align">Contract Type</label>
                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="bin-type" name="binType" required>
                        <option value="">Select Contract Type</option>
                        <option value="B">Contract</option>
                        <option value="M">Negotiable</option>
                    </select>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="bind-id" class="col-md-3 label-align">Contract ID</label>
                <div class="col-md-7 col-xs-12">
                    <input type="text" class="form-control" id="bind-id"
                           name="binShtDesc" required>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="bind-name" class="col-md-3 label-align">Contract Name</label>
                <div class="col-md-7 col-xs-12">
                    <input type="text" class="editUserCntrls form-control"
                           id="bind-name" name="binName"
                           required>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="bind-calc-type" class="col-md-3 label-align">Calculator Type</label>

                <div class="col-md-7 col-xs-12">
                    <select class="form-control" id="bind-calc-type" name="binType" required>
                        <option value="">Select Calculator Type</option>
                        <option value="E">Excel</option>
                        <option value="I">Integration</option>
                        <option value="N">Non-Motor</option>
                    </select>
                </div>
            </div>
        </div>
        <div class="item form-group" id="integration">
            <div class="col-md-6 col-xs-12">
                <label for="bind-calc-url" class="col-md-3 label-align">Quote/Proposal Creation URL</label>

                <div class="col-md-7 col-xs-12">
                    <input type="text" class="editUserCntrls form-control"
                           id="bind-calc-url" name="premiumUrl">
                </div>
            </div>

            <div class="col-md-6 col-xs-12">
                <label for="bind-policy-url" class="col-md-3 label-align">Policy Creation URL</label>

                <div class="col-md-7 col-xs-12">
                    <input type="text" class="editUserCntrls form-control"
                           id="bind-policy-url" name="policyUrl">
                </div>
            </div>
        </div>

        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="prd-code" class="col-md-3 label-align">Product</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="prd-id" rv-value="product.proCode" name="product"/>
                    <input type="hidden" id="pr-name">
                    <div id="prd-code" class="form-control"
                         select2-url="<c:url value="/protected/setups/binders/selproducts"/>">

                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="cur-def" class="col-md-3 label-align">Currency</label>
                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="cur-id" name="currency"/>
                    <input type="hidden" id="cur-name">
                    <div id="cur-def" class="form-control"
                         select2-url="<c:url value="/protected/setups/binders/activeCurrencies"/>">
                    </div>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="bind-pol-no" class="col-md-3 label-align">Min Premium</label>
                <div class="col-md-7 col-xs-12">
                    <input type="text" class="editUserCntrls form-control"
                           id="bind-min-prem" name="minPrem"
                           required>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="bin-remarks" class="col-md-3 label-align">Contract Remarks</label>
                <div class="col-md-7 col-xs-12">
								<textarea rows="3" cols="20" name="binRemarks" id="bin-remarks"
                                          class="editUserCntrls form-control"></textarea>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="chk-active" class="col-md-3 label-align">Active Indicator</label>
                <div class="col-md-7 col-xs-12">
                    <label>
                        <input type="checkbox" name="active" id="chk-active">
                    </label>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="bin-default" class="col-md-3 label-align">Default?</label>
                <div class="col-md-7 col-xs-12">
                    <label>
                        <input type="checkbox" name="binDefault" id="bin-default">
                    </label>
                </div>
            </div>
        </div>
        <div class="item form-group binder-fund">
            <div class="col-md-6 col-xs-12">
                <label for="bin-fund-binder" class="col-md-3 label-align">Fund Contract?</label>
                <div class="col-md-7 col-xs-12">
                    <label>
                        <input type="checkbox" name="fundBinder" id="bin-fund-binder">
                    </label>
                </div>
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
<div class="x_panel">
    <div class="x_title">
        <h2>Contract Details/Rate Tables</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="" role="tabpanel" data-example-id="togglable-tabs">
            <ul id="myTab-2" class="nav nav-tabs bar_tabs" role="tablist">
                <li role="presentation" class="active"><a href="#tab_content1-1"
                                                          id="home-tab-2" role="tab" data-toggle="tab"
                                                          aria-expanded="true">Contract Details</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content3"
                                                    role="tab" id="comm-rates-tab"
                                                    data-toggle="tab" aria-expanded="false">Commission
                    Rates</a>
                </li>
                <li role="presentation" class=""><a href="#admin-fee-rates-tab"
                                                    role="tab" id="admin-fee-rates-tab-3"
                                                    data-toggle="tab" aria-expanded="false">Admin Fee
                    Rates</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content5"
                                                    role="tab" id="life-comm-rates-tab"
                                                    data-toggle="tab" aria-expanded="false">Life
                    Commission
                    Rates</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content6"
                                                    role="tab" id="sub-comm-rates-tab"
                                                    data-toggle="tab" aria-expanded="false">Sub Agent
                    Commission Rates</a>
                </li>
                <li role="presentation" class=""><a href="#tab_content8"
                                                    role="tab" id="sub-life-comm-rates-tab"
                                                    data-toggle="tab" aria-expanded="false">Sub Agent
                    Commission Life Rates</a>
                </li>
            </ul>
            <div id="myTabContent-1" class="tab-content">
                <div role="tabpanel" class="tab-pane active"
                     id="tab_content1-1" aria-labelledby="home-tab-1">
                    <input type="hidden" id="binder-det-code-pk">
                    <input type="hidden" id="binder-det-sub-code">
                    <button type="button" class="btn btn-info btn-dis" id="btn-add-binder-det">New</button>
                    <div class="spacer"></div>
                    <div class="table-responsive">
                        <table id="binderDetList" class="table table-striped" style="width: 100%">
                            <thead>
                            <tr class="headings">
                                <th>Sub Class Id</th>
                                <th>Sub Class Name</th>
                                <th>Cover Type Id</th>
                                <th>Cover Type Name</th>
                                <th>Cover Summary</th>
                                <th>Min Prem</th>
                                <%--                                <th>Single Section Cover</th>--%>
                                <%--                                <th>Limits Per Section</th>--%>
                                <%--                                <th>No of Installments</th>--%>
                                <%--                                <th>Installment Percentages</th>--%>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content5" aria-labelledby="life-comm-rates-tab">
                    <button type="button" class="btn btn-info btn-dis"
                            id="btn-add-life-comm-rate">New
                    </button>
                    <input type="hidden" id="comm-binder-code">
                    <div class="table-responsive">
                        <table id="lifecommRatesList" class="table table-striped"
                               style="width: 100%">
                            <thead>
                            <tr class="headings">
                                <th>Term From</th>
                                <th>Term To</th>
                                <th>Yr From</th>
                                <th>Yr To</th>
                                <th>Rate</th>
                                <th>Div Factor</th>
                                <th>Pay Frequency</th>
                                <th>W.e.f</th>
                                <th>W.e.t</th>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content8" aria-labelledby="life-comm-rates-tab">
                    <button type="button" class="btn btn-info btn-dis"
                            id="btn-add-sub-life-comm-rate">New
                    </button>
                    <input type="hidden" id="sub-comm-binder-code">
                    <div class="table-responsive">
                        <table id="sub-lifecommRatesList" class="table table-striped"
                               style="width: 100%">
                            <thead>
                            <tr class="headings">
                                <th>Account Type</th>
                                <th>Term From</th>
                                <th>Term To</th>
<%--                                <th>Yr From</th>--%>
<%--                                <th>Yr To</th>--%>
                                <th>Rate</th>
                                <th>Div Factor</th>
                                <th>Pay Frequency</th>
                                <th>W.e.f</th>
                                <th>W.e.t</th>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content3" aria-labelledby="comm-rates-tab">

                    <button type="button" class="btn btn-info btn-dis"
                            id="btn-add-comm-rates">New
                    </button>
                    <div class="spacer"></div>
                    <div class="table-responsive">
                        <table id="commRatesList" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>

                                <!-- 	<th>Rate Desc</th> -->
                                <th>Rate Type</th>
                                <th>Rate</th>
                                <th>Div Factor</th>
                                <th>Range From</th>
                                <th>Range To</th>
                                <th>Applicable At</th>
                                <th>Trans Code</th>
                                <th>Active?</th>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="admin-fee-rates-tab" aria-labelledby="comm-rates-tab">

                    <button type="button" class="btn btn-info btn-dis"
                            id="btn-add-admin-fee">New
                    </button>
                    <div class="spacer"></div>
                    <div class="table-responsive">
                        <table id="admin-fee-list" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>

                                <th>Admin Fee Rate</th>
                                <th>Fee Rate Type</th>
                                <th>WHTX Rate</th>
                                <th>WHTX Rate Type</th>
                                <th>Excise Duty Rate</th>
                                <th>Excise Rate Type</th>
                                <th>Active?</th>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content6" aria-labelledby="sub-comm-rates-tab">

                    <button type="button" class="btn btn-info btn-dis"
                            id="btn-add-sub-comm-rates">New
                    </button>
                    <div class="spacer"></div>
                    <div class="table-responsive">
                        <table id="sub_commRatesList" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>

                                <!-- 	<th>Rate Desc</th> -->
                                <th>Rate Type</th>
                                <th>Rate</th>
                                <th>Div Factor</th>
                                <th>Range From</th>
                                <th>Range To</th>
                                <th>Applicable At</th>
                                <th>Trans Code</th>
                                <th>Active?</th>
                                <th width="5%"></th>
                                <th width="5%"></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
                <div class="x_panel">
                    <div class="x_title">
                        <h2>Premium Rates/Clauses/Limits of Liability/Perils</h2>
                        <ul class="nav navbar-right panel_toolbox">
                            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                            </li>
                        </ul>
                        <div class="clearfix"></div>
                    </div>
                    <div class="x_content">
                        <div class="" role="tabpanel" data-example-id="togglable-tabs">
                            <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                                <li role="presentation" class="active"><a href="#tab_content1"
                                                                          id="home-tab" role="tab"
                                                                          data-toggle="tab"
                                                                          aria-expanded="true">Premium Items</a>
                                </li>
                                <li role="presentation" class=""><a href="#tab_content-prem"
                                                                    role="tab" id="profile-tab-rates"
                                                                    data-toggle="tab" aria-expanded="false">Prem
                                    Rate Tables</a>
                                </li>
                                <li role="presentation" class=""><a href="#tab_content-sheet"
                                                                    role="tab" id="comp-sheet-names"
                                                                    data-toggle="tab" aria-expanded="false">Computation
                                    Sheets</a>
                                </li>

                                <li role="presentation" class=""><a href="#tab_content2"
                                                                    role="tab" id="profile-tab"
                                                                    data-toggle="tab"
                                                                    aria-expanded="false">Clauses</a>
                                </li>
                                <li role="presentation" class="">
                                    <a href="#tab_content4" role="tab" id="binder-perils" data-toggle="tab"
                                       aria-expanded="false">Perils</a>
                                </li>
                                <%--                                    <li role="presentation" class="">--%>
                                <%--                                        <a href="#tab_content09" role="tab" id="binder-req-docs" data-toggle="tab"--%>
                                <%--                                           aria-expanded="false">Required Documents</a>--%>
                                <%--                                    </li>--%>
                            </ul>
                            <div id="myTabContent" class="tab-content">
                                <div role="tabpanel" class="tab-pane active"
                                     id="tab_content1" aria-labelledby="home-tab">
                                    <button type="button" class="btn btn-info btn-dis" id="btn-add-prem-rates">
                                        New
                                    </button>
                                    <input type="hidden" id="binder-sc-code-pk">
                                    <div class="table-responsive">
                                        <table id="premList" class="table table-striped" style="width: 100%">
                                            <thead>
                                            <tr class="headings">
                                                <th>Premium Item</th>
                                                <th>Range Type</th>
                                                <th>Range From</th>
                                                <th>Range To</th>
                                                <th>Rate</th>
                                                <th>Rate Type</th>
                                                <th>Prorated Full</th>
                                                <th>Free Limit</th>
                                                <th>Mandatory</th>
                                                <th>Active?</th>
                                                <th></th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                            </tr>
                                            </thead>
                                        </table>
                                    </div>
                                </div>
                                <%--                                        <div class="item form-group life-comm-rates">--%>
                                <%--                                            <div class="x_title">--%>
                                <%--                                                <h2>Commission Rates</h2>--%>
                                <%--                                                <ul class="nav navbar-right panel_toolbox">--%>
                                <%--                                                    <li><a class="collapse-link"><i--%>
                                <%--                                                            class="fa fa-chevron-up"></i></a>--%>
                                <%--                                                    </li>--%>
                                <%--                                                </ul>--%>
                                <%--                                                <div class="clearfix"></div>--%>
                                <%--                                            </div>--%>
                                <%--                                        </div>--%>



                                <div role="tabpanel" class="tab-pane fade"
                                     id="tab_content-prem" aria-labelledby="profile-tab-2">
                                    <form id="prem-rate-upload-form" class="form-horizontal"
                                          enctype="multipart/form-data">
                                        <input type="hidden" class="form-control" id="rates-bind-det-code"
                                               name="binderDetails">
                                        <div class="col-md-10 col-xs-12 form-required">

                                            <div class="col-md-5 col-xs-12">
                                                <div class="input-group col-xs-12">
                                                    <input name="file" class="btn-dis" type="file"
                                                           id="file-avatar"
                                                           required>
                                                </div>
                                            </div>
                                            <div class="col-md-3 col-xs-12">
                                                <input type="submit"
                                                       class="btn btn-success btn-sm float-left btn-dis"
                                                       style="margin-right: 10px;" value="Upload">

                                            </div>
                                        </div>
                                    </form>
                                    <table id="prem-rates-table" class="table table-striped"
                                           style="width: 100%">
                                        <thead>
                                        <tr class="headings">

                                            <th width="40%">Rate Table Name</th>
                                            <th width="40%">Effective From Date</th>
                                            <th width="10%">Download</th>
                                            <th width="10%">Delete</th>
                                        </tr>
                                        </thead>
                                    </table>

                                </div>

                                <div role="tabpanel" class="tab-pane fade"
                                     id="tab_content-sheet" aria-labelledby="comp-sheet-names">
                                    <button type="button" class="btn btn-info btn-dis" id="btn-add-comp-sheet">
                                        New
                                    </button>
                                    <div class="table-responsive">
                                        <table id="compSheetList" class="table table-striped"
                                               style="width: 100%">
                                            <thead>
                                            <tr class="headings">
                                                <th>Id</th>
                                                <th>Sheet Name</th>
                                                <th>Computation Type</th>
                                                <th width="5%"></th>
                                                <th width="5%"></th>
                                            </tr>
                                            </thead>
                                        </table>
                                    </div>
                                </div>


                                <%--                                <div role="tabpanel" class="tab-pane fade"--%>
                                <%--                                     id="tab_content09" aria-labelledby="medical-covers-tab">--%>
                                <%--                                    <button type="button" class="btn btn-info btn-dis" id="btn-add-reqd-docs">New--%>
                                <%--                                    </button>--%>
                                <%--                                    <div class="cutom-container">--%>
                                <%--                                        <table id="reqDocsList" class="table" style="width:100%">--%>
                                <%--                                            <thead>--%>
                                <%--                                            <tr>--%>
                                <%--                                                <th>Id</th>--%>
                                <%--                                                <th>Desc</th>--%>
                                <%--                                                <th>Mandatory</th>--%>
                                <%--                                                <th width="5%"></th>--%>
                                <%--                                                <th width="5%"></th>--%>
                                <%--                                            </tr>--%>
                                <%--                                            </thead>--%>
                                <%--                                        </table>--%>
                                <%--                                    </div>--%>
                                <%--                                </div>--%>
                                <div role="tabpanel" class="tab-pane fade"
                                     id="tab_content2" aria-labelledby="profile-tab">

                                    <button type="button" class="btn btn-info btn-dis"
                                            id="btn-add-binder-clauses">New
                                    </button>
                                    <table id="bindclausesList" class="table table-striped" style="width:100%">
                                        <thead>
                                        <tr>

                                            <th>Clause Id</th>
                                            <th>Clause Heading</th>
                                            <th>Editable?</th>
                                            <th>Mandatory?</th>
                                            <th width="5%"></th>
                                            <th width="5%"></th>
                                        </tr>
                                        </thead>
                                    </table>

                                </div>
                                <div role="tabpanel" class="tab-pane fade"
                                     id="tab_content4" aria-labelledby="binder-perils">
                                    <div class="col-md-6 col-xs-12">
                                        <label for="binder-sect-form" class="label-align col-md-5">
                                            Select Section</label>
                                        <div class="col-md-7 col-xs-12">
                                            <input type="hidden" id="binder-sect-pk">
                                            <div id="binder-sect-form" class="form-control"
                                                 select2-url="<c:url value="/protected/setups/binders/selPerilSections"/>">

                                            </div>
                                            <button type="button" class="btn btn-info btn-dis"
                                                    id="btn-add-sect-perils">Add
                                            </button>
                                        </div>
                                    </div>


                                    <table id="binder-perils-tbl" class="table table-striped"
                                           style="width:100%">
                                        <thead>
                                        <tr>
                                            <th>Peril Id</th>
                                            <th>Peril</th>
                                            <th width="5%"></th>
                                        </tr>
                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>


            </div>

        </div>


    </div>


</div>
<jsp:include page="bindermodals/modals.jsp"></jsp:include>
