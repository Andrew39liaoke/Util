package com.example.utils.ossUtil;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author sun
 * @since 2025-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_expenses")
@ApiModel(value="Expenses对象", description="")
public class Expenses implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id值")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer payType;

    private Date payDate;

    private BigDecimal payAmount;

    private String payReson;

    private String payee;

    private String payImg;

    private String operatorId;

    private String operatorName;

    private String note;


}
