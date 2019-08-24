package com.maplequad.fo.ods.tradecore.balcm.data.model;

import java.util.Date;
import java.util.List;

/**
 * BulkAmendView
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
public class BulkAmendView {

    private String bulkAmendId;
    private String bulkAmendType;
    private String createdBy;
    private String description;
    private String scope;
    private String status;
    private Date lastModifiedAt;
    //private List<BulkAmendRecord> pre;
    //private List<BulkAmendRecord> post;

    public String getBulkAmendId() {
        return bulkAmendId;
    }

    public void setBulkAmendId(String bulkAmendId) {
        this.bulkAmendId = bulkAmendId;
    }

/*    public List<BulkAmendRecord> getPre() {
        return pre;
    }

    public void setPre(List<BulkAmendRecord> pre) {
        this.pre = pre;
    }

    public List<BulkAmendRecord> getPost() {
        return post;
    }

    public void setPost(List<BulkAmendRecord> post) {
        this.post = post;
    }*/

    public String getBulkAmendType() {
        return bulkAmendType;
    }

    public void setBulkAmendType(String bulkAmendType) {
        this.bulkAmendType = bulkAmendType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Date lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }
}
