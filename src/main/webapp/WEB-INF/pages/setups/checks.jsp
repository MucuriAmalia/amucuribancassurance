<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/setups/checks.js"/>"></script>

<div class="x_panel">

    <div class="x_content">

        <div class="x_panel">
            <div class="x_title">
                <h2><i class="fa fa-bars"></i> Checks</h2>
                <ul class="nav navbar-right panel_toolbox">
                    <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                    </li>
                </ul>
                <div class="clearfix"></div>
            </div>
            <div class="x_content">
                <div class="" role="tabpanel" data-example-id="togglable-tabs">
                    <ul id="myTab-2" class="nav nav-tabs bar_tabs" role="tablist">
                        <li role="presentation" class="active"><a href="#tab_content1-1"
                                                                  id="home-tab-2" role="tab" data-toggle="tab" aria-expanded="true">All Checks</a>
                        </li>
                        <li role="presentation" class=""><a href="#tab_content2-2"
                                                            role="tab" id="profile-tab-2" data-toggle="tab" aria-expanded="false">Product Checks</a>
                        </li>
                        </ul>
                    <div id="myTabContent-1" class="tab-content">
                        <div role="tabpanel" class="tab-pane active"
                             id="tab_content1-1" aria-labelledby="home-tab-1">

                            <button class="btn btn-success btn btn-info float-right" id="btn-add-common-checks">New</button>

                            <table id="common-checks-tbl" class="table table-striped" style="width: 100%">
                                <thead>
                                <tr class="headings">

                                    <th>Check ID</th>
                                    <th>Check Name</th>
                                    <th>Check Type</th>
                                    <th>Active</th>
                                    <th>Common Check</th>
                                    <th>Check Permission</th>
                                    <th width="5%"></th>
                                    <th width="5%"></th>
                                </tr>
                                </thead>
                            </table>

                            </div>
                        <div role="tabpanel" class="tab-pane fade"
                             id="tab_content2-2" aria-labelledby="home-tab-2">
                            <form id="prg-grp-form" class="form-horizontal">
                                <div class="item form-group form-required">
                                    <div class="col-md-6">
                                        <label for="brn-id" class="col-md-5 label-align">
                                            Product</label>

                                        <div class="col-md-7">
                                            <input type="hidden" id="prod-id"/>
                                            <div id="prod-frm" class="form-control"
                                                 select2-url="<c:url value="/protected/setups/checks/selproducts"/>" >

                                            </div>

                                        </div>
                                    </div>


                                </div>
                            </form>
                <button class="btn btn-success btn btn-info float-right" id="btn-add-checks">New</button>

    <table id="checks-tbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">

            <th>Check ID</th>
            <th>Check Name</th>
            <th>Check Type</th>
            <th>Active</th>
            <th>Check Permission</th>
            <th width="5%"></th>
        </tr>
        </thead>
    </table>
                            </div>
                            </div>
</div>
</div>
            <div class="modal fade" id="prodChecksModal" tabindex="-1" role="dialog"
                 aria-labelledby="prodChecksModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h4 class="modal-title" id="prodChecksModalLabel">Select Checks</h4>
                            <button type="button" class="close" data-dismiss="modal"
                                    aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>

                        </div>
                        <div class="modal-body">
                            <div style="height: 300px !important; overflow: scroll;">
                                <table class="table table-striped table-hover table-bordered table-fixed" id="prodChecksTbl">
                                    <thead>
                                    <tr>
                                        <th width="1%"></th>
                                        <th width="4%">Check Id</th>
                                        <th width="12%">Check</th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    </tbody>
                                </table>
                            </div>
                            <form id="prod-check-form">
                                <input type="hidden" id="check-pro-code" name="proCode"/>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button data-loading-text="Saving..." id="saveProdChecksBtn"
                                    type="button" class="btn btn-success">Save</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">
                                Cancel</button>
                        </div>
                    </div>
                </div>
            </div>

<div class="modal fade" id="checksModal" tabindex="-1" role="dialog"
     aria-labelledby="checksModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="checksModalLabel">
                    Edit/Add Checks
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="branch_model">
                <form id="checks-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="check-id" name="checkId">
                    <input type="hidden" class="form-control" id="check-prod-id" name="product">

                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Check ID</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="check-sht-desc"
                                   name="checkShtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Check Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="check-name"
                                   name="checkName"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Check Type</label>

                        <div class="col-md-8">
                            <select class="form-control" id="check-type" name="checkType" required>
                                <option value="">Select Check Type</option>
                                <option value="ER">Error</option>
                                <option value="EX">Exception</option>
                            </select>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Permission</label>

                        <div class="col-md-8" >

                            <input type="hidden" id="perm-id" name="permissionsDef"/>
                            <input type="hidden" id="perm-name">

                            <div id="perm-def" class="form-control"
                                 select2-url="<c:url value="/protected/setups/checks/exceptions"/>" >
                            </div>
                        </div>
                        </div>

                    <div class="item form-group">

                        <label for="rate-taxable" class="col-md-3 label-align">Common Check?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="checkCommon" id="chk-appli">
                            </label>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="rate-taxable" class="col-md-3 label-align">Active?</label>
                        <div class="col-md-9 checkbox">
                            <label>
                                <input type="checkbox" name="status" id="chk-active">
                            </label>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveChecks"
                        type="button" class="btn btn-success">
                    Save
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>
            <div class="modal fade" id="userRolesModal" tabindex="-1" role="dialog"
                 aria-labelledby="userRolesModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"
                                    aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title" id="usersModalLabel">
                                Edit/Add Roles
                            </h4>
                        </div>
                        <div class="modal-body">
                            <form id="roles-form" class="form-horizontal">
                                <input type="hidden" class="form-control" id="user-role-id" name="roleId">
                                <div class="item form-group">
                                    <label for="unit-id" class="col-md-3 label-align">Role</label>

                                    <div class="col-md-8">
                                        <input type="text" class="form-control" id="user-role-name"
                                               name="roleName"  required>
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button data-loading-text="Saving..." id="saveRoles"
                                    type="button" class="btn btn-success">
                                Save
                            </button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="permissionsModal" tabindex="-1" role="dialog"
                 aria-labelledby="permissionsModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"
                                    aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title" id="permissionsModalLabel">
                                Add Exception Permissions
                            </h4>
                        </div>
                        <div class="modal-body" id="branch_models">
                            <form id="permissions-form" class="form-horizontal">
                                <input type="hidden" class="form-control" id="moduleId" name="module">

                                <div class="item form-group">
                                    <label for="unit-id" class="col-md-3 label-align">Check ID</label>

                                    <div class="col-md-8">
                                        <input type="text" class="form-control" id="permission-desc"
                                               name="permDesc"  required>
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <label for="unit-id" class="col-md-3 label-align">Permission Name</label>

                                    <div class="col-md-8">
                                        <input type="text" class="form-control" id="permission-name"
                                               name="permName"  required>
                                    </div>
                                </div>
                                <div class="item form-group">
                                    <label for="cou-name" class="col-md-3 label-align">Description</label>

                                    <div class="col-md-8">
                                        <select class="form-control" id="access-type" name="accessType" required>
                                            <option value="">Select Access Type</option>
                                            <option value="A">Access</option>
                                            <option value="L">Limit</option>
                                        </select>
                                    </div>
                                </div>

                            </form>
                        </div>
                        <div class="modal-footer">
                            <button data-loading-text="Saving..." id="save-permission"
                                    type="button" class="btn btn-success">
                                Save
                            </button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            </div>