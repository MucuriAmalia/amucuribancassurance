<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/updatepolno/updatepolnoview.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Underwriter policy number update report</h2>
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
                                   id="from-date" required/>
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
                                   id="wet-date" required/>
                            <div class="input-group-addon">
                                <span class="fa fa-calendar"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="item form-group">
                <div class="col-md-6 col-xs-12">
                    <label for="brn-id" class="col-md-5 label-align">Insurance Company
                    </label>

                    <div class="col-md-7 col-xs-12">
                        <input type='hidden' class="form-control float-right"
                               id="agent-search-number" name="accountCode"/>
                        <div id="acc-frm" class="form-control"
                             select2-url="<c:url value="/protected/setups/binders/selAgentsAccounts"/>">
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-xs-12">
                    <label for="status" class="col-md-5 label-align">Status</label>
                    <div class="col-md-7 col-xs-12">
                        <select id="status" name="status" class="form-control" required>
                            <option value="" disabled selected>Select status</option>
                            <option value="true">Updated</option>
                            <option value="false">Not Updated</option>
                        </select>
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
        <div class="card-box table-responsive">
            <table id="update-tbl" class="table table-striped" style="width:100%">
                <thead>
                <tr>
                    <th>Client Name</th>
                    <th>Risk Note No</th>
                    <th>System Policy No</th>
                    <th>Underwriter Policy No</th>
                    <th>Product Class</th>
                    <th>Policy Authorization Date</th>
                    <th>Policy No Update Date</th>
                </tr>
                </thead>
            </table>
        </div>
        <button class="btn btn-primary float-right" id="print-report">Print Report</button>
    </div>
</div>