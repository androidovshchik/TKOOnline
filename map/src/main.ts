import {Platform} from "./platform.interface";
import {getColor} from "./status.enum";

declare const Android;

declare const ymaps;

let id, map, markersCollection, locationCollection;

function init() {
    // @ts-ignore
    id = `${document.getElementById('main').src}?`.split("?")[1];
    const latitude = localStorage.getItem(`${id}_latitude`);
    const longitude = localStorage.getItem(`${id}_longitude`);
    const zoom = localStorage.getItem(`${id}_zoom`);
    map = new ymaps.Map("map", {
        center: [latitude ? Number(latitude) : 55.75222, longitude ? Number(longitude) : 37.61556],
        zoom: zoom ? Number(zoom) : 8,
        controls: []
    }, {
        suppressMapOpenBlock: true
    });
    markersCollection = new ymaps.GeoObjectCollection();
    locationCollection = new ymaps.GeoObjectCollection();
    map.geoObjects
        .add(markersCollection)
        .add(locationCollection);
    Android.onReady();
}

const script = document.createElement('script');
script.src = "https://api-maps.yandex.ru/2.1/?apikey=a92bd5d3-176f-40b5-8213-844995d832ab&lang=ru_RU";
script.type = "text/javascript";
script.onload = function () {
    ymaps.ready(init);
};
document.getElementsByTagName('head')[0].appendChild(script);

/**
 * NOTICE the number at window functions is needed for similarity detection
 */

// @ts-ignore
window._1_mapZoomIn = function (duration: number = 500) {
    if (map == null) {
        return
    }
    map.setZoom(map.getZoom() + 1, {
        duration: duration
    });
};

// @ts-ignore
window._1_mapZoomOut = function (duration: number = 500) {
    if (map == null) {
        return
    }
    map.setZoom(map.getZoom() - 1, {
        duration: duration
    });
};

// @ts-ignore
window._2_mapMoveTo = function (latitude: number, longitude: number, zoom: number = 12, duration: number = 800) {
    if (map == null) {
        return
    }
    map.setCenter([latitude, longitude], zoom, {
        duration: duration
    });
};

// @ts-ignore
window._3_mapClearMarkers = function () {
    if (map == null) {
        return
    }
    markersCollection.removeAll();
};

function addPlatforms(platforms: Platform[]) {
    platforms.forEach(p => {
        const layout = ymaps.templateLayoutFactory.createClass(`
            <div class="placemark">
                ${p._e.length > 0 ? `<span class="trash_text">${p._e.join(", ")}</span>` : ''}
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

// @ts-ignore
window._3_mapSetMarkers = function (first: Platform[], second: Platform[] = []) {
    if (map == null) {
        return
    }
    markersCollection.removeAll();
    addPlatforms(first);
    addPlatforms(second);
};

// @ts-ignore
window._4_mapClearLocation = function () {
    if (map == null) {
        return
    }
    locationCollection.removeAll();
};

/**
 * @param radius in meters
 */
// @ts-ignore
window._4_mapSetLocation = function (latitude: number, longitude: number, radius: number = 0) {
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

// @ts-ignore
window._5_mapClearState = function (all: boolean = false) {
    if (map == null) {
        return
    }
    if (all) {
        localStorage.clear()
    } else {
        localStorage.removeItem(`${id}_latitude`);
        localStorage.removeItem(`${id}_longitude`);
        localStorage.removeItem(`${id}_zoom`);
    }
};

// @ts-ignore
window._5_mapSaveState = function () {
    if (map == null) {
        return
    }
    const center = map.getCenter();
    localStorage.setItem(`${id}_latitude`, center[0]);
    localStorage.setItem(`${id}_longitude`, center[1]);
    localStorage.setItem(`${id}_zoom`, map.getZoom());
};