<div class="modal fade" id="businessGroupModal" tabindex="-1" role="dialog"
     aria-labelledby="businessGroupModallLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="businessGroupModalLabel">
                    Edit/Add Business Source Group
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

            </div>
            <div class="modal-body" id="sourcegroup_model">
                <form id="sourcegroup-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="srcGroup-Id" name="srcGroupId">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Sht Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="group-shtDesc"
                                   name="shtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="srcGroup-Id" class="col-md-3 label-align">Description</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control"
                                   id="desc-name" name="desc"
                                   required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveSourceGroupBtn"
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

<div class="modal fade" id="sourceModal" tabindex="-1" role="dialog"
     aria-labelledby="sourceModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="sourceModalLabel">
                    Edit/Add Business Source
                </h4>
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>

                <input type="hidden" class="form-control" id="sourcegrp-pk-code" name="businessSourceGroup">
            </div>
            <div class="modal-body" id="sourcegrp_model">
                <form id="source-form" class="form-horizontal">
                    <input type="hidden" class="form-control" id="src-Id" name="srcId">
                    <input type="hidden" class="form-control" id="srcGroup-Id-code" name="businessSourceGroup">
                    <div class="item form-group">
                        <label for="brn-id" class="col-md-3 label-align">Sht Desc</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control" id="source-shtDesc"
                                   name="shtDesc"  required>
                        </div>
                    </div>
                    <div class="item form-group">
                        <label for="cou-name" class="col-md-3 label-align">Description</label>

                        <div class="col-md-8">
                            <input type="text" class="form-control"
                                   id="source-desc" name="desc"
                                   required>
                        </div>
                    </div>

                </form>
            </div>
            <div class="modal-footer">
                <button data-loading-text="Saving..." id="saveSourceBtn"
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