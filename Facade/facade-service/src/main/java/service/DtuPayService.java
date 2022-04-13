package service;

import messaging.MessageQueue;

public class DtuPayService<T> {
    private final AccountService<T> regServ;
    private final PaymentService<T> payServ;
    private final ReportService<T> reportService;
    private final TokenService<T> tokenServ;

    public DtuPayService(MessageQueue q) {
        this.regServ = new AccountService<T>(q);
        this.payServ = new PaymentService<T>(q);
        this.reportService = new ReportService<>(q);
        this.tokenServ = new TokenService<>(q);
    }

    public AccountService<T> getAccountService() {
        return regServ;
    }

    public PaymentService<T> getPaymentService() {
        return payServ;
    }

    public ReportService<T> getReportService() {
        return reportService;
    }

    public TokenService<T> getTokenService() {
        return tokenServ;
    }
}
