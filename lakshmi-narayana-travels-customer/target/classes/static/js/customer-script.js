// Lakshmi Narayana Travels - Interactive Animation & UI Engine

document.addEventListener('DOMContentLoaded', () => {
    initCarPartsExplorer();
    initLiveFilters();
    initCardTilt();
    initPaymentInteractions();
});

// 1. Interactive Car Parts Explorer
function initCarPartsExplorer() {
    const hotspots = document.querySelectorAll('.car-hotspot');
    const partTitle = document.getElementById('partTitle');
    const partDesc = document.getElementById('partDesc');
    const partBadge = document.getElementById('partBadge');
    const partSpecs = document.getElementById('partSpecs');

    if (!hotspots.length || !partTitle) return;

    const partsData = {
        engine: {
            title: "Smart BS6 Phase 2 Engine & Powertrain",
            badge: "High Performance & Efficiency",
            desc: "Refined multi-valve turbo petrol, torque-rich diesel, and factory-fitted smart CNG powertrains engineered for seamless hill climbs and high-speed highway cruising with zero vibration.",
            specs: ["BS6 RDE Compliant", "Eco, City & Sport Power Modes", "Quick Response Turbo Boost", "Smart Stop/Start Idle Technology"]
        },
        chassis: {
            title: "Aerodynamic High-Tensile Steel Safety Cage",
            badge: "Rigid Structural Safety",
            desc: "Engineered with hot-stamped boron steel pillars and crumple zones designed to absorb crash impacts, ensuring peak cabin safety and effortless high-speed wind stability.",
            specs: ["Crumple Zone Crash Absorption", "Low Drag Coefficient Cd 0.29", "Anti-Corrosion Zinc Undercoating", "Noise Insulation Acoustic Glass"]
        },
        wheels: {
            title: "Ventilated Disc Brakes & Precision Alloy Wheels",
            badge: "Active Grip & Braking",
            desc: "Equipped with 4-wheel Anti-lock Braking System (ABS), Electronic Brakeforce Distribution (EBD), and precision-balanced low-noise all-weather radial tyres.",
            specs: ["Dual Ventilated Front Discs", "Active ABS & Cornering Braking Control (CBC)", "TPMS Real-time Tyre Pressure Monitor", "Diamond Cut Alloy Wheels"]
        },
        interior: {
            title: "Ergonomic Luxury Cabin & Multi-Zone Climate Control",
            badge: "Pure Comfort & Silence",
            desc: "Plush leather-wrapped ventilated seats, rear AC blower vents for every row, ambient mood lighting, and wireless charging so you arrive refreshed after long journeys.",
            specs: ["Dual-Zone Automatic Climate Control (ACC)", "Ventilated Front Seats", "Foldable 60:40 Flexible Seating", "PM 2.5 Active Cabin Air Filter"]
        },
        safety: {
            title: "6 Airbags & Advanced Driver Safety (ADAS)",
            badge: "5-Star Level Protection",
            desc: "Comprehensive safety suite with up to 7 airbags, Electronic Stability Program (ESP), Hill-Descent Control, and ISOFIX child seat anchor mounts.",
            specs: ["6 to 7 Standard Airbags", "Electronic Stability Program (ESP)", "ISOFIX Child Seat Anchors", "360-Degree Surround Parking Cameras"]
        },
        boot: {
            title: "Massive Luggage Capacity with Flexible Folding",
            badge: "Spacious Travel Utility",
            desc: "Expansive luggage bay that easily accommodates 4 to 6 large travel suitcases, golf bags, and baby strollers with a flat-folding third-row bench.",
            specs: ["Up to 600 Litres Luggage Volume", "Smart Handsfree Tailgate Release", "Sturdy Tie-Down Luggage Hooks", "Low Loading Lip for Easy Access"]
        }
    };

    hotspots.forEach(spot => {
        spot.addEventListener('click', () => {
            hotspots.forEach(s => s.classList.remove('active'));
            spot.classList.add('active');

            const partKey = spot.getAttribute('data-part');
            const data = partsData[partKey];
            if (data) {
                partTitle.innerText = data.title;
                partBadge.innerText = data.badge;
                partDesc.innerText = data.desc;

                let specsHtml = '';
                data.specs.forEach(s => {
                    specsHtml += `<li class="d-flex align-items-center mb-2"><i class="fas fa-check-circle text-success me-2"></i> ${s}</li>`;
                });
                partSpecs.innerHTML = specsHtml;
            }
        });
    });
}

