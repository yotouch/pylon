<#import "wf.main.layout.ftl" as u>

<@u.page>
<div class="container">
    <div class="row">
        
    
        <form action="" method="post" class="form">
            <div class="">
                <label for="name">名称</label>
                <div>
                    <input class="form-control" type="text" name="workflow_name" id="workflow_name" value="${workflow.v("name")!}" required/>
                </div>
            </div>
            <div class="">
                <label for="name">显示名称</label>
                <div>
                    <input class="form-control" type="text" name="workflow_displayName" id="workflow_displayName" value="${workflow.v("displayName")!}" required/>
                </div>
            </div>
            <div class="">
                <label for="name">Entity</label>
                <div>
                    <select class="form-control" name="metaEntity" id="metaEntity">
                        <#list metaEntityList as me>
                            <option value="${me.uuid}">${me.name}</option>
                        </#list>
                    </select>
                </div>
            </div>
            <div>
                <input type="submit" class="btn btn-primary">
            </div>
        </form>
    </div>
</div>

</@u.page>