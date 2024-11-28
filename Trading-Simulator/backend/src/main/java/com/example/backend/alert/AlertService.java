package com.example.backend.alert;

import com.example.backend.alert.mail.EmailAlertDTO;
import com.example.backend.alert.mail.EmailAlertMapper;
import com.example.backend.alert.mail.EmailAlertRepository;
import com.example.backend.alert.trade.TradeAlertDTO;
import com.example.backend.alert.trade.TradeAlertMapper;
import com.example.backend.alert.trade.TradeAlertRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertService { // Przypominajka, przemyśleć / usunać xd

    private final EmailAlertRepository emailAlertRepository;
    private final TradeAlertRepository tradeAlertRepository;
    private final EmailAlertMapper emailAlertMapper;
    private final TradeAlertMapper tradeAlertMapper;

    public Page<EmailAlertDTO> getAllEmailAlerts(Pageable pageable) {
        return emailAlertRepository.findAll(pageable).map(emailAlertMapper::toDTO);
    }

    public Page<TradeAlertDTO> getAllTradeAlerts(Pageable pageable) {
        return tradeAlertRepository.findAll(pageable).map(tradeAlertMapper::toDTO);
    }

    @Transactional
    public void deleteEmailAlertById(Integer alertId) {
        emailAlertRepository.deleteById(alertId);
    }

    @Transactional
    public void deleteTradeAlertById(Integer alertId) {
        tradeAlertRepository.deleteById(alertId);
    }

}
