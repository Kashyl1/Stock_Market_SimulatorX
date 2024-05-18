package com.example.backend.watchlistitem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.watchlist.Watchlist;
import com.example.backend.currency.Currency;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "WatchlistItems")
public class WatchlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer watchlistItemID;

    @ManyToOne
    @JoinColumn(name = "watchlistID", nullable = false)
    private Watchlist watchlist;

    @ManyToOne
    @JoinColumn(name = "currencyID", nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime addedAt;
}
