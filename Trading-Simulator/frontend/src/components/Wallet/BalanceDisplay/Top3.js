import React, { useState, useEffect, useCallback } from "react";
import { getTop3Players } from '../../../services/PortfolioService';


const Top3 = () => {
  const [top3Players, setTop3Players] = useState([]);
  const [refresh, setRefresh] = useState(false);

  const fetchTop3Players = useCallback(async () => {
    try {
      const players = await getTop3Players();
      setTop3Players(players);
    } catch (error) {
      console.error('Failed to fetch top 3 players:', error);
    }
  }, []);

  useEffect(() => {
    fetchTop3Players();
  }, [fetchTop3Players, refresh]);

  const handleReload = () => {
    setRefresh((prev) => !prev);
  };

  return (
    <div className="top3-card">
      <div className="top3-content">
        <div className="top3-header">
          <span className="title">Top 3 Users</span>
        </div>
        <div className="top3-list">
          {top3Players.length > 0 ? (
            top3Players.map((player, index) => (
              <div key={index} className="top3-item">
                <span className="player-name">{player.firstname}</span>
                <span className="player-gain" style={{ color: player.totalGain > 0 ? "green" : "red" }}>
                  {player.totalGain > 0 ? "+" : ""}${parseFloat(player.totalGain).toFixed(2)}
                </span>
              </div>
            ))
          ) : (
            <div>Loading...</div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Top3;
