let map, markersCollection, locationCollection;

function init() {
    map = new ymaps.Map("map", {
        center: [55.75222, 37.61556],
        zoom: 8,
        controls: []
    }, {
        suppressMapOpenBlock: true
    });
    markersCollection = new ymaps.GeoObjectCollection();
    locationCollection = new ymaps.GeoObjectCollection();
    map.geoObjects
        .add(markersCollection)
        .add(locationCollection);
    // @ts-ignore
    mapSetMarkers();
    // @ts-ignore
    mapSetLocation();
}

const script = document.createElement('script');
script.src = "https://api-maps.yandex.ru/2.1/?apikey=a92bd5d3-176f-40b5-8213-844995d832ab&lang=ru_RU";
script.type = "text/javascript";
script.async = false;
script.onload = function () {
    ymaps.ready(init);
};
document.getElementsByTagName('head')[0].appendChild(script);

// @ts-ignore
window.mapZoomIn = function (duration = 500) {
    if (map == null) {
        return
    }
    map.setZoom(map.getZoom() + 1, {
        duration: duration
    });
};

// @ts-ignore
window.mapZoomOut = function (duration = 500) {
    if (map == null) {
        return
    }
    map.setZoom(map.getZoom() - 1, {
        duration: duration
    });
};

// @ts-ignore
window.mapMoveTo = function (latitude, longitude, zoom = 12, duration = 800) {
    if (map == null) {
        return
    }
    map.setCenter([latitude, longitude], zoom, {
        duration: duration
    });
};

// @ts-ignore
window.mapClearMarkers = function () {
    if (map == null) {
        return
    }
    map.geoObjects.removeAll();
};

// @ts-ignore
window.mapSetMarkers = function () {
    if (map == null) {
        return
    }
    const layout = ymaps.templateLayoutFactory.createClass(`
        <div class="placemark">
            <div class="trash_circle"></div>
            <span class="trash_text">${1}</span>
            <div class="trash_ring" style="border-color: green"></div>
            <img class="trash_icon" src="icons/ic_delete.svg">
        </div>`
    );
    locationCollection
        .add(new ymaps.Placemark([0, 0], {}, {
            // @ts-ignore
            iconLayout: 'default#imageWithContent',
            iconImageSize: [0, 0],
            iconContentLayout: layout
        }))
};

/**
 * @param radius in meters
 */
// @ts-ignore
window.mapSetLocation = function (latitude, longitude, radius = 0) {
    if (map == null) {
        return
    }
    locationCollection.removeAll();
    if (radius > 0) {
        const circle = new ymaps.Circle([[latitude, longitude], radius], {}, {
            fillColor: "#70b06e99",
            strokeWidth: 0
        });
        locationCollection.add(circle);
    }
    const layout = ymaps.templateLayoutFactory.createClass(`
        <div class="placemark">
            <img class="location_icon" src="icons/ic_location.svg">
        </div>`
    );
    locationCollection
        .add(new ymaps.Placemark([latitude, longitude], {}, {
            // @ts-ignore
            iconLayout: 'default#imageWithContent',
            iconImageSize: [0, 0],
            iconContentLayout: layout
        }))
};