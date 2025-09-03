<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/setups/escalation.js"/>"></script>
<style>
    .selected-row {
        background-color: #f8d7da !important;
    }

</style>
<div class="x_panel">
    <div role="tabpanel" data-example-id="togglable-tabs">
        <ul id="myTab" class="nav nav-tabs bar_tabs" role="tablist">
            <li role="presentation" class="active">
                <a href="#tab_content1" id="config-tab" role="tab" data-toggle="tab" aria-expanded="true">Task Configuration</a>
            </li>
            <li role="presentation">
                <a href="#tab_content3" role="tab" id="hierarchy-tab" data-toggle="tab" aria-expanded="false">View Tasks</a>
            </li>
            <li role="presentation">
                <a href="#tab_content2" role="tab" id="hierarchy-tab" data-toggle="tab" aria-expanded="false">Hierarchy Roles</a>
            </li>
        </ul>
        <div id="myTabContent" class="tab-content">
            <div role="tabpanel" class="tab-pane fade active in" id="tab_content1" aria-labelledby="config-tab">
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i> Task Configuration</h2>

                    <div class="clearfix"></div>
                </div>
                <button class="btn btn-primary pull-right" id="btnNewConfig" data-toggle="modal" data-target="#configModal">New</button>
                <div class="table-responsive">
                <table id="taskConfigTable" class="table table-striped table-bordered">
                    <thead>
                        <tr>
                            <th>Task Type</th>
                            <th>Time Limit per User (min)</th>
                            <th>Time Limit per Transaction (min)</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Data will be dynamically populated -->
                    </tbody>
                </table>
                </div>
            </div>

            <div role="tabpanel" class="tab-pane fade active in" id="tab_content3" aria-labelledby="config-tab">
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i>View Tasks: Fill the below parameters to get system tasks</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="form-group row">
                    <div class="col-md-4 col-xs-12">
                        <label for="acct-branch" class="label-align">Branch</label>
                        <div id="acct-branch" class="form-control"
                             select2-url="<c:url value='/protected/uw/policies/allbranches'/>">
                            <input type="hidden" id="obId" name="branchId" />
                        </div>
                    </div>

                    <div class="col-md-4 col-xs-12">
                        <label for="startDate" class="label-align">Start Date</label>
                        <input type="datetime-local" id="startDate" class="form-control" />
                    </div>

                    <div class="col-md-4 col-xs-12">
                        <label for="endDate" class="label-align">End Date</label>
                        <input type="datetime-local" id="endDate" class="form-control" />
                    </div>
                </div>

                <div class="form-group row">
                    <div class="col-md-12 text-center">
                        <button type="button" id="filterTasksButton" class="btn btn-primary">Get Tasks</button>
                    </div>
                </div>

                <div class="table-responsive">
                    <table id="escalationRecordsTable" class="table table-striped">
                        <thead>
                            <tr>
                                <th>Task ID</th>
                                <th>Task Initiation Time</th>
                                <th>Check Time</th>
                                <th>Overdue?</th>
                                <th>Escalation Level Role Name</th>
                                <th>Owner</th>
                                <th>Task Name</th>
                                <th>Verifier Username</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Data will be appended here dynamically -->
                        </tbody>
                    </table>
                    <div id="paginationControls"></div>
                </div>
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i>Click on a task to show activity</h2>
                    <div class="clearfix"></div>
                </div>
                <div class="table-responsive">
                <table id="activityRecordsTable" class="table table-striped">
                    <thead>
                        <tr>
                            <th>Activity Time</th>
                            <th>Escalation Level</th>
                            <th>User</th>
                            <th>Comments</th>
                            <th>System Action</th>
                        </tr>
                    </thead>
                    <tbody></tbody>
                </table>
                </div>

            </div>

            <div role="tabpanel" class="tab-pane fade" id="tab_content2" aria-labelledby="hierarchy-tab">
                <div class="x_title">
                    <h2><i class="fa fa-bars"></i> Existing Hierarchy Roles</h2>
                    <div class="clearfix"></div>
                </div>
                <!-- Hierarchy Roles Section -->
                <div class="table-responsive">
                <table id="hierarchyRolesTable" class="table table-striped table-bordered">
                    <thead>
                        <tr>
                            <th>Level</th>
                            <th>Role Name</th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal for Adding/Editing Configuration -->
<div class="modal fade" id="configModal" tabindex="-1" role="dialog" aria-labelledby="configModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form id="configForm">
                <div class="modal-header">
                    <h4 class="modal-title" id="configModalLabel">Task Configuration</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="taskType">Task Type</label>
                        <select id="taskType" class="form-control" required>
                            <option value="">Select Task Type</option>
                            <option value="CL">Client</option>
                            <option value="ALP">Life Policy Administration</option>
                            <option value="ANP">General Policy Administration</option>
                            <option value="RC">Receipt</option>
                            <option value="EN">Endorsement</option>
                            <option value="IM">Insurers/Agents</option>
                            <option value="BN">Binder</option>
                            <option value="COA">Chart of Accounts</option>
                            <option value="RV">Reversals</option>
                            <option value="RN">Renewals</option>
                            <option value="CA">Claims</option>
                            <option value="REC">Reconciliation</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="timeLimitPerUser">Time Limit per User (minutes)</label>
                        <input type="number" class="form-control" id="timeLimitPerUser" name="timeLimitPerUser" required>
                    </div>
                    <div class="form-group">
                        <label for="timeLimitPerTransaction">Time Limit per Transaction (minutes)</label>
                        <input type="number" class="form-control" id="timeLimitPerTransaction" name="timeLimitPerTransaction" required>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Save</button>
                </div>
            </form>
        </div>
    </div>
</div>

