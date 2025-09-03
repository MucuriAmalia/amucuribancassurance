<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/claims/enquireclaims.js"/>"></script>
<div class="x_panel">
    <div class="x_title">
        <h4>Medical Cards Management</h4>
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
                <label for="brn-id" class="col-md-4 label-align">Card
                    No.</label>

                <div class="col-md-8 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="card-search-number" />
                </div>
            </div>
        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Member
                    Name</label>

                <div class="col-md-8 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="txt-member-name" />
                </div>
            </div>
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Member
                    No</label>

                <div class="col-md-8 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="txt-member-no" />
                </div>
            </div>

        </div>
        <div class="item form-group">
            <div class="col-md-6 col-xs-12">
                <label for="brn-id" class="col-md-4 label-align">Scheme
                    Name</label>

                <div class="col-md-8 col-xs-12">
                    <input type='text' class="form-control float-right"
                           id="dr-scheme-name" />
                </div>
            </div>

        </div>
        <div class="item form-group">
            <input type="button" class="btn btn-info float-right"
                   style="margin-right: 10px;" value="Search"
                   id="btn-search-cards">
        </div>


    </form>
    <div class="card-box table-responsive">
    <table id="searchMembersTbl" class="table table-striped" style="width: 100%">
        <thead>
        <tr class="headings">
            <th><input name="select_all" value="1" id="example-select-all" type="checkbox" /></th>
            <th>Member No.</th>
            <th>Card No.</th>
            <th>Client No.</th>
            <th>Member</th>
            <th>WEF</th>
            <th>WET</th>
            <th>DOB</th>
            <th>Policy No</th>
            <th>Status</th>
            <th></th>
        </tr>
        </thead>
    </table>
    </div>
    <input type="button" class="btn btn-info float-right"
           value="Dispatch" id="btn-dispatch-cards">
    <input type="button" class="btn btn-info float-right"
           value="Mark as Received" id="btn-mark-received">

    <input type="button" class="btn btn-info float-right"
           value="Mark as Requested" id="btn-mark-request" >

</div>
<div class="modal fade" id="cardNoModal" tabindex="-1" role="dialog"
     aria-labelledby="cardNoModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="cardNoModalLabel">
                    Edit/Add Card No
                </h4>
            </div>
            <div class="modal-body">
                <form id="med-cardno-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="card-id" name="cardId">

                    <div class="item form-group" >
                        <label for="unit-id" class="col-md-3 label-align">Policy No</label>
                        <div class="col-md-8">
                            <p class="form-control-static" id="policyno-Display"> </p>
                        </div>
                    </div>
                    <div class="item form-group" >
                        <label for="unit-id" class="col-md-3 label-align">Member</label>
                        <div class="col-md-8">
                            <p class="form-control-static" id="member-Display"> </p>
                        </div>
                    </div>
                    <div class="item form-group" >
                        <label for="unit-id" class="col-md-3 label-align">Member No</label>
                        <div class="col-md-8">
                            <p class="form-control-static" id="memberno-Display"> </p>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="unit-id" class="col-md-3 label-align">Card No</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="card-No"
                                   name="cardNo"  required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveCardNo"
                        type="button" class="btn btn-success">
                    Save Card No
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Cancel
                </button>
            </div>
        </div>
    </div>
</div>

<form id="cards-process-form">

</form>

