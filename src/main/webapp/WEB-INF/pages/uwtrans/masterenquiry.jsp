<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/enquireclaims.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/policies.js"/>"></script>
<c:if test="${not empty clntId}">
<script type="text/javascript">
    var clntId = ${clntId};
</script>
    </c:if>
<c:if test="${not empty polIds}">

<script type="text/javascript">
    var polId = ${polIds};
</script>

</c:if>
<c:if test="${not empty group}">
    <script type="text/javascript">
        var group= ${group};
    </script>

</c:if>
<c:if test="${not empty claimId}">
    <script>
        var claimId=${claimId};
    </script>
</c:if>
<div class="x_panel">
    <div class="x_title">
        <h4>Master Enquiry</h4>
    </div>

    <form id="search-form" class="form-horizontal">
        <div class="item form-group">

            <div class="col-md-4 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Client</label>

                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="client-id" name="clientId"/>
                    <input type="hidden" id="client-fname">
                    <input type="hidden" id="client-other-name">

                    <div id="client-frm" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/allClients"/>">
                    </div>
                </div>
            </div>
            <div class="col-md-4 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Policy No.</label>

                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="pol-id" name="policyId"/>
                    <input type="hidden" id="pol-no">

                    <div id="pol-cont" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/allPolicies"/>">
                    </div>
                </div>
            </div>


            <div class="col-md-4 col-xs-12">
                <label for="brn-id" class="col-md-5 label-align">Risk</label>

                <div class="col-md-7 col-xs-12">
                    <input type="hidden" id="risk-id" name="riskId"/>
                    <input type="hidden" id="risk-desc">

                    <div id="risk-cont" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/allRisksLov"/>">
                    </div>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-offset-6">
            <button type="button" class="btn btn-info btn-master"
                   style="margin-right: 10px;"
                    id="btn-search-master">Search</button>
            </div>
        </div>


    </form>
</div>

<div class="x_panel hide-col">
    <div class="table-responsive">
        <table id="clTbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Client ID</th>
                <th>Client Name</th>
                <th>Other Names</th>
                <th>Id/Passport No</th>
                <th>Client Type</th>
                <th>Pin No.</th>
                <th>Branch</th>
                <th>Phone</th>
                <th>Email</th>
                <th></th>

            </tr>
            </thead>
        </table>
    </div>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="" role="tabpanel" data-example-id="togglable-tabs">
                <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
                        <li role="presentation" class="active"><a href="#tab_content3"
                                                                  role="tab" id="policy-tab" data-toggle="tab" aria-expanded="false">Policies</a>
                        </li>
                        <li role="presentation" class=""><a href="#tab_content1"
                                                            id="receipt-tab" role="tab" data-toggle="tab" aria-expanded="true">Receipts</a>
                        </li>
                </ul>
                <div id="myTabContent" class="tab-content">
                    <div role="tabpanel" class="tab-pane active"
                     id="tab_content3" aria-labelledby="policy-tab">
    <div class="x_panel">
        <table id="polTbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Policy No</th>
                <th>Branch</th>
                <th>Product</th>
                <th>WEF</th>
                <th>WET</th>
                <th>Status</th>
                <th>Total SI</th>
                <th>Total Premium</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
        </div>
    </div>

                <div role="tabpanel" class="tab-pane fade"
                     id="tab_content1" aria-labelledby="receipt-tab">
                                    <div class="x_panel">
                                            <table id="rcptTbl" class="table table-striped" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th>Receipt ID</th>
                                                    <th>Description</th>
                                                    <th>Ammount</th>
                                                    <%--<th>Client</th>--%>
                                                    <th>Branch</th>
                                                    <th>Date Issued</th>
                                                    <th>Transaction Date</th>
                                                    <th width="5%"></th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                    <div class="x_panel">
                        <div id="hideDet">
                            <table id="rcptdtlsTbl" class="table table-striped" style="width:100%">
                                <thead>
                                <tr>
                                    <th>Receipt ID</th>
                                    <th>Reference No.</th>
                                    <th>D/C</th>
                                    <th>Ammount</th>
                                    <th>Settled Date</th>
                                    <th>Policy No.</th>
                                    <th>Narration</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                                    </div>
                </div></div>
        </div>
    </div>
    <div class="custom-container">
        <div id="hideRisk">
        <table id="polRisDabl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Plate Number</th>
                <th>Risk Desc</th>
                <th>WEF</th>
                <th>WET</th>
                <th>Risk SI</th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
        </div>
    </div>

    <div class="table-responsive">
        <div id="hideClaim">
        <table id="claimTbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Claim ID</th>
                <th>Loss Desc</th>
                <th>Loss Date</th>
                <th>Plate Number</th>
                <th>Claim Date</th>
                <th>Reserve</th>
                <th>Peril</th>
                <th>Status</th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
        </div>
</div>

</div>

