document.addEventListener("DOMContentLoaded", () => {

    const plants = document.querySelectorAll('.plant-container');

    plants.forEach(plant => {
        updatePlantBar(plant);
    });

    /// Delete Modal elements
    const modal = document.getElementById('delete-modal');
    const cancelBtn = document.getElementById('cancel-btn');
    const confirmBtn = document.getElementById('confirm-btn');

    let plantToDelete = null; // Referens till plant-container som ska tas bort

    /// Tag Modal elements
    const tagModal = document.getElementById('tag-selection-modal');
    const tagCancelBtn = document.getElementById('tag-cancel-btn');
    const tagConfirmBtn = document.getElementById('tag-confirm-btn');

    let currentPlantForTag = null; // Reference to plant-container for tagging

    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            plantToDelete = e.target.closest('.plant-container'); // spara referens
            modal.style.display = 'flex'; // visa modal
        });
    });

// Cancel button closes modal
    cancelBtn.addEventListener('click', () => {
        modal.style.display = 'none';
        plantToDelete = null;
    });

// Confirm button deletes plant
    confirmBtn.addEventListener('click', async () => {
        if (!plantToDelete) return;

        const plantId = plantToDelete.dataset.plantid;
        const userId = plantToDelete.dataset.userid;

        try {
            const response = await fetch(`/library/${userId}/plants/${plantId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                plantToDelete.remove();
                window.location.reload(); //update the window to show the sorted list
            } else {
                alert("Failed to delete plant.");
            }
        } catch (error) {
            console.error(error);
        } finally {
            modal.style.display = 'none';
            plantToDelete = null;
        }
    });

// Close modal by clicking outside content
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
            plantToDelete = null;
        }
        if (e.target === tagModal) {
            tagModal.classList.add('hidden');
            currentPlantForTag = null;
        }
    });

    // Tag Modal: Open when clicking "Add Tag" button
    document.querySelectorAll('#open-tag-selection').forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            currentPlantForTag = e.target.closest('.plant-container');
            tagModal.classList.remove('hidden');
        });
    });

    // Tag Modal: Cancel button
    tagCancelBtn.addEventListener('click', () => {
        tagModal.classList.add('hidden');
        currentPlantForTag = null;
    });

    // Water
    document.querySelectorAll('.water-btn').forEach(button => {
        button.addEventListener('click', async (e) => {
            e.preventDefault();
            const plantContainer = e.target.closest('.plant-container');
            const plantId = plantContainer.dataset.plantid;
            const userId = plantContainer.dataset.userid;

            try {
                const response = await fetch(
                    `/library/${userId}/plants/${plantId}/water`,
                    {
                        method: 'PUT'
                    }
                );

                if (response.ok) {

                    // Uppdatera lastWatered i frontend
                    plantContainer.dataset.lastWatered = new Date().toISOString();

                    updatePlantBar(plantContainer);

                } else {
                    alert("Failed to water plant.");
                }

            } catch (error) {
                console.error(error);
            }
        });
    });
});

function updatePlantBar(plant) {
    const lastWateredStr = plant.dataset.lastWatered;
    const wateringDays = parseInt(plant.dataset.wateringDays, 10);

    if (!lastWateredStr || !wateringDays) return;

    const lastWatered = new Date(lastWateredStr);
    const now = new Date();

    const daysSinceWatered = Math.floor((now - lastWatered) / (1000 * 60 * 60 * 24));
    const percent = Math.min((daysSinceWatered / wateringDays) * 100, 100);
    const barFill = plant.querySelector('.progress-fill');
    barFill.style.width = percent + '%';

    if (percent >= 100) {
        barFill.style.background = "#ef4444";
    } else if (percent >= 70) {
        barFill.style.background = "#f97316";
    } else if (percent >= 40) {
        barFill.style.background = "#eab308";
    } else {
        barFill.style.background = "#22c55e";
    }

    const daysText = plant.querySelector('.days-since-watered');
    daysText.textContent = `Days since last watered: ${daysSinceWatered} days`;
}
