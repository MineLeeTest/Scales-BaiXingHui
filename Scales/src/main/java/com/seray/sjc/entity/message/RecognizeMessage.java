package com.seray.sjc.entity.message;

import com.seray.sjc.api.result.RecognizeResult;
import com.seray.sjc.entity.product.SjcProduct;

import java.io.Serializable;
import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/5/15 16:31
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class RecognizeMessage implements Serializable {

    public boolean isSuccess;

    public List<RecognizeResult> serverData;

    public List<SjcProduct> similarProducts;

    public RecognizeMessage() {
    }

    public RecognizeMessage( boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
