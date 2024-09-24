import Particles from 'particlesjs';

export const initParticles = () => {
  const backgroundElement = document.querySelector('.background');

  if (backgroundElement) {
    Particles.init({
      selector: '.background',
      maxParticles: 150,
      sizeVariations: 4,
      speed: 0.15,
      color: ['#FFFFFF', '#FFFAFA', '#D3D3D3', '#FFD700'],
      minDistance: 140
    });
  } else {
    console.warn('Element with selector ".background" not found, skipping particles initialization.');
  }
};
