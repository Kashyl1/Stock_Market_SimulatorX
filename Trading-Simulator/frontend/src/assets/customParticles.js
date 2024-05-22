import Particles from 'particlesjs';

export const initParticles = () => {
  try {
    const backgroundElement = document.querySelector('.background');
    if (!backgroundElement) {
      throw new Error('Element with selector ".background" not found.');
    }

    Particles.init({
      selector: '.background',
      maxParticles: 150,
      sizeVariations: 25,
      speed: 0.15,
      color: ['#DCE0D9', '#31081F', '#6B0F1A', '#595959', '#808F85'],
      minDistance: 140,
    });
  } catch (error) {
    console.error('Error initializing particles:', error);
  }
};
