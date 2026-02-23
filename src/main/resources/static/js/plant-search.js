document.addEventListener("DOMContentLoaded", () => {

    const plantGrid = document.getElementById('plantGrid');
    const initialPlantsHTML = plantGrid.innerHTML;
    const searchInput = document.getElementById('plantSearch');

    if (!searchInput) return;

    //Listener for 3 or more letters in search bar
    /*
    let debounceTimer;
    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.trim();
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            if (query.length >= 3) {
                fetchPlants(query);
            } else if (query.length === 0) {
                plantGrid.innerHTML = initialPlantsHTML;
            }
        }, 400);
    });
     */

    // Enter press on search bar
    searchInput.addEventListener('keydown', async (e) => {
        if (e.key === 'Enter') {
            const query = e.target.value.trim();
            if (query.length >= 3) {
                await fetchPlants(query);
                document.getElementById("searchNotice").style.display = "none"
            } else if (query.length === 0) {
                plantGrid.innerHTML = initialPlantsHTML;
            }else{
                document.getElementById("searchNotice").textContent = "You must type at least 3 letters to see results.";
                document.getElementById("searchNotice").style.display = "block";
                e.preventDefault();
            }
        }
    });

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
        const response = await fetch(`/plants/search?q=${encodeURIComponent(query)}`);
        const html = await response.text();
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        const newGrid = doc.getElementById('plantGrid');

        if (newGrid) {
            grid.innerHTML = newGrid.innerHTML;
        }
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