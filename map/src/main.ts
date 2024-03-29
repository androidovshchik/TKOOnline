import {Platform} from "./platform.interface";
import {getColor} from "./status.enum";
import {getAngle, LocationEvent} from "./location.interface";

declare const Android;

declare const ymaps;

let id, map, markersCollection, locationCollection, routeCollection, isGpsAvailable = true;

// @ts-ignore
const MutationObserver = window.MutationObserver || window.WebKitMutationObserver;

function changeIcon(element) {
    if (isGpsAvailable) {
        element.src = "icons/ic_location.svg";
    } else {
        element.src = "icons/ic_location_gray.svg";
    }
}

const observer = new MutationObserver((mutations) => {
    const element = document.getElementById('my_location');
    if (element) {
        cancelObserver();
        changeIcon(element);
    }
});

function cancelObserver() {
    observer.disconnect();
    observer.takeRecords();
}

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
    routeCollection = new ymaps.GeoObjectCollection();
    map.geoObjects
        .add(markersCollection)
        .add(locationCollection)
        .add(routeCollection);
    if (typeof Android !== 'undefined') {
        Android.onReady();
    }
}

const script = document.createElement('script');
script.src = `https://api-maps.yandex.ru/2.1/?apikey=a92bd5d3-176f-40b5-8213-844995d832ab&lang=ru_RU&_today=${new Date().toJSON().slice(0, 10)}`;
script.type = "text/javascript";
script.onload = function () {
    ymaps.ready(init);
};
document.getElementsByTagName('head')[0].appendChild(script);

/**
 * NOTICE the number at window functions is needed for similarity detection
 */

// @ts-ignore
window._0_mapSetBounds = function (lat1: number, lon1: number, lat2: number, lon2: number) {
    if (map == null) {
        return
    }
    map.setBounds([[lat1, lon1], [lat2, lon2]]);
};

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
window._2_mapMoveTo = function (latitude: number, longitude: number, zoom: number = null, duration: number = 800) {
    if (map == null) {
        return
    }
    map.setCenter([latitude, longitude], zoom ? zoom : map.getZoom(), {
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
        const placemark = new ymaps.Placemark([p.latitude, p.longitude], {}, {
            iconLayout: 'default#imageWithContent',
            iconImageSize: [0, 0],
            iconImageOffset: [0, 0],
            // NOTICE magic -23
            iconContentOffset: [-23, -24],
            iconContentLayout: layout,
            iconShape: {
                type: 'Circle',
                coordinates: [0, 0],
                radius: 24
            },
            zIndex: 9999
        } as any);
        placemark.events.add('click', function () {
            if (typeof Android !== 'undefined') {
                Android.onPlatform(p.kp_id);
            }
        });
        markersCollection
            .add(placemark)
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
    cancelObserver();
    isGpsAvailable = false;
    locationCollection.removeAll();
};

/**
 * @param radius in meters
 */
// @ts-ignore
window._4_mapSetLocation = function (latitude: number, longitude: number, radius: number = 0, active: boolean = true) {
    if (map == null) {
        return
    }
    cancelObserver();
    isGpsAvailable = true;
    locationCollection.removeAll();
    if (radius > 0) {
        locationCollection.add(new ymaps.Circle([[latitude, longitude], radius], {}, {
            fillColor: "#70b06e99",
            strokeWidth: 0
        }));
    }
    const layout = ymaps.templateLayoutFactory.createClass(`
        <div class="placemark">
            <img id="my_location" class="location_icon" src="icons/ic_location${active ? '' : '_gray'}.svg">
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
            zIndex: 99999
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

// @ts-ignore
window._6_mapChangeIcon = function (active: boolean) {
    if (map == null) {
        return
    }
    cancelObserver();
    isGpsAvailable = active;
    const element = document.getElementById("my_location");
    if (element) {
        changeIcon(element);
    } else {
        observer.observe(document.body, {
            subtree: true,
            childList: true
        });
    }
};

// @ts-ignore
window._7_mapClearRoute = function () {
    if (map == null) {
        return
    }
    routeCollection.removeAll();
};

// @ts-ignore
window._7_mapSetRoute = function (locations: LocationEvent[] = []) {
    if (map == null) {
        return
    }
    routeCollection.removeAll();
    locations.forEach((loc, i) => {
        const layout = ymaps.templateLayoutFactory.createClass(`
            <div id="arrow_${i}" class="placemark">
                <img class="arrow_icon" src="icons/gen/${loc._s}${loc._w ? 'G' : ''}${getAngle(loc.data.dir)}.png">
            </div>`
        );
        const balloon = ymaps.templateLayoutFactory.createClass(`
            <span>
                ID пакета: ${loc.id}<br>
                Скорость: ${loc.data.spd}<br>
                Пробег: ${loc.data.race}<br>
                Высота: ${loc.data.height}<br>
                Кол-во спутников: ${loc.data.sat_cnt}<br>
                Валидность: ${loc.data.valid}<br>
                Отправлено: ${loc._t}<br>
                Время события: ${loc.data.event_time}
            </span>
        `);
        routeCollection
            .add(new ymaps.Placemark([loc.data.lat, loc.data.lon], {}, {
                hideIconOnBalloonOpen: false,
                iconLayout: 'default#imageWithContent',
                // NOTICE magic numbers
                iconImageSize: [24, 28],
                iconImageOffset: [-10, -14],
                iconContentOffset: [-9, 0],
                iconContentLayout: layout,
                balloonContentLayout: balloon,
                // Запретим замену обычного балуна на балун-панель.
                // Если не указывать эту опцию, на картах маленького размера откроется балун-панель.
                balloonPanelMaxMapArea: 0
            } as any));
    });
};