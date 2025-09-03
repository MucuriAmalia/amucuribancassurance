<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript"
        src="<c:url value="/js/modules/home.js"/>"></script>
<script type="text/javascript">
  const CONTEXT = '${pageContext.request.contextPath}';
</script>

<div class="x_panel">
  <div class="x_title">
    <h2><i class="fa fa-bars"></i> DashBoard</h2>
    <div class="clearfix"></div>
  </div>
  <div class="x_content">
  <div class="row">
    <section class="dibva">
      <div class="breif">
        <div class="combination">
          <div class="heading">
            <h4>Sum Assured (YTD)</h4>
          </div>
          <div class="amount">
            <h1><div class="count blue pending-quote">0.00</div></h1>
          </div>
        </div>
      </div>
      <div class="breif">
        <div class="combination">
          <div class="heading">
            <h4>Policies Sold (YTD)</h4>
          </div>
          <div class="amount">
            <h1><div class="count blue expired-pols">0</div></h1>
          </div>
        </div>
      </div>
      <div class="breif">
        <div class="combination">
          <div class="heading">
            <h4>Total Premium (YTD)</h4>
          </div>
          <div class="amount">
            <h1><div class="count blue pend-endorse">0.00</div></h1>
          </div>
        </div>
      </div>
    </section>
  </div>


<div class="row">
  <div class="x_panel">
    <div class="x_title">
      <h2>Tasks/Dashboard</h2>
      <div class="clearfix"></div>
    </div>
    <div class="x_content">
  <div class="col-md-12 col-sm-12 col-xs-12">
    <div class="" role="tabpanel" data-example-id="togglable-tabs">
      <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
        <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
          <li role="presentation" class="active">
            <a href="#tab_content4" role="tab" id="checker-tab" data-toggle="tab" aria-expanded="true">Checker Tasks</a>
          </li>
        </sec:authorize>


        <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
          <li role="presentation">
            <a href="#tab_content6" role="tab" id="maker-tab-checker" data-toggle="tab" aria-expanded="false">Maker Tasks</a>
          </li>
        </sec:authorize>


        <sec:authorize access="!hasAnyAuthority('AUTHORIZE_POLICY')">
          <li role="presentation" class="active">
            <a href="#tab_content6" role="tab" id="maker-tab-maker" data-toggle="tab" aria-expanded="true">Maker Tasks</a>
          </li>
        </sec:authorize>

        <!-- Pending Transactions -->
        <li role="presentation">
          <a href="#tab_content3" role="tab" id="pending-tab" data-toggle="tab" aria-expanded="false">Pending Transactions</a>
        </li>


        <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
          <li role="presentation">
            <a href="#tab_content8" role="tab" id="claims-tab" data-toggle="tab" aria-expanded="false">Claim Transactions</a>
          </li>
        </sec:authorize>


        <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
          <li role="presentation">
            <a href="#tab_content7" role="tab" id="receipts-tab" data-toggle="tab" aria-expanded="false">Receipts</a>
          </li>
        </sec:authorize>

        <li role="presentation">
          <a href="#tab_content5" role="tab" id="portfolio-tab" data-toggle="tab" aria-expanded="false">My Portfolio</a>
        </li>


        <li role="presentation">
          <a href="#tab_content1" id="home-tab" role="tab" data-toggle="tab" aria-expanded="false">Dashboard</a>
        </li>
      </ul>
      <div id="myTabContent" class="tab-content">
      <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
        <div role="tabpanel" class="tab-pane active"
             id="tab_content4" aria-labelledby="task-tab2">
          <div class="table-responsive">

            <table id="tasks" class="table table-striped" style="width: 100%">
              <thead>
              <tr class="headings">
                <th>Task ID</th>
                <th>Policy Number</th>
                <th>Task Name</th>
                <th>Client Name</th>
                <th>Duration</th>
                <th>Initiated By</th>
                <th>Resubmission comments</th>
                <th width="5%"></th>
              </tr>
              </thead>
            </table>
          </div>
        </div>
  </sec:authorize>


        <!-- Maker Tasks Content for Checkers (not active by default) -->
        <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
          <div role="tabpanel" class="tab-pane fade" id="tab_content6" aria-labelledby="maker-tab-checker">
            <div class="table-responsive">
              <table id="maker-tasks" class="table table-striped" style="width: 100%">
                <thead>
                <tr class="headings">
                  <th>Task ID</th>
                  <th>Task Name</th>
                  <th>Task Date</th>
                  <th>Status</th>
                  <th>Checked By</th>
                  <th>Checked Date</th>
                  <th>Reason Desc</th>
                  <th>Comments</th>
                  <th width="5%"></th>
                </tr>
                </thead>
              </table>
            </div>
          </div>
        </sec:authorize>

        <!-- Maker Tasks Content for Non-Checkers (active by default) -->
        <sec:authorize access="!hasAnyAuthority('AUTHORIZE_POLICY')">
          <div role="tabpanel" class="tab-pane active" id="tab_content6" aria-labelledby="maker-tab-maker">
            <div class="table-responsive">
              <table id="maker-tasks" class="table table-striped" style="width: 100%">
                <thead>
                <tr class="headings">
                  <th>Task ID</th>
                  <th>Task Name</th>
                  <th>Task Date</th>
                  <th>Status</th>
                  <th>Checked By</th>
                  <th>Checked Date</th>
                  <th>Reason Desc</th>
                  <th>Comments</th>
                  <th width="5%"></th>
                </tr>
                </thead>
              </table>
            </div>
          </div>
        </sec:authorize>




        <sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">

        <div role="tabpanel" class="tab-pane fade" access="hasAnyAuthority('VIEW_USER_PORTFOLIO')"
             id="tab_content7" aria-labelledby="task-tab">
          <div class="x_panel table-responsive">

            <table id="receipt_tbl" class="table table-striped" style="width: 100%">
              <thead>
              <tr class="headings">
                <th>Task ID</th>
                <th>Task Name</th>
                <th>Policy Number</th>
                <th>Task Date</th>
                <th>Initiated By</th>
                <th width="5%"></th>
              </tr>
              </thead>
            </table>
          </div>
        </div>
