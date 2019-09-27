import {Platform} from "./platform.interface";

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
    mapSetMarkers();
    // @ts-ignore
    mapSetLocation(55, 37, 10000);
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
window.mapSetMarkers = function (json: string) {
    if (map == null) {
        return
    }
    markersCollection.removeAll();
    const platforms: Platform[] = JSON.parse(json);
    platforms.forEach(p => {
        //var color: Color = Color[green];
        const layout = ymaps.templateLayoutFactory.createClass(`
            <div class="placemark">
                <div class="trash_circle"></div>
                <span class="trash_text" ${!p.p_errors ? 'style="display: none"' : ''}>${p.p_errors}</span>
                <div class="trash_ring" style="border-color: green"></div>
                <img class="trash_icon" src="icons/ic_delete.svg">
            </div>`
        );
        markersCollection
            .add(new ymaps.Placemark([p.latitude, p.longitude], {}, {
                iconLayout: 'default#imageWithContent',
                iconImageSize: [0, 0],
                iconContentLayout: layout
            } as any))
    });
};

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
            iconContentLayout: layout
        } as any))
};