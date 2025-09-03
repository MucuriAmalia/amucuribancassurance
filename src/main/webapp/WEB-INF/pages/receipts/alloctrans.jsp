<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/alloctrans.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Allocate Credit Transactions</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <form class="form-horizontal">
            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Client
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="client-id" />
                        <div id="client-frm" class="form-control"
                             select2-url="<c:url value="/protected/reports/clients"/>" >
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Currency
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="cur-id"  />
                        <div id="curr-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Branch
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="brn-id" />
                        <div id="brn-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwbranches"/>" >
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="rate-taxable" class="col-md-5 label-align">Unallocated Transactions?</label>
                    <div class="col-md-7 checkbox">
                        <label>
                            <input type="checkbox" name="active" id="chk-balance">
                        </label>

                    </div>
                </div>
            </div>
            <div class="item form-group">
                <input type="button" class="btn btn-info float-right"
                       style="margin-right: 10px;" value="Search"
                       id="btn-search-trans">
            </div>

        </form>
        <div class="x_panel">
            <div class="x_title">
                <h2>Credit Transactions</h2>
                <ul class="nav navbar-right panel_toolbox">
                    <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                    </li>
                </ul>
                <div class="clearfix"></div>
            </div>
            <div class="x_content">
                <div class="card-box table-responsive">
        <table id="credits-tbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Reference No</th>
                <th>Date</th>
                <th>Narration</th>
                <th>Type</th>
                <th>Amount</th>
                <th>Balance</th>
                <th>Payee</th>
            </tr>
            </thead>
        </table>
                    </div>
                <input type="hidden" id="cr-trans-pk">
    </div>
</div>
        <div class="x_content">
            <div class="" role="tabpanel" data-example-id="togglable-tabs">
                <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                    <li role="presentation" class="active"><a href="#tab_content1"
                                                              id="home-tab" role="tab" data-toggle="tab" aria-expanded="true">Debit Transactions</a>
                    </li>
                    <li role="presentation" class=""><a href="#tab_content2"
                                                        role="tab" id="profile-tab" data-toggle="tab" aria-expanded="false">Settlements</a>
                    </li>
                </ul>
                <div id="myTabContent" class="tab-content">
                    <div role="tabpanel" class="tab-pane active"
                         id="tab_content1" aria-labelledby="home-tab">
                        <div class="cutom-container">
                <table id="debits-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Reference No</th>
                        <th>Date</th>
                        <th>Policy No</th>
                        <th>Type</th>
                        <th>Gross Amount</th>
                        <th>Net Due</th>
                        <th>Settlements</th>
                        <th>Balance</th>
                        <th>Alloc Amount</th>
                        <th>Check</th>
                        <th>Allocate</th>
                    </tr>
                    </thead>
                </table>
                    </div>
                    </div>
                    <div role="tabpanel" class="tab-pane fade" id="tab_content2"
                         aria-labelledby="profile-tab" >
                        <table id="debits-settle-tbl" class="table table-striped" style="width:100%">
                            <thead>
                            <tr>
                                <th>Reference No</th>
                                <th>D/C</th>
                                <th>Amount</th>
                                <th>Setlled Date</th>
                                <th>Policy No</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>