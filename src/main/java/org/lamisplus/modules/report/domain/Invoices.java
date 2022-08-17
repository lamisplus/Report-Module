package org.lamisplus.modules.report.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Invoices {
    Integer itemId;
    String itemName;
    Integer itemQty;
    Double totalPrice;
    Date itemSoldDate;
}
