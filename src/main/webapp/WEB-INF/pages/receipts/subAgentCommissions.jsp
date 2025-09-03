<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/subAgentCommissions.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Sub Agent Commission Payment</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <form id="credits-form" class="form-horizontal">
            <div class="item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Date
                        From</label>

                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="wef-date">
                            <input type='text' class="form-control float-right" name="wefDate"
                                   id="from-date" required />
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Date To
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <div class='input-group date datepicker-input' id="cover-to-date">
                            <input type='text' class="form-control float-right" name="wetDate"
                                   id="wet-date" required />
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Sub Agent
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="agent-search-number" name="accountCode" />
                        <div id="acc-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/inhouseagents"/>">
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Currency
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="cur-id" name="curCode" />
                        <div id="curr-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwcurrencies"/>" >
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group">
                <input type="button" class="btn btn-primary float-right"
                       style="margin-right: 10px;" value="Search"
                       id="btn-search-trans">
            </div>

        </form>
        <div class="clearfix"></div>
        <div class="card-box table-responsive" style="height: 300px !important; overflow: scroll;">
            <table id="credits-tbl" class="table" style="width:100%">
                <thead>
                <tr>
                    <th><input type="checkbox" id="selectAll"></th>
                    <th>Policy</th>
                    <th>Sub Agent</th>
                    <th>DR. No.</th>
                    <th>CR. No.</th>
                    <th>Product</th>
                    <th>Insurer</th>
                    <th>Payment</th>
                    <th>Premium</th>
                    <th>Commission</th>
                    <th>Comm Earned</th>
                    <th>Payable Amt</th>
                </tr>
                </thead>
            </table>
        </div>
        <span class="float-right"> <input type="text" class="form-control total" disabled></span>
        <input type="hidden" id="total-val" value="0">
        <button class="btn btn-primary float-right" id="process_trans">Process Transactions</button>
    </div>
</div>
<div class="x_panel">
    <div class="x_title">
        <h2>Transactions</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="card-box table-responsive">
            <table id="instrans-tbl" class="table" style="width:100%">
                <thead>
                <tr>

                    <th>Trans Date</th>
                    <th>ID</th>
                    <th>Sub Agent</th>
                    <th>Account</th>
                    <th>Amount</th>
                    <th>Remittance No</th>
                    <th></th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<div class="x_panel">
    <div class="x_title">
        <h2>Transaction Log</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <div class="card-box table-responsive">
            <table id="payment-trans-tbl" class="table" style="width:100%">
                <thead>
                <tr>
                    <th>Reference No</th>
                    <th>Policy No</th>
                    <th>Client Name</th>
                    <th>Client Account</th>
                    <th>Product</th>
                    <th>Amount</th>
                    <th>Commission</th>
                    <th>WHTX</th>
                    <th></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>