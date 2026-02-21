document.addEventListener("DOMContentLoaded", () => {

    const plantGrid = document.getElementById('plantGrid');
    const initialPlantsHTML = plantGrid.innerHTML;
    //Listener for search bar
    const searchInput = document.getElementById('plantSearch');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
        const query = e.target.value.trim();
            if (query.length >= 3) {
                fetchPlants(query);
            } else if (query.length > 0) {
                plantGrid.innerHTML = '<div class="search-notice">Please enter at least 3 characters to search...</div>';
            }
            else {
                plantGrid.innerHTML = initialPlantsHTML;
            }
        });
    }

    //Listener for add button
    plantGrid.addEventListener('click', async (e) => {
        if (e.target.classList.contains('add-btn')) {
            const button = e.target;
            const plantId = button.getAttribute('data-id');

            await addToLibrary(plantId, button);
        }
    });
});

async function fetchPlants(query) {
    const grid = document.getElementById('plantGrid');
    try {
        const response = await fetch(`/api/plants/search?query=${encodeURIComponent(query)}`);
        const plants = await response.json();

        //View result
        grid.innerHTML = '';
        if (plants.length === 0) {
            grid.innerHTML = '<p class="no-results">No plants found matching your search.</p>';
            return;
        }
        //Rendering new plants
        plants.forEach(plant => {
            const card = createPlantCard(plant);
            grid.appendChild(card);
        });
    } catch (error) {
        console.error("Fetch error:", error);
    }
}

// Build plant cards
function createPlantCard(plant) {
    const article = document.createElement('article');
    article.className = 'plant-card';
    article.innerHTML = `
        <img src="${plant.imageUrl}" alt="${plant.name}" class="plant-image">
        <div class="plant-info">
            <div class="plant-name">${plant.name}</div>
            <div class="scientific-name">${plant.scientificName}</div>
            <button class="add-btn" onclick="addToLibrary(${plant.id})">
                + Add to library
            </button>
        </div>
    `;
    return article;
}

async function addToLibrary(plantId, button){
    const originalText = button.innerHTML;

    try {
        button.disabled = true;
        button.innerHTML = 'Adding...';
        const response = await fetch(`/api/library/add/${plantId}`, {
            method: 'PUT',
        });

        if (response.ok) {
            button.innerHTML = '✓ Added to Library';
            button.style.backgroundColor = '#585E58';
            button.classList.add('success');
        } else {
            throw new Error('Could not add plant');
        }
    } catch (error) {
        console.error("Error:", error);
        button.innerHTML = 'Error! Try again';
        button.disabled = false;
        setTimeout(() => {
            button.innerHTML = originalText;
        }, 2000);
    }
}