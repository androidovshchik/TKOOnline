let map;

function init() {
    map = new ymaps.Map("map", {
        center: [55.45, 37.36],
        zoom: 8,
        controls: []
    }, {
        suppressMapOpenBlock: true
    });
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
window.zoomIn = function () {
    if (map == null) {
        return
    }
    map.setZoom(map.getZoom() + 1);
};

// @ts-ignore
window.zoomOut = function () {
    if (map == null) {
        return
    }
    map.setZoom(map.getZoom() - 1);
};

// @ts-ignore
window.moveTo = function (latitude, longitude) {
    if (map == null) {
        return
    }
    map.setCenter([latitude, longitude], 12);
};