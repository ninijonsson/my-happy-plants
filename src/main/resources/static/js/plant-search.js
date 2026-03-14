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

    //Instant frontend filtering when searching
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
                loading.classList.remove('hidden');
                loading.classList.add('flex');
                await fetchPlants(query);
                document.getElementById("searchNotice").style.display = "none"
                loading.classList.remove('flex');
                loading.classList.add('hidden');
            } else if (query.length === 0) {
                plantGrid.innerHTML = initialPlantsHTML;
            } else {
                document.getElementById("searchNotice").textContent = "You must type at least 3 letters to see results.";
                document.getElementById("searchNotice").style.display = "block";
                e.preventDefault();
            }
        }
    });

    plantGrid.addEventListener('click', function(e) {
        const button = e.target.closest('.add-btn');
        if (button) {
            e.preventDefault();

            const form = button.closest('form');
            const originalText = button.textContent;

            // Ändra knappen
            button.textContent = '✓ Added!';
            button.style.backgroundColor = '#10b981';
            button.disabled = true;

            // Skicka till servern
            fetch(form.action, {
                method: 'POST',
                body: new FormData(form)
            });

            // Återställ knappen efter 2 sekunder
            setTimeout(() => {
                button.textContent = originalText;
                button.style.backgroundColor = '';
                button.disabled = false;
            }, 2000);
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

setTimeout(()=>{
    const toast = document.querySelector(".toast");
    if(toast) toast.style.display = "none";
}, 3000);