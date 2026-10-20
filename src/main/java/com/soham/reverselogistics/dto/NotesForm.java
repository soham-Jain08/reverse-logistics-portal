package com.soham.reverselogistics.dto;

import com.soham.reverselogistics.model.ReturnRequest;
import java.math.BigDecimal;

/** Notes that staff can update while a request is still open. */
public class NotesForm {

    private String remarks;
    private String inspectionRemarks;
    private BigDecimal refundAmount;

    public static NotesForm from(ReturnRequest r) {
        NotesForm form = new NotesForm();
        form.remarks = r.getRemarks();
        form.inspectionRemarks = r.getInspectionRemarks();
        form.refundAmount = r.getRefundAmount();
        return form;
    }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getInspectionRemarks() { return inspectionRemarks; }
    public void setInspectionRemarks(String inspectionRemarks) { this.inspectionRemarks = inspectionRemarks; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
}
