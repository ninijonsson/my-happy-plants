
    const searchInput = document.getElementById("plantSearch");
    const familyFilter = document.getElementById("familyFilter");
    const waterFilter = document.getElementById("waterFilter");
    const sunFilter = document.getElementById("sunFilter");
    const plantCards = document.querySelectorAll(".plant-card");

    function filterPlants() {
        const searchQuery = searchInput.value.toLowerCase();
        const familyValue = familyFilter.value.toLowerCase();
        const waterValue = waterFilter.value.toLowerCase();
        const sunValue = sunFilter.value.toLowerCase();

        plantCards.forEach(card => {
            const name = card.querySelector("h3").textContent.toLowerCase();
            const desc = card.querySelector("p").textContent.toLowerCase();
            const family = (card.getAttribute("data-family") || "").toLowerCase();
            const water = (card.getAttribute("data-water") || "").toLowerCase();
            const sun = (card.getAttribute("data-sun") || "").toLowerCase();

            const matchesSearch = name.includes(searchQuery) || desc.includes(searchQuery);
            const matchesFamily = !familyValue || family === familyValue;
            const matchesWater = !waterValue || water === waterValue;
            const matchesSun = !sunValue || sun === sunValue;

            if (matchesSearch && matchesFamily && matchesWater && matchesSun) {
                card.style.display = "block";
            } else {
                card.style.display = "none";
            }
        });
    }

    searchInput.addEventListener("input", filterPlants);
    familyFilter.addEventListener("change", filterPlants);
    waterFilter.addEventListener("change", filterPlants);
    sunFilter.addEventListener("change", filterPlants);
