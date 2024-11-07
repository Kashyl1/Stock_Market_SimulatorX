package com.example.backend.alert.trade;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.exceptions.*;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeAlertService {

    private final TradeAlertRepository tradeAlertRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final CurrencyRepository currencyRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public TradeAlert createTradeAlert(CreateTradeAlertRequest request) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

        Currency currency = currencyRepository.findById(request.getCurrencyId())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));

        if (request.getTradeAlertType() == TradeAlertType.SELL) {
            boolean ownsCurrency = portfolio.getPortfolioAssets().stream()
                    .anyMatch(asset -> asset.getCurrency().equals(currency) && asset.getAmount().compareTo(BigDecimal.ZERO) > 0);
            if (!ownsCurrency) {
                throw new AssetNotOwnedException("You do not own this currency in the selected portfolio");
            }
        }

        if (request.getTradeAlertType() == TradeAlertType.BUY) {
            BigDecimal amountInUSD = request.getTradeAmount();
            if (user.getBalance().compareTo(amountInUSD) < 0) {
                throw new InsufficientFundsException("Insufficient balance to set this trade alert");
            }
        }

        BigDecimal currentPrice = currency.getCurrentPrice();
        if (currentPrice == null) {
            throw new PriceNotAvailableException("Current price is not available for the selected currency");
        }

        TradeAlert tradeAlert = TradeAlert.builder()
                .user(user)
                .portfolio(portfolio)
                .currency(currency)
                .tradeAlertType(request.getTradeAlertType())
                .conditionType(request.getConditionType())
                .conditionValue(request.getConditionValue())
                .tradeAmount(request.getTradeAmount())
                .active(true)
                .initialPrice(currentPrice)
                .build();

        return tradeAlertRepository.save(tradeAlert);
    }

    @Transactional
    public void deactivateTradeAlert(Integer tradeAlertId) {
        TradeAlert tradeAlert = tradeAlertRepository.findById(tradeAlertId)
                .orElseThrow(() -> new UnsupportedAlertTypeException("Trade alert not found"));

        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (!tradeAlert.getUser().equals(user)) {
            throw new UnauthorizedActionException("You do not have permission to modify this trade alert");
        }

        tradeAlert.setActive(false);
        tradeAlertRepository.save(tradeAlert);
    }

    @Transactional
    public void deleteTradeAlert(Integer tradeAlertId) {
        TradeAlert tradeAlert = tradeAlertRepository.findById(tradeAlertId)
                .orElseThrow(() -> new UnsupportedAlertTypeException("Trade alert not found"));

        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (!tradeAlert.getUser().equals(user)) {
            throw new UnauthorizedActionException("You do not have permission to delete this trade alert");
        }

        tradeAlertRepository.delete(tradeAlert);
    }

    @Transactional(readOnly = true)
    public List<TradeAlert> getUserTradeAlerts() {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);
        return tradeAlertRepository.findByUser(user);
    }

}
