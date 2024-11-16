package com.example.backend.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for transaction history")
public class TransactionHistoryDTO {

    @Schema(description = "Unique identifier of the transaction", example = "1")
    private Integer transactionid;

    @Schema(description = "Type of transaction (e.g., BUY, SELL)", example = "BUY")
    private String transactionType;

    @Schema(description = "Amount of currency transacted", example = "0.05")
    private BigDecimal amount;

    @Schema(description = "Rate at which the transaction occurred", example = "45000.00")
    private BigDecimal rate;

    @Schema(description = "Timestamp of the transaction", example = "2023-10-01T12:34:56")
    private LocalDateTime timestamp;

    @Schema(description = "Name of the currency", example = "Bitcoin")
    private String currencyName;

    @Schema(description = "Name of the portfolio", example = "Retirement Fund")
    private String portfolioName;

    @Schema(description = "Indicates if the transaction is marked as suspicious", example = "false")
    private boolean suspicious;

    @Schema(description = "Email of the user who made the transaction", example = "user@example.com")
    private String userEmail;

    @Schema(description = "Total price of the transaction", example = "2250.00")
    private BigDecimal totalPrice;

    @Schema(description = "ID of the portfolio", example = "1")
    private Integer portfolioid;
}
