document.addEventListener("DOMContentLoaded", () => {
    const loader = document.getElementById("loading");
    if (!loader) return;

    loader.classList.add("hidden");


    //Show loader for links
    document.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", function(e) {
            if (this.href &&
                this.target !== "_blank" &&
                !this.getAttribute("href").startsWith("#") &&
                !this.classList.contains("no-loader")) {

                loader.classList.remove("hidden");
                setSafetyTimeout(loader);
            }
        });
    });

    //Show loader for forms
    document.querySelectorAll("form").forEach(form => {
        form.addEventListener("submit", function() {
            if (!this.classList.contains("no-loader")) {
                loader.classList.remove("hidden");
                setSafetyTimeout(loader);
            }
        });
    });

    //Hide loader if navigating back
    window.addEventListener('pageshow', (event) => {
        if (event.persisted) {
            loader.classList.add("hidden");
        }
    });

    //This is only in case the loader gets stuck, we cap it to 6 seconds of loading
    function setSafetyTimeout(loaderElement) {
        setTimeout(() => {
            if (!loaderElement.classList.contains("hidden")) {
                loaderElement.classList.add("hidden");
                console.warn("Loader hidden by safety timeout (6s limit reached).");
            }
        }, 6000);
    }
});

