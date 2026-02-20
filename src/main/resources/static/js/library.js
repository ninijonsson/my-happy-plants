document.addEventListener("DOMContentLoaded", () => {

    const plants = document.querySelectorAll('.plant-container');
    const searchLibrary = document.getElementById('library-search');
    const plantList = document.getElementById('users-plants');

    //Listen for typing into the search bar
    searchLibrary.addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase();

        //Only filter once the user types 3 or more characters
        if (term.length >= 3) {
            const matches = [];
            const nonMatches = [];

            plants.forEach(plant => {
                //Get the common name of the plant for comparison
                const name = plant.querySelector('h3').textContent.toLowerCase();

                if (name.includes(term)) {
                    //We show matches with full opacity and the original size
                    plant.style.opacity = "1";
                    plant.style.transform = "scale(1)";
                    matches.push(plant);
                } else {
                    //Dim out the non matches and slightly shrink them
                    plant.style.opacity ="0.3";
                    plant.style.transform ="scale(0.95)";
                    nonMatches.push(plant);
                }
            });

            //Move the most relevant searches to the top while keeping the others visible below
            const newOrder = [...matches, ...nonMatches];
            newOrder.forEach(node => plantList.appendChild(node));
        } else {
            //Reset the view if search is cleared or < 3 characters, restore all plants to 100% visible
            plants.forEach(plant => {
                plant.style.opacity = "1";
                plant.style.transform ="scale(1)";
            });
            //Restore the original sort order
            plants.forEach(node => plantList.appendChild(node));
        }
    });

    plants.forEach(plant => {
        updatePlantBar(plant);
    });

    /// Modal elements
    const modal = document.getElementById('delete-modal');
    const cancelBtn = document.getElementById('cancel-btn');
    const confirmBtn = document.getElementById('confirm-btn');

    let plantToDelete = null; // Referens till plant-container som ska tas bort

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
