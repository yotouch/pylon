<script src="/static/libs/jquery/jquery-1.11.3.min.js"></script>
<script src="/static/libs/jquery/jquery.ui.widget.js"></script>
<script src="/static/libs/jquery/jquery.iframe-transport.js"></script>
<script src="/static/libs/jquery/jquery.fileupload.js"></script>
<script src="/static/libs/jquery/jquery.fileupload-image.js"></script>
<form action="/admin/attachment/watermark">
    <input id="fileupload" type="file" name="file" data-url="/admin/attachment/upload" multiple accept="image/*">
    <input type="text" name="attUuid" id="attUuid" placeholder="Attachment uuid">
    <input type="text" name="watermarkUuid" id="watermarkUuid" placeholder="watermark uuid">
    <div id="att">
    </div>
</form>
<script>
    $(function () {
        $('#fileupload').fileupload({
            dataType: 'json',
            done: function (e, data) {
                console.log(e);
                console.log(data);

                var result = data.result;
                console.log(result);
                
                $('#att').append('<p><a href="/attachment/get?uuid='+result.uuid+'" target="_blank">'+result.uuid+'</a></p>')
                $('#attUuid').val(result.uuid);
            }
        });
    });
</script>
