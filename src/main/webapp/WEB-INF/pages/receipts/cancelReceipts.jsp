<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/cancelReceipts.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Cancel Receipts</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <form id="search-form" class="form-horizontal">
        <div class="item form-group">

            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Date
                    From</label>

                <div class="col-md-7 col-xs-12">
                    <div class='input-group date datepicker-input' id="wef-date">
                        <input type='text' class="form-control float-right"
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
                        <input type='text' class="form-control float-right"
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
                <label for="brn-id" class="col-md-5 label-align">Client
                </label>
                <div class="col-md-7 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="rev-search-name" />
                    <div id="client-frm" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Receipt
                    No.</label>

                <div class="col-md-7 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="dr-search-number" placeholder="Receipt No"/>
                </div>
            </div>
            </div>
            <div class="item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Policy
                        No.</label>

                    <div class="col-md-7 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="pol-search-number" placeholder="Policy No" />
                    </div>
                </div>
                <div class="col-md-6 col-xs-12" >
                    <label for="brn-id" class="col-md-5 label-align">Ref
                        No.</label>

                    <div class="col-md-7 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="rev-search-number" placeholder="Ref No"/>
                    </div>
                </div>
            </div>


        <div class="item form-group">
            <input type="button" class="btn btn-primary float-right"
                   style="margin-right: 10px;" value="Search"
                   id="btn-search-receipts">
        </div>


    </form>
    <hr>
    <div class="card-box table-responsive">


        <table id="rcts-tbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>

                <th>Receipt No.</th>
                <th>Receipt Date</th>
                <th>Narrative</th>
                <th>Amount</th>
                <th>Payment Mode</th>
                <th>Ref</th>
                <th>Paid By</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>

    </div>
    <form   class="form-horizontal" id="print-form">


    </form>
    <button class="btn btn-primary float-right" id="btn-receipt-cancel">Cancel Receipt</button>
</div>

