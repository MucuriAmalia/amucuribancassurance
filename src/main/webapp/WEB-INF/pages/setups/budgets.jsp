<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/xlsx.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/FileSaver.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/coa.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/clients/clients.js"/>" rel="stylesheet"></script>
<link type="text/css" href="<c:url value="/libs/css/upload.css"/>>">
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Budgets Setup</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>

    <input type="button" class="btn btn-info float-right"
           style="margin-right: 10px;" value="New"
           id="btn-add-budget">
    <input type="button" class="btn btn-info float-right"
           style="margin-right: 10px;" value="Import"
           id="btn-import">
    <button class="btn btn-danger float-right" id="btn-view-unloaded">Failed</button>
    <button class="btn btn-danger float-right" id="btn-delete-unloaded">Delete</button>
    <button class="btn btn-info float-right" id="btn-download-excel">Download</button>

    <div class="clearfix"></div>
    <div class="item form-group form-required">
        <div class="col-md-6">
            <label for="brn-id" class="col-md-5 label-align">Select
                Report Group</label>

            <div class="col-md-7">
                <input type="hidden" id="find-id" name="productReportGroup"/>
                <input type="hidden" id="find-name"/>
                <div id="find-rpt" class="form-control"
                     select2-url="<c:url value="/protected/setups/prdrptgroups"/>">

                </div>
            </div>
        </div>
    </div>
    <div class="clearfix"></div>
    <div class="table-responsive" id="loaded">
        <table id="budgets-tbl" class="table table-striped" style="width: 100%">
            <thead>
            <tr class="headings">
                <th>Name</th>
                <th>Product</th>
                <th>User</th>
                <th>Branch</th>
                <th>Year</th>
                <th>Jan Premium</th>
                <th>Jan Policies</th>
                <th>Feb Premium</th>
                <th>Feb Policies</th>
                <th>Mar Premium</th>
                <th>Mar Policies</th>
                <th>Apr Premium</th>
                <th>Apr Policies</th>
                <th>May Premium</th>
                <th>May Policies</th>
                <th>Jun Premium</th>
                <th>Jun Policies</th>
                <th>Jul Premium</th>
                <th>Jul Policies</th>
                <th>Aug Premium</th>
                <th>Aug Policies</th>
                <th>Sep Premium</th>
                <th>Sep Policies</th>
                <th>Oct Premium</th>
                <th>Oct Policies</th>
                <th>Nov Premium</th>
                <th>Nov Policies</th>
                <th>Dec Premium</th>
                <th>Dec Policies</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>
    </div>
    <input type="hidden" id="cr-trans-pk">
    <div class="table-responsive" id="unloaded">
        <table id="unloaded-budgets-tbl" class="table table-striped table-hover table-bordered bg-danger">
            <thead>
            <tr>
                <th>Name</th>
                <th>Product</th>
                <th>User</th>
                <th>Branch</th>
                <th>Year</th>
                <th>Error</th>
                <th>Jan Premium</th>
                <th>Jan Policies</th>
                <th>Feb Premium</th>
                <th>Feb Policies</th>
                <th>Mar Premium</th>
                <th>Mar Policies</th>
                <th>Apr Premium</th>
                <th>Apr Policies</th>
                <th>May Premium</th>
                <th>May Policies</th>
                <th>Jun Premium</th>
                <th>Jun Policies</th>
                <th>Jul Premium</th>
                <th>Jul Policies</th>
                <th>Aug Premium</th>
                <th>Aug Policies</th>
                <th>Sep Premium</th>
                <th>Sep Policies</th>
                <th>Oct Premium</th>
                <th>Oct Policies</th>
                <th>Nov Premium</th>
                <th>Nov Policies</th>
                <th>Dec Premium</th>
                <th>Dec Policies</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<div class="modal fade" id="budgetModal" tabindex="-1" role="dialog"
     aria-labelledby="budgetModalLabel" aria-hidden="true" data-keyboard="false" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="budgetModalLabel">
                    Budget
                </h4>
                <button type="button" id="disbud" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form method="POST" id="budget-form" class="form-horizontal" enctype="multipart/form-data">
                    <input type="hidden" name="id" id="myBudgetId">
                    <div class="item form-group">
                        <label for="name" class="label-align col-md-3">Budget Name<span
                                class="required">*</span></label>
                        <div class="col-md-6 col-xs-12">
                            <input type="text" name="name" id="budget_name" class="form-control"
                                   placeholder="Budget Name" required>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 col-xs-12">
                            <div class="item form-group">

                                <label for="brn-id" class="label-align col-md-3">Group</label>


                                <input type="hidden" id="rpt-id" name="productReportGroup"/>
                                <input type="hidden" id="rpt-name"/>
                                <div id="rpt-group" class="form-control"
                                     select2-url="<c:url value="/protected/setups/prdrptgroups"/>">

                                </div>

                            </div>
                        </div>

                        <div class="col-md-6 col-xs-12">
                            <div class="item form-group">
                                <label for="year" class="label-align col-md-3">Year<span
                                        class="required">*</span></label>
                                <input type="hidden" id="myValue" name="year" required>
                                <input type="hidden" id="yearname">
                                <div id="myYear" class="form-control"
                                     select2-url="<c:url value="/protected/setups/year"/>">

                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="custom-control custom-radio custom-control-inline">
                        <input type="radio" class="custom-control-input" id="per-user" name="budSet" value="U">
                        <label class="custom-label-align" for="per-user">Per-User</label>


                        <input type="radio" class="custom-control-input" id="per-branch" name="budSet" value="B">
                        <label class="custom-label-align" for="per-branch">Per-Branch</label>


                        <input type="radio" class="custom-control-input" id="both" name="budSet" value="UB">
                        <label class="custom-label-align" for="both">Both</label>
                    </div>
                    <div class="row">
                        <div class="col-md-6 col-xs-12" id="branch-per">
                            <div class="item form-group">
                                <label for="ten-branch" class="label-align col-md-3">Branch</label>

                                <input type="hidden" id="obId" name="orgBranch"/>
                                <input type="hidden" id="ob-name">
                                <div id="ten-branch" class="form-control"
                                     select2-url="<c:url value="/protected/setups/branches"/>">

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12" id="user-per">
                            <div class="item form-group">
                                <label for="usertype" class="label-align col-md-3">User<span
                                        class="required">*</span></label>

                                <input type="hidden" id="sub-agent-id" name="accountDef"/>
                                <input type="hidden" id="sub-agent-name">
                                <div id="sub-agent-frm" class="form-control"
                                     select2-url="<c:url value="/protected/uw/policies/inhouseagents"/>">

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 col-xs-12">
                            <div class="item form-group">
                                <div class="row">
                                    <label for="Jan" class="label-align col-md-3">Jan<span
                                            class="required">*</span></label>
                                </div>
                                <div class="row">
                                    <div class="col-md-5">
                                        <label for="janprod" class="label-align col-md-3">Production<span
                                                class="required">*</span></label>
                                        <input type="number" min="0" name="janProd" id="janprod" class="form-control"
                                               placeholder="0">
                                    </div>
                                    <div class="col-md-5">
                                        <label for="janpol" class="label-align col-md-3">Policies<span class="required">*</span></label>
                                        <input type="number" min="0" name="janPol" id="janpol" class="form-control"
                                               placeholder="0">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <div class="item form-group">
                                <div class="row">
                                    <label for="Feb" class="label-align col-md-3">Feb<span
                                            class="required">*</span></label>
                                </div>
                                <div class="row">
                                    <div class="col-md-5">
                                        <label for="febprod" class="label-align col-md-3">Production<span
                                                class="required">*</span></label>
                                        <input type="number" min="0" name="febProd" id="febprod" class="form-control"
                                               placeholder="0">
                                    </div>
                                    <div class="col-md-5">
                                        <label for="febpol" class="label-align col-md-3">Policies<span class="required">*</span></label>
                                        <input type="number" min="0" name="feb" id="febpol" class="form-control"
                                               placeholder="0">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">Mar<span class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="marprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="marProd" id="marprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="marpol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="marPol" id="marpol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">April<span
                                                class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="aprprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="aprProd" id="aprprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="aprpol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="aprPol" id="aprpol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">May<span class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="mayprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="mayProd" id="mayprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="maypol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="mayPol" id="maypol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">June<span
                                                class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="junprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="junProd" id="junprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="junpol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="junPol" id="junpol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">July<span
                                                class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="julprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="julProd" id="julprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="julpol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="julPol" id="julpol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">Aug<span class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="augprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="augProd" id="augprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="augpol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="augPol" id="augpol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">Sep<span class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="sepprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="sepProd" id="sepprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="seppol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="sepPol" id="seppol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">

                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">Oct<span class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="octprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="octProd" id="octprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="octpol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="octPol" id="octpol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">Nov<span class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="novprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="novProd" id="novprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="novpol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="novPol" id="novpol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-xs-12">
                                <div class="item form-group">
                                    <div class="row">
                                        <label for="Jan" class="label-align col-md-3">Dec<span class="required">*</span></label>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-5">
                                            <label for="decprod" class="label-align col-md-3">Production<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="decProd" id="decprod"
                                                   class="form-control"
                                                   placeholder="0">
                                        </div>
                                        <div class="col-md-5">
                                            <label for="decpol" class="label-align col-md-3">Policies<span
                                                    class="required">*</span></label>
                                            <input type="number" min="0" name="decPol" id="decpol" class="form-control"
                                                   placeholder="0">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">
                            <input type="submit" id="btn-save-budget" class="btn btn-success float-right"
                                   style="margin-right: 10px" value="Save">

                        </div>
                        <div class="col-md-5">
                            <button type="button" id="fdisbud" class="btn btn-default" data-dismiss="modal">
                                Cancel
                            </button>
                        </div>
                    </div>
                </form>

            </div>
            <div class="modal-footer">


            </div>
        </div>
    </div>
</div>
</div>
<div class="modal fade" id="excelModal" tabindex="-1" role="dialog"
     aria-labelledby="budgetModalLabels" aria-hidden="true" data-keyboard="false" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="budgetModalLabels">
                    Import Excel
                </h4>
                <button type="button" id="disbuds" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form method="POST" id="budget-excel" class="form-horizontal" enctype="multipart/form-data">

                    <label class="btn btn-success btn-file" id="prem">
                        Excel <input type="file" id="btn-import-trans" style="display: none;">
                    </label>

                </form>

            </div>
            <div class="modal-footer">


            </div>
        </div>
    </div>
</div>



