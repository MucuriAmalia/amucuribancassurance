<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/certs/printcerts.js"/>"></script>
<script>
    var requestContextPath = '${pageContext.request.contextPath}';
</script>

<div class="x_panel">
    <div class="x_title">
        <h4>Search Certificates</h4>
    </div>
    <form class="form-horizontal">
        <div class="item form-group">
            <div class="col-md-6">
                <label for="brn-id" class="col-md-3 label-align">Underwriter</label>

                <div class="col-md-8">
                    <input type="hidden" id="userCod"/>
                    <div id="underwriter-def" class="form-control"
                         select2-url="<c:url value="/protected/setups/binders/selAccounts"/>">

                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <label for="brn-id" class="col-md-3 label-align">Branch</label>
                <div class="col-md-8">
                    <input type="hidden" id="brn-id"/>
                    <div id="brn-frm" class="form-control"
                         select2-url="<c:url value="/protected/uw/policies/uwbranches"/>">

                    </div>

                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6">
                <label for="brn-id" class="col-md-3 label-align">Sub Class</label>
                <div class="col-md-8">
                    <input type="hidden" id="cert-type-pk"/>
                    <div id="cert-type" class="form-control"
                         select2-url="<c:url value="/protected/certs/selectSubclassCertTypes"/>">
                        <%--select2-url="<c:url value="/protected/certs/selCertTypes"/>">--%>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <label for="brn-id" class="label-align col-md-3">
                    Status </label>
                <div class="col-md-6">
                    <select class="form-control" id="cert-status">
                        <option value="">Select cerificate Status</option>
                        <option value="N">Not Printed</option>
                        <option value="R">Ready for Printing</option>
                        <option value="P">Printed</option>
                    </select>
                </div>
            </div>
        </div>
        <div class="item form-group">

            <div class="col-md-6">
                <label for="brn-id" class="col-md-3 label-align">Policy
                    No.</label>

                <div class="col-md-6">
                    <input type='text' class="form-control float-right"
                           id="pol-search-number" placeholder="Policy No" style="text-transform:uppercase;" />
                </div>
            </div>
            <div class="item form-group riskIdgroup">
                <div class="col-md-6" >
                    <label for="brn-id" class="col-md-3 label-align">Risk
                        ID.</label>

                    <div class="col-md-6">
                        <input type='text' class="form-control float-right"
                               id="rev-search-number" placeholder="Risk ID" style="text-transform:uppercase;"/>
                    </div>
                </div>
            </div>
        </div>

    </form>
    <div class="item form-group">
        <div class="col-md-12">
            <label for="brn-id" class="col-md-2 label-align"></label>
            <div class="col-md-10">
                <button class="btn btn-success btn btn-info float-right" id="btn-search-certs"><span class="glyphicon glyphicon-search">Search</span></button>
            </div>
        </div>
    </div>
</div>
<div class="x_panel">
    <div class="x_title">
        <h4>Certificates Available</h4>
    </div>
    <form class="form-horizontal" id="cert-form">
        <div class="item form-group">
            <div class="col-md-6">
                <label for="brn-id" class="col-md-3 label-align">Select Certificate</label>

                <div class="col-md-8">
                    <div id="cert-type-frm" class="form-control"
                         select2-url="<c:url value="/protected/certs/selbranchcerttypes"/>">

                    </div>


                </div>
            </div>
            <div class="col-md-6">

            </div>
        </div>

        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="no-from" class="label-align col-md-5">
                    No From</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="no-from"> </p>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="no-to" class="label-align col-md-5">
                    No To</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="no-to"> </p>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="cert-no" class="label-align col-md-5">
                    Certificate No</label>
                <div class="col-md-7 col-xs-12">
                    <input type="number"  id="cert-no" class="form-control" readonly
                           placeholder="Certificate No">
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="avail-certs" class="label-align col-md-5">
                    Available Certs</label>
                <div class="col-md-7 col-xs-12">
                    <p class="form-control-static" id="avail-certs"> </p>
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <input type="button" value="Allocate" class="btn btn-success" id="btn-allocate">
                <input type="button" value="Deallocate" class="btn btn-success" id="btn-deallocate">
            </div>
            <div class="col-md-6 col-xs-12">

            </div>
        </div>
        </form>
    <div class="x_panel">
        <div class="x_title">
            <h4>Certificates Pool</h4>
        </div>
        <form id="print-cert-form">
            <input type="hidden" id="cert-type-id" name="branchCert"/>
        </form>
        <div class="table-responsive">
        <table id="cert_tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">

            <th>Policy No</th>
            <th>Client</th>
            <th>Risk</th>
            <th>WEF</th>
            <th>WET</th>
            <th>Start Time</th>
            <th>Status</th>
            <th>Alloc By</th>
            <th>Cert No</th>
            <th>Good For Print</th>
        </tr>
        </thead>
    </table>
            </div>
        <form   class="form-horizontal" id="print-form">


        </form>
        <input type="button" value="Print" class="btn btn-success" id="btn-print">

        </div>
</div>