</sec:authorize>
        <div role="tabpanel" class="tab-pane fade" access="hasAnyAuthority('VIEW_USER_PORTFOLIO')"
             id="tab_content3" aria-labelledby="task-tab">
          <div class="x_panel table-responsive">

            <table id="pol_tbl" class="table table-striped" style="width: 100%">
              <thead>
              <tr class="headings">
                <th>Task ID</th>
                <th>Task Name</th>
                <th>Ref No</th>
                <th>Client</th>
                <th>Prep. By</th>
                <th>Duration</th>
                <th width="5%"></th>
              </tr>
              </thead>
            </table>
          </div>
        </div>
        <div role="tabpanel" class="tab-pane fade" id="tab_content5" aria-labelledby="task-tab">
            <div class="x_panel">
                <div class="filter-container">
                    <label for="statusFilter">Filter by Status:</label>
                    <select id="statusFilter" class="form-control" style="width:200px; display:inline-block; margin-bottom:15px;">
                        <option value="">All</option>
                        <option value="A">Active</option>
                        <option value="D">Draft</option>
                        <option value="CN">Cancelled</option>
                        <option value="CO">Converted</option>
                        <option value="R">Ready</option>
                    </select>
                </div>
                <div class="table-responsive">
                    <table id="pol_enquiry_tbl" class="table table-striped" style="width:100%">
                        <thead>
                            <tr>
                                <th width="5%"></th>
                                <th>Policy No</th>
                                <th>Endors. No</th>
                                <th>Product</th>
                                <th>Client</th>
                                <th>Intermediary</th>
                                <th>Prep. By</th>
                                <th>Status</th>
                                <th>Authorizer Comments</th>
                            </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>
<sec:authorize access="hasAnyAuthority('AUTHORIZE_POLICY')">
        <div role="tabpanel" class="tab-pane fade" access="hasAnyAuthority('VIEW_USER_PORTFOLIO')"
             id="tab_content8" aria-labelledby="task-tab">
          <div class="x_panel table-responsive">

            <table id="claims_tbl" class="table table-striped" style="width: 100%">
              <thead>
              <tr class="headings">
                <th>Task ID</th>
                <th>Task Name</th>
                <th>Activity Notes</th>
                <th>Next Review Date</th>
                <th>Task Date</th>
                <th>Initiated By</th>
                <th width="5%"></th>
              </tr>
              </thead>
            </table>
          </div>
        </div>

</sec:authorize>

        <div role="tabpanel" class="tab-pane fade"
             id="tab_content1" aria-labelledby="home-tab">
          <div class="dashboard_graph">

            <div class="row x_title">
              <div class="col-md-6">
                <h3>Premium Production</h3>
              </div>
              <div class="col-md-6">

              </div>
            </div>

            <div class="col-md-12 col-sm-12 col-xs-12">
              <canvas id="lineChart" class="demo-placeholder"></canvas>
            </div>

            <div class="row">


              <div class="col-md-4 col-sm-4 col-xs-12">
                <div class="x_panel tile fixed_height_350">
                  <div class="x_title">
                    <h2>Top 5 Products</h2>
                    <div class="clearfix"></div>
                  </div>
                  <div class="x_content">
                    <canvas id="productsDoughnut" height="2" width="2"></canvas>
                  </div>
                </div>
              </div>

              <div class="col-md-4 col-sm-4 col-xs-12">
                <div class="x_panel tile fixed_height_350">
                  <div class="x_title">
                    <h2>Top 5 Branches</h2>
                    <div class="clearfix"></div>
                  </div>
                  <div class="x_content">
                    <canvas id="branchesDoughnut" height="2" width="2"></canvas>
                  </div>
                </div>
              </div>

            </div>


            <div class="clearfix"></div>
          </div>
        </div>
<%--        <div role="tabpanel" class="tab-pane fade"--%>
<%--             id="tab_content2" aria-labelledby="calendar-tab">--%>
<%--          <div id='calendar'></div>--%>

<%--        </div>--%>
      </div>
    </div>
  </div>
  </div>
  </div>
</div>
<br />
  </div>
</div>


<!-- Rejection Modal -->
<div id="rejectionModal" class="modal fade" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Reject Task</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <textarea id="rejectionReason" class="form-control" placeholder="Enter the reason for rejection"></textarea>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
        <button type="button" id="rejectConfirmButton" class="btn btn-danger">Reject</button>
      </div>
    </div>
  </div>
</div>


<div id="approvalModal" class="modal fade" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Approve Task</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>Are you sure you want to approve this task?</p>
        <input type="hidden" id="approvalTaskId">
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
        <button type="button" id="approveConfirmButton" class="btn btn-primary">Approve</button>
      </div>
    </div>
  </div>
</div>





