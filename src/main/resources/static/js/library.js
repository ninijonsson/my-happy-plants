/**
 * Library Dashboard Controller
 * Manages plant filtering, watering logic, and modal interactions.
 */
document.addEventListener("DOMContentLoaded", () => {

    const plants = document.querySelectorAll('.plant-container');
    const searchLibrary = document.getElementById('library-search');
    const plantList = document.getElementById('users-plants');
    
    const loading = document.getElementById('loading');

    /**
     * Live Library Filter
     * Filters plants existing in the DOM. Does not make API calls.
     * Diminishes non-matches and reorders the list to move matches to the top.
     */
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
        const tagLabel = plant.dataset.taglabel;

        if (tagLabel === "Wishlist") {
            return;
        }

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
    const customTagInput = document.getElementById('custom-tag-input');

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
    document.querySelectorAll('.open-tag-selection').forEach(button => {
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
            const lastWatered = new Date(plantContainer.dataset.lastWatered);
            const wateringDays = parseInt(plantContainer.dataset.wateringDays, 10);
            const plantId = plantContainer.dataset.plantid;
            const userId = plantContainer.dataset.userid;

            const now = new Date();
            const diffTime = now - lastWatered;
            const diffDays = diffTime / (1000 * 60 * 60 * 24);
            const percent = (diffDays / wateringDays) * 100;
            const wasNeedingWater = percent >= 100;

            const date = new Date();
            const wateringDate = date.getFullYear() + '-' +
                String(date.getMonth()+1).padStart(2,'0') + '-' +
                String(date.getDate()).padStart(2,'0') + 'T' +
                String(date.getHours()).padStart(2,'0') + ':' +
                String(date.getMinutes()).padStart(2,'0') + ':' +
                String(date.getSeconds()).padStart(2,'0');

            const params = new URLSearchParams();
            params.append('wateringDate', wateringDate);

            try {
                const response = await fetch(
                    `/library/${userId}/plants/${plantId}/water?${params.toString()}`,
                    {
                        method: 'PUT'
                    }
                );

                if (response.ok) {
                    // Update lastWatered in frontend
                    plantContainer.dataset.lastWatered = new Date().toISOString();
                    updatePlantBar(plantContainer);
                    if (wasNeedingWater) {
                        const counter = document.getElementById("needs-watering-count");
                        if (counter) {
                            let current = parseInt(counter.textContent, 10);
                            if (current > 0) {
                                counter.textContent = current - 1;
                            }
                        }
                    }

                    const plantList = document.getElementById('users-plants');
                    //Move to the end of the list - this works better than moving to the end, but reload is not as nice looking. a fade looks nice, but previous logic would send it to the wishlist if you are in water in the menu.
                   document.body.classList.add("fade-out");

                   setTimeout(() => {
                       window.location.reload();
                   }, 300);


                } else {
                    alert("Failed to water plant.");
                }

            } catch (error) {
                console.error(error);
            }
        });
    });

    // Update tag & create tag
    tagConfirmBtn.addEventListener('click', async () => {
        if (!currentPlantForTag) return;

        const selectedTag = document.querySelector('input[name="tag-selection"]:checked');
        const customLabel = (customTagInput?.value || '').trim();

        if (customLabel.length === 0 && !selectedTag) {
            alert("Please select a tag or write a new one.");
            return;
        }

        if (customLabel.length > 0 && selectedTag) {
            alert("Please select either a tag or write a new one, not both.");
            return;
        }

        const plantId = currentPlantForTag.dataset.plantid;

        try {
            let response;

            if (customLabel.length > 0) {
                response = await fetch(`/library/plants/${plantId}/tag`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ label: customLabel })
                });
            } else {
                const tagId = selectedTag.value;
                response = await fetch(`/library/plants/${plantId}/tags/${tagId}`, {
                    method: 'PUT'
                });
            }

            if (response.ok) {
                closeTagModal();
                window.location.reload();
            } else {
                alert("Failed to update tag.");
            }
        } catch (error) {
            console.error(error);
            alert("Failed to update tag.");
        }
    });

    tagCancelBtn.addEventListener('click', closeTagModal);

    // Getting Tags
    const loadTags = async () => {

        const response = await fetch('/library/tags', {method: 'GET'});
        const data = await response.json();

        const tagContainer = document.getElementById('tag-list');
        tagContainer.innerHTML = '';

        const tags = Array.isArray(data) ? data : (data.tags || []);

        tags.forEach(tag => {
            tagContainer.innerHTML += `
                                        <div class="tag">
                                            <input type="radio" name="tag-selection" value="${tag.id}" id="tag${tag.id}">
                                            <label for="tag${tag.id}">${tag.label}</label>
                                        </div>
                                        `;
        });
    };

    loadTags();

    function closeTagModal() {
        tagModal.classList.add('hidden');
        currentPlantForTag = null;

        if (customTagInput) {
            customTagInput.value = '';
        }

        const selected = document.querySelector('input[name="tag-selection"]:checked');
        if (selected) selected.checked = false;
    }

    // drop down sort elements
    const sortBtn = document.getElementById("sort-dropdown-btn");
    const sortMenu = document.getElementById("sort-dropdown-menu");

    if (sortBtn) {
        sortBtn.addEventListener("click", () => {
            sortMenu.classList.toggle("hidden");
        });

        document.addEventListener("click", (e) => {
            if (!sortBtn.contains(e.target) && !sortMenu.contains(e.target)) {
                sortMenu.classList.add("hidden");
            }
        });
    }


});

/**
 * Calculates and updates the visual watering progress bar.
 * Updates bar width, color, and tooltip text based on days since last watered.
 * * @param {HTMLElement} plant - The plant container element with data-attributes.
 */
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
    const statusText = plant.querySelector(".water-status-text");

    // Bar coloring
    let color, text;
    if (percent >= 100) {
        color = "#ef4444";
        text = "Needs watering now!";
    } else if (percent >= 80) {
        color = "#f97316";
        text = "Almost thirsty";
    } else if (percent >= 60) {
        color = "#eab308";
        text = "Getting dry";
    } else {
        color = "#22c55e";
        text = "Plenty of water";
    }
    barFill.style.background = color;

    // View text when hover on bar
    const progressBar = plant.querySelector('.progress-bar');
    progressBar.addEventListener('mouseenter', () => {
        statusText.textContent = text;
        statusText.style.opacity = 1;
        statusText.style.color = color; // matcha barfärgen
    });
    progressBar.addEventListener('mouseleave', () => {
        statusText.style.opacity = 0;
    });

    // Days since last watered
    const daysText = plant.querySelector('.days-since-watered');
    daysText.textContent = `Days since last watered: ${daysSinceWatered} days`;
}

async function handleBrokenImage(img) {
    if (img.dataset.retrying === "true") return;
    img.dataset.retrying = "true";

    const plantId = img.dataset.plantid;

    try {
        const response = await fetch(`/library/plants/${plantId}/refresh-image`, {
            method: 'PUT'
        });

        if (response.ok) {
            const data = await response.json();

            if (data.imageUrl) {
                img.src = data.imageUrl;
                return;
            }
        }
    } catch (error) {
        console.error("Image refresh failed:", error);
    }

    img.src = "/images/plant.jpg";
}


