console.log('rawData:', rawData);
console.log('first item:', rawData?.[0]);

const chartData = rawData.map(d => ({
    x: new Date(d.date + "T00:00:00"),
    y: Number(d.count)
}));

console.log('chartData:', chartData);
console.log('invalid dates:', chartData.filter(p => Number.isNaN(p.x.getTime())));

document.addEventListener('DOMContentLoaded', function () {
    if (!rawData || rawData.length === 0) return;

    const canvas = document.getElementById('wateringChart');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    const chartData = rawData.map(d => ({
        x: new Date(d.date + "T00:00:00"),
        y: Number(d.count)
    }));

    const showLine = chartData.length >= 2;
    const minDate = new Date(chartData[0].x);
    minDate.setHours(0, 0, 0, 0);

    const maxDate = new Date(); // today
    maxDate.setHours(23, 59, 59, 999);

    new Chart(ctx, {
        type: 'line',
        data: {
            datasets: [{
                label: 'Total watering',
                data: chartData,
                borderColor: '#427A43',
                backgroundColor: '#427A43',
                fill: false,
                tension: 0.3,
                // nodes (dots)
                pointRadius: 5,
                pointHoverRadius: 7,
                // only draw the line if there are 2+ points
                showLine: showLine
            }]
        },
        options: {
            parsing: false,
            animation: false,
            scales: {
                x: { type: 'time', time: { unit: 'day' },
                    min: minDate,
                    max: maxDate,
                    title: { display: true, text: 'Date' }
                },
                y: { beginAtZero: true, title: { display: true, text: 'Times Watered' },
                max: 30
                }
            }
        }
    });
});