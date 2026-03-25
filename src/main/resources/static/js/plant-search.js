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

    // Instant frontend filtering when searching
    searchInput.addEventListener('input', async (e) => {
        const term = e.target.value.toLowerCase().trim();
        const plantCards = plantGrid.querySelectorAll('.plant-card');
        let visibleCount = 0;

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
        } else {
            notice.style.display = "none";
        }
    });

    // Enter press on search bar
    searchInput.addEventListener('keydown', async (e) => {
        if (e.key === 'Enter') {
            const query = e.target.value.trim();
            if (query.length >= 3) {
                if (loading) {
                    loading.classList.remove('hidden');
                    loading.classList.add('flex');
                }
                await fetchPlants(query);
                document.getElementById("searchNotice").style.display = "none";
                if (loading) {
                    loading.classList.remove('flex');
                    loading.classList.add('hidden');
                }
            } else if (query.length === 0) {
                plantGrid.innerHTML = initialPlantsHTML;
            } else {
                document.getElementById("searchNotice").textContent = "You must type at least 3 letters to see results.";
                document.getElementById("searchNotice").style.display = "block";
                e.preventDefault();
            }
        }
    });

    // Listener for add button
    plantGrid.addEventListener('click', async (e) => {
        const button = e.target.closest('.add-btn');
        if (button) {
            const plantId = button.getAttribute('data-id');
            if (plantId) {
                if (loading) {
                    loading.classList.remove('hidden');
                    loading.classList.add('flex');
                }

                await addToLibrary(plantId, button);

                if (loading) {
                    loading.classList.remove('flex');
                    loading.classList.add('hidden');
                }
            }
        }
    });

    // Läs query-parametern från URL och sätt i sökfältet
    const urlParams = new URLSearchParams(window.location.search);
    const queryParam = urlParams.get('q');
    if (queryParam) {
        searchInput.value = queryParam;
        // Triggera sökningen automatiskt
        setTimeout(() => {
            const event = new KeyboardEvent('keydown', { key: 'Enter' });
            searchInput.dispatchEvent(event);
        }, 100);
    }
});

/**
 * Communicates with the Backend to fetch Perenual API results.
 * Parses the returned HTML and replaces the current grid content.
 * @param {string} query - The search term.
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

/**
 * Adds a plant to the user's library
 * @param {string} plantId - The Perenual plant ID
 * @param {HTMLElement} button - The button element that was clicked
 */
async function addToLibrary(plantId, button) {
    try {
        const response = await fetch('/plants/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `perenualPlantId=${plantId}`
        });

        if (response.ok) {
            // Show success on button
            const originalText = button.textContent;
            button.textContent = '✓ Added!';
            button.style.backgroundColor = '#10b981';
            button.disabled = true;

            // Reset button after 2 seconds
            setTimeout(() => {
                button.textContent = originalText;
                button.style.backgroundColor = '';
                button.disabled = false;
            }, 2000);
        } else {
            console.error('Failed to add plant');
            button.textContent = '❌ Error';
            setTimeout(() => {
                button.textContent = '+ Add to library';
            }, 2000);
        }
    } catch (error) {
        console.error('Error adding plant:', error);
        button.textContent = '❌ Error';
        setTimeout(() => {
            button.textContent = '+ Add to library';
        }, 2000);
    }
}

// Auto-hide temporary toast messages after 3 seconds
setTimeout(() => {
    const toast = document.querySelector(".toast");
    if (toast) toast.style.display = "none";
}, 3000);