package com.seray.instance;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@ToString
@NoArgsConstructor
public class CompanyDB implements Serializable {
    private Integer company_id;
    private String create_time  ;
    private String update_time ;
    private Boolean enables;
    private String aes_key;
    private String company_name;
    private String company_address;
    private String wx_merchant_name;
    private String wx_appid;
    private String wx_secretkey;
    private String wx_merchant_id;
    private String trans_url;
    private String remark;

}