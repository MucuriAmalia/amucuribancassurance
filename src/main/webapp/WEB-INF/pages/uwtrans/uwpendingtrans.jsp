<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/uwtrans/pendingtrans.js"/>"></script>
<div class="x_panel">
    <div class="card-box table-responsive">
        <div class="x_title">
            <h2><i class="fa fa-bars"></i> Pending Policy Transactions</h2>
            <div class="clearfix"></div>
        </div>
        <form id="search-form" class="form-horizontal">
            <div class="item form-group">

                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-4 label-align">Policy
                        No.</label>

                    <div class="col-md-8 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="pol-search-number" />
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-4 label-align">Risk
                        ID</label>

                    <div class="col-md-8 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="rev-search-number" />
                    </div>
                </div>
            </div>
            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-4 label-align">DR
                        No.</label>

                    <div class="col-md-8 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="dr-search-number" />
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-4 label-align">Client
                    </label>

                    <div class="col-md-8 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="rev-search-name" />
                        <div id="client-frm" class="form-control"
                             select2-url="<c:url value="/protected/uw/policies/uwClients"/>" >

                        </div>
                    </div>
                </div>

            </div>
            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-4 label-align">Underwriter
                    </label>

                    <div class="col-md-8 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="agent-search-number" />
                        <div id="acc-frm" class="form-control"
                             select2-url="<c:url value="/protected/setups/binders/selAccounts"/>" >
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-4 label-align">Product
                    </label>

                    <div class="col-md-8 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="product-search-number" />
                        <div id="prd-code" class="form-control"
                             select2-url="<c:url value="/protected/setups/binders/selproducts"/>" >
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group">
                <input type="button" class="btn btn-info float-right"
                       style="margin-right: 10px;" value="Search"
                       id="btn-search-policies">
            </div>


        </form>
        <div class="table-responsive">
            <table id="pol_enquiry_tbl" class="table table-striped" style="width: 100%">
                <thead>
                <tr class="headings">
                    <th>Policy No</th>
                    <th>Endors. No</th>
                    <th>Product</th>
                    <th>Cover From</th>
                    <th>Cover To</th>
                    <th>Client</th>
                    <th>Ins Company</th>
                    <th>Transaction Type</th>
                    <th>Currency</th>
                    <th>Prep. By</th>
                    <th width="5%"></th>
                    <th width="5%"></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>