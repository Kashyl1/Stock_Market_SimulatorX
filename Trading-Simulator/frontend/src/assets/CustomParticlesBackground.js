import React from 'react';
import ParticlesBg from 'particles-bg';

const CustomParticlesBackground = () => {
  const config = {
    num: [1, 2], // Liczba cząsteczek
    rps: 0.001,       // Obrót na sekundę
    radius: [1, 5],// Promień cząsteczek
    life: [1, 3], // Czas życia cząsteczek w sekundach
    v: [0.0001, 0.0002],    // Prędkość cząsteczek
    tha: [-120, 120], // Kąt ruchu cząsteczek
    alpha: [1, 0.2],// Przezroczystość cząsteczek
    scale: [0.5, 0.8],// Skalowanie cząsteczek
    position: 'all',// Pozycja generowania cząsteczek
    color: ['#ff7e5f', '#feb47b'], // Kolory cząsteczek
    cross: 'dead',  // Sposób zachowania na krawędziach
    random: 5,     // Stopień losowości
    g: 3,           // Grawitacja
  };

  return (
    <ParticlesBg type="custom" config={config} bg={true} />
  );
};

export default CustomParticlesBackground;