document.addEventListener("DOMContentLoaded", () => {

    const plantGrid = document.getElementById('plantGrid');
    const initialPlantsHTML = plantGrid.innerHTML;
    const searchInput = document.getElementById('plantSearch');
    
    const loading = document.getElementById('loading');

    if (!searchInput) return;

    // Enter press on search bar
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
