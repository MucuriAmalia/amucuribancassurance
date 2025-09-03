<%--
  Created by IntelliJ IDEA.
  User: hp
  Date: 2/10/2020
  Time: 4:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/xlsx.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/coa.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/clients/clients.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/products/product.js"/>" ></script>

<div class="x_panel">
    <div class="x_title">
        <h2>Product Reports Setup</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <form class="form-horizontal">
            <div class="item form-group">
                <label for="brn-id" class="col-md-3 label-align">
                    Report Groups</label>

                <div class="col-md-4">
                    <input type="hidden" id="rpt-id" name="productReportGroup"/>
                    <input type="hidden" id="rpt-name"/>
                    <div id="rpt-group" class="form-control"
                         select2-url="<c:url value="/protected/setups/prdrptgroups"/>" >

                    </div>

                </div>
                <div class="col-md-4">
                    <button type="button" class="btn btn-info btn-sm" id="btn-add-rptgroup">New</button>
                    <button type="button" class="btn btn-info btn-sm" id="btn-edit-rptgroup">Edit</button>
                </div>
            </div>


        </form>

    </div>



    <div class="row" id="spec-prod">
        <div class="x_title">CURRENT PRODUCTS</div>

        <div class="col-md-10">

            <table id="prodList" class="table table-striped" style="width: 100%">
                <thead>
                <tr class="headings">
                    <th>PRODUCT</th>
                    <th>PRODUCT PREFIX</th>
                    <th>ACTIVE</th>
                </tr>
                </thead>

            </table>
        </div>
    </div>
        <div class="col-md-2">
        </div>
</div>

<div class="modal fade" id="prodRptGrpModal" tabindex="-1" role="dialog"
     aria-labelledby="prodRptGrpModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="prodRptGrpModalLabel">
                    Add Product Report Group
                </h4>
            </div>
            <div class="modal-body">

                <form id="prgrpt-group-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="prgrptId" name="rptId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Report Group Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="rpt-desc"
                                   name="repDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Report Group Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="rpt-shtdesc"
                                   name="repShtDesc"  required>
                        </div>
                    </div>


                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveproductRptGrpBtn"
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
<div class="modal fade" id="prodModal" tabindex="-1" role="dialog"
     aria-labelledby="prodModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="prodModalLabel">
                    Edit/Add Product
                </h4>
            </div>
            <div class="modal-body">

                <form id="product-form" class="form-horizontal">
                    <input type="hidden" id="prg-group-pks" name="proGroup">
                    <input type="hidden" class="form-control" id="pro-code" name="proCode">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Product ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="pro-sht-desc"
                                   name="proShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Product Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="pro-name"
                                   name="proDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Policy Prefix</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="pro-pol-prefix"
                                   name="proPolPrefix"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Claim Prefix</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="pro-clm-prefix"
                                   name="proClmPrefix"  required>
                        </div>
                    </div>

                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Min Premium</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="pro-min-prem"
                                   name="minPrem">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Interface Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="pro-inter-type" name="interfaceType">
                                <option value="">Select Interface Type</option>
                                <option value="A">Accrual</option>
                                <option value="C">Cash</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Risk Note</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="pro-risk-note"
                                   name="riskNote">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Multi Sub Class?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="multiSubClass" id="chk-multi-class">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Renewable?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="renewable" id="chk-renewable">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Motor Product</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="motorProduct" id="chk-motor-prod">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Installment Allowed</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="installAllowed" id="chk-motor-install-allowd">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Midnight Expiry</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="midnightExp" id="chk-mid-exp">
                            </label>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Age Applicable</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="ageApplicable" id="chk-age-applicable">
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
                <button data-loading-text="Saving..." id="saveproductBtn"
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