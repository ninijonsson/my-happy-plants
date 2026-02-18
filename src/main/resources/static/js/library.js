document.addEventListener("DOMContentLoaded", () => {
    const plants = document.querySelectorAll('.plant-container');

    plants.forEach(plant => {
        const lastWateredStr = plant.dataset.lastWatered;
        const wateringDays =parseInt(plant.dataset.wateringDays, 10);
        if (!lastWateredStr || !wateringDays) return;

        const lastWatered = new Date(lastWateredStr);
        const now = new Date();

        const daysSinceWatered = Math.floor((now - lastWatered) / (1000 * 60 * 60 * 24));
        const percent = Math.min((daysSinceWatered / wateringDays) * 100, 100);

        let color = 'bg-blue-500';
        if (percent > 100) color = 'bg-red-500';
        else if (percent > 80) color = 'bg-orange-500';
        else if (percent > 50) color = 'bg-yellow-400';

        const barFill = plant.querySelector('.hp-bar-fill');
        barFill.style.width = percent + '%';
        barFill.className = `hp-bar-fill h-4 rounded ${color}`;

        const daysText = plant.querySelector('.days-since-watered');
        daysText.textContent = `Days since last watered: ${daysSinceWatered} days`;
    });
});
