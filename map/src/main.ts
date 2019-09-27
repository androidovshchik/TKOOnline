let map, markersCollection, locationCollection;

function init() {
    map = new ymaps.Map("map", {
        center: [55.45, 37.36],
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
    const MyIconContentLayout = ymaps.templateLayoutFactory.createClass(
        `<div class="placemark_container">
        <div class="trash_circle"></div>
        <span class="trash_text">Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text
            Text Text Text Text
        </span>
        <div class="trash_ring" style="border-color: green"></div>
        <img class="trash_icon" src="icons/ic_delete.svg">
    </div>`
    );
    markersCollection
        .add(new ymaps.Placemark([55.45, 37.36], {
        }, {
// @ts-ignore
            iconLayout: 'default#imageWithContent',
            iconImageSize: [0, 0],
            iconContentLayout: MyIconContentLayout,
            iconShape: {
                type: 'Rectangle',
                // Прямоугольник описывается в виде двух точек - верхней левой и нижней правой.
                // @ts-ignore
                coordinates: [
                    [-250, -250], [250, 250]
                ]
            }
        }))
};

// @ts-ignore
window.mapSetLocation = function () {
    if (map == null) {
        return
    }

    // Создаем круг.
    var myCircle = new ymaps.Circle([
        // Координаты центра круга.
        [55.76, 37.60],
        // Радиус круга в метрах.
        10000
    ], {}, {
        fillColor: "#70b06e99",
        strokeWidth: 0
    });

    // Добавляем круг на карту.
    locationCollection.add(myCircle);
};