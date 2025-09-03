<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<c:url value="/js/modules/users/userroles.js"/>"></script>

<div class="x_panel">
    <button class="btn btn-success btn btn-info float-right" id="btn-add-roles">New</button>
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> User Roles</h2>
        <div class="clearfix"></div>
    </div>
    <div class="table-responsive">
        <table id="users-roles-tbl" class="table table-striped" style="width:100%">
            <thead>
            <tr>
                <th>Role ID</th>
                <th>Role Name</th>
                <th width="5%"></th>
                <th width="5%"></th>
            </tr>
            </thead>
        </table>
    </div>
</div>
<div class="x_panel permissions">
    <div class="x_title">
        <h4>Users Permissions
        </h4>

    </div>
    <div class="item form-group clearfix">

        <button class="btn btn-success btn btn-info float-left btn-role" id="btn-add-modules">New Module</button>
        <button class="btn btn-success btn btn-info float-right btn-perm" id="btn-add-permissions" disabled>New Permissions</button>

    </div>
    <div class="row">
        <div class="col-md-2">
            <table id="users-modules-tbl" class="table" style="width:100%">
                <thead>
                <tr>
                    <th>Module</th>
                </tr>
                </thead>
            </table>
        </div>
        <div class="col-md-10">
            <div class="table-responsive">
                <table id="user-permission-tbl" class="table table-striped" style="width:100%">
                    <thead>
                    <tr>
                        <th>Permission Name</th>
                        <th>Permission Desc</th>
                        <th>Access Type</th>
                        <th>Min Limit</th>
                        <th>Max Limit</th>
                        <th width="5%"></th>
                        <th width="5%"></th>
                    </tr>
                    </thead>
                </table>
            </div>
            <form id="permission-form">
                <input type="hidden" name="moduleId" id="module-id-pk">
                <input type="hidden" name="roleId" id="role-id-pk">
                <input type="hidden" name="permissionId" id="role-perm-id">
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="permissionsModal" tabindex="-1" role="dialog"
     aria-labelledby="permissionsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="permissionsModalLabel">
                    Add Permissions
                </h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body" id="branch_models">
                <form id="permissions-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Permission Desc</label>

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
                        <label for="cou-name" class="col-md-3 label-align">Access Type</label>

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



<div class="modal fade" id="permLimitsModal" tabindex="-1" role="dialog"
     aria-labelledby="permLimitsModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="permLimitsModalLabel">
                    Edit Permission Limits
                </h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="perm-limits-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="limits-role-id" name="roleId">
                    <input type="hidden" class="form-control" id="limits-perm-id" name="permId">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Min Amount</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="perm-min-amount"
                                   name="minAmount"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Max Amount</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="perm-max-amount"
                                   name="maxAmount"  required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="savePermLimits"
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
                <h4 class="modal-title" id="usersModalLabel">
                    Edit/Add Roles
                </h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
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
<div class="modal fade" id="modulesModal" tabindex="-1" role="dialog"
     aria-labelledby="modulesModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modulesModalLabel">
                    Add Modules
                </h4>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="modules-form" class="form-horizontal">
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Name</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="module-name"
                                   name="moduleName"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Description</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="sht-desc"
                                   name="shtDesc"  required>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="add-mods"
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