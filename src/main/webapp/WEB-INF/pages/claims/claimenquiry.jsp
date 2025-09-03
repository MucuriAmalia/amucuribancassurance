<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/enquireclaims.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Claim Enquiry</h2>
        <div class="clearfix"></div>
    </div>
    <form id="search-form" class="form-horizontal">

            <div class="col-md-6 col-xs-12">
              <div class="item form-group">
                <label for="brn-id" class="col-md-4 label-align">Policy
                    No.</label>

                <div class="col-md-8 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="pol-search-number" placeholder="Policy No" />
                </div>
              </div>
            </div>
            <div class="col-md-6 col-xs-12" >
            <div class="item form-group">
                    <label for="brn-id" class="col-md-4 label-align">Risk
                        ID.</label>

                    <div class="col-md-8 col-xs-12">
                        <input type='text' class="form-control float-right"
                               id="rev-search-number" placeholder="Risk ID"/>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12" >
               <div class="item form-group">
                <label for="brn-id" class="col-md-4 label-align">Claim No</label>

                <div class="col-md-8 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="rev-claim-number" placeholder="Claim No"/>
                </div>
               </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <div class="item form-group">
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
        <div class="col-md-6 col-xs-12">
        <div class="item form-group">
            <input type="button" class="btn btn-info float-right"
                   style="margin-right: 10px;" value="Search"
                   id="btn-search-claims">
        </div>
        </div>


    </form>
    <div class="table-responsive">
    <table id="clm_enquiry_tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">
            <th>Claim No</th>
            <th>Risk ID</th>
<%--            <th>Policy Number</th>--%>
            <th>Client Name</th>
            <th>Insurers Claim Ref</th>
            <th>Insurer</th>
            <th>Product Name</th>
            <th>Product Type</th>
            <th>Status</th>
            <th>Loss Date</th>

            <th>Notification Date</th>
            <th>Booked By</th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
        </div>
    </div>