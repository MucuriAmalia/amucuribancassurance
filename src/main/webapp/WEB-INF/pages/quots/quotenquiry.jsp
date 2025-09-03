<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/quotes/quotes.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Quotes Enquiry</h2>
        <div class="clearfix"></div>
    </div>
    <form id="search-form" class="form-horizontal">
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Quotation Number
                    No.</label>

                <div class="col-md-8 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="quote-search-number" />
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Client</label>
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
                <label for="brn-id" class="col-md-4 label-align">Prospect
                </label>

                <div class="col-md-8 col-xs-12">
                    <input type='hidden' class="form-control float-right"
                           id="prs-search-name" />
                    <div id="prospects-frm" class="form-control"
                         select2-url="<c:url value="/protected/quotes/selprospects"/>" >

                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xs-12">

            </div>

        </div>
        <div class="item form-group">
            <input type="button" class="btn btn-primary float-right"
                   style="margin-right: 10px;" value="Search"
                   id="btn-search-quotes">
        </div>


    </form>
    <div class="table-responsive">
    <table id="quot_enquiry" class="table table-striped" >
        <thead>
        <tr class="headings">
            <th width="5"></th>
            <th>Quote No</th>
            <th>Revision. No</th>
            <th>Cover From</th>
            <th>Cover To</th>
            <th>Client/Prospect</th>
            <th>Product Name</th>
            <th>Currency</th>
            <th>Status</th>
            <th>Prep. By</th>
            <th></th>
            <th></th>
            <th></th>
        </tr>
        </thead>
    </table>
        </div>
</div>