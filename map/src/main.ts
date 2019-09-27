import {Platform} from "./platform.interface";
import {getColor} from "./status.enum";

declare const ymaps;

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
    mapSetLocation(55, 37, 1000);
    // @ts-ignore
    mapSetMarkers(JSON.stringify([{
        latitude: 55.1,
        longitude: 37.1,
        status: 10,
        p_errors: null
    }, {
        latitude: 55.2,
        longitude: 37.2,
        status: 20,
        p_errors: ''
    }, {
        latitude: 55.3,
        longitude: 37.3,
        status: 30,
        p_errors: 'Нет проезда'
    }]));
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
window.mapZoomIn = function (duration: number = 500) {
    if (map == null) {
        return
    }
    map.setZoom(map.getZoom() + 1, {
        duration: duration
    });
};

// @ts-ignore
window.mapZoomOut = function (duration: number = 500) {
    if (map == null) {
        return
    }
    map.setZoom(map.getZoom() - 1, {
        duration: duration
    });
};

// @ts-ignore
window.mapMoveTo = function (latitude: number, longitude: number, zoom: number = 12, duration: number = 800) {
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
    markersCollection.removeAll();
};

// @ts-ignore
window.mapSetMarkers = function (first: string, second: string = "[]") {
    if (map == null) {
        return
    }
    markersCollection.removeAll();
    addPlatforms(JSON.parse(first));
    addPlatforms(JSON.parse(second));
};

function addPlatforms(platforms: Platform[]) {
    platforms.forEach(p => {
        const layout = ymaps.templateLayoutFactory.createClass(`
            <div class="placemark">
                <span class="trash_text" ${!p.p_errors ? 'style="display: none"' : ''}>${p.p_errors}</span>
                <div class="trash_ring" style="border-color: ${getColor(p.status)}"></div>
                <img class="trash_icon" src="icons/ic_delete.svg">
            </div>`
        );
        markersCollection
            .add(new ymaps.Placemark([p.latitude, p.longitude], {}, {
                iconLayout: 'default#imageWithContent',
                iconImageSize: [0, 0],
                iconImageOffset: [0, 0],
                // NOTICE magic -23
                iconContentOffset: [-23, -24],
                iconContentLayout: layout
            } as any))
    });
}

/**
 * @param radius in meters
 */
// @ts-ignore
window.mapSetLocation = function (latitude: number, longitude: number, radius: number = 0) {
    if (map == null) {
        return
    }
    locationCollection.removeAll();
    if (radius > 0) {
        locationCollection.add(new ymaps.Circle([[latitude, longitude], radius], {}, {
            fillColor: "#70b06e99",
            strokeWidth: 0
        }));
    }
    const layout = ymaps.templateLayoutFactory.createClass(`
        <div class="placemark">
            <img class="location_icon" src="icons/ic_location.svg">
        </div>`
    );
    locationCollection
        .add(new ymaps.Placemark([latitude, longitude], {}, {
            iconLayout: 'default#imageWithContent',
            iconImageSize: [0, 0],
            iconImageOffset: [0, 0],
            // NOTICE magic -21
            iconContentOffset: [-21, -16],
            iconContentLayout: layout,
            zIndex: 999999
        } as any))
};