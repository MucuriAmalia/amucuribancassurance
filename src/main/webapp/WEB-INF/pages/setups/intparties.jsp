<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript"
        src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript"
        src="<c:url value="/js/modules/setups/intparties.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h2><i class="fa fa-bars"></i> Interested Parties/Premium Financer/Beneficiary List</h2>
        <ul class="nav navbar-right panel_toolbox">
            <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            </li>
        </ul>
        <div class="clearfix"></div>
    </div>
    <div class="x_content">
        <button type="button" class="btn btn-info float-right"
                id="btn-add-int-parties">New</button>
        <div class="table-responsive">
            <table id="intPartiesTbl" class="table table-striped" style="width: 100%">
                <thead>
                <tr class="headings">

                    <th>Name</th>
                    <th>Type</th>
                    <th>Pin</th>
                    <th>Address</th>
                    <th>Tel No</th>
                    <th>Email Address</th>
                    <th>DOB/Date Registered</th>
                    <th>Created By</th>
                    <th>Created Date</th>
                    <th width="5%"></th>
                    <th width="5%"></th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<div class="modal fade" id="intPartiesModal" tabindex="-1" role="dialog"
     aria-labelledby="intPartiesModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="intPartiesModalLabel">Edit/Add Interested Parties</h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body">

                <form id="parties-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="part-code" name="partCode">
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="part-name" class="label-align col-md-5">
                                Name<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="partName" id="part-name" class="form-control"
                                       placeholder="Name" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="part-type" class="label-align col-md-5">
                               Type<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <select class="form-control" id="part-type" name="partType" required>
                                    <option value="">Select Type</option>
                                    <option value="I">Interested Party</option>
                                    <option value="P">Premium Financier</option>
                                    <option value="B">Beneficiary</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="part-post-address" class="label-align col-md-5">
                                Postal Address</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="postalAddress" id="part-post-address" class="form-control"
                                       placeholder="Postal Address" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="part-email" class="label-align col-md-5">
                                Email Address</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="email" name="emailAddress" id="part-email" class="form-control"
                                       placeholder="Email Address">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="part-pin" class="label-align col-md-5">
                                Pin Number<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="pinNumber" id="part-pin" class="form-control"
                                       placeholder="Pin No" required>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="part-tel" class="label-align col-md-5">
                               Tel No</label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="telNo" id="part-tel" class="form-control"
                                       placeholder="Tel No">
                            </div>
                        </div>
                    </div>
                    <div class="item form-group">
                        <div class="col-md-6 col-xs-12">
                            <label for="date-reg" class="label-align col-md-5 dob">
                               Date of Birth<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <div class='input-group date datepicker-input'>
                                    <input type='text' class="form-control float-right" name="dateRegistered"
                                           id="date-reg" required />
                                    <div class="input-group-addon">
                                        <span class="fa fa-calendar"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-xs-12">
                            <label for="part-id-no" class="label-align col-md-5 idno">
                                ID No<span class="required">*</span></label>
                            <div class="col-md-7 col-xs-12">
                                <input type="text" name="regNo" id="part-id-no" class="form-control"
                                       placeholder="ID No" required>
                            </div>
                        </div>

                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveIntPartiesBtn"
                        type="button" class="btn btn-success">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel</button>
            </div>
        </div>
    </div>
</div>