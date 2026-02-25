/**
 * External Plant Search Controller
 * Handles live DOM filtering and AJAX requests to the Perenual API.
 */

document.addEventListener("DOMContentLoaded", () => {

    const plantGrid = document.getElementById('plantGrid');
    const initialPlantsHTML = plantGrid.innerHTML;
    const searchInput = document.getElementById('plantSearch');
    
    const loading = document.getElementById('loading');

    if (!searchInput) return;

    //Instant frontend filtering when searching (happens when the user types)
    searchInput.addEventListener('input', async (e) => {
        const term = e.target.value.toLowerCase().trim();
        const plantCards = plantGrid.querySelectorAll('.plant-card');
        let visibleCount = 0; // keep track of how many plants are currently showing

        plantCards.forEach(card => {
            const name = card.querySelector('.plant-name')?.textContent.toLowerCase() || "";
            const sciName = card.querySelector('.scientific-name')?.textContent.toLowerCase() || "";

            if (term === '' || name.includes(term) || sciName.includes(term)) {
                card.style.display = "";
                visibleCount++;
            } else {
                card.style.display = "none";
            }
        });

        const notice = document.getElementById("searchNotice");
        if (visibleCount === 0) {
            notice.textContent = "Not found on this page, press enter to search through the entire database!";
            notice.style.display = "block";
            notice.className = "search-notice text-blue-800 bg-blue-100 p-3 rounded-lg mt-2";
        } else {
            notice.style.display = "none";
        }
    });

    // Enter press on search bar
    /**
     * Trigger API Search
     * Listens for 'Enter' key. If query >= 3 chars, fetches new HTML from server.
     */
    searchInput.addEventListener('keydown', async (e) => {
        if (e.key === 'Enter') {
            const query = e.target.value.trim();
            if (query.length >= 3) {
                loading.classList.remove('hidden');
                loading.classList.add('flex');
                await fetchPlants(query);
                document.getElementById("searchNotice").style.display = "none"
                loading.classList.remove('flex');
                loading.classList.add('hidden');
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
            loading.classList.remove('hidden');
            loading.classList.add('flex');
            await addToLibrary(plantId, button);
            loading.classList.remove('flex');
            loading.classList.add('hidden');
            
        }
    });
});

/**
 * Communicates with the Backend to fetch Perenual API results.
 * Parses the returned HTML and replaces the current grid content.
 * * @param {string} query - The search term.
 */
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

setTimeout(()=>{
    const toast = document.querySelector(".toast");
    if(toast) toast.style.display = "none";
}, 3000);