// 2. Client-Side Live Filters for Car Fleet
function initLiveFilters() {
    const filterFuel = document.getElementById('filterFuel');
    const filterSeaters = document.getElementById('filterSeaters');
    const filterTransmission = document.getElementById('filterTransmission');
    const filterPrice = document.getElementById('filterPrice');
    const priceDisplay = document.getElementById('priceDisplay');
    const carItems = document.querySelectorAll('.car-item-card');

    if (!carItems.length) return;

    function applyFilters() {
        const fuelVal = filterFuel ? filterFuel.value.toLowerCase() : 'all';
        const seaterVal = filterSeaters ? filterSeaters.value : 'all';
        const transVal = filterTransmission ? filterTransmission.value.toLowerCase() : 'all';
        const priceVal = filterPrice ? parseFloat(filterPrice.value) : 10000;

        if (priceDisplay && filterPrice) {
            priceDisplay.innerText = '₹' + filterPrice.value;
        }

        let visibleCount = 0;
        carItems.forEach(card => {
            const cFuel = card.getAttribute('data-fuel').toLowerCase();
            const cSeaters = card.getAttribute('data-seaters');
            const cTrans = card.getAttribute('data-trans').toLowerCase();
            const cPrice = parseFloat(card.getAttribute('data-price'));

            let matchFuel = (fuelVal === 'all' || cFuel === fuelVal);
            let matchSeater = (seaterVal === 'all' || cSeaters === seaterVal);
            let matchTrans = (transVal === 'all' || cTrans === transVal);
            let matchPrice = (cPrice <= priceVal);

            if (matchFuel && matchSeater && matchTrans && matchPrice) {
                card.style.display = 'block';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        });

        const noResults = document.getElementById('noResultsMsg');
        if (noResults) {
            noResults.style.display = visibleCount === 0 ? 'block' : 'none';
        }
    }

    if (filterFuel) filterFuel.addEventListener('change', applyFilters);
    if (filterSeaters) filterSeaters.addEventListener('change', applyFilters);
    if (filterTransmission) filterTransmission.addEventListener('change', applyFilters);
    if (filterPrice) filterPrice.addEventListener('input', applyFilters);
}

// 3. 3D Card Hover Perspective Tilt
function initCardTilt() {
    const cards = document.querySelectorAll('.tilt-card');
    cards.forEach(card => {
        card.addEventListener('mousemove', (e) => {
            const rect = card.getBoundingClientRect();
            const x = e.clientX - rect.left - rect.width / 2;
            const y = e.clientY - rect.top - rect.height / 2;
            const rotateX = -(y / 25).toFixed(2);
            const rotateY = (x / 25).toFixed(2);
            card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateY(-6px)`;
        });

        card.addEventListener('mouseleave', () => {
            card.style.transform = `perspective(1000px) rotateX(0deg) rotateY(0deg) translateY(0px)`;
        });
    });
}

// 4. Payment Interactions (Card live mirror & UPI simulated flow)
function initPaymentInteractions() {
    const cardInput = document.getElementById('cardNumberInput');
    const cardDisplay = document.getElementById('cardDisplayNumber');
    const nameInput = document.getElementById('cardNameInput');
    const nameDisplay = document.getElementById('cardDisplayName');
    const expiryInput = document.getElementById('cardExpiryInput');
    const expiryDisplay = document.getElementById('cardDisplayExpiry');

    if (cardInput && cardDisplay) {
        cardInput.addEventListener('input', (e) => {
            let val = e.target.value.replace(/\D/g, '').substring(0, 16);
            val = val.match(/.{1,4}/g)?.join(' ') || '';
            e.target.value = val;
            cardDisplay.innerText = val.length > 0 ? val : '•••• •••• •••• ••••';
        });
    }

    if (nameInput && nameDisplay) {
        nameInput.addEventListener('input', (e) => {
            nameDisplay.innerText = e.target.value.toUpperCase() || 'YOUR NAME';
        });
    }

    if (expiryInput && expiryDisplay) {
        expiryInput.addEventListener('input', (e) => {
            let val = e.target.value.replace(/\D/g, '').substring(0, 4);
            if (val.length >= 2) {
                val = val.substring(0, 2) + '/' + val.substring(2);
            }
            e.target.value = val;
            expiryDisplay.innerText = val || 'MM/YY';
        });
    }
}

