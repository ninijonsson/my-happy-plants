
// navbar.js

// Toggle visibility of the settings dropdown menu
function toggleSettingsMenu() {
    var dropdown = document.getElementById('settingsDropdown');
    if (dropdown) {
        dropdown.classList.toggle('hidden');
        console.log('Dropdown toggled');
    } else {
        console.log('Dropdown element not found');
    }
}

// Close dropdown when clicking outside of it
document.addEventListener('click', function(event) {
    var dropdown = document.getElementById('settingsDropdown');
    if (!dropdown) return;

    // Check if the click originated from the settings button (which uses inline onclick)
    var settingsButton = event.target.closest('a[onclick*="toggleSettingsMenu"]');

    if (!settingsButton && !dropdown.classList.contains('hidden')) {
        dropdown.classList.add('hidden');
    }
});