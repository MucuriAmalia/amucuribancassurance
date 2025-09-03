<div class="modal fade" id="importDataModal" tabindex="-1" role="dialog"
     aria-labelledby="importDataModalLabel" aria-hidden="true">
    <form id="data-upload-form" class="form-horizontal" enctype="multipart/form-data">
        <div class="modal-dialog modal-md">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="uploadTransModal">Upload Data</h4>
                    <button type="button" class="close" data-dismiss="modal"
                            aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>

                </div>
                <div class="modal-body">

                    <div class="item form-group">
                        <label for="file-avatar" class="label-align col-md-5">
                            Excel File<span class="required">*</span>
                        </label>
                        <div class="col-md-7 col-xs-12">
                            <input name="file" type="file" id="file-avatar" required>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <input type="submit" class="btn btn-success" style="margin-right: 10px;" value="Upload">
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="modal fade" id="reportsModal" tabindex="-1" role="dialog"
     aria-labelledby="reportsModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <h4 class="modal-title" id="reportsModalLabel">Reports</h4>
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-6">
                        <ul style="list-style-type: none;" class="reports-links">
                            <li>
                                <a id="processed-trans-link" href="javascript:void(0);" target="_blank">Processed
                                    Transactions</a>
                            </li>
                            <li>
                                <a id="unprocessed-trans-link" href="javascript:void(0);" target="_blank">Unprocessed
                                    Transactions</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    Close
                </button>
            </div>
        </div>
    </div>
</div>
