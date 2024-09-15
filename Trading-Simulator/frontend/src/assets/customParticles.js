import Particles from 'particlesjs';

export const initParticles = () => {
  const backgroundElement = document.querySelector('.background');

  if (backgroundElement) {
    Particles.init({
      selector: '.background',
      maxParticles: 150,
      sizeVariations: 25,
      speed: 0.15,
      color: ['#DCE0D9', '#31081F', '#6B0F1A', '#595959', '#808F85'],
      minDistance: 140,
    });
  } else {
    console.warn('Element with selector ".background" not found, skipping particles initialization.');
  }
};
