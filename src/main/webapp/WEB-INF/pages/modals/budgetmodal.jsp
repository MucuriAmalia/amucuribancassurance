<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="modal fade" id="budgetModal" tabindex="-1" role="dialog"
     aria-labelledby="budgetModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="budgetModalLabel">
                     Add Budget
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="budget-form" class="form-horizontal">

                    <div class="item form-group">
                        <label for="name" class="label-align col-md-3">Budget
                            Name<span class="required">*</span></label>
                        <div class="col-md-9 col-xs-12">
                            <input type="text" name="name" id="budget_name" class="form-control"
                                   placeholder="Budget Name" required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="clienttype" class="label-align col-md-3">Client Type</label>
                        <div class="col-md-9 col-xs-12">

                            <select class="form-control" id="client_type" name="clientType" required>
                                <option value="">Select Type</option>
                                <option value="individual">Individual</option>
                                <option value="corporate">Corporate</option>
                            </select>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="branches" class="label-align col-md-3">Branch</label>
                        <div class="col-md-9 col-xs-12">
                            <div id="ten-branch" class="form-control"
                                 select2-url="<c:url value="/protected/setups/branches"/>" >

                            </div>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="users" class="label-align col-md-3">Users</label>
                        <div class="col-md-9 col-xs-12">
                            <div id="user_list" class="form-control"
                                 select2-url="<c:url value="/protected/users/usersList/A"/>" >

                            </div>

                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="year" class="label-align col-md-3">Email</label>
                        <div class="col-md-9 col-xs-12">
                            <input type="number" name="year" id="budget_year" class="form-control"
                                   placeholder="Year">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="Jan" class="label-align col-md-3">Jan</label>
                        <div class="col-md-9 col-xs-12">
                            <input type="text" name="jan" id="january" class="form-control"
                                   placeholder="January">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="Feb" class="label-align col-md-3">Jan</label>
                        <div class="col-md-9 col-xs-12">
                            <input type="text" name="feb" id="february" class="form-control"
                                   placeholder="February">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="Mar" class="label-align col-md-3">Mar</label>
                        <div class="col-md-9 col-xs-12">
                            <input type="text" name="mar" id="march" class="form-control"
                                   placeholder="March">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="April" class="label-align col-md-3">April</label>
                        <div class="col-md-9 col-xs-12">
                            <input type="text" name="april" id="April" class="form-control"
                                   placeholder="April">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="May" class="label-align col-md-3">May</label>
                        <div class="col-md-9 col-xs-12">
                            <input type="text" name="may" id="may" class="form-control"
                                   placeholder="May">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="June" class="label-align col-md-3">June</label>
                        <div class="col-md-9 col-xs-12">
                            <input type="text" name="june" id="june" class="form-control"
                                   placeholder="June">
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="July" class="label-align col-md-3">July</label>
                        <div class="col-md-9 col-xs-12">
                            <input type="text" name="july" id="july" class="form-control"
                                   placeholder="July">
                        </div>
                    </div>
                    </div>
            <div class="item form-group">
                <label for="August" class="label-align col-md-3">Aug</label>
                <div class="col-md-9 col-xs-12">
                    <input type="text" name="aug" id="august" class="form-control"
                           placeholder="August">
                </div>
            </div>
            <div class="item form-group">
                <label for="September" class="label-align col-md-3">Sep</label>
                <div class="col-md-9 col-xs-12">
                    <input type="text" name="sep" id="september" class="form-control"
                           placeholder="September">
                </div>
            </div>
            <div class="item form-group">
                <label for="October" class="label-align col-md-3">Oct</label>
                <div class="col-md-9 col-xs-12">
                    <input type="text" name="oct" id="october" class="form-control"
                           placeholder="October">
                </div>
            </div>
            <div class="item form-group">
                <label for="Jan" class="label-align col-md-3">Nov</label>
                <div class="col-md-9 col-xs-12">
                    <input type="text" name="nov" id="november" class="form-control"
                           placeholder="November">
                </div>
            </div>
            <div class="item form-group">
                <label for="Jan" class="label-align col-md-3">Dec</label>
                <div class="col-md-9 col-xs-12">
                    <input type="text" name="dec" id="december" class="form-control"
                           placeholder="December">
                </div>
            </div>
                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveBudget"
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

