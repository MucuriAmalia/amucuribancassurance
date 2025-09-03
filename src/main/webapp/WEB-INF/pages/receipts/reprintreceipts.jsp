<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/receipts/reprintReceipts.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Reprint Receipts</h2>
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
            </tr>
            </thead>
        </table>

    </div>
    <form   class="form-horizontal" id="print-form">


    </form>
    <button class="btn btn-primary float-right" id="btn-receipt-print">Reprint</button>
</div>

<div class="modal fade" id="printReceiptModal" tabindex="-1" role="dialog"
     aria-labelledby="printReceiptModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="printReceiptModalLabel">
                    Print Receipt
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">
                <input type="hidden" id="print-receipt-id">
                <div class="media">
                    <iframe class="col-lg-12 col-md-12 col-sm-12" id="receipt-div" height="600">

                    </iframe>
                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" data-dismiss="modal">
                    OK
                </button>
            </div>
        </div>
    </div>
</div>