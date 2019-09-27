let map;

function init() {
    map = new ymaps.Map("map", {
        center: [55.45, 37.36],
        zoom: 8,
        controls: []
    }, {
        suppressMapOpenBlock: true
    });
    // @ts-ignore
    mapSetMarkers();
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
        '<div class="placemark_container">\n' +
        '        <div class="trash_circle"></div>\n' +
        '        <div class="trash_ring"></div>\n' +
        '        <img class="trash_icon" src="icons/ic_delete.svg">\n' +
        '        <div class="trash_text">Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text Text\n' +
        '            Text Text Text Text\n' +
        '        </div>\n' +
        '    </div>'
    );
    map.geoObjects
        .add(new ymaps.Placemark([55.45, 37.36], {
            iconCaption: 'Нет проезда'
        }, {
// @ts-ignore
            iconLayout: 'default#imageWithContent',
            // Своё изображение иконки метки.
            iconImageHref: 'icons/ic_delete.svg',
            // Размеры метки.
            iconImageSize: [24, 24],
            // Макет содержимого.
            iconContentLayout: MyIconContentLayout
        }))
};

// @ts-ignore
window.mapSetLocation = function () {
    if (map == null) {
        return
    }
};