
// navbar.js
function toggleSettingsMenu() {
    var dropdown = document.getElementById('settingsDropdown');
    if (dropdown) {
        dropdown.classList.toggle('hidden');
        console.log('Dropdown toggled');
    } else {
        console.log('Dropdown element not found');
    }
}

// Stäng dropdown om man klickar utanför
document.addEventListener('click', function(event) {
    var dropdown = document.getElementById('settingsDropdown');
    if (!dropdown) return;

    var settingsButton = event.target.closest('a[onclick*="toggleSettingsMenu"]');

    if (!settingsButton && !dropdown.classList.contains('hidden')) {
        dropdown.classList.add('hidden');
    }
